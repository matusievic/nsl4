package by.bsuir.nc.server.command.impl;

import by.bsuir.nc.server.command.ServerCommand;
import by.bsuir.nc.server.logger.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class DownloadCommand implements ServerCommand {
    private Map<String, Long> interruptedDownloads = new HashMap<>();

    @Override
    public void execute(Socket client, String command) throws IOException {
        Logger.log(command);

        DataOutputStream output = new DataOutputStream(client.getOutputStream());
        String fileName = command.split(" ")[1];
        File file = new File("server" + File.separator + fileName);
        FileInputStream input;

        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            output.writeLong(0);
            output.flush();
            Logger.log("File not found");
            return;
        }

        long length = file.length();

        if (interruptedDownloads.containsKey(fileName)) {
            long position = interruptedDownloads.get(fileName);
            length -= input.skip(position);
        }

        output.writeLong(length);
        output.flush();


        byte[] buffer = new byte[1];
        long sentByteCount = 0;
        int count;
        try {
            while ((count = input.read(buffer)) > 0) {
                output.write(buffer, 0, count);
                output.flush();
                sentByteCount += count;
            }
        } catch (SocketException e) {
            interruptedDownloads.put(fileName, sentByteCount - 1);
            Logger.log("Downloading interrupted");
            return;
        }

        input.close();

        if (interruptedDownloads.containsKey(fileName)) {
            interruptedDownloads.remove(fileName);
        }

        Logger.log("Downloading finished");
    }
}
