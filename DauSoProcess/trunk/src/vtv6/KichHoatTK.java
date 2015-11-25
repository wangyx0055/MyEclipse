package vtv6;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class KichHoatTK extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			String info = msgObject.getUsertext();
			String userId = msgObject.getUserid();
			
			if(!checkUser(userId)) {
				msgObject.setUsertext("Ban chua dang ky Chat tren Hop Nhac So. Soan, NAM/NU nick noio gui 8751. DTHT: 1900571566.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(500);
			} else {
				String pass = "";
				String password = "";
				
				
				int updatePass = 0;
				updatePass = UpdateNick(userId);
				if(updatePass == 1) {
					//TH tra tin thanh cong
					msgObject.setUsertext("Ban da kich hoat thanh cong tai khoan. DTHT 1900571577");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	private int UpdateNick(String sdt) throws Exception {
		int result = 0;
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String proceExe = "UPDATE NickName SET IsValid = 1 WHERE MSISDN = '"+sdt+"'";
		
		try {
			Util.logger.info(proceExe);
			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			callStmt = connection.prepareCall(proceExe);

			if (callStmt.execute()) {
				Util.logger.error("UPDATE NickName (kich hoat tai khoan)");
				return result;
			}
			result = 1;
			return result;
			
		} catch (SQLException ex3) {
			Util.logger.error("[Sp_CheckUserID_SMS]:" + ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("[Sp_CheckUserID_SMS]:" + ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}
	
	public static boolean checkUser(String userId){
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		boolean check=false;
		String sqlQuery = "SELECT * From NickName Where MSISDN='"+userId+"'";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			callStmt = connection.prepareCall(sqlQuery);

			callStmt.execute();
			rs = callStmt.getResultSet();
			while (rs.next()) {
				check=true;  
			}
			
			
		} catch (SQLException ex3) {
			Util.logger.error("get mlist info: SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("get mlist info: SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}
		return check;
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
