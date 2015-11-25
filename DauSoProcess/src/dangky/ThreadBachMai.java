package dangky;


import com.vmg.sms.process.MsgObject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.DBPool;

public class ThreadBachMai extends Thread {

	int processnum = 1;
	int processindex = 1;

	public static void main(String[] args) {
		ThreadBachMai smsConsole = new ThreadBachMai();

		smsConsole.start();

	}

	public static String FormatNumber(int i) {
		try {
			if (i >= 0 && i < 10)
				return "0" + i;
			else
				return "" + i;
		} catch (Exception e) {
			return "";
		}
	}

	public static long String2MilisecondNew(String strInputDate) {
		String strDate = strInputDate.trim();
		int i, nYear, nMonth, nDay, nHour, nMinute, nSecond;
		String strSub = null;
		if (strInputDate == null || "".equals(strInputDate)) {
			return 0;
		}
		strDate = strDate.replace('-', '/');
		strDate = strDate.replace('.', '/');
		strDate = strDate.replace(' ', '/');
		strDate = strDate.replace('_', '/');
		strDate = strDate.replace(':', '/');
		i = strDate.indexOf("/");

		// System.err.println("String2Milisecond.strDate:" + strDate);
		if (i < 0) {
			return 0;
		}
		try {
			// Get Nam
			String[] arrDate = strDate.split("/");
			nYear = (new Integer(arrDate[0].trim())).intValue();
			nMonth = (new Integer(arrDate[1].trim())).intValue() - 1;
			nDay = (new Integer(arrDate[2].trim())).intValue();
			nHour = (new Integer(arrDate[3].trim())).intValue();
			nMinute = (new Integer(arrDate[4].trim())).intValue();
			nSecond = (new Integer(arrDate[5].trim())).intValue();
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.set(nYear, nMonth, nDay, nHour, nMinute, nSecond);

			return calendar.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void run() {

		while (1 == 1) {

			Calendar cal = Calendar.getInstance();
			int nowyear = cal.get(Calendar.YEAR);
			int nowmonth = cal.get(Calendar.MONTH) + 1;
			String table = "bachmai" + nowyear + FormatNumber(nowmonth);
			// Lay ma game
			String sqlSelect = "SELECT user_id,name,ngayhen,note FROM " + table
					+ "  where status=1";

			Connection connection = null;
			PreparedStatement statement = null;
			ResultSet rs = null;
			DBPool dbpool = new DBPool();

			try {
				connection = dbpool.getConnection("bachmai");

				statement = connection.prepareStatement(sqlSelect);

				if (statement.execute()) {
					rs = statement.getResultSet();
					while (rs.next()) {
						String userid=rs.getString(1);
						String name=rs.getString(2);
						String ngayhen=rs.getString(3);
						String note=rs.getString(4);
						MsgObject msgobj = new MsgObject();
						msgobj.setUserid(userid);
				//		msgobj.setMobileoperator(operator);
				//		msgobj.setServiceid(service_id);
				//		msgobj.setRequestid(request_id);
					}
				}
				try {
					sleep(1000 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();

				}

			} catch (SQLException e) {
				Util.logger.error(": Error:" + e.toString());

			} catch (Exception e) {
				Util.logger.error(": Error:" + e.toString());

			} finally {
				dbpool.cleanup(rs);
				dbpool.cleanup(statement);
				dbpool.cleanup(connection);
			}

		}

	}

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

}
