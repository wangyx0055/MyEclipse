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
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import services.textbases.LogValues;
import vov.Thantuong_DBInsert;
import vov.Thantuong_DBSelect;



public class ThreadMTCHPDaugia extends Thread {
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
	        		 	
	        		 	Daugia_cauhoiphu DG = new Daugia_cauhoiphu();
	        		 	MsgObject msgObject = new MsgObject();
	        		 	String time_question = DG.time_question();
	        		 	
	        		 	String mt1 = "Chuc mung ban da tra loi dung cau hoi phu.Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi may man duoc chon khong nhe!DTHT: 1900571566.";	        		 	
	        		 	
					String[] strQuestion1_start = time_question.split(";");
						
        			Timestamp start = Timestamp.valueOf(strQuestion1_start[0]);
					Timestamp end = Timestamp.valueOf(strQuestion1_start[1]);
					
					
					Timestamp time  = Timestamp.valueOf(Gettime);
					
					if ( time.getTime() >= start.getTime())
					{
						String[] customerTrue = DG.customerTrue();	
						
						String[] arrUs = customerTrue[0].split(";");
						String [] arrRe = customerTrue[1].split(";");
						
						
							if (!arrUs[0].equalsIgnoreCase(""))
							{								
								for (int i= 0 ; i < arrUs.length;i ++)
									{
										msgObject.setServiceid("8751");
										String mopbile = getMobileOperatorNew(arrUs[i], 2);
										msgObject.setMobileoperator(mopbile);
										msgObject.setKeyword("DP");
										msgObject.setUserid(arrUs[i]);
										
										int req = Integer.parseInt(arrRe[i].toString());
										java.math.BigDecimal bigReq = new java.math.BigDecimal(String.valueOf(req));
										msgObject.setRequestid(bigReq);
										msgObject.setUsertext(mt1);
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);	
									}
								DG.UpdateCauhoiphu();
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