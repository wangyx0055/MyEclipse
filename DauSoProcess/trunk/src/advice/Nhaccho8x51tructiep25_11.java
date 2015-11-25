package advice;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import neo.funring.FunRingGateSoap11BindingImpl;

import com.viettel.service.IMZGWSoap11BindingImpl;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.sendXMLGPC;
import com.vmg.soap.mo.sendXMLVIETTEL;

import cs.ExecuteADVCR;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * ・
 * </pre>
 * 
 * @author Vietnamnet I-Com HaPTT
 * @version 1.0
 */
public class Nhaccho8x51tructiep25_11 extends ContentAbstract {

	/* First String */
	private static String viettelGuide = "Buoc1:Dang ki su dung,soan:DK gui 1221.Buoc 2:Chon bai hat,soan:BH maso gui 1221.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String vinaphoneGuide = "Buoc1:Dang ki su dung,soan:DK gui 9194.Buoc 2:Chon bai hat,soan:TUNE maso gui 9194.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String mobifoneGuide = "Buoc1:Dang ki su dung,soan:DK gui 9224.Buoc 2:Chon bai hat,soan:CHON maso gui 9224.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String beelineGuide = "De tai bai hat.Soan tin:CHON masobaihat gui 1221.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";

	private static String dbcontent = "icom_dangky_nhaccho_tructiep";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "1";
		try {
			Collection messages = new ArrayList();

			String infoid = "HOT";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String numberid = "8x51";
			String dtbase = "gateway";
			String last_msg = "MCA tencasy gui 8751.DTHT 1900571566";
			String urlgpc = "http://ringtunes.vinaphone.com.vn/rbt_prov/prov";
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				numberid = getString(_option, "numberid", numberid);
				dtbase = getString(_option, "dtbase", dtbase);
				inv_telco = getString(_option, "inv_telco", inv_telco);
				last_msg = getString(_option, "last_msg", last_msg);
				ismtadd = getString(_option, "ismtadd", ismtadd);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			/* kiem tra thue bao khach hang */
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

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			// lay so thue bao nguoi gui
			String mt1 = "";
			String userid = msgObject.getUserid();
			/* lay noi dung gui */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext()
					.toUpperCase());
			String operator = msgObject.getMobileoperator();
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			String serviceid = msgObject.getServiceid();
			BigDecimal request_id = msgObject.getRequestid();
			/*
			 * nc ten bai hat
			 */

