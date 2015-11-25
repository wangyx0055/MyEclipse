package vov;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
public class RunClassIphone extends Thread {

	public static Hashtable SECRET_NUMBER = null;
	public boolean LOAD = false;
	public static int isProcess = 0;

	public static String getSECRET_NUMBER(String secretNumber) {
		String retobj = "";

		try {
			retobj = (String) SECRET_NUMBER.get(secretNumber);
			return retobj;

		} catch (Exception e) {
		}

		return "";
	}

	@Override
	public void run() {

		try {

			while (ConsoleSRV.processData) {
				try {

					getTime();
					this.LOAD = true;
					this.sleep(1000);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			Util.logger.error("Error Run: " + ex.toString());
		}
	}

	private Hashtable getTime() {

		String[] result = new String[17];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		Hashtable secretNumber = new Hashtable();

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return SECRET_NUMBER;
			}

			String sqlSelect = "SELECT session,begintime, endtime, isprocess FROM iphone_session WHERE active = 1";
			
			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					secretNumber.put("session", result[0]);
					result[1] = rs.getString(2);
					secretNumber.put("begintime", result[1]);
					result[2] = rs.getString(3);
					secretNumber.put("endtime", result[2]);
					result[3] = rs.getString(4);
					secretNumber.put("isprocess", result[3]);
					
				}

				SECRET_NUMBER = secretNumber;

				if ("1".equalsIgnoreCase(getSECRET_NUMBER("isprocess"))) {
					isProcess = 1;
				} else {
					isProcess = 0;
				}
			}

			return SECRET_NUMBER;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return SECRET_NUMBER;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return SECRET_NUMBER;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

}
