package com.goldsword;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.goldsword.alao.soap.sync.server.GoldSwordService_ServiceLocator;


public class Main
{
	public static void main(String[] args)
	{
		GoldSwordService_ServiceLocator lo = new GoldSwordService_ServiceLocator();
		lo.setGoldSwordServiceEndpointAddress("http://123.30.235.137:9080/services/GoldSwordService");
		try
		{
			System.out.println(lo.getGoldSwordService().syncCharge("", "", "", "", "",
			        "", "", "", ""));
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}
}
