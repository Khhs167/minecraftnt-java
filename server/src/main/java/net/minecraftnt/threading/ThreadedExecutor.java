package net.minecraftnt.threading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ThreadedExecutor extends Thread {

    public static final Logger LOGGER = LogManager.getLogger(ThreadedExecutor.class);
    public static final boolean THREAD_INSTAKILL = false;

    public ThreadedExecutor(String name) {
        setName(name);
    }

    @Override
    public synchronized void start() {
        if(!isAlive) {
            isAlive = true;
            tasks.clear();
            weight = 0;
            super.start();
        }
    }

    private boolean isAlive = false;
    private final Queue<WeightedRunnable> tasks = new LinkedList<>();
    private int weight = 0;
    @Override
    public void run() {
        LOGGER.info("Starting execution thread");
        while (isAlive) {
            if (tasks.size() > 0) {
                WeightedRunnable runnable = tasks.poll();

                if(runnable == null){
                    LOGGER.fatal("Null task found! This means something has gone horribly wrong!");
                    LOGGER.warn("Cleaning tasks");
                    while (!tasks.isEmpty() && tasks.peek() == null)
                        tasks.poll();
                    if(THREAD_INSTAKILL)
                        kill();
                    continue;
                }

                try {
                    runnable.run();
                } catch (Exception e) {
                    LOGGER.error("Execution error: {}", e.toString());
                    LOGGER.throwing(e);
                }
                weight -= runnable.getWeight();

            } else {
                Thread.onSpinWait();
            }
        }
        LOGGER.info("Execution thread closed");
    }

    public void enqueue(Runnable runnable) {
        enqueue(runnable, 1);
    }

    public void enqueue(Runnable runnable, int weight) {
        enqueue(new WeightedRunnable(runnable, weight));
    }

    public void enqueue(WeightedRunnable weightedRunnable) {
        tasks.add(weightedRunnable);
    }

    public void addWeight(int weight) {
        this.weight += weight;
    }

    public int getWeight() {
        return weight;
    }

    public void kill() {
        isAlive = false;
    }
}
