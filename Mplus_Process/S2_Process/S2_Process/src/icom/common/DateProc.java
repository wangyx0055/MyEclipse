package icom.common;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.sql.Timestamp;
import java.util.Calendar;

public class DateProc {
  public DateProc() {}

  public static java.sql.Timestamp createTimestamp() {
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    return new java.sql.Timestamp( (calendar.getTime()).getTime());
  }

  public static java.sql.Timestamp createDateTimestamp(java.util.Date date) {
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    calendar.setTime(date);
    return new java.sql.Timestamp( (calendar.getTime()).getTime());
  }

  public static java.sql.Timestamp String2Timestamp(String strInputDate) {
    String strDate = strInputDate.trim();
    int i, nYear, nMonth, nDay;
    String strSub = null;
    if (strInputDate == null || "".equals(strInputDate)) {
      return null;
    }
    strDate = strDate.replace('-', '/');
    strDate = strDate.replace('.', '/');
    strDate = strDate.replace(' ', '/');
    strDate = strDate.replace('_', '/');
    i = strDate.indexOf("/");
    if (i < 0) {
      return null;
    }
    try {
      strSub = strDate.substring(0, i);
      nDay = (new Integer(strSub.trim())).intValue();
      strDate = strDate.substring(i + 1);
      i = strDate.indexOf("/");
      if (i < 0) {

        return null;
      }
      strSub = strDate.substring(0, i);
      nMonth = (new Integer(strSub.trim())).intValue() - 1; // Month begin from 0 value
      strDate = strDate.substring(i + 1);
      if (strDate.length() < 4) {
        if (strDate.substring(0, 1).equals("9")) {
          strDate = "19" + strDate.trim();
        }
        else {
          strDate = "20" + strDate.trim();
        }
      }
      nYear = (new Integer(strDate)).intValue();
      java.util.Calendar calendar = java.util.Calendar.getInstance();
      calendar.set(nYear, nMonth, nDay);
      return new java.sql.Timestamp( (calendar.getTime()).getTime());
    }
    catch (Exception e) {
      return null;
    }
  }

  public static String getDateTimeString(java.sql.Timestamp ts) {
    if (ts == null) {
      return "";
    }
    return Timestamp2DDMMYYYY(ts) + " " + Timestamp2HHMMSS(ts, 1);
  }

  /*return date with format: dd/mm/yyyy */
  public static String getDateString(java.sql.Timestamp ts) {
    if (ts == null) {
      return "";
    }
    return Timestamp2DDMMYYYY(ts);
  }

  public static String getTimeString(java.sql.Timestamp ts) {
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    calendar.setTime(new java.util.Date(ts.getTime()));
    return calendar.get(calendar.HOUR_OF_DAY) + ":" +
        calendar.get(calendar.MINUTE) + ":" +
        calendar.get(calendar.SECOND);
  }

  /*return date with format: dd/mm/yyyy */
  public static String Timestamp2DDMMYYYY(java.sql.Timestamp ts) {
    if (ts == null) {
      return "";
    }
    else {
      java.util.Calendar calendar = java.util.Calendar.getInstance();
      calendar.setTime(new java.util.Date(ts.getTime()));

      String strTemp = Integer.toString(calendar.get(calendar.
          DAY_OF_MONTH));
      if (calendar.get(calendar.DAY_OF_MONTH) < 10) {
        strTemp = "0" + strTemp;
      }
      if (calendar.get(calendar.MONTH) + 1 < 10) {
        return strTemp + "/0" + (calendar.get(calendar.MONTH) + 1) +
            "/" +
            calendar.get(calendar.YEAR);
      }
      else {
        return strTemp + "/" + (calendar.get(calendar.MONTH) + 1) + "/" +
            calendar.get(calendar.YEAR);
      }
    }
  }

