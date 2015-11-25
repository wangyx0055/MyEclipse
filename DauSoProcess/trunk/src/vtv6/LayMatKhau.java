package vtv6;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

public class LayMatKhau extends ContentAbstract{

	public static final int MIN_LENGTH = 10;

	protected static java.util.Random r = new java.util.Random();
	protected static char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
      'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
      'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
      'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
      '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '@', };
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
				pass = getNext();
				password = codePass(pass);
				
				int updatePass = 0;
				updatePass = UpdateNick(userId,password);
				if(updatePass == 1) {
					msgObject.setUsertext("Mat khau cua ban la "+pass+". Dang nhap http//:hopnhacso.vn de upload avatar. DTHT 1900571577");
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
	
	private int UpdateNick(String sdt,String password) throws Exception {
		int result = 0;
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String proceExe = "UPDATE NickName SET Password ='"+password+"' WHERE MSISDN = '"+sdt+"'";
		
		try {
			Util.logger.info(proceExe);
			if (connection == null) {
				connection = dbpool.getConnection("M4teen");
			}
			callStmt = connection.prepareCall(proceExe);

			if (callStmt.execute()) {
				Util.logger.error("UPDATE NickName");
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
	
	 public String getNext() {
		    StringBuffer sb = new StringBuffer();
		    for (int i = 0; i < MIN_LENGTH; i++) {
		      sb.append(goodChar[r.nextInt(goodChar.length)]);
		    }
		    return sb.toString();
	 }
	 
	 public static String codePass(String code) {
		 MessageDigest md5 = null;
	        try {
	            md5 = MessageDigest.getInstance("MD5"); // Ma hoa MD5
	        }
	        catch (NoSuchAlgorithmException ex) {
	            return null;
	        }
	        md5.update(code.getBytes());
	        BigInteger bg = new BigInteger(1, md5.digest());
	        return bg.toString(16);
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
				connection = dbpool.getConnection("M4teen");
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
