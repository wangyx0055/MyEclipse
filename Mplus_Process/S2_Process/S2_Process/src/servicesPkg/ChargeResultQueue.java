package servicesPkg;

import java.util.Vector;

public class ChargeResultQueue {
	protected Vector<ChargeResultInfo> queue;

	public ChargeResultQueue() {
		queue = new Vector<ChargeResultInfo>();
	}

	public ChargeResultInfo getElement(int index) {
		synchronized (queue) {
			ChargeResultInfo item = queue.get(index);
			queue.removeElement(item);
			return item;
		}
	}

	public void add(ChargeResultInfo item) {
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

	public void setVector(Vector<ChargeResultInfo> v) {
		this.queue = v;
	}

	public Vector<ChargeResultInfo> getVector() {
		return this.queue;
	}
}
