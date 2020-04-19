package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class Room implements Commands{
    @Override
    public String name() {
        return "/room";
    }

    @Override
    public String description() {
        return "Send Message to a group: /room <groupname> <message>";
    }

    @Override
    public void implementation(Server server, String userName, String command) {


        if(command.split(" ").length<=2){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>"+description());
            return;
        }

        String groupName = command.split(" ")[1];

        if(!server.groupExists(groupName)){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>group doesn't exist.");
            return;
        }

        if(!server.getGroup(groupName).contains(server.getServerWorker(userName))){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>user is not on the group!");
            return;
        }

        server.messageGroup(command.split(" ")[1],command.substring(command.indexOf(" ", 2)), server.getServerWorker(userName));
    }
}
