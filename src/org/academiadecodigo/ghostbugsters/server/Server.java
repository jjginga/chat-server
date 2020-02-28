package org.academiadecodigo.ghostbugsters.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {



    private ServerSocket serverSocket;
    private Hashtable<String, ServerWorker> swHashTable;
    private HashMap<String, List<ServerWorker>> groups;
    private ExecutorService swPool;
    private int i;


    public static void main(String[] args) {
        Server server = new Server(8080);

    }


    public Server(int port) {
        swHashTable = new Hashtable<>();
        swPool = Executors.newCachedThreadPool();
        groups = new HashMap<>();
        i=1;

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

    private void listen(){


        /**this has to be synchronized**/
        Socket clientSocket = null;

        try {

            clientSocket = serverSocket.accept();

            synchronized (swHashTable) {
                swHashTable.put("user" + i, new ServerWorker(clientSocket, this));
                swPool.submit(swHashTable.get("user" + i));
                swHashTable.get("user" + i).setUserName("user" + i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        i++;
    }

    void broadcast(String message){


        synchronized (swHashTable) {
            for (String key : swHashTable.keySet()) {
                swHashTable.get(key).writeToClient(message);
            }
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

        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("; "));
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    boolean isOn(String name){

        if(swHashTable.containsKey(name)){
            return true;
        }

        return false;
    }

    public ServerWorker getServerWorker(String name){

        return swHashTable.get(name);
    }

    public Hashtable<String, ServerWorker> getSwHashTable() {
        return swHashTable;
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

    boolean groupExists(String groupName){

        return groups.containsKey(groupName);
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

    void messageGroup(String groupName, String message, ServerWorker serverWorker){

        synchronized (groups.get(groupName)) {

            for (ServerWorker worker : groups.get(groupName)) {
                worker.writeToClient(">>" + groupName + "<< " + serverWorker.getUserName() + ": " + message);
            }

        }


    }

    void killSw(ServerWorker serverWorker){

        swHashTable.remove(serverWorker);

    }

    public void rename(ServerWorker serverWorker, String oldName, String newName){

        synchronized (swHashTable) {
            swHashTable.put(newName, serverWorker);
            swHashTable.remove(oldName);
        }

    }



    private void close(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
