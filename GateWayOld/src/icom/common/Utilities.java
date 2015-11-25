package icom.common;

/**
 * <p>Title: SMPP Client</p>
 * Copyright (c) 2001 ICom
 * @author ICom
 * @version 1.0
 */

import icom.gateway.*;

import java.sql.*;
import java.io.*;
import java.util.*;
import javax.naming.*;
import java.math.BigDecimal;
import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat; // import
									// org.logicalcobwebs.proxool.configuration.PropertyConfigurator;
// import org.logicalcobwebs.proxool.ProxoolException;
import uk.org.primrose.vendor.standalone.PrimroseLoader;
import uk.org.primrose.GeneralException;


/**
 * This class contains some common methods usually used in your programs.
 */
public class Utilities {
	// *********************************//
	// CHANGE THESE PARAMETERS IF NEEDED//
	// *********************************//

	// The URL to the weblogicServer
	String url = "t3://localhost:80";
	String user = null;
	String password = null;
	static FileOutputStream fout = null;
	static FileInputStream fin = null;

	// static Preference pre = new Preference();
	/**
	 * You want to connect to weblogicServer to lookup any object via its JNDI
	 * name.
	 * 
	 * @return a Context to connect to WeblogicServer
	 */
	public Context getWebLogicContext() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,
				"weblogic.jndi.WLInitialContextFactory");
		p.put(Context.PROVIDER_URL, url);

		if (user != null) {
			p.put(Context.SECURITY_PRINCIPAL, user);
			if (password == null) {
				password = "";
			}
			p.put(Context.SECURITY_CREDENTIALS, password);
		}
		return new InitialContext(p);
	}

	public Connection getDBConnection() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@192.168.75.11:1521:ORA", "smpp",
					"smpp2003");
		} catch (Exception ex) {
			System.out.println("Utilities.getDBConnection:: " + ex.toString());
		}
		return conn;
	}

	public Connection getDBConnection(String driver, String url, String user,
			String password) throws SQLException {
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException ex) {
			throw new SQLException(ex.getMessage());
		}
		return conn;
	}

	public DataSource getDataSource(String strDataSourceName)
			throws NamingException {
		DataSource datasource = null;
		Context ic = this.getWebLogicContext();
		datasource = (DataSource) ic.lookup(strDataSourceName);
		return datasource;
	}

	/**
	 * This method will close the connection and statement if presented.
	 */
	public void cleanup(Connection con, PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
		}
	}

	public void cleanup(PreparedStatement ps, Statement stmt) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
		}
	}

	public void cleanup(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
		}
	}

	public void closeConnection(Connection connection, Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
		}
	}

	// Question: How can I convert any Java Object into byte array?
	// Answer : Very elegant way I found on SUN's web site:
	public static byte[] getBytes(Object obj) throws java.io.IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		bos.close();
		byte[] data = bos.toByteArray();
		return data;
	}

	public static byte[] readFile(String filename) {
		byte[] buffer = null;
		try {
			FileInputStream fin = new FileInputStream(filename);
			buffer = new byte[fin.available()];
			fin.read(buffer);
		} catch (IOException ex) {
			System.out.println("Error reading file :" + filename);
			System.exit(200);
		}
		return buffer;
	}

	public static void saveToFile(byte[] output, String filename) {
		try {
			File f = new File(filename);
			FileOutputStream out = new FileOutputStream(f);

			out.write(output);
			out.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// e.g: round(3.1537, 2) --> output is 3.15
	public static double round(double value, int decimalPlace) {
		double power_of_ten = 1;
		while (decimalPlace-- > 0) {
			power_of_ten *= 10.0;
		}
		return Math.round(value * power_of_ten) / power_of_ten;
	}

	public static double roundEx(double value, int decimalPlace) {
		BigDecimal bigValue = new BigDecimal(value);
		bigValue = bigValue.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bigValue.doubleValue();
	}

	private static void openLogFile() {
		try {

			// if (fout == null) {
			// System.currentTimeMillis();
			fout = new FileOutputStream("MO-Data.log", true); // append=true
			// }
		} catch (Exception ex) {
			System.out.println("Utilities.openLogFile: " + ex.getMessage());
		}
	}

	private static void openLogQueue() {
		try {
			fout = new FileOutputStream("SMPPQueueRX.log", false); // append=true
		} catch (Exception ex) {
			System.out.println("Utilities.openLogFile: " + ex.getMessage());
		}
	}

	public static void logMO(String s) {

		System.out.println(s);
		try {
			openLogFile();
			Timestamp time = new Timestamp(System.currentTimeMillis());
			fout
					.write(("[" + DateProc.getDateTime24hString(time) + "],"
							+ s + "\n").getBytes());
			fout.flush();
		} catch (Exception e) {
			System.out.println("Utilities.log: " + e.getMessage());
		}
	}

	public static void logQueue(String s) {

		if (Preference.ViewConsole == 1) {
			System.out.println(s);
		}

		try {
			openLogQueue();
			Timestamp time = new Timestamp(System.currentTimeMillis());
			fout
					.write(("[" + DateProc.getDateTime24hString(time) + "]\n"
							+ s + "\n").getBytes());
			System.out.println("[" + DateProc.getDateTime24hString(time)
					+ "]\n" + s + "\n");
			fout.flush();
		} catch (Exception e) {
			System.out.println("Utilities.log: " + e.getMessage());
		}
	}

	public static void logQueueBak(String s) {

		System.out.println(s);
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			java.util.Date date = new java.util.Date();
			String datetime = dateFormat.format(date);
			openLogFile("LogQueue-bak.log");
			fout.write((datetime + ":\n " + s + "\n").getBytes());
			fout.flush();
		} catch (Exception e) {
			System.out.println("Utilities.log: " + e.getMessage());
		}
	}

	public static void logMOBak(String s) {

		System.out.println(s);
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date = new java.util.Date();
			String datetime = dateFormat.format(date);
			openLogFile("MObak" + datetime + ".log");
			fout.write((s + "\n").getBytes());
			fout.flush();
		} catch (Exception e) {
			System.out.println("Utilities.log: " + e.getMessage());
		}
	}

	public static void logMOInvBak(String s) {

		System.out.println(s);
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date = new java.util.Date();
			String datetime = dateFormat.format(date);
			openLogFile("InvalidMO" + datetime + ".dat");
			fout.write((s + "\n").getBytes());
			fout.flush();
		} catch (Exception e) {
			System.out.println("Utilities.log: " + e.getMessage());
		}
	}

	public static void log(String ClassName, String s) {

		//if (Preference.ViewConsole == 1) {
		//	System.out.println(s);
		//}
		Logger.info(ClassName, s);
		/*
		 * if (Preference.WriteLog == 1) { try { DateFormat dateFormat = new
		 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); DateFormat dateFormatF =
		 * new SimpleDateFormat("yyyyMMdd"); java.util.Date date = new
		 * java.util.Date(); String datetime = dateFormat.format(date); String
		 * datetimeF = dateFormatF.format(date);
		 * openLogFile(Preference.LogFolder + "smpp-" + datetimeF + ".log");
		 * fout.write( ("[" + datetime + "]-{Channel=" + Preference.Channel + "
		 * }{" + ClassName + "}" + s + "\n"). getBytes()); fout.flush(); } catch
		 * (Exception e) { System.out.println("Utilities1.log: " +
		 * e.getMessage()); } }
		 */
	}

	public static void logConsole(String ClassName, String s) {

		System.out.println(s);
		Logger.info(ClassName, s);
		/*
		if (Preference.WriteLog == 1) {
			try {
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss.SSS");
				DateFormat dateFormatF = new SimpleDateFormat("yyyyMMdd");
				java.util.Date date = new java.util.Date();
				String datetime = dateFormat.format(date);
				String datetimeF = dateFormatF.format(date);
				openLogFile(Preference.LogFolder + "smpp-" + datetimeF + ".log");
				fout.write(("[" + datetime + "]-{Channel=" + Preference.Channel
						+ " }{" + ClassName + "}" + s + "\n").getBytes());
				fout.flush();
			} catch (Exception e) {
				System.out.println("Utilities1.log: " + e.getMessage());
			}
		}
		*/
	}

	public static void logErr(String ClassName, String s) {
		Logger.error(ClassName, s);

		// System.out.println(s);
		/*
		 * 
		 * try { DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd
		 * HH:mm:ss.SSS"); DateFormat dateFormatF = new
		 * SimpleDateFormat("yyyyMMdd"); java.util.Date date = new
		 * java.util.Date(); String datetime = dateFormat.format(date); String
		 * datetimeF = dateFormatF.format(date);
		 * openLogFile(Preference.LogFolder + "smpp-" + datetimeF + ".log");
		 * fout.write( ("[" + datetime + "]-{Channel=" + Preference.Channel + "
		 * }{" + ClassName + "}" + s + "\n"). getBytes()); fout.flush();
		 * com.vmg.smpp.gateway.DBTools.Alert2YM(ClassName + "{Channel=" +
		 * Preference.Channel + "}" + s); } catch (Exception e) {
		 * System.out.println("Utilities1.log: " + e.getMessage()); }
		 */
	}

	private static void openLogFile(String s) {
		try {
			// fout = null;
			// if (fout == null) {
			System.currentTimeMillis();
			fout = new FileOutputStream(s, true); // append=true
			// }
		} catch (Exception ex) {
			System.out.println("Utilities.openLogFile: " + ex.getMessage());
		}
	}

	public Collection getLogMO() {
		Vector keys = new Vector();
		try {
			BufferedReader in = new BufferedReader(
					new FileReader("MO-Data.log"));
			String str;
			// String[] mo;
			while ((str = in.readLine()) != null) {
				System.out.println(str);
				keys.addElement(str);
				logMOBak(str);
				// deleteLine();
			}
			in.close();
		}

		catch (Exception e) {
			System.out.println("Utilities.log: " + e.getMessage());
			return null;
		}
		DeleteFile("MO-Data.log");
		return keys;
	}

	public static void DeleteFile(String fileName) {
		// String fileName = "file.txt";
		// A File object to represent the filename
		File f = new File(fileName);

		// Make sure the file or directory exists and isn't write protected
		if (!f.exists()) {
			throw new IllegalArgumentException(
					"Delete: no such file or directory: " + fileName);
		}

		if (!f.canWrite()) {
			throw new IllegalArgumentException("Delete: write protected: "
					+ fileName);
		}

		// If it is a directory, make sure it is empty
		if (f.isDirectory()) {
			String[] files = f.list();
			if (files.length > 0) {
				throw new IllegalArgumentException(
						"Delete: directory not empty: " + fileName);
			}
		}

		// Attempt to delete it
		boolean success = f.delete();

		if (!success) {
			throw new IllegalArgumentException("Delete: deletion failed");
		}
	}

	public static void main(String args[]) {
		//Utilities util = new Utilities();
		//System.out.println(util.getDBConnection());
		
		 short PORT_VCARD = 0x23F4;
		 System.out.println("AA:" +"8079".substring(1, 2));
		 System.out.println("AA:" +  PORT_VCARD);
		 System.out.println("AA:" +  Short.parseShort( Short.toString(PORT_VCARD)));
		 
		 
		 
		 char[] chars = "cong hoa".toCharArray();
		 StringBuffer output = new StringBuffer();
		 for(int i = 0; i < chars.length; i++){
		   output.append(Integer.toHexString((int)chars[i]));
		 }
        
		 //byte[] aa = output.

		 //System.out.println("AA:" +HexaTool.);
	}

	/*
	 * public static void ConfigProxool() { try {
	 * PropertyConfigurator.configure("database.cfg"); } catch (ProxoolException
	 * ex) { log("Utilities", "{Config Database Proxool}{Error}" +
	 * ex.getMessage()); } }
	 */
	public static void ConfigPrirose() {

		Logger.info("ConfigPrirose", "ConfigPrirose.start");
		try {

			try {
				List a = PrimroseLoader.load("database.cfg", true);

			} catch (GeneralException ex) {
				log("Utilities", "ConfigPrimrose:" + ex.toString());
			}

		} catch (Exception exp) {
			log("Utilities", "{Utilities}{Config Database Primrose}{Error}"
					+ exp.getMessage());
		}
	}

}
