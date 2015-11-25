package vov.radiochat;



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
public class Answer_vovhn extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			DBSelect_hanoi dbSelect = new DBSelect_hanoi();
			DBInsert_Hanoi dbInsert = new DBInsert_Hanoi();	
			
			String commandCode = msgObject.getKeyword();			
			String userID = msgObject.getUserid();	
			String serviceID = msgObject.getServiceid();
			
				if(commandCode.equalsIgnoreCase("RL") && serviceID.equalsIgnoreCase("8551"))
				{						
					try
						{
						
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
								dbInsert.UpdateToVovhn_chat_answer_couple("player_boy", userID, "boy_answer1", answer, session);
								
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							else if(time.getTime() >= question2_start.getTime() && time.getTime() <= question3_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_boy", userID, "boy_answer2", answer, session);
								
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							else if(time.getTime() >= question3_start.getTime() && time.getTime() <= question4_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_boy", userID, "boy_answer3", answer, session);
								
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}	
							else if(time.getTime() >= question4_start.getTime() && time.getTime() <= question5_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_boy", userID, "boy_answer1", answer, session);
								
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							else if(time.getTime() >= question5_start.getTime() && time.getTime() <= question6_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_boy", userID, "boy_answer2", answer, session);
								
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}	
							else if(time.getTime() >= question6_start.getTime() && time.getTime() <= question6_end.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_boy", userID, "boy_answer3", answer, session);
								
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}	
							else										
							{
							msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveRadio Chat. DTHT: 1900571566.");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);								
							
							Thread.sleep(1000);
							return null;
							}
						}	
						else if(userID.equalsIgnoreCase(player_girl))
						{									
							if(time.getTime() >= question1_start.getTime() && time.getTime() <= question2_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_girl", userID, "girl_answer1", answer, session);
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							if(time.getTime() >= question2_start.getTime() && time.getTime() <= question3_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_girl", userID, "girl_answer2", answer, session);
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							if(time.getTime() >= question3_start.getTime() && time.getTime() <= question4_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_girl", userID, "girl_answer3", answer, session);
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							if(time.getTime() >= question4_start.getTime() && time.getTime() <= question5_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_girl", userID, "girl_answer1", answer, session);
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							if(time.getTime() >= question5_start.getTime() && time.getTime() <= question6_start.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_girl", userID, "girl_answer2", answer, session);
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							if(time.getTime() >= question6_start.getTime() && time.getTime() <= question6_end.getTime())
							{										
								dbInsert.UpdateToVovhn_chat_answer_couple("player_girl", userID, "girl_answer3", answer, session);
								msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveLoveRadio. Chuc ban co phuong an an y cung ban choi. Vui long don nghe tren LoveRadio Chat. DTHT: 1900571566.");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);								
								
								Thread.sleep(1000);
								return null;
							}
							else
							{							
							msgObject.setUsertext("Cau tra loi cua ban da duoc gui toi LoveRadio Chat. DTHT: 1900571566.");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);								
							
							Thread.sleep(1000);
							return null;
							}
						}
						else
						{
							msgObject.setUsertext("Chuong trinh LoveRadio Chat da ghi nhan dap ap cua ban !");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);								
							
							Thread.sleep(1000);
							return null;
						}
												
					}						
						catch (Exception e) {																	
							Util.logger.error ("Info:" + msgObject.getUsertext());
							
							msgObject.setUsertext("Yeu cau khong hop le. De tra loi cau hoi, soan: RL <dap an> gui 8551. DTHT:1900571566.");
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
}
