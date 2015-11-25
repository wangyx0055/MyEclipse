package spam;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class viettel extends SpamQM {

	@Override
	protected String[] checkspam(MsgObject msgObject) throws Exception {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {
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

					//sRet[1] = "Tin nhan SPAM. Ban khong duoc su dung dich vu qua 150.000d/ngay. Vui long goi 1900561558 de duoc huong dan them";
					sRet[1] = Constants._prop.getProperty("spam.mt."
							+ operator,
							"Ban da vi pham quy dinh chong Spam");
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
