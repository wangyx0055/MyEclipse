package wap;

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

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

/**
 * WappushBH class.<br>
 * 
 * <pre>
 * ・ Update from WappushQM class
 * </pre>
 * 
 * @author Vietnamnet ICom
 * @version 2.0
 */
public class WappushTruyen extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 16;
	String SERVER = "http://mobinet.com.vn";
	String URL_INV = "http://mobinet.com.vn/?c=wap3";
	String infoid = "";
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

		try {

			HashMap _option = new HashMap();

			String options = keyword.getOptions();

			_option = getParametersAsString(options);

			GID = getOption(_option, 16);
			Util.logger.info("GID: " + GID);
			SERVER = getString(_option, "server", SERVER);
			Util.logger.info("SERVER: " + SERVER);
			String type = getString(_option, "type", "JavaApp");
			Util.logger.info("type: " + type);

			String mtcdma = getString(_option, "mtcdma",
					"Hien tai dich vu chua ho tro mang cua ban,moi ban quay lai sau.");

			String mtinvsubcode = getString(
					_option,
					"mtinvsubcode",
					"Ban nhan tin sai,tang ban link truyen:");

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

			String userid = msgObject.getUserid();
			String info = msgObject.getUsertext();
			if ("other".equalsIgnoreCase(infoid)) {
				msgObject.setUsertext(mtcdma);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			info = replaceAllWhiteWithOne(info);
			String[] sTokens = info.split(" ");

			if (sTokens.length < 2) {

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
				String listcomicTA = findListofComic(dtbase, userid, lastid, 0,
						"TA");
				Util.logger.info("List of Comic TA: " + listcomicTA);
				
				lastid = getComicID(userid, dbcontent, 0);
				String listcomicTV = findListofComic(dtbase, userid, lastid, 0,
						"TV");
				Util.logger.info("List of Comic TV: " + listcomicTV);
				
				if (("TruyenTA:".equalsIgnoreCase(listcomicTA))	&& ("TruyenTV:".equalsIgnoreCase(listcomicTV)))
				{
					msgObject.setUsertext("Danh sach truyen dang tiep tuc duoc cap nhat.Soan tin TR tentruyen gui 8751 de tai truyen cho be yeu.DTHT 19001745.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));

				}
				else if (!"TruyenTA:".equalsIgnoreCase(listcomicTA) && !"TruyenTV:".equalsIgnoreCase(listcomicTV) )
				{
					// gui danh sach truyen tieng anh
					
					listcomicTA = splitString(listcomicTA)[0];
					msgObject.setUsertext(listcomicTA);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					
					// gui ds truyen tieng viet
					
					listcomicTV = splitString(listcomicTV)[0];
					msgObject.setUsertext(listcomicTV);
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					
				}
				else 
				{
					if (!"TruyenTA:".equalsIgnoreCase(listcomicTA))
					{
						// gui danh sach truyen tieng anh
						
						listcomicTA = splitString(listcomicTA)[0];
						msgObject.setUsertext(listcomicTA);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
					}
					else if (!"TruyenTV:".equalsIgnoreCase(listcomicTV))
					{
						// gui ds truyen tieng viet
						
						listcomicTV = splitString(listcomicTV)[0];
						msgObject.setUsertext(listcomicTV);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));		
					}
						
					
				}
			
				return messages;
			} else {

				// Tim vi tri so dien thoai
				int number = getValidPhone(sTokens);
				Util.logger.info("Number: " + number);
				String phone = msgObject.getUserid();
				if (number > 1) {

					// Co gui tang
					Util.logger.info("sTokens[1]: " + sTokens[1]);
					
					VALID_CODE = validcode(sTokens[1], type, GID);
					
					// Co truyen trong he thong.
					if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
						
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
									Util.logger.info("Giff Telco: "
											+ giff_telco);

									if ("-".equals(giff_telco)
											|| "other"
													.equalsIgnoreCase(checkMobileOperator(giff_telco))) {
										msgObject.setUsertext("Hien tai dich vu chua ho tro mang cua thue bao duoc tang");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										messages.add(new MsgObject(msgObject));
										return messages;
									} else {
										giff = true;
									}
									
								}
							}

							if (giff) {
								// Sdt khach hang tang thoa man la 3 mang
								// viettel, mobifone, vinaphone
								sendGifMsg(msgObject.getServiceid(),
										giff_userid, giff_telco, msgObject
												.getKeyword(), "Sdt "
												+ msgObject.getUserid()
												+ " tang ban " + code + ":"
												+ SERVER + "/?p=" + phone
												+ "&c=" + code + "&f=" + type
												+ "&g=" + GID, msgObject
												.getRequestid(), 8);

								msgObject.setUsertext("Ban da gui tang truyen "
										+ code + " thanh cong toi sdt "
										+ giff_userid + ".");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
								return messages;

							} else {

								// Check xem mobi_operator cua khach hang
								if ("other".equalsIgnoreCase(infoid)) {
									msgObject.setUsertext(mtcdma);
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									messages.add(new MsgObject(msgObject));
									return messages;
								}
                                
								// Okie thi gui
								msgObject.setUsertext("Truyen " + code + ":"
										+ SERVER + "/?p=" + phone + "&c="
										+ code + "&f=" + type + "&g=" + GID);
								msgObject.setContenttype(8);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
							}

							return messages;
						}

					} else {

						// Truyen Tit khong co check tiep Titvamit 0972
						String musicname = "";
						for (int i = 1; i < number; i++) {
							musicname = musicname + sTokens[i];
						}

						Util.logger.info("Comic Name: " + musicname);
						VALID_CODE = validcode(musicname, type, GID);

						// Co bai hat trong he thong.
						
						if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
							
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
												|| "other"
														.equalsIgnoreCase(checkMobileOperator(giff_telco))) {
											msgObject.setUsertext("Hien tai dich vu chua ho tro mang cua thue bao duoc tang");
											msgObject.setMsgtype(1);
											msgObject.setContenttype(0);
											messages.add(new MsgObject(msgObject));
											return messages;
										} else {
											giff = true;
										}
									}
								}

								if (giff) {
									sendGifMsg(msgObject.getServiceid(),
											giff_userid, giff_telco, msgObject
													.getKeyword(), "Sdt "
													+ msgObject.getUserid()
													+ " tang ban " + code + ":"
													+ SERVER + "/?p=" + phone
													+ "&c=" + code + "&f="
													+ type + "&g=" + GID,
											msgObject.getRequestid(), 8);

									msgObject
											.setUsertext("Ban da gui tang thanh cong toi sdt "
													+ giff_userid + ".");
									msgObject.setContenttype(0);
									msgObject.setMsgtype(1);
									messages.add(new MsgObject(msgObject));
									return messages;

								} else {

									// Check xem mobi_operator cua khach hang
									if ("other".equalsIgnoreCase(infoid)) {
										msgObject.setUsertext(mtcdma);
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										messages.add(new MsgObject(msgObject));
										return messages;
									}

									// Okie thi gui
									msgObject.setUsertext("Truyen " + code
											+ ":" + SERVER + "/?p=" + phone
											+ "&c=" + code + "&f=" + type
											+ "&g=" + GID);
									msgObject.setContenttype(8);
									msgObject.setMsgtype(1);
									messages.add(new MsgObject(msgObject));
									return messages;
								}
							}
						}else {
							String code=getRandom(type,GID);
							mtinvsubcode +=SERVER + "/?p=" + phone
											+ "&c=" + code + "&f=" + type
											+ "&g=" + GID;
							msgObject.setUsertext(mtinvsubcode);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(8);
							messages.add(new MsgObject(msgObject));
							return messages;
						}
					}
				} else if (number == 1) {

					// Khach hang nhan tin TR 0987654321
					String code=getRandom(type,GID);
					mtinvsubcode += SERVER + "/?p=" + phone
									+ "&c=" + code + "&f=" + type
									+ "&g=" + GID;
					msgObject.setUsertext(mtinvsubcode);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(8);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else if (number == 0) {

					// Neu khong gui tang. Check xem Mobile_Operator cua khach
					// hang.
					
					// Neu thoa man la thuoc 3 mang Viettel, Mobi, Vina
					// Khong tang ai ca.Khach hang nhan TR tit va mit. Check
					// truyen tit truoc
					
					VALID_CODE = validcode(sTokens[1], type, GID);
					Util.logger.info("sTokens[1]: " + sTokens[1]);

					// Co truyen trong he thong.
					if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
					
						String code = VALID_CODE;

						if (saverequest(phone, VALID_CODE, type, GID)) {

							msgObject.setUsertext("Truyen " + code + ":"
									+ SERVER + "/?p=" + phone + "&c=" + code
									+ "&f=" + type + "&g=" + GID);
							msgObject.setContenttype(8);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));
							return messages;
						}
					} else {
						String comicname = "";
						for (int i = 1; i < sTokens.length; i++) {
							comicname = comicname + sTokens[i];
						}
						// Valid toan bo
						VALID_CODE = validcode(comicname, type, GID);
						Util.logger.info("Comic name: " + comicname);
						
						if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
							
							String code = VALID_CODE;

							if (saverequest(phone, VALID_CODE, type, GID)) {

								msgObject.setUsertext("Truyen " + code + ":"
										+ SERVER + "/?p=" + phone + "&c="
										+ code + "&f=" + type + "&g=" + GID);
								msgObject.setContenttype(8);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
								return messages;
							}
						} else {
							String code=getRandom(type,GID);
							mtinvsubcode += SERVER + "/?p=" + phone
											+ "&c=" + code + "&f=" + type
											+ "&g=" + GID;
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
	

	// Check xem truyen co trong danh sach khong?
	private String validcode(String code, String type, int gid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("content");
			
			String query1 = "select code,filetype from icom_wap.dbo.upload  where (upper(code)='"
					+ code.toUpperCase()
					+ "'  or upper(code)='"
					+ code.toUpperCase()
					+ "P' ) and upper(filetype)='"
					+ type.toUpperCase() + "' and cgroup=" + gid;
          
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
	private String getRandom1( String type, int gid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("content");
			
			String query1 = "select Top 1 code,filetype from icom_wap.dbo.upload  where  cgroup=" +gid+" and upper(filetype)='"
				+ type.toUpperCase()+" ORDER BY NEWID()";
          
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
	private String getRandom( String type, int gid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String sequence_temp = "";
		try {
			connection = dbpool.getConnectionGateway();
			
			String query1 = "SELECT comicname FROM icom_comic ORDER BY RAND()LIMIT 1";
          
			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				sequence_temp = (String) item.elementAt(0);

				return sequence_temp;
			}

		} catch (Exception e) {
			return sequence_temp;
		} finally {
			dbpool.cleanup(connection);

		}
		return sequence_temp;
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
			if ("TA".equalsIgnoreCase(types)) {
				result = "TruyenTA:";
			} else if ("TV".equalsIgnoreCase(types)) {
				result = "TruyenTV:";
			}

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {

					result = result + rs.getString(1) + ";";

					if (result.length() >= 160) {
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

	private static String[] splitString(String splitS) {
		String[] result = new String[2];
		boolean resultBoolean = false;
		int i = 160;
		int j = 0;
		String tempString = splitS;
		if (splitS.length() >= 160) {
			while (!(resultBoolean)) {
				if (splitS.charAt(i) == ';') {
					result[0] = splitS.substring(0, i);
					j = i + 1;
					resultBoolean = true;
				}
				i--;
			}
			resultBoolean = false;
			i = tempString.length() - 1;

			while (!(resultBoolean)) {
				// <=160
				if ((tempString.charAt(i) == ';') && ((i - j) <= 160)) {
					result[1] = tempString.substring(j, i);
					resultBoolean = true;
				}
				i--;
			}
		} else {
			result[0] = splitS;
			result[1] = "";
		}

		return result;

	}

	// Luu thong tin nguoi dung
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

	// Luu thong tin de download
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
}
