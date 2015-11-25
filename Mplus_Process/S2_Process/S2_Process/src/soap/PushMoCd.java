package soap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.Util;

public class PushMoCd extends QuestionManager   {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		add2MO( msgObject);
		Util.logger.info("PushMoCd push mo success \t@user:"+msgObject.getUserid() +"\t@info:"+msgObject.getUsertext());
		return null;
		
	}
	private static int add2MO(MsgObject msgObject) {
		
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		
		sSQLInsert = "INSERT INTO `mo_queue_cd`(`USER_ID`,`SERVICE_ID`," +
				"`MOBILE_OPERATOR`,`COMMAND_CODE`,`INFO`,`RECEIVE_DATE`," +
				"`RESPONDED`,`REQUEST_ID`,`CHANNEL_TYPE`)"+
				"VALUES(?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?)";
		
		try {
			connection = dbpool.getConnection("cdmo");

			
			statement = connection.prepareStatement(sSQLInsert);
			
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getCommandCode());
			statement.setString(5, msgObject.getUsertext());
			statement.setInt(6,1);
			statement.setString(7, msgObject.getRequestid()+"");
			statement.setInt(8, msgObject.getChannelType());
			
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2MO:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return -1;
			}else{
				Util.logger.info("add2MO:@getUserid:" + msgObject.getUserid()+
						"\t@getServiceid:"+ msgObject.getServiceid()+
						"\t@getMobileoperator:"+ msgObject.getMobileoperator()+
						"\t@getCommandCode:"+ msgObject.getCommandCode()+
						"\t@getUsertext:"+ msgObject.getUsertext()+
						"\t@getRequestid:"+ msgObject.getRequestid()+
						"\t@getChannelType:"+msgObject.getChannelType());
			}
			statement.close();
			return 1;
		} catch (SQLException e) {
			Util.logger.error("add2MO:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error("add2MO:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return -1;
		}
		finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}
}
