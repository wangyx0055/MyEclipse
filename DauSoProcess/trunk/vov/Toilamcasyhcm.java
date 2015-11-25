package vov;

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

public class Toilamcasyhcm extends ContentAbstract {

	private static final String DBCONTENT = "toilamcasy_confighcm";
	private static final String DBCUSTOMER = "toilamcasy_customerhcm";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		try {
			// Get Information From Customer
			String sUsertext = msgObject.getUsertext();
			Timestamp timeReceive = msgObject.getTTimes();
			sUsertext = replaceAllWhiteWithOne(sUsertext);
			String[] sTokens = sUsertext.split(" ");
			String sServiceid = msgObject.getServiceid();
			String sKeyword = msgObject.getKeyword();
			String user_id = msgObject.getUserid();
			String mobile_operator = msgObject.getMobileoperator();

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			String content = getString(_option, "content", "X Y");
			int lastPosition = getInt(_option, "position", 4);

			// Get Ma so hom nay
			String sCode = getCode(DBCONTENT, timeReceive);
			String[] sCodes = sCode.split(",");
			int _type = getInt(_option, "type", 2);

			String _mtInvalid = "Ban nhan tin sai cu phap.Soan tin "
					+ sKeyword
					+ " "
					+ content
					+ " gui "
					+ sServiceid
					+ " trong do X la ma so giong hat,Y la so nguoi binh chon giong ban.DTHT:1900571566";
			
			if (sTokens.length < 2) {
				// Tin nhan sai cu phap
				msgObject.setUsertext(_mtInvalid);
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {

				// Kiem tra xem type the nao = 1: BC ma loi chuc
				// = 2 : BC maloihuc so nguoi du doan giong ban
				if (_type == 1) {
					String code = sTokens[1];
					// BC maloichuc
					if (!checkCode(sCodes, code)) {
						msgObject
								.setUsertext("Ma so binh chon giong hat cua ngay hom nay la "
										+ sCode
										+ ".Soan "
										+ sKeyword
										+ " "
										+ content
										+ " gui "
										+ sServiceid
										+ " de binh chon cho giong hat yeu thich va gianh phan qua hap dan.");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
						return messages;
					} else {
						// Check vi tri
						// Update vao csdlieu
						updateCodeTotal(DBCONTENT, code);
						// Xac dinh vi tri va thong bao cho khach hang
						int position = getPosition(DBCONTENT, code,
								timeReceive, lastPosition);

						// Ghi vao co so du lieu
						saveCustomer(DBCUSTOMER, user_id, sServiceid,
								mobile_operator, sKeyword, code, 1);
						// Thong bao cho khach hang
						msgObject
								.setUsertext("Ban da binh chon cho giong hat so "
										+ code
										+ ".Hien nay giong hat nay dang o vi tri "
										+ position
										+ ".Chuc ban gianh duoc phan qua cua chuong trinh.DTHT:1900571566");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
						return messages;
					}
				} else {

					// _type = 2
					if (sTokens.length < 3) {
						// Tin nhan sai cu phap
						msgObject.setUsertext(_mtInvalid);
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
						return messages;
					} else {
						// Ma so loi chuc
						String code = sTokens[1];
						// So nguoi du doan
						String total = sTokens[2];
						if (!checkCode(sCodes, code)) {
							msgObject
									.setUsertext("Ma so giong hat cua ngay hom nay la "
											+ sCode
											+ ".Soan tin "
					+ sKeyword
					+ " "
					+ content
					+ " gui "
					+ sServiceid
					+ " trong do X la ma so giong hat,Y la so nguoi binh chon giong ban.DTHT 1900571566");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));
							return messages;
						} else {
							// Check vi tri
							// Update vao csdlieu
							updateCodeTotal(DBCONTENT, code);
							// Xac dinh vi tri va thong bao cho khach hang
							int position = getPosition(DBCONTENT, code,
									timeReceive, lastPosition);

							int number = convertString(total);

							// Ghi vao co so du lieu
							saveCustomer(DBCUSTOMER, user_id, sServiceid,
									mobile_operator, sKeyword, code, number);
							// Thong bao cho khach hang
							msgObject
									.setUsertext("Ban da binh chon cho giong hat "
											+ code
											+ ".Hien nay giong hat nay dang o vi tri "
											+ position
											+ ".Chuc ban gianh duoc phan qua hap dan cua chuong trinh.DTHT:1900571566");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(1);
							messages.add(new MsgObject(msgObject));
							return messages;
						}
					}
				}
			}
		} catch (Exception e) {
			Util.logger.error("Error : " + e.getMessage());
		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
		return messages;
	}

	// Get Content
	private String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)
					|| temp == null) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	// Convert String
	private int convertString(String total) {
		try {
			int result = Integer.parseInt(total);
			return result;
		} catch (Exception e) {
			return 1;
		}
	}

	/* Save customer play traffice game follow date */
	private static boolean saveCustomer(String database, String user_id,
			String service_id, String mobile_operator, String command_code,
			String wish_id, int total) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO "
					+ database
					+ "(user_id, service_id, mobile_operator, command_code, wish_id, total ) VALUES ('"
					+ user_id.toUpperCase() + "','" + service_id.toUpperCase()
					+ "','" + mobile_operator.toUpperCase() + "','"
					+ command_code.toUpperCase() + "','"
					+ wish_id.toUpperCase() + "'," + total + ")";

			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.info("Insert Succesful !!!");
				return true;
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

	/* Save customer */
	private static boolean updateCodeTotal(String database, String code) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlUpdate = "UPDATE " + database
					+ " SET total_sms= total_sms + 1 WHERE upper(wish_id)='"
					+ code.toUpperCase() + "'";
			Util.logger.info("Update:" + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.error("Update Successfull !!!");
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

	// Get Code in day
	private String getCode(String database, Timestamp timeReceive) {
		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String result = "";

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT wish_id FROM " + database
					+ " WHERE date(date_time) <= date('" + timeReceive + "') and date(end_time)>=('" + timeReceive + "')";
			Util.logger.info("SELECT :" + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					if ("".equalsIgnoreCase(result)) {
						result = rs.getString(1);
					} else {
						result = result + "," + rs.getString(1);
					}
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

	// Get Code in day
	private int getPosition(String database, String code,
			Timestamp timeReceive, int lastPosition) {
		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String result = "";
		int position = 0;
		int total_sms = 0;
		int defaultValue = lastPosition;

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return defaultValue;
			}

			String sqlSelect = "SELECT total_sms, wish_id FROM " + database
					+ " WHERE date(date_time) <= date('" + timeReceive + "') and date(end_time)>=('" + timeReceive + "') ORDER BY total_sms DESC";
			Util.logger.info("SELECT :" + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					position++;
					String sCode = rs.getString(2);
					if (code.equalsIgnoreCase(sCode)) {
						return position;
					}
				}
			}

			return defaultValue;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return defaultValue;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return defaultValue;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private boolean checkCode(String[] sCodes, String code) {
		for (int i = 0; i < sCodes.length; i++) {
			if (code.equalsIgnoreCase(sCodes[i])) {
				return true;
			}
		}
		return false;
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

	private int getInt(HashMap _option1, String field, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get(field));
		} catch (Exception e) {
			return defaultvalue;
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

}
