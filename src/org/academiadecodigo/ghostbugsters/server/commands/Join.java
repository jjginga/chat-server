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

        String groupName = command.split(" ")[1];
        server.joinGroup(groupName, server.getServerWorker(userName));
    }
}
