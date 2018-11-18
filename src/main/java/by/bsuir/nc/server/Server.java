package by.bsuir.nc.server;

import by.bsuir.nc.server.command.CommandProvider;
import by.bsuir.nc.server.logger.Logger;
import by.bsuir.nc.server.util.PoolConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Server {
    private ServerSocket server;
    private boolean interrupted;
    private int port;
    private ThreadPoolExecutor pool;
    private List<ServerThread> threads;

    public Server() {
        this.port = 3345;
    }

    public Server(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        pool = new ThreadPoolExecutor(PoolConfig.INIT_SIZE, PoolConfig.MAX_SIZE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        server = new ServerSocket(port);
        threads = new ArrayList<>(PoolConfig.MAX_SIZE);

        while (!interrupted) {
            // wait for the client
            Socket client = server.accept();
            ServerThread thread = new ServerThread(client);
            pool.execute(thread);
            threads.add(thread);

            System.out.println("THREADS:");
            for (int i = 0; i < threads.size(); i++) {
                ServerThread t = threads.get(i);
                if (t.isInterrupted()) {
                    threads.remove(i);
                }
            }
            threads.stream().map(ServerThread::name).forEach(System.out::println);
        }
    }

    public void interrupt() {
        this.interrupted = true;
    }
}

class ServerThread extends Thread {
    private CommandProvider provider;
    private Socket client;
    private String name;

    public ServerThread(Socket client) {
        this.provider = CommandProvider.instance;
        this.client = client;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(client.getInetAddress().toString());
        name = Thread.currentThread().getName();
        try {
            Writer output = new PrintWriter(client.getOutputStream());
            Logger.log("Output stream initialized");

            Scanner input = new Scanner(client.getInputStream());
            Logger.log("Input stream initialized");

            // event handling cycle
            while (!client.isClosed() && input.hasNextLine()) {
                String command = input.nextLine();
                provider.command(command.split(" ")[0]).execute(client, command);
            }
        } catch (IOException e) {
            throw new RuntimeException("Client I/O exception");
        }
    }

    public String name() {
        return name;
    }
}