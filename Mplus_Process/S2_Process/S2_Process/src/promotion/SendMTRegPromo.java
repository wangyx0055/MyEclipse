package promotion;

import icom.DBPool;
import icom.Sender;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import servicesPkg.MlistInfo;

/**
 * 
 * @Date 2011-07-14
 * @author DanND
 *
 */

public class SendMTRegPromo extends Thread {
	
	private String mlistFarm = "mlist_farm";
	private String mlistGame = "mlist_game";
	private String poolGateWay = "gateway";
	private String poolVMS = "s2mplus";
	
	public void run() {
		
		
		PromoCommon commObj = new PromoCommon();

		Boolean isTest = commObj.isTest();
		
		String[] arrPhoneTest = null;
		
		if(isTest){
			arrPhoneTest = commObj.getPhoneTest();
			Util.logger.info("DANG CHAY HE THONG TEST GAME IPHONE 4");
		}
		
		
		while(Sender.getData){
			
			if(!PromoProcess.isPromo){
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
				
			}
			
			sendPromotionCode(mlistGame, commObj, arrPhoneTest,poolGateWay);
			sendPromotionCode(mlistFarm, commObj, arrPhoneTest,poolVMS);
			
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}// end while
		
	}
	
	private void sendPromotionCode(String tblMlist, PromoCommon commObj,String[] arrPhoneTest, String poolName){

		ArrayList<MlistInfo> arrObj = this.loadMlist(tblMlist,poolName);
		
		for(int i = 0;i<arrObj.size();i++){
			
			if(!Sender.getData) break;
			
			MlistInfo obj = arrObj.get(i);
			
			//System.out.println("UserID = " + obj.getUserId());
			// Neu he thong dang chay test
			if(arrPhoneTest != null){
				
				if(!commObj.isPhoneTest(obj.getUserId(), arrPhoneTest)){								
					updateMlist(1,obj.getId(),tblMlist,poolName);
					continue;
				}
				
			}
			
			//System.out.println("So Test = " + obj.getUserId());
			
			ArrayList<PromotionCodeObj> arrCode = commObj.getPromoCode(1);
			
			if(arrCode.size() == 0){
				
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				arrCode = commObj.getPromoCode(1);
				
			}
			
			PromotionCodeObj objCode = arrCode.get(0); 
			
			commObj.updateIsUsed(objCode.getId(),1);
			
			String info = this.getInfo(objCode.getPromotionCode());
			
			if(commObj.sendMTmplusPromoCode(obj, info)==1){
				
				if(updateMlist(1,obj.getId(),tblMlist,poolName) == 1){
					
					commObj.insertUserCode(obj.getUserId(), 
							objCode.getPromotionCode(),
							obj.getCommandCode(),0);
					
				}
				
			}
			
		}// end for
	}
	
	private ArrayList<MlistInfo> loadMlist(String tblMlist,String poolName) {

		String tableName =  tblMlist; //mlist_game, mlist_farm;
		
		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName + " WHERE IS_SEND_PROMOCODE = 0 AND REG_COUNT = 1 ";

		ArrayList<MlistInfo> arrMlistInfo = new ArrayList<MlistInfo>();
		MlistInfo mlistInfo;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					mlistInfo = null;
					mlistInfo = new MlistInfo();
					mlistInfo.setId(rs.getInt("ID"));
					mlistInfo.setUserId(rs.getString("USER_ID"));
					mlistInfo.setServiceId(rs.getString("SERVICE_ID"));
					mlistInfo.setToday(rs.getString("DATE"));
					mlistInfo.setOptions(rs.getString("OPTIONS"));
					mlistInfo.setFailures(rs.getString("FAILURES"));
					mlistInfo.setLastCode(rs.getString("LAST_CODE"));
					mlistInfo.setAutoTimeStamps(rs
							.getTimestamp("AUTOTIMESTAMPS"));
					mlistInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					mlistInfo.setRequestId(rs.getString("REQUEST_ID"));
					mlistInfo.setMessageType(rs.getInt("MESSAGE_TYPE"));
					mlistInfo.setMobiOperator(rs.getString("MOBILE_OPERATOR"));
					mlistInfo.setMtCount(rs.getInt("MT_COUNT"));
					mlistInfo.setMtFree(rs.getInt("MT_FREE"));
					mlistInfo.setDuration(rs.getInt("DURATION"));
					mlistInfo.setAmount(rs.getInt("AMOUNT"));
					mlistInfo.setContentId(rs.getInt("CONTENT_ID"));
					mlistInfo.setService(rs.getString("SERVICE"));
					mlistInfo.setCompanyId(rs.getString("COMPANY_ID"));
					mlistInfo.setActive(rs.getInt("ACTIVE"));
					mlistInfo.setChanelType(rs.getInt("CHANNEL_TYPE"));
					mlistInfo.setRegCount(rs.getInt("REG_COUNT"));

					arrMlistInfo.add(mlistInfo);
					
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("SendMTRegPromo - loadMlist. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("SendMTRegPromo - loadMlist. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;
	}

	
	private int updateMlist(int isSendPromoCode, int id, String tblMlist, String poolName){
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			
			connection = dbpool.getConnection(poolName);
			
			if (connection == null) {				
				return -1;
			}

			sqlString = "UPDATE " + tblMlist + " SET IS_SEND_PROMOCODE = " 
				+ isSendPromoCode + " WHERE ID = " + id;

			statement = connection.prepareStatement(sqlString);

			iReturn = statement.executeUpdate();

		} catch (SQLException e) {
			Util.logger
					.crisis("SendMTRegPromo  ###@## updateMlist: Error@ = " + e.getMessage());
		} catch (Exception e) {
			Util.logger
			.crisis("SendMTRegPromo  ###@## updateMlist: Error@ = " + e.getMessage());
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
		
	}
	
	private String getInfo(String promoCode){
		
		String info = "Ma so du thuong CTKM mPlus cua ban la " + promoCode
				+ ". Tu 23/9-10/10, dang ky cac the loai game khac de"
				+ " them co hoi so huu xe Air Blade va 620 giai thuong"
				+ " khac tu MobiFone. Chi tiet tai http://m.mplus.vn"
				+ " hoac goi 9244.";
		

		return info;
	}


	
}
