package advice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;

public class RunClassLastSongbd extends Thread {

	public static Hashtable LAST_SONG = null;
	public boolean LOAD = false;
	public static int isProcess = 0;

	public static String getLAST_SONG(String lastSong) {
		String retobj = "";

		try {
			retobj = (String) LAST_SONG.get(lastSong);
			return retobj;

		} catch (Exception e) {
			return "";
		}

	}

	@Override
	public void run() {

		try {

			while (ConsoleSRV.processData) {
				try {
					getTime();
					this.LOAD = true;
					this.sleep(1000);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			Util.logger.error("Error Run: " + ex.toString());
		}
	}

	private Hashtable getTime() {

		String[] result = new String[18];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		Hashtable lastSong = new Hashtable();
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return LAST_SONG;
			}

			String sqlSelect = "SELECT lastsong, subquestion, answer, start_time, block1, block2, block3, block4, block5, block6, block7, block8, finish_time, start_vote, end_vote, isprocess, id, block9 FROM icom_lastsong_configbd WHERE active = 1";
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				int i = 0;
				// Util.logger.info("SQL SELECT : " + sqlSelect);
				while (rs.next()) {
					i++;
					result[0] = rs.getString(1);
					lastSong.put("lastsong", result[0]);
					Util.logger.info("Last Song: " + result[0]);
					result[1] = rs.getString(2);
					lastSong.put("subquestion", result[1]);
					result[2] = rs.getString(3);
					lastSong.put("answer", result[2]);
					result[3] = rs.getString(4);
					lastSong.put("start_time", result[3]);
					result[4] = rs.getString(5);
					lastSong.put("block1", result[4]);
					result[5] = rs.getString(6);
					lastSong.put("block2", result[5]);
					result[6] = rs.getString(7);
					lastSong.put("block3", result[6]);
					result[7] = rs.getString(8);
					lastSong.put("block4", result[7]);
					result[8] = rs.getString(9);
					lastSong.put("block5", result[8]);
					result[9] = rs.getString(10);
					lastSong.put("block6", result[9]);
					result[10] = rs.getString(11);
					lastSong.put("block7", result[10]);
					result[11] = rs.getString(12);
					lastSong.put("block8", result[11]);
					result[12] = rs.getString(13);
					lastSong.put("finish_time", result[12]);
					result[13] = rs.getString(14);
					lastSong.put("start_vote", result[13]);
					result[14] = rs.getString(15);
					lastSong.put("end_vote", result[14]);
					result[15] = rs.getString(16);
					lastSong.put("isprocess", result[15]);
					result[16] = rs.getString(17);
					lastSong.put("id", result[16]);
					result[17] = rs.getString(18);
					lastSong.put("block9", result[17]);

				}

				LAST_SONG = lastSong;
			
					if ("1".equalsIgnoreCase(getLAST_SONG("isprocess"))) {
						isProcess = 1;
					} else if ("2".equalsIgnoreCase(getLAST_SONG("isprocess"))) {
						isProcess = 2;
					} else if ("3".equalsIgnoreCase(getLAST_SONG("isprocess"))) {
						isProcess = 3;
					} else {
						isProcess = 0;
					}
				
			}

			return LAST_SONG;
		} catch (SQLException e) {
			Util.logger.error(": Error1:" + e.toString());
			return LAST_SONG;
		} catch (Exception e) {
			Util.logger.error(": Error2:" + e.toString());
			return LAST_SONG;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
}
