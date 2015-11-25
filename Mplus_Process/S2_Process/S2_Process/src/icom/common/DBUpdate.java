package icom.common;

import icom.DBPool;

import java.sql.Connection;

/**
 * 
 * @author DanND ICom
 * @Date_Created 2011-04-19
 *
 */

public class DBUpdate {

	public int updateLastCodeMlist(String mlistTable, String commandCode,
			String userId, String lastcode) {

		int iReturn = -1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "update "
				+ mlistTable
				+ " set last_code = '"
				+ lastcode
				+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 "
				+ " where user_id = '" + userId
				+ "' and upper(command_code) like '" + commandCode + "%'";
		
		Util.logger.info("updateLastCodeMlist :: SQL = " + sqlUpdate);
		
		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("DBUpdate updateLastCodeMlist@"
						+ ": uppdate Statement: UPDATE  " + mlistTable
						+ " Failed## SQL = " + sqlUpdate);
			}else iReturn = 1;
			
		} catch (Exception ex) {
			Util.logger.error("DBUpdate updateLastCodeMlist@@: UPDATE  " + mlistTable
					+ " Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int updateStatusMT(int id, int status, String tableName){

		int ireturn = 1;
			
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableName + " SET STATUS_MT = "
				+ status + " WHERE ID = " + id;
		
		
		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateListSendStatus@"
						+ ": uppdate Statement: UPDATE  "
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateMlistDateRetry@: UPDATE  "
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return ireturn;

	}
}
