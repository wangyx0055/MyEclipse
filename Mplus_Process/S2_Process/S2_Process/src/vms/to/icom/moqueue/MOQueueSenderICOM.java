package vms.to.icom.moqueue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import multiService.LoadMultiService;

import icom.DBPool;
import icom.MsgObject;
import icom.common.Util;

public class MOQueueSenderICOM {
	public static int insertMOQueueICOM(MsgObject ems) {
		if (ems.getMultiService() == 1) {
			return insertMOMultiICOM(ems);
		}

		String currentDate = icom.common.Util.getCurrentDate();
		icom.common.Util.logger.info("InsertMOQueueVMS@User_ID="
				+ ems.getUserid() + ",SERVICE_ID=" + ems.getServiceid()
				+ ",MOBILE_OPERATOR=" + ems.getMobileoperator()
				+ ",COMMAND_CODE=" + ems.getCommandCode() + ",INFO="
				+ ems.getKeyword() + ",RECEIVE_DATE=" + currentDate + ""
				+ ",RESPONDED=" + 0 + ",REQUEST_ID=" + ems.getRequestid()
				+ ", CHANNEL_TYPE=" + ems.getChannelType());
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();
		try {
			return receiver.insertMOQueueICOM(ems.getUserid(), ems
					.getServiceid(), ems.getMobileoperator(), ems
					.getCommandCode(), ems.getKeyword(), currentDate, "0", ems
					.getRequestid()
					+ "", ems.getChannelType() + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	private static int insertMOMultiICOM(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		Util.logger.info("insertMOQueueICOM@insertMOMultiICOM\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnection(LoadMultiService.icommplus);
			if (connection == null) {
				Util.logger
						.crisis("insertMOQueueICOM@insertMOMultiICOM: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ " mo_queue_multi "
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, REQUEST_ID,CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, LoadMultiService.ICOM_MULTI);
			statement.setString(5, msgObject.getKeyword());
			statement.setBigDecimal(6, msgObject.getRequestid());
			statement.setInt(7, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("insertMOQueueICOM@insertMOMultiICOM: Error@userid="
								+ msgObject.getUserid()
								+ "@service_id="
								+ msgObject.getServiceid()
								+ "@user_text="
								+ msgObject.getUsertext()
								+ "@message_type="
								+ msgObject.getMsgtype()
								+ "@request_id="
								+ msgObject.getRequestid().toString()
								+ "@channel_type=" + msgObject.getChannelType());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger
					.crisis("insertMOQueueICOM@insertMOMultiICOM: Error:@userid="
							+ msgObject.getUserid()
							+ "@serviceid="
							+ msgObject.getServiceid()
							+ "@usertext="
							+ msgObject.getUsertext()
							+ "@messagetype="
							+ msgObject.getMsgtype()
							+ "@requestid="
							+ msgObject.getRequestid().toString()
							+ "@"
							+ e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger
					.crisis("ContentAbstract@insertMOMultiICOM: Error:@userid="
							+ msgObject.getUserid() + "@serviceid="
							+ msgObject.getServiceid() + "@usertext="
							+ msgObject.getUsertext() + "@messagetype="
							+ msgObject.getMsgtype() + "@requestid="
							+ msgObject.getRequestid().toString() + "@"
							+ e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static String searchMOMulti(MsgObject msgObject) {

		String info = null;

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(LoadMultiService.icommplus);
			if (connection == null) {
				Util.logger
						.crisis("insertMOQueueICOM@searchMOMulti: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return null;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "Select  INFO from" + " mo_queue_multi WHERE ID = "
					+ msgObject.getRequestid();

			statement = connection.prepareStatement(sqlString);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					info = rs.getString("INFO");
				}
			}

		} catch (SQLException e) {
			Util.logger.crisis("ContentAbstract@searchMOMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return null;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@searchMOMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return null;
		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}

		return info;
	}

	private static int updateMOMulti(MsgObject msgObject, String info) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(LoadMultiService.icommplus);
			if (connection == null) {
				Util.logger
						.crisis("insertMOQueueICOM@updateMOMulti: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = " UPDATE " + " mo_queue_multi  " + "SET INFO = '"
					+ info + "," + msgObject.getServiceName() + "'"
					+ " WHERE ID = " + msgObject.getRequestid();

			statement = connection.prepareStatement(sqlString);

			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("ContentAbstract@updateMOMulti: Error@userid="
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
			Util.logger.crisis("ContentAbstract@updateMOMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@updateMOMulti: Error:@userid="
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
