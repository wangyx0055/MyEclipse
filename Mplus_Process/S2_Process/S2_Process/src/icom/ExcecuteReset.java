package icom;

import icom.common.DBUtil;
import icom.common.Util;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;



/**
 * ExcecuteCharging class.<br>
 * 
 * <pre>
 *  Excute insert into table vms_charge for INgw charge money Subcriber
 *  Total All of subcriber in all of table start with mlist_ and charge
 * </pre>
 * 
 * @author Vietnamnet ICom TrungVD
 * @version 2.0
 */
public class ExcecuteReset extends Thread {
	 
	private int timeReset = Integer.parseInt(Constants._prop.getProperty("timereset", "23"));
	private String listTable = "";
	//private String lastTimeRequest = Util.getBeforeOneDay();
	
	Timer timer;
	long repeat = 1000*60;
	
	@Override
	public void run() {		
		listTable = getListTableReset();
		Util.logger.info("list table=" + listTable);
		timer = new Timer();
		schedule();	
	}
	void schedule(){
		timer.schedule(new ExceResetTask(),10000, repeat);
	}
	class ExceResetTask extends TimerTask{
		@Override
		public void run(){		
			if(!Sender.processData) 
			{
				//Util.logger.info("destroy timeer exce reset!");
				timer.cancel();
				return;
			}	
			//Util.logger.info("exce reset chay tiep ko");
			java.util.Calendar calendar = java.util.Calendar.getInstance();			
			int sHour = calendar.get(Calendar.HOUR_OF_DAY);			
			if(sHour==timeReset)
			{
				//reset active
				resetActive(listTable);	
				resetServices();
				resetListSend();
				// Reset Run_Insert_List_Send , DanNd 
				resetRunInsertService();
				//lastTimeRequest = Util.getCurrentYearMonthDay();				
			}	
		}
	}
	private void resetActive(String listTable)
	{
		String[] tableName = listTable.split(",");
		for(String ch:tableName)
		{
			String sql = "Update " + ch + " set active=0";
			try{
				DBUtil.Update(sql);
				Util.logger.info("icom@ExcecuteReset:resetActive: reset table" + ch + " OK!");
			}catch(Exception ex)
			{
				Util.logger.error("icom@ExcecuteReset@resetActive@error when update table" + ch + ",ex=" + ex.getMessage());
				continue;
			}
		}
		Util.logger.info("icom@ExcecuteReset@run: reset "+ listTable +" thanh cong!");
	}
	private void resetServices()
	{
		String sql = "Update services set active=0";
		try{
			DBUtil.Update(sql);
			Util.logger.info("icom@ExcecuteReset:resetServices: reset table services OK!");
		}catch(Exception ex)
		{
			Util.logger.error("icom@ExcecuteReset@resetServices@error when update table services,ex=" + ex.getMessage());			
		}
		Util.logger.info("icom@ExcecuteReset@run: reset services cong!");
	}
	private void resetListSend()
	{	
		String sql = "truncate table list_send";
		try{
			DBUtil.Update(sql);
			Util.logger.info("icom@ExcecuteReset:resetListSend: truncate list_send OK!");
		}catch(Exception ex)
		{
			Util.logger.error("icom@ExcecuteReset@resetServices@error when reset table list send,ex=" + ex.getMessage());			
		}
		Util.logger.info("icom@ExcecuteReset@run: reset list send thanh cong!");
	}
	private String getListTableReset()
	{
		String ret="";
		long milliSecond = System.currentTimeMillis();
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		int sDay = calendar.get(Calendar.DAY_OF_WEEK);
		
		//String sqlSelect = "SELECT options FROM "
		//	+ "services WHERE (dayofweek like '%"
		//	+ sDay
		//	+ "%' or upper(dayofweek)='X')";
		String sqlSelect = "SELECT options FROM "
			+ "services";
		Vector vtService = null;
		
		try {
			vtService = DBUtil.getVectorTable("gateway", sqlSelect);
		//	Util.logger.info("icom@ExcecuteReset@getListTableReset@ SQL:" + sqlSelect);
			
		} catch (Exception e) {
			Util.logger
					.error("icom@ExcecuteReset@getListTableReset@ error when get service names (check table services).ex="
							+ e.getMessage());
			DBUtil.Alert("Process.icom", "ExcecuteReset", "major",
					"ExcecuteReset.Exception(check table services):"
							+ e.toString(), "processAdmin");
		}
		if(vtService != null)
		{
			Hashtable hashtable = new Hashtable();
			
			for (int i = 0; i < vtService.size(); i++) {
				Vector item = (Vector) vtService.elementAt(i);
				String sOptions = (String) item.elementAt(0);
				HashMap _option = new HashMap();
				_option = Util.getParametersAsString(sOptions);
				String sTable = Util.getStringfromHashMap(_option, "mlist", "x");
				
				if(!"x".equalsIgnoreCase(sTable))
				{
					if(!hashtable.containsKey(sTable))
					{
						hashtable.put(sTable, sTable);
						ret += "," + sTable;
					}
				}
			}
		}
		return ret.replaceFirst(",", "");
	}
	
	private boolean resetRunInsertService() {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.error("VMS-ICOM RESET @ExcecuteReset@resetRunInsertService@connection is null.");
				return false;
			}

			// Update
			String sSQL = "UPDATE services SET run_insert_list_send = 0 ";

			Util.logger
					.info("VMS-ICOM RESET @ExcecuteReset@resetRunInsertService@SQL UPDATE: "
							+ sSQL);
			statement = connection.prepareStatement(sSQL);
			if (statement.execute()) {
				Util.logger.error("VMS-ICOM RESET @ExcecuteReset@resetRunInsertService @"
						+" has to sent");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger
					.error("VMS-ICOM RESET @ExcecuteReset@resetRunInsertService@: Error:"
							+ e.toString());
			return false;
		} catch (Exception e) {
			Util.logger
					.error("VMS-ICOM RESET @ExcecuteReset@resetRunInsertService@: Error:"
							+ e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

}
