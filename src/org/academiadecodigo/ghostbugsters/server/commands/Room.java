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
        server.messageGroup(command.split(" ")[1],command.substring(command.indexOf(" ", 2)), server.getServerWorker(userName));
    }
}
