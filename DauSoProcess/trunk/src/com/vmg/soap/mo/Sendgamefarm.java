package com.vmg.soap.mo;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.DateProc;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;

import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import com.vmg.soap.mo.MOSender;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sendgamefarm extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		if (msgObject.getMobileoperator().equalsIgnoreCase("VMS")) {
			msgObject.setUsertext("Ban nhan tin sai cu phap. DTHT 1900571566");
			msgObject.setMsgtype(1);
			DBUtil.sendMT(msgObject);
			Thread.sleep(1000);
			return null;
		}
		if ((msgObject.getMobileoperator().equalsIgnoreCase("VIETTEL"))
				&& (msgObject.getKeyword().equalsIgnoreCase("LV"))) {
			msgObject.setUsertext("Ban nhan tin sai cu phap. DTHT 1900571566");
			msgObject.setMsgtype(1);
			DBUtil.sendMT(msgObject);
			Thread.sleep(1000);
			return null;
		}
		if ((msgObject.getMobileoperator().equalsIgnoreCase("GPC"))
				&& (msgObject.getKeyword().equalsIgnoreCase("FA"))) {
			msgObject.setUsertext("Ban nhan tin sai cu phap. DTHT 1900571566");
			msgObject.setMsgtype(1);
			DBUtil.sendMT(msgObject);
			Thread.sleep(1000);
			return null;
		}
		String info = msgObject.getUsertext();
		String[] sTokens = replaceAllWhiteWithOne(info).split(" ");

		if (sTokens.length == 1) {

			insertMO2lottery(msgObject);
		} else if (sTokens.length == 2) {
			if (sTokens[1].equalsIgnoreCase("OFF")) {
				insertMO2lottery(msgObject);
				return null;
			} else if (sTokens[1].equalsIgnoreCase("LINK")) {// FA LINK Lay
				// lai Link

				Util.logger.info(" Lay lai Link: " + msgObject.getUserid());

				msgObject
						.setUsertext("Chao mung ban tro lai voi Game Lam Vuon That Tuyet.Bam vao link http://funzone.vn/lamvuon de quay tro lai trang trai cua ban.Dien thoai ho tro 1900571566.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

			} else if ((sTokens[1].equalsIgnoreCase("HD"))
					&& msgObject.getMobileoperator()
							.equalsIgnoreCase("VIETTEL")) {// FA HD Huong
				// dan

				Util.logger.info("Lay huong dan: " + msgObject.getUserid());

				msgObject
						.setUsertext("De dang ky game Lam vuon that tuyet.Soan FA gui 8151.De HUY dich vu,soan FA OFF gui 8151.De nhan lai url game, soan FA LINK gui 8151.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				// mt2
				msgObject
						.setUsertext("Dien thoai ho tro 1900571566.Chi tiet tai http://funzone.vn");
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
			} else if ((sTokens[1].equalsIgnoreCase("HD"))
					&& msgObject.getMobileoperator().equalsIgnoreCase("GPC")) {
				msgObject
				.setUsertext("De dang ky game Lam vuon that tuyet.Soan LV gui 8151.De HUY dich vu,soan LV OFF gui "
						+ msgObject.getServiceid()
						+ ".De nhan lai url game, soan LV LINK gui "
						+ msgObject.getServiceid() + ".");
		msgObject.setMsgtype(1);
		msgObject.setContenttype(0);
		DBUtil.sendMT(msgObject);
		Thread.sleep(1000);
		// mt2
		msgObject
				.setUsertext("Dien thoai ho tro 1900571566.Chi tiet tai http://funzone.vn");
		msgObject.setMsgtype(0);
		msgObject.setContenttype(0);
		DBUtil.sendMT(msgObject);
		Thread.sleep(1000);
			}
		}

			msgObject
					.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			DBUtil.sendMT(msgObject);
			Thread.sleep(1000);

		
		return null;

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

	public String insertMO2lottery(MsgObject msgObject) throws Exception {

		Util.logger.info("insertSMSgamefarm:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mo_queue";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,  INFO, RECEIVE_DATE, REQUEST_ID)"
				+ " values(?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnection("gamefarm");

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getUsertext());
			statement.setString(5, msgObject.getUsertext());
			statement.setTimestamp(6, msgObject.getTTimes());
			statement.setBigDecimal(7, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("insertSMSvender:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return "-1";
			}
			statement.close();
			return "1";
		} catch (SQLException e) {
			Util.logger.error("insertSMSvender:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive queue:" + e.toString());
			return "-1";
		} catch (Exception e) {
			Util.logger.error("insertSMSvender:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive queue:" + e.toString());
			return "-1";
		}

		finally {
			dbpool.cleanup(connection);

		}

	}

	private static BigDecimal add2SMSSendFailed(MsgObject msgObject) {

		Util.logger.info("add2SMSSendFailed:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CPID)"
				+ " values(?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getKeyword());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

}
