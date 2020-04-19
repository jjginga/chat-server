package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class Alias implements Commands{



    @Override
    public String name() {
        return "/alias";
    }

    @Override
    public String description() {
        return "Change username: /alias <new_name>";
    }

    @Override
    public void implementation(Server server, String userName, String command) {

        synchronized (server.getSwHashTable()) {

            if (command.split(" ").length<=1){
                server.getServerWorker(userName).writeToClient("/invalid");
                server.getServerWorker(userName).writeToClient(">>invalid. " + description());
                return;
            }

            String new_name=command.split(" ")[1];

            if(server.isOn(new_name)){
                server.getServerWorker(userName).writeToClient("/invalid");
                server.getServerWorker(userName).writeToClient(">>name already taken.");
                return;
            }

                server.getServerWorker(userName).setUserName(new_name);
                server.rename(server.getServerWorker(userName), userName, command.split(" ")[1]);
        }
    }
}
