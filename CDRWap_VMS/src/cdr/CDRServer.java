package cdr;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import MyUtility.MyLogger;

public class CDRServer extends Thread
{
	public static boolean IsRunning = true;
	public static MyLogger mLog = new MyLogger(CDRServer.class.toString());
	static BufferedReader Keyboard = new BufferedReader(new InputStreamReader(System.in));
	public CDRServer()
	{
		
		try
		{
			LocalConfig.loadProperties("FTPConfig.properties");

		}
		catch (Exception ex)
		{
			mLog.log.error("CDRServer: khong tim thay file cau hinh",ex);
		}		
		
	}

	public static void main(String args[])
	{
		new CDRServer().start();
		new DBScanner().start();
	}

	public void run()
	{
		// while (running) {
		// this.showMenu();
		// }
	}

	private void ShowMenu()
	{
		String option = "";
		try
		{
			// Blocked until a line input
			option = Keyboard.readLine();
			if ("Q".equals(option.toUpperCase()))
			{
				this.exit();
			}
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	private void exit()
	{
		IsRunning = false;
		System.out.println("Stop.");
		System.exit(0);
	}
}