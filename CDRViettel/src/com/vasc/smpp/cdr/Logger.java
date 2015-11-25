package com.vasc.smpp.cdr;

import java.io.*;
import java.util.*;
import com.vasc.smpp.cdr.DebugLogger;

/**
 * Logging class for the pools.
 */

public class Logger
{
	public Logger()
	{
	}

	// The log file to log to
	private static PrintWriter logWriter = null;
	private static boolean logVerbose = false;
	private static boolean logInfo = false;
	private static boolean logWarn = false;
	private static boolean logError = false;
	private static boolean logCrisis = false;
	// private String logLevel = "";
	private static int logDayOfMonth = -1;
	private static String origLogName = null;
	private static String emailEvents = null;
	private static String mxServer = null;
	private static String toAddress = null;
	private static String poolName = null;

	public static void setLogWriter(String log) throws IOException
	{
		if (origLogName == null)
			origLogName = log;

		Calendar cal = Calendar.getInstance();
		int localLogDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		boolean makeNewLog = false;
		if (logDayOfMonth == -1 && logWriter == null)
			makeNewLog = true; // indicates first start up
		if (origLogName.indexOf("${") > -1 && localLogDayOfMonth != logDayOfMonth)
		{
			String dateFormat = origLogName.substring(origLogName.indexOf("${") + 2, origLogName.indexOf("}"));
			String logPrefix = origLogName.substring(0, origLogName.indexOf("${"));
			String logSuffix = origLogName.substring(origLogName.indexOf("}") + 1, origLogName.length());
			java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat(dateFormat);
			java.util.Date tmp = new java.util.Date();
			String formattedDate = sdf2.format(tmp);
			log = (logPrefix + formattedDate + logSuffix);
			logDayOfMonth = localLogDayOfMonth;
			makeNewLog = true;
		}
		if (makeNewLog)
		{
			if (DebugLogger.getEnabled())
				DebugLogger.log("[Logger@" + poolName + "] Making new log (" + log + ")");
			if (logWriter != null)
				logWriter.close();
			logWriter = new PrintWriter(new FileOutputStream(log, true), true);
		}
	}

	public static void setEmailDetails(String emailEvents, String toAddress, String mxServer, String poolName)
	{
		/*
		 * if (emailEvents != null && emailEvents.length() > 0 &&
		 * !emailEvents.equals("null")) { this.emailEvents =
		 * emailEvents.toUpperCase(); this.emailEvents += ",CRISIS"; // Always
		 * add CRISIS event if want have email events this.toAddress =
		 * toAddress; this.mxServer = mxServer; this.poolName = poolName; }
		 */
	}

	public static void setLogLevel(String level)
	{
		if (level != null && level.length() > 0)
		{
			String[] levels = level.split(",");
			for (int i = 0; i < levels.length; i++)
			{
				if (levels[i].equalsIgnoreCase("verbose"))
				{
					logVerbose = true;
				}
				else if (levels[i].equalsIgnoreCase("info"))
				{
					logInfo = true;
				}
				else if (levels[i].equalsIgnoreCase("warn"))
				{
					logWarn = true;
				}
				else if (levels[i].equalsIgnoreCase("error"))
				{
					logError = true;
				}
				else if (levels[i].equalsIgnoreCase("crisis"))
				{
					logCrisis = true;
				}
				else if (levels[i].equalsIgnoreCase("debug"))
				{
					DebugLogger.setEnabled(true);
				}
			}
		}
	}

	/**
	 * Print a stack trace from an exception to the logs
	 */
	public static void printStackTrace(Throwable t)
	{
		if (logWriter != null)
		{
			t.printStackTrace(logWriter);
		}
		else
		{
			t.printStackTrace(System.err);
		}
		if (DebugLogger.getEnabled())
			DebugLogger.printStackTrace(t);
	}

	/**
	 * Close the logger's file handle.
	 */
	public static void close()
	{
		if (logWriter != null)
		{
			logWriter.close();
		}

	}

	/**
	 * Log verbose messages
	 */
	public static void verbose(String data)
	{
		if (logVerbose)
			log("VERBOSE", data);
		if (DebugLogger.getEnabled())
			DebugLogger.log(data);
	}

	/**
	 * Log an info message
	 */
	public static void info(byte[] data)
	{
		if (logInfo)
			log("INFO", new String(data));
	}

