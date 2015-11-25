package com.vasc.smpp.cdr;
import java.sql.*;
import java.util.*;

public class Untitled1 {

    public static Connection getConnection() {
      Connection conn = null;
      String url = "jdbc:mysql://localhost:3306/";
      String dbName = "cc_invalid?useUnicode=true&characterEncoding=UTF-8";
      String driver = "com.mysql.jdbc.Driver";
      String userName = "root";
      String password = "itrd";


      try
      {
          Class.forName(driver).newInstance();
          conn = DriverManager.getConnection(url + dbName, userName, password);
          System.out.print("OK");
      }
      catch (Exception e)
      {
           System.out.print("?????");
          e.printStackTrace();
          conn = null;
      }

      return conn;
    }
    public static void main (String args[]) {
      Connection conn;
      conn = getConnection();

}



}
  /*
  public static void addMoreConnection2Pool(int number) {
            GatewayCDR.util.log("Connecting to database.....");
            for (int i=0; i< number; i++) {
                java.sql.Connection conn = null;
                try {
                    conn = util.getDBConnectionMySQL("localhost",
                                                 "cc_invalid",
                                                "root",
                                                 "itrd");
                } catch (java.sql.SQLException ex) {
                    GatewayCDR.util.log("Error: " + ex.getMessage());
                    GatewayCDR.util.log("Khong noi dc voi database roi, xem lai di.!!!!!!!!!!!");
                    System.exit(1);
                }
                if (conn != null) {
                    dbPool.add(conn);
                }
            }
        }



        public Connection getDBConnectionMySQL(String server, String database,
                                                   String username, String password) throws
                SQLException {
              Connection conn = null;
              String url = "jdbc:mysql://" + server + ":3306/" + database;
              try {
                //Class.forName("oracle.jdbc.driver.OracleDriver");
                   Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url, username, password);
              }
              catch (ClassNotFoundException ex) {
                System.out.print(ex);
                throw new SQLException(ex.getMessage());
              }
              return conn;
            }

*/

