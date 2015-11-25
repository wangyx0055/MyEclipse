package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Dangkydichvubongda extends ContentAbstract {

	/**
	 * getMessages.<br> ◆ Handle exception
	 * 
	 * @param msgObject
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		// Get customer's info
		String user_id = msgObject.getUserid();
		String service_id = msgObject.getServiceid();
		String operator = msgObject.getMobileoperator();
		String keywords = msgObject.getKeyword();
		Timestamp timesend = msgObject.getTTimes();
		BigDecimal request_id = msgObject.getRequestid();

		String hour = "06:00";

		String info = msgObject.getUsertext();
		String[] _info = info.split(" ");
		String reply = "", content = "";

		// Neu khach hang chua nhap ma so doi bong
		// tra ve thong bao toi khach hang
		if (_info.length == 1) {
			reply = "Ban chua nhap ma so doi bong " + "Soan tin "
					+ msgObject.getKeyword()
					+ " [madoi] voi ma doi la ma so doi chu nha gui "
					+ msgObject.getServiceid();
			msgObject.setUsertext(reply);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
			return messages;
		}
		String sub1 = _info[1];
		sub1 = sub1.toUpperCase();
		String sub2 = "";
		String sub3 = "";

		// Kiem tra xem ma so doi bong khach hang nhap co dung hay khong ?
		if (!(validCode(sub1))) {
			reply = "Ma doi bong ban nhap " + sub1 + " chua dung .Soan tin "
					+ keyword.getKeyword()
					+ " [madoi] voi ma doi la ma so doi chu nha gui ";
			msgObject.setUsertext(reply);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
			return messages;
		}

		// Truoc 06:00 - Tra ve ket qua tran dau cua doi nay truoc do
		if (!isNewSession(hour)) {
			content = getContent2(sub1);
			reply = " " + content;
			msgObject.setUsertext(reply);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
			return messages;

		} else {

			// Neu thong tin ve tran dau nay chua cap nhat
			content = getContent(sub1);
			if ("".equals(content)) {
				saveCustomer(user_id, service_id, operator, keywords, sub1,
						sub2, sub3, timesend, request_id);
				Util.logger.info("Save customer..........");
				reply = "Ban da tham gia buoi tuong thuat truc tiep tran dau cua 6x57."
						+ "Soan tin "
						+ msgObject.getKeyword()
						+ " [Ma nhom] gui 6757 de nhan ket qua 10 tran lien tiep";
				msgObject.setUsertext(reply);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));

				reply = "Thong tin ve tran dau nay chua co ."
						+ "Cac thong tin cap nhat ve tran dau se gui den ban sau";

				msgObject.setUsertext(reply);
				msgObject.setMsgtype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {
				if (!"".equals(getfinish(sub1))) {

					// Neu khach hang nhan tin vao thoi diem tran dau da Ket
					// thuc
					// tra ve thong tin ket qua cua tran dau do
					reply = "Tran dau da ket thuc voi ty so : "
							+ getfinish(sub1);
					msgObject.setUsertext(reply);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {
					saveCustomer(user_id, service_id, operator, keywords, sub1,
							sub2, sub3, timesend, request_id);
					Util.logger.info("Save customer............");
					reply = "Ban da tham gia buoi tuong thuat truc tiep tran dau cua 6x57."
							+ "Soan tin "
							+ msgObject.getKeyword()
							+ " [Ma nhom] gui 6757 de nhan ket qua 10 tran lien tiep";
					msgObject.setUsertext(reply);
					msgObject.setMsgtype(0);
					messages.add(new MsgObject(msgObject));

					// Tra ve thong tin hien tai ve tran dau
					msgObject.setUsertext(getContent(sub1));
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;

				}
			}
		}

	}

	// Lay thong tin ve tran bong da ngay hom nay
	public static String getContent(String code) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		Statement s = null;
		String content = "";

		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select info from icom_footballmatch"
					+ " where (dateup < current_timestamp()) && (machinh = '"
					+ code + "' || maphu ='" + code + "')";
			Util.logger.info("QUERT SELECT " + query1);
			s = connection.createStatement();
			rs = s.executeQuery(query1);

			while (rs.next()) {
				content = rs.getString(1);
				if (content.contains(code))
					break;

			}

			// Close Statement and ResultSet
			s.close();
			rs.close();

		} catch (SQLException e) {
			System.out.println(e);
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(s);
			dbpool.cleanup(connection);
		}
		return content;
	}

	// Lay thong tin ve tran bong da ngay truoc do
	public static String getContent2(String code) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		Statement s = null;
		String content = "";

		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select info from icom_footballmatch"
					+ " where dateup < current_timestamp() && (machinh = '"
					+ code + "' || maphu = '" + code + "')";
			s = connection.createStatement();
			rs = s.executeQuery(query1);

			while (rs.next()) {
				content = rs.getString(1);
				if (content.contains(code))
					break;

			}

			// / Close Statement and ResultSet
			s.close();
			rs.close();

		} catch (SQLException e) {
			// TODO: handle exception
			System.out.println(e);
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(s);
			dbpool.cleanup(connection);
		}
		return content;
	}

	public static void saveCustomer(String user_id, String service_id,
			String operator, String keywords, String sub1, String sub2,
			String sub3, Timestamp timesend, BigDecimal request_id) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		Statement s = null;

		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "insert into icom_customer(user_id,service_id,operator,keywords,request_id,timesend,subcode1,subcode2,subcode3)  values"
					+ "('"
					+ user_id
					+ "','"
					+ service_id
					+ "','"
					+ operator
					+ "','"
					+ keywords
					+ "','"
					+ request_id
					+ "','"
					+ timesend
					+ "','" + sub1 + "','" + sub2 + "','" + sub3 + "')";
			s = connection.prepareStatement(query1);
			s.execute(query1);

		} catch (SQLException e) {
			// TODO: handle exception
			System.out.println(e);
		} finally {

			dbpool.cleanup(s);
			dbpool.cleanup(connection);

		}
	}

	public static boolean isNewSession(String time) {
		String sTime2Queue = time;

		String[] arrH = new String[20];
		int iHour = 0;
		int iMinute = 0;
		arrH = sTime2Queue.split(":");
		if (arrH.length > 1) {
			iHour = Integer.parseInt(arrH[0].trim());
			iMinute = Integer.parseInt(arrH[1].trim());
		} else {
			iHour = Integer.parseInt(arrH[0].trim());

		}
		long milliSecond = System.currentTimeMillis();
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.setTime(new java.util.Date(milliSecond));
		if (((calendar.get(calendar.HOUR_OF_DAY) == iHour) && (calendar
				.get(calendar.MINUTE) >= iMinute))
				|| ((calendar.get(calendar.HOUR_OF_DAY) > iHour))) {
			return true;
		}
		return false;
	}

	private boolean validCode(String code) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select id from icom_team where code = '" + code
					+ "'";

			// query1 = "select db_name()";

			Util.logger.info(this.getClass().getName() + "query1:" + query1
					+ "");
			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}

	// / Lay thong tin ve tran dau khi da ket thuc
	public String getfinish(String code) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		Statement s = null;
		String content = "";

		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select info from icom_footballmatch"
					+ " where ketthuc = true && (machinh = '" + code
					+ "' || maphu = '" + code + "')";
			s = connection.createStatement();
			rs = s.executeQuery(query1);

			while (rs.next()) {
				content = rs.getString(1);
				if (content.contains(code))
					break;

			}

			// / Close Statement and ResultSet
			s.close();
			rs.close();

		} catch (SQLException e) {
			System.out.println(e);
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(s);
			dbpool.cleanup(connection);
		}
		return content;
	}

}
