package org.academiadecodigo.ghostbugsters;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
public class Client {

    private Socket socket;
    private BufferedReader systemIn;
    private BufferedWriter clientOut;
    private BufferedReader clientIn;

    public static void main(String[] args) {

        Client client = new Client("127.0.0.1",8080);
        client.receiveFromServer();
    }


    public Client(String serverAddress, int serverPort) {

        System.out.println("Trying to establishing the connection, please wait...");


        try {
            // connect to the specified host name and port
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to: " + socket);
            // create the streams
            setupSystemIn();
            setupSocketStreams();

        } catch (UnknownHostException ex) {

            System.out.println("Host unknown: " + ex.getMessage());
            System.exit(1);

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
            System.exit(1);

        }


        Thread writeToServer = new Thread(new WriteToServer());

        writeToServer.start();
        receiveFromServer();
    }

    public void receiveFromServer() {


        String inMessage = "";

        // message quit is sent back from server.
        while (!inMessage.equals("/quit")) {

            try {

                // read the message from the stream;
                inMessage = clientIn.readLine();

                if (inMessage.equals("/invalid")){
                    System.out.println(clientIn.readLine());
                    continue;
                }

                if(inMessage.equals("/file receive")){
                   receiveFile();
                   continue;
                }

                if(inMessage.equals("/file send")){
                    String path = clientIn.readLine();
                    sendFile(path);
                    continue;
                }

                if(inMessage.equals("/quit")){
                    break;
                }

                // write the message to the console
                System.out.println(inMessage);

            } catch (IOException ex) {

                System.out.println("Sending error: " + ex.getMessage() + ", closing client...");
                System.exit(2);
                break;

            }

        }
        System.exit(1);
        stop();
    }


    public void setupSocketStreams() throws IOException {

        clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        clientOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void setupSystemIn(){
        systemIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void stop() {

        try {

            if (socket != null) {
                System.out.println("Closing the socket");
                socket.close();
            }

        } catch (IOException ex) {

            System.out.println("Error closing connection: " + ex.getMessage());

        }
    }



    private class WriteToServer implements Runnable{

        @Override
        public void run() {

            String line ="";

            //when client starts asks for nickname
            setUsername();
            while (true) {
                // read the pretended message from the console
                try {
                    line = systemIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                // write the pretended message to the output buffer
                write(line);
            }

            // close the client socket and buffers
            stop();
        }

    }

    void write(String message){
        try {
            clientOut.write(message);
            clientOut.newLine();
            clientOut.flush();
        } catch (IOException ex) {

            System.out.println("Sending error: " + ex.getMessage());
        }
    }

    private void setUsername(){
        try {
            System.out.println("Username: ");
            write("/alias "+systemIn.readLine());
            System.out.println("Type /help for help.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(){

        DataInputStream in = null;
        FileOutputStream fStream=null;

        byte[] buffer = new byte[1024];
        int num;

        try {
            in = new DataInputStream(socket.getInputStream());
            fStream = new FileOutputStream("resources/user");
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {

            while ((num = in.read(buffer))==buffer.length){
                fStream.write(buffer, 0, num);
            }

            fStream.write(buffer, 0, num);

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                fStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        System.out.println("- Ficheiro Recebido.");

    }

    private void sendFile(String path) throws IOException {

        byte[] buffer = new byte[1024];
        int num;
        FileInputStream fStream=null;
        DataOutputStream out = null;

        try {
            out = new DataOutputStream(socket.getOutputStream());
            fStream = new FileInputStream(new File(path));

            while((num=fStream.read(buffer))!=-1){
                out.write(buffer, 0,num);
            }

            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("- Ficheiro enviado");

    }

}
