package daily;

import icom.DBPool;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class LinkQueueMng {
	
	// String1: Service Name; String2: UserId
	private static Hashtable<String, Hashtable<String, LinkInfo>> hServiceLink 
					= new Hashtable<String, Hashtable<String,LinkInfo>>();
	
	private static LinkQueueMng linkQueue = null;
	
	private LinkQueueMng(){
		
	}
	
	public static synchronized LinkQueueMng getInstance(){
		if(linkQueue == null){
			linkQueue = new LinkQueueMng();
		}
		return linkQueue;
	}
	
	public synchronized String getLink(String serviceName,String userId){
		
		String link = "";
		
		Hashtable<String, LinkInfo> hUserLink = hServiceLink.get(serviceName);
		
		if(hUserLink == null){
			hUserLink = getArrLinkInfo(serviceName);
			hServiceLink.put(serviceName, hUserLink);
		}
		
		if(hUserLink != null){
			LinkInfo objInfo = hUserLink.get(userId);
			if(objInfo != null) link = objInfo.getLink();
		}
		
		return link;
	}
	
	public synchronized void removeAllLink(String serviceName){
		hServiceLink.remove(serviceName);
		deleteLink(serviceName);
	}
	
	private Hashtable<String, LinkInfo> getArrLinkInfo(String serviceName){
		
		Hashtable<String, LinkInfo> hLink = new Hashtable<String, LinkInfo>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "SELECT id, user_id, command_code, link, gameid FROM linkgame_queue WHERE "
			+ " upper(Command_code)='"
			+ serviceName.toUpperCase() + "'";
		// System.out.println("getExistMessage QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					LinkInfo obj = new LinkInfo();
					obj.setId(rs.getInt("id"));
					obj.setUserId(rs.getString("user_id"));
					obj.setCommandCode(rs.getString("command_code"));
					obj.setLink(rs.getString("link"));
					obj.setGameId(rs.getInt("gameid"));
					
					hLink.put(obj.getUserId(), obj);
					obj = null;					
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("LinkQueueMng - getArrLinkInfo. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("LinkQueueMng - getArrLinkInfo. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		return hLink;
		
	}
	
	private boolean deleteLink(String serviceName) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM linkgame_queue  WHERE upper(command_code)='"
					+ serviceName.toUpperCase() + "'";
			
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("LinkQueueMng: Loi xoa link  trong bang link game queue");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error("LinkQueueMng: Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error("LinkQueueMng: Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
	
	public synchronized void updateAllLastCode(String mlistTable, String serviceName){
		
		Hashtable<String, LinkInfo> hUserLink = hServiceLink.get(serviceName);
		if(hUserLink == null) return;
		
		Enumeration<LinkInfo> e = hUserLink.elements();
		while(e.hasMoreElements()){
			
									
		}
		
	}
	
	public synchronized void updateLastCodeInList(String serviceName, String userId, String newLastCode){
		
		//Hashtable<String, LinkInfo> hUserLink = hServiceLink.get(serviceName);
		
	}
	
	private int updateLastCodeMlist(String mlistTable, String commandCode,
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
	
}
