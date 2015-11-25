package services;


import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import mtPush.PushMTConstants;


public class TCVanSu extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {
		
		int numberTable = PushMTConstants.getNumberMTPushTable() + 1;
		String tableMTpush = PushMTConstants.TABLE_MT_PUSH;
		int result = -1;		
		for(int i = 0;i<numberTable;i++){
			int id = isExistInMTPush(tableMTpush, msgObject.getUserid());
			if(id>0){
				result = deleteById(id, tableMTpush);
				break;
			}else{
				if(i>0){
					tableMTpush = PushMTConstants.TABLE_MT_PUSH + i;
				}else{
					tableMTpush = PushMTConstants.TABLE_MT_PUSH + "1";
				}
			}
		}
		
		if(result == 1){
			msgObject.setUsertext(keyword.getSubMsg());
		}else{
			msgObject.setUsertext(keyword.getUnsubMsg());
		}
		
		ArrayList<MsgObject> arrMsg = new ArrayList<MsgObject>();
		
		arrMsg.add(msgObject);
		return arrMsg;
	}
	
	/***
	 * 
	 * @param tblName
	 * @return ID of record
	 */
	public int isExistInMTPush(String tableName, String userId){
		int id = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, USER_ID, status, COMMAND_CODE FROM " + tableName
		+ " WHERE USER_ID = '" + userId + "'"; 
		
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
	
	public int deleteById(int id, String tableName){
		int ireturn = 1;
		
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "DELETE FROM " + tableName + " WHERE ID = " + id;

		Util.logger.info("TCVanSu @@@ deleteById: SQL = " + sqlUpdate);

		try {

			connection = dbpool.getConnection(PushMTConstants.pushMTPool);

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("TCVanSu @@@ deleteById: table Name =  " + tableName
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return ireturn;
	}
}
