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

        String userToKick = command.split(" ")[1];
        server.kick(userToKick);

    }
}
