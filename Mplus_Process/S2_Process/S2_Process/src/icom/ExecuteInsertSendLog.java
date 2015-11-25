package icom;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import icom.common.DBUtil;
import icom.common.Util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

public class ExecuteInsertSendLog extends Thread {

	static MsgQueue queueLog = null;

	BigDecimal AM = new BigDecimal(-1);

	public ExecuteInsertSendLog(MsgQueue queueLog) {
		this.queueLog = queueLog;
	}

	public static void add2queueReceiveLog(MsgObject msgObject) {
		queueLog.add(msgObject);
	}

	public void run() {

		MsgObject msgObject = null;

		BigDecimal returnId = AM;

		try {
			sleep(1000);
		} catch (InterruptedException ex1) {
		}

		try {
			while (Sender.processData) {
				returnId = AM;
				try {
					if(queueLog.getSize()==0)
					{
						sleep(60 * 1000);
					}
					msgObject = (MsgObject) queueLog.remove();

					returnId = processQueueMsg(msgObject);
					if (returnId.equals(AM)) {
						queueLog.add(msgObject);
					}

				} catch (Exception ex) {
					Util.logger.error(ex.toString());
					queueLog.add(msgObject);

				}
				sleep(30);
			}
		}
		catch (Exception ex) {
			Util.logger.crisis("Error: ExecuteAddReceivelog.run :"
					+ ex.toString());

		}
	}

	
	private BigDecimal processQueueMsg(MsgObject msgObject) {
		BigDecimal returnid = new BigDecimal(-1);
		if (msgObject.getObjtype() == 0) {
			returnid = add2molog(msgObject);
		} else {
			returnid = add2mtlog(msgObject);

			if ((msgObject.getMsgtype() + "").equals(Constants.MT_CHARGING)
					&& (msgObject.getAmount() > 0)
					&& (msgObject.getProcess_result() == Constants.RET_OK)) {
				Util.logger.info("{cdr=yes}{@user_id= " + msgObject.getUserid()
						+ "}{@service_id= " + msgObject.getServiceid()
						+ "}{@request_id= " + msgObject.getRequestid()
						+ "}{@message_type= " + msgObject.getMsgtype()
						+ "}{@amount= " + msgObject.getAmount() + "@channel_type=" +  msgObject.getChannelType() + "}");

				add2cdrqueue(msgObject);
				//if (ExecuteEvent.isEvent() == true) {
				///	MsgObject mo = new MsgObject(msgObject);
				//	mo.setObjtype(0);
				//	
				//	ExecuteEvent.add2queueEvent(mo);
				//}

			} else {
				Util.logger.info("{cdr=no}{userid= " + msgObject.getUserid()
						+ "}{serviceid= " + msgObject.getServiceid()
						+ "}{requestid= " + msgObject.getRequestid()
						+ "}{messagetype= " + msgObject.getMsgtype()
						+ "}{amount= " + msgObject.getAmount() + "@channel_type="+ msgObject.getChannelType() + "}");
			}
		}

		return returnid;
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

	private static BigDecimal add2mtlog(MsgObject msgObject) {

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String tablename = "mt"
				+ new SimpleDateFormat("yyyyMM").format(new Date());
		Util.logger.info("add2:" + tablename + "@" + msgObject.getUserid()
				+ "@" + msgObject.getUsertext());
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, "
				+ "SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, "
				+ "NOTES, CP_MO, CP_MT,AMOUNT,ID)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setTimestamp(8, msgObject.getTTimes());
			statement.setInt(9, 1);
			statement.setInt(10, msgObject.getMsgtype());
			statement.setBigDecimal(11, msgObject.getRequestid());
			statement.setString(12, msgObject.getMsgnotes());
			statement.setInt(13, msgObject.getCp_mo());
			statement.setInt(14, msgObject.getCp_mt());
			statement.setLong(15, msgObject.getAmount());
			statement.setLong(16, msgObject.getMsg_id());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2:" + tablename + "@"
						+ msgObject.getUserid() + ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				long lcurrent = System.currentTimeMillis();
				saveSMSObject(lcurrent + ".mtlog", msgObject);
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			long lcurrent = System.currentTimeMillis();
			saveSMSObject(lcurrent + ".mtlog", msgObject);
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			long lcurrent = System.currentTimeMillis();
			saveSMSObject(lcurrent + ".mtlog", msgObject);
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private static BigDecimal add2cdrqueue(MsgObject msgObject) {

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String tablename = "cdr_queue";
		Util.logger.info("add2:" + tablename + "@" + msgObject.getUserid()
				+ "@" + msgObject.getUsertext());
		sSQLInsert = "insert into "
				+ tablename
				+ "( USER_ID, SERVICE_ID, "
				+ "MOBILE_OPERATOR, COMMAND_CODE, INFO, "
				+ "SUBMIT_DATE, DONE_DATE, TOTAL_SEGMENTS,Message_Type,process_result,request_id,cpid,amount) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());

			String info = msgObject.getUsertext();
			if (info == null) {
				info = msgObject.getKeyword();
			} else if (info.length() > 20) {
				info = info.substring(0, 20);
				info = info + "...";
			}
			statement.setString(5, info);
			statement.setTimestamp(6, msgObject.getTSubmitTime());
			statement.setTimestamp(7, msgObject.getTDoneTime());
			statement.setInt(8, 1);
			statement.setInt(9, msgObject.getMsgtype());
			statement.setInt(10, msgObject.getProcess_result());
			statement.setBigDecimal(11, msgObject.getRequestid());
			statement.setInt(12, msgObject.getCp_mo());
			statement.setLong(13, msgObject.getAmount());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2:" + tablename + "@"
						+ msgObject.getUserid() + ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				long lcurrent = System.currentTimeMillis();
				saveSMSObject(lcurrent + ".cdr", msgObject);
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext() + ":Error add row from :"
					+ tablename + "@" + e.toString());
			long lcurrent = System.currentTimeMillis();
			saveSMSObject(lcurrent + ".cdr", msgObject);
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext() + ":Error "
					+ "add row from:" + tablename + "@" + e.toString());
			long lcurrent = System.currentTimeMillis();
			saveSMSObject(lcurrent + ".cdr", msgObject);
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private static boolean validpin(String db, String pincode) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection(db);
			String query = "select luckynumber from lucky_number where luckynumber='"
					+ pincode.toUpperCase() + "'";

			Vector result = DBUtil.getVectorTable(connection, query);

			if (result.size() > 0) {
				return false;
			} else {
				return true;
			}

		} catch (Exception ex) {
			Util.logger.info("Failed" + ex.getMessage());

		} finally {
			dbpool.cleanup(connection);
		}
		return true;
	}

	public static String genpin(String dbname) {

		try {

			boolean runnext = true;
			String sLuckynumber = "x";
			int icount = 0;
			while (runnext) {

				icount++;
				Random iRandom = new Random();
				int luckynumber = iRandom.nextInt(999999);
				sLuckynumber = "" + luckynumber;
				sLuckynumber = "000000".substring(0, 6 - sLuckynumber.length())
						+ sLuckynumber;
				if (validpin(dbname, sLuckynumber) == true) {
					return sLuckynumber;
				}

				if (icount > 100000) {
					runnext = false;
					sLuckynumber = "x";
				}

			}
			return sLuckynumber;
		} catch (Exception e) {
			// TODO: handle exception
			return "x";
		}

	}

	private static BigDecimal add2luckynumber(MsgObject msgObject) {

		String luckynumber = genpin("gateway");

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String tablename = "lucky_number";
		Util.logger.info("add2:" + tablename + "@" + msgObject.getUserid()
				+ "@" + msgObject.getUsertext());
		sSQLInsert = "insert into " + tablename
				+ "( userid,  luckynumber, commandcode, serviceid) "
				+ "values (?, ?, ?, ?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, luckynumber);
			statement.setString(3, msgObject.getKeyword());
			statement.setString(4, msgObject.getServiceid());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2:" + tablename + "@"
						+ msgObject.getUserid() + ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");

				return new BigDecimal(-1);
			}
			if ("x".equalsIgnoreCase(luckynumber))
				return new BigDecimal(-1);
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext() + ":Error add row from :"
					+ tablename + "@" + e.toString());

			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext() + ":Error "
					+ "add row from:" + tablename + "@" + e.toString());

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
