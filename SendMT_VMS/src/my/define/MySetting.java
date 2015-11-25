package my.define;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import MyUtility.MyCurrent;
import MyUtility.MyLogger;

public class MySetting
{
	/**
	 * Cho biết chương trình chạy hay không
	 */
	public static boolean AllowRunning = false;

	public static String FolderConfigName = "/Config/";
	public static String PoolName_GateWay = "gateway";

	public static String GetLog4JConfigPath()
	{
		String Path = MyCurrent.GetCurrentPath() + FolderConfigName + "log4j.properties";
		return Path;
	}
	public static String GetProxoolConfigPath()
	{
		String Path = MyCurrent.GetCurrentPath() + FolderConfigName + "ProxoolConfig.xml";
		return Path;
	}
	public static String GetConfigPath()
	{
		String Path = MyCurrent.GetCurrentPath() + FolderConfigName + "config.properties";
		return Path;
	}

	
	public static MyLogger mLog = new MyLogger(GetLog4JConfigPath(), MySetting.class.toString());
	
	
	/**
	 * Số lượng thread load MT lên để bắn sang VMS.
	 */
	public static Integer LoadMT_Thread_Number = 1;
	
	public static Integer SendMT_Thread_Number = 1;
	
	public static Integer SaveMTLog_Thread_Number = 1;
	
	public static Integer SaveCDRQueue_Thread_Number = 1;

	/**
	 * Max số lần retry khi gửi MT sang VMS không thành công
	 */
	public static Integer MaxRetry_SendMT = 1;

	/**
	 * Các file lưu các queue khi chương trình bị dừng đột ngột
	 */
	public static String MTQueue_Path_Save = "";
	public static String MTLogQueue_Path_Save = "";
	public static String CDRQueue_Path_Save = "";

	/**
	 * File lưu các MT không gửi sang được VMS khi vượt quá số lần retry.
	 */
	public static String MTNotSend_Path_Save = "";

	/**
	 * File lưu các MT không insert vào table MT log được
	 */
	public static String MTNotSaveToLog_Path_Save = "";

	public static String MTNotSaveToCDRQueue_Path_Save = "";

	// Thong tin config tu VMS
	public static String spId = "";
	public static String spPassword = "";
	public static String LinkSendSMS = "";
	public static String LinkSendWappush = "";

	public static void LoadConfig()
	{
		try
		{
			Properties properties = new Properties();
			FileInputStream fin = new FileInputStream(GetConfigPath());
			properties.load(fin);
			fin.close();
			
			PoolName_GateWay =properties.getProperty("PoolName_GateWay", "gateway"); 
			
			//Get config cua VMS
			spId =properties.getProperty("spId", ""); 
			spPassword =properties.getProperty("spPassword", ""); 
			LinkSendSMS =properties.getProperty("LinkSendSMS", "");
			LinkSendWappush =properties.getProperty("LinkSendWappush",""); 
			
			
			//Lay config cac file luu log, 
			MTQueue_Path_Save =properties.getProperty("MTQueue_Path_Save",""); 
			MTLogQueue_Path_Save =properties.getProperty("MTLogQueue_Path_Save",""); 
			CDRQueue_Path_Save =properties.getProperty("CDRQueue_Path_Save",""); 
			
			MTNotSend_Path_Save =properties.getProperty("MTNotSend_Path_Save",""); 
			MTNotSaveToLog_Path_Save =properties.getProperty("MTNotSaveToLog_Path_Save",""); 
			MTNotSaveToCDRQueue_Path_Save =properties.getProperty("MTNotSaveToCDRQueue_Path_Save",""); 
			
			
			LoadMT_Thread_Number = Integer.parseInt(properties.getProperty("LoadMT_Thread_Number", "1"));
			SendMT_Thread_Number = Integer.parseInt(properties.getProperty("SendMT_Thread_Number", "1"));
			SaveMTLog_Thread_Number = Integer.parseInt(properties.getProperty("SaveMTLog_Thread_Number", "1"));
			SaveCDRQueue_Thread_Number = Integer.parseInt(properties.getProperty("SaveCDRQueue_Thread_Number", "1"));

			MaxRetry_SendMT = Integer.parseInt(properties.getProperty("MaxRetry_SendMT", "1"));
			
		}
		catch (Exception ex)
		{
			
		}
	}
}
