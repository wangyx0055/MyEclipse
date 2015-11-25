package vov.radiochat;


import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Vector;
import services.textbases.LogValues;

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
public class Agreed_vovhn extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			DBSelect_hanoi dbSelect = new DBSelect_hanoi();
			DBInsert_Hanoi dbInsert = new DBInsert_Hanoi();	
			
			String commandCode = msgObject.getKeyword();			
			String userID = msgObject.getUserid();	
			boolean checkU = dbSelect.checkUser(userID);
			
			  Calendar now = Calendar.getInstance();
		        int year = now.get(Calendar.YEAR);
		        int month = now.get(Calendar.MONTH) + 1;
		        int date = now.get(Calendar.DAY_OF_MONTH);
		        int hour = now.get(Calendar.HOUR_OF_DAY);
		        int minutes = now.get(Calendar.MINUTE);
		        int second = now.get(Calendar.SECOND);
		        String dateNow = "";
		        
		        dateNow =now.get(Calendar.YEAR) + "-" + Utils.FormatNumber(month) + "-" +
		        		 Utils.FormatNumber(date) +  " " +  Utils.FormatNumber(hour) + ":" +  Utils.FormatNumber(minutes)+ ":" +  Utils.FormatNumber(second) ;
				
			if(commandCode.equalsIgnoreCase("TD"))
				{
					if(checkU == true)
					{
						dbInsert.UpdateToVovhn_chat_users(userID,dateNow);
						//dbInsert.update_tbl_Love_Social(userID, dateNow);
						msgObject.setUsertext("Ban da xac nhan la nguoi choi cua chuong trinh hom nay. Vui long don nghe tren LoveRadio. Chuc ban co nhung giay phut vui ve cung chuong trinh.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						Thread.sleep(1000);			
						return null;
					}
					else
					{
						msgObject.setUsertext("Ban chua dk nick chat tren he thong Radio Love. Soan: RL <ten nick> <gioi tinh> <so thich> gui 8751 de dang ki nick, tham gia Chat cung ban be. DTHT:1900571566.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						Thread.sleep(1000);			
						return null;
					}
				
				}
				return null;
				}
			catch (Exception e) {
					Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
					return null;}
			finally {	}
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

	
}
