package vov.radiochat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;


/***
 * @Discription All Insert Query Puts in This Class.
 * @author LoanDT ICOM
 *
 */

public class DBInsert_Hanoi {

	/******
	 * 
	 * @param moObject
	 * @return 1: if Insert success <br/>
	 * 		   -1: if insert fail
	 */
	
	public int insertToVovhn_chat_users(String userID, String nick, int sex, String habit) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		
		MsgObject msgObject = new MsgObject();
		DBPool dbpool = new DBPool();

		Util.logger.info("VOV HN Chat-@@-Insert \tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_Info:" + msgObject.getUsertext()
				+ "\t RequestID:" + msgObject.getRequestid().toString()				
				+ "\tcommand_code:" + msgObject.getKeyword());		
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				iReturn = -1;
			}

			sqlString = "INSERT INTO vovhn_chat_users"				
					+ "( user_id, nick,sex, habit) "
					+ "VALUES (?,?,?,?)";			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userID);
			statement.setString(2, nick);
			statement.setInt(3, sex);
			statement.setString(4, habit);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HN Chat-@@-Insert : Error@userid="
								+ userID
								+ "\tTO"
								+ nick );
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Insert : Error@userid="
					+ userID
					+ "\tTO"
					+ nick 
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Insert : Error@userid="
					+ userID
					+ "\tTO"
					+ nick );
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	
public void UpdateToVovhn_chat_users( String userID, String nick,int sex, String habit) {
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
		MsgObject msgObject = new MsgObject();

		Util.logger.info("VOV HN Chat-@@-Update \tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_Info:" + msgObject.getUsertext()
				+ "\t RequestID:" + msgObject.getRequestid().toString()				
				+ "\tcommand_code:" + msgObject.getKeyword());		
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ userID
								+ "\t nick"
								+ nick);			
			}

			sqlString = "Update vovhn_chat_users"				
					+ " set nick = ?, habit =? where  user_id = ?";								
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, nick);			
			statement.setString(2, habit);
			statement.setString(3, userID);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HN Chat-@@-Update : Error@userid="
								+ userID
								+ "\t nick"
								+ nick);
				}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Update : Error@userid="
					+ userID
					+ "\t nick"
					+ nick
					+ "; Error = " + e.getMessage());		
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Update : Error@userid="
					+ userID
					+ "\t nick"
					+ nick);	
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}		
	}

	
public void UpdateToVovhn_chat_users( String userID, String agreeg_date) {
	
	Connection connection = null;
	PreparedStatement statement = null;
	String sqlString = null;

	DBPool dbpool = new DBPool();
	MsgObject msgObject = new MsgObject();

	Util.logger.info("VOV HN Chat-@@-Update " +
			"\tuser_id:"
			+ msgObject.getUserid()
			+ "\tservice_id:"
			+ msgObject.getServiceid()
			+ "\tuser_Info:"
			+ msgObject.getUsertext()
			+ "\t RequestID:"
			+ msgObject.getRequestid().toString()				
			+ "\tcommand_code:"
			+ msgObject.getKeyword()
			+ "\tagreeg_date:"
			+ agreeg_date);		
	try {
		connection = dbpool.getConnectionGateway();
		if (connection == null) {
			
			Util.logger
					.crisis("ContentAbstract@sendMT: Error connection == null"
							+ userID)
							;			
		}

		sqlString = "Update vovhn_chat_users"				
				+ " set private = ?,agreeg_date =? where  user_id = ?";								
		
		statement = connection.prepareStatement(sqlString);
		statement.setInt(1, 1);
		statement.setString(2, agreeg_date);
		statement.setString(3, userID);
	
		if (statement.executeUpdate() != 1) {
			Util.logger
					.crisis("VOV HN Chat-@@-Update : Error@userid="
							+ userID
							);
			}

	} catch (SQLException e) {
		Util.logger
		.crisis("VOV HN Chat-@@-Update : Error@userid="
				+ userID				
				+ "; Error = " + e.getMessage());		
	} catch (Exception e) {
		Util.logger
		.crisis("VOV HN Chat-@@-Update : Error@userid="
				+ userID
				);	
	} finally {
		dbpool.cleanup(statement);
		dbpool.cleanup(connection);
	}		
}


