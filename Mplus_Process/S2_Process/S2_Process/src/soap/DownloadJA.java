package soap;

import icom.QuestionManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import icom.Constants;
import icom.DBPool;
import icom.ExecuteReSendQueue;
import icom.Keyword;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

public class DownloadJA extends QuestionManager {

	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {

		Collection messages = new ArrayList();

		try {

			HashMap _option = new HashMap();

			String MLIST = "mlist";

			String options = keyword.getOptions();
			_option = getParametersAsString(options);

			// type = 1 la Game, 2 la Theme
			String key = "GAME";

			// Main code gui sang Kraze
			key = getStringfromHashMap(_option, "key", key);
			String category = getStringfromHashMap(_option, "category", "Game");

			// The loai Game la 1, 2 la theme
			int type = getIntfromHashMap(_option, "type", 1);
			
			String cp = getString(_option, "cp", "KRAZE").toUpperCase();

			String DBCONTENT = getStringfromHashMap(_option, "dbcontent",
					"gateway");
			String dtbase = getStringfromHashMap(_option, "dtbase",
					"icom_javaapp");

			// Kiem tra so lan nhan tin
			int sendtime = getUserID(msgObject.getUserid(), dtbase, type);

			String lastid = "";
			if (sendtime >= 1) {
				lastid = getSendID(msgObject.getUserid(), dtbase, type);
			} else {
				// Ghi lai danh sach khach hang da gui
				saverequest(msgObject.getUserid(), dtbase, type);
			}

			// Check valid code
			String result = "";
			String Gamecode = "0";
			String[] stoken = msgObject.getUsertext().split(" ");

			if (stoken.length > 1) {
				Gamecode = stoken[1];
			} else {
				String content = "";
				content = findList(dtbase, msgObject.getUserid(), lastid, type);

				if ("".equalsIgnoreCase(content) || content == null) {
					content = findList(dtbase, msgObject.getUserid(), "", type);
					updateList(msgObject.getUserid(), "", dtbase, type);
				}
				msgObject.setUsertext("D/sach ma so the loai "
						+ category + ":" + content);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			String gameCodeKraze = checkValidCode(DBCONTENT, Gamecode, type);

			if ("INVALID".equalsIgnoreCase(gameCodeKraze)) {

				// Lay ma so game hoac theme
				String content = "";
				content = findList(dtbase, msgObject.getUserid(), lastid, type);

				if ("".equalsIgnoreCase(content) || content == null) {
					content = findList(dtbase, msgObject.getUserid(), "", type);
					updateList(msgObject.getUserid(), "", dtbase, type);
				}
				msgObject
						.setUsertext("Tin nhan sai cu phap.D/sach ma so the loai "
								+ category + ":" + content);

				// Gui cac ma so cho khach hang o day
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			// Valid ma game
			// Neu okie thi gui sang
			msgObject.setUsertext(gameCodeKraze);

			Util.logger.info("Keywords Gui sang Partner: " + key);
			Util.logger.info("Partner: " + cp);

			result = msgObject.sendMessageMO(gameCodeKraze, cp, key);

			Util.logger.info(this.getClass().getName() + "@" + "Got " + result
					+ " , ok,Details: " + "Msisdn: " + msgObject.getUserid()
					+ " Shortcode: " + msgObject.getServiceid() + " Keyword: "
					+ msgObject.getKeyword() + " RequestID: "
					+ msgObject.getRequestid() + "CommandCode: "
					+ keyword.getKeyword() + " Online Retry : "
					+ msgObject.getRetries_num());

			result = getValue(result, "GetMODataResult");
			Util.logger.info("Ket qua thong tin nhan duoc: " + result);

			if (result.startsWith("-1")) {

				int retriesnum = msgObject.getRetries_num();

				Util.logger.info(this.getClass().getName() + "@" + "Got "
						+ result + " , Going For Retry, Sleeping,Details: "
						+ "Msisdn: " + msgObject.getUserid() + " Shortcode: "
						+ msgObject.getServiceid() + " Keyword: "
						+ msgObject.getKeyword() + " RequestID: "
						+ msgObject.getRequestid() + "CommandCode: "
						+ keyword.getKeyword() + " Online Retry countdown: "
						+ retriesnum);
				msgObject.setTTimes(DateProc.createTimestamp());

				if (retriesnum >= Constants.MAX_RETRIES) {
					if (msgObject.getObjtype() == 0) {
						add2MOSendFailed(msgObject);
					} else {
						add2MTSendFailed(msgObject);
					}

				} else {
					msgObject.setRetries_num(retriesnum + 1);
					ExecuteReSendQueue.add2queueResend(msgObject);
				}
			} else if (result.startsWith("0") || result.startsWith("0|")) {
				// String[] sTokens = result.split("");
				int start = result.indexOf("0");
				String temp = result.substring(start + 2);
				Util.logger.info("Thong tin tra ve cho khach hang:" + temp);
				msgObject.setUsertext(temp);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(8);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {
				msgObject.setUsertext("Tin nhan sai cu phap.");
				msgObject.setMsgtype(2);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

		} catch (Exception e) {
			int retriesnum = msgObject.getRetries_num();
			Util.logger
					.error(this.getClass().getName()
							+ "@"
							+ "Some Exception..!! Got -1, Going For Retry, Sleeping,Details: "
							+ "Msisdn: " + msgObject.getUserid()
							+ " Shortcode: " + msgObject.getServiceid()
							+ " Keyword: " + msgObject.getKeyword()
							+ " RequestID: " + msgObject.getRequestid()
							+ "CommandCode: " + keyword.getKeyword()
							+ " Online Retry countdown: " + retriesnum);
			Util.logger.info(this.getClass().getName() + "@" + "Exception: "
					+ e.toString());
			e.printStackTrace();
			msgObject.setTTimes(DateProc.createTimestamp());
			if (retriesnum >= Constants.MAX_RETRIES) {
				if (msgObject.getObjtype() == 0) {
					add2MOSendFailed(msgObject);
				} else {
					add2MTSendFailed(msgObject);
				}
			} else {
				msgObject.setRetries_num(retriesnum + 1);
				ExecuteReSendQueue.add2queueResend(msgObject);
			}
		}

		return messages;
	}
	
	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)
					|| temp == null) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static int getUserID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT user_id FROM " + dtbase + " WHERE user_id= '"
				+ userid.toUpperCase() + "' AND type =" + subcode;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			String temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					temp = rs.getString(1);
					return 1;
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
		return -1;
	}

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static String getSendID(String userid, String dtbase, int type) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT send_id FROM " + dtbase
					+ " WHERE user_id= '" + userid.toUpperCase()
					+ "' AND type=" + type;
			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return sequence_temp;
			}
			Util.logger.info("Query: " + query);
			statement = cnn.prepareStatement(query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return sequence_temp;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return sequence_temp;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
	}

