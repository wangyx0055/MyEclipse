package alert;

import icom.DBPool;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

/***
 * 
 * Get MaxId, isDate from CMS then update to alert_textbase
 * 
 * @author DanND
 * @date 2011-10-12
 */

public class TextBaseAlert extends Thread{
	
	
	String[] runHours = {"12:00","20:00"};
	String dbContent = "content";
	
	public void run(){
		
		Util.logger.info("Start Thread ALERT TEXTBASE.");
		
		while(Sender.processData){
		
			if (isTimeRun()) {
				
				// get list services
				ArrayList<String> arrServices = getServicesName();
				
				for (int i = 0; i < arrServices.size(); i++) {
					
					if(!Sender.processData) break;
					
					String serviceName = arrServices.get(i);
					AlertTextBaseObj alertObj = getMaxId(serviceName);

					if (alertObj == null)
						continue;

					if (isInAlertTextBase(serviceName)) {

						updateAlertTextBase(alertObj);

					} else {

						insertAlertTextBase(alertObj);

					}

				}

			} // end for
		
		try {
			Thread.sleep(60* 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		}
		
	}
	
	private Boolean isTimeRun(){
		
		Boolean check = false;
		
		Calendar now = Calendar.getInstance();
		String currHour = new java.text.SimpleDateFormat("HH:mm").format(now
				.getTime());
		
		for(int i=0;i<runHours.length;i++){
			
			if(currHour.equals(runHours[i])){
				check = true;
				break;
			}
			
		}
		
		return check;
		
	}
	
	/**
	 * get list service name
	 * @return
	 */
	private ArrayList<String> getServicesName(){
		
		ArrayList<String> arrServices = new ArrayList<String>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT services FROM alert_textbased ";
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
					String serviceName = rs.getString("services");
					arrServices.add(serviceName);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("TextBaseAlert :: getServicesName:: Error: "
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("TextBaseAlert :: getServicesName:: Error:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrServices;
		
	}
	
	private AlertTextBaseObj getMaxId(String serviceName){
		
		AlertTextBaseObj alertObj = null;
		
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			connection = dbpool.getConnection(dbContent);
			
			String query = "select id,isdate from newsagri_news_info " +
					" where id=(select max(id) from newsagri_news_info" +
					" where newstypecode='" + serviceName + "')";

			Util.logger.info("TextBaseAlert: getMaxId: sql =" + query);
			stmt = connection.prepareStatement(query);

			if (stmt.execute()) {
				
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					alertObj = new AlertTextBaseObj();
					alertObj.setMaxId(rs.getInt("id"));
					alertObj.setLastDate(rs.getString("isdate"));
					alertObj.setServiceName(serviceName);
					
				}
			}

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getMaxId: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);

		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
				
		return alertObj;
	}
	
	private Boolean isInAlertTextBase(String servicesName){
		
		int count = 0;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		
		String sqlQuery = "SELECT COUNT(*) AS number FROM alert_textbased " +
				" WHERE services = '" + servicesName + "'";
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("TextBaseAlert :: isInAlertTextBase:: Error: "
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("TextBaseAlert :: isInAlertTextBase:: Error:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		if(count == 0) return false;
		
		return true;
		
	}
	
	private int insertAlertTextBase(AlertTextBaseObj alertObj){
		
		String sqlUpdate = "INSERT INTO alert_textbased(" +
				"services,maxid,lastdate)" +
				" VALUES('" + alertObj.getServiceName() + 
				"', '" + alertObj.getMaxId() + 
				"', '" + alertObj.getLastDate() + "')";
		
		Util.logger.info("insertAlertTextBase :: SQL Querry = " + sqlUpdate);
		
		return DBUtil.executeSQL("gateway", sqlUpdate);
		
	}
	
	private int updateAlertTextBase(AlertTextBaseObj alertObj){
		
		String sqlUpdate = "UPDATE alert_textbased SET " +
				" maxid = '" + alertObj.getMaxId() + "', " +
				" lastdate = '" + alertObj.getLastDate() +"' " +
				"WHERE services = '" + alertObj.getServiceName() + "'";
		
		Util.logger.info("updateAlertTextBase :: SQL Querry = " + sqlUpdate);
		
		return DBUtil.executeSQL("gateway", sqlUpdate);
		
	}
	

}
