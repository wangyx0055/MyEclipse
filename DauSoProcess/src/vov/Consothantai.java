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

public class Consothantai extends ContentAbstract {
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
			String stime = "Chuong trinh chua bat dau.Hay quay lai vao chuong trinh lan sau tren VOV giao thong 91MHz.DTHT:1900571566";
			stime = getString(_option, "stime", stime);
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
				msgObject.setUsertext(stime);
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
			if (sTokens.length == 1) {
				//Muc gia ban dua ra khong hop le. Soan tin: AM conso gui 8751 de chien thang giai thuong len toi 100 trieu dong. DTHT: 1900571566
				msgObject
						.setUsertext("Tin nhan sai cu phap.Soan tin "
								+ keyword.getKeyword()
								+ " conso gui "
								+ keyword.getServiceid()
								+ " de chien thang giai thuong len toi 30 trieu dong.DTHT:1900571566");
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));

			} else {

				String sMTReturn = "";
				sMTReturn = mtReturn(sKeyword, sServiceid, sUserid, sUsertext,
						iPhien, sSanpham);
			    Util.logger.info("sMTReturn:"+sMTReturn);
				msgObject.setUsertext(sMTReturn);
				msgObject.setMsgtype(1);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				
				msgObject.setUsertext("De biet khoang chua con so dang chiem uu the, soan: "
								+ keyword.getKeyword()
								+ " gui 8551. De biet con so ban muon da co ai dat chua, soan: "
								+ keyword.getKeyword()
								+ " con so gui 8551. DTHT: 1900571566");
				msgObject.setMsgtype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

				String[] gw_old = getWinnerKQ(iPhien);
				String[] gw = getWinner(iPhien);
				Util.logger.info("user old:" + gw_old[0] + ",price: "
						+ gw_old[1]);
				Util.logger.info("gw[0]:" + gw[0] + "gw[1]:" + gw[1] + "gw[2]:"
						+ gw[2]);
				if (!gw_old[1].equalsIgnoreCase("-1")) {
					Util.logger.info("Co nguoi thang cu");
					String text = "Con so cua ban chon khong con la duy nhat va cao nhat. Hay nhanh tay dat so khac de tro thanh nguoi thang cuoc";
					String giff_telco = "";
					if (!gw_old[1].equalsIgnoreCase(gw[1])) {
						if (!gw[0].equalsIgnoreCase("-1")) {
							Util.logger
									.info("Co nguoi thang moi , thay the nguoi thag cu");
							try {
								giff_telco = getMobileOperatorNew(gw_old[0], 2);
								Util.logger.info("operator giff:" + giff_telco);
								sendGifMsg(msgObject.getServiceid(), gw_old[0],
										giff_telco, msgObject.getKeyword(),
										text, msgObject.getRequestid(), 0);
							} catch (Exception ex3) {
								Util.logger
										.error("Khong gui duoc tin cho nguoi thang cu");

							}
							updateWinner(sKeyword, sServiceid, gw[2], gw[1],
									iPhien);

						} else {
							try {
								giff_telco = getMobileOperatorNew(gw_old[0], 2);
								Util.logger.info("operator giff:" + giff_telco);
								sendGifMsg(msgObject.getServiceid(), gw_old[0],
										giff_telco, msgObject.getKeyword(),
										text, msgObject.getRequestid(), 0);
							} catch (Exception ex3) {
								Util.logger
										.error("Khong gui duoc tin cho nguoi thang cu");

							}
							Util.logger.info("Hien tai khong co ai thang");
							deleteWinner(iPhien);
						}
					}

				} else if (!gw[0].equalsIgnoreCase("-1")) {
					Util.logger.info("Co nguoi thang moi");
					saveWinner(sKeyword, sServiceid, sUserid, gw[1], iPhien);
				} else
					Util.logger.info("Hien tai khong co ai thang");
				return null;
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
					.sysLog(2, this.getClass().getName(),
							"Tra tin nhan ve cho nguoi ko con la duy nhat va thap nhat loiiiii");
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

	private String mtReturn(String keyword, String serviceid, String userid,
			String usertext, int iPhien, String sSanpham) {
		String mtReturn = "";

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
		usertext = replaceAllWhiteWithOne(usertext);
		String[] sTokens = usertext.split(" ");

		boolean isSaveFailed = false;
		String mtWinner = "";
		iPrice = getPrice(sTokens);
		if ((iPrice > 0)&&(iPrice<=30000)) {

			deletePrice(iPrice);
			String[] kq2 = checkPrice(userid, keyword, serviceid, iPrice,
					iPhien);
			sMaxPrice = kq2[0];
			sMinPrice = kq2[1];

			isEqual = Integer.parseInt(kq2[2]);
			iNum = Integer.parseInt(kq2[3]);
			isMO1 = Integer.parseInt(kq2[4]);

			int iNumEqual = Integer.parseInt(kq2[5]);

			String[] gw = new String[3];
			gw = getWinner(iPhien);
			String num = "";
			if ((iPrice == Integer.parseInt(gw[1]))
					&& (!gw[0].equalsIgnoreCase("-1"))) {
				mtReturn = "Con so cua ban la "
						+ iPrice
						+ ".Chuc mung ban, day dang la con so cao nhat va duy nhat tai thoi diem nay. DTHT: 1900571566";

				return mtReturn;

			}

			if (iNum == 0) {
				if (isEqual == 1) {
					//Con so cua ban la xxxxxx. Day la con so cao nhat nhung khong phai duy nhat. Nhanh tay dat so khac de tro thanh nguoi thang cuoc. DTHT: 1900571566
					mtReturn = "Con so cua ban la "
							+ iPrice
							+ ". Day la con so cao nhat nhung khong phai duy nhat. Nhanh tay dat so khac de tro thanh nguoi thang cuoc. DTHT: 1900571566";

				}
			} else {
				if (isEqual == 1) {
					//Con so cua ban la xxxxxx. Day chua phai la con so cao nhat va duy nhat. Hay nhanh tay dat so khac de tro thanh nguoi thang cuoc. DTHT: 1900571566
					mtReturn = "Con so cua ban la "
							+ iPrice
							+ ". Day chua phai la con so cao nhat va duy nhat. Hay nhanh tay dat so khac de tro thanh nguoi thang cuoc. DTHT: 1900571566";
				} else {
					//Con so cua ban la xxxxxx. Day la con so duy nhat nhung khong phai cao nhat. Hay nhanh tay dat so khac de tro thanh nguoi thang cuoc. DTHT: 1900571566
					mtReturn = "Con so cua ban la "
							+ iPrice
							+ ". Day la con so duy nhat nhung khong phai cao nhat. Hay nhanh tay dat so khac de tro thanh nguoi thang cuoc. DTHT: 1900571566";
				}
			}
		} else {
			//Muc gia ban dua ra khong hop le. Soan tin: AM conso gui 8751 de chien thang giai thuong len toi 100 trieu dong. DTHT: 1900571566
			mtReturn = "Con so ban dua ra khong hop le.Soan tin " + keyword
					+ " conso gui " + serviceid
					+ " de chien thang giai thuong len toi 30 trieu dong. DTHT: 1900571566";

		}
		return mtReturn;

	}

	// Tach gia tu MO
	private int getPrice(String sTokens[]) {
		int flag = -1;
		try {
			if (sTokens.length >= 2) {
				String sPrice = sTokens[1].trim();

				if (isNumeric(sPrice)) {
					flag = Integer.parseInt(sPrice);
				}
			}
		} catch (Exception ex) {
			return -1;
		}
		return flag;
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
			String query1 = "select phien from tbphien";
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

			String query2 = "select * from conso_phien where current_timestamp >"
					+ " endtime and phien=" + phien;
			statement = connection.prepareStatement(query2);
			if (statement.execute()) {
				rs = statement.getResultSet();
				if (rs.next()) {
					newSession = "1";
					Util.logger.info("newSession:" + newSession);
					phien = phien + 1;
					String sqlUpdate = "update tbphien set phien=" + phien;
					DBUtil.executeSQL(connection, sqlUpdate);

					DBUtil
							.executeSQL(
									connection,
									"insert into conso_phien(phien,begintime,endtime) select phien + 1,adddate(begintime,7),adddate(endtime,7)  from conso_phien where phien="
											+ (phien - 1));
				}
			}
			dbpool.cleanup(rs, statement);

			String query5 = "select begintime,endtime,sanpham from conso_phien where phien="
					+ phien;
			statement = connection.prepareStatement(query5);
			if (statement.execute()) {
				if (statement.execute()) {
					rs = statement.getResultSet();
					while (rs.next()) {
						begintime = rs.getString(1);
						endtime = rs.getString(2);
						sp = rs.getString(3);
						Util.logger.info("begintime:" + begintime + "endtime: "
								+ endtime);
					}
				}

			}

			kq[0] = phienTemp;
			kq[1] = newSession;
			kq[2] = begintime;
			kq[3] = endtime;
			kq[4] = sp;
		} catch (SQLException ex2) {
			Util.logger.error("checkSession. Ex:3" + ex2.toString());

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
		String[] result = new String[3];
		result[0] = "-1";
		result[1] = "-1";
		result[2] = "-1";
		String Winner = "-1";
		String WinnerUser = "";
		String WinnerPrice = "-1";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String query1 = "select max(price)from (select price,count(1) from (select * from conso_sieure where phien="
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
			if (WinnerPrice.equalsIgnoreCase(null))
			{
				WinnerPrice="-1";
			}
			dbpool.cleanup(rs, statement);
			if (!"".equals(WinnerPrice)) {
				Winner = "1";
				int iWinnerPrice = Integer.parseInt(WinnerPrice);
				String query2 = "select user_id from conso_sieure where price = "
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
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;
		PreparedStatement statement3 = null;
		PreparedStatement statement4 = null;
		String sqlString = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String sqlInsert = "Insert into conso_sieure (user_id,command_code,service_id,price,phien) values ('"
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
						.info("conso_sieure: Insert into conso_sieure Failed");
			}
			String query1 = "select price from conso_sieure where user_id='"
					+ userid + "'";
			statement1 = connection.prepareStatement(query1);
			if (statement1.execute()) {
				rs = statement1.getResultSet();
				while (rs.next()) {
					MO1 = "-1";
					// rs = statement.getResultSet();
					String query2 = "select min(price),max(price) from conso_sieure where phien="
							+ phienid;
					statement2 = connection.prepareStatement(query2);

					if (statement2.execute()) {
						ResultSet rs2 = statement2.getResultSet();
						while (rs2.next()) {
							maxprice = rs2.getString(1);
							minprice = rs2.getString(2);
						}
						dbpool.cleanup(rs2, statement2);
					}

					String query3 = "select count(1) from conso_sieure where price ="
							+ price + " and phien=" + phienid;
					statement3 = connection.prepareStatement(query3);
					int iNumEqual = 0;

					if (statement3.execute()) {
						ResultSet rs3 = statement3.getResultSet();
						while (rs3.next()) {
							NumEqualTemp = rs3.getString(1);
						}
						dbpool.cleanup(rs3, statement3);
					}
					iNumEqual = Integer.parseInt(NumEqualTemp);
					if (iNumEqual != 1)
						Equal = "1";// isEqual=true
				}
				String query4 = "select count(1) from conso_sieure where price >"
						+ price + " and phien=" + phienid;
				statement4 = connection.prepareStatement(query4);
				if (statement4.execute()) {
					ResultSet rs4 = statement4.getResultSet();
					while (rs4.next()) {
						num = rs4.getString(1);
					}
					dbpool.cleanup(rs4, statement4);
				}
			}
			kq2[0] = maxprice;
			kq2[1] = minprice;
			kq2[2] = Equal;
			kq2[3] = num;
			kq2[4] = MO1;
			kq2[5] = NumEqualTemp;
			// return kq2;
			dbpool.cleanup(rs, statement1);
			
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

	// Check khach hang duy nhat va nho nhat truoc do cua phien
	private String[] getWinnerKQ(int phien) {
		String[] winner = new String[5];
		winner[0] = "-1";
		winner[1] = "-1";
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
			String query = "select user_id,info from conso_winner where phien ="
					+ phien;

			statement = connection.prepareStatement(query);
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					winner[0] = rs.getString(1);
					winner[1] = rs.getString(2);

				}
			}
		} catch (SQLException ex2) {
			Util.logger.error("getWinnerKQ Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("getWinnerKQ Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}
		return winner;
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
		String query = "SELECT phien,begintime,endtime,sanpham FROM conso_phien  where isprocess=1 ";

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

	
	private int saveWinner(String keyword, String serviceid, String userid,
			String mttext, int phien) throws Exception {

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
			String sqlInsert = "Insert into conso_winner (command_code,user_id,service_id,info,phien) values ('"
					+ keyword
					+ "','"
					+ userid
					+ "','"
					+ serviceid
					+ "','"
					+ mttext + "'," + phien + ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.info("SieuRe: Insert in to conso_winner Failed");
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

	// Update khach hang dua ra gia duy nhat va nho nhat
	private static boolean updateWinner(String keyword, String serviceid,
			String userid, String mttext, int phien) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "UPDATE conso_winner  SET command_code ='"
					+ keyword + " ',user_id= '" + userid + "',service_id='"
					+ serviceid + "',info='" + mttext + "' WHERE phien="
					+ phien;
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update nguoi trung thuong to send " + userid
						+ " to dbcontent");
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

	private static boolean deletePrice(int price) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM conso  WHERE id=" + price;
			Util.logger.info(" DELETE Gia dau: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Loi xoa gia dau ");
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

	private static boolean deleteWinner(int phien) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM conso_winner  WHERE phien="
					+ phien;
			Util.logger.info(" DELETE WINNER: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Loi xoa nguoi duy nhat thap nhat ");
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