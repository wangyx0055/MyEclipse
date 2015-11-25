package wap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;

public class MusicWapnew extends QuestionManager {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	String SERVER = "http://www.mobinet.com.vn";
	String URL_INV = "http://www.mobinet.com.vn/?c=wap3";

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		Collection messages = new ArrayList();
		String VALID_CODE = "";
		String DBCONTENT = "content";
		String type = "media";
		String mtcdma = "Dich vu khong ho tro mang CDMA";
		int wappush = 8;

		int msg1mt = Integer.parseInt(Constants.MT_PUSH);
		int msg2mt = Integer.parseInt(Constants.MT_PUSH);

		if (keyword.getService_type() == Constants.PACKAGE_SERVICE) {
			msg1mt = Integer.parseInt(Constants.MT_CHARGING);
			msg2mt = Integer.parseInt(Constants.MT_PUSH);

		} else if (keyword.getService_type() == Constants.DAILY_SERVICE) {
			msg2mt = Integer.parseInt(Constants.MT_CHARGING);
			msg1mt = Integer.parseInt(Constants.MT_NOCHARGE);
		}
		HashMap _option = new HashMap();

		String options = keyword.getOptions();
		_option = getParametersAsString(options);

		// Lay cac thong so tu option
		try {

			type = getStringfromHashMap(_option, "type", type);
			SERVER = getStringfromHashMap(_option, "server", SERVER);
			Util.logger.info("Server: " + SERVER);
			URL_INV = getStringfromHashMap(_option, "url_inv", URL_INV);
			Util.logger.info("URL Invalid: " + URL_INV);
			DBCONTENT = getStringfromHashMap(_option, "dbcontent", DBCONTENT);
			mtcdma = getStringfromHashMap(_option, "mtcdma", mtcdma);
			GID = getOption(_option, GID);
			wappush = getOption1(_option, wappush);
		} catch (Exception e) {
			Util.logger.info(this.getClass().getName() + "Error: "
					+ e.toString());
			throw new Exception("Wrong config in options");
		}

		String mtinvsubcode = getStringfromHashMap(_option, "mtinvsubcode",
				"Ma so ban tai khong hop le. Tang ban TOP nhac:" + URL_INV);
		Util.logger.info("mtinvsubcode1: " + mtinvsubcode);

		// Tu doan nay

		if (msgObject.getMobileoperator().equalsIgnoreCase("EVN")
				|| msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
			msgObject.setUsertext(mtcdma);

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
		info = info.replace('/', ' ');
		String[] sTokens = replaceAllWhiteWithOne(info).split(" ");

		if (sTokens.length < 2) {
			Util.logger.info("mtinvsubcode2: " + mtinvsubcode);
			msgObject.setUsertext(mtinvsubcode);

			msgObject.setMsgtype(1);
			msgObject.setContenttype(8);
			messages.add(new MsgObject(msgObject));

			return messages;
		} else {

			// Tim vi tri so dien thoai
			int number = getValidPhone(sTokens);
			Util.logger.info("Number: " + number);

			if (number > 2) {

				// Co gui tang
				VALID_CODE = validcode(sTokens[2], GID, DBCONTENT, type);

				// Co bai hat trong he thong.
				if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
					String phone = msgObject.getUserid();
					String code = VALID_CODE;

					if (saverequest(phone, VALID_CODE, type, GID, DBCONTENT)) {

						String giff_userid = "";
						String giff_telco = "";
						boolean giff = false;

						if (sTokens.length > 2) {
							giff_userid = ValidISDNNew(sTokens[number]);
							if (!giff_userid.equalsIgnoreCase("-")) {
								giff_telco = getMobileOperatorNew(giff_userid,
										2);

								if ("-".equals(giff_telco)
										|| "EVN".equalsIgnoreCase(giff_telco)
										|| ("SFONE"
												.equalsIgnoreCase(giff_telco))) {
									giff = false;
								} else {
									giff = true;
								}
							}
						}

						if (giff == true) {
							// Sdt khach hang tang thoa man la 3 mang
							// viettel, mobifone, vinaphone
							MsgObject msgObj = new MsgObject(
									1,
									msgObject.getServiceid(),
									giff_userid,
									msgObject.getKeyword(),
									"Sodt " + msgObject.getUserid()
											+ " tang ban " + code + ":"
											+ SERVER + "/?p=" + phone + "&c="
											+ code + "&f=" + type + "&g=" + GID,
									msgObject.getRequestid(), DateProc
											.createTimestamp(), giff_telco, 0,
									wappush);
							// Gui thong tin cho nguoi duoc tang
							sendMT(msgObj, "MUSICWAP");

							msgObject.setUsertext("Ban da gui tang " + code
									+ " thanh cong toi thue bao " + giff_userid
									+ ".");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));
							return messages;

						} else {

							// Check xem mobi_operator cua khach hang
							if (msgObject.getMobileoperator().equalsIgnoreCase(
									"EVN")
									|| msgObject.getMobileoperator()
											.equalsIgnoreCase("SFONE")) {
								msgObject.setUsertext(mtcdma);
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								messages.add(new MsgObject(msgObject));
								return messages;
							}

							// Okie thi gui
							msgObject.setUsertext(code + ":" + SERVER + "/?p="
									+ phone + "&c=" + code + "&f=" + type
									+ "&g=" + GID);
							msgObject.setContenttype(8);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));
						}

