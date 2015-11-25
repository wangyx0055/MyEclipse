package DAO;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WinnerDailyDAO {
	public static String getDgAmountWinner(String date,String pool) {
		String sReturn = "";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sql = "Select * from winner_daily where date_win = '" + date
				+ "'";

		try {
			con = dbpool.getConnection(pool);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				sReturn = rs.getString("dg_amount");
			}
		} catch (Exception e) {
			Util.logger.error("getDgAmountWinner @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(rs, ps);
			dbpool.cleanup(con);
		}
		return sReturn;
	}
}
