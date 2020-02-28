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

        String groupName = command.split(" ")[1];
        server.createGroup( groupName, server.getServerWorker(userName));

    }
}
