package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class Tratinbongda extends Thread {

	long miliseconds = 1000 * 60;
	ArrayList<String> result, firstResult = new ArrayList<String>();

	public void run() {

		try {
			
			ArrayList<String> result = getContent3();
			for (int i = 0; i < result.size(); i++) {
				firstResult.add(result.get(i));
			}
			while (ConsoleSRV.processData) {
				this.sleep(10 * 1000);

				try {
					result = getContent3();

					for (int i = 0; i < result.size(); i++) {
						String str1 = firstResult.get(i);
						String str2 = result.get(i);

						// Co mot tran thay doi ty so hay thong tin
						if (!(str1.equalsIgnoreCase(str2))) {
							String code = getcode(result.get(i));
							String code2 = getcode2(result.get(i));

							// Neu tran dau da ket thuc , send MT thong bao
							// toi khach hang
							if (validfinish(str2)) {
								Util.logger.info("Tran dau da ket thuc");
								sendMT(code, "Tran dau ket thuc voi " + str2);
								sendMT(code2, "Tran dau ket thuc voi " + str2);
								// Xoa thong tin khach hang da dang ky cac tran
								// dau tuong ung
								deleteCustomer(code);
								deleteCustomer(code2);
								break;
							} else {

								sendMT(code, result.get(i));
								sendMT(code2, result.get(i));

								firstResult.set(i, str2);
								continue;

							}
						}
					}

					this.sleep(miliseconds);

				} catch (Exception e) {
					Util.logger.info("Error Connect to Database");
				}
			}

		} catch (Exception ex) {
			Util.logger.info("Error: executeMsg.run :" + ex.toString());
		}

	}

	// Lay thong tin ve tat ca cac tran dau bong da trong ngay hom nay
	public static ArrayList<String> getContent3() {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		Statement s = null;
		ArrayList<String> content = new ArrayList<String>();

		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select info from icom_footballmatch"
					+ " where dateup < current_timestamp() ";
			Util.logger.info("QUERY :" + query1);

			s = connection.createStatement();

			rs = s.executeQuery(query1);

			while (rs.next()) {

				content.add(rs.getString(1));
			}

		} catch (SQLException e) {

			System.out.println(e);
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(s);
			dbpool.cleanup(connection);
		}
		return content;
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
					"Insert vmg_vnnlinks_winner Failed");
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

	// / Tra tin MT voi khach hang tuong ung voi ma doi bong
	public void sendMT(String code, String content) {
		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		try {
			String serviceid = "";
			String userid = "";
			String operator = "";
			String keyword = "";

			BigDecimal requestid = new BigDecimal(0);
			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String selectQuery = "SELECT service_id,user_id,operator,keywords,request_id FROM icom_customer"
					+ " where subcode1 = '" + code + "'";
			statement = connection.prepareStatement(selectQuery);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {

					// Set cac thong tin de gui cho khach hang

					serviceid = rs.getString(1);
					userid = rs.getString(2);
					operator = rs.getString(3);
					keyword = rs.getString(4);
					requestid = rs.getBigDecimal(5);

					// Lan luot gui thong tin den cho cac khach hang
					sendGifMsg(serviceid, userid, operator, keyword, content,
							requestid);
					this.sleep(miliseconds / 6);
				}

			}

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());

		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());

		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	// Lay ma chinh tran dau tu noi dung tuong ung
	public String getcode(String content) {
		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String code = "";
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String selectQuery = "SELECT machinh FROM icom_footballmatch"
					+ " where upper(INFO) = '" + content.toUpperCase() + "'";
			statement = connection.prepareStatement(selectQuery);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {

					// Set cac thong tin de gui cho khach hang
					code = rs.getString(1);

				}

			}

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());

		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());

		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return code;
	}

	// Lay ma phu tran dau tu noi dung tuong ung
	public String getcode2(String content) {
		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String code = "";
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String selectQuery = "SELECT maphu FROM icom_footballmatch"
					+ " where upper(INFO) = '" + content.toUpperCase() + "'";
			statement = connection.prepareStatement(selectQuery);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {

					// Set cac thong tin de gui cho khach hang
					code = rs.getString(1);

				}

			}

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());

		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());

		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return code;
	}

	private boolean validfinish(String content) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select id from icom_footballmatch where info = '"
					+ content + "' && ketthuc = true";

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

	private void deleteCustomer(String code) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		Statement s = null;
		try {

			connection = dbpool.getConnectionGateway();
			String query1 = "delete from icom_customer where subcode1 = '"
					+ code + "'";
			// Util.logger.info("Delete...............");
			s = connection.createStatement();
			s.execute(query1);
			// Util.logger.info("Da delete ....");

		} catch (SQLException e) {
			// TODO: handle exception
			System.out.println(e);
		} finally {
			dbpool.cleanup(s);
			dbpool.cleanup(connection);

		}
	}

}
