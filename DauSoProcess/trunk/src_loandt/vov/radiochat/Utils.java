package vov.radiochat;

import java.sql.*;
import java.text.*;
import java.util.Date;
import java.util.*;

public class Utils {
  public Utils() {
  }

  public static String getStr(Object o, String strNullValue)
  {
    try
    {
      return o.toString();
    }
    catch(Exception ex)
    {
      return strNullValue;
    }
  }

  public static int ParserInt(Object o)
  {
    try
    {
      return Integer.parseInt(o.toString());
    }
    catch(Exception ex)
    {
      return 0;
    }
  }

  public static double ParserDouble(Object o)
  {
    try
    {
      return Double.parseDouble(o.toString());
    }
    catch(Exception ex)
    {
      return 0;
    }
  }

  public static java.sql.Date ParserDate(String s, String p)
  {
    try {
      java.util.Date d = new SimpleDateFormat(p).parse(s);
      return new java.sql.Date(d.getTime());
    }
    catch (ParseException ex) {
      return new java.sql.Date(0);
    }
  }

  public static String DisplayDate(String s)
  {
    if (s == null || s == "") return "";
    Date d = null;
    try {
      d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(s);
    }
    catch (ParseException ex) {
    }
    return new SimpleDateFormat("dd/MM/yyyy").format(d);
  }

  public static String DisplayDate(java.sql.Date d)
  {
    if (d == null) return "";
    java.util.Date rd = new java.util.Date(d.getTime());
    return new SimpleDateFormat("dd/MM/yyyy").format(rd);
  }

  public static String DisplayDateTime(java.sql.Date d)
  {
    if (d == null) return "";
    java.util.Date rd = new java.util.Date(d.getTime());
    return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(rd);
  }

  public static String DisplayDateTime(java.sql.Timestamp d)
  {
    if (d == null) return "";
    java.util.Date rd = new java.util.Date(d.getTime());
    return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(rd);
  }

  public static String DisplayDateTime(java.sql.Timestamp d, String type)
    {
      if (d == null) return "";
      java.util.Date rd = new java.util.Date(d.getTime());
      return new SimpleDateFormat(type).format(rd);
    }

  public static String FormatNumber(int i)
  {
    try
    {
      if (i >= 0 && i < 10)return "0" + i;
      else return "" + i;
    }
    catch (Exception e)
    {
      return "";
    }
  }

 
  public static String getOperator(String phoneNumber)
  {
    if (phoneNumber == null || phoneNumber.length() < 10) return null;
    if (!phoneNumber.substring(0,2).equalsIgnoreCase("84")) return null;
    String[] strPrefix = {"91","94","123","90","93","122","97","98","168","169","96","95","92","166","167","125","199"};
    String[] strOperator = {"GPC","GPC","GPC","VMS","VMS","VMS","VIETTEL","VIETTEL","VIETTEL","VIETTEL","EVN","SFONE","HTC","VIETTEL","VIETTEL","GPC","BEELINE"};
    int i = 0;
    for (i = 0; i < strPrefix.length; i++)
    {
      if (phoneNumber.substring(2, 2 + strPrefix[i].length()).equalsIgnoreCase(strPrefix[i])) return strOperator[i];
    }
    return null;
  }

  //Logging
  public static String displayDateTime(java.util.Date d, String f) {
    if (d == null) {
      return "";
    }
    return new SimpleDateFormat(f).format(d);
  }
  public static String displayDateTime(java.util.Date d) {
    return displayDateTime(d, "dd/MM/yyyy hh:mm:ss");
  }
  public static String getDateTimeNow() {
    Calendar now = Calendar.getInstance();
    return displayDateTime(now.getTime());
  }
  public static void log(String s) {
    System.out.println("[" + getDateTimeNow() + "] " + s);
  }
}