public int insert_tbl_love_social(String userID, String nick, int sex, String habit) {

	int iReturn = 1;
	Connection connection = null;
	PreparedStatement statement = null;
	String sqlString = null;
	
	MsgObject msgObject = new MsgObject();
	DBPool dbpool = new DBPool();

	Util.logger.info("VOV HN Chat-@@-Insert \tuser_id:"
			+ msgObject.getUserid() + "\tservice_id:"
			+ msgObject.getServiceid() + "\tuser_Info:" + msgObject.getUsertext()
			+ "\t RequestID:" + msgObject.getRequestid().toString()				
			+ "\tcommand_code:" + msgObject.getKeyword());		
	try {
		connection = dbpool.getConnection("asterisk");
		if (connection == null) {
			
			Util.logger
					.crisis("ContentAbstract@sendMT: Error connection == null"
							+ msgObject.getUserid()
							+ "\tTO"
							+ msgObject.getServiceid()
							+ "\t"
							+ msgObject.getUsertext()
							+ "\trequest_id:"
							+ msgObject.getRequestid().toString());
			iReturn = -1;
		}

		sqlString = "INSERT INTO tbl_love_social"				
				+ "( user_id, nickname,sex, habit,channel_type) "
				+ "VALUES (?,?,?,?,?)";			
		
		statement = connection.prepareStatement(sqlString);
		statement.setString(1, userID);
		statement.setString(2, nick);
		statement.setInt(3, sex);
		statement.setString(4, habit);
		statement.setInt(5, 1);
	
		if (statement.executeUpdate() != 1) {
			Util.logger
					.crisis("VOV HN Chat-@@-Insert : Error@userid="
							+ userID
							+ "\tTO"
							+ nick );
			iReturn = -1;
		}

	} catch (SQLException e) {
		Util.logger
		.crisis("VOV HN Chat-@@-Insert : Error@userid="
				+ userID
				+ "\tTO"
				+ nick 
				+ "; Error = " + e.getMessage());
		iReturn = -1;
	} catch (Exception e) {
		Util.logger
		.crisis("VOV HN Chat-@@-Insert : Error@userid="
				+ userID
				+ "\tTO"
				+ nick );
		iReturn = -1;
	} finally {
		dbpool.cleanup(statement);
		dbpool.cleanup(connection);
	}

	return iReturn;
}

public void update_tbl_love_social(String userID,String agreeg_date) {
	
	Connection connection = null;
	PreparedStatement statement = null;
	String sqlString = null;

	DBPool dbpool = new DBPool();
	MsgObject msgObject = new MsgObject();

	Util.logger.info("VOV HN Chat-@@-Update " +
			"\tuser_id:"
			+ msgObject.getUserid()
			+ "\tservice_id:"
			+ msgObject.getServiceid()
			+ "\tuser_Info:"
			+ msgObject.getUsertext()
			+ "\t RequestID:"
			+ msgObject.getRequestid().toString()				
			+ "\tcommand_code:"
			+ msgObject.getKeyword()
			+ "\tagreeg_date:"
			+ agreeg_date);		
	try {
		connection =  dbpool.getConnection("asterisk");
		if (connection == null) {
			
			Util.logger
					.crisis("ContentAbstract@sendMT: Error connection == null"
							+ userID)
							;			
		}

		sqlString = "Update tbl_love_social"				
				+ " set agreeg_date =? where  user_id = ?";								
		
		statement = connection.prepareStatement(sqlString);
		statement.setString(1, agreeg_date);
		statement.setString(2, userID);
	
		if (statement.executeUpdate() != 1) {
			Util.logger
					.crisis("VOV HN Chat-@@-Update : Error@userid="
							+ userID
							);
			}

	} catch (SQLException e) {
		Util.logger
		.crisis("VOV HN Chat-@@-Update : Error@userid="
				+ userID				
				+ "; Error = " + e.getMessage());		
	} catch (Exception e) {
		Util.logger
		.crisis("VOV HN Chat-@@-Update : Error@userid="
				+ userID
				);	
	} finally {
		dbpool.cleanup(statement);
		dbpool.cleanup(connection);
	}		
}

