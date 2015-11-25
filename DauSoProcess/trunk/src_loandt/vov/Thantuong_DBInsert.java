package vov;

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

public class Thantuong_DBInsert {

	/******
	 * 
	 * @param moObject
	 * @return 1: if Insert success <br/>
	 * 		   -1: if insert fail
	 */
	
	public int saveCustomer(String session, String user_id, String mobile_operator,
			String guess_name, int exactly, int block, String request_id,
			String keyword) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;		
	
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				iReturn = -1;
			}

			sqlString = "INSERT INTO icom_thantuong_customer "
				+ "( session, user_id, mobile_operator, guess_name, exactly, block, request_id, keyword) VALUES ('"
				+ session + "','" + user_id + "','" + mobile_operator
				+ "','" + guess_name + "', " + exactly + "," + block + ",'"
				+ request_id + "','" + keyword + "')";			
			
			statement = connection.prepareStatement(sqlString);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("VOV HN Chat-@@-Insert : Error@userid="
								+ user_id
								+ "\t Sesion"
								+ session );
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Insert : Error@userid="
					+ user_id
					+ "\tsession"
					+ session 
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("VOV HN Chat-@@-Insert : Error@userid="
					+ user_id
					+ "\tsession"
					+ session );
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public void saveCustomerVote(String session, String user_id, String mobile_operator,
			String guess_name, int exactly, String request_id, String keyword, String block) {
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
				
			
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
			}

			sqlString = "INSERT INTO icom_thantuong_vote "			
				+ "( session, user_id, mobile_operator, guess, exactly, request_id, keyword, block) VALUES ('"
				+ session + "','" + user_id + "','" + mobile_operator
				+ "','" + guess_name + "'," + exactly + ",'" + request_id
				+ "','" + keyword + "','" + block + "')";				
			
			statement = connection.prepareStatement(sqlString);			
			Util.logger.info("sqlString++" + sqlString);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("Nhan dien than tuong : Error@User_ID="
								+ user_id
								+ "\t stage"
								+ keyword
								+ "\t answer"
								+ exactly
								+ "\t session"
								+ session);			}

		} catch (SQLException e) {
			Util.logger
			.crisis("Nhan dien than tuong : Error@User_ID="
					+ user_id
					+ "\t stage"
					+ keyword
					+ "\t answer"
					+ exactly
					+ "\t session"
					+ session);				
		} catch (Exception e) {
			Util.logger
			.crisis("Nhan dien than tuong : Error@User_ID="
					+ user_id
					+ "\t stage"
					+ keyword
					+ "\t answer"
					+ exactly
					+ "\t session"
					+ session);	
		
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}		
	}

public void UpdateToicom_thantuong_vote(int block) {
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
				
			
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
			}

			sqlString = "Update icom_thantuong_vote"				
					+ " set status = 1 where exactly =1 and block = " + block +"";								
			
			statement = connection.prepareStatement(sqlString);			
			Util.logger.info("sqlString++" + sqlString);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("Error ");			}

		} catch (SQLException e) {
			Util.logger
			.crisis("Error ");					
		} catch (Exception e) {
			Util.logger
			.crisis("Error ");	
		
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}		
	}

public void UpdateToicom_thantuongCustomer() {
	
	Connection connection = null;
	PreparedStatement statement = null;
	String sqlString = null;

	DBPool dbpool = new DBPool();
			
		
	try {
		connection = dbpool.getConnectionGateway();
		if (connection == null) {				
		}

		sqlString = "Update icom_thantuong_customer"				
				+ " set status = 1 where exactly =1";								
		
		statement = connection.prepareStatement(sqlString);			
		Util.logger.info("sqlString++" + sqlString);
	
		if (statement.executeUpdate() != 1) {
			Util.logger
					.crisis("Error ");			}

	} catch (SQLException e) {
		Util.logger
		.crisis("Error ");					
	} catch (Exception e) {
		Util.logger
		.crisis("Error ");	
	
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
