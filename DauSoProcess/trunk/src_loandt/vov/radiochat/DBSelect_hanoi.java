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

public class DBSelect_hanoi {
	
	/****
	 * check cap user_id va nick 
	 * 
	 */
	public boolean checkUserNick(String userID, String nick){
		
		boolean result = false;		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT * FROM vovhn_chat_users where user_id = '" + userID + "' and  nick = '" + nick + "' ";
		
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
						.error("dbManager - search UserID, nick  CT HN : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("dbManager - search UserID, nick  CT HN. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("dbManager - search UserID, nick  CT HN. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return result;
	}
	
	/****
	 * check cap user_id 
	 * 
	 */
	public boolean checkUser(String userID){
		
		boolean result = false;		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT * FROM vovhn_chat_users where user_id = '" + userID + "' ";
		
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
						.error("dbManager - search UserID  CT HN : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("dbManager - search UserID  CT HN. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("dbManager - search UserID  CT HN. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return result;
	}
	
public boolean checkNick(String nick){
		
		boolean result = false;		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT * FROM vovhn_chat_users where nick = '" + nick + "' ";
		
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
						.error("dbManager - search id  CT HN : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("dbManager - search id  CT HN. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("dbManager - search id  CT HN. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return result;
	}

// check thoi gian phat song chuong trinh

public boolean checkTime(String revice_date){
	
	boolean result = false;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT * FROM vovhn_chat_time where status = 1 and start_time <= '" + revice_date + "' and  end_time >= '" + revice_date + "' ";
	Util.logger.info("sqlQuery++ " + sqlQuery);
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
					.error("dbManager - check time active  CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - check time active  CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - check time active  CT HN. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public int getSession(String revice_date){
	
	int result = 0;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT session_id FROM vovhn_chat_time where status = 1 and start_time <= '" + revice_date + "' and  end_time >= '" + revice_date + "' ";
	
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

public int getSession(){
	
	int result = 0;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT session_id FROM vovhn_chat_time order by session_id desc limit 1 ";
	
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
public boolean checkVovhn_chat_couple(String userID){
	
	boolean result = false;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT * FROM vovhn_chat_couple where user_id = '" + userID + "' ";
	
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
					.error("dbManager - search id  CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search id  CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search id  CT HN. SQLException:"
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

	String sqlQuery = "SELECT * FROM vovhn_chat_time where status =1 ";
	
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
				result = rs.getString("question1")+ ";";
				result = result + rs.getString("question2") + ";";
				result = result + rs.getString("question3") + ";";
				result = result + rs.getString("question4") + ";";
				result = result + rs.getString("question5") + ";";
				result = result + rs.getString("question6") + ";";								
				result = result + rs.getString("end_timequestion6");
				
			}
		} else {
			Util.logger
					.error("dbManager - search time for question1  CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search search time for question1   CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search search time for question1   CT HN. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}


public String player_boy(){
	
	String result = null;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT player_boy FROM vovhn_chat_answer_couple where action = 1";
	
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
				result = rs.getString("player_boy");
			}
		} else {
			Util.logger
					.error("dbManager - search player_boy CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search search player_boy  CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search search player_boy  CT HN. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}


public String player_girl(){
	
	String result = null;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT player_girl FROM vovhn_chat_answer_couple where action = 1";
	
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
				result = rs.getString("player_girl");
			}
		} else {
			Util.logger
					.error("dbManager - search player_girl CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search search player_girl  CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search search player_girl  CT HN. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public String getNick(String UserID){
	
	String result = null;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT nick FROM vovhn_chat_users where user_id = '" + UserID +"' ";
	
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
				result = rs.getString("nick");
			}
		} else {
			Util.logger
					.error("dbManager - search id of userID  CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search id of userID CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search id of userID  CT HN. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public String getUserID(String nick){
	
	String result = null;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT user_id FROM vovhn_chat_users where nick = '" + nick +"' ";
	
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
				result = rs.getString("user_id");
			}
		} else {
			Util.logger
					.error("dbManager - search user_id of userID  CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search user_id of userID CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search user_id of userID  CT HN. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}

public int getID(String UserID){
	
	int result = 0;		
	Connection connection = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	DBPool dbpool = new DBPool();

	String sqlQuery = "SELECT id FROM tbl_love_social where user_id = '" + UserID +"'  and channel_type =1";
	
	try {
		if (connection == null) {
			connection = dbpool.getConnection("asterisk");
		}
		stmt = connection.prepareStatement(sqlQuery,
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);

		if (stmt.execute()) {
			rs = stmt.getResultSet();
			while (rs.next()) {
				result = rs.getInt("id");
			}
		} else {
			Util.logger
					.error("dbManager - search id of userID  CT HN : execute Error!!");
		}
	} catch (SQLException ex3) {
		Util.logger
				.error("dbManager - search id of userID CT HN. SQLException:"
						+ ex3.toString());
		Util.logger.printStackTrace(ex3);
	} catch (Exception ex2) {
		Util.logger
				.error("dbManager - search id of userID  CT HN. SQLException:"
						+ ex2.toString());
		Util.logger.printStackTrace(ex2);
	} finally {
		dbpool.cleanup(rs, stmt);
		dbpool.cleanup(connection);
	}
	
	return result;
}



}


