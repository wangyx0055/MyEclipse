package vtv6;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class DanhSachIcon extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			String info = msgObject.getUsertext();
			String userId = msgObject.getUserid();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			if (sTokens.length < 2) {
				msgObject
						.setUsertext("Sai cu phap. Soan: CX DS gui 8251. DTHT 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {
				List<String> danhsach = new ArrayList<String>();
				danhsach = DanhSachIcon();
				
				msgObject
				.setUsertext("Danh sach ma Icon chuong trinh HNS: " + danhsach.get(0) + "," + danhsach.get(1) + "," + danhsach.get(2) + ","+ danhsach.get(3) + "," + danhsach.get(4));
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	private List<String> DanhSachIcon() {
		List<String> result = new ArrayList<String>();
		// String musicid = "";
		Vector result1 = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("DAIV6");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "select TOP 5 IconName, IconCode from Icon";
			Util.logger.info("SEARCH IconID: " + sqlSelect);

			Util.logger.info("proceExe:"+sqlSelect);
			result1 = DBUtil.getVectorTable(connection, sqlSelect);

			Util.logger.info("DBUtil.getCode: queryStatement:" + sqlSelect);

			if (result1.size() > 0) {
				for (int i = 0; i < result1.size(); i++) {
					Vector item = (Vector) result1.elementAt(i);
					String icon_code = (String) item.elementAt(1);
					String icon_name = (String) item.elementAt(0);
					String code_name_icon = icon_name + "-" + icon_code;
					result.add(code_name_icon);
				}
				
				return result;
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
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
