package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public class File implements Commands {
    @Override
    public String name() {
        return "/file";
    }

    @Override
    public String description() {
        return "Send file to user /file <user_name> <file_path>";
    }

    @Override
    public void implementation(Server server, String userName, String command) {
        server.sendFile(userName , command.split(" ")[1], command.split(" ")[2]);
    }
}
