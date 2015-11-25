package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class tuvanxoso extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		
		// Kiem tra thoi gian gui voi lai thoi gian hien tai cua he thong.
		// Lay thoi gian hien tai cua he thong
		
		

		return null;
	}

	// Lay sdt cua khach hang dua ve dang chuan co 84
	public String ValidISDN(String sISDN) {
		Util.logger.sysLog(2, this.getClass().getName(), "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.sysLog(2, this.getClass().getName(), "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "+" + tempisdn;
			} else {
				tempisdn = "+84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.sysLog(2, this.getClass().getName(), "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	public static String getCalendarString(Calendar calendar) {
		StringBuffer sb = new StringBuffer();
		int i;
		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		i = calendar.get(Calendar.MONTH) + 1;
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append("-");
		i = calendar.get(Calendar.DAY_OF_MONTH);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(" ");
		i = calendar.get(Calendar.HOUR_OF_DAY);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(":");
		i = calendar.get(Calendar.MINUTE);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(":");
		i = calendar.get(Calendar.SECOND);
		if (i < 10)
			sb.append("0");
		sb.append(i);

		return (sb.toString());
	}

	// Lay time hien tai
	public static String getCalendarString080000(Calendar calendar) {
		StringBuffer sb = new StringBuffer();
		int i;

		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		i = calendar.get(Calendar.MONTH) + 1;
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append("-");
		i = calendar.get(Calendar.DAY_OF_MONTH);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(" 08:00:00");
		return (sb.toString());
	}

	// Luu so lan da dc nhan hay gui cua 1 sdt
	// Luu lan dau tien.

	private static boolean saverequest(String userid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_infoclient( userid) VALUES ('"
					+ userid + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_giamcan");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static int getUserID(String userid) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT times FROM icom_client WHERE userid= '"
				+ userid.toUpperCase() + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getInt(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
		return -1;
	}
	
	// Chuyen thoi gian cua hien tai ve thoi dinh dang moi
	public static String Milisec2YYYYMMDDHHMISSS(long ts) {
		if (ts == 0) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts));
			return getCalendarString(calendar);

		}
	}
	
	/*
	 * Thay nhieu dau _____ -> _
	 */
	public static String replaceAllWhiteWithOne(String sInput) {
		String strTmp = sInput.trim();
		String strResult = "";
		for (int i = 0; i < strTmp.length(); i++) {
			char ch = strTmp.charAt(i);
			if (ch == ' ') {
				for (int j = i; j < strTmp.length(); j++) {
					char ch2 = strTmp.charAt(j);
					if (ch2 != ' ') {
						i = j;
						strResult = strResult + ' ' + ch2;
						break;
					}
				}

			} else {
				strResult = strResult + ch;
			}

		}
		return strResult;
	}
}