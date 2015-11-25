package advice;

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
import java.util.Random;
import java.util.Vector;

import services.textbases.VnnLinksTextbaseTicket;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * ・</pre>
 * 
 * @author Vietnamnet I-Com TrungVD
 * @version 1.0
 */
public class Nhaccho extends ContentAbstract {

	/* First String trước các bản tin gửi */
	private static String viettelHot = "Cai dat nhac cho: B1: Soan DK gui 1221. B2 soan BH maso gui 1221. D/s ma so cua ";
	private static String vinaphoneHot = "Cai dat nhac cho: B1: Soan DK gui 9194 .B2: TUNE  maso gui 9194.D/s ma so cua ";
	private static String mobifoneHot = "Cai dat nhac cho: B1: Soan DK gui 9224. B2: CHON  maso gui 9224.D/s ma so cua ";
	private String[] musicHot_Viettel = { "100 yeu em: ma so 633395",
			"Cau vong khuyet: ma so 633519", "Con duong mua: ma so 633407",
			"Dong thoi gian: ma so 633655", "Let you go: ma so 633429",
			"Gia vo yeu : ma so 633423", "Don coi 1: ma so 633364",
			"The gioi to that to: ma so 633448",
			"Hot boy Thue bao het tien: ma so 63318",
			"Tai em yeu don phuong: ma so 633902" };
	private static final String[] musicHot_Vinaphone = {
			"Hoa Thien Dieu: ma so 750124", "Con duong mua: ma so 750077",
			"Don coi: ma so 750069", "Gia vo yeu: ma so 750057",
			"Lac loi: ma so 750046", "Let you go: ma so 750550",
			"Loi nguyen: ma so 750041", "Doi mat: ma so 750070",
			"Ngoi nha tuyet trang: ma so 750137",
			"Tu tin de dung vung: ma so 750161" };
	private static final String[] musicHot_Mobifone = {
			"Let you go: ma so 5201094", "Hoa Thien Dieu: ma so 5201026",
			"Neu: ma so 5201014", "Boi roi: ma so 520364",
			"Loi nguyen: ma so 520375", "Con duong mua: ma so 520369",
			"Doi mat: ma so 520370", "Boi roi: ma so 520364",
			"Don coi: ma so 520371", "Ngoi nha tuyet trang: ma so 520971" };

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String sKeyword = msgObject.getKeyword();

