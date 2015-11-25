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

import org.tempuri.SendMOICom2HTSSoapProxy;

import sub.DeliveryManager;



public class DapAn extends Thread{
	
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
		DBInsert insertMT = new DBInsert();
		MsgObject msgObject = new MsgObject();
		Connection connection = null;
		DBPool dbpool = new DBPool();
		connection = dbpool.getConnectionGateway();
		String user_id = "";
		String info = "";
		String service_id = "";
		String mobile_operator = "";
		String command_code = "";
		int content_type = 0;
		String requestID ="";
		String check_result = "";
		String dh = "";//1 dh, 0 cd
		String xml = "";
		String dapan = "";
		String status = "";
		String made = "";
		String count_call = "";
		String charge_status = "";
		String charge_count = "";
		try {
			
		//	String query3 = "select REQUEST_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, USER_ID, STATUS from mlist_diemthi_dk where CHARGE_STATUS = 1 and RESPONSE_STATUS=0 and COMMAND_CODE IN('DADH', 'DACD')";
			String query3 = "select REQUEST_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, USER_ID, STATUS, CHARGE_STATUS, CHARGE_COUNT from mlist_diemthi_dk where RESPONSE_STATUS=0 and COMMAND_CODE IN('DADH', 'DACD')";
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
					charge_status = String.valueOf(item.elementAt(7));
					charge_count = String.valueOf(item.elementAt(8));
					
					String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
					
					String khoi_mon = "";
					if(sTokens.length==3) {
						made = "000";
					} else {
						made = sTokens[3];
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
							if("DADH".equalsIgnoreCase(command_code)) {
								dh = "1";
							} else if("DACD".equalsIgnoreCase(command_code)) {
								dh = "0";
							} 
							
							khoi_mon = sTokens[1].trim().toUpperCase() + "-" + sTokens[2].trim().toUpperCase();
							String api_dapan = "";
							//Call api lay dap an
							api_dapan = url+"gameID=20&type=11&kid="+ sTokens[1] + "&dh="+dh+"&mid="+sTokens[2]+"&did="+made+"&request_id=" +requestID +
							"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
							
							//GOI API LAY DAP AN
							//+ check trong table mamon_dh/mamon_cd 
							//  - neu co thi goi api dap an.
							//  - neu chua co: check trong table dapan_mon: co thi goi api k thi thong bao chua co diem chuan
							if("DADH".equalsIgnoreCase(command_code)) {
								//neu dap an co chinh thuc
								if(isExistMamon_DH(khoi_mon)) {
									//goi API lay dap an chinh thuc
									api_dapan = api_dapan.replace(" ","%20");
									xml = Http_post(api_dapan);
									status = getValue(xml, "status");
									if(!"0".equalsIgnoreCase(status)) {
										dapan = getValue(xml, "dapan");
										String[] dapan1 = dapan.split("<dapan>");
										dapan = dapan1[1];
										Util.logger.info("Dap an cua bo GD: " + dapan);
									}
									
								} else {
									//check em co dap an tham khao chua
									if(isExistMaMon(khoi_mon)) {
										//Co
										//goi API lay dap an chinh thuc
										api_dapan = api_dapan.replace(" ","%20");
										xml = Http_post(api_dapan);
										status = getValue(xml, "status");
										if(!"0".equalsIgnoreCase(status)) {
											dapan = getValue(xml, "dapan");
											String[] dapan1 = dapan.split("<dapan>");
											dapan = dapan1[1];
											Util.logger.info("Dap an tham khao: " + dapan);
										}
									} else  if("0".equalsIgnoreCase(count_call)){
										String sqlUpdateRunning = "update mlist_diemthi_dk set STATUS=1 where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
										DBUtil.executeSQL("gateway", sqlUpdateRunning);
										//chua co dap an tham khao
										msgObject.setUsertext("Dang ky thanh cong. Ban se nhan duoc dap an tham khao som nhat va dap an cua Bo ngay khi he thong cap nhat");
										msgObject.setMsgtype(1);
										msgObject.setContenttype(0);
										msgObject.setUserid(user_id);
										msgObject.setServiceid(service_id);
										msgObject.setCommandCode(command_code);
										DBUtils.sendMT(msgObject);
										return null;
									}
								}
							} else if("DACD".equalsIgnoreCase(command_code)) {
								//neu dap an co chinh thuc
								if(isExistMaMon_CD(khoi_mon)) {
									//goi API lay dap an chinh thuc
									api_dapan = api_dapan.replace(" ","%20");
									xml = Http_post(api_dapan);
									status = getValue(xml, "status");
									if(!"0".equalsIgnoreCase(status)) {
										dapan = getValue(xml, "dapan");
										String[] dapan1 = dapan.split("<dapan>");
										dapan = dapan1[1];
									}
									
								} else if("0".equalsIgnoreCase(count_call)){
									String sqlUpdateRunning = "update mlist_diemthi_dk set STATUS=1 where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
									DBUtil.executeSQL("gateway", sqlUpdateRunning);
									//chua co dap an tham khao
									msgObject.setUsertext("Dang ky thanh cong. Ban se nhan duoc dap an tham khao som nhat va dap an cua Bo ngay khi he thong cap nhat");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									msgObject.setUserid(user_id);
									msgObject.setServiceid(service_id);
									msgObject.setCommandCode(command_code);
									DBUtils.sendMT(msgObject);
									return null;
								}
							}
						}
						
						/*String sqlUpdate = "update mlist_diemthi_dk set COUNT_SMS=COUNT_SMS+1, PARTNER = 'Goldsoft' where USER_ID=" + user_id + " and COMMAND_CODE= " + "'"+command_code+"' and REQUEST_ID=" +requestID;
						DBUtil.executeSQL("gateway", sqlUpdate);*/
						
						if(status.equalsIgnoreCase("1")) {
							msgObject.setUsertext(dapan);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(21);
							msgObject.setUserid(user_id);
							msgObject.setServiceid(service_id);
							msgObject.setCommandCode(command_code);
							DBUtils.sendMT(msgObject);
							if(!isExistUserid(info, dapan)) {
								insert_result(info, dapan);
							}
							//insertMT.sendMT(msgObject);
							String sqlUpdateRunning = "update mlist_diemthi_dk set RESPONSE_STATUS=1, COUNT_SMS=COUNT_SMS+1, PARTNER = 'Goldsoft' where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
							DBUtil.executeSQL("gateway", sqlUpdateRunning);
						} else if("-1".equalsIgnoreCase(status)){
							Util.logger.error("Truyen thieu tham so: " + status);
							return null;
						} else if("0".equalsIgnoreCase(status)){
							//xoa khoi mlist_diemthi_dk
							//insert vao backup_diemthi
							
							Util.logger.error("Khong tim thay du lieu do sai ma de");
							deleteUser(user_id, requestID, command_code, "mlist_diemthi_dk");
							insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID, count_call);
							/*if("DACD".equalsIgnoreCase(command_code)) {
								//chua co dap an tham khao
								msgObject.setUsertext("*Ban nhan sai ma de.*De nhan dap an cua Mon ban du thi, Soan:  DACD <Makhoi> <MonThi> <Made> gui 9209. VD: DACD A LY 749 Gui 9209");
							} else if("DADH".equalsIgnoreCase(command_code)) {
								msgObject.setUsertext("*Ban nhan sai ma de.*De nhan dap an cua Mon ban du thi, Soan:  DADH <Makhoi> <MonThi> <Made> gui 9209. VD: DADH A LY 749 Gui 9209");
							}
							msgObject.setMsgtype(0);
							msgObject.setServiceid(service_id);
							msgObject.setContenttype(0);
							msgObject.setUserid(user_id);
							msgObject.setCommandCode(command_code);
							DBUtils.sendMT(msgObject);*/
							return null;
						}
					}
					
				}
						
					}

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return null;
		} catch (Exception e) {
			Util.logger.info(this.getClass().getName() + "@" + "Khong goi duoc webservice ben doi tac");
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
	//Check xem mon da co trong table chua
	private boolean isExistMamon_DH(String mamon) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  mamon_DH where mamon = '"+ mamon+ "'";

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

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		// Util.logger.info("getResponseCode : " +
		// connection.getResponseCode());
		String line = null;
		String data = "";
		while ((line = reader.readLine()) != null) {
			data += line;
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
