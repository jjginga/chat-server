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

                    if(line.matches("^/.+")){

                        command(line);
                        continue;
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
            case "/help":
                writeToClient(Commands.HELP.listAll());
                break;
            case "/kick":
                if(command.split(" ")[1]==null){
                    writeToClient(">> Kick yourself!\n"+Commands.KICK.getDescription());
                    break;
                }
                if(!server.isOn(command.split(" ")[1])){
                    writeToClient(">> User not on!");
                    break;
                }
                server.kick(command.split(" ")[1]);
                break;
            case "/whisper":
                if(command.split(" ")[1]==null){
                    writeToClient(">> Are you talking to yourself?\n"+Commands.WHISPER.getDescription());
                    break;
                }

                if(command.split(" ")[2]==null){
                    writeToClient(">> Don't you have anything to say?\n"+Commands.WHISPER.getDescription());
                    break;
                }

                if(!server.isOn(command.split(" ")[1])){
                    writeToClient(">> User not on!");
                    break;
                }

                server.whisper(command.split(" ")[1], command.substring(command.indexOf(" ", 2)), userName);
                break;
            case "/create":
                if(command.split(" ")[1]==null){
                    writeToClient(">> Are you talking to yourself?\n"+Commands.CREATE.getDescription());
                    break;
                }

                if(server.groupExists(command.split(" ")[1])){
                    writeToClient(">> Group already exists!");
                    break;
                }

                server.createGroup(command.split(" ")[1], this);
                break;
            case "/join":
                if(command.split(" ")[1]==null){
                    writeToClient(">> Join where?\n"+Commands.JOIN.getDescription());
                    break;
                }

                if(!server.groupExists(command.split(" ")[1])){
                    writeToClient(">> Group doesn't exists!");
                    break;
                }
                server.joinGroup(command.split(" ")[1], this);
                break;
            case "/room":
                if(command.split(" ").length<3){
                    writeToClient(">> what?\n"+Commands.ROOM.getDescription());
                    break;
                }

                if(!server.groupExists(command.split(" ")[1])){
                    writeToClient(">> Group doesn't exists!");
                    break;
                }
                server.messageGroup(command.split(" ")[1],command.substring(command.indexOf(" ", 2)), this);
                break;
            case "/quit":
                quit();
                break;
            default:
                writeToClient(">> Invalid Command.");
                break;

        }
    }


    public String getUserName() {
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


    void writeToClient(String message){
        try {
            outputBufferedWriter.write(message);
            outputBufferedWriter.newLine();
            outputBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private enum Commands{
        NAME("/name", "Change username: /name <new_name>"),
        LIST("/list", "List all users: /list"),
        QUIT("/quit", "Leave the chat: /leave"),
        HELP("/help", "List all commands: /help"),
        KICK("/kick", "Remove user from chat: /kick <username>"),
        WHISPER("/whisper", "Send private message to user: /whisper <username> <message>"),
        CREATE("/create", "Creates a group: /create <groupname>"),
        JOIN("/join", "Join a group: /join <groupname>"),
        ROOM("/room", "Send Message to a group: /room <groupname> <message>");

        private String name;
        private String description;

        Commands(String name, String description){
            this.name=name;
            this.description=description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String listAll(){
            StringBuilder stringBuilder = new StringBuilder();

            for (Commands commands : Commands.values()) {
                stringBuilder.append(">> "+commands.getName()+": "+commands.getDescription()+"\n");
            }

            return stringBuilder.toString();
        }

    }


}
