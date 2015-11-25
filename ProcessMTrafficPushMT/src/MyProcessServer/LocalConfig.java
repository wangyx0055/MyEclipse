package MyProcessServer;

import java.io.FileInputStream;
import java.util.Properties;

import MyUtility.MyLogger;

public class LocalConfig
{
	public static MyLogger mLog = new MyLogger(LocalConfig.class.toString());

	public static String PoolName_Data_SQL = "VOVTraffic";
	public static String PoolName_Data_MySQL = "Gateway";
	public static String LogDataFolder = ".\\Data\\";

	public static String Result_NodeName_VNP = "messageId";

	public static Integer MaxCountClose = 500;

	public static Integer StreetServiceID = 0;
	/**
	 * Đầu số cho dịch vụ
	 */
	public static String SHORT_CODE = "1546";

	public static Integer MAX_PID = 50;
	
	/**
	 * Số lượng Thread được tạo để push MT
	 */
	public static Integer PUSH_MT_PROCESS_NUMBER = 2;

	/**
	 * File để lưu trữ thông tin khi đang push mà bị stop đột ngột VD: process
	 * đang push cho dịch vụ Giao thông, và đang push cho thuê bao có OrderID
	 * =100 thì bị dừng lại. Sau khi start lại chương trình, thì sẽ dựa vào
	 * thông tin này để push tiếp
	 */
	public static String PUSH_MT_STOP_PATH = ".\\Data\\PUSHMT.dat";

	/**
	 * Số lượng MSISDn được lến lên đề push cho mỗi lần query xuống SQL server
	 */
	public static Integer PUSH_MT_ROWCOUNT = 10;

	/**
	 * Giá trị nhỏ nhất PID của table Subscrber (PID theo số điện thoại)
	 */
	public static Integer PUSH_MT_PID_MIN = 0;

	/**
	 * Giá trị lớn nhất PID của table Subscrber (PID theo số điện thoại)
	 */
	public static Integer PUSH_MT_PID_MAX = 20;

	/**
	 * Chứa các thông tin về đội tường PushMT nhưng không thành công, Các đối
	 * tượng này sẽ được Push lại bằng 1 process khác
	 */
	public static String PUSH_MT_FAIL_PATH = ".\\Data\\PushFail.dat";

	/**
	 * KHoảng thòi gian để kiểm tra thời gian pust tin VD: thời gian push tin là
	 * 6h15 thì kiểm tra trong khoảng 6h10 và 6h20.
	 */
	public static Integer CHECK_PUSH_TIME_INTERVAL = 5;

	/**
	 * Khoảng thời gian chạy cho mỗi lần kiểm tra thời gian push tin,được tính
	 * bằng phút
	 */
	public static Integer CHECK_PUSH_TIME_DELAY = 2;

	/**
	 * Số lần retry cho 1 bản tin push fial (Tính theo phút)
	 */
	public static Integer RETRY_PUSH_MT_FAIL_MAX_COUNT = 5;

	/**
	 * Khoảng thời gian cho phép được Push lại MT tình từ lúc add to Queue (tính
	 * theo phút)
	 */
	public static Integer RETRY_PUSH_MT_FAIL_TIME_INTERVAL = 60;

	/**
	 * Thời gian delay cho mỗi lần chạy Retry Push mt (tính theo phút)
	 */
	public static Integer RETRY_PUSH_MT_FAIL_TIME_DELAY = 1;

	public static Integer RETRY_PUSH_MT_FAIL_ALLOW_PUSHING = 1;

	/**
	 * Số lần retry chạy thread stop (Tính theo phút)
	 */
	public static Integer RETRY_THREAD_STOP_MAX_COUNT = 5;

	/**
	 * Khoảng thời gian cho phép được thread stop tình từ lúc add to Queue (tính
	 * theo phút)
	 */
	public static Integer RETRY_THREAD_STOP_TIME_INTERVAL = 60;

	/**
	 * Thời gian delay cho mỗi lần chạy Retry thread stop (tính theo phút)
	 */
	public static Integer RETRY_THREAD_STOP_TIME_DELAY = 1;

	public static Integer RETRY_THREAD_STOP_ALLOW_PUSHING = 1;

