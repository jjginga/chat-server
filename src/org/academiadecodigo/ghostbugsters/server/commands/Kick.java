package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class Kick implements Commands{
    @Override
    public String name() {
        return "/kick";
    }

    @Override
    public String description() {
        return "Remove user from chat: /kick <username>";
    }

    @Override
    public void implementation(Server server, String userName, String command) {

        if(command.split(" ").length<=1){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>"+description());
            return;
        }

        String userToKick = command.split(" ")[1];

        if(!server.isOn(userToKick)){
            server.getServerWorker(userName).writeToClient("/invalid");
            server.getServerWorker(userName).writeToClient(">>user doesn't exist.");
            return;
        }

        server.kick(userToKick);
    }
}
