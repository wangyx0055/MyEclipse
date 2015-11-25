package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class tratinbongdatrongngay extends Thread {

	// Thoi gian de xoa client
	private String hour = "06:00";
	long milisecond = 1000 * 10;
	private String dbKetQuaTrucTiep = "icom_ketquatructiep";
	private String dbDangKyKQ = "icom_dangky_cautructiep";

	@Override
	public void run() {

		String firstResult = "";
		long ts = 0;
		try {
			while (ConsoleSRV.processData) {

				// Lay noi dung thong tin va gui cho khach hang
				String contentKetQua = getContent();

				// Lay thoi gian hien tai
				ts = System.currentTimeMillis();
				Timestamp timestamp = new Timestamp(ts);
				String timeString = timeToString(timestamp);

				// Lay noi dung icom_cactrandautrongngay
				String result = getContent(timeString);

				// Gui tra thong tin cho khach hang
				try {
					if ("".equals(result)) {
						this.sleep(milisecond);
					} else {
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
							int report = 0;
							BigDecimal requestid = new BigDecimal(0);
							connection = dbpool.getConnectionGateway();

							if (connection == null) {
								Util.logger
										.error("Impossible to connect to DB");
							}
							String selectQuery = "SELECT serviceid, userid, operator, keyword, requestid, report FROM icom_dangky_cautructiep";
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
									keyword = rs.getString(4);
									requestid = rs.getBigDecimal(5);

									// Xem Khachhang da dc gui tin thong bao
									// tran dau chua
									report = rs.getInt(6);
									mtcontent = result;

									// Neu chua gui
									if (report == 0) {
										// Gui thong tin den cho khach hang
										sendGifMsg(serviceid, userid, operator,
												keyword, mtcontent, requestid);

										// update
										updateTimes(userid);
									}
								}
								this.sleep(milisecond);
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

				} catch (InterruptedException e) {
					Util.logger.info("Error Connect to Database");
				}

				try {
					if (("".equals(contentKetQua))
							&& (firstResult.equalsIgnoreCase(contentKetQua))) {
						this.sleep(milisecond);
					} else {
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
							int report = 0;
							BigDecimal requestid = new BigDecimal(0);
							connection = dbpool.getConnectionGateway();

							if (connection == null) {
								Util.logger
										.error("Impossible to connect to DB");
							}
							String selectQuery = "SELECT serviceid, userid, operator, keyword, requestid, report FROM icom_dangky_cautructiep";
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
									keyword = rs.getString(4);
									requestid = rs.getBigDecimal(5);

									// Xem Khachhang da dc gui tin thong bao
									// tran dau chua
									report = rs.getInt(6);
									mtcontent = contentKetQua;
									if (!firstResult.equals(contentKetQua)) {
										// Gui thong tin den cho khach hang
										sendGifMsg(serviceid, userid, operator,
												keyword, mtcontent, requestid);
									}
								}
								this.sleep(milisecond);
							}
							firstResult = contentKetQua;
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

				} catch (InterruptedException e) {
					Util.logger.info("Error Connect to Database");
				}

				// Neu dung 6h thi xoa
				if (isNewSession(hour)) {
					deleteClient(dbDangKyKQ);
					deleteClient(dbKetQuaTrucTiep);
					//deleteClient("icom_cactrandautrongngay");
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

			String query = "SELECT content FROM icom_ketquatructiep";
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
			Util.logger.info(this.getClass().getName() + " SoiCauXoSo: Failed"
					+ ex.getMessage());
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

	// chuyen ngay sang dinh dang tu 6h sang hom truoc den 6h sang hom sau
	// Cai nay de lay icom_cactrandautrongngay
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

			String query = "SELECT content FROM icom_cactrandautrongngay WHERE date(timeupdate)='"
					+ timeUpdate + "'";
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
			Util.logger.info(this.getClass().getName() + " SoiCauXoSo: Failed"
					+ ex.getMessage());
			ex.printStackTrace();
			return result;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	// Update
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
			String sqlUpdate = "UPDATE icom_dangky_cautructiep SET report = 1 WHERE userid = '"
					+ userid.toUpperCase() + "'";
			Util.logger.info("UPDATE: " + sqlUpdate);
			statement2 = connection.prepareStatement(sqlUpdate);
			if (statement2.execute()) {
				Util.logger.error("Update send to client " + userid
						+ " to icom_dangky_cautructiep");
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
