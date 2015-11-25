package vov;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;


import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;



/**
 * Chat VOV hcm class.<br>
 * 
 * <pre>
 * 
 * </pre>
 * 
 * @author Vietnamnet I-Com LoanDT
 * @version 1.0
 */
public class Daugia_cauhoiphu extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			
			String mobileOperator = msgObject.getMobileoperator();
			String commandCode = msgObject.getKeyword();	
			String userID = msgObject.getUserid();			
													
					try
						{
						
						String info =  msgObject.getUsertext();
						String usertext = info.substring(commandCode.length() +1);
						
						int session = getSession();
						Timestamp time = msgObject.getTTimes();
						String time_question = time_question();
						try
						{
							String[] strQuestion1_start = time_question.split(";");
							
							Util.logger.info ("start____" + strQuestion1_start[0]);
							Util.logger.info ("end____" + strQuestion1_start[1]);
							Util.logger.info ("dapan____" + strQuestion1_start[2]);
							
							Timestamp start = Timestamp.valueOf(strQuestion1_start[0]);
							Timestamp end = Timestamp.valueOf(strQuestion1_start[1]);
							String dapan = strQuestion1_start[2];							
							
							String mt3_2 ="Chuc mung ban da tra loi dung cau hoi phu.Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi may man duoc chon khong nhe!DTHT: 1900571566.";
							String mt3_1 ="Rat tiec!Dap an ban chon chua dung.Phan qua hap dan van dang cho ban.Hay nhanh tay soan tin de tro thanh nguoi dau tien doan dung nhe! DTHT:1900571566";							
							String mt3 = "Rat tiec!Dap an ban chon chua dung.Phan qua hap dan van dang cho ban.Hay nhanh tay soan tin de tro thanh nguoi dau tien doan dung nhe! DTHT:1900571566";
							
							boolean flag = false;
							 
							//	dbInsert.saveCustomer(session, user_id, mobile_operator, guess_name, exactly, block, request_id, keyword)(userID, session);
							
							
								if( time.getTime() >= start.getTime())
								{									
								
									if (usertext.equalsIgnoreCase(dapan))
									{										
										flag = true;
										mt3 = mt3_2;
									}
									else
									{
										mt3 = mt3_1;
										flag = false;
									}
								}													
						
								
								if (time.getTime() >= start.getTime())									
								{	
									if (flag == true)
									{	
										saveCauhoiphu(session+"", userID, mobileOperator, usertext , 1, msgObject.getRequestid().toString(), msgObject.getKeyword());
									}
									else
									{
										saveCauhoiphu(session+"", userID, mobileOperator, usertext , 0, msgObject.getRequestid().toString(), msgObject.getKeyword());										
											// Tra MT thong bao ket qua sai									
																			
									}	
									
									msgObject.setUsertext(mt3);
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);	
								}						
						}
						catch (Exception e) {																	
														
							msgObject.setUsertext("Chuong trinh hien khong phat song.Vui long don nghe Dau gia nguoc cac ngay Thu2 den chu nhat hang tuan tren lan song 91 Mhz. DTHT: 1900571566.");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);							
							
							Thread.sleep(1000);
							return null;						
						} finally {	 }									
					}						
						catch (Exception e) {																	
							Util.logger.error ("Info:" + msgObject.getUsertext());							
							msgObject.setUsertext("Tin nhan khong hop le. De tham gia tra loi cau hoi phu cua Daugianguoc soan tin DP <dapan> gui 8751.Chuc ban may man.DTHT 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							
							Thread.sleep(1000);
							return null;						
						} finally {	 }						
					
				return null;
				}
			catch (Exception e) {
					Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
					return null;}
			finally {	}
	}
	
	public static String replaceAllWhiteWithOne(String sInput) {
		String strTmp = sInput.trim();
		String strResult = "";
		for (int i = 0; i < strTmp.length(); i++) {
			char ch = strTmp.charAt(i);
			if (ch == ' ') {
				for (int j = i; j < strTmp.length(); j++) {
					char ch2 = strTmp.charAt(j);
					if (ch2 != ' ') {
						i = j;
						strResult = strResult + ' ' + ch2;
						break;
					}
				}
			} else {
				strResult = strResult + ch;
			}

		}
		return strResult;
	}

	public int getSession(){
		
		int result = 0;		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT phien FROM daugia_phien where active =1 order by id desc limit 1 ";
		
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
					result = rs.getInt(1);				
				}
			} else {
				Util.logger
						.error("dbManager - get session daugia_phien : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("dbManager - get session daugia_phien. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("dbManager - get session daugia_phien. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return result;
	}

	public String time_question(){
		
		String result = null;		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT begintime,endtime,dapan FROM daugia_phien where active =1 order by id desc limit 1";
		//Util.logger.info("sqlQuery___"+ sqlQuery);
		
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
					//Util.logger.info("Toi day");
					result = rs.getString("begintime")+ ";";
					result = result + rs.getString("endtime")+ ";";					
					result = result + rs.getString("dapan")+ ";";
				}
			} else {
				Util.logger
						.error("dbManager - search time for daugia_phien : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("dbManager - search search time for daugia_phien . SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("dbManager - search search time for daugia_phien . SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return result;
	}

	
	public String[] customerTrue(){
		
		String[] result  = new String[2];	
		result[0] ="";
		result[1] ="";
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT user_id,request_id FROM daugia_cauhoiphu where exactly = 1 and status =0 and " +
		"session like (SELECT phien FROM daugia_phien where active =1 order by id desc limit 1) ";
		
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
					result[0] +=  rs.getString("user_id")+";";
					result[1] +=  rs.getString("request_id")+";";
				}
			} else {
				Util.logger
						.error("dbManager - search userID, sesion idol : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("dbManager - search userID, sesion idol. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("dbManager - search userID, sesion idol. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return result;
	}
	//user_id, mobile_operator, guess, exactly, session, request_id, keyword, date_time,  
	public int saveCauhoiphu(String session, String user_id, String mobile_operator,
			String guess_name, int exactly, String request_id,
			String keyword) {

		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;		
	
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				iReturn = -1;
			}

			sqlString = "INSERT INTO daugia_cauhoiphu "
				+ "( session, user_id, mobile_operator, guess, exactly, request_id, keyword) VALUES ('"
				+ session + "','" + user_id + "','" + mobile_operator
				+ "','" + guess_name + "', " + exactly + ",'"
				+ request_id + "','" + keyword + "')";			
			
			statement = connection.prepareStatement(sqlString);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("daugia_cauhoiphut-@@-Insert : Error@userid="
								+ user_id
								+ "\t Sesion"
								+ session );
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis("daugia_cauhoiphu-@@-Insert : Error@userid="
					+ user_id
					+ "\tsession"
					+ session 
					+ "; Error = " + e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis("daugia_cauhoiphu-@@-Insert : Error@userid="
					+ user_id
					+ "\tsession"
					+ session );
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public void UpdateCauhoiphu() {
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();
				
			
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {				
			}

			sqlString = "Update daugia_cauhoiphu"				
					+ " set status = 1 where exactly =1";								
			
			statement = connection.prepareStatement(sqlString);			
			Util.logger.info("sqlString++" + sqlString);
		
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("Error ");			}

		} catch (SQLException e) {
			Util.logger
			.crisis("Error ");					
		} catch (Exception e) {
			Util.logger
			.crisis("Error ");	
		
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}		
	}

}
