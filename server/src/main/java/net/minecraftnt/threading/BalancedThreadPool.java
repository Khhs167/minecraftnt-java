package net.minecraftnt.threading;

import net.minecraftnt.MinecraftntData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;

public class BalancedThreadPool {
    public static final Logger LOGGER = LogManager.getLogger(BalancedThreadPool.class);
    public static final int GLOBAL_SIZE = Runtime.getRuntime().availableProcessors();

    private static BalancedThreadPool globalInstance;

    public static BalancedThreadPool getGlobalInstance() {
        if(globalInstance == null)
            globalInstance = new BalancedThreadPool("GlobalPool", GLOBAL_SIZE);
        return globalInstance;
    }

    private final ArrayList<ThreadedExecutor> executorPool = new ArrayList<>();
    private final String name;

    public BalancedThreadPool(String name, int size) {
        LOGGER.info("Creating thread pool with size {}, named '{}'", size, name);
        this.name = name;
        for(int i = 0; i < size; i++){
            executorPool.add(new ThreadedExecutor(name + "-" + i));
        }
    }

    public void start() {
        LOGGER.info("Starting thread pool '{}'", name);
        for(ThreadedExecutor executor : executorPool) {
            executor.start();
        }
    }

    public void kill() {
        LOGGER.info("Stopping thread pool '{}'", name);
        for(ThreadedExecutor executor : executorPool) {
            executor.kill();
        }
    }

    public void ping() {
        for (int i = 0; i < executorPool.size(); i++) {
            ThreadedExecutor executor = executorPool.get(i);
            if(!executor.isAlive()) {
                LOGGER.warn("Executor '{}' found dead! Replacement will be done!", executor.getName());
                executorPool.remove(executor);
            }
        }
    }

    public void enqueue(Runnable runnable) {
        enqueue(runnable, 1);
    }

    public void enqueue(Runnable runnable, int weight) {
        enqueue(new WeightedRunnable(runnable, weight));
    }

    public void enqueue(WeightedRunnable weightedRunnable) {
        int lowestWeight = Integer.MAX_VALUE;
        ThreadedExecutor executorToUse = null;
        for (ThreadedExecutor executor : executorPool) {
            if (executor.getWeight() < lowestWeight) {
                executorToUse = executor;
                lowestWeight = executor.getWeight();
            }
        }

        assert executorToUse != null;
        executorToUse.enqueue(weightedRunnable);
        executorToUse.addWeight(weightedRunnable.getWeight());
    }
}