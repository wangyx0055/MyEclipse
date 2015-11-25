package dailymulti;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import sub.CServices;
import icom.Constants;
import icom.DBPool;
import icom.Sender;

import icom.common.DBUtil;

public class Delivery_daily_multi extends Thread {

	// String mlist = "mlist_jokes";
	DBPool dbpool = new DBPool();
	int processnum = 1;
	int processindex = 1;

	public Delivery_daily_multi(int processnum, int processindex) {
		this.processnum = processnum;
		this.processindex = processindex;
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

	@Override
	public void run() {

		while (Sender.processData) {

			// Connection connection = null;
			// PreparedStatement stmt = null;
			// ResultSet rs = null;
			CServices[] arrServices = null;
			try {

				Vector vtServices = DBUtil
						.getVectorTable(
								"gateway",
								"select id, services, minutes, hours, dayofmonth, month, dayofweek, weekofyear, result, retries, lasttime, class,options,alertmin,notcharge,service_daily,idx,num from services_daily "
										+ " where (mod(id,"
										+ processnum
										+ ")="
										+ processindex + ")");
				if (vtServices.size() > 0) {
					arrServices = new CServices[vtServices.size()];

					for (int i = 0; i < vtServices.size(); i++) {
						if (!Sender.processData) {
							break;
						}
						Vector item = (Vector) vtServices.elementAt(i);

						String id = (String) item.elementAt(0);
						String services = (String) item.elementAt(1);
						String minutes = (String) item.elementAt(2);
						String hours = (String) item.elementAt(3);
						String dayofmonth = (String) item.elementAt(4);
						String month = (String) item.elementAt(5);
						String dayofweek = (String) item.elementAt(6);
						String weekofyear = (String) item.elementAt(7);
						int result = Integer.parseInt((String) item
								.elementAt(8));
						int retries = Integer.parseInt((String) item
								.elementAt(9));
						String slasttime = (String) item.elementAt(10);
						Timestamp lasttime = new Timestamp(
								String2MilisecondNew(slasttime));
						String classname = (String) item.elementAt(11);
						String option = (String) item.elementAt(12);
						int alertmin = Integer.parseInt((String) item
								.elementAt(13));

						int notcharge = Integer.parseInt((String) item
								.elementAt(14));
						int idx = Integer.parseInt((String) item.elementAt(16));
						int num = Integer.parseInt((String) item.elementAt(17));

						CServices cservices = new CServices(services, minutes,
								hours, dayofmonth, month, dayofweek,
								weekofyear, result, retries, lasttime,
								classname, option);

						if (cservices.istime2run()) {
							// chay nao
							// System.out.println("chay nao");
							DeliveryManager_multi delegate = null;
							Class delegateClass = Class.forName(classname);
							Object delegateObject = delegateClass.newInstance();
							delegate = (DeliveryManager_multi) delegateObject;

							delegate.start(id, services, option, notcharge,
									idx, num);

							Timestamp tTime_current = new Timestamp(System
									.currentTimeMillis());
							java.util.Calendar calendar_current = java.util.Calendar
									.getInstance();
							calendar_current.setTime(new java.util.Date(
									tTime_current.getTime()));
							int nMinutes_current = calendar_current
									.get(Calendar.MINUTE);

							if (nMinutes_current > alertmin) {
								DBUtil.Alert("DeliveryDaily", "IsTime2RUN",
										"major", "Kiem tra dich vu:" + services
												+ "", "CongLT:0963536888");
							}

						} else {

							Timestamp tTime = new Timestamp(System
									.currentTimeMillis());
							java.util.Calendar calendar = java.util.Calendar
									.getInstance();
							calendar
									.setTime(new java.util.Date(tTime.getTime()));

							int nHour = calendar.get(Calendar.HOUR_OF_DAY);
							if (nHour < 6 && result != Constants.DELIVER_NOTRUN) {
								// Update
								String sqlUpdate = "update services_daily set result="
										+ Constants.DELIVER_NOTRUN
										+ " where id=" + id;
								// System.out.println("hicihc");
								DBUtil.executeSQL("gateway", sqlUpdate);
							}

						}

					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			} finally {

			}

			try {
				sleep(1000 * 20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
