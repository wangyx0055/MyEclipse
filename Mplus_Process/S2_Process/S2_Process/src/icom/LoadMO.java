package icom;

import icom.common.DBUtil;
import icom.common.Util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
		
		String SQL_LOAD = "select * from " + Constants.tblMOQueue
				+ " where (mod(id," + processnum + ")=" + processindex + ")" 
				+ " limit 100";
		
		Util.logger.info("LoadMO - Start");
		Util.logger.info("LoadMO - SQL:" + SQL_LOAD);
		
		
		
		while (Sender.getData) {
			ArrayList<MsgObject> arrMO = null;  
			arrMO = new ArrayList<MsgObject>();
			
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

					while (rs.next() && Sender.getData) {

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
						msgObject.setChannelType(rs.getInt("CHANNEL_TYPE"));
						msgObject.setTimeSendMO(rs.getString("RECEIVE_DATE"));
						
						msgObject.setObjtype(0);
						try {
							rs.deleteRow();
							arrMO.add(msgObject);

						} catch (SQLException ex) {
							Util.logger.error("{Load MO}{Ex:" + ex.toString());
							Util.logger.info("{LoadMO}{add2queue:" + "Q"
									+ serviceId + "[" + queue.getSize() + "]"
									+ userId + "@" + info + "@SQLException:"
									+ ex.toString() + "}");
							Util.logger.printStackTrace(ex);
							// queue.remove();

						} catch (Exception ex1) {
							Util.logger.error("Load MO. ex1:" + ex1.toString());
							// queue.remove();
							Util.logger.printStackTrace(ex1);
						}
					}
					
				}
			} catch (SQLException ex3) {
				Util.logger.error("Load MO. SQLException:" + ex3.toString());
				DBUtil.Alert("Process.LoadMO", "LoadMO.SQLException", "major",
						"LoadMO.SQLException:" + ex3.toString(), "S2Admin");
				Util.logger.printStackTrace(ex3);
			} catch (Exception ex2) {
				Util.logger.error("Load MO. Exception:" + ex2.toString());
				DBUtil.Alert("Process.LoadMO", "LoadMO.Exception", "major",
						"LoadMO.Exception:" + ex2.toString(), "processAdmin");
				Util.logger.printStackTrace(ex2);
			} finally {
				dbpool.cleanup(rs, stmt);
				dbpool.cleanup(connection);
			}
			
			for(int i = 0;i< arrMO.size(); i++){
				MsgObject msgObj = arrMO.get(i);
				if( !isUserBlackList(msgObj.getUserid())){
					queue.add(msgObj);
					Util.logger.info("{LoadMO}-add2queue:" + "Q"
							+ msgObj.getServiceid() + "[" + queue.getSize() + "]@user_id="
							+ msgObj.getUserid() + "@command_code=" 
							+ msgObj.getKeyword() +"@info=" 
							+ msgObj.getUsertext() + "@channel_type="
							+ msgObj.getChannelType() +"}");
				}else{
					// 9: Blacklist
					// 8: Invalid SMS.
					msgObj.setChannelType(9);
					msgObj.setCommandCode("BLACKLIST");
					add2molog(msgObject);
				}
			}
			
			try {
				sleep(TIME_DELAY_LOAD_MO);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
						
	}
	
	/**
	 * 
	 * @param userId
	 * @return true - userId is in blacklist <br>
	 *         false - userId is not in blacklist
	 * @author DanND 
	 * @Date 2011-03-03        
	 */
    public boolean isUserBlackList(String userId) {

		Boolean check = false;

		DBPool dbPoolBL = new DBPool();

		String tblBlackList = "s2user_blacklist";

		String SQL_LOAD = "select * from " + tblBlackList
				+ " where USER_ID = '" + userId + "'";

		Util.logger.info("########  LoadMO - Check User In Blacklist ########");

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (connection == null) {
				connection = dbPoolBL.getConnectionGateway();
			}
			stmt = connection.prepareStatement(SQL_LOAD,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {

				rs = stmt.getResultSet();

				while (rs.next()) {
					check = true;
					Util.logger.info("LOADMO ## BLACKLIST ### USER_ID = " + userId
							+ " Co trong BlackList (s2user_blacklist) !");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Load MO.isUserBlackList SQLException:"
					+ ex3.toString());
			DBUtil.Alert("Process.LoadMO.isUserBlackList",
					"LoadMO.SQLException", "major", "LoadMO.SQLException:"
							+ ex3.toString(), "S2Admin");
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Load MO.isUserBlackList Exception:"
					+ ex2.toString());
			DBUtil.Alert("Process.LoadMO.isUserBlackList", "LoadMO.Exception",
					"major", "LoadMO.isUserBlackList.Exception:"
							+ ex2.toString(), "processAdmin");
			Util.logger.printStackTrace(ex2);
		} finally {
			dbPoolBL.cleanup(rs, stmt);
			dbPoolBL.cleanup(connection);
		}

		if (check == false) {
			Util.logger.info("LOADMO ###@@@@@ USER_ID = " + userId
					+ " KHONG CO TRONG BLACKLIST !!!!!");
		}

		return check;
	}
    
	private static BigDecimal add2molog(MsgObject msgObject) {

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mo"
				+ new SimpleDateFormat("yyyyMM").format(new Date());
		Util.logger.info("add2:" + tablename + "@" + msgObject.getUserid()
				+ "@" + msgObject.getUsertext());
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CP_MO,CP_MT,ID,CHANNEL_TYPE)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			//statement.setString(5, msgObject.getKeyword());
			statement.setString(5, msgObject.getCommandCode());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getCp_mo());
			statement.setInt(10, msgObject.getCp_mt());
			statement.setLong(11, msgObject.getMsg_id());
			statement.setLong(12, msgObject.getChannelType());
			// statement.setString(12, msgObject.getContentid());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2:" + tablename + "@"
						+ msgObject.getUserid() + ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				long lcurrent = System.currentTimeMillis();
				saveSMSObject(lcurrent + ".molog", msgObject);
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			long lcurrent = System.currentTimeMillis();
			saveSMSObject(lcurrent + ".molog", msgObject);
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			long lcurrent = System.currentTimeMillis();
			saveSMSObject(lcurrent + ".molog", msgObject);
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}
	
	public static void saveSMSObject(String sfile, MsgObject object) {
		Util.logger.info(" Saving MsgObject into file " + sfile);
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Date date = new java.util.Date();
		String datetime = dateFormat.format(date);
		try {
			fout = new java.io.FileOutputStream("queue/" + datetime + sfile);
			objOut = new ObjectOutputStream(fout);
			objOut.writeObject(object);
			objOut.flush();
		} catch (IOException ex) {
			Util.logger.error("Save data error: " + ex.getMessage());
		} finally {
			try {
				objOut.close();
				fout.close();
			} catch (IOException ex) {
			}
		}
	}
}
