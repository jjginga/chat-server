package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class Join implements Commands{



    @Override
    public String name() {
        return "/join";
    }

    @Override
    public String description() {
        return "Join a group: /join <groupname>";
    }

    @Override
    public void implementation(Server server, String userName, String command) {

        if(command.split(" ").length<=1){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>"+description());
            return;
        }

        String groupName = command.split(" ")[1];

        if(server.getGroup(groupName).contains(userName)){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>already on group.");
            return;
        }

        server.joinGroup(groupName, server.getServerWorker(userName));
    }
}
