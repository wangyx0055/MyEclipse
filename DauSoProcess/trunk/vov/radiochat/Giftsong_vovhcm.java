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
public class Giftsong_vovhcm extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			DBSelect_hcm dbSelect = new DBSelect_hcm();
			DBInsert_hcm dbInsert = new DBInsert_hcm();	
			
			String commandCode = msgObject.getKeyword();			
			String userID = msgObject.getUserid();		
			boolean checkUserID = dbSelect.checkUser(userID);	
			
				
			if(commandCode.equalsIgnoreCase("QH"))
				{	
				try{
					String info = "";
					String songname= "";
					String temp  = "";
					String phone = "";
					String content ="";
					
					if(msgObject.getUsertext().substring(commandCode.length(),commandCode.length()+1).equalsIgnoreCase(" "))
					{
						 info =  msgObject.getUsertext().substring(commandCode.length() +1);
					}
					else
					{
					 info =  msgObject.getUsertext().substring(commandCode.length());
					}
					
					 songname = info.substring(0, info.indexOf(" "));
					 temp  = info.substring(songname.length() +1);
					 phone = temp.substring(0, temp.indexOf(" "));
					 content = temp.substring(phone.length() +1);
					 
					Util.logger.error ("songname:" +songname);
					Util.logger.error ("temp:" +temp);
					Util.logger.error ("phone:" +phone);
					Util.logger.error ("content:" +content);
					
					if( checkUserID == true)
					{
							// lay sesion choi cua UserID
							int sess = dbSelect.getSession();
							dbInsert.insertToVovhcm_chat_giftsong(userID, phone, songname, content, sess);
							msgObject.setUsertext("Ban da gui tin thanh cong toi chuong trinh.Vui long don nghe tren Radio Chat.");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							boolean check1 = dbSelect.checkVovhcm_chat_single(userID);
							if( check1 == false)
							{
								msgObject.setUsertext("De gui den cong dong Radio Chat loi nhan va lam quen voi cac thanh vien khac soan : TR <sdtnhan> <noidungloinhan> gui 8551.");
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								
								msgObject.setUsertext("Soan tin EBD 2 gui 8751 de tai Game 'Kim cuong' voi nhung vien da nhieu mau sac, de tang cho ban be Soan EBD 2 sodienthoai gui 8751.");
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
							}
							boolean check2 = dbSelect.checkVovhcm_chat_couple(userID);
							if( check2 == false)
							{
								msgObject.setUsertext("De chat voi nick khac, soan: CR <nicknguoinhan> <noidungchat> gui 8751. DTHT: 1900571566.");
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);	
							}
																
							Thread.sleep(1000);			
							return null;	
					}
					else
					{
						msgObject.setUsertext("Ban chua dang ky nick chat tren he thong Radio Chat. Soan: RC <ten nick toi da 20 ky tu> <gioi tinh(nu)(nam)> <so thich> 8751 de tham gia chat cung ban be. DTHT:1900571566");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						msgObject.setUsertext("Ban co the chat voi nick khac bang cach soan: CR <nickmuonchat>  <noidungchat> gui 8751. DTHT: 1900571566");
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
				}
				catch (Exception e) {																	
					Util.logger.error ("Info:" + msgObject.getUsertext());
					msgObject.setUsertext("Tin nhan cua ban khong hop le. De gui tang bai hat va gui loi nhan qua Radio Chat, soan : QH <tenbaihatvietlien> <sdtnhan> <loinhan> gui 8751.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);		
					
					msgObject.setUsertext("Soan tin: LVB gui 8551 de nhan ngay link de choi Game Lam vuon vo cung hap dan.Chi tiet: http://funzone.vn.Chuc ban co giay phut vui ve");
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
