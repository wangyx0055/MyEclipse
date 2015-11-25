package Longcheer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import services.textbases.LogValues;
import services.textbases.VnnLinksTextbaseTicket;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.SoapLongcheerMO;

import cs.ExecuteADVCR;

public class TextbaseQM extends ContentAbstract {

	private String directory = "/home/oracle/Process/textbase/ticket";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		try {

			directory = Constants._prop.getProperty("dirtextbase", directory);

			HashMap _option = new HashMap();
			Collection messages = new ArrayList();

			String options = keyword.getOptions();
			String GAME_ID = "";
			int NUM_SUBCODE = 0;
			String MTCS = "0";
			String GAME_TYPE = "";
			String GIFF_MSG = "";
			String INV_MSG = "";
			String INV_SUBCODE_MSG = "";
			String SUBCODE1 = "";
			String SUBCODE2 = "";
			String SUBCODETYPE = "0";
			int NUM_DAY = 0;
			int NUM_MINUTE_DELAY = 0;
			int NUM_CNT = 0;
			String GIFF_USER = "-";
			String GIFF_USER_TELCO = "-";

			String service = msgObject.getKeyword();
			try {
				// options =
				// "game_id=sinhcontrai&num_subcode=0&num_minute=1&num_day=6&mtcs=&game_type=2&subcodetype=0&num_cnt=0&giff_msg=Ban
				// da gui tang thanh cong&inv_msg=Ban da nhan sai cu phap";
				// Num_cnt: so luong noi dung, cho truong hop quy doi subcode
				// sang
				// so
				// subcodetype: kieu subcode, 1:binh thuong, 2: kieu cong tong
				// so,
				// 3: doi chu cai sang so
				// sao cho tong cac so < num_cnt( neu num_cnt =0 thi chi cong 1
				// lan)
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);

				// Phan tich option
				// gameid=ddd&numsubcode=0&

				GAME_ID = ((String) _option.get("game_id")).toUpperCase();
				NUM_SUBCODE = Integer.parseInt((String) _option
						.get("num_subcode"));
				NUM_DAY = Integer.parseInt((String) _option.get("num_day"));
				NUM_MINUTE_DELAY = Integer.parseInt((String) _option
						.get("num_minute"));
				// num_minute
				MTCS = (String) _option.get("mtcs");
				GAME_TYPE = (String) _option.get("game_type");// 1: tra hang
				// ngay
				// khong theo thu
				// tu, 2: tra hang
				// ngay theo thu tu
				GIFF_MSG = (String) _option.get("giff_msg");// Thong bao gui
				// tang:
				// Ban da gui tang thanh
				// cong den thue bao
				// $GIFF_USER
				INV_MSG = (String) _option.get("inv_msg");
				INV_SUBCODE_MSG = (String) _option.get("inv_subcode_msg");
				SUBCODETYPE = ((String) _option.get("subcodetype"))
						.toUpperCase();
				NUM_CNT = Integer.parseInt((String) _option.get("num_cnt"));

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}
			// ////////

			String motext = msgObject.getUsertext().replaceAll("'", "");

			String sInfo = msgObject.getUsertext();
			sInfo = replaceAllWhiteWithOne(sInfo.trim()).trim().toUpperCase();
			String sKeyword = msgObject.getKeyword();
			sKeyword = sKeyword.toUpperCase();
			String sServiceid = msgObject.getServiceid();
			String USERID = msgObject.getUserid();

			String[] sTokens = sInfo.split(" ");

			String CTX_MTCONTENT = "";
			Util.logger.sysLog(2, this.getClass().getName(), "sToken: "
					+ sTokens.length);
			Util.logger.sysLog(2, this.getClass().getName(), "NUM_SUBCODE: "
					+ NUM_SUBCODE);
			if (SoapLongcheerMO.getMessages(msgObject)) {
				Util.logger.info("OK");
			} else

