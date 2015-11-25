package vtv6;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Icon extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			String info = msgObject.getUsertext();
			String userId = msgObject.getUserid();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			if (sTokens.length < 2) {
				msgObject
						.setUsertext("Sai cu phap. Soan: CX masoIcon gửi 8551. DTHT 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {
				//check trong table icon co icon do ko
				//neu co thi insert vao duyet_iconv6, ko thi tra mt ko co icon
				int checkIcon = 0;
				checkIcon = CheckIcon(sTokens[1]);
				if(checkIcon==0) {
					msgObject
					.setUsertext("Ma Icon nay hien khong co trong he thong. Vui  long chon Icon khac. DTHT 1900571566. De lay danh sach soan: CX DS gui 8251.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				} else{
					boolean updateicon = false;
					updateicon = updateDuyetIcon(userId,checkIcon);
					if(updateicon == true) {
						msgObject.setUsertext("Tin nhan cua ban da chuyen den he thong. Icon va so dien thoai cua ban hien dang trong che do cho hien thi. Vui long don xem..");
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
	
	private boolean updateDuyetIcon(String userid, int icon_id) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			String sqlInsert = "INSERT INTO duyet_iconV6 (user_id,icon_id,status)"
					+ "VALUES(?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setInt(2, icon_id);
			statement.setInt(3, 0);

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
	private static int CheckIcon(String codeIcon) {
		int result = 0;
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("DAIV6");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT IconID FROM Icon where IconCode='" + codeIcon
					+ "'";
			Util.logger.info("SEARCH IconID: " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result =Integer.parseInt(rs.getString(1));
					return result;
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
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
