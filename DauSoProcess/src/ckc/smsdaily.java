package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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

import cs.ExecuteADVCR;

public class smsdaily extends ContentAbstract {

	// Nơi lưu trữ các ticket
	private String directory = "C:/ticket";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		int MILI_SECOND = 1000 * 60 * 60 * 24;

		try {

			directory = Constants._prop.getProperty("dirtextbase", directory);
			HashMap _option = new HashMap();

			String options = keyword.getOptions();
			_option = getParametersAsString(options);

			String GAME_ID = "";
			String GIFF_USER = "-";
			String GIFF_USER_TELCO = "-";
			String DBCONTENT = "gateway";
			String GIFF_MSG = "";
			String INV_MSG = "";
			String sKeyword = "";
			String serviceid = "";
			String USERID = msgObject.getUserid();
			String SUCCESS_MSG = "";
			BigDecimal requestid;
			String GAME_TYPE = "";
			int NUM_DAY = 0;
			String lastSMS = "lastSMS";

			try {

				GAME_ID = ((String) _option.get("game_id")).toUpperCase();
				GIFF_MSG = (String) _option.get("giff_msg");
				INV_MSG = (String) _option.get("inv_msg");
				DBCONTENT = getString(_option, "dbcontent", DBCONTENT);
				Util.logger.info("Dbcontent: " + DBCONTENT);
				SUCCESS_MSG = ((String) _option.get("success_msg"));
				GAME_TYPE = (String) _option.get("game_type");
				NUM_DAY = Integer.parseInt((String) _option.get("num_day"));
				lastSMS = getString(_option, "lastsms", lastSMS);

				sKeyword = msgObject.getKeyword();
				serviceid = msgObject.getServiceid();
				requestid = msgObject.getRequestid();

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			// Lay noi dung khach hang gui toi
			String info = msgObject.getUsertext();
			info = replaceAllWhiteWithOne(info);
			String[] sTokens = info.split(" ");
			Util.logger.info("Length: " + sTokens.length);

			//
			String info2Client = "Ban da gui tin nhan sai cu phap.De gui loi chuc toi nguoi than, ban be trong 7 ngay.Soan tin "
					+ sKeyword
					+ " X Y gui "
					+ serviceid
					+ ".Trong do X la thoi gian se gui.Y la sdt nguoi nhan";

			if (sTokens.length < 3) {
				// Thong bao khach hang gui tin nhan sai cu phap

				msgObject.setUsertext(info2Client);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;

			} else {

				// Lay cac thong tin cua nguoi nhan
				String timeSend = sTokens[1];
				GIFF_USER = ValidISDNNew(sTokens[2]);
				Util.logger.info("Giff User:" + GIFF_USER);
				if (!"-".equalsIgnoreCase(GIFF_USER)) {
					GIFF_USER_TELCO = getMobileOperatorNew(GIFF_USER, 2);
				}

				// Kiem tra xem thoi gian gui co dung hay khong ?
				String time = validateTimesend(timeSend);
				Util.logger.info("Times: " + time);
				if ("-".equalsIgnoreCase(time)) {

					msgObject.setUsertext(info2Client);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {
					Util.logger.info("Giff User Telco: " + GIFF_USER_TELCO);
					// Neu GIFF_USER_TELCO sai
					if ("-".equalsIgnoreCase(GIFF_USER_TELCO)) {

						msgObject.setUsertext(info2Client);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;
					} else {

						// Gui tin nhan thue bao XXX da gui thanh cong den thue
						// bao YYY
						String reply = GIFF_MSG.replace("#1", GIFF_USER);
						msgObject.setUsertext(reply);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));

						// Gui 1 tin toi khach hang dc tang
						SUCCESS_MSG = SUCCESS_MSG.replace("#1", USERID);
						sendGifMsg(serviceid, GIFF_USER, GIFF_USER_TELCO
								.toUpperCase(), sKeyword, SUCCESS_MSG,
								requestid);

						// Lay noi dung va render vao ticket theo tung ngay
						// trong tuan.

						String subcode = daynight(time);
						String smsdaily = validKeyword(sKeyword);
						String gameid = GAME_ID + smsdaily;

						// Lay noi dung Content
						String[] ALLCONTENTS = new String[NUM_DAY + 1];
						ALLCONTENTS = getAllcontent(gameid, subcode, NUM_DAY,
								GAME_TYPE, DBCONTENT);

						Timestamp timeReceived = msgObject.getTTimes();

						if (ALLCONTENTS.length > 1) {
							if (isNewSession(time)) {

								// Tao ticket
								for (int i = 1; i < ALLCONTENTS.length; i++) {
									String content = ALLCONTENTS[i];
									String[] mt2send = content.split("###");

									String date2send = "";
									// Lấy sau thời gian gửi tin 1 ngày
									Date date = new Date(timeReceived.getTime()
											+ MILI_SECOND * i);
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(date);
									date2send = getCalendarString(calendar,
											time);

									Util.logger.sysLog(2, this.getClass()
											.getName(), "ALLCONTENTS.length: "
											+ ALLCONTENTS.length);

									Util.logger.info("mt2send : "
											+ mt2send.length);

									for (int j = 0; j < mt2send.length; j++) {

										if (!" ".equalsIgnoreCase(mt2send[j])) {
											Util.logger.sysLog(2, this
													.getClass().getName(),
													"createReservationTicket: "
															+ mt2send[j]);

											VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
													.createReservationTicket(
															mt2send[j],
															GIFF_USER,
															serviceid,
															date2send,
															GIFF_USER_TELCO,
															GIFF_USER_TELCO,
															sKeyword,
															msgObject
																	.getRequestid()
																	.toString());

											VnnLinksTextbaseTicket
													.storeReservationTicket(
															ticket, directory,
															GIFF_USER);
										}
									}
								}

								// Ticket cho khach hang dang ky su dung dich
								// vu.
								// Lay noi dung tu textbase
								String lastcontentSMS = getLastSMS(lastSMS);
								String date2send = "";
								// Lấy thoi gian gui cuoi cung
								Date date = new Date(timeReceived.getTime()
										+ MILI_SECOND
										* (ALLCONTENTS.length - 1));
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								date2send = getCalendarString(calendar, time);

								if (!"".equals(lastcontentSMS)) {
									Util.logger.sysLog(2, this.getClass()
											.getName(),
											"createReservationTicket: "
													+ lastSMS);

									VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
											.createReservationTicket(
													lastcontentSMS,
													USERID,
													serviceid,
													date2send,
													msgObject
															.getMobileoperator(),
													msgObject
															.getMobileoperator(),
													sKeyword, msgObject
															.getRequestid()
															.toString());

									VnnLinksTextbaseTicket
											.storeReservationTicket(ticket,
													directory, USERID);
								}

							} else {

								for (int i = 1; i < ALLCONTENTS.length; i++) {
									String content = ALLCONTENTS[i];
									Util.logger.info("content : " + content);
									String[] mt2send = content.split("###");

									String date2send = "";

									// Chua den gio.
									Date date = new Date(timeReceived.getTime()
											+ MILI_SECOND * (i - 1));
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(date);
									date2send = getCalendarString(calendar,
											time);

									Util.logger.sysLog(2, this.getClass()
											.getName(), "ALLCONTENTS.length: "
											+ ALLCONTENTS.length);

									Util.logger.info("mt2send : "
											+ mt2send.length);

									for (int j = 0; j < mt2send.length; j++) {

										if (!" ".equalsIgnoreCase(mt2send[j])) {
											Util.logger.sysLog(2, this
													.getClass().getName(),
													"createReservationTicket: "
															+ mt2send[j]);

											VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
													.createReservationTicket(
															mt2send[j],
															GIFF_USER,
															serviceid,
															date2send,
															GIFF_USER_TELCO,
															GIFF_USER_TELCO,
															sKeyword,
															msgObject
																	.getRequestid()
																	.toString());

											VnnLinksTextbaseTicket
													.storeReservationTicket(
															ticket, directory,
															GIFF_USER);
										}
									}
								}

								// Ticket cho khach hang dang ky su dung dich
								// vu.
								// Lay noi dung tu textbase
								String lastcontentSMS = getLastSMS(lastSMS);
								String date2send = "";
								// Lấy thoi gian gui cuoi cung.
								Date date = new Date(timeReceived.getTime()
										+ MILI_SECOND
										* (ALLCONTENTS.length - 2));
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								date2send = getCalendarString(calendar, time);

								if (!"".equals(lastcontentSMS)) {
									Util.logger.sysLog(2, this.getClass()
											.getName(),
											"createReservationTicket: "
													+ lastSMS);

									VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
											.createReservationTicket(
													lastcontentSMS,
													USERID,
													serviceid,
													date2send,
													msgObject
															.getMobileoperator(),
													msgObject
															.getMobileoperator(),
													sKeyword, msgObject
															.getRequestid()
															.toString());

									VnnLinksTextbaseTicket
											.storeReservationTicket(ticket,
													directory, USERID);
								}
							}
						}
					}
				}
			}
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
		return messages;
	}

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);

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

	String[] getContent(String query, String dbcontent) {
		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(dbcontent);
			Util.logger.info("dbcontent is : " + dbcontent);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.sysLog(1, this.getClass().getName(),
					"getContent: queryStatement:" + query);

			if (result.size() > 0) {

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

	public static String getMobileOperatorNew(String userid, int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("gateway");

			String query = "SELECT operator FROM icom_isdnseries WHERE prefix= substr('"
					+ userid + "',1, length(prefix)) ";
			Util.logger.info("Query: " + query);
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

	// Chuyen dinh dang thoi gian.
	public static String validateTimesend(String timeSend) {
		String result = "-";
		try {

			if (timeSend.length() != 4) {
				return result;
			}
			int time = Integer.parseInt(timeSend);
			if ((time < 0) || (time > 2400)) {
				return result;
			} else {
				result = timeSend.substring(0, 2) + ":" + timeSend.substring(2)
						+ ":00";
				return result;
			}

		} catch (Exception e) {
			return result;
		}
	}

	public static String daynight(String timesend) {
		String result = "day";
		String[] sContent = timesend.split(":");

		int iHour = Integer.parseInt(sContent[0]);
		Util.logger.info("iHour: " + iHour);

		int iMinute = Integer.parseInt(sContent[1]);
		Util.logger.info("iMinute: " + iMinute);

		if ((2 < iHour) && (iHour < 19)) {
			return result;
		}

		if (((iHour == 2) && (iMinute == 0))
				|| ((iHour == 19) && (iMinute == 0))) {
			return result;
		}

		return "night";
	}

	public static String validKeyword(String keyword) {
		if (keyword.startsWith("GX")) {
			return "GX";
		} else {
			return "GY";
		}
	}

	public String[] getAllcontent(String gameid, String subcode1, int numday,
			String gametype, String dbcontent) {

		// Lay noi dung tra luon
		String lastid = "";
		String[] cnttemp = new String[numday + 1];
		String sqlSELECT = "SELECT content, id FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid
				+ "' AND upper(subcode1) = '"
				+ subcode1.toUpperCase()
				+ "'";

		String sqlORDER = " order by rand() limit 1";

		for (int i = 1; i < numday + 1; i++) {

			String sqltemp = sqlSELECT;
			if ("2".equalsIgnoreCase(gametype)) {
				sqltemp = sqltemp + " and dayid =" + i;
			}
			if (!"".equalsIgnoreCase(lastid)) {
				sqltemp = sqltemp + " and id not in(" + lastid + ") ";
			}
			sqltemp = sqltemp + sqlORDER;
			String[] temp = getContent(sqltemp, dbcontent);
			cnttemp[i] = temp[0];
			Util.logger.info("Temp :" + temp[0]);
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

	public static boolean isNewSession(String time) {
		String sTime2Queue = time;

		String[] arrH = new String[20];
		int iHour = 0;
		int iMinute = 0;
		arrH = sTime2Queue.split(":");
		if (arrH.length > 1) {
			iHour = Integer.parseInt(arrH[0].trim());
			iMinute = Integer.parseInt(arrH[1].trim());
		} else {
			iHour = Integer.parseInt(arrH[0].trim());

		}
		long milliSecond = System.currentTimeMillis();
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.setTime(new java.util.Date(milliSecond));
		if (((calendar.get(calendar.HOUR_OF_DAY) == iHour) && (calendar
				.get(calendar.MINUTE) >= iMinute))
				|| ((calendar.get(calendar.HOUR_OF_DAY) > iHour))) {
			return true;
		}
		return false;
	}

	// Lay time hien tai
	public static String getCalendarString(Calendar calendar, String time) {
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
		sb.append(time);

		return (sb.toString());
	}

	public String getLastSMS(String gameid) {
		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();
			String query1 = "select content from icom_textbase_data where upper(gameid) = '"
					+ gameid.toUpperCase() + "'";
			stmt = connection.prepareStatement(query1);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					content = rs.getString(1);
				}
			}
			return content;
		} catch (SQLException e) {
			System.out.println(e);
			return content;
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(rs);
			dbpool.cleanup(connection);

		}
	}
}
