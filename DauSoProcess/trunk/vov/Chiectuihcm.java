package vov;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class Chiectuihcm extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		try {

			// Get info of client
			String user_id = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String service_id = msgObject.getServiceid();
			Timestamp receive_date = msgObject.getTTimes();
			String keywords = msgObject.getKeyword();
			info = replaceAllWhiteWithOne(info);
			String mobile_operator = msgObject.getMobileoperator();
			String database_customer = "icom_chiectui_customerhcm";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();

			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				database_customer = getString(_option, "database_customer",
						database_customer);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			//
			String mtContent = "";
			if ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator()
					.toUpperCase())) {

				// Hoan tien mang sfone
				msgObject
						.setUsertext("Hien tai dich vu chua ho tro mang cua ban.Vui long quay tro lai sau.DTHT 1900571566");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			Hashtable numberDelay = Threadchiectuihcm.lastestnumber.SECRET_NUMBER;

			String session = getSECRET_NUMBER(numberDelay, "session");
			Util.logger.info("Session : " + session);
			String start_time = getSECRET_NUMBER(numberDelay, "start");
			Util.logger.info("Start Time : " + start_time);
			String end_time = getSECRET_NUMBER(numberDelay, "end");
			Util.logger.info("End Time : " + end_time);
			String isProcess = getSECRET_NUMBER(numberDelay, "isprocess");
			Util.logger.info("is Process :" + isProcess);
			String current = getSECRET_NUMBER(numberDelay, "current");
			Util.logger.info("Current: " + current);
			String current_time = getSECRET_NUMBER(numberDelay, "current_times");
			Util.logger.info("current_time: " + current_time);
			String current_question = getSECRET_NUMBER(numberDelay,
					"current_question");
			Util.logger.info("current_question: " + current_question);

			if (Threadchiectuihcm.lastestnumber.isProcess == 0) {

				// Phien choi chua bat dau
				msgObject
						.setUsertext("Phien choi tiep theo chua bat dau.Hay quay tro lai vao 18h00 thu 3,5,7 trên VOV Giao thông 91Mhz.DTHT 1900571566.");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else if ((Threadchiectuihcm.lastestnumber.isProcess == 1)) {

				String[] sTokens = info.split(" ");
				// tin sai cu phap
				if (sTokens.length < 2) {
					mtContent = "Ban nhan tin sai cu phap.Soan tin "
							+ keywords
							+ " dapan gui"
							+ service_id
							+ "de tang co hoi tro thanh nguoi choi chinh va gianh giai phu cua chuong trinh.DTHT:1900571566";
					msgObject.setUsertext(mtContent);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {

					if (!checkUser(database_customer, user_id, session)) {
						saveCustomer(database_customer, session, user_id,
								mobile_operator);
					}

					String result = sTokens[1];
					Util.logger.info("Result: " + result);

					// Xem dang o block thu may
					int status1 = Integer.parseInt(current_question);

					Util.logger.info("Dang o block thu: " + status1);

					int time1 = getInfoCustomer(database_customer, user_id,
							session, status1);

					if ("".equalsIgnoreCase(result)) {

						updateInfoCustomer(database_customer, user_id, session,
								status1, time1, 0, 0);
						mtContent = "Ban nhan tin sai cu phap.Soan tin "
								+ keywords
								+ " dapan gui"
								+ service_id
								+ "de tang co hoi tro thanh nguoi choi chinh va gianh giai phu cua chuong trinh.DTHT:1900571566";
						msgObject.setUsertext(mtContent);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
						return messages;
					} else {

						int time = 0;
						time = getInfoCustomer(database_customer, user_id,
								session, status1);

						if (!result.equalsIgnoreCase(current)) {
							// Update MO
							updateInfoCustomer(database_customer, user_id,
									session, status1, time, 0, 0);

							msgObject
									.setUsertext("Ban tra loi chua chinh xac.Nhanh tay soan "
											+ keywords
											+ " dapan gui"
											+ service_id
											+ "de tang co hoi tro thanh nguoi choi chinh va gianh giai phu cua chuong trinh");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));
							return messages;
						} else {

							// Kiem tra xem da tra loi dung hay chua lay tg

							if (time > 0) {

								// Tra loi dung
								updateInfoCustomer(database_customer, user_id,
										session, status1, time, 0, 0);

								msgObject
										.setUsertext("Chuc mung ban da tra loi dung.Ban co co hoi de tro thanh nguoi choi chinh,hay san sang tham gia.Tra loi them cac cau hoi de gianh giai phu cua chuong trinh");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
								return messages;
							} else {
								// update thoi gian tra loi dung
								int time_user = 0;
								String block = current_time;

								time_user = calculateTime(Timestamp
										.valueOf(block), receive_date);

								updateInfoCustomer(database_customer, user_id,
										session, status1, time_user, time_user,
										1);
								// Tra tin chuc mung tra loi dung
								msgObject
										.setUsertext("Chuc mung ban da tra loi dung.Ban co co hoi de tro thanh nguoi choi chinh,hay san sang tham gia.Tra loi them cac cau hoi de gianh giai phu cua chuong trinh");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
								return messages;
							}

						}

					}
				}
			}

			return messages;
		} catch (Exception ex) {
			Util.logger.error("Error:" + ex.toString());
			return null;
		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
	}

	// Replace all space by one space
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

	// Check phone number
	private boolean checkNumber(String numberString) {
		try {
			long number = Integer.parseInt(numberString);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	// Kiem tra xem co khach hang nay chua
	private boolean checkUser(String database_customer, String user_id,
			String session) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlSelect = "SELECT user_id FROM " + database_customer
					+ " WHERE session='" + session + "' AND user_id='"
					+ user_id + "'";
			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					Util.logger.error("SQL Select: " + sqlSelect);
					return true;
				}
			}
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
		return false;
	}

	// Get Info Customer
	private int getInfoCustomer(String database_customer, String user_id,
			String session, int status) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return 0;
			}

			String sqlSelect = "";

			sqlSelect = "SELECT block" + status + " FROM " + database_customer
					+ " WHERE session='" + session + "' AND user_id='"
					+ user_id + "'";

			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					Util.logger.info("SQL Select: " + sqlSelect);
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return 0;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return 0;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return 0;
	}

	// Get total time
	private int getTotaltime(String database_customer, String user_id,
			String session) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return 0;
			}

			String sqlSelect = "SELECT total_time FROM " + database_customer
					+ " WHERE session='" + session + "' AND user_id='"
					+ user_id + "'";
			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					Util.logger.info("SQL Select: " + sqlSelect);
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return 0;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return 0;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return 0;
	}

	// Get Info Customer
	private boolean updateInfoCustomer(String database_customer,
			String user_id, String session, int status, int time,
			int time_user, int correct) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlUpdate = "";
			if (status > 0)
				sqlUpdate = "UPDATE " + database_customer + " SET block"
						+ status + "=" + time
						+ ", total_mo=total_mo+1, total_time=total_time+"
						+ time_user + ", total_correct=total_correct+"
						+ correct + " WHERE user_id='" + user_id
						+ "' AND session='" + session + "'";
			else if (status == 0) {
				sqlUpdate = "UPDATE " + database_customer + " SET block0="
						+ time + ", total_mo=total_mo+1 WHERE user_id='"
						+ user_id + "' AND session='" + session + "'";
			} else {
				sqlUpdate = "UPDATE " + database_customer + " SET blockdk="
						+ time + ", total_mo=total_mo+1 WHERE user_id='"
						+ user_id + "' AND session='" + session + "'";
			}
			Util.logger.info("SQL Update: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {

				return true;
			}
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
		return false;
	}

	// Ghi lai danh sach khach hang
	private static boolean saveCustomer(String database_customer,
			String session, String user_id, String mobile_operator) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO " + database_customer
					+ "( session, user_id, mobile_operator)VALUES ('" + session
					+ "','" + user_id + "','" + mobile_operator + "')";

			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert Into : " + database_customer);
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

	public static String getSECRET_NUMBER(Hashtable numberDelay,
			String secretNumber) {
		String retobj = "";

		try {
			retobj = (String) numberDelay.get(secretNumber);
			return retobj;

		} catch (Exception e) {
		}

		return "";
	}

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		if (sISDN.startsWith("84")) {
			sISDN = "0" + sISDN.substring(2);
			return sISDN;
		}
		if (!sISDN.startsWith("0")) {
			sISDN = "0" + sISDN;
		}
		return sISDN;

	}

	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "###");

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

	// Get number guess of Customer
	public int getValidInput(String sInput) {
		int result = -1;
		sInput = sInput.charAt(0) + "";

		try {
			int input = Integer.parseInt(sInput);
			result = input;
		} catch (Exception ex) {
			return result;
		}

		return result;
	}

	// Calculate time play game of customer
	private int calculateTime(Timestamp start_time, Timestamp end_time) {
		int total = 0;
		long start = start_time.getTime();
		long end = end_time.getTime();
		total = (int) (end - start) / 1000;
		return total;
	}
}
