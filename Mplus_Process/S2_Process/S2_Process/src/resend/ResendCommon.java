package resend;

import icom.DBPool;
import icom.MsgObject;
import icom.common.DBInsert;
import icom.common.DBSelect;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import ws.vmscharge.VMSChargeSender;

public class ResendCommon {
	
	/**
	 * ICOM
	 * active = 0;
	 * @param tableMlist
	 */
	public void handleJustRegICOM(String tableMlist,String serviceName){
		
		Calendar now = Calendar.getInstance();
		String strCurrHour = new java.text.SimpleDateFormat("HH:mm").format(now
				.getTime());
		
		String lastHour = getLastHourResend(serviceName);
		
		if(strCurrHour.compareTo(lastHour) >= 0){
			return;
		}
		
		
		DBSelect dbSelect = new DBSelect();
		
		ArrayList<MsgObject> arrMlistObj = 
				dbSelect.getMlistInfoActive(tableMlist,serviceName, 0);
		
		for(int i = 0;i<arrMlistObj.size(); i++){
			
			MsgObject msgObj = arrMlistObj.get(i);
			
			int result = -1;
			
			try{
				result = VMSChargeSender.SendToVmsCharge(msgObj,"24");
			}catch(Exception ex){
				Util.logger.error(ex.getMessage());
			}
			
			if(result == 1){
			// update Active
				String sqlUpdateMlist = "UPDATE " + tableMlist 
							+ " SET ACTIVE = 1 WHERE ID = " + msgObj.getId();
				DBUtil.Update(sqlUpdateMlist);
			}
			
		}
		
	}
	
	/**
	 * VMS
	 * active = 0
	 * @param tblMlist
	 */
	/*public void handleJustRegVMS(String tblMlist,String serviceName){
		
		Calendar now = Calendar.getInstance();
		String strCurrHour = new java.text.SimpleDateFormat("HH:mm").format(now
				.getTime());
		
		String lastHour = getLastHourResend(serviceName);
		
		if(strCurrHour.compareTo(lastHour) >= 0){
			return;
		}
		
		DBSelect dbSelect = new DBSelect();
		
		ArrayList<MsgObject> arrMlistObj = 
				dbSelect.getMlistInfoActive(tblMlist,serviceName, 0);
		
		for(int i = 0;i<arrMlistObj.size(); i++){
			
			MsgObject msgObj = new MsgObject();
			
			int result = -1;
			
			try{
				// insert VMS Charge
				DBInsert dbInsert = new DBInsert();
				result = dbInsert.insertVMSCharge(msgObj,"24");
				
			}catch(Exception ex){
				Util.logger.error(ex.getMessage());
			}
			
			if(result == 1){
			// update Active
				String sqlUpdateMlist = "UPDATE " + tblMlist 
							+ " SET ACTIVE = 1 WHERE ID = " + msgObj.getId();
				DBUtil.Update(sqlUpdateMlist);
			}
			
		}
		
		
	}*/
	
	private String getLastHourResend(String serviceName){
		
		String lastHour = "";
		String listHours = "";
		
		

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "resendAllDay";

		
		String sqlQuery = "SELECT COMMAND_CODE,listhours FROM " + tableName
				+ " WHERE COMMAND_CODE = '" + serviceName + "'" ;

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
					listHours = rs.getString("listhours");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("getArrayReSend SQLException: "
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("getArrayReSend SQLException: "
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		String[] arrHours = listHours.split(";");
		try{
			lastHour = arrHours[arrHours.length-1];
		}catch(Exception ex){
			Util.logger.error(ex.getMessage());
		}
		
		return lastHour;
		
	}
	
}
