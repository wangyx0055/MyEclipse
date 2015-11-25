package icom.common;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.util.Vector;

public class Queue {
  protected Vector queue;

  public Queue() {
    queue = new Vector();
  }

  /**
   * This method is used by a consummer. If you attempt to remove
   * an object from an queue is empty queue, you will be blocked
   * (suspended) until an object becomes available to remove. A
   * blocked thread will thus wake up.
   * @return the first object (the one is removed).
   */
  public Object dequeue() {
    int i = 0;
    synchronized (queue) {
      while (queue.isEmpty()) { //Threads are blocked
        try { //if the queue is empty.
          //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>The queue is empty");
          //queue.wait(); //wait until other thread call notify().
          queue.wait(1000);
          i++;
          //System.out.println("wait until other thread call notify(): " + i);

        }
        catch (InterruptedException ex) {}
      }
      Object item = queue.firstElement();
      queue.removeElement(item);
      return item;
    }
  }

  public void enqueue(Object item) {
    synchronized (queue) {
      queue.addElement(item);
      queue.notifyAll();
    }
  }

  public int size() {
    return queue.size();
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }
}
