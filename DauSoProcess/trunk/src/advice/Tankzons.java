package advice;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.MOSender;
import common.DBUtils;

public class Tankzons extends ContentAbstract {

	// Lay thong tin doi tac va gui sang
	public String sendMessageMO(MsgObject msgObject, String textMessage,
			String partner, String commandcode) throws Exception {

		String url = Constants._prop.getProperty("mo.soap." + partner + ".url");
		String username = Constants._prop.getProperty("mo.soap." + partner
				+ ".username");
		String password = Constants._prop.getProperty("mo.soap." + partner
				+ ".password");
		String action = Constants._prop.getProperty("mo.soap." + partner
				+ ".action");
		String module = Constants._prop.getProperty("mo.soap." + partner
				+ ".module");
		String encode = Constants._prop.getProperty("mo.soap." + partner
				+ ".encode", "");
		String telco = Constants._prop.getProperty("mo.soap." + partner
				+ ".telco", Constants.TELCOLIST);

		if (url == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".url");

		if (url == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".module");

		MOSender sender = (MOSender) Class.forName(module).newInstance();
		String result = sender.sendMO(url, username, password, action,
				msgObject, textMessage, commandcode, encode, telco);

		return result;
	}

	// Xu ly chinh o day
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		int iRetries = 10;
		int iTimeout = 10;
		String info = msgObject.getUsertext();
		info = replaceAllWhiteWithOne(info);
		String[] sTokens = info.split(" ");
		String sKeyword = msgObject.getKeyword();
		String serviceid = msgObject.getServiceid();
		String userid = msgObject.getUserid();

		String resultok = Constants._prop.getProperty("mo.soap."
				+ keyword.getOptions() + ".ok", "1");
		if (checkspam(msgObject.getUserid(), msgObject.getServiceid()) == true) {

			String spaminfo15k = "Ban khong duoc gui qua 3SMS/30phut hoac 10SMS/24h. DT ho tro 04-35561862";
			String spaminfo = "Ban khong duoc gui toi dau so 8751 qua 3SMS/5phut hoac 10SMS/1gio hoac 100SMS/24h. DT ho tro 04-35561862";

			if ("8751".equalsIgnoreCase(msgObject.getServiceid())) {
				msgObject.setUsertext(spaminfo);
			} else {
				msgObject.setUsertext(spaminfo15k);
			}

			msgObject.setMsgtype(2);

			messages.add(new MsgObject(msgObject));

			return messages;
		}

