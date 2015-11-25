package tracuudiemthi;



import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.DBUtils;
import icom.common.Util;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javassist.compiler.ProceedHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DiemThi_Khoi extends Thread{
	
	private int processIndex;
	private int processNum;
	public DiemThi_Khoi(int processIndex,int processNum)
	{
		this.processIndex=processIndex;
		this.processNum=processNum;
	}
	
	public void run(){
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		java.util.Date today = new java.util.Date();
		String currHour = formatter.format(today);
		
		String timeToCallAPI = Constants._prop.getProperty("TIME_CALL_API", "12:00");
		
		//String[] arrTimeCall = timeToCallAPI.split(";");
		
		while(Sender.processData){
				try {
					if(timeToCallAPI.indexOf(currHour)>=0) {						
							getMessages();
						}
					Thread.sleep(30*1000);
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	protected Collection getMessages() throws Exception {
		String url = Constants._prop.getProperty("url_tuyensinh");
		int count_api = Integer.parseInt(Constants._prop.getProperty("count_api"));
		String resultDT = "";
		MsgObject msgObject = new MsgObject();
		String user_id = "";
		String info = "";
		String service_id = "";
		String mobile_operator = "";
		String command_code = "";
		String requestID ="";
		String check_result = "";
		String xml = "";
		String mon = "";
		String status = "";
		String tongdiem = "";
		String result = "";
		String tents = "";
		String count_sms = "";
		try {
			String query3 = "select REQUEST_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, USER_ID,COUNT_SMS from diemthi_queue_khoi where RESPONDED=0 " +
			" AND MOD(ID,"+processNum+")="+processIndex +" limit 100";
			Vector result3 = DBUtil.getVectorTable("gateway", query3);
			if (result3.size() > 0) {
				for(int iIndex = 0;iIndex<result3.size();iIndex++) {
					
					Vector item = (Vector) result3.elementAt(iIndex);
					requestID = String.valueOf(item.elementAt(0));
					service_id = String.valueOf(item.elementAt(1));
					mobile_operator = String.valueOf(item.elementAt(2));
					command_code = String.valueOf(item.elementAt(3));
					info = String.valueOf(item.elementAt(4));
					user_id= String.valueOf(item.elementAt(5));
					count_sms = String.valueOf(item.elementAt(6));
					String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
					String ma_truong = "";
					String sbd = "";
					String sbd1 = "";
					String ma_khoi = "";
					String ma_khoi1 = "";
					String sbd_all = "";
					info = info.replaceAll(" ", "");
					sbd_all = info.substring(command_code.length(), info.length());
					String text = command_code + " " + sbd_all;
					//info = command_code + " " + sbd_all;
					if(sbd_all.length() > 4) {
						ma_truong = sbd_all.substring(0, 3);
						ma_khoi = sbd_all.substring(3, 4);
						sbd = sbd_all.substring(4, sbd_all.length());
						if("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi)) {
							//cat so tu sau ma khoi
							ma_khoi1 = sbd_all.substring(3, 5);
							sbd1 = sbd_all.substring(5, sbd_all.length());
						}
					} 
					
					check_result = result_diemthi(info);
					if(check_result!= "") {
						msgObject.setUsertext(check_result);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						msgObject.setUserid(user_id);
						msgObject.setServiceid(service_id);
						msgObject.setCommandCode(command_code);
						Sender.msgPushMTQueue.add(msgObject);
						
						String sqlUpdateRunning = "update diemthi_queue_khoi set RESPONDED =1, RECEIVE_DATE=current_timestamp() where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
						DBUtil.executeSQL("gateway", sqlUpdateRunning);
						continue;
					} else {
					//	if(Integer.parseInt(count_sms ) < count_api) {
							String api_diemthi= ""; 
							String api_diemthi1= "";
								if(sbd!= "" && ma_truong != "" && ma_khoi != "") {
									api_diemthi = url+"gameID=20&type=3&sbd="+sbd+"&sID="+ma_truong+"&khoi="+ma_khoi+"&request_id=" +requestID +
									"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+text+"";
								} else if("".equalsIgnoreCase(sbd) || "".equalsIgnoreCase(ma_truong) || "".equalsIgnoreCase(ma_khoi)){
									deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
									continue;
								}
								if("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi)) {
									if(sbd1!= "" && ma_truong != "" && ma_khoi1 != "") {
										api_diemthi1 = url+"gameID=20&type=3&sbd="+sbd1+"&sID="+ma_truong+"&khoi="+ma_khoi1+"&request_id=" +requestID +
										"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+text+"";
									} else if("".equalsIgnoreCase(sbd1) || "".equalsIgnoreCase(ma_truong) || "".equalsIgnoreCase(ma_khoi1)){
										deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
										continue;
									}
								}
			if(isExistMaTruong(ma_truong)) {
					api_diemthi = api_diemthi.replace(" ","%20");
					Util.logger.info("API call diem: " + api_diemthi);
					xml = Http_post(api_diemthi);
					if(xml != null && xml != "") { 
						status = getValue(xml, "status");
						if("1".equalsIgnoreCase(status)) {
							mon = getValue(xml, "mon");
							tongdiem = getValue(xml, "tongdiem");
							tents = getValue(xml, "name");
							tents = tents.replace("'", "");
							tents = convertAccent(tents);
							mon = resultDiem(xml);
							if(mon != "") {
								result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sbd +" -" + mon.substring(0, mon.length()-1);
					
								msgObject.setUsertext(result);
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								msgObject.setUserid(user_id);
								msgObject.setServiceid(service_id);
								msgObject.setCommandCode(command_code);
								//DBUtils.sendMT(msgObject);
								Sender.msgPushMTQueue.add(msgObject);
								if(!isExistUserid(info, result)) {
									insert_result(info, result);
								}
								/*//insertMT.sendMT(msgObject);
								UpdateListSentObject update = new UpdateListSentObject();
								update.setResponse_status(1);
								update.setCount_sms(Integer.parseInt(count_sms)+1);
								update.setPartner("Goldsoft");
								update.setCommand_code(command_code);
								update.setUserid(user_id);
								update.setRequestid(requestID);
								update.setDb_name("diemthi_queue_khoi");
								Sender.queueUpdate.add(update);*/
								String sqlUpdateRunning = "update diemthi_queue_khoi set RESPONDED =1,COUNT_SMS = COUNT_SMS+1, RECEIVE_DATE=current_timestamp() where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
								DBUtil.executeSQL("gateway", sqlUpdateRunning);
								continue;
							} else {
								return null;
							}
							
						} else if("0".equalsIgnoreCase(status)){
							if(("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi))) {
								api_diemthi1 = api_diemthi1.replace(" ","%20");
								Util.logger.info("API call diem: " + api_diemthi1);
								xml = Http_post(api_diemthi1);
								if(xml != null && xml != "") {
									status = getValue(xml, "status");
									if("1".equalsIgnoreCase(status)) {
										mon = getValue(xml, "mon");
										tongdiem = getValue(xml, "tongdiem");
										tents = getValue(xml, "name");
										tents = tents.replace("'", "");
										tents = convertAccent(tents);
										mon = resultDiem(xml);
										if(mon !="") {
											result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sbd1+" -" + mon.substring(0, mon.length()-1);
											msgObject.setUsertext(result);
											msgObject.setMsgtype(1);
											msgObject.setContenttype(0);
											msgObject.setUserid(user_id);
											msgObject.setServiceid(service_id);
											msgObject.setCommandCode(command_code);
											Sender.msgPushMTQueue.add(msgObject);
											if(!isExistUserid(info, result)) {
												insert_result(info, result);
											}
											/*UpdateListSentObject update = new UpdateListSentObject();
											update.setResponse_status(1);
											update.setCount_sms(Integer.parseInt(count_sms)+2);
											update.setPartner("Goldsoft");
											update.setCommand_code(command_code);
											update.setUserid(user_id);
											update.setRequestid(requestID);
											update.setDb_name("diemthi_queue_khoi");
											Sender.queueUpdate.add(update);*/
											String sqlUpdateRunning = "update diemthi_queue_khoi set RESPONDED =1,COUNT_SMS = COUNT_SMS+2, RECEIVE_DATE=current_timestamp() where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
											DBUtil.executeSQL("gateway", sqlUpdateRunning);
											continue;
											
										} else {
											return null;
										}
										
									} else if("0".equalsIgnoreCase(status)){
									/*	String sqlUpdateRunning = "update diemthi_queue_khoi set RESPONSE_STATUS=1, COUNT_SMS=COUNT_SMS+2, PARTNER = 'Goldsoft', LAST_TIME=current_timestamp() where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
										DBUtil.executeSQL("gateway", sqlUpdateRunning);*/
										msgObject.setUsertext("Sai so bao danh, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
										msgObject.setMsgtype(0);
										msgObject.setContenttype(0);
										msgObject.setUserid(user_id);
										msgObject.setServiceid(service_id);
										msgObject.setCommandCode(command_code);
									//	DBUtils.sendMT(msgObject);
										Sender.msgPushMTQueue.add(msgObject);
										deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
										insertDiemthi_Invalid(user_id, service_id, mobile_operator, command_code, info, requestID, Integer.parseInt(count_sms)+2);
										continue;
									} 
								} else {
									deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
									insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, Integer.parseInt(count_sms)+2);
									continue;
								}
								
							} else {
								msgObject.setUsertext("Sai so bao danh, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								msgObject.setUserid(user_id);
								msgObject.setServiceid(service_id);
								msgObject.setCommandCode(command_code);
							//	DBUtils.sendMT(msgObject);
								Sender.msgPushMTQueue.add(msgObject);
								deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
								insertDiemthi_Invalid(user_id, service_id, mobile_operator, command_code, info, requestID, Integer.parseInt(count_sms)+1);
								continue;
							}
						} else if("2".equalsIgnoreCase(status)) {
							if(("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi))) {
								api_diemthi1 = api_diemthi1.replace(" ","%20");
								Util.logger.info("API call diem: " + api_diemthi1);
								xml = Http_post(api_diemthi1);
								if(xml != null && xml != "") {
									status = getValue(xml, "status");
									if("1".equalsIgnoreCase(status)) {
										mon = getValue(xml, "mon");
										tongdiem = getValue(xml, "tongdiem");
										tents = getValue(xml, "name");
										tents = tents.replace("'", "");
										tents = convertAccent(tents);
										mon = resultDiem(xml);
										if(mon !="") {
											result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sbd1+" -" + mon.substring(0, mon.length()-1);
											msgObject.setUsertext(result);
											msgObject.setMsgtype(1);
											msgObject.setContenttype(0);
											msgObject.setUserid(user_id);
											msgObject.setServiceid(service_id);
											msgObject.setCommandCode(command_code);
											Sender.msgPushMTQueue.add(msgObject);
											if(!isExistUserid(info, result)) {
												insert_result(info, result);
											}
											/*UpdateListSentObject update = new UpdateListSentObject();
											update.setResponse_status(1);
											update.setCount_sms(Integer.parseInt(count_sms)+2);
											update.setPartner("Goldsoft");
											update.setCommand_code(command_code);
											update.setUserid(user_id);
											update.setRequestid(requestID);
											update.setDb_name("diemthi_queue_khoi");
											Sender.queueUpdate.add(update);*/
											String sqlUpdateRunning = "update diemthi_queue_khoi set RESPONDED =1,COUNT_SMS = COUNT_SMS+2, RECEIVE_DATE=current_timestamp() where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
											DBUtil.executeSQL("gateway", sqlUpdateRunning);
											continue;
											
										} else {
											return null;
										}
										
									} else if("0".equalsIgnoreCase(status)){
									/*	String sqlUpdateRunning = "update diemthi_queue_khoi set RESPONSE_STATUS=1, COUNT_SMS=COUNT_SMS+2, PARTNER = 'Goldsoft', LAST_TIME=current_timestamp() where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
										DBUtil.executeSQL("gateway", sqlUpdateRunning);*/
										msgObject.setUsertext("Sai so bao danh, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
										msgObject.setMsgtype(0);
										msgObject.setContenttype(0);
										msgObject.setUserid(user_id);
										msgObject.setServiceid(service_id);
										msgObject.setCommandCode(command_code);
									//	DBUtils.sendMT(msgObject);
										Sender.msgPushMTQueue.add(msgObject);
										deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
										insertDiemthi_Invalid(user_id, service_id, mobile_operator, command_code, info, requestID, Integer.parseInt(count_sms)+2);
										continue;
									} 
								} else {
									deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
									insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, Integer.parseInt(count_sms)+2);
									continue;
								}
								
							}
						} else {
							deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
							insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, Integer.parseInt(count_sms)+1);
							continue;
						}
					} else {
						deleteUser(user_id, requestID, command_code, "diemthi_queue_khoi");
						insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, Integer.parseInt(count_sms)+1);
						continue;
					}
				} 
			/*} else {
				deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
				insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, count_sms);
				continue;
			}*/
						
					}
				
				}
			}
			} catch (SQLException e) {
				Util.logger.error(": Error:" + e.toString());
				return null;
			} catch (Exception e) {
				Util.logger.info(this.getClass().getName() + "@" + e.toString());
				return null;
			} finally {
			//	dbpool.cleanup(connection);
			}
	 //}
	//	}
		return null;
}
		
	//Check xem mon da co trong table chua
	private boolean isExistMaTruong(String matruong) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  matruong_diemthi where matruong = '"+ matruong+ "'";

			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistMaMon Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}
	
	private String convertAccent(String str) {
		str = str.replace("À","A");
		str = str.replace("Â","A");
		str = str.replace("Ä","A");		
		str = str.replace("È","E");
		str = str.replace("É","E");
		str = str.replace("Ê","E");
		str = str.replace("Ë","E");		
		str = str.replace("Î","I");
		str = str.replace("Ï","I");		
		str = str.replace("Ô","O");				
		str = str.replace("Ù","U");
		str = str.replace("Û","U");		
		str = str.replace("Ü","U");		
		str = str.replace("Ÿ","Y");
		
		str = str.replace("à","a");
		str = str.replace("â","a");
		str = str.replace("ä","a");		
		str = str.replace("è","e");
		str = str.replace("é","e");
		str = str.replace("ê","e");
		str = str.replace("ë","e");		
		str = str.replace("î","i");
		str = str.replace("ï","i");		
		str = str.replace("ô","o");				
		str = str.replace("ù","u");
		str = str.replace("û","u");		
		str = str.replace("u","u");		
		str = str.replace("ÿ","y");
		
		// Vietnamese accents
		str = str.replace("ấ", "a");
	    str = str.replace("ầ", "a");
	    str = str.replace("ẩ", "a");
	    str = str.replace("ẫ", "a");
	    str = str.replace("ậ", "a");
	    //---------------------------------A^ 
	    str = str.replace("Ấ", "A");
	    str = str.replace("Ầ", "A");
	    str = str.replace("Ẩ", "A");
	    str = str.replace("Ẫ", "A");
	    str = str.replace("Ậ", "A");
	    //---------------------------------a( 
	    str = str.replace("ắ", "a");
	    str = str.replace("ằ", "a");
	    str = str.replace("ẳ", "a");
	    str = str.replace("ẵ", "a");
	    str = str.replace("ặ", "a");
	    //---------------------------------A( 
	    str = str.replace("Ắ", "A");
	    str = str.replace("Ằ", "A");
	    str = str.replace("Ẳ", "A");
	    str = str.replace("Ẵ", "A");
	    str = str.replace("Ặ", "A");
	    //---------------------------------a 
	    str = str.replace("á", "a");
	    str = str.replace("à", "a");
	    str = str.replace("ả", "a");
	    str = str.replace("ã", "a");
	    str = str.replace("ạ", "a");
	    str = str.replace("â", "a");
	    str = str.replace("ă", "a");
	    //---------------------------------A 
	    str = str.replace("Á", "A");
	    str = str.replace("À", "A");
	    str = str.replace("Ả", "A");
	    str = str.replace("Ã", "A");
	    str = str.replace("Ạ", "A");
	    str = str.replace("Â", "A");
	    str = str.replace("Ă", "A");
	    //---------------------------------e^ 
	    str = str.replace("ế", "e");
	    str = str.replace("ề", "e");
	    str = str.replace("ể", "e");
	    str = str.replace("ễ", "e");
	    str = str.replace("ệ", "e");
	    //---------------------------------E^ 
	    str = str.replace("Ế", "E");
	    str = str.replace("Ề", "E");
	    str = str.replace("Ể", "E");
	    str = str.replace("Ễ", "E");
	    str = str.replace("Ệ", "E");
	    //---------------------------------e 
	    str = str.replace("é", "e");
	    str = str.replace("è", "e");
	    str = str.replace("ẻ", "e");
	    str = str.replace("ẽ", "e");
	    str = str.replace("ẹ", "e");
	    str = str.replace("ê", "e");
	    //---------------------------------E 
	    str = str.replace("É", "E");
	    str = str.replace("È", "E");
	    str = str.replace("Ẻ", "E");
	    str = str.replace("Ẽ", "E");
	    str = str.replace("Ẹ", "E");
	    str = str.replace("Ê", "E");
	    //---------------------------------i 
	    str = str.replace("í", "i");
	    str = str.replace("ì", "i");
	    str = str.replace("ỉ", "i");
	    str = str.replace("ĩ", "i");
	    str = str.replace("ị", "i");
	    //---------------------------------I 
	    str = str.replace("Í", "I");
	    str = str.replace("Ì", "I");
	    str = str.replace("Ỉ", "I");
	    str = str.replace("Ĩ", "I");
	    str = str.replace("Ị", "I");
	    //---------------------------------o^ 
	    str = str.replace("ố", "o");
	    str = str.replace("ồ", "o");
	    str = str.replace("ổ", "o");
	    str = str.replace("ỗ", "o");
	    str = str.replace("ộ", "o");
	    //---------------------------------O^ 
	    str = str.replace("Ố", "O");
	    str = str.replace("Ồ", "O");
	    str = str.replace("Ổ", "O");
	    str = str.replace("Ô", "O");
	    str = str.replace("Ộ", "O");
	    //---------------------------------o* 
	    str = str.replace("ớ", "o");
	    str = str.replace("ờ", "o");
	    str = str.replace("ở", "o");
	    str = str.replace("ỡ", "o");
	    str = str.replace("ợ", "o");
	    //---------------------------------O* 
	    str = str.replace("Ớ", "O");
	    str = str.replace("Ờ", "O");
	    str = str.replace("Ở", "O");
	    str = str.replace("Ỡ", "O");
	    str = str.replace("Ợ", "O");
	    //---------------------------------u* 
	    str = str.replace("ứ", "u");
	    str = str.replace("ừ", "u");
	    str = str.replace("ử", "u");
	    str = str.replace("ữ", "u");
	    str = str.replace("ự", "u");
	    //---------------------------------U* 
	    str = str.replace("Ứ", "U");
	    str = str.replace("Ừ", "U");
	    str = str.replace("Ử", "U");
	    str = str.replace("Ữ", "U");
	    str = str.replace("Ự", "U");
	    //---------------------------------y 
	    str = str.replace("ý", "y");
	    str = str.replace("ỳ", "y");
	    str = str.replace("ỷ", "y");
	    str = str.replace("ỹ", "y");
	    str = str.replace("ỵ", "y");
	    //---------------------------------Y 
	    str = str.replace("Ý", "Y");
	    str = str.replace("Ỳ", "Y");
	    str = str.replace("Ỷ", "Y");
	    str = str.replace("Ỹ", "Y");
	    str = str.replace("Ỵ", "Y");
	    //---------------------------------DD 
	    str = str.replace("Đ", "D");
	    str = str.replace("Đ", "D");
	    str = str.replace("đ", "d");
	    //---------------------------------o 
	    str = str.replace("ó", "o");
	    str = str.replace("ò", "o");
	    str = str.replace("ỏ", "o");
	    str = str.replace("õ", "o");
	    str = str.replace("ọ", "o");
	    str = str.replace("ô", "o");
	    str = str.replace("ơ", "o");
	    //---------------------------------O 
	    str = str.replace("Ó", "O");
	    str = str.replace("Ò", "O");
	    str = str.replace("Ỏ", "O");
	    str = str.replace("Õ", "O");
	    str = str.replace("Ọ", "O");
	    str = str.replace("Ô", "O");
	    str = str.replace("Ơ", "O");
	    //---------------------------------u 
	    str = str.replace("ú", "u");
	    str = str.replace("ù", "u");
	    str = str.replace("ủ", "u");
	    str = str.replace("ũ", "u");
	    str = str.replace("ụ", "u");
	    str = str.replace("ư", "u");
	    //---------------------------------U 
	    str = str.replace("Ú", "U");
	    str = str.replace("Ù", "U");
	    str = str.replace("Ủ", "U");
	    str = str.replace("Ũ", "U");
	    str = str.replace("Ụ", "U");
	    str = str.replace("Ư", "U");
		return str;
	}
	
	private int insertBackup_diemthi(String user_id,String service_id,String mobile_operator,String command_code,String info, String request_id, int count_sms) {
		int ireturn = 1;
		
		
		String sqlUpdate = "insert into diemthi_queue_invalid( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, REQUEST_ID, charge_type, COUNT_SMS)"
				+ " values('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ mobile_operator
				+ "','"
				+ command_code
				+ "','"
				+ info
				+ "','"
				+ request_id
				+ "',1," + count_sms +")";
		try {

			Util.logger.info("Insert into diemthi_queue_invalid: " + sqlUpdate);
			if (DBUtil.executeSQL("gateway", sqlUpdate) < 0) {
				Util.logger.info("Insert Statement: Insert mlist_diemthi Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.info("Insert Statement: Insert mlist_diemthi Failed");
			ireturn = -1;
		} 
		return ireturn;
	}
	
	private int insertDiemthi_Invalid(String user_id,String service_id,String mobile_operator,String command_code,String info, String request_id, int count_sms) {
		int ireturn = 1;
		
		
		String sqlUpdate = "insert into diemthi_invalid( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, REQUEST_ID, charge_type, COUNT_SMS)"
				+ " values('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ mobile_operator
				+ "','"
				+ command_code
				+ "','"
				+ info
				+ "','"
				+ request_id
				+ "',1," + count_sms +")";
		try {

			Util.logger.info("Insert into diemthi_invalid: " + sqlUpdate);
			if (DBUtil.executeSQL("gateway", sqlUpdate) < 0) {
				Util.logger.info("Insert Statement: Insert diemthi_invalid Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.info("Insert Statement: Insert diemthi_invalid Failed");
			ireturn = -1;
		} 
		return ireturn;
	}

	private int insert_Sbd_queue(String user_id,String service_id,String mobile_operator,String command_code,String info, String request_id, int count_sms) {
		int ireturn = 1;
		
		
		String sqlUpdate = "insert into diemthi_queue_khoi( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, REQUEST_ID, charge_type, COUNT_SMS)"
				+ " values('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ mobile_operator
				+ "','"
				+ command_code
				+ "','"
				+ info
				+ "','"
				+ request_id
				+ "',1," + count_sms +")";
		try {

			Util.logger.info("Insert into diemthi_queue: " + sqlUpdate);
			if (DBUtil.executeSQL("gateway", sqlUpdate) < 0) {
				Util.logger.info("Insert Statement: Insert diemthi_queue Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.info("Insert Statement: Insert diemthi_queue Failed");
			ireturn = -1;
		} 
		return ireturn;
	}
	
	private static boolean deleteUser(String user, String request, String command_code, String table) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM " + table + "  WHERE USER_ID='"
					+ user + "' and REQUEST_ID = '"+request+"' and COMMAND_CODE='"+command_code+"'";
			Util.logger.info(" DELETE USER: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger
						.info("Loi xoa user " + user + "trong bang " + table);
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
	public static String resultDiem(String result) throws Exception{
		String Result ="";
		String xmlResult  = result;
		
		if(xmlResult!="") {
		
			try {
				DocumentBuilderFactory dbf =   DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				ByteArrayInputStream bis = new ByteArrayInputStream(xmlResult.getBytes());
				Document doc = db.parse(bis);
				
				NodeList nodes = doc.getElementsByTagName("mon");
				
				// iterate the employees
				for (int i = 0; i < nodes.getLength(); i++) {
					Element element = (Element) nodes.item(i);
					
					NodeList Pps = element.getElementsByTagName("tenMon");
					Element line = (Element) Pps.item(0);
					Result = Result +  getCharacterDataFromElement(line)+ ":";	
					
					NodeList diem = element.getElementsByTagName("diem");
					line = (Element) diem.item(0);
					Result = Result +  Float.parseFloat(getCharacterDataFromElement(line))/100 +" -";
				}
			}
			catch (Exception e) {
				Util.logger.info("get ResultDiem failed: " + e.getMessage());
				return null;
			} 
		}
		return Result;
	}
	
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
		   CharacterData cd = (CharacterData) child;
		   return cd.getData();
		}
		return "?";
		}
	//Check xem mon da co trong table chua
	private Vector isExistMaTruong_DH() {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		Vector result = null;
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select matruong from  matruong_diemthi";

			result = DBUtils.getVectorTable(connection, query1);
			if (result.size() > 0) {
				return result;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistMaMon Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}
		return result;
	}
	
	//Check xem mon da co trong table chua
	private boolean isExistMaMon(String mamon) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  dapan_thamkhao where mamon = '"+ mamon+ "'";

			Vector result = DBUtils.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistMaMon Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}
	
	//Check xem mon da co trong table chua
	private boolean isExistMaMon_CD(String mamon) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  mamon_CD where mamon = '"+ mamon+ "'";

			Vector result = DBUtils.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistMaMon Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}
	//Check xem trong table result_diemthi da co chua
	private boolean isExistUserid(String info, String result_kq) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  result_diemthi where info='"
					+ info
					+ "' and result= '"
					+ result_kq
					+ "'";

			Vector result = DBUtils.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistUserid Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}
	
	private int insert_result(String info, String result) {
		int ireturn = 1;
		String sqlUpdate = "insert into result_diemthi( info, result)"
				+ " values('"
				+ info
				+ "','"
				+ result
				+ "')";
		try {

			if (DBUtil.executeSQL("gateway", sqlUpdate) < 0) {
				Util.logger.info("Insert Statement: Insert result_diemthi Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.info("Insert Statement: Insert result_diemthi Failed");
			ireturn = -1;
		} 
		return ireturn;
	}
	
	//Check xem trong table result_diemthi da co chua
	private String result_diemthi(String info) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String result_diemthi = "";
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select result from  result_diemthi where info='"+ info+ "'"; 

			Vector result = DBUtils.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);
				result_diemthi = String.valueOf(item.elementAt(0));
				return result_diemthi;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistUserid Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return result_diemthi;
	}
	/*
	 * Thay nhieu dau _____ -> _
	 */
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
	
	public static String Http_post(String surl) throws IOException {
		URL url = new URL(surl);
		String data = "";
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setConnectTimeout(15000);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			// Util.logger.info("getResponseCode : " +
			// connection.getResponseCode());
			String line = null;
			while ((line = reader.readLine()) != null) {
				data += line;
			}
			//System.out.println("result "+line);
			
		} catch (Exception e) {
			Util.logger.info("Call API failed: " + e.getMessage());
			return null;
		}finally {
		      if(connection != null) {
		    	  connection.disconnect(); 
		      }
		    }
	//	 Util.logger.info("data :   " + data);
		return data;
	}
	
	/*public static void main(String[] args)
	{
		try
		{
		String url="http://210.211.101.5/WebTS/api/LoadData.jsp?gameID=20&type=3&sbd=DDKA.27267&sID=DDK&khoi=A&request_id=9053395&user_id=841202765089&service_id=9209&mobile_operator=VMS&command_code=DT&infor=DT%20DDKADDKA.27267";
		Http_post(url);
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			
		}
		catch(Throwable ex)
		{
			System.out.println(ex.getMessage());	
		}
	}*/
	
	private static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";
	
		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);
	
		return (f > l) ? "" : xml.substring(f, l);
	}
	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "&");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			// setParameter(key, value);
			_params.put(key, value);
		}

		return _params;

	}

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)
					|| temp == null) {
				return _defaultval;
			}
			return temp;
		} catch (Exception e) {
			return _defaultval;
		}
	}
}
