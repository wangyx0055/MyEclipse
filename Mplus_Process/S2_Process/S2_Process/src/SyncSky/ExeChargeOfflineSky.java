package SyncSky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import icom.Constants;
import icom.DBPool;
import icom.Sender;
import icom.common.Util;

public class ExeChargeOfflineSky extends Thread {
	String className = "ExeChargeOfflineSky ";

	public void run() {
		Util.logger.info(className + " start");
		while (Sender.getData) {
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			DBPool pool = new DBPool();
			String sql = "SELECT * FROM list_send WHERE  COMMAND_CODE LIKE '%SKY' and IS_SYNC_SKY = 0";
			try {
				String curTime = new SimpleDateFormat("HH:mm")
						.format(new Date());
				String timeRun = Constants._prop.getProperty(
						"time_sync_mr_sky", "05:00");
				String[] arrTimeRun = timeRun.split(";");
				for (int i = 0; i < arrTimeRun.length; i++) {
					if (curTime.equals(arrTimeRun[i])) {
						Util.logger.info(className
								+ " time to synchronization mr to sky.");
						List<String> listUser = new ArrayList<String>();
						con = pool.getConnectionGateway();
						if (con != null) {
							ps = con.prepareStatement(sql,
									ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_UPDATABLE);
							rs = ps.executeQuery();
							while (rs.next()) {
								String userId = rs.getString("USER_ID");
								String amount = rs.getInt("AMOUNT") + "";
								String requestId = rs.getString("REQUEST_ID");
								listUser.add(userId + ";" + amount+";"+requestId);
								rs.updateInt("IS_SYNC_SKY", 1);

								rs.updateRow();
							}
						} else {
							Util.logger
									.error(className + " connection is null");
						}

						for (String user : listUser) {
							String msisdn = user.split(";")[0];
							String cost = user.split(";")[1];
							String request  = user.split(";")[2];
							Util.logger.info(className
									+ " synchronization mr to sky @user_id:"
									+ msisdn + "\t@amount:" + cost);

							String response = SyncAPI.SyncMR(msisdn, cost,request);

							Util.logger.info(className + " response:"
									+ response);
						}

					}
				}
			} catch (Exception e) {
				Util.logger.info(className + " @error:" + e);
				Util.logger.printStackTrace(e);
			} finally {
				pool.cleanup(rs, ps);
				pool.cleanup(con);
			}

			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
