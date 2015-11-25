package tracuudiemthi;



import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.tempuri.SendMOICom2HTSSoapProxy;



public class Dangky extends QuestionManager {
	
	String url = Constants._prop.getProperty("url_tuyensinh");

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		Collection messages = new ArrayList();
		String info = msgObject.getUsertext();
		String user_id = msgObject.getUserid();
		String service_id = msgObject.getServiceid();
		int service_type = keyword.getService_type();
		String mobile_operator = msgObject.getMobileoperator();
	//	String command_code = msgObject.getCommandCode();
		String command_code = msgObject.getCommandCode();
		int content_type = msgObject.getContenttype();
		String requestID = String.valueOf(msgObject.getRequestid());
		//String info = msgObject.get
		String message_type = String.valueOf(msgObject.getMsgtype());
		int amount = 3000;
		int resultDT = 0;
		List<String> lstKhoi = new ArrayList<String>();
		lstKhoi.add("A1");
		lstKhoi.add("A");
		lstKhoi.add("B");
		lstKhoi.add("C");
		lstKhoi.add("D");
		lstKhoi.add("D1");
		lstKhoi.add("D2");
		lstKhoi.add("D3");
		lstKhoi.add("D4");
		lstKhoi.add("D5");
		lstKhoi.add("D6");
		
		List<String> lstMon = new ArrayList<String>();
		lstMon.add("SINH");
		lstMon.add("VAN");
		lstMon.add("TOAN");
		lstMon.add("DUC");
		lstMon.add("LY");
		lstMon.add("HOA");
		lstMon.add("ANH");
		lstMon.add("TRUNG");
		lstMon.add("NHAT");
		lstMon.add("NGA");
		lstMon.add("PHAP");
		lstMon.add("DIA");
		
		//info = replaceAllWhiteWithOne(info);
		
