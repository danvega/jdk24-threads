package dev.danvega.threads.synchronization;

/**
 * This class demonstrates the use of synchronized blocks in Java.
 * The synchronized block ensures that only one thread can execute
 * the block at a time, providing thread safety.
 */
public class BlockSynchronizedCounter {

    private int count = 0;
    private final Object lock = new Object(); // Object used for synchronization

    /**
     * Increments the counter by 1.
     * The synchronized block ensures that only one thread can execute
     * the critical section at a time, preventing race conditions.
     */
    public void increment() {
        synchronized (lock) {
            count++;
        }
    }

    /**
     * Returns the current value of the counter.
     * This method also uses a synchronized block to ensure that the most up-to-date
     * value is returned and to establish a happens-before relationship with
     * other synchronized blocks.
     * 
     * @return the current count
     */
    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }

}
