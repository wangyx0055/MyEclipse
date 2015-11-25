package MyCallWebservice;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.goldsword.alao.soap.sync.server.GoldSwordService_ServiceLocator;


import MyUtility.MyLogger;

public class GoldSword
{

	public static String CallWebservice(String User_ID, String Service_ID, String Command_Code, String Info,
			String Request_ID, String Receive_Date, String Operator, String UserName, String Password)
	{
		MyLogger mLog = new MyLogger(SetRingback.class.getName());

		String Result = "-1";
		try
		{
			GoldSwordService_ServiceLocator lo = new GoldSwordService_ServiceLocator();
			lo.setGoldSwordServiceEndpointAddress("http://123.30.235.137:9080/services/GoldSwordService");

			Result = lo.getGoldSwordService().syncCharge(User_ID, Service_ID, Command_Code, Info, Request_ID,
					Receive_Date, Operator, UserName, Password);
		}
		catch (RemoteException ex)
		{
			mLog.log.error(ex);
			Result = "-1";
		}
		catch (ServiceException ex)
		{
			mLog.log.error(ex);
			Result = "-1";
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
			Result = "-1";
		}
		return Result;
	}

	public static String getCharacterDataFromElement(Element e)
	{
		Node child = e.getFirstChild();
		if (child instanceof CharacterData)
		{
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}
}
