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

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

/**
 * Ketquacautructiep class.<br>
 * 
 * <pre>
 * ・</pre>
 * 
 * @author Vietnamnet I-Com TrungVD
 * @version 1.0
 */
public class Ketquacautructiep extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		try {
			String timeReceive = "";
			String userid = msgObject.getUserid();
			String serviceid = msgObject.getServiceid();
			String operator = msgObject.getMobileoperator();
			String keywords = msgObject.getKeyword();
			Timestamp timesend = msgObject.getTTimes();
			BigDecimal requestid = msgObject.getRequestid();

			// Lay noi dung va tra lai cho khach hang

			timeReceive = timeToString(timesend);
			Util.logger.info("TIME RECEIVE" + timeReceive);

			// Lay noi dung theo thoi gian nhan tin nhan.Tin nay la cac tran dau
			// trong ngay
			String replyToClient = getContent(timeReceive);

			// Neu trong ngay ma chua co noi dung ?
			if ("".equalsIgnoreCase(replyToClient)) {
				msgObject
						.setUsertext("Hien thoi he thong cua chung toi chua cap nhat cac tran dau trong ngay. Chung toi se thong bao cho quy vi khi co tin moi nhat");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				// Luu lai thong tin khach hang va se gui lai
				saveClient(userid, serviceid, keywords, operator, requestid,
						timesend, 0);
			} else {
				msgObject.setUsertext(replyToClient);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				// Thong bao cho khach hang cac ket qua moi nhat. Lay tu
				// icom_ketquatructiep
				// 1. Neu ketqua = "" thi khong gui lai cho KH
				// 2. Neu khac thi gui cho KH ket qua hien tai.
				// 2.1 Neu co chu ket thuc thi khong ghi vao csdl
				// 2.2 Neu khong co chu ket thuc thi ghi vao list can truyen
				// ketqua
				// truc tiep
				String resultLiveScore = getKetQuaTrucTiep();

				// Neu da co ket qua
				if (!"".equalsIgnoreCase(resultLiveScore)) {
					if (resultLiveScore.startsWith("KT")) {
						// Gui thong tin cho khach hang va thong bao da ket thuc
						msgObject.setUsertext(resultLiveScore);
						msgObject.setMsgtype(0);
						messages.add(new MsgObject(msgObject));
					} else {
						// Gui thong tin va dua ra thong bao
						msgObject.setUsertext(resultLiveScore);
						msgObject.setMsgtype(0);
						messages.add(new MsgObject(msgObject));
						// Luu thong tin cac khach hang dang ky vao List
						saveClient(userid, serviceid, keywords, operator,
								requestid, timesend, 1);

					}

				} else {

					// Neu chua co ket qua thi van luu vao trong database
					saveClient(userid, serviceid, keywords, operator,
							requestid, timesend, 1);

				}
			}
			return messages;
		} catch (Exception ex) {
			msgObject.setUsertext("Ban da nhan tin sai cu phap.");
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
		}
		return messages;
	}

	// Lưu thông tin khách hàng gửi về tổng đài
	private static boolean saveClient(String userid, String serviceid,
			String keyword, String operator, BigDecimal requestid,
			Timestamp timesend, int report) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO icom_dangky_cautructiep( userid, serviceid, keyword, operator, requestid, timesend, report) VALUES ('"
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
					+ report + "')";
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

	// Lay noi dung de gui ve khach hang
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

	// Lay noi dung de gui ve khach hang
	private String getKetQuaTrucTiep() {

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

	// Chuyen ngay sang dinh dang tu 6h sang hom truoc den 6h sang hom sau
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

}
