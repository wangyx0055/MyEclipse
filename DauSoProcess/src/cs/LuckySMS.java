package cs;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
// com.services.sfone.ConSoMayMan
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.ResultSet;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class LuckySMS extends ContentAbstract {
	String GAMEID = "LUCKYSMS";
	public static String Hour2Queue_North = "19:00";
	public static String Hour2Queue_South = "16:00";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		MsgObject mt = msgObject;
		boolean error = false;
		String sKeyword = "OK";
		try {
			String sUserid = msgObject.getUserid();
			 sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();

			if ("0k".equalsIgnoreCase(sKeyword)) {
				msgObject.setKeyword("OK");
				sKeyword = "OK";
			}

			String sMTReturn = mtReturn(sUserid, sServiceid, sKeyword);
			mt.setUsertext(sMTReturn);
			mt.setMsgtype(1);
			messages.add(new MsgObject(mt));
			return messages;
		} catch (Exception e) {
			// TODO: handle exception
			error = true;
			return null;
		} finally {
			if (error == false) {
				ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
						.getServiceid(), msgObject.getUserid(), sKeyword, msgObject.getRequestid(), msgObject
						.getTTimes(), msgObject.getMobileoperator());
			}
		}
	}

	private String[] get_cs_maxid(String userid) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String[] lreturn = new String[4];
		lreturn[0] = "-1";
		lreturn[1] = "0";
		lreturn[2] = "0";
		lreturn[3] = "/";
		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null)
				throw new Exception("Impossible to connect to the channel "
						+ "service");

			String query = " SELECT id,lucky_code,companyid, lucky_date FROM ICOM_LUCKYNUMBER_USER  WHERE user_id = '"
					+ userid
					+ "' AND response = 0 and timestamps > TIMESTAMPADD (minute,-10,current_timestamp )and id in (select max(id) from ICOM_LUCKYNUMBER_USER  WHERE user_id = '"
					+ userid + "')";
			// System.out.println(query);
			statement = connection.prepareStatement(query,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (statement.execute() == true) {
				rs = statement.getResultSet();
				while (rs.next()) {
					lreturn[0] = rs.getString(1);
					lreturn[1] = rs.getString(2);
					lreturn[2] = rs.getString(3);
					lreturn[3] = rs.getString(4);
				}

			}
			return lreturn;

		} catch (Exception ex) {
			Util.logger.info("Get get_cs_maxid" + ex.toString());
			ex.printStackTrace();
			return lreturn;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	public boolean isNewSession() {
		String sTime2Queue = Hour2Queue_North;

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

	String getDate(int iday) {
		long milliSecond = System.currentTimeMillis();

		long lNewtime = milliSecond + iday * 24 * 60 * 60 * 1000;

		return Milisec2DDMMYYYY(lNewtime);

	}

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

	private String ConsomaymanMienBac(String userId, String commandCode,
			String serviceId, long companyId) {
		try {
			Random iRandom = new Random();
			int luckynumber = iRandom.nextInt(99999);
			String sLuckynumber = "" + luckynumber;
			sLuckynumber = "00000".substring(0, 5 - sLuckynumber.length())
					+ sLuckynumber;
			String sDate = "";

			if (!isNewSession()) {
				sDate = getDate(0);
			} else {
				sDate = getDate(1);
			}

			String strMTADD = "Ma So May Man cua ban:" + sLuckynumber
					+ "\n*So voi giai DB cua XSMB " + sDate.substring(0, 5)
					+ " de trung 2 trieu"
					+ "\n*Tra KQXSMB truc tiep, soan: XS gui 8551"
					+ "\n*Hay tiep tuc soan: " + commandCode + " gui "
					+ serviceId;

			long bret = ExecuteADVCR.savedata(userId, sLuckynumber, sDate,
					commandCode, serviceId, companyId, 1);
			if (bret > 0) {

				return strMTADD;

			} else {

				return "";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}

	}

	public static int executeSQL(Connection obj, String sql) {

		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {

			statement = obj.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();

			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			dbpool.cleanup(statement);

		}
	}

	private String mtReturn(String userid, String serviceid, String keyword)
			throws Exception {
		String mtReturn = "";

		String[] strMaxId = get_cs_maxid(userid);

		if ("-1".equalsIgnoreCase(strMaxId[0])) {

			mtReturn = ConsomaymanMienBac(userid, keyword, serviceid, 1);
		} else {

			Connection connection = null;
			DBPool dbpool = new DBPool();
			try {

				String sql = "update ICOM_LUCKYNUMBER_USER set response=1 where id ="
						+ strMaxId[0];
				connection = dbpool.getConnectionGateway();
				executeSQL(connection, sql);

				mtReturn = "Ma So May Man cua ban:" + strMaxId[1]
						+ "\n*So voi giai DB cua XSMB "
						+ strMaxId[3].substring(1, 5) + " de trung 2 trieu"
						+ "\n*Tra KQXSMB truc tiep, Soan: XS gui 8551"
						+ "\n*Hay tiep tuc soan: " + keyword + " gui "
						+ serviceid;
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				dbpool.cleanup(connection);
			}
		}

		return mtReturn;
	}

}