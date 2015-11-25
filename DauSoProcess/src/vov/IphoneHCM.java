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
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.ResultSet;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class IphoneHCM extends ContentAbstract {
	String SUPPORT = "1900571566";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {

			Collection messages = new ArrayList();
			MsgObject mt = msgObject;

			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();
			int kq = getPhien();
			String sBeginDate = "";
			String sEndDate = "";
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);
			String stime = "7h thu 3";
			stime = getString(_option, "stime", stime);
			String tensp = getString(_option, "tensp", "NOKIAN9");
			String iphone = getString(_option, "iphone", "NOKIA");
			String phone = getString(_option, "phone", "NOKI");

			if ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator()
					.toUpperCase())) {
				msgObject
						.setUsertext("Hien tai dich vu chua ho tro mang cua ban.Vui long quay tro lai sau.DTHT "
								+ SUPPORT);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			if (kq == -1) {
				msgObject
						.setUsertext("Chuong trinh chua bat dau.Hay quay lai vao chuong trinh lan sau vao "
								+ stime
								+ " hang tuan tren VOV giao thong.DTHT:"
								+ SUPPORT);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			String sMTReturn = mtReturn(sUserid, kq, sServiceid, sKeyword,
					tensp, iphone, phone);
			Util.logger.sysLog(2, this.getClass().getName(), "mt return: "
					+ sMTReturn);

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

	private String mtReturn(String userid, int session, String serviceid,
			String keyword, String tensp, String iphone, String phone)
			throws Exception {
		String mtReturn = "";
		String mtWinner = "";
		int lSequence = 0;
		boolean isWinner = false;
		lSequence = getSequence(session) + 1;

		Util.logger.info("sequence:" + lSequence);
		long lsubsequence = (lSequence % 10);
		String result = "";
		String sresult = getResult(userid, session);
		Util.logger.info("sresult:" + sresult);
		String iiphone = getResult(session);
		
		String[] siphone1 = iiphone.split(",");
		if (siphone1.length==5)
		{
			iiphone=iiphone+","+siphone1[3];
		}
		String[] siphone = iiphone.split(",");
		
	
		
		if ((lSequence % 1000000) == 0) {
			// IPHONE
			mtReturn = "Xin  chuc mung ban.Ban da may man nhan duoc chu \""
					+ iphone + "\".Chiec " + tensp
					+ " da thuoc ve ban.Vui long lien he tong dai " + SUPPORT
					+ " de lam thu tuc nhan thuong";
			result = iphone;
		} else if ((lSequence % 100000) == 0) {
			// I
			result = siphone[0];
			if (sresult.equalsIgnoreCase("")) {
				saveUser(lSequence, userid, result, session);

			} else

			{
				// update
				sresult = sresult + "," + result;
				updateResult(userid, sresult, session, lSequence);
			}
			int times = countresult(sresult, siphone[0]);

			if (times == 1) {
				mtReturn = "Con cho gi nua!Hay tiep tuc cuoc choi vi ban da co chu \""
						+ siphone[0]
						+ "\".Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ ".Co hoi so huu chiec "
						+ tensp
						+ " dang rat gan.DTHT:" + SUPPORT;
			} else if (times == 2) {
				mtReturn = "May man dang den rat gan voi ban!Ban da so huu ky tu \""
						+ siphone[0]
						+ "\".Nhanh soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " suu tap them cac ky tu con thieu.DTHT:"
						+ SUPPORT;
			} else {
				mtReturn = "Chuc mung ban.Ban da so huu ky tu \"" + siphone[0]
						+ "\"may man.Co hoi so huu " + tensp
						+ " dang den rat gan.Nhanh tay soan tin:" + keyword
						+ " gui " + serviceid
						+ " de suu tap them cac ky tu con thieu.";

			}
		} else if ((lSequence % 500) == 0) {
			// 100k

			mtReturn = "Ban that may man vi ban da nhan duoc 100k tu chuong trinh o chu may man. Tiep tuc nhan tin de suu tap cac chu cai con lai va tro thanh chu nhan cua "+tensp+" DTHT:" + SUPPORT;
			result="100";
			if (sresult.equalsIgnoreCase("")) {
				saveUser(lSequence, userid, result, session);

			} else

			{
				// update
				sresult = sresult + "," + result;
				updateResult(userid, sresult, session, lSequence);
			}
		} else if ((lSequence % 1700) == 0) {
			// E
			result = siphone[1];
			if (sresult.equalsIgnoreCase("")) {
				saveUser(lSequence, userid, result, session);

			} else

			{
				// update
				sresult = sresult + "," + result;
				updateResult(userid, sresult, session, lSequence);
			}
			int times = countresult(sresult, siphone[1]);

			if (times == 1) {
				mtReturn = "Co hoi so huu " + tensp
						+ " dang den rat gan.Ban nhan duoc chu \"" + siphone[1]
						+ "\" may man.Soan:" + keyword + " gui " + serviceid
						+ " de nhanh tay suu tap cac chu con thieu.DTHT:"
						+ SUPPORT;
			} else if (times == 2) {
				mtReturn = "Chuc mung ban.Ban nhan duoc chu \"" + siphone[1]
						+ "\" may man.Co hoi so huu chiec " + tensp
						+ " dang o rat gan. Hay nhanh tay soan tin:" + keyword
						+ " gui " + serviceid
						+ " suu tap them cac ky tu con thieu.DTHT:" + SUPPORT;
			} else {
				mtReturn = "That may man! Ban da so huu ky tu \"" + siphone[1]
						+ "\" Nhanh soan tin:" + keyword + " gui " + serviceid
						+ " de suu tap them cac ky tu con thieu.DTHT:"
						+ SUPPORT;

			}
		} else if ((lsubsequence == 1) || (lsubsequence == 5)
				|| (lsubsequence == 9)) {
			// N
			result = siphone[2];
			if (sresult.equalsIgnoreCase("")) {
				saveUser(lSequence, userid, result, session);

			} else

			{
				// update
				sresult = sresult + "," + result;
				updateResult(userid, sresult, session, lSequence);
			}
			int times = countresult(sresult, siphone[2]);
			if (times == 1) {
				mtReturn = "Chu \""
						+ siphone[2]
						+ "\" thuoc ve ban.Soan: "
						+ keyword
						+ " gui "
						+ serviceid
						+ " de tim them cho minh nhung chu con thieu trong bo chu "
						+ iphone + ".Chuc ban may man.DTHT:" + SUPPORT;
			} else if (times == 2) {
				mtReturn = "Ban co 1 chu \""
						+ siphone[2]
						+ "\".May man se den voi ban trong tin nhan tiep theo.  Tiep tuc soan tin:"
						+ keyword + " gui " + serviceid
						+ " de su tap cac chu ban con thieu.DTHT:" + SUPPORT;
			} else {
				mtReturn = "Ban nhan duoc chu \"" + siphone[2] + "\".Soan:"
						+ keyword + " gui " + serviceid
						+ " de suu tap cac chu khac.Co du bo chu \"" + phone
						+ "\" trung ngay 500,000d.So huu ngay " + tensp
						+ " khi ban co du bo chu \"" + iphone + "\"";

			}
		} else if ((lsubsequence == 3) || (lsubsequence == 7)) {
			// H
			result = siphone[3];
			if (sresult.equalsIgnoreCase("")) {
				saveUser(lSequence, userid, result, session);

			} else

			{
				// update
				sresult = sresult + "," + result;
				updateResult(userid, sresult, session, lSequence);
			}
			int times = countresult(sresult, siphone[3]);
			if (times == 1) {
				mtReturn = "Chuong trinh gui den ban chu \""
						+ siphone[3]
						+ "\". Hay soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap cac chu con thieu.Chuc ban may man.DTHT "
						+ SUPPORT;
			} else {
				mtReturn = "Ban nhan duoc chu \""
						+ siphone[3]
						+ "\".Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap cac chu khac. Co du bo chu \""
						+ phone
						+ "\" trung ngay 500,000d.  So huu ngay "+tensp+"  khi ban co du bo chu \""
						+ iphone + "\"";
			}
		} else if ((lsubsequence == 0) || (lsubsequence == 4)
				|| (lsubsequence == 8)) {
			// P
			result = siphone[4];
			if (sresult.equalsIgnoreCase("")) {
				saveUser(lSequence, userid, result, session);

			} else

			{
				// update
				sresult = sresult + "," + result;
				updateResult(userid, sresult, session, lSequence);
			}
			int times = countresult(userid, siphone[4]);
			if (times == 1) {
				mtReturn = "Ban da so huu ky tu \"" + siphone[4]
						+ "\".Soan tin:" + keyword + " gui " + serviceid
						+ ".Suu tap du bo chu: " + iphone
						+ " de so huu ngay chiec " + tensp
						+ " sanh dieu.Dien thoai ho tro:" + SUPPORT;
			} else if (times == 2) {
				mtReturn = "Ban nhan duoc ky tu \""
						+ siphone[4]
						+ "\".Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap them cac ky tu con lai. Dien thoai ho tro:"
						+ SUPPORT;
			} else {
				mtReturn = "Ban co 1 ky tu \""
						+ siphone[4]
						+ "\".Kien nhan tham gia cuoc choi de suu tap du bo chu "
						+ iphone + " va danh cho minh chiec " + tensp
						+ " sanh dieu. DTHT" + SUPPORT;
			}
		} else if ((lsubsequence == 2) || (lsubsequence == 6)) {
			// O
			
			result = siphone[5];
			if (sresult.equalsIgnoreCase("")) {
				saveUser(lSequence, userid, result, session);

			} else

			{
				// update
				sresult = sresult + "," + result;
				updateResult(userid, sresult, session, lSequence);
			}
			int times = countresult(sresult, siphone[5]);

			if (times == 1) {
				mtReturn = "Ban nhan duoc chu \"" + siphone[5] + "\".Soan:"
						+ keyword + " gui " + serviceid
						+ " de suu tap cac chu khac.Co du bo chu \"" + phone
						+ "\" trung ngay 500,000d.So huu " + tensp
						+ " khi co du bo chu \"" + iphone + "\"";
			} else if (times == 2) {
				mtReturn = "Ky tu \"" + siphone[5]
						+ "\" da thuoc quyen so huu cua ban.Soan: " + keyword
						+ " gui " + serviceid + ".Suu tap du bo chu:" + iphone
						+ " de so huu chiec " + tensp + " sanh dieu.DTHT"
						+ SUPPORT;

			} else {
				mtReturn = "Ban co 1 ky tu \""
						+ siphone[5]
						+ "\".Tiep tuc nhan tin voi cu phap:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap chu cai ban con thieu.Chuc ban may man.DTHT:"
						+ SUPPORT;
			}

		}

		// Check user
		/*
		 * Neu chua co thi insert, neu co roi thi update
		 */

		return mtReturn;
	}

	private static String getResult(String userid, int session) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT result FROM icom_iphonehcm WHERE user_id= '"
				+ userid.toUpperCase() + "' and session= " + session;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			String sequence_temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("getResult. Ex:" + ex2.toString());
		} catch (Exception ex3) {
			Util.logger.error("getResult. Ex3:" + ex3.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
		return "";
	}

	private static boolean updateResult(String userid, String result,
			int session, int sequence) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update cac danh sach bai hat da gui cho khach hang
			String sqlUpdate = "UPDATE icom_iphonehcm SET result = '" + result
					+ "' , sequence=" + sequence + " WHERE upper(user_id)='"
					+ userid.toUpperCase() + "' and session=" + session;
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update result icom_phone " + userid
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

	private static int getPhien() {
		int result = -1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT session FROM iphone_sessionhcm  where isprocess=1 ";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
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

	private int getSequence(int session) {
		int sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select sequence from icom_iphonehcm where session="
				+ session + " order by sequence desc limit 1";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			String sequence_temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence = rs.getInt(1);
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

	private String getResult(int session) {
		String sequence = "I,P,H,O,N,E";

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select result from iphone_sessionhcm where session="
				+ session;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			String sequence_temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence = rs.getString(1);
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

	private int countChar(String userid, String c, String session) {
		int sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select count(1) from icom_iphonehcm where user_id='"
				+ userid + "' and result='" + c + "' and session='" + session
				+ "'";

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

			sequence = Integer.parseInt(sequence_temp);

		} catch (SQLException ex2) {
			Util.logger.error("getGameid. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("GetGameid. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return sequence;
	}

	private int countresult(String result, String c) {
		String[] split = result.split(",");
		int count = 0;
		for (int i = 0; i < split.length; i++) {
			if (split[i].equalsIgnoreCase(c.toUpperCase())) {
				count++;
			}
		}

		return count;
	}

	private int saveUser(int sequence, String user_id, String result,
			int session) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		// Util.logger.info("sendMT:" + msgObject.getUserid()+ "@TO" +
		// msgObject.getServiceid() + "@" + msgObject.getUsertext() );
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return -1;
			}
			String sqlInsert = "Insert into icom_iphonehcm (sequence,user_id,result,session) values ("
					+ sequence
					+ ",'"
					+ user_id
					+ "','"
					+ result
					+ "',"
					+ session + ")";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger
						.error(user_id + ": Insert in to icom_iphonehcm Failed");
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.error(user_id + ": Error:" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error(user_id + ": Error:" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

}