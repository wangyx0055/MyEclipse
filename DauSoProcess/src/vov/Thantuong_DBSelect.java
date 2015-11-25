package vov;

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

public class Thantuong_DBSelect {
	
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

	String sqlQuery = "SELECT id FROM icom_thantuong_config order by id desc limit 1 ";
	
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
					.error("dbManager - get session than tuong : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - get session than tuong. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - get session than tuong. SQLException:"
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

	String sqlQuery = "SELECT * FROM icom_thantuong_config where active =1 order by id desc limit 1";
	//Util.logger.info("sqlQuery___"+ sqlQuery);
	
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
				result = result + rs.getString("block1")+ ";";
				result = result + rs.getString("block2") + ";";
				result = result + rs.getString("block3") + ";";		
				result = result + rs.getString("start_question1")+ ";";
				result = result + rs.getString("question2")+ ";";
				result = result + rs.getString("question3")+ ";";
				result = result + rs.getString("question4")+ ";";
				result = result + rs.getString("question5")+ ";";
				result = result + rs.getString("end_question")+ ";";
				result = result + rs.getString("answer1")+ ";";
				result = result + rs.getString("answer2")+ ";";
				result = result + rs.getString("answer3")+ ";";
				result = result + rs.getString("answer4")+ ";";
				result = result + rs.getString("answer5")+ ";";
				result = result + rs.getString("tenthantuong");
				
			}
		} else {
			Util.logger
					.error("dbManager - search time for idol : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search search time for idol . SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search search time for idol . SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public boolean checkUS(String userID){
	
	boolean result = false;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT * FROM icom_thantuong_customer where user_id = '" + userID + "'";
	
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
					.error("dbManager - search userID, sesion idol : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search userID, sesion idol. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search userID, sesion idol. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public boolean checkUser(String userID){
	
	boolean result = false;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT * FROM icom_thantuong_vote where user_id = '" + userID + "'";
	
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
					.error("dbManager - search userID, sesion idol : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search userID, sesion idol. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search userID, sesion idol. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public String customerTrue(){
	
	String result = "";		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT user_id FROM icom_thantuong_customer where exactly = 1 and status =0 and " +
	"session in (SELECT id FROM icom_thantuong_config where active =1 order by id desc) ";
	
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
				result = result + rs.getString("user_id") + ";";
			}
		} else {
			Util.logger
					.error("dbManager - search userID, sesion idol : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search userID, sesion idol. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search userID, sesion idol. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public String customervoteTrue(){
	
	String result = "";		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT user_id, block FROM icom_thantuong_vote where exactly = 1 and status =0  and session in (SELECT id FROM icom_thantuong_config where active =1 order by id desc) ";
	
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
				result = result + rs.getString("user_id") + "/";
				result = result + rs.getString("block") + ";";
			}
		} else {
			Util.logger
					.error("dbManager - search userID, sesion idol : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search userID, sesion idol. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search userID, sesion idol. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

}


