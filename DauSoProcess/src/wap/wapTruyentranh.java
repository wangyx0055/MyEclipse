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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import cs.ExecuteADVCR;

public class wapTruyentranh extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	private static String dbcontent = "comic_save";
	String dtbase = "gateway";

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
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)
					|| temp == null) {
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
		String SERVER = "http://6x54.com.vn";
		String URL_INV = "http://6x54.com.vn/?c=wap3";
		String DBCONTENT = "content";
		String type = "media";
		String mtcdma = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.";
		String infoid = "";
		try {

			HashMap _option = new HashMap();
			String options = keyword.getOptions();

			_option = getParametersAsString(options);

			GID = getOption(_option, 0);
			Util.logger.info("GID: " + GID);

			// Lay cac thong so tu option
			try {

				type = getString(_option, "type", type);
				SERVER = getString(_option, "server", SERVER);
				Util.logger.info("Server: " + SERVER);
				URL_INV = getString(_option, "url_inv", URL_INV);
				Util.logger.info("URL Invalid: " + URL_INV);
				DBCONTENT = getString(_option, "dbcontent", DBCONTENT);
				mtcdma = getString(_option, "mtcdma", mtcdma);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			String mtinvsubcode = getString(
					_option,
					"mtinvsubcode",
					"Ban nhan tin  sai . De tai truyen soan  TR tentruyen gui 8751. Nhan sanh sach truyen khac soan  TR gui 8751. DTHT  19001745");
			Util.logger.info("mtinvsubcode1: " + mtinvsubcode);

			if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {
				infoid = "viettel";
			} else if (("VMS".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| "mobifone".equalsIgnoreCase(msgObject
							.getMobileoperator())) {
				infoid = "mobifone";
			} else if (("GPC".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| ("VINAPHONE".equalsIgnoreCase(msgObject
							.getMobileoperator()))) {
				infoid = "vinaphone";
			} else {
				infoid = "other";
			}
			 if ("other".equalsIgnoreCase(infoid)){
				msgObject.setUsertext(mtcdma);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				return messages;
			}
			String userid = msgObject.getUserid();
			// System.err.println("GID:" + GID);
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");

			if (sTokens.length == 1) {
				int timesend = getUserID(userid, dbcontent, 0);
				Util.logger.info("Time Send: " + timesend);
				String lastid = "";
				if (timesend >= 1) {
					// Gui lan thu 2
					lastid = getComicID(userid, dbcontent, 0);
				} else {
					// Ghi lai thong tin ghi nhan khach hang da gui
					saverequest(userid, dbcontent, 0);
				}
				String listcomicTA = "Truyen TA:"
						+ findListofComic(dtbase, userid, lastid, 0, "TA");
				Util.logger.info("List of Comic TA: " + listcomicTA);
				// gui danh sach truyen tieng anh
				if (!"Truyen TA:".equalsIgnoreCase(listcomicTA)) {
					if (!"".equalsIgnoreCase(listcomicTA)) {
						msgObject.setUsertext(listcomicTA);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

					}
				}

				lastid = getComicID(userid, dbcontent, 0);
				String listcomicTV = "Truyen TV:"
						+ findListofComic(dtbase, userid, lastid, 0, "TV");
				Util.logger.info("List of Comic TV: " + listcomicTV);
				// gui ds truyen tieng viet
				if (!"Truyen TV:".equalsIgnoreCase(listcomicTV)) {
					if (!"".equalsIgnoreCase(listcomicTV)) {
						msgObject.setUsertext(listcomicTV);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));

					}
				}
				if (("Truyen TA:".equalsIgnoreCase(listcomicTA))
						&& ("Truyen TV:".equalsIgnoreCase(listcomicTV))) {
					msgObject
							.setUsertext("Danh sach truyen dang tiep tuc  duoc cap nhat. Soan tin TR tentruyen gui 8751 de tai truyen cho be yeu. DTHT 19001745.");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));

				}
				return messages;
			}

			else {
				VALID_CODE = validcode(sTokens[1], GID, DBCONTENT, type);
				Util.logger.info("Valid Code:" + VALID_CODE);
				Util.logger.info("Invalid Code:" + INVALID_CODE);

				if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
					String phone = msgObject.getUserid();
					String code = VALID_CODE;

					if (saverequest(phone, VALID_CODE, type, GID, DBCONTENT)) {

						String giff_userid = "";
						String giff_telco = "";
						boolean giff = false;

						if (sTokens.length > 2) {
							giff_userid = ValidISDN(sTokens[2]);
							if (!giff_userid.equalsIgnoreCase("-")) {
								giff_telco = getMobileOperator(giff_userid);

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

							sendGifMsg(
									msgObject.getServiceid(),
									giff_userid,
									giff_telco,
									msgObject.getKeyword(),
									"Ban da duoc sdt "
											+ msgObject.getUserid()
											+ " gui tang truyen. De nhan danh sach truyen soan TR gui 8751. DTHT 1900145. Link truyen "
											+ code + ":" + SERVER + "/?p="
											+ phone + "&c=" + code + "&f="
											+ type + "&g=" + GID, msgObject
											.getRequestid(), 8);

							msgObject
									.setUsertext("Ban da gui tang thanh cong toi thue bao "
											+ giff_userid + ".");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));

						} else {
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

					Util.logger.info("mtinvsubcode3: " + mtinvsubcode);
					msgObject.setUsertext(mtinvsubcode);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(8);
					messages.add(new MsgObject(msgObject));

					return messages;

				}

			}

		} catch (Exception e) {
			Util.logger.error("Error 1:" + e.getMessage());
			Util.logger.printStackTrace(e);
		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

		return null;
	}

	private static int getUserID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT times FROM " + dtbase + " WHERE userid= '"
				+ userid.toUpperCase() + "' AND subcode =" + subcode;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getInt(1);
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

	private static String getComicID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT comicid FROM " + dtbase + " WHERE userid= '"
					+ userid.toUpperCase() + "' AND subcode=" + subcode;
			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return sequence_temp;
			}
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

	public static String deteleSpaces(String sInput) {
		String strResult = "";
		for (int i = 2; i < sInput.length(); i++) {
			char ch = sInput.charAt(i);
			if (ch != ' ') {
				strResult = sInput + ch;
			}
		}
		return strResult;
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
					+ "(userid, subcode) VALUES ('" + userid + "'," + subcode
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
				|| mobileNumber.startsWith("0166")
				|| mobileNumber.startsWith("84164")
				|| mobileNumber.startsWith("0164")) {
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

	private static String findListofComic(String dtbase, String userid,
			String lastid, int subcode, String types) {
		String musiclist = lastid;
		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT commic_sms,comicname FROM icom_comic WHERE ( type ='"
					+ types + "') ";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND comicname not in (" + lastid
						+ ") ";

			}

			Util.logger.info("SEARCH Comic : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {

					result = result + rs.getString(1) + ";";

					if (result.length() > 150) {
						// cap nhat cho khach hang list danh sach da gui
						updateListComic(userid, lastid, dbcontent, subcode);
						return result;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + "'" + rs.getString(2) + "'";
						} else {
							lastid = lastid + "," + "'" + rs.getString(2) + "'";
						}

					}
				}

			}
			updateListComic(userid, lastid, dbcontent, subcode);

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

	private static boolean updateListComic(String userid, String lastid,
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

			// Update cac danh sach truyen da gui cho khach hang

			String sqlUpdate = "UPDATE " + dtbase + " SET comicid = \" "
					+ lastid + "\" WHERE upper(userid)='"
					+ userid.toUpperCase() + "' AND subcode=" + subcode;
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list comic to send " + userid
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
