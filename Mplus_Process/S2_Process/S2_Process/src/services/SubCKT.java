package services;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import icom.DBPool;
import java.util.Date;

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.Prices;
import icom.QuestionManager;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class SubCKT extends QuestionManager {

	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {

		Collection messages = new ArrayList();

		int msg1mt = Integer.parseInt(Constants.MT_PUSH);
		int msg2mt = Integer.parseInt(Constants.MT_PUSH);

		if (keyword.getService_type() == Constants.PACKAGE_SERVICE) {
			msg1mt = Integer.parseInt(Constants.MT_CHARGING);
			msg2mt = Integer.parseInt(Constants.MT_PUSH);

		} else if (keyword.getService_type() == Constants.DAILY_SERVICE) {
			msg2mt = Integer.parseInt(Constants.MT_CHARGING);
			msg1mt = Integer.parseInt(Constants.MT_NOCHARGE);
		}

		String[] sMTReturn = mtReturn(prop, msgObject, keyword, msg2mt);

		if (sMTReturn.length >= 1) {
			for (int j = 0; j < sMTReturn.length; j++) {
				if (j == 0) {
					msgObject.setMsgtype(msg1mt);
					if (msg1mt == Integer.parseInt(Constants.MT_NOCHARGE)) {
						msgObject.setAmount(0);
					}
				} else {
					msgObject.setMsgtype(Integer
							.parseInt(Constants.MT_NOCHARGE));
					msgObject.setAmount(0);
				}
				msgObject.setContenttype(0);
				msgObject.setUsertext(sMTReturn[j]);

				messages.add(new MsgObject(msgObject));

			}
		}

		if (msg1mt == Integer.parseInt(Constants.MT_NOCHARGE)) {
			Iterator iter = messages.iterator();
			for (int i = 1; iter.hasNext(); i++) {
				MsgObject msgMT = (MsgObject) iter.next();
				Subgeneric.sendMT2sendqueue(msgMT);
			}
			//savefirstmo(msgObject, keyword);
			return null;
		}

		//savefirstmo(msgObject, keyword);

		return messages;

	}

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));
			return temp;
		} catch (Exception e) {
			// TODO: handle exception
			return _defaultval;
		}
	}

	int savefirstmo(MsgObject msgobj, Keyword keyword) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ Constants.tblMO1Queue
				+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO,  REQUEST_ID) values ('"
				+ msgobj.getUserid() + "','" + msgobj.getServiceid() + "','"
				+ msgobj.getMobileoperator() + "','" + msgobj.getKeyword()
				+ "','" + keyword.getService_ss_id() + "','"
				+ msgobj.getRequestid().toString() + "')";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + sqlInsert
						+ " Failed:" + sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName() + ":Insert  "
					+ sqlInsert + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;

	}

	private String[] mtReturn(Properties prop, MsgObject msgObject,
			Keyword keyword, int msgtype) throws Exception {

		HashMap _option = new HashMap();

		String MLIST = "mlist";

		String options = keyword.getOptions();
		_option = getParametersAsString(options);
		// System.out.println("option:" + options);
		MLIST = ((String) _option.get("mlist"));
		// /mlist=mlist_cauxs&mtfree=7&companyid=81
		Util.logger.info("mlist=" + MLIST);
		String mtfree = getStringfromHashMap(_option, "mtfree", "0");

		String companyid = getStringfromHashMap(_option, "companyid", "1");

		Prices price = Sender.loadconfig.getPrice(keyword.getService_ss_id());

		if (price != null) {
			mtfree = price.getMt_free() + "";
		}

		String info = msgObject.getUsertext();

		info = info.replace('-', ' ');
		info = info.replace(';', ' ');
		info = info.replace('+', ' ');
		info = info.replace('.', ' ');
		info = info.replace(',', ' ');
		info = info.replace('_', ' ');
		info = replaceAllWhiteWithOne(info.trim());

		String[] arrInfo = info.split(" ");
		// dk ck mack

		boolean infodetails = false;
		Util.logger.info("info:" + info);
		String syntax = "CK";
		if (arrInfo.length > 2) {
			for (int i = 2; i < arrInfo.length; i++) {
				if (isexist_stocksymbol(arrInfo[i])) {
					syntax = syntax + " " + arrInfo[i].trim();

					infodetails = true;
					Util.logger.info("isexist_stocksymbol:" + arrInfo[i]);
				}

			}

		}
		Util.logger.info("syntax:" + syntax);

		// Kiem tra xem da co hay chua?
		String[] mtReturn = new String[1];

		if (isexist(msgObject.getUserid(), MLIST, companyid)) {
			mtReturn[0] = keyword.getExistMsg();
		} else {

			mtReturn[0] = keyword.getSubMsg();

			if (Constants.MODE_ADV == 0) {
				if (!isexist_in_cancel(msgObject.getUserid(), MLIST, companyid)) {
					if (!mtfree.equalsIgnoreCase("0")) {
						mtReturn[0] = keyword.getPromoMsg();
					}
				} else {
					// da dung roi thi khong con khuyen mai
					mtfree = "0";

				}
			} else
				mtfree = "0";

			insertData(msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getServiceid(), MLIST, msgObject, mtfree,
					msgtype, keyword.getService_ss_id(), syntax);
			// mtReturn[0] = keyword.getSubMsg();
			insertSubUser(msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getServiceid(), MLIST, msgObject, mtfree,
					msgtype, keyword.getService_ss_id(), companyid, keyword.getService_ss_id());

		}

		return mtReturn;

	}

	private int insertData(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			String mtfree, int msgtype, String Service_ss_id, String options) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "(user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,options) values ('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getLongRequestid() + "','" + msgtype + "','"
				+ msgObject.getMobileoperator() + "'," + mtfree + ",'"
				+ options + "')";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName() + ":Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}
	public int insertSubUser(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			String mtfree, int msgtype, String Service_ss_id, String companyid,
			String service) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into mlist_subcriber(service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,company_id) values ('"
				+ service.toUpperCase()
				+ "','"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getLongRequestid() + "','" + msgtype + "','"
				+ msgObject.getMobileoperator() + "'," + mtfree + ","
				+ companyid + ")";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName() + ":Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}
	private static boolean isexist(String userid, String mlist, String companyid) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist + " where user_id='"
					+ userid + "'";

			Util.logger.info(query3);

			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	private static boolean isexist_stocksymbol(String stocksymbol) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from icom_mack where upper(mack)='"
					+ stocksymbol.trim().toUpperCase() + "' ";

			Util.logger.info(query3);

			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	private static boolean isexist_in_cancel(String userid, String mlist,
			String companyid) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist
					+ "_cancel where user_id='" + userid + "'";

			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
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

	private static int sendMT(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("sendMT@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis("sendMT: Error connection == null"
						+ msgObject.getUserid() + "@TO"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO mt_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("sendMT: Error:@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("sendMT: Error:@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

	@SuppressWarnings("unchecked")
	private Hashtable GetOptions(int duration) {

		Util.logger.info(this.getClass().getName() + "@" + "Duration (int)"
				+ duration);
		Hashtable options = new Hashtable();
		Calendar cal = Calendar.getInstance();
		Util.logger.info(this.getClass().getName() + "@" + "Current Date: "
				+ new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
		options.put("startdate", new SimpleDateFormat("yyyy-MM-dd").format(cal
				.getTime()));
		cal.add(Calendar.DAY_OF_MONTH, duration);
		Util.logger.info(this.getClass().getName() + "@" + "End Date: "
				+ new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
		options.put("enddate", new SimpleDateFormat("yyyy-MM-dd").format(cal
				.getTime()));
		Util.logger.info(this.getClass().getName() + "@" + "Warning Date: "
				+ new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
		options.put("warningdate", new SimpleDateFormat("yyyy-MM-dd")
				.format(cal.getTime()));// enddate same as warning date.

		cal.add(Calendar.DAY_OF_MONTH, 1);
		Util.logger.info(this.getClass().getName() + "@" + "Billing Date: "
				+ new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
		options.put("billingdate", new SimpleDateFormat("yyyy-MM-dd")
				.format(cal.getTime()));// enddate same as warning date.

		options.put("duration", duration + "");
		return options;
	}

}
