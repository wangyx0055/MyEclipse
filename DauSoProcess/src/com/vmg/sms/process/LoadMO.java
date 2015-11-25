package com.vmg.sms.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import spam.SpamQM;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class LoadMO extends Thread {

	MsgQueue queue = null;
	int processnum = 1;
	int processindex = 1;
	// private Logger logger = null;
	static int TIME_DELAY_LOAD_MO = 100;

	DBPool dbpool = new DBPool();
	public static String[] mobileOperators = { "GPC", "VMS", "VIETEL", "EVN",
			"SFONE", "HTC", "CPHONE" };

	public LoadMO(MsgQueue queue, int processnum, int processindex) {
		this.queue = queue;
		this.processnum = processnum;
		this.processindex = processindex;

	}

	public void run() {
		MsgObject msgObject = null;
		String serviceId = "";
		String userId = "";
		String info = "";
		Timestamp tTime;
		String operator = "";
		BigDecimal requestId = new BigDecimal(-1);

		// /////////

		String SQL_LOAD = "Select  ID, SERVICE_ID,USER_ID, INFO, RECEIVE_DATE,MOBILE_OPERATOR,  REQUEST_ID from sms_receive_queue where (mod(id,"
				+ processnum + ")=" + processindex + ")";
		Util.logger.info("LoadMO - Start");
		Util.logger.info("LoadMO - SQL:" + SQL_LOAD);

		while (ConsoleSRV.getData) {
			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {

				connection = dbpool.getConnectionGateway();

				stmt = connection.prepareStatement(SQL_LOAD,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);

				if (stmt.execute()) {
					rs = stmt.getResultSet();
					while (rs.next()) {
						serviceId = rs.getString("SERVICE_ID");
						userId = rs.getString("USER_ID");
						info = rs.getString("INFO");
						tTime = rs.getTimestamp("RECEIVE_DATE");
						operator = rs.getString("MOBILE_OPERATOR");
						requestId = rs.getBigDecimal("REQUEST_ID");

						msgObject = new MsgObject(serviceId, userId, "INV",
								info, requestId, tTime, operator, 0, 0);

						// System.out.println("Q" + serviceId + "["
						// + queue.getSize() + "]");

						try {
							rs.deleteRow();
							/*
							 * Check spam 13/5/2011 Neu la spam thi ko luu vao
							 * queue ma tra MT luon MT dau: insert vao mt_queue,
							 * Mt sau trong ngay: insert vao bang cdr_queue.
							 */
							String ischeckspam = Constants._prop.getProperty(
									"spam.check", "1");

							String mtspam = Constants._prop.getProperty(
									"spam.mt."
											+ msgObject.getMobileoperator()
													.toLowerCase(),
									"Ban da vi pham quy dinh chong Spam");
							String msgtype = Constants._prop.getProperty(
									"spam.msgtype", "2");
							String autoreply = Constants._prop.getProperty(
									"spam.autoreply", "0");
							if (ischeckspam.equalsIgnoreCase("1")) {
								add2SMSReceiveDay(msgObject);
								if (isSpam(msgObject, mtspam, msgtype,
										autoreply)) {
									msgObject.setKeyword("SPAM");							
									add2SMSReceiveLog(msgObject, "1");
									msgObject.setUsertext(mtspam);
									msgObject.setMsgtype(2);
									if (checklistspam(msgObject)) {
										// neu spam va co trong list spam thi se
										// insert vao bang cdr_queue
										// message_type=2
										msgObject.setMsgtype(2);
										InsertCdr_queue(msgObject);

									} else {
										// neu chua co trong bang spam thi se
										// luu
										// vao bang va tra tin bi spam
										saverequest(msgObject);
										DBUtil.sendMT(msgObject);
									}

								}

								else {
									deletelistspam(msgObject);

									queue.add(msgObject);

									ConsoleSRV.incrementAndGet_load(operator);

									Util.logger.info("{LoadMO}-add2queue:"
											+ "Q" + serviceId + "["
											+ queue.getSize() + "]" + userId
											+ "@" + info + "}");
								}
							} else {
								queue.add(msgObject);

								ConsoleSRV.incrementAndGet_load(operator);

								Util.logger.info("{LoadMO}-add2queue:" + "Q"
										+ serviceId + "[" + queue.getSize()
										+ "]" + userId + "@" + info + "}");
							}
						} catch (SQLException ex) {
							Util.logger.error("{Load MO}{Ex:" + ex.toString());
							Util.logger.info("{LoadMO}{add2queue:" + "Q"
									+ serviceId + "[" + queue.getSize() + "]"
									+ userId + "@" + info + "@SQLException:"
									+ ex.toString() + "}");
							// queue.remove();

						} catch (Exception ex1) {
							Util.logger.error("Load MO. ex1:" + ex1.toString());
							// queue.remove();

						}

					}

					sleep(TIME_DELAY_LOAD_MO);
				}

			} catch (SQLException ex3) {
				Util.logger.error("Load MO. SQLException:" + ex3.toString());
				DBUtil
						.Alert("Process.LoadMO", "LoadMO.SQLException",
								"major", "LoadMO.SQLException:"
										+ ex3.toString(), "processAdmin");
			} catch (Exception ex2) {
				Util.logger.error("Load MO. Exception:" + ex2.toString());
				DBUtil.Alert("Process.LoadMO", "LoadMO.Exception", "major",
						"LoadMO.Exception:" + ex2.toString(), "processAdmin");
			} finally {
				dbpool.cleanup(rs, stmt);
				dbpool.cleanup(connection);

			}

		}

	}

	public static int InsertCdr_queue(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("DBUtil@InsertCdr_queue@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("InsertCdr_queue@InsertCdr_queue@userid="
				+ msgObject.getUserid() + "@serviceid="
				+ msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("InsertCdr_queue@InsertCdr_queue: Error connection == null"
								+ msgObject.getUserid()
								+ "@TO"
								+ msgObject.getServiceid()
								+ "@"
								+ msgObject.getUsertext()
								+ "@requestid="
								+ msgObject.getRequestid().toString());
				return -1;
			}
			sqlString = "INSERT INTO cdr_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,cpid) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?,?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, msgObject.getUsertext());
			statement.setTimestamp(6, null);
			statement.setTimestamp(7, null);
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getMsgtype());
			statement.setBigDecimal(10, msgObject.getRequestid());
			statement.setInt(11, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("InsertCdr_queue: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("InsertCdr_queue: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("InsertCdr_queue: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

	public static boolean saverequest(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into tablespam ( user) values ('"
					+ msgObject.getUserid() + "')";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to list spam");
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

	private static BigDecimal add2SMSReceiveLog(MsgObject msgObject,
			String isspam) {

		Util.logger.info("add2SMSReceiveLog:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_log"
				+ new SimpleDateFormat("yyyyMM").format(new Date());
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CPID,SPAM)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getKeyword());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getCpid());
			statement.setString(10, isspam);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2ReceiveLog:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private static BigDecimal add2SMSReceiveDay(MsgObject msgObject) {

		Util.logger.info("add2SMSReceiveDay:{userid=" + msgObject.getUserid()
				+ "}{usertext=" + msgObject.getUsertext() + "}{requestid="
				+ msgObject.getRequestid().toString() + "}");
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_day_"
				+ msgObject.getMobileoperator().toLowerCase();
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CPID)"
				+ " values(?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getKeyword());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getCpid());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2SMSReceiveDay: {userid="
						+ msgObject.getUserid() + "}{usertext="
						+ msgObject.getUsertext() + "}{requestid="
						+ msgObject.getRequestid().toString() + "}"
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2SMSReceiveDay:{userid="
					+ msgObject.getUserid() + "}{usertext="
					+ msgObject.getUsertext() + "}{requestid="
					+ msgObject.getRequestid().toString() + "}"
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2SMSReceiveDay:{userid="
					+ msgObject.getUserid() + "}{usertext="
					+ msgObject.getUsertext() + "}{requestid="
					+ msgObject.getRequestid().toString()
					+ "}:Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	public static boolean checklistspam(MsgObject msgObject) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  tablespam where user='"
					+ msgObject.getUserid() + "'";

			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}

	public static boolean deletelistspam(MsgObject msgObject) {

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

			String sqlDelete = "DELETE FROM tablespam where user='"
					+ msgObject.getUserid() + "'";

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

	private boolean isSpam(MsgObject msgObj, String smtspam, String smsgtype,
			String sautoreply) {
		try {
			SpamQM spamqm = null;
			String classcheckspam = "spam."
					+ msgObj.getMobileoperator().toLowerCase();
			Class delegateclass = Class.forName(classcheckspam);
			Object delegateObj = delegateclass.newInstance();
			spamqm = (SpamQM) delegateObj;

			return spamqm.isSpam(msgObj, sautoreply, smtspam, smsgtype);

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.error("CheckSpam:{userid=" + msgObj.getUserid()
					+ "}{usertext=" + msgObj.getUsertext() + "}{requestid="
					+ msgObj.getRequestid().toString() + "}@" + e.toString());
		}
		return false;
	}

}
