package cs;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import subscription.Subutil;

public class CalculatorMTAdd extends Thread
{
  public void run()
  {
    try
    {
      while (ConsoleSRV.processData)
      {
        try
        {String times = "23:59";
        times = Constants._prop.getProperty("time_CalculatorMTAdd", times);
		
          Util.logger.info("True or False:" + isNewSession(times));

          if (isNewSession(times))
          {
            long milliSecond = System.currentTimeMillis();
            Calendar calendar = 
              Calendar.getInstance();

            calendar.setTime(new Date(milliSecond));
            int month = calendar.get(2) + 1;

            String year_month = calendar.get(1) + ""+
            Format(month);
            Util.logger.info("YEAR-MONTH:" + year_month);

            String date_time = getCalendarString(calendar);

            String sqlSelect = "SELECT service_id, command_code, sub_service, sub_code FROM icom_mtadd_cnt";

            Vector vtResult = Subutil.getVectorTable("gateway", 
              sqlSelect);

            Util.logger.info("Start Action:");

            for (int j = 0; j < vtResult.size(); j++)
            {
              Vector item = (Vector)vtResult.elementAt(j);

              String service_id = (String)item.elementAt(0);
              String command_code = (String)item.elementAt(1);
              String sub_service = (String)item.elementAt(2);
              String sub_code = (String)item.elementAt(3);

              int main_mo = getValue("sms_receive_log", 
                year_month, date_time, service_id, 
                command_code, "receive_date");
              Util.logger.info("So luong MO cua MAIN_Service:" + 
                main_mo);

              int sub_mo = getValue("sms_receive_log", 
                year_month, date_time, sub_service, 
                sub_code, "receive_date");
              Util.logger.info("So luong MO cua SUB_Service:" + 
                sub_mo);

              int main_mt = getValue("ems_send_log", year_month, 
                date_time, service_id, command_code, 
                "done_date");
              Util.logger.info("So luong MT cua MAIN_Service:" + 
                main_mo);

              int sub_mt = getValue("ems_send_log", year_month, 
                date_time, sub_service, sub_code, 
                "done_date");
              Util.logger.info("So luong MT cua SUB_Service:" + 
                sub_mo);

              int main_cdr = getValue("cdr_log", year_month, 
                date_time, service_id, command_code, 
                "done_date");
              Util.logger.info("So luong CDR cua MAIN_Service:" + 
                main_mo);

              int sub_cdr = getValue("cdr_log", year_month, 
                date_time, sub_service, sub_code, 
                "done_date");
              Util.logger.info("So luong CDR cua SUB_Service:" + 
                sub_mo);

              saveRequest(service_id, command_code, sub_service, 
                sub_code, main_mo, main_mt, main_cdr, 
                sub_mo, sub_mt, sub_cdr, date_time);
            }
          }
        }
        catch (Exception e) {
          Util.logger.info("Error Connect to Database");
        }
        sleep(5000L);
      }
    }
    catch (Exception ex) {
      Util.logger.info("Error: executeMsg.run Loi o day: " + 
        ex.toString());
    }
  }
  public static String Format(int month) {
		String monthchar = "";
		if (month < 10)
			monthchar = "0" + month;
		else
			monthchar = "" + month;
		return monthchar;

	}
  private static int getValue(String database, String year_month, String date_time, String service_id, String command_code, String date)
  {
    Connection connection = null;
    PreparedStatement statement = null;
    DBPool dbpool = new DBPool();
    int value = 0;
    ResultSet rs = null;
    try {
      connection = dbpool.getConnectionGateway();
      if (connection == null) {
        Util.logger.error("Impossible to connect to DB");
      }
      String strSelect = "SELECT count(id) FROM " + database + year_month + 
        " WHERE service_id='" + service_id + 
        "' AND upper(command_code) ='" + 
        command_code.toUpperCase() + "' AND (DATE_FORMAT(" + date + 
        ",'%d/%m/%Y') = '" + date_time + "') ";

      Util.logger.info("String Select: " + strSelect);
      statement = connection.prepareStatement(strSelect);

      if (statement.execute()) {
        rs = statement.getResultSet();
        while (rs.next()) {
          value = rs.getInt(1);
        }
      }

       return value;
    } catch (SQLException e) {
      Util.logger.error(": Error:" + e.toString());
      return value;
    } catch (Exception e) {
      Util.logger.error(": Error:" + e.toString());
      int i = value;
      return i;
    } finally {
      dbpool.cleanup(statement);
      dbpool.cleanup(connection);
    }
  }

  public static boolean isNewSession(String time)
  {
    String sTime2Queue = time;

    String[] arrH = new String[2];
    int iHour = 0;
    int iMinute = 0;
    arrH = sTime2Queue.split(":");
    if (arrH.length > 1) {
      iHour = Integer.parseInt(arrH[0].trim());
      iMinute = Integer.parseInt(arrH[1].trim());
    } else {
      iHour = Integer.parseInt(arrH[0].trim());
    }

    long milliSecond = System.currentTimeMillis();
    Calendar calendar = Calendar.getInstance();

    calendar.setTime(new Date(milliSecond));

    return ((calendar.get(11) == iHour) && 
      (calendar.get(12) >= iMinute)) || 
      (calendar.get(11) > iHour);
  }

  public static String getCalendarString(Calendar calendar)
  {
    StringBuffer sb = new StringBuffer();

    int i = calendar.get(5);
    if (i < 10)
      sb.append("0");
    sb.append(i + "/");

    i = calendar.get(2) + 1;
    if (i < 10)
      sb.append("0");
    sb.append(i + "/");

    sb.append(calendar.get(1));

    return sb.toString();
  }

  private static boolean saveRequest(String main_service, String main_code, String sub_service, String sub_code, int main_mo, int main_mt, int main_cdr, int sub_mo, int sub_mt, int sub_cdr, String date_time)
  {
    Connection connection = null;
    PreparedStatement statement = null;

    DBPool dbpool = new DBPool();
    try {
      connection = dbpool.getConnectionGateway();
      if (connection == null)
      {
        Util.logger.error("Impossible to connect to DB");
        return false;
      }
      while (true)
      {
       

        String sqlInsert = "INSERT INTO icom_mtadd_report(main_service, main_code, sub_service, sub_code, main_mo, main_mt, main_cdr, sub_mo, sub_mt, sub_cdr, date_time ) VALUES ('" + 
          main_service + 
          "','" + 
          main_code + 
          "','" + 
          sub_service + 
          "','" + 
          sub_code + 
          "'," + 
          main_mo + 
          "," + 
          main_mt + 
          "," + 
          main_cdr + 
          "," + 
          sub_mo + 
          "," + 
          sub_mt + 
          "," + 
          sub_cdr + 
          ",'" + date_time + "')";
        Util.logger.info("Insert:" + sqlInsert);
        statement = connection.prepareStatement(sqlInsert);
        if (!statement.execute()){
        Util.logger.error("Insert into icom_mtadd_report");
        return false;
        
        }
        return true;
      }
   
     
    } catch (SQLException e) {
      while (true) Util.logger.error(": Error:" + e.toString()); 
    }
    catch (Exception e) {
      while (true)
        Util.logger.error(": Error:" + e.toString());
    }
    finally {
      dbpool.cleanup(statement);
      dbpool.cleanup(connection);
    }
  }
}