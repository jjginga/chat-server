package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

public interface Commands {

    public String name();

    public String description();

    public void implementation(Server server, String userName, String command);
}
