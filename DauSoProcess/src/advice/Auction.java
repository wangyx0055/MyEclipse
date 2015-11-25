package advice;

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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.sql.ResultSet;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Auction extends ContentAbstract {
	// private String sUserid = "";
	// public String sServiceid = "";
	// public String sKeyword = "";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		String sUserid = msgObject.getUserid();
		String sKeyword = msgObject.getKeyword();
		String sServiceid = msgObject.getServiceid();
		String sUsertext = msgObject.getUsertext();
		sUsertext = replaceAllWhiteWithOne(sUsertext);
		String[] sTokens = sUsertext.split(" ");
		if (sTokens.length == 1) {
			msgObject
					.setUsertext("Ban da soan sai cu phap.Hay tham gia mua san pham, soan tin: "
							+ keyword.getKeyword()
							+ " giaSP gui "
							+ keyword.getServiceid()
							+ ". Vi du: "
							+ keyword.getKeyword()
							+ " 121 gui "
							+ keyword.getServiceid() + ". DT ho tro 1800095");
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));

		} else {
			String[] sMTReturn = new String[2];

			sMTReturn = mtReturn(sKeyword, sServiceid, sUserid, sUsertext);

			msgObject.setUsertext(sMTReturn[0]);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));

			if (!"".equals(sMTReturn[1])) {
				msgObject.setUsertext(sMTReturn[1]);
				msgObject.setMsgtype(0);
				messages.add(new MsgObject(msgObject));
			}

		}

		return messages;
	}

	private String[] mtReturn(String keyword, String serviceid, String userid,
			String usertext) {
		String[] mtReturn = new String[2];

		int iPhien = 1;
		int iPrice = -1;
		String sMaxPrice = "";
		String sMinPrice = "";
		int isNewSession = -1;
		int isMO1 = -1;
		int isEqual = -1;
		int isWinner = -1;
		int iNum = 0;
		String sWinnerUser = "";
		String sWinnerPrice = "";
		String sBeginDate = "";
		String sEndDate = "";
		String sSanpham = "xxxx";
		usertext = replaceAllWhiteWithOne(usertext);
		String[] sTokens = usertext.split(" ");

		String[] kq1 = checkSession();
		iPhien = Integer.parseInt(kq1[0]);
		Util.logger.info("iPhien" + iPhien);
		isNewSession = Integer.parseInt(kq1[1]);
		sBeginDate = String2Date(kq1[2]);
		sEndDate = String2Date(kq1[3]);

		if ("KQ".equalsIgnoreCase(sTokens[1].trim())) {
			if (iPhien == 1)
				mtReturn[0] = "Day la phien choi dau tien chua co nguoi thang cuoc. DT ho tro 1800095";
			else {
				String[] sWinnerKQ = getWinnerKQ("SIEURE", serviceid);
				String winnerUserKQ = sWinnerKQ[0];
				String winnerPrice = sWinnerKQ[1];
				mtReturn[0] = "Thue bao " + winnerUserKQ
						+ " duoc mua SP voi gia : " + winnerPrice;
			}
		} else {
			boolean isSaveFailed = false;
			String mtWinner = "";
			iPrice = getPrice(sTokens);
			if (iPrice > 0) {

				String[] kq2 = checkPrice(userid, keyword, serviceid, iPrice,
						iPhien);
				// iMaxPrice=Integer.parseInt(kq2[0]);
				// iMinPrice=Integer.parseInt(kq2[1]);
				sMaxPrice = kq2[0];
				sMinPrice = kq2[1];

				isEqual = Integer.parseInt(kq2[2]);
				iNum = Integer.parseInt(kq2[3]);
				isMO1 = Integer.parseInt(kq2[4]);

				int iNumEqual = Integer.parseInt(kq2[5]);

				Util.logger.error("iNum" + iNum + " iNumEqual: " + iNumEqual);

				Util.logger.error("iNum" + iNum + " isEqual: " + isEqual);

				mtReturn[0] = "Gia ban tra: " + iPrice
						+ "000d, chuong trinh bat dau tu ngay " + sBeginDate
						+ " ket thuc ngay " + sEndDate
						+ ". De mua gia re nhat soan:" + keyword
						+ " GiaSP gui " + serviceid;

				if (iNum == 0) {
					if (isEqual == 1) {
						mtReturn[1] = "Co nguoi tra gia san pham nhu muc gia ban tra. Gia ban tra khong con la duy nhat. Soan tin: "
								+ keyword
								+ " giaSP gui "
								+ serviceid
								+ " de chien thang";
					}
				} else {
					if (isEqual == 1) {
						mtReturn[1] = "Muc gia "
								+ iPrice
								+ "000d cho san pham hien dang la duy nhat nhung chua phai thap nhat."
								+ " Hay tiep tuc soan " + keyword
								+ " giaSP gui " + serviceid
								+ " de chien thang!";
					} else {
						mtReturn[1] = "Muc ban tra "
								+ iPrice
								+ "000d cho san pham hien la duy nhat nhung chua phai la thap nhat. Hay soan tin: "
								+ keyword + " giaSP gui " + serviceid
								+ " de gianh chien thang!";
					}
				}
			} else {
				mtReturn[0] = "Gia ban chon khong hop le. Hay tham gia mua san pham, soan tin: "
						+ keyword
						+ " giaSP gui "
						+ serviceid
						+ ". Vi du: "
						+ keyword + " 121 gui " + serviceid;

			}

		}

		return mtReturn;

	}

	// Tach gia tu MO
	private int getPrice(String sTokens[]) {
		int flag = -1;
		if (sTokens.length >= 2) {
			String sPrice = sTokens[1];
			if (isNumeric(sPrice)) {
				flag = Integer.parseInt(sPrice);
			}
		}
		Util.logger.error("getPrice" + flag);
		return flag;
	}

	private boolean isNumeric(String sVal) {
		int iVal;
		if ("".equals(sVal) || sVal == "") {
			return false;
		}
		try {
			Util.logger.info("isNumeric" + sVal);
			iVal = Integer.parseInt(sVal.trim());
			return true;
		} catch (Exception e) {
			Util.logger.error("Error" + e);
		}
		return false;
	}

	private String[] checkSession() {

		String[] kq = new String[20];
		String newSession = "-1";
		String Winner = "-1";
		String WinnerPrice = "xxx";
		String WinnerUser = "xxx";
		String begintime = "xxx";
		String endtime = "xxx";
		String sp = "xxx";

		Connection connection = null;
		PreparedStatement statement = null;

		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String query1 = "select phien from sfone_phiensr";
			statement = connection.prepareStatement(query1);
			String phienTemp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					phienTemp = rs.getString(1);
					Util.logger.info("Phientemp:" + phienTemp);
				}
			}
			dbpool.cleanup(rs, statement);

			// int phien = Integer.parseInt(kq[0]);
			int phien = Integer.parseInt(phienTemp);

			// check xem da ket thuc phien hay chua. Neu da ket thuc phien thi
			// tim nguoi trung thuong

			String query2 = "select * from sfone_dmphiensr where current_timestamp >"
					+ " endtime and phien=" + phien;
			statement = connection.prepareStatement(query2);
			if (statement.execute()) {
				rs = statement.getResultSet();
				if (rs.next()) {
					newSession = "1";
					Util.logger.info("newSession:" + newSession);
					phien = phien + 1;
					String sqlUpdate = "update sfone_phiensr set phien="
							+ phien;
					DBUtil.executeSQL(connection, sqlUpdate);

					DBUtil
							.executeSQL(
									connection,
									"insert into sfone_dmphiensr(phien,begintime,endtime) select phien + 1,adddate(begintime,7),adddate(endtime,7)  from sfone_dmphiensr where phien="
											+ (phien - 1));
				}
			}
			dbpool.cleanup(rs, statement);

			String query5 = "select begintime,endtime from sfone_dmphiensr where phien="
					+ phien;
			statement = connection.prepareStatement(query5);
			if (statement.execute()) {
				if (statement.execute()) {
					rs = statement.getResultSet();
					while (rs.next()) {
						begintime = rs.getString(1);
						endtime = rs.getString(2);
						Util.logger.info("begintime:" + begintime + "endtime: "
								+ endtime);
					}
				}

			}

			kq[0] = phienTemp;
			kq[1] = newSession;
			kq[2] = begintime;
			kq[3] = endtime;
			// kq[2]=Winner;
			// kq[3]=WinnerPrice;
			// kq[4]=WinnerUser;
			// kq[5]=begintime;
			// kq[6]=endtime;
			// kq[7]=sp;
		} catch (SQLException ex2) {
			Util.logger.error("checkSession. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("checkSession. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}

		return kq;
	}

	// Tim nguoi tra gia thap nhat va duy nhat

	private String[] getWinner(int phien) {
		String[] result = new String[10];
		String Winner = "-1";
		String WinnerUser = "";
		String WinnerPrice = "";

		Connection connection = null;
		PreparedStatement statement = null;
		// String sqlString = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String query1 = "select min(price)from (select price,count(*) from (select * from sfone_sieure where phien="
					+ phien
					+ ") as T1 group by price having count(*) = 1) as T2";
			statement = connection.prepareStatement(query1);
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
				String query2 = "select user_id from sfone_sieure where price = "
						+ iWinnerPrice + " and phien=" + phien;
				statement = connection.prepareStatement(query2);
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

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}

		return result;
	}

	private String[] checkPrice(String userid, String keyword,
			String serviceid, int price, int phienid) {
		String[] kq2 = new String[20];
		String maxprice = "xxx";
		String minprice = "xxx";
		String Equal = "-1";
		String num = "0";
		String MO1 = "1";
		String winner = "";
		String sequence = "";
		String NumEqualTemp = "0";

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String sqlInsert = "Insert into sfone_sieure (user_id,command_code,service_id,price,phien) values ('"
					+ userid
					+ "','"
					+ keyword
					+ "','"
					+ serviceid
					+ "',"
					+ price + "," + phienid + ")";
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger
						.error("sfone_sieure: Insert into sfone_sieure Failed");
			}
			String query1 = "select price from sfone_sieure where user_id='"
					+ userid + "'";
			statement = connection.prepareStatement(query1);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					MO1 = "-1";
					// rs = statement.getResultSet();
					String query2 = "select max(price),min(price) from sfone_sieure where phien="
							+ phienid;
					statement = connection.prepareStatement(query2);

					if (statement.execute()) {
						ResultSet rs2 = statement.getResultSet();
						while (rs2.next()) {
							maxprice = rs2.getString(1);
							minprice = rs2.getString(2);
						}
						dbpool.cleanup(rs2, statement);
					}

					String query3 = "select count(*) from sfone_sieure where price ="
							+ price + " and phien=" + phienid;
					statement = connection.prepareStatement(query3);
					int iNumEqual = 0;

					if (statement.execute()) {
						ResultSet rs3 = statement.getResultSet();
						while (rs3.next()) {
							NumEqualTemp = rs3.getString(1);
						}
						dbpool.cleanup(rs3, statement);
					}
					iNumEqual = Integer.parseInt(NumEqualTemp);
					if (iNumEqual != 1)
						Equal = "1";// isEqual=true
				}

				String query4 = "select count(*) from sfone_sieure where price <"
						+ price + " and phien=" + phienid;
				statement = connection.prepareStatement(query4);
				if (statement.execute()) {
					ResultSet rs4 = statement.getResultSet();
					while (rs4.next()) {
						num = rs4.getString(1);
					}
					dbpool.cleanup(rs4, statement);
				}

			}
			kq2[0] = maxprice;
			kq2[1] = minprice;
			kq2[2] = Equal;
			kq2[3] = num;
			kq2[4] = MO1;
			kq2[5] = NumEqualTemp;
			// return kq2;

		} catch (SQLException ex2) {
			Util.logger.error("checkPrice. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("checkPrice. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}

		return kq2;
	}

	// Check khach hang trung thuong cua phien choi truoc do
	private String[] getWinnerKQ(String GameID, String ServiceID) {
		int iConn = -3;
		String[] winnerKQ = new String[10];
		String winner = "";
		String sequence = "";
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String query = "select user_id, sequence from sfone_winner where game_id ="
					+ GameID.toUpperCase() + "'";

			statement = connection.prepareStatement(query);
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					winner = rs.getString(1);
					sequence = rs.getString(2);
				}
			}
		} catch (SQLException ex2) {
			Util.logger.error("getWinner. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("getWinner. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}
		return winnerKQ;
	}

	private int saveWinner(String gameid, String keyword, String serviceid,
			String userid, String mttext, long lsequence) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		DBPool dbpool = new DBPool();
		// Util.logger.info("sendMT:" + msgObject.getUserid()+ "@TO" +
		// msgObject.getServiceid() + "@" + msgObject.getUsertext() );
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return -1;
			}
			String sqlInsert = "Insert into sfone_winner (game_id,command_code,user_id,service_id,info,sequence) values ('"
					+ gameid
					+ "','"
					+ keyword
					+ "','"
					+ userid
					+ "','"
					+ serviceid + "','" + mttext + "'," + lsequence + ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("SieuRe: Insert in to sfone_winner Failed");
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.error("SieuRe: Error:" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error("SieuRe: Error:" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
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

	private String reverseString(String str) {
		String tmp = "";
		for (int i = str.length() - 1; i >= 0; i--) {
			tmp += str.charAt(i);
		}
		return tmp;
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