				Util.logger.info("Chua post duoc");
			if ((NUM_SUBCODE + 1) > sTokens.length) {
				// Tin nhan sai cu phap
				CTX_MTCONTENT = INV_MSG;
				Util.logger.sysLog(2, this.getClass().getName(),
						"Tin nhan sai cu phap");

				msgObject.setUsertext(CTX_MTCONTENT);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				return messages;
			} else {

				// Kiem tra xem co phai gui tang hay khong?
				if (sTokens.length > (NUM_SUBCODE + 1)) {
					Util.logger.sysLog(2, this.getClass().getName(),
							"Kiem tra xem co phai gui tang hay khong?*"
									+ sTokens[NUM_SUBCODE + 1] + "*");
					GIFF_USER = ValidISDNNew(sTokens[NUM_SUBCODE + 1]);
					Util.logger.sysLog(2, this.getClass().getName(),
							"GIFF_USER?*" + GIFF_USER + "*");
					if (!"-".equalsIgnoreCase(GIFF_USER)) {
						GIFF_USER_TELCO = getMobileOperatorNew(GIFF_USER, 2);
					}
					Util.logger.sysLog(2, this.getClass().getName(),
							"GIFF_USER_TELCO?*" + GIFF_USER_TELCO + "*");
				}
			}

			if (NUM_SUBCODE == 1) {
				SUBCODE1 = sTokens[1];
			}
			if (NUM_SUBCODE == 2) {
				SUBCODE1 = sTokens[1];
				SUBCODE2 = sTokens[2];
			}

			String[] ALLCONTENTS = new String[NUM_DAY];

			ALLCONTENTS = getAllcontent(GAME_ID, SUBCODE1, SUBCODE2, NUM_DAY,
					GAME_TYPE, SUBCODETYPE, NUM_CNT);

			Util.logger.sysLog(2, this.getClass().getName(),
					"ALLCONTENTS.length: " + ALLCONTENTS.length);

