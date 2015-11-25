package tracuudiemthi;



import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.DBUtils;

import icom.common.Util;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import org.tempuri.SendMOICom2HTSSoapProxy;



public class Dangky_Diem_DH extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		String username = Constants._prop.getProperty("user_tuyensinh");
		String pass = Constants._prop.getProperty("pass_tuyensinh");
		String url = Constants._prop.getProperty("url_tuyensinh");
		Collection messages = new ArrayList();
		String info = msgObject.getUsertext();
		String user_id = msgObject.getUserid();
		String service_id = msgObject.getServiceid();
		String mobile_operator = msgObject.getMobileoperator();
	//	String command_code = msgObject.getCommandCode();
		String command_code = msgObject.getCommandCode();
		int content_type = msgObject.getContenttype();
		String requestID = String.valueOf(msgObject.getRequestid());
		//String info = msgObject.get
		String message_type = String.valueOf(msgObject.getMsgtype());
		int amount = 3000;
		int resultDT = 0;
		
		info = replaceAllWhiteWithOne(info);
		
		String[] sTokens = info.split(" ");
		// String MASO = "1";
		/*if (sTokens.length == 1) {
			msgObject
			.setUsertext("Cu phap tin nhan khong hop le. Soan: DT <SoBaoDanh> gui 9209 de xem diem thi dai hoc nam 2012. HT: 1900571566");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			messages.add(new MsgObject(msgObject));
			return messages;
		}*/
		
		if("DIEM".equalsIgnoreCase(command_code) && sTokens.length < 3) {
			msgObject.setUsertext("Cu phap tin nhan khong hop le. Soan: DIEM <Tinh> <SoBaoDanh> gui 9209 de xem diem thi tot nghiep nam 2012.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("DT".equalsIgnoreCase(command_code) && sTokens.length <2) {
			msgObject.setUsertext("Cu phap tin nhan khong hop le. Soan: DT <SoBaoDanh> gui 9209 de tra diem thi dai hoc nam 2012.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("DC".equalsIgnoreCase(command_code)&& sTokens.length <2) {
			msgObject.setUsertext("Cu phap tin nhan khong hop le. Soan: DC <MaTruong> gui 9209 de tra diem chuan dai hoc nam 2012.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("VT".equalsIgnoreCase(command_code) && sTokens.length <2) {
			msgObject.setUsertext("Cu phap tin nhan khong hop le. Soan: VT <SoBaoDanh> gui 9209 de xem vi tri cua ban trong truong dai du thi 2012.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("DHNV2".equalsIgnoreCase(command_code) && sTokens.length <2) {
			msgObject.setUsertext("Cu phap tin nhan khong hop le. Soan: DHNV2 <MucDiem> gui 9209 de tra cuu nhung truong co chi tieu xet NV2 tu 1 muc diem nhat dinh.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("NV2".equalsIgnoreCase(command_code) && sTokens.length <2) {
			msgObject.setUsertext("Cu phap tin nhan khong hop le. Soan: NV2 <MaTruong> gui 9209 de tra chi tieu va diem xet tuyen NV2 cua truong.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("DADH".equalsIgnoreCase(command_code) && sTokens.length <4) {
			msgObject.setUsertext("*Ban nhan tin sai.*De nhan dap an cua Mon ban du thi>Soan: DADH <Khoi> <Mon> <MaDe> gui 9209>VD: DADH A TOAN 128 Gui 9209");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("DACD".equalsIgnoreCase(command_code) && sTokens.length <4) {
			msgObject.setUsertext("*Ban nhan tin sai.*De nhan dap an cua Mon ban du thi>Soan: DACD <Khoi> <Mon> <MaDe> gui 9209>VD: DACD A TOAN 128 Gui 9209");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		}  else if("DB".equalsIgnoreCase(command_code) && sTokens.length <3) {
			msgObject.setUsertext("*Cu phap tin nhan khong hop le. Soan: DB <SoBD> <MaTruong> gui 9209 de biet du bao dau hay truot");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("MD".equalsIgnoreCase(command_code) && sTokens.length <3) {
			msgObject.setUsertext("*Cu phap tin nhan khong hop le. Soan: MD <MaTruong> <MucDiem> gui 9209 de tra cuu so thi sinh dat cung 1 muc diem");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("CTDH".equalsIgnoreCase(command_code) && sTokens.length <2) {
			msgObject.setUsertext("*Cu phap tin nhan khong hop le. Soan: CTDH <MaTruong> gui 9209 de tra cuu so chi tieu tuyen sinh");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		} else if("CHOI".equalsIgnoreCase(command_code) && sTokens.length <2) {
			msgObject.setUsertext("*Cu phap tin nhan khong hop le. Soan: CHOI <MaTruong> gui 9209 de tra cuu ty le choi");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			msgObject.setUserid(user_id);
			msgObject.setServiceid(service_id);
			msgObject.setCommandCode(command_code);
			DBUtils.sendMT(msgObject);
			return null;
		}
		try {
			
			//goi ws check xem dung ma truong ko
			SendMOICom2HTSSoapProxy serviceResult = new SendMOICom2HTSSoapProxy();
			serviceResult.setEndpoint(url);
			if("DC".equalsIgnoreCase(command_code)) {
				String matruong = sTokens[1].trim();
				resultDT = serviceResult.checkMaTruongDH(username, pass, matruong);
			} else if("DT".equalsIgnoreCase(command_code)) {
				String matruong = sTokens[1].trim();
				resultDT = serviceResult.checkMaTruongDH(username, pass, matruong);
			} else if("NV2".equalsIgnoreCase(command_code)) {
				String matruong = sTokens[1].trim();
				resultDT = serviceResult.checkMaTruongDH(username, pass, matruong);
			} else if("MD".equalsIgnoreCase(command_code)) {
				String matruong = sTokens[1].trim();
				resultDT = serviceResult.checkMaTruongDH(username, pass, matruong);
			} else if("CTDH".equalsIgnoreCase(command_code)) {
				String matruong = sTokens[1].trim();
				resultDT = serviceResult.checkMaTruongDH(username, pass, matruong);
			} else if("CHOI".equalsIgnoreCase(command_code)) {
				String matruong = sTokens[1].trim();
				resultDT = serviceResult.checkMaTruongDH(username, pass, matruong);
			}
			
			if("DC".equalsIgnoreCase(command_code) && resultDT==0) {
				msgObject
				.setUsertext("*Kiem tra lai MaTruong. He thong chua cap nhat diem chuan cua Truong nay.*De Tra diem chuan Dai Hoc soan DC <MaTruong> Gui 9209");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtils.sendMT(msgObject);
				return null;
			} else if("DT".equalsIgnoreCase(command_code) && resultDT==0) {
				msgObject
				.setUsertext("*Ban nhap sai ma truong. *Muon biet diem thi dai hoc soan: DT <MaTruong> <MaKhoi> <SoBaoDanh> gui 9209");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtils.sendMT(msgObject);
				return null;
			} else if("NV2".equalsIgnoreCase(command_code) && resultDT==0) {
				msgObject
				.setUsertext("*Ban nhap sai ma truong. *Muon biet chi tieu va diem xet VN2 cua truong soan: NV2 <MaTruong> gui 9209");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtils.sendMT(msgObject);
				return null;
			} else if("MD".equalsIgnoreCase(command_code) && resultDT==0) {
				msgObject
				.setUsertext("*Ban nhan sai Ma Truong *De biet so thi sinh cung muc diem nhu ban. Soan MD<matruong><mucdiem> gui 9209");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}  else if("CTDH".equalsIgnoreCase(command_code) && resultDT==0) {
				msgObject
				.setUsertext("*Ban nhan sai Ma Truong *De tra cuu chi tieu tuyen sinh. Soan CTDH <MaTruong> gui 9209");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtils.sendMT(msgObject);
				return null;
			} else if("CHOI".equalsIgnoreCase(command_code) && resultDT==0) {
				msgObject
				.setUsertext("*Ban nhan sai Ma Truong. *De tra cuu ti le choi. Soan CHOI <MaTruong> gui 9209");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtils.sendMT(msgObject);
				return null;
			} else {
				//insert va table charge_diemthi
				//String user_id,String service_id,String mobile_operator,String command_code,String content_type,String info,String message_type,String amount
				//Neu charge thanh cong thi goi sv ben doi tac.
				//    + neu goi ws co ket qua thi tra luon cho kh. Neu chua co thi luu vao mlist_diemthi_dk
				
				// Insert vào table mlist neu result = 1 thi insert vao charge_diemthi
				int insert_mlist = insert_mlist_DK(user_id, service_id, mobile_operator, command_code, info, requestID);
				int result = 0;
				if(insert_mlist ==1) {
					insertCharge_diemthi(user_id, service_id, mobile_operator, command_code, info, content_type, message_type, amount, command_code);
					
				}
			}
		} catch (Exception e) {
			Util.logger.info(this.getClass().getName() + "@" + "Khong goi duoc webservice ben doi tac");
			return null;
		}
		return null;
	}

	private int insertCharge_diemthi(String user_id,String service_id,String mobile_operator,String command_code,String info, int content_type, String message_type,int amount, String service_name) {
		int ireturn = 1;
		//INSERT INTO `charge_diemthi`(`ID`,`USER_ID`,`SERVICE_ID`,`MOBILE_OPERATOR`,`COMMAND_CODE`,`CONTENT_TYPE`,`INFO`,`SUBMIT_DATE`,`DONE_DATE`,`RESULT_CHARGE`,`MESSAGE_TYPE`,`REQUEST_ID`,`MESSAGE_ID`,`TOTAL_SEGMENTS`,`RETRIES_NUM`,`INSERT_DATE`,`NOTES`,`SERVICE_NAME`,`CHANNEL_TYPE`,`CONTENT_ID`,`AMOUNT`)
		//VALUES(NULL,'84934258288','049209','VMS','DIEM','1','DIEM','2012-02-02 00:00:00','2012-02-02 00:00:00',
		//'0','1','0','0','0','0',CURRENT_TIMESTAMP,NULL,'DIEM','0','1','500');
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
				+ "',0,1,0,0,0,0,'"
				+ command_code
				+ "', 0, 1,3000)";

		try {
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


}
