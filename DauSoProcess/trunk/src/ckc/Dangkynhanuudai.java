package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class Dangkynhanuudai extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String dtbase = "icom_dangky_nhanuudai";
		long MILI_SECOND = 1000 * 60 * 60 * 24;

		try {
			String userid = msgObject.getUserid();
			String usertext = msgObject.getUsertext();
			String serviceid = msgObject.getServiceid();
			String operator = msgObject.getMobileoperator();
			Timestamp timesend = msgObject.getTTimes();
			BigDecimal requestid = msgObject.getRequestid();
			String sKeyword = msgObject.getKeyword();

			Timestamp timeend = new Timestamp(timesend.getTime() + 365
					* MILI_SECOND);

			saveClient(dtbase, userid, serviceid, operator, timesend, sKeyword,
					requestid, timeend);
			msgObject
					.setUsertext("Ban da dang ky nhan cac diem uu dai thanh cong. Cam on ban da su dung dich vu.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			messages.add(new MsgObject(msgObject));
			return messages;

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
	}

	private static boolean saveClient(String dbcontent, String userid,
			String serviceid, String operator, Timestamp timesend,
			String sKeyword, BigDecimal requestid, Timestamp timeend) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO "
					+ dbcontent
					+ "(userid, serviceid, keyword, operator, timesend, requestid, timeend )"
					+ "VALUES(?,?,?,?,?,?,?)";

			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setString(2, serviceid);
			statement.setString(3, sKeyword);
			statement.setString(4, operator);
			statement.setTimestamp(5, timesend);
			statement.setBigDecimal(6, requestid);
			statement.setTimestamp(7, timeend);
			statement.executeUpdate();
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

	/*
	 * Thay nhieu dau _____ -> _
	 */
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

}
