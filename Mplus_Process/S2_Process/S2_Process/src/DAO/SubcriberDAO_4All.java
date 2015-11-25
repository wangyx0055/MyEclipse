package DAO;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import mtPush.PushMTConstants;

public class SubcriberDAO_4All {	
	public static Hashtable<String, String> hashServices = new Hashtable<String, String>();
	/**
	 * LoanDT
	 * for subcriber all service
	 * check, insert, remove from mlist to cancel...	
	 */

	public static boolean isexist_in_mlist(String userid, String mlist,	String command_code, String options) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist + " where user_id='"
					+ userid + "' and upper(command_code)='"
					+ command_code.trim().toUpperCase() + "'";
			if (!options.equalsIgnoreCase(""))
				query3 += " and options = '" + options +"'";
		
			
			Util.logger.info("isexist_in_mlist @sql:"+query3);
			
			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public static boolean isexist_in_cancel(String userid, String mlist,
			String command_code, String options) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist
					+ "_cancel where user_id='" + userid
					+ "' and upper(command_code)='"
					+ command_code.trim().toUpperCase() + "'";
			if (!options.equalsIgnoreCase(""))
				query3 += " and options = '" + options +"'";
			/****
			 * Doi voi cac dich vu xo so thi lay theo companyid
			 * **/
			
			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	/*****
	 * Move mlist cancel sang mlist_subcriber
	 * 
	 * *
	 ****/

	
	public static int InsertSubcriberCancel(MsgObject ems, String mtfree,
			int msgtype) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "Insert into mlist_subcriber_cancel(service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,company_id,channel_type,options,reg_count) values ('"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getUserid()
				+ "','"
				+ ems.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getLongRequestid()
				+ "','"
				+ msgtype
				+ "','"
				+ ems.getMobileoperator()
				+ "',"
				+ mtfree
				+ ","
				+ ems.getCompany_id()
				+ ","
				+ ems.getChannelType()
				+ ",'"
				+ ems.getOption() + "',1)";
		Util.logger.info("DbUtil@InsertSubcriberCancel@SQL Insert: "
				+ sqlInsert);
		try {
			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertSubcriberCancel@"
						+ ": insert Statement: Insert Failed:" + sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertSubcriberCancel@:Insert Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	/***
	 * Trong truong hop huy, move toan bo tu mlist sang mlist_cancel
	 * **/
	public static int InsertMlist2MlistCancel(String mlist, String user_id,
			long amount, int channelType, String command_code, String options) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String sqlInsert = "insert into "
				+ mlist
				+ "_cancel(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
				+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
				+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT)"
				+ " select USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,current_timestamp()"
				+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
				+ "," + amount + ",CONTENT_ID,SERVICE,COMPANY_ID,0,"
				+ channelType + ",REG_COUNT from " + mlist + " WHERE USER_ID='"
				+ user_id + "' and upper(COMMAND_CODE)='"
				+ command_code.toUpperCase() + "'";
		if (!options.equalsIgnoreCase(""))
			sqlInsert += " and options = '" + options +"'";
		Util.logger.info("DbUtil@InsertMlist2MlistCancel@SQL Insert: "
				+ sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertMlist2MlistCancel@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertMlist2MlistCancel@:Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public static int InsertSubcriber(String mlist, MsgObject ems,
			String mtfree, int msgtype) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "Insert into mlist_subcriber(service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,company_id,channel_type,options,reg_count) values ('"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getUserid()
				+ "','"
				+ ems.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getLongRequestid()
				+ "','"
				+ msgtype
				+ "','"
				+ ems.getMobileoperator()
				+ "',"
				+ mtfree
				+ ","
				+ ems.getCompany_id()
				+ ","
				+ ems.getChannelType()
				+ ",'"
				+ ems.getOption() + "',1)";
		Util.logger.info("DbUtil@InsertSubcriber@SQL Insert: " + sqlInsert);
		try {
			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertSubcriber@"
						+ ": insert Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertSubcriber@:Insert  " + mlist + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	/*****
	 * Move tu mlist sang mlist_subcriber_cancel Move xong thi Xoa user id ra
	 * khoi Subcriber *
	 ****/
	public static void MoveMlist2SubcriberCancel(String mlist, String user_id,
			String command_code, int channel_type, String options) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String whereClause = "where user_id='" + user_id
				+ "' and upper(command_code)='" + command_code.toUpperCase()
				+ "'";	
		if (!options.equalsIgnoreCase(""))
			whereClause += " and options = '" + options +"'";
		String sqlUpdateMlistUser = "insert into mlist_subcriber_cancel(service,user_id,service_id,date,options"
				+ ",failures,last_code,autotimestamps,command_code,request_id,message_type,mobile_operator,mt_count"
				+ ",mt_free,company_id,duration,active,channel_type,reg_count)"
				+ " select service,user_id,service_id,date,options,failures,last_code,autotimestamps,command_code"
				+ ",request_id,message_type,mobile_operator,mt_count,mt_free,company_id,duration,active,"
				+ channel_type
				+ ",reg_count "
				+ " from "
				+ mlist
				+ " "
				+ whereClause;

		Util.logger
				.info("DBUtil@MoveSubcriber2SubcriberCancel:Sql insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnectionGateway();

			DBUtil.executeSQL("gateway", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("DBUtil@MoveSubcriber2SubcriberCancel@:move mlist_subcriber to mlist_subcriber_cancel, user_id="
							+ user_id
							+ ",command_code="
							+ command_code
							+ " Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}

	/*****
	 * Move mlist cancel sang mlist_subcriber
	 * 
	 * *
	 ****/
	public static void MoveMlistCancel2Subcriber(String mlist, String user_id,
			String command_code, int channel_type, String options) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String whereClause = "where user_id='" + user_id
				+ "' and upper(command_code)='" + command_code.toUpperCase()
				+ "'";
		if (!options.equalsIgnoreCase(""))
			whereClause += " and options = '" + options +"'";
		String sqlUpdateMlistUser = "insert into mlist_subcriber(service,user_id,service_id,date,options"
				+ ",failures,last_code,autotimestamps,command_code,request_id,message_type,mobile_operator,mt_count"
				+ ",mt_free,company_id,duration,active,channel_type,reg_count)"
				+ " select service,user_id,service_id,date,options,failures,last_code,autotimestamps,command_code"
				+ ",request_id,message_type,mobile_operator,mt_count,mt_free,company_id,duration,active,"
				+ channel_type
				+ ",reg_count + 1"
				+ " from "
				+ mlist
				+ "_cancel " + whereClause;

		Util.logger
				.info("DBUtil@MoveSubcriberCancel2Subcriber:Sql insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnectionGateway();

			DBUtil.executeSQL("gateway", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("DBUtil@MoveSubcriberCancel2Subcriber@:move mlist_subcriber to mlist_subcriber_cancel, user_id="
							+ user_id
							+ ",command_code="
							+ command_code
							+ " Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public static void DeleteSubcriber(String user_id, String command_code,
			int channel_type, String options) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// delete khoi danh sach user
		String sqlUpdateMlistUser = "delete from mlist_subcriber where user_id='"
				+ user_id
				+ "' and upper(command_code)='"
				+ command_code.toUpperCase() + "'";
		if (!options.equalsIgnoreCase(""))
			sqlUpdateMlistUser += " and options = '" + options +"'";
		Util.logger.info("DBUtil@DeleteSubcriber:Sql delete:"
				+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnectionGateway();

			DBUtil.executeSQL("gateway", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("DeleteSubcriber@:Delete  mlist_subcriber, user_id="
							+ user_id + ",command_code=" + command_code
							+ "options = '" + options +"'+ Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public static void DeleteSubcriberCancel(String user_id,
			String command_code, int channel_type, String options) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// delete khoi danh sach user
		String sqlUpdateMlistUser = "delete from mlist_subcriber_cancel where user_id='"
				+ user_id
				+ "' and upper(command_code)='"
				+ command_code.toUpperCase() + "'";

		if (!options.equalsIgnoreCase(""))
			sqlUpdateMlistUser += " and options = '" + options +"'";
		Util.logger.info("DBUtil@DeleteSubcriberCancel:Sql delete:"
				+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnectionGateway();

			DBUtil.executeSQL("gateway", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("DeleteSubcriberCancel@:Delete  mlist_subcriber_cancel, user_id="
							+ user_id
							+ ",command_code="
							+ command_code
							+ " and options ='"+options+"' + Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}
	public static int InsertMtpush2MtpushCancel(String mlist, String user_id,
			String command_code) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String sqlInsert = "insert into "
				+ mlist
				+ "_cancel(USER_ID,STATUS,COMMAND_CODE,NUMBER_MT_ALARM,LINK_RING,LAST_CODE)"
				+ " select USER_ID,STATUS,COMMAND_CODE,NUMBER_MT_ALARM,LINK_RING,LAST_CODE from "
				+ mlist + " WHERE USER_ID='" + user_id
				+ "' and upper(COMMAND_CODE)='" + command_code.toUpperCase()
				+ "'";

		Util.logger.info("DbUtil@InsertMlist2MlistCancel@SQL Insert: "
				+ sqlInsert);

		try {

			connection = dbpool.getConnection(PushMTConstants.pushMTPool);
			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertMlist2MlistCancel@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertMlist2MlistCancel@:Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}
	/***
	 * Trong truong hop dang ky lai, move toan bo tu mlist_cancel sang mlist
	 * **/
	
	/***
	 * xoa trong mlist(mlist or mlist_cancel)
	 * **/
	public static int DelMlist(String mlist, String user_id, String command_code, String options) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqldel = "DELETE FROM " + mlist + " WHERE user_id='" + user_id
				+ "' and upper(command_code)='" + command_code.toUpperCase()
				+ "' ";
		if (!options.equalsIgnoreCase(""))
			sqldel += " and options = '" + options +"'";
		Util.logger.info("DBUtil@DelMlist@SQL delete: " + sqldel);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqldel) < 0) {
				Util.logger.error("Insert2Mlist@"
						+ ": delete Statement: Delete  " + mlist + " Failed:"
						+ sqldel);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("Insert2Mlist@:Insert  " + mlist + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}
	/***
	 * Trong truong hop dang ky moi, insert vao mlist
	 * **/
	public static int Insert2Mlist(String mlist, MsgObject ems, String mtfree,
			int msgtype, long lduration, long amount) {
		int ireturn = 1;

		if (ems.getPkgService())
			return 1;

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "INSERT INTO "
				+ mlist
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID,ACTIVE,CHANNEL_TYPE,REG_COUNT) values ('"
				+ ems.getUserid()
				+ "','"
				+ ems.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ ems.getOption()
				+ "',"
				+ 0
				+ ",'"
				+ ems.getLast_code()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + ems.getCommandCode()
				+ "','" + ems.getLongRequestid() + "','" + msgtype + "','"
				+ ems.getMobileoperator() + "','" + 0 + "'," + mtfree + ","
				+ lduration + "," + amount + "," + ems.getContentId() + ",'"
				+ ems.getCommandCode() + "','" + ems.getCompany_id() + "'," + 0
				+ "," + ems.getChannelType() + "," + 1 + ")";

		Util.logger.info("@Insert2Mlist@SQL Insert: " + sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("Insert2Mlist@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("Insert2Mlist@:Insert  " + mlist + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}	
	public static String GetOption (String services)
	{
		String result ="";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuerry = "Select options from service_detail where services = '"+ services +"'";
		Util.logger.info("Querry get option:" + sqlQuerry);
		try {
			connection = dbpool.getConnectionGateway();

			Util.logger.info("SQL Query get content: " + sqlQuerry);
			Vector vtResult = DBUtil.getVectorTable(connection, sqlQuerry);

			Util.logger.info("@getContent: queryStatement:" + vtResult.size() + "@"
					+ sqlQuerry);
			if (vtResult.size() == 0) {				
			} else {

				for (int i = 0; i < vtResult.size(); i++) {

					Vector item = (Vector) vtResult.elementAt(i);
					result += (String) item.elementAt(0) +";";
				}
			}
			Util.logger.info("getOption: record[0]:" + result);			
			
		
		} catch (Exception ex) {
			Util.logger.info("getOption: Failed"+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return result;
		} finally {
			dbpool.cleanup(connection);
		}	
		return result;
	}
	
	public static void loadServicesDetail(String services)
	{ 
		Connection cnn=null;
		PreparedStatement stmt=null;
		ResultSet rs=null;
		DBPool dbpool = new DBPool();	
		try
		{			
			String	sqlSelect="Select options, name_services from service_detail";	
			//Util.logger.info("HastableDAO @Select: "+sqlSelect);
			cnn=dbpool.getConnectionGateway();
			stmt=cnn.prepareStatement(sqlSelect);		
			rs=stmt.executeQuery();
			while(rs.next())
			{
				hashServices.put(rs.getString("options"),rs.getString("name_services") );								
			}
			
		}
		catch(Exception ex)
		{
			Util.logger.error("Error at get companyID :"+ex.getMessage());
			Util.logger.printStackTrace(ex);
		}
		finally
		{
			dbpool.cleanup(rs);
			dbpool.cleanup(cnn,stmt);
		}  
	  
	}	
	public static int InsertMlistCancel2Mlist(String mlist, String user_id,
			long amount, int channelType, String command_code,
			String sOptions) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String sqlInsert = "insert into "
				+ mlist
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
				+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
				+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT)"
				+ " select USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,current_timestamp()"
				+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
				+ "," + amount + ",CONTENT_ID,SERVICE,COMPANY_ID,0,"
				+ channelType + ",REG_COUNT +1 from " + mlist
				+ "_cancel WHERE USER_ID='" + user_id
				+ "' and upper(COMMAND_CODE)='" + command_code.toUpperCase()
				+ "'";

		if (!"".equalsIgnoreCase(sOptions)) {
			sqlInsert = "insert into "
					+ mlist
					+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
					+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
					+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT)"
					+ " select USER_ID,SERVICE_ID,DATE,'"
					+ sOptions
					+ "',FAILURES,LAST_CODE,current_timestamp()"
					+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
					+ "," + amount + ",CONTENT_ID,SERVICE,COMPANY_ID,0,"
					+ channelType + ",REG_COUNT +1 from " + mlist
					+ "_cancel WHERE USER_ID='" + user_id
					+ "' and upper(COMMAND_CODE)='"
					+ command_code.toUpperCase() + "'";
		}
		Util.logger.info("DbUtil@InsertMlistCancel2Mlist@SQL Insert: "
				+ sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertMlistCancel2Mlist@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertMlistCancel2Mlist@:Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

  
}
