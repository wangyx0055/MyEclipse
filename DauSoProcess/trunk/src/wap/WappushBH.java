package wap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

/**
 * 
 * @version 2.0
 */
public class WappushBH extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	String SERVER = "http://mobinet.com.vn";
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

			GID = getOption(_option, 0);
			String mtcdma = getString(_option, "mtcdma",
					"Hien tai dich vu chua ho tro mang cua ban.Vui long quay tro lai sau.DTHT 1900571566");
			String mtinvsubcode = getString(_option, "mtinvsubcode",
					"Ma so ban tai khong hop le. Tang ban TOP nhac:" + URL_INV);
			SERVER = getString(_option, "server", SERVER);

			String type = getString(_option, "type", "media");

			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			if (msgObject.getMobileoperator()
					.equalsIgnoreCase("EVN")
					|| msgObject.getMobileoperator()
							.equalsIgnoreCase("SFONE")) {
				msgObject.setUsertext(mtcdma);
				msgObject.setMsgtype(2);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			if (sTokens.length == 1) {
				msgObject.setUsertext(mtinvsubcode);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(8);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {

				// Tim vi tri so dien thoai
				int number = getValidPhone(sTokens);
				Util.logger.info("Number: " + number);

				if (number > 1) {

					// Co gui tang
					Util.logger.info("sTokens[1]: " + sTokens[1]);
					VALID_CODE = validcode(sTokens[1], GID);

					// Co bai hat trong he thong.
					if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
						String phone = msgObject.getUserid();
						String code = VALID_CODE;

						if (saverequest(phone, VALID_CODE, type, GID)) {

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
								// Sdt khach hang tang thoa man la 3 mang
								// viettel, mobifone, vinaphone
								sendGifMsg(msgObject.getServiceid(),
										giff_userid, giff_telco, msgObject
												.getKeyword(), "Sodt "
												+ msgObject.getUserid()
												+ " tang ban " + code + ":"
												+ SERVER + "/?p=" + phone
												+ "&c=" + code + "&f=" + type
												+ "&g=" + GID, msgObject
												.getRequestid(), 8);

								msgObject.setUsertext("Ban da gui tang " + code
										+ " thanh cong toi thue bao "
										+ giff_userid + ".");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
								return messages;

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

								// Okie thi gui
								msgObject.setUsertext(code + ":" + SERVER
										+ "/?p=" + phone + "&c=" + code + "&f="
										+ type + "&g=" + GID);
								msgObject.setContenttype(8);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
							}

							return messages;
						}

					} else {
						
						// BH khong co check tiep BH boi roi 0972
						String musicname = "";
						for (int i = 1; i < number; i++) {
							musicname = musicname + sTokens[i];
						}

						Util.logger.info("Music Name: " + musicname);
						VALID_CODE = validcode(musicname, GID);

						// Co bai hat trong he thong.
						if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
							String phone = msgObject.getUserid();
							String code = VALID_CODE;

							if (saverequest(phone, VALID_CODE, type, GID)) {

								String giff_userid = "";
								String giff_telco = "";
								boolean giff = false;

								if (sTokens.length > 2) {
									giff_userid = ValidISDN(sTokens[number]);
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

									sendGifMsg(msgObject.getServiceid(),
											giff_userid, giff_telco, msgObject
													.getKeyword(), "Sodt "
													+ msgObject.getUserid()
													+ " tang ban " + code + ":"
													+ SERVER + "/?p=" + phone
													+ "&c=" + code + "&f="
													+ type + "&g=" + GID,
											msgObject.getRequestid(), 8);

									msgObject.setUsertext("Ban da gui tang "
											+ code
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
											+ "/?p=" + phone + "&c=" + code
											+ "&f=" + type + "&g=" + GID);
									msgObject.setContenttype(8);
									msgObject.setMsgtype(1);
									messages.add(new MsgObject(msgObject));
								}

								return messages;
							}

						}

					}

				} else if (number == 1) {

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
					VALID_CODE = validcode(sTokens[1], GID);
					Util.logger.info("sTokens[1]: " + sTokens[1]);

					// Co bai hat trong he thong.
					if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
						String phone = msgObject.getUserid();
						String code = VALID_CODE;

						if (saverequest(phone, VALID_CODE, type, GID)) {

							msgObject.setUsertext(code + ":" + SERVER + "/?p="
									+ phone + "&c=" + code + "&f=" + type
									+ "&g=" + GID);
							msgObject.setContenttype(8);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));

							return messages;
						}
					} else {
						String musicname = "";
						for (int i = 1; i < sTokens.length; i++) {
							musicname = musicname + sTokens[i];
						}
						// Valid toan bo
						VALID_CODE = validcode(musicname, GID);
						Util.logger.info("Music Name: " + musicname);

						if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
							String phone = msgObject.getUserid();
							String code = VALID_CODE;

							if (saverequest(phone, VALID_CODE, type, GID)) {

								msgObject.setUsertext(code + ":" + SERVER
										+ "/?p=" + phone + "&c=" + code + "&f="
										+ type + "&g=" + GID);
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

	// Check xem bai hat co trong danh sach khong?
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

			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				String codetemp = (String) item.elementAt(0);

				return codetemp;
			}

		} catch (Exception e) {
			return INVALID_CODE;
		} finally {
			dbpool.cleanup(connection);

		}
		return INVALID_CODE;
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
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
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

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		if (sISDN.trim().length() < 8) {
			return "-";
		}
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

	// Find Giff Phone
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
}
