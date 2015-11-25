package icom;

import icom.common.DBUtil;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
public class LoadMT extends Thread {

	Map mapResponse = null;
	MsgQueue queueRequest = null;
	int processnum = 1;
	int processindex = 1;
	// private Logger logger = null;
	static int TIME_DELAY_LOAD_MO = 100;

	DBPool dbpool = new DBPool();
	public static String[] mobileOperators = { "GPC", "VMS", "VIETEL", "EVN",
			"SFONE", "HTC", "CPHONE" };

	public LoadMT(MsgQueue queue, Map mapresponse, int processnum,
			int processindex) {
		this.queueRequest = queue;
		this.processnum = processnum;
		this.processindex = processindex;
		this.mapResponse = mapresponse;

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

		String SQL_LOAD = "select * from " + Constants.tblMTQueue
				+ " where (mod(id," + processnum + ")=" + processindex + ")";
		Util.logger.info("LoadMT - Start");
		Util.logger.info("LoadMT - SQL:" + SQL_LOAD);

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
						info = rs.getString("INFO");
						tTime = new Timestamp(System.currentTimeMillis());
						operator = rs.getString("MOBILE_OPERATOR");
						requestId = rs.getBigDecimal("REQUEST_ID");

						msgObject = new MsgObject(1, serviceId, userId, "INV",
								info, requestId, tTime, operator, 0, 0);

						msgObject.setMsg_id(rs.getLong("ID"));
						msgObject.setUserid(rs.getString("USER_ID"));
						msgObject.setServiceid(rs.getString("SERVICE_ID"));
						msgObject.setMobileoperator(rs
								.getString("MOBILE_OPERATOR"));
						msgObject.setKeyword(rs.getString("COMMAND_CODE"));
						msgObject.setUsertext(rs.getString("INFO"));
						msgObject.setRequestid(rs.getBigDecimal("REQUEST_ID"));

						msgObject.setMsgtype(rs.getInt("MESSAGE_TYPE"));
						msgObject.setContenttype(rs.getInt("CONTENT_TYPE"));
						msgObject.setTSubmitTime(new Timestamp(System
								.currentTimeMillis()));

						String currTime = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(new Date());
						msgObject.setMsgNotes(0, rs.getString("NOTES")
								+ "@submittime=" + currTime);

						msgObject.setObjtype(1);

						try {
							rs.deleteRow();

							// Add vao quue de xu ly
							queueRequest.add(msgObject);

							Util.logger.info("{LoadMT}-add2queue:" + "Q"
									+ serviceId + "[" + queueRequest.getSize()
									+ "]" + userId + "@" + info + "}");
							
							
							// Dong thoi dua vao 1 qu^eu khac de cho phan hoi
							synchronized (mapResponse) {
								this.mapResponse.put(
										msgObject.getMsg_id() + "", msgObject);
							}

						} catch (SQLException ex) {
							Util.logger.error("{Load MT}{Ex:" + ex.toString());
							Util.logger.info("{LoadMT}{add2queue:" + "Q"
									+ serviceId + "[" + queueRequest.getSize()
									+ "]" + userId + "@" + info
									+ "@SQLException:" + ex.toString() + "}");
							// queue.remove();

						} catch (Exception ex1) {
							Util.logger.error("Load MT. ex1:" + ex1.toString());
							// queue.remove();

						}

					}

					sleep(TIME_DELAY_LOAD_MO);
				}

			} catch (SQLException ex3) {
				Util.logger.error("Load MT. SQLException:" + ex3.toString());
				DBUtil
						.Alert("Process.LoadMT", "LoadMT.SQLException",
								"major", "LoadMT.SQLException:"
										+ ex3.toString(), "processAdmin");
			} catch (Exception ex2) {
				Util.logger.error("Load MT. Exception:" + ex2.toString());
				DBUtil.Alert("Process.LoadMT", "LoadMT.Exception", "major",
						"LoadMT.Exception:" + ex2.toString(), "processAdmin");
			} finally {
				dbpool.cleanup(rs, stmt);
				dbpool.cleanup(connection);

			}

		}

	}

}