		String[] sTokens = info.split(" ");
		String ma_truong = "";
		String info_diemthi = "";
		try {
			if("DT".equalsIgnoreCase(command_code)) {
				info = info.replace("-", "");
				info = info.replace(";", "");
				info = info.replace("%", "");
				info = info.replace("#", "");
				info = info.replace("<", "");
				info = info.replace(">", "");
				info = info.replace("(", "");
				info = info.replace(")", "");
				info = info.replace("[", "");
				info = info.replace("]", "");
				info = info.replace("-", "");
				info = info.replace(",", "");
				info = info.replace(".", "");
				info = info.replace("%", "");
				info = info.replace("{", "");
				info = info.replace("}", "");
				info = info.replace("/", "");
				info = info.replace("?", "");
				info = info.replace("$", "");
				info = info.replace("&", "");
				info = info.replace("@", "");
				info = info.replace("*", "");
				info = info.replace("'", "");
				info = info.replaceAll(" ", "");
				//dtbkad
				String sbd = info.substring(msgObject.getKeyword()
						.length(), info.length());
				
				String susertext = msgObject.getKeyword() + " " + sbd;
				String matruong = "";
				String makhoi = "";
				String sdb = "";
				String check_infor = "";
				String status = "";
				status = isExistSBD(msgObject.getUserid(), susertext);
				if("0".equalsIgnoreCase(status)) {
					msgObject.setUsertext("So bao danh nay ban da tra cuu truoc do, ban se nhan duoc tin tra loi ngay khi truong co diem.");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					msgObject.setUserid(user_id);
					msgObject.setServiceid(service_id);
					msgObject.setCommandCode(command_code);
					DBUtils.sendMT(msgObject);
					return null;
				/*} else if("1".equalsIgnoreCase(status)) {
					msgObject.setUsertext("So bao danh ban tra cuu he thong da gui diem ve cho khach hang. Cam on ban da su dung dich vu cua MobiFone. ");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					msgObject.setUserid(user_id);
					msgObject.setServiceid(service_id);
					msgObject.setCommandCode(command_code);
					DBUtils.sendMT(msgObject);
					return null;*/
				} else {
					//voi sTokens =1: 
					// 1. nhung truong hop viet lien, viet hoa thi deu cho la dung cu phap.
					if(sTokens[0].toUpperCase().startsWith("DT")) {
						info = susertext;
						//	info = susertext;
						if(info.length() < 6) {
							info = susertext;
							//	insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
							msgObject.setUsertext("Tin nhan chua hop le, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							msgObject.setUserid(user_id);
							msgObject.setServiceid(service_id);
							msgObject.setCommandCode(command_code);
							DBUtils.sendMT(msgObject);
							return null;
						} else {
							matruong = sbd.substring(0, 3);
							makhoi = sbd.substring(3, 4);
							info = susertext;
							//TH dtsbd
							if(!checkMaTruong(matruong)) {
								if(!checkMaTruong_API(matruong)) {
									insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
									msgObject.setUsertext("Kiem tra lai MaTruong, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
									msgObject.setMsgtype(0);
									msgObject.setContenttype(0);
									msgObject.setUserid(user_id);
									msgObject.setServiceid(service_id);
									msgObject.setCommandCode(command_code);
									DBUtils.sendMT(msgObject);
									return null;
								}
							}
							//check ma khoi
							if(!makhoi.matches("[a-zA-Z]+$")) {
								info = susertext;
								insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
								msgObject.setUsertext("Sai ma khoi thi, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
								msgObject.setMsgtype(0);
								msgObject.setContenttype(0);
								msgObject.setUserid(user_id);
								msgObject.setServiceid(service_id);
								msgObject.setCommandCode(command_code);
								DBUtils.sendMT(msgObject);
								return null;
							} 
							
							if(!sbd.substring(4, sbd.length()).matches("[0-9]+$")) {
								info = susertext;
								//	insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
								msgObject.setUsertext("Sai so bao danh, ban vui long soan lai theo VD: DT BKAA12345 gui 9209 (Ban luu y Sobaodanh bao gom ca chu va so Vi du ban thi khoi A truong Bach Khoa thi sobaodanh day du la: BKAA12345, vui long soan tin: DT BKAA12345 gui 9209). Chi tiet goi 9244");
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
				
			if("DC".equalsIgnoreCase(command_code)&& sTokens.length <2) {
				insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
				msgObject.setUsertext("Tin nhan sai cu phap, de xem diem chuan cua truong soan: DC matruong gui 9209. Chi tiet goi 9244");
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				msgObject.setUserid(user_id);
				msgObject.setServiceid(service_id);
				msgObject.setCommandCode(command_code);
				DBUtils.sendMT(msgObject);
				return null;
			} else if("DC".equalsIgnoreCase(command_code)){
				if(!checkMaTruong(sTokens[1])) {
					insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
					return null;
				}
			}
			
			if("DADH".equalsIgnoreCase(command_code) && sTokens.length <3) {
				insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
				return null;
			} else if("DADH".equalsIgnoreCase(command_code)){
				if(!lstKhoi.contains(sTokens[1].toUpperCase())) {
					insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
					return null;
				} else if(!lstMon.contains(sTokens[2].toUpperCase())) {
					insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
					return messages;
				}
			}
			
			
			if("DACD".equalsIgnoreCase(command_code) && sTokens.length <3) {
				insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
				return null;
			} else if("DACD".equalsIgnoreCase(command_code)){
				if(!lstKhoi.contains(sTokens[1].toUpperCase())) {
					insertBackup_diemthi(user_id, service_id, mobile_operator, command_code, info, requestID);
					return null;
				}
			}
					//insert va table charge_diemthi
					//String user_id,String service_id,String mobile_operator,String command_code,String content_type,String info,String message_type,String amount
					//Neu charge thanh cong thi goi sv ben doi tac.
					//    + neu goi ws co ket qua thi tra luon cho kh. Neu chua co thi luu vao mlist_diemthi_dk
					
					// Insert vào table mlist neu result = 1 thi insert vao charge_diemthi
					int insert_mlist = insert_mlist_DK(user_id, service_id, mobile_operator, command_code, info, requestID);
	 				int result = 0;
					if(insert_mlist ==1) {
						insertCharge_diemthi(user_id, service_id, mobile_operator, command_code, info, content_type, message_type, amount, command_code, requestID, service_type);
						
					}
			} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	
	private String isExistSBD(String sdt, String info) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String status = "";
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select RESPONSE_STATUS from  mlist_diemthi_dk where USER_ID = '"+sdt+"' and INFO = '"+info+"'";

			Vector result = DBUtils.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				status = (String)item.elementAt(0);
				return status;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistSBD Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return status;
	}
	private int insertBackup_diemthi(String user_id,String service_id,String mobile_operator,String command_code,String info, String request_id) {
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
				+ "',0,0)";
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

	//Check xem mon da co trong table chua
	private boolean checkMaTruong_API(String matruong) {
		Boolean result = false;
		 String urlCheckTruong = url +"gameID=20&type=24&sID=" +matruong;
		 Util.logger.info("call:" + urlCheckTruong);
		 String check = urlCheckTruong.replace(" ","%20");
		 String xml = "";
		 try {
			 xml = Http_post(urlCheckTruong);
			 if(xml != "") {
				 String status = getValue(xml, "status");
				 if("1".equalsIgnoreCase(status)) {
					 result = true;
				 } else {
					 result = false;
				 }
			 }
			
		} catch (Exception e) {
			Util.logger.error("Exception: " +e);
		}
		 return result;
	}
	
	private boolean checkMaTruong(String matruong) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  ma_truong where matruong = '"+ matruong+ "'";

			Vector result = DBUtils.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("check matruong Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}
	
	private int insertCharge_diemthi(String user_id,String service_id,String mobile_operator,String command_code,String info, int content_type, String message_type,int amount, String service_name, String request_id, int service_type) {
		int ireturn = 1;
		
		String sqlUpdate = "insert into charge_diemthi(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO , SUBMIT_DATE, DONE_DATE, RESULT_CHARGE, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, AMOUNT)"
				+ " values('"
				+ user_id
				+ "','049209','"
				+ mobile_operator
				+ "','"
				+ command_code
				+ "','"
				+ content_type
				+ "','"
				+ info
				+ "','"
				+  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				+ "','"
				+  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				+ "',0,1,'"+request_id+"',0,0,0,'"
				+ command_code
				+ "', 0,"+service_type+",3000)";

		try {
			Util.logger.info("Insert into table charge_diemthi: " + sqlUpdate);
			if (DBUtil.executeSQL("gateway", sqlUpdate) < 0) {
				Util.logger.info("Insert Statement: Insert charge_diemthi Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.info("Insert Statement: Insert charge_diemthi Failed");
			ireturn = -1;
		}
		return ireturn;
	}
	
	private int insert_mlist_DK(String user_id,String service_id,String mobile_operator,String command_code,String info, String request_id) {
		int ireturn = 1;
		String sqlUpdate = "insert into mlist_diemthi_dk( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, REQUEST_ID)"
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
				+ request_id + "')";
		try {
			Util.logger.info("Insert into table mlist_diemthi_dk: " + sqlUpdate);
			if (DBUtil.executeSQL("gateway", sqlUpdate) < 0) {
				Util.logger.info("Insert Statement: Insert mlist_diemthi_dk Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.info("Insert Statement: Insert mlist_diemthi_dk Failed");
			ireturn = -1;
		} 
		return ireturn;
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


	private static boolean saverequest(String userid, String code,
			String sobaodanh) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into tuyensinh_diemthi_dk( userid,matruong,sobaodanh) values ('"
					+ userid + "','" + code + "','" + sobaodanh + "')";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to tuyensinh_diemchuan_dk");
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
	public static String Http_post(String surl) throws IOException {
		URL url = new URL(surl);
		String data = "";
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(15000);
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

}
