package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class Help implements Commands{
    @Override
    public String name() {
        return "/help";
    }

    @Override
    public String description() {
        return "List all commands: /help";
    }

    @Override
    public void implementation(Server server, String userName, String command) {
        //TODO implement, get package, list files.
    }
}
