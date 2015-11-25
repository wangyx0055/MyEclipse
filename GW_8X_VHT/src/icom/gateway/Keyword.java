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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;



public class Keyword {

	private final static String tablekeyword = "keyword_config";

	private String serviceid;
	private String keyword;
	private String classname;
	private int cpid = 0;
	private String options;
	private Utilities util  = new Utilities();
	

	public String getKeyword() {
		return keyword;
	}

	public String getServiceid() {
		return serviceid;
	}

	public String getClassname() {
		return classname;
	}

	public String getOptions() {
		return options;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public static Hashtable retrieveKeyword() throws Exception {

		Logger.info("retrieveKeyword");
		String query = "select service_id,keyword,class_name,options, cpid from "
				+ tablekeyword
				+ "  where status = 1 and current_timestamp >= activedate and ((current_timestamp < inactivedate ) or inactivedate is null  or inactivedate='0000-00-00 00:00:00' ) order by length(keyword) desc, keyword asc ";
		Logger.info("retrieveKeyword@"+ query);
		DBTools dbpool = new DBTools();
		Hashtable keywords = new Hashtable();
		Vector vtkeywords = new Vector();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			connection = dbpool.getConnection(Preference.LIST_DB_CONFIG[0].trim());

			stmt = connection.prepareStatement(query);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					Keyword keywordtemp = new Keyword();
					keywordtemp.serviceid = rs.getString(1);
					keywordtemp.keyword = rs.getString(2);
					keywordtemp.classname = rs.getString(3);
					keywordtemp.options = rs.getString(4);
					keywordtemp.cpid = rs.getInt(5);
					keywords.put(keywordtemp.serviceid + "@"
							+ keywordtemp.keyword, keywordtemp);
					
					vtkeywords.addElement(keywordtemp.serviceid + "@"
							+ keywordtemp.keyword);
					
					

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
			Logger.error("Load Keyword. Ex:" + ex2.toString());
			
			DBTools.ALERT("LoadKeyword",
					"LoadKeywordError", Constants.ALERT_MAJOR,
					Preference.Channel
							+ ex2.getMessage()
							,
					Preference.ALERT_CONTACT);
			

		} catch (Exception ex3) {
			Logger.error("Load Keyword. Ex3:" + ex3.toString());
			
			DBTools.ALERT("LoadKeyword",
					"LoadKeywordError", Constants.ALERT_MAJOR,
					Preference.Channel
							+ ex3.getMessage()
							,
					Preference.ALERT_CONTACT);

			

		}

		finally {
			dbpool.cleanup(connection);
		}
		Gateway.loadconfig.vtKeyword = vtkeywords;
		return keywords;
	}

	public int getCpid() {
		return cpid;
	}

	public void setCpid(int cpid) {
		this.cpid = cpid;
	}

}
