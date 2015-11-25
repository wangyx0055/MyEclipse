package icom.gateway;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */
import icom.gateway.ConfigGW;

import java.io.*;
import java.util.*;

import org.smpp.Data;
import org.smpp.pdu.Address;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.WrongLengthOfStringException;

public class Preference {
	// private static Logger logger = new Logger("Preference");
	// Lottery

	private static Hashtable preference = null;

	// DB CONFIG
	public static String[] LIST_DB_SEND_LOG = { "gateway" };
	public static String[] LIST_DB_CDR_QUEUE = { "gateway" };
	public static String[] LIST_DB_SEND_QUEUE = { "gateway" };
	public static String[] LIST_DB_ALERT = { "gateway" };
	public static String[] LIST_TABLE_SEND_QUEUE = { "0" };
	public static String[] LIST_DB_CONFIG = { "gateway" };
	public static String[] LIST_DB_RECEIVE_QUEUE = { "gateway" };
	
	public static String[] arrLIST_SERVICEID = null;
	public static String strLIST_SERVICEID = "";

	// ////////
	public static boolean bLottery = false;
	public static String PreRequestID = "0";
	public static int NumOfRetries = 1;
	public static int RetriesTime = 3;
	public static boolean bLog = false;

	public static int ALERT = 1;
	public static String ALERT_CONTACT = "SMS Gateway Admin";
	// if ((toSMSC.size() > 2) || (fromSMSC.size() > 2)
	// || (respondSMSC.size() > 2) || (EMSQueue.size() > 2)) {
	public static int ALERT_TOSMSC = 30;
	public static int ALERT_FROMSMSC = 30;
	public static int ALERT_RESPONSESMSC = 30;
	public static int ALERT_EMSQUEUE = 30;
	public static int ALERT_RESPONSETABLE = 30;

	// Thread MO-MT-RESPOND

	public static int nTheardMO = 1;
	public static int nTheardMT = 1;
	public static int nTheardResp = 1;
	public static int nTheardMOSim = 1;

	public static int nAddNewTheardMO = 0;
	public static int nAddNewTheardMT = 0;
	public static int nAddNewTheardResp = 0;
	public static int nAddNewBuildEMS = 0;
	public static int nAddNewSendLog = 0;
	public static int nAddNewMOSim = 0;

	public static int nBuildEMS = 1;
	public static int nGetMTfromDB = 1;
	public static int nSendLog = 1;
	public static int n = 1;

	public static int nLogQueue = 1;

	public static int WriteLog = 1;
	public static int ViewConsole = 1;

	// Address of the SMSC.
	public static String ipAddress = null;

	// The port number to bind to on the SMSC server.
	public static int port = 0;

	// The username which identifies you to SMSC.
	public static String systemId = null;

	// The password for authentication to SMSC.
	public static String password = null;

	// Ket qua
	public static String ketqua = null;
	public static String Channel = null;
	public static String mobileOperator = "";

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

	public static String[] validServiceIds = { "6083", "6183", "6283", "6383", "6483", "6583", "6683", "6783" };

	public static String[] VALID_SERVICEID = {"6083", "6183", "6283", "6383", "6483", "6583", "6683", "6783"};
	
	public static String[] SERVICEID_8x81 = { "8081", "8181", "8281", "8381",
			"8481", "8581", "8681", "8781" };
	public static String[] SERVICEID_8x79 = { "8079", "8179", "8279", "8379",
		"8479", "8579", "8679", "8779" };
	public static String[] SERVICEID_6x66 =  { "6066", "6166", "6266", "6366",
		"6466", "6566", "6666", "6766", "6866", "6966" };
	public static String[] SERVICEID_6x69 = { "6069", "6169", "6269", "6369",
			"6469", "6569", "6669", "6769", "6869", "6969" };

	public static String[] SERVICEID_8x42 = { "8042", "8142", "8242", "8342",
			"8442", "8542", "8642", "8742" };

