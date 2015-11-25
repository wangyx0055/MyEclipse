package daugia;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmptyMng {
	
	private static int amountEmpty = -1;
	
	private static EmptyMng emptyObj = null;
	
	public static synchronized EmptyMng getInstance(){
		if(emptyObj == null){
			emptyObj = new EmptyMng();
		}
		return emptyObj;
	}
	
	public synchronized int getAmountEmpty(){
		
		if(amountEmpty == -1){
			amountEmpty = findAmountEmpty();
		}
		return amountEmpty;
	}
	
	public synchronized void resetAmountEmpty(){
		amountEmpty = -1;
	}
	
	private int findAmountEmpty(){

		int amountEmpty = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		
		String sqlQuery = "SELECT amount_empty FROM" +
				" daugia_amount_empty WHERE status = 0 " +
				" ORDER BY amount_empty ASC LIMIT 1";
		// System.out.println("getMlistTableName: " + sqlQuery);
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
					amountEmpty = rs.getInt("amount_empty");
				}
			} else {
				Util.logger
						.error("EmptyMng - findAmountEmpty : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("EmptyMng - findAmountEmpty. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("EmptyMng - findAmountEmpty. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
//		System.out.println(" Amount Empty = " + amountEmpty);
		return amountEmpty;
		
	}
	
}
