package livesms;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.SoapLiveSMS;

public class Charge_livesms extends Thread {

	public static Hashtable SECRET_NUMBER = null;
	public boolean LOAD = false;
	public static int isProcess = 0;

	public static String getSECRET_NUMBER(String secretNumber) {
		String retobj = "";

		try {
			retobj = (String) SECRET_NUMBER.get(secretNumber);
			return retobj;

		} catch (Exception e) {
		}

		return "";
	}

	@Override
	public void run() {

		while (ConsoleSRV.processData) {
			int runhour = Integer.parseInt(Constants._prop.getProperty(
					"runcharge_live", "18"));

			String[] result = new String[17];
			Connection connection = null;
			PreparedStatement statement = null;
			ResultSet rs = null;
			Calendar cal = Calendar.getInstance();
			int nowhour = cal.get(Calendar.HOUR_OF_DAY);
			int nowday = cal.get(Calendar.DATE);
			int nowmonth = cal.get(Calendar.MONTH) + 1;
			int nowyear = cal.get(Calendar.YEAR);
			Hashtable secretNumber = new Hashtable();
			int maxretry = 3;
			DBPool dbpool = new DBPool();
			if (nowhour == runhour) {

				// check trong cdr farm queue.
				try {
					connection = dbpool.getConnectionGateway();
					if (connection == null) {
						Util.logger.error("Impossible to connect to DB");

					}

					String sqlSelect = "select user_id,service_id,command_code,mobile_operator,request_id,day_charge, status,times_charge from cdrfarm_queue where mobile_operator='GPC'";

					statement = connection.prepareStatement(sqlSelect);
					if (statement.execute()) {
						rs = statement.getResultSet();
						while (rs.next()) {
							String user_id = rs.getString(1);
							String service_id = rs.getString(2);
							String command_code = rs.getString(3);
							String operator = rs.getString(4);
							int request = rs.getInt(5);
							BigDecimal request_id = new BigDecimal(request);
							int day_charge = rs.getInt(6);
							int status = rs.getInt(7);
							int times_charge = rs.getInt(8);

							Calendar cal1 = Calendar.getInstance();
							cal1.add(Calendar.DATE, 1);
							int day1 = cal1.get(Calendar.DATE);

							MsgObject msgobj = new MsgObject();
							msgobj.setUserid(user_id);
							msgobj.setMobileoperator(operator);
							msgobj.setServiceid(service_id);
							msgobj.setRequestid(request_id);
							msgobj.setKeyword(command_code);
							if (times_charge == maxretry) {
								// Neu so lan retry=maxretry

								msgobj.setUsertext("STOP+LS");
								if (SoapLiveSMS.getMessages(msgobj))
									Util.logger.info("OK");
								else

									Util.logger.info("Chua post duoc");
								msgobj
										.setUsertext("Ban da bi tam dung su dung dich vu SMS Color do tai khoan cua ban khong du de tiep tuc su dung dich vu. Vui long nap them tien de tiep tuc su dung.DTHT:1900571566");
								msgobj.setMsgtype(0);
								msgobj.setContenttype(0);
								DBUtil.sendMT(msgobj);
								Thread.sleep(1000);

							}
							times_charge += 1;
							updateCDRqueue(user_id, times_charge, day1);
						}

					}

				} catch (SQLException e) {
					Util.logger.error(": Error:" + e.toString());

				} catch (Exception e) {
					Util.logger.error(": Error:" + e.toString());

				} finally {
					dbpool.cleanup(rs);
					dbpool.cleanup(statement);
					dbpool.cleanup(connection);
				}

				try {
					connection = dbpool.getConnectionGateway();
					if (connection == null) {
						Util.logger.error("Impossible to connect to DB");

					}

					String sqlSelect = "select user_id,service_id,mobile_operator,request_id,day_charge,day_14 from mlist_farm where date(autotimestamps)< date(CURDATE())and  mobile_operator='GPC'";

					statement = connection.prepareStatement(sqlSelect);
					if (statement.execute()) {
						rs = statement.getResultSet();
						while (rs.next()) {
							String user_id = rs.getString(1);
							String service_id = rs.getString(2);
							String mobile_operator = rs.getString(3);

							int request = rs.getInt(4);
							BigDecimal request_id = new BigDecimal(request);
							int day_charge = rs.getInt(5);

							int day_14 = rs.getInt(6);
							Calendar cal1 = Calendar.getInstance();
							cal1.add(Calendar.DATE, 1);
							int day1 = cal1.get(Calendar.DATE);
							int month1 = cal1.get(Calendar.MONTH) + 1;
							int year1 = cal1.get(Calendar.YEAR);

							cal1.add(Calendar.MONTH, 1);
							int day30 = cal1.get(Calendar.DATE);
							int month30 = cal1.get(Calendar.MONTH) + 1;
							int year30 = cal1.get(Calendar.YEAR);

							MsgObject msgobj = new MsgObject();
							msgobj.setUserid(user_id);
							msgobj.setMobileoperator(mobile_operator);
							msgobj.setServiceid(service_id);
							msgobj.setRequestid(request_id);

							Calendar last = Calendar.getInstance();
							last.add(Calendar.MONTH, -1);
							int lday = last.get(Calendar.DATE);
							int lmonth = last.get(Calendar.MONTH) + 1;
							int lyear = last.get(Calendar.YEAR);
							String timelast = lyear + "-"
									+ FormatNumber(lmonth) + "-"
									+ FormatNumber(lday);
							String time = isexistCDRlog(msgobj.getUserid(),
									"cdr_livesms_log", timelast);
							if (nowday == day_charge) {
								/*
								 * Neu ko co trong cdr-farm log
								 */
								if (time.equalsIgnoreCase("")) {

									insertCDRqueue(msgobj.getUserid(), msgobj
											.getServiceid(), msgobj
											.getKeyword(), msgobj, day1, 1);

								}

							} else if (day1 == day_charge) {
								if (time.equalsIgnoreCase("")) {
								msgobj
										.setUsertext("DV SMS Color ban dang su dung se het han vao ngay "
												+ day1
												+ "/"
												+ month1
												+ "/"
												+ year1
												+ ".De tiep tuc dv soan: LS GH gui 8x51 de gia han den "
												+ day30
												+ "/"
												+ month30
												+ "/"
												+ year30
												+ ".Phi dv:5000d/thang. DTHT:1900571566");
								msgobj.setMsgtype(0);
								msgobj.setContenttype(0);
								DBUtil.sendMT(msgobj);
								Thread.sleep(1000);

							}
							}

						}
					}

				} catch (SQLException e) {
					Util.logger.error(": Error:" + e.toString());

				} catch (Exception e) {
					Util.logger.error(": Error:" + e.toString());

				} finally {
					dbpool.cleanup(rs);
					dbpool.cleanup(statement);
					dbpool.cleanup(connection);
				}

				try {
					sleep(1000 * 60 * 60);
					Util.logger.info("Sleep Thread getlink 60 phut ....");

				} catch (InterruptedException e) {
					e.printStackTrace();

				}
			} else {

				try {
					sleep(1000 * 60 * 60);
					Util.logger.info("Sleep Thread getlink 60 phut ....");

				} catch (InterruptedException e) {
					e.printStackTrace();

				}

			}
		}

	}