	private static boolean saverequest(String userid, String dtbase, int subcode) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO " + dtbase
					+ "(user_id, type) VALUES ('" + userid + "'," + subcode
					+ ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into dbcontent");
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

	// Cập nhật danh sách các id của các bài hát đã gửi cho khách hàng.
	private static boolean updateList(String userid, String lastid,
			String dtbase, int subcode) {

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
			String sqlUpdate = "UPDATE " + dtbase + " SET send_id = '" + lastid
					+ "' WHERE upper(user_id)='" + userid.toUpperCase()
					+ "' AND type=" + subcode;
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list id to send " + userid
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

	/* Tìm list bai hat */
	private static String findList(String dtbase, String userid, String lastid,
			int type) {
		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT name_vi, icom_id FROM icom_krazevn WHERE (type="
					+ type + ")";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND icom_id not in (" + lastid + ") ";
			}

			Util.logger.info("SEARCH Game ID MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = result + rs.getString(1) + "-" + rs.getString(2)
							+ ";";
					if (result.length() > 160) {
						// cap nhat cho khach hang list danh sach da gui
						updateList(userid, lastid, dtbase, type);
						return result;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + rs.getString(2);
						} else {
							lastid = lastid + "," + rs.getString(2);
						}
					}
				}
			}
			updateList(userid, lastid, dtbase, type);
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);

		return (f > l) ? "" : xml.substring(f, l);
	}

	private String checkValidCode(String poolname, String validcode, int type) {

		String strResult = "INVALID";
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String query = "SELECT kraze_id FROM icom_krazevn WHERE upper(icom_id) = '"
				+ validcode.toUpperCase() + "' AND type=" + type;

		try {
			connection = dbpool.getConnection(poolname);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + result.size() + "@"
					+ query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				strResult = (String) item.elementAt(0);

				Util.logger.info(this.getClass().getName()
						+ "getContent: Ma Game Kraze:" + strResult);
				return strResult;
			}

			return strResult;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return strResult;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));

			if (temp == null) {
				return _defaultval;
			}
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return _defaultval;
			}

			return temp;
		} catch (Exception e) {
			return _defaultval;
		}
	}

	public int getIntfromHashMap(HashMap _map, String _key, int _defaultval) {
		try {
			String temp = ((String) _map.get(_key));

			if (temp == null) {
				return _defaultval;
			}
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return _defaultval;
			}
			try {
				int i = Integer.parseInt(temp);
				return i;
			} catch (Exception ex) {
				return _defaultval;
			}

		} catch (Exception e) {
			return _defaultval;
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

	private static BigDecimal add2MOSendFailed(MsgObject msgObject) {
		Util.logger.info("mo_queue_error:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mo_queue_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE)"
				+ " values(?,?,?,?,?,?,?)";

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

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2MOSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2MOSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2MOSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private static BigDecimal add2MTSendFailed(MsgObject msgObject) {
		Util.logger.info("mt_queue_error:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mt_queue_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID)"
				+ " values(?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2MTSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2MTSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2MTSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

}
