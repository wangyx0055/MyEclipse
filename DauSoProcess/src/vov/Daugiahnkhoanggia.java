package vov;

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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.sql.ResultSet;

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject; //HAPTT

import cs.ExecuteADVCR;
import spam.DBUtils2;

public class Daugiahnkhoanggia extends ContentAbstract {
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			Collection messages = new ArrayList();
			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();
			String sUsertext = msgObject.getUsertext();

			sUsertext = replaceAllWhiteWithOne(sUsertext);
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);

			String info = "Gia dang chiem uu the nam trong khoang $T1 den $T2. De biet muc gia ban muon da co nguoi dat chua, soan: DG gia gui 8551";
			info = getString(_option, "info", info);
			String inactive = "Chuong trinh tiep theo chua bat dau. DTHT 1900571566";
			inactive = getString(_option, "inactive", inactive);
			String[] sTokens = sUsertext.split(" ");
			String[] kq1 = getPhien();
			String sBeginDate = "";
			String sEndDate = "";
			String sSanpham = "xxxx";

			int iPhien = 0;

			if ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator()
					.toUpperCase())) {
				msgObject
						.setUsertext("Hien tai dich vu chua ho tro mang cua ban.Vui long quay tro lai sau.DTHT 1900571566");
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			if (kq1[0].equalsIgnoreCase("")) {
				msgObject.setUsertext(inactive);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			HashMap bgia = new HashMap();

			iPhien = Integer.parseInt(kq1[0]);
			Util.logger.info("iPhien" + iPhien);
			sBeginDate = String2Date(kq1[1]);
			sEndDate = String2Date(kq1[2]);
			sSanpham = kq1[3];
			String[] gw = new String[3];
			gw = getWinner(iPhien);
			if (sTokens.length != 1) {
				msgObject
						.setUsertext("Tin nhan sai cu phap.Soan tin "
								+ keyword.getKeyword()
								+ " giaSP gui "
								+ keyword.getServiceid()
								+ " de tham gia chuong trinh Dau gia nguoc.DTHT:1900571566");
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));

			} else {
				// tim khoang gia

				int amountWin = Integer.parseInt(gw[1]);
				if (amountWin < 0)
					amountWin = 0;
				amountWin=amountWin*1000;
				Random random = new Random();

				int randomMin = 1000 + random.nextInt(20) * 1000;

				int randomMax = 10000 + random.nextInt(20) * 1000;

				int minAmount = amountWin - randomMin;

				if (minAmount < 0)
					minAmount = 1000;

				int maxAmount = amountWin + randomMax;

				info = info.replace("$T1", minAmount + "");

				info = info.replace("$T2", maxAmount + "");

				info = info.replace("$Y", sSanpham);
				msgObject.setUsertext(info);
				msgObject.setMsgtype(1);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

			}

			return messages;

		} catch (Exception ex) {
			Util.logger.error("Error:" + ex.toString());
			return null;
		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}


	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ((temp == null) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	public static String getMobileOperatorNew(String userid, int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("gateway");

			String query = "SELECT operator FROM icom_isdnseries WHERE prefix= substr('"
					+ userid + "',1, length(prefix)) ";
			if (type == 1 || type == 0) {
				query += " and type=" + type;
			}

			Vector result = DBUtil.getVectorTable(connection, query);
			for (int i = 0; i < result.size(); i++) {
				Vector item = (Vector) result.elementAt(i);
				tmpOperator = (String) item.elementAt(0);
			}

			return tmpOperator;
		} catch (Exception ex) {
			Util.logger.sysLog(LogValues.INFORMATIVE, "Utils",
					"getMobileOperator: Get MobileOpereator Failed"
							+ ex.toString());
			return tmpOperator;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	
	// Tim nguoi tra gia thap nhat va duy nhat

	private String[] getWinner(int phien) {
		String[] result = new String[3];
		result[0] = "-1";
		result[1] = "-1";
		result[2] = "-1";
		String Winner = "-1";
		String WinnerUser = "";
		String WinnerPrice = "";

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String query1 = "select min(price)from (select price,count(1) from (select * from daugia_sieure where phien="
					+ phien
					+ ") as T1 group by price having count(1) = 1) as T2";
			statement = connection.prepareStatement(query1);
			Util.logger.info("select price:" + query1);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					WinnerPrice = rs.getString(1);
					Util.logger.info("WinnerPrice" + WinnerPrice);
				}
			}
			dbpool.cleanup(rs, statement);
			if (!"".equals(WinnerPrice)) {
				Winner = "1";
				int iWinnerPrice = Integer.parseInt(WinnerPrice);
				String query2 = "select user_id from daugia_sieure where price = "
						+ iWinnerPrice + " and phien=" + phien;
				statement = connection.prepareStatement(query2);
				Util.logger.info("select user win:" + query2);
				if (statement.execute()) {
					rs = statement.getResultSet();
					while (rs.next()) {
						WinnerUser = rs.getString(1);
						Util.logger.info("WinnerUser" + WinnerUser);
					}
				}
			}
			result[0] = Winner;
			result[1] = WinnerPrice;
			result[2] = WinnerUser;
		} catch (SQLException ex2) {
			Util.logger.error("getWinner. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("getWinner. Ex3:" + ex3.toString());
			return result;

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}

		return result;
	}

	
	private static String[] getPhien() {
		String[] result = new String[4];
		result[0] = "";
		result[1] = "";
		result[2] = "";
		result[3] = "";

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT phien,begintime,endtime,sanpham FROM daugia_phien  where isprocess=1 ";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getInt(1) + "";
					result[1] = rs.getString(2);
					result[2] = rs.getString(3);
					result[3] = rs.getString(4);
					return result;
				}
			}
			return result;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return result;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
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

	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "&");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			// setParameter(key, value);
			_params.put(key, value);
		}

		return _params;
	}

	// Chuyen kieu 2008:02:27 09:00:00 thanh 27/02/2008
	private String String2Date(String str) {
		String tmp = "";
		str = str.replace(':', '/');
		str = str.replace('-', '/');
		str = str.replace(':', '/');
		tmp = str.substring(0, 10);
		String[] sToken = tmp.split("/");
		tmp = sToken[2] + "/" + sToken[1] + "/" + sToken[0];
		return tmp;
	}
}