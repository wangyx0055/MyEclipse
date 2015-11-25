package my.config;
import MyUtility.*;
public class MySetting
{
	public static String FolderConfigName = "/conf/WSNotifyConfig/";
	public static String PoolName_GateWay = "gateway";
	
	public static String GetLog4JConfigPath()
	{
		String Path = MyCurrent.GetCurrentPath()+FolderConfigName+"log4j.properties";
		
		return Path;
	}
	public static String GetProxoolConfigPath()
	{
		String Path = MyCurrent.GetCurrentPath()+FolderConfigName+"ProxoolConfig.xml";
		return Path;
	}
}
