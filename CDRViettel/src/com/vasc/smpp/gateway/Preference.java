package com.vasc.smpp.gateway;

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

public class Preference
{
	// Address of the SMSC.
	public static String ipAddress = null;

	// The port number to bind to on the SMSC server.
	public static int port = 0;

	// The username which identifies you to SMSC.
	public static String systemId = null;

	// The password for authentication to SMSC.
	public static String password = null;

	/**
	 * How to bind to the SMSC: transmitter (t), receiver (r) or transciever
	 * (tr). Transciever can both send messages and receive messages. Note, that
	 * if you bind as receiver you can still receive responses to you requests
	 * (submissions).
	 */
	public static String bindMode = "tr"; // bind-mode

	/**
	 * Indicates that the Session has to be asynchronous. Asynchronous Session
	 * means that when submitting a Request to the SMSC the Session does not
	 * wait for a response. Instead the Session is provided with an instance of
	 * implementation of ServerPDUListener from the smpp library which receives
	 * all PDUs received from the SMSC. It's application responsibility to match
	 * the received Response with sended Requests.
	 */
	public static boolean asynchronous = false; // sync_mode

	// For more information about the underlying variables have a look in the
	// SMPP 3.4 specification, part 5.x.x
	public static Collection sourceAddressList = null;

	public static String[] validServiceIds = { "996", "997", "998", "19001255", "19001522", "19001799", "84996", "84997", "84998", "8419001255", "8419001522",
			"8419001799", "04996", "04997", "04998", "0419001255", "0419001522", "0419001799" };

	public static boolean isValidServiceId(String serviceId)
	{
		if (serviceId == null)
			return false;
		if (serviceId.startsWith("+"))
		{
			serviceId = serviceId.substring(1);
		}
		for (int i = 0; i < validServiceIds.length; i++)
		{
			if (validServiceIds[i].equals(serviceId))
			{
				return true;
			}
		}
		return false;
	}

	public static String systemType = "";
	public static String serviceType = "";

	public static String scheduleDeliveryTime = "";
	public static String validityPeriod = "";
	// /////////////////////////////////////
	public static String shortMessage = "";
	// /////////////////////////////////////
	public static String messageId = "";
	public static byte esmClass = 0;
	public static byte protocolId = 0;
	public static byte priorityFlag = 0;
	public static byte registeredDelivery = 0; // Delivery Receipt/report
												// request

	public static byte replaceIfPresentFlag = 0; // This sm is to replace the
													// submitted sm (=0: no, =1:
													// replace, >1: NA)
	public static byte dataCoding = 0;

	public static byte smDefaultMsgId = 0;// SMSC index of a pre-defined
											// message( 0: reserved, 1-254:
											// Allowed values, 255: reserved)

	// If you attempt to receive message, how long will the application wait for
	// data.
	public static long receiveTimeout = 0; // receive_timeout

	private static byte src_addr_ton = 0;
	private static byte src_addr_npi = 0;

	private static byte dest_addr_ton = 0;
	private static byte dest_addr_npi = 0;

	// ========================================================================//
	// Database Parameters
	// ========================================================================//
	static String db_DriverClassName = "com.mysql.jdbc.Driver";
	static String db_name = "votting_sfone";
	static String db_server = "localhost";
	static String db_user = "root";
	static String db_password = "itrd";
	static String db_port = "3306";
	static int db_MaxConnections = 3;

	// ============================Alert======================================//
	static String db_DriverClassName_alert = "com.mysql.jdbc.Driver";
	static String db_name_alert = "votting_sfone";
	static String db_server_alert = "localhost";
	static String db_user_alert = "root";
	static String db_password_alert = "itrd";
	static String db_port_alert = "3306";
	static int db_MaxConnections_alert = 3;
	public static String alert_person = "Nguyen Chi Linh";
	public static String alert_mobile = "";
	// ========================================================================//
	public static int maxNumOfSend = 0;
	public static int logToFile = 0;
	public static String fileToLog = null;
	public static int timeRebind = 1000;
	public static int timeResend = 300000; // 300secs or 5mins
	public static int timeEnquireLink = 70000; // 70secs
	public static int maxSMPerSecond = 1;
	public static String mobileOperator = "";
	// Turn on/off report ability
	public static boolean reportRequired = false;

	public static boolean cdrEnabled = false;
	public static String cdroutFolder = "./CDROUT";
	public static String cdrsentFolder = "./CDRSENT";
	public static String cdrfileExtension = ".bil";

