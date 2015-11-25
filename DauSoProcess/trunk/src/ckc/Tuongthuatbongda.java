package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

/**
 * Tuongthuatbongda class.<br>
 * 
 * <pre>
 * ・</pre>
 * 
 * @author Vietnamnet I-Com TrungVD
 * @version 1.0
 */
public class Tuongthuatbongda extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String sKeyword = msgObject.getKeyword();
		String serviceid = msgObject.getServiceid();

		try {

			String timeReceive = "";
			String userid = msgObject.getUserid();
			String operator = msgObject.getMobileoperator();
			String usertext = msgObject.getUsertext();
			Timestamp timesend = msgObject.getTTimes();
			BigDecimal requestid = msgObject.getRequestid();

			// Lay thong tin khach hang gui den
			usertext = replaceAllWhiteWithOne(usertext);
			
			// Lay thoi gian khach hang gui den
			timeReceive = timeToString(timesend);
			Util.logger.info("TIME RECEIVE" + timeReceive);

			String[] sTokens = usertext.split(" ");

			String group = "";
			if (sTokens.length > 1) {
				group = sTokens[1];
				Util.logger.info("Group AAAAABBBB" + group);
			} else {

				msgObject
						.setUsertext("Ban da nhan tin sai cu phap.De nhan thong tin truc tiep ket qua bong da ban hay gui tin "
								+ sKeyword + " [Ma nhom] gui " + serviceid);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			// Kiem tra xem co ton tai ma nhom hay khong.
			if (!checkGroup(group, timeReceive)) {
				msgObject
						.setUsertext("Ma "
								+ group
								+ " khong ton tai hoac hom nay khong dien ra cac tran dau.De nhan thong tin truc tiep ket qua bong da ban hay gui tin "
								+ sKeyword + " [Ma nhom] gui " + serviceid);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			Util.logger.info("Group :" + group);
			usertext = group;

			msgObject
					.setUsertext("Ban da dang ky tham gia lay ket qua truc tiep.Khi tran dau dien ra chung toi se tu dong tra thong tin nhanh nhat ve cho ban");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			messages.add(new MsgObject(msgObject));

			// Lay noi dung tra ve khach hang
			String matchInDay = getContent(group, timeReceive);
			Util.logger.info("Match in day :" + matchInDay);

			// Lay ket qua trong csdl roi gui cho KH
			String content = getKetQuaTrucTiep(group);

			if (!"".equalsIgnoreCase(matchInDay)) {
				msgObject.setUsertext("Cac tran dau trong ngay :" + matchInDay);
				msgObject.setMsgtype(0);
				messages.add(new MsgObject(msgObject));

				// Gui tra lai cho KH ket qua cuoi cung.
				if (!"".equalsIgnoreCase(content)) {
					msgObject.setUsertext(content);
					msgObject.setMsgtype(0);
					messages.add(new MsgObject(msgObject));
				}

				// Neu van chua ket thuc thi ghi vao csdl
				if (!checkFinish(group)) {
					saveClient(userid, serviceid, usertext, operator,
							requestid, timesend, content);
				} else {
					return messages;
				}

			} else {
				saveClient(userid, serviceid, usertext, operator, requestid,
						timesend, content);
			}

			return messages;
		} catch (Exception ex) {
			msgObject
					.setUsertext("Ban da nhan tin sai cu phap. De nhan ket qua bong da truc tiep hay nhan tin theo cu phap "
							+ sKeyword + " [Ma nhom] gui " + serviceid);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
		}
		return messages;
	}

	// Lưu thông tin khách hàng gửi về tổng đài
	private static boolean saveClient(String userid, String serviceid,
			String keyword, String operator, BigDecimal requestid,
			Timestamp timesend, String content) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO icom_dangky_bongda( userid, serviceid, keyword, operator, requestid, timesend, content) VALUES ('"
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
					+ content + "')";
			Util.logger.info("INSERT :" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert into icom_dangky_cautructiep");
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

	// Kiem tra xem co nhom trong group ko ? Neu co thi okie, neu khong co thi
	// gan mac dinh
	private boolean checkGroup(String groupfootball, String timeReceive) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		boolean check = false;

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return check;
			}

			String query = "SELECT groupfootball FROM icom_bongda WHERE (upper(groupfootball) ='"
					+ groupfootball.toUpperCase() + "') AND ( date(timeupdate)='" + timeReceive + "')";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY : " + query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					String result = rs.getString(1);
					if (groupfootball.equalsIgnoreCase(result)) {
						return true;
					}
				}
			}
			return check;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName()
					+ " Tuong thuat bong da: Failed" + ex.getMessage());
			ex.printStackTrace();
			return check;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	private boolean checkFinish(String groupfootball) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String query = "SELECT isfinish FROM icom_ketqua_bongda WHERE upper(groupfootball) ='"
					+ groupfootball.toUpperCase() + "'";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY : " + query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					int result = rs.getInt(1);
					if (result == 1) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName()
					+ " Tuong thuat bong da: Failed" + ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	// Lay noi dung cac tran dau de gui ve khach hang
	private String getContent(String groupfootball, String timeReceive) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		String result = "";

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String query = "SELECT info FROM icom_bongda WHERE (upper(groupfootball)='"
					+ groupfootball.toUpperCase()
					+ "') AND ( date(timeupdate) ='" + timeReceive + "')";
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
					+ " Tra tin bong da: Failed" + ex.getMessage());
			ex.printStackTrace();
			return result;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	// Lay noi dung de gui ve khach hang
	private String getKetQuaTrucTiep(String groupfootball) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		String result = "";

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String query = "SELECT info FROM icom_ketqua_bongda WHERE upper(groupfootball)='"
					+ groupfootball.toUpperCase() + "'";
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
					+ " Truc tiep ket qua: Failed" + ex.getMessage());
			ex.printStackTrace();
			return result;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	// chuyen ngay sang dinh dang tu 6h sang hom truoc den 6h sang hom sau
	private String timeToString(Timestamp ts) {

		int MILI_SECOND = 1000 * 60 * 60 * 24;
		String result = "";
		Date date;
		int i;
		i = ts.getHours();
		Util.logger.info("HOUR TO SEND:" + i);

		// Neu ma thoi gian nho hon 6
		if (i < 6) {
			date = new Date(ts.getTime() - MILI_SECOND);
		} else {
			date = new Date(ts.getTime());
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		result = getCalendarString(calendar);

		return result;

	}

	// Chuyen Calendar sang string chi lay nam ngay thang
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
		// sb.append(" 08:00:00");

		return (sb.toString());
	}

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

	private static boolean updateContent(String userid, String content) {

		Connection connection = null;
		PreparedStatement statement2 = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update so lan giam can.
			String sqlUpdate = "UPDATE icom_dangky_bongda SET content = '"
					+ content + "' WHERE userid = '" + userid.toUpperCase()
					+ "'";
			Util.logger.info("UPDATE: " + sqlUpdate);
			statement2 = connection.prepareStatement(sqlUpdate);
			if (statement2.execute()) {
				Util.logger.error("Update Content send to client " + userid
						+ " to icom_dangky_bongda");
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
			dbpool.cleanup(statement2);
			dbpool.cleanup(connection);
		}
	}

}
