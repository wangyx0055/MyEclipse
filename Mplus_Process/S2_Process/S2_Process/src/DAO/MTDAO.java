package DAO;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

public class MTDAO {
	public static int sendMT(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("MTDAO@sendMT\tuser_id:" + msgObject.getUserid()
					+ "\tservice_id:" + msgObject.getServiceid()
					+ "\trequest_id:" + msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("MTDAO@sendMT\tuser_id:" + msgObject.getUserid()
				+ "\tservice_id:" + msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis("MTDAO@sendMT: Error connection == null"
						+ msgObject.getUserid() + "\tTO"
						+ msgObject.getServiceid() + "\t"
						+ msgObject.getUsertext() + "\trequest_id:"
						+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,"
					+ "MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setInt(9, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("MTDAO@sendMT: Error@userid="
						+ msgObject.getUserid() + "@service_id="
						+ msgObject.getServiceid() + "@user_text="
						+ msgObject.getUsertext() + "@message_type="
						+ msgObject.getMsgtype() + "@request_id="
						+ msgObject.getRequestid().toString()
						+ "@channel_type=" + msgObject.getChannelType());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("MTDAO@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("MTDAO@sendMT: Error:@userid="
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

	/**
	 * push mt to other database
	 * 
	 * @param obj
	 * @param index
	 * @return
	 */
	public static int pustMtQueue(MsgObject obj, int index) {
		Connection cnn = null;
		PreparedStatement stmt = null;
		int result = 1;
		String table = "";
		DBPool dbPool = new DBPool();
		// obj.setContentType(21);
		// if(index!=0) table = "ems_send_queue"+index;
		// else table = "ems_send_queue";
		if (index != 0)
			table = "mt_queue" + index;
		else
			table = "mt_queue";
		try {
			cnn = dbPool.getConnectionGateway();
			String sqlString = "INSERT " + table
					+ "(USER_ID, SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,"
					+ "CONTENT_TYPE,INFO,SUBMIT_DATE,DONE_DATE,MESSAGE_TYPE)"
					+ "VALUES ('" + obj.getUserid() + "','"
					+ obj.getServiceid() + "','" + obj.getMobileoperator()
					+ "'," + "'" + obj.getKeyword() + "','"
					+ obj.getContenttype() + "','" + obj.getUsertext()
					+ "',NOW(),NOW(),'" + obj.getMsgtype() + "')";
			Util.logger.info("pustMtQueue \t@sql" + sqlString);
			stmt = cnn.prepareStatement(sqlString);
			if (stmt.executeUpdate() != 1) {
				Util.logger.crisis("MTqueueDAO@pustMtQueue: Error@userid="
						+ obj.getUserid() + "@service_id=" + obj.getServiceid()
						+ "@user_text=" + obj.getUsertext() + "@message_type="
						+ obj.getMsgtype() + "@request_id="
						+ obj.getRequestid().toString() + "@channel_type="
						+ obj.getChannelType());
				result = -1;
			}

		} catch (SQLException e) {
			result = -1;
			Util.logger.crisis("MTqueueDAO@pustMtQueue: Error@userid="
					+ obj.getUserid() + "@service_id=" + obj.getServiceid()
					+ "@user_text=" + obj.getUsertext() + "@message_type="
					+ obj.getMsgtype() + "@request_id="
					+ obj.getRequestid().toString() + "@channel_type="
					+ obj.getChannelType());

		} catch (Exception e) {
			result = -1;
			Util.logger.crisis("MTqueueDAO@pustMtQueue: Error@userid="
					+ obj.getUserid() + "@service_id=" + obj.getServiceid()
					+ "@user_text=" + obj.getUsertext() + "@message_type="
					+ obj.getMsgtype() + "@request_id="
					+ obj.getRequestid().toString() + "@channel_type="
					+ obj.getChannelType());

		} finally {

			dbPool.cleanup(cnn, stmt);
		}
		return result;
	}

	public static void writeMTLog(MsgObject msgObject) {

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbPool = new DBPool();
		String tablename = "mt"
				+ new SimpleDateFormat("yyyyMM").format(new Date());
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,CONTENT_TYPE,INFO,MESSAGE_TYPE,CHANNEL_TYPE,AMOUNT,SUBMIT_DATE,DONE_DATE)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbPool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			// statement.setString(5, msgObject.getKeyword());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setInt(8, msgObject.getChannelType());
			statement.setInt(9, 0);
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			statement.setTimestamp(10, getCurrentTime());
			statement.setTimestamp(11, getCurrentTime());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2:" + tablename + "@"
						+ msgObject.getUserid() + ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
			} else
				Util.logger.info(sSQLInsert);
			statement.close();
		} catch (SQLException e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext() + ":Error add row from "
					+ tablename + ":" + e.toString());

		} catch (Exception e) {
			Util.logger.error("add2:" + tablename + "@" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext() + ":Error add row from "
					+ tablename + ":" + e.toString());
		}

		finally {
			dbPool.cleanup(connection, statement);

		}
	}

	public static Timestamp getCurrentTime() {
		Date date = new Date();
		Timestamp time = new Timestamp(date.getTime());
		return time;
	}

	public static Hashtable<String, String> getMessageReminder() {
		String funName = "getMessageReminder ";

		Hashtable<String, String> hMessageReminder = new Hashtable<String, String>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		DBPool pool = new DBPool();
		String sql = "SELECT * FROM  daugia_randommessage";

		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					String sType = rs.getInt("Type") + "";
					String sInfo = rs.getString("Info");

					if (hMessageReminder.get(sType) != null) {
						sInfo = sInfo + ";" + hMessageReminder.get(sType);
						hMessageReminder.remove(sType);
					}
					hMessageReminder.put(sType, sInfo);
				}
			} else {
				Util.logger.error(funName + " @connection null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return hMessageReminder;
	}

	public static String getRandomMessage(String input, int size)
			throws Exception {
		String output = "";
		String[] aTemp = input.split(";");
		Random ran = new Random();
		int iRan = ran.nextInt(size);
		output = aTemp[iRan];
		return output;
	}
}
