package cs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;

public class PlaySpam extends Thread {

	String SEND_MSG = "2idol-Duy Khanh&Duyen Anh Idol - ngay 25/10.Neu ban dong y nhan ve tham gia, soan tin nhan DY gui 8051(500d/sms)";

	private int updateData(String user_id, String operator) {
		int ireturn = 1;
		String sqlInsert = "insert into ems_send_queue(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID, CPId)"
				+ "values('"
				+ user_id
				+ "','8051','"
				+ operator
				+ "','DY',0,'"
				+ SEND_MSG + "',0,0,26)";

		String sqlUpdate = "update _icom_list2send set done='1' , submitdate='"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "' where user_id='" + user_id
				+ "'";
		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnectionGateway();
			if (SEND_MSG.length() > 160) {
				return -1;
			}

			//System.out.println("ins:" + sqlInsert);
			Util.logger.sysLog(2, this.getClass().getName(), ": Send2:"
					+ user_id);
			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.sysLog(2, this.getClass().getName(),
						": uppdate Statement: Update sms_receive_queue Failed");
				ireturn = -1;

			} else {
				//System.out.println("ins:" + sqlUpdate);
				Util.logger.sysLog(2, this.getClass().getName(), ": update:"
						+ user_id);

				if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
					Util.logger
							.sysLog(2, this.getClass().getName(),
									": uppdate Statement: Update sms_receive_queue Failed");
					ireturn = -1;

				}
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
			String soperator = "";
			// user_id, done, submitdate, operator
			stmt = connection
					.prepareStatement(
							"SELECT user_id ,operator FROM _icom_list2send  where done <>'1' limit 10",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					suserid = rs.getString(1);
					soperator = rs.getString(2);
					updateData(suserid, soperator.toUpperCase());

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

	public void run() {

		while (ConsoleSRV.processData) {

			try {

				checkandplay();

				sleep(1000 * 10 * 1);

			} catch (InterruptedException ex3) {
			}
		}
	}
}
