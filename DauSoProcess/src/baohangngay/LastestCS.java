package baohangngay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Hashtable;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;

public class LastestCS extends Thread {

	public static Hashtable MTADD = null;
	public boolean CS_LOADED = false;

	static public String getMTADD(String keyword, String serviceid) {
		String retobj = "";

		try {
			retobj = (String) MTADD.get(serviceid + "@" + keyword);
			return retobj;

		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
		}

		return "";
	}

	private void loadLOTTERY_CS_MTADD() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Hashtable mtadd = new Hashtable();
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String sKeyword = "";
			String sMTADV = "";
			String sServiceid = "";
			stmt = connection
					.prepareStatement(
							"SELECT command_code,  mt_add,service_id FROM icom_mtadd_cnt  ORDER BY ID ASC",
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					sKeyword = rs.getString(1);
					sMTADV = rs.getString(2);
					sServiceid = rs.getString(3);
					mtadd.put(sServiceid + "@" + sKeyword, sMTADV);

				}
				MTADD = mtadd;
			}

		} catch (Exception ex3) {
			Util.logger.error("Khong load duoc LastestCS@loadLOTTERY_MTADD"
					+ ex3.toString());
			ex3.printStackTrace();

		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

	}

	public void run() {

		while (ConsoleSRV.processData) {

			try {

				loadLOTTERY_CS_MTADD();

				this.CS_LOADED = true;

				sleep(1000 * 60 * 1);

			} catch (InterruptedException ex3) {
				//System.out.println("Error:InterruptedException "
				//		+ ex3.toString());
			}
		}
	}
}
