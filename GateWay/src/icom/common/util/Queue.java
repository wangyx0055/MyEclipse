package icom.common.util;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author ICom
 * @version 1.0
 */

import java.util.Vector;

public class Queue {
	protected Vector queue;

	public Queue() {
		queue = new Vector();
	}

	/**
	 * This method is used by a consummer. If you attempt to remove an object
	 * from an queue is empty queue, you will be blocked (suspended) until an
	 * object becomes available to remove. A blocked thread will thus wake up.
	 * 
	 * @return the first object (the one is removed).
	 */
	public Object dequeue() {
		synchronized (queue) {
			while (queue.isEmpty()) { // Threads are blocked
				try { // if the queue is empty.
					queue.wait(); // wait until other thread call notify().
				} catch (InterruptedException ex) {
				}
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
