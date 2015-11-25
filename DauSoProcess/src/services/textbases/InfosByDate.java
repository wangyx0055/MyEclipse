package services.textbases;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class InfosByDate extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		// TODO Auto-generated method stub
		try {

			Collection messages = new ArrayList();

			String sMTReturn = mtReturn(msgObject.getUserid(), msgObject
					.getServiceid(), msgObject.getKeyword(), keyword
					.getOptions());

			if (sMTReturn == null) {

				msgObject.setUsertext("Dich vu tam dung");
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));

			} else {

				String[] mt2send = sMTReturn.split("###");
				for (int i = 0; i < mt2send.length; i++) {
					if (!"".equalsIgnoreCase(mt2send[i])) {
						msgObject.setUsertext(mt2send[i]);
						if (i == 0) {
							msgObject.setMsgtype(1);

						} else {
							msgObject.setMsgtype(0);
						}
						messages.add(new MsgObject(msgObject));
					}
				}
			}

			return messages;

		} catch (Exception e) {
			// TODO: handle exception
			return null;

		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
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

	public static String Timestamp2DDMMYYYY(java.sql.Timestamp ts) {
		if (ts == null) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts.getTime()));

			String strTemp = Integer.toString(calendar
					.get(calendar.DAY_OF_MONTH));
			if (calendar.get(calendar.DAY_OF_MONTH) < 10) {
				strTemp = "0" + strTemp;
			}
			if (calendar.get(calendar.MONTH) + 1 < 10) {
				return strTemp + "/0" + (calendar.get(calendar.MONTH) + 1)
						+ "/" + calendar.get(calendar.YEAR);
			} else {
				return strTemp + "/" + (calendar.get(calendar.MONTH) + 1) + "/"
						+ calendar.get(calendar.YEAR);
			}
		}
	}

	private String mtReturn(String userid, String serviceid, String keyword,
			String options) throws Exception {
		HashMap _option = new HashMap();
		_option = getParametersAsString(options);

		String INFO_ID = ((String) _option.get("info_id")).toUpperCase();
		String DBTT = "DBTT";
		String SDATE = "";
		long milliSecond = System.currentTimeMillis();
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		if (DBTT.equalsIgnoreCase(INFO_ID)) {
			if (calendar.get(calendar.HOUR_OF_DAY) < 17) {
				SDATE = Timestamp2DDMMYYYY(new Timestamp(milliSecond));
			} else {
				SDATE = Timestamp2DDMMYYYY(new Timestamp(milliSecond + 24 * 60
						* 60 * 1000));
			}

		} else {
			SDATE = Timestamp2DDMMYYYY(new Timestamp(milliSecond));
		}

		return getInfo(SDATE, INFO_ID);
	}

	private String getInfo(String sDate, String infoid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection("gateway");

			String query = "select content from icom_infoservice where info_type ='"
					+ infoid
					+ "' and DATE_FORMAT(info_date,'%d/%m/%Y')='"
					+ sDate + "'";

			Vector result = DBUtil.getVectorTable(connection, query);
			if (result.size() == 0) {
				return null;
			} else {

				for (int i = 0; i < result.size(); i++) {

					Vector item = (Vector) result.elementAt(i);
					return (String) item.elementAt(0);

				}

			}

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getInfo: Failed"
					+ ex.getMessage());
			ex.printStackTrace();

		} finally {
			dbpool.cleanup(connection);
		}
		return null;

	}

}
