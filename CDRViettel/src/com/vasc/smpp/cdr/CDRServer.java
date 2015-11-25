package com.vasc.smpp.cdr;

/**
 * <p>Title: IT R&D - VMG Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VMG</p>
 * @author Vu Quang Mai
 * @version 1.0
 */

import java.io.*;
import com.vasc.smpp.gateway.GatewayCDR;
import com.vasc.smpp.gateway.Preference;
import com.vasc.smpp.gateway.DBPool;

public class CDRServer extends Thread
{
	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

	static boolean running = true;
	static boolean writing = false;
	static GatewayCDR gateway = null;
	DBPool dbpool = new DBPool();

	public CDRServer()
	{

		gateway = new GatewayCDR();
		try
		{
			Preference.loadProperties("gateway.cfg");
			FtpData.loadProperties("ftp2cdrserver.cfg");
		}
		catch (IOException e)
		{
			// System.out.println("CDRServer: khong tim thay file cau hinh ");
			Logger.info("CDRServer:", "khong tim thay file cau hinh");
		}
		// gateway.addMoreConnection2Pool(1);
		dbpool.ConfigDB();

		try
		{
			Logger.setLogWriter("log/cdr${yyyy-MM-dd}.log");
		}
		catch (IOException ex)
		{
		}
		Logger.setLogLevel("info,warn,error,crisis");

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

	private void showMenu()
	{
		// System.out.println("Q - quit");
		String option = "";
		try
		{
			// Blocked until a line input
			option = keyboard.readLine();
			if ("Q".equals(option.toUpperCase()))
			{
				this.exit();
			}
		}
		catch (Exception e)
		{
			// System.out.println("CDRServer::" + e.getMessage());
			Logger.info("CDRServer:", e.getMessage());
		}
	}

	private static void exit()
	{
		running = false;
		gateway.closeAllConnectionInPool();
		System.out.println("Stop.");
		System.exit(0);
	}
}
