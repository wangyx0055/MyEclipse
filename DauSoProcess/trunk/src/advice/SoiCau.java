package advice;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;

import com.vmg.sms.common.Util;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
//import com.vmg.sms.process.QuestionManager;

public class SoiCau extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String sServiceID = msgObject.getServiceid();
		String sMsg = replaceAllWhiteLetter(msgObject.getUsertext());

		String sMsg1 = replaceAllWhiteWithOne(sMsg);
		String sProvinceCode = sMsg1.substring(3).trim();
		String sContent = "";
		String[] sToken = null;

		try {
			sContent = getContent(sProvinceCode, sServiceID);
			if (!"".equals(sContent)) {
				sToken = sContent.split("#");

			} else {
				sContent = "Tin sai cu phap. Hay soan tin: CAU<MaTinh> gui "
						+ sServiceID + " de soi cau xo so. DTHT:19001255";
				sToken = sContent.split("#");
			}

		} catch (Exception e) {
			// TODO: handle exception
			sContent = "Tin sai cu phap. Hay soan tin: CAU<MaTinh> gui "
					+ sServiceID + " de soi cau xo so. DTHT:19001255";
			sToken = sContent.split("#");
		}

		// }
		// add2SMSSendFailed(msgObject);
		Collection messages = new ArrayList();

		if (sToken.length == 1) {

			msgObject.setUsertext(sToken[0]);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
		} else {
			msgObject.setUsertext(sToken[0]);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
			for (int i = 1; i < sToken.length; i++) {
				msgObject.setUsertext(sToken[i]);
				msgObject.setMsgtype(0);
				messages.add(new MsgObject(msgObject));
				
			}

		}
		return messages;
		
	}


	private String getContent(String ProvinceCode, String ServiceID) {

		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("gateway");
			String query = "select Content from vmg_vnnlinks_luckynumlot where ProvinceCode='"
					+ ProvinceCode.toUpperCase()
					+ "' or ProvinceCodeAlias like '%" + ProvinceCode + "%'";

			Vector result = DBUtil.getVectorTable(connection, query);
			for (int i = 0; i < result.size(); i++) {
				Vector item = (Vector) result.elementAt(i);
				content = (String) item.elementAt(0);
				
				
			}

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "SoiCauXoSo: Failed"
					+ ex.getMessage());
			ex.printStackTrace();
		} finally {
			dbpool.cleanup(connection);
		}
		return content;
	}

	private static String replaceAllWhiteLetter(String sInput) {
		String strTmp = sInput;
		for (int i = strTmp.length() - 1; i >= 0; i--) {
			char ch = strTmp.charAt(i);
			if ((ch <= '/') || (ch > '9' && ch < 'A') || (ch > 'Z' && ch < 'a')
					|| (ch > 'z')) {
				strTmp = strTmp.replace(ch, ' ');
			}
		}
		return strTmp;
	}

	public String replaceAllWhiteWithOne(String sInput) {

		String strTmp = sInput.trim();
		strTmp = strTmp.replace('-', ' ');
		strTmp = strTmp.replace('.', ' ');
		strTmp = strTmp.replace('/', ' ');
		strTmp = strTmp.replace('_', ' ');
		strTmp = strTmp.replace(',', ' ');

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