			if (sTokens.length > 1) {

				String[] resultMusicID = new String[2];
				// 0 la ten bai hat, 1 ma ma bai hat
				resultMusicID = findMusicID(dtbase, sTokens[1], infoid);
				// MT1
				if (!"".equalsIgnoreCase(resultMusicID[1])) {

					Random iRandom = new Random();
					int imusic = iRandom.nextInt(10);
					// Neu la Vinaphone thi se dung ham BUYRBT, sau do la IBASE

					//

					String giff_userid = "";
					String giff_telco = "";
					boolean giff = false;

					if (sTokens.length > 2) {
						giff_userid = ValidISDN(sTokens[2]);
						if (!giff_userid.equalsIgnoreCase("-")) {
							giff_telco = getMobileOperatorNew(giff_userid,
									"gateway", 0);

							if ("-".equals(giff_telco)) {
								giff = false;
							} else {
								giff = true;
							}

						}
					}

					if (giff == true) {
						if (giff_telco.toUpperCase().equalsIgnoreCase("GPC")
								&& infoid.equalsIgnoreCase("vinaphone")) {
							// Mang GPC gui tang
							String resultgift = null;
							resultgift = sendXMLGPC.GIFRBT(urlgpc, "GIFRBT",
									userid, giff_userid, request_id,
									resultMusicID[1]);
							if (resultgift.equalsIgnoreCase("0")) {
								sendGifMsg(msgObject.getServiceid(),
										giff_userid, giff_telco, msgObject
												.getKeyword(), "Sodt "
												+ msgObject.getUserid()
												+ " tang ban bai nhac cho: "
												+ resultMusicID[0], msgObject
												.getRequestid(), 0);

								msgObject
										.setUsertext("Ban da gui tang  thanh cong toi thue bao "
												+ giff_userid + ".");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								DBUtil.sendMT(msgObject);
							} else {
								Util.logger.info("Gui tang khong thanh cong:"
										+ resultgift);
								msgObject
										.setUsertext("Ban gui tang chua thanh cong co the do nguoi nhan khong ton tai hoac chua dang ky nhac cho");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								DBUtil.sendMT(msgObject);
							}
						} else if (giff_telco.toUpperCase().equalsIgnoreCase(
								"VIETTEL")
								&& infoid.equalsIgnoreCase("viettel")) {

						} else if (giff_telco.toUpperCase().equalsIgnoreCase(
								"VMS")
								&& infoid.equalsIgnoreCase("mobifone")) {
							// Gui tang cua mang MOBIPHONE
							msgObject
									.setUsertext("Dich vu chua ho tro mang cua ban");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(2);
							DBUtil.sendMT(msgObject);
							return null;
						} else

						{
							msgObject
									.setUsertext("Ban gui tang chua thanh cong do thue bao nhan khong cung mang.Tang ban bai hat HOT:");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(1);
							DBUtil.sendMT(msgObject);
						}
						int time2send = getUserID(userid, dbcontent, 1);
						String lastid = "";
						if (time2send >= 1) {
							lastid = getMusicID(userid, dbcontent, 1);
						} else {
							// Ghi lai danh sach khach hang da gui
							saverequest(userid, dbcontent, 1);
						}

						// Lay danh sach cac bai hat HOT
						String musichotsend = findListofMusic(dtbase, userid,
								infoid, lastid, 1);

						String[] listsendClient = splitString(musichotsend);

						if (!"".equalsIgnoreCase(listsendClient[0])) {
							msgObject.setUsertext(listsendClient[0]);
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
						}

						return null;

					} else {

						if ("viettel".equalsIgnoreCase(infoid)) {
							// Cai dat nhac cho cho khach hang
							// Tra tin cai dat thanh cong
							// Gui ham lay ma cai dat
							String useridvt = GetuserVT(userid);
							
							   IMZGWSoap11BindingImpl bi=new    IMZGWSoap11BindingImpl();
						String result="";
						try
						{	Util.logger.info("useridvt:"
								+ useridvt);
						   Util.logger.info(" now():"
								+  now());
						
							result = bi.processCRBTs(useridvt,"IMZGW", "IMZ_ICOM","icom", "icom123$%", "0",  now(),"GETVALIDATECODE", "495890", "2");
						Util.logger.info("result:"
								+ result);
						}
						catch (Exception ex)
						{	Util.logger.info("Khong goi duoc ws");
							
						}
						
							// Tra tin: Ban gui lai ma so de xac nhan viec cai
							if (result.equalsIgnoreCase("000000")) {
								mt1 = "Ban hay gui lai ma kiem tra de xac nhan dang ky nhac cho, soan tin XNNC makiemtra gui 8051.Tang ban TOP bai hat HOT";

								// luu user voi bai hat tai
								saveuser(userid, "nhaccho_viettel",
										resultMusicID[1]);

							} else {
								if (result.equalsIgnoreCase("301008 ")) {// Da
																			// cai
																			// dat
																			// nhac
																			// cho
									Util.logger.info("Da cai dat nhac cho roi:"
											+ userid);
									String result2 = sendXMLVIETTEL
											.SendXML("imuzic", "SETTONE",
													useridvt, request_id,
													now(), resultMusicID[1]);
									if (result2.equalsIgnoreCase("000000")) {
										Util.logger
												.info("Cai dat nhac cho thanh cong cho thue bao:"
														+ userid);
										mt1 = "Chuc mung ban da cai dat thanh cong bai hat "
												+ resultMusicID[0]
												+ ".Tang cho ban be: NC tenbaihat sdt gui 8751.Tang ban top bai hat HOT:";

									} else {
										Util.logger.info("User:" + userid
												+ ";Result2:" + result);

										mt1 = "Ban cai dat nhac cho chua thanh cong. Tang ban top bai hat HOT:";
									}
								} else
									// Cai dat nhac cho chua thanh cong
									Util.logger.info("User:" + userid
											+ ";Result:" + result);
								mt1 = "Cai dat nhac cho chua thanh cong. Xin vui long thu lai sau. DTHT 1900571566.Tang ban top bai hat HOT:";

							}

						} else if ("vinaphone".equalsIgnoreCase(infoid)) {

							// Cai dat nhac cho cho khach hang

							String result = sendXMLGPC.SendXML(urlgpc,
									"BUYRBT", userid, request_id,
									resultMusicID[1]);

							if (result.startsWith("0")) {
								try {
									String Base = sendXMLGPC.SendXML(urlgpc,
											"IBASE", userid, request_id,
											resultMusicID[1]);
									if (Base.startsWith("0")) {
										Util.logger
												.info("Cai dat nhac cho thanh cong cho thue bao:"
														+ userid);
										mt1 = "Chuc mung ban da cai dat thanh cong bai hat "
												+ resultMusicID[0]
												+ ".Tang cho ban be: NC tenbaihat sdt gui 8751.Tang ban top bai hat HOT:";
										// Tra tin cai dat thanh cong
									} else {
										Util.logger
												.info("Khong thanh cong: Result_base:"
														+ Base);

										mt1 = "Ban cai dat nhac cho chua thanh cong. Tang ban top bai hat HOT:";

									}
								} catch (Exception ex) {
									Util.logger.info("Base khong thanh cong");

									mt1 = "Ban cai dat nhac cho chua thanh cong. Tang ban bai hat HOT:";

								}
							} else {
								Util.logger.info("Result:" + result);
								if (result.startsWith("104")) {
									mt1 = "Bai hat da ton tai trong bo suu tap. Tang ban top bai hat HOT:";

								} else if (result.startsWith("3399")
										|| result.startsWith("3301")) {
									mt1 = "Ban cai dat nhac cho chua thanh cong do khong du tien. Tang ban top bai hat HOT:";

								} else

									mt1 = "Ban cai dat nhac cho chua thanh cong. Tang ban top bai hat HOT:";
							}

						} else if ("mobifone".startsWith(infoid)) {
							// Cai dat nhac cho cho khach hang
							// Tra tin cai dat thanh cong
							FunRingGateSoap11BindingImpl bi2=new FunRingGateSoap11BindingImpl();
							try
							{
							String result=bi2.getUserStatus("icom", "icomfrpass","0934258288");	
							Util.logger.info("Result:"+result);
							}
							catch (Exception ex)
							{	Util.logger.error("Khong goi duoc ws");
								
							}

						}

						msgObject.setUsertext(mt1);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

						// tim danh sach bai hat hot
						// Kiem tra so lan gui tin cua khach hang
						int time2send = getUserID(userid, dbcontent, 1);
						String lastid = "";
						if (time2send >= 1) {
							lastid = getMusicID(userid, dbcontent, 1);
						} else {
							// Ghi lai danh sach khach hang da gui
							saverequest(userid, dbcontent, 1);
						}

						// Lay danh sach cac bai hat HOT
						String musichotsend = findListofMusic(dtbase, userid,
								infoid, lastid, 1);

						String[] listsendClient = splitString(musichotsend);

						if (!"".equalsIgnoreCase(listsendClient[0])) {
							msgObject.setUsertext(listsendClient[0]);
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
						}

						return null;
					}
				} else {

					// Bai hat chua co trong he thong.
					// Send MT 1
					String info2Client = "";
					info2Client = "Bai hat ban thich hien tai chua co trong he thong.Truy cap ngay trang:http://nhipdap.vn/ de lua chon cac BH Hot khac.Tang ban top bai hat HOT:";

					msgObject.setUsertext(info2Client);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					// Send MT 2
					// tim danh sach bai hat hot
					// Kiem tra so lan gui tin cua khach hang
					int time2send = getUserID(userid, dbcontent, 1);
					String lastid = "";
					if (time2send >= 1) {
						lastid = getMusicID(userid, dbcontent, 1);
					} else {
						// Ghi lai danh sach khach hang da gui
						saverequest(userid, dbcontent, 1);
					}

					// Lay danh sach cac bai hat HOT
					String musichotsend = findListofMusic(dtbase, userid,
							infoid, lastid, 1);

					String[] listsendClient = splitString(musichotsend);

					if (!"".equalsIgnoreCase(listsendClient[0])) {
						msgObject.setUsertext(listsendClient[0]);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}

					return null;
				}
			} else {
				String info2Client = "Tin nhan sai cu phap. Truy cap ngay trang: http://nhipdap.vn/ de lua chon cac BH Hot khac. CSKH:1900571566";
				msgObject.setUsertext(info2Client);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

				return null;
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {

			if ("1".equalsIgnoreCase(ismtadd))
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
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

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
			long itemp = Integer.parseInt(sISDN.trim());
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

	public String GetuserVT(String sISDN) {
		Util.logger.info(this.getClass().getName() + "GetuserVT?*" + sISDN
				+ "*");

		String tempisdn = "-";

		if (sISDN.trim().length() < 8) {
			return "-";
		}
		try {
			Util.logger.info(this.getClass().getName() + "itemp?*" + sISDN
					+ "*");
			tempisdn = sISDN;
			if (tempisdn.startsWith("84")) {
				tempisdn = tempisdn.substring(2);
			} else if (tempisdn.startsWith("0")) {
				tempisdn = tempisdn.substring(1);
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	// Replace ____ with _
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

	/* tim ma so cac bai hat */
	private static String[] findMusicID(String dtbase, String find,
			String infoid) {

		String[] result = new String[2];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		for (int i = 0; i < 2; i++) {
			result[i] = "";
		}

		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT musicname_sms, musicid FROM icom_music WHERE ( upper(musicname) = '"
					+ find.toUpperCase()
					+ "') AND ( upper(operator) ='"
					+ infoid.toUpperCase() + "')";

			Util.logger.info("SEARCH MA SO  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SEARCH MA SO");
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
				}
			}
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

	public static String getMobileOperatorNew(String dtbase, String userid,
			int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection(dtbase);

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
			Util.logger.info("getMobileOperator: Get MobileOpereator Failed"
					+ ex.toString());
			return tmpOperator;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	/* tim list bai hat HOT */
	private static String findListofMusic(String dtbase, String userid,
			String hotmusic, String infoid, String lastid) {
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

			String sqlSelect = "SELECT musicname_sms, musicid FROM icom_music WHERE ( ( upper(group1) = '"
					+ hotmusic.toUpperCase()
					+ "') OR (upper(group2)='"
					+ hotmusic.toUpperCase()
					+ "') OR (upper(group3)='"
					+ hotmusic.toUpperCase()
					+ "') OR (upper(group4)='"
					+ hotmusic.toUpperCase()
					+ "') OR (upper(group5)='"
					+ hotmusic.toUpperCase()
					+ "') ) AND ( upper(operator) = '"
					+ infoid.toUpperCase() + "')";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND musicid not in(" + lastid + ") ";
			}

			Util.logger.info("SEARCH SONG MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = result + rs.getString(1) + "-" + rs.getString(2)
							+ ";";
					if (result.length() > 320) {
						// cap nhat cho khach hang list danh sach da gui
						updateListMusic(userid, lastid, dbcontent, 1);
						return result;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + "'" + rs.getString(2) + "'";
						} else {
							lastid = lastid + ",'" + rs.getString(2) + "'";
						}
					}
				}
			}
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

	/* Chia thanh 2 MT */
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

			/* tach thanh 2 */
			while (!(resultBoolean)) {
				// neu <160
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

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
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

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static String getMusicID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT musichotid FROM " + dtbase
					+ " WHERE userid= '" + userid.toUpperCase()
					+ "' AND subcode=" + subcode;
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

	private static boolean saveuser(String userid, String dtbase,
			String contentid) {

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
					+ "(userid, contentid) VALUES ('" + userid + "','"
					+ contentid + "')";
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

	// cap nhat danh sach khach hang
	private static boolean updateListMusic(String userid, String lastid,
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

			// Update cac danh sach bai hat da gui cho khach hang
			String sqlUpdate = "UPDATE " + dtbase + " SET musichotid = '"
					+ lastid + "' WHERE upper(userid)='" + userid.toUpperCase()
					+ "' AND subcode=" + subcode;
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

	/* Tim list bai hat */
	private static String findListofMusic(String dtbase, String userid,
			String infoid, String lastid, int subcode) {
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

			String sqlSelect = "SELECT musicname_sms, musicid FROM icom_music WHERE ( upper(operator) = '"
					+ infoid.toUpperCase() + "')";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND musicid not in(" + lastid + ") ";
			}

			Util.logger.info("SEARCH SONG MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = result + rs.getString(1) + "-" + rs.getString(2)
							+ ";";
					if (result.length() > 160) {
						// cap nhat cho khach hang list danh sach da gui
						updateListMusic(userid, lastid, dbcontent, subcode);
						return result;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + rs.getString(2);
						} else {
							lastid = lastid + "," + rs.getString(2);
						}
					}
				}
			}
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

}
