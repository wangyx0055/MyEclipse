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

public class Ochubimathcm extends ContentAbstract {

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
			String database_customer = "icom_ochubimathcm_customer";
			String stime = " 20h-21h  thu 2,4,6 ";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();

			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				database_customer = getString(_option, "database_customer",
						database_customer);
				stime = getString(_option, "time", stime);

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
			Hashtable numberDelay = Threadochubimathcm.lastestnumber.SECRET_NUMBER;

			String session = getSECRET_NUMBER(numberDelay, "session");
			Util.logger.info("Session : " + session);
			String start_time = getSECRET_NUMBER(numberDelay, "start");
			Util.logger.info("Start Time : " + start_time);
			String end_time = getSECRET_NUMBER(numberDelay, "end");
			Util.logger.info("End Time : " + end_time);
			String start_dk = getSECRET_NUMBER(numberDelay, "startdk");
			Util.logger.info("Startdk : " + start_dk);
			String finish_dk = getSECRET_NUMBER(numberDelay, "finishdk");
			Util.logger.info("Finishdk : " + finish_dk);
			String block1 = getSECRET_NUMBER(numberDelay, "block1");
			Util.logger.info("Block 1 : " + block1);
			String block2 = getSECRET_NUMBER(numberDelay, "block2");
			Util.logger.info("Block 2 : " + block2);
			String block3 = getSECRET_NUMBER(numberDelay, "block3");
			Util.logger.info("Block 3 : " + block3);
			String block4 = getSECRET_NUMBER(numberDelay, "block4");
			Util.logger.info("Block 4 : " + block4);
			String block5 = getSECRET_NUMBER(numberDelay, "block5");
			Util.logger.info("Block 5 : " + block5);
			String block6 = getSECRET_NUMBER(numberDelay, "block6");
			Util.logger.info("Block 6 : " + block6);
			String block7 = getSECRET_NUMBER(numberDelay, "block7");
			Util.logger.info("Block 7 : " + block7);
			String block8 = getSECRET_NUMBER(numberDelay, "block8");
			Util.logger.info("Block 8 : " + block8);
			String block9 = getSECRET_NUMBER(numberDelay, "block9");
			Util.logger.info("Block 9 : " + block9);
			String block10 = getSECRET_NUMBER(numberDelay, "block10");
			Util.logger.info("Block 10 : " + block10);
			String block11 = getSECRET_NUMBER(numberDelay, "block11");
			Util.logger.info("Block 11 : " + block11);
			String block12 = getSECRET_NUMBER(numberDelay, "block12");
			Util.logger.info("block12  : " + block12);
			String block13 = getSECRET_NUMBER(numberDelay, "block13");
			Util.logger.info("block13  : " + block13);
			String block14 = getSECRET_NUMBER(numberDelay, "block14");
			Util.logger.info("block14  : " + block14);
			String isProcess = getSECRET_NUMBER(numberDelay, "isprocess");
			Util.logger.info("is Process :" + isProcess);
			String current = getSECRET_NUMBER(numberDelay, "current");
			Util.logger.info("Current: " + current);

