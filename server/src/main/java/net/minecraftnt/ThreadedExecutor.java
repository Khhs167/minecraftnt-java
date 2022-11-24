package net.minecraftnt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

public class ThreadedExecutor extends Thread {

    public static final Logger LOGGER = LogManager.getLogger(ThreadedExecutor.class);

    public ThreadedExecutor(String name) {
        setName(name);
    }

    private boolean isAlive = true;
    private final Queue<Runnable> tasks = new LinkedList<>();

    @Override
    public void run() {
        LOGGER.info("Starting execution thread");
        while (isAlive) {
            if (!tasks.isEmpty()) {
                try {
                    tasks.poll().run();
                } catch (Exception e) {
                    LOGGER.error("Execution error: {}", e.toString());
                    LOGGER.warn("Execution thread occurred! Recovering...");
                }
            }
        }
        LOGGER.info("Execution thread closed");
    }

    public void enqueue(Runnable runnable) {
        tasks.add(runnable);
    }

    public void kill() {
        isAlive = false;
    }
}
