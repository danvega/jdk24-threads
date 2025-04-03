package dev.danvega.threads.synchronization;

/**
 * This class demonstrates the use of synchronized methods in Java.
 * The synchronized keyword on a method ensures that only one thread
 * can execute the method at a time, providing thread safety.
 */
public class MethodSynchronizedCounter {

    private int count = 0;

    /**
     * Increments the counter by 1.
     * The synchronized keyword ensures that only one thread can execute
     * this method at a time, preventing race conditions.
     */
    public synchronized void increment() {
        count++;
    }

    /**
     * Decrements the counter by 1.
     * The synchronized keyword ensures that only one thread can execute
     * this method at a time, preventing race conditions.
     */
    public synchronized void decrement() {
        count--;
    }

    /**
     * Returns the current value of the counter.
     * This method is also synchronized to ensure that the most up-to-date
     * value is returned and to establish a happens-before relationship with
     * other synchronized methods.
     * 
     * @return the current count
     */
    public synchronized int getCount() {
        return count;
    }

}
