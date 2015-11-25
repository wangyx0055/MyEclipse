package resend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import sub.DeliveryManager;

import icom.DBPool;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

public class ResendProcess extends Thread{
	
	public void run(){
		
		while(Sender.processData){
			
			ArrayList<String> arrServices = getArrayReSend();
			for (int i = 0; i < arrServices.size(); i++) {

				String serviceName = arrServices.get(i);

				Util.logger.info("Start Resend - Service = " + serviceName);

				String strSql = "SELECT id,OPTIONS,notcharge,class,result,dayofweek "
						+ " FROM services WHERE "
						+ " services = '"
						+ serviceName + "'";

				try {

					Vector vtServices = DBUtil
							.getVectorTable("gateway", strSql);

					Vector element = (Vector) vtServices.elementAt(0);

					String id = (String) element.elementAt(0);
					String options = (String) element.elementAt(1);
					int notCharge = Integer.parseInt((String) element
							.elementAt(2));
					String className = (String) element.elementAt(3);
					int result = Integer
							.parseInt((String) element.elementAt(4));
					String dayofweek = (String) element.elementAt(5);
					dayofweek = dayofweek.trim();

					if (result != 1) {

						if (dayofweek.equals("x")) {
							continue;
						} else {
							String[] days = dayofweek.split(";");
							Boolean check = false;
							String sToday = Calendar.getInstance().get(
									Calendar.DAY_OF_WEEK)
									+ "";
							
							for (int k = 0; k < days.length; k++) {

								if (sToday.equals(days[k])) {
									check = true;
									break;
								}

							}

							if (check) {
								continue;
							}

						}

					}

					DeliveryManager delegate = null;
					Class delegateClass = Class.forName(className);
					Object delegateObject = delegateClass.newInstance();
					delegate = (DeliveryManager) delegateObject;

					delegate.start(id, serviceName, options, notCharge);

				} catch (Exception e) {
					Util.logger.error("ResendProcess - ERROR: "
							+ e.getMessage());
				}

				

			}// end for i
			
			Util.logger.info("End Resend.");
			
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}// end while
		
	}
	
	private ArrayList<String> getArrayReSend(){
		
		ArrayList<String> arrService = new ArrayList<String>();
		
		Calendar now = Calendar.getInstance();
		String strCurrHour = new java.text.SimpleDateFormat("HH:mm").format(now
				.getTime());

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "resendAllDay";

		
		String sqlQuery = "SELECT COMMAND_CODE,listhours FROM " + tableName
				+ " WHERE listhours like '%" + strCurrHour + "%'" ;

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
					String serviceName = rs.getString("COMMAND_CODE");	
					arrService.add(serviceName);
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
						
		return arrService;
		
	}
	
	

}
