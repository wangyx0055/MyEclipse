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
import java.util.Random;
import java.util.StringTokenizer;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * ・
 * </pre>
 * 
 * @author Vietnamnet I-Com TrungVD
 * @version 1.0
 */
public class Nhaccho8x51 extends ContentAbstract {

	/* First String */
	private static String viettelGuide = "Buoc1:Dang ki su dung,soan:DK gui 1221.Buoc 2:Chon bai hat,soan:BH maso gui 1221.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String vinaphoneGuide = "Buoc1:Dang ki su dung,soan:DK gui 9194.Buoc 2:Chon bai hat,soan:TUNE maso gui 9194.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String mobifoneGuide = "Buoc1:Dang ki su dung,soan:DK gui 9224.Buoc 2:Chon bai hat,soan:CHON maso gui 9224.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String beelineGuide = "De tai bai hat.Soan tin:CHON masobaihat gui 1221.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private String[] musicHot_Viettel = { "100 yeu em - 633395",
			"Cau vong khuyet - 633519", "Con duong mua - 633407",
			"Dong thoi gian - 633655", "Let you go - 633429",
			"Gia vo yeu - 633423", "Don coi 1 - 633364",
			"The gioi to that to - 633448",
			"Hot boy Thue bao het tien - 63318",
			"Tai em yeu don phuong = 633902" };
	private static final String[] musicHot_Vinaphone = {
			"Hoa Thien Dieu - 750124", "Con duong mua - 750077",
			"Don coi = 750069", "Gia vo yeu - 750057", "Lac loi = 750046",
			"Let you go - 750550", "Loi nguyen - 750041", "Doi mat - 750070",
			"Ngoi nha tuyet trang - 750137", "Tu tin de dung vung - 750161" };
	private static final String[] musicHot_Mobifone = { "Let you go - 5201094",
			"Hoa Thien Dieu - 5201026", "Neu - 5201014", "Boi roi - 520364",
			"Loi nguyen - 520375", "Con duong mua - 520369",
			"Doi mat - 520370", "Boi roi - 520364", "Don coi - 520371",
			"Ngoi nha tuyet trang - 520971" };
	/* Beeline 10 bai hat */
	private static final String[] musicHot_Beeline = {
			"Dau mot lan de biet yeu mot lan - 651047",
			"Teen vong co - 655237", "Ngoi nha hanh phuc 1 - 652956",
			"30 ngay yeu - 650017", "Con duong tinh yeu 1 - 650890",
			"Nho Anh May Mua - 653103", "Mat uot mi cay - 652501",
			"Nang gio va la - 652793", "Co nang hay ghen - 650843",
			"Nang - 652786" };

	private static String dbcontent = "icom_dangky_nhaccho";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "1";
		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String numberid = "8x51";
			
	
			String dtbase = "gateway";
			String last_msg = "MCA tencasy gui 8751.DTHT 1900571566";

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

