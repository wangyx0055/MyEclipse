package com.vasc.smpp.gateway;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import uk.org.primrose.vendor.standalone.PrimroseLoader;
import java.util.List;
import uk.org.primrose.GeneralException;
import javax.naming.Context;
import javax.sql.DataSource;
import javax.naming.InitialContext;

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
public class DBPool
{

	public DBPool()
	{

	}

	public void cleanup12(Statement statement)
	{
		try
		{
			if (statement != null)
			{
				statement.close();
			}
		}
		catch (SQLException e)
		{
			// System.out.print(" Close statement:" + e.toString());
		}
		catch (Exception ex)
		{

		}

	}

	public void cleanup11(PreparedStatement statement)
	{
		try
		{
			if (statement != null)
			{
				statement.close();
			}
		}
		catch (SQLException e)
		{
			// /System.out.print(" Close statement:" + e.toString());
		}
		catch (Exception ex)
		{

		}

	}

	public void cleanup1(ResultSet rs, PreparedStatement pst)
	{
		try
		{
			if (rs != null)
			{
				rs.close();
			}
			if (pst != null)
			{
				pst.close();
			}
		}
		catch (SQLException e)
		{
			// System.out.println("Error: closeRS" + e.toString());
		}
		catch (Exception ex)
		{

		}

	}

	public void cleanup(ResultSet rs)
	{
		try
		{
			if (rs != null)
			{
				rs.close();
			}
		}
		catch (SQLException e)
		{
			// System.out.println("Error: closeRS" + e.toString());
		}
		catch (Exception ex)
		{

		}

	}

	public static void cleanup1(Connection con, PreparedStatement ps)
	{
		try
		{
			if (ps != null)
			{
				ps.close();
			}
			if (con != null)
			{
				con.close();
			}
		}
		catch (SQLException e)
		{
			// System.out.println("Error: closeRS" + e.toString());
		}

		catch (Exception e)
		{
		}
	}

	public static void cleanup(Connection con)
	{
		try
		{
			if (con != null)
			{
				con.close();
			}
		}
		catch (SQLException e)
		{
			// System.out.println("Error: closeRS" + e.toString());
		}

		catch (Exception e)
		{
		}
	}

	public Connection getConnectionGateway()
	{
		Connection conn = null;
		try
		{
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + "gateway");
			conn = ds.getConnection();

		}
		catch (SQLException e)
		{
			// Util.logger.crisis("getConnectionGateway Failed!" + e);
		}

		catch (Exception e)
		{
			// Util.logger.crisis("getConnectionGateway Failed!" + e);

		}
		return conn;
	}

	public Connection getConnectionSequence()
	{
		Connection conn = null;
		try
		{
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + "report");

			conn = ds.getConnection();

		}
		catch (SQLException e)
		{
			// / Util.logger.crisis("getConnectionSequence Failed!" + e);
		}

		catch (Exception e)
		{
			// Util.logger.crisis("getConnectionSequence Failed!" + e);
		}
		return conn;
	}

	public Connection getConnectionService()
	{
		Connection conn = null;
		try
		{
			// conn = DriverManager.getConnection("proxool.service");
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + "service");

			conn = ds.getConnection();

		}
		catch (SQLException e)
		{
			// Util.logger.crisis("getConnectionService Failed!" + e);
		}

		catch (Exception e)
		{
			// Util.logger.crisis("getConnectionService Failed!" + e);
		}
		return conn;
	}

	public Connection getConnectionAlert()
	{
		Connection conn = null;
		try
		{
			// conn = DriverManager.getConnection("proxool.invalid");
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + "alert");

			conn = ds.getConnection();

		}
		catch (SQLException e)
		{
			// Util.logger.crisis("getConnectionInvalid Failed!" + e);
		}

		catch (Exception e)
		{
			// Util.logger.crisis("getConnectionInvalid Failed!" + e);
		}
		return conn;
	}

	public static void ConfigDB()
	{

		try
		{

			try
			{
				List a = PrimroseLoader.load("database.config", true);

			}
			catch (GeneralException ex)
			{
				// Util.logger.crisis("ConfigPrirose Failed!" + ex.toString());
			}

		}
		catch (Exception exp)
		{
			// Util.logger.crisis("{Utilities}{Config Database Proxool}{Error}"
			// + exp.getMessage());
		}
	}

}
