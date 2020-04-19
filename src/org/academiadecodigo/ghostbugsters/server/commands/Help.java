package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

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

        String help = "Change username: /alias <new_name>\n"+
                "Create a group: /create <groupname>\n"+
                "Send file to user /file <user_name> <file_path>\n"+
                "Join a group: /join <groupname>\n"+
                "Remove user from chat: /kick <username>\n"+
                "List all users: /list\n"+
                "Send Message to a group: /room <groupname> <message>\n"+
                "Send a private message to a user: /whisper <user_name> <message>";

        server.getServerWorker(userName).writeToClient(help);

    }



}
