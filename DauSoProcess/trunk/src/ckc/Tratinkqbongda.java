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
import java.util.Vector;

import subscription.Subutil;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class Tratinkqbongda extends Thread {

	// Thoi gian de xoa client
	private String hour = "06:00";
	long milisecond = 1000 * 60;
	private String dbKetQuaTrucTiep = "icom_ketqua_bongda";
	private String dbDangKyKQ = "icom_dangky_bongda";

	@Override
	public void run() {

		long ts = 0;
		String serviceid = "";
		String userid = "";
		String operator = "";
		String keyword = "";
		String mtcontent = "";
		int report = 0;
		String requestid = "";

		try {

			while (ConsoleSRV.processData) {

				ArrayList<String> resultGroup = new ArrayList<String>();

				// Lay thoi gian hien tai
				ts = System.currentTimeMillis();
				Timestamp timestamp = new Timestamp(ts);
				String timeReceive = timeToString(timestamp);

				// Lay noi dung icom_bongda
				String result = getContent(timeReceive);

				// Lay danh sach cac nhom
				resultGroup = getGroup();

				// Lay noi dung cua tung nhom
				String[] mtContent = getKetQua(resultGroup);

				for (int i = 0; i < mtContent.length; i++) {
					Util.logger.info("mtContent: " + i + ":" + mtContent[i]);
				}

				// Gui tra thong tin cho khach hang
				try {
					if ("".equals(result)) {
						this.sleep(milisecond);
					} else {
						// Lay danh sach cac khach hang chua dc gui thong tin ve
						// cac tran dau trong ngay.
						String selectQuery = "SELECT userid, serviceid, operator, keyword, requestid, content FROM icom_dangky_bongda";
						Vector vtUsers = Subutil.getVectorTable("gateway",
								selectQuery);
						result = "Cac tran dau trong ngay :" + result;
						for (int j = 0; j < vtUsers.size(); j++) {

							Vector item = (Vector) vtUsers.elementAt(j);

							userid = (String) item.elementAt(0);
							serviceid = (String) item.elementAt(1);
							operator = (String) item.elementAt(2);
							keyword = (String) item.elementAt(3);
							requestid = (String) item.elementAt(4);
							mtcontent = (String) item.elementAt(5);

							Util.logger.info("SIZE :" + resultGroup.size());
							for (int i = 0; i < resultGroup.size(); i++) {

								// Cung la nhom va khac noi dung
								if ((keyword.equalsIgnoreCase(resultGroup
										.get(i)))
										&& (!mtcontent
												.equalsIgnoreCase(mtContent[i]))
										&& (!"".equalsIgnoreCase(mtContent[i]))) {

									sendGifMsg(serviceid, userid, operator,
											"BD", mtContent[i],
											new BigDecimal(requestid));

									updateContent(userid, mtContent[i], keyword);

								}
							}

						}
						this.sleep(milisecond);
					}

				} catch (InterruptedException e) {
					Util.logger.info("Error Connect to Database");
				}

				// Neu dung 6h thi xoa khach hang dang ky va cac ket qua trong ngay.
				if (isNewSession(hour)) {
					deleteClient(dbDangKyKQ);
					deleteClient(dbKetQuaTrucTiep);
				}

			}

		} catch (Exception ex) {
			Util.logger.info("Error: executeMsg.run :" + ex.toString());
		}
	}

	// Xoa cac thong tin dang ky vao 6h sang hang ngay
	private static boolean deleteClient(String dbcontent) {
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
			String sqlDelete = "DELETE FROM " + dbcontent;
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

	// Kiem tra xem co dung la 6h sang hay khong ?
	public boolean isNewSession(String hour2send) {
		String sTime2Queue = hour2send;

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
		if ((calendar.get(calendar.HOUR_OF_DAY) == iHour)
				&& (calendar.get(calendar.MINUTE) == iMinute)) {
			return true;
		}
		return false;
	}

	// Lay noi dung tin
	private String[] getKetQua(ArrayList<String> groupfootball) {

		// String content = "";
		// String area = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		// String[] result = null;
		String[] result = new String[groupfootball.size()];

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			for (int i = 0; i < groupfootball.size(); i++) {
				result[i] = "";
				String query = "SELECT info FROM icom_ketqua_bongda WHERE upper(groupfootball)='"
						+ groupfootball.get(i).toUpperCase() + "'";

				statement = connection.prepareStatement(query);

				Util.logger.info("QUERY : " + query);

				if (statement.execute()) {
					rs = statement.getResultSet();
					Util.logger.info("rs:" + rs);
					while (rs.next()) {
						result[i] = rs.getString(1);

					}
				}
			}
			return result;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName()
					+ " Tra tin bong da : Failed" + ex.getMessage());
			ex.printStackTrace();
			return result;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	// Tra tin cho khach hang
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

	// chuyen ngay sang dinh dang tu 6h sang hom truoc den 6h sang hom sau
	// Cai nay de lay icom_cactrandautrongngay
	private String timeToString(Timestamp ts) {

		int MILI_SECOND = 1000 * 60 * 60 * 24;
		String result = "";
		Date date;
		int i = 0;
		i = ts.getHours();
		Util.logger.info("HOUR TO SEND: " + i);

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

	// Chuyen Calendar sang string
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

	// Lấy nội dung icom_cactrandautrongngay
	private String getContent(String timeUpdate) {

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

			String query = "SELECT info FROM icom_bongda WHERE date(timeupdate)='"
					+ timeUpdate + "'";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY getContent: " + query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = result + rs.getString(1) + "; ";
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

	// Update
	private static boolean updateContent(String userid, String content, String groupfootball) {

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
					+ content + "' WHERE ( userid = '" + userid.toUpperCase()
					+ "') AND ( upper(keyword)='" + groupfootball.toUpperCase() + "')";
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

	private ArrayList<String> getGroup() {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		ArrayList<String> content = new ArrayList<String>();

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return null;
			}

			String query = "SELECT groupfootball FROM icom_bongda GROUP BY groupfootball";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY : " + query);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					content.add(rs.getString(1));
				}
			}
			return content;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName()
					+ " Ket qua bong da: Failed" + ex.getMessage());
			ex.printStackTrace();
			return null;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

}
