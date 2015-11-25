package icom.common;

import icom.DBPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author DanND ICom
 * @Date_Created 2011-04-19
 *
 */
public class DBDelete {
	
	/**
	 * 
	 * @param id
	 * @param tableName
	 * @return 1: delete successful<br/>
	 * 		   -1: delete fail
	 */
	public int deleteTableByID(int id, String tableName){
		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				iReturn = -1;
			}

			sqlString = "DELETE FROM "
					+ tableName
					+ " WHERE ID = " + id;
			
			statement = connection.prepareStatement(sqlString);
			
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("DBDelete deleteTableByID Error@ID="
								+ id + ";; table name = " + tableName);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("DBDelete deleteTableByID: Error@ID="
					+ id + ";; table name = " + tableName);
			iReturn = -1;
		} catch (Exception e) {
			Util.logger.crisis("DBDelete deleteTableByID:: Error@ID="+ id 
					+ ";; table name = " + tableName);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int deleteByID(int id, String tableName, String poolName){
		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(poolName);
			if (connection == null) {
				
				iReturn = -1;
			}

			sqlString = "DELETE FROM "
					+ tableName
					+ " WHERE ID = " + id;
			
			Util.logger.info("deleteByID ### poolName = " + poolName + ";; SQL = " + sqlString);
			
			statement = connection.prepareStatement(sqlString);
			
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("DBDelete deleteTableByID Error@ID="
								+ id + ";; table name = " + tableName);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("DBDelete deleteTableByID: Error@ID="
					+ id + ";; table name = " + tableName);
			iReturn = -1;
		} catch (Exception e) {
			Util.logger.crisis("DBDelete deleteTableByID:: Error@ID="+ id 
					+ ";; table name = " + tableName);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
}
