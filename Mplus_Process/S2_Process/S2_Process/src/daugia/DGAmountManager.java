package daugia;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

public class DGAmountManager {

	public int insertDGAmount(DGAmount dgAmountObj, String tableName){
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "INSERT INTO "
			+ tableName
			+ "( USER_ID, SERVICE_ID, TIME_SEND_MO, DG_AMOUNT, MA_SP ) "
			+ " VALUES ( ?, ?, ?, ?, ? ) ";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, dgAmountObj.getUserId());
			stmt.setString(2, dgAmountObj.getServiceId());
			stmt.setString(3, dgAmountObj.getTimeSendMO());
			stmt.setString(4, dgAmountObj.getDgAmount());
			stmt.setString(5, dgAmountObj.getMaSP());

			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			}

		} catch (SQLException ex3) {
			Util.logger.error("@DGAmountManager insertDGAmount. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("@DGAmountManager insertDGAmount SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return iReturn;

	}
	
	public int moveDGAmountLog(String tableLog){
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "INSERT INTO " + tableLog
						 + "(USER_ID, SERVICE_ID, TIME_SEND_MO, DG_AMOUNT, MA_SP) " 
						 + " (SELECT USER_ID, SERVICE_ID, TIME_SEND_MO, DG_AMOUNT, MA_SP "
						 + " FROM daugia_amount)";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			
			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			}

		} catch (SQLException ex3) {
			Util.logger.error("@DGAmountManager moveDGAmountLog. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("@DGAmountManager moveDGAmountLog SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return iReturn;

	}
	
	public int insertDGAmountWin(DGAmount dgAmountObj, String tableName){
		
		int iReturn = -1;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "INSERT INTO "
			+ tableName
			+ "( USER_ID, SERVICE_ID, TIME_SEND_MO, DG_AMOUNT, MA_SP,WIN_RANK, NUMBER_DAUGIA, NUMBER_WIN_TMP ) "
			+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, dgAmountObj.getUserId());
			stmt.setString(2, dgAmountObj.getServiceId());
			stmt.setString(3, dgAmountObj.getTimeSendMO());
			stmt.setString(4, dgAmountObj.getDgAmount());
			stmt.setString(5, dgAmountObj.getMaSP());
			stmt.setInt(6, dgAmountObj.getWinRank());
			stmt.setInt(7, dgAmountObj.getNumberDG());
			stmt.setInt(8, dgAmountObj.getNumberWinnerTmp());

			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			}

		} catch (SQLException ex3) {
			Util.logger.error("@DGAmountManager insertDGAmountWin. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("@DGAmountManager insertDGAmountWin SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return iReturn;

	}
	
	
	public Hashtable<String, Integer> getHashDGAmount(){
		
		Hashtable<String, Integer> hAmount = new Hashtable<String, Integer>();		
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
		
		return hAmount;
	}
	
	public Hashtable<String, Integer> getHashDGAmountOnly(){
		
		Hashtable<String, Integer> hAmount = new Hashtable<String, Integer>();		
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
					if(numberUser == 1){
						hAmount.put(dgAmount, numberUser);
					}
				}
			} else {
				Util.logger
						.error("DAUGIA - getHashDGAmountOnly : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - getHashDGAmountOnly. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - getHashDGAmountOnly. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return hAmount;
	}
	

	
	private String getAmountMinAndOnly(Hashtable<String, Integer> hAmount){

		String mintAmount = "-1";
		int count = -1;		
		Enumeration<String> e = hAmount.keys();
		while(e.hasMoreElements()){
			String strAmount = e.nextElement();
			Integer numberUser = hAmount.get(strAmount);
			if(numberUser == 1){
				if(count == -1){
					mintAmount = strAmount;
					count = 0;
				}else{
					if(mintAmount.length() > strAmount.length()){
						mintAmount = strAmount;
					}else if(mintAmount.length() == strAmount.length()){
						if(mintAmount.compareTo(strAmount)>0){
							mintAmount = strAmount;
						}
					}	
				}
			}
		}
		
		return mintAmount;
	}
	
	private String getMinAmount(Hashtable<String, Integer> hAmount){

		String mintAmount = "-1";
		int count = -1;
		
		Enumeration<String> e = hAmount.keys();
		while(e.hasMoreElements()){
			String strAmount = e.nextElement();			
			if(count == -1){
					mintAmount = strAmount;
					count = 0;
			}else{
				if(mintAmount.length() > strAmount.length()){
					mintAmount = strAmount;
				}else if(mintAmount.length() == strAmount.length()){
					if(mintAmount.compareTo(strAmount)>0){
						mintAmount = strAmount;
					}
				}
			}
			
		}
		
		return mintAmount;
	}
	
	public String getAmountTrungThuong() {
		AmountQueue amountQueue = AmountQueue.getInstance();
		String amountWin = getAmountMinAndOnly(amountQueue.getHAmount());
		if (amountWin.equals("-1")) {
			Hashtable<String, Integer> hAmount = this.getHashDGAmountOnly();
			return getAmountMinAndOnly(hAmount);
		} else {
			return amountWin;
		}
	}
	
	public DGAmount getUserWin(){
		DGAmount userWin = null;
		
		String amountWin = this.getAmountTrungThuong();
		
		double test = -1;
		try{
			test = Double.parseDouble(amountWin);
		}catch(Exception ex){}
		
		if(test>0){
			userWin = getDGAmount(amountWin);
		}
		
		return userWin;
	}
	
	public DGAmount getDGAmount(String amount){
		
		DGAmount dgObj = null;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, USER_ID, SERVICE_ID, TIME_SEND_MO, DG_AMOUNT, MA_SP FROM " +
				" daugia_amount WHERE DG_AMOUNT = '" + amount + "'";
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
					dgObj = new DGAmount();
					dgObj.setId(rs.getInt("ID"));
					dgObj.setUserId(rs.getString("USER_ID"));
					dgObj.setServiceId(rs.getString("SERVICE_ID"));
					dgObj.setTimeSendMO(rs.getString("TIME_SEND_MO"));
					dgObj.setDgAmount(rs.getString("DG_AMOUNT"));
					dgObj.setMaSP(rs.getString("MA_SP"));
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
		
		return dgObj;
	}
	
	public int resetDGAmount(String tableName){
		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		Util.logger.info("resetDGAmount");
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("resetDGAmount: Error connection == null");
				iReturn = -1;
			}

			sqlString = "DELETE FROM "
					+ tableName;
			
			statement = connection.prepareStatement(sqlString);
			
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("DAUGIA resetDGAmount Error at table name = " + tableName);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("DAUGIA resetDGAmount: Error@ table name = " + tableName);
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("DAUGIA resetDGAmount: Error@ table name = " + tableName);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int findNumberEmptyUnder(String dgAmount){
		
		int lengthAmount = dgAmount.length()+1;
		
		if(lengthAmount>9) return -1;
		
		if(lengthAmount == 9){
			if(dgAmount.compareTo("20000000")>0)
				return -1;
		}
		
		int countNumber = 0;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS numbercount FROM daugia_amount_empty " +
				" WHERE amount_empty <" + dgAmount + " AND status = 0 ";
//		System.out.println("findNumberEmptyUnder: " + sqlQuery);
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
					countNumber = rs.getInt("numbercount");
				}
			} else {
				Util.logger
						.error("DGAmountManager - findNumberEmptyUnder : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DGAmountManager - findNumberEmptyUnder. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DGAmountManager - findNumberEmptyUnder. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
//		System.out.println("Number Empty Under = " + countNumber);
		return countNumber;
		
	}
	
	public int findAmountEmpty(){

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
						.error("DGAmountManager - findAmountEmpty : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DGAmountManager - findAmountEmpty. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DGAmountManager - findAmountEmpty. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
//		System.out.println(" Amount Empty = " + amountEmpty);
		return amountEmpty;
		
	}
	
	public int updateEmptyAmount(String dgAmount, int status){
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "UPDATE daugia_amount_empty SET status="
				+ status +
				" WHERE amount_empty = " + dgAmount;
		
//		System.out.println(sqlQuery);
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			
			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			}

		} catch (SQLException ex3) {
			Util.logger.error("@DGAmountManager updateEmptyAmount. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("@DGAmountManager updateEmptyAmount SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
	}
	
	/**
	 * 
	 * @param dgAmount
	 * @return status.<br/>
	 *  status = -1, dgAmount is not exist in daugia_amount_empty
	 *  status = 0, nobody bet this amount
	 *  status = 1, someone already bet this amount.
	 */
	public int getEmptyStatus(String dgAmount){
		
		int status = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		
		String sqlQuery = "SELECT STATUS FROM daugia_amount_empty " +
					" WHERE amount_empty = " + dgAmount;
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
					status = rs.getInt("STATUS");
				}
			} else {
				Util.logger
						.error("DGAmountManager - getEmptyStatus : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DGAmountManager - getEmptyStatus. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DGAmountManager - getEmptyStatus. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return status;
		
	}
	
	public int getNumberDG(String dgAmount){
		
		int number = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		
		String sqlQuery = "SELECT COUNT(ID) AS number FROM daugia_amount WHERE DG_AMOUNT = " + dgAmount;
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
					number = rs.getInt("number");
				}
			} else {
				Util.logger
						.error("DGAmountManager - getNumberDG : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DGAmountManager - getNumberDG. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DGAmountManager - getNumberDG. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return number;
	}
	
	

}
