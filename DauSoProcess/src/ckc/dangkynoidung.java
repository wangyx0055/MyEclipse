package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class dangkynoidung extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			Collection messages = new ArrayList();

			// Lay option
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			String GAME_ID = ((String) _option.get("game_id")).toUpperCase();

			// Lay thong tin khach hang gui ve.
			String info = msgObject.getUsertext();
			info = replaceAllWhiteWithOne(info);
			String[] sTokens = info.split(" ");

			String sKeyword = msgObject.getKeyword();
			String serviceid = msgObject.getServiceid();
			String userid = msgObject.getUserid();
			String operator = msgObject.getMobileoperator();
			Timestamp timesend = msgObject.getTTimes();
			BigDecimal requestid = msgObject.getRequestid();

			if ((sTokens.length < 2) || (info.length() > 160)) {
				// Thong bao khach hang gui tin nhan sai.
				msgObject
						.setUsertext("Cam on ban da tham gia chuong trinh, de dang ky tham gia chuong trinh RONG BAY hay soan tin: "
								+ sKeyword + " [ten cua ban] gui " + serviceid);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {

				// Lay noi dung khach hang gui toi khong bao gom keyword
				String content = info.substring(sTokens[0].length() + 1);

				// Luu vao csdl noi dung khach hang gui toi.
				saveClient(userid, operator, sKeyword, serviceid, timesend,
						requestid, content);

				// Lay noi dung khach hang gui ve va luu vao csdl
				String mtcontent = getContent(GAME_ID);
				String[] mtcontents = mtcontent.split("###");
				for (int i = 0; i < mtcontents.length; i++) {
					if (!"".equalsIgnoreCase(mtcontents[i])) {
						msgObject.setUsertext(mtcontents[i]);
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

			_params.put(key, value);
		}

		return _params;

	}

	// Lay noi dung que boi
	public String getContent(String gameid) {
		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();
			String query1 = "select content from icom_textbase_data where upper(gameid) = '"
					+ gameid.toUpperCase() + "'";
			stmt = connection.prepareStatement(query1);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					content = rs.getString(1);
				}
			}
			return content;
		} catch (SQLException e) {
			System.out.println(e);
			return content;
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(rs);
			dbpool.cleanup(connection);

		}
	}

	/* Ghi lại danh sách các khách hàng hệ thống không đáp ứng được yêu cầu */
	private static boolean saveClient(String userid, String operator,
			String keyword, String serviceid, Timestamp timesend,
			BigDecimal requestid, String content) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_customer_register( userid, operator, keyword, serviceid, timesend, requestid, content) VALUES ('"
					+ userid
					+ "','"
					+ operator.toUpperCase()
					+ "','"
					+ keyword
					+ "','"
					+ serviceid.toUpperCase()
					+ "','"
					+ timesend
					+ "','" + requestid + "','" + content + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_list_customer");
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
