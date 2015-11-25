package com.vasc.smpp.gateway;

/**
 * <p>Title: M-Commerce Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
* @author Huynh Ngoc Tuan
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