package com.vasc.common;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: IT R&D - VMG</p>
 * @author Vu Quang Mai
 * @version 1.0
 */

import java.sql.*;
import java.io.*;
import java.util.*;
import javax.naming.*;
import java.math.BigDecimal;
import javax.sql.DataSource;
import com.vasc.smpp.gateway.Preference;

/**
 * This class contains some common methods usually used in your programs.
 */
public class Utilities
{
	// *********************************//
	// CHANGE THESE PARAMETERS IF NEEDED//
	// *********************************//

	// The URL to the weblogicServer
	String url = "t3://localhost:80";
	String user = null;
	String password = null;

	static FileOutputStream fout = null;

	// A kind of trigger constant, used to display debug info.
	// Set this constant to the value of <code> true </code> for debugging
	static final boolean VERBOSE = true;

	// CONSTRUCTOR
	public Utilities()
	{
	}

	/**
	 * You want to connect to weblogicServer to lookup any object via its JNDI
	 * name.
	 * 
	 * @return a Context to connect to WeblogicServer
	 */
	public Context getWebLogicContext() throws NamingException
	{
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		p.put(Context.PROVIDER_URL, url);

		if (user != null)
		{
			p.put(Context.SECURITY_PRINCIPAL, user);
			if (password == null)
			{
				password = "";
			}
			p.put(Context.SECURITY_CREDENTIALS, password);
		}
		return new InitialContext(p);
	}

