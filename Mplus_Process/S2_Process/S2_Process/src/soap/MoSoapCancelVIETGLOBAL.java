package soap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import org.tempuri.WsMoReceiveSoapProxy;

import icom.Constants;
import icom.DBPool;
import icom.ExecuteInsertSendLog;
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

public class MoSoapCancelVIETGLOBAL extends QuestionManager {
	String className = "MoSoapCancelVIETGLOBAL  ";
	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		try{
			WsMoReceiveSoapProxy proxy = new WsMoReceiveSoapProxy();
			String url = Constants._prop.getProperty("mo.soap.VIETGLOBAL.url");
			String sUserName = Constants._prop.getProperty("mo.soap.VIETGLOBAL.username");
			String sPassword = Constants._prop.getProperty("mo.soap.VIETGLOBAL.password");
					
			String sMsisdn = msgObject.getUserid();
			String sServiceNumber = msgObject.getServiceid();
			String sServiceCode = msgObject.getCommandCode();
			String sInfo = msgObject.getUsertext();
			int iRequestId =  msgObject.getRequestid().intValue();
			String sOperator = msgObject.getMobileoperator();
			int iChannel_Type = msgObject.getChannelType();
			proxy.setEndpoint(url);
			
			
			Util.logger.info(className + " fw mo.@info :\t@sUser:"
					+sUserName +"\t@Pass:"
					+sPassword +"\t@Msisdn:"
					+sMsisdn +"\t@ServiceId:"
					+sServiceNumber+"\t@CommandCode:"
					+sServiceCode+"\t@info:"
					+sInfo+"\t@requestid:"
					+iRequestId +"\t@Mobile:"
					+sOperator+"\tChannel:"+iChannel_Type);
					
		 	int iReturn = proxy.moReveive(sUserName, sPassword, sMsisdn, sServiceNumber, sServiceCode, sInfo, iRequestId, sOperator, iChannel_Type);
			
		 	Util.logger.info(className +" @return code:"+iReturn+"\t@user:"+sMsisdn);
		 	
		 	if(iReturn==1){
		 		String poolName = "gateway";
		 		String[] options  = keyword.getOptions().split("=");
		 		String tableName = "mlist_xoso5";
		 		if(options.length>2)
		 			tableName = options[1];
		 		String tableNameCancel = tableName + "_cancel";
		 		Util.logger.info(className +" table:"+tableName +"\t@table_cancel:"+tableNameCancel
		 				+"\t@user:"+msgObject.getUserid());
			
				/*DBSelect dbSelect = new DBSelect();
				MTPushObject mtPushObject = dbSelect.getMTPushByUserId(msgObject.getUserid(), tableName);
				if(mtPushObject != null){
					DBInsert dbInsert = new DBInsert();
					Util.logger.info("SoapMOCancel ## " +
							"insert into "+tableNameCancel+", userid = " 
							 + msgObject.getUserid());
					dbInsert.insertMTPush(mtPushObject, tableNameCancel,poolName);
					
					
					Util.logger.info(className +"@ " +
							"delete from "+tableName+", userid = " 
							 + msgObject.getUserid());
					DBDelete dbDelete = new DBDelete();*/
					//dbDelete.deleteByID(mtPushObject.getID(), tableName,poolName);
					DBUtil.UnRegisterServices(msgObject, keyword);
				//}
		 	}
		}catch (Exception e) {
			Util.logger.info(this.getClass().getName() + "@" + "Exception: "
					+ e.toString());
			msgObject.setTTimes(DateProc.createTimestamp());
			if (msgObject.getObjtype() == 0) {
				add2MOSendFailed(msgObject);
			} else {
				add2MTSendFailed(msgObject);
				msgObject.setProcess_result(Constants.RET_SEND_FAILED);
				msgObject.setMsgNotes(1, "Max retires:" + e.getMessage());
				msgObject.setTDoneTime(new Timestamp(System
						.currentTimeMillis()));
				ExecuteInsertSendLog.add2queueReceiveLog(msgObject);
			}
		}
		
		
		return null;
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
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE, CHANNEL_TYPE)"
				+ " values(?,?,?,?,?,?,?,?)";

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
			statement.setInt(8, msgObject.getChannelType());
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
			dbpool.cleanup(connection);

		}
	}

	private static BigDecimal add2MTSendFailed(MsgObject msgObject) {
		Util.logger.info("mt_queue_error:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mt_queue_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,NOTES,CHANNEL_TYPE)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setString(9, msgObject.getMsgnotes());
			statement.setInt(10, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2MTSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2MTSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2MTSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}
		finally {
			dbpool.cleanup(connection);

		}
	}
}
