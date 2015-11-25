package baohangngay;

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

public class tratinhangngay extends Thread {

	String hour2send = "09:00";
	String info_type = "BAO";
	String dbcontent = "textbase";
	long milisecond = 1000;

	@Override
	public void run() {

		try {
			while (ConsoleSRV.processData) {
				// Neu > 9:00 thi moi lay thong tin
				if (isNewSession()) {
					// Lay noi dung thong tin can gui
					String contentBAO = getAllcontentBao(info_type, dbcontent);
					Util.logger.info("NOI DUNG THONG BAO : " + contentBAO);

					try {
						if ("".equals(contentBAO)) {
							this.sleep(milisecond);
						} else {
							Connection connection = null;
							PreparedStatement statement = null;
							DBPool dbpool = new DBPool();
							
							try {

								String serviceid = "";
								String userid = "";
								String operator = "";
								String service = "";
								String mtcontent = "";
								BigDecimal requestid = new BigDecimal(0);
								connection = dbpool.getConnectionGateway();
								ResultSet rs = null;
								if (connection == null) {
									Util.logger
											.error("Impossible to connect to DB");
								}
								String selectQuery = "SELECT serviceid, userid, operator, service, requestid FROM icom_thuebao_bao";
								statement = connection
										.prepareStatement(selectQuery);
								if (statement.execute()) {
									rs = statement.getResultSet();
									while (rs.next()) {

										// Set cac thong tin de gui cho khach
										// hang
										serviceid = rs.getString(1);
										userid = rs.getString(2);
										operator = rs.getString(3);
										service = rs.getString(4);
										requestid = rs.getBigDecimal(5);
										mtcontent = contentBAO;

										// Gui thong tin den cho khach hang
										sendGifMsg(serviceid, userid, operator,
												service, mtcontent, requestid);

										// Cap nhat thời gian còn lại của khach hang
										updaterequest(userid);

										/*
										 * Kiểm tra xem tài khoản của khách hàng
										 * còn hay không ?
										 */
										if (getUserID(userid) <= 0) {
											mtcontent = "Tai khoan cua quy vi da het. Vui long soan tin : "
													+ service
													+ " gui "
													+ serviceid
													+ " de duoc nhan them thong tin hang ngay.";
											sendGifMsg(serviceid, userid,
													operator, service,
													mtcontent, requestid);

											// Xoa thong tin Khach hang trong
											// database neu tk = 0
											deleteClient(userid);
										}
										this.sleep(milisecond);
									}
								}

							} catch (SQLException e) {
								Util.logger.error(": Error:" + e.toString());

							} catch (Exception e) {
								Util.logger.error(": Error:" + e.toString());

							} finally {
								dbpool.cleanup(statement);
								dbpool.cleanup(connection);
							}
						}

					} catch (InterruptedException e) {
						Util.logger.info("Error Connect to Database");
					}
				}
			}
		} catch (Exception ex) {
			Util.logger.info("Error: executeMsg.run :" + ex.toString());
		}
	}

	/* return date with format: dd/mm/yyyy */
	public static String Milisec2DDMMYYYY(long ts) {
		if (ts == 0) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts));

			String strTemp = Integer.toString(calendar
					.get(calendar.DAY_OF_MONTH));
			if (calendar.get(calendar.DAY_OF_MONTH) < 10) {
				strTemp = "0" + strTemp;
			}
			if (calendar.get(calendar.MONTH) + 1 < 10) {
				return strTemp + "/0" + (calendar.get(calendar.MONTH) + 1)
						+ "/" + calendar.get(calendar.YEAR);
			} else {
				return strTemp + "/" + (calendar.get(calendar.MONTH) + 1) + "/"
						+ calendar.get(calendar.YEAR);
			}
		}
	}

	public String getAllcontentBao(String info_type, String dbcontent) {
		String cnttemp = "";
		String sqlSELECT = "SELECT content, id FROM icom_infoservice WHERE upper(info_type) = '"
				+ info_type.toUpperCase() + "'";
		String sqltemp = sqlSELECT;
		sqltemp = sqltemp + " and DATE_FORMAT(info_date,'%d/%m/%Y')='"
				+ getDate(0) + "'";
		String[] temp = getContent(sqltemp, dbcontent);
		cnttemp = temp[0];

		return cnttemp;
	}

	String[] getContent(String query, String dbcontent) {
		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(dbcontent);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.sysLog(1, this.getClass().getName(),
					"getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"getContent: Failed" + ex.getMessage());
			ex.printStackTrace();
			return record;
		} finally {
			dbpool.cleanup(connection);
		}

	}

	private static int getUserID(String userid) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT day_remain FROM icom_thuebao_bao WHERE userid= '"
				+ userid.toUpperCase() + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getInt(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
		return -1;
	}

	String getDate(int iday) {
		long milliSecond = System.currentTimeMillis();

		long lNewtime = milliSecond + (iday - 1) * 24 * 60 * 60 * 1000;

		return Milisec2DDMMYYYY(lNewtime);
	}

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

			// delete icom_thuebao_bao where user id
			String sqlDelete = "DELETE FROM icom_thuebao_bao WHERE userid = '"
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

	private static boolean updaterequest(String userid) {

		Connection connection = null;
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			int day = getUserID(userid);
			// Plus day
			day = day - 1;

			// Update day remain into icom_thuebao_bao
			String sqlUpdate = "UPDATE icom_thuebao_bao SET day_remain =" + day
					+ " WHERE userid = '" + userid.toUpperCase() + "'";
			Util.logger.info("UPDATE : " + sqlUpdate);
			statement2 = connection.prepareStatement(sqlUpdate);
			if (statement2.execute()) {
				Util.logger.error("UPDATE day_remain TO icom_thuebao_bao");
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
			dbpool.cleanup(statement1);
			dbpool.cleanup(statement2);
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

	// Co phai la > 9:00 khong??
	public boolean isNewSession() {
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
		
		/* Lấy thời gian hiện tại */
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
}
