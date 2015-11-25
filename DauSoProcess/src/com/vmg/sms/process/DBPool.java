package com.vmg.sms.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import uk.org.primrose.GeneralException;
import uk.org.primrose.vendor.standalone.PrimroseLoader;

import com.vmg.sms.common.Util;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class DBPool {

	public DBPool() {

	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
 */
	// ///////////////////////////////////////////////////////////////
	public void cleanup(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			Util.logger.error(" Close statement:" + e.toString());
		} catch (Exception ex) {
			Util.logger.error(" Close statement:" + ex.toString());

		}

	}
	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////

	public void cleanup(PreparedStatement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			// /System.out.print(" Close statement:" + e.toString());
			Util.logger.error(" Close PreparedStatement:" + e.toString());
		} catch (Exception ex) {
			Util.logger.error(" Close PreparedStatement:" + ex.toString());

		}

	}
	
	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public void cleanup(ResultSet rs, PreparedStatement pst) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pst != null) {
				pst.close();
			}
		} catch (SQLException e) {
			// System.out.println("Error: closeRS" + e.toString());
			Util.logger.error("cleanup ResultSet,PreparedStatement"
					+ e.toString());
		} catch (Exception e) {
			Util.logger.error("cleanup ResultSet,PreparedStatement"
					+ e.toString());

		}

	}
	// ////////////////////////////////////////////////////////////.//
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////

	public void cleanup(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			Util.logger.error("cleanup ResultSet" + e.toString());
		} catch (Exception e) {
			Util.logger.error("cleanup ResultSet" + e.toString());

		}

	}
	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public void cleanup(Connection con, PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			// System.out.println("Error: closeRS" + e.toString());
			Util.logger.error("cleanup Connection,PreparedStatement"
					+ e.toString());
		}

		catch (Exception e) {
			Util.logger.error("cleanup Connection,PreparedStatement"
					+ e.toString());
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public void cleanup(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			Util.logger.error("cleanup Connection" + e.toString());
		}

		catch (Exception e) {
			Util.logger.error("cleanup Connection,PreparedStatement"
					+ e.toString());
		}
	}
	// ///////////////////////////////////////////////////////////////
	/**
	 * get Connection Gateway
	 * 
	 * @return Connection
	 */
	// ///////////////////////////////////////////////////////////////

	public Connection getConnectionGateway() {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/"
					+ "gateway");
			conn = ds.getConnection();

		} catch (SQLException e) {
			Util.logger.crisis("getConnectionGateway Failed!" + e);
		}

		catch (Exception e) {
			Util.logger.crisis("getConnectionGateway Failed!" + e);

		}
		return conn;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * get Connection alert
	 * 
	 * @return Connection
	 */
	// ///////////////////////////////////////////////////////////////

	public Connection getConnectionAlert() {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + "alert");

			conn = ds.getConnection();

		} catch (SQLException e) {
			Util.logger.crisis("getConnectionAlert Failed!" + e);
		}

		catch (Exception e) {
			Util.logger.crisis("getConnectionAlert Failed!" + e);
		}
		return conn;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * get Connection 
	 * @param dbname
	 * @return Connection
	 */
	// ///////////////////////////////////////////////////////////////

	public Connection getConnection(String dbname) {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + dbname);

			conn = ds.getConnection();

		} catch (SQLException e) {
			Util.logger.crisis("getConnection Failed!" + dbname + ":" + e);
		}

		catch (Exception e) {
			Util.logger.crisis("getConnection Failed!" + dbname + ":" + e);
		}
		return conn;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * get Connection CMSDB
	 * 
	 * @return Connection
	 */
	// ///////////////////////////////////////////////////////////////
	public Connection getConnectionCMS() {
		Connection conn = null;
		try {

			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + "cmsdb");

			conn = ds.getConnection();

		} catch (SQLException e) {
			Util.logger.crisis("getConnectionCMS Failed!" + e);
		}

		catch (Exception e) {
			Util.logger.crisis("getConnectionCMS Failed!" + e);
		}
		return conn;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Setup DB config
	 * 
	 * @param Load config from file config.cfg
	 */
	// ///////////////////////////////////////////////////////////////
	public static void ConfigDB() {

		try {

			try {
				List a = PrimroseLoader.load("config.cfg", true);
				Util.logger.info("ConfigPrirose - Pool:" + a);

			} catch (GeneralException ex) {
				Util.logger.crisis("ConfigPrirose Failed!" + ex.toString());
			}

		} catch (Exception exp) {
			Util.logger.crisis("{Utilities}{Config Database Proxool}{Error}"
					+ exp.getMessage());
		}
	}

}
