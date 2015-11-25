package services.textbases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
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
import common.DBUtils;

import cs.ExecuteADVCR;

public class Trieuphu extends ContentAbstract {
	String GAMEID = "OMINA";
	String INFOCS = "E71 mien phi,Soan: AC gui 8551 de so huu";
	String SUPPORT = "0435561862";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {

			Collection messages = new ArrayList();
			MsgObject mt = msgObject;
			String infoid = "";

			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();
			String sOption = keyword.getOptions();

			if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {
				infoid = "viettel";
			} else if (("VMS".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| "mobifone".equalsIgnoreCase(msgObject
							.getMobileoperator())) {
				infoid = "mobifone";
			} else if (("GPC".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| ("VINAPHONE".equalsIgnoreCase(msgObject
							.getMobileoperator()))) {
				infoid = "vinaphone";
			} else {
				infoid = "other";
			}

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject
						.setUsertext("Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.");
				msgObject.setMsgtype(1);
				messages.add(msgObject);
				return messages;
			}

			String sMTReturn = mtReturn(sUserid, sServiceid, sKeyword,
					msgObject.getUsertext(), sOption, msgObject);

			if ("".equalsIgnoreCase(sMTReturn)) {
				return null;
			}
			mt.setUsertext(sMTReturn);
			mt.setMsgtype(1);
			mt.setContenttype(0);
			messages.add(new MsgObject(mt));

			return messages;
		} catch (Exception e) {
			// TODO: handle exception

			Util.logger.error("trieuphu" + ": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return null;

		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
	}

	private String mtReturn(String userid, String serviceid, String keyword,
			String info, String option, MsgObject msgObj) throws Exception {
		String mtReturn = "";
		String mtWinner = "";
		long lSequence = 1;
		boolean isWinner = false;

		String sMO = replaceAllWhiteWithOne(info);
		String sToken[] = sMO.split(" ");

		Util.logger.info("sequence:" + lSequence);
		HashMap _option = new HashMap();

		_option = getParametersAsString(option);

		String URL_INV = "Tang ban:http://www.mobinet.com.vn/?g=8&c=wap5";
		String URL_SUCCESS1 = "Tang ban:http://www.mobinet.com.vn/?g=7&f=image&c=hot";
		String URL_SUCCESS2 = "Tang ban:http://www.mobinet.com.vn/?g=8&c=wap6";
		String URL_SUCCESS3 = "Tang ban:http://www.mobinet.com.vn/?g=7&f=image&c=hot";

		String SERVICE7 = getString(_option, "service7", "8751");
		String SERVICE1 = getString(_option, "service1", "8151");
		String SERVICE5 = getString(_option, "service1", "8551");

		String WAP_INV = getString(_option, "WAP_INV", URL_INV);
		String WAP_1 = getString(_option, "WAP_1", URL_SUCCESS1);
		String WAP_2 = getString(_option, "WAP_2", URL_SUCCESS2);
		String WAP_3 = getString(_option, "WAP_3", URL_SUCCESS3);
		String sKeyword = getString(_option, "keyword", "PT");

		String timediff = "0 00:15:00";
		if (SERVICE7.equalsIgnoreCase(serviceid)) {
			lSequence = getSequence(GAMEID, serviceid);
			int kq1[] = checkValidity(keyword, serviceid, userid, timediff,
					lSequence);
			int isValid = kq1[0];
			int questionID = kq1[1];
			if (isValid == 1) {
				String sQuestion = getQuestion(questionID);
				String mt1 = "Chao mung ban den voi ctrinh \"Nha trieu phu\". Sau khi nhan duoc cau hoi,ban chon dap an A,B,C,D de tra loi.VD ban chon dap an A.Soan tin: "
						+ sKeyword + " A gui " + SERVICE1;
				String mt2 = "Cau hoi cho ban la: " + sQuestion;
				String mt3 = "Neu ban muon su tro giup cua tong dai, ban hay soan tin "
						+ sKeyword
						+ " gui "
						+ SERVICE5
						+ " de tong dai bo di 2 phuong an sai.HT 19001745";
				msgObj.setMsgtype(0);
				msgObj.setUsertext(mt1);
				DBUtil.sendMT(msgObj);
				Thread.sleep(2000);

				msgObj.setMsgtype(0);
				msgObj.setUsertext(mt2);
				DBUtil.sendMT(msgObj);
				Thread.sleep(2000);

				return mt3;
			} else {
				return "Ban da vi pham luat choi cua Chuong trinh \"Nha trieu phu\". Ho tro 19001745";
			}

		} else if (SERVICE1.equalsIgnoreCase(serviceid)) {
			if (sToken.length == 1
					&& (sToken[0].substring(keyword.length()).equalsIgnoreCase(
							"A")
							|| sToken[0].substring(keyword.length())
									.equalsIgnoreCase("B")
							|| sToken[0].substring(keyword.length())
									.equalsIgnoreCase("C") || sToken[0]
							.substring(keyword.length()).equalsIgnoreCase("D"))
					|| sToken.length == 2
					&& (sToken[1].equalsIgnoreCase("A")
							|| sToken[1].equalsIgnoreCase("B")
							|| sToken[1].equalsIgnoreCase("C") || sToken[1]
							.equalsIgnoreCase("D"))) {

				String answer = "";
				if (sToken.length == 1
						&& (sToken[0].substring(keyword.length())
								.equalsIgnoreCase("A")
								|| sToken[0].substring(keyword.length())
										.equalsIgnoreCase("B")
								|| sToken[0].substring(keyword.length())
										.equalsIgnoreCase("C") || sToken[0]
								.substring(keyword.length()).equalsIgnoreCase(
										"D")))
					answer = sToken[0].substring(keyword.length());
				else
					answer = sToken[1];
				int isRight = checkAnswer(userid, timediff, answer);
				String mt = "";
				if (isRight == 1)
					mt = "Chuc mung ban da vuot qua cau hoi cua chuong trinh. Hay soan tin "
							+ keyword
							+ " CHON gui "
							+ SERVICE1
							+ " de lua chon phan thuong danh cho ban.HT 19001745";
				else if (isRight == -1) {
					mt = "Ban da nhan tin sai cu phap. Vui long nhan lai theo dung cu phap hoac lien he den tong dai 19001745 de biet them chi tiet";
				} else {
					mt = "Chuc ban may man lan sau.Chuong trinh gui tang ban 10 nhac chuong hot nhat+1game Java.May phai cai dat GPRS,soan GPRS gui 8751 de cai GPRS tu dong";

					msgObj.setMsgtype(0);
					msgObj.setUsertext(WAP_INV);
					msgObj.setContenttype(8);
					DBUtil.sendMT(msgObj);
				}
				return mt;

			} else if (sToken.length == 1
					&& sToken[0].substring(keyword.length()).equalsIgnoreCase(
							"CHON") || sToken.length == 2
					&& sToken[1].equalsIgnoreCase("CHON")) {
				// Kiem tra xem co tra loi dung hay khong ? check isWin
				Util.logger.info("isWin: " + isWin(userid));

				int isWinner1 = isWin(userid);
				
				// Check xem da nhan qua tang chua ?
				int isExpire = isExpire(userid);

				if (isWinner1 == 1) {
					// Chua tra loi nhung da chon qua
					String mt = "Ban chua tra loi cau hoi cua chuong trinh \"Nha trieu phu\" Moi ban chon dap an A,B,C,D de tra loi.VD ban chon dap an A:Soan tin: "
							+ keyword + " A gui " + SERVICE1;
					msgObj.setMsgtype(1);
					msgObj.setUsertext(mt);
					msgObj.setContenttype(0);
					DBUtil.sendMT(msgObj);
					return "";

				} else if ((isWinner1 == 3) && (isExpire == 0)) {
					// Chon qua
					String mt = "";
					Random iRandom = new Random();
					int i = iRandom.nextInt(4);
					switch (i) {
					case 0:
						mt = WAP_1;
						break;
					case 1:
						mt = WAP_2;
						break;
					case 2:
						mt = WAP_3;
						break;
					}

					msgObj.setMsgtype(1);
					msgObj.setUsertext(mt);
					msgObj.setContenttype(8);
					// Update da nhan qua`
					updateExpire(userid);
					DBUtil.sendMT(msgObj);
					return "";
				} else {
					// Chua tham gia chuong trinh hoac tham gia ma tra loi sai
					// hoac da nhan qua` con muon nhan nua
					String mt = "De tham gia chuong trinh: \"Nha trieu phu\" moi ban soan tin TP gui "
							+ SERVICE7
							+ " va lam theo huong dan cua chuong trinh. Phan thuong 30 trieu dong dang cho chu nhan cua no. Nhanh tay len cac ban nhe!";
					msgObj.setMsgtype(1);
					msgObj.setUsertext(mt);
					msgObj.setContenttype(0);
					DBUtil.sendMT(msgObj);
					return "";
				}

			}

		} else if (SERVICE5.equalsIgnoreCase(serviceid)) {
			String mt = "";
			String half_answer = getHalfAnswer(userid, timediff);
			if ("".equals(half_answer))
				mt = "Ban da nhan tin sai cu phap. Vui long nhan lai theo dung cu phap hoac lien he den tong dai 19001255 de biet them chi tiet.";
			else
				mt = half_answer;
			return mt;
		} else {
			return "Ban da nhan tin sai cu phap hoac dau so.Hay goi toi so 19001745 de duoc ho tro";
		}

		return "";
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
				dbpool.cleanup(statement);
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

	private String getQuestion(int questionid) {
		String question = "";

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT question FROM " + "icom_trieuphu_data"
				+ " WHERE question_id=" + questionid;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					question = rs.getString(1);
				}
			}

		} catch (SQLException ex2) {
			Util.logger.error("getQuestion. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("getQuestion. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return question;
	}

	private long getBalance(String userid) {
		long sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select banlance from icom_truytimkhobau where msisdn='"
				+ userid + "'";

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

	private int saveWinner(String gameid, String keyword, String serviceid,
			String userid, String mttext, long lsequence) throws Exception {

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
			String sqlInsert = "Insert into icom_winner (game_id,command_code,user_id,service_id,info,sequence) values ('"
					+ gameid
					+ "','"
					+ keyword
					+ "','"
					+ userid
					+ "','"
					+ serviceid + "','" + mttext + "'," + lsequence + ")";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger
						.error(gameid + ": Insert in to sfone_winner Failed");
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.error(gameid + ": Error:" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error(gameid + ": Error:" + e.toString());
			return -1;
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

	/**
	 * Sets &quot;virtual field&quot; value for all parameters
	 * 
	 * @param params
	 */
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

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if (temp == null || "null".equalsIgnoreCase(temp)
					|| "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			// TODO: handle exception
			return defaultvalue;
		}

	}

	private int[] checkValidity(String commandcode, String serviceid,
			String userid, String timediff, long sequence) {

		int isValid;
		int questionid;
		int checkValidityKQ[];
		String tblName_user = "icom_trieuphu_user";
		String tblName_data = "icom_trieuphu_data";
		isValid = 0;
		questionid = 1;
		checkValidityKQ = new int[2];
		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("gateway");

			String sqlSelect = "SELECT isValidity FROM " + tblName_user
					+ " WHERE user_id='" + userid + "'";

			Vector result = DBUtil.getVectorTable(connection, sqlSelect);

			if (result.size() > 0) {
				String sqlSelect1 = "SELECT isValidity FROM "
						+ tblName_user
						+ " WHERE user_id='"
						+ userid
						+ "' AND isValidity=1 AND current_timestamp < addtime(timestamp,'"
						+ timediff + "')";
				Vector result1 = DBUtil.getVectorTable(connection, sqlSelect1);

				if (result1.size() == 0) {
					isValid = 1;
					String sqlSelect2 = "SELECT max(question_id) FROM "
							+ tblName_user + " WHERE user_id='" + userid + "'";
					Vector result2 = DBUtil.getVectorTable(connection,
							sqlSelect2);

					if (result2.size() > 0) {

						Vector item2 = (Vector) result2.elementAt(0);
						String sqid = (String) item2.elementAt(0);
						questionid = Integer.parseInt(sqid);

						String sqlSelect3 = "SELECT max(question_id) FROM "
								+ tblName_data;
						Vector result3 = DBUtil.getVectorTable(connection,
								sqlSelect3);

						Vector item3 = (Vector) result3.elementAt(0);
						String smaxqid = (String) item3.elementAt(0);

						int max_question_id = Integer.parseInt(smaxqid);

						if (++questionid > max_question_id)
							questionid = 1;
					} else {
						questionid = 1;
					}
					String sqlInsert = "INSERT INTO "
							+ tblName_user
							+ " (command_code,service_id,user_id,question_id,timestamp,sequence) VALUES ('"
							+ commandcode + "','" + serviceid + "','" + userid
							+ "'," + questionid + ",'"
							+ new Timestamp((new Date()).getTime()) + "',"
							+ sequence + ")";

					if (DBUtils.executeSQL(connection, sqlInsert) < 0) {
						Util.logger.sysLog(2, this.getClass().getName(),
								": checkValidity:Insert data failed");
						isValid = 0;
						throw new Exception("checkValidity:Insert data failed");
					}

				}
			} else {
				isValid = 1;
				String sqlInsert = "INSERT INTO "
						+ tblName_user
						+ " (command_code,service_id,user_id,question_id,timestamp,sequence) VALUES ('"
						+ commandcode + "','" + serviceid + "','" + userid
						+ "'," + questionid + ",'"
						+ new Timestamp((new Date()).getTime()) + "',"
						+ sequence + ")";
				if (DBUtils.executeSQL(connection, sqlInsert) < 0) {
					Util.logger.sysLog(2, this.getClass().getName(),
							": checkValidity:Insert data failed");
					isValid = 0;
					throw new Exception("checkValidity:Insert data failed");
				}
			}

		} catch (Exception ex) {
			Util.logger.sysLog(4, getClass().getName(),
					"checkValidity:query data fail " + ex.getMessage());
			isValid = 0;

		} finally {
			dbpool.cleanup(connection);

		}

		checkValidityKQ[0] = isValid;
		checkValidityKQ[1] = questionid;
		return checkValidityKQ;
	}

	private int checkAnswer(String userid, String timediff, String answer) {

		int isRight;
		String tblName_user = "icom_trieuphu_user";
		String tblName_data = "icom_trieuphu_data";

		isRight = 0;
		int questionid = 0;

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("gateway");
			String sqlSelect = "SELECT question_id FROM " + tblName_user
					+ " WHERE user_id='" + userid + "' AND isValidity=1";
			Vector result = DBUtil.getVectorTable(connection, sqlSelect);
			if (result.size() > 0) {
				String sqlSelect1 = "SELECT question_id FROM "
						+ tblName_user
						+ " WHERE user_id='"
						+ userid
						+ "' AND isValidity=1 and current_timestamp < addtime(timestamp,'"
						+ timediff + "') order by timestamp desc";
				Vector result1 = DBUtil.getVectorTable(connection, sqlSelect1);

				if (result1.size() > 0) {

					Vector item1 = (Vector) result1.elementAt(0);
					String sqid = (String) item1.elementAt(0);

					questionid = Integer.parseInt(sqid);
					String sqlSelect2 = "SELECT answer FROM " + tblName_data
							+ " WHERE question_id=" + questionid
							+ " AND answer='" + answer.toUpperCase() + "'";
					Vector result2 = DBUtil.getVectorTable(connection,
							sqlSelect2);

					if (result2.size() > 0)
						isRight = 1;
					String sqlUpdate = "UPDATE " + tblName_user
							+ " SET isValidity=0,isWin= " + isRight
							+ ",answer='" + answer + "' WHERE user_id='"
							+ userid + "' and isValidity=1 and question_id="
							+ questionid;
					if (DBUtils.executeSQL(connection, sqlUpdate) < 0) {
						Util.logger.sysLog(4, getClass().getName(), "Loi roi");
					}

				} else {
					isRight = -1;
				}
			} else {
				isRight = -1;
			}

		} catch (Exception ex) {
			Util.logger.sysLog(4, getClass().getName(), "Loi roi:"
					+ ex.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return isRight;
	}

	private String getHalfAnswer(String userid, String timediff) {
		String tblName_user = "icom_trieuphu_user";
		String tblName_data = "icom_trieuphu_data";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String questionid = "";
		String half_answer = "";
		try {
			connection = dbpool.getConnection("gateway");
			String sqlSelect = "SELECT question_id FROM " + tblName_user
					+ " WHERE user_id='" + userid
					+ "' AND isValidity=1 order by timestamp desc";
			Vector result = DBUtil.getVectorTable(connection, sqlSelect);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				questionid = (String) item.elementAt(0);
				String sqlSelect2 = "SELECT half_answer FROM " + tblName_data
						+ " WHERE question_id=" + questionid;
				Vector result2 = DBUtil.getVectorTable(connection, sqlSelect2);

				Vector item2 = (Vector) result2.elementAt(0);
				half_answer = (String) item2.elementAt(0);

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			dbpool.cleanup(connection);

		}

		return half_answer;
	}

	private int getQuestionid(String userid) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		int result = 0;
		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return 0;
			}

			String query = "SELECT max(question_id) FROM icom_trieuphu_user WHERE upper(user_id) ='"
					+ userid.toUpperCase() + "'";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY : " + query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
				}
			}
			return result;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName()
					+ " Get Question: Failed" + ex.getMessage());
			ex.printStackTrace();
			return result;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	private int isWin(String userid) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		int resultWin = 0;
		String resultAnswer = "";
		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return 0;
			}
			int question = getQuestionid(userid);

			// Neu chua co cau hoi
			if (question == 0) {
				return 0;
			}

			String query = "SELECT isWin, answer FROM icom_trieuphu_user WHERE (upper(user_id) ='"
					+ userid.toUpperCase()
					+ "') AND (question_id="
					+ question
					+ ")";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY : " + query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					resultWin = rs.getInt(1);
					resultAnswer = rs.getString(2);
					if (resultWin == 1) {
						return 3;
					} else if ((resultWin == 0)
							&& ((resultAnswer == null) || (""
									.equalsIgnoreCase(resultAnswer)))) {
						return 1;
					} else if ((resultWin == 0)
							&& ((resultAnswer != null) || (!""
									.equalsIgnoreCase(resultAnswer)))) {
						return 2;
					}
				}
			}

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + " Get isWin: Failed"
					+ ex.getMessage());
			ex.printStackTrace();
			return 0;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
		return 0;

	}

	// Chi duoc chon 1 lan qua
	private int isExpire(String userid) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		int resultExpire = 0;

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return 0;
			}
			int question = getQuestionid(userid);

			// Neu chua co cau hoi
			if (question == 0) {
				return 0;
			}

			String query = "SELECT isExpire FROM icom_trieuphu_user WHERE (upper(user_id) ='"
					+ userid.toUpperCase()
					+ "') AND (question_id="
					+ question
					+ ")";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY : " + query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					resultExpire = rs.getInt(1);
					return resultExpire;
				}
			}

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName()
					+ " Get isExpire: Failed" + ex.getMessage());
			ex.printStackTrace();
			return 0;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
		return 0;

	}

	private boolean updateExpire(String userid) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		int resultExpire = 0;

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			int question = getQuestionid(userid);

			// Neu chua co cau hoi
			if (question == 0) {
				return false;
			}

			String query = "UPDATE icom_trieuphu_user SET isExpire = 1 WHERE (upper(user_id) ='"
					+ userid.toUpperCase()
					+ "') AND (question_id="
					+ question
					+ ")";
			statement = connection.prepareStatement(query);

			Util.logger.info("QUERY : " + query);
			statement.execute();
			return true;

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName()
					+ " Get isExpire: Failed" + ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

}