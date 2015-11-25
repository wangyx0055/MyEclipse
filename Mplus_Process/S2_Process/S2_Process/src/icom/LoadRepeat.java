package icom;

import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import java.util.Vector;

public class LoadRepeat extends Thread {

	private Hashtable repeats;

	public Hashtable getRepeats() {
		return repeats;
	}

	public void setRepeats(Hashtable repeats) {
		this.repeats = repeats;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	// public Vector vtRepeat;
	public boolean isLoaded = false;

	public LoadRepeat() {

	}

	public boolean isRepeat(String userid) {
		if (!"1".equalsIgnoreCase(Constants.BLACKLIST)) {
			// System.err.println("Notcheck");
			return false;
		}

		try {
			// System.err.println("userid:" + userid);
			String _userid = (String) repeats.get(userid);
			// System.err.println("_userid:" + _userid);

			if (_userid != null && userid.equals(_userid)) {
				Util.logger.info("{Loadrepeat.isRepeat}{Keytosearh=" + userid
						+ "} {repeat=true}");
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
		}

		Util.logger.info("{Loadrepeat.isRepeat}{Keytosearh=" + userid
				+ "} {repeat=false}");
		return false;

	}

	public static Hashtable retrieveRepeatlist() throws Exception {

		//Util.logger.info("retrieveRepeatlist");

		String tableblacklist = "icom_black_list";
		String query = "select user_id from " + tableblacklist;

		// Logger.info("retrieveKeyword@"+ query);
		DBPool dbpool = new DBPool();
		Hashtable htList = new Hashtable();
		Vector vtrepeat = new Vector();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			connection = dbpool.getConnectionGateway();

			stmt = connection.prepareStatement(query);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					String userid = rs.getString(1);
					htList.put(userid, userid);

				}
			}
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException ex2) {
			Util.logger.error("LoadRepeat. Ex:" + ex2.toString());
			Util.logger.printStackTrace(ex2);

			DBUtil.Alert("Process.Loadrepeat", "Loadrepeat.Exception", "major",
					"LoadRepeat.Exception:" + ex2.toString(), "processAdmin");

		} catch (Exception ex3) {
			Util.logger.error("LoadRepeat. Ex3:" + ex3.toString());
			Util.logger.printStackTrace(ex3);

			DBUtil.Alert("Process.Loadrepeat", "Loadrepeat.Exception", "major",
					"LoadRepeat.Exception:" + ex3.toString(), "processAdmin");

		}

		finally {
			dbpool.cleanup(connection);
		}
		// Gateway.loadrepeat.vtRepeat = vtrepeat;
		return htList;
	}

	@Override
	public void run() {

		Util.logger.info("LoadRepeat - Start");
		isLoaded = false;
		String new_day = "";
		String old_day = "";
		boolean isruned12 = false;
		while (Sender.getData) {

			try {
				repeats = retrieveRepeatlist();
				isLoaded = true;

				try {
					sleep(1 * 100 * 60);

				} catch (InterruptedException ex3) {

				}
			} catch (Exception ex3) {
				Util.logger.crisis("Load repeat error:" + ex3.toString());
				Util.logger.printStackTrace(ex3);
				DBUtil.Alert("Process.Loadrepeat", "Loadrepeat.Exception",
						"major", "LoadRepeat.Exception:" + ex3.toString(),
						"processAdmin");

			}

		}

	}
}
