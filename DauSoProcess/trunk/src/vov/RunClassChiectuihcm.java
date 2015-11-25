package vov;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
public class RunClassChiectuihcm extends Thread {

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
					System.out.println("AAAA");
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

			String sqlSelect = "SELECT id,current ,start_time, block1, block2, block3, block4, block5, block6,block7,block8,block9,block10, end_time, isprocess,current_times,current_question FROM icom_chiectui_confighcm WHERE active = 1";
			
			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					secretNumber.put("session", result[0]);
					result[1] = rs.getString(2);
					secretNumber.put("current", result[1]);
					result[2] = rs.getString(3);
					secretNumber.put("start", result[2]);
					result[3] = rs.getString(4);
					secretNumber.put("block1", result[3]);
					result[4] = rs.getString(5);
					secretNumber.put("block2", result[4]);
					result[5] = rs.getString(6);
					secretNumber.put("block3", result[5]);
					result[6] = rs.getString(7);
					secretNumber.put("block4", result[6]);
					result[7] = rs.getString(8);
					secretNumber.put("block5", result[7]);
					result[8] = rs.getString(9);
					secretNumber.put("block6", result[8]);
					result[9] = rs.getString(10);
					secretNumber.put("block7", result[9]);
					result[10] = rs.getString(11);
					secretNumber.put("block8", result[10]);
					result[11] = rs.getString(12);
					secretNumber.put("block9", result[11]);
					result[12] = rs.getString(13);
					secretNumber.put("block10", result[12]);
					result[13] = rs.getString(14);
					secretNumber.put("end", result[13]);
					result[14] = rs.getString(15);
					secretNumber.put("isprocess", result[14]);
					result[15] = rs.getString(16);
					secretNumber.put("current_times", result[15]);
					result[16] = rs.getString(17);
					secretNumber.put("current_question", result[16]);
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