  /*return date with format: dd/mm/yy */
  public static String Timestamp2DDMMYY(java.sql.Timestamp ts) {
    int endYear;
    if (ts == null) {
      return "";
    }
    else {
      java.util.Calendar calendar = java.util.Calendar.getInstance();
      calendar.setTime(new java.util.Date(ts.getTime()));

      String strTemp = Integer.toString(calendar.get(calendar.
          DAY_OF_MONTH));
      endYear = calendar.get(calendar.YEAR) % 100;
      if (calendar.get(calendar.DAY_OF_MONTH) < 10) {
        strTemp = "0" + strTemp;
      }
      if (calendar.get(calendar.MONTH) + 1 < 10) {
        if (endYear < 10) {
          return strTemp + "/0" + (calendar.get(calendar.MONTH) + 1) +
              "/0" +
              endYear;
        }
        else {
          return strTemp + "/0" + (calendar.get(calendar.MONTH) + 1) +
              "/" +
              endYear;
        }
      }
      else {
        if (endYear < 10) {
          return strTemp + "/" + (calendar.get(calendar.MONTH) + 1) +
              "/0" +
              endYear;
        }
        else {
          return strTemp + "/" + (calendar.get(calendar.MONTH) + 1) +
              "/" +
              endYear;
        }
      }
    }
  }

  /**
   * @Author Huynh Ngoc Tuan
   * @param ts          Timestamp to convert
   * @param iStyle      0: 24h,  otherwise  12h clock
   * @return HHMMSS's String
   */
  public static String Timestamp2HHMMSS(java.sql.Timestamp ts, int iStyle) {
    if (ts == null) {
      return "";
    }
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    calendar.setTime(new java.util.Date(ts.getTime()));

    String strTemp;
    if (iStyle == 0) {
      strTemp = Integer.toString(calendar.get(calendar.HOUR_OF_DAY));
    }
    else {
      strTemp = Integer.toString(calendar.get(calendar.HOUR));
    }

    if (strTemp.length() < 2) {
      strTemp = "0" + strTemp;
    }
    if (calendar.get(calendar.MINUTE) < 10) {
      strTemp += ":0" + calendar.get(calendar.MINUTE);
    }
    else {
      strTemp += ":" + calendar.get(calendar.MINUTE);
    }
    if (calendar.get(calendar.SECOND) < 10) {
      strTemp += ":0" + calendar.get(calendar.SECOND);
    }
    else {
      strTemp += ":" + calendar.get(calendar.SECOND);
    }

    if (iStyle != 0) {
      if (calendar.get(calendar.AM_PM) == calendar.AM) {
        strTemp += " AM";
      }
      else {
        strTemp += " PM";
      }
    }
    return strTemp;
  }

  /**
   * Convert timestamp to string which 24h pattern
   * @param ts Timestamp to convert
   * @return date time used for 24 hour clock
   */
  public static String getDateTime24hString(java.sql.Timestamp ts) {
    if (ts == null) {
      return "";
    }
    return Timestamp2DDMMYYYY(ts) + " " + Timestamp2HHMMSS(ts, 0);
  }

  /**
   *  Convert Timestamp to string which 12h pattern
   *  @param ts Timestamp to convert
   *  @return date time used for 12 hour clock
   */
  public static String getDateTime12hString(java.sql.Timestamp ts) {
    if (ts == null) {
      return "";
    }
    return Timestamp2DDMMYYYY(ts) + " " + Timestamp2HHMMSS(ts, 1);
  }

  /**
   * Convert Timestamp to string which ddmmyyyy partern
   * @param ts Timestamp to convert
   * @param iDayPlus    number of day to add
   * @return string dd/mm/yyyy from a Timestamp + a addtional day
   */
  public static String TimestampPlusDay2DDMMYYYY(java.sql.Timestamp ts,
                                                 int iDayPlus) {
    if (ts == null) {
      return "";
    }
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    calendar.setTime(new java.util.Date(ts.getTime()));
    int iDay = calendar.get(calendar.DAY_OF_MONTH);
    calendar.set(calendar.DAY_OF_MONTH, iDay + iDayPlus);

    java.sql.Timestamp tsNew = new java.sql.Timestamp( (calendar.getTime()).
        getTime());
    return Timestamp2DDMMYYYY(tsNew);
  }

  /**
   * Get one day before the date
   * @param ts Date to get previous
   * @return The Timestamp before ts
   */
  public static Timestamp getPreviousDate(Timestamp ts) {
    if (ts == null) {
      return null;
    }
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    calendar.setTime(new java.util.Date(ts.getTime()));
    int iDay = calendar.get(calendar.DAY_OF_MONTH);
    calendar.set(calendar.DAY_OF_MONTH, iDay - 1);

    java.sql.Timestamp tsNew = new java.sql.Timestamp( (calendar.getTime()).
        getTime());
    return tsNew;
  }

