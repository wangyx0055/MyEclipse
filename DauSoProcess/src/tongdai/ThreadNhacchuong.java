package tongdai;

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

public class ThreadNhacchuong extends Thread {

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
				connection = dbpool.getConnection("tongdai");
				if (connection == null) {
					Util.logger.error("Impossible to connect to DB");

				}

				String sqlSelect = "SELECT id,user_id,error_code, content FROM tblRingTone  WHERE STATUS=0";

				statement = connection.prepareStatement(sqlSelect);
				if (statement.execute()) {
					rs = statement.getResultSet();
					while (rs.next()) {
						int id = Integer.parseInt(rs.getString(1));
						String user_id = rs.getString(2);
						String error_code = rs.getString(3);
						String content = rs.getString(4);

						MsgObject msgobj = new MsgObject();
						msgobj.setUserid(ValidISDN(user_id));
						msgobj.setMobileoperator(getMobileOperatorNew(
								ValidISDN(user_id).toUpperCase(), 2));
						msgobj.setServiceid("8751");
						BigDecimal request_id = new BigDecimal(0);
						msgobj.setRequestid(request_id);
						msgobj.setKeyword("TONGDAI");
						if (error_code.equalsIgnoreCase("300")){
							
							msgobj.setUsertext("So dien thoai cua ban khong hop le de tai nhac chuong. DTHT 1900571515 ");
							msgobj.setMsgtype(0);
							msgobj.setContenttype(0);
							DBUtil.sendMT(msgobj);
							Thread.sleep(1000);
						}else 
							if (content.startsWith("http")) {
								msgobj.setUsertext("Link bai hat ban muon tai: "
										+ content);
								msgobj.setMsgtype(0);
								msgobj.setContenttype(8);
								DBUtil.sendMT(msgobj);
								Thread.sleep(1000);
								
						}
						else {
							msgobj.setUsertext(" Ban tai bai hat chua thanh cong xin vui long thu lai hoac goi dien den 1900571515 de duoc ho tro");
							msgobj.setMsgtype(0);
							msgobj.setContenttype(0);
							DBUtil.sendMT(msgobj);
							Thread.sleep(1000);
							
						}
						msgobj
								.setUsertext("De tang ban be va nguoi than mon qua am nhac y nghia,hay goi den so 1900571515-nhanh 2 va lam theo huong dan");
						msgobj.setMsgtype(0);
						msgobj.setContenttype(0);
						DBUtil.sendMT(msgobj);
						Thread.sleep(1000); 

						updateStatus(id);
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
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
			long itemp= Long.parseLong(sISDN);
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
			connection = dbpool.getConnection("tongdai");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "UPDATE tblRingTone   SET status ='1' where id="
					+ id;
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update status for id=" + id
						+ " to db asterick");
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
