package tuyensinh;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.Utils;

public class Diemthi extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		// TODO Auto-generated method stub
		Collection messages = new ArrayList();
		String info = msgObject.getUsertext();
		String[] sTokens = replaceAllWhiteWithOne(info).split(" ");

		// String MASO = "1";
		if (sTokens.length == 1) {
			msgObject
					.setUsertext("Cu phan tin nhan khong hop le. Soan: "
							+ keyword.getKeyword()
							+ " SoBaoDanh gui "
							+ keyword.getServiceid()
							+ " de xem Diem Thi DH,CD nam 2010(SoBaoDanh bao gom ma truong). HT: 1900571566");

			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			messages.add(new MsgObject(msgObject));
			return messages;

		}

		String sobaodanh = sTokens[1].trim().toUpperCase();

		String validtentruong = "x";
		String validmatruong = "x";

		Util.logger.info(this.getClass().getName() + "sobaodanh:" + sobaodanh
				+ "");
		if (sobaodanh.length() > 3) {
			validmatruong = sobaodanh.substring(0, 3);
			validtentruong = validMaTruong(validmatruong);
		}

		if ("x".equalsIgnoreCase(validtentruong)) {
			msgObject
					.setUsertext("So Bao Danh hoac Ma Truong khong hop le. Soan: "
							+ keyword.getKeyword()
							+ " SoBaoDanh gui "
							+ keyword.getServiceid()
							+ " de xem Diem Thi DH,CD nam 2010. Ho tro: 1900571566");

			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			messages.add(new MsgObject(msgObject));
			return messages;
		} else {

			String phone = msgObject.getUserid();

			String giff_userid = "";
			String giff_telco = "";
			boolean giff = false;

			if (sTokens.length > 2) {
				giff_userid = ValidISDN(sTokens[2]);
				if (!giff_userid.equalsIgnoreCase("-")) {
					giff_telco = Utils.getMobileOperator(giff_userid, 4);

					if ("-".equals(giff_telco)) {
						giff = false;
					} else {
						giff = true;
					}

				}

			}

			if (giff == true) {
				saverequest(giff_userid, validmatruong, sobaodanh);
				sendGifMsg(
						msgObject.getServiceid(),
						giff_userid,
						giff_telco,
						msgObject.getKeyword(),
						"Sodt "
								+ msgObject.getUserid()
								+ " da dang ky xem Diem SBD:"
								+ sobaodanh
								+ " cho ban. He thong se gui den ban ngay khi co",
						msgObject.getRequestid(), 0);

				msgObject
						.setUsertext("Ban da dang ky xem Diem cua SBD: "
								+ sobaodanh + " thanh cong cho thue bao "
								+ giff_userid);
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;

			} else {

				saverequest(phone, validmatruong, sobaodanh);

				msgObject
						.setUsertext("Ban da dang ky xem Diem cua SBD: "
								+ sobaodanh
								+ " thanh cong. He thong se gui den ban ngay khi co. HT 1900571566");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			
		}
	}

	public String ValidISDN(String sISDN) {
		Util.logger.info(this.getClass().getName() + "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		if (sISDN.trim().length() < 8) {
			return "-";
		}
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	private String validMaTruong(String code) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select tentruong from tuyensinh_dm_truong  where upper(matruong)='"
					+ code.toUpperCase() + "' ";

			// query1 = "select db_name()";

			
			Util.logger.info(this.getClass().getName() + "query1:" + query1
					+ "");
			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				String codetemp = (String) item.elementAt(0);

				return codetemp;
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbpool.cleanup(connection);

		}
		return "x";
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

	private void sendGifMsg(String serviceid, String userid, String operator,
			String service, String mtcontent, BigDecimal requestid,
			int contenttype) {
		try {

			MsgObject msg = new MsgObject();
			msg.setContenttype(contenttype);
			msg.setUserid(userid);
			msg.setMobileoperator(operator);
			msg.setMsgtype(0);
			msg.setRequestid(requestid);
			msg.setServiceid(serviceid);
			msg.setKeyword(service);
			msg.setUsertext(mtcontent);

			DBUtil.sendMT(msg);

		} catch (Exception ex) {
			Util.logger
					.sysLog(2, this.getClass().getName(), "sendGifMsgFailed");
		}
	}

	private static boolean saverequest(String userid, String code,
			String sobaodanh) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into tuyensinh_diemthi_dk( userid,matruong,sobaodanh) values ('"
					+ userid + "','" + code + "','" + sobaodanh + "')";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to tuyensinh_diemchuan_dk");
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

}
