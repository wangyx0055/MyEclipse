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
public class Nhacchohot extends ContentAbstract {

	/* First String trước các bản tin gửi */
	private static String viettelHot = "Cai dat nhac cho soan: BH maso gui 1221. D/s ma so cua ";
	private static String vinaphoneHot = "Cai dat nhac cho soan: TUNE maso gui 9194.Danh sach ma so bai hat cua ";
	private static String mobifoneHot = "Cai dat nhac cho soan: CHON maso gui 9224.Danh sach ma so bai hat cua ";

	// Lưu trữ các ticket
	private String directory = "C:/ticket";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		int MILI_SECOND = 1000 * 60 * 60 * 24;

		try {
			Collection messages = new ArrayList();

			String infoid = "";
			Timestamp timeEndDate = msgObject.getTTimes();

			// Lựa chọn nơi ghi vào ticket.
			directory = Constants._prop.getProperty("dirtextbase", directory);

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

			/* Nếu nhập đúng cú pháp */
			if (sTokens.length > 1) {

				String subTokens = "";
				for (int k = 1; k < sTokens.length; k++) {
					subTokens = subTokens + sTokens[k] + " ";
				}

				// Tên nhóm viết liền
				String subTokens2 = subTokens.replace(" ", "");
				String[] resultFindMusicHot = new String[5];
				resultFindMusicHot = findHotMusic(subTokens2, infoid);

				// Lấy nội dung trả lại cho khách hàng
				String userticket = msgObject.getUserid();
				String sServiceid = msgObject.getServiceid();
				String telcoticket = msgObject.getMobileoperator();
				String service = msgObject.getKeyword();
				BigDecimal requestid = msgObject.getRequestid();
				int l = 0;

				// Gửi trả lại tin cho khách hàng lần đầu tiên
				// Start

				if (!"".equals(resultFindMusicHot[0])) {
					
					if ("viettel".equalsIgnoreCase(infoid)) {
						resultFindMusicHot[l] = viettelHot + subTokens2 + ": "
								+ resultFindMusicHot[l];
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						resultFindMusicHot[l] = vinaphoneHot + subTokens2
								+ ": " + resultFindMusicHot[l];
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						resultFindMusicHot[l] = mobifoneHot + subTokens2 + ": "
								+ resultFindMusicHot[l];
					}

					// Nếu list các nhóm >160 ký tự
					if (resultFindMusicHot[l].length() >= 160) {

						String[] result = new String[2];
						result = splitString(resultFindMusicHot[l]);
						Util.logger.info("RESULT111111" + result[0]);
						Util.logger.info("RESULT111111" + result[1]);
						for (int j = 0; j < 2; j++) {
							if (!"".equalsIgnoreCase(result[j])) {
								msgObject.setUsertext(result[j]);
								if (j == 0) {
									msgObject.setMsgtype(1);
								} else {
									msgObject.setMsgtype(0);
								}
								msgObject.setContenttype(0);
								messages.add(new MsgObject(msgObject));
							}
						}
						// return messages;
					} else {

						msgObject.setUsertext(resultFindMusicHot[0]);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));

						Util.logger
								.error("CHUA DU MT CHO 5 NGAY NHU DA QUANG CAO");
						// return messages;
					}

					// / END

					// Từ tin hot ngày thứ 2 phải ghi vào ticket
					// Start
					for (int i = 1; i < resultFindMusicHot.length; i++) {
						if (!"".equals(resultFindMusicHot[i])) {

							// Thêm nội dung cố định trước các MT
							if ("viettel".equalsIgnoreCase(infoid)) {
								resultFindMusicHot[i] = viettelHot + subTokens2
										+ ": " + resultFindMusicHot[i];
							} else if ("vinaphone".equalsIgnoreCase(infoid)) {
								resultFindMusicHot[i] = vinaphoneHot
										+ subTokens2 + ": "
										+ resultFindMusicHot[i];
							} else if ("mobifone".equalsIgnoreCase(infoid)) {
								resultFindMusicHot[i] = mobifoneHot
										+ subTokens2 + ": "
										+ resultFindMusicHot[i];
							}

							if (resultFindMusicHot[i].length() >= 160) {

								// Phần này để tạo ticket
								String[] result = new String[2];
								result = splitString(resultFindMusicHot[i]);

								String date2send = "";
								// Lấy sau thời gian gửi tin 1 ngày
								Date date = new Date(timeEndDate.getTime()
										+ MILI_SECOND * i);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								date2send = getCalendarString080000(calendar);

								for (int k = 0; k < 2; k++) {
									// Tạo ticket

									VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
											.createReservationTicket(result[k],
													userticket, sServiceid,
													date2send, telcoticket,
													telcoticket, service,
													requestid.toString());

									VnnLinksTextbaseTicket
											.storeReservationTicket(ticket,
													directory, userticket);

								}

							} else {

								// Chuỗi ngày thứ i bé hơn 160;

								String date2send = "";
								// Lấy sau thời gian gửi tin 1 ngày
								Date date = new Date(timeEndDate.getTime()
										+ MILI_SECOND * i);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								date2send = getCalendarString080000(calendar);

								// Tạo ticket

								VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
										.createReservationTicket(
												resultFindMusicHot[i],
												userticket, sServiceid,
												date2send, telcoticket,
												telcoticket, service, requestid
														.toString());

								VnnLinksTextbaseTicket.storeReservationTicket(
										ticket, directory, userticket);

							}
						}
					}
					return messages;
				} else {
					String info2Client = "Oppz!"
							+ subTokens
							+ " ban thich hien tai  chua co trong he thong. He thong se tra ve ngay sau khi "
							+ subTokens
							+ " duoc cap nhat. Hang ngay nhan ms bai hat HOT soan MC HOT gui"
							+ serviceid;
					/*
					 * Lưu lại danh sách KH không đáp ứng được yêu cầu
					 */

					saveClient(userid, msgObject.getMobileoperator(),
							subTokens, serviceid, msgObject.getTTimes(),
							msgObject.getRequestid());

					// Neu info2Client.length >160 thi tach thanh 2 MT
					if (info2Client.length() > 160) {
						String[] subString = new String[2];
						subString[0] = info2Client.substring(0, 159);
						subString[1] = info2Client.substring(159);
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

						msgObject.setUsertext(info2Client);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
					}
					return messages;
				}
				// return messages;
			} else {

				String info2Client = "Ban da nhan tin sai cu phap.De hang ngay nhan ma so bai hat soan MC tennhom gui "
						+ serviceid + ".VD MC HOT gui " + serviceid;
				msgObject.setUsertext(info2Client);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(),
					"Exception:3534535" + e.getMessage());
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

	/* Tìm nhạc HOT */

	private static String[] findHotMusic(String find, String infoid) {

		String[] result = new String[5];
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

			String sqlSelect = "SELECT musicname_sms, musicid"
					+ " FROM icom_music WHERE ( upper(operator)= '"
					+ infoid.toUpperCase() + "') AND ( upper(group1) = '"
					+ find + "' OR upper(group2) = '" + find
					+ "' OR	upper(group3) = '" + find
					+ "' OR upper(group4) = '" + find
					+ "' OR	upper(group5) = '" + find + "')";
			String sqlOrder = " ORDER BY rand() LIMIT 1000";

			sqlSelect = sqlSelect + sqlOrder;

			Util.logger.info(" SEARCH HOT MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			for (int j = 0; j < 5; j++) {
				result[j] = "";
			}
			int i = 0;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {

					result[i] = result[i] + rs.getString(1) + " "
							+ rs.getString(2) + ";";

					if (result[i].length() > 260) {
						Util.logger.info("KET QUA :" + result[i]);
						Util.logger.info("LENGTH :" + result[i].length());
						i = i + 1;
						if (i > 4) {
							return result;
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

	/*
	 * Chuyển sang định dạng 08:00:00 - Tin nhắn sẽ đc trả về cho khách hàng vào
	 * 8 h sáng hàng ngày
	 */
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
}
