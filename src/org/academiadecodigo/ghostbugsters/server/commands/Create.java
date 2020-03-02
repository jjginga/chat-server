package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class Create implements Commands{
    @Override
    public String name() {
        return "/create";
    }

    @Override
    public String description() {
        return "Creates a group: /create <groupname>";
    }

    @Override
    public void implementation(Server server, String userName, String command) {

        //synchronized?

        if(command.split(" ").length<=1){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>"+description());
            return;
        }

        String groupName = command.split(" ")[1];

        if(server.groupExists(groupName)){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>group already exists");
            return;
        }

        server.createGroup( groupName, server.getServerWorker(userName));

    }
}
