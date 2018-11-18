package by.bsuir.nc.server.command.impl;

import by.bsuir.nc.server.command.ServerCommand;
import by.bsuir.nc.server.logger.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

public class EchoCommand implements ServerCommand {
    public void execute(Socket client, String command) throws IOException {
        Writer output = new PrintWriter(client.getOutputStream());
        String message = command.substring(command.indexOf(' ') + 1);
        Logger.log(message);
        output.write("SERVER: " + message + '\n');
        output.flush();
    }
}
