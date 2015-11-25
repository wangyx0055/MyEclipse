package DAO;

import icom.DBPool;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Vector;

import DTO.PromotionCodeDTO;

public class PromotionDAO {

	public static int activeSendMT(String msisdn, String commandCode) {
		int iReturn = 0;

		String funName = "activeProtionCode ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "UPDATE service_promotion_code SET mt_status = 1 WHERE msisdn = '"
				+ msisdn + "' AND command_code = '" + commandCode + "'";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				ps.execute();
				iReturn = 1;
				Util.logger.info(funName + "@sql:" + sql);

			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}

	public static int activeProtionCode(int iProCode) {
		int iReturn = 0;

		String funName = "activeProtionCode ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "UPDATE service_promotion_code SET active = 1, active_time = NOW() WHERE promotion_code = "
				+ iProCode;
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				ps.execute();
				iReturn = 1;
				Util.logger.info(funName + "@sql:" + sql);
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}

	
	public static int deactiveProtionCode(String msisdn, String commandCode) {
		int iReturn = 0;

		String funName = "activeProtionCode ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "UPDATE service_promotion_code SET active = 2, active_time = NOW() WHERE msisdn = '"
				+ msisdn + "' AND command_code = '" + commandCode + "'";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				ps.execute();
				iReturn = 1;
				Util.logger.info(funName + "@sql:" + sql);
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}
	
	public static Vector<PromotionCodeDTO> getPromotionCodeDTO(String msisdn,
			String commandCode) {
		Vector<PromotionCodeDTO> vProCode = new Vector<PromotionCodeDTO>();

		String funName = "getPromotionCodeDTO ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT *  FROM  service_promotion_code WHERE msisdn = '"
				+ msisdn + "' AND command_code = '" + commandCode + "'";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					Timestamp activeTime = rs.getTimestamp("active_time");
					int iProCode = rs.getInt("promotion_code");
					int active = rs.getInt("active");
					int mtStatus = rs.getInt("mt_status");

					PromotionCodeDTO proCode = new PromotionCodeDTO();
					proCode.setMsisdn(msisdn);
					proCode.setCommandCode(commandCode);
					proCode.setActive(active);
					proCode.setActiveTime(activeTime);
					proCode.setMtStatus(mtStatus);
					proCode.setPromotionCode(iProCode);

					vProCode.add(proCode);
				}
				//Util.logger.info(funName + "@sql:" + sql);
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}

		return vProCode;
	}
	
	public static Vector<String> getServiceNameP(String poolName){
		String funName = "getServiceNameP ";
		Vector<String> vSerPro = new Vector<String>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM service_promotion";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnection(poolName);
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					String commandCode = rs.getString("service");
					String numP = rs.getInt("num_promotion") + "";

					vSerPro.add(commandCode);
				}

			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return vSerPro;
	}

	public static Hashtable<String, String> getServicePromotion() {
		String funName = "getServicePromotion ";
		Hashtable<String, String> hSerPro = new Hashtable<String, String>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM service_promotion";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					String commandCode = rs.getString("service");
					String numP = rs.getInt("num_promotion") + "";

					hSerPro.put(commandCode, numP);
				}

			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return hSerPro;
	}

	public static int hasPromotionCode(String msisdn, String commandCode) {
		int iReturn = 0;

		String funName = "hasPromotionCode ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) AS num FROM  service_promotion_code WHERE msisdn = '"
				+ msisdn + "' AND command_code = '" + commandCode + "'";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					iReturn = rs.getInt("num");
				}
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}
	
	public static int hasPromotionCodeNotActive(String msisdn, String commandCode) {
		int iReturn = 0;

		String funName = "hasPromotionCode ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) AS num FROM  service_promotion_code WHERE msisdn = '"
				+ msisdn + "' AND command_code = '" + commandCode + "'";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					iReturn = rs.getInt("num");
				}
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}

	public static int insertPromotion(String msisdn, String commandCode) {
		int iReturn = 0;

		String funName = "insertPromotion ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "INSERT service_promotion_code(msisdn, command_code, mt_status) VALUES ('"
				+ msisdn + "','" + commandCode + "','1')";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				ps.execute();
				iReturn = 1;
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}
	
	
	public static int insertPromotionActiceAll(String msisdn, String commandCode) {
		int iReturn = 0;

		String funName = "insertPromotion ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "INSERT service_promotion_code(msisdn, command_code, mt_status, active) VALUES ('"
				+ msisdn + "','" + commandCode + "','1','1')";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				ps.execute();
				iReturn = 1;
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}
	
	public static int insertPromotionNoMT(String msisdn, String commandCode) {
		int iReturn = 0;

		String funName = "insertPromotion ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "INSERT service_promotion_code(msisdn, command_code, mt_status) VALUES ('"
				+ msisdn + "','" + commandCode + "','0')";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				ps.execute();
				iReturn = 1;
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}

	public static Vector<String> getPromotionCode(String msisdn,
			String commandCode) {
		Vector<String> vProCode = new Vector<String>();

		String funName = "getPromotionCode ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT *  FROM  service_promotion_code WHERE msisdn = '"
				+ msisdn + "' AND command_code = '" + commandCode + "'";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					vProCode.add(rs.getInt("promotion_code") + "");
				}

			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return vProCode;

	}
	
	public static Vector<String> getPromotionCodeSendMt(String msisdn,
			String commandCode, int size) {
		Vector<String> vProCode = new Vector<String>();

		String funName = "getPromotionCode ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT *  FROM  service_promotion_code WHERE msisdn = '"
				+ msisdn + "' AND command_code = '" + commandCode + "' order by active_time limit 0,2";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					vProCode.add(rs.getInt("promotion_code") + "");
				}

			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return vProCode;

	}

	public static int hasPromotionCodeNotActivated(String msisdn,
			String commandCode) {
		int iReturn = 0;

		String funName = "hasPromotionCodeNotActivated ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) AS num FROM  service_promotion_code WHERE msisdn = '"
				+ msisdn
				+ "' AND command_code = '"
				+ commandCode
				+ "' AND active = 0";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					iReturn = rs.getInt("num");
				}
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}

	public static int deletePromotionCode(String msisdn, String commandCode) {
		int iReturn = 0;

		String funName = "hasPromotionCodeNotActivated ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "DELETE  FROM service_promotion_code WHERE  msisdn = '"
				+ msisdn + "' AND command_code = '" + commandCode
				+ "' AND active = 0";
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				ps.execute();
				iReturn = 1;
				Util.logger.info(funName +" @sql:"+sql);
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}

	public static int isExistUser(String msisdn) {
		int iReturn = 0;

		String funName = "isExistUser ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) AS num FROM user_test where msisdn = "
				+ msisdn;
		DBPool pool = new DBPool();
		try {
			con = pool.getConnectionGateway();
			if (con != null) {
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					iReturn = rs.getInt("num");
				}
			} else {
				Util.logger.info(funName + " conntion is null");
			}
		} catch (Exception e) {
			Util.logger.error(funName + " @error:" + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(rs, ps);
			pool.cleanup(con);
		}
		return iReturn;
	}
}