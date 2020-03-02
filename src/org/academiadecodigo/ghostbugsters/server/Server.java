package org.academiadecodigo.ghostbugsters.server;

import org.academiadecodigo.ghostbugsters.server.commands.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {



    private ServerSocket serverSocket;
    private Hashtable<String, ServerWorker> swHashTable;
    private HashMap<String, List<ServerWorker>> groups;
    private HashMap<String, Commands> commands;
    private ExecutorService swPool;
    private int i;


    public static void main(String[] args) {
        Server server = new Server(8080);

    }


    //Constructor
    public Server(int port) {

        swHashTable = new Hashtable<>();
        swPool = Executors.newCachedThreadPool();
        groups = new HashMap<>();
        i=1;
        commandsInit();

        try {

            System.out.println("Binding to port " + port);
            serverSocket = new ServerSocket(port);

            System.out.println("Server started: " + serverSocket);

            while (true) {

                listen();

            }

        } catch (IOException ioe) {

            System.out.println(ioe.getMessage());

        } finally {

            close();

        }

    }

    //Strategy Design patern
    private void commandsInit(){
        commands = new HashMap<>();
        commands.put("/alias", new Alias());
        commands.put("/create", new Create());
        commands.put("/help", new Help());
        commands.put("/join", new Join());
        commands.put("/kick", new Kick());
        commands.put("/list", new org.academiadecodigo.ghostbugsters.server.commands.List());
        commands.put("/room", new Room());
        commands.put("/whisper", new Whisper());
        commands.put("/file", new org.academiadecodigo.ghostbugsters.server.commands.File());
    }

    //Wait for new connections
    private void listen(){

        /**this has to be synchronized**/
        Socket clientSocket = null;

        try {

            clientSocket = serverSocket.accept();



                swHashTable.put("user" + i, new ServerWorker(clientSocket, this));

                swPool.submit(swHashTable.get("user" + i));

                swHashTable.get("user" + i).setUserName("user" + i);

        } catch (IOException e) {
            e.printStackTrace();
        }

        i++;
    }

    //Writes to all users
    void broadcast(String message, String userName){

        System.out.println(userName+" :"+message);

        if(message.matches("^/.+")){

            command(message, userName);
            return;
        }


        synchronized (swHashTable) {

            for (String key : swHashTable.keySet()) {
                swHashTable.get(key).writeToClient(userName+": "+message);
            }
        }


    }

    void command(String message, String userName){

            commands.get(message.split(" ")[0]).implementation(this, userName, message);
    }

    //Basic Commands auxiliary methods
    public void rename(ServerWorker serverWorker, String oldName, String newName){

        synchronized (swHashTable) {
            swHashTable.put(newName, serverWorker);
            swHashTable.remove(oldName);
        }

    }

    public String list(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");

        synchronized (swHashTable) {
            for (String key : swHashTable.keySet()) {
                stringBuilder.append(swHashTable.get(key).getUserName() + "; ");
            }
        }

        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" ; "));
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public void kick(String name){

        if(isOn(name)){
            getServerWorker(name).quit();
        }

    }

    public void whisper(String name, String message, String sender){

        if(isOn(name)){
            getServerWorker(name).writeToClient(sender+" whispers:"+message);
        }
    }

    public void sendFile(String senderName, String recipientName, String path){


        DataOutputStream outputStream = swHashTable.get(recipientName).getDataOutputStream();
        DataInputStream inputStream = swHashTable.get(senderName).getDataInputStream();

        swHashTable.get(recipientName).receiveFile(path.split("/")[path.split("/").length-1]);
        swHashTable.get(senderName).sendFile(path);

        byte[] buffer = new byte[1024];
        int num=0;

        try {
            while ((num = inputStream.read(buffer)) == -1) {
                outputStream.write(buffer, 0, num);
                if (num != buffer.length){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    public void createGroup(String groupName, ServerWorker serverWorker){

        if(groupExists(groupName)){
            return;
        }

        groups.put(groupName, Collections.synchronizedList(new LinkedList<ServerWorker>()));
        groups.get(groupName).add(serverWorker);
    }

    public void joinGroup(String groupName, ServerWorker serverWorker){

        groups.get(groupName).add(serverWorker);

    }

    public void messageGroup(String groupName, String message, ServerWorker serverWorker){

        synchronized (groups.get(groupName)) {

            for (ServerWorker worker : groups.get(groupName)) {
                worker.writeToClient(">>" + groupName + "<< " + serverWorker.getUserName() + ": " + message);
            }

        }


    }


    //Verification methods
    public boolean isOn(String name){

        if(swHashTable.containsKey(name)){
            return true;
        }

        return false;
    }

    public boolean groupExists(String groupName){

        return groups.containsKey(groupName);
    }

    //Other Auxiliary Methods
    public ServerWorker getServerWorker(String name){

        return swHashTable.get(name);
    }

    public Hashtable<String, ServerWorker> getSwHashTable() {
        return swHashTable;
    }

    public List getGroup(String groupName){

        return groups.get(groupName);
    }

    void killSw(ServerWorker serverWorker){

        swHashTable.remove(serverWorker);

    }

    private void close(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
