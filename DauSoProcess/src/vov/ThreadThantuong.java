package vov;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Logger;
import com.vmg.sms.process.MsgObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import services.textbases.LogValues;
import vov.Thantuong_DBInsert;
import vov.Thantuong_DBSelect;



public class ThreadThantuong extends Thread {
	public static Hashtable SECRET_NUMBER = null;
	public boolean LOAD = false;
	public static int isProcess = 0;				
		
		public void run() {
		try {
			while (ConsoleSRV.processData)
				
				try {					
					// Get ngay thang hien tai
					//Util.logger.info("START FORWAD MT .........");					
					
					try
					{
						
						Calendar now = Calendar.getInstance();
	        		 	String Gettime = now.get(Calendar.YEAR) + "-" + FormatNumber(now.get(Calendar.MONTH)+1) + "-" + FormatNumber(now.get(Calendar.DAY_OF_MONTH)) + " " + FormatNumber(now.get(Calendar.HOUR_OF_DAY)) + ":" + FormatNumber(now.get(Calendar.MINUTE)) + ":" + FormatNumber(now.get(Calendar.SECOND));
	        		 	
	        		 	Thantuong_DBSelect dbSelect = new Thantuong_DBSelect();
	        			Thantuong_DBInsert dbInsert = new Thantuong_DBInsert();
	        		 	MsgObject msgObject = new MsgObject();
	        		 	String time_question = dbSelect.time_question();
	        		 	
	        		 	String mt1 = "Chuc mung ban da du doan dung ten than tuong bi mat.Lang nghe MC thong bao de biet ban co phai la nguoi trung thuong cua chuong trinh khong nhe!DTHT:1900571566";
	        		 	String mt2 = "Chuc mung ban da tra loi dung cau hoi phu.Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi dau tien tra loi dung khong nhe!DTHT:1900571566.";
	        		 	
					String[] strQuestion1_start = time_question.split(";");
						
        			Timestamp start = Timestamp.valueOf(strQuestion1_start[0]);
					Timestamp question1_start = Timestamp.valueOf(strQuestion1_start[1]);
					Timestamp question2_start = Timestamp.valueOf(strQuestion1_start[2]);
					Timestamp end = Timestamp.valueOf(strQuestion1_start[3]);							
					Timestamp question1 = Timestamp.valueOf(strQuestion1_start[4]);
					Timestamp question2 = Timestamp.valueOf(strQuestion1_start[5]);
					Timestamp question3 = Timestamp.valueOf(strQuestion1_start[6]);
					Timestamp question4 = Timestamp.valueOf(strQuestion1_start[7]);
					Timestamp question5 = Timestamp.valueOf(strQuestion1_start[8]);
					Timestamp question6 = Timestamp.valueOf(strQuestion1_start[9]);
					
					Timestamp time  = Timestamp.valueOf(Gettime);
					
					if ( time.getTime() >= end.getTime() && time.getTime() <= question5.getTime() ) // 	Tra MT " Ban da tra loi dung than tuong
					{
						String customerTrue = dbSelect.customerTrue();					
						
							if (!customerTrue.equalsIgnoreCase(""))
							{
								String [] arrUS = customerTrue.split(";");
								for (int i= 0 ; i < arrUS.length;i ++)
									{
										msgObject.setServiceid("8751");
										String mopbile = getMobileOperatorNew(arrUS[i], 2);
										msgObject.setMobileoperator(mopbile);
										msgObject.setKeyword("TH");
										msgObject.setUserid(arrUS[i]);
										msgObject.setUsertext(mt1);
										msgObject.setMsgtype(0);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);	
									}
								dbInsert.UpdateToicom_thantuongCustomer();
							}
					}
					else if ( time.getTime() >= question2.getTime()&& time.getTime() <= question3.getTime() )
					{
						String customervoteTrue = dbSelect.customervoteTrue();	
							if (!customervoteTrue.equalsIgnoreCase(""))
							{
								String [] arrUser = customervoteTrue.split(";");
								for (int i= 0 ; i < arrUser.length;i ++)
									{
										String rsDetail = arrUser[i];
										String [] arrTemp =  rsDetail.split("/");
										String user = arrTemp[0];
										String block = arrTemp[1];
										 if (block.equalsIgnoreCase("1"))
										 {
											 	msgObject.setServiceid("8751");
												String mopbile = getMobileOperatorNew(arrUser[i], 2);
												msgObject.setMobileoperator(mopbile);
												msgObject.setKeyword("TH");
												msgObject.setUserid(user);
												msgObject.setUsertext("Chuc mung ban da tra loi dung cau hoi phu thu 1" +
												"Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi dau tien tra loi dung khong nhe!DTHT:1900571566.");
												msgObject.setMsgtype(0);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);
										 }
									}
								dbInsert.UpdateToicom_thantuong_vote(1);
							}
							
						}
					else if ( time.getTime() >= question3.getTime()&& time.getTime() <= question5.getTime() )
					{
						String customervoteTrue = dbSelect.customervoteTrue();	
							if (!customervoteTrue.equalsIgnoreCase(""))
							{
								String [] arrUser = customervoteTrue.split(";");
								for (int i= 0 ; i < arrUser.length;i ++)
									{
										String rsDetail = arrUser[i];
										String [] arrTemp =  rsDetail.split("/");
										String user = arrTemp[0];
										String block = arrTemp[1];
										 if (block.equalsIgnoreCase("2"))
										 {
											 	msgObject.setServiceid("8751");
												String mopbile = getMobileOperatorNew(arrUser[i], 2);
												msgObject.setMobileoperator(mopbile);
												msgObject.setKeyword("TH");
												msgObject.setUserid(user);
												msgObject.setUsertext("Chuc mung ban da tra loi dung cau hoi phu thu 2" +
												"Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi dau tien tra loi dung khong nhe!DTHT:1900571566.");
												msgObject.setMsgtype(0);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);
										 }
									}
								dbInsert.UpdateToicom_thantuong_vote(2);
							}
							
						}
					else if ( time.getTime() >= question4.getTime() && time.getTime() <= question5.getTime() )
					{
						String customervoteTrue = dbSelect.customervoteTrue();	
							if (!customervoteTrue.equalsIgnoreCase(""))
							{
								String [] arrUser = customervoteTrue.split(";");
								for (int i= 0 ; i < arrUser.length;i ++)
									{
										String rsDetail = arrUser[i];
										String [] arrTemp =  rsDetail.split("/");
										String user = arrTemp[0];
										String block = arrTemp[1];
										 if (block.equalsIgnoreCase("3"))
										 {
											 	msgObject.setServiceid("8751");
												String mopbile = getMobileOperatorNew(arrUser[i], 2);
												msgObject.setMobileoperator(mopbile);
												msgObject.setKeyword("TH");
												msgObject.setUserid(user);
												msgObject.setUsertext("Chuc mung ban da tra loi dung cau hoi phu thu 3" +
												"Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi dau tien tra loi dung khong nhe!DTHT:1900571566.");
												msgObject.setMsgtype(0);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);
										 }
									}
								dbInsert.UpdateToicom_thantuong_vote(3);
							}
						}
					else if ( time.getTime() >= question5.getTime() && time.getTime() <= question6.getTime() )
					{
						String customervoteTrue = dbSelect.customervoteTrue();	
							if (!customervoteTrue.equalsIgnoreCase(""))
							{
								String [] arrUser = customervoteTrue.split(";");
								for (int i= 0 ; i < arrUser.length;i ++)
									{
										String rsDetail = arrUser[i];
										String [] arrTemp =  rsDetail.split("/");
										String user = arrTemp[0];
										String block = arrTemp[1];
										 if (block.equalsIgnoreCase("4"))
										 {
											 	msgObject.setServiceid("8751");
												String mopbile = getMobileOperatorNew(arrUser[i], 2);
												msgObject.setMobileoperator(mopbile);
												msgObject.setKeyword("TH");
												msgObject.setUserid(user);
												msgObject.setUsertext("Chuc mung ban da tra loi dung cau hoi phu thu 4" +
												"Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi dau tien tra loi dung khong nhe!DTHT:1900571566.");
												msgObject.setMsgtype(0);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);
										 }
									}
								dbInsert.UpdateToicom_thantuong_vote(4);
							}
						}
					else if ( time.getTime() > question6.getTime() )
					{
						String customervoteTrue = dbSelect.customervoteTrue();	
							if (!customervoteTrue.equalsIgnoreCase(""))
							{
								String [] arrUser = customervoteTrue.split(";");
								for (int i= 0 ; i < arrUser.length;i ++)
									{
										String rsDetail = arrUser[i];
										String [] arrTemp =  rsDetail.split("/");
										String user = arrTemp[0];
										String block = arrTemp[1];
										 if (block.equalsIgnoreCase("5"))
										 {
											 	msgObject.setServiceid("8751");
												String mopbile = getMobileOperatorNew(arrUser[i], 2);
												msgObject.setMobileoperator(mopbile);
												msgObject.setKeyword("TH");
												msgObject.setUserid(user);
												msgObject.setUsertext("Chuc mung ban da tra loi dung cau hoi phu thu 5" +
												"Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi dau tien tra loi dung khong nhe!DTHT:1900571566.");
												msgObject.setMsgtype(0);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);
										 }
									}
								dbInsert.UpdateToicom_thantuong_vote(5);
							}
							
						}

					sleep(1000*60);
					}
										
					catch (Exception e) {						
					Thread.sleep(1000*60);											
					} finally {	 }	
				
				} catch (Exception ex) {
					Util.logger.error("Error Run: " + ex.toString());
				}
		} catch (Exception ex) {
			Util.logger.error("Error Run: " + ex.toString());
		}
	}
		public static String getMobileOperatorNew(String userid, int type) {

			String tmpOperator = "-";
			Connection connection = null;
			DBPool dbpool = new DBPool();
			try {

				connection = dbpool.getConnection("gateway");

				String query = "SELECT operator FROM icom_isdnseries WHERE prefix= substr('"
						+ userid + "',1, length(prefix)) ";
				if (type == 1 || type == 0) {
					query += " and type=" + type;
				}

				Vector result = DBUtil.getVectorTable(connection, query);
				for (int i = 0; i < result.size(); i++) {
					Vector item = (Vector) result.elementAt(i);
					tmpOperator = (String) item.elementAt(0);
				}

				return tmpOperator;
			} catch (Exception ex) {
				Util.logger.sysLog(LogValues.INFORMATIVE, "Utils",
						"getMobileOperator: Get MobileOpereator Failed"
								+ ex.toString());
				return tmpOperator;
			} finally {
				dbpool.cleanup(connection);
			}
		}

		
		  public static String FormatNumber(int i)
		  {
		    try
		    {
		      if (i >= 0 && i < 10)return "0" + i;
		      else return "" + i;
		    }
		    catch (Exception e)
		    {
		      return "";
		    }
		  }
}