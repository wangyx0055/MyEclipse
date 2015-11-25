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
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tempuri.SendMOICom2HTSSoapProxy;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sub.DeliveryManager;



public class DiemChuan extends Thread{
	
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
		String kid = "";//khoi
		String dh = "";//1 dh, 0 cd
		String mid = "";//mon
		String xml = "";
		String nganh = "";
		String status = "";
		String tongdiem = "";
		String result = "";
		String tents = "";
		String nam = "";
		String count_call = "";
		String charge_status = "";
		String charge_count = "";
		int count_sms = 0;
		try {

		//	String query3 = "select REQUEST_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, USER_ID, STATUS from mlist_diemthi_dk where CHARGE_STATUS = 1 and RESPONSE_STATUS=0 and COMMAND_CODE = 'DC'";
			
			String query3 = "select REQUEST_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, USER_ID, STATUS, CHARGE_STATUS, CHARGE_COUNT from mlist_diemthi_dk where RESPONSE_STATUS=0 and COMMAND_CODE = 'DC'";

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
					if(sTokens.length==3) {
						nam = sTokens[2];
					} else if(sTokens.length==2) {
						Calendar now = Calendar.getInstance();
						nam = String.valueOf(now.get(Calendar.YEAR));
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
							String api_diemthi= ""; //sTokens[1]
							api_diemthi = url+"gameID=20&type=18&sID="+sTokens[1]+"&request_id=" +requestID +
							"&user_id="+ user_id+"&service_id="+service_id+"&mobile_operator="+mobile_operator+"&command_code="+command_code+"&infor="+info+"";
							
							//GOI API LAY Diemthi
							
							//neucos diem
							if(isExistMaTruong_DH(sTokens[1])) {
								//goi API lay dap an chinh thuc
								api_diemthi = api_diemthi.replace(" ","%20");
								xml = Http_post(api_diemthi);
								status = getValue(xml, "status");
								if("1".equalsIgnoreCase(status)) {
									nganh = getValue(xml, "nganh");
									
									nganh = resultDiem(xml);
									result = "Diem chuan DH "+sTokens[1]+" "+nam+" -" + nganh.substring(0, nganh.length()-1);
									
									String sqlUpdate = "update mlist_diemthi_dk set COUNT_SMS=COUNT_SMS+1, PARTNER = 'Goldsoft' where USER_ID=" + user_id + " and COMMAND_CODE= " + "'"+command_code+"' and REQUEST_ID=" +requestID;
									DBUtil.executeSQL("gateway", sqlUpdate);
									
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
									String sqlUpdateRunning = "update mlist_diemthi_dk set RESPONSE_STATUS=1 where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
									DBUtil.executeSQL("gateway", sqlUpdateRunning);
								} else if("-1".equalsIgnoreCase(status)){
									Util.logger.error("Truyen thieu tham so: " + status);
									return null;
								} else if("0".equalsIgnoreCase(status)){
									Util.logger.error("Khong tim thay du lieu: " + status);
									return null;
								}
								
							} else if("0".equalsIgnoreCase(count_call)){
								String sqlUpdateRunning = "update mlist_diemthi_dk set STATUS=1 where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
								DBUtil.executeSQL("gateway", sqlUpdateRunning);
								Util.logger.error("Update status = 1: " + sqlUpdateRunning);
								//chua co dap an tham khao
								msgObject.setUsertext("Ban da dang ky thanh cong dich vu. Ban se nhan duoc tin nhan tra loi ngay khi truong cong bo diem chuan.");
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
	
	public static String resultDiem(String result) throws Exception{
		String Result ="";
		String xmlResult  = result;
		
		if(xmlResult!="") {
		
			try {
				DocumentBuilderFactory dbf =   DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				ByteArrayInputStream bis = new ByteArrayInputStream(xmlResult.getBytes());
				Document doc = db.parse(bis);
				
				NodeList nodes = doc.getElementsByTagName("nganh");
				
				// iterate the employees
				for (int i = 0; i < nodes.getLength(); i++) {
					Element element = (Element) nodes.item(i);
					
					NodeList Pps = element.getElementsByTagName("ten");
					Element line = (Element) Pps.item(0);
					Result = Result +  getCharacterDataFromElement(line)+ ":";	
					
					NodeList diem = element.getElementsByTagName("diemchuan");
					line = (Element) diem.item(0);
					Result = Result +  getCharacterDataFromElement(line) +"-";
				}
			}
			catch (Exception e) {
				e.printStackTrace();
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
