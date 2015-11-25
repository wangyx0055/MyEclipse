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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;


public class InvalidSMS extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop,MsgObject msgObject, Keyword keyword, Hashtable services)
			throws Exception {
		add2Invalid(msgObject);
		return null;
	}

	private static BigDecimal add2Invalid(MsgObject msgObject) {
		Util.logger.info("add2Invalid:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement preStmt = null;
		String strSQL = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_invalid_queue";
		strSQL = "INSERT INTO sms_receive_invalid_queue (ID, USER_ID, SERVICE_ID, "
				+ "MOBILE_OPERATOR, COMMAND_CODE, INFO, RECEIVE_TIME, RESPONDED,receive_id) "
				+ "VALUES (S_sms_receive_invalid_queue.nextval, ?, ?, ?, ?, ?, sysdate, ?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			preStmt = connection.prepareStatement(strSQL);
			preStmt.setString(1, msgObject.getUserid());
			preStmt.setString(2, msgObject.getServiceid());
			preStmt.setString(3, msgObject.getMobileoperator());
			preStmt.setString(4, msgObject.getKeyword());
			preStmt.setString(5, msgObject.getUsertext());
			// preStmt.setDate(6, date);
			preStmt.setInt(6, 0);
			preStmt.setBigDecimal(7, msgObject.getRequestid());
			if (preStmt.executeUpdate() != 1) {
				Util.logger.error("add2ReceiveLog:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			preStmt.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

}
