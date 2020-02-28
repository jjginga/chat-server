package org.academiadecodigo.ghostbugsters.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {



    private ServerSocket serverSocket;
    private LinkedList<ServerWorker> swList;
    private HashMap<String, LinkedList<ServerWorker>> groups;
    private ExecutorService swPool;

    public static void main(String[] args) {
        Server server = new Server(8080);

    }


    public Server(int port) {
        swList = new LinkedList<>();
        swPool = Executors.newCachedThreadPool();
        groups = new HashMap<>();

        try {

            // bind the socket to specified port
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

        // block waiting for a client to connect

        /**this has to be synchronized**/
        Socket clientSocket = null;

        try {
            clientSocket = serverSocket.accept();
            swList.add(new ServerWorker(clientSocket,this));
            swPool.submit(swList.peekLast());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void broadcast(String message){



        for (ServerWorker serverWorker : swList) {
            serverWorker.writeToClient(message);
        }


    }

    String list(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ ");
        for (ServerWorker serverWorker : swList) {
            stringBuilder.append(serverWorker.getUserName()+" ");
        }
        stringBuilder.append(" ]");

        return stringBuilder.toString();
    }

    boolean isOn(String name){
        for (ServerWorker serverWorker : swList) {
            if(serverWorker.getUserName().equals(name)){
                return true;
            }
        }

        return false;
    }

    void kick(String name){
        for (ServerWorker serverWorker : swList) {
            if(serverWorker.getUserName().equals(name)){
                serverWorker.quit();
            }
        }
    }

    void whisper(String name, String message, String sender){
        for (ServerWorker serverWorker : swList) {
            if(serverWorker.getUserName().equals(name)){
                serverWorker.writeToClient(sender+" whispers:"+message);
            }
        }
    }

    boolean groupExists(String name){
        for (String s : groups.keySet()) {
           if(s.equals(name)){
               return true;
           }
        }

        return false;
    }

    void createGroup(String groupName, ServerWorker serverWorker){
        groups.put(groupName, new LinkedList<ServerWorker>());
        groups.get(groupName).add(serverWorker);
    }

    void joinGroup(String groupName, ServerWorker serverWorker){
        groups.get(groupName).add(serverWorker);
    }

    void messageGroup(String groupName, String message, ServerWorker serverWorker){
        for (ServerWorker worker : groups.get(groupName)) {
            worker.writeToClient(serverWorker.getUserName()+": "+message);
        }

    }

    void killSw(ServerWorker serverWorker){
        swList.remove(serverWorker);
    }

    private void close(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
