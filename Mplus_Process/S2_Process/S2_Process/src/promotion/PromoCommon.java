package promotion;

import icom.Constants;
import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import servicesPkg.MlistInfo;

public class PromoCommon {
	
	public String promoService = "GAME";

	public int updateIsUsed(int id, int isUsed){

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

			sqlString = "UPDATE promotion_code SET is_used = " 
					+ isUsed + " WHERE id = "+ id;

			statement = connection.prepareStatement(sqlString);

			iReturn = statement.executeUpdate();

		} catch (SQLException e) {
			Util.logger.crisis("PromoCommon  ###@## deleteUsedCode: Error@ = "
					+ e.getMessage());
		} catch (Exception e) {
			Util.logger.crisis("PromoCommon  ###@## deleteUsedCode: Error@ = "
					+ e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;

	}
	
	public ArrayList<PromotionCodeObj> getPromoCode(int numberCode){
		
		ArrayList<PromotionCodeObj> promoCode = new ArrayList<PromotionCodeObj>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT id,services,promotion_code,is_used FROM promotion_code " +
				" WHERE is_used = 0 ORDER BY RAND() limit " + numberCode;
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

					
					PromotionCodeObj obj = new PromotionCodeObj();
					obj.setPromotionCode(rs.getString("promotion_code"));
					obj.setId(rs.getInt("id"));
					obj.setServices(rs.getString("services"));
					obj.setIsUsed(rs.getInt("is_used"));
					
					promoCode.add(obj);
					
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("PromoCommon - getPromoCode SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("PromoCommon - getPromoCode SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return promoCode;
		
	}
	
	/**
	 * Send insert to MTQUEUE
	 * @param msgObject
	 * @param info
	 * @return
	 */
	public int sendMTmplusPromoCode(MlistInfo msgObject, String info) {
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("s2mplus");
			if (connection == null) {
				
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, "
					+ "CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserId());
			statement.setString(2, msgObject.getServiceId());
			statement.setString(3, msgObject.getMobiOperator());
			statement.setString(4, msgObject.getCommandCode());
			statement.setInt(5,0);
			statement.setString(6, info);
			statement.setInt(7, 0);
			statement.setString(8, msgObject.getRequestId());
			statement.setInt(9, 0);

			iReturn = statement.executeUpdate();
			
		} catch (SQLException e) {
			Util.logger.crisis("PromoCommon Error: " + e.getMessage());
		} catch (Exception e) {
			Util.logger.crisis("PromoCommon Error: " + e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
		
	}
	
	/**
	 * Send insert to MTQUEUE
	 * @param msgObject
	 * @param info
	 * @return
	 */
	public int sendMTGateWayPromoCode(MlistInfo msgObject, String info) {
		
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

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, "
					+ "CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserId());
			statement.setString(2, msgObject.getServiceId());
			statement.setString(3, msgObject.getMobiOperator());
			statement.setString(4, msgObject.getCommandCode());
			statement.setInt(5,0);
			statement.setString(6, info);
			statement.setInt(7, 0);
			statement.setString(8, msgObject.getRequestId());
			statement.setInt(9, 0);

			iReturn = statement.executeUpdate();
			
		} catch (SQLException e) {
			Util.logger.crisis("PromoCommon Error: " + e.getMessage());
		} catch (Exception e) {
			Util.logger.crisis("PromoCommon Error: " + e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
		
	}
	
	/**
	 * 
	 * @param userId
	 * @param codeProm
	 * @param services
	 * @param codeType : 0 registration, 1: WAPPUSH
	 * @return
	 */
	public int insertUserCode(String userId, String codeProm, String services, int codeType){
		
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

			sqlString = "INSERT promotion_user_code(SERVICES,USER_ID,PROMOTION_CODE,code_type) " +
					" VALUES(?,?,?,?)";
			
			
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, services);
			statement.setString(2, userId);
			statement.setString(3, codeProm);
			statement.setInt(4, codeType);
			
			iReturn = statement.executeUpdate();

		} catch (SQLException e) {
			Util.logger.crisis("PromoCommon  ###@## insertUserCode: Error@ = "
					+ e.getMessage());
		} catch (Exception e) {
			Util.logger.crisis("PromoCommon  ###@## insertUserCode: Error@ = "
					+ e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	public int getRandomNumber(){
		Random rd = new Random();
		return rd.nextInt(10);
	}
	
	/**
	 * true: dang test
	 * false: chay that
	 * @return
	 */
	public Boolean isTest(){
		
		String strTest = Constants._prop.getProperty("RUN_PROMOTION");
		int iTest = 1;
		try{
			
			iTest = Integer.parseInt(strTest.trim());
			
		}catch(Exception ex){
			
		}
		
		if(iTest == 1) return false;
		
		return true;
	}
	
	public String[] getPhoneTest(){

		
		String phoneTest = 
			Constants._prop.getProperty("PROMOTION_PHONE_TEST");
		
		String[] arrPhoneTest = phoneTest.split(";");
		
		return arrPhoneTest;
		
	}
	
	public Boolean isPhoneTest(String user_id, String[] arrPhoneTest){
		
		Boolean blCheck = false;
		
		for(int i = 0;i< arrPhoneTest.length;i++){
			
			if(user_id.equals(arrPhoneTest[i].trim())){
				blCheck = true;
				break;
			}
			
		}
		
		return blCheck;
		
	}
	
}
