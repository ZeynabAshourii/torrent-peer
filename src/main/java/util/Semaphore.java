package util;

public class Semaphore {
    private final static Semaphore mutex = new Semaphore(1);
    private final Object lock;
    private final int permits;
    private boolean forceLocked;
    private int signals;

    public Semaphore(Object obj, int permits) {
        lock = obj;
        this.permits = permits;
    }

    public Semaphore(int permits) {
        lock = new Object();
        this.permits = permits;
    }

    public void acquire() {
        /*System.out.println("acquire " + signals + " " + Thread.currentThread());
        synchronized (lock) {
            while (signals >= permits) {
                try {
                    lock.wait();
                } catch (Exception ignored) {
                }
            }
            signals++;
        }*/
    }

    public void release() {
        /*System.out.println("release " + signals + " " + Thread.currentThread());
        synchronized (lock) {
            signals--;
            lock.notify();
        }*/
    }

    public void releaseAll() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void forceLock() {
        synchronized (lock) {
            forceLocked = true;
            while (forceLocked) {
                try {
                    lock.wait();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void forceRelease() {
        synchronized (lock) {
            forceLocked = false;
            lock.notifyAll();
        }
    }

    public static Semaphore getMutex() {
        return mutex;
    }
}

