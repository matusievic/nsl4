package by.bsuir.nc.server.command.impl;

import by.bsuir.nc.server.command.ServerCommand;
import by.bsuir.nc.server.logger.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.time.LocalDateTime;

public class TimeCommand implements ServerCommand {
    public void execute(Socket client, String command) throws IOException {
        Writer output = new PrintWriter(client.getOutputStream());
        Logger.log("TIME");
        output.write("SERVER: " + LocalDateTime.now().toString() + '\n');
        output.flush();
    }
}