			if (Threadochubimathcm.lastestnumber.isProcess == 0) {

				// Phien choi chua bat dau
				msgObject
						.setUsertext("Chuong trinh chua bat dau.Hay quay lai vao chuong trinh lan sau vao "
								+ stime
								+ " hang tuan tren VOV giao thong 91MHz.DTHT : 1900571566.");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else if ((Threadochubimathcm.lastestnumber.isProcess == 1)) {

				String[] sTokens = info.split(" ");
				// tin sai cu phap
				if (sTokens.length < 2) {
					mtContent = "Tin nhan sai cu phap. Soan tin  "
							+ keywords
							+ " dapan gui "
							+ service_id
							+ " de tham gia chuong trinh O chu bi mat. DTHT: 1900571566";
					msgObject.setUsertext(mtContent);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {

					if (!checkUser(database_customer, user_id, session)) {
						saveCustomer(database_customer, session, user_id,
								mobile_operator);
					}

					String result = "";

					for (int k = 1; k < sTokens.length; k++) {
						result = result + sTokens[k];
					}
					Util.logger.info("Result: " + result);

					// Xem dang o block thu may
					int status1 = -1;
					if ((finish_dk.compareToIgnoreCase(start_dk) < 0)
							|| (finish_dk.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = -1;
					} else if ((start_time.compareToIgnoreCase(start_dk) < 0)
							|| (start_time.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 0;
					} else if ((block1.compareToIgnoreCase(start_dk) < 0)
							|| (block1.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 1;
					} else if ((block2.compareToIgnoreCase(start_dk) < 0)
							|| (block2.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 2;
					} else if ((block3.compareToIgnoreCase(start_dk) < 0)
							|| (block3.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 3;
					} else if ((block4.compareToIgnoreCase(start_dk) < 0)
							|| (block4.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 4;
					} else if ((block5.compareToIgnoreCase(start_dk) < 0)
							|| (block5.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 5;
					} else if ((block6.compareToIgnoreCase(start_dk) < 0)
							|| (block6.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 6;
					} else if ((block7.compareToIgnoreCase(start_dk) < 0)
							|| (block7.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 7;
					} else if ((block8.compareToIgnoreCase(start_dk) < 0)
							|| (block8.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 8;
					} else if ((block9.compareToIgnoreCase(start_dk) < 0)
							|| (block9.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 9;
					} else if ((block10.compareToIgnoreCase(start_dk) < 0)
							|| (block10.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 10;
					} else if ((block11.compareToIgnoreCase(start_dk) < 0)
							|| (block11.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 11;
					} else if ((block12.compareToIgnoreCase(start_dk) < 0)
							|| (block12.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 12;
					} else if ((block13.compareToIgnoreCase(start_dk) < 0)
							|| (block13.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 13;
					} else {
						status1 = 14;
					}
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

						if (status1 < 10) {
							if (!result.equalsIgnoreCase(current)) {
								// Update MO
								updateInfoCustomer(database_customer, user_id,
										session, status1, time, 0, 0);

								msgObject
										.setUsertext("Rat tiec ban da tra loi chua dung dap an cua cau hoi nay, nhanh tay soan tin tham gia chuong trinh de tro thanh nguoi may man tiep theo.");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
								return messages;
							} else {

								// Kiem tra xem da tra loi dung hay chua lay tg

								if (time > 0) {

									// Tra loi dung
									updateInfoCustomer(database_customer,
											user_id, session, status1, time, 0,
											0);

									msgObject
											.setUsertext("Chuc mung ban da tra loi dung dap an cau hoi. Hay tiep tuc theo doi de biet ban co phai la nguoi choi may man tiep theo khong nhe");
									msgObject.setContenttype(0);
									msgObject.setMsgtype(1);
									messages.add(new MsgObject(msgObject));
									return messages;
								} else {
									// update thoi gian tra loi dung
									int time_user = 0;
									if (status1 == -1 || status1 == 0) {
										time_user = calculateTime(Timestamp
												.valueOf(start_dk),
												receive_date);

									} else if (status1 == 1) {
										time_user = calculateTime(Timestamp
												.valueOf(start_time),
												receive_date);
									} else {
										String block = "";
										if (status1 == 2) {
											block = block1;
										} else if (status1 == 3) {
											block = block2;
										} else if (status1 == 4) {
											block = block3;
										} else if (status1 == 5) {
											block = block4;
										} else if (status1 == 6) {
											block = block5;
										} else if (status1 == 7) {
											block = block6;
										} else if (status1 == 8) {
											block = block7;
										} else if (status1 == 9) {
											block = block8;
										}
										time_user = calculateTime(Timestamp
												.valueOf(block), receive_date);
									}
									updateInfoCustomer(database_customer,
											user_id, session, status1,
											time_user, time_user, 1);
									// Tra tin chuc mung tra loi dung
									msgObject
											.setUsertext("Chuc mung ban da tra loi dung dap an cau hoi. Hay tiep tuc theo doi de biet ban co phai la nguoi choi may man tiep theo khong nhe");
									msgObject.setContenttype(0);
									msgObject.setMsgtype(1);
									messages.add(new MsgObject(msgObject));
									return messages;
								}

							}

						} else if (status1 == 14) {
							// dang o status 14

							
								// Update MO
								saveCustomerendblock(session, user_id,
										msgObject.getUsertext());

								msgObject
										.setUsertext("Cam on ban da tham gia vong thach dau cua gameshow Ghep chu,hay theo doi tiep chuong trinh de biet ban co  may man nhan giai phu cua chuong trinh hay khong");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
								return messages;
							

						} else {
							if (!result.equalsIgnoreCase(current)) {
								// Update MO
								updateInfoCustomer(database_customer, user_id,
										session, status1, time, 0, 0);

								msgObject
										.setUsertext("Rat tiec ban da tra loi chua dung dap an cua cau hoi nay, nhanh tay soan tin tham gia chuong trinh de tro thanh nguoi may man tiep theo.");
								msgObject.setContenttype(0);
								msgObject.setMsgtype(1);
								messages.add(new MsgObject(msgObject));
								return messages;
							} else {

								// Tinh thoi gian tu dau block den cuoi block
								time = getInfoCustomer(database_customer,
										user_id, session, status1);

								if (time > 0) {
									// Co nghia la da tra loi dung
									// Get total_time va tra loi cho khach hang
									// int total_time =
									// getTotaltime(database_customer, user_id,
									// session);
									// Update MO, MT
									updateInfoCustomer(database_customer,
											user_id, session, 10, time, 0, 0);
									msgObject
											.setUsertext("Chuc mung ban da tra loi dung cau hoi cua chuong trinh. Don xem KQ de biet ban co phai la nguoi may man");
									msgObject.setContenttype(0);
									msgObject.setMsgtype(1);
									messages.add(new MsgObject(msgObject));
									return messages;
								} else {
									// Update total_time va total_mo cho khach
									// hang

									// Tinh thoi gian tu dau block den cuoi
									// block
									// 
									String block = "";

									if (status1 == 10) {
										block = block9;
									} else if (status1 == 11) {
										block = block10;
									} else if (status1 == 12) {
										block = block11;
									} else if (status1 == 13) {
										block = block12;
									} else
										block = block13;
									int time_user = calculateTime(Timestamp
											.valueOf(block), receive_date);

									// Update MO
									updateInfoCustomer(database_customer,
											user_id, session, status1,
											time_user, time_user, 1);

									msgObject
											.setUsertext("Chuc mung ban da tra loi dung cau hoi cua chuong trinh. Don xem KQ de biet ban co phai la nguoi may man");
									msgObject.setContenttype(0);
									msgObject.setMsgtype(1);
									messages.add(new MsgObject(msgObject));
									return messages;
								}

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
			
			if (status >= 0) {
				sqlSelect = "SELECT block" + status + " FROM "
						+ database_customer + " WHERE session='" + session
						+ "' AND user_id='" + user_id + "'";
			} else
				sqlSelect = "SELECT blockdk  FROM " + database_customer
						+ " WHERE session='" + session + "' AND user_id='"
						+ user_id + "'";	statement = connection.prepareStatement(sqlSelect);
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

	private static boolean saveCustomerendblock(String session, String user_id,
			String message) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO ghepchu_endblock ( session, user_id, message)VALUES ('"
					+ session + "','" + user_id + "','" + message + "')";

			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert Into ghepchu_endblock ");
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
