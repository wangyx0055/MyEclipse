package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class tracnghiemngaysinh extends ContentAbstract {

	/**
	 * getMessages.<br> ◆ Processing order ◆ Handle exception
	 * 
	 * @param msgObject
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			Collection messages = new ArrayList();
			String reply = "";

			// Lay option
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			String GAME_ID = ((String) _option.get("game_id")).toUpperCase();

			String info = msgObject.getUsertext();
			info = replaceAllWhiteWithOne(info);
			String[] sTokens = info.split(" ");

			// Get keyword
			String sKeyword = msgObject.getKeyword();
			String serviceid = msgObject.getServiceid();

			if (sTokens.length < 2) {
				msgObject
						.setUsertext("Ban da nhan tin sai cu phap. De nhan duoc tinh cach qua ngay sinh. Soan tin:"
								+ sKeyword + " ngaysinh gui " + serviceid);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
			} else {
				String day = sTokens[1];

				if ("".equalsIgnoreCase(getfromday(day))) {
					reply = "Tin nhan ban gui khong dung cu phap . Soan tin "
							+ sKeyword
							+ " ngaysinh gui "
							+ serviceid
							+ " .Trong do "
							+ sKeyword
							+ " la tu khoa chuong trinh, ngaysinh la ngay sinh cua ban";
					msgObject.setUsertext(reply);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {
					String code = getfromday(day);
					String content = getContent(GAME_ID, code);
					String[] sContent = content.split("###");
					for (int i = 0; i < sContent.length; i++) {
						if (!"".equalsIgnoreCase(sContent[i])) {
							msgObject.setUsertext(sContent[i]);
							if (i == 0) {
								msgObject.setMsgtype(1);
							} else {
								msgObject.setMsgtype(0);
							}
							msgObject.setContenttype(0);
							messages.add(new MsgObject(msgObject));
						}
					}
				}
			}
			return messages;
		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}

	public String getContent(String gameid, String maso) {
		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();
			String query1 = "SELECT content FROM icom_textbase_data WHERE upper(gameid) = '"
					+ gameid.toUpperCase() + "' AND subcode1 = '" + maso + "'";
			stmt = connection.prepareStatement(query1);

			if (stmt.execute()) {
				rs = stmt.getResultSet();

				while (rs.next()) {
					content = rs.getString(1);
				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
			return content;
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(rs);
			dbpool.cleanup(connection);

		}

		return content;
	}

	// Tra ve ma so MT tuong ung voi ngay sinh khach hang
	public String getfromday(String str) {
		try {
			String numb = "";
			int day = Integer.parseInt(str);

			switch (day) {
			case 1:
			case 10:
			case 19:
			case 28:
				numb = "01";
				break;

			case 2:
			case 11:
			case 20:
			case 29:
				numb = "02";
				break;
			case 3:
			case 12:
			case 21:
			case 30:
				numb = "03";
				break;
			case 4:
			case 13:
			case 22:
			case 31:
				numb = "04";
				break;
			case 5:
			case 14:
			case 23:
				numb = "05";
				break;
			case 6:
			case 15:
			case 24:
				numb = "06";
				break;
			case 7:
			case 16:
			case 25:
				numb = "07";
				break;
			case 8:
			case 17:
			case 26:
				numb = "08";
				break;
			case 9:
			case 18:
			case 27:
				numb = "09";
				break;

			default:
				numb = "";
				break;
			}
			return numb;
		} catch (Exception e) {
			return "";
		}
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
}