  /**
   * return the dd/mm/yyyy of current month
   *   eg:   05/2002  -->   31/05/2002
   *
   * @param strMonthYear  : input string mm/yyyy
   * @return the String format of latest day
   */
  public static String getLastestDateOfMonth(String strMonthYear) {
    String strDate = strMonthYear;
    int i, nYear, nMonth, nDay;
    String strSub = null;

    i = strDate.indexOf("/");
    if (i < 0) {
      return "";
    }
    strSub = strDate.substring(0, i);
    nMonth = (new Integer(strSub)).intValue(); // Month begin from 0 value
    strDate = strDate.substring(i + 1);
    nYear = (new Integer(strDate)).intValue();

    boolean leapyear = false;
    if (nYear % 100 == 0) {
      if (nYear % 400 == 0) {
        leapyear = true;
      }
    }
    else
    if ( (nYear % 4) == 0) {
      leapyear = true;
    }

    if (nMonth == 2) {
      if (leapyear) {
        return "29/" + strDate;
      }
      else {
        return "28/" + strDate;
      }
    }
    else {
      if ( (nMonth == 1) || (nMonth == 3) || (nMonth == 5) || (nMonth == 7) ||
          (nMonth == 8) || (nMonth == 10) || (nMonth == 12)) {
        return "31/" + strDate;
      }
      else
      if ( (nMonth == 4) || (nMonth == 6) || (nMonth == 9) ||
          (nMonth == 11)) {
        return "30/" + strDate;
      }
    }
    return "";
  }

  public static java.sql.Timestamp String2TimestampOld(String strInputDate) {
    String strDate = strInputDate.trim();
    java.util.Calendar calendar_curr = java.util.Calendar.getInstance();
    int i, nYear, nMonth, nDay;
    String strSub = null;
    if (strInputDate == null || "".equals(strInputDate)) {
      return null;
    }
    strDate = strDate.replace('-', '/');
    strDate = strDate.replace('.', '/');
    strDate = strDate.replace(' ', '/');
    strDate = strDate.replace('_', '/');
    i = strDate.indexOf("/");

    if (i < 0) {
      strDate = strDate + "/" +
          (calendar_curr.get(calendar_curr.MONTH) + 1);
      strDate = strDate + "/" + calendar_curr.get(calendar_curr.YEAR);
      i = strDate.indexOf("/");

      //return null;
    }
    try {
      strSub = strDate.substring(0, i);
      nDay = (new Integer(strSub.trim())).intValue();
      strDate = strDate.substring(i + 1);
      i = strDate.indexOf("/");
      if (i < 0) {
        strDate = strDate + "/" + calendar_curr.get(calendar_curr.YEAR);
        i = strDate.indexOf("/");
        //return null;
      }
      strSub = strDate.substring(0, i);
      //conglt
      nMonth = (new Integer(strSub.trim())).intValue() - 1; // Month begin from 0 value
      //nMonth = (new Integer(strSub.trim())).intValue() ;
      strDate = strDate.substring(i + 1);
      if (strDate.length() < 4) {
        if (strDate.substring(0, 1).equals("9")) {
          strDate = "19" + strDate.trim();
        }
        else {
          strDate = "20" + strDate.trim();
        }
      }
      nYear = (new Integer(strDate)).intValue();
      java.util.Calendar calendar = java.util.Calendar.getInstance();
      calendar.set(nYear, nMonth, nDay);
      return new java.sql.Timestamp( (calendar.getTime()).getTime());
    }
    catch (Exception e) {
      return null;
    }
  }

