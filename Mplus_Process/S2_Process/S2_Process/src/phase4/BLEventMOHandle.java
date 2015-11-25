package phase4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;
import icom.common.DBUtil;
import icom.common.Util;

public class BLEventMOHandle extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		String[] arrInfo = msgObject.getUsertext().trim().split(" ");
		if(arrInfo.length == 1){
			return DBUtil.RegisterServices(msgObject, keyword, Constants.TYPE_OF_SERVICE_TEXTBASE,services);
		}
		
		String blId = arrInfo[1].trim();
		// check blId
		Util.logger.info("Check code BINH LUAN: user_id = " + msgObject.getUserid()
				+ "; BinhLuanID = " + blId + "; info = " + msgObject.getUsertext());
		Ph4Common cmmObj = new Ph4Common();
		if(checkCodeBL(blId)){
			// insert charge online new.
			msgObject.setAmount(500);
			
			if(cmmObj.isFreeList(msgObject.getUserid(),msgObject.getCommandCode())){
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT, msgObject);
			}else{
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObject);
			}

		}else{			
			// send MT
			msgObject.setUsertext(this.getErrContent(msgObject.getServiceName()));
			DBInsert dbInsert = new DBInsert();
			dbInsert.sendMT(msgObject);
						
		}
		
		return null;
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
