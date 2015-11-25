package promotion;

import icom.DBPool;
import icom.Sender;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenPromoCode extends Thread {
	
	private int codeStart = 10000000;
	private String promoService = "GAME";
	
	
	public void run(){

		while (Sender.getData) {

			int numberCode = this.getNumberCode();
			
			// generate new code
			if (numberCode < 1000) {

				int codeGen = codeStart;
				
				String maxCode = this.getMaxCode();
				
				if(!maxCode.trim().equals("")){
					try{
						codeGen = Integer.parseInt(maxCode.trim());
					}catch (Exception e) {
					}
				}else{
					this.insertMaxCode(codeStart+"");
				}

//				PromotionCodeObj objCode = this.getLastestPromoCode();
//				if (objCode != null) {
//					try {
//						codeGen = Integer.parseInt(objCode.getPromotionCode());
//					} catch (Exception ex) {
//						Util.logger.info(ex.getMessage());
//					}
//				}

				for (int i = 0; i < 100; i++) {
					if (!Sender.getData)
						break;
					codeGen = codeGen + 1;
					this.insertNewCode(promoService, codeGen + "");
					
				}
				
				this.updateMaxCode(codeGen + "");

			}
			// end generate code
			
			// delete used code
			this.deleteUsedCode();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

	}
	
	private int getNumberCode(){
		
		int numberCode = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT COUNT(*) AS number FROM " +
				"promotion_code WHERE is_used = 0";
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
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
					numberCode = rs.getInt("number");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("GenPromoCode - getNumberCode SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("GenPromoCode - getNumberCode SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return numberCode;
		
	}
	
	private PromotionCodeObj getLastestPromoCode(){
		
		PromotionCodeObj obj = null;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT id,services,promotion_code,is_used FROM " +
				" promotion_code ORDER BY PROMOTION_CODE DESC LIMIT 1 ";
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
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
					obj = new PromotionCodeObj();
					obj.setId(rs.getInt("id"));
					obj.setServices(rs.getString("services"));
					obj.setPromotionCode(rs.getString("promotion_code"));
					obj.setIsUsed(rs.getInt("is_used"));
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("GenPromoCode - getLastestPromoCode SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("GenPromoCode - getLastestPromoCode SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return obj;
	}
	
	private int insertNewCode(String services,String genCode){
		
		Util.logger.info("Gen Code for Game Prommotion!");
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
				return -1;
			}

			sqlString = "INSERT INTO promotion_code(services,promotion_code) "
					+ "VALUES (?, ?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, services);
			statement.setString(2, genCode);

			iReturn = statement.executeUpdate();

		} catch (SQLException e) {
			Util.logger
					.crisis("GenPromoCode  ###@## insertNewCode: Error@ = " + e.getMessage());
		} catch (Exception e) {
			Util.logger
			.crisis("GenPromoCode  ###@## insertNewCode: Error@ = " + e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		
		return iReturn;
		
	}
	
	
	private int deleteUsedCode(){
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
				return -1;
			}

			sqlString = "DELETE FROM promotion_code WHERE is_used = 1";

			statement = connection.prepareStatement(sqlString);

			iReturn = statement.executeUpdate();

		} catch (SQLException e) {
			Util.logger
					.crisis("GenPromoCode  ###@## deleteUsedCode: Error@ = " + e.getMessage());
		} catch (Exception e) {
			Util.logger
			.crisis("GenPromoCode  ###@## deleteUsedCode: Error@ = " + e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		
		return iReturn;
	}
	
	private int insertMaxCode(String maxCode){
		
		Util.logger.info("Gen Code for Game Prommotion!");
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
				return -1;
			}

			sqlString = "INSERT INTO promotion_maxcode(MAX_CODE) "
					+ "VALUES (?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, maxCode);

			iReturn = statement.executeUpdate();

		} catch (SQLException e) {
			Util.logger
					.crisis("GenPromoCode  ###@## insertMaxCode: Error@ = " + e.getMessage());
		} catch (Exception e) {
			Util.logger
			.crisis("GenPromoCode  ###@## insertMaxCode: Error@ = " + e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		
		return iReturn;
		
	}
	
	private int updateMaxCode(String maxCode){
		
		Util.logger.info("Gen Code for Game Prommotion!");
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
				return -1;
			}

			sqlString = "UPDATE promotion_maxcode set MAX_CODE = '" + maxCode
					+ "' ";

			statement = connection.prepareStatement(sqlString);

			iReturn = statement.executeUpdate();

		} catch (SQLException e) {
			Util.logger
					.crisis("GenPromoCode  ###@## updateMaxCode: Error@ = " + e.getMessage());
		} catch (Exception e) {
			Util.logger
			.crisis("GenPromoCode  ###@## updateMaxCode: Error@ = " + e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		
		return iReturn;
		
	}
	
	private String getMaxCode(){
		
		String maxCode = "";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT MAX_CODE FROM promotion_maxcode";
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
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
					maxCode = rs.getString("MAX_CODE");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("GenPromoCode - getMaxCode SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("GenPromoCode - getMaxCode SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return maxCode;
	}
	
	
	
//	private int updateIsUsed(int id){
//		
//		int iReturn = -1;
//		
//		Connection connection = null;
//		PreparedStatement statement = null;
//		String sqlString = null;
//
//		DBPool dbpool = new DBPool();
//
//		try {
//			connection = dbpool.getConnectionGateway();
//			if (connection == null) {				
//				return -1;
//			}
//
//			sqlString = "UPDATE promotion_code SET is_used = 0 WHERE id = " + id;
//
//			statement = connection.prepareStatement(sqlString);
//
//			iReturn = statement.executeUpdate();
//
//		} catch (SQLException e) {
//			Util.logger
//					.crisis("GenPromoCode  ###@## deleteUsedCode: Error@ = " + e.getMessage());
//		} catch (Exception e) {
//			Util.logger
//			.crisis("GenPromoCode  ###@## deleteUsedCode: Error@ = " + e.getMessage());
//		} finally {
//			dbpool.cleanup(statement);
//			dbpool.cleanup(connection);
//		}
//
//		
//		return iReturn;
//		
//	}
	
}
