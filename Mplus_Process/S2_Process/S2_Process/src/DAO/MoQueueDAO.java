package DAO;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.MsgQueue;
import icom.Sender;
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
import java.util.Date;


public class MoQueueDAO extends Thread {
	private int processIndex;
	private int processNum;

	MsgQueue queue;
	public MoQueueDAO(int processIndex,int processNum,MsgQueue queue)
	{
		this.processIndex=processIndex;
		this.processNum=processNum;
		this.queue=queue;
	}
	@Override
	public void run()
	{
		String SQL_LOAD = "select * from " + Constants.TABLENAME_MOQUEUE9222 + " where (mod(id,"
				+ processNum + ")=" + processIndex + ")" + " limit 100";
		Util.logger.info(SQL_LOAD);
		while (Sender.getData) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		BigDecimal requestId = new BigDecimal(-1);
		DBPool dbPool=new DBPool();
			try {
				if (connection == null) {
					connection = dbPool.getConnectionGateway();
				}
				stmt = connection.prepareStatement(SQL_LOAD,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);

				if (stmt.execute()) {

					rs = stmt.getResultSet();
					while (rs.last()) {
						MsgObject moObj = new MsgObject();
						requestId = rs.getBigDecimal("ID");
						moObj.setId(rs.getInt("ID"));
						moObj.setUserid(rs.getString("USER_ID"));
						moObj.setServiceid(rs.getString("SERVICE_ID"));
						moObj.setMobileoperator(rs.getString("MOBILE_OPERATOR"));
						moObj.setCommandCode(rs.getString("COMMAND_CODE"));
						moObj.setUsertext(rs.getString("INFO"));
						moObj.setChannelType(rs.getInt("CHANNEL_TYPE"));
						moObj.setRequestid(requestId);

						try {
							rs.deleteRow();
							// push doi tuong mo vao queue
							queue.add(moObj);
							

						} catch (SQLException ex) {
							Util.logger
									.error("@MOQueueDAO error at function getMoqueue"
											+ ex.getMessage());
							Util.logger.printStackTrace(ex);
						} catch (Exception ex1) {
							Util.logger
									.error("@MOQueueDAO error at function getMoqueue"
											+ ex1.getMessage());
							Util.logger.printStackTrace(ex1);
						}

					}

				}
			} catch (SQLException ex3) {
				Util.logger.error("Load MO. SQLException:" + ex3.toString());
				Util.logger.printStackTrace(ex3);
			} catch (Exception ex2) {
				Util.logger.error("Load MO. Exception:" + ex2.toString());
				Util.logger.printStackTrace(ex2);
			} finally { // giai phong cac ket noi toi mysql
				dbPool.cleanup(rs, stmt);
				dbPool.cleanup(connection);
			}
			// cho ung dung ngu 1/2 giay
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	public static MsgObject getMOQueue9222(String msisdn)
	{
		MsgObject msgObject = null;
		String serviceId = "";
		String userId = "";
		String info = "";
		Timestamp tTime;
		String operator = "";
		BigDecimal requestId = new BigDecimal(-1);		
		String SQL_LOAD = "select * from " + Constants.TABLENAME_MOQUEUEAPPROVE +" Where USER_ID='"+msisdn+"' Order By RECEIVE_DATE Desc Limit 1";		
		Util.logger.info(SQL_LOAD);		
			DBPool dbpool=new DBPool();			
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

					while (rs.next()) {

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
						
						try {
							rs.deleteRow();

						} catch (SQLException ex) {
							Util.logger.error("{MOQueueDAO}{Ex:" + ex.toString());							
							Util.logger.printStackTrace(ex);
							// queue.remove();

						} catch (Exception ex1) {
							Util.logger.error("MOQueueDAO. ex1:" + ex1.toString());
							// queue.remove();
							Util.logger.printStackTrace(ex1);
						}
					}
					
				}
			} catch (SQLException ex3) {
				Util.logger.error("SQLException:" + ex3.toString());
				DBUtil.Alert("MoQueueDAO", "SQLException", "major",
						"LoadMO.SQLException:" + ex3.toString(), "S2Admin");
				Util.logger.printStackTrace(ex3);
			} catch (Exception ex2) {
				Util.logger.error("Exception:" + ex2.toString());
				DBUtil.Alert("MoQueueDAO", "Exception", "major",
						"Exception:" + ex2.toString(), "processAdmin");
				Util.logger.printStackTrace(ex2);
			} finally {
				dbpool.cleanup(rs, stmt);
				dbpool.cleanup(connection);
			}
			return msgObject;
			
		}
	
	public static void writeToMoQueue(MsgObject msgObject,String tableName) {

		DBPool dbPool=new DBPool();
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;		
		sSQLInsert = "insert into "+tableName
				+ "(USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,CHANNEL_TYPE,REQUEST_ID)"
				+ " values('"+msgObject.getUserid()+"','"+msgObject.getServiceid()+"','"+msgObject.getMobileoperator()+"'" +
						",'"+msgObject.getCommandCode()+"','"+msgObject.getUsertext()+"',"+msgObject.getChannelType()+"," +
								"'"+msgObject.getRequestid()+"')";
		Util.logger.info(sSQLInsert);
		try {
			connection = dbPool.getConnectionGateway();
			statement = connection.prepareStatement(sSQLInsert);			
									
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2:" + Constants.tblMOQueue + "@"
						+ msgObject.getUserid() + ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");			
			}
				
		} catch (SQLException e) {
			Util.logger.error("add2:" + Constants.tblMOQueue + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from moqueuelog:" + e.toString());

		} catch (Exception e) {
			Util.logger.error("add2:" + Constants.tblMOQueue + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from moqueuelog:" + e.toString());
		}

		finally {
			dbPool.cleanup(connection,statement);

		}
	}
	
	public static BigDecimal add2molog(MsgObject msgObject) {

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
