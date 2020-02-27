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

            loop:
            while (!line.equals("/quit")) {

                try {

                    // synchronize this
                    line = inputBufferedReader.readLine();

                    if(line.matches("^/.+")){

                        command(line);
                        continue loop;
                    }
                    server.broadcast(userName+": "+line);


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

    private void command(String command){
        switch (command.split(" ")[0]) {
            case "/name":
                if(!(command.split(" ").length>1)){
                    writeToClient(">>Please put a name!");
                    break;
                }
                Thread.currentThread().setName(command.split(" ")[1]);
                userName=Thread.currentThread().getName();
                break;
            case "/list":
                writeToClient(server.list());
                break;
            case "/quit":
                writeToClient("/quit");
                server.killSw(this);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }


    public String getUserName() {
        return userName;
    }

    private void close(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void writeToClient(String message){
        try {
            outputBufferedWriter.write(message);
            outputBufferedWriter.newLine();
            outputBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