	public static String[] SERVICEID_8x51 = { "8051", "8151", "8251", "8351",
			"8451", "8551", "8651", "8751" };
	public static String[] SERVICEID_9x99 = { "9099", "9199", "9299", "9399",
			"9499", "9599", "9699", "9799" };

	public static String[] SERVICEID_6x61 = { "6061", "6161", "6261", "6361",
			"6461", "6561", "6661", "6761" };
	public static String[] SERVICEID_6x57 = { "6057", "6157", "6257", "6357",
			"6457", "6557", "6657", "6757" };
	public static String[] SERVICEID_6x93 = { "6093", "6193", "6293", "6393",
			"6493", "6593", "6693", "6793" };
	public static String[] SERVICEID_6x34 = { "6034", "6134", "6234", "6334",
			"6434", "6534", "6634", "6734" };
	
	public static String[] SERVICEID_SPECIAL = {"1546"}; // 1368abce121211

	public static boolean isValidServiceId(String serviceId) {
		if (serviceId == null) {
			return false;
		}
		if (serviceId.startsWith("+")) {
			serviceId = serviceId.substring(1);
		}
		if (serviceId.length() > 2) {
			return true;
		}
		/*
		 * for(int i=0; i< validServiceIds.length; i++) { if
		 * (validServiceIds[i].equals(serviceId)) { return true; } }
		 */
		// 998xxx
		if (serviceId.startsWith("998") && serviceId.length() == 10) {
			return true;
		}
		return false;
	}

