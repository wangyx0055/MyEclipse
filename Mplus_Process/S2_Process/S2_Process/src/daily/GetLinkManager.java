package daily;

import icom.DBPool;
import icom.common.Util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetLinkManager {

	public String poolName = "store";

	/***
	 * Get Link Game from DB Link
	 * 
	 * @param cateId
	 * @param mediaId
	 *            = -1
	 * @param requestTime
	 *            = current time
	 * @param userId
	 *            = phone number
	 * @return
	 */
	public String getLinkGame1(int cateId, int mediaId, String requestTime,
			String userId, double price) {

		String result = "";

		Connection connection = null;
		PreparedStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "EXEC  [dbo].[Sp_GetLinkGame_S2] " + "@CateID = "
				+ cateId + "," + "@MediaID =  " + mediaId + ", "
				+ "@RequestTime = N'" + requestTime + "' ," + "@MSISDN = N'"
				+ userId + "' , " + "@Price = " + price;

		Util.logger.info("getLinkGame QUERRY: " + proceExe);

		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareStatement(proceExe);

			rs = callStmt.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
				Util.logger.info("getLinkGame row count = 1 ");
				break;
			}

		} catch (SQLException ex3) {
			Util.logger.error("GetLinkManager - getLinkGame. SQLException:"
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("GetLinkManager- getLinkGame. SQLException:"
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}

	public String getLinkGame(int cateId, int mediaId, String requestTime,
			String userId, double price) {

		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call dbo.Sp_GetLinkGame_S2(?,?,?,?,?,?) }";


		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareCall(proceExe);
			callStmt.setInt(1, cateId);
			callStmt.setInt(2, mediaId);
			callStmt.setString(3, requestTime);
			callStmt.setString(4, userId);
			callStmt.setDouble(5, price);
			callStmt.registerOutParameter(6, java.sql.Types.NVARCHAR);
			
			callStmt.execute();
			
			result = callStmt.getString(6);

		} catch (SQLException ex3) {
			Util.logger.error("GetLinkManager - getLinkGame. SQLException:"
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("GetLinkManager- getLinkGame. SQLException:"
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}

	/****
	 * This Method not use
	 * @param mediaId
	 * @param requestTime
	 * @param userId
	 * @param price
	 * @return
	 */
	public String getLinkRing1(int mediaId, String requestTime, String userId,
			double price) {
		String result = "";

		Connection connection = null;
		PreparedStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "EXEC  [dbo].[Sp_GetLinkRingtone_S2] "
				+ "@MediaID =  " + mediaId + ", " + "@RequestTime = N'"
				+ requestTime + "' ," + "@MSISDN = N'" + userId + "' , "
				+ "@Price = " + price;

		Util.logger.info("getLinkRing QUERRY: " + proceExe);

		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareStatement(proceExe);

			rs = callStmt.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
				Util.logger.info("getLinkRing row count = 1 ");
				break;
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("getLinkRing - getLinkRingAndPicture. SQLException:"
							+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger
					.error("getLinkRing- getLinkRingAndPicture. SQLException:"
							+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}

	public String getLinkRing(int mediaId, String requestTime, String userId,
			double price) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call dbo.Sp_GetLinkRingtone_S2(?,?,?,?,?) }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareCall(proceExe);
			callStmt.setInt(1, mediaId);
			callStmt.setString(2, requestTime);
			callStmt.setString(3, userId);
			callStmt.setDouble(4, price);
			callStmt.registerOutParameter(5, java.sql.Types.NVARCHAR);
			
			callStmt.execute();
			
			result = callStmt.getString(5);

		} catch (SQLException ex3) {
			Util.logger.error("GetLinkManager - getLinkRing. SQLException:"
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("GetLinkManager- getLinkRing. SQLException:"
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}

	/***
	 * This Method not use
	 * @param mediaId
	 * @param requestTime
	 * @param userId
	 * @param price
	 * @return
	 */
	public String getLinkImage1(int mediaId, String requestTime, String userId,
			double price) {
		String result = "";

		Connection connection = null;
		PreparedStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "EXEC  [dbo].[Sp_GetLinkImage_S2] " + "@MediaID =  "
				+ mediaId + ", " + "@RequestTime = N'" + requestTime + "' ,"
				+ "@MSISDN = N'" + userId + "' , " + "@Price = " + price;

		Util.logger.info("getLinkImage QUERRY: " + proceExe);

		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareStatement(proceExe);

			rs = callStmt.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
				Util.logger.info("getLinkImage row count = 1 ");
				break;
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("getLinkImage - getLinkRingAndPicture. SQLException:"
							+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger
					.error("getLinkImage- getLinkRingAndPicture. SQLException:"
							+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}
	
	public String getLinkImage(int mediaId, String requestTime, String userId,
			double price) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call dbo.Sp_GetLinkImage_S2(?,?,?,?,?) }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareCall(proceExe);
			callStmt.setInt(1, mediaId);
			callStmt.setString(2, requestTime);
			callStmt.setString(3, userId);
			callStmt.setDouble(4, price);
			callStmt.registerOutParameter(5, java.sql.Types.NVARCHAR);
			
			callStmt.execute();
			
			result = callStmt.getString(5);

		} catch (SQLException ex3) {
			Util.logger.error("GetLinkManager - getLinkImage. SQLException:"
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("GetLinkManager- getLinkImage. SQLException:"
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}
	
	
	public String getLinkClip(int mediaId, String requestTime, String userId,
			double price) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call dbo.Sp_GetLinkVideo_S2(?,?,?,?,?) }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareCall(proceExe);
			callStmt.setInt(1, mediaId);
			callStmt.setString(2, requestTime);
			callStmt.setString(3, userId);
			callStmt.setDouble(4, price);
			callStmt.registerOutParameter(5, java.sql.Types.NVARCHAR);
			
			callStmt.execute();
			
			result = callStmt.getString(5);

		} catch (SQLException ex3) {
			Util.logger.error("GetLinkManager - getLinkClip. SQLException:"
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("GetLinkManager- getLinkClip. SQLException:"
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}
	
	/**
	 * Get Link Image for Promotion
	 * @param mediaId
	 * @param requestTime
	 * @param userId
	 * @param price
	 * @return
	 */
	
	public String getLinkImagePromotion(int mediaId, String requestTime, String userId,
			double price){
		
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call dbo.Sp_GetLinkImage_KhuyenMai(?,?,?,?,?) }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareCall(proceExe);
			callStmt.setInt(1, mediaId);
			callStmt.setString(2, requestTime);
			callStmt.setString(3, userId);
			callStmt.setDouble(4, price);
			callStmt.registerOutParameter(5, java.sql.Types.NVARCHAR);
			
			callStmt.execute();
			
			result = callStmt.getString(5);

		} catch (SQLException ex3) {
			Util.logger.error("GetLinkManager - getLinkImagePromotion. SQLException:"
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("GetLinkManager- getLinkImagePromotion. SQLException:"
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
		
	}
	
	/**
	 * 
	 * @param mediaId
	 * @param requestTime
	 * @param userId
	 * @param price
	 * @return
	 */
	public String getLinkRingPromotion(int mediaId, String requestTime, String userId,
			double price) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call dbo.Sp_GetLinkRingtone_KhuyenMai(?,?,?,?,?) }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection(poolName);
			}
			callStmt = connection.prepareCall(proceExe);
			callStmt.setInt(1, mediaId);
			callStmt.setString(2, requestTime);
			callStmt.setString(3, userId);
			callStmt.setDouble(4, price);
			callStmt.registerOutParameter(5, java.sql.Types.NVARCHAR);
			
			callStmt.execute();
			
			result = callStmt.getString(5);

		} catch (SQLException ex3) {
			Util.logger.error("GetLinkManager - getLinkRingPromotion. SQLException:"
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("GetLinkManager- getLinkRingPromotion. SQLException:"
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}
	
}
