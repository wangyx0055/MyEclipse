package daugia;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class AmountQueue {
	
	private static Hashtable<String, Integer> hAmount = new Hashtable<String, Integer>();
	// int1 - key: amount, int2:number user bet
	private static AmountQueue amountQueue = null;
	
	public static synchronized AmountQueue getInstance(){
		if(amountQueue == null){
			amountQueue = new AmountQueue();
		}
		return amountQueue;
	}
	
	public void inithAmount(){
		
		for(int i =0;i<20000;i++){
			int key = (i+1)*1000;
			hAmount.put(key + "", 0);
		}
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT DG_AMOUNT, count(DG_AMOUNT) as NUMBER_USER FROM " +
				" daugia_amount group by DG_AMOUNT";
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
					String dgAmount = rs.getString("DG_AMOUNT");
					Integer numberUser = rs.getInt("NUMBER_USER");
					Integer check = hAmount.get(dgAmount);
					if(check == null || check <0) continue;
					hAmount.put(dgAmount, numberUser);
				}
			} else {
				Util.logger
						.error("DAUGIA - getHashDGAmount : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - getHashDGAmount. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - getHashDGAmount. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
	}
	
	public Hashtable<String, Integer> getHAmount(){
		return hAmount;
	}
	
	public void updateAmount(String dgAmount){
		Integer number = hAmount.get(dgAmount);
		if(number == null || number < 0) return;
		number = number + 1;
		hAmount.put(dgAmount, number);
	}
	
	public void resetAmountQueue(){
		for(int i =0;i<20000;i++){
			int key = (i+1)*1000;
			hAmount.put(key + "", 0);
		}
	}
	
}
