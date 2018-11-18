package by.bsuir.nc.server.command.impl;

import by.bsuir.nc.server.command.ServerCommand;
import by.bsuir.nc.server.logger.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class UploadCommand implements ServerCommand {
    private Map<String, Long> interruptedUploads = new HashMap<>();

    @Override
    public void execute(Socket client, String command) throws IOException {
        Logger.log(command);

        DataOutputStream output = new DataOutputStream(client.getOutputStream());
        DataInputStream input = new DataInputStream(client.getInputStream());

        String fileName = command.substring(command.indexOf(' ') + 1);
        OutputStream downloadedFile = new FileOutputStream("server" + File.separator + fileName, true);

        long initPosition = 0;
        if (interruptedUploads.containsKey(fileName)) {
            initPosition = interruptedUploads.get(fileName);
        }
        output.writeLong(initPosition);
        output.flush();

        byte[] lengthBuffer = new byte[Long.BYTES];
        input.read(lengthBuffer);

        long length = ByteBuffer.wrap(lengthBuffer).getLong();
        if (length == 0) {
            Logger.log("File not found");
            return;
        }

        byte[] buffer = new byte[1];
        int count;
        long receivedByteCount = 0;
        try {
            while ((count = input.read(buffer)) > 0) {
                downloadedFile.write(buffer, 0, count);
                downloadedFile.flush();
                receivedByteCount += count;

                if (receivedByteCount >= length) {
                    if (interruptedUploads.containsKey(fileName)) {
                        interruptedUploads.remove(fileName);
                    }
                    break;
                }

                if (!client.isConnected() || client.isClosed()) {
                    throw new SocketException("Client disconnected");
                }
            }
        } catch (SocketException e) {
            interruptedUploads.put(fileName, receivedByteCount - 1);
            Logger.log("Uploading interrupted");
            return;
        }

        downloadedFile.close();
        Logger.log("Uploading finished");
    }
}
