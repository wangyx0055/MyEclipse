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

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class Tracnghiem365 extends ContentAbstract {
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
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);
			String stime = "7h thu 3";
			stime = getString(_option, "stime", stime);

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
			// get result
			// if bang result thi tra tin dung, neu sai thi ra tin sai
			// 
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext()
					.toUpperCase());
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			String result=getResult(kq);
			if (sTokens.length > 1) {
				String subTokens = "";
				for (int k = 1; k < sTokens.length; k++) {
					subTokens = subTokens + sTokens[k];
				}
				if (result.equalsIgnoreCase(subTokens))
				{// insert exactly=1
					saveUser(sUserid, subTokens, 1,
							kq);
					msgObject.setUsertext("Ban da tra loi dung dap an.Hay theo doi chuong trinh va lang nghe MC de xem minh co la nguoi tra loi nhanh nhat khong nhe!Chuc ban may man.DTHT "+ SUPPORT);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				}else
				{// insert exactly=0
					saveUser(sUserid, subTokens, 0,
							kq);
					msgObject.setUsertext("Ban da tra loi sai dap an.Hay tiep tuc nhan tin de tro thanh nguoi chien thang.Chuc ban may man.DTHT "	+ SUPPORT);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				}
				
			} else

			{
				msgObject.setUsertext("Ban nhan tin sai. Soan "+msgObject.getKeyword()+" dapan gui "+msgObject.getServiceid()+" de tra loi cau hoi tu chuong trinh.Chuc ban may man.DTHT "	+ SUPPORT);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
			}
			return null;
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

	private static String getResult(int session) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT result FROM tracnghiem365phien WHERE session= "
				+ session;

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
			String sqlUpdate = "UPDATE icom_iphone SET result = '" + result
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
		String query = "SELECT session FROM tracnghiem365phien  where isprocess=1 ";

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


	private int countChar(String userid, String c, String session) {
		int sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select count(1) from icom_iphone where user_id='"
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

	private int saveUser( String user_id, String info,int exactly,
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
			String sqlInsert = "Insert into tracnghiem365custom (user_id,info,exactly,session) values ('"
					+ user_id
					+ "','"
					+ info
					+ "',"
					+ exactly
					+ ","
					+ session + ")";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger
						.error(user_id + ": Insert in to tracnghiem365custom Failed");
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