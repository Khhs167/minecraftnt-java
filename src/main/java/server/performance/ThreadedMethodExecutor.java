package server.performance;

import java.util.LinkedList;
import java.util.Queue;

public class ThreadedMethodExecutor {
    private static ThreadedMethodExecutor threadedMethodExecutor;

    public static ThreadedMethodExecutor getExecutor(){
        System.out.println("Getting thread..");
        if(threadedMethodExecutor == null)
            threadedMethodExecutor = new ThreadedMethodExecutor();
        return threadedMethodExecutor;
    }

    private Thread executionThread;
    private Queue<ThreadedMethod> threadedMethods = new LinkedList<>();

    private ThreadedMethodExecutor(){
        System.out.println("Creating thread..");
        executionThread = new Thread(this::executionLoop, "ThreadedExecution");
        executionThread.start();
    }

    public ThreadedMethodExecutor addThreadedMethod(ThreadedMethod method){
        threadedMethods.add(method);
        return this;
    }

    private void executionLoop(){
        System.out.println("Starting thread..");
        try {
            while (shouldRun) {
                System.out.println("Checking for threadedMethod");
                if (!threadedMethods.isEmpty()) {
                    System.out.println("Running threadedMethod");
                    threadedMethods.remove().run();
                }
            }
        } catch(Throwable t){
            t.printStackTrace();
        }


    }

    private boolean shouldRun = true;

    public void halt(){
        System.out.println("Halting...");
        shouldRun = false;
    }
}