	private int insertCDRqueue(String user_id, String service_id,
			String command_code, MsgObject msgObject, int day_charge, int status) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into cdrfarm_queue (user_id, service_id, date,command_code,request_id,mobile_operator,day_charge,status) values ('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ command_code

				+ "','"
				+ msgObject.getRequestid()
				+ "','"
				+ msgObject.getMobileoperator() + "',"

				+ day_charge + "," + status + ")";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  cdrfarm_queue Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName()
					+ ":Insert cdrfarm_queue Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public static String FormatNumber(int i) {
		try {
			if (i >= 0 && i < 10)
				return "0" + i;
			else
				return "" + i;
		} catch (Exception e) {
			return "";
		}
	}

	private static String isexistCDRlog(String userid, String table, String time) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		String tempMilisec = "";
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select date(autotimestamps) from " + table
					+ " where user_id='" + userid
					+ "'  and  date(autotimestamps) > '" + time
					+ "' order by autotimestamps desc limit 1 ";

			Util.logger.info(query3);

			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);

				tempMilisec = (String) item.elementAt(0);
			}
			return tempMilisec;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return tempMilisec;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return tempMilisec;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	private static boolean updateCDRqueue(String userid, int times_charge,
			int date_charge) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "UPDATE cdrfarm_queue  SET times_charge ="
					+ times_charge + " ,day_charge= " + date_charge
					+ " WHERE user_id='" + userid + "'";
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update so lan charge va ngay charge" + userid
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

	private int insertData(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			int day_charge, int day_14) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "(user_id, service_id, date,command_code,request_id,mobile_operator,day_charge,day_14) values ('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + msgObject.getKeyword()
				+ "'," + msgObject.getRequestid() + ",'"
				+ msgObject.getMobileoperator() + "'," + day_charge + ","
				+ day_14 + ")";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName() + ":Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private static boolean deleteUser(String user, String table) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM " + table + "  WHERE user_id='"
					+ user + "'";
			Util.logger.info(" DELETE USER: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger
						.info("Loi xoa user " + user + "trong bang " + table);
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

}