	/**
	 * Log an info message
	 */
	public static void info(String data)
	{

		if (logInfo)
			log("INFO", data);
		if (DebugLogger.getEnabled())
			DebugLogger.log(data);

	}

	/**
	 * Log an info message
	 */
	public static void info(String classname, String data)
	{
		data = classname + "@" + data;

		if (logInfo)
			log("INFO", data);
		if (DebugLogger.getEnabled())
			DebugLogger.log(data);

	}

	/**
	 * Log an warn message
	 */
	public static void warn(String data)
	{
		if (logWarn)
			log("WARN", data);
		if (DebugLogger.getEnabled())
			DebugLogger.log(data);

	}

	/**
	 * Log an error message
	 */
	public static void error(String data)
	{
		if (logError)
			log("ERROR", data);
		if (DebugLogger.getEnabled())
			DebugLogger.log(data);
	}

	/*
	 * public static void error(String classname, String data) { data =
	 * classname + "@" + data;
	 * 
	 * if (logError) log("ERROR", data); try { DBTools.Alert(classname, data,
	 * "major", data, "gatewayadmin"); // DBTools.Alert(classname + "{Channel="
	 * + Preference.Channel + "}" // + data); } catch (DBException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } if
	 * (DebugLogger.getEnabled()) DebugLogger.log(data);
	 * 
	 * }
	 */

	public static void email(String eventType, String message)
	{
		if (emailEvents == null || emailEvents.indexOf(eventType.toUpperCase()) == -1)
		{
			return;
		}
		/*
		 * if (DebugLogger.getEnabled()) DebugLogger.log("About to email event "
		 * + eventType + " :: " + message);
		 */
		String host = "unknown_host";
		try
		{
			host = java.net.InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e)
		{
			printStackTrace(e);
		}

		// String fromAddress = poolName +"@" +host;
		// String fromAddress = poolName +"_at_" +host +"@primrose.org.uk";
		String fromAddress = "pools@primrose.org.uk";
		String subject = eventType + " : " + poolName + "@" + host;
		info("Sending email for eventType(" + eventType + "), toAddress(" + toAddress + "), fromAddress(" + fromAddress + ") message(" + message + ")");

		try
		{
			// new SendMail(this, mxServer, toAddress, fromAddress, subject,
			// message).send();
		}
		catch (Exception e)
		{
			printStackTrace(e);
		}
	}

	/**
	 * Log an fatal message and email the world !
	 */
	/*
	 * public static void crisis(String message) { if (logCrisis) {
	 * log("CRISIS", message); try { DBTools.Alert("gateway", message,
	 * "serious", message, "gatewayadmin"); // DBTools.Alert(classname +
	 * "{Channel=" + Preference.Channel + // "}" // + data); } catch
	 * (DBException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } }
	 * 
	 * if (DebugLogger.getEnabled()) DebugLogger.log(message); if (emailEvents
	 * == null) return; email("CRISIS", message); }
	 * 
	 * public static void crisis(String classname, String message) { message =
	 * classname + "@" + message; if (logCrisis) { try { log("CRISIS", message);
	 * DBTools.Alert(classname, message, "major", message, "gatewayadmin");
	 * 
	 * } catch (DBException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 * if (DebugLogger.getEnabled()) DebugLogger.log(message); if (emailEvents
	 * == null) return; email("CRISIS", message); }
	 * 
	 * /** Log an info message
	 */
	private static void log(String level, String data)
	{
		try
		{
			setLogWriter(origLogName); // alter log name if we need to (if date
			// changes && they want date logging)
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		Calendar now = Calendar.getInstance();
		String nowString = now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.YEAR) + " "
				+ now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);

		String message = nowString + " : " + level + ": " + data;

		if (logWriter == null)
		{
			System.out.println(message);
		}
		else
		{
			logWriter.println(message);
		}
	}

	/**
	 * Print a blank new line / line break to the log
	 */
	public static void linebreak()
	{
		if (logWriter == null)
		{
			System.out.println("\n");
		}
		else
		{
			logWriter.println("\n");
		}
	}

	public String getLogLevel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getLogDayOfMonth()
	{
		return logDayOfMonth;
	}

	public void setLogDayOfMonth(int logDayOfMonth)
	{
		this.logDayOfMonth = logDayOfMonth;
	}

}
