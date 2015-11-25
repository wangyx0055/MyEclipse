package services;

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
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.Sender;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import DAO.MListDAO;
import DTO.MlistInfoDTO;

public class FowardMOToOtherDatabase extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		Util.logger.info("Starting Handle MO with user= "
				+ msgObject.getUserid() + ", info=" + msgObject.getUsertext());
		try {
			// xac dinh mlist
			HashMap _option = Util.getParametersAsString(keyword.getOptions());
			String dbContent = Util.getStringfromHashMap(_option, "dbcontent",
					"x").trim();
			String tableName = Util.getStringfromHashMap(_option, "tableName",
					"x").trim();
			if ("x".equalsIgnoreCase(dbContent)
					|| "x".equalsIgnoreCase(tableName)) {
				Util.logger
						.info("Khong xac dinh duoc mlist || dbcontent duoc get ra tu param options:"
								+ " "
								+ msgObject.getUserid()
								+ ","
								+ keyword.getOptions());
				return null;
			} else {
				msgObject.setCommandCode(keyword.getService_ss_id());
				// kiem tra ton tai trong tap mlist
				insertMoQueue(tableName, msgObject, dbContent);
			}

		} catch (Exception ex) {
			Util.logger.error("@Error at class sub mohandles: "
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
		}
		Util.logger.info("End  Handle MO with user " + msgObject.getUserid());
		return null;
	}

	private static int insertMoQueue(String tableName, MsgObject msg,
			String pool) {

		DBPool dbpool = new DBPool();
		Connection cnn = null;
		int result = 1;
		String sqlInsert = "INSERT INTO `mo_queue`"
				+ "(`USER_ID`,`SERVICE_ID`,`MOBILE_OPERATOR`,`COMMAND_CODE`,"
				+ "`INFO`,`RESPONDED`,`REQUEST_ID`,`CHANNEL_TYPE`)"
				+ "VALUES('" + msg.getUserid() + "','" + msg.getServiceid()
				+ "'," + "'VMS','" + msg.getCommandCode() + "','"
				+ msg.getUsertext() + "'," + "'0','" + msg.getRequestid()
				+ "','" + msg.getChannelType() + "')";

		Util.logger.info("insertMoQueue @sql:" + sqlInsert);
		try {
			cnn = dbpool.getConnection(pool);

			if (DBUtil.executeSQL(cnn, sqlInsert) < 0) {
				Util.logger.error("insertMoQueueDB "
						+ ": uppdate Statement: Insert mo_queue Failed:"
						+ sqlInsert);
				result = -1;
			}

		} catch (Exception ex) {
			Util.logger.error("Error at @InsertCustomer :" + ex.getMessage());
			result = -1;
		} finally {

			dbpool.cleanup(cnn);
		}
		return result;
	}

}
