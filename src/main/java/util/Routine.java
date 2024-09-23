package util;

public class Routine extends Thread{
    private final double FPS;
    private boolean killed;
    private final Semaphore semaphore = new Semaphore(0);
    private boolean paused;
    protected final Runnable task;

    public Routine(double FPS, Runnable task) {
        this.FPS = FPS;
        this.task = task;
    }

    public void run() {
        long currentTime = System.nanoTime();
        long sleepTime;
        while(!killed) {
            if (paused) {
                semaphore.forceLock();
            }
            if (System.nanoTime() - currentTime >= (long) 1000000 / FPS ) {
                currentTime = System.nanoTime();
                task.run();
                sleepTime =  (long) ((1000 / FPS) -  (System.nanoTime() - currentTime) / 1000000);
                try {
                    sleep(Math.max(sleepTime, 0));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void kill() {
        killed = true;
    }
    public void restart() {
        paused = false;
        semaphore.forceRelease();
    }
    public void pause() {
        paused = true;
    }
}