	public static String receiveLogFolder = "./LOG-R";
	public static String sendLogFolder = "./LOG-S";

	public static Map prefixMap = new Hashtable(); // source_address(String) ::
													// prefix (Collection)
	public static Map messageMap = new Hashtable(); // source_address(String) ::
													// message(String)

	/**
	 * Contains the parameters and default values for this gateway such as
	 * system id, password, default npi, and ton of sender...
	 */
	private static Properties properties = new Properties();

	/**
	 * Loads configuration parameters from the file with the given name. Sets
	 * private variable to the loaded values.
	 */
	public static void loadProperties(String fileName) throws IOException
	{
		System.out.println("Reading configuration file " + fileName + "...");
		FileInputStream propsFile = new FileInputStream(fileName);
		properties.load(propsFile);
		propsFile.close();

		System.out.println("Setting default parameters...");
		ipAddress = properties.getProperty("ip_address");
		port = getIntProperty("port", port);
		systemId = properties.getProperty("system_id"); // username
		password = properties.getProperty("password");

		// The address range this smpp gateway will serve

		// The source address for this gateway
		src_addr_ton = getByteProperty("source_addr_ton", src_addr_ton);
		src_addr_npi = getByteProperty("source_addr_npi", src_addr_ton);
		sourceAddressList = parseString(properties.getProperty("source_addr", ""));
		if (sourceAddressList != null && sourceAddressList.size() > 0)
		{
			System.out.println("This gateway will process: " + sourceAddressList);
		}
		else
		{
			System.out.println("Warning: No source address is specified!");
		}

		serviceType = properties.getProperty("service_type", serviceType);
		systemType = properties.getProperty("system_type", systemType);

		// bind mode
		String strTemp = properties.getProperty("bind_mode", bindMode);
		if (!strTemp.equalsIgnoreCase("t") && !strTemp.equalsIgnoreCase("r") && !strTemp.equalsIgnoreCase("tr"))
		{
			System.out.println("Wrong value of bind_mode parameter in " + "the configuration file " + fileName + "--> Setting the default: t");
			strTemp = "t";
		}
		bindMode = strTemp;

		// Receiving mode
		String syncMode = properties.getProperty("sync_mode", (asynchronous ? "async" : "sync"));
		if (syncMode.equalsIgnoreCase("sync"))
		{
			asynchronous = false;
		}
		else if (syncMode.equalsIgnoreCase("async"))
		{
			asynchronous = true;
		}
		else
		{
			asynchronous = false;
		}
		// receive timeout in the cfg file is in seconds, we need milliseconds
		// also conversion from -1 which indicates infinite blocking
		// in the cfg file to Data.RECEIVE_BLOCKING which indicates infinite
		// blocking in the library is needed.
		int rcvTimeout = 0;
		rcvTimeout = getIntProperty("receive_timeout", rcvTimeout);

		// ========================================================================//
		// Database Parameters
		// ========================================================================//
		db_DriverClassName = properties.getProperty("db_driver", db_DriverClassName);
		db_name = properties.getProperty("db_name", db_name);
		db_server = properties.getProperty("db_server", db_server);
		db_user = properties.getProperty("db_user", db_user);
		db_password = properties.getProperty("db_password", db_password);
		db_port = properties.getProperty("db_port", db_port);
		db_MaxConnections = getIntProperty("db_max_connections", db_MaxConnections);

		db_DriverClassName_alert = properties.getProperty("db_driver_alert", db_DriverClassName_alert);
		db_name_alert = properties.getProperty("db_name_alert", db_name_alert);
		db_server_alert = properties.getProperty("db_server_alert", db_server_alert);
		db_user_alert = properties.getProperty("db_user_alert", db_user_alert);
		db_password_alert = properties.getProperty("db_password_alert", db_password_alert);
		db_port_alert = properties.getProperty("db_port_alert", db_port_alert);
		db_MaxConnections_alert = getIntProperty("db_max_connections_alert", db_MaxConnections_alert);
		alert_person = properties.getProperty("alert_person", alert_person);
		alert_mobile = properties.getProperty("alert_mobile", alert_mobile);

		// ========================================================================//
		maxNumOfSend = getIntProperty("max_num_of_send", maxNumOfSend);
		logToFile = getIntProperty("log_to_file", logToFile);
		fileToLog = properties.getProperty("file_to_log", fileToLog);

		timeRebind = getIntProperty("time_rebind", timeRebind / 1000) * 1000;
		timeResend = getIntProperty("time_resend", timeResend / 1000) * 1000;
		timeEnquireLink = getIntProperty("time_enquire_link", timeEnquireLink / 1000) * 1000;

		maxSMPerSecond = getIntProperty("max_sm_per_sec", maxSMPerSecond);
		mobileOperator = properties.getProperty("mobile_operator", mobileOperator).toUpperCase();

		String sTemp = properties.getProperty("report_required", "0");
		if ("1".equals(sTemp))
			reportRequired = true;

		sTemp = properties.getProperty("cdr_enabled", "0");
		if ("1".equals(sTemp))
			cdrEnabled = true;
		cdroutFolder = properties.getProperty("cdrout_folder", cdroutFolder);
		cdrsentFolder = properties.getProperty("cdrsent_folder", cdrsentFolder);
		cdrfileExtension = properties.getProperty("cdrfile_extension", cdrfileExtension);
		receiveLogFolder = properties.getProperty("receive-log-folder", receiveLogFolder);
		sendLogFolder = properties.getProperty("send-log-folder", sendLogFolder);

		// Prefixes and message for each source_address
		if (sourceAddressList != null && sourceAddressList.size() > 0)
		{
			for (Iterator it = sourceAddressList.iterator(); it.hasNext();)
			{
				String sNumber = (String) it.next();
				Collection cPrefixes = parseString(properties.getProperty("prefix_" + sNumber));
				if (cPrefixes != null && cPrefixes.size() > 0)
				{
					prefixMap.put(sNumber, cPrefixes);
				}
				else
				{
					System.out.println("Warning: No prefix is specified for " + sNumber);
				}

				String sMessage = properties.getProperty("message_" + sNumber);
				if (sMessage != null)
				{
					messageMap.put(sNumber, sMessage);
				}
				else
				{
					System.out.println("Warning: No message is specified for " + sNumber);
				}
			}
		}
	}

