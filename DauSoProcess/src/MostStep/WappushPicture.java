package MostStep;

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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.SoapLongcheerMO;
import com.vmg.soap.mo.sendXMLStore;

import cs.ExecuteADVCR;

public class WappushPicture extends ContentAbstract {

	String URL_INV = "http://mobinet.com.vn/?c=wap3";

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

			_params.put(key, value);
		}

		return _params;

	}

	String getOption(HashMap _option1, String defaultvalue) {
		try {
			return ((String) _option1.get("gid")).toUpperCase();
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	int getOption(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("gid"));
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String VALID_CODE = "";

		try {

			HashMap _option = new HashMap();

			String options = keyword.getOptions();

			_option = getParametersAsString(options);

			String mtcdma = getString(_option, "mtcdma",
					"Dich vu khong ho tro mang CDMA");
			String mtinvsubcode = getString(_option, "mtinvsubcode",
					"Ban nhan tin sai cu phap. Tang ban TOP nhac:" + URL_INV);

			String type = getString(_option, "type", "media");

			if (msgObject.getMobileoperator().equalsIgnoreCase("EVN")
					|| msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
				msgObject.setUsertext(mtcdma);

				msgObject.setMsgtype(2);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				return messages;
			}

			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			
			// log send MO		
			String temp = SoapMOMT.SendMO(msgObject);
			Util.logger.info("temp____" + temp);
			
			String lastcode = "0";
			// rieng voi CBHD2 la cu phap co 1 phan tu
			// cu phap khac CBHD2 la sai cu phap
			if (sTokens.length == 1 && !sTokens[0].startsWith("CBHD2")) {
				msgObject.setUsertext(mtinvsubcode);

				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				return messages;
			} else {
				
				// cu phap CBHD2 
				// cu phap CBHN..., CBHD1 HOT
				int cateid =0;
				String namecode="";
				if(sTokens.length == 1 && sTokens[0].startsWith("CBHD2"))
				{
					 cateid = GateCateid("HD2");
					 namecode = GateCateName(sTokens[0]);
				}	 
				else
				{
					cateid = GateCateid(sTokens[1]);
					 namecode = GateCateName(sTokens[0]);
				}
				// chua can lastcode, de lastcode=0
				//int code = getCode(cateid, lastcode);
				lastcode = getLastCode(msgObject.getUserid(), cateid);

				int code = getCode(cateid, "0");
				
				
				if (lastcode.length() > 1500) {

					String templastcode = lastcode.substring(500);
					if (templastcode.startsWith(",")) {
						lastcode = "0" + templastcode;
					}
				} else {
					if (code != 0) {
						lastcode = lastcode + "," + code;
					}
				}
				if (isexist(msgObject.getUserid(), "user_wappush")) {
					saveuser(msgObject.getUserid(), cateid, lastcode);
				} else {
					updateLastcode(msgObject.getUserid(), lastcode, cateid);
				}
				String price = SoapMOMT.GetPrice(msgObject.getServiceid());
				String link =  SoapMOMT.SendXML( 5,
						msgObject.getUserid(), code + "", 3, price, now());
				Util.logger.info("link:" + link);

				if (link.startsWith("0")) {
					
					msgObject.setUsertext(namecode+":"+link.substring(2));
					// log send MT		
					String temp2 = SoapMOMT.SendMT(msgObject);
					Util.logger.info("temp2____" + temp2);
					
					Util.logger.info("Noi dung tra khach hang:"+
							namecode+":"+ link.substring(2));

					msgObject.setContenttype(8);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));

					return messages;

				} else {
					
					msgObject.setUsertext("Hinh nen ban muon tai khong co tren he thong");
					msgObject.setContenttype(0);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					Util.logger.error("Loi he thong:" + link);

					return null;
				}

			}
		} catch (Exception e) {
			Util.logger.error("Exception: " + e.getMessage());
		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

		return null;
	}

	private static boolean updateLastcode(String userid, String lastcode,
			int Cateid) {

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
			String sqlUpdate = "UPDATE user_wappush SET lastcode = '"
					+ lastcode + "' WHERE upper(user_id)='"
					+ userid.toUpperCase() + "' AND Cateid=" + Cateid;
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list music to send " + userid
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

	private static boolean isexist(String userid, String table) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + table + " where user_id='"
					+ userid + "' ";

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

	public static int getCode(int CateID, String lastcode) {
		// tach lastcode

	
		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT TOP 1[ImageID] FROM [IComStore].[dbo].[Image] where CateID="
				+ CateID
				+ "  AND ImageID not in ("
				+ lastcode
				+ ")  and IsActive = 1 order by newid()";

		try {
			connection = dbpool.getConnection("store");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	public static int GateCateid(String command_code) {
		int cateid = 0;
		if (command_code.equalsIgnoreCase("HOT")) {
			cateid = 433;
		}
		if (command_code.equalsIgnoreCase("HD2")) {
			cateid = 432;
		}
		if (command_code.equalsIgnoreCase("TN")) {
			cateid = 429;
		}
		if (command_code.equalsIgnoreCase("NN")) {
			cateid = 428;
		}
		if (command_code.equalsIgnoreCase("TT")) {
			cateid = 427;
		}
		if (command_code.equalsIgnoreCase("XE")) {
			cateid = 444;
		}
		if (command_code.equalsIgnoreCase("TY")) {
			cateid = 430;
		}
		return cateid;
	}

	public static String GateCateName(String command_code) {
		String catename = "";
		if (command_code.equalsIgnoreCase("HOT")) {
			catename = "Hinh hot";
		}
		if (command_code.equalsIgnoreCase("HINHDONG")) {
			catename = "Hinh dong";
		}
		if (command_code.equalsIgnoreCase("TN")) {
			catename = "Hinh tu nhien";
		}
		if (command_code.equalsIgnoreCase("TT")) {
			catename = "Hinh the thao";

		}
		if (command_code.equalsIgnoreCase("NN")) {
			catename = "Hinh ngo nghinh";
		}
		if (command_code.equalsIgnoreCase("TY")) {
			catename = "Hinh tinh yeu";
		}
		if (command_code.equalsIgnoreCase("XE")) {
			catename = "Hinh phuong tien";
		}
		return catename;
	}

	public static String getLastCode(String user_id, int cateid) {
		// tach lastcode

		String lastcode = "0";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT lastcode FROM user_wappush WHERE upper(user_id) = '"
				+ user_id.toUpperCase() + "' and cateid=" + cateid;

		try {
			connection = dbpool.getConnectionGateway();
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				lastcode = (String) item.elementAt(0);
				return lastcode;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return lastcode;
		} finally {
			dbpool.cleanup(connection);
		}
		return lastcode;
	}

	
	private static boolean saveuser(String user_id, int cateid, String lastcode) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into user_wappush( user_id, cateid, lastcode) values ('"
					+ user_id + "'," + cateid + ",'" + lastcode + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to download");
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

	public String ValidISDN(String sISDN) {
		Util.logger.info(this.getClass().getName() + "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";

		if (sISDN.trim().length() < 8) {
			return "-";
		}
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	private String getMobileOperator(String mobileNumber) {
		if (mobileNumber.startsWith("8491") || mobileNumber.startsWith("+8491")
				|| mobileNumber.startsWith("091")
				|| mobileNumber.startsWith("91")
				|| mobileNumber.startsWith("8494")
				|| mobileNumber.startsWith("+8494")
				|| mobileNumber.startsWith("094")
				|| mobileNumber.startsWith("94")
				|| mobileNumber.startsWith("0123")
				|| mobileNumber.startsWith("84123")
				|| mobileNumber.startsWith("84125")
				|| mobileNumber.startsWith("0125")) {
			return "GPC";
		} else if (mobileNumber.startsWith("8490")
				|| mobileNumber.startsWith("+8490")
				|| mobileNumber.startsWith("090")
				|| mobileNumber.startsWith("90")
				|| mobileNumber.startsWith("8493")
				|| mobileNumber.startsWith("+8493")
				|| mobileNumber.startsWith("093")
				|| mobileNumber.startsWith("93")
				|| mobileNumber.startsWith("0122")
				|| mobileNumber.startsWith("84122")
				|| mobileNumber.startsWith("84126")
				|| mobileNumber.startsWith("0126")) {
			return "VMS";
		} else if (mobileNumber.startsWith("8498")
				|| mobileNumber.startsWith("+8498")
				|| mobileNumber.startsWith("098")
				|| mobileNumber.startsWith("98")
				|| mobileNumber.startsWith("8497")
				|| mobileNumber.startsWith("+8497")
				|| mobileNumber.startsWith("097")
				|| mobileNumber.startsWith("97")
				|| mobileNumber.startsWith("84168")
				|| mobileNumber.startsWith("0168")
				|| mobileNumber.startsWith("84168")
				|| mobileNumber.startsWith("0169")
				|| mobileNumber.startsWith("84169")
				|| mobileNumber.startsWith("84166")
				|| mobileNumber.startsWith("0166")) {
			return "VIETTEL";
		} else if (mobileNumber.startsWith("8495")
				|| mobileNumber.startsWith("+8495")
				|| mobileNumber.startsWith("095")
				|| mobileNumber.startsWith("95")) {
			return "SFONE";
		} else if (mobileNumber.startsWith("8492")
				|| mobileNumber.startsWith("+8492")
				|| mobileNumber.startsWith("092")
				|| mobileNumber.startsWith("92")) {
			return "HTC";
		} else if (mobileNumber.startsWith("8496")
				|| mobileNumber.startsWith("+8496")
				|| mobileNumber.startsWith("096")
				|| mobileNumber.startsWith("96")) {
			return "EVN";
		} else {
			return "-";
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
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}
}
