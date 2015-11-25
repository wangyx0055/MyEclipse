package cs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; //import java.sql.Timestamp;
//import java.util.Hashtable;
import java.util.Random;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.DateProc;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;

public class PlaySieutoc extends Thread {
	
	private String user_id = "841252984827";
	private String operator = "GPC";
	private String session_id = "31";
	private String end_date = "2009-07-19 23:57:00";

	private int updateData() {
		int ireturn = 1;
		String sqlUpdate = "insert into sms_receive_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO,  REQUEST_ID)"
				+ "values('"
				+ user_id
				+ "','8551','"
				+ operator
				+ "','AA','AC',0)";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.sysLog(2, this.getClass().getName(),
						": uppdate Statement: Update sms_receive_queue Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					":Update  sms_receive_queue Failed");
			ireturn = -1;
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private void checkandplay() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String suserid = "";

			stmt = connection.prepareStatement(
					"SELECT user_id FROM icom_stduser  where session_id="
							+ session_id + " order by lasttime  desc limit 1",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					suserid = rs.getString(1);

					if (!suserid.equalsIgnoreCase(user_id)) {
						updateData();
					}

				}

			}

		} catch (Exception ex3) {
			Util.logger.error("Khong load duoc LastestCS@loadLOTTERY_MTADD"
					+ ex3.toString());
			ex3.printStackTrace();

		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

	}

	public static long String2MilisecondNew(String strInputDate) {
		// System.err.println("String2Milisecond.strInputDate:" + strInputDate);
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

			// System.err.println("nYear: " + nYear + "@"+ nMonth + "@" +
			// nDay+"@"+ nHour + "@" + nMinute + "@" + nSecond);
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.set(nYear, nMonth, nDay, nHour, nMinute, nSecond);

			return calendar.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void run() {

		while (ConsoleSRV.processData) {

			try {
				long endtime = String2MilisecondNew(end_date);
				long currtime = System.currentTimeMillis();

				if (currtime < endtime) {
					checkandplay();
				} else {
					Util.logger.info("Playsieutoc:" + endtime + "@" + currtime);
				}

				Random iRandom = new Random();
				int i = iRandom.nextInt(50 * 1);
				i = i + 10;
				sleep(1000 * i * 1);

			} catch (InterruptedException ex3) {
				//System.out.println("Error:InterruptedException "
				//		+ ex3.toString());
			}
		}
	}
}