	// ==========================================================================
	public static boolean isValidServiceNumber(String serviceNum)
	{
		if (serviceNum == null)
			return false;
		if (serviceNum.startsWith("+"))
		{
			serviceNum = serviceNum.substring(1);
		}
		Iterator it = sourceAddressList.iterator();
		while (it.hasNext())
		{
			if (((String) it.next()).equals(serviceNum))
				return true;
		}
		return false;
	}

	// ========================================================================//
	// ============================ PRIVATE METHODS
	// ===========================//
	// ========================================================================//

	// Gets a property and converts it into byte.
	static byte getByteProperty(String propName, byte defaultValue)
	{
		return Byte.parseByte(properties.getProperty(propName, Byte.toString(defaultValue)).trim());
	}

	// Gets a property and converts it into integer.
	static int getIntProperty(String propName, int defaultValue)
	{
		return Integer.parseInt(properties.getProperty(propName, Integer.toString(defaultValue)).trim());
	}

	/*
	 * In text: a string seperate by comma (,) Out a collection of elements
	 * without (between) comma (,)
	 */
	static Vector parseString(String text)
	{
		Vector prefixes = new Vector();
		if (text == null || "".equals(text))
			return prefixes;

		String tempStr = text.toUpperCase();
		String currentLabel = null;

		int index = tempStr.indexOf(",");
		while (index != -1)
		{
			currentLabel = tempStr.substring(0, index).trim();
			if (!"".equals(currentLabel))
				prefixes.addElement(currentLabel);
			tempStr = tempStr.substring(index + 1);
			index = tempStr.indexOf(",");
		}
		// Last label
		currentLabel = tempStr.trim();
		if (!"".equals(currentLabel))
			prefixes.addElement(currentLabel);
		return prefixes;
	}

	public static void main(String args[])
	{
		Preference pre = new Preference();
		try
		{
			pre.loadProperties("gateway.cfg");
			// System.out.println( pre.smPrefixFromSMSC.size() );
			// java.util.Iterator it = null;
			// for (it = pre.smPrefixFromSMSC.iterator(); it.hasNext(); ) {
			// String s = (String) it.next();
			// System.out.println(s);
			// }

			// System.out.println(pre.cdroutFolder);
			// System.out.println(pre.cdrsentFolder);
			// System.out.println(pre.cdrfileExtension);
			// System.out.println(pre.cdrEnabled);
			//
			// System.out.println("Contain 997 = " +
			// Preference.sourceAddressList.contains("997"));

		}
		catch (IOException ex)
		{
		}

	}
}
