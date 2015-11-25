package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

public class EMSException extends Exception {
    public EMSException() {
        super();
    }
    public EMSException(String s) {
        super(s);
    }
}
