package services.textbases;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.*;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Invalid extends ContentAbstract {
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword )
			throws Exception {
		Collection messages = new ArrayList();
		String reply = "Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu.";
		String mobile_operator = "";
		String mt2 = "Tang ban 3 game online HOT nhat hien nay.DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm";
		mobile_operator = msgObject.getMobileoperator();
		String info = msgObject.getUsertext();		
		String info1 ;
				// lam cau lenh tim kiem trong cai bang coomand code= info .neu not null
		// thi foward mo
		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String[]result=new String[2];
			result=getcomd(info);
			String infosai=result[1];
			String command_code1 = result[0];
			String[] result2=getINV(info);
			if (!command_code1.equalsIgnoreCase("")) {				
				// set command
				info1 = command_code1+info.substring(infosai.length());				
				Util.logger.info(" info1:" + info1);				
			
				msgObject.setKeyword(command_code1);
				msgObject.setUsertext(info1);	
				//insertSms(msgObject);
				insertMO2lottery(msgObject);
				
				msgObject.setKeyword(command_code1);
				msgObject.setUsertext(info);
				insertSms(msgObject);				
				
				//update
				System.out.println(" sleep....");
				Thread.sleep(5000);
				Util.logger.info(" result2:" + result2[0]+";"+result2[1]);		

				
			} else {
				if ("VNM".equalsIgnoreCase(mobile_operator)
						|| "SFONE".equalsIgnoreCase(mobile_operator)) {
					msgObject.setUsertext(reply);
					msgObject.setMsgtype(2);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);

				} else {
					msgObject.setUsertext(reply);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
				}
				
			}
			return null;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static String[] getcomd(String mess) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String result[] = new String[2];
		result[0]="";
		result[1]="";
		
		try {
			cnn = dbpool.getConnectionGateway();
			String sqlcommand = " select command_code,info from keyword_config_inv where (instr( '"
					+ mess  + "',info)>0 ) and if(info in ('SX LAMDONG','XSLAM DONG','XSLAM DONG','SXLAMDONG','SX Q NI','XS QNI','SXQNI'),true,info='"+ mess  +"') ;";
			Util.logger.info("select:" + sqlcommand);
			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			statement = cnn.prepareStatement(sqlcommand);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
				}
			}
			
			return result;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return result;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
	}	
	
	private static String[] getINV(String info) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String result[] = new String[2];
		result[0]="";
		result[1]="";
		
		try {
			cnn = dbpool.getConnectionGateway();
			String sqlcommand = " select command_code,info from keyword_config_inv where info ='" + info + "'" ;
			Util.logger.info("select:" + sqlcommand);
			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			statement = cnn.prepareStatement(sqlcommand);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
				}
			}
			return result;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return result;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
	}		

	public String insertSms(MsgObject msgObject) throws Exception {

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		 Calendar cal =   Calendar.getInstance();	        
	        int monthint1=cal.get(Calendar.MONTH)+1;
			int yearint = cal.get(Calendar.YEAR);	
			String monthint="";
			if 	( monthint1 <10) 
				monthint = "0" + monthint1;
				else
				monthint =  monthint1+ "";	
		String tablename = "sms_receive_log" + yearint + monthint ;
		System.out.println(" SQL tablename:" + tablename);
		sSQLInsert = "insert into " + tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,info,request_id,cpid,CHANNEL_TYPE)"
				+ " values(?,?,?,?,?,?,?,1)";

		try {
			connection = dbpool.getConnectionGateway();
			statement = connection.prepareStatement(sSQLInsert);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, msgObject.getUsertext());			
			statement.setString(6, msgObject.getRequestid().toString());
			statement.setInt(7, 28);
			statement.executeUpdate();
			System.out.println(" SQL ínert:" + sSQLInsert);
			return null;
		} finally {
			dbpool.cleanup(connection);
		}
	}
	public String UpdateSms_receive(String comd, String info) throws Exception {
		String month =null;        	 
        Calendar cal =   Calendar.getInstance();       
        int monthint=cal.get(Calendar.MONTH)+1;
		int year1 = cal.get(Calendar.YEAR);
		if (monthint < 10) 
		month= "0" + monthint;
		else 
			month = monthint+"";
		PreparedStatement statement = null;
		String sqlUpdate = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();		
		String tablename = "sms_receive_log" +  year1 + month;
		sqlUpdate = "update " + tablename
				+ " set command_code  = '" + comd + "' where command_code ='INV' and info ='" + info + "'" ;

		try {
			connection = dbpool.getConnectionGateway();
			statement = connection.prepareStatement(sqlUpdate);				
			statement.executeUpdate();
			System.out.println(" SQL Update:" + sqlUpdate);
			return null;
		} finally {
			dbpool.cleanup(connection);
		}
	}


	public String insertMO2lottery(MsgObject msgObject) throws Exception {

		Util.logger.info("insertMO2VHT:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "vhtcms.sys_dm_queue";
		sSQLInsert = "insert into "
				+ tablename
				+ "(ID,USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,  MESSAGE, RECEIVE_DATE,REQUEST_ID)"
				+ " values(vhtcms.hibernate_sequence.nextval,?,?,?,?,?,sysdate,?)";
		try {
			connection = dbpool.getConnection("vhtoracle");
			statement = connection.prepareStatement(sSQLInsert);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, msgObject.getUsertext());
			statement.setBigDecimal(6, msgObject.getRequestid());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("insertMO2VHT:" + msgObject.getUserid() + ":"
						+ msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return "-1";
			}
			statement.close();
			return "1";
		} catch (SQLException e) {
			Util.logger.error("insertMO2VHT:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sys_dm_queue:" + e.toString());
			return "-1";
		} catch (Exception e) {
			Util.logger.error("insertMO2VHT:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sys_dm_queue:" + e.toString());
			return "-1";
		}

		finally {
			dbpool.cleanup(connection);

		}

	}

}