			/*kiem tra thue bao khach hang*/
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
			}else if (("GTEL".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| ("Beeline".equalsIgnoreCase(msgObject
							.getMobileoperator()))) {
				infoid = "beeline";
			}  else {
				infoid = "other";
			}

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			//lay so thue bao nguoi gui
			String userid = msgObject.getUserid();
			/*lay noi dung gui */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext()
					.toUpperCase());
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			String serviceid = msgObject.getServiceid();

			/*
			 * nc ten bai hat
			 */

			if (sTokens.length > 1) {

				String subTokens = "";
				for (int k = 1; k < sTokens.length; k++) {
					subTokens = subTokens + sTokens[k] + " ";
				}

				Util.logger.info("SubTokens : " + subTokens);

				String subTokens2 = subTokens.replace(" ", "");
				String[] resultMusicID = new String[2];
				// 0 la ten bai hat, 1 ma ma bai hat
				resultMusicID = findMusicID(dtbase, subTokens2, infoid);
				// MT1
				if (!"".equalsIgnoreCase(resultMusicID[1])) {
					
					Random iRandom = new Random();
					int imusic = iRandom.nextInt(10);
					// Neu la Vinaphone thi se dung ham BUYRBT, sau do la IBASE
					
					//
					if ("viettel".equalsIgnoreCase(infoid)) {
						if (musicHot_Viettel[imusic]
								.startsWith(resultMusicID[0].trim())) {
							if (imusic == musicHot_Viettel.length) {
								imusic = imusic - 1;
							} else {
								imusic = imusic + 1;
							}
						}
						resultMusicID[1] = "De tai "
								+ resultMusicID[0]
								+ " ban soan: B1: Soan DK gui 1221.B2: Soan BH "
								+ resultMusicID[1]
								+ " gui 1221.Bai hat HOT nhat trong thang: "
								+ musicHot_Viettel[imusic];
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						if (musicHot_Vinaphone[imusic]
								.startsWith(resultMusicID[0].trim())) {
							if (imusic == musicHot_Vinaphone.length) {
								imusic = imusic - 1;
							} else {
								imusic = imusic + 1;
							}
						}
						resultMusicID[1] = "De tai "
								+ resultMusicID[0]
								+ " ban soan: B1: Soan DK gui 9194.B2: Soan TUNE "
								+ resultMusicID[1]
								+ " gui 9194.Bai hat HOT nhat trong thang: "
								+ musicHot_Vinaphone[imusic];
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						if (musicHot_Mobifone[imusic]
								.startsWith(resultMusicID[0].trim())) {
							if (imusic == musicHot_Mobifone.length) {
								imusic = imusic - 1;
							} else {
								imusic = imusic + 1;
							}
						}
						resultMusicID[1] = "De tai "
								+ resultMusicID[0]
								+ " ban soan: B1: Soan DK gui 9224.B2: Soan CHON "
								+ resultMusicID[1]
								+ " gui 9224.Bai hat HOT nhat trong thang: "
								+ musicHot_Mobifone[imusic];
					} else if ("beeline".equalsIgnoreCase(infoid)) {

						// New mang Beeline
						if (musicHot_Beeline[imusic]
								.startsWith(resultMusicID[0].trim())) {
							if (imusic == musicHot_Beeline.length) {
								imusic = imusic - 1;
							} else {
								imusic = imusic + 1;
							}
						}
						resultMusicID[1] = "De tai " + resultMusicID[0]
								+ " ban soan: CHON " + resultMusicID[1]
								+ " gui 1221.Bai hat HOT nhat trong thang: "
								+ musicHot_Beeline[imusic];
					}

					if (resultMusicID[1].length() > 160) {
						String[] subString = new String[2];
						subString[0] = resultMusicID[1].substring(0, 159);
						subString[1] = resultMusicID[1].substring(160);
						for (int k = 0; k < 2; k++) {
							if (!"".equalsIgnoreCase(subString[k])) {
								msgObject.setUsertext(subString[k]);
								if (k == 0) {
									msgObject.setMsgtype(1);
								} else {
									msgObject.setMsgtype(0);
								}
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(1000);
							}
						}

					} else {

						msgObject.setUsertext(resultMusicID[1]);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						// messages.add(new MsgObject(ms//gObject));
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

					}

					//tim danh sach bai hat hot
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
						// messages.add(new MsgObject(msgObject));
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}

					// Send MT4 cho khach hang
					String sendtoClient = "Ngoai nhung BH ban vua nhan duoc,van con hang ngan BH khac tren "
							+ numberid
							+ ".De tiep tuc nhan duoc ma so BH.Soan:"
							+ last_msg;
					msgObject.setUsertext(sendtoClient);
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					// messages.add(new MsgObject(msgObject));
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);

					return null;

				} else {

					// Bai hat chua co trong he thong.
					// Send MT 1
					String info2Client = "";
					if ("viettel".equalsIgnoreCase(infoid)) {
						info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 1221.Buoc2, soan: BH maso gui 1221. Tang ban ma so BH HOT:";
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 9194.Buoc2, soan: TUNE maso gui 9194. Tang ban ma so BH HOT:";
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 9224.Buoc2, soan: CHON maso gui 9224. Tang ban ma so BH HOT:";
					} else if ("beeline".equalsIgnoreCase(infoid)) {
						info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat, soan: CHON masobaihat gui 1221. Tang ban ma so BH HOT:";
					}

					msgObject.setUsertext(info2Client);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					// Send MT 2 - 3
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
						// messages.add(new MsgObject(msgObject));
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}

					// Send MT4 cho khach hang
					String sendtoClient = "Ngoai nhung BH ban vua nhan duoc, van con hang ngan BH khac tren "
							+ numberid
							+ ".De tiep tuc nhan duoc ma so BH.Soan: "
							+ last_msg;
					msgObject.setUsertext(sendtoClient);
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
					return null;
				}
			} else {

				/* nc */
				// Send MT 
				int timesend = getUserID(userid, dbcontent, 0);
				String lastid = "";
				if (timesend >= 1) {
					// Gui lan thu 2
					lastid = getMusicID(userid, dbcontent, 0);
				} else {
					// Ghi lai thong tin ghi nhan khach hang da gui den 6x54
					saverequest(userid, dbcontent, 0);
				}

				String info2Client = "";
				if ("viettel".equalsIgnoreCase(infoid)) {
					info2Client = viettelGuide;
				} else if ("vinaphone".equalsIgnoreCase(infoid)) {
					info2Client = vinaphoneGuide;
				} else if ("mobifone".equalsIgnoreCase(infoid)) {
					info2Client = mobifoneGuide;
				} else if ("beeline".equalsIgnoreCase(infoid)) {
					info2Client = beelineGuide;
				}
				msgObject.setUsertext(info2Client);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				// messages.add(new MsgObject(msgObject));
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

				// MT2 - 3
				String listmusic = findListofMusic(dtbase, userid, infoid,
						lastid, 0);
				String[] listsendClient = splitString(listmusic);

				if (!"".equalsIgnoreCase(listsendClient[0])) {
					msgObject.setUsertext(listsendClient[0]);
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					// messages.add(new MsgObject(msgObject));
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
				}

				// MT 4
				// Send MT4 cho khach hang
				String sendtoClient = "Ngoai nhung BH ban vua nhan duoc, van con hang ngan BH khac tren "
						+ numberid
						+ ".De tiep tuc nhan duoc ma so BH.Soan: "
						+ last_msg;
				msgObject.setUsertext(sendtoClient);
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				// messages.add(new MsgObject(msgObject));
				DBUtil.sendMT(msgObject);
				Thread.sleep(500);
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

	/* ghi lai dsach khach hang */
	private static boolean saveClient(String userid, String operator,
			String keyword, String serviceid, Timestamp timesend,
			BigDecimal requestid) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_list_customer( userid, operator, keyword, serviceid, timesend, requestid) VALUES ('"
					+ userid
					+ "','"
					+ operator.toUpperCase()
					+ "','"
					+ keyword
					+ "','"
					+ serviceid.toUpperCase()
					+ "','"
					+ timesend
					+ "','" + requestid + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_list_customer");
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
							lastid = lastid + "'"+rs.getString(2)+"'";
						} else {
							lastid = lastid + ",'" + rs.getString(2)+"'";
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

			/* tach thanh 2*/
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

	//cap nhat danh sach khach hang
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
