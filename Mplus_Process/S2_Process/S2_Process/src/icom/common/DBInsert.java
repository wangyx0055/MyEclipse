package icom.common;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import daugia.DGConstants;

/**
 * 
 * @author DanND ICom
 * @Date_Created 2011-04-19
 * 
 */

public class DBInsert {
	
	public int insertMTPush(MTPushObject mtObj, String tblName, String poolName) {

		int iReturn = 1;

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(poolName);
			if (connection == null) {				
				return -1;
			}

			sqlString = "INSERT INTO "
					+ tblName
					+ "( USER_ID, status, COMMAND_CODE, NUMBER_MT_ALARM, LINK_RING, LAST_CODE) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, mtObj.getUserId());
			statement.setInt(2, mtObj.getStatus());
			statement.setString(3, mtObj.getCommandCode());
			statement.setInt(4, 0);
			statement.setString(5, mtObj.getLinkRing());
			statement.setString(6, mtObj.getLastCode());

			Util.logger.info("insertMTPush SQL Insert Query = " + sqlString);
			if (statement.executeUpdate() != 1) {				
				iReturn = -1;
			} 

		} catch (SQLException e) {
			Util.logger
					.crisis("Icom.common.DBInsert  ###@## insertMTPush: Error@ = " + e.getMessage());
			return -1;
		} catch (Exception e) {
			Util.logger
			.crisis("Icom.common.DBInsert  ###@## insertMTPush: Error@ = " + e.getMessage());
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	// Chuyen thong tin vao bang vms_charge
	public int insertVMSCharge(MsgObject msgObject, String sHours) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = "";

		DBPool dbpool = new DBPool();

		Util.logger.info("VMS@DBInsert: insertVMSCharge \tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tmobile_operator:"
				+ msgObject.getMobileoperator() + "\tkeyword:"
				+ msgObject.getServiceName() + "\tcontent_type:"
				+ "\tmessage_type:" + msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tcontent_id:"
				+ msgObject.getContentId() + "\tamount:"
				+ msgObject.getAmount() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tservice_name:"
				+ msgObject.getServiceName());

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("VMS@DBInsert: insertVMSCharge: connection is null"
								+ msgObject.getUserid()
								+ ":\tTO"
								+ msgObject.getServiceid()
								+ ":\t"
								+ msgObject.getUsertext()
								+ ":\trequest_id="
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblCharge
					+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, MESSAGE_TYPE, REQUEST_ID, SERVICE_NAME, CHANNEL_TYPE, "
					+ " CONTENT_ID, AMOUNT, TIME_DELIVERY, COMPANY_ID, IS_THE_SEND, LAST_CODE, OPTIONS, MESSAGE_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

			Util.logger.info("VMS@DBInsert: insertVMSCharge: \tSQL Insert:"
					+ sqlString);

			statement = connection.prepareStatement(sqlString);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getCommandCode());
			statement.setInt(5, msgObject.getMsgtype());
			statement.setBigDecimal(6, msgObject.getRequestid());
			statement.setString(7, msgObject.getServiceName());

			statement.setInt(8, msgObject.getChannelType());

			statement.setInt(9, msgObject.getContentId());
			statement.setLong(10, msgObject.getAmount());

			statement.setString(11, sHours);

			// Ten dich vu
			statement.setString(12, msgObject.getCompany_id());
			statement.setString(13, "1");
			statement.setString(14, msgObject.getLast_code());
			statement.setString(15, msgObject.getOption());
			statement.setLong(16, msgObject.getMsg_id());

			Util.logger
					.info("VMS@DBInsert: insertVMSCharge: \tuser_id:"
							+ msgObject.getUserid()
							+ "\tservice_id:"
							+ msgObject.getServiceid()
							+ "\tmobile_operator:"
							+ msgObject.getMobileoperator()
							+ "\tkeyword:"
							+ msgObject.getServiceName()
							+ "\tmessage_type:"
							+ msgObject.getMsgtype()
							+ "\trequest_id:"
							+ msgObject.getRequestid().toString()
							+ "\tcontent_id:"
							+ msgObject.getContentId()
							+ "\tamount:"
							+ msgObject.getAmount()
							+ "\tservice_name:" + msgObject.getServiceName());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis(msgObject.getUserid() + "\tservice_id:"
						+ msgObject.getServiceid() + "\tmobile_operator:"
						+ msgObject.getMobileoperator() + "\tkeyword:"
						+ msgObject.getServiceName() + "\tmessage_type:"
						+ msgObject.getMsgtype() + "\trequest_id:"
						+ msgObject.getRequestid().toString() + "\tcontent_id:"
						+ msgObject.getContentId() + "\tamount:"
						+ msgObject.getAmount() + "\tservice_name:"
						+ msgObject.getServiceName());
				return -1;
			}
		} catch (SQLException e) {
			Util.logger
					.error("VMS@ExcecuteCharging:sendMT\tSql error\tuser_id="
							+ msgObject.getUserid() + "\tservice_id:"
							+ msgObject.getServiceid() + "\tmobile_operator:"
							+ msgObject.getMobileoperator() + "\tkeyword:"
							+ msgObject.getServiceName() + "\tcontent_type:"
							+ "\tmessage_type=" + msgObject.getMsgtype()
							+ "\trequest_id:"
							+ msgObject.getRequestid().toString()
							+ "\tcontent_id:" + msgObject.getContentId()
							+ "\tamount:" + msgObject.getAmount()
							+ "\tservice_name:" + msgObject.getServiceName());

			Util.logger.error("VMS@DBInsert: insertVMSCharge\tSql ex:"
					+ e.getMessage());

			return -1;
		} catch (Exception e) {
			Util.logger
					.error("VMS@DBInsert: insertVMSCharge to: \tsendMT\tuser_id:"
							+ msgObject.getUserid() + "\tservice_id:"
							+ msgObject.getServiceid() + "\tmobile_operator:"
							+ msgObject.getMobileoperator() + "\tkeyword:"
							+ msgObject.getServiceName() + "\tcontent_type:"
							+ "\tmessage_type:" + msgObject.getMsgtype()
							+ "\trequest_id:"
							+ msgObject.getRequestid().toString()
							+ "\tcontent_id:" + msgObject.getContentId()
							+ "\tamount:" + msgObject.getAmount()
							+ "\tchannel_type:" + msgObject.getChannelType()
							+ "\tservice_name:" + msgObject.getServiceName());
			Util.logger.error("VMS@ExcecuteCharging:\tsendMTError to: ex:"
					+ e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return 1;
	}
	
	public int sendMT(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("DBInsert@sendMT\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("DBInsert@sendMT\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("DBInsert@sendMT: Error connection == null"
								+ msgObject.getUserid() + "\tTO"
								+ msgObject.getServiceid() + "\t"
								+ msgObject.getUsertext() + "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ DGConstants.MTQUEUE_DKHUY
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

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
				Util.logger.crisis("DBInsert@sendMT: Error@userid="
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
			Util.logger.crisis("DBInsert@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("DBInsert@sendMT: Error:@userid="
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
	
}
