package daugia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import icom.Constants;
import icom.DBPool;
import icom.Sender;
import icom.common.Util;

public class ExeCheckMaxSession extends Thread{
	
	private String NUMBER_MT_DAUGIA_ON_DAY = "30";
	
	public ExeCheckMaxSession(){
		NUMBER_MT_DAUGIA_ON_DAY = 
			Constants._prop.getProperty("DAUGIA_NUMBER_MT_ON_DAY");
	}
	
	public void run(){
		
		while(Sender.processData){
			
			ArrayList<DGAmount> arrMaxSession = findMaxSession();
			
			DaugiaCommon dgCommon = new DaugiaCommon();
			
			for(int i = 0;i<arrMaxSession.size();i++){
				
				DGAmount amountObj = arrMaxSession.get(i);
				Boolean check = dgCommon.isInMaxSession(amountObj.getUserId());
				if(!check){
					insertMaxSession(arrMaxSession.get(i));
				}
				
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private ArrayList<DGAmount> findMaxSession(){
		
		ArrayList<DGAmount> arrMaxSession = new ArrayList<DGAmount>();
	
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String currDate = getCurrDate();

		String sqlQuery = "SELECT * FROM "
				+ "( SELECT COUNT(*) AS number,user_id,time_send_mo "
				+ " FROM daugia_amount WHERE " + " time_send_mo LIKE '"
				+ currDate + "%' GROUP BY user_id ) t"
				+ " WHERE t.number >= " + NUMBER_MT_DAUGIA_ON_DAY;
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
					DGAmount obj = new DGAmount();
					obj.setUserId(rs.getString("user_id"));
					obj.setTimeSendMO(currDate);
					arrMaxSession.add(obj);
				}
			} else {
				Util.logger
						.error("DAUGIA - DGCheckMaxSession - getMaxSession : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - DGCheckMaxSession - getMaxSession. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - DGCheckMaxSession - getMaxSession. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMaxSession;
		
	}
	
	private String getCurrDate(){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date today = new java.util.Date();			
		String currDate = formatter.format(today);
		
		return currDate;
	}
	
	private int insertMaxSession(DGAmount dgAmountObj){

		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "INSERT INTO daugia_max_session "
			+ "( USER_ID, DATE_INSERT) "
			+ " VALUES ( ?, ?) ";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, dgAmountObj.getUserId());
			stmt.setString(2, dgAmountObj.getTimeSendMO());
			
			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			}

		} catch (SQLException ex3) {
			Util.logger.error("@DGCheckMaxSession insertMaxSession. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("@DGCheckMaxSession insertMaxSession SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
	}
	
}
