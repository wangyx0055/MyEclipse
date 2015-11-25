package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class Tratinkqxoso extends Thread {

	// Thoi gian de xoa client
	private String timesendTuvan = "11:00";
	long milisecond = 1000 * 10;
	private String timesendkqdb = "19:30";

	@Override
	public void run() {

		String firstResult = "";
		String dbcontent = "icom_tuvanxoso";
		String firstTuvan = "";
		String firstKetqua = "";
		try {
			while (ConsoleSRV.processData) {

				// Neu >11 gio thi lay noi dung tu van va gui cho khach hang nao
				// chua duoc gui
				if (isNewSession(timesendTuvan)) {
					String content = getContent(dbcontent);

					// Lay thong tin va gui lai cho khach hang
					Connection connection = null;
					PreparedStatement statement = null;
					DBPool dbpool = new DBPool();
					ResultSet rs = null;
					try {
						String serviceid = "";
						String userid = "";
						String operator = "";
						String keyword = "";
						String mtcontent = "";
						int sendtuvan = 0;
						BigDecimal requestid = new BigDecimal(0);
						connection = dbpool.getConnectionGateway();

						if (connection == null) {
							Util.logger.error("Impossible to connect to DB");
						}
						String selectQuery = "SELECT serviceid, userid, operator, keyword, requestid, sendtuvan, sendkqdb FROM icom_dangky_tuvanxoso";
						statement = connection.prepareStatement(selectQuery);
						if (statement.execute()) {
							rs = statement.getResultSet();
							while (rs.next()) {

								// Set cac thong tin de gui cho khach
								// hang
								serviceid = rs.getString(1);
								userid = rs.getString(2);
								operator = rs.getString(3);
								keyword = rs.getString(4);
								requestid = rs.getBigDecimal(5);
								mtcontent = content;
								sendtuvan = rs.getInt(6);

								// Neu chua gui thi gui cho khach hang va tin
								// nhan phai khac cai da gui
								if ((sendtuvan == 0)
										&& (!firstTuvan
												.equalsIgnoreCase(content))) {
									// Gui thong tin den cho khach hang
									sendGifMsg(serviceid, userid, operator,
											keyword, mtcontent, requestid);

									// Cap nhat thong tin da dc gui tin tuvan
									// cho userid
									updateTimes(userid);
								}
							}
							this.sleep(milisecond);
						}
						firstTuvan = content;
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

				// Neu thoi gian lon hon 19h30 thi gui tin ket qua xo so cho
				// khach hang

				if (isNewSession(timesendkqdb)) {

					// Lay noi dung ket qua xo so
					String contentKetQua = getContent();

					// Lay thong tin va gui lai cho khach hang
					Connection connection = null;
					PreparedStatement statement = null;
					DBPool dbpool = new DBPool();
					ResultSet rs = null;
					try {
						String serviceid = "";
						String userid = "";
						String operator = "";
						String keyword = "";
						String mtcontent = "";
						int sendkqdb = 0;
						BigDecimal requestid = new BigDecimal(0);
						connection = dbpool.getConnectionGateway();

						if (connection == null) {
							Util.logger.error("Impossible to connect to DB");
						}
						String selectQuery = "SELECT serviceid, userid, operator, keyword, requestid, sendtuvan, sendkqdb FROM icom_dangky_tuvanxoso";
						statement = connection.prepareStatement(selectQuery);
						if (statement.execute()) {
							rs = statement.getResultSet();
							while (rs.next()) {

								// Set cac thong tin de gui cho khach
								// hang
								serviceid = rs.getString(1);
								userid = rs.getString(2);
								operator = rs.getString(3);
								keyword = rs.getString(4);
								requestid = rs.getBigDecimal(5);
								mtcontent = contentKetQua;
								sendkqdb = rs.getInt(7);

								// Neu chua gui thi gui cho khach hang
								if ((sendkqdb == 0)
										&& (!firstKetqua
												.equalsIgnoreCase(contentKetQua))) {
									// Gui thong tin den cho khach hang
									sendGifMsg(serviceid, userid, operator,
											keyword, mtcontent, requestid);

									// Cap nhat thong tin da dc gui tin tuvan
									// cho userid
									updateKetQua(userid);
								}
							}
							this.sleep(milisecond);
						}
						firstKetqua = contentKetQua;
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

			}

		} catch (Exception ex) {
			Util.logger.info("Error: executeMsg.run :" + ex.toString());
		}
	}

	// Xoa cac thong tin dang ky vao 6h sang hang ngay
	private static boolean deleteClient() {
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
			String sqlDelete = "DELETE FROM icom_dangky_cautructiep";
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
				&& (calendar.get(calendar.MINUTE) >= iMinute)) {
			return true;
		}
		return false;
	}

	// Lay noi dung tin
	private String getContent() {

		// String content = "";
		// String area = "";
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

			String query = "SELECT content FROM icom_ketquaxoso";
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
					+ " Ket qua xo so: Failed" + ex.getMessage());
			ex.printStackTrace();
			// content = "Ma tinh khong hop le. Soan tin: CAU<Matinh> gui 8551
			// de soi cau chinh xac nhat";
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

	// Lay noi dung de gui ve khach hang
	private String getContent(String dbcontent) {

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

			String query = "SELECT content FROM" + dbcontent;
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

	// Cap nhat da gui tin tu van cho khach hang
	private static boolean updateTimes(String userid) {

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
			String sqlUpdate = "UPDATE icom_dangky_tuvanxoso SET sendtuvan = 1 WHERE userid = '"
					+ userid.toUpperCase() + "'";
			Util.logger.info("UPDATE: " + sqlUpdate);
			statement2 = connection.prepareStatement(sqlUpdate);
			if (statement2.execute()) {
				Util.logger.error("Update da tra tin tu van of " + userid
						+ " to icom_dangky_tuvanxoso");
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

	// Cap nhat da gui tin ket qua dac biet cho khach hang
	private static boolean updateKetQua(String userid) {

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
			String sqlUpdate = "UPDATE icom_dangky_tuvanxoso SET sendkqdb = 1 WHERE userid = '"
					+ userid.toUpperCase() + "'";
			Util.logger.info("UPDATE: " + sqlUpdate);
			statement2 = connection.prepareStatement(sqlUpdate);
			if (statement2.execute()) {
				Util.logger.error("Update da tra tin ket qua dac biet of "
						+ userid + " to icom_dangky_tuvanxoso");
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
	
	
	// CAN CO 1 HAM DE CHECK KET QUA TU VAN CO TRUNG VOI KET QUA RA TRONG NGAY HAY KHONG ?
	// NEU TRUNG THI KHI GUI KETQUA CHO KHACH HANG XONG THI DEL LUON
	// NGUOC LAI VAN GIU NGUYEN DANH SACH VA SET CHUA GUI TU VAN LUC 00:00
}
