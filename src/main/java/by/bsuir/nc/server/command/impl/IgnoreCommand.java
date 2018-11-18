package by.bsuir.nc.server.command.impl;

import by.bsuir.nc.server.command.ServerCommand;
import by.bsuir.nc.server.logger.Logger;

import java.io.IOException;
import java.net.Socket;

public class IgnoreCommand implements ServerCommand {
    @Override
    public void execute(Socket client, String command) throws IOException {
        Logger.log(command);
    }
}
