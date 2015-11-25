package soap;

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

import icom.Constants;
import icom.DBPool;
import icom.ExecuteInsertSendLog;
import icom.ExecuteReSendQueue;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBDelete;
import icom.common.DBInsert;
import icom.common.DBSelect;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.MTPushObject;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class SoapMOCancelCD extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {

		try {

			msgObject.setSubCP(-1);
			String result = "";

			String commandcode = keyword.getKeyword();
			String[] sTokens = commandcode.split(" ");
			if (sTokens.length > 1) {
				commandcode = sTokens[1];
			}
							
			result = msgObject.sendMessageMO(msgObject.getUsertext(),
					"CD", commandcode);
			
			result = result.trim();
			
			Util.logger.info("SoapMOCancelCD ; user_id = " + msgObject.getUserid() 
					+ "; user text = " + msgObject.getUsertext() +" ### Send CD result = " + result);
			/*Requirememt about value response code:
				+ 1: Success
				+ Other response code, C/Dilaogues provide*/
			if(result.equals("1")){
				//truong hop huy? move tu customer -> customer_cacel
				//huy TC dk EU
				Util.logger.info("SoapMOCancelCD : send MO success \t@userid:"+msgObject.getUserid());
				String tableName = "customer";
				if(isexistCustomer(msgObject.getUserid(),tableName)){
					MoveCustomer2Cancel(msgObject.getUserid());
					deleteCustomer(msgObject.getUserid());
				}
			}
		} catch (Exception e) {
			int retriesnum = msgObject.getRetries_num();
			Util.logger
					.error(this.getClass().getName()
							+ "@"
							+ "Some Exception..!! Got -1, Going For Retry, Sleeping,Details: "
							+ "Msisdn: " + msgObject.getUserid()
							+ " Shortcode: " + msgObject.getServiceid()
							+ " Keyword: " + msgObject.getKeyword()
							+ " RequestID: " + msgObject.getRequestid()
							+ "CommandCode: " + keyword.getKeyword()
							+ " Online Retry countdown: " + retriesnum);
			Util.logger.info(this.getClass().getName() + "@" + "Exception: "
					+ e.toString());
			e.printStackTrace();
			msgObject.setTTimes(DateProc.createTimestamp());
			if (retriesnum >= Constants.MAX_RETRIES) {
				
				add2MOSendFailed(msgObject);
				
				DBUtil.Alert("SoapQM", "Sendfailed", "warn", "Sendfailed:"
						+ msgObject.getUserid() + "@" + msgObject.getMsg_id(),
						"tuannq");
			} else {
				msgObject.setRetries_num(retriesnum + 1);
				ExecuteReSendQueue.add2queueResend(msgObject);
			}
		}
		return null;
	}
	
	public static boolean isexistCustomer(String userid, String mlist) {
		Connection connection;
		DBPool dbpool;
		connection = null;
		boolean check =  false;
		dbpool = new DBPool();
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {

			connection = dbpool.getConnection("euro_report");

			String query3 = "select count(*) as count from " + mlist + " where pid = '"+getPid(userid)+"' and msisdn='" + userid + "' ";
			Util.logger.info("isexistCustomer:"+query3);
			stmt = connection.prepareStatement(query3,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("count");
					
				}
			}
		} catch (SQLException e) {
			Util.logger.error("isexistCustomer : Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return false;
		} catch (Exception e) {
			Util.logger.error("isexistCustomer: Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return false;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}
		if(count>0){
			check = true;
		}
		return check;
	}
	private static BigDecimal add2MOSendFailed(MsgObject msgObject) {
		Util.logger.info("mo_queue_error:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mo_queue_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE)"
				+ " values(?,?,?,?,?,?,?)";

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

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2MOSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2MOSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2MOSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}
	public static void MoveCustomer2Cancel(String user_id) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		
		String sqlUpdateMlistUser = "INSERT INTO customer_cancel(msisdn,LastQuestionId,SubscriptionType," +
				"TotalRepeatFail,CreateDate"
				+",CustomerLevel,LastUpdate,pid,QuestionSended,CurrentScore,DiemChuaSinhMDT,DiemDaSinhMDT,TotalMO"
				+",TotalScore,TotalMOByDay)"
				 +"SELECT msisdn,LastQuestionId,SubscriptionType,TotalRepeatFail,CreateDate"
				+",CustomerLevel,NOW(),pid,QuestionSended,CurrentScore,DiemChuaSinhMDT,DiemDaSinhMDT,TotalMO"
				+",TotalScore,TotalMOByDay "
				 +"FROM customer WHERE pid = '"+getPid(user_id)+"' and  msisdn ='"+user_id+"'";

		
		Util.logger
				.info("DBUtil@MoveCustomer2Cancel insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnection("euro_report");

			DBUtil.executeSQL("euro_report", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("MoveCustomer2Cancel@:move customer to customer_cancel, user_id="
							+ user_id
							+" Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}
	public static int getPid(String msisdn)
	{
		int numberPid=0;
		String pid="";
		if(msisdn.length()==11) pid=msisdn.substring(4,6);
		else
			pid=msisdn.substring(5,7);
		try
		{
			numberPid=Integer.parseInt(pid);
		}
		catch(Exception ex)
		{
			Util.logger.error("Error at Convert string to interger @getCustomer :"+ex.getMessage());
		}
		return numberPid;
	}
	public static void deleteCustomer(String user_id){
		Connection connection = null;
		DBPool dbpool = new DBPool();
		
		String sqlUpdateMlistUser = "delete FROM customer WHERE pid = '"+getPid(user_id)+"' and msisdn ='"+user_id+"' ";

		Util.logger
				.info("DBUtil@deleteCustomer insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnection("euro_report");

			DBUtil.executeSQL("euro_report", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("deleteCustomer@:move customer to customer_cancel, user_id="
							+ user_id
							+" Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}
}
