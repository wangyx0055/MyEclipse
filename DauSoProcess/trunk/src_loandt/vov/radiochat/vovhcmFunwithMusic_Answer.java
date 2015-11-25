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
public class vovhcmFunwithMusic_Answer extends ContentAbstract {

	/* First String */
	
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)throws Exception {
		try {			
			vovhcmFunwithMusic_DBSelect dbSelect = new vovhcmFunwithMusic_DBSelect();
			vovhcmFunwithMusic_DBInsert dbInsert = new vovhcmFunwithMusic_DBInsert();	
			
			String mobileOperator = msgObject.getMobileoperator();
					
			String mtVT = "Tang ban ma so  bai Boiroi-633397.De cai nhac cho.Buoc1,soan DK gui 1221.Buoc2,soan BH  maso  gui1221.";			
			String mtVMS = "Tang ban ma so bai Boiroi-633397.De cai nhac cho. Buoc1,soan DK gui 9224. Buoc2,soan: CHON maso  gui 9224.";
			String mtGPC = "Tang ban ma so bai Boiroi-633397.De cai nhac cho. Buoc1,soan: DK gui 9194. Buoc2,soan: TUNE  maso  gui 9194.";
			String commandCode = msgObject.getKeyword();	
			String userID = msgObject.getUserid();				
			
				if(commandCode.startsWith("AN"))
				{						
					try
						{
						
						String info =  msgObject.getUsertext();
						String songname = info.substring(commandCode.length() +1);
						if(songname.length() >=30)
						songname = songname.substring(0,29);
						
						int session = dbSelect.getSession();
						Timestamp time = msgObject.getTTimes();
						String time_question = dbSelect.time_question();
						try
						{
							String[] strQuestion1_start = time_question.split(";");
							
							Util.logger.info ("start____" + strQuestion1_start[0]);
							Util.logger.info ("start 1____" + strQuestion1_start[1]);
							Util.logger.info ("start 2____" + strQuestion1_start[2]);
							Util.logger.info ("start 3___" + strQuestion1_start[3]);						
							Util.logger.info ("start add____" + strQuestion1_start[4]);
							Util.logger.info ("and____" + strQuestion1_start[5]);
							
							Util.logger.info ("time recive____" + msgObject.getTTimes());
							Util.logger.info ("userID____" + userID);	
							
							Timestamp start = Timestamp.valueOf(strQuestion1_start[0]);
							Timestamp question1_start = Timestamp.valueOf(strQuestion1_start[1]);
							Timestamp question2_start = Timestamp.valueOf(strQuestion1_start[2]);
							Timestamp question3_start = Timestamp.valueOf(strQuestion1_start[3]);					
							Timestamp question_add = Timestamp.valueOf(strQuestion1_start[4]);
							Timestamp end = Timestamp.valueOf(strQuestion1_start[5]);
							
							
							boolean checkUS = dbSelect.checkUS(userID, session);
							Util.logger.info ("checkUS____" + checkUS);
							
							Util.logger.info ("songname____" + songname);						
							Util.logger.info ("Keyword____" + commandCode);
							 // check xem sdt trong phien do da co cau tra loi nao hay chua
							// chua thi insert vao bang tra loi cau hoi cua chuong trinh
							
							if(checkUS == false)
							{
								//Util.logger.error ("Insert___________");
								dbInsert.insertvovhcmfunwithmusic_answer(userID, session);
								
							}
							
							// co roi thi update cau tra loi 
							// AN là chang
							// thoi gian tuong ung
							
							 if(commandCode.equalsIgnoreCase("AN"))							
							{
								//Util.logger.info ("Come herer ________" );
								if( time.getTime() >= question1_start.getTime() && time.getTime() <=question2_start.getTime())
								{
									dbInsert.UpdateTovovhcmfunwithmusic_answer(userID, "stage1", songname, session);
									msgObject.setUsertext("Ban da du doan la "+ songname +".Hay tiep tuc theo doi de biet ket qua.Du doan cang nhieu co hoi trung thuong cang lon.DTHT 1900571566");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
								else if( time.getTime() >= question2_start.getTime() && time.getTime() <=question3_start.getTime())
								{
									dbInsert.UpdateTovovhcmfunwithmusic_answer(userID, "stage2", songname, session);
									msgObject.setUsertext("Ban da du doan la "+ songname +".Hay tiep tuc theo doi de biet ket qua.Du doan cang nhieu co hoi trung thuong cang lon.DTHT 1900571566");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
								else if( time.getTime() >= question3_start.getTime() && time.getTime() <=question_add.getTime())
								{
									msgObject.setUsertext("Thoi gian du doan bai hat goc da ket thuc.Hay lang nghe MC thong bao de tham gia tra loi cau hoi phu. DTHT 1900571566");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
								else if( time.getTime() >= question_add.getTime() && time.getTime() <=end.getTime())
								{
									dbInsert.UpdateTovovhcmfunwithmusic_answer(userID, "stage_add", songname, session);
									msgObject.setUsertext("Dap an cua ban la " + songname +". Hay tiep tuc theo doi de biet ket qua. Du doan cang nhieu co hoi trung thuong cang lon. DTHT 1900571566");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
								else
								{
									msgObject.setUsertext("Chuong trinh chua bat dau.Hay quay lai vao chuong trinh lan sau vao 19h10 thu 6 va 18h thu 7, chu nhat hang tuan tren VOV giao thong 91MHz. DTHT 1900571566");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
									
								}
								
							}	
							
							// doan bai hat goc
							// thoi gian la toan bo chuong trinh
							
							else if(commandCode.equalsIgnoreCase("AN BHG"))
							{
								if (time.getTime() >= start.getTime() && time.getTime() <=question_add.getTime())
								{
									dbInsert.UpdateTovovhcmfunwithmusic_answer(userID, "original_songs", songname, session);
									msgObject.setUsertext("Ban da du doan ten bai hat goc la " + songname +". Hay tiep tuc theo doi de biet ket qua. Du doan cang nhieu co hoi trung thuong cang lon. DTHT 1900571566");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
								else
								{
									msgObject.setUsertext("Thoi gian du doan bai hat goc da ket thuc.Hay lang nghe MC thong bao de tham gia tra loi cau hoi phu. DTHT 1900571566");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
							}
							
						}
						catch (Exception e) {																	
														
							msgObject.setUsertext("Chuong trinh chua bat dau.Hay quay lai vao chuong trinh lan sau vao 19h10 thu 6 va 18h thu 7, chu nhat hang tuan tren VOV giao thong 91MHz. DTHT 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);							
							
							Thread.sleep(1000);
							return null;						
						} finally {	 }						
				
							
							// mt add cho rieng tung mang
							if (mobileOperator.equalsIgnoreCase("VMS"))
							{
								msgObject.setUsertext(mtVMS);
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								
								Thread.sleep(1000);
								return null;							
							}	
							else if (mobileOperator.equalsIgnoreCase("GPC"))
							{
								msgObject.setUsertext(mtGPC);
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								
								Thread.sleep(1000);
								return null;
							}
							else if (mobileOperator.equalsIgnoreCase("VIETTEL"))
							{
								msgObject.setUsertext(mtVT);
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								
								Thread.sleep(1000);
								return null;
							
							}
							
							
												
					}						
						catch (Exception e) {																	
							Util.logger.error ("Info:" + msgObject.getUsertext());
							
							msgObject.setUsertext("Tin nhan sai cu phap. Soan tin  AN BHG <tenbaihat> gui 8751 hoac AN <tenbaihat> gui 8751 de tham gia chuong trinh Vui cung am nhac. DTHT 1900571566");
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
}
