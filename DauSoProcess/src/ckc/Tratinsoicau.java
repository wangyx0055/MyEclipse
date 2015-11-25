package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;
import subscription.Subutil;

public class Tratinsoicau extends Thread {

	// 2 phut 1 lan.
	private long milisecond = 1000 * 10;

	@Override
	public void run() {

		String hour2Send = "08:00";
		String hour2Finish = "19:30";
		try {
			while (ConsoleSRV.processData) {

				// Neu dung thoi gian gui
				// Util.logger.info("IS NEW SESSION " +isNewSession(hour2Send,
				// hour2Finish));
				if (!isNewSession(hour2Send, hour2Finish)) {

					// Lay thong tin khach hang chua dap ung dc yeu cau
					String selectQuery = "SELECT serviceid, userid, operator, keyword, requestid, content, cpid FROM icom_soicau_client";
					Vector vtUsers = Subutil.getVectorTable("gateway",
							selectQuery);
					// Util.logger.info("VT USER " + vtUsers);
					for (int j = 0; j < vtUsers.size(); j++) {

						Vector item = (Vector) vtUsers.elementAt(j);
						String serviceid = (String) item.elementAt(0);
						String userid = (String) item.elementAt(1);
						String operator = (String) item.elementAt(2);
						String keyword = (String) item.elementAt(3);
						String requestid = (String) item.elementAt(4);
						String mtcontent = (String) item.elementAt(5);
						String cpid = (String) item.elementAt(6);

						// Lay noi dung hom nay
						String result = getContent(keyword, cpid);

						// Neu noi dung khac nhau thi gui
						if ((!result.equalsIgnoreCase(mtcontent))
								&& (!"".equalsIgnoreCase(result))) {
							String[] sTokens = result.split("###");

							for (int i = 0; i < sTokens.length; i++) {

								sendGifMsg(serviceid, userid, operator,
										keyword, sTokens[i], new BigDecimal(
												requestid));

							}
							// Xoa ten khach hang
							deleteClient(userid);
						}
					}
					this.sleep(milisecond);
				}

			}

		} catch (Exception ex) {
			Util.logger.info("Error: executeMsg.run :" + ex.toString());
		}
	}

	// Lay noi dung va ngay thang

	// Lay noi dung trong icom_cau_data kiem tra xem co cai moi khong ?
	// gameid = COMMAND_CODE
	private String getContent(String gameid, String cpid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		// String[] result = null;
		String result = "";

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String query = "SELECT content FROM icom_cau_data WHERE ( upper(gameid) = '"
					+ gameid.toUpperCase()
					+ "') AND ( upper(subcode1) = '"
					+ cpid.toUpperCase() + "')";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY : " + query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);

				}
			}
			return result;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName()
					+ " Soi Cau Xo So: Failed" + ex.getMessage());
			ex.printStackTrace();
			return result;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	// Xoa thong tin khach hang sau khi da gui lai cho khach hang.
	private static boolean deleteClient(String userid) {
		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Xoa toan bo thong tin khach hang da dang ky
			String sqlDelete = "DELETE FROM icom_soicau_client WHERE upper(userid) ='"
					+ userid.toUpperCase() + "'";
			Util.logger.info("DELETE : " + sqlDelete);
			statement = connection.prepareStatement(sqlDelete);
			if (statement.execute()) {
				Util.logger.error("DELETE database");
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

	private void sendGifMsg(String serviceid, String userid, String operator,
			String service, String mtcontent, BigDecimal requestid) {
		try {

			MsgObject msg = new MsgObject();
			msg.setContenttype(0);
			msg.setUserid(userid);
			msg.setMobileoperator(operator);
			msg.setMsgtype(0);
			msg.setRequestid(requestid);
			msg.setServiceid(serviceid);
			msg.setKeyword(service);
			msg.setUsertext(mtcontent);

			DBUtil.sendMT(msg);

		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}

	// Kiem tra gio
	public boolean isNewSession(String hour2send, String hour2finish) {

		String[] arrSend = new String[20];
		String[] arrFinish = new String[20];
		int iHourSend = 0;
		int iMinuteSend = 0;
		int iHourFinish = 0;
		int iMinuteFinish = 0;
		arrSend = hour2send.split(":");
		arrFinish = hour2finish.split(":");
		if (arrSend.length > 1) {
			iHourSend = Integer.parseInt(arrSend[0].trim());
			iMinuteSend = Integer.parseInt(arrSend[1].trim());
		} else {
			iHourSend = Integer.parseInt(arrSend[0].trim());

		}
		if (arrFinish.length > 1) {
			iHourSend = Integer.parseInt(arrFinish[0].trim());
			iMinuteSend = Integer.parseInt(arrFinish[1].trim());
		} else {
			iHourSend = Integer.parseInt(arrFinish[0].trim());

		}

		long milliSecond = System.currentTimeMillis();
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.setTime(new java.util.Date(milliSecond));
		if ((calendar.get(calendar.HOUR_OF_DAY) >= iHourSend)
				&& ((calendar.get(calendar.HOUR_OF_DAY) < iHourFinish))) {
			return true;
		}
		if ((calendar.get(calendar.HOUR_OF_DAY) == iHourFinish)
				&& ((calendar.get(calendar.MINUTE) <= iHourFinish))) {
			return true;
		}
		return false;
	}

}
