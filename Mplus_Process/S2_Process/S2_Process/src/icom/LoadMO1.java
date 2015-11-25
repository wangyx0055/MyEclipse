package icom;

import icom.common.DBUtil;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
public class LoadMO1 extends Thread {

	MsgQueue queue = null;
	int processnum = 1;
	int processindex = 1;
	// private Logger logger = null;
	static int TIME_DELAY_LOAD_MO = 100;

	DBPool dbpool = new DBPool();
	public static String[] mobileOperators = { "GPC", "VMS", "VIETEL", "EVN",
			"SFONE", "HTC", "CPHONE" };

	public LoadMO1(MsgQueue queue, int processnum, int processindex) {
		this.queue = queue;
		this.processnum = processnum;
		this.processindex = processindex;

	}

	@Override
	public void run() {
		MsgObject msgObject = null;
		String serviceId = "";
		String userId = "";
		String info = "";
		Timestamp tTime;
		String operator = "";
		BigDecimal requestId = new BigDecimal(-1);

		// /////////

		String SQL_LOAD = "select * from " + Constants.tblMO1Queue
				+ " where (mod(id," + processnum + ")=" + processindex + ")";
		Util.logger.info("LoadMO1 - Start");
		Util.logger.info("LoadMO1 - SQL:" + SQL_LOAD);

		while (Sender.getData) {
			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				if (connection == null) {
					connection = dbpool.getConnectionGateway();
				}
				stmt = connection.prepareStatement(SQL_LOAD,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);

				if (stmt.execute()) {
					rs = stmt.getResultSet();
					while (rs.next() && (Sender.getData)) {
						serviceId = rs.getString("SERVICE_ID");
						userId = rs.getString("USER_ID");
						info = rs.getString("INFO").toUpperCase();
						tTime = new Timestamp(System.currentTimeMillis());
						operator = rs.getString("MOBILE_OPERATOR");
						requestId = rs.getBigDecimal("REQUEST_ID");
						requestId = rs.getBigDecimal("ID");

						msgObject = new MsgObject(0, serviceId, userId, "INV",
								info, requestId, tTime, operator, 0, 0);

						msgObject.setMsg_id(rs.getLong("ID"));
						msgObject.setUserid(rs.getString("USER_ID"));
						msgObject.setServiceid(rs.getString("SERVICE_ID"));
						msgObject.setMobileoperator(rs
								.getString("MOBILE_OPERATOR"));
						msgObject.setKeyword(rs.getString("COMMAND_CODE"));
						msgObject.setUsertext(rs.getString("INFO"));
						msgObject.setRequestid(requestId);
						//msgObject.setContentid(rs.getString("CONTENT_ID"));
						msgObject.setObjtype(0);
						try {
							rs.deleteRow();
							queue.add(msgObject);

							Util.logger.info("{LoadMO1}-add2queue:" + "Q"
									+ serviceId + "[" + queue.getSize() + "]"
									+ userId + "@" + info + "}");

						} catch (SQLException ex) {
							Util.logger.error("{Load MO1}{Ex:" + ex.toString());
							Util.logger.info("{LoadMO1}{add2queue:" + "Q"
									+ serviceId + "[" + queue.getSize() + "]"
									+ userId + "@" + info + "@SQLException:"
									+ ex.toString() + "}");
							Util.logger.printStackTrace(ex);
							// queue.remove();

						} catch (Exception ex1) {
							Util.logger.error("Load MO1. ex1:" + ex1.toString());
							// queue.remove();
							Util.logger.printStackTrace(ex1);

						}

					}

					sleep(TIME_DELAY_LOAD_MO * 5);
				}

			} catch (SQLException ex3) {
				Util.logger.error("Load MO1. SQLException:" + ex3.toString());
				DBUtil
						.Alert("Process.LoadMO1", "LoadMO1.SQLException",
								"major", "LoadMO1.SQLException:"
										+ ex3.toString(), "processAdmin");
				Util.logger.printStackTrace(ex3);
			} catch (Exception ex2) {
				Util.logger.error("Load MO1. Exception:" + ex2.toString());
				DBUtil.Alert("Process.LoadMO1", "LoadMO.Exception", "major",
						"LoadMO1.Exception:" + ex2.toString(), "processAdmin");
				Util.logger.printStackTrace(ex2);
			} finally {
				dbpool.cleanup(rs, stmt);
				dbpool.cleanup(connection);

			}

		}

	}

}
