package icom.common.util;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
* @author ICom
 * @version 1.0
 */

// Time the execution

final class Timer {
    private long start;
    private long end;

    public Timer() {
        reset();
    }

    public void start() {
        System.gc();
        start = System.currentTimeMillis();
    }

    public void end() {
        System.gc();
        end = System.currentTimeMillis();
    }

    public long duration() {
        return (end - start);
    }

    public void reset() {
        start = 0;
        end = 0;
    }

    public static void main(String s[]) {
        // simple example
        Timer t = new Timer();
        t.start();
        for (int i = 0; i < 80; i++) {
            System.out.print(".");
        }
        t.end();
        System.out.println("\n" + t.duration());
    }
}
