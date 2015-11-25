package icom;

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

public class MsgQueue {
	// Queue for ACK message
	protected Vector queue;

	public MsgQueue() {
		queue = new Vector();
	}

	public Object remove() {
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

	public void add(Object item) {
		synchronized (queue) {
			queue.addElement(item);
			queue.notify();
		}
	}

	public long getSize() {
		return queue.size();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public void setVector(Vector v) {
		this.queue = v;
	}

	public Vector getVector() {
		return this.queue;
	}
}
