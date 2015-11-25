package com.vasc.smpp.gateway;

/*
 /**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: IT R&D - VMG</p>
 * @author Vu Quang Mai
 * @version 1.0
 */

import java.io.*;
import java.util.*;

import java.sql.Timestamp;
import java.math.BigDecimal;

//import com.logica.smpp.*;
//import com.logica.smpp.pdu.*;

import com.vasc.common.*;

//import com.vasc.sms.process.*;

/**
 * Class <code>Gateway</code> shows how to use the SMPP toolkit. You can bound
 * and unbind from the SMSC, you can send every possible pdu and wait for a pdu
 * sent from the SMSC.
 * 
 */

public class GatewayCDR
{
	static final String copyright = "Copyright © 2012 by Chilinh";
	static final String version = "Billing System, version 1.0\n";
	static
	{
		System.out.println();
		System.out.println(copyright);
		System.out.println(version);
	}

	// File with default settings for the application.
	static String propsFilePath = "gateway.cfg";
	static long startup_time = System.currentTimeMillis();
	// Time when gateway is bound or unbound
	static long bound_or_unbound_time = 0;
	// Thoi gian nhan pdu lan cuoi cung
	// Ap dung khi ket noi o mode SYN,
	// voi dg knoi nhan ve
	static long last_received_time = 0;

	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

	// ========================================================================//
	// This is the SMPP session used for communication with SMSC.

	// If the application is bound to the SMSC.
	static boolean bound = false;
	static boolean boundr = false;

	public static boolean running = true;

	public static boolean isRunning()
	{
		return (running);
	}

	// ========================================================================//
	// 3 queues containing PDUs from SMSC
	// map (String) msgId -&- (BigDecimal) id of send_log
	// msgId is key
	static MsgQueue dbPool = null; // database pools
	public static Utilities util = null;

	/**
	 * Initialises the application, lods default values for connection to SMSC
	 * and for various PDU fields.
	 */
	public GatewayCDR()
	{
		dbPool = new MsgQueue();
		util = new Utilities();
	}

	/**
	 * Prompts the user to enter a string value for a parameter.
	 */
	private String getParam(String prompt, String defaultValue)
	{
		String value = "";
		String promptFull = prompt;
		promptFull += defaultValue == null ? "" : " [" + defaultValue + "] ";
		System.out.print(promptFull);
		try
		{
			value = keyboard.readLine();
		}
		catch (IOException e)
		{
			System.out.println("Got exception getting a param. " + e);
		}
		if (value.compareTo("") == 0)
		{
			return defaultValue;
		}
		else
		{
			return value;
		}
	}

	/*
	 * public static void addMoreConnection2Pool(int number) {
	 * GatewayCDR.util.log("Connecting to database....."); for (int i=0; i<
	 * number; i++) { java.sql.Connection conn = null; try { conn =
	 * util.getDBConnection(Preference.db_DriverClassName, Preference.db_URL,
	 * Preference.db_user, Preference.db_password); } catch
	 * (java.sql.SQLException ex) { GatewayCDR.util.log("Error: " +
	 * ex.getMessage());
	 * GatewayCDR.util.log("Khong noi dc voi database roi, xem lai di.!!!!!!!!!!!"
	 * ); System.exit(1); } if (conn != null) { dbPool.add(conn); } } }
	 */
	public static void addMoreConnection2Pool(int number)
	{
		GatewayCDR.util.log("Connecting to database.....");
		for (int i = 0; i < number; i++)
		{
			java.sql.Connection conn = null;
			try
			{
				conn = util.getDBConnectionMySQL(Preference.db_DriverClassName, Preference.db_server, Preference.db_name, Preference.db_user,
						Preference.db_password, Preference.db_port);
			}
			catch (java.sql.SQLException ex)
			{
				GatewayCDR.util.log("Error: " + ex.getMessage());
				GatewayCDR.util.log("Khong noi dc voi database roi, xem lai di.!!!!!!!!!!!");
				System.exit(1);
			}
			if (conn != null)
			{
				dbPool.add(conn);
				System.out.print("connect to database successful !" + "\n");
			}
		}
	}

	// This method is used for reconnect to database automatically
	/*
	 * public static void rebuildDBConnections(int number) {
	 * closeAllConnectionInPool();
	 * 
	 * boolean ok = false; java.sql.Connection conn = null; while(!ok) { try {
	 * conn = util.getDBConnection(Preference.db_DriverClassName,
	 * Preference.db_URL, Preference.db_user, Preference.db_password); ok =
	 * true; } catch (java.sql.SQLException e) {
	 * util.log("Get DB Connection FAILT. Try later in " +
	 * (Preference.timeRebind/1000) + " seconds"); try {
	 * Thread.sleep(Preference.timeRebind); } catch (InterruptedException ie) {}
	 * } } if (conn != null) { dbPool.add(conn); } addMoreConnection2Pool(number
	 * - 1); }
	 */
	public static void rebuildDBConnections(int number)
	{
		closeAllConnectionInPool();

		boolean ok = false;
		java.sql.Connection conn = null;
		while (!ok)
		{
			try
			{
				conn = util.getDBConnectionMySQL(Preference.db_DriverClassName, Preference.db_server, Preference.db_name, Preference.db_user,
						Preference.db_password, Preference.db_port);
				ok = true;
			}
			catch (java.sql.SQLException e)
			{
				util.log("Get DB Connection FAILT. Try later in " + (Preference.timeRebind / 1000) + " seconds");
				try
				{
					Thread.sleep(Preference.timeRebind);
				}
				catch (InterruptedException ie)
				{
				}
			}
		}
		if (conn != null)
		{
			dbPool.add(conn);
		}
		addMoreConnection2Pool(number - 1);
	}

	public static void closeAllConnectionInPool()
	{
		GatewayCDR.util.log("Closing database connection...");
		int size = (int) dbPool.getSize();
		for (int i = 0; i < size; i++)
		{
			java.sql.Connection conn = (java.sql.Connection) dbPool.remove();
			try
			{
				if (conn != null)
					conn.close();
			}
			catch (java.sql.SQLException ex)
			{
			}
		}
	}
}
