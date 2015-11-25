package Config;
import MyConn.*;
public class MTrafficConfig
{
	public static void ShutdownProxool()
	{
		//MyConnection.Shutdown();
	}
	public static boolean IsDisableShutdownHook()
	{
		//return MyConnection.IsDisableShutdownHook();
		return true;
	}
	public static void CloseAllConnection() throws Exception
	{
		try
		{
			MyConnection.CloseAll();
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
}
