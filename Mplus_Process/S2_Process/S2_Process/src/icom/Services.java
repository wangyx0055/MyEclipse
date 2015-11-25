package icom;

import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import java.sql.Timestamp;;

/*******
 * 2010-11-11: PhuongDT
 * Object Services phuc vu trong viec lay thong tin cau hinh dich vu free
 * *****/
public class Services {
	public static Vector vtService= new Vector();
	private String service_name;
	private Timestamp from_date_free;
	private Timestamp to_date_free;
	private int number_free;
	private int active_free;
	public static boolean loaded = false;
	public String getServiceName()
	{
		return this.service_name;
	}
	public void setServiceName(String serviceName)
	{
		this.service_name = serviceName;
	}
	public Timestamp getFromDateFree()
	{
		return this.from_date_free;
	}
	public void setFromDateFree(Timestamp fromDateFree)
	{
		this.from_date_free = fromDateFree;
	}
	public Timestamp getToDateFree()
	{
		return this.to_date_free;
	}
	public void setToDateFree(Timestamp toDateFree)
	{
		this.to_date_free = toDateFree;
	}
	public int getNumberFree()
	{
		return this.number_free;
	}
	public void setToDateFree(int numberFree)
	{
		this.number_free = numberFree;
	}
	public int getActiveFree()
	{
		return this.active_free;
	}
	public void setActiveFree(int activeFree)
	{
		this.active_free = activeFree;
	}
	public static Hashtable getInfo() throws Exception {

		String query = "select * from services";

		Util.logger.info("icom.Services: getInfo: select:\t " + query);

		DBPool dbpool = new DBPool();
		
		Hashtable hServices = new Hashtable();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbpool.getConnectionGateway();
			if(connection==null)
			{ 	
				Util.logger.info("bi null" );
				return new Hashtable();
			}
			stmt = connection.prepareStatement(query);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					Services temp= new Services();
					temp.service_name = rs.getString("services");
					temp.from_date_free = rs.getTimestamp("from_date_free");
					temp.to_date_free = rs.getTimestamp("to_date_free");
					temp.number_free = rs.getInt("number_free");
					temp.active_free = rs.getInt("active_free");					
					hServices.put(temp.service_name, temp);	
					vtService.addElement(temp.service_name);
				}
			}
		} catch (Exception ex3) {
			Util.logger.error("Load services. Ex3:" + ex3.toString());
			DBUtil.Alert("Process.LoadKeyword", "icom.Services: getInfo",
					"major", "icom.Services: getInfo: Ex:" + ex3.toString(),
					"processAdmin");
			loaded = false;
		}
		finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			dbpool.cleanup(connection);			
		}	
		if(hServices.size() >0) loaded = true;
		return hServices;
	}
	
	public static icom.Services getService(String serviceName, Hashtable services) throws Exception {
		Vector vtService = icom.Services.vtService;
		
		for (Iterator it = vtService.iterator(); it.hasNext();) {
			String sKey = (String) it.next();
			if(sKey.equalsIgnoreCase(serviceName) || sKey.startsWith(serviceName))
			{
				return (icom.Services) services.get(sKey);				
			}
		}		
		return new icom.Services();		
	}
}
