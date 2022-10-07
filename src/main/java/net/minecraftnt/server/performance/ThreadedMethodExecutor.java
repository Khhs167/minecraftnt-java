package net.minecraftnt.server.performance;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ThreadedMethodExecutor extends Thread{
    private static ThreadedMethodExecutor threadedMethodExecutor;

    private static final Logger LOGGER = LogManager.getLogger(ThreadedMethodExecutor.class);

    public static ThreadedMethodExecutor getExecutor(){
        if(threadedMethodExecutor == null)
            threadedMethodExecutor = new ThreadedMethodExecutor();
        return threadedMethodExecutor;
    }

    private Queue<ThreadedMethod> threadedMethods = new LinkedList<>();
    private static int latestExecutor_id = 0;
    private final int currentExecutor_id;

    private ThreadedMethodExecutor(){
        this.currentExecutor_id = latestExecutor_id + 1;
        latestExecutor_id = currentExecutor_id;
        LOGGER.info("Creating thread..");
        this.setName("TheadedMethodExecutor_" + currentExecutor_id);
        shouldRun = true;
        this.start();
    }

    public ThreadedMethodExecutor addThreadedMethod(ThreadedMethod method){
        threadedMethods.add(method);
        return this;
    }

    @Override
    public void run(){
        LOGGER.info("Starting thread..");
        try {
            while (shouldRun) {
                if (!threadedMethods.isEmpty()) {
                    threadedMethods.remove().run();
                }
            }
            LOGGER.info("Loop ended...");
            //threadedMethods.remove().run();
        } catch(Throwable t){
            t.printStackTrace();
        }


    }

    private boolean shouldRun;

    public void halt(){
        LOGGER.info("Halting...");
        shouldRun = false;
    }
}