			/* Kiểm tra xem thuê bao của khách hàng */
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
				msgObject
						.setUsertext("He thong cua chung toi chi ho tro tren 3 mang Viettel, Mobifone va Vinaphone.");
				msgObject.setMsgtype(1);
				messages.add(msgObject);
				return messages;
			}

			// Lấy số thuê bao người gửi
			String userid = msgObject.getUserid();
			/* Lấy nội dung của người gửi đến */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext()
					.toUpperCase());
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");

			String serviceid = msgObject.getServiceid();

			/*
			 * Nếu đăng ký xem mã số bài hát MC tenbaihat gửi 8351 hoặc MC
			 * tencasy gửi 8351
			 */
			if (sTokens.length > 1) {

				// String subTokens = userText.substring(3);
				String subTokens = "";
				for (int k = 1; k < sTokens.length; k++) {
					subTokens = subTokens + sTokens[k] + " ";
				}

				Util.logger.info("SubTokens : " + subTokens);

				String subTokens2 = subTokens.replace(" ", "");
				String resultListofSinger = findListofMusic(subTokens2, infoid);

				/* Nếu có bài hát của ca sỹ */
				if (!"".equalsIgnoreCase(resultListofSinger)) {

					if ("viettel".equalsIgnoreCase(infoid)) {
						resultListofSinger = viettelHot + subTokens2 + ": "
								+ resultListofSinger;
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						resultListofSinger = vinaphoneHot + subTokens2 + ": "
								+ resultListofSinger;
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						resultListofSinger = mobifoneHot + subTokens2 + ": "
								+ resultListofSinger;
					}

					// Gửi tin nhắn mã số các bài hát của ca sỹ đến KH
					if (resultListofSinger.length() > 160) {
						String[] result = null;
						result = splitString(resultListofSinger);
						for (int i = 0; i < 2; i++) {
							if (!"".equalsIgnoreCase(result[i])) {
								msgObject.setUsertext(result[i]);
								if (i == 0) {
									msgObject.setMsgtype(1);

								} else {
									msgObject.setMsgtype(0);
								}
								msgObject.setContenttype(0);
								messages.add(new MsgObject(msgObject));
							}
						}
					} else {
						msgObject.setUsertext(resultListofSinger);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
					}
				} else {
					/*
					 * Nếu không có bài hát nào của ca sỹ thì coi như KH gửi tin
					 * tìm bài hát
					 */
					String[] resultMusicID = new String[2];
					// 0 là tên bài hát, 1 là mã bài hát
					resultMusicID = findMusicID(subTokens2, infoid);

					if (!"".equalsIgnoreCase(resultMusicID[1])) {
						Random iRandom = new Random();
						int imusic = iRandom.nextInt(10);
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
									messages.add(new MsgObject(msgObject));
								}
							}

						} else {

							msgObject.setUsertext(resultMusicID[1]);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							messages.add(new MsgObject(msgObject));
						}
						return messages;

					} else {

						String info2Client = "";
						Random iRandom = new Random();
						int imusic = iRandom.nextInt(10);
						if ("viettel".equalsIgnoreCase(infoid)) {
							info2Client = "Oppz! Bai hat ban thich hien tai chua co trong he thong. He thong se tra ve cho ban ma so ngay sau khi bai hat duoc cap nhat. MS HOT: "
									+ musicHot_Viettel[imusic];
						} else if ("vinaphone".equalsIgnoreCase(infoid)) {
							info2Client = "Oppz! Bai hat ban thich hien tai chua co trong he thong. He thong se tra ve cho ban ma so ngay sau khi bai hat duoc cap nhat. MS HOT: "
									+ musicHot_Vinaphone[imusic];
						} else if ("mobifone".equalsIgnoreCase(infoid)) {
							info2Client = "Oppz! Bai hat ban thich hien tai chua co trong he thong. He thong se tra ve cho ban ma so ngay sau khi bai hat duoc cap nhat. MS HOT: "
									+ musicHot_Mobifone[imusic];
						}
						/*
						 * Lưu lại thông tin danh sách khách hàng chưa được đáp
						 * ứng yêu cầu
						 */
						saveClient(userid, msgObject.getMobileoperator(),
								subTokens, serviceid, msgObject.getTTimes(),
								msgObject.getRequestid());

						msgObject.setUsertext(info2Client);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;
					}
				}
			} else {
				/* Nếu khách hàng nt MC gửi 8351 */
				String info2Client = "Ban da nhan tin sai cu phap. De lay ma so bai hat ban yeu thich: Soan "
						+ sKeyword
						+ " tenbai gui "
						+ serviceid
						+ ".Cap nhat nhac HOT hang ngay soan MTA HOT gui 8751.";
				msgObject.setUsertext(info2Client);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			return messages;

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
	}

	/* Ghi lại danh sách các khách hàng hệ thống không đáp ứng được yêu cầu */
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

			String sqlInsert = "INSERT INTO icom_list_client( userid, operator, keyword, serviceid, timesend, requestid) VALUES ('"
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
				Util.logger.error("Insert into icom_list_client");
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

	/* Tìm mã số của bài hát xxx */
	private static String[] findMusicID(String find, String infoid) {

		String[] result = new String[2];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		for (int i = 0; i < 2; i++) {
			result[i] = "";
		}

		try {
			connection = dbpool.getConnectionGateway();
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

	/* Tìm list ca sỹ */
	private static String findListofMusic(String singer, String infoid) {

		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT musicname_sms, musicid FROM icom_music WHERE ( upper(singer) = '"
					+ singer.toUpperCase()
					+ "') AND ( upper(operator) = '"
					+ infoid.toUpperCase() + "')";

			Util.logger.info("SEARCH SONG MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = result + rs.getString(1) + " " + rs.getString(2)
							+ ";";
					if (result.length() > 260) {
						return result;
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

	/* Chia thành 2 MT */
	private static String[] splitString(String splitS) {
		String[] result = new String[2];
		boolean resultBoolean = false;
		int i = 160;
		int j = 0;
		String tempString = splitS;

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

		/* Tách chuỗi thứ 2 */
		while (!(resultBoolean)) {
			// Nếu là ; và <160
			if ((tempString.charAt(i) == ';') && ((i - j) <= 160)) {
				result[1] = tempString.substring(j, i);
				resultBoolean = true;
			}
			i--;
		}

		return result;

	}

}
