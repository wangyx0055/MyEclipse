package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;

public class Tuvanxoso extends ContentAbstract {

	private static final String WRONG_SYTAX = "Ban da nhan tin sai cu phap.Nhan lai TVXS MB gui 6757";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		try {

			// Lay thong tin nguoi gui
			String sInfo = msgObject.getUsertext();
			String userText = replaceAllSpaceWithOne(sInfo);
			String userid = msgObject.getUserid();
			String serviceid = msgObject.getServiceid();
			String sKeyword = msgObject.getKeyword();
			Timestamp timesend = msgObject.getTTimes();
			BigDecimal requestid = msgObject.getRequestid();
			String operator = msgObject.getMobileoperator();

			String[] sTokens = userText.split(" ");

			// Neu nhan tin sai cu phap
			if (sTokens.length < 2) {
				msgObject.setUsertext(WRONG_SYTAX);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
			} else {

				// Neu sai
				if (!isStartWithMB(sTokens[1])) {
					msgObject.setUsertext(WRONG_SYTAX);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
				} else {

					// Neu okie
					// Kiem tra xem co nam trong thoi gian tu 19h30 den 11h
					// khong sendtuvan = 0 : chua gui tin tu van. sendkqdb = 0
					// chua gui ket qua dac biet
					if (isNewSession()) {
						msgObject
								.setUsertext("Cam on ban da yeu cau phuc vu. Tu van giai DB se gui den luc 11h");
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));

						// Luu vao danh sach se gui
						saveClient(userid, serviceid, sKeyword, operator,
								requestid, timesend, 0, 0);
					} else {
						// Lay thong tin trong db va gui cho khach hang

						String content = getContent();
						if (!"".equalsIgnoreCase(content)) {
							msgObject.setUsertext(content);
							msgObject.setMsgtype(1);

							// Luu vao danh sach can gui tin ket qua dac biet
							// free
							saveClient(userid, serviceid, sKeyword, operator,
									requestid, timesend, 1, 0);
						}
					}

				}

			}

		} catch (Exception ex) {
			Util.logger.error("Exception : ");
			ex.printStackTrace();
			msgObject.setUsertext(WRONG_SYTAX);
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			messages.add(new MsgObject(msgObject));
		}
		return messages;
	}

	// Replace ____ with _
	public static String replaceAllSpaceWithOne(String sInput) {
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

	// Kiem tra xem co phai la MB khong ?
	private static boolean isStartWithMB(String sInput) {
		if (sInput.startsWith("MB")) {
			return true;
		}
		return false;

	}

	// Kiem tra xem co nam trong khoang thoi gian 19h30 den 11h sang hay khong
	public boolean isNewSession() {
		String[] arrH = new String[20];
		int iHour1 = 11;
		int iMinute1 = 0;
		int iHour2 = 19;
		int iMinute2 = 30;

		long milliSecond = System.currentTimeMillis();
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.setTime(new java.util.Date(milliSecond));
		if ((calendar.get(calendar.HOUR_OF_DAY) < iHour1)) {
			return true;
		}

		if ((calendar.get(calendar.HOUR_OF_DAY) > iHour2)) {
			return true;
		}

		if (((calendar.get(calendar.HOUR_OF_DAY) == iHour2))
				&& (calendar.get(calendar.MINUTE) >= iMinute2)) {
			return true;
		}
		return false;
	}

	// Lưu thông tin khách hàng gửi về tổng đài
	private static boolean saveClient(String userid, String serviceid,
			String keyword, String operator, BigDecimal requestid,
			Timestamp timesend, int sendtuvan, int sendkqdb) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO icom_dangky_tuvanxoso( userid, serviceid, keyword, operator, requestid, timesend, sendtuvan, sendkqdb) VALUES ('"
					+ userid
					+ "','"
					+ serviceid
					+ "','"
					+ keyword
					+ "','"
					+ operator
					+ "','"
					+ requestid
					+ "','"
					+ timesend
					+ "','"
					+ sendtuvan + "','" + sendkqdb + "')";
			Util.logger.info("INSERT :" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert into icom_dangky_tuvanxoso");
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

	// Lay noi dung de gui ve khach hang
	private String getContent() {

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

			String query = "SELECT content FROM icom_tuvanxoso";
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
					+ " Tu van xo so: Failed" + ex.getMessage());
			ex.printStackTrace();
			return result;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

}
