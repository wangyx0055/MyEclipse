package icom;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

public class Prices {

	

	private String service_ss_id;
	private int price_new = 0;
	private int mt_free = 0;

	public String getService_ss_id() {
		return service_ss_id;
	}

	public void setService_ss_id(String service_ss_id) {
		this.service_ss_id = service_ss_id;
	}

	public int getPrice_new() {
		return price_new;
	}

	public void setPrice_new(int price_new) {
		this.price_new = price_new;
	}

	public int getMt_free() {
		return mt_free;
	}

	public void setMt_free(int mt_free) {
		this.mt_free = mt_free;
	}

	@SuppressWarnings("unchecked")
	public static Hashtable retrieve() throws Exception {

		String query = "select service_ss_id, price_new, mt_free from prices where begin_time <= current_timestamp() and end_time >= current_timestamp() ";

		Util.logger.info("retrievePrice:" + query);

		DBPool dbpool = new DBPool();
		Hashtable prices = new Hashtable();
		Vector vtprices = new Vector();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			connection = dbpool.getConnectionGateway();

			stmt = connection.prepareStatement(query);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					Prices objtemp = new Prices();
					// service_ss_id, price_new, mt_free
					objtemp.service_ss_id = rs.getString("service_ss_id");
					objtemp.mt_free = rs.getInt("mt_free");
					objtemp.price_new = rs.getInt("price_new");

					prices.put(objtemp.service_ss_id, objtemp);
					vtprices.addElement(objtemp.service_ss_id);

				}
			}
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

			dbpool.cleanup(connection);

		} catch (SQLException ex2) {
			Util.logger.error("Load Keyword. Ex:" + ex2.toString());
			DBUtil.Alert("Process.LoadKeyword", "LoadKeyword.SQLException",
					"major", "LoadMO.SQLException:" + ex2.toString(),
					"processAdmin");

		} catch (Exception ex3) {
			Util.logger.error("Load Keyword. Ex3:" + ex3.toString());
			DBUtil.Alert("Process.LoadKeyword", "LoadKeyword.Exception",
					"major", "LoadMO.Exception:" + ex3.toString(),
					"processAdmin");

		}

		finally {
			dbpool.cleanup(connection);
		}

		Sender.loadconfig.vtPrices = vtprices;
		return prices;
	}

}
