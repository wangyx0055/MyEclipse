package subscription;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;

//import icom.Sender;
//import icom.SenderTest;
import com.vmg.sms.common.DBUtil;

public class Delivery_daily extends Thread {

	// String mlist = "mlist_jokes";
	DBPool dbpool = new DBPool();
	//int processnum = 1;
	//int processindex = 1;

	public Delivery_daily() {
		
	}

	public static long String2MilisecondNew(String strInputDate) {
		// System.err.println("String2Milisecond.strInputDate:" + strInputDate);
		String strDate = strInputDate.trim();
		int i, nYear, nMonth, nDay, nHour, nMinute, nSecond;
		String strSub = null;
		if (strInputDate == null || "".equals(strInputDate)) {
			return 0;
		}
		strDate = strDate.replace('-', '/');
		strDate = strDate.replace('.', '/');
		strDate = strDate.replace(' ', '/');
		strDate = strDate.replace('_', '/');
		strDate = strDate.replace(':', '/');
		i = strDate.indexOf("/");

		// System.err.println("String2Milisecond.strDate:" + strDate);
		if (i < 0) {
			return 0;
		}
		try {
			// Get Nam
			String[] arrDate = strDate.split("/");
			nYear = (new Integer(arrDate[0].trim())).intValue();
			nMonth = (new Integer(arrDate[1].trim())).intValue() - 1;
			nDay = (new Integer(arrDate[2].trim())).intValue();
			nHour = (new Integer(arrDate[3].trim())).intValue();
			nMinute = (new Integer(arrDate[4].trim())).intValue();
			nSecond = (new Integer(arrDate[5].trim())).intValue();

			// System.err.println("nYear: " + nYear + "@"+ nMonth + "@" +
			// nDay+"@"+ nHour + "@" + nMinute + "@" + nSecond);
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.set(nYear, nMonth, nDay, nHour, nMinute, nSecond);

			return calendar.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static Vector getVectorTable(String poolname, String strSQL)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;
		Vector vt = null;
		Connection cnn = null;
		DBPool dbpool = new DBPool();
		try {

			cnn = dbpool.getConnection(poolname);
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			vt = DBUtil.convertToVector(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
			closeObject(cnn);
		}
		return vt;
	}

	

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(PreparedStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(CallableStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Statement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(ResultSet obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Connection obj) {
		try {
			if (obj != null) {
				if (!obj.isClosed()) {
					if (!obj.getAutoCommit()) {
						obj.rollback();
					}
					obj.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {

		while (ConsoleSRV.processData) {

			// Connection connection = null;
			// PreparedStatement stmt = null;
			// ResultSet rs = null;
			CServices[] arrServices = null;
			try {

				Vector vtServices = getVectorTable(
								"gateway",
								"select id, services, minutes, hours, dayofmonth, month, dayofweek, weekofyear, result, retries, lasttime, class,options,alertmin,notcharge from icom_services order by id desc");
				arrServices = new CServices[vtServices.size()];
 
				for (int i = 0; i < vtServices.size(); i++) {
					Vector item = (Vector) vtServices.elementAt(i);

					// id, services, minutes, hours, dayofmonth, month,
					// dayofweek, weekofyear, result, retries, lasttime, class
					String id = (String) item.elementAt(0);
					String services = (String) item.elementAt(1);
					String minutes = (String) item.elementAt(2);
					String hours = (String) item.elementAt(3);
					String dayofmonth = (String) item.elementAt(4);
					String month = (String) item.elementAt(5);
					String dayofweek = (String) item.elementAt(6);
					String weekofyear = (String) item.elementAt(7);
					int result = Integer.parseInt((String) item.elementAt(8));
					int retries = Integer.parseInt((String) item.elementAt(9));
					String slasttime = (String) item.elementAt(10);
					Timestamp lasttime = new Timestamp(
							String2MilisecondNew(slasttime));
					String classname = (String) item.elementAt(11);
					String option = (String) item.elementAt(12);
					int alertmin = Integer
							.parseInt((String) item.elementAt(13));

					int notcharge = Integer.parseInt((String) item
							.elementAt(14));

					CServices cservices = new CServices(services, minutes,
							hours, dayofmonth, month, dayofweek, weekofyear,
							result, retries, lasttime, classname, option);

					if (cservices.istime2run()) {
						// chay nao
						// System.out.println("chay nao");
						DeliveryManager delegate = null;
						Class delegateClass = Class.forName(classname);
						Object delegateObject = delegateClass.newInstance();
						delegate = (DeliveryManager) delegateObject;

						delegate.start(id, services, option, notcharge);

						Timestamp tTime_current = new Timestamp(System
								.currentTimeMillis());
						java.util.Calendar calendar_current = java.util.Calendar
								.getInstance();
						calendar_current.setTime(new java.util.Date(
								tTime_current.getTime()));
						int nMinutes_current = calendar_current
								.get(calendar_current.MINUTE);

						if (nMinutes_current > alertmin) {
							DBUtil.Alert("DeliveryDaily", "IsTime2RUN",
									"major", "Kiem tra dich vu:" + services
											+ "",
									"CongLT:0963536888");
						}

					} else {

						Timestamp tTime = new Timestamp(System
								.currentTimeMillis());
						java.util.Calendar calendar = java.util.Calendar
								.getInstance();
						calendar.setTime(new java.util.Date(tTime.getTime()));

						int nHour = calendar.get(calendar.HOUR_OF_DAY);
						if (nHour < 6 && result != Constantsub.DELIVER_NOTRUN) {
							// Update
							String sqlUpdate = "update icom_services set result="
									+ Constantsub.DELIVER_NOTRUN
									+ " where id="
									+ id;
							// System.out.println("hicihc");
							executeSQL("gateway", sqlUpdate);
						}

					}

					
					
				}
				

			} catch (Exception e) {
				// TODO: handle exception
			} finally {

				// dbpool.cleanup(connection);
			}

			try {
				sleep(1000 * 60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	public static int executeSQL(String poolname, String sql) {

		PreparedStatement statement = null;
		Connection cnn = null;
		DBPool dbpool = new DBPool();

		try {

			cnn = dbpool.getConnection(poolname);
			statement = cnn.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();

			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			closeObject(statement);
			closeObject(cnn);

		}
	}
}