						return messages;
					}

				} else {

					// BH khong co check tiep BH boi roi 0972
					String musicname = "";
					for (int i = 2; i < number; i++) {
						musicname = musicname + sTokens[i];
					}

					Util.logger.info("Music Name: " + musicname);
					VALID_CODE = validcode(musicname, GID, DBCONTENT, type);
					// VALID_CODE = validcode(musicname, GID);

					// Co bai hat trong he thong.
					if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
						String phone = msgObject.getUserid();
						String code = VALID_CODE;

						if (saverequest(phone, VALID_CODE, type, GID, DBCONTENT)) {

							String giff_userid = "";
							String giff_telco = "";
							boolean giff = false;

							if (sTokens.length > 2) {
								giff_userid = ValidISDNNew(sTokens[number]);
								if (!giff_userid.equalsIgnoreCase("-")) {
									giff_telco = getMobileOperatorNew(
											giff_userid, 2);

									if ("-".equals(giff_telco)
											|| "EVN"
													.equalsIgnoreCase(giff_telco)
											|| ("SFONE"
													.equalsIgnoreCase(giff_telco))) {
										giff = false;
									} else {
										giff = true;
									}
								}
							}

							if (giff == true) {

								MsgObject msgObj = new MsgObject(1, msgObject
										.getServiceid(), giff_userid, msgObject
										.getKeyword(), "Sodt "
										+ msgObject.getUserid() + " tang ban "
										+ code + ":" + SERVER + "/?p=" + phone
										+ "&c=" + code + "&f=" + type + "&g="
										+ GID, msgObject.getRequestid(),
										DateProc.createTimestamp(), giff_telco,
										0, wappush);
								// Gui thong tin cho nguoi duoc tang
								sendMT(msgObj, "MUSICWAP");

								msgObject.setUsertext("Ban da gui tang " + code
										+ " thanh cong toi thue bao "
										+ giff_userid + ".");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));

							} else {

								// Check xem mobi_operator cua khach hang
								if (msgObject.getMobileoperator()
										.equalsIgnoreCase("EVN")
										|| msgObject.getMobileoperator()
												.equalsIgnoreCase("SFONE")) {
									msgObject.setUsertext(mtcdma);
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									messages.add(new MsgObject(msgObject));
									return messages;
								}

								msgObject.setUsertext(code + ":" + SERVER
										+ "/?p=" + phone + "&c=" + code + "&f="
										+ type + "&g=" + GID);
								msgObject.setContenttype(8);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
							}

							return messages;
						}

					}

				}

			} else if ((number == 2) || (number == 1)) {

				// Khach hang nhan tin BH 0987654321
				msgObject.setUsertext(mtinvsubcode);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(8);
				messages.add(new MsgObject(msgObject));
				return messages;

			} else if (number == 0) {

				// Neu khong gui tang. Check xem Mobile_Operator cua khach
				// hang.
				if (msgObject.getMobileoperator().equalsIgnoreCase("EVN")
						|| msgObject.getMobileoperator().equalsIgnoreCase(
								"SFONE")) {
					msgObject.setUsertext(mtcdma);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				}

				// Neu thoa man la thuoc 3 mang Viettel, Mobi, Vina
				// Khong tang ai ca.Khach hang nhan BH boi roi. Check bai
				// boi truoc
				VALID_CODE = validcode(sTokens[2], GID, DBCONTENT, type);
				Util.logger.info("sTokens[2]: " + sTokens[2]);

				// Co bai hat trong he thong.
				if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
					String phone = msgObject.getUserid();
					String code = VALID_CODE;

					if (saverequest(phone, VALID_CODE, type, GID, DBCONTENT)) {

						msgObject.setUsertext(code + ":" + SERVER + "/?p="
								+ phone + "&c=" + code + "&f=" + type + "&g="
								+ GID);
						msgObject.setContenttype(8);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));

						return messages;
					}
				} else {
					String musicname = "";
					for (int i = 2; i < sTokens.length; i++) {
						musicname = musicname + sTokens[i];
					}
					// Valid toan bo
					VALID_CODE = validcode(musicname, GID, DBCONTENT, type);
					Util.logger.info("Music Name: " + musicname);

					if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
						String phone = msgObject.getUserid();
						String code = VALID_CODE;

						if (saverequest(phone, VALID_CODE, type, GID, DBCONTENT)) {

							msgObject.setUsertext(code + ":" + SERVER + "/?p="
									+ phone + "&c=" + code + "&f=" + type
									+ "&g=" + GID);
							msgObject.setContenttype(8);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));

							return messages;
						}
					} else {
						msgObject.setUsertext(mtinvsubcode);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(8);
						messages.add(new MsgObject(msgObject));
						return messages;
					}
				}
			}

		}

		return messages;
	}

	private static int sendMT(MsgObject msgObject, String sclassname) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error(sclassname + "@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info(sclassname + "@sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@sendMT: Error connection == null"
						+ msgObject.getUserid() + "@TO"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
				Util.logger.crisis(sclassname + "@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());

				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());

			Util.logger.printStackTrace(e);

			return -1;
		} catch (Exception e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

	private String validcode(String code, int gid, String dbcontent,
			String ftype) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection(dbcontent);
			String query1 = "select code,filetype from upload where (upper(code)='"
					+ code.toUpperCase()
					+ "'  or upper(code)='"
					+ code.toUpperCase()
					+ "P' ) AND upper(filetype)='"
					+ ftype.toUpperCase().trim() + "' and cgroup=" + gid;

			Util.logger.info("Query : " + query1);
			// System.out.println(query1);
			Vector result = DBUtil.getVectorTable(connection, query1);
			Util.logger.info("Result size: " + result.size());
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				String codetemp = (String) item.elementAt(0);

				return codetemp;
			}

		} catch (Exception e) {
			Util.logger.error("Error 2:" + e.getMessage());
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(connection);

		}
		return INVALID_CODE;
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
			Util.logger.info("MobileOperator: Get MobileOpereator Failed"
					+ ex.toString());
			return tmpOperator;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	private static boolean saverequest(String userid, String code, String type,
			int gid, String dbcontent) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection(dbcontent);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			
			String sqlInsert = "Insert into download( phone,code,filetype,cgroup) values ('"
					+ userid + "','" + code + "','" + type + "'," + gid + ")";

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

	private String getOption(HashMap _option1, String defaultvalue) {
		try {
			return ((String) _option1.get("gid")).toUpperCase();
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	public int getOption(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("gid"));
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	public int getOption1(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("int"));
		} catch (Exception e) {
			return defaultvalue;
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

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));
			if ((temp == null) || "".equalsIgnoreCase(temp)
					|| "null".equalsIgnoreCase(temp)) {
				return _defaultval;
			}

			return temp;
		} catch (Exception e) {
			return _defaultval;
		}
	}

	// Find Giff Phone
	private static int getValidPhone(String[] sTokens) {
		int place = 0;
		for (int i = 0; i < sTokens.length; i++) {

			if (!"-".equalsIgnoreCase(getMobileOperatorNew(
					(ValidISDNNew(sTokens[i])), 2))) {
				place = i;
				return place;
			}
		}
		return place;
	}

}
