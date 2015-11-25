package com.vasc.smpp.cdr;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
 * @author Huynh Ngoc Tuan
 * @version 1.0
 */
import java.io.*;
import java.util.Properties;

public class FtpData
{

	static String FTP_SERVER = ""; // "ftp://abc.xyz.com/"; // port=21 (default)
	static String FTP_USER = "maivq";
	static String FTP_PASSWORD = "cuchuoi";
	static String IS_8X = "N";
	static String FTP_PATH = "/";
	static String MOBILE_OPERATOR = "VMS";
	static String LOCAL_FOLDER = "./CDROUT";
	static String SENT_FOLDER = "./CDRSENT";
	static String SENT_FOLDER_VMS = "./CDRSENT";
	static String FILE_EXTENSION = ".bil";

	static int SCHEDULE_TIME = 10; // minutes

	private static Properties properties = new Properties();

	/**
	 * Loads configuration parameters from the file with the given name. Sets
	 * private variable to the loaded values.
	 */
	public static void loadProperties(String fileName) throws IOException
	{
		System.out.println("Reading configuration file " + fileName + "...");
		FileInputStream propsFile = new FileInputStream(fileName);
		properties.load(propsFile);
		propsFile.close();
		System.out.println("Setting default parameters...");

		FTP_SERVER = properties.getProperty("FTP_SERVER");
		FTP_USER = properties.getProperty("FTP_USER");
		FTP_PASSWORD = properties.getProperty("FTP_PASSWORD");
		FTP_PATH = properties.getProperty("FTP_PATH");
		LOCAL_FOLDER = properties.getProperty("LOCAL_FOLDER");
		SENT_FOLDER = properties.getProperty("SENT_FOLDER");
		FILE_EXTENSION = properties.getProperty("FILE_EXTENSION");
		IS_8X = properties.getProperty("IS_8X", IS_8X);
		MOBILE_OPERATOR = properties.getProperty("MOBILE_OPERATOR", MOBILE_OPERATOR);
		int time = getIntProperty("SCHEDULE_TIME", SCHEDULE_TIME);
		// if (time < 1) time = 1;
		// if (time > 20) time = 20;
		SCHEDULE_TIME = time;
	}

	// ========================================================================//
	// ============================ PRIVATE METHODS
	// ===========================//
	// ========================================================================//
	// Gets a property and converts it into byte.
	static byte getByteProperty(String propName, byte defaultValue)
	{
		return Byte.parseByte(properties.getProperty(propName, Byte.toString(defaultValue)).trim());
	}

	// Gets a property and converts it into integer.
	static int getIntProperty(String propName, int defaultValue)
	{
		return Integer.parseInt(properties.getProperty(propName, Integer.toString(defaultValue)).trim());
	}

	// Test
	public static void main(String args[])
	{
		try
		{
			FtpData.loadProperties("ftp2cdrserver.cfg");
			System.out.println(FtpData.LOCAL_FOLDER);
			System.out.println(FtpData.FTP_SERVER);
			System.out.println(FtpData.SCHEDULE_TIME);
		}
		catch (IOException ex)
		{
			System.out.println(ex.getMessage());
		}
	}
}
