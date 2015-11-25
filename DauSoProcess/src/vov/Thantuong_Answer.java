package vov;



import java.sql.Timestamp;
import java.util.Collection;


import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
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
public class Thantuong_Answer extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			Thantuong_DBSelect dbSelect = new Thantuong_DBSelect();
			Thantuong_DBInsert dbInsert = new Thantuong_DBInsert();	
			
			String mobileOperator = msgObject.getMobileoperator();
			String commandCode = msgObject.getKeyword();	
			String userID = msgObject.getUserid();				
													
					try
						{
						
						String info =  msgObject.getUsertext();
						String songname = info.substring(commandCode.length() +1);
						String infos ="";
						int session = dbSelect.getSession();
						Timestamp time = msgObject.getTTimes();
						String time_question = dbSelect.time_question();
						try
						{
							String[] strQuestion1_start = time_question.split(";");
							
							Util.logger.info ("start____" + strQuestion1_start[0]);
							Util.logger.info ("block 1____" + strQuestion1_start[1]);
							Util.logger.info ("block 2____" + strQuestion1_start[2]);
							Util.logger.info ("block 3___" + strQuestion1_start[3]);						
							Util.logger.info ("end____" + strQuestion1_start[4]);
							Util.logger.info ("question1____" + strQuestion1_start[5]);
							Util.logger.info ("question2____" + strQuestion1_start[6]);
							Util.logger.info ("question3____" + strQuestion1_start[7]);
							Util.logger.info ("question4____" + strQuestion1_start[8]);
							Util.logger.info ("question5____" + strQuestion1_start[9]);
							Util.logger.info ("Endquestion____" + strQuestion1_start[10]);
							
							Util.logger.info ("time recive____" + msgObject.getTTimes());
							Util.logger.info ("userID____" + userID);	
							
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
							String answer1 = strQuestion1_start[10];
							String answer2 = strQuestion1_start[11];
							String answer3 = strQuestion1_start[12];
							String answer4 = strQuestion1_start[13];
							String answer5 = strQuestion1_start[14];
							String singername = strQuestion1_start[15];
							
							String status = "";
							String mt1 = "Ban du doan:" + songname +" .Hay du doan them de tang co hoi chien thang.Soan MCA tenbaihat gui 8751 de tai cac bai hat yeu thich lam nhac cho.DTHT:1900571566";
							String mt2 ="Tham gia dau gia Iphone4 soan DT <gia sp> gui 8751,dua ra gia thap nhat va duy nhat de chien thang.Bat dau tu 14h hom nay den 12h45 ngay mai.DTHT:1900571566";							
							String mt3_1 ="Rat tiec!Ban du doan chua chinh xac ten than tuong bi mat.Hay lang nghe ca du kien va nhanh tay du doan them de tang co hoi trung thuong!DTHT:1900571566";
							String mt3_2 ="Rat tiec!Dap an ban chon chua dung.Phan qua hap dan van dang cho ban.Hay nhanh tay soan tin de tro thanh nguoi dau tien doan dung nhe!DTHT:1900571566";
							String mt3 = "Rat tiec!Ban du doan chua chinh xac ten than tuong bi mat.Hay lang nghe ca du kien va nhanh tay du doan them de tang co hoi trung thuong!DTHT:1900571566";
							
							boolean flag = false;
														
							boolean checkUS = dbSelect.checkUS(userID);
							Util.logger.info ("checkUS____" + checkUS);							
							Util.logger.info ("songname____" + songname);						
							Util.logger.info ("Keyword____" + commandCode);
							
							singername = replaceAllWhiteWithOne(singername);
							singername = singername.replace(" ", "");
							infos = songname ;
							infos = replaceAllWhiteWithOne(infos);
							infos = infos.replace(" ", "");
							 
							//	dbInsert.saveCustomer(session, user_id, mobile_operator, guess_name, exactly, block, request_id, keyword)(userID, session);
							
							
								if( time.getTime() >= start.getTime() && time.getTime() <=question1_start.getTime())
								{	
									status = "1";
								
									if (singername.equalsIgnoreCase(infos))
									{
										//mt3 = mt3_true;
										flag = true;
									}
									else
									{
										mt3 = mt3_1;
										flag = false;
									}
								}
								else if( time.getTime() >= question1_start.getTime() && time.getTime() <=question2_start.getTime())
								{
									status = "2";									
									if (singername.equalsIgnoreCase(infos))
									{
										// = mt3_true;
										flag = true;
									}
									else
									{
										mt3 = mt3_1;
										flag = false;
									}
								}
								else if( time.getTime() >= question2_start.getTime() && time.getTime() <=end.getTime())
								{
									status = "3";									
									if (singername.equalsIgnoreCase(infos))
									{
										//mt3 = mt3_true;
										flag = true;
									}
									else
									{
										mt3 = mt3_1;
										flag = false;
									}
								}
								else if( time.getTime() >= question1.getTime() && time.getTime() <=question2.getTime())
								{
									status = "1";									
									if (answer1.equalsIgnoreCase(infos))
									{
										//mt3 = mt3_true;
										flag = true;
									}
									else
									{
										mt3 = mt3_2;
										flag = false;
									}
								}
								else if( time.getTime() >= question2.getTime() && time.getTime() <=question3.getTime())
								{
									status = "2";									
									if (answer2.equalsIgnoreCase(infos))
									{
										//mt3 = mt3_true;
										flag = true;
									}
									else
									{
										mt3 = mt3_2;
										flag = false;
									}									
								}
								else if( time.getTime() >= question3.getTime() && time.getTime() <=question4.getTime())
								{
									status = "3";									
									if (answer3.equalsIgnoreCase(infos))
									{
										//mt3 = mt3_true;
										flag = true;
									}
									else
									{
										mt3 = mt3_2;
										flag = false;
									}								
									
								}
								else if( time.getTime() >= question4.getTime() && time.getTime() <=question5.getTime())
								{
									status = "4";									
									if (answer4.equalsIgnoreCase(infos))
									{
										//mt3 = mt3_true;
										flag = true;
									}
									else
									{
										mt3 = mt3_2;
										flag = false;
									}
									
								}
								else if( time.getTime() >= question5.getTime() && time.getTime() <=question6.getTime())
								{
									status = "5";									
									if (answer5.equalsIgnoreCase(infos))
									{
										//mt3 = mt3_true;
										flag = true;
									}
									else
									{
										mt3 = mt3_2;
										flag = false;
									}
									
									}
								else if( time.getTime() >= end.getTime() && time.getTime() <=question1.getTime())
								{
									msgObject.setUsertext("Thoi gian tra loi cau hoi chua bat dau.Hay nghe Nhan dien than tuong de tham gia tra loi cau hoi va rinh ve nhung giai thuong gia tri.DTHT: 1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);	
								}
								else
								{
									msgObject.setUsertext("Chuong trinh hien khong phat song.Vui long don nghe Nhan dien than tuong luc 19h10 - 20h10 cac ngay Thu2 va Thu4 tren lan saong 91 Mhz. DTHT: 1900571566.");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);									
								}	
								
								// Tra MT chinh
								if (time.getTime() >= start.getTime() && time.getTime() <= question6.getTime())
								{									
									msgObject.setUsertext(mt1);
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);									
								}
								
	//tra tin MT dinh kem	
								boolean checkExist1 = dbSelect.checkUS(userID);
								boolean checkExist2 = dbSelect.checkUser(userID);
								if (checkExist1 == false && checkExist2 == false)
								{
									msgObject.setUsertext(mt2);
									msgObject.setMsgtype(0);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);	
								}		
								// Insert vao bang
								// Cuswtomer voi time in (start, end)
								// CuswtomerVote voi time in (question1, question6)							
						
								
								if (time.getTime() >= start.getTime() && time.getTime() <= end.getTime() )									
								{	
									if (flag == true)
									{	
										dbInsert.saveCustomer(session+"", userID, mobileOperator, songname , 1, Integer.parseInt(status), msgObject.getRequestid().toString(), msgObject.getKeyword());
									}
									else
									{	
										dbInsert.saveCustomer(session+"", userID, mobileOperator, songname , 0, Integer.parseInt(status), msgObject.getRequestid().toString(), msgObject.getKeyword());										
											// Tra MT thong bao ket qua sai
										
										msgObject.setUsertext(mt3);
										msgObject.setMsgtype(0);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);										
									}			
								}
								else if(time.getTime() >= question1.getTime() && time.getTime() <= question6.getTime() )
								{	
									if (flag == true)
									{
										dbInsert.saveCustomerVote(session+"", userID, msgObject.getMobileoperator(), songname, 1, msgObject.getRequestid().toString(), msgObject.getKeyword(), status);
									}
									else
									{
										dbInsert.saveCustomerVote(session+"", userID, msgObject.getMobileoperator(), songname, 0, msgObject.getRequestid().toString(), msgObject.getKeyword(), status);
										
										// Tra MT thong bao sai
										
										msgObject.setUsertext(mt3);
										msgObject.setMsgtype(0);
										msgObject.setContenttype(0);
										DBUtil.sendMT(msgObject);
									}	
								}				
	// 													
						}
						catch (Exception e) {																	
														
							msgObject.setUsertext("Chuong trinh hien khong phat song.Vui long don nghe Nhan dien than tuong luc 19h10 - 20h10 cac ngay Thu2 va Thu4 tren lan saong 91 Mhz. DTHT: 1900571566.");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);							
							
							Thread.sleep(1000);
							return null;						
						} finally {	 }									
					}						
						catch (Exception e) {																	
							Util.logger.error ("Info:" + msgObject.getUsertext());							
							msgObject.setUsertext("Chuong trinh hien khong phat song.Vui long don nghe Nhan dien than tuong luc 19h10 - 20h10 cac ngay Thu2 va Thu4 tren lan saong 91 Mhz. DTHT: 1900571566.");
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

}
