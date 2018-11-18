package by.bsuir.nc.server.logger;

import org.slf4j.LoggerFactory;

public final class Logger {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger("server");

    public synchronized static void log(String message) {
        log.info(Thread.currentThread().toString() + " > " + message);
    }
}
