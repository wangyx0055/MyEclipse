package advice;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class AuctionDaily extends Thread {

	private String keyword = "SP";
	private String serviceid = "9199";
	private int cpid = 26;

	public AuctionDaily() {

	}

	private void sendmt(int phien) {

		String userid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String maxprice = "";
		String minprice = "";
		String mt = "";
		try {
			connection = dbpool.getConnectionGateway();

			String query2 = "select max(price),min(price) from sfone_sieure where phien="
					+ phien;
			statement = connection.prepareStatement(query2);
			ResultSet rs2 = null;
			if (statement.execute()) {
				rs2 = statement.getResultSet();
				while (rs2.next()) {
					maxprice = rs2.getString(1);
					minprice = rs2.getString(2);
				}

			}
			mt = "Gia cua san pham hien dang o muc tu " + minprice
					+ "000d den " + maxprice
					+ "000d. De mua voi gia re nhat, hay tiep tuc soan: "
					+ keyword + " giaSP gui " + serviceid;

			dbpool.cleanup(rs2, statement);
			
			String query = "select distinct (user_id) from sfone_sieure where phien="
					+ phien;

			statement = connection.prepareStatement(query);
			if (statement.execute()) {
				rs = statement.getResultSet();
				Timestamp tTime = new Timestamp(System.currentTimeMillis());
				while (rs.next()) {
					userid = rs.getString(1);
					DBUtil.sendMT(new MsgObject(serviceid, userid, keyword, mt,
							new BigDecimal(1), tTime, "SFONE", 3, 0, cpid,
							"MTPush-Sieure"));

				}
			}
		} catch (SQLException ex2) {
			Util.logger.error("getWinner. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("getWinner. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}

	}

	public void run() {

		Util.logger.info("AuctionDaily - Start");
		System.out.println("AuctionDaily - Start");
		while (ConsoleSRV.processData) {
			try {
				// Gui mt quang cao
				if ((DBUtil.getDayOfWeek() == 6)
						&& (DBUtil.getHourOfDay() == 17)) {
					
					int phien = getcurrentPhien();
					
					sendmt(phien);

				}
				// gui thong bao trung thuong
				
				if ((DBUtil.getDayOfWeek() == 2)
						&& (DBUtil.getHourOfDay() == 8)) {
					String[] winner = getWinner();
					int isWinner = Integer.parseInt(winner[0]);
					String sWinnerUser = winner[2];
					String sWinnerPrice = winner[1];
					int phien = Integer.parseInt(winner[3]);

					if (isWinner == 1) {

						long iWinnerPrice = Long.parseLong(sWinnerPrice);
						String mtWinner = "Chuc mung ban la nguoi tra gia thap nhat va duy nhat. Ban duoc quyen mua san pham voi gia "
								+ iWinnerPrice + " 000d. DT ho tro 1800095";
						try {
							if (saveWinner("SIEURE" + phien, keyword,
									serviceid, sWinnerUser, mtWinner,
									iWinnerPrice) < 0) {
								Util.logger.error("saveWinner failed- phien="
										+ phien);
							} else {
								Timestamp tTime = new Timestamp(System
										.currentTimeMillis());
								DBUtil.sendMT(new MsgObject(serviceid,
										sWinnerUser, keyword, mtWinner,
										new BigDecimal(1), tTime, "SFONE", 3,
										0, cpid, "MTPush-Sieure"));

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				try {
					sleep(1000 * 60 * 60);

				} catch (InterruptedException ex3) {

				}
			} catch (Exception ex3) {
				Util.logger.crisis("Loi khi doc cau hinh:" + ex3.toString());

			}

		}

	}
	private void sendalertAuction()
	{
		
	}

	private int getcurrentPhien() {
		Connection connection = null;
		PreparedStatement statement = null;
		// String sqlString = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		int phien = 0;
		connection = dbpool.getConnectionGateway();
		if (connection == null) {
			Util.logger.error("Impossible to connect to DB");
		}
		String query0 = "select phien from sfone_dmphiensr where current_timestamp > "
				+ " begintime and current_timestamp < endtime";
		try {
			statement = connection.prepareStatement(query0);
			if (statement.execute()) {
				rs = statement.getResultSet();
				if (rs.next()) {
					phien = rs.getInt(1);

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return phien;

	}

	private String[] getWinner() {
		String[] result = new String[10];
		String Winner = "-1";
		String WinnerUser = "";
		String WinnerPrice = "";
		int phien = 0;

		Connection connection = null;
		PreparedStatement statement = null;
		// String sqlString = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		// select * from sfone_dmphiensr where adddate(current_timestamp,-7) >
		// begintime and adddate(current_timestamp,-7) < endtime ;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
			}
			String query0 = "select * from sfone_dmphiensr where adddate(current_timestamp,-7) > "
					+ " begintime and adddate(current_timestamp,-7) < endtime";
			statement = connection.prepareStatement(query0);
			if (statement.execute()) {
				rs = statement.getResultSet();
				if (rs.next()) {
					phien = rs.getInt(1);

				}
			}
			String query1 = "select min(price)from (select price,count(*) from (select * from sfone_sieure where phien="
					+ phien
					+ ") as T1 group by price having count(*) = 1) as T2";
			statement = connection.prepareStatement(query1);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					WinnerPrice = rs.getString(1);
					Util.logger.info("WinnerPrice" + WinnerPrice);
				}
			}
			dbpool.cleanup(rs, statement);
			if (!"".equals(WinnerPrice)) {
				Winner = "1";
				int iWinnerPrice = Integer.parseInt(WinnerPrice);
				String query2 = "select user_id from sfone_sieure where price = "
						+ iWinnerPrice + " and phien=" + phien;
				statement = connection.prepareStatement(query2);
				if (statement.execute()) {
					rs = statement.getResultSet();
					while (rs.next()) {
						WinnerUser = rs.getString(1);
						Util.logger.info("WinnerUser" + WinnerUser);
					}
				}
			}
			result[0] = Winner;
			result[1] = WinnerPrice;
			result[2] = WinnerUser;
			result[3] = "" + phien;
		} catch (SQLException ex2) {
			Util.logger.error("getWinner. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("getWinner. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(connection);
		}

		return result;
	}

	private int saveWinner(String gameid, String keyword, String serviceid,
			String userid, String mttext, long lsequence) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		DBPool dbpool = new DBPool();
		// Util.logger.info("sendMT:" + msgObject.getUserid()+ "@TO" +
		// msgObject.getServiceid() + "@" + msgObject.getUsertext() );
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return -1;
			}
			String sqlInsert = "Insert into sfone_winner (game_id,command_code,user_id,service_id,info,sequence) values ('"
					+ gameid
					+ "','"
					+ keyword
					+ "','"
					+ userid
					+ "','"
					+ serviceid + "','" + mttext + "'," + lsequence + ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("SieuRe: Insert in to sfone_winner Failed");
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.error("SieuRe: Error:" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error("SieuRe: Error:" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

}
