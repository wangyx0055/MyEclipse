package vov.radiochat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.vmg.sms.common.Util;
import com.vmg.sms.process.DBPool;


/******
 * All Select query put here! 
 * 
 *
 */

public class vovhnFunwithMusic_DBSelect {
	
	/****
	 * check cap user_id va nick 
	 * 
	 */
	
public int getSession(){
	
	int result = 0;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT session_id FROM vovhnfunwithmusic_time order by session_id desc limit 1 ";
	
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
				result = rs.getInt(1);				
			}
		} else {
			Util.logger
					.error("dbManager - get Session  CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - get Session  CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - get Session  CT HN. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public String time_question(){
	
	String result = null;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT * FROM vovhnfunwithmusic_time where status =1 ";
	Util.logger.info("sqlQuery___"+ sqlQuery);
	
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
				//Util.logger.info("Toi day");
				result = rs.getString("start_time")+ ";";
				result = result + rs.getString("stage1")+ ";";
				result = result + rs.getString("stage2") + ";";
				result = result + rs.getString("stage3") + ";";
				result = result + rs.getString("stage_add") + ";";				
				result = result + rs.getString("end_time");
				
			}
		} else {
			Util.logger
					.error("dbManager - search time for Fun with Music : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search search time for Fun with Music . SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search search time for Fun with Music . SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public boolean checkUS(String userID, int session){
	
	boolean result = false;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT * FROM vovhnfunwithmusic_answer where user_id = '" + userID + "' and session_id = " + session+" ";
	
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
				result = true;
			}
		} else {
			Util.logger
					.error("dbManager - search userID, sesion Fun with Music : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search userID, sesion Fun with Music. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search userID, sesion Fun with Music. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}
}


