package vote;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.axis.transport.jms.TopicConnector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.Utils;

import cs.ExecuteADVCR;

public class VoteSaomai extends ContentAbstract {

	// ///

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

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			// TODO: handle exception
			return defaultvalue;
		}

	}

	int getInt(HashMap _option1, String field, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get(field));
		} catch (Exception e) {
			// TODO: handle exception
			// Integer.parseInt((String) _option.get("num_cnt"));
			return defaultvalue;
		}

	}

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		try {

			String _subject = "";
			int _status = 0;
			String _mtvalid = "VPBank cam on ban da binh chon cho SBD #1 thi sinh #2 trong SMDH 2010. Chuc may man trung thuong cua VPbank.";
			String _mtinvalid = "Tin nhan chua chinh xac. Binh chon ca sy duoc yeu thich nhat SMDH 2010: SM X (X:so bao danh) gui 8342";
			String _mtinactive = "VPBank cam on ban da binh chon cho ca sy trong SMDH. Moi cac ban theo doi danh sach trung thuong sau liveshow.";

			String _timeactive = "";
			String _timeinactive = "";
			String _mtinactiveall="Chuong trinh da ket thuc. Cam on ban da quan tam den chuong trinh";
			int _type = 0;

			// subject=ABC&status=0&type=0&timeactive=2009-06-12
			// 00:00:00&timeinactive=2009-06-12 22:22:3
			// &mtvalid=ban da tham gia&mtinvalid=sai cu phap
			// &mtinactive=chua den gio

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);

			_subject = getString(_option, "subject", "");
			_status = getInt(_option, "status", 0);
			
			_type = getInt(_option, "type", 0);
			// kieu voting:
			// 1: co 1 subcode VOT X(X: pa)
			
			if (_status == 0) {
				Util.logger.info("Chat&vote@VOTE is LOCKED@userid="
						+ msgObject.getUserid());
				msgObject.setUsertext(_mtinactiveall);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			_timeactive=gettime()[0];
			_timeinactive=gettime()[1];
			long lActivetime = String2MilisecondNew(_timeactive);
			Util.logger.info("_timeactive:"+lActivetime);
					
			long lInactivetime = String2MilisecondNew(_timeinactive);
			Util.logger.info("_timeinactive:"+lInactivetime);
			
			long lCurrenttime = System.currentTimeMillis();
			Util.logger.info("lCurrenttime:"+lCurrenttime);
			
			if ((lActivetime > lCurrenttime) || (lInactivetime < lCurrenttime)) {
				Util.logger.info("Chat&vote@VOTE is Inactive@userid="
						+ msgObject.getUserid());
				msgObject.setUsertext(_mtinactive);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			String info = msgObject.getUsertext();

			info = info.replace('-', ' ');
			info = info.replace(';', ' ');
			info = info.replace('+', ' ');
			info = info.replace('.', ' ');
			info = info.replace(',', ' ');
			info = info.replace('_', ' ');
			info = replaceAllWhiteWithOne(info.trim());

			String[] sTokens = info.split(" ");
			String vote_x = "x";
			String xname="x";
			int vote_z = 1;
			// System.out.println("AAAAAAA:" + sTokens.length);
			
			Util.logger.info("Chat&vote@vote_type=" + _type);
			if (sTokens.length == 2) {
				String tam=sTokens[1];
				while (tam.startsWith("0"))
				{
					tam=tam.substring(1);
				}
				String[] sret = validcode(tam);
				vote_x = sret[0];
				xname = sret[1];
				if ("x".equalsIgnoreCase(vote_x)) {
					Util.logger.info("Chat&vote@invalid X=" + vote_x
							+ "@userid=" + msgObject.getUserid());
					msgObject.setUsertext(_mtinvalid);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {
					_mtvalid = _mtvalid.replace("#1", vote_x);
					_mtvalid = _mtvalid.replace("#2", xname);

					if (saverequest( msgObject.getUserid(), vote_x)) {
						msgObject.setUsertext(_mtvalid);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
						
						msgObject.setUsertext("De tai bai hat trong SMDH2010 soan: GAG gui 8542. Cam on ban da quan tam den chuong trinh");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						return null;
					} else {
						Util.logger
								.error("Chat&vote@saverequest = failed@userid="
										+ msgObject.getUserid());
						return null;
					}
				}
			} else {

				Util.logger.info("Chat&vote@invalid@userid="
						+ msgObject.getUserid());
				msgObject.setUsertext(_mtinvalid);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				
				return null;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

		return null;
	}
	private String[] gettime() {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT start_time,end_time FROM voting_config WHERE status = 1 order by id limit 1";

		
		try {
			connection = dbpool.getConnectionGateway();
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return record;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	private int valid_z(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			return 1;
		}

	}

	private String[] validcode(String votingid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String[] sret = new String[2];
		sret[0] = "x";
		sret[1] = "x";
			try {
			connection = dbpool.getConnectionGateway();

			String query1 = "select voting_id,alias from voting_item  where  upper(voting_id)='"
					+ votingid
					+ "'";

			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				
				sret[0] = (String) item.elementAt(0);
				sret[1] = (String) item.elementAt(1);


				return sret;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.error("Exeption:" + e.getMessage());
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(connection);

		}
		return sret;
	}
	

	private static boolean saverequest(String user_id, String x) {
		// id, game_time, keyword, service_id, user_id, x, y, z, autotimestamps
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into VOTING(  msisdn, x)"
					+ " values ('"
					+ user_id
					+ "','"
					+x
					+ "' )";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to voting");
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
			Util.logger
					.sysLog(2, this.getClass().getName(), "sendGifMsgFailed");
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

}
