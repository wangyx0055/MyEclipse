package advice;

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

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class gamech extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		String game_ch_hist = "game_ch_hist";
		String game_ch = "game_ch";
		String game_ch_result = "game_ch_result";
		String gameid = "";
		try {

			// Lay option
			HashMap _option = new HashMap();
			HashMap _option1 = new HashMap();

			String options1 = keyword.getOptions();
			_option1 = getParametersAsString(options1);
			gameid = ((String) _option1.get("game_id")).toUpperCase();
			Util.logger.info("game id: " + gameid);
			String options = getPhien(gameid);
			Util.logger.info("Gio mo phien : " + options);

			_option = getParametersAsString(options);
			String phien1 = ((String) _option.get("phien1")).toUpperCase();
			Util.logger.info("Phien 1: " + phien1);
			String phien2 = ((String) _option.get("phien2")).toUpperCase();
			Util.logger.info("Phien 2: " + phien2);

			String phien3 = ((String) _option.get("phien3")).toUpperCase();
			Util.logger.info("Phien 3: " + phien3);

			Timestamp timeReceived = msgObject.getTTimes();
			String usertext = msgObject.getUsertext();
			String userid = msgObject.getUserid();
			String serviceid = msgObject.getServiceid();
			String sKeyword = msgObject.getKeyword();
			String operator = msgObject.getMobileoperator();
			usertext = replaceAllWhiteWithOne(usertext);
			String[] sTokens = usertext.split(" ");

			String wrongSytax = "Ban da nhan tin sai cu phap. Soan tin "
					+ sKeyword
					+ " X gui "
					+ serviceid
					+ " de tham gia phien dau gia. Trong do X la so tien tu 0 den 9990";

			if (sTokens.length < 2) {

				// Thong bao gui sai cu phap
				msgObject.setUsertext(wrongSytax);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;

			} else {
				int costProduct = 0;
				try {
					costProduct = Integer.parseInt(sTokens[1]);
				} catch (NumberFormatException ex) {

					// Thong bao gui sai cu phap
					msgObject.setUsertext(wrongSytax);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;

				}

				if ((costProduct < 0) || (costProduct > 9990)) {
					msgObject.setUsertext(wrongSytax);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				}

				// Tin nhan dung ???
				// Thong bao cho khach hang dang o phien thu ?
				Date date = new Date(timeReceived.getTime());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				Util.logger.info("Calendar : " + calendar);
				int phien = checkSession(calendar, phien1, phien2, phien3);
				int phien_real = phien;
				if (phien == 4) {
					phien_real = 1;
				}
				String reply2Client = "Ban da tra gia san pham la "
						+ costProduct + ". Ban dang o phien thu " + phien_real
						+ ". Chuc ban may man.";
				msgObject.setUsertext(reply2Client);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				// Save Client
				saverequest(game_ch_hist, userid, serviceid, operator,
						costProduct, usertext, timeReceived, sKeyword,
						phien_real);

				saverequest(game_ch, userid, serviceid, operator, costProduct,
						usertext, timeReceived, sKeyword, phien_real);

				// Get Cost Highest and unique;
				int maxCost = getUnique(game_ch, phien_real);
				Util.logger.info("Max cost : " + maxCost);
				// 
				Timestamp timeclose = getTimeClose(timeReceived, phien, phien1,
						phien2, phien3);
				Util.logger.info("Time Close:" + timeclose);

				// Lay gia tri dang la ket qua
				int winCost = getCostUnique(game_ch_result, timeclose,
						phien_real);
				Util.logger.info("win Cost: " + winCost);

				if (winCost < 0) {
					saveGameCHwin(game_ch_result, userid, serviceid, operator,
							costProduct, usertext, timeReceived, sKeyword,
							phien_real, timeclose);
				} else {

					if (maxCost < 0) {
						deleteClient(game_ch_result, timeclose);
					} else {
						String[] result = new String[6];
						result = getInfo(game_ch, phien_real, maxCost);
						updateHighCost(game_ch_result, result[0], result[1],
								result[2], maxCost, result[3], Timestamp
										.valueOf(result[4]), result[5],
								phien_real, timeclose);

					}
				}
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
		return messages;
	}

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

	// Append Time
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
		sb.append(" " + time);

		return (sb.toString());
	}

	// Get time
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

	// Kiem tra xem dang o phien nao ??
	public static int checkSession(Calendar calendar, String phien1,
			String phien2, String phien3) {
		StringBuffer sb = new StringBuffer();
		int i;
		i = calendar.get(Calendar.HOUR_OF_DAY);
		if (i < 10)
			sb.append("0");
		sb.append(i + ":");
		i = calendar.get(Calendar.MINUTE);
		if (i < 10)
			sb.append("0");
		sb.append(i + ":");
		i = calendar.get(Calendar.SECOND);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		String time = sb.toString();
		Util.logger.info("Time: " + time);
		Util.logger.info("Phien 1: " + phien1);
		Util.logger.info("Phien 2: " + phien2);
		Util.logger.info("Phien 3: " + phien3);
		if (time.compareTo(phien3) > 0) {
			return 4;
		}
		if ((time.compareTo(phien1) < 0)) {
			return 1;
		} else if ((time.compareTo(phien1) > 0) && time.compareTo(phien2) < 0) {
			return 2;
		}
		return 3;
	}

	private static boolean saverequest(String dbcontent, String userid,
			String serviceid, String operator, int costProduct,
			String usertext, Timestamp time, String commandcode, int phien) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO "
					+ dbcontent
					+ "(GAME_ID, SERVICE_NUMBER, MSISDN, X, NOI_DUNG_TIN_NHAN, TIMESEND, COMMAND_CODE, PHIEN_ID) VALUES (?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setString(2, serviceid);
			statement.setString(3, operator);
			statement.setInt(4, costProduct);
			statement.setString(5, usertext);
			statement.setTimestamp(6, time);
			statement.setString(7, commandcode);
			statement.setInt(8, phien);

			statement.executeUpdate();
			Util.logger.info("SQL INSERT: " + sqlInsert);
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

	private static int getUnique(String dbcontent, int phien) {

		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		int total = 0;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return -1;
			}

			String sqlSelect = "SELECT count(*), x, phien_id FROM " + dbcontent
					+ " WHERE PHIEN_ID = " + phien + " GROUP BY x desc";
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SELECT:" + sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					total = rs.getInt(1);
					if (total == 1) {
						result = rs.getInt(2);
						return result;
					}
				}
			}

			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error1: " + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error2: " + e.toString());
			return result;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static boolean deleteClient(String dbcontent, Timestamp timeclose) {

		Connection connection = null;
		PreparedStatement statement = null;
		boolean result = true;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		int total = 0;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlDelete = "DELETE FROM " + dbcontent
					+ " WHERE TIME_CLOSE = '" + timeclose + "'";
			statement = connection.prepareStatement(sqlDelete);
			Util.logger.info("Delete:" + sqlDelete);
			if (statement.execute()) {
				return result;
			}

			return false;
		} catch (SQLException e) {
			Util.logger.error(": Error1: " + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error2: " + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static int getCostUnique(String dbcontent, Timestamp timeclose,
			int phien) {

		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			// Nguoi doat giai theo phien va thoi gian ket thuc ??
			String sqlSelect = "SELECT X FROM " + dbcontent
					+ " WHERE PHIEN_ID =" + phien + " AND TIME_CLOSE = '"
					+ timeclose + "'";
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SELECT:" + sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					int cost = rs.getInt(1);
					return cost;
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
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static String getPhien(String gameid) {

		Connection connection = null;
		PreparedStatement statement = null;
		String result = "";
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			// Nguoi doat giai theo phien va thoi gian ket thuc ??
			String sqlSelect = "SELECT content FROM icom_textbase_data "
					+ " WHERE upper(gameid) ='" + gameid.toUpperCase() + "'";
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SELECT:" + sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
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
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	// Lay ngay ket thuc phien giao dich
	@SuppressWarnings("static-access")
	private static Timestamp getTimeClose(Timestamp timeReceived, int phien,
			String phien1, String phien2, String phien3) {

		long milisecond = 1000 * 60 * 60 * 24;
		Timestamp temp = null;
		String timeClose = "";
		Util.logger.info("Time Received : " + timeReceived);

		Calendar calendar = Calendar.getInstance();
		int i = calendar.get(Calendar.DAY_OF_WEEK);
		Date date = new Date(timeReceived.getTime());
		calendar.setTime(date);

		Util.logger.info("DAY OF WEEK: " + i);
		if (i < 5) {
			if (phien == 1) {
				timeClose = getCalendarString(calendar, phien1);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 2) {
				timeClose = getCalendarString(calendar, phien2);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 3) {
				timeClose = getCalendarString(calendar, phien3);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 4) {
				Date date1 = new Date(timeReceived.getTime() + milisecond);
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(date1);
				timeClose = getCalendarString(calendar1, phien1);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}
		} else if (i == 5) {
			if (phien == 1) {
				timeClose = getCalendarString(calendar, phien1);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 2) {
				timeClose = getCalendarString(calendar, phien2);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 3) {
				timeClose = getCalendarString(calendar, phien3);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 4) {
				Date date1 = new Date(timeReceived.getTime() + (9 - i)
						* milisecond);
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(date1);
				timeClose = getCalendarString(calendar1, phien1);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}
		} else {
			Date date1 = new Date(timeReceived.getTime() + (9 - i) * milisecond);
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(date1);
			timeClose = getCalendarString(calendar1, phien1);
			temp = Timestamp.valueOf(timeClose);
			return temp;
		}
		return temp;
	}

	private static boolean updateHighCost(String dbcontent, String userid,
			String serviceid, String operator, int costProduct,
			String usertext, Timestamp timesend, String commandcode, int phien,
			Timestamp timeclose) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update thời gian kết thúc quá trình gửi tin nhắn
			String sqlUpdateEndDate = "UPDATE " + dbcontent
					+ " SET GAME_ID = '" + userid.toUpperCase()
					+ "', SERVICE_NUMBER ='" + serviceid.toUpperCase()
					+ "', MSISDN ='" + operator.toUpperCase() + "', X ="
					+ costProduct + ", NOI_DUNG_TIN_NHAN ='"
					+ usertext.toUpperCase() + "', TIMESEND ='" + timesend
					+ "', COMMAND_CODE ='" + commandcode.toUpperCase()
					+ "', PHIEN_ID =" + phien + ", TIME_CLOSE ='" + timeclose
					+ "' WHERE TIME_CLOSE ='" + timeclose + "'";
			Util.logger.info(" UPDATE DATE: " + sqlUpdateEndDate);
			statement = connection.prepareStatement(sqlUpdateEndDate);
			if (statement.execute()) {
				Util.logger.error("Update end date of " + userid
						+ " to icom_giamcan");
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

	private static boolean saveGameCHwin(String dbcontent, String userid,
			String serviceid, String operator, int costProduct,
			String usertext, Timestamp timesend, String commandcode, int phien,
			Timestamp timeclose) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO "
					+ dbcontent
					+ "(GAME_ID, SERVICE_NUMBER, MSISDN, X, NOI_DUNG_TIN_NHAN, TIMESEND, COMMAND_CODE, PHIEN_ID, TIME_CLOSE) VALUES (?,?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setString(2, serviceid);
			statement.setString(3, operator);
			statement.setInt(4, costProduct);
			statement.setString(5, usertext);
			statement.setTimestamp(6, timesend);
			statement.setString(7, commandcode);
			statement.setInt(8, phien);
			statement.setTimestamp(9, timeclose);

			statement.executeUpdate();
			Util.logger.info("SQL INSERT: " + sqlInsert);
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

	private static String[] getInfo(String dbcontent, int phien, int maxCost) {

		Connection connection = null;
		PreparedStatement statement = null;
		String[] result = new String[6];
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			// Nguoi doat giai theo phien va thoi gian ket thuc ??
			String sqlSelect = "SELECT GAME_ID, SERVICE_NUMBER, MSISDN, NOI_DUNG_TIN_NHAN, TIMESEND, COMMAND_CODE FROM "
					+ dbcontent
					+ " WHERE PHIEN_ID ="
					+ phien
					+ " AND X = "
					+ maxCost;
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SELECT:" + sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
					result[2] = rs.getString(3);
					result[3] = rs.getString(4);
					result[4] = rs.getString(5);
					result[5] = rs.getString(6);
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error1:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error2:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static Timestamp getTimeClosePhien(Timestamp timeReceived,
			int phien, String phien1, String phien2, String phien3) {

		long milisecond = 1000 * 60 * 60 * 24;
		Timestamp temp = null;
		String timeClose = "";
		Util.logger.info("Time Received : " + timeReceived);

		Calendar calendar = Calendar.getInstance();
		int i = calendar.get(Calendar.DAY_OF_WEEK);
		Date date = new Date(timeReceived.getTime());
		calendar.setTime(date);

		Util.logger.info("DAY OF WEEK: " + i);
		if (i < 5) {
			if (phien == 1) {
				timeClose = getCalendarString(calendar, phien1);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 2) {
				timeClose = getCalendarString(calendar, phien2);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 3) {
				timeClose = getCalendarString(calendar, phien3);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 4) {
				Date date1 = new Date(timeReceived.getTime() + milisecond);
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(date1);
				timeClose = getCalendarString(calendar1, phien1);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}
		} else if (i == 5) {
			if (phien == 1) {
				timeClose = getCalendarString(calendar, phien1);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 2) {
				timeClose = getCalendarString(calendar, phien2);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 3) {
				timeClose = getCalendarString(calendar, phien3);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}

			if (phien == 4) {
				Date date1 = new Date(timeReceived.getTime() + (9 - i)
						* milisecond);
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(date1);
				timeClose = getCalendarString(calendar1, phien1);
				temp = Timestamp.valueOf(timeClose);
				return temp;
			}
		} else {
			Date date1 = new Date(timeReceived.getTime() + (9 - i) * milisecond);
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(date1);
			timeClose = getCalendarString(calendar1, phien1);
			temp = Timestamp.valueOf(timeClose);
			return temp;
		}
		return temp;
	}

}
