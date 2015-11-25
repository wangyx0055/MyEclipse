package vtv6;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class IconText extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			String info = msgObject.getUsertext();
			String userId = msgObject.getUserid();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			String loichuc = "";
			if (sTokens.length < 2) {
				msgObject
						.setUsertext("Sai cu phap. IT noidungloichuc gui 8751 (noi dung khong qua 15 ky tu). DTHT 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {
			
				if(sTokens.length > 2) {
					for(int i =1; i< sTokens.length; i++) {
						loichuc += sTokens[i] + " ";
					}
					loichuc = loichuc.substring(0, loichuc.length()-1);
				} else {
					loichuc = sTokens[1];
				}
				if(loichuc.length() > 160) {
					msgObject.setUsertext("Noi dung khong hop le. Vui long soan noi dung khac. DTHT 1900571566");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
				} else {
					boolean updateicon = false;
					updateicon = InsertLoiChuc(userId,loichuc);
					if(updateicon == true) {
						msgObject.setUsertext("Loi chuc va sdt cua ban dang trong che do cho duyet! Xin vui long cho!");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					}
				}
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	private boolean InsertLoiChuc(String userid, String loichuc) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			String sqlInsert = "INSERT INTO IconText (MSISDN,Message,CreateDate)"
					+ "VALUES(?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setString(2, loichuc);
			statement.setString(3, new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss").format(new Date()));

			statement.executeUpdate();
			Util.logger.info("sqlInsert  : " + sqlInsert);
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
