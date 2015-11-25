// Serial SMS Transfer for handling SMS communication with a mobile terminal
// (C) 2003 by VASC M-Commerce

package com.vasc.sms.common;

import javax.naming.*;
import java.sql.*;
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.Date;

/**
 * This class contains some common methods which
 * are usually used in your programs.
 * @author Nguyen Trong Tho
 * @version 1.0
 */
public class Utilities {
    //*********************************//
    //CHANGE THESE PARAMETERS IF NEEDED//
    //*********************************//
    /**
     * The URL to the server on which weblogicServer is running
     */
    String url = "t3://192.168.75.214:80";
    String user = null;
    String password = null;
    /**
     * The data source for database connection.
     * Modify this value if needed.
     */
    final static String DATA_SOURCE = "SMSDataSource";
    /**
     * A kind of trigger constant, used to display debug info.
     * Set this constant to the value of <code> true </code> for debugging
     */
    final static boolean VERBOSE = true;

    // CONSTRUCTOR
    public Utilities() {

    }


    /**
     * You want to connect to weblogicServer to lookup any object
     * via its JNDI name.
     * @return a Context to connect to WeblogicServer
     */
    public Context getWebLogicContext()
        throws NamingException {
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        p.put(Context.PROVIDER_URL, url);
        if (user != null) {
            p.put(Context.SECURITY_PRINCIPAL, user);
            if (password == null)
                    password = "";
            p.put(Context.SECURITY_CREDENTIALS, password);
        }
        return new InitialContext(p);
    }

    public static String exceptPrefix(String mobile){
      if(mobile.length()<=3) return mobile;
      if(mobile.substring(0,2).equals("04") || mobile.substring(0,2).equals("08"))
        return mobile.substring(2);
      if(mobile.substring(0,3).equals("084") || mobile.substring(0,3).equals("084"))
        return mobile.substring(3);
      return mobile;
    }
    /**
     * For Oracle Database only
     * @param user
     * @param password
     * @param url
     * @return
     */
    public Connection getDBConnection(String user, String password, String SID)
    {
        Connection conn = null;
        try {
            Properties props = new Properties();
            props.put("user", user);
            props.put("password", password);
            Driver myDriver = (Driver) Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            String url = "jdbc:oracle:oci8:@" + SID;
            conn = myDriver.connect(url, props);
        }
        catch (Exception e)
        {
            log("Utilities.getDBConnection: " + e.getMessage());
        }
        return conn;
    }
    /**
     * The method is used for displaying debug info dynamically, instead of the
     * <code> System.out.println()</code> method.
     * You can turn on and off the debug info by setting the constant
     * <code> VERBOSE </code> to true and false respectively.
     */
    public void log(String s) {
        if (VERBOSE) System.out.println(s);
    }

    /**
     * This method will close the connection and statement if presented.
     * If failt, this throws an EJBException which requires
     * the calling method to declare.
     * @param Connection con The connection to your database
     * @param PreparedStatement ps
     * @exception EJBException
     */
    public void cleanup(Connection con, PreparedStatement ps) {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (Exception e) {
            log("Error closing Connection: " + e.getMessage());
        }
    }

    public void closeConnection(Connection connection, Statement statement) {
    try {
      if (statement != null) {
        statement.close();
      }
    }catch(SQLException e) { }
    try {
      if (connection != null) {
        connection.close();
      }
    } catch(SQLException e) { }
  }
}

