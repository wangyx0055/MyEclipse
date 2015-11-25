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


public class DiemThi_ThanhCong extends Thread{
	private int processIndex;
	private int processNum;
	public DiemThi_ThanhCong(int processIndex,int processNum)
	{
		this.processIndex=processIndex;
		this.processNum=processNum;
	}
	
public void run(){
		
		while(Sender.processData){
				try {
					getMessages();
					Thread.sleep(10*1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	protected Collection getMessages() throws Exception {
		String url = Constants._prop.getProperty("url_tuyensinh");
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
		String count_call = "";
		String count_sms = "";
		String charge_status = "";
		String charge_count = "";
		
		try {

			String query3 = "select REQUEST_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, USER_ID, STATUS, COUNT_SMS,  CHARGE_STATUS, CHARGE_COUNT from mlist_diemthi_dk where RESPONSE_STATUS=0 and COMMAND_CODE = 'DT' and STATUS = 0 And MOD(ID,"+processNum+")="+processIndex+" Limit 100";
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
					if("0".equalsIgnoreCase(charge_status) && "1".equalsIgnoreCase(charge_count) && "0".equalsIgnoreCase(count_call)) {
						String sqlUpdateRunning = "update mlist_diemthi_dk set STATUS=1 where COMMAND_CODE='"+ command_code+"' and USER_ID=" + user_id + " and REQUEST_ID=" +requestID;
						DBUtil.executeSQL("gateway", sqlUpdateRunning);
						msgObject.setUsertext("Ban da dang ky thanh cong goi dich vu Diem thi.Cuoc phi su dung: 3000d/sms.Hien tai khoan cua ban khong du de su dung, cuoc phi se duoc tinh sau khi ban nap tien vao tai khoan. Diem se cap nhat ngay khi truong thi co ket qua.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(21);
						msgObject.setUserid(user_id);
						msgObject.setServiceid(service_id);
						msgObject.setCommandCode(command_code);
						Sender.msgPushMTQueue.add(msgObject);
					} else if("1".equalsIgnoreCase(charge_status) && "0".equalsIgnoreCase(count_call)) {
						//check xem truong co diem chua.
						if(!isExistMaTruong_DH(ma_truong)) {
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
							Sender.msgPushMTQueue.add(msgObject);
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
		}
		return null;
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
