package icom.gateway;

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

import icom.common.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class ConfigGW {

	private final static String tablekeyword = "sfone_keyword_config";

	private String configname;
	private String configvalue;

	private static Utilities util = new Utilities();

	public static Hashtable retrieveConfigGW(String gateway_name)
			throws Exception {

		String query = "select c.name, a.val from gateway_config a, smsc_dict b, paramgw_dict c where a.smscid = b.id and a.paramid = c.id and b.name='"
				+ gateway_name + "'";

		Hashtable keywords = new Hashtable();
		Logger.info("ConfigGW.retrieve", "query:" + query);

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DBTools.getConnection(Preference.LIST_DB_CONFIG[0]);
			stmt = connection.prepareStatement(query);
			if (stmt.execute()) {
				rs = stmt.getResultSet();

				while (rs.next()) {
					ConfigGW keywordtemp = new ConfigGW();
					keywordtemp.configname = rs.getString(1);
					keywordtemp.configvalue = rs.getString(2);

					keywords.put(keywordtemp.configname, keywordtemp);

				}
			}

		} catch (SQLException ex2) {
			Logger.error("ConfigGW.retrieveConfiggw", "Load config. Ex:"
					+ ex2.toString());
			DBTools.ALERT("ConfigGW", "retrieveConfiggw", Constants.ALERT_WARN,
					Preference.Channel + "@Load config. Ex:" + ex2.toString(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(ex2);
		} catch (Exception ex2) {
			Logger.printStackTrace(ex2);
			Logger.error("ConfigGW.retrieveConfiggw", "Load config. Ex:"
					+ ex2.toString());
			DBTools.ALERT("ConfigGW", "retrieveConfiggw", Constants.ALERT_WARN,
					Preference.Channel + "@Load config. Ex:" + ex2.toString(),
					Preference.ALERT_CONTACT);
		} finally {
			util.cleanup(rs);
			util.cleanup(connection, stmt);
		}

		return keywords;
	}

	public String getConfigname() {
		return configname;
	}

	public void setConfigname(String configname) {
		this.configname = configname;
	}

	public String getConfigvalue() {
		return configvalue;
	}

	public void setConfigvalue(String configvalue) {
		this.configvalue = configvalue;
	}

}