  public static java.sql.Timestamp String2TimestampNewOld(String strInputDate) {

    java.util.Calendar calendar_curr = java.util.Calendar.getInstance();
    int i, nYear, nMonth, nDay;
    String strSub = null;
    if (strInputDate == null || "".equals(strInputDate)) {
      return null;
    }
    String strDate = strInputDate.trim();
    //System.out.println("strDate in:" + strDate);
    strDate = strDate.replace('-', '/');
    strDate = strDate.replace('.', '/');
    strDate = strDate.replace(' ', '/');
    strDate = strDate.replace('_', '/');
    i = strDate.indexOf("/");

    if (i < 0) {
      if (strDate.length() > 2) { // check truong hop ngay thang viet lien nhau ddmmyyyy
        //strSub = strDate.substring(0,2);

        strDate = strDate.substring(0, 2) + "/" + strDate.substring(2);
        //  System.out.println("strDate 1:" + strDate);
        i = strDate.indexOf("/");

      }
      else {

        strDate = strDate + "/" +
            (calendar_curr.get(calendar_curr.MONTH) + 1);
        strDate = strDate + "/" + calendar_curr.get(calendar_curr.YEAR);
        // System.out.println("strDate 2 : " + strDate);
        i = strDate.indexOf("/");
      }
      //return null;
    }
    try {
      strSub = strDate.substring(0, i);
      nDay = (new Integer(strSub.trim())).intValue();
      strDate = strDate.substring(i + 1);
      i = strDate.indexOf("/");
      if (i < 0) {
        if (strDate.length() > 2) {
          strDate = strDate.substring(0, 2) + "/" +
              strDate.substring(2);
          // System.out.println("strDate 3 " + strDate);
          i = strDate.indexOf("/");
        }
        else {
          strDate = strDate + "/" +
              calendar_curr.get(calendar_curr.YEAR);
          i = strDate.indexOf("/");
          //  System.out.println("strDate 4 " + strDate);
        }
        //return null;
      }
      strSub = strDate.substring(0, i);
      //conglt
      nMonth = (new Integer(strSub.trim())).intValue() - 1; // Month begin from 0 value
      //nMonth = (new Integer(strSub.trim())).intValue() ;
      strDate = strDate.substring(i + 1);
      i = strDate.indexOf("/");
      if (i > 0) {
        strDate = strDate.substring(0, i);
      }
      if (strDate.length() < 4) {
        //if (strDate.length() == 1)
        //  {
        //    strDate = "0" + strDate;
        //  }
        if (strDate.substring(0, 1).equals("9")) {

          strDate = "19" + strDate.trim();
        }
        else {
          strDate = "20" + strDate.trim();
        }
      }
      //System.out.println("year text:" + strDate);
      nYear = (new Integer(strDate)).intValue();
      java.util.Calendar calendar = java.util.Calendar.getInstance();
      calendar.set(nYear, nMonth, nDay);
      return new java.sql.Timestamp( (calendar.getTime()).getTime());
    }
    catch (Exception e) {
      return null;
    }
  }

  public static String String2StringDDMMYYYY(String strInputdate) {
    java.sql.Timestamp timenew = String2TimestampNew(strInputdate);
    return Timestamp2DDMMYYYY(timenew);
  }

