package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

/**
 * This exception is thrown when fail to connect to ORACLE
 */
public class DBException extends Exception {
    public DBException() {
        super();
    }
    public DBException(String s) {
        super(s);
    }

}
