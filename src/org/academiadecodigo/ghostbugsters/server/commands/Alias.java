package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class Alias implements Commands{
    @Override
    public String name() {
        return "/alias";
    }

    @Override
    public String description() {
        return "Change username: /name <new_name>";
    }

    @Override
    public void implementation(Server server, String userName, String command) {

        String new_name=command.split(" ")[1];

        synchronized (server.getSwHashTable()) {
            server.getServerWorker(userName).setUserName(new_name);
            server.rename(server.getServerWorker(userName), userName, command.split(" ")[1]);
        }
    }
}
