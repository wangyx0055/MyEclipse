package vov.radiochat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


import com.vmg.sms.common.Util;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;


/***
 * @Discription All Insert Query Puts in This Class.
 * @author LoanDT ICOM
 *
 */

public class DBInsert {

	/******
	 * 
	 * @param moObject
	 * @return 1: if Insert success <br/>
	 * 		   -1: if insert fail
	 */
	
	public int insertToVovhcm_chat_users(String userID, String nick) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		
		MsgObject msgObject = new MsgObject();
		DBPool dbpool = new DBPool();

		Util.logger.info("VOV HCM Chat-@@-Insert \tuser_id:"
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

			sqlString = "INSERT INTO vovhcm_chat_users"				
					+ "( user_id, nick) "
					+ "VALUES (?, ?)";			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userID);
			statement.setString(2, nick);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HCM Chat-@@-Insert : Error@userid="
								+ userID
								+ "\tTO"
								+ nick );
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Insert : Error@userid="
					+ userID
					+ "\tTO"
					+ nick 
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Insert : Error@userid="
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
	
	
	public void UpdateToVovhcm_chat_users( String userID,int sex, String habit) {
	
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
		MsgObject msgObject = new MsgObject();

		Util.logger.info("VOV HCM Chat-@@-Update \tuser_id:"
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
								+ "\t Sex"
								+ sex
								+ "\t Habit"
								+ habit	);			
			}

			sqlString = "Update vovhcm_chat_users"				
					+ " set sex = ?, habit =?  where  user_id = ?";								
			
			statement = connection.prepareStatement(sqlString);
			statement.setInt(1, sex);
			statement.setString(2, habit);
			statement.setString(3, userID);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HCM Chat-@@-Update : Error@userid="
								+ userID
								+ "\t Sex"
								+ sex
								+ "\t Habit"
								+ habit	);			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Update : Error@userid="
					+ userID
					+ "\t Sex"
					+ sex
					+ "\t Habit"
					+ habit
					+ "; Error = " + e.getMessage());		
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Update : Error@userid="
					+ userID
					+ "\t Sex"
					+ sex
					+ "\t Habit"
					+ habit	);
		
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}		
	}
	
	public void UpdateToVovhcm_chat_users( String userID, String nick) {
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
		MsgObject msgObject = new MsgObject();

		Util.logger.info("VOV HCM Chat-@@-Update \tuser_id:"
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

			sqlString = "Update vovhcm_chat_users"				
					+ " set nick = ? where  user_id = ?";								
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, nick);
			statement.setString(2, userID);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HCM Chat-@@-Update : Error@userid="
								+ userID
								+ "\t nick"
								+ nick);
				}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Update : Error@userid="
					+ userID
					+ "\t nick"
					+ nick
					+ "; Error = " + e.getMessage());		
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Update : Error@userid="
					+ userID
					+ "\t nick"
					+ nick);	
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}		
	}

	
	public int insertToVovhcm_chat_couple(String userID, String nick, String date, String content,int sess) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
	
		DBPool dbpool = new DBPool();

		Util.logger.info("Vovhcm_chat_couple-@@-Insert" +
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

			sqlString = "INSERT INTO vovhcm_chat_couple"				
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
						.crisis("Vovhcm_chat_couple-@@-Insert" +
								" \tuser_id:" + userID +
								"\t Nick:" + nick +
								"\tuser_Info:" + content +
								"\tSessiom:" + sess
								+ "\t Date:" + date );
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("Vovhcm_chat_couple-@@-Insert" +
					" \tuser_id:" + userID +
					"\t Nick:" + nick +
					"\tuser_Info:" + content +
					"\tSessiom:" + sess
					+ "\t Date:" + date 
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("Vovhcm_chat_couple-@@-Insert" +
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
	
	public int insertToVovhcm_chat_single(String userID, String nick, String content, int sess) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
	
		DBPool dbpool = new DBPool();

		Util.logger.info("VOV HCM Chat-@@-Insert vovhcm_chat_single \tuser_id:"
				+ userID + "\t Nick:"
				+ nick + "\tuser_Info:" + content				
				+ "\t Session:" + sess);		
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ userID + "\t Nick:"
								+ nick + "\tuser_Info:" + content							
								+ "\t Session:" + sess);	
				iReturn = -1;
			}

			sqlString = "INSERT INTO vovhcm_chat_single"				
					+ "(user_id,nick,content,session_id) "
					+ "VALUES (?,?,?,?)";			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userID);
			statement.setString(2, nick);
			statement.setString(3, content);			
			statement.setInt(4, sess);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HCM Chat-@@-Insert : Error@userid="
								+ userID + "\t Nick:"
								+ nick + "\tuser_Info:" + content								
								+ "\t Session:" + sess);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Insert : Error@userid="
					+ userID + "\t Nick:"
					+ nick + "\tuser_Info:" + content					
					+ "\t Session:" + sess
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Insert : Error@userid="
					+ userID + "\t Nick:"
					+ nick + "\tuser_Info:" + content				
					+ "\t Session:" + sess);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	
	public int insertToVovhcm_chat_chooseplayer(String userID, String nick,int player, String receive_date, int sess) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
	
		DBPool dbpool = new DBPool();

		Util.logger.info("VOV HCM Chat-@@-Insert vovhcm_chat_chooseplayer \tuser_id:"
				+ userID + "\t Nick:"
				+ nick + "\t player:" + player				
				+ "\t Session:" + sess);		
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ userID + "\t Nick:"
								+ nick + "\t player:" + player				
								+ "\t Session:" + sess);	
				iReturn = -1;
			}

			sqlString = "INSERT INTO vovhcm_chat_chooseplayer"				
					+ "(user_id,nick,player,receive_date,session_id) "
					+ "VALUES (?,?,?,?,?)";			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userID);
			statement.setString(2, nick);
			statement.setInt(3, player);
			statement.setString(4, receive_date);			
			statement.setInt(5, sess);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HCM Chat-@@-Insert chooseplayer: Error@userid="
								+ userID + "\t Nick:"
								+ nick + "\t player:" + player				
								+ "\t Session:" + sess);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Insert chooseplayer : Error@userid="
					+ userID + "\t Nick:"
					+ nick + "\t player:" + player				
					+ "\t Session:" + sess
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Insert chooseplayer : Error@userid="
					+ userID + "\t Nick:"
					+ nick + "\t player:" + player				
					+ "\t Session:" + sess);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	

	public int insertToVovhcm_chat_giftsong(String userID, String phone, String songname, String content,int sess) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
	
		DBPool dbpool = new DBPool();

		Util.logger.info("Vovhcm_chat_giftsong-@@-Insert" +
				" \tuser_id:" + userID +
				"\t phoneGet:" + phone +
				"\tuser_Info:" + content +
				"\tSessiom:" + sess
				+ "\t songname:" + songname );		
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null" +
								" \tuser_id:" + userID +
								"\t phoneGet:" + phone +
								"\tuser_Info:" + content +
								"\tSessiom:" + sess
								+ "\t songname:" + songname );	
				iReturn = -1;
			}

			sqlString = "INSERT INTO vovhcm_chat_giftsong"				
					+ "(user_id,phoneget,songname,content,session_id) "
					+ "VALUES (?,?,?,?,?)";			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userID);
			statement.setString(2, phone);
			statement.setString(3, songname);
			statement.setString(4, content);
			statement.setInt(5, sess);
			
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("Vovhcm_chat_giftsong-@@-Insert" +
								" \tuser_id:" + userID +
								"\t phoneGet:" + phone +
								"\tuser_Info:" + content +
								"\tSessiom:" + sess
								+ "\t songname:" + songname );
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("Vovhcm_chat_giftsong-@@-Insert" +
					" \tuser_id:" + userID +
					"\t phoneGet:" + phone +
					"\tuser_Info:" + content +
					"\tSessiom:" + sess
					+ "\t songname:" + songname  
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("Vovhcm_chat_giftsong-@@-Insert" +
					" \tuser_id:" + userID +
					"\t phoneGet:" + phone +
					"\tuser_Info:" + content +
					"\tSessiom:" + sess
					+ "\t songname:" + songname );
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}

	
	public int insertToVovhcm_chat_voting(String userID, int couple, String date, int sess) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
	
		DBPool dbpool = new DBPool();

		Util.logger.info("Vovhcm_chat_votting-@@-Insert" +
				" \tuser_id:" + userID +
				"\t couple:" + couple +
				"\tdate:" + date +
				"\tSessiom:" + sess	);		
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null" +
								" \tuser_id:" + userID +
								"\t couple:" + couple +
								"\tdate:" + date +
								"\tSessiom:" + sess	);	
				iReturn = -1;
			}

			sqlString = "INSERT INTO vovhcm_chat_voting"				
					+ "(user_id,couple,receive_date,session_id) "
					+ "VALUES (?,?,?,?)";			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userID);
			statement.setInt(2, couple);
			statement.setString(3, date);			
			statement.setInt(4, sess);
			
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("Vovhcm_chat_votting-@@-Insert" +
								" \tuser_id:" + userID +
								"\t couple:" + couple +
								"\tdate:" + date +
								"\tSessiom:" + sess	);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("Vovhcm_chat_votting-@@-Insert" +
					" \tuser_id:" + userID +
					"\t couple:" + couple +
					"\tdate:" + date +
					"\tSessiom:" + sess	  
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("Vovhcm_chat_votting-@@-Insert" +
					" \tuser_id:" + userID +
					"\t couple:" + couple +
					"\tdate:" + date +
					"\tSessiom:" + sess	);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public void UpdateToVovhcm_chat_answer_couple(String file_player,String player, String fiel_answer,String answer, int session) {
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
				
			
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
			}

			sqlString = "Update vovhcm_chat_answer_couple"				
					+ " set " + fiel_answer +" = ? where  session_id = ? and " + file_player +" =? and action =1 ";								
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, answer);
			statement.setInt(2, session);
			statement.setString(3, player);
			Util.logger.info("sqlString++" + sqlString);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HCM Chat-@@-Update : Error@file_player="
								+ file_player
								+ "\t player"
								+ player
								+ "\t fiel_answer"
								+ fiel_answer
								+ "\t session"
								+ session);			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Update :Error@file_player="
								+ file_player
								+ "\t player"
								+ player
								+ "\t fiel_answer"
								+ fiel_answer
								+ "\t session"
								+ session);					
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HCM Chat-@@-Update :Error@file_player="
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
