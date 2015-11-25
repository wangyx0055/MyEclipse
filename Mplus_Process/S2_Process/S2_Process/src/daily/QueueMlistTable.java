package daily;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

public class QueueMlistTable {
	
	// String Key = ServiceName, String element = MLIST table name
	private static Hashtable<String, String> hMlist = new Hashtable<String, String>();
	private static QueueMlistTable queue = null;
	
	private QueueMlistTable(){}
	
	public static synchronized QueueMlistTable getInstance(){
		if(queue == null){
			queue = new QueueMlistTable();
		}
		
		return queue;
	}
	
	public synchronized String getMlistTableName(String serviceName){
		String tableName = "";
		
		tableName = hMlist.get(serviceName);
		if(tableName == null || tableName.trim().equals("")){
			tableName = getNewMlistTable(serviceName);
		}
		
		return tableName;
	}
	
	private String getNewMlistTable(String commandCode){
		
		String tableName = "";
		
		String option = getOptions(commandCode);
		
		HashMap _option = new HashMap();
		_option = Util.getParametersAsString(option);

		// Ten bang luu danh sach khach hang
		tableName = Util.getStringfromHashMap(_option, "mlist", "x");
		
		return tableName;
	}
	
	private String getOptions(String commandCode){
		String options = "";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "services";
		
		String sqlQuery = "SELECT services, options FROM " + tableName
				+ " WHERE services = '" + commandCode + "'";
		// System.out.println("==== DBSelect: getOptions - QUERRY = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {					
					options = rs.getString("options");
					break;
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("QueueMlistTable - getOptions SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("QueueMlistTable - getOptions SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		
		return options;
	}
	
}