  public static java.sql.Timestamp String2TimestampNew(String strInputDate) {

    java.util.Calendar calendar_curr = java.util.Calendar.getInstance();
    int i, nYear, nMonth, nDay;
    String strSub = null;
    if (strInputDate == null || "".equals(strInputDate)) {
      return null;
    }
    String strDate = strInputDate.trim();
    //System.out.println("strDate in:" + strDate);
    strDate = strDate.replace('-', '/');
    strDate = strDate.replace('.', '/');
    strDate = strDate.replace(' ', '/');
    strDate = strDate.replace('_', '/');
    i = strDate.indexOf("/");

    if (i < 0) {
      String currYear = "" + calendar_curr.get(calendar_curr.YEAR);
      if ( (strDate.length() == "dmyyyy".length()) &&
          (strDate.indexOf(currYear) > 0)) {
        strDate = "0" + strDate.substring(0, 1) + "0" + strDate.substring(1, 2) +
            currYear;
      }
      if ( (strDate.length() == "ddmyyyy".length()) &&
          (strDate.indexOf(currYear) > 0)) {
        int itemp = (new Integer(strDate.substring(0, 2))).intValue();
        if (itemp > 9) {
          strDate = strDate.substring(0, 2) + "0" + strDate.substring(2, 3) +
              currYear;
        }
        else {
          strDate = "0" + strDate.substring(0, 1) + strDate.substring(1, 3) +
              currYear;
        }
      }

      if (strDate.length() > 2) { // check truong hop ngay thang viet lien nhau ddmmyyyy
        //strSub = strDate.substring(0,2);

        strDate = strDate.substring(0, 2) + "/" + strDate.substring(2);
        //  System.out.println("strDate 1:" + strDate);
        i = strDate.indexOf("/");

      }
      else {
        if ( (new Integer(strDate.trim())).intValue() >
            calendar_curr.get(calendar_curr.DAY_OF_MONTH)) {
          if (calendar_curr.get(calendar_curr.MONTH) == 0) {
            strDate = strDate + "/" + "12";
            strDate = strDate + "/" +
                (calendar_curr.get(calendar_curr.YEAR) - 1);
          }
          else {
            strDate = strDate + "/" +
                (calendar_curr.get(calendar_curr.MONTH));
            strDate = strDate + "/" + calendar_curr.get(calendar_curr.YEAR);
          }
        }
        else {
          strDate = strDate + "/" +
              (calendar_curr.get(calendar_curr.MONTH) + 1);
          strDate = strDate + "/" + calendar_curr.get(calendar_curr.YEAR);

        }

        // System.out.println("strDate 2 : " + strDate);
        i = strDate.indexOf("/");
      }
      //return null;
    }
    try {
      strSub = strDate.substring(0, i);
      nDay = (new Integer(strSub.trim())).intValue();
      strDate = strDate.substring(i + 1);
      i = strDate.indexOf("/");
      if (i < 0) {
        if (strDate.length() > 2) {
          if (strDate.length() == 3 || strDate.length() == 5) {
            strDate = "0" + strDate.substring(0, 1) + "/" +
                strDate.substring(1);
          }
          else {
            strDate = strDate.substring(0, 2) + "/" +
                strDate.substring(2);
            // System.out.println("strDate 3 " + strDate);
          }
          i = strDate.indexOf("/");
        }
        else {
          strDate = strDate + "/" +
              calendar_curr.get(calendar_curr.YEAR);
          i = strDate.indexOf("/");
          //  System.out.println("strDate 4 " + strDate);
        }
        //return null;
      }
      strSub = strDate.substring(0, i);
      //conglt
      nMonth = (new Integer(strSub.trim())).intValue() - 1; // Month begin from 0 value
      //nMonth = (new Integer(strSub.trim())).intValue() ;
      strDate = strDate.substring(i + 1);
      i = strDate.indexOf("/");
      if (i > 0) {
        strDate = strDate.substring(0, i);
      }
      if (strDate.length() < 4) {
        //if (strDate.length() == 1)
        //  {
        //    strDate = "0" + strDate;
        //  }
        if (strDate.substring(0, 1).equals("9")) {

          strDate = "19" + strDate.trim();
        }
        else {
          strDate = "20" + strDate.trim();
        }
      }
      //System.out.println("year text:" + strDate);
      if (strDate.trim().length() == 4) {
        nYear = (new Integer(strDate)).intValue();
      }
      else {
        nYear = calendar_curr.get(calendar_curr.YEAR);
      }

      java.util.Calendar calendar = java.util.Calendar.getInstance();
      calendar.set(nYear, nMonth, nDay);
      return new java.sql.Timestamp( (calendar.getTime()).getTime());
    }
    catch (Exception e) {
      return null;
    }
  }

