package spam;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class sfone extends SpamQM {

	@Override
	protected String[] checkspam(MsgObject msgObject) throws Exception {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {

			String[] checkdbbycount = checkspamindbbycount(msgObject
					.getUserid(), msgObject.getMobileoperator());
			if (checkdbbycount[0].equalsIgnoreCase("1")) {
				return sRet;
			}
			return checkspamindb(msgObject.getUserid(), msgObject
					.getMobileoperator());
		} catch (Exception e) {
			Util.logger.error("checkspam:" + e.getMessage());
		}
		return sRet;
	}

	private String[] checkspamindb(String user_id, String operator) {

		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		String sqlselect = "select sum(t1.price) from  sms_receive_day_"
				+ operator.toLowerCase()
				+ " t2  join price t1 on  t1.service_id=t2.service_id and t2.user_id='"
				+ user_id + "'";
		Connection connection = null;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = DBUtils.getVectorTable(connection, sqlselect);
			if (result2.size() > 0) {
				Vector item = (Vector) result2.elementAt(0);
				String scount = (String) item.elementAt(0);
				long icount = Long.parseLong(scount);
				if (icount > 150000) {
					Util.logger.info(this.getClass().getName()
							+ "checkspam:userid=" + user_id
							+ " @amount:>150.000vnd");
					sRet[0] = "1";

					
					sRet[1] =  Constants._prop.getProperty("spam.mt."
							+ operator, "Ban da vi pham quy dinh chong Spam");
					
					return sRet;

				}

			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}

	private String[] checkspamindbbycount(String user_id, String operator) {

		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		String sqlselect = "";
		String sql5min = "select count(*)-3,5  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-5,current_timestamp )";
		String sql10min = "select count(*)-5,10  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-10,current_timestamp )";
		String sql60min = "select count(*)-30,60  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-60,current_timestamp )";
		String sqlallday = "select count(*)-300,1440  from sms_receive_day_"
				+ operator.toLowerCase() + " where user_id='" + user_id + "' ";
		Connection connection = null;

		sqlselect = sql5min + " union " + sql10min + " union " + sql60min
				+ " union " + sqlallday;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = DBUtils.getVectorTable(connection, sqlselect);
			for (int i = 0; i < result2.size(); i++) {
				Vector item = (Vector) result2.elementAt(i);
				String scount = (String) item.elementAt(0);
				String sdesc = (String) item.elementAt(1);
				int icount = Integer.parseInt(scount);

				if (icount > 0) {
					Util.logger.info(this.getClass().getName()
							+ "checkspam:userid=" + user_id + " @rule:"
							+ icount + "MO/" + sdesc + "minute");
					sRet[0] = "1";

					String mt1 = Constants._prop.getProperty("spam.mt."
							+ operator, "Ban da vi pham quy dinh chong Spam");
					sRet[1] = Constants._prop.getProperty("spam.mt." + operator
							+ "." + sdesc, mt1);
					return sRet;
				}
			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}

}
