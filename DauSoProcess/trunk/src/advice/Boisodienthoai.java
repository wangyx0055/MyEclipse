package advice;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class Boisodienthoai extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		Collection messages = new ArrayList();
		String dtbase = "gateway";
		String operator = msgObject.getMobileoperator();
		
		try {
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);

			_option = getParametersAsString(options);
			dtbase = getString(_option, "dtbase", dtbase);
	
			int[] result = new int[2];
			String infoid = msgObject.getMobileoperator();
			Timestamp time = msgObject.getTTimes();
			if ("-".equals(infoid) || "other".equalsIgnoreCase(infoid)) {
				msgObject
						.setUsertext("Xin loi,dich vu nay ko ho tro mang cua ban.Moi thac mac lien he: 19001515");
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			String userid = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");

			if (sTokens.length ==2) {
				String sdt = sTokens[1];
				

				if (isNumeric(sdt)) {
					
					int num=Getnum(sdt);
					String noidung= getContent(num);
					String[] mtsend = noidung.split("###");
					msgObject.setUsertext(mtsend[0]);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					for (int i = 1; i < mtsend.length; i++) {
						msgObject.setUsertext(mtsend[i]);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					}

					return messages;
				} else {
					msgObject
							.setUsertext("Tin sai cu phap.Lien he tong dai 1900571566 de biet them chi tiet.Nhan DS Nhac cho sieu hot cho de soan MCA gui 8551");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				}

			} else {
				msgObject
						.setUsertext("Tin sai cu phap.Lien he tong dai 1900571566 de biet them chi tiet.Nhan DS Nhac cho sieu hot cho de soan MCA gui 8551");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
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
			Util.logger.sysLog(2, this.getClass().getName(), "Send gif Failed");
		}
	}
    private static int Getnum(String sdt)
    {
    	int num=0;
    	int tam=0;
    	Character c;
    	if (sdt.length()==1) 
    	
    	{
    		num=Integer.parseInt(sdt);
    		
    	}
    	else
    	{
    		for (int i=0; i<sdt.length();i++)
    		{   c=new Character(sdt.charAt(i));
    			tam=Integer.parseInt(c.toString() );
    			num=num+tam;
    		}
    		sdt= Integer.toString(num);
    		return Getnum(sdt);
    	}
    	
    	
    	return num;
    }
	private static String checkMobileOperator(String mobile_operator) {

		String infoid = "other";
		if ("VIETTEL".equalsIgnoreCase(mobile_operator)
				|| "VIETEL".equalsIgnoreCase(mobile_operator)) {
			infoid = "viettel";
		} else if (("VMS".equalsIgnoreCase(mobile_operator))
				|| "mobifone".equalsIgnoreCase(mobile_operator)) {
			infoid = "mobifone";
		} else if (("GPC".equalsIgnoreCase(mobile_operator))
				|| ("VINAPHONE".equalsIgnoreCase(mobile_operator))) {
			infoid = "vinaphone";
		} else {
			infoid = "other";
		}
		return infoid;

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

	private static int getValidPhone(String[] sTokens) {
		int place = 0;
		for (int i = 0; i < sTokens.length; i++) {
			if (!"-".equalsIgnoreCase(ValidISDNNew(sTokens[i]))) {
				place = i;
				return place;
			}
		}
		return place;
	}

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Long.parseLong(sISDN);

			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			Util.logger.info("Utils.ValidISDN" + "Exception?*" + e.toString()
					+ "*");
			return "-";
		}
		return tempisdn;
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
			Util.logger.info("Gia khong hop le");
		}
		return false;
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

	private static boolean saveClient(String dbcontent, String userid,
			int code, String filmname) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO " + dbcontent
					+ "(user_id, code,filmname)" + "VALUES(?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setInt(2, code);
			statement.setString(3, filmname);
			statement.executeUpdate();
			Util.logger.info("sqlInsert  : " + sqlInsert);
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

	private static String getContent(int id) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return sequence_temp;
			}

			String query = "SELECT noidung FROM boisodienthoai WHERE id = "
					+ id + "";
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

}