		// Khach hang gui den 8551
		if ("8551".equalsIgnoreCase(serviceid)) {
			// Kiem tra xem khach hang da gui tin nhan dung cu phap chua ???
			if (!checkString(info)) {
				msgObject
						.setUsertext("Ban da nhan tin sai cu phap. Vui long soan tin theo cu phap "
								+ sKeyword
								+ " xxxx yyyy zzzz gui"
								+ serviceid
								+ " De tai JAVAGAME khac soan GAME maso gui 8751");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {
				// Chuyen sang 5th Media
				String result = "";
				while (iRetries > 0) {
					try {
						result = sendMessageMO(msgObject, msgObject
								.getUsertext(), keyword.getOptions(), msgObject
								.getKeyword());
						if (result.equals(resultok)) {
							Util.logger.info(this.getClass().getName() + "@"
									+ "send ok ,Details: " + "Msisdn: "
									+ msgObject.getUserid() + " Shortcode: "
									+ msgObject.getServiceid() + " Keyword: "
									+ msgObject.getKeyword() + " RequestID: "
									+ msgObject.getRequestid()
									+ "CommandCode: " + msgObject.getKeyword()
									+ " Online Retry countdown: " + iRetries);
							return null;
						} else {

							Util.logger.info(this.getClass().getName() + "@"
									+ "Got " + result
									+ ", Going For Retry, Sleeping,Details: "
									+ "Msisdn: " + msgObject.getUserid()
									+ " Shortcode: " + msgObject.getServiceid()
									+ " Keyword: " + msgObject.getKeyword()
									+ " RequestID: " + msgObject.getRequestid()
									+ "CommandCode: " + msgObject.getKeyword()
									+ " Online Retry countdown: " + iRetries);
							iRetries--;
							Thread.sleep(iTimeout * 1000);
							continue;
						}

					} catch (Exception e) {
						Util.logger.error(this.getClass().getName() + "@"
								+ "Some Exception..!! Got " + result
								+ ", Going For Retry, Sleeping,Details: "
								+ "Msisdn: " + msgObject.getUserid()
								+ " Shortcode: " + msgObject.getServiceid()
								+ " Keyword: " + msgObject.getKeyword()
								+ " RequestID: " + msgObject.getRequestid()
								+ "CommandCode: " + msgObject.getKeyword()
								+ " Online Retry countdown: " + iRetries);

						Util.logger.info(this.getClass().getName() + "@"
								+ "Exception: " + e.toString());

						Util.logger.printStackTrace(e);
						iRetries--;
						Thread.sleep(iTimeout * 1000);
						continue;
					}
				}
				add2SMSSendFailed(msgObject);
				return null;
			}
		} else if ("8751".equalsIgnoreCase(serviceid)) {
			// Gui 8751
			// Check xem gui lan thu may ?
			// Lan dau thi tra 1 MT ve. Lan 2 thi chuyen sang 5thMedia
			// Tin nhan co dung cu phap khong ?
			if (!checkString(info)) {
				msgObject
						.setUsertext("Ban da gui tin sai cu phap. De tai JavaGame khac soan tin GAME maso gui 8751");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {
				int timesSend = getUserID(userid);
				if (timesSend < 1) {
					saverequest(userid);
					msgObject
							.setUsertext("Ban soan 1 tin nhan "
									+ sKeyword
									+ " "
									+ sTokens[1]
									+ " "
									+ sTokens[2]
									+ " "
									+ sTokens[3]
									+ " gui "
									+ serviceid
									+ " nua de nhan tron bo 40 cap do cua Tankzons. Hay thoa suc chien dau cung binh chung tang thiet giap ban nhe!");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {

					// Chuyen sang 5th Media
					String result = "";
					while (iRetries > 0) {
						try {
							result = sendMessageMO(msgObject, msgObject
									.getUsertext(), keyword.getOptions(),
									msgObject.getKeyword());
							if (result.equals(resultok)) {
								Util.logger.info(this.getClass().getName()
										+ "@" + "send ok ,Details: "
										+ "Msisdn: " + msgObject.getUserid()
										+ " Shortcode: "
										+ msgObject.getServiceid()
										+ " Keyword: " + msgObject.getKeyword()
										+ " RequestID: "
										+ msgObject.getRequestid()
										+ "CommandCode: "
										+ msgObject.getKeyword()
										+ " Online Retry countdown: "
										+ iRetries);
								deleteClient(userid);
								return null;
							} else {

								Util.logger
										.info(this.getClass().getName()
												+ "@"
												+ "Got "
												+ result
												+ ", Going For Retry, Sleeping,Details: "
												+ "Msisdn: "
												+ msgObject.getUserid()
												+ " Shortcode: "
												+ msgObject.getServiceid()
												+ " Keyword: "
												+ msgObject.getKeyword()
												+ " RequestID: "
												+ msgObject.getRequestid()
												+ "CommandCode: "
												+ msgObject.getKeyword()
												+ " Online Retry countdown: "
												+ iRetries);
								iRetries--;
								Thread.sleep(iTimeout * 1000);
								continue;
							}

						} catch (Exception e) {
							Util.logger.error(this.getClass().getName() + "@"
									+ "Some Exception..!! Got " + result
									+ ", Going For Retry, Sleeping,Details: "
									+ "Msisdn: " + msgObject.getUserid()
									+ " Shortcode: " + msgObject.getServiceid()
									+ " Keyword: " + msgObject.getKeyword()
									+ " RequestID: " + msgObject.getRequestid()
									+ "CommandCode: " + msgObject.getKeyword()
									+ " Online Retry countdown: " + iRetries);

							Util.logger.info(this.getClass().getName() + "@"
									+ "Exception: " + e.toString());

							Util.logger.printStackTrace(e);
							iRetries--;
							Thread.sleep(iTimeout * 1000);
							continue;
						}
					}
					add2SMSSendFailed(msgObject);
					return null;
				}
			}
		}

		return null;
	}

	private static BigDecimal add2SMSSendFailed(MsgObject msgObject) {

		Util.logger.info("add2SMSSendFailed:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CPID)"
				+ " values(?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getKeyword());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private boolean checkspam(String user_id, String service_id) {
		String tablename = "sms_receive_log"
				+ new SimpleDateFormat("yyyyMM").format(new Date());

		String sqlselect = "select count(*),service_id from sms_receive_log200905 where user_id= '84963536888' and  receive_date > TIMESTAMPADD (minute,-60*24,current_timestamp ) group by service_id ";
		Connection connection = null;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			if ("84988348398".equalsIgnoreCase(user_id)
					|| ("0988348398".equalsIgnoreCase(user_id))
					|| ("+84988348398".equalsIgnoreCase(user_id))
					|| ("988348398".equalsIgnoreCase(user_id))) {
				return false;
			}

			if ("8751".equalsIgnoreCase(service_id)) {
				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-30,current_timestamp ) group by service_id ";

				Vector result2 = DBUtils.getVectorTable(connection, sqlselect);
				if (result2.size() > 0) {
					Vector item = (Vector) result2.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 3) {

						Util.logger.sysLog(2, this.getClass().getName(),
								"checkspam:userid=" + user_id + " &serviceid="
										+ service_id + "@rule:>3MO/30minute");
						return true;
					}

				}

				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-1440,current_timestamp ) group by service_id ";

				Vector result3 = DBUtils.getVectorTable(connection, sqlselect);
				if (result3.size() > 0) {
					Vector item = (Vector) result3.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 10) {
						Util.logger
								.sysLog(2, this.getClass().getName(),
										"checkspam:userid=" + user_id
												+ " &serviceid=" + service_id
												+ "@rule:>10MO/1440minute");
						return true;
					}

				}

			} else {
				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-5,current_timestamp ) group by service_id ";

