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

public class vovhcmFunwithMusic_DBInsert {

	/******
	 * 
	 * @param moObject
	 * @return 1: if Insert success <br/>
	 * 		   -1: if insert fail
	 */
	
	public int insertvovhcmfunwithmusic_answer(String userID, int session) {

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

			sqlString = "INSERT INTO vovhcmfunwithmusic_answer"				
					+ "( user_id, session_id) "
					+ "VALUES (?,?)";			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userID);			
			statement.setInt(2, session);
			
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HN Chat-@@-Insert : Error@userid="
								+ userID
								+ "\t Sesion"
								+ session );
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Insert : Error@userid="
					+ userID
					+ "\tsession"
					+ session 
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Insert : Error@userid="
					+ userID
					+ "\tsession"
					+ session );
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public void UpdateTovovhcmfunwithmusic_answer(String User_ID,String stage,String answer, int session) {
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
				
			
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
			}

			sqlString = "Update vovhcmfunwithmusic_answer"				
					+ " set " + stage +" = ? where  session_id = ? and User_ID =?";								
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, answer);
			statement.setInt(2, session);
			statement.setString(3, User_ID);
			Util.logger.info("sqlString++" + sqlString);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV Fun with Music-@@-Update : Error@User_ID="
								+ User_ID
								+ "\t stage"
								+ stage
								+ "\t answer"
								+ answer
								+ "\t session"
								+ session);			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV Fun with Music-@@-Update : Error@User_ID="
					+ User_ID
					+ "\t stage"
					+ stage
					+ "\t answer"
					+ answer
					+ "\t session"
					+ session);					
		} catch (Exception e) {
			Util.logger
			.crisis("VOV Fun with Music-@@-Update : Error@User_ID="
					+ User_ID
					+ "\t stage"
					+ stage
					+ "\t answer"
					+ answer
					+ "\t session"
					+ session);		
		
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
