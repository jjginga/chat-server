package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class List implements Commands{



    @Override
    public String name() {
        return "/list";
    }

    @Override
    public String description() {
        return "List all users: /list";
    }

    @Override
    public void implementation(Server server, String userName, String command) {

        server.getServerWorker(userName).writeToClient(server.list());

    }
}
