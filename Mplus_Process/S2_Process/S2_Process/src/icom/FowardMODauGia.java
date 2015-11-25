package icom;

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

import icom.Keyword;
import icom.MsgObject;

import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

public class FowardMODauGia extends QuestionManager {

	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		try {
			HashMap _option = Util.getParametersAsString(keyword.getOptions());
			String dbContent = Util.getStringfromHashMap(_option, "dbcontent",
					"x").trim();
			if ("x".equalsIgnoreCase(dbContent)) {
				Util.logger
						.info("Khong xac dinh duoc dbcontent duoc get ra tu param options:"
								+ " "
								+ msgObject.getUserid()
								+ ","
								+ keyword.getOptions());
				return null;
			} else {
				msgObject.setCommandCode(keyword.getService_ss_id());
				// kiem tra ton tai trong tap mlist
				writeToQueue(msgObject, "mo_queue", dbContent);

			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName() + " error: "
					+ ex.getMessage());
		}
		return null;
	}

	public static void writeToQueue(MsgObject msgObject, String tableName,
			String dbContent) {

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbPool = new DBPool();

		sSQLInsert = "insert into "
				+ tableName
				+ "(USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,REQUEST_ID,CHANNEL_TYPE)"
				+ " values('" + msgObject.getUserid() + "','"
				+ msgObject.getServiceid() + "','"
				+ msgObject.getMobileoperator() + "','"
				+ msgObject.getKeyword() + "','" + msgObject.getUsertext()
				+ "','" + getCurrentDate() + "',0,'" + msgObject.getId() + "',"
				+ msgObject.getChannelType() + ")";

		try {

			connection = dbPool.getConnection(dbContent);
			statement = connection.prepareStatement(sSQLInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("@PoolName : " + dbContent + " add2:"
						+ tableName + "@" + msgObject.getUserid() + ":"
						+ msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
			} else
				Util.logger.info("@PoolName : " + dbContent + " ,Sqlcommand: "
						+ sSQLInsert);
			statement.close();
		} catch (SQLException e) {
			Util.logger.error("@PoolName : " + dbContent + " add2:" + tableName
					+ "@" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext() + ":Error add row from "
					+ tableName + ":" + e.toString());

		} catch (Exception e) {
			Util.logger.error("@PoolName : " + dbContent + " add2:" + tableName
					+ "@" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext() + ":Error add row from "
					+ tableName + ":" + e.toString());
		}

		finally {
			dbPool.cleanup(connection, statement);

		}
	}

	public static String getCurrentDate() {
		Calendar now = Calendar.getInstance();
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now
				.getTime());
	}
}
