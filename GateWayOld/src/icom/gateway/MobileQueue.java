package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import java.util.Vector;

/**
 * Stores list of mobiles along with last time (MO) using service.
 * The maximum number of entries is limited.
 */

public class MobileQueue implements java.io.Serializable {
    final static int MAX_QUEUE_SIZE = 20;
    Vector queue = new Vector();

    public void add(String mobile, long time) {
        add(new MobileQueueInfo(mobile, time));
    }

    public void add(MobileQueueInfo info) {
        if (info == null)
            return;
        synchronized (queue) {
            // Delete first element if queue full
            if (queue.size() >= MAX_QUEUE_SIZE) {
                queue.removeElementAt(0);
            }
            queue.add(info);
        }
    }

    public void update(String mobile, long time) {
        update(new MobileQueueInfo(mobile, time));
    }
    public void update(MobileQueueInfo info) {
        if (info == null) return;
        // remove
        this.remove(info.mobile);
        // add
        this.add(info);
    }

    public long lookup(String mobile) {
        return getTime(mobile);
    }

    public long getTime(String mobile) {
        if (mobile == null) return -1;
        long time = 0;
        synchronized (queue) {
            for (int i=0; i <queue.size(); i++) {
                MobileQueueInfo info = (MobileQueueInfo) queue.elementAt(i);
                if (mobile.equals(info.mobile)) {
                    time = info.time;
                    break;
                }
            }
        }
        return time;
    }

    public void remove(String theKey) {
        if (theKey == null)
            return;
        synchronized (queue) {
            for (int i = 0; i < queue.size(); i++) {
                MobileQueueInfo info = (MobileQueueInfo) queue.elementAt(i);
                if (theKey.equals(info.mobile)) {
                    queue.removeElementAt(i);
                    break;
                }
            }
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

    // Test
    public static void main(String args[]) {
        MobileQueue queue = new MobileQueue();
        queue.add(new MobileQueueInfo("84904060008", 128));
        queue.add(new MobileQueueInfo("84989152696", 127));
    }
}

//==============================================
class MobileQueueInfo {
    String mobile = null;
    long time = 0; //secs from 1970: last usage time
    public MobileQueueInfo(String mobile, long time) {
        this.mobile = mobile;
        this.time = time;
    }
}