	public static boolean LoadProperties(String propFile)
	{
		Properties properties = new Properties();
		mLog.log.debug("Reading configuration file " + propFile);
		try
		{
			FileInputStream fin = new FileInputStream(propFile);
			properties.load(fin);
			fin.close();
			MaxCountClose = Integer.parseInt(properties.getProperty("MaxCountClose", MaxCountClose.toString()));

			LogDataFolder = properties.getProperty("LogDataFolder", LogDataFolder);
			PoolName_Data_SQL = properties.getProperty("PoolName_Data_SQL", PoolName_Data_SQL);
			PoolName_Data_MySQL = properties.getProperty("PoolName_Data_MySQL", PoolName_Data_MySQL);
			Result_NodeName_VNP = properties.getProperty("Result_NodeName_VNP", Result_NodeName_VNP);

			SHORT_CODE = properties.getProperty("SHORT_CODE", SHORT_CODE);

			PUSH_MT_STOP_PATH = properties.getProperty("PUSH_MT_STOP_PATH", PUSH_MT_STOP_PATH);

			PUSH_MT_PROCESS_NUMBER = Integer.parseInt(properties.getProperty("PUSH_MT_PROCESS_NUMBER", PUSH_MT_PROCESS_NUMBER.toString()));

			PUSH_MT_ROWCOUNT = Integer.parseInt(properties.getProperty("PUSH_MT_ROWCOUNT", PUSH_MT_ROWCOUNT.toString()));

			PUSH_MT_PID_MIN = Integer.parseInt(properties.getProperty("PUSH_MT_PID_MIN", PUSH_MT_PID_MIN.toString()));
			PUSH_MT_PID_MAX = Integer.parseInt(properties.getProperty("PUSH_MT_PID_MAX", PUSH_MT_PID_MAX.toString()));

			PUSH_MT_FAIL_PATH = properties.getProperty("PUSH_MT_FAIL_PATH", PUSH_MT_FAIL_PATH);

			CHECK_PUSH_TIME_INTERVAL = Integer.parseInt(properties.getProperty("CHECK_PUSH_TIME_INTERVAL", CHECK_PUSH_TIME_INTERVAL.toString()));
			CHECK_PUSH_TIME_DELAY = Integer.parseInt(properties.getProperty("CHECK_PUSH_TIME_DELAY", CHECK_PUSH_TIME_DELAY.toString()));

			RETRY_PUSH_MT_FAIL_MAX_COUNT = Integer.parseInt(properties.getProperty("RETRY_PUSH_MT_FAIL_MAX_COUNT", RETRY_PUSH_MT_FAIL_MAX_COUNT.toString()));
			RETRY_PUSH_MT_FAIL_TIME_INTERVAL = Integer.parseInt(properties.getProperty("RETRY_PUSH_MT_FAIL_TIME_INTERVAL",
					RETRY_PUSH_MT_FAIL_TIME_INTERVAL.toString()));
			RETRY_PUSH_MT_FAIL_TIME_DELAY = Integer.parseInt(properties.getProperty("RETRY_PUSH_MT_FAIL_TIME_DELAY", RETRY_PUSH_MT_FAIL_TIME_DELAY.toString()));
			RETRY_PUSH_MT_FAIL_ALLOW_PUSHING = Integer.parseInt(properties.getProperty("RETRY_PUSH_MT_FAIL_ALLOW_PUSHING",
					RETRY_PUSH_MT_FAIL_ALLOW_PUSHING.toString()));

			RETRY_THREAD_STOP_MAX_COUNT = Integer.parseInt(properties.getProperty("RETRY_THREAD_STOP_MAX_COUNT", RETRY_THREAD_STOP_MAX_COUNT.toString()));
			RETRY_THREAD_STOP_TIME_INTERVAL = Integer.parseInt(properties.getProperty("RETRY_THREAD_STOP_TIME_INTERVAL",
					RETRY_THREAD_STOP_TIME_INTERVAL.toString()));
			RETRY_THREAD_STOP_TIME_DELAY = Integer.parseInt(properties.getProperty("RETRY_THREAD_STOP_TIME_DELAY", RETRY_THREAD_STOP_TIME_DELAY.toString()));
			RETRY_THREAD_STOP_ALLOW_PUSHING = Integer.parseInt(properties.getProperty("RETRY_THREAD_STOP_ALLOW_PUSHING",
					RETRY_THREAD_STOP_ALLOW_PUSHING.toString()));

			return true;
		}
		catch (Exception e)
		{
			mLog.log.error(e);
			return false;
		}

	}

}
