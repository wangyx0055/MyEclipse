package tracuudiemthi;



import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBInsert;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




public class DiemThi_3 extends Thread{
	
public void run(){
		
		while(Sender.processData){
				try {
					getMessages();
					Thread.sleep(30*1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	protected Collection getMessages() throws Exception {
		String url = Constants._prop.getProperty("url_tuyensinh");
		String resultDT = "";
		MsgObject msgObject = new MsgObject();
		Connection connection = null;
		DBPool dbpool = new DBPool();
		connection = dbpool.getConnectionGateway();
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
		String count_call = "";
		String count_sms = "";
		String charge_status = "";
		String charge_count = "";
		
		try {

			//String query3 = "select REQUEST_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, USER_ID, STATUS, COUNT_SMS from mlist_diemthi_dk where CHARGE_STATUS = 1 and RESPONSE_STATUS=0 and COMMAND_CODE = 'DT'";
			String query3 = "select REQUEST_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, USER_ID, STATUS, COUNT_SMS,  CHARGE_STATUS, CHARGE_COUNT from mlist_diemthi_dk where RESPONSE_STATUS=0 and COMMAND_CODE = 'DT' AND MOD(ID,5)=3";
		//	Util.logger.info(query3);

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
					count_call = String.valueOf(item.elementAt(6));
					count_sms = String.valueOf(item.elementAt(7));
					charge_status = String.valueOf(item.elementAt(8));
					charge_count = String.valueOf(item.elementAt(9));
					String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
					String ma_truong = "";
					String sbd = "";
					String sbd1 = "";
					String ma_khoi = "";
					String ma_khoi1 = "";
					Util.logger.info("MO diemthi: " + info);
					if(sTokens.length ==2) {
						if(sTokens[1].length() > 4) {
							ma_truong = sTokens[1].trim().substring(0, 3);
							ma_khoi = sTokens[1].substring(3, 4);
							sbd = sTokens[1].substring(4, sTokens[1].length());
							if("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi)) {
								//cat so tu sau ma khoi
								ma_khoi1 = sTokens[1].substring(3, 5);
								sbd1 = sTokens[1].substring(5, sTokens[1].length());
							}
						} 
					} else if(sTokens.length ==4) {
							ma_truong = sTokens[1];
							ma_khoi = sTokens[2];
							sbd = sTokens[3];
					} else if(sTokens.length == 1) {
						ma_truong = info.substring(2, 5); //sTokens[0].substring(2, 5);
						ma_khoi = info.substring(5, 6);
						sbd = info.substring(6, info.length());
						if("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi)) {
							//cat so tu sau ma khoi
							ma_khoi1 = info.substring(5, 7);
							sbd1 = info.substring(7, info.length());
						}
							
					} else if(sTokens.length == 3) {
						ma_truong = sTokens[1].trim().substring(0, 3);
					//	sbd = sTokens[2];
						if(sTokens[1].length() > 3) {
							ma_khoi = sTokens[1].trim().substring(3, sTokens[1].length());
							sbd = sTokens[2];
						} else {
							ma_khoi = sTokens[2].substring(0, 1);
							sbd = sTokens[2].substring(1, sTokens[2].length());
						}
						
					}  else {
						deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
						insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, count_sms);
						return null;
					}
					
					if("0".equalsIgnoreCase(charge_status) && "1".equalsIgnoreCase(charge_count) && "0".equalsIgnoreCase(count_call)) {
						String sqlUpdateRunning = "update mlist_diemthi_dk set STATUS=1 where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
						DBUtil.executeSQL("gateway", sqlUpdateRunning);
						msgObject.setUsertext("Ban da dang ky thanh cong goi dich vu Diem thi.Cuoc phi su dung: 3000d/sms.Hien tai khoan cua ban khong du de su dung, cuoc phi se duoc tinh sau khi ban nap tien vao tai khoan. Diem se cap nhat ngay khi truong thi co ket qua.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(21);
						msgObject.setUserid(user_id);
						msgObject.setServiceid(service_id);
						msgObject.setCommandCode(command_code);
						DBUtils.sendMT(msgObject);
						return null;
					} else if("1".equalsIgnoreCase(charge_status)) {
						check_result = result_diemthi(info);
						if(check_result!= "") {
							msgObject.setUsertext(check_result);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							msgObject.setUserid(user_id);
							msgObject.setServiceid(service_id);
							msgObject.setCommandCode(command_code);
							DBUtils.sendMT(msgObject);
							String sqlUpdateRunning = "update mlist_diemthi_dk set RESPONSE_STATUS=1 where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
							DBUtil.executeSQL("gateway", sqlUpdateRunning);
							return null;
						} else {
							String api_diemthi= ""; 
							String api_diemthi1= "";
							if(sTokens.length ==2) {
								if(sbd!= "" && ma_truong != "" && ma_khoi != "") {
									api_diemthi = url+"gameID=20&type=3&sbd="+sbd+"&sID="+ma_truong+"&khoi="+ma_khoi+"&request_id=" +requestID +
									"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
								} else {
									deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
									return null;
								}
								if("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi)) {
									if(sbd1!= "" && ma_truong != "" && ma_khoi1 != "") {
										api_diemthi1 = url+"gameID=20&type=3&sbd="+sbd1+"&sID="+ma_truong+"&khoi="+ma_khoi1+"&request_id=" +requestID +
										"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
									} else {
										deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
										return null;
									}
								}
							} else if(sTokens.length == 4) {
								if(sbd!= "" && ma_truong != "" && ma_khoi != "") {
									api_diemthi = url+"gameID=20&type=3&sbd="+sTokens[3]+"&sID="+ma_truong+"&khoi="+ma_khoi+"&request_id=" +requestID +
									"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
								} else {
									deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
									return null;
								}
							} else if(sTokens.length == 1) {
								if(sbd!= "" && ma_truong != "" && ma_khoi != "") {
									api_diemthi = url+"gameID=20&type=3&sbd="+sbd+"&sID="+ma_truong+"&khoi="+ma_khoi+"&request_id=" +requestID +
									"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
								} else {
									deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
									return null;
								}
								if("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi)) {
									if(sbd1!= "" && ma_truong != "" && ma_khoi1 != "") {
										api_diemthi1 = url+"gameID=20&type=3&sbd="+sbd1+"&sID="+ma_truong+"&khoi="+ma_khoi1+"&request_id=" +requestID +
										"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
									} else {
										deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
										return null;
									}
								}
						} else if(sTokens.length == 3) {
							if(sbd!= "" && ma_truong != "" && ma_khoi != "") {
								api_diemthi = url+"gameID=20&type=3&sbd="+sbd+"&sID="+ma_truong+"&khoi="+ma_khoi+"&request_id=" +requestID +
								"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
							} else {
								deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
								return null;
							}
							if("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi)) {
								if(sbd1!= "" && ma_truong != "" && ma_khoi1 != "") {
									api_diemthi1 = url+"gameID=20&type=3&sbd="+sbd1+"&sID="+ma_truong+"&khoi="+ma_khoi1+"&request_id=" +requestID +
									"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
								} else {
									deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
									return null;
								}
							}
						} else {
							deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
							return null;
						}
							
							//GOI API LAY Diemthi
							//neucos diem
							if(isExistMaTruong_DH(ma_truong)) {
								//goi API lay dap an chinh thuc
								api_diemthi = api_diemthi.replace(" ","%20");
								Util.logger.info("API call diem: " + api_diemthi);
								xml = Http_post(api_diemthi);
								if(xml != "") { 
									status = getValue(xml, "status");
									if("1".equalsIgnoreCase(status)) {
										mon = getValue(xml, "mon");
										tongdiem = getValue(xml, "tongdiem");
										tents = getValue(xml, "name");
										mon = resultDiem(xml);
										if(mon != "") {
											result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sbd +" -" + mon.substring(0, mon.length()-1);
											/*if(sTokens.length ==2) {
											result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sTokens[1]+" -" + mon.substring(0, mon.length()-1);
										} else if(sTokens.length > 2){
											result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sTokens[3]+" -" + mon.substring(0, mon.length()-1);
										}*/
											msgObject.setUsertext(result);
											msgObject.setMsgtype(1);
											msgObject.setContenttype(0);
											msgObject.setUserid(user_id);
											msgObject.setServiceid(service_id);
											msgObject.setCommandCode(command_code);
											DBUtils.sendMT(msgObject);
											if(!isExistUserid(info, result)) {
												insert_result(info, result);
											}
											//insertMT.sendMT(msgObject);
											String sqlUpdateRunning = "update mlist_diemthi_dk set RESPONSE_STATUS=1, COUNT_SMS=COUNT_SMS+1, PARTNER = 'Goldsoft' where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
											DBUtil.executeSQL("gateway", sqlUpdateRunning);
											return null;
										} else {
											return null;
										}
										
									} else if("0".equalsIgnoreCase(status)){
										String sqlUpdateRunning1 = "update mlist_diemthi_dk set COUNT_SMS=COUNT_SMS+1, PARTNER = 'Goldsoft' where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
										DBUtil.executeSQL("gateway", sqlUpdateRunning1);
										if(sTokens.length != 4 &&("A".equalsIgnoreCase(ma_khoi) || "D".equalsIgnoreCase(ma_khoi))) {
											api_diemthi1 = api_diemthi1.replace(" ","%20");
											Util.logger.error("API call diem: " + api_diemthi1);
											xml = Http_post(api_diemthi1);
											if(xml != "") {
												status = getValue(xml, "status");
												if("1".equalsIgnoreCase(status)) {
													mon = getValue(xml, "mon");
													tongdiem = getValue(xml, "tongdiem");
													tents = getValue(xml, "name");
													mon = resultDiem(xml);
													/*if(sTokens.length ==2) {
														result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sTokens[1]+" -" + mon.substring(0, mon.length()-1);
													} else if(sTokens.length > 2){
														result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sTokens[3]+" -" + mon.substring(0, mon.length()-1);
													}*/
													if(mon !="") {
														result = "Diem Thi DH 2012 *TS: "+ tents + " *SBD: " + sbd1+" -" + mon.substring(0, mon.length()-1);
														msgObject.setUsertext(result);
														msgObject.setMsgtype(1);
														msgObject.setContenttype(0);
														msgObject.setUserid(user_id);
														msgObject.setServiceid(service_id);
														msgObject.setCommandCode(command_code);
														DBUtils.sendMT(msgObject);
														if(!isExistUserid(info, result)) {
															insert_result(info, result);
														}
														//insertMT.sendMT(msgObject);
														String sqlUpdateRunning = "update mlist_diemthi_dk set RESPONSE_STATUS=1, COUNT_SMS=COUNT_SMS+1, PARTNER = 'Goldsoft' where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
														DBUtil.executeSQL("gateway", sqlUpdateRunning);
														return null;
														
													} else {
														return null;
													}
													
												} else if("0".equalsIgnoreCase(status)){
													String sqlUpdateRunning = "update mlist_diemthi_dk set RESPONSE_STATUS=1, COUNT_SMS=COUNT_SMS+1, PARTNER = 'Goldsoft' where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
													DBUtil.executeSQL("gateway", sqlUpdateRunning);
													Util.logger.error("Goi API nhung ko co so bao danh do");
													msgObject.setUsertext("Sai so bao danh, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
													msgObject.setMsgtype(0);
													msgObject.setContenttype(0);
													msgObject.setUserid(user_id);
													msgObject.setServiceid(service_id);
													msgObject.setCommandCode(command_code);
													DBUtils.sendMT(msgObject);
													deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
													insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, count_sms);
													return null;
												} else {
													return null;
												}
											}
											
										} else {
											msgObject.setUsertext("Sai so bao danh, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
											msgObject.setMsgtype(0);
											msgObject.setContenttype(0);
											msgObject.setUserid(user_id);
											msgObject.setServiceid(service_id);
											msgObject.setCommandCode(command_code);
											DBUtils.sendMT(msgObject);
											deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
											insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, count_sms);
											return null;
										}
									}  else if("-1".equalsIgnoreCase(status)){
										Util.logger.error("Truyen thieu tham so: " + status);
										return null;
									}
									
								} else {
									return null;
								}
								
							} else if("0".equalsIgnoreCase(count_call)){
								String sqlUpdateRunning = "update mlist_diemthi_dk set STATUS=1 where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
								DBUtil.executeSQL("gateway", sqlUpdateRunning);
								Util.logger.error("Update status = 1: " + sqlUpdateRunning);
								//chua co dap an tham khao
								msgObject.setUsertext("Ban dang ky thanh cong dich vu. Ban se nhan duoc tin tra loi ngay khi truong co diem.");
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								msgObject.setUserid(user_id);
								msgObject.setServiceid(service_id);
								msgObject.setCommandCode(command_code);
								DBUtils.sendMT(msgObject);
								return null;
							}
						}
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
			dbpool.cleanup(connection);
		}
		return null;
	}
	
	private int insertBackup_diemthi(String user_id,String service_id,String mobile_operator,String command_code,String info, String request_id, String count_sms) {
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
	private boolean isExistMaTruong_DH(String matruong) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  matruong_diemthi where matruong = '"+ matruong+ "'";

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
			
		} catch (Exception e) {
			return null;
		}finally {
		      if(connection != null) {
		    	  connection.disconnect(); 
		      }
		    }
		// Util.logger.info("data :   " + data);
		return data;
	}
	
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