				Vector result2 = DBUtils.getVectorTable(connection, sqlselect);
				if (result2.size() > 0) {
					Vector item = (Vector) result2.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 3) {

						Util.logger.sysLog(2, this.getClass().getName(),
								"checkspam:userid=" + user_id + " &serviceid="
										+ service_id + "@rule:>3MO/5minute");
						return true;
					}

				}

				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-60,current_timestamp ) group by service_id ";

				Vector result3 = DBUtils.getVectorTable(connection, sqlselect);
				if (result3.size() > 0) {
					Vector item = (Vector) result3.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 10) {
						Util.logger.sysLog(2, this.getClass().getName(),
								"checkspam:userid=" + user_id + " &serviceid="
										+ service_id + "@rule:>10MO/60minute");
						return true;
					}

				}

				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-1440,current_timestamp ) group by service_id ";

				Vector result4 = DBUtils.getVectorTable(connection, sqlselect);
				if (result4.size() > 0) {
					Vector item = (Vector) result4.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 100) {
						Util.logger.sysLog(2, this.getClass().getName(),
								"checkspam:userid=" + user_id + " &serviceid="
										+ service_id
										+ "@rule:>100MO/1440minute");
						return true;
					}

				}
			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return false;
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

	public static boolean checkString(String info) {
		boolean result = false;
		String[] sTokens = info.split(" ");
		if (sTokens.length < 4) {
			return result;
		}

		for (int i = 1; i < 4; i++) {
			if (sTokens[i].length() != 4) {
				return result;
			}
			try {
				int z = Integer.parseInt(sTokens[i]);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	private static int getUserID(String userid) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT times FROM icom_tankzons WHERE userid= '"
				+ userid.toUpperCase() + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getInt(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
		return -1;
	}

	private static boolean saverequest(String userid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_tankzons (userid) VALUES ('"
					+ userid + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_tankzons");
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

	private static boolean deleteClient(String userid) {
		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// delete icom_thuebao_bao where user id
			String sqlDelete = "DELETE FROM icom_tankzons WHERE userid = '"
					+ userid.toUpperCase() + "'";
			Util.logger.info("DELETE : " + sqlDelete);
			statement = connection.prepareStatement(sqlDelete);
			if (statement.execute()) {
				Util.logger.error("DELETE database");
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
