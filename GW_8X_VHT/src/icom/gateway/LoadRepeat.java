package icom.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Hashtable;

import java.util.Vector;

public class LoadRepeat extends Thread {

	private Hashtable repeats;
	// public Vector vtRepeat;
	public boolean isLoaded = false;

	public LoadRepeat() {

	}

	public boolean isRepeat(String userid) {
		if (!"1".equalsIgnoreCase(Preference.BLACKLIST)) {
			//System.err.println("Notcheck");
			return false;
		}

		try {
			//System.err.println("userid:" + userid);
			
			String _userid = (String) repeats.get(userid);
			//System.err.println("_userid:" + _userid);
			

			if (_userid != null && userid.equals(_userid)) {
				Logger.info("{Loadrepeat.isRepeat}{Keytosearh=" + userid
						+ "} {repeat=true}");
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		Logger.info("{Loadrepeat.isRepeat}{Keytosearh=" + userid
				+ "} {repeat=false}");
		return false;

	}

	public static Hashtable retrieveRepeatlist() throws Exception {

		Logger.info("retrieveRepeatlist");

		String tableblacklist = "blacklist";
		String query = "select user_id from " + tableblacklist
				+ "  where upper(mobile_operator) = '"
				+ Preference.mobileOperator.toUpperCase() + "' ";

		// Logger.info("retrieveKeyword@"+ query);
		DBTools dbpool = new DBTools();
		Hashtable htList = new Hashtable();
		Vector vtrepeat = new Vector();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			connection = dbpool.getConnection(Preference.LIST_DB_CONFIG[0].trim());

			stmt = connection.prepareStatement(query);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					String userid = rs.getString(1);
					htList.put(userid, userid);
					//System.err.println("add:" + userid);
					//vtrepeat.addElement(userid);

				}
			}
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException ex2) {
			Logger.error("LoadRepeat. Ex:" + ex2.toString());
			Logger.printStackTrace(ex2);

			DBTools.ALERT("LoadRepeat", "LoadRepeat", Constants.ALERT_MAJOR,
					Preference.Channel + ex2.getMessage(),
					Preference.ALERT_CONTACT);

		} catch (Exception ex3) {
			Logger.error("LoadRepeat. Ex3:" + ex3.toString());
			Logger.printStackTrace(ex3);

			DBTools.ALERT("LoadRepeat", "LoadRepeat", Constants.ALERT_MAJOR,
					Preference.Channel + ex3.getMessage(),
					Preference.ALERT_CONTACT);

		}

		finally {
			dbpool.cleanup(connection);
		}
		// Gateway.loadrepeat.vtRepeat = vtrepeat;
		return htList;
	}

	public void run() {

		Logger.info("LoadRepeat - Start");
		isLoaded = false;
		while (Gateway.running) {

			try {
				repeats = retrieveRepeatlist();
				isLoaded = true;

				try {
					sleep(1 * 1000 * 60);

				} catch (InterruptedException ex3) {

				}
			} catch (Exception ex3) {
				Logger.crisis("Load repeat error:" + ex3.toString());
				Logger.printStackTrace(ex3);
				DBTools.ALERT("Load repeat error", "Load repeat error",
						Constants.ALERT_SERIOUS, Preference.Channel
								+ "Load config error: " + ex3.getMessage(),
						Preference.ALERT_CONTACT);

			}

		}

	}
}
