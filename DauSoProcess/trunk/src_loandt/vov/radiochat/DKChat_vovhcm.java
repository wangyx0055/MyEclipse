package vov.radiochat;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import com.vmg.soap.mo.sendXMLRING;

import cs.ExecuteADVCR;

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
public class DKChat_vovhcm extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			DBSelect dbSelect = new DBSelect();
			DBInsert dbInsert = new DBInsert();			
			
			String commandCode = msgObject.getKeyword();
			String serviceID = msgObject.getServiceid();
			String userID = msgObject.getUserid();	
			String nick = msgObject.getUsertext().replace(" ", "");
			
			boolean checkUserID = dbSelect.checkUser(userID);	
			
			nick = nick.substring(commandCode.length());
			boolean checkUN = dbSelect.checkUserNick(userID, nick);					
			boolean checkNick = dbSelect.checkNick(nick);		
			// check  subscription cmmandCode = RC and ServiceID = 8751
			nick = nick.replace(" ","");
				if(commandCode.equalsIgnoreCase("RC") && !nick.equalsIgnoreCase("")  )
				{	
					if(serviceID.equalsIgnoreCase("8751"))
					{
						try
						{							
							if(checkUserID == false) // so dien thoai nay chua co nick
							{
								if( checkNick == true) // nick da co
								{														
									msgObject.setUsertext("Nick ban chon da co nguoi su dung. Vui long chon nick khac. Chuc ban co nhung giay phut vui ve cung Radio Chat.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);							
									
									msgObject.setUsertext("De gui den cong dong Radio Chat loi nhan va lam quen voi cac thanh vien khac soan: TR <nicknhan><noidungloinhan> gui 8551.");
									msgObject.setMsgtype(0);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
									
									msgObject.setUsertext("Ban co the chat voi nick khac bang cach soan:CR <nickmuonchat>  <noidungchat> gui 8751.DTHT:1900571566");
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
										msgObject.setUsertext("Tin nhan khong hop le. De tham gia Radio Chat, B1: soan: RC <nick_toida20kytu> gui 8751. B2: soan: RC <gioitinh> <sothich> gui 8151. DTHT:1900571566");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										msgObject.setUsertext("Soan tin: LVB gui 8551 de nhan ngay link de choi Game Lam vuon vo cung hap dan.Chi tiet: http://funzone.vn.Chuc ban co giay phut vui ve");
										msgObject.setMsgtype(0);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										Thread.sleep(1000);			
										return null;
								 }
								 else
								 {
										dbInsert.insertToVovhcm_chat_users(userID,nick);										
										msgObject.setUsertext("TK " + nick +"  da dky thanh cong. Hay gui them thong tin de tang co hoi thanh nguoi choi chinh, "
										+ "soan: RC <gioitinh> <sothich> gui 8151. DTHT: 1900571566");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										Thread.sleep(1000);			
										return null;		
								 }
								}
							}
							else
							{
								 if(nick.length() >20)
								 {
										msgObject.setUsertext("Tin nhan khong hop le. De tham gia Radio Chat, B1: soan: RC <nick_toida20kytu> gui 8751. B2: soan: RC <gioitinh> <sothich> gui 8151. DTHT:1900571566");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										Thread.sleep(1000);			
										return null;
								 }
								 else
								 {
									 dbInsert.UpdateToVovhcm_chat_users(userID, nick);								
										msgObject.setUsertext("Ban vua cap nhat thanh cong nick moi. DTHT: 1900571566");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										Thread.sleep(1000);			
										return null;
								 }											
							}
						}
						catch (Exception e) {																	
							Util.logger.error ("Info:" + msgObject.getUsertext());
							msgObject.setUsertext("Tin nhan khong hop le. De tham gia Radio Chat, B1: soan: RC <nick_toida20kytu> gui 8751. B2: soan: RC <gioitinh> <sothich> gui 8151. DTHT:1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							Thread.sleep(1000);
							return null;						
						} finally {	 }						
					}
					else if(serviceID.equalsIgnoreCase("8151"))
					{						
						if(checkUserID == false )
						{
							msgObject.setUsertext("Ban chua dang ky tham gia cong dong Radio Chat. Soan: RC nick gui 8751 de dky nick va tham gia cung ban be. DTHT: 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							msgObject.setUsertext("De chat rieng voi nick khac tren Radio Chat, soan: CR nicknguoiay noidung gui 8751. DTHT: 1900571566");
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							Thread.sleep(1000);			
							return null;
						}
						else
						{
							int sex =-1;
							try{
								String info =  msgObject.getUsertext().substring(commandCode.length() +1);
								String gioitinh = info.substring(0, info.indexOf(" "));							
								String habit = info.substring(gioitinh.length() +1);
								
								
								if(gioitinh.equalsIgnoreCase("nu"))
									sex = 1;
								else if (gioitinh.equalsIgnoreCase("nam"))
									sex =0;			
								
								if(sex != 0 && sex !=1)
								{
									msgObject.setUsertext("Tin nhan khong hop le. De tham gia Radio Chat, B1: soan: RC <nick_toida20kytu> gui 8751. B2: soan: RC <gioitinh> <sothich> gui 8151. DTHT:1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
									
									Thread.sleep(1000);			
									return null;
								}
								else
								{
								dbInsert.UpdateToVovhcm_chat_users(userID,sex, habit);								
								
								msgObject.setUsertext("Thong tin cua ban da duoc gui toi Radio Chat. Chuc ban may man tro thanh nguoi choi chinh cua chuong trinh. Vui long don nghe tren Radio Chat.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
															
								msgObject.setUsertext("De chat rieng voi nick khac tren Radio Chat, soan: CR nicknguoiay noidung gui 8751. DTHT: 1900571566");
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								
								Thread.sleep(1000);			
								return null;
								}
							}
							catch (Exception e) {																	
								Util.logger.error ("Info:" + msgObject.getUsertext());
								msgObject.setUsertext("Tin nhan khong hop le. De tham gia Radio Chat, B1: soan: RC <nick_toida20kytu> gui 8751. B2: soan: RC <gioitinh> <sothich> gui 8151. DTHT:1900571566");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								
								msgObject.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu.");
								msgObject.setMsgtype(0);
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
					}				
				}
				else
				{								
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
								
								/*if(time == false) // check thoi gian  phat song chuong trinh
								{
									msgObject.setUsertext("Yeu cau khong hop le, hien tai chuong trinh Radio Chat khong phat song. Vui long lien he tong dai 1900571566 de biet them thong tin chi tiet.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
									
									boolean check1 = dbSelect.checkVovhcm_chat_single(userID);
									if(check1 == false)
									{
										msgObject.setUsertext("Ban co the chat voi nick khac bang cach soan: CR <nickmuonchat>  <noidungchat> gui 8751. DTHT: 1900571566");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										msgObject.setUsertext("Soan tin EBB 1 gui 8751 de tai Game 'Khung long ban trung' vo cung hap dan, de tang cho ban be soan EBB 1 sodienthoai gui 8751");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
									}									
									
									
									Thread.sleep(1000);			
									return null;
								}	*/					
								 if(checkUserID == false)// chua co nick chat
								{
									msgObject.setUsertext("Ban chua dang ky nick chat tren he thong Radio Chat. Soan: RC <nick_toida20kytu> gui 8751 de dang ki nick, tham gia Chat cung ban be. DTHT: 1900571566.");
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
										msgObject.setUsertext("Ban co the chat voi nick khac bang cach soan: CR <nickmuonchat>  <noidungchat> gui 8751. DTHT: 1900571566");
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
					}
					if(commandCode.equalsIgnoreCase("QM"))
					{		
							try{
								boolean time = dbSelect.checkTime(msgObject.getTTimes().toString());
								String stt =  msgObject.getUsertext().substring(commandCode.length() +1);
								String getNick = dbSelect.getNick(userID);
								
								if(time == false) // check thoi gian  phat song chuong trinh
								{
									msgObject.setUsertext("Hien tai chuong trinh Radio Chat khong phat song. Ban chat voi cac nick khac bang cach soan tin: CR <nickmuonchat>  <noidungchat> gui 8751. DTHT: 1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
									
									boolean check1 = dbSelect.checkVovhcm_chat_single(userID);
									if(check1 == false)
									{
										msgObject.setUsertext("Ban co the chat voi nick khac bang cach soan: CR <nickmuonchat>  <noidungchat> gui 8751. DTHT: 1900571566");
										msgObject.setMsgtype(0);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
									}
									
									Thread.sleep(1000);			
									return null;
								}						
								else if(checkUserID == false)// chua co nick chat
								{
									msgObject.setUsertext("Ban chua dang ky nick chat tren he thong Radio Chat. Soan: RC <nick_toida20kytu>  gui 8751 de dang ky nick, tham gia Chat cung ban be. DTHT: 1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
																									
									Thread.sleep(1000);			
									return null;
								}
								else
								{
									// get session cua chuong trinh ung voi thoi gian nhan MO
									int sess = dbSelect.getSession();
									dbInsert.insertToVovhcm_chat_chooseplayer(userID, getNick, Integer.parseInt(stt), msgObject.getTTimes().toString(), sess);
									msgObject.setUsertext("Tin nhan cua ban da gui thanh cong. Chuc ban may man tro thanh nguoi choi cua chuong trinh. Vui long don nghe ket qua tren Radio Chat.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
									
									Thread.sleep(1000);			
									return null;
								}
							}
							catch (Exception e) {																	
								Util.logger.error ("Info:" + msgObject.getUsertext());
								msgObject.setUsertext("Yc ko hop le. De chon nguoi choi, soan: QM <sothutu> gui 8751. Hay thao tac nhanh de tro thanh nguoi choi truc tiep cung Radio Chat. DTHT:1900571566.");
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
					else if (commandCode.equalsIgnoreCase("CR"))
					{	
							try{
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
								String content = "Tin nhan tu " + nickSend + " tren RadioChat: " + contentGet +" ";
								
								// lay so dien thoai cua nick se nhan duoc tin nhan de gui
								String phoneGet = dbSelect.getUserID(nickGet);
								boolean checkNickNew = dbSelect.checkNick(nickGet);
							
								
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
											msgObject.setUsertext("De gui den cong dong Radio Chat loi nhan va lam quen voi cac thanh vien khac soan : TR <nicknhan><noidungloinhan> gui 8551.");
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
									else
									{
										msgObject.setUsertext("Tin chat cua ban chua duoc gui di do sai nick nguoi nhan. Vui long kiem tra chinh xac va gui lai. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										boolean check1 = dbSelect.checkVovhcm_chat_couple(userID);
										if( check1 == false)
										{
											msgObject.setUsertext("De gui den cong dong Radio Chat loi nhan va lam quen voi cac thanh vien khac soan : TR <nicknhan><noidungloinhan> gui 8551.");
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
									msgObject.setUsertext("Ban chua dang ky nick chat tren he thong Radio Chat. Soan: RC <nick_toida20kytu> gui 8751 de dang ki nick, tham gia Chat cung ban be. DTHT: 1900571566.");
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
								//Util.logger.error ("Info:" + msgObject.getUsertext());
								msgObject.setUsertext("Yeu cau khong hop le. De chat voi nick khac, soan: CR <nicknguoinhan> <noidungchat> gui 8751. DTHT: 1900571566.");
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
					/*else if (commandCode.equalsIgnoreCase("VT"))
					{	
						try{
								Util.logger.info("time revice ++ " +msgObject.getTTimes().toString());
								boolean time = dbSelect.checkTime(msgObject.getTTimes().toString());
								String info =  msgObject.getUsertext().substring(commandCode.length() +1);
								Util.logger.info("info:++" +info);
								int stt = 0;
								stt = Integer.parseInt(info.toString());
								Util.logger.info("stt:++" +stt);
								Util.logger.info("time:++" +time);
								
								if( time == true)
								{
									if (stt >=1 && stt <=4) //  nick duoc nhan co ton tai 
									{
										// lay sesion choi cua UserID
										int sess = dbSelect.getSession();
										dbInsert.insertToVovhcm_chat_voting(userID, stt, msgObject.getTTimes().toString(), sess);
										msgObject.setUsertext("Cam on ban da Vote cho cap doi so " + stt +". Chuc ban may man tro thanh khan gia du doan nhanh va chinh xac nhat. Vui long theo doi ket qua tren Radio Chat");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
										
										boolean check1 = dbSelect.checkVovhcm_chat_couple(userID);
										if( check1 == false)
										{
											msgObject.setUsertext("De gui den cong dong Radio Chat loi nhan va lam quen voi cac thanh vien khac soan : TR <nicknhan><noidungloinhan> gui 8551.");
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
								else{
									msgObject.setUsertext("So thu tu cap doi ban da chon khong ton tai. Vui long kiem tra va vote lai voi cu phap: VT <Sothutucapdoi> gui 8751. STT tu 1 den 4. DTHT: 1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);								
									
									Thread.sleep(1000);
									return null;
								}
								}
								else
								{
									msgObject.setUsertext("Thoi gian vote cua ban khong hop le. Vui long kiem tra lai.  DTHT: 1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);	
									
									boolean check1 = dbSelect.checkVovhcm_chat_couple(userID);
									if( check1 == false)
									{
										msgObject.setUsertext("De gui den cong dong Radio Chat loi nhan va lam quen voi cac thanh vien khac soan: TR <nicknhan><noidungloinhan> gui 8551.");
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
							catch (Exception e) {																	
								//Util.logger.error ("Info:" + msgObject.getUsertext());
								msgObject.setUsertext("Tin cua ban chua hop le. De vote, soan: VT <Sothutucapdoi> gui 8551. STT tu 1 den 4. DTHT: 1900571566.");
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
					} */	
					else if (commandCode.equalsIgnoreCase("QH"))
					{	
						try{
								String info = "";
								String songname= "";
								String temp  = "";
								String phone = "";
								String content ="";
								
								//Util.logger.info(" Inffo:" + commandCode);
								//Util.logger.info(" Inffo:" + commandCode.length());
								
								//Util.logger.info(" Inffo:" + msgObject.getUsertext().substring(commandCode.length()));
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
											msgObject.setUsertext("De gui den cong dong Radio Chat loi nhan va lam quen voi cac thanh vien khac soan : TR <nicknhan><noidungloinhan> gui 8551.");
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
									msgObject.setUsertext("Ban chua dang ky nick chat tren he thong Radio Chat. Soan: RC <nick_toida20kytu> gui 8751 de dang ki nick, tham gia Chat cung ban be. DTHT: 1900571566.");
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
					
					else if (commandCode.equalsIgnoreCase("HA"))
					{	
						try{
								
								String answer =  msgObject.getUsertext().substring(commandCode.length() +1);
								int session = dbSelect.getSession();
								Timestamp time = msgObject.getTTimes();
								String player_boy  = dbSelect.player_boy();
								String player_girl  = dbSelect.player_girl();
								String time_question = dbSelect.time_question();
								String[] strQuestion1_start = time_question.split(";");
								
								Timestamp question1_start = Timestamp.valueOf(strQuestion1_start[0]);
								Timestamp question2_start = Timestamp.valueOf(strQuestion1_start[1]);
								Timestamp question3_start = Timestamp.valueOf(strQuestion1_start[2]);
								Timestamp question4_start = Timestamp.valueOf(strQuestion1_start[3]);
								Timestamp question5_start = Timestamp.valueOf(strQuestion1_start[4]);
								Timestamp question6_start = Timestamp.valueOf(strQuestion1_start[5]);
								Timestamp question6_end = Timestamp.valueOf(strQuestion1_start[6]);
								
								Util.logger.error ("start 1:" + strQuestion1_start[0]);
								Util.logger.error ("start 2:" + strQuestion1_start[1]);
								Util.logger.error ("start 3:" + strQuestion1_start[2]);
								Util.logger.error ("start 4:" + strQuestion1_start[3]);
								Util.logger.error ("start 5:" + strQuestion1_start[4]);
								Util.logger.error ("start 6:" + strQuestion1_start[5]);
								Util.logger.error ("end 6:" + strQuestion1_start[6]);
								Util.logger.error ("time recive:" + msgObject.getTTimes());								
								
								Util.logger.error ("player_girl:" + player_girl);
								Util.logger.error ("player_boy:" + player_boy);
								
								if (userID.equalsIgnoreCase(player_boy))									
								{
									
									if(time.getTime() >= question1_start.getTime() && time.getTime() <= question2_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_boy", userID, "boy_answer1", answer, session);
										
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									else if(time.getTime() >= question2_start.getTime() && time.getTime() <= question3_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_boy", userID, "boy_answer2", answer, session);
										
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									else if(time.getTime() >= question3_start.getTime() && time.getTime() <= question4_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_boy", userID, "boy_answer3", answer, session);
										
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}	
									else if(time.getTime() >= question4_start.getTime() && time.getTime() <= question5_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_boy", userID, "boy_answer1", answer, session);
										
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									else if(time.getTime() >= question5_start.getTime() && time.getTime() <= question6_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_boy", userID, "boy_answer2", answer, session);
										
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}	
									else if(time.getTime() >= question6_start.getTime() && time.getTime() <= question6_end.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_boy", userID, "boy_answer3", answer, session);
										
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}	
									else										
									
									msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. DTHT: 1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);								
									
									Thread.sleep(1000);
									return null;
								}
								else if(userID.equalsIgnoreCase(player_girl))
								{									
									if(time.getTime() >= question1_start.getTime() && time.getTime() <= question2_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_girl", userID, "girl_answer1", answer, session);
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									if(time.getTime() >= question2_start.getTime() && time.getTime() <= question3_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_girl", userID, "girl_answer2", answer, session);
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									if(time.getTime() >= question3_start.getTime() && time.getTime() <= question4_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_girl", userID, "girl_answer3", answer, session);
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									if(time.getTime() >= question4_start.getTime() && time.getTime() <= question5_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_girl", userID, "girl_answer1", answer, session);
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									if(time.getTime() >= question5_start.getTime() && time.getTime() <= question6_start.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_girl", userID, "girl_answer2", answer, session);
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									if(time.getTime() >= question6_start.getTime() && time.getTime() <= question6_end.getTime())
									{										
										dbInsert.UpdateToVovhcm_chat_answer_couple("player_girl", userID, "girl_answer3", answer, session);
										msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren Radio Chat. DTHT: 1900571566.");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);								
										
										Thread.sleep(1000);
										return null;
									}
									
									msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi Radio Chat. DTHT: 1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);								
									
									Thread.sleep(1000);
									return null;
								}
								else
								{
									msgObject.setUsertext("Chuong trinh Radio Chat da ghi nhan dap ap cua ban !");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);								
									
									Thread.sleep(1000);
									return null;
								}
														
							}
							catch (Exception e) {																	
								Util.logger.error ("Info:" + msgObject.getUsertext());
								msgObject.setUsertext("Yc ko hop le. De tra loi cau hoi, soan: HA <dap an> gui 8751. DTHT:1900571566");
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
					
					else
					{
						msgObject.setUsertext("Tin nhan khong hop le. De tham gia Radio Chat, B1: soan: RC <nick_toida20kytu> gui 8751. B2: soan: RC <gioitinh> <sothich> gui 8151. DTHT:1900571566.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						
						msgObject.setUsertext("Soan tin: LVB gui 8551 de nhan ngay link de choi Game Lam vuon vo cung hap dan.Chi tiet: http://funzone.vn.Chuc ban co giay phut vui ve");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);	
						
						Thread.sleep(1000);			
						return null;
					}					
		} catch (Exception e) {
			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {
		}
	}

	
	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	// Replace ____ with _
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
