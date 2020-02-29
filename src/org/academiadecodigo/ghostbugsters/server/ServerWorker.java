package org.academiadecodigo.ghostbugsters.server;

import java.io.*;
import java.net.Socket;

public class ServerWorker implements Runnable{

    private BufferedReader inputBufferedReader;
    private BufferedWriter outputBufferedWriter;
    private Socket clientSocket;
    private Server server;
    private String userName;

    public ServerWorker(Socket clientSocket, Server server){
        this.clientSocket=clientSocket;
        this.server=server;
       setupSocketStreams();

    }

    @Override
    public void run() {

            String line ="";


            while (!line.equals("/quit")) {

                try {

                    // synchronize this
                    line = inputBufferedReader.readLine();

                    if(line.matches("")){
                        continue;
                    }

                    server.broadcast(line, userName);


                } catch (IOException ex) {

                    System.out.println("Sending error: " + ex.getMessage() + ", closing client...");
                    break;

                }

            }

        close();

    }

    private void setupSocketStreams(){

        try {
            inputBufferedReader=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputBufferedWriter=new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
        Thread.currentThread().setName(userName);
    }

    String getUserName() {
        return userName;
    }

    void quit(){
        writeToClient("/quit");
        server.killSw(this);

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void close(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeToClient(String message){
        try {
            outputBufferedWriter.write(message);
            outputBufferedWriter.newLine();
            outputBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    DataOutputStream getDataOutputStream(){
        try {
            return new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    DataInputStream getDataInputStream(){

        try {
            return new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    void sendFile(DataOutputStream out, File file){

        byte[] buffer = new byte[1024];
        int num;
        FileInputStream fStream=null;

        try {
            fStream = new FileInputStream(file);
            while((num=fStream.read(buffer))!=-1){
                out.write(buffer, 0,num);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            fStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    void receiveFile(DataInputStream in){
        byte[] buffer = new byte[1024];
        int num;
        FileOutputStream fStream=null;

        String path="/Users/codecadet/joel/workspace/homework/multiclientserver/resources";
        try {
            fStream = new FileOutputStream(path);
            while ((num = in.read(buffer))!=-1){
                fStream.write(buffer, 0, num);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
