package icom;


import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import daugia.DaugiaCommon;

import multiService.LoadMultiService;

import vmg.itrd.ws.MTSenderVMS;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public abstract class QuestionManager {

	@SuppressWarnings("unchecked")
	public void start(Properties prop, MsgObject msgObject, Keyword keyword,
			Hashtable services) throws Exception {
		try {
			Collection messages = getMessages(prop, msgObject, keyword,
					services);
			if (messages != null) {
				Iterator iter = messages.iterator();
				for (int i = 1; iter.hasNext(); i++) {
					MsgObject ems = (MsgObject) iter.next();

					if (ems.getPkgService()) {// Xu ly voi dich vu theo goi
												
						if(ems.getChargingPackage() == 2){// HET KM => CHarging not send MT
							continue;
						}
					}

					if (ems.getSubCP() == 0)// ben VMS
					{
						Util.logger
								.info("QuestionManager: Send ems vao mt_queue ben VMS");
						if(ems.getMultiService() == 1){ // MultiService
							insertMTMulti(ems);
							continue;
						}
						
						if(keyword.getService_ss_id().trim().equals("DAUGIA")){
							DaugiaCommon dgCommon = new DaugiaCommon();
							dgCommon.sendMT(ems);							
						}else if(sendMT(ems) == 1) {
							Util.logger.info("QuestionManager: Send ems vao mt_queue OK");
						}
						
					} else if (ems.getSubCP() == 1)// tu cp khac
					{
						if(ems.getMultiService() == 1){ // MultiService
							icomInsertMTMulti(ems);
							continue;
						}
						
						Util.logger.info("QuestionManager@Send ems tu ICOM vao mt_queue ben VMS qua ws");
						if (MTSenderVMS.insertMTQueueVMS(ems) == 1) {
							Util.logger
									.info("QuestionManager@Send ems tu ICOM vao mt_queue ben VMS OK");
						}
					}else if(ems.getSubCP() == -1){
						//***********
						// VASC - Xu ly Ben VASC												
						continue;
					}else if( ems.getSubCP() == 2){
						if(ems.getMultiService() == 1){ // MultiService
							insertMTMulti(ems);
							continue;
						}
					}
					else {
						return;
					}

				}
			} else {
				Util.logger.info("ContentAbstract@start:"
						+ msgObject.getUserid() + "@TO@"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + ": LOST MESSAGE");

			}
		} catch (Exception e) {
			Util.logger
					.info("ContentAbstract@start:" + msgObject.getUserid()
							+ "@TO@" + msgObject.getServiceid() + "@"
							+ msgObject.getUsertext() + ": LOST MESSAGE"
							+ e.toString());
			Util.logger.printStackTrace(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected abstract Collection getMessages(Properties prop,
			MsgObject msgObject, Keyword keyword, Hashtable services)
			throws Exception;

	private static int sendMT(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("ContentAbstract@sendMT\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("ContentAbstract@sendMT\tuser_id:"
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
						.crisis("ContentAbstract@sendMT: Error connection == null"
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
					+ Constants.tblMTQueue
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
				Util.logger.crisis("ContentAbstract@sendMT: Error@userid="
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
			Util.logger.crisis("ContentAbstract@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@sendMT: Error:@userid="
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
	
	private static int insertMTMulti(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("ContentAbstract@insertMTMulti\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}
		
//		String commandCode = searchMTMulti(msgObject);
//		if(commandCode != null){
//			if(!commandCode.equals(""))
//			return updateMTMulti(msgObject, commandCode);
//		}

		DBPool dbpool = new DBPool();

		Util.logger.info("ContentAbstract@insertMTMulti\tuser_id:"
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
						.crisis("ContentAbstract@insertMTMulti: Error connection == null"
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
					+ " mt_queue_multi "
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE, MO_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getServiceName());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setInt(9, msgObject.getChannelType());
			statement.setBigDecimal(10, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ContentAbstract@insertMTMulti: Error@userid="
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
			Util.logger.crisis("ContentAbstract@insertMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@insertMTMulti: Error:@userid="
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
	
	private static int icomInsertMTMulti(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("ContentAbstract@icomInsertMTMulti\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}
		
//		String commandCode = icomSearchMTMulti(msgObject);
//		if(commandCode != null){
//			if(!commandCode.equals(""))
//			return icomUpdateMTMulti(msgObject, commandCode);
//		}

		DBPool dbpool = new DBPool();

		Util.logger.info("ContentAbstract@icomInsertMTMulti\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnection(LoadMultiService.VMSMPLUS_POOL);
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@icomInsertMTMulti: Error connection == null"
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
				+ " mt_queue_multi "
				+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE, MO_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getServiceName());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setInt(9, msgObject.getChannelType());
			statement.setBigDecimal(10, msgObject.getMoId()); // Khac voi VMS
		
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ContentAbstract@icomInsertMTMulti: Error@userid="
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
			Util.logger.crisis("ContentAbstract@icomInsertMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@icomInsertMTMulti: Error:@userid="
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
	
	private static String searchMTMulti(MsgObject msgObject) {
		
		String commandCode = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		ResultSet rs = null;
		
		DBPool dbpool = new DBPool();
		
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@searchMTMulti: Error connection == null"
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
			sqlString = "Select  COMMAND_CODE from"
					+ " mt_queue_multi WHERE MO_ID = " + msgObject.getRequestid();

			statement = connection.prepareStatement(sqlString);
			
			if(statement.execute()){
				rs = statement.getResultSet();
				while(rs.next()){
					commandCode = rs.getString("COMMAND_CODE");
				}
			}
			
		} catch (SQLException e) {
			Util.logger.crisis("ContentAbstract@searchMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return null;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@searchMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return null;
		} finally {
			dbpool.cleanup(rs,statement);
			dbpool.cleanup(connection);
		}
		
		return commandCode;
	}
	
	private static String icomSearchMTMulti(MsgObject msgObject) {
		
		String commandCode = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		ResultSet rs = null;
		
		DBPool dbpool = new DBPool();
		
		try {
			connection = dbpool.getConnection(LoadMultiService.VMSMPLUS_POOL);
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@icomSearchMTMulti: Error connection == null"
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
			sqlString = "Select  COMMAND_CODE from"
					+ " mt_queue_multi WHERE MO_ID = " + msgObject.getRequestid();

			statement = connection.prepareStatement(sqlString);
			
			if(statement.execute()){
				rs = statement.getResultSet();
				while(rs.next()){
					commandCode = rs.getString("COMMAND_CODE");
				}
			}
			
		} catch (SQLException e) {
			Util.logger.crisis("ContentAbstract@icomSearchMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return null;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@icomSearchMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return null;
		} finally {
			dbpool.cleanup(rs,statement);
			dbpool.cleanup(connection);
		}
		
		return commandCode;
	}
	
	private static int updateMTMulti(MsgObject msgObject, String commandCode) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@updateMTMulti: Error connection == null"
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
			String paramComand = "";
			if(commandCode.equals("DK") || commandCode.equals("HUY")){
				paramComand = commandCode + " " + msgObject.getCommandCode();
			}else{
				paramComand = commandCode + "," + msgObject.getCommandCode();
			}
			sqlString = " UPDATE "
					+ " mt_queue_multi  " 
					+ "SET COMMAND_CODE = '" + paramComand + "'"
					+ " WHERE MO_ID = " + msgObject.getRequestid();
			
			
			statement = connection.prepareStatement(sqlString);
			
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ContentAbstract@updateMTMulti: Error@userid="
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
			Util.logger.crisis("ContentAbstract@updateMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@updateMTMulti: Error:@userid="
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
	
	private static int icomUpdateMTMulti(MsgObject msgObject, String commandCode) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(LoadMultiService.VMSMPLUS_POOL);
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@updateMTMulti: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}
			
			String paramComand = "";
			if(commandCode.equals("DK") || commandCode.equals("HUY")){
				paramComand = commandCode + " " + msgObject.getServiceName();
			}else{
				paramComand = commandCode + "," + msgObject.getServiceName();
			}
			
			sqlString = " UPDATE "
					+ " mt_queue_multi  " 
					+ "SET COMMAND_CODE = '" + paramComand + "'"
					+ " WHERE MO_ID = " + msgObject.getRequestid();
			
			statement = connection.prepareStatement(sqlString);
			
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ContentAbstract@updateMTMulti: Error@userid="
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
			Util.logger.crisis("ContentAbstract@updateMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@updateMTMulti: Error:@userid="
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