  public static int getDayOfWeek(Timestamp ts) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new java.util.Date(ts.getTime()));
    return calendar.get(calendar.DAY_OF_WEEK);
  }

  public static boolean isValidString2Timestamp(String strInputDate) {

    java.util.Calendar calendar_curr = java.util.Calendar.getInstance();
    int curMonth = calendar_curr.get(calendar_curr.MONTH);
    int curYear = calendar_curr.get(calendar_curr.YEAR);
    int i, nYear, nMonth, nDay;
    String strSub = null;
    if (strInputDate == null || "".equals(strInputDate)) {
      return true;
    }
    String strDate = strInputDate.trim();
    //System.out.println("strDate in:" + strDate);
    strDate = strDate.replace('-', '/');
    strDate = strDate.replace('.', '/');
    strDate = strDate.replace(' ', '/');
    strDate = strDate.replace('_', '/');
    i = strDate.indexOf("/");

    if (i < 0) {
      String currYear = "" + calendar_curr.get(calendar_curr.YEAR);
      if ( (strDate.length() == "dmyyyy".length()) &&
          (strDate.indexOf(currYear) > 0)) {
        strDate = "0" + strDate.substring(0, 1) + "0" + strDate.substring(1, 2) +
            currYear;
      }
      if ( (strDate.length() == "ddmyyyy".length()) &&
          (strDate.indexOf(currYear) > 0)) {
        int itemp = (new Integer(strDate.substring(0, 2))).intValue();
        if (itemp > 9) {
          strDate = strDate.substring(0, 2) + "0" + strDate.substring(2, 3) +
              currYear;
        }
        else {
          strDate = "0" + strDate.substring(0, 1) + strDate.substring(1, 3) +
              currYear;
        }
      }

      if (strDate.length() > 2) { // check truong hop ngay thang viet lien nhau ddmmyyyy
        //strSub = strDate.substring(0,2);

        strDate = strDate.substring(0, 2) + "/" + strDate.substring(2);
        //  System.out.println("strDate 1:" + strDate);
        i = strDate.indexOf("/");

      }
      else {
        strDate = strDate + "/" +
            (calendar_curr.get(calendar_curr.MONTH) + 1);
        strDate = strDate + "/" + calendar_curr.get(calendar_curr.YEAR);
        // System.out.println("strDate 2 : " + strDate);
        i = strDate.indexOf("/");
      }
      //return null;
    }
    try {
      strSub = strDate.substring(0, i);
      nDay = (new Integer(strSub.trim())).intValue();
      strDate = strDate.substring(i + 1);
      i = strDate.indexOf("/");
      if (i < 0) {
        if (strDate.length() > 2) {
          //strDate = strDate.substring(0, 2) + "/" +
          //   strDate.substring(2);
          // System.out.println("strDate 3 " + strDate);
          //i = strDate.indexOf("/");
          if (strDate.length() == 3 || strDate.length() == 5) {
            strDate = "0" + strDate.substring(0, 1) + "/" +
                strDate.substring(1);
          }
          else {
            strDate = strDate.substring(0, 2) + "/" +
                strDate.substring(2);
            // System.out.println("strDate 3 " + strDate);
          }
          i = strDate.indexOf("/");

        }
        else {
          strDate = strDate + "/" +
              calendar_curr.get(calendar_curr.YEAR);
          i = strDate.indexOf("/");
          //  System.out.println("strDate 4 " + strDate);
        }
        //return null;
      }
      strSub = strDate.substring(0, i);
      //conglt
      nMonth = (new Integer(strSub.trim())).intValue() - 1; // Month begin from 0 value
      //nMonth = (new Integer(strSub.trim())).intValue() ;
      strDate = strDate.substring(i + 1);
      i = strDate.indexOf("/");
      if (i > 0) {
        strDate = strDate.substring(0, i);
      }
      if (strDate.length() < 4) {
        // if (strDate.length() == 1)
        //   {
        //     strDate = "0" + strDate;
        //   }
        if (strDate.substring(0, 1).equals("9")) {

          strDate = "19" + strDate.trim();
        }
        else {
          strDate = "20" + strDate.trim();
        }
      }
      if (strDate.trim().length() == 4) {
        nYear = (new Integer(strDate)).intValue();
      }
      else {
        nYear = calendar_curr.get(calendar_curr.YEAR);
      }
      //nYear = (new Integer(strDate)).intValue();
      java.util.Calendar calendar = java.util.Calendar.getInstance();

      //System.out.println("nYear text:" + nYear);
      //System.out.println("nMonth text:" + nMonth);
      //System.out.println("nDay text:" + nDay);

      calendar.set(nYear, nMonth, nDay);
      if (nDay > 31 || nDay < 1) {
        return false;
      }
      if (nMonth > 12 || nMonth < 0) {
        return false;
      }
      // if (nYear <curYear-10) {
      //  return false;
      //}
      //if (nYear == curYear)  {
      // if (nMonth > curMonth) {
      //    return false;
      //  }
      //}
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

}
