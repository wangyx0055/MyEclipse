package dailyAll;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class AlldayRechargeMng {
	
	
	private static AlldayRechargeMng instance = null;
	private static ArrayList<String> arrService = null;
	
	private AlldayRechargeMng(){
		
		arrService = this.getAlldayService();
	}
	
	
	public synchronized static AlldayRechargeMng getInstance(){
		
		if(instance == null){
			instance = new AlldayRechargeMng();
		}
		
		return instance;
		
	}
	
	
	private ArrayList<String> getAlldayService() {
		
		ArrayList<String> arrServiceTmp = new ArrayList<String>();
		
		String sqlSelect = "SELECT COMMAND_CODE FROM recharge_allday";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mplus");
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					arrServiceTmp.add(rs.getString("COMMAND_CODE"));
				}
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("AlldayRechargeMng - getAlldayService. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("AlldayRechargeMng - getAlldayService. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrServiceTmp;

	}
	
	/**
	 * 
	 * @param commandCode
	 * @return true if service is recharged all day <br/>
	 * 			false if not recharged all day
	 */
	public Boolean isRechargeAll(String commandCode){
		
		Boolean check = false;
		
		for(int i = 0;i<arrService.size();i++){
			
			if(commandCode.equals(arrService.get(i).trim())){
				check = true;
				break;
			}
			
		}
		
		return check;
	}
	
}
