package by.bsuir.nc.server.command.impl;

import by.bsuir.nc.server.command.ServerCommand;
import by.bsuir.nc.server.logger.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

public class CloseCommand implements ServerCommand {
    public void execute(Socket client, String command) throws IOException {
        Writer output = new PrintWriter(client.getOutputStream());
        Logger.log("CLOSE");
        output.write("SERVER: Connection is closed\n");
        output.flush();
        client.close();
        Logger.log("The connection is closed");
    }
}
