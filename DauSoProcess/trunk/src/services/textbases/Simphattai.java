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
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.ResultSet;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class Simphattai extends ContentAbstract {
	String GAMEID = "SIMPHATTAI";
	String INFOCS = "Soan: AC gui 8551 de so huu E71 MIEN PHI";
	String SUPPORT = "DTHT:0435561862";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {

			Collection messages = new ArrayList();
			MsgObject mt = msgObject;

			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();

			String sMTReturn = mtReturn(sUserid, sServiceid, sKeyword);
			mt.setUsertext(sMTReturn);
			mt.setMsgtype(1);
			messages.add(new MsgObject(mt));
			return messages;
		} catch (Exception e) {
			// TODO: handle exception
			return null;

		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
	}

	int getRANDOM(int iBalance) {
		Random iRandom = new Random();
		int i = iRandom.nextInt(99);
		boolean nextloop = true;
		while (nextloop) {
			i = iRandom.nextInt(99);
			if (i > 0) {
				nextloop = false;
			}
		}
		if (i < 6) {
			return 11;
		} else
			return i;
	}

	public static int executeSQL(String sql) {

		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		Connection obj = dbpool.getConnectionGateway();
		try {

			statement = obj.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {

			return -1;
		} catch (Exception e) {
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(obj);

		}
	}

	private String mtReturn(String userid, String serviceid, String keyword)
			throws Exception {
		String mtReturn = "";

		// Bien de luu so thu tu tin nhan cua KH
		long lSequence = getSequence(GAMEID, serviceid);
		Random iRandom = new Random();
		// Lay ngau nhien 1 trong 4 MT tra ve khi KH khong trung thuong
		int i = iRandom.nextInt(3);

		String[] seriYY1 = new String[1];
		String seriYY = "";
		String XX = "";
		int SimLoc = isSimLoc(lSequence, this.getLastSubString(userid, 6),
				seriYY1);
		seriYY = seriYY + seriYY1[0];
		switch (i) {
		case 0:
			mtReturn = "Ban dang tham gia SIM PHAT TAI, so may man cua ban: "
					+ XX + seriYY
					+ "\nSo sanh voi so dt cua ban de danh giai thuong."
					+ "\n*Soan: " + keyword + " gui " + serviceid
					+ " de tham gia va trung thuong";
			break;
		case 1:
			mtReturn = "So may man cua ban: " + XX + seriYY + "\n";
			mtReturn += "Hay so sanh voi so dt cua ban de t/thuong SIM PHAT TAI"
					+ "\n";
			mtReturn += "Hay tiep tuc soan:" + keyword + " gui " + serviceid
					+ "\n*" + SUPPORT;
			break;
		case 2:
			mtReturn = "So PHAT TAI cua ban: " + XX + seriYY + "\n";
			mtReturn += "*Trung: 2 so=100 ngan;3 so cuoi=1 tr;\n 4 so cuoi=2 tr; 5 so cuoi=5 tr; 6 so cuoi= 10tr."
					+ "\n";
			mtReturn += "*Soan tin: " + keyword + " gui " + serviceid;
			break;
		}

		return mtReturn;
	}

	private String GetRandomInteger(int aStart, int aEnd, Random aRandom) {
		int randomNumber = -1;
		try {
			if (aStart > aEnd) {
				throw new IllegalArgumentException(
						"SimLoc.GetRandomInteger error: Start cannot exceed End.");
			}
			// get the range, casting to long to avoid overflow problems
			long range = (long) aEnd - (long) aStart + 1;
			// compute a fraction of the range, 0 <= frac < range
			long fraction = (long) (range * aRandom.nextDouble());
			randomNumber = (int) (fraction + aStart);
			// return randomNumber;
			// log("Generated : " + randomNumber);
		} catch (Exception ex) {
			randomNumber = -1;
			Util.logger.sysLog(LogValues.INFORMATIVE,
					this.getClass().getName(),
					"SIMLOC.GetRandomInteger Failed " + ex.getMessage());
		} finally {

		}
		return Integer.toString(randomNumber);
	}

	private String getRandomNumberByLength(String strUserPhoneNumber,
			int lenOfNumber) {
		int chk = 0;
		int START;
		int END;
		String SimLocNumber = "";
		String strTmp = "";
		String strRdn = "";
		try {
			/*
			 * Bat dau lay mot so random 6 chu so sao cho ko trung voi 6 chu so
			 * cuoi cua so dt cua KH
			 */

			Random random = new Random();
			boolean conti = true;
			START = 0;
			END = 9;
			SimLocNumber = "";
			String SimLocNumberTmp = "";
			String strUserPhoneNumberAtI = "";
			// Util.sysLog(LogValues.INFORMATIVE, this.getClass().getName(),
			// "getRandomNuber.strUserPhoneNumber.length: " +
			// strUserPhoneNumber.length());
			for (int i = 0; i <= lenOfNumber - 1; i++) {
				conti = true;
				SimLocNumberTmp = "";
				strUserPhoneNumberAtI = String.valueOf(strUserPhoneNumber
						.charAt(i));
				while (conti) {
					SimLocNumberTmp = GetRandomInteger(START, END, random);
					if (!SimLocNumberTmp.equals(strUserPhoneNumberAtI)) {
						conti = false;
					}
				}
				SimLocNumber += SimLocNumberTmp;
				// Util.sysLog(LogValues.INFORMATIVE, this.getClass().getName(),
				// "getRandomNuber.i: " + Integer.toString(i));
				// Util.sysLog(LogValues.INFORMATIVE, this.getClass().getName(),
				// "getRandomNuber.SimLocNumberTmp: " + SimLocNumberTmp);
			}
		} catch (Exception ex) {
			SimLocNumber = "";
			Util.logger.sysLog(LogValues.INFORMATIVE,
					this.getClass().getName(), "getRandomNuber failed "
							+ ex.getMessage());
			ex.printStackTrace();
		} finally {
			// Util.sysLog(LogValues.INFORMATIVE, this.getClass().getName(),
			// "getRandomNuber.SimLocNumber: " + SimLocNumber);

		}
		return SimLocNumber;
	}

	private int isSimLoc(long SequenceNumber, String strUserPhoneNumber,
			String[] strSimLocReturn) {
		int chk = 0;
		int START;
		int END;
		String SimLocNumber = "";
		String strTmp = "";
		String strRdn = "";
		try {
			/*
			 * Bat dau lay mot so random 6 chu so sao cho ko trung voi 6 chu so
			 * cuoi cua so dt cua KH
			 */
			SimLocNumber = this.getRandomNumberByLength(strUserPhoneNumber,
					strUserPhoneNumber.length());

			chk = 0;
			strSimLocReturn[0] = SimLocNumber;

		} catch (Exception ex) {
			chk = 0;
			Util.logger.sysLog(LogValues.INFORMATIVE,
					this.getClass().getName(), "SIMLOC.isSimLoc Failed "
							+ ex.getMessage());
		} finally {
			// Util.sysLog(LogValues.INFORMATIVE, this.getClass().getName(),
			// "SIMLOC.strSimLocReturn: " + SimLocNumber);

		}
		return chk;
	}

	private String getLastSubString(String strUserPhoneNumber, int SplitNumber) {
		int len = 0;
		String strReturn = "";
		try {
			len = strUserPhoneNumber.length();

			strReturn = strUserPhoneNumber.substring(len - SplitNumber);
		} catch (Exception ex) {
			strReturn = "";
			Util.logger.sysLog(LogValues.INFORMATIVE,
					this.getClass().getName(),
					"SIMLOC.getLastSubString Failed " + ex.getMessage());
		} finally {

		}
		return strReturn;

	}

	private long getSequence(String gameid, String serviceid) {
		long sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select sequence from icom_sequence where gameid='"
				+ gameid.toUpperCase() + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			String sequence_temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}

			sequence = Long.parseLong(sequence_temp);
			if (sequence > 0) {
				String sqlUpdate = "update icom_sequence set sequence = sequence + 1 where gameid='"
						+ gameid.toUpperCase() + "'";
				statement = cnn.prepareStatement(sqlUpdate);
				if (statement.executeUpdate() != 1) {
					Util.logger
							.error("GetSequence: Update icom_sequence Failed");
				}
			}

		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return sequence;
	}

}