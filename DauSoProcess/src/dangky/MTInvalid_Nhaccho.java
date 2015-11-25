package dangky;

import java.util.Calendar;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ConsoleSRV;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class MTInvalid_Nhaccho extends Thread {
	//public static Hashtable SECRET_NUMBER = null;
	public boolean LOAD = false;
	public static int isProcess = 0;

	public String[] getQueueINV(){
		
		String timeReply = "30";
		timeReply = Constants._prop.getProperty("time", timeReply);
		
		String startTime = "8";
		String endTime = "21";	
		startTime = Constants._prop.getProperty("startTime", startTime);
		endTime = Constants._prop.getProperty("endTime", endTime);
		
		int hour =0;
		 Calendar now = Calendar.getInstance();
		 hour = now.get(Calendar.HOUR_OF_DAY);
		
		
		String[] result = new String[5];
		result[0] = "";
		result[1] = "";
		result[2] = "";
		result[3] = "";
		result[4] = "";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sSQLSelect = null;
		if(hour >= Integer.parseInt(startTime) && hour <= Integer.parseInt(endTime))
		 {
			sSQLSelect = " Select id,user_id, service_id,MOBILE_OPERATOR, COMMAND_CODE from sms_receive_queue_inv  "
				+ " where receive_date  < (NOW() - INTERVAL " + Integer.parseInt(timeReply) + " MINUTE)" ;
			
			}
		else
		 {
			sSQLSelect = " Select id,user_id, service_id,MOBILE_OPERATOR, COMMAND_CODE from sms_receive_queue_inv  ";
				
		 }
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sSQLSelect,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					result[0] += rs.getString(1) + ";";
					result[1] += rs.getString(2) + ";";
					result[2] += rs.getString(3) + ";";
					result[3] += rs.getString(4) + ";";
					result[4] += rs.getString(5) + ";";
					//System.out.println("1111:+++" + result[0]);
					//System.out.println("2222:+++" + result[1]);
					//System.out.println("3333:+++" + result[2]);
				}
			} else {
				Util.logger
						.error("dbManager - getAuthor : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("dbManager - getAuthor. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("dbManager - getAuthor. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return result;
	}

	public String insertSms(String userid, String serviceid, String operator ,String command_code ) throws Exception {
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String tablename = "ems_send_queue";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR,COMMAND_CODE, INFO,MESSAGE_TYPE,CPID,CONTENT_TYPE)"
				+ " values(?,?,?,?,?,1,26,0)";
		try {
			// chuoi tra tin nhan dang sms
			
			String info2Client = "";
			if ("viettel".equalsIgnoreCase(operator)) {
				info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 1221.Buoc2, soan: BH maso gui 1221.";
			} else if ("GPC".equalsIgnoreCase(operator)) {
				info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 9194.Buoc2, soan: TUNE maso gui 9194.";
			} else if ("VMS".equalsIgnoreCase(operator)) {
				info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 9224.Buoc2, soan: CHON maso gui 9224.";
			} else if ("beeline".equalsIgnoreCase(operator)) {
				info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat, soan: CHON masobaihat gui 1221.";
			}

			//Insert vao bang ems_send_queue	
				connection = dbpool.getConnectionGateway();				
				statement = connection.prepareStatement(sSQLInsert);
				statement.setString(1, userid);
				statement.setString(2,serviceid);
				statement.setString(3, operator);
				statement.setString(4, command_code);
				statement.setString(5, info2Client);
				statement.executeUpdate();
				Util.logger.info(" SQL isnert:" + sSQLInsert);
			return null;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
			
		}
	}
	
	
	public String Delete(int id) throws Exception {
		PreparedStatement statement = null;
		String sSQLDelete = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		
		if (connection == null) {
			connection = dbpool.getConnectionGateway();			
		}
		
		sSQLDelete = "DELETE FROM sms_receive_queue_inv " 
		+ " WHERE id=?";
		try {
			statement = connection.prepareStatement(sSQLDelete);
			statement.setInt(1, id);
			statement.executeUpdate();
			Util.logger.info(" sSQLDelete:" + sSQLDelete);			

			return null;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	} 

	public void run() {
		try {
			
			while (ConsoleSRV.processData)						
				try {
					
					String timeReply = "30";
					timeReply = Constants._prop.getProperty("time", timeReply);
					
					String startTime = "8";
					String endTime = "21";	
					startTime = Constants._prop.getProperty("startTime", startTime);
					endTime = Constants._prop.getProperty("endTime", endTime);
					
					int hour =0;
					 Calendar now = Calendar.getInstance();
					 hour = now.get(Calendar.HOUR_OF_DAY);
					
					 
					String[] rsRecord = getQueueINV();
					
					
					//System.out.println("Get data .. ");
					String[] arrID = rsRecord[0].split(";");		
					String[] arrUserID = rsRecord[1].split(";");
					String[] arrService = rsRecord[2].split(";");
					String[] arrOperator = rsRecord[3].split(";");
					String[] arrCommandcode = rsRecord[4].split(";");
					int i=0;
					
					if(hour >= Integer.parseInt(startTime) && hour < Integer.parseInt(endTime))
					 {
						
						//System.out.println("1111111111");
						if(!arrID[0].equalsIgnoreCase(""))
						{	
						for (i=0;i<= arrID.length -1;i++)
						{
							insertSms(arrUserID[i], arrService[i], arrOperator[i] ,arrCommandcode[i]);
							Delete(Integer.parseInt(arrID[i]));														
						}
							Thread.sleep(1000*60*Integer.parseInt(timeReply));
						}
					 }
					else
					 {
						if(!arrID[0].equalsIgnoreCase(""))
						{
						for (i=0;i<= arrID.length -1;i++)
						{
							insertSms(arrUserID[i], arrService[i], arrOperator[i] ,arrCommandcode[i]);
							Delete(Integer.parseInt(arrID[i]));														
						}
							Thread.sleep(1000*60*2);
						}	
					 }						
					} catch (Exception ex) {
					Util.logger.error("Error Run: " + ex.toString());
				}				
		} catch (Exception ex) {
			Util.logger.error("Error Run: " + ex.toString());
		}
	}
}