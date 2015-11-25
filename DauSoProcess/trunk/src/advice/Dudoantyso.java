package advice;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class Dudoantyso extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	String URL = "http://www.mobinet.com.vn/?p=84918431157&c=808&f=JavaGame&g=9";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		String VALID_CODE = "";

		try {

			// Get info of client
			String user_id = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String service_id = msgObject.getServiceid();
			Timestamp receive_date = msgObject.getTTimes();
			BigDecimal request_id = msgObject.getRequestid();
			String keywords = msgObject.getKeyword();
			info=info.replace("-"," ");
			Util.logger.info("URL: " + info);

			
			info=info.replace("."," ");
			info=info.replace("/"," ");
			info=info.replace(","," ");
			info=info.replace(";"," ");
			
			info = replaceAllWhiteWithOne(info);
			String mobile_operator = msgObject.getMobileoperator();
			int cpid = msgObject.getCpid();
			String infoid = "";
			String dbcontent = "listtranbong";
			// Get Option
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			GID = getOption(_option, 0);
			String type = getString(_option, "type", "media");
			URL = getString(_option, "url", URL);
			Util.logger.info("URL: " + URL);

			if ("VIETTEL".equalsIgnoreCase(mobile_operator)
					|| "VIETEL".equalsIgnoreCase(mobile_operator)) {
				infoid = "viettel";
			} else if (("VMS".equalsIgnoreCase(mobile_operator))
					|| "mobifone".equalsIgnoreCase(mobile_operator)) {
				infoid = "mobifone";
			} else if (("GPC".equalsIgnoreCase(mobile_operator))
					|| ("VINAPHONE".equalsIgnoreCase(mobile_operator))) {
				infoid = "vinaphone";
			} 
			  else if ("BEELINE".equalsIgnoreCase(mobile_operator)){
				infoid = "beeline";
			} else {
				infoid = "other";
			}
		
			String[] sTokens = info.split(" ");

			if (sTokens.length < 3) {
				msgObject.setUsertext("Tin nhan sai cu phap. Soan tin"
						+ keywords + " matran tyso gui " + service_id
						+ " .DTHT 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {

				String matran = sTokens[1];
				int ma = Integer.parseInt(matran);
				Util.logger.info("ma:" + ma);

				if (ma <= 29 && ma >= 1) {
					String d1;
					String d2;

					if (sTokens.length > 3) {
						d1 = sTokens[2];
						d2 = sTokens[3];
					} else {
						if (sTokens[2].length()>= 2) {
							d1 = sTokens[2].substring(0, 1);
							Util.logger.info("d1:" + d1);

							d2 = sTokens[2].substring(1, 2);
							Util.logger.info("d2:" + d2);
						} else {
							msgObject
									.setUsertext("Tin nhan sai cu phap. Soan tin"
											+ keywords
											+ " matran tyso gui "
											+ service_id + " .DTHT 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							messages.add(new MsgObject(msgObject));
							return messages;
						}

					}
					String[] tranbong = null;
					tranbong = Gettran(dbcontent, matran);

					// MT 1
					if ("1".equalsIgnoreCase(tranbong[1])) {
						String mt1 = "Ban da du doan ty so tran dau "
								+ tranbong[0]
								+ " la "
								+ d1
								+ "-"
								+ d2
								+ ".Hay tiep tuc theo doi de biet ket qua. Du doan cang nhieu co hoi trung thuong cang lon.";

						msgObject.setUsertext(mt1);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
						saveClient(matran, user_id, service_id,
								mobile_operator, info, request_id.toString(),
								0, d1, d2, receive_date, cpid, keywords);

						
					} else {
						msgObject
								.setUsertext("Thoi gian du doan tran "
										+ tranbong[0]
										+ " da ket thuc.Hay theo doi va du doan ngay cac tran tiep theo de tang co hoi trung thuong cua chuong trinh.DTHT:1900571566");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));

					}
				} else {
					msgObject
							.setUsertext("Khong co ma so tran dau nay.Soan tin"
									+ keywords + " matran tyso gui "
									+ service_id + " .DTHT 1900571566");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
				}
				return messages;
			}
		} catch (Exception ex) {
			Util.logger.error("Error:" + ex.toString());
			return null;
		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}

	// Replace all space by one space
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

	public static String getSECRET_NUMBER(Hashtable numberDelay,
			String secretNumber) {
		String retobj = "";

		try {
			retobj = (String) numberDelay.get(secretNumber);
			return retobj;

		} catch (Exception e) {
		}

		return "";
	}

	private static String[] Gettran(String dtbase, String matran) {
		String[] result = new String[2];
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT tentran,finish FROM " + dtbase
				+ " WHERE matran= '" + matran.toUpperCase() + "'";
		result[0] = "";
		result[1] = "";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);
			Util.logger.info("get userid  : " + query);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
					return result;
				}
			}

			return result;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return result;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
	}

	private static boolean saveClient(String tran, String user_id,
			String service_id, String mobile_operator, String info,
			String request_id, int exactly, String doi1, String doi2,
			Timestamp receive_date, int Cpid, String keyword) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_dudoantyso_customer (tran, user_id, service_id, mobile_operator, info, request_id, exactly, doi1,doi2, receive_time, cpid, keyword ) VALUES ('"
					+ tran
					+ "','"
					+ user_id
					+ "','"
					+ service_id
					+ "','"
					+ mobile_operator
					+ "','"
					+ info
					+ "','"
					+ request_id
					+ "',"
					+ exactly
					+ ",'"
					+ doi1
					+ "','"
					+ doi2
					+ "','"
					+ receive_date + "'," + Cpid + ",'" + keyword + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_");
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

	public String ValidISDN(String sISDN) {
		Util.logger.info(this.getClass().getName() + "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	String getOption(HashMap _option1, String defaultvalue) {
		try {
			return ((String) _option1.get("gid")).toUpperCase();
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	int getOption(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("gid"));
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "###");

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

	String getString(HashMap _option1, String field, String defaultvalue) {
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

	// Save customer join sub Question
	private static boolean saveAnswer(String session, String user_id,
			String service_id, String mobile_operator, String answer,
			String request_id, int exactly, Timestamp receive_date, int Cpid) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_lastsong_answer (session, user_id, service_id, mobile_operator, answer, request_id, correct, receive_date, cpid ) VALUES ('"
					+ session
					+ "','"
					+ user_id
					+ "','"
					+ service_id
					+ "','"
					+ mobile_operator
					+ "','"
					+ answer
					+ "','"
					+ request_id
					+ "'," + exactly + ",'" + receive_date + "'," + Cpid + ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_");
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

	// Check phone number
	private boolean checkNumber(String numberString) {
		try {
			long number = Integer.parseInt(numberString);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
