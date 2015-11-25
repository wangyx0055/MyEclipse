package wap;

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

public class Ringtone extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	String SERVER = "http://www.mobinet.com.vn";
	String URL_INV = "http://www.mobinet.com.vn/?c=wap3";
	String TOP_CODE = "1";
	int NUM_SUBCODE=0;

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

	String getOption(HashMap _option1, String defaultvalue) {
		try {
			return ((String) _option1.get("gid")).toUpperCase();
		} catch (Exception e) {
			return defaultvalue;
		}

	}
	
	String getTopcode(HashMap _option1, String defaultvalue) {
		try {
			return ((String) _option1.get("topcode")).toUpperCase();
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
	
	int getNumsubcode(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("numsubcode"));
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String VALID_CODE = "";
		

		try {

			if (msgObject.getMobileoperator().equalsIgnoreCase("EVN")
					|| msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
				msgObject.setUsertext("Dich vu khong ho tro mang CDMA."
						+ "\n*Tuong thuat TT KQXS: XS<matinh> gui 8551"
						+ "\n*Suu tam OMNIA: TE gui 8551"
						+ "\n*Tham gia Sieu Toc: AC gui 8551");
				
			
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				return messages;
			}

			HashMap _option = new HashMap();

			String options = keyword.getOptions();

			_option = getParametersAsString(options);

			GID = getOption(_option, 0);
			TOP_CODE = getTopcode(_option, "1");
			NUM_SUBCODE = getNumsubcode(_option, 0);

			// System.err.println("GID:" + GID);
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");

			// String MASO = "1";
			if (sTokens.length == 1) {
				// msgObject
				// .setUsertext("[ICom] Ma so ban tai khong hop le. Tang ban TOP
				// nhac:http://203.162.168.75:9090");

				// msgObject.setMsgtype(1);
				// msgObject.setContenttype(8);
				// messages.add(new MsgObject(msgObject));

				// return messages;
				VALID_CODE = TOP_CODE;
			} else {
				VALID_CODE = validcode(sTokens[1], GID);
				
			}

			String type = "media";

			if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
				String phone = msgObject.getUserid();
				String code = VALID_CODE;

				if (saverequest(phone, VALID_CODE, type, GID)) {

					String giff_userid = "";
					String giff_telco = "";
					boolean giff = false;

					if (sTokens.length > NUM_SUBCODE) {
						giff_userid = ValidISDN(sTokens[NUM_SUBCODE+1]);
						if (!giff_userid.equalsIgnoreCase("-")) {
							//giff_telco = getMobileOperator(giff_userid);
							giff_telco=Utils.getMobileOperator(giff_userid, 1);

							if ("-".equals(giff_telco)
									|| "EVN".equalsIgnoreCase(giff_telco)
									|| ("SFONE".equalsIgnoreCase(giff_telco))) {
								giff = false;
							} else {
								giff = true;
							}

						}
					}

					if (giff == true) {

						saverequest(phone, code, type, GID);
						sendGifMsg(msgObject.getServiceid(), giff_userid,
								giff_telco, msgObject.getKeyword(), "Sodt "
										+ msgObject.getUserid() + " tang ban "
										+ code + ":" + SERVER + "/?p=" + phone
										+ "&c=" + code + "&f=" + type + "&g="
										+ GID, msgObject.getRequestid(), 8);

						msgObject.setUsertext("Ban da gui tang " + code
								+ " thanh cong toi thue bao " + giff_userid
								+ ".");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));

					} else {
						msgObject.setUsertext(code + ":" + SERVER + "/?p="
								+ phone + "&c=" + code + "&f=" + type + "&g="
								+ GID);
						msgObject.setContenttype(8);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
					}
					
					return messages;
				}

			} else {
				// msgObject
				// .setUsertext("[ICom] Ma so ban tai khong hop le. Tang ban TOP
				// nhac:http://203.162.168.75:9090");
				saverequest(msgObject.getUserid(), TOP_CODE, type, GID);
				msgObject.setUsertext("Ma so ban tai khong hop le, tang ban:" + SERVER + "/?p="
						+ msgObject.getUserid() + "&c=" + TOP_CODE + "&f="
						+ type + "&g=" + GID);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(8);
				messages.add(new MsgObject(msgObject));

				return messages;

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

	private String validcode(String code, int gid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection("content");
			String query1 = "select code,filetype from icom_wap.dbo.upload  where (upper(code)='"
					+ code.toUpperCase()
					+ "'  or upper(code)='"
					+ code.toUpperCase()
					+ "P' ) and upper(filetype)='MEDIA' and cgroup=" + gid;

			// query1 = "select db_name()";

			//System.out.println(query1);
			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				String codetemp = (String) item.elementAt(0);

				return codetemp;
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbpool.cleanup(connection);

		}
		return TOP_CODE;
	}

	private static boolean saverequest(String userid, String code, String type,
			int gid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection("content");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into icom_wap.dbo.download( phone,code,filetype,cgroup) values ('"
					+ userid + "','" + code + "','" + type + "'," + gid + ")";
			Util.logger.error("Insert:" + sqlInsert);
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
		if ( sISDN.trim().length() < 8) {
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
					"sendGifMsgFailed");
		}
	}
}
