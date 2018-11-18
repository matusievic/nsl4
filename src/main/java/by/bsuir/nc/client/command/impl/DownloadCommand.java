package by.bsuir.nc.client.command.impl;

import by.bsuir.nc.client.command.ClientCommand;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.ByteBuffer;

public class DownloadCommand implements ClientCommand {
    @Override
    public void execute(Socket server, String command) throws IOException {
        System.out.println("INFO: Downloading started");

        Writer output = new PrintWriter(server.getOutputStream());
        DataInputStream input = new DataInputStream(server.getInputStream());

        output.write(command + '\n');
        output.flush();

        byte[] lengthBuffer = new byte[Long.BYTES];
        input.read(lengthBuffer);

        long length = ByteBuffer.wrap(lengthBuffer).getLong();
        if (length == 0) {
            System.out.println("INFO: File not found");
            return;
        }

        String fileName = command.substring(command.indexOf(' ') + 1);
        OutputStream downloadedFile = new FileOutputStream("client" + File.separator + fileName, true);

        long position = 0;
        byte[] buffer = new byte[1];
        int count;
        long startTime = System.nanoTime();
        while ((count = input.read(buffer)) > 0) {
            downloadedFile.write(buffer, 0, count);
            downloadedFile.flush();
            position += count;

            if (position >= length) {
                break;
            }
        }

        long finishTime = System.nanoTime();
        downloadedFile.close();

        System.out.println("INFO: Downloading finished");
        System.out.println("Bitrate: " + (length / ((finishTime - startTime) / 1000000000.0)) + " bps");
    }
}
