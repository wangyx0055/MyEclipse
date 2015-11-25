package cdr;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import MyUtility.MyLogger;

public class LocalConfig
{
	public static MyLogger mLog = new MyLogger(LocalConfig.class.toString());
	public static Properties _prop;

	public static String PoolName_Data_SQL = "HBCharging_Local";
	public static String PoolName_Data_MySQL = "gateway";

	static String FTP_SERVER = "ftp://10.50.9.248"; // "ftp://abc.xyz.com/"; //
													// port=21 (default)
	static String FTP_USER = "wapportalhb";
	static String FTP_PASSWORD = "wphb13313";

	static String FTP_PATH = "/";
	static String LOCAL_FOLDER = "./CDROUT";
	static String SENT_FOLDER = "./CDRSENT";
	static String SENT_FOLDER_TELCO = "./CDRSENT";
	static String FILE_EXTENSION = ".txt";

	public static String SHORT_CODE = "040000";

	// Thời gian delay cho mỗi lần chạy (tính bằng phút)
	static int SCHEDULE_TIME = 30;

	/**
	 * Khoảng thời gian cho phép đẩy fle CDR sang Telco
	 */
	static String INTERVAL_PUSH_CDR = "02:00|05:00";
	static String INTERVAL_WRITE_CDR = "06:00|23:59";

	static int START_PUSH_CDR_HOUR = 5;
	static int START_PUSH_CDR_MINUTE = 00;

	static int STOP_PUSH_CDR_HOUR = 5;
	static int STOP_PUSH_CDR_MINUTE = 30;

	static int START_WRITE_CDR_HOUR = 5;
	static int START_WRITE_CDR_MINUTE = 00;

	static int STOP_WRITE_CDR_HOUR = 5;
	static int STOP_WRITE_CDR_MINUTE = 30;

	// File lưu trữ tên file cdr cuối cũng được tạo
	static String FILE_STORE_LAST_CDR_FILENAME = "./lastcdrfileCDR.dat";

	public static String CP_NAME = "HBCOM";
	public static int ROWCOUNT = 10;

	public static boolean loadProperties(String propFile)
	{
		Properties properties = new Properties();
		mLog.log.debug("Reading configuration file " + propFile);
		try
		{
			FileInputStream fin = new FileInputStream(propFile);
			properties.load(fin);
			_prop = properties;
			fin.close();
			INTERVAL_PUSH_CDR = properties.getProperty("INTERVAL_PUSH_CDR", "01:00|05:00");
			INTERVAL_WRITE_CDR = properties.getProperty("INTERVAL_WRITE_CDR", "06:30|23:59");

			CP_NAME = properties.getProperty("CP_NAME", "HBCOM");
			FTP_SERVER = properties.getProperty("FTP_SERVER");
			FTP_USER = properties.getProperty("FTP_USER");
			FTP_PASSWORD = properties.getProperty("FTP_PASSWORD");
			FTP_PATH = properties.getProperty("FTP_PATH");
			LOCAL_FOLDER = properties.getProperty("LOCAL_FOLDER");
			SENT_FOLDER = properties.getProperty("SENT_FOLDER");
			SENT_FOLDER_TELCO = properties.getProperty("SENT_FOLDER_TELCO");

			FILE_EXTENSION = properties.getProperty("FILE_EXTENSION", ".txt");
			ROWCOUNT = Integer.parseInt(properties.getProperty("ROWCOUNT", "10"));
			SCHEDULE_TIME = Integer.parseInt(properties.getProperty("SCHEDULE_TIME", "10"));

			PoolName_Data_SQL = properties.getProperty("PoolName_Data_SQL", "HBCharging_Local");
			PoolName_Data_SQL = properties.getProperty("PoolName_Data_MySQL", "gateway");

			// Lấy thông tin giờ, phút cho việc Push CDR sang VNP
			if (!INTERVAL_PUSH_CDR.equals(""))
			{
				String[] Arr = INTERVAL_PUSH_CDR.split("\\|");
				if (Arr.length == 2)
				{
					String[] Arr_Start = Arr[0].split(":");
					String[] Arr_Stop = Arr[1].split(":");

					if (Arr_Start.length == 2)
					{
						START_PUSH_CDR_HOUR = Integer.parseInt(Arr_Start[0]);
						START_PUSH_CDR_MINUTE = Integer.parseInt(Arr_Start[1]);
					}
					
					if (Arr_Stop.length == 2)
					{
						STOP_PUSH_CDR_HOUR = Integer.parseInt(Arr_Stop[0]);
						STOP_PUSH_CDR_MINUTE = Integer.parseInt(Arr_Stop[1]);
					}
				}
			}

			if (!INTERVAL_WRITE_CDR.equals(""))
			{
				String[] Arr = INTERVAL_WRITE_CDR.split("\\|");
				if (Arr.length == 2)
				{
					String[] Arr_Start = Arr[0].split(":");
					String[] Arr_Stop = Arr[1].split(":");

					if (Arr_Start.length == 2)
					{
						START_WRITE_CDR_HOUR = Integer.parseInt(Arr_Start[0]);
						START_WRITE_CDR_MINUTE = Integer.parseInt(Arr_Start[1]);
					}
					
					if (Arr_Stop.length == 2)
					{
						STOP_WRITE_CDR_HOUR = Integer.parseInt(Arr_Stop[0]);
						STOP_WRITE_CDR_MINUTE = Integer.parseInt(Arr_Stop[1]);
					}
				}
			}
			

			return true;

		}
		catch (Exception e)
		{
			mLog.log.error(e);
			return false;
		}

	}

	public synchronized static void setNewFilenameVMS(String name) throws IOException
	{
		DataOutputStream fout = new DataOutputStream(new FileOutputStream(FILE_STORE_LAST_CDR_FILENAME, false)); // append
																													// =
																													// false
		fout.writeBytes(name);
		fout.flush();
		fout.close();
	}

}
