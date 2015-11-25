package services.textbases;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

// import com.vmg.sms.common.DateProc;
// import java.sql.ResultSet;

// import com.vmg.sms.common.DBUtil;
import common.DBUtils;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class SieutocTG extends ContentAbstract {
	String GAMEID = "SIEUTOC";
	String INFOCS = "So huu OMNIA trong tam tay: TI gui 8551";
	String SUPPORT = "DT ho tro:0435561862";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			Collection messages = new ArrayList();
			MsgObject mt = msgObject;

			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();

			String sMTReturn = mtReturn(sUserid, sServiceid, sKeyword,
					msgObject.getMobileoperator(), msgObject.getRequestid());
			mt.setUsertext(sMTReturn);
			mt.setMsgtype(1);
			messages.add(new MsgObject(mt));

			// ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), sServiceid,
			// sUserid,
			// msgObject.getKeyword(), msgObject.getRequestid(),
			// msgObject.getTTimes(), msgObject.getMobileoperator());
			return messages;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}

	public static long String2MilisecondNew(String strInputDate) {
		// System.err.println("String2Milisecond.strInputDate:" + strInputDate);
		String strDate = strInputDate.trim();
		int i, nYear, nMonth, nDay, nHour, nMinute, nSecond;
		String strSub = null;
		if (strInputDate == null || "".equals(strInputDate)) {
			return 0;
		}
		strDate = strDate.replace('-', '/');
		strDate = strDate.replace('.', '/');
		strDate = strDate.replace(' ', '/');
		strDate = strDate.replace('_', '/');
		strDate = strDate.replace(':', '/');
		i = strDate.indexOf("/");

		// System.err.println("String2Milisecond.strDate:" + strDate);
		if (i < 0) {
			return 0;
		}
		try {
			// Get Nam
			String[] arrDate = strDate.split("/");
			nYear = (new Integer(arrDate[0].trim())).intValue();
			nMonth = (new Integer(arrDate[1].trim())).intValue() - 1;
			nDay = (new Integer(arrDate[2].trim())).intValue();
			nHour = (new Integer(arrDate[3].trim())).intValue();
			nMinute = (new Integer(arrDate[4].trim())).intValue();
			nSecond = (new Integer(arrDate[5].trim())).intValue();

			// System.err.println("nYear: " + nYear + "@"+ nMonth + "@" +
			// nDay+"@"+ nHour + "@" + nMinute + "@" + nSecond);
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.set(nYear, nMonth, nDay, nHour, nMinute, nSecond);

			return calendar.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private String mtReturn(String userid, String serviceid, String keyword,
			String operator, BigDecimal requestid) throws Exception {

		String sKeyword = keyword;
		sKeyword = sKeyword.toUpperCase();
		String sServiceid = serviceid;
		String sUserid = userid;

		int iPhien = 1;
		int isNewSession = -1;
		int isMO1 = -1;
		int isTheFirstMO = -1;
		String sMTReturn = "";
		String sLastKeyword = keyword;

		String[] kq1 = checkSession(GAMEID);

		String sSanpham = " ";
		String sEndDate = "";
		sSanpham = kq1[2];

		if ("xxx".equalsIgnoreCase(sSanpham)) {
			sMTReturn = "Phien choi ki nay chua bat dau, moi ban quay tro lai sau."
					+ "\n*Tuong thuat TT KQXS: XS<matinh> gui 8551"
					+ "\n*Truy tim kho bau: GE gui 8551"
					+ "\n*Suu tam OMNIA: TE gui 8551";
			return sMTReturn;
		}
		// String[] kq1 = checkSession("SIEUTOCDO");
		iPhien = Integer.parseInt(kq1[0]);
		isNewSession = Integer.parseInt(kq1[1]);

		sEndDate = String2Date(kq1[3]);
		Timestamp current_timestamp = new Timestamp(getCurrentMiliSec());

		if (isNewSession == 1) {
			sMTReturn = "Ban la nguoi dau tien chiem giu san pham phien choi tuan nay.Soan tin: "
					+ sKeyword
					+ " gui "
					+ sServiceid
					+ " de tiep tuc chiem giu. Ket thuc phien 24h ngay "
					+ sEndDate + "";
			insertData(sKeyword, sServiceid, sUserid, current_timestamp,
					current_timestamp, 0, iPhien, operator);
		} else {
			// Check xem co phai MO dau tien hay khong
			// lay ra thong tin ve lan update cuoi cung,lay ra thong tin ve MO
			// truoc do
			// Check xem da ket thuc ngay hay chua: neu ket thuc ngay thi insert
			// mot dong moi, neu chua thi update
			String[] kq2 = getInfo(sKeyword, sServiceid, sUserid, iPhien,
					isNewSession);
			isTheFirstMO = Integer.parseInt(kq2[0]);
			isMO1 = Integer.parseInt(kq2[1]);
			// System.out.println("KQ2[2]:" + kq2[2]);
			long last_totaltime = Long.parseLong(kq2[2]);
			long current_totaltime = Long.parseLong(kq2[3]);
			long lasttime = String2MilisecondNew(kq2[6]);
			int iID = Integer.parseInt(kq2[4]);
			int iLastID = Integer.parseInt(kq2[8]);
			sLastKeyword = kq2[9];
			String sLastUser = kq2[5];
			String sOperator = kq2[7];

			String sHiddenLastUser = "0" + sLastUser.substring(2, 4) + "***"
					+ sLastUser.substring(7);
			String sHiddenUser = "0" + sUserid.substring(2, 4) + "***"
					+ sUserid.substring(7);
			String sMTInform = "Doi thu " + sHiddenUser + " da doat "
					+ sSanpham + " cua ban, ban da giu " + last_totaltime
					+ " giay. Soan: " + sLastKeyword + " gui " + sServiceid
					+ ". Ket thuc phien 24h " + sEndDate.substring(0, 5) + "\n"
					+ INFOCS;

			if (!sLastUser.equalsIgnoreCase(userid)) {
				sendGifMsg(serviceid, sLastUser, sOperator, keyword, sMTInform,
						requestid);
			}
			// alertWinner(sServiceid, sLastUser, sOperator, service,
			// sMTInform);
			if (isTheFirstMO == 1) {
				sMTReturn = "Chao mung ban tham gia Sieu Toc.Ban la nguoi dau tien chiem giu san pham. Soan:"
						+ sKeyword
						+ " gui "
						+ sServiceid
						+ " de giu san pham. Ket thuc phien 24h "
						+ sEndDate.substring(0, 5);
				insertData(sKeyword, sServiceid, sUserid, current_timestamp,
						current_timestamp, 0, iPhien, operator);
			} else {
				if (isMO1 == 1) {
					sMTReturn = "Chao mung ban tham gia Sieu Toc. Ban da doat "
							+ sSanpham + " tu doi thu " + sHiddenLastUser
							+ ". Ket thuc phien 24h "
							+ sEndDate.substring(0, 5) + "\n" + INFOCS;
					insertData(sKeyword, sServiceid, sUserid,
							current_timestamp, current_timestamp, 0, iPhien,
							operator);
					updateData(last_totaltime, iLastID);
				} else {
					if (!sLastUser.equalsIgnoreCase(userid)) {
						sMTReturn = "Ban da doat lai " + sSanpham
								+ ". Ban giu " + current_totaltime
								+ " giay. Soan tin " + sKeyword + " gui "
								+ sServiceid
								+ " de giu san pham. Ket thuc phien 24h "
								+ sEndDate.substring(0, 5) + "\n" + SUPPORT;
						updateData(last_totaltime, iLastID);
						updateData(current_timestamp, current_totaltime, iID);
					} else {
						sMTReturn = "Ban dang giu " + sSanpham + ". Ban giu "
								+ (last_totaltime) + " giay. Soan tin "
								+ sKeyword + " gui " + sServiceid
								+ " de giu san pham. Ket thuc phien 24h "
								+ sEndDate.substring(0, 5) + "\n" + INFOCS;
						// if (checkNewDay(lasttime)) {
						// insertData(sKeyword, sServiceid, sUserid,
						// current_timestamp, current_timestamp,
						// current_totaltime, iPhien, operator);
						// } else {
						updateData(current_timestamp, last_totaltime, iID);
						// }
					}

				}
			}

		}
		return sMTReturn;
	}

	public String[] checkSession(String gameid) {
		// int iConn = -3;
		String[] kq = new String[20];
		String newSession = "-1";
		String endtime = "xxx";
		// String begintime = "xxx";
		String sp = "xxx";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			String query1 = "select phien from icom_phien where game_id='"
					+ gameid + "'";
			// Vector result = queryStatement(_env.getModule(), query1, iConn);

			connection = dbpool.getConnectionGateway();
			Vector result = DBUtils.getVectorTable(connection, query1);
			String phienTemp = "";
			for (int i = 0; i < result.size(); i++) {

				Vector item = (Vector) result.elementAt(i);
				phienTemp = (String) item.elementAt(0);

				// String[] record = (String[]) result.get(i);
				// phienTemp = record[0];
				// kq[0]=record[0];//phien
			}
			// int phien = Integer.parseInt(kq[0]);
			int phien = Integer.parseInt(phienTemp);
			Util.logger.sysLog(2, this.getClass().getName(), "Sieutocdo:"
					+ phien);
			String query2 = "select * from icom_dmphienstd where current_timestamp >"
					+ " endtime and phien=" + phien;
			Vector result2 = DBUtils.getVectorTable(connection, query2);
			// Util.logger.sysLog(2, this.getClass().getName(), "Sieutocdo:"
			// + result2.size());
			if (result2.size() > 0) {
				newSession = "1";// Phien moi
				phien = phien + 1;
				phienTemp = phien + "";
				String sqlUpdate = "update icom_phien set phien=" + phien
						+ " where game_id='" + gameid + "'";
				if (DBUtils.executeSQL(connection, sqlUpdate) < 0) {
					Util.logger.sysLog(2, this.getClass().getName(),
							": Update icom_phien Failed");
				}
			}

			String query3 = "select sanpham,endtime from icom_dmphienstd where current_timestamp >= begintime and phien="
					+ phien;
			Vector result3 = DBUtils.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				// for (int i = 0; i < result3.size(); i++) {
				Vector item = (Vector) result3.elementAt(0);
				sp = (String) item.elementAt(0);
				endtime = (String) item.elementAt(1);
				// begintime= (String) item.elementAt(2);

				// String[] record = (String[]) result3.get(i);
				// sp = record[0]; // san pham
				// endtime = record[1];// thoi gian ket thuc

				// }
			}

			String query = "select count(*) from icom_stduser where session_id="
					+ phien;
			// System.out.println("AAA:" + query);
			Vector result4 = DBUtils.getVectorTable(connection, query);

			if (result4.size() > 0) {// La MO dau tien cua chuong trinh

				Vector item = (Vector) result4.elementAt(0);
				String tempret = (String) item.elementAt(0);
				if ("0".equalsIgnoreCase(tempret)) {
					newSession = "1";// Phien moi
				}

			}

			Util.logger.sysLog(2, this.getClass().getName(),
					"SieuTocDo:sanpham" + sp + " " + endtime);

			kq[0] = phienTemp;
			kq[1] = newSession;
			kq[2] = sp;
			kq[3] = endtime;
			return kq;
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"Sieu toc do: Get icom_phienstd Failed" + ex.toString());
			ex.printStackTrace();
			return null;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	private String String2Date(String str) {
		String tmp = "";

		str = str.replace(':', '/');
		str = str.replace('-', '/');

		tmp = str.substring(0, 10);
		String[] sToken = tmp.split("/");
		tmp = sToken[2] + "/" + sToken[1] + "/" + sToken[0];
		return tmp;
	}

	private int insertData(String keyword, String serviceid, String userid,
			Timestamp lasttime, Timestamp date, long totaltime, int sessionid,
			String operator) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into icom_stduser (command_code,service_id,user_id,lasttime,date,totaltime,session_id,operator,milisec) values ('"
				+ keyword
				+ "','"
				+ serviceid
				+ "','"
				+ userid
				+ "','"
				+ lasttime
				+ "','"
				+ date
				+ "',"
				+ totaltime
				+ ","
				+ sessionid
				+ ",'" + operator + "'," + lmilisec + ")";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtils.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.sysLog(2, this.getClass().getName(),
						": uppdate Statement: Insert  icom_stduser Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					":Insert  icom_stduser Failed");
			ireturn = -1;
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private String[] getInfo(String keyword, String serviceid, String userid,
			int sessionid, int isFirstMO) {

		// int iConn = -3;
		// long totaltime = 0;
		String TheFirstMO = "-1";
		String MO1 = "-1";
		String current_totaltime_temp = "0";
		String last_totaltime_temp = "0";
		String current_lasttime_temp = "0";
		String last_lasttime_temp = "0";
		String lastUser = "";
		String current_id_temp = "0";
		String last_id_temp = "0";
		String[] getKQ = new String[11];
		Connection connection = null;
		String operator_lastuser = "";
		DBPool dbpool = new DBPool();
		String tempMilisec = "0";
		String lastKeyword = "";
		try {
			connection = dbpool.getConnectionGateway();
			// Check xem co phai la MO dau tien cau dich vu hay khong hay khong
			if (isFirstMO != 1) {// La MO dau tien cua chuong trinh
				// Kiem tra co phai la MO dau tien cua KH khong?
				String query1 = "select id,totaltime,lasttime from icom_stduser where user_id='"
						+ userid + "' and session_id=" + sessionid;
				Vector result1 = DBUtils.getVectorTable(connection, query1);
				if (result1.size() == 0) {// Neu la MO dau tien cua KH
					// Tim khach hang chiem giu san pham truoc do
					MO1 = "1";
				} else {
					Vector item = (Vector) result1.elementAt(0);
					current_id_temp = (String) item.elementAt(0);
					current_totaltime_temp = (String) item.elementAt(1);
					current_lasttime_temp = (String) item.elementAt(2);

				}

				String query3 = "select max(milisec) from icom_stduser where session_id="
						+ sessionid;

				Vector result3 = DBUtils.getVectorTable(connection, query3);
				if (result3.size() > 0) {
					Vector item = (Vector) result3.elementAt(0);
					tempMilisec = (String) item.elementAt(0);

				}
				String query2 = "select user_id,operator,lasttime,totaltime,id,command_code from icom_stduser where session_id="
						+ sessionid
						+ " and milisec = "
						+ tempMilisec
						+ " order by id desc";
				// System.out.println("Q2:" + query2);
				Vector result2 = DBUtils.getVectorTable(connection, query2);
				if (result2.size() > 0) {
					// for (int i = 0; i < result2.size(); i++) {

					Vector item = (Vector) result2.elementAt(0);
					lastUser = (String) item.elementAt(0);
					operator_lastuser = (String) item.elementAt(1);
					last_lasttime_temp = (String) item.elementAt(2);

					last_totaltime_temp = (String) item.elementAt(3);
					last_id_temp = (String) item.elementAt(4);
					lastKeyword = (String) item.elementAt(5);
				}

				long milliSecond = getCurrentMiliSec();// System.currentTimeMillis();

				long lLasttime = String2MilisecondNew(last_lasttime_temp);

				last_totaltime_temp = Long.parseLong(last_totaltime_temp)
						+ (milliSecond - lLasttime) / 1000 + "";

				lLasttime = String2MilisecondNew(current_lasttime_temp);
			} else {
				TheFirstMO = "1";// La MO dau tien cua chuong trinh
			}

			getKQ[0] = TheFirstMO;
			getKQ[1] = MO1;

			getKQ[2] = last_totaltime_temp;
			getKQ[3] = current_totaltime_temp;
			getKQ[4] = current_id_temp;
			getKQ[5] = lastUser;
			getKQ[6] = last_lasttime_temp;
			getKQ[7] = operator_lastuser;
			getKQ[8] = last_id_temp;
			getKQ[9] = lastKeyword;
			return getKQ;

		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"sieutoc:Get vmg_vnnlinks_stduser Failed" + ex.toString());
			return null;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public static long String2Milisecond1(String strInputDate) {
		System.err.println("String2Milisecond.strInputDate:" + strInputDate);
		String strDate = strInputDate.trim();
		int i, nYear, nMonth, nDay, nHour, nMinute, nSecond;
		String strSub = null;
		if (strInputDate == null || "".equals(strInputDate)) {
			return 0;
		}
		strDate = strDate.replace('-', '/');
		strDate = strDate.replace('.', '/');
		strDate = strDate.replace(' ', '/');
		strDate = strDate.replace('_', '/');
		strDate = strDate.replace(':', '/');
		i = strDate.indexOf("/");
		// System.out.println("i1: " + i);
		// System.err.println("String2Milisecond.strDate:" + strDate);
		if (i < 0) {
			return 0;
		}
		try {
			// Get Nam
			strSub = strDate.substring(0, i);
			if (strDate.length() < 4) {
				if (strDate.substring(0, 1).equals("9")) {
					strDate = "19" + strDate.trim();
				} else {
					strDate = "20" + strDate.trim();
				}
			}
			nYear = (new Integer(strSub.trim())).intValue();

			// Get Thang
			strDate = strDate.substring(i + 1);
			i = strDate.indexOf("/");
			if (i < 0) {
				return 0;
			}
			strSub = strDate.substring(0, i);
			nMonth = (new Integer(strSub.trim())).intValue() - 1; // Month:nMonth
			// = thu tu
			// thang - 1
			//System.out.println("nMonth: " + nMonth);
			// Get ngay
			strDate = strDate.substring(i + 1);

			i = strDate.indexOf("/");
			if (i < 0) {
				return 0;
			}
			strSub = strDate.substring(0, i);
			nDay = (new Integer(strSub.trim())).intValue();

			// Get gio
			strDate = strDate.substring(i + 1);
			i = strDate.indexOf("/");
			strSub = strDate.substring(0, i);
			nHour = (new Integer(strSub.trim())).intValue();
			// GetMinute
			strDate = strDate.substring(i + 1);
			i = strDate.indexOf("/");
			strSub = strDate.substring(0, i);
			nMinute = (new Integer(strSub.trim())).intValue();
			// Get Second
			strSub = strDate.substring(i + 1);

			// System.out.println("strDate: " + strDate);

			nSecond = (new Integer(strSub.trim())).intValue();

			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.set(nYear, nMonth, nDay, nHour, nMinute, nSecond);
			return calendar.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static boolean checkNewDay(long milliSecond) {
		long current_milliSecond = getCurrentMiliSec();// System.currentTimeMillis();
		java.util.Calendar calendar1 = java.util.Calendar.getInstance();
		java.util.Calendar calendar2 = java.util.Calendar.getInstance();
		calendar1.setTime(new java.util.Date(current_milliSecond));
		calendar2.setTime(new java.util.Date(milliSecond));
		// System.out.println("day of month11:" +
		// calendar1.get(calendar1.DAY_OF_MONTH));
		// System.out.println("day of month12:" +
		// calendar2.get(calendar2.DAY_OF_MONTH));

		if (calendar1.get(calendar1.DAY_OF_MONTH) != calendar2
				.get(calendar2.DAY_OF_MONTH)) {
			return true;
		} else {
			return false;
		}
	}

	private int updateData(Timestamp current_timestamp, long totaltime, int id) {
		int ireturn = 1;
		long lmilisec = System.currentTimeMillis();

		String sqlUpdate = "update icom_stduser set lasttime='"
				+ current_timestamp + "',totaltime=" + totaltime + ",milisec="
				+ lmilisec + " where id =" + id;

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtils.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.sysLog(2, this.getClass().getName(),
						": uppdate Statement: Update icom_stduser Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					":Update  icom_stduser Failed");
			ireturn = -1;
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private int updateData(long totaltime, int id) {
		int ireturn = 1;
		String sqlUpdate = "update icom_stduser set totaltime=" + totaltime
				+ " where id =" + id;

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtils.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.sysLog(2, this.getClass().getName(),
						": uppdate Statement: Update icom_stduser Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					":Update  icom_stduser Failed");
			ireturn = -1;
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

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

			DBUtils.sendMT(msg);

		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}

	public static void main(String[] args) {

		long milliSecond = getCurrentMiliSec();// System.currentTimeMillis();
		String last_lasttime_temp = "2008-12-16 13:00:00";

		long lLasttime = String2MilisecondNew(last_lasttime_temp);

		System.out.println("Mili:" + milliSecond);
		System.out.println("Mili1:" + System.currentTimeMillis());
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Mili2:" + System.currentTimeMillis());
		System.out.println("Mili3:" + System.currentTimeMillis());
		System.out.println("Mili:" + lLasttime);

		System.out.println("last_totaltime_temp:" + last_lasttime_temp);
		System.out.println("lLasttime:" + lLasttime);
		String date = new SimpleDateFormat("dd/MM/yyyy hh24:mm:ss.SSS")
				.format(new Date());
		System.out.println("date1:"
				+ new SimpleDateFormat("dd/MM/yyyy hh24:mm:ss.SSS")
						.format(new Date()));
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("date2:"
				+ new SimpleDateFormat("dd/MM/yyyy hh24:mm:ss.SSS")
						.format(new Date()));
		System.out.println("date3:"
				+ new SimpleDateFormat("dd/MM/yyyy hh24:mm:ss.SSS")
						.format(new Date()));
		// System.out.println("current_lasttime_temp:" + current_lasttime_temp);
		// System.out.println("chor:" +
		// DateProc.Timestamp2HHMMSS(DateProc.createTimestamp(),0));
		// last_totaltime_temp = Long.parseLong(last_totaltime_temp)
		// + (milliSecond - lLasttime) / 1000 + "";

	}

	public static long getCurrentMiliSec() {
		return System.currentTimeMillis();
		// return
		// String2MilisecondNew(DateProc.getDateTimeStringYYYYMMDDHHMISS(DateProc.createTimestamp()));
	}
}