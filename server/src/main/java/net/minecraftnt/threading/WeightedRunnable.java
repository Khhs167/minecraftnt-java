package net.minecraftnt.threading;

public class WeightedRunnable {

    private final Runnable runnable;
    private final int weight;

    public WeightedRunnable(Runnable runnable, int weight) {
        this.runnable = runnable;
        this.weight = weight;
    }

    public WeightedRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.weight = 1;
    }

    public int getWeight() {
        return weight;
    }

    public void run() {
        runnable.run();
    }
}
