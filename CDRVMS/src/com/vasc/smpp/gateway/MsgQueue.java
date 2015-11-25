package com.vasc.smpp.gateway;
import java.util.Vector;
/**
 * <p>Title: M-Commerce</p>
 * <p>Description: Mobile Commerce Project</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: VASC</p>
 * @author Huynh Ngoc Tuan
 * @version 1.0
 */

public class MsgQueue {
    // Queue for ACK message
    protected Vector queue;

    public MsgQueue() {
        queue = new Vector();
    }
    /**
     * This method is used by a consummer. If you attempt to remove
     * an object from an queue is empty queue, you will be blocked
     * (suspended) until an object becomes available to remove. A
     * blocked thread will thus wake up.
     * @return the first object (the one is removed).
     */
    public Object remove() {
        synchronized(queue) {
            while(queue.isEmpty()) {  //Threads are blocked
                try {                 //if the queue is empty.
                    queue.wait();     //wait until other thread call notify().
                } catch(InterruptedException ex) { }
            }
            Object item = queue.firstElement();
            queue.removeElement(item);
            return item;
        }
    }
    public void add(Object item) {
           synchronized(queue) {
               queue.addElement(item);
               queue.notify();
           }
       }

   public long getSize(){
     return queue.size();
   }
   public boolean isEmpty() {
        return queue.isEmpty();
   }
  public void setVector(Vector v){
    this.queue = v;
  }
 public Vector getVector(){
   return this.queue;
 }
}