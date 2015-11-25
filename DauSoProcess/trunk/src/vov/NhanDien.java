package vov;

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

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class NhanDien extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		try {

			// Get info of client
			String user_id = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String service_id = msgObject.getServiceid();
			Timestamp receive_date = msgObject.getTTimes();
			String keywords = msgObject.getKeyword();
			info = replaceAllWhiteWithOne(info);
			String mobile_operator = msgObject.getMobileoperator();
			BigDecimal request_id = msgObject.getRequestid();
			String request = request_id.toString();
			String sKeyword = msgObject.getKeyword();

			// ADD 2009-12-08 TrungVD - Choice Database save
			String database_customer = "icom_thantuong_customer";
			String database_vote = "icom_thantuong_vote";

			String giff = "Soan MCA tencasy gui 8751 de tai cac bai hat cua ca sy yeu thich";
			String inv_msg = "Chuong trinh moi chua bat dau.Moi ban tham gia choi vao 15h30 thu bay va chu nhat hang tuan tren VOV Giao thong 91MHz.DTHT 1900571566";
			String process2 = "Thoi gian tra loi cau hoi phu chua bat dau.Vui long lang nghe thong bao cua MC de tham gia tra loi cau hoi phu.DTHT: 1900571566";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();

			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				database_customer = getString(_option, "database_customer",
						database_customer);
				giff = getString(_option, "giff", giff);
				inv_msg = getString(_option, "inv_msg", inv_msg);
				process2 = getString(_option, "process2", process2);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			//
			String mtContent = "";
			Hashtable numberDelay = Threadnhandien.lastestnumber.SECRET_NUMBER;

			String session = getSECRET_NUMBER(numberDelay, "session");
			Util.logger.info("Session : " + session);
			String start_time = getSECRET_NUMBER(numberDelay, "start");
			Util.logger.info("Start Time : " + start_time);
			String end_time = getSECRET_NUMBER(numberDelay, "end");
			Util.logger.info("End Time : " + end_time);
			String block1 = getSECRET_NUMBER(numberDelay, "block1");
			Util.logger.info("Block 1 : " + block1);
			String block2 = getSECRET_NUMBER(numberDelay, "block2");
			Util.logger.info("Block 2 : " + block2);
			String block3 = getSECRET_NUMBER(numberDelay, "block3");
			Util.logger.info("Block 3 : " + block3);
			String isProcess = getSECRET_NUMBER(numberDelay, "isprocess");
			Util.logger.info("is Process :" + isProcess);
			String answer = getSECRET_NUMBER(numberDelay, "answer");
			Util.logger.info("Answer :" + answer);

			String nameIcon = getSECRET_NUMBER(numberDelay, "name");
			if  ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator().toUpperCase())){

				// Hoan tien mang sfone
				msgObject
						.setUsertext("Hien tai dich vu chua ho tro mang cua ban.Vui long quay tro lai sau.DTHT 1900571566");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			if (Threadnhandien.lastestnumber.isProcess == 0) {

				// Phien choi chua bat dau
				msgObject.setUsertext(inv_msg);
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else if ((Threadnhandien.lastestnumber.isProcess == 1)) {

				// Chi du doan 1 so va so sanh voi current
				String[] sTokens = info.split(" ");

				if (sTokens.length < 2) {
					mtContent = "Ban nhan tin sai cu phap.Soan: "
							+ keywords
							+ " tenthantuong gui "
							+ service_id
							+ " de gianh giai thuong cua chuong trinh Nhan dien than tuong.DTHT 1900571566";
					msgObject.setUsertext(mtContent);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {

					// Xem dang o block thu may
					int status1 = 1;
					if ((block1.compareToIgnoreCase(start_time) < 0)
							|| (block1.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 1;
					} else if ((block2.compareToIgnoreCase(start_time) < 0)
							|| (block2.compareToIgnoreCase(receive_date
									.toString()) > 0)) {
						status1 = 2;
					} else {
						status1 = 3;
					}

					// Xem du doan cua khach hang
					String guess1 = sTokens[1];
					String guess2 = "";
					for (int i = 1; i < sTokens.length; i++) {
						guess2 += sTokens[i];
					}
					Util.logger.info("Info: " + info);
					Util.logger.info("Du doan 2: " + guess2);

					if (guess1.equalsIgnoreCase(nameIcon)) {
						saveCustomer(database_customer, session, user_id,
								mobile_operator, guess1, 1, status1, request,
								sKeyword);
						msgObject
								.setUsertext("Ban da du doan than tuong la "
										+ guess1
										+ ".Nhanh tay du doan them de tang co hoi trung thuong."
										+ giff);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;
					} else {
						if (guess2.equalsIgnoreCase(nameIcon)) {
							saveCustomer(database_customer, session, user_id,
									mobile_operator, guess2, 1, status1,
									request, sKeyword);
							msgObject
									.setUsertext("Ban da du doan than tuong la "
											+ guess2
											+ ".Nhanh tay du doan them de tang co hoi trung thuong."
											+ giff);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							messages.add(new MsgObject(msgObject));
							return messages;
						} else {
							saveCustomer(database_customer, session, user_id,
									mobile_operator, guess1, 0, status1,
									request, sKeyword);
							msgObject
									.setUsertext("Ban da du doan than tuong la "
											+ guess1
											+ ".Nhanh tay du doan them de tang co hoi trung thuong."
											+ giff);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							messages.add(new MsgObject(msgObject));
							return messages;
						}
					}
				}
			} else if ((Threadnhandien.lastestnumber.isProcess == 2)) {

				// Chua den gio du doan cau hoi phu
				msgObject.setUsertext(process2);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;

			} else if ((Threadnhandien.lastestnumber.isProcess == 3)) {

				String[] sTokens = info.split(" ");
				if (sTokens.length < 2) {
					mtContent = "Tin nhan khong hop le.De tham gia tra loi cau hoi phu cua nhan dien than tuong soan tin"
							+ sKeyword
							+ " <dapan> gui "
							+ service_id
							+ ".Chuc ban may man.DTHT 1900571566";
					msgObject.setUsertext(mtContent);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {
					String guess = sTokens[1];

					if (guess.equalsIgnoreCase(answer)) {
						msgObject
								.setUsertext("Chuc mung ban da tra loi dung cau hoi phu.Hay tiep tuc theo doi chuong trinh de biet ban co phai la nguoi dau tien tra loi dung khong nhe!DTHT:1900571566.");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));

						saveCustomerVote(database_vote, session, user_id,
								mobile_operator, guess, 1, request, sKeyword);
						return messages;
					} else {
						// Sai
						msgObject
								.setUsertext("Rat tiec!Dap an ban chon chua dung.Phan qua hap dan van dang cho ban.Hay nhanh tay soan tin de tro thanh nguoi dau tien doan dung nhe!DTHT:1900571566");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
						saveCustomerVote(database_vote, session, user_id,
								mobile_operator, guess, 0, request, sKeyword);
						return messages;
					}
				}

			}
			return messages;
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

	// Check phone number
	private boolean checkNumber(String numberString) {
		try {
			long number = Integer.parseInt(numberString);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	// Ghi lai danh sach khach hang
	private static boolean saveCustomer(String database_customer,
			String session, String user_id, String mobile_operator,
			String guess_name, int exactly, int block, String request_id,
			String keyword) {

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
					+ database_customer
					+ "( session, user_id, mobile_operator, guess_name, exactly, block, request_id, keyword) VALUES ('"
					+ session + "','" + user_id + "','" + mobile_operator
					+ "','" + guess_name + "', " + exactly + "," + block + ",'"
					+ request_id + "','" + keyword + "')";

			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert Into : " + database_customer);
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

	private static boolean saveCustomerVote(String database_vote,
			String session, String user_id, String mobile_operator,
			String guess_name, int exactly, String request_id, String keyword) {

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
					+ database_vote
					+ "( session, user_id, mobile_operator, guess, exactly, request_id, keyword) VALUES ('"
					+ session + "','" + user_id + "','" + mobile_operator
					+ "','" + guess_name + "'," + exactly + ",'" + request_id
					+ "','" + keyword + "')";

			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert Into : " + database_vote);
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

}
