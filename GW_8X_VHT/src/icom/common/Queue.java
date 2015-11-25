package icom.common;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author ICom
 * @version 1.0
 */

import icom.gateway.Logger;

import java.util.LinkedList;


public class Queue {
    protected LinkedList queue;
    public Queue() {
        queue = new LinkedList();
    }

    /**
     * This method is used by a consummer. If you attempt to remove
     * an object from an queue is empty queue, you will be blocked
     * (suspended) until an object becomes available to remove. A
     * blocked thread will thus wake up.
     * @return the first object (the one is removed).
     */
    public Object dequeue() {
        synchronized (queue) {
            while (queue.isEmpty()) { //Threads are blocked
                try { //if the queue is empty.
                    queue.wait(); //wait until other thread call notify().
                   // queue.wait(100); //
                    
                    
                }
                catch (InterruptedException ex) {
                	Logger.error("Queue", "InterruptedException:" + ex.getMessage());
                	Logger.printStackTrace(ex);
                }
            }
            Object item = queue.removeFirst();
            return item;
        }
    }

    public void enqueue(Object item) {
        synchronized (queue) {
            queue.addLast(item);
            //queue.notify();
            queue.notifyAll();
        }
    }
    
    public void enqueuecongt1(Object item) {
        synchronized (queue) {
            queue.addLast(item);
           // queue.notify();
        }
    }

    public int size() {
        synchronized (queue) {
            return queue.size();
        }
    }

    public boolean isEmpty() {
        synchronized (queue) {
            return queue.isEmpty();
        }
    }

}