public void update_tbl_love_social(String userID,String nick,int sex, String habit) {
	
	Connection connection = null;
	PreparedStatement statement = null;
	String sqlString = null;

	DBPool dbpool = new DBPool();
	MsgObject msgObject = new MsgObject();

	Util.logger.info("VOV HN Chat-@@-Update " +
			"\tuser_id:"
			+ msgObject.getUserid()
			+ "\tservice_id:"
			+ msgObject.getServiceid()
			+ "\tuser_Info:"
			+ msgObject.getUsertext()
			+ "\t RequestID:"
			+ msgObject.getRequestid().toString()				
			+ "\tcommand_code:"
			+ msgObject.getKeyword()
			+ "\tsex:"
			+ sex);		
	try {
		connection =  dbpool.getConnection("asterisk");
		if (connection == null) {
			
			Util.logger
					.crisis("ContentAbstract@sendMT: Error connection == null"
							+ userID)
							;			
		}

		sqlString = "Update tbl_love_social"				
				+ " set nickname =?,sex =?,habit =? where  user_id = ?";								
		
		statement = connection.prepareStatement(sqlString);
		statement.setString(1, nick);
		statement.setInt(2, sex);
		statement.setString(3, habit);
		statement.setString(4, userID);
	
		if (statement.executeUpdate() != 1) {
			Util.logger
					.crisis("VOV HN Chat-@@-Update : Error@userid="
							+ userID
							);
			}

	} catch (SQLException e) {
		Util.logger
		.crisis("VOV HN Chat-@@-Update : Error@userid="
				+ userID				
				+ "; Error = " + e.getMessage());		
	} catch (Exception e) {
		Util.logger
		.crisis("VOV HN Chat-@@-Update : Error@userid="
				+ userID
				);	
	} finally {
		dbpool.cleanup(statement);
		dbpool.cleanup(connection);
	}		
}




	public int insertToVovhn_chat_couple(String userID, String nick, String date, String content,int sess) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
	
		DBPool dbpool = new DBPool();

		Util.logger.info("Vovhn_chat_couple-@@-Insert" +
				" \tuser_id:" + userID +
				"\t Nick:" + nick +
				"\tuser_Info:" + content +
				"\tSessiom:" + sess
				+ "\t Date:" + date );		
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null" +
								" \tuser_id:" + userID +
								"\t Nick:" + nick +
								"\tuser_Info:" + content +
								"\tSessiom:" + sess
								+ "\t Date:" + date );	
				iReturn = -1;
			}

			sqlString = "INSERT INTO vovhn_chat_couple"				
					+ "(user_id,nick,receiver,content, session_id) "
					+ "VALUES (?,?,?,?,?)";			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userID);
			statement.setString(2, nick);
			statement.setString(3, date);
			statement.setString(4, content);
			statement.setInt(5, sess);
			
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("Vovhn_chat_couple-@@-Insert" +
								" \tuser_id:" + userID +
								"\t Nick:" + nick +
								"\tuser_Info:" + content +
								"\tSessiom:" + sess
								+ "\t Date:" + date );
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("Vovhn_chat_couple-@@-Insert" +
					" \tuser_id:" + userID +
					"\t Nick:" + nick +
					"\tuser_Info:" + content +
					"\tSessiom:" + sess
					+ "\t Date:" + date 
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("Vovhn_chat_couple-@@-Insert" +
					" \tuser_id:" + userID +
					"\t Nick:" + nick +
					"\tuser_Info:" + content +
					"\tSessiom:" + sess
					+ "\t Date:" + date );
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public void UpdateToVovhn_chat_answer_couple(String file_player,String player, String fiel_answer,String answer, int session) {
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
				
			
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
			}

			sqlString = "Update vovhn_chat_answer_couple"				
					+ " set " + fiel_answer +" = ? where  session_id = ? and " + file_player +" =? and action =1 ";								
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, answer);
			statement.setInt(2, session);
			statement.setString(3, player);
			Util.logger.info("sqlString++" + sqlString);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HN Chat-@@-Update : Error@file_player="
								+ file_player
								+ "\t player"
								+ player
								+ "\t fiel_answer"
								+ fiel_answer
								+ "\t session"
								+ session);			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Update :Error@file_player="
								+ file_player
								+ "\t player"
								+ player
								+ "\t fiel_answer"
								+ fiel_answer
								+ "\t session"
								+ session);					
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Update :Error@file_player="
								+ file_player
								+ "\t player"
								+ player
								+ "\t fiel_answer"
								+ fiel_answer
								+ "\t session"
								+ session);		
		
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}		
	}

	public String insertSms(String userid, String serviceid, String operator ,String command_code, String content ) throws Exception {
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String tablename = "ems_send_queue";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR,COMMAND_CODE, INFO,MESSAGE_TYPE,CPID,CONTENT_TYPE)"
				+ " values(?,?,?,?,?,0,26,0)";
		try {			

			//Insert vao bang ems_send_queue	
				connection = dbpool.getConnectionGateway();				
				statement = connection.prepareStatement(sSQLInsert);
				statement.setString(1, userid);
				statement.setString(2,serviceid);
				statement.setString(3, operator);
				statement.setString(4, command_code);
				statement.setString(5, content);
				statement.executeUpdate();
				Util.logger.info(" SQL isnert:" + sSQLInsert);
			return null;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
			
		}
	}



	/****
	 * Insert into mo_queue with invalid MO is fixed correct content!
	 * 
	 * @return 1: insert successful <br/>
	 * 			-1: insert failure
	 */
}
