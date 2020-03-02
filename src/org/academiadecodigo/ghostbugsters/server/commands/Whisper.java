package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class Whisper implements Commands{
    @Override
    public String name() {
        return "/whisper";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public void implementation(Server server, String userName, String command) {

        if(command.split(" ").length<=2){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>"+description());
            return;
        }

        String userToWhisperTo = command.split(" ")[1];

        if(!server.isOn(userToWhisperTo)){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>user doesn't exist.");
            return;
        }

        String message = command.substring(command.indexOf(" ", 2));

        server.whisper(userToWhisperTo, message,  userName);

    }
}
