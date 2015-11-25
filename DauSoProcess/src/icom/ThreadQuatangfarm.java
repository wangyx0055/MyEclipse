package icom;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.SoapLiveSMS;
import com.vmg.soap.mo.sendXMLStorenew;

public class ThreadQuatangfarm extends Thread {

	public static Hashtable SECRET_NUMBER = null;
	public boolean LOAD = false;
	public static int isProcess = 0;

	public static String getSECRET_NUMBER(String secretNumber) {
		String retobj = "";

		try {
			retobj = (String) SECRET_NUMBER.get(secretNumber);
			return retobj;

		} catch (Exception e) {
		}

		return "";
	}

	@Override
	public void run() {

		while (ConsoleSRV.processData) {

			Connection connection = null;
			PreparedStatement statement = null;
			ResultSet rs = null;

			DBPool dbpool = new DBPool();

			// check trong cdr farm queue.
			try {
				connection = dbpool.getConnection("gateway");
				if (connection == null) {
					Util.logger.error("Impossible to connect to DB");

				}

				String sqlSelect = "SELECT id,user_id, service_id, mobile_operator,info FROM mo_queue_farm ";

				statement = connection.prepareStatement(sqlSelect);
				if (statement.execute()) {
					rs = statement.getResultSet();
					while (rs.next()) {
						int id = Integer.parseInt(rs.getString(1));
						String user_id = rs.getString(2);
						String service_id = rs.getString(3);
						String mobile_operator = rs.getString(4);
						String info = rs.getString(5);

						MsgObject msgobj = new MsgObject();
						msgobj.setUserid(user_id);
						msgobj.setMobileoperator(mobile_operator);
						msgobj.setServiceid(service_id);
						BigDecimal request_id = new BigDecimal(0);
						msgobj.setRequestid(request_id);
						msgobj.setKeyword("LV");
						int type = 4;
						int code = getRingStore();
						String cateid="";
						String typename = "RINGHOT";

						if (info.equalsIgnoreCase("GAME")) {
							type = 5;
							typename = "IMAGEHOT";
							code = getImageStore();
						}

						String link = sendXMLStorenew.SendXML(type, msgobj
								.getUserid(), code + "", 3, "0", now());
						if (!link.equalsIgnoreCase("")) {
							msgobj.setUsertext(typename + ":" + link);
							msgobj.setMsgtype(0);
							msgobj.setContenttype(0);
							DBUtil.sendMT(msgobj);
							Thread.sleep(1000);
						} else {
							msgobj.setUsertext("Qua tang:http://www.mobinet.com.vn/?c=wap3");
							msgobj.setMsgtype(0);
							msgobj.setContenttype(0);
							DBUtil.sendMT(msgobj);
							Thread.sleep(1000);
						}
						updateStatus(id);
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							// 
							e.printStackTrace();
						}
					}

				}

			} catch (SQLException e) {
				Util.logger.error(": Error:" + e.toString());

			} catch (Exception e) {
				Util.logger.error(": Error:" + e.toString());

			} finally {
				dbpool.cleanup(rs);
				dbpool.cleanup(statement);
				dbpool.cleanup(connection);
			}

			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	public static int getRingStore() {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT top 1 [RingtoneID] FROM [IComStore].[dbo].[Ringtone] where CateID =1024 order by newid()";

		try {
			connection = dbpool.getConnection("store");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}
	public static int getImageStore() {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT top 1 [ImageID] FROM [IComStore].[dbo].[Image] where CateID =1026   order by newid()";

		try {
			connection = dbpool.getConnection("store");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}

	
	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	public String ValidISDN(String sISDN) {
		Util.logger.info(this.getClass().getName() + "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		if (sISDN.trim().length() < 8) {
			return "-";
		}
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
			// TODO: handle exception
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	// 

	public static String getMobileOperatorNew(String userid, int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("gateway");

			String query = "SELECT operator FROM icom_isdnseries WHERE prefix= substr('"
					+ userid + "',1, length(prefix)) ";
			if (type == 1 || type == 0) {
				query += " and type=" + type;
			}

			Vector result = DBUtil.getVectorTable(connection, query);
			for (int i = 0; i < result.size(); i++) {
				Vector item = (Vector) result.elementAt(i);
				tmpOperator = (String) item.elementAt(0);
			}

			return tmpOperator;
		} catch (Exception ex) {
			Util.logger.sysLog(LogValues.INFORMATIVE, "Utils",
					"getMobileOperator: Get MobileOpereator Failed"
							+ ex.toString());
			return tmpOperator;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public static String FormatNumber(int i) {
		try {
			if (i >= 0 && i < 10)
				return "0" + i;
			else
				return "" + i;
		} catch (Exception e) {
			return "";
		}
	}

	private static boolean updateStatus(int id) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE from mo_queue_farm   where id="
					+ id;
			Util.logger.info(" DELETE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("DELETE for id=" + id
						);
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
