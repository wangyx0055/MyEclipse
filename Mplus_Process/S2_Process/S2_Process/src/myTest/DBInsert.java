package myTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBInsert {

	public void insertMTPushAD(int numberInsert) {

		// USER_ID, status, COMMAND_CODE
		String driverClass= "com.mysql.jdbc.Driver";
		String driverURL= "jdbc:mysql://localhost:3306/s2mplus";
		String user="root";
		String password= "root";

		String sqlInsert = "INSERT INTO mtpush_s2vms_qc(USER_ID, status, COMMAND_CODE)"
				+ " VALUES(?, ?, ?)";

		for (int i = 0; i < numberInsert; i++) {
			Connection connection = null;
			PreparedStatement statement = null;

			try {
				Class.forName(driverClass);				
				connection = DriverManager.getConnection(driverURL,user,password);
				
				if (connection == null) {
					return;
				}

				statement = connection.prepareStatement(sqlInsert);
				statement.setString(1, "0123456789");
				statement.setInt(2, 0);
				statement.setString(3, "LVS");

				//Util.logger.info("TEST Insert MTPush SQL Query" + sqlInsert);
				if (statement.executeUpdate() != 1) {
					return;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					statement.close();
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public void insertMTPush(int numberInsert) {

		// USER_ID, status, COMMAND_CODE
		String driverClass= "com.mysql.jdbc.Driver";
		String driverURL= "jdbc:mysql://localhost:3306/s2mplus";
		String user="root";
		String password= "root";

		String sqlInsert = "INSERT INTO mtpush_s2vms(USER_ID, status, COMMAND_CODE)"
				+ " VALUES(?, ?, ?)";

		for (int i = 0; i < numberInsert; i++) {
			Connection connection = null;
			PreparedStatement statement = null;

			try {
				Class.forName(driverClass);				
				connection = DriverManager.getConnection(driverURL,user,password);
				
				if (connection == null) {
					return;
				}

				statement = connection.prepareStatement(sqlInsert);
				statement.setString(1, "0123456789");
				statement.setInt(2, 0);
				statement.setString(3, "VANSU");

				//Util.logger.info("TEST Insert MTPush SQL Query" + sqlInsert);
				if (statement.executeUpdate() != 1) {
					return;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					statement.close();
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] agrs){
		
		DBInsert dbInsert = new DBInsert();
		//dbInsert.insertMTPushAD(5000);
		dbInsert.insertMTPush(5000);
	}
}
