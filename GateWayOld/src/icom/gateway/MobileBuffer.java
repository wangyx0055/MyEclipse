package icom.gateway;

/**
 * <p>Title: SMPP Project</p>
 * <p>Description: M-Commerce Center</p>
 * <p>Copyright: 2004</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

/**
 * Buffer luu cac so dien thoai cung voi so luong tin nhan va
 * thoi gian su dung dich vu gan nhat trong ngay
 *
 * This buffer stores all info about mobiles sent and received SMS
 * Info includes:
 *   - Last time sent SMS (MO)
 *   - Number of SMS sent this day
 *   - Last time received SMS (MT)
 *   - Number of SMS received this day
 **/

public class MobileBuffer implements java.io.Serializable {
 //   private static Logger logger = new Logger("MobileBuffer");

    static final int NUM_OF_BUFFERS = 16; //2^n (n=4)
    static final MobileBuffer[] bufferArray = new MobileBuffer[NUM_OF_BUFFERS];
    static {
        System.out.println("initializing " + NUM_OF_BUFFERS + " buffers...");
        for (int idx = bufferArray.length-1; idx >= 0; idx--) {
            bufferArray[idx] = new MobileBuffer();
        }
        System.out.println("OK");
    }

    public static int DAY_IN_BUFFER = today();
    private static int today() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new java.util.Date(System.currentTimeMillis()));
        return calendar.get(calendar.DAY_OF_MONTH);
    }

    // key: mobile(849xxx); value: MobileBufferInfo
    private Map hashmap = new HashMap();

    private void addInternal(String theKey, MobileBufferInfo theVal) {
        if (theKey == null || theVal == null) {
            return;
        }
        synchronized (hashmap) {
            hashmap.put(theKey, theVal);
        }
    }

    /* Private methods */
    private MobileBufferInfo lookupInternal(String theKey) {
        MobileBufferInfo theVal = null;
        synchronized (hashmap) {
            theVal = (MobileBufferInfo) hashmap.get(theKey);
        }
        return theVal;
    }
    private void updateInternal(String theKey, MobileBufferInfo theVal) {
        if (theKey == null || theVal == null) {
            return;
        }
        synchronized (hashmap) {
            hashmap.put(theKey, theVal);
        }
    }
    private void removeInternal(String theKey) {
        if (theKey == null) {
            return;
        }
        synchronized (hashmap) {
            hashmap.remove(theKey);
        }
    }
    // Removes all mappings from this map
    private void clearInternal() {
        synchronized (hashmap) {
            hashmap.clear();
        }
    }
    private int sizeInternal() {
        synchronized (hashmap) {
            return hashmap.size();
        }
    }

    /* Public Class methods */
    public static void add(String mobile, MobileBufferInfo info) {
        int h = mobile.hashCode();
        int bucket = h & (NUM_OF_BUFFERS-1); // pick a buffer
        bufferArray[bucket].addInternal(mobile, info);
    }
    public static MobileBufferInfo lookup(String mobile) {
        int h = mobile.hashCode();
        int bucket = h & (NUM_OF_BUFFERS-1); // pick a buffer
        return bufferArray[bucket].lookupInternal(mobile);
    }
    public static void update(String mobile, MobileBufferInfo info) {
        int h = mobile.hashCode();
        int bucket = h & (NUM_OF_BUFFERS - 1); // pick a buffer
        bufferArray[bucket].updateInternal(mobile, info);
    }
    public static void remove(String mobile) {
        int h = mobile.hashCode();
        int bucket = h & (NUM_OF_BUFFERS - 1); // pick a buffer
        bufferArray[bucket].removeInternal(mobile);
    }
    // Removes all mappings from buffer
    public static void clearAll() {
        for (int i=0; i<bufferArray.length; i++) {
            System.out.println("Clearing buffer " + i);
            bufferArray[i].clearInternal();
        }
        //Reset day
        DAY_IN_BUFFER = today();
    }
    public static int size() {
        int size = 0;
        for (int i=0; i<bufferArray.length; i++) {
            System.out.println("size of buffer " + i + ": " + bufferArray[i].sizeInternal());
            size += bufferArray[i].sizeInternal();
        }
        return size;
    }
}

//==============================================
class MobileBufferInfo {
    String mobile = null; // 849xxx
    long mo_Time = 0; // secs from 1970: last usage time.
    long mt_Time = 0; // secs from 1970: last usage time.
    int  mo_Counter = 0; // number of MOs  in current day.
    int  mt_Counter = 0; // number of MTs  in current day.
    int cdr_Counter = 0; // number of CDRs in current day.
    String msg = "";

    public MobileBufferInfo() {

    }
    public MobileBufferInfo(String mobile, long moTime, long mtTime, int moCounter, int mtCounter, int cdrCounter) {
        this.mobile = mobile;
        this.mo_Time = moTime;
        this.mt_Time = mtTime;
        this.mo_Counter  = moCounter;
        this.mt_Counter  = mtCounter;
        this.cdr_Counter = cdrCounter;
    }
    public MobileBufferInfo(String mobile, long moTime, long mtTime, int moCounter, int mtCounter, int cdrCounter, String msg) {
      this.mobile = mobile;
      this.mo_Time = moTime;
      this.mt_Time = mtTime;
      this.mo_Counter  = moCounter;
      this.mt_Counter  = mtCounter;
      this.cdr_Counter = cdrCounter;
      this.msg = msg;
  }

}

