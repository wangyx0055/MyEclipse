package services;

import java.sql.Connection;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.Util;

public class ForwardMO2MOQueueSuperSim extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		Util.logger.info("ForwardMO2MOQueuestart! user_text:"+msgObject.getUsertext());
		
		if(msgObject.getUsertext().equals("")){
			Util.logger.error("ForwardMO2MOQueue @user test is empty");
			return null;
		}
		if(insertMoQueue("mo_queue",msgObject)!=-1){
			Util.logger.info("insertMoQueueDB ok user:"+msgObject.getUserid());
		}else{
			Util.logger.error("insertMoQueueDBE false user:"+msgObject.getUserid());
		}
		
		return null;
	}

	private static int insertMoQueue(String tableName, MsgObject msg){
		
		DBPool dbpool = new DBPool();
		Connection cnn=null;
		int result=1;
		String sqlInsert="INSERT INTO `mo_queue`" +
		"(`USER_ID`,`SERVICE_ID`,`MOBILE_OPERATOR`,`COMMAND_CODE`," +
		"`INFO`,`RESPONDED`,`REQUEST_ID`,`CHANNEL_TYPE`)" +
		"VALUES('"+msg.getUserid()+"','"+msg.getServiceid()+"'," +
				"'VMS','"+msg.getCommandCode()+"','"+msg.getUsertext()+"'," +
						"'0','"+msg.getRequestid()+"','"+msg.getChannelType()+"')"; 

		Util.logger.info("insertMoQueue @sql:"+sqlInsert);
		try{
			cnn=dbpool.getConnection("supersim");
			
			if (DBUtil.executeSQL(cnn, sqlInsert) < 0)
			{
				Util.logger.error("insertMoQueueDB "
						+ ": uppdate Statement: Insert mo_queue Failed:"
						+ sqlInsert);
				result = -1;
			}
			
		}
		catch(Exception ex){
			Util.logger.error("Error at @InsertCustomer :"+ex.getMessage());
			result=-1;
		}
		finally{
			
			dbpool.cleanup(cnn);
		}
		return result;
	}
}
