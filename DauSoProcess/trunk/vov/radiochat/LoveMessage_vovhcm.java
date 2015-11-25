package vov.radiochat;


import java.sql.Connection;
import java.sql.Timestamp;
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
public class LoveMessage_vovhcm extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			DBSelect_hcm dbSelect = new DBSelect_hcm();
			DBInsert_hcm dbInsert = new DBInsert_hcm();	
			
			String commandCode = msgObject.getKeyword();			
			String userID = msgObject.getUserid();		
			boolean checkUserID = dbSelect.checkUser(userID);	
			
				
			if(commandCode.equalsIgnoreCase("TR"))
				{	
				try{
					//boolean time = dbSelect.checkTime(msgObject.getTTimes().toString());
					String info =  msgObject.getUsertext().substring(commandCode.length() +1);
					String phone = info.substring(0, info.indexOf(" "));
					// Cat lay 30 ky tu
					if(phone.length() >30)
						phone = phone.substring(0,29);								
					String content = info.substring(phone.length() +1);
					//Cat lay 150 ky tu
					if(phone.length() >150)
						content = content.substring(0,149);
								
					 if(checkUserID == false)// chua co nick chat
					{
						msgObject.setUsertext("Ban chua dang ky nick chat tren he thong Radio Chat.De dky tham gia, soan:RC <ten nick toi da 20 ky tu> <gioi tinh(nu)(nam)> <so thich> 8751.DTHT:1900571566.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						msgObject.setUsertext("Ban co the chat voi nick khac bang cach soan: CR <nicknguoinhan> <noidungchat> gui 8751.");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						msgObject.setUsertext("Soan tin EBB 1 gui 8751 de tai Game 'Khung long ban trung' vo cung hap dan, de tang cho ban be soan EBB 1 sodienthoai gui 8751");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						Thread.sleep(1000);			
						return null;
					}
					else
					{
						// get session cua chuong trinh ung voi thoi gian nhan MO
						int sess = dbSelect.getSession();
						Util.logger.info("sess +=" + sess);
						dbInsert.insertToVovhcm_chat_single(userID, phone, content, sess);
						msgObject.setUsertext("Ban da gui tin thanh cong toi chuong trinh.Vui long don nghe tren Radio Chat.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						boolean check1 = dbSelect.checkVovhcm_chat_couple(userID);
						Util.logger.info("check1 +=" + check1);
						if(check1 == false)
						{
							msgObject.setUsertext("Ban co the chat voi nick khac bang cach soan: CR <nickmuonchat> <noidungchat> gui 8751. DTHT: 1900571566");
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							msgObject.setUsertext("Soan tin EBB 1 gui 8751 de tai Game 'Khung long ban trung' vo cung hap dan, de tang cho ban be soan EBB 1 sodienthoai gui 8751");
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
						}									
															
						Thread.sleep(1000);			
						return null;
					}
				}
				catch (Exception e) {																	
					Util.logger.error ("Info:" + msgObject.getUsertext());
					msgObject.setUsertext("Tin ban gui khong hop le. De gui loi nhan qua Radio Chat, soan : TR <sdtnhan> <loinhan> gui 8751.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);		
					
					msgObject.setUsertext("Tang ban 3 game online HOT nhat hien nay.DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);								
					
					Thread.sleep(1000);
					return null;						
				} finally {	 }								
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
