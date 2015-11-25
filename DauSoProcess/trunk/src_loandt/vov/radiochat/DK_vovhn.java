package vov.radiochat;


import java.sql.Connection;
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
public class DK_vovhn extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			DBSelect_hanoi dbSelect = new DBSelect_hanoi();
			DBInsert_Hanoi dbInsert = new DBInsert_Hanoi();	
			
			String commandCode = msgObject.getKeyword();			
			String userID = msgObject.getUserid();	
			String serviceID = msgObject.getServiceid();
			
				if(commandCode.equalsIgnoreCase("RL")  && serviceID.equalsIgnoreCase("8751"))
				{						
					try
						{		
						String[] info = msgObject.getUsertext().split(" ");						
						String nick = info[1];
						String gioitinh = info[2];
						int startIndex = commandCode.length() + 1 + nick.length() +1 + gioitinh.length() +1;
						String habit = msgObject.getUsertext().substring(startIndex);	
						
						Util.logger.info ("habit:" + habit);
						
						boolean checkUserID = dbSelect.checkUser(userID);	
						boolean checkUN = dbSelect.checkUserNick(userID, nick);					
						boolean checkNick = dbSelect.checkNick(nick);		
						
						
						if(checkUserID == false) // so dien thoai nay chua co nick
							{
								if( checkNick == true) // nick da co
								{														
									msgObject.setUsertext("Nick ban chon da co nguoi su dung. Vui long chon nick khac. Chuc ban co nhung giay phut vui ve cung LoveRadio.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);							
									
									msgObject.setUsertext("De gui qua tang am nhac và nhung loi nhan den nguoi yeu thuong, moi ban goi den tong dai 1900599888.");
									msgObject.setMsgtype(0);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
									
									Thread.sleep(1000);			
									return null;
								}
							 else if (checkUN == false) // Ktra khong co
								{
								 if(nick.length() >20)
								 {
										msgObject.setUsertext("Tin nhan khong hop le. De dky tham gia LoveRadio, soan: RL <ten nick toi da 20 ky tu> <gioi tinh> <so thich> 8751. DTHT:1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										msgObject.setUsertext("Soan tin: LVB gui 8551 de nhan ngay link de choi Game Lam vuon vo cung hap dan.Chi tiet: http://funzone.vn.Chuc ban co giay phut vui ve.");
										msgObject.setMsgtype(0);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										Thread.sleep(1000);			
										return null;
								 }
								 else if (!gioitinh.equalsIgnoreCase(""))
								 {
									 int sex =-1;										
											if (gioitinh.equalsIgnoreCase("nu"))
												sex = 1;
											else if (gioitinh.equalsIgnoreCase("nam"))
												sex =0;			
											Util.logger.info ("sex:" + sex);
											if(sex != 0 && sex !=1)
											{
												msgObject.setUsertext("Tin nhan khong hop le. De dky tham gia LoveRadio, soan: RL <ten nick toi da 20 ky tu> <gioi tinh(nu)(nam)> <so thich> 8751. DTHT:1900571566.");
												msgObject.setMsgtype(1);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);
												
												Thread.sleep(1000);			
												return null;
											}
											else
											{	
												dbInsert.insertToVovhn_chat_users(userID, nick, sex, habit);	
												dbInsert.insert_tbl_love_social(userID.substring(2), nick, sex, habit);
												int id = dbSelect.getID(userID.substring(2));
												msgObject.setUsertext("DK thanh cong. Ma ID cua ban la " + id +". Tiep theo, ban goi len tong dai 1900599888, bam 5 roi lam theo huong dan de thu am loi gioi thieu ve minh");
												msgObject.setMsgtype(1);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);												
																				
												msgObject.setUsertext("Ban co the chat voi nick khac bang cach soan: NR <nickmuonchat> <noidungchat> gui 8751. DTHT: 1900571566.");
												msgObject.setMsgtype(0);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);
												
												msgObject.setUsertext("De la nguoi choi tham gia chuong trinh LoveRadio. Chi can soan tin xac nhan: TD gui 8551. DTHT: 1900571566.");
												msgObject.setMsgtype(0);
												msgObject.setContenttype(0);
												DBUtil.sendMT(msgObject);
												
												Thread.sleep(1000);			
												return null;
											}
								 		}								
									}
								}
						else
						{
							
							 int sex =-1;										
								if (gioitinh.equalsIgnoreCase("nu"))
									sex = 1;
								else if (gioitinh.equalsIgnoreCase("nam"))
									sex =0;			
							dbInsert.UpdateToVovhn_chat_users(userID, nick,sex, habit);
							dbInsert.update_tbl_love_social(userID.substring(2), nick, sex, habit);
							msgObject.setUsertext("Ban da doi lai ten nick thanh cong. Tiep theo, ban goi len tong dai 1900599888, bam 5 roi lam theo huong dan de thu am loi gioi thieu ve minh");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
						}
							}						
						catch (Exception e) {																	
							Util.logger.error ("Info:" + msgObject.getUsertext());
							msgObject.setUsertext("Tin nhan khong hop le. De dky tham gia LoveRadio, soan: RL <ten nick toi da 20 ky tu> <gioi tinh(nu)(nam)> <so thich> 8751. DTHT:1900571566");
							msgObject.setMsgtype(1);
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
