package my.define;

import java.util.Vector;

import MyUtility.MyLogger;

public class MTQueue
{
	MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), this.getClass().toString());

	// Queue for ACK message
	protected Vector<MTObject> queue;

	public MTQueue()
	{
		queue = new Vector<MTObject>();
	}

	public Object remove()
	{
		synchronized (queue)
		{
			while (queue.isEmpty())
			{ // Threads are blocked
				try
				{ // if the queue is empty.
					queue.wait(); // wait until other thread call notify().
				}
				catch (InterruptedException ex)
				{
					mLog.log.error(ex);
				}
			}
			Object item = queue.firstElement();
			queue.removeElement(item);
			return item;
		}
	}

	public void add(MTObject item)
	{
		synchronized (queue)
		{
			queue.addElement(item);
			queue.notify();
		}
	}

	public long getSize()
	{
		return queue.size();
	}

	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	public void setVector(Vector<MTObject> v)
	{
		this.queue = v;
	}

	public Vector<MTObject> getVector()
	{
		return this.queue;
	}
}
