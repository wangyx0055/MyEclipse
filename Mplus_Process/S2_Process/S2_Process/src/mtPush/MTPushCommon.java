package mtPush;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MTPushCommon {

	/***
	 * 
	 * @param tblName
	 * @return ID of record
	 * 
	 */
	public int isExistInMTPush(String tableName, String userId) {
		int id = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, USER_ID, status, COMMAND_CODE FROM "
				+ tableName + " WHERE USER_ID = '" + userId + "'";

		try {
			if (connection == null) {
				connection = dbpool.getConnection(PushMTConstants.pushMTPool);
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					id = rs.getInt("ID");
					Util.logger.info("TC QUANG CAO: USER_ID = " + userId + " is in mtPush table: + " + tableName);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("TCVanSu - isExistInMTPush SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("TCVanSu - isExistInMTPush SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		return id;
	}

}
