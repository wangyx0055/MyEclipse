package phase4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.common.DBInsert;
import icom.common.Util;

public class BLMOHandle {
	
	public BLMOHandle() {
		// TODO Auto-generated constructor stub
	}
	
	public void handleMO(MsgObject msgObj){
		
		msgObj.setCommandCode("BL");
		
		String info = msgObj.getUsertext();
		String[] arrTmp = info.split(" ");
		
		String blId = arrTmp[1].trim();
		// check blId
		Util.logger.info("Check code BINH LUAN: user_id = " + msgObj.getUserid()
				+ "; BinhLuanID = " + blId + "; info = " + info);
		Ph4Common cmmObj = new Ph4Common();
		if(checkCodeBL(blId)){
			// insert charge online new.
			msgObj.setAmount(500);
			
			if(cmmObj.isFreeList(msgObj.getUserid(),msgObj.getCommandCode())){
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT, msgObj);
			}else{
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObj);
			}

		}else{			
			// send MT
			
			String strError = this.getErrContent(msgObj.getServiceName());
			msgObj.setUsertext(strError);
			DBInsert dbInsert = new DBInsert();
			dbInsert.sendMT(msgObj);
						
		}
		
						
	}
	
	public void handleMOBLN(MsgObject msgObj){
		
	}
	
	/**
	 * return true, if blId is exist.<br/>
	 * return false if blId is not exist
	 */
	public Boolean checkCodeBL(String blId){
		
		Boolean check = false;
		int count = 0;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM Comments " +
				" WHERE subcode = '" + blId +"' AND Status = 2 ";
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection("content2012");
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger
						.error("BLMOHandle - checkCodeBL : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("BLMOHandle - checkCodeBL :. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("BLMOHandle - checkCodeBL. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
//		System.out.println("count = " + count);
		if(count > 0){
			check = true;
		}
		
		return check;

	}
	public String getErrContent(String serviceName){
		String strReturn = "";
		String FUNCTIONNAME = "getSecondMapping ";
		
		String sqlSelect  = "SELECT errormessage FROM keywords WHERE service_name LIKE '"+serviceName+"'";
		DBPool dbpool = new DBPool();
		

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = dbpool.getConnectionGateway();
			if (conn != null) {										
				stmt = conn.prepareStatement(sqlSelect,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);

				if (stmt.execute()) {
					rs = stmt.getResultSet();
					while (rs.next()) {
						//set information for object
						strReturn = rs.getString("errormessages");
					}
				}else{
					Util.logger.error(FUNCTIONNAME+" : Statement can't execute .");
				}
			}else{
				Util.logger.error(FUNCTIONNAME+": Connection is null.");
			}
		}catch (Exception ex) {
			Util.logger.error(FUNCTIONNAME+" error: "+ex.toString());
			ex.printStackTrace();
		}finally{
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(conn);
		}
		
		return strReturn;
	}
	
}
