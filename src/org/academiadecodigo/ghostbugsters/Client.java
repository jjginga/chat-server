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
        client.run();
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


        //Thread receiveFromServer = new Thread(this);
        Thread writeToServer = new Thread(new WriteToServer());

        //receiveFromServer.start();
        writeToServer.start();
        run();




    }

    public void run() {


        String inMessage = "";

        // while the client doesn't signal to quit
        while (!inMessage.equals("/quit")) {

            try {

                // read the pretended message from the stream;
                inMessage = clientIn.readLine();

                if(inMessage.equals("/quit")){
                    break;
                }

                // write the pretended message to the console
                System.out.println(inMessage);

            } catch (IOException ex) {

                System.out.println("Sending error: " + ex.getMessage() + ", closing client...");
                break;

            }

        }

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

            try {
                System.out.println("Username: ");
                write("/name "+systemIn.readLine());
                System.out.println("Type /help for help.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {


                    // read the pretended message from the console
                try {
                    line = systemIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                write(line);
                    // write the pretended message to the output buffer
            }


            // close the client socket and buffers
            stop();
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

    }


}