	public static boolean CheckServiceId(String serviceId) {
		try {
			serviceId = serviceId.trim();
			if (serviceId == null || "".equalsIgnoreCase(serviceId)) {
				return false;
			}

			for (int i = 0; i < VALID_SERVICEID.length; i++) {
				if (VALID_SERVICEID[i].endsWith(serviceId)) {
					return true;
				}
			}
			
			for(int i = 0; i<SERVICEID_SPECIAL.length; i++){
				
				if(serviceId.startsWith(SERVICEID_SPECIAL[i])){
					return true;
				}
				
			}

			return false;
			
		} catch (Exception e) {
			// TODO: handle exception
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

	public static byte smDefaultMsgId = 0; // SMSC index of a pre-defined
	// message( 0: reserved, 1-254:
	// Allowed values, 255: reserved)

	// The range of addresses the smpp session will serve.
	public static AddressRange addressRange = new AddressRange();

	// If you attempt to receive message, how long will the application wait for
	// data.
	public static long receiveTimeout = 0; // receive_timeout

	private static byte src_addr_ton = 0;
	private static byte src_addr_npi = 0;

	private static byte dest_addr_ton = 0;
	private static byte dest_addr_npi = 0;

	public static String PayLoadUDH = null;

	// ========================================================================//
	// Database Parameters
	// ========================================================================//
	static String db_DriverClassName = "oracle.jdbc.driver.OracleDriver";
	static String db_URL = "jdbc:oracle:oci8:@ORA";

	// static String db_URL = "jdbc:oracle:thin:@192.168.75.11:1521:ORA";
	static String db_user = "smpp";
	static String db_password = "smpp2003";
	static int db_MaxConnections = 3;

	// ========================================================================//
	// ========================================================================//
	// Database Service Parameters
	// ========================================================================//
	static String db_DriverClassName_Service = "oracle.jdbc.driver.OracleDriver";
	static String db_URL_Service = "jdbc:oracle:oci8:@ORA";
	static String db_user_Service = "smpp";
	static String db_password_Service = "smpp2003";
	static int db_MaxConnections_Service = 1;

	// ========================================================================//
	// Database Invalid Parameters
	// ========================================================================//
	static String db_DriverClassName_Inv = "oracle.jdbc.driver.OracleDriver";
	static String db_URL_Inv = "jdbc:oracle:oci8:@ORA";
	static String db_user_Inv = "smpp";
	static String db_password_Inv = "smpp2003";
	static int db_MaxConnections_Inv = 1;

	// ========================================================================//

	public static int maxNumOfSend = 0;
	public static int logToFile = 0;
	public static int logToConsole = 0;
	public static String logToFolder = ".\\LOG-G";
	public static int logFullMessage = 0;
	public static String MODatafile = "MOData.dat";

	public static int commandFromLocal = 0; // Process command from console
	public static int commandFromRemote = 0; // Process command from telnet
	public static int telnetServerPort = 7002;
	public static Collection telnetAllowedIp = null; // {"127.0.0.1",
	// "10.4.5.100",
	// "10.4.5.128"};

	public static int timeRebind = 1000;
	public static int timeResend = 3; // mins
	public static int timeOut = 2;
	public static int timeEnquireLink = 70000; // 70secs
	public static int maxSMPerSecond = 1;

	// Turn on/off report ability
	public static boolean reportRequired = false;

	public static boolean cdrEnabled = false;
	public static String cdroutFolder = ".\\CDROUT";
	public static String cdrsentFolder = ".\\CDRSENT";
	public static String cdrfileExtension = ".bil";
	public static String LogFolder = "./";

	public static String receiveLogFolder = ".\\LOG-R";
	public static String sendLogFolder = ".\\LOG-S";
	public static String VIETTEL_MODE = "tr";
	public static String SEND_MODE = "";
	public static String prefix_requestid = "20";

	// Cau hinh cho SFONE
	public static String MAPCP = "1";
	public static String REBUILD_USERID = "1";
	public static String REFUND_ACTIVE = "1";
	public static String SFONE_ACTIVE = "1";

	public static String CONSOLE_MODE = "1";
	// blacklist
	public static String BLACKLIST = "0";
	// public static Map prefixMap = new Hashtable(); // source_address(String)
	// ::
	// prefix (Collection)
	// public static Map messageMap = new Hashtable(); // source_address(String)
	// ::
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
	public static void loadProperties(String fileName) throws IOException {
		// System.out.println("Reading configuration file " + fileName + "...");
		Logger.info("Reading configuration file " + fileName + "...");
		FileInputStream propsFile = new FileInputStream(fileName);
		properties.load(propsFile);
		propsFile.close();

		try {
			String gateway_name = properties.getProperty("gateway_name", "");
			// if ("".equalsIgnoreCase(gateway_name)) {
			Logger.info("Reading configuration file");
			// } else {
			// Logger.info("retrieveConfigGW from DB :" + gateway_name);
			// preference = ConfigGW.retrieveConfigGW(gateway_name);
			// }

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Logger.info("retrieveConfigGW " + e1.toString() + "...");
			e1.printStackTrace();
		}

		// System.out.println("Setting default parameters...");
		// Alert va Log config

		Logger.info("Setting parameters...");
		ALERT = getIntProperty("ALERT", 1);
		ALERT_CONTACT = getStringProperty("ALERT_CONTACT", ALERT_CONTACT);

		ALERT_EMSQUEUE = getIntProperty("ALERT_EMSQUEUE", ALERT_EMSQUEUE);
		ALERT_FROMSMSC = getIntProperty("ALERT_FROMSMSC", ALERT_FROMSMSC);
		ALERT_TOSMSC = getIntProperty("ALERT_TOSMSC", ALERT_TOSMSC);
		ALERT_RESPONSESMSC = getIntProperty("ALERT_RESPONSESMSC",
				ALERT_RESPONSESMSC);

		byte ton;
		byte npi;
		String addr;

		// Thread MO- MT - Respond
		nTheardMO = getIntProperty("num_thread_mo", 1);
		nTheardMT = getIntProperty("num_thread_mt", 1);
		nTheardResp = getIntProperty("num_thread_respond", 1);
		nLogQueue = getIntProperty("log_queue", 1);
		WriteLog = getIntProperty("write_log", 1);
		ViewConsole = getIntProperty("view_console", 1);

		nAddNewTheardMO = getIntProperty("add_thread_mo", 0);
		nAddNewTheardMT = getIntProperty("add_thread_mt", 0);
		nAddNewTheardResp = getIntProperty("add_thread_respond", 0);
		nAddNewBuildEMS = getIntProperty("add_build_ems", 0);
		nAddNewSendLog = getIntProperty("add_thread_send_log", 0);
		nAddNewMOSim = getIntProperty("add_thread_mo_sim", 0);

		nTheardMOSim = getIntProperty("num_thread_mo_sim", 1);
		nBuildEMS = getIntProperty("num_build_ems", 1);
		nGetMTfromDB = getIntProperty("num_get_mt_from_db", 1);
		nSendLog = getIntProperty("num_thread_send_log", 1);

		ipAddress = getStringProperty("ip_address");
		port = getIntProperty("port", port);
		systemId = getStringProperty("system_id"); // username
		password = getStringProperty("password");
		ketqua = getStringProperty("ketqua");
		Channel = getStringProperty("channel");
		// The address range this smpp gateway will serve
		ton = getByteProperty("addr_ton", addressRange.getTon());
		npi = getByteProperty("addr_npi", addressRange.getNpi());
		addr = getStringProperty("addr_range", addressRange.getAddressRange());
		addressRange.setTon(ton);
		addressRange.setNpi(npi);
		try {
			addressRange.setAddressRange(addr);
		} catch (WrongLengthOfStringException e) {
			System.out
					.println("The length of address_range parameter is wrong.");
		}

		// The source address for this gateway
		src_addr_ton = getByteProperty("source_addr_ton", src_addr_ton);
		src_addr_npi = getByteProperty("source_addr_npi", src_addr_npi);

		sourceAddressList = parseString(properties.getProperty("source_addr",
				""));

		if (sourceAddressList != null && sourceAddressList.size() > 0) {
			Logger.info("This gateway will process: " + sourceAddressList);
		} else {
			Logger.info("Warning: No source address is specified!");
		}

		// The default destination address
		dest_addr_ton = getByteProperty("dest_addr_ton", dest_addr_ton);
		dest_addr_npi = getByteProperty("dest_addr_npi", dest_addr_npi);
		addr = getStringProperty("destination_addr", "");

		serviceType = getStringProperty("service_type", serviceType);
		systemType = getStringProperty("system_type", systemType);

		// bind mode
		String strTemp = getStringProperty("bind_mode", bindMode);
		if (!strTemp.equalsIgnoreCase("t") && !strTemp.equalsIgnoreCase("r")
				&& !strTemp.equalsIgnoreCase("tr")) {
			Logger.info("Wrong value of bind_mode parameter in "
					+ "the configuration file " + fileName
					+ "--> Setting the default: t");
			strTemp = "t";
		}
		bindMode = strTemp;

		// Tham so Lottery
		ketqua = null;
		bLottery = false;
		bLog = false;
		// Receiving mode
		String syncMode = getStringProperty("sync_mode",
				(asynchronous ? "async" : "sync"));
		if (syncMode.equalsIgnoreCase("sync")) {
			asynchronous = false;
		} else if (syncMode.equalsIgnoreCase("async")) {
			asynchronous = true;
		} else {
			asynchronous = false;
		}
		// receive timeout in the cfg file is in seconds, we need milliseconds
		// also conversion from -1 which indicates infinite blocking
		// in the cfg file to Data.RECEIVE_BLOCKING which indicates infinite
		// blocking in the library is needed.
		int rcvTimeout = 0;
		rcvTimeout = getIntProperty("receive_timeout", rcvTimeout);
		if (rcvTimeout == -1) {
			receiveTimeout = Data.RECEIVE_BLOCKING; // infinite blocking
		} else {
			receiveTimeout = rcvTimeout * 1000; // secs -> millisecs
		}
		PayLoadUDH = Byte.toString((byte) 0x06) + Byte.toString((byte) 0x05)
				+ Byte.toString((byte) 0x04) + Byte.toString((byte) 0x13)
				+ Byte.toString((byte) 0x88) + Byte.toString((byte) 0x00)
				+ Byte.toString((byte) 0x00);

		NumOfRetries = getIntProperty("num_retries", NumOfRetries);
		RetriesTime = getIntProperty("retries_time", RetriesTime);
		maxNumOfSend = getIntProperty("max_num_of_send", maxNumOfSend);
		logToFile = getIntProperty("log_to_file", logToFile);
		logToConsole = getIntProperty("log_to_console", logToConsole);
		logToFolder = getStringProperty("log_to_folder", logToFolder);
		MODatafile = getStringProperty("file_mo_data", MODatafile);
		logFullMessage = getIntProperty("log_full_message", logFullMessage);

		telnetServerPort = getIntProperty("telnet_server_port",
				telnetServerPort);
		telnetAllowedIp = parseIPaddresses(properties.getProperty(
				"telnet_allowed_ip", ""));

		timeRebind = getIntProperty("time_rebind", timeRebind / 1000) * 1000;
		timeResend = getIntProperty("time_resend", timeResend);
		timeOut = getIntProperty("time_out", timeResend);
		timeEnquireLink = getIntProperty("time_enquire_link",
				timeEnquireLink / 1000) * 1000;

		maxSMPerSecond = getIntProperty("max_sm_per_sec", maxSMPerSecond);
		mobileOperator = getStringProperty("mobile_operator", mobileOperator)
				.toUpperCase();

		String sTemp = getStringProperty("report_required", "0");
		if ("1".equals(sTemp)) {
			reportRequired = true;

		}
		sTemp = getStringProperty("cdr_enabled", "0");
		if ("1".equals(sTemp)) {
			cdrEnabled = true;
		}
		cdroutFolder = getStringProperty("cdrout_folder", cdroutFolder);
		cdrsentFolder = getStringProperty("cdrsent_folder", cdrsentFolder);
		LogFolder = getStringProperty("log_path", LogFolder);
		cdrfileExtension = getStringProperty("cdrfile_extension",
				cdrfileExtension);
		receiveLogFolder = getStringProperty("receive-log-folder",
				receiveLogFolder);
		sendLogFolder = getStringProperty("send-log-folder", sendLogFolder);
		// User for Viettel operator
		VIETTEL_MODE = getStringProperty("vt-mode", VIETTEL_MODE);
		SEND_MODE = getStringProperty("send_mode", SEND_MODE);
		prefix_requestid = getStringProperty("prefix_requestid",
				prefix_requestid);

		BLACKLIST = getStringProperty("BLACKLIST", BLACKLIST);

		// public static String[] LIST_DB_SEND_LOG = { "gateway" };
		// /public static String[] LIST_DB_CDR_QUEUE = { "gateway" };
		// public static String[] LIST_DB_SEND_QUEUE = { "gateway" };
		// public static String[] LIST_DB_ALERT = { "gateway" };
		// public static String[] LIST_TABLE_SEND_QUEUE = { "0" };
		// public static String[] LIST_DB_CONFIG = { "gateway" };
		// public static String[] LIST_DB_RECEIVE_QUEUE = { "gateway" };

		String sLIST_DB_SEND_LOG = properties.getProperty("LIST_DB_SEND_LOG",
				"gateway");
		LIST_DB_SEND_LOG = parseString(sLIST_DB_SEND_LOG, ",");

		String sLIST_DB_CDR_QUEUE = properties.getProperty("LIST_DB_CDR_QUEUE",
				"gateway");
		LIST_DB_CDR_QUEUE = parseString(sLIST_DB_CDR_QUEUE, ",");

		String sLIST_DB_SEND_QUEUE = properties.getProperty(
				"LIST_DB_SEND_QUEUE", "gateway");
		LIST_DB_SEND_QUEUE = parseString(sLIST_DB_SEND_QUEUE, ",");

		String sLIST_DB_ALERT = properties
				.getProperty("LIST_DB_ALERT", "alert");
		LIST_DB_ALERT = parseString(sLIST_DB_ALERT, ",");

		String sLIST_TABLE_SEND_QUEUE = properties.getProperty(
				"LIST_TABLE_SEND_QUEUE", "0");
		LIST_TABLE_SEND_QUEUE = parseString(sLIST_TABLE_SEND_QUEUE, ",");

		String sLIST_DB_CONFIG = properties.getProperty("LIST_DB_CONFIG",
				"gateway");
		LIST_DB_CONFIG = parseString(sLIST_DB_CONFIG, ",");

		String sLIST_DB_RECEIVE_QUEUE = properties.getProperty(
				"LIST_DB_RECEIVE_QUEUE", "gateway");
		LIST_DB_RECEIVE_QUEUE = parseString(sLIST_DB_RECEIVE_QUEUE, ",");
		
		
		String sList_service = "";
		sList_service = properties.getProperty("LIST_SERVICEID", sList_service);
		if ( "".equalsIgnoreCase(sList_service)) {
			arrLIST_SERVICEID = VALID_SERVICEID;
		}
		else  {
			arrLIST_SERVICEID = parseString(sList_service, ",");
				
		}
		strLIST_SERVICEID = "";
		for (int ii = 0; ii < arrLIST_SERVICEID.length; ii++) {
			strLIST_SERVICEID = strLIST_SERVICEID + ","
					+ "'" + arrLIST_SERVICEID[ii].trim() + "'";
		}
		strLIST_SERVICEID = strLIST_SERVICEID
				.substring(1);
		

		// Prefixes and message for each source_address
		/*
		 * if (sourceAddressList != null && sourceAddressList.size() > 0) { for
		 * (Iterator it = sourceAddressList.iterator(); it.hasNext();) { String
		 * sNumber = (String) it.next(); Collection cPrefixes =
		 * parseString(getStringProperty("prefix_" + sNumber)); if (cPrefixes !=
		 * null && cPrefixes.size() > 0) { prefixMap.put(sNumber, cPrefixes); }
		 * else { System.out.println("Warning: No prefix is specified for " +
		 * sNumber); } String sMessage = getStringProperty("message_" +
		 * sNumber); if (sMessage != null) { messageMap.put(sNumber, sMessage);
		 * } else { System.out.println("Warning: No message is specified for " +
		 * sNumber); } } }
		 */
	}

	// ==========================================================================
	public static Address buildSrcAddress(String srcAddr) {
		Address address = new Address();
		address.setTon(src_addr_ton);
		address.setNpi(src_addr_npi);
		try {
			// address.setAddress(srcAddr, Data.SM_ADDR_LEN); //Max addr length
			address.setAddress(srcAddr);
			return address;
		} catch (WrongLengthOfStringException e) {
			Logger.info("The length of dest_addr parameter is wrong: "
					+ srcAddr);
			return null;
		}
	}

	// ==========================================================================
	public static Address buildDestAddress(String destAddr) {
		Address address = new Address();
		address.setTon(dest_addr_ton);
		address.setNpi(dest_addr_npi);
		try {
			// address.setAddress(destAddr, Data.SM_ADDR_LEN); //Max addr length
			address.setAddress(destAddr);
			return address;
		} catch (WrongLengthOfStringException e) {
			Logger.info("The length of dest_addr parameter is wrong: "
					+ destAddr);
			return null;
		}
	}

	// ========================================================================//
	// ============================ PRIVATE METHODS
	// ===========================//
	// ========================================================================//

	// Gets a property and converts it into byte.
	static byte getByteProperty(String propName, byte defaultValue) {

		String temp = getConfigValue(propName);
		byte result = defaultValue;
		if (temp != null) {
			result = Byte.parseByte(temp);
			Logger.info("{DB}-" + propName + ":" + result);
		}

		else {

			result = Byte.parseByte(properties.getProperty(propName,
					Byte.toString(defaultValue)).trim());
			Logger.info("{FILE}-" + propName + ":" + result);
		}

		return result;
	}

	// Gets a property and converts it into integer.
	static int getIntProperty(String propName, int defaultValue) {
		String temp = getConfigValue(propName);
		int result = defaultValue;
		if (temp != null) {
			result = Integer.parseInt(temp);
			Logger.info("{DB}-" + propName + ":" + result);
		} else {
			result = Integer.parseInt(properties.getProperty(propName,
					Integer.toString(defaultValue)).trim());
			Logger.info("{FILE}-" + propName + ":" + result);
		}

		return result;
	}

	// Gets a property.
	static String getStringProperty(String propName) {
		String temp = getConfigValue(propName);
		String result = "";
		if (temp != null) {
			result = temp;
			Logger.info("{DB}-" + propName + ":" + result);
		} else {
			result = properties.getProperty(propName);
			Logger.info("{FILE}-" + propName + ":" + result);
		}

		return result;
	}

	static String getStringProperty(String propName, String defaultval) {
		String temp = getConfigValue(propName);
		String result = "";

		if (temp != null) {
			result = temp;
			Logger.info("{DB}-" + propName + ":" + result);
		} else {
			result = properties.getProperty(propName, defaultval);
			Logger.info("{FILE}-" + propName + ":" + result);
		}

		return result;
	}

	// Gets a property and converts it into integer.
	// static int getIntProperty(String propName, int defaultValue) {
	// return Integer.parseInt(properties.getProperty(propName,
	// Integer.toString(defaultValue)).trim());
	// }

	/*
	 * In text: a string seperate by comma (,) Out a collection of elements
	 * without (between) comma
	 */
	static Collection parseString(String text) {
		Vector prefixes = new Vector();
		if (text == null || "".equals(text)) {
			return prefixes;
		}

		String tempStr = text.toUpperCase();
		String currentLabel = null;

		int index = tempStr.indexOf(",");
		while (index != -1) {
			currentLabel = tempStr.substring(0, index).trim();
			if (!"".equals(currentLabel)) {
				prefixes.addElement(currentLabel);
			}
			tempStr = tempStr.substring(index + 1);
			index = tempStr.indexOf(",");
		}
		// Last label
		currentLabel = tempStr.trim();
		if (!"".equals(currentLabel)) {
			prefixes.addElement(currentLabel);
		}
		return prefixes;
	}

	/*
	 * In text: a string seperate by comma (,) Out a collection of elements
	 * without (between) comma
	 */
	static Collection parseIPaddresses(String text) {
		Vector prefixes = new Vector();
		if (text == null || "".equals(text)) {
			return prefixes;
		}

		String tempStr = text.toUpperCase();
		String currentLabel = null;

		int index = tempStr.indexOf(",");
		while (index != -1) {
			currentLabel = tempStr.substring(0, index).trim();
			if (!"".equals(currentLabel)) {
				prefixes.addElement(currentLabel);
			}
			tempStr = tempStr.substring(index + 1);
			index = tempStr.indexOf(",");
		}
		// Last label
		currentLabel = tempStr.trim();
		if (!"".equals(currentLabel)) {
			prefixes.addElement(currentLabel);
		}
		return prefixes;
	}

	/**
	 * Loads configuration parameters from the DB with the given name. Sets
	 * private variable to the loaded values.
	 */

	public static String getConfigValue(String configname) {

		ConfigGW retobj = new ConfigGW();
		if (preference == null) {
			return null;
		}
		Enumeration keys = preference.keys();
		String keytosearch = configname;
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (keytosearch.equalsIgnoreCase(key)) {
				retobj = (ConfigGW) preference.get(key);
				return retobj.getConfigvalue();
			}
		}
		return null;
	}

	public static String formatUserId(String userId, int formatType) {
		if (userId == null || "".equals(userId)) {
			return null;
		}
		String temp = userId;

		switch (formatType) {
		case Constants.USERID_FORMAT_INTERNATIONAL:
			if (temp.startsWith("9")) {
				temp = "84" + temp;
			} else if (temp.startsWith("09")) {
				temp = "84" + temp.substring(1);
			} // else startsWith("84")
			else if (temp.startsWith("01")) {
				temp = "84" + temp.substring(1);
			} else if (temp.startsWith("1")) {
				temp = "84" + temp;
			}
			break;
		case Constants.USERID_FORMAT_NATIONAL_NINE:
			if (temp.startsWith("84")) {
				temp = temp.substring(2);
			} else if (temp.startsWith("0")) {
				temp = temp.substring(1);
			} // else startsWith("9")
			break;
		case Constants.USERID_FORMAT_NATIONAL_ZERO:
			if (temp.startsWith("84")) {
				temp = "0" + temp.substring(2);
			} else if (temp.startsWith("9") || temp.startsWith("1")) {
				temp = "0" + temp;
			} // else startsWith("09")
			break;
		default:
			// System.out.println("formatUserId: Invalid userId format_type "
			// + formatType);
			return temp; // TrungDK
		}
		return temp;
	}

	public static String formatUserIdMO(String userId, int formatType) {
		if (userId == null || "".equals(userId)) {
			return null;
		}
		String temp = userId;
		if ("0".equalsIgnoreCase(Preference.REBUILD_USERID)) {
			return userId;
		}
		switch (formatType) {
		case Constants.USERID_FORMAT_INTERNATIONAL:
			if (temp.startsWith("9")) {
				temp = "84" + temp;
			} else if (temp.startsWith("09")) {
				temp = "84" + temp.substring(1);
			} // else startsWith("84")
			else if (temp.startsWith("01")) {
				temp = "84" + temp.substring(1);
			} else if (temp.startsWith("1")) {
				temp = "84" + temp;
			}
			break;
		case Constants.USERID_FORMAT_NATIONAL_NINE:
			if (temp.startsWith("84")) {
				temp = temp.substring(2);
			} else if (temp.startsWith("0")) {
				temp = temp.substring(1);
			} // else startsWith("9")
			break;
		case Constants.USERID_FORMAT_NATIONAL_ZERO:
			if (temp.startsWith("84")) {
				temp = "0" + temp.substring(2);
			} else if (temp.startsWith("9") || temp.startsWith("1")) {
				temp = "0" + temp;
			} // else startsWith("09")
			break;
		default:
			// System.out.println("formatUserId: Invalid userId format_type "
			// + formatType);
			return temp; // TrungDK
		}
		return temp;
	}

	public static String getTableSendQueue(int id) {
		try {
			String temp = LIST_TABLE_SEND_QUEUE[id];
			if ("0".equalsIgnoreCase(temp)) {
				return "";
			} else {
				return temp;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public static int getIDdbReceiveQueue(String userid) {
		try {
			int itemp = Integer.parseInt(userid.substring(userid.length() - 6));
			int idtemp = (itemp % LIST_DB_RECEIVE_QUEUE.length);
			return idtemp;

		} catch (Exception e) {
			return 0;
		}

	}

	public static String[] parseString(String text, String seperator) {
		Vector vResult = new Vector();
		if (text == null || "".equals(text)) {
			return null;
		}
		String tempStr = text.trim();
		String currentLabel = null;
		int index = tempStr.indexOf(seperator);
		while (index != -1) {
			currentLabel = tempStr.substring(0, index).trim();

			if (!"".equals(currentLabel)) {
				vResult.addElement(currentLabel);
			}
			tempStr = tempStr.substring(index + 1);
			index = tempStr.indexOf(seperator);
		} // Last label
		currentLabel = tempStr.trim();
		if (!"".equals(currentLabel)) {
			vResult.addElement(currentLabel);
		}
		String[] re = new String[vResult.size()];
		Iterator it = vResult.iterator();
		index = 0;
		while (it.hasNext()) {
			re[index] = (String) it.next();
			index++;
		}
		return re;
	}

}
