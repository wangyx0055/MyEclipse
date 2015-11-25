package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class Xoacsdl extends Thread {

	String hour2send = "09:00";
	String info_type = "BAO";
	String dbcontent = "game_ch";
	long milisecond = 1000;

	@Override
	public void run() {
		String gameid = "giomophien";

		try {
			HashMap _option = new HashMap();

			String options = getPhien(gameid);
			_option = getParametersAsString(options);
			String phien3 = ((String) _option.get("phien3")).toUpperCase();
			Util.logger.info("Phien 3: " + phien3);
			while (ConsoleSRV.processData) {
				// Sau gio ket thuc phien 3 moi xoa co so du lieu
				if (isNewSession(phien3)) {
					deleteClient(dbcontent);
				}
				this.sleep(milisecond * 180);
			}
		} catch (Exception ex) {
			Util.logger.info("Error: executeMsg.run :" + ex.toString());
		}
	}

	/* return date with format: dd/mm/yyyy */
	public static String Milisec2DDMMYYYY(long ts) {
		if (ts == 0) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts));

			String strTemp = Integer.toString(calendar
					.get(calendar.DAY_OF_MONTH));
			if (calendar.get(calendar.DAY_OF_MONTH) < 10) {
				strTemp = "0" + strTemp;
			}
			if (calendar.get(calendar.MONTH) + 1 < 10) {
				return strTemp + "/0" + (calendar.get(calendar.MONTH) + 1)
						+ "/" + calendar.get(calendar.YEAR);
			} else {
				return strTemp + "/" + (calendar.get(calendar.MONTH) + 1) + "/"
						+ calendar.get(calendar.YEAR);
			}
		}
	}

	// Trong khoang 18h den 18h 3 phut thi xoa csdl
	public boolean isNewSession(String phien3) {
		int iHour = 0;
		int iMinute = 0;

		Calendar calendar = Calendar.getInstance();
		int i = calendar.get(Calendar.DAY_OF_WEEK);

		if ((i == 1) || (i > 5)) {
			return false;
		} else {
			String[] sTokens = phien3.split(":");
			iHour = Integer.parseInt(sTokens[0]);
			iMinute = Integer.parseInt(sTokens[1]);

			if (((calendar.get(calendar.HOUR_OF_DAY) == iHour) && ((calendar
					.get(calendar.MINUTE) >= iMinute) && (calendar
					.get(calendar.MINUTE) <= iMinute + 3)))) {

				return true;
			}
			return false;
		}
	}

	private static boolean deleteClient(String dbcontent) {

		Connection connection = null;
		PreparedStatement statement = null;
		boolean result = true;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		int total = 0;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlDelete = "DELETE FROM " + dbcontent;

			statement = connection.prepareStatement(sqlDelete);
			Util.logger.info("Delete:" + sqlDelete);
			if (statement.execute()) {
				return result;
			}

			return false;
		} catch (SQLException e) {
			Util.logger.error(": Error1: " + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error2: " + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static String getPhien(String gameid) {

		Connection connection = null;
		PreparedStatement statement = null;
		String result = "";
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			// Nguoi doat giai theo phien va thoi gian ket thuc ??
			String sqlSelect = "SELECT content FROM icom_textbase_data "
					+ " WHERE upper(gameid) ='" + gameid.toUpperCase() + "'";
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SELECT:" + sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
				}
			}

			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	// Get time
	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "&");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			// setParameter(key, value);
			_params.put(key, value);
		}

		return _params;

	}

}