	public Connection getDBConnection()
	{
		Connection conn = null;
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@10.4.5.100:1521:ORA", "smpp", "smpp2004");
		}
		catch (Exception ex)
		{
			System.out.println("Utilities.getDBConnection:: " + ex.toString());
		}
		return conn;
	}

	public Connection getDBConnection(String driver, String url, String user, String password) throws SQLException
	{
		Connection conn = null;
		try
		{
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
		}
		catch (ClassNotFoundException ex)
		{
			throw new SQLException(ex.getMessage());
		}
		return conn;
	}

	public Connection getDBConnectionAlert(String driver, String server, String database, String user, String password, String port) throws SQLException
	{
		Connection conn = null;
		String url = "jdbc:mysql://" + server + ":" + port + "/" + database;
		// String driver = "com.mysql.jdbc.Driver";
		try
		{
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, user, password);
		}
		catch (Exception e)
		{
			System.out.print(e);
			conn = null;
		}

		return conn;
	}

	public Connection getDBConnectionMySQL(String driver, String server, String database, String user, String password, String port) throws SQLException
	{
		Connection conn = null;
		String url = "jdbc:mysql://" + server + ":" + port + "/" + database;
		// String driver = "com.mysql.jdbc.Driver";
		try
		{
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, user, password);
		}
		catch (Exception e)
		{
			System.out.print(e);
			// e.printStackTrace();
			conn = null;
		}

		return conn;
	}

	public DataSource getDataSource(String strDataSourceName) throws NamingException
	{
		DataSource datasource = null;
		Context ic = this.getWebLogicContext();
		datasource = (DataSource) ic.lookup(strDataSourceName);
		return datasource;
	}

	/**
	 * The method is used for displaying debug info dynamically, instead of the
	 * <code> System.out.println()</code> method. You can turn on and off the
	 * debug info by setting the constant <code> VERBOSE </code> to true and
	 * false respectively.
	 */
	public static void log(String s)
	{
		if (VERBOSE)
		{
			System.out.println(s);
			if (Preference.logToFile == 1)
			{
				try
				{
					openLogFile();
					Timestamp time = new Timestamp(System.currentTimeMillis());
					fout.write(("[" + DateProc.getDateTime24hString(time) + "] " + s + "\n").getBytes());
					fout.flush();
				}
				catch (Exception e)
				{
					System.out.println("Utilities.log: " + e.getMessage());
				}
			}
		}
	}

	private static void openLogFile()
	{
		try
		{
			if (fout == null)
			{
				fout = new FileOutputStream(Preference.fileToLog, true); // append=true
			}
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("Utilities.openLogFile: " + ex.getMessage());
		}
	}

	/**
	 * This method will close the connection and statement if presented.
	 */
	public void cleanup(Connection con, PreparedStatement ps)
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
		catch (Exception e)
		{
		}
	}

	public void cleanup(PreparedStatement ps, Statement stmt)
	{
		try
		{
			if (ps != null)
			{
				ps.close();
			}
			if (stmt != null)
			{
				stmt.close();
			}
		}
		catch (Exception e)
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
		catch (Exception e)
		{
		}
	}

	public void closeConnection(Connection connection, Statement statement)
	{
		try
		{
			if (statement != null)
			{
				statement.close();
			}
			if (connection != null)
			{
				connection.close();
			}
		}
		catch (SQLException e)
		{
		}
	}

	// Question: How can I convert any Java Object into byte array?
	// Answer : Very elegant way I found on SUN's web site:
	public static byte[] getBytes(Object obj) throws java.io.IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		bos.close();
		byte[] data = bos.toByteArray();
		return data;
	}

	// ////////////////////////////////////////////////////////////////////
	// HEX Conversion
	// You can display in hex using code like this:
	// String hex = Integer.toString(i , 16 /* radix */ );
	// That won't apply any lead zeroes.
	// Here is how to get a lead 0 for a fixed two character hex representation
	// of a byte: convert a byte b to 2-char hex string with possible leading
	// zero.
	// String s2 = Integer.toString( ( b & 0xff ) + 0x100, 16 /* radix */ )
	// .substring( 1 );
	// You can convert a hex String to internal binary like this:
	// int i = Integer.parseInt(g .trim(), 16 /* radix */ );

	// ////////////////////////////////////////////////////////////////////
	// Fast convert a byte array to a hex string
	// with possible leading zero.
	public static String toHexString(byte[] b)
	{
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++)
		{
			// look up high nibble char
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			// look up low nibble char
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	// table to convert a nibble to a hex char.
	static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Convert a hex string to a byte array. Permits upper or lower case hex.
	 * 
	 * @param s
	 *            String must have even number of characters. and be formed only
	 *            of digits 0-9 A-F or a-f. No spaces, minus or plus signs.
	 * @return corresponding byte array.
	 */
	public static byte[] fromHexString(String s)
	{
		int stringLength = s.length();
		if ((stringLength & 0x1) != 0)
		{
			throw new IllegalArgumentException("fromHexString requires an even number of hex characters");
		}
		byte[] b = new byte[stringLength / 2];

		for (int i = 0, j = 0; i < stringLength; i += 2, j++)
		{
			int high = charToNibble(s.charAt(i));
			int low = charToNibble(s.charAt(i + 1));
			b[j] = (byte) ((high << 4) | low);
		}
		return b;
	}

	/**
	 * convert a single char to corresponding nibble.
	 * 
	 * @param c
	 *            char to convert. must be 0-9 a-f A-F, no spaces, plus or minus
	 *            signs.
	 * 
	 * @return corresponding integer
	 */
	private static int charToNibble(char c)
	{
		if ('0' <= c && c <= '9')
		{
			return c - '0';
		}
		else if ('a' <= c && c <= 'f')
		{
			return c - 'a' + 0xa;
		}
		else if ('A' <= c && c <= 'F')
		{
			return c - 'A' + 0xa;
		}
		else
		{
			throw new IllegalArgumentException("Invalid hex character: " + c);
		}
	}

	// e.g: round(3.1537, 2) --> output is 3.15
	public static double round(double value, int decimalPlace)
	{
		double power_of_ten = 1;
		while (decimalPlace-- > 0)
		{
			power_of_ten *= 10.0;
		}
		return Math.round(value * power_of_ten) / power_of_ten;
	}

	public static double roundEx(double value, int decimalPlace)
	{
		BigDecimal bigValue = new BigDecimal(value);
		bigValue = bigValue.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bigValue.doubleValue();
	}

	public static void main(String args[])
	{
		Utilities util = new Utilities();
		try
		{
			System.out.println(util.getDBConnection("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@10.4.5.100:1521:ORA", "smpp", "smpp2004"));
		}
		catch (SQLException ex)
		{
		}
	}
}