			try {

				if (ALLCONTENTS.length > 1) {
					// Tao ticket
					String userticket = "";
					String telcoticket = "";
					if ("-".equalsIgnoreCase(GIFF_USER_TELCO)) {
						userticket = USERID;
						telcoticket = msgObject.getMobileoperator();
					} else {
						userticket = GIFF_USER;
						telcoticket = GIFF_USER_TELCO;
					}
					for (int i = 1; i < ALLCONTENTS.length; i++) {
						if (i == 1) {
							continue;
						}

						String[] mt2send = ALLCONTENTS[i].split("###");
						String date2send = getTimebyMinute((i - 1)
								* NUM_MINUTE_DELAY);

						Util.logger.sysLog(2, this.getClass().getName(),
								"ALLCONTENTS.length: " + ALLCONTENTS.length);

						for (int j = 0; j < mt2send.length; j++) {
							if (!"".equalsIgnoreCase(mt2send[j])) {
								Util.logger.sysLog(2,
										this.getClass().getName(),
										"createReservationTicket: "
												+ mt2send[j]);
								VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
										.createReservationTicket(mt2send[j],
												userticket, sServiceid,
												date2send, telcoticket,
												telcoticket, service, msgObject
														.getRequestid()
														.toString());
								VnnLinksTextbaseTicket.storeReservationTicket(
										ticket, directory, userticket);
							}
						}

					}
				}

				if ("-".equalsIgnoreCase(GIFF_USER_TELCO)) {
					CTX_MTCONTENT = ALLCONTENTS[1];

				} else {
					CTX_MTCONTENT = GIFF_MSG;
					String[] mt2send = ALLCONTENTS[1].split("###");
					for (int i = 0; i < mt2send.length; i++) {
						if (!"".equalsIgnoreCase(mt2send[i])) {
							sendGifMsg(sServiceid, GIFF_USER, GIFF_USER_TELCO,
									service, mt2send[i], msgObject
											.getRequestid());
						}
					}

				}

				if ("".equalsIgnoreCase(ALLCONTENTS[1])) {
					CTX_MTCONTENT = INV_SUBCODE_MSG;
				}

				Util.logger.sysLog(2, this.getClass().getName(),
						"CTX_MTCONTENT:" + CTX_MTCONTENT);

				String[] mt2send = CTX_MTCONTENT.split("###");

				for (int i = 0; i < mt2send.length; i++) {
					if (!"".equalsIgnoreCase(mt2send[i])) {
						msgObject.setUsertext(mt2send[i]);
						if (i == 0) {
							msgObject.setMsgtype(1);

						} else {
							msgObject.setMsgtype(0);
						}
						messages.add(new MsgObject(msgObject));
					}
				}
			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
						+ e.getMessage());
			}

			return messages;

		} catch (Exception e) {
			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			e.printStackTrace();
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

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
		Util.logger.sysLog(2, this.getClass().getName(), "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.sysLog(2, this.getClass().getName(), "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "+" + tempisdn;
			} else {
				tempisdn = "+84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.sysLog(2, this.getClass().getName(), "Exception?*"
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

	public int getNumber(String subcode, int index) {
		try {
			return Integer.parseInt(subcode.substring(index, index + 1));
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	public String convertsubcode(String subcode, String typesubcode, int numcnt) {
		if ("1".equalsIgnoreCase(typesubcode)) {
			return subcode.toUpperCase();
		} else if ("2".equalsIgnoreCase(typesubcode)) {
			int sum = 0;
			for (int i = 0; i < subcode.length(); i++) {
				sum += getNumber(subcode, i);
			}
			if (numcnt == 0) {
				return sum + "";
			} else {

				while (sum > numcnt) {
					String ssum = sum + "";
					sum = 0;
					for (int i = 0; i < ssum.length(); i++) {
						sum += getNumber(ssum, i);
					}
				}
				return sum + "";
			}
		} else if ("3".equalsIgnoreCase(typesubcode)) {
			int sum = 0;
			for (int i = 0; i < subcode.length(); i++) {
				if ((subcode.substring(i, i + 1).equalsIgnoreCase("A"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("J"))) {
					sum += 1;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("B"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("K"))) {
					sum += 2;
				}

				else if ((subcode.substring(i, i + 1).equalsIgnoreCase("C"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("L"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("U"))) {
					sum += 3;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("D"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("M"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("V"))) {
					sum += 4;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("E"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("N"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("W"))) {
					sum += 5;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("F"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("O"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("X"))) {
					sum += 6;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("G"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("P"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Y"))) {
					sum += 7;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("H"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Q"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Z"))) {
					sum += 8;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("I"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("R"))) {
					sum += 9;
				} else {
					sum += 0;
				}

			}
			if (numcnt == 0) {
				return sum + "";
			}

			while (sum > numcnt) {
				String ssum = sum + "";
				sum = 0;
				for (int i = 0; i < ssum.length(); i++) {
					sum += getNumber(ssum, i);
				}
			}
			return sum + "";
		}
		return subcode.toUpperCase();
	}

	public String getsubode(int idsubcode, String subcode1, String subcode2,
			String subcodetype, int numcnt) {
		if (idsubcode == 1) {
			if (!"".equalsIgnoreCase(subcode2)) {
				return subcode1.toUpperCase();
			} else {
				return convertsubcode(subcode1, subcodetype, numcnt);
			}

		} else {
			return convertsubcode(subcode2, subcodetype, numcnt);
		}
	}

	public String[] getAllcontent(String gameid, String subcode1,
			String subcode2, int numday, String gametype, String subcodetype,
			int numcnt) {

		// Lay noi dung tra luon
		String lastid = "";
		String[] cnttemp = new String[numday + 1];
		String sqlSELECT = "SELECT CONTENT,ID FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid + "'";

		if (!"".equalsIgnoreCase(subcode1)) {
			sqlSELECT = sqlSELECT + " AND upper(subcode1)='"
					+ getsubode(1, subcode1, subcode2, subcodetype, numcnt)
					+ "'";
		}

		if (!"".equalsIgnoreCase(subcode2)) {
			sqlSELECT = sqlSELECT + " AND upper(subcode2)='"
					+ getsubode(2, subcode1, subcode2, subcodetype, numcnt)
					+ "'";

		}

		String sqlORDER = "  order by rand() limit 1";

		for (int i = 1; i < numday + 1; i++) {
			String sqltemp = sqlSELECT;
			if ("2".equalsIgnoreCase(gametype)) {
				sqltemp = sqltemp + "  and dayid =" + i;

			}
			if (!"".equalsIgnoreCase(lastid)) {
				sqltemp = sqltemp + " and id not in(" + lastid + ") ";
			}
			sqltemp = sqltemp + sqlORDER;
			String[] temp = getContent(sqltemp);
			cnttemp[i] = temp[0];
			if (!"".equalsIgnoreCase(temp[1])) {
				if ("".equalsIgnoreCase(lastid)) {
					lastid = temp[1];
				} else {
					lastid = lastid + "," + temp[1];
				}
			}

		}

		return cnttemp;
	}

	String[] getContent(String query) {
		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.sysLog(1, this.getClass().getName(),
					"getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"getContent: Failed" + ex.getMessage());
			ex.printStackTrace();
			return record;
		} finally {
			dbpool.cleanup(connection);
		}

	}

	String getTimebyMinute(int iminute) {
		long milliSecond = System.currentTimeMillis();

		long lNewtime = milliSecond + iminute * 60 * 1000;

		// /* return date with format: dd/mm/yyyy */
		if (iminute < 24 * 60) {
			return Milisec2YYYYMMDDHHMISSS(lNewtime);
		} else {
			return Milisec2YYYYMMDD080000(lNewtime);
		}

	}

	// return a string in the following format "yyyy/mm/dd hh:mm:ss"
	public static String Milisec2YYYYMMDDHHMISSS(long ts) {
		if (ts == 0) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts));
			return getCalendarString(calendar);

		}
	}

	/**
	 * This method returns a string with the date in the following format
	 * "yyyy/mm/dd hh:mm:ss". This date represents the Calendar ogject passed as
	 * a parameter.
	 * 
	 * @param Calendar
	 *            calendar The Calendar object
	 * @return a string in the following format "yyyy/mm/dd hh:mm:ss"
	 * 
	 */
	public static String getCalendarString(Calendar calendar) {
		StringBuffer sb = new StringBuffer();
		int i;

		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		i = calendar.get(Calendar.MONTH) + 1;
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append("-");
		i = calendar.get(Calendar.DAY_OF_MONTH);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(" ");
		i = calendar.get(Calendar.HOUR_OF_DAY);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(":");
		i = calendar.get(Calendar.MINUTE);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(":");
		i = calendar.get(Calendar.SECOND);
		if (i < 10)
			sb.append("0");
		sb.append(i);

		return (sb.toString());
	}

	// return a string in the following format "yyyy/mm/dd hh:mm:ss"
	public static String Milisec2YYYYMMDD080000(long ts) {
		if (ts == 0) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts));
			return getCalendarString080000(calendar);

		}
	}

	public static String getCalendarString080000(Calendar calendar) {
		StringBuffer sb = new StringBuffer();
		int i;

		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		i = calendar.get(Calendar.MONTH) + 1;
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append("-");
		i = calendar.get(Calendar.DAY_OF_MONTH);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(" 08:00:00");

		return (sb.toString());
	}

	private void sendGifMsg(String serviceid, String userid, String operator,
			String service, String mtcontent, BigDecimal requestid) {
		try {

			MsgObject msg = new MsgObject();
			msg.setContenttype(0);
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

	private long getBalanc(String userid) {
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

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);

			tempisdn = "" + itemp;
			if (sISDN.trim().length() < 8) {
				return "-";
			}
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("Utils.ValidISDN" + "Exception?*" + e.toString()
					+ "*");
			return "-";
		}
		return tempisdn;
	}

	public static String getMobileOperatorNew(String userid, int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("gateway");

			String query = "Select operator from icom_isdnseries where prefix= substr('"
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

}
