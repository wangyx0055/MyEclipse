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
public class couplechat_vovhcm extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			DBSelect_hcm dbSelect = new DBSelect_hcm();
			DBInsert_hcm dbInsert = new DBInsert_hcm();	
			
			String commandCode = msgObject.getKeyword();			
			String userID = msgObject.getUserid();			
				
			if(commandCode.equalsIgnoreCase("CR"))
				{						
					try
						{
						
						String info =  msgObject.getUsertext().substring(commandCode.length() +1);
						// lay nick se nhan duoc loi nhan
						String nickGet = info.substring(0, info.indexOf(" "));		
						// noi dung loi nhan
						String contentGet = info.substring(nickGet.length() +1);
						// lay nick cua sdt da gui loi nhan
						String nickSend = dbSelect.getNick(userID);
						
						// lay 110 ky tu tu noi dung loi nhan
						if (contentGet.length() >110)
							contentGet =  contentGet.substring(0,109);
						
						String contentSend = contentGet;
						String content = "Tin nhan tu " + nickSend + " tren Radio Chat: " + contentGet +" ";
						
						// lay so dien thoai cua nick se nhan duoc tin nhan de gui
						String phoneGet = dbSelect.getUserID(nickGet);
						boolean checkNickNew = dbSelect.checkNick(nickGet);
						boolean checkUserID = dbSelect.checkUser(userID);							
					
						
						if( checkUserID == true)
						{
							if (checkNickNew == true) //  nick duoc nhan co ton tai 
							{
								// lay sesion choi cua UserID
								int sess = dbSelect.getSession();
								dbInsert.insertToVovhcm_chat_couple(userID, nickGet, msgObject.getTTimes().toString(), contentSend,sess);
								dbInsert.insertSms(phoneGet, msgObject.getServiceid(), getMobileOperatorNew(phoneGet,2).toUpperCase(), msgObject.getKeyword(), content);
								msgObject.setUsertext("Tin chat cua ban da duoc gui thanh cong toi " + nickGet + ". Chuc ban som nhan duoc hoi am.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								
								boolean check1 = dbSelect.checkVovhcm_chat_couple(userID);
								if( check1 == false)
								{
									msgObject.setUsertext("Soan tin EBD 2 gui 8751 de tai Game Kim cuong voi nhung vien da nhieu mau sac, de tang cho ban be Soan EBD 2 sodienthoai gui 8751.");
									msgObject.setMsgtype(0);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
																	
								Thread.sleep(1000);			
								return null;
							}
							else
							{
								msgObject.setUsertext("Tin chat cua ban chua duoc gui di do sai nick nguoi nhan. Vui long kiem tra chinh xac va gui lai. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								
								boolean check1 = dbSelect.checkVovhcm_chat_couple(userID);
								if( check1 == false)
								{
									msgObject.setUsertext("De gui den cong dong Radio Chat loi nhan va lam quen voi cac thanh vien khac soan : TR <sodienthoainguoinhan><noidungloinhan> gui 8751.");
									msgObject.setMsgtype(0);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
									
									msgObject.setUsertext("Soan tin EBD 2 gui 8751 de tai Game 'Kim cuong' voi nhung vien da nhieu mau sac, de tang cho ban be Soan EBD 2 sodienthoai gui 8751.");
									msgObject.setMsgtype(0);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
								
								Thread.sleep(1000);			
								return null;
							}
						}
						else
						{
							msgObject.setUsertext("Ban chua dang ky nick chat tren he thong Radio Chat.Soan RC <ten nick toi da 20 ky tu> <gioi tinh(nu)(nam)> <so thich> 8751. DTHT:1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							Thread.sleep(1000);			
							return null;
						}							
					}						
						catch (Exception e) {																	
							Util.logger.error ("Info:" + msgObject.getUsertext());
							
							msgObject.setUsertext("YC ko hop le. De chat voi nick khac, soan: NR <nicknguoinhan> <noidungchat> gui 8751. DTHT: 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							msgObject.setUsertext("Soan tin: LVB gui 8551 de nhan ngay link de choi Game Lam vuon vo cung hap dan.Chi tiet: http://funzone.vn.Chuc ban co giay phut vui ve.");
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
