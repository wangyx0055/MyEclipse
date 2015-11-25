package baohangngay;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class imusic extends ContentAbstract{

	@SuppressWarnings("unchecked")
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		
	
		return null;
	}
	private static boolean saveRequest(String userid, String serviceid,
			String keyword, String operator, BigDecimal requestid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO icom_register_music( userid, serviceid, service, operator, requestid) VALUES ('"
					+ userid
					+ "','"
					+ serviceid
					+ "','"
					+ keyword
					+ "','"
					+ operator + "','" + requestid + "')";
			Util.logger.info("INSERT :" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert into icom_thuebao_bao");
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

	String getDate(int iday) {
		long milliSecond = System.currentTimeMillis();

		long lNewtime = milliSecond + (iday - 1) * 24 * 60 * 60 * 1000;

		return Milisec2DDMMYYYY(lNewtime);
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
	private static int getUserID(String userid) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT day_remain FROM icom_thuebao_bao WHERE userid= '"
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

	private static boolean updaterequest(String userid) {

		Connection connection = null;
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			int day = getUserID(userid);
			// Plus day
			day = day + 7;

			// Update day remain into icom_thuebao_bao
			String sqlUpdate = "UPDATE icom_thuebao_bao SET day_remain =" + day
					+ " WHERE userid = '" + userid.toUpperCase() + "'";
			Util.logger.info("UPDATE : " + sqlUpdate);
			statement2 = connection.prepareStatement(sqlUpdate);
			if (statement2.execute()) {
				Util.logger.error("Update day_remain to icom_thuebao_bao");
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
			dbpool.cleanup(statement1);
			dbpool.cleanup(statement2);
			dbpool.cleanup(connection);
		}
	}

}
