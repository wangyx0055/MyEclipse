package icom.gateway;

/*
 * Copyright (c) 2006-2008
 *  VietNamNet ICom
 * All rights reserved.
 * @version 2008.03
 */

import icom.common.Queue;
import icom.common.StringTool;
import icom.common.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;



import org.smpp.Data;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindReceiver;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.PDU;
import org.smpp.pdu.UnbindResp;
import org.smpp.util.ByteBuffer;

/**
 * Class <code>Gateway</code> shows how to use the SMPP toolkit. You can bound
 * and unbind from the SMSC, you can send every possible pdu and wait for a pdu
 * sent from the SMSC.
 */
public class Gateway extends Thread {
	static Utilities util = new Utilities();

	public static LoadConfig loadconfig = null;
	public static LoadRepeat loadrepeat = null;

	static final String copyright = "Copyright (c) 2006-2009 by VietNamNet ICom Jsc";
	static final String version = "SMSGateway - Sendman - Version 2010.01.18.08";

	static {
		util.log("Gateway", copyright);
		util.log("Gateway", version);
	}

	// File with default settings for the application.
	static String propsFilePath = "gateway.cfg";

	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(
			System.in));

	// ========================================================================//
	// This is the SMPP session used for communication with SMSC.
	static Session session = null; // for ASYNC mode and transmit in SYNC mode
	static Session sessionr = null; // for receive in SYNC mode

	// If the application is bound to the SMSC.
	static boolean bound = false;
	static boolean boundr = false;

	static boolean rebound = false;
	static boolean running = true; // gateway is running
	static boolean learning = false; // learning emsdata from emsbuilder

	// ========================================================================//
	// 3 queues containing PDUs from SMSC
	// private Queue requestQueue = null;
	static Queue responseQueue = null;
	private Queue deliveryQueue = null;
	static Queue requestQueue = null;
	static Queue EMSQueue = null;
	static Queue SendLogQueue = null;
	static Queue ResendQueue = null;

	static Queue BlackListQueue = null;

	// map (String) seqNumber -&- Wait4Response object
	// seqNumber is key
	static Map wait4ReportTable = new HashMap();
	static Map wait4ResponseTable = new HashMap();

	static Queue toSMSC = new Queue();

	// static Queue logQueue = new Queue();

	static long startup_time = System.currentTimeMillis();

	// Time when gateway is bound or unbound
	static long bound_or_unbound_time = 0;

	private PDUEventListener pduListener = null;

	// //-------------------------------

	public static GetMt2EmsQueue[] getmt2emsqueue = null;
	public static SMSCSender[] smscsender = null;
	public static EMSBuilderfromQueue[] emsbuilderfromqueue = null;
	public static ResponseProcessor[] responseprocessor = null;
	// Add2EmsSendLog
	public static Add2EmsSendLog[] add2emssendlog = null;

	public static Add2BlackListLog[] add2blacklistlog = null;

	// ResendErrMt2Telcos
	public static ResendErrMt2Telcos[] resenderrmt2telcos = null;
	// CheckTimeOutWait4Response
	public static CheckTimeOutWait4Response[] checktimeoutwait4response = null;
	// RequestProcessor
	public static RequestProcessor[] requestprocessor = null;
	// SMSCReceiver

	public static SMSCReceiver[] smscreceiver = null;
	// ResponseSender
	// public static ResponseSender[] responsesender = null;
	// WriteLogQueue
	public static WriteLogQueue[] writelogqueue = null;
	static Gateway gateway = new Gateway();
	public static CheckStatusThread checkstatusthread = new CheckStatusThread();

	// ========================================================================//
	private static Vector liveThreads = new Vector();

	public static boolean isAllThreadStarted = false;

	static Vector getLiveThreads() {
		return liveThreads;
	}

	static void addLiveThread(Thread thread) {
		synchronized (liveThreads) {
			liveThreads.add(thread);
		}
	}

	static boolean removeThread(Thread diedThread) {
		synchronized (liveThreads) {
			Iterator it = liveThreads.iterator();
			while (it.hasNext()) {
				Thread currThread = (Thread) it.next();
				if (currThread.equals(diedThread)) {
					liveThreads.remove(diedThread);
					return true;
				}
			}
			return false;
		}
	}

	// ========================================================================//
	private static Vector liveTelnetSessions = null;

	static Vector getLiveTelnetSessions() {
		if (liveTelnetSessions == null) {
			liveTelnetSessions = new Vector();
		}
		return liveTelnetSessions;
	}

	public Queue getRequestQueue() {
		return requestQueue;
	}

	public Queue getResendQueue() {
		return ResendQueue;
	}

	public Queue getResponseQueue() {
		return responseQueue;
	}

	public Queue getDeliveryQueue() {
		return deliveryQueue;
	}

	public Queue getToSMSCQueue() {
		return toSMSC;
	}

	public Queue getEMSQueue() {
		return EMSQueue;
	}

	public Queue getSendLogQueue() {
		return SendLogQueue;
	}

	public Session getSession() {
		return session;
	}

	public Map getWait4ReportTable() {
		return wait4ReportTable;
	}

	public Map getWait4ResponseTable() {
		return wait4ResponseTable;
	}

	public PDUEventListener getPDUEventListener() {
		return pduListener;
	}

	// ========================================================================//
	/**
	 * Initialises the application, lods default values for connection to SMSC
	 * and for various PDU fields.
	 */
	public Gateway() {
		requestQueue = new Queue();
		responseQueue = new Queue();
		deliveryQueue = new Queue();
		EMSQueue = new Queue();
		SendLogQueue = new Queue();
		ResendQueue = new Queue();

		BlackListQueue = new Queue();

		// ../log/
		try {
			Logger.setLogWriter("../log/gateway" + "${yyyy-MM-dd}.log");
		} catch (IOException ex) {
		}
		Logger.setLogLevel("info,warn,error,crisis");

	}

	public static void main(String args[]) {
		gateway = new Gateway();
		util.log("Gateway", copyright);
		util.log("Gateway", version);
		Utilities.ConfigPrirose();
		try {
			Preference.loadProperties(propsFilePath);
		} catch (IOException e) {
			Logger.info("Gateway", "main: khong tim thay file cau hinh"
					+ propsFilePath);
		}

		// try {
		// String domain, String issue, String level,
		// String alertmsg, String contact)
		// /DBTools.Alert(Preference.Channel, "Gateway Start!",
		// Constants.ALERT_NONE, new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss").format(new Date())
		// + " - Gateway " + Preference.Channel + " start.",
		// Preference.ALERT_CONTACT);
		// } catch (DBException ex) {
		// }
		// gateway.addMoreConnection2Pool(Preference.db_MaxConnections);

		ShutdownInterceptor shutdownInterceptor = new ShutdownInterceptor(
				gateway);
		Runtime.getRuntime().addShutdownHook(shutdownInterceptor);

		gateway.bind(); // blocked until bound to SMSC
		gateway.start();
	}

	public void run() {

		if ("1".equalsIgnoreCase(Preference.MAPCP)) {
			loadconfig = new LoadConfig();
			loadconfig.setPriority(Thread.MAX_PRIORITY);
			loadconfig.start();
			while (!loadconfig.isLoaded) {
				try {
					System.out.print(".");
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if ("1".equalsIgnoreCase(Preference.BLACKLIST)) {
			loadrepeat = new LoadRepeat();
			loadrepeat.setPriority(Thread.MAX_PRIORITY);
			loadrepeat.start();
			while (!loadrepeat.isLoaded) {
				try {
					System.out.print(".");
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		try {
			PullMO2Queue();
		} catch (IOException ex) {
			Logger.info(this.getClass().getName(), "PullMO2Queue IOException:"
					+ ex.getMessage());
		}

		//
		if (Preference.bindMode.equalsIgnoreCase("t")
				|| Preference.bindMode.equalsIgnoreCase("tr")) {
			// OK
			// /for (int i = 0; i < Preference.nTheardMT; i++) {
			// / new SMSCSender(toSMSC, this).start();
			// /}
			smscsender = new SMSCSender[Preference.nTheardMT];
			for (int i = 0; i < Preference.nTheardMT; i++) {
				smscsender[i] = new SMSCSender(toSMSC, this);
				smscsender[i].setPriority(Thread.MAX_PRIORITY);
				smscsender[i].start();
			}
			// OK
			// for (int k = 0; k < Preference.nGetMTfromDB; k++) {
			// new GetMt2EmsQueue(EMSQueue, Preference.nGetMTfromDB + "", k
			// + "").start();
			// }
			getmt2emsqueue = new GetMt2EmsQueue[Preference.nGetMTfromDB
					* Preference.LIST_DB_SEND_QUEUE.length];
			int pidsendqueue = 0;
			for (int i = 0; i < Preference.LIST_DB_SEND_QUEUE.length; i++) {
				for (int k = 0; k < Preference.nGetMTfromDB; k++) {
					getmt2emsqueue[pidsendqueue] = new GetMt2EmsQueue(EMSQueue,
							Preference.nGetMTfromDB + "", k + "", i);
					getmt2emsqueue[pidsendqueue]
							.setPriority(Thread.MAX_PRIORITY);
					getmt2emsqueue[pidsendqueue].start();
					pidsendqueue++;

				}
			}

			// OK
			// for (int n = 0; n < Preference.nBuildEMS; n++) {
			// new EMSBuilderfromQueue(toSMSC, EMSQueue, wait4ResponseTable)
			// .start();
			// }
			emsbuilderfromqueue = new EMSBuilderfromQueue[Preference.nBuildEMS];
			for (int n = 0; n < Preference.nBuildEMS; n++) {
				emsbuilderfromqueue[n] = new EMSBuilderfromQueue(toSMSC,
						EMSQueue, wait4ResponseTable, BlackListQueue);
				emsbuilderfromqueue[n].setPriority(Thread.MAX_PRIORITY);
				emsbuilderfromqueue[n].start();
			}

			// OK
			// for (int j = 0; j < Preference.nTheardResp; j++) {
			// new ResponseProcessor(responseQueue, wait4ResponseTable,
			// SendLogQueue, ResendQueue).start();
			// }

			responseprocessor = new ResponseProcessor[Preference.nTheardResp];
			for (int j = 0; j < Preference.nTheardResp; j++) {
				responseprocessor[j] = new ResponseProcessor(responseQueue,
						wait4ResponseTable, SendLogQueue, ResendQueue);
				responseprocessor[j].setPriority(Thread.MAX_PRIORITY);
				responseprocessor[j].start();
			}
			// OK
			// for (int l = 0; l < Preference.nSendLog; l++) {
			// new Add2EmsSendLog(SendLogQueue).start();
			// }
			add2emssendlog = new Add2EmsSendLog[Preference.nSendLog];
			for (int l = 0; l < Preference.nSendLog; l++) {
				add2emssendlog[l] = new Add2EmsSendLog(SendLogQueue);
				add2emssendlog[l].setPriority(Thread.MIN_PRIORITY);
				add2emssendlog[l].start();
			}

			add2blacklistlog = new Add2BlackListLog[1];
			add2blacklistlog[0] = new Add2BlackListLog(BlackListQueue);
			add2blacklistlog[0].start();

			// OK
			// new ResendErrMt2Telcos(ResendQueue, EMSQueue).start();
			resenderrmt2telcos = new ResendErrMt2Telcos[1];
			resenderrmt2telcos[0] = new ResendErrMt2Telcos(ResendQueue,
					EMSQueue);
			resenderrmt2telcos[0].setPriority(Thread.NORM_PRIORITY);
			resenderrmt2telcos[0].start();

			// OK
			// new CheckTimeOutWait4Response(wait4ResponseTable, SendLogQueue)
			// .start();
			checktimeoutwait4response = new CheckTimeOutWait4Response[1];
			checktimeoutwait4response[0] = new CheckTimeOutWait4Response(
					wait4ResponseTable, SendLogQueue);
			checktimeoutwait4response[0].setPriority(Thread.NORM_PRIORITY);
			checktimeoutwait4response[0].start();

		}
		if (Preference.bindMode.equalsIgnoreCase("r")
				|| Preference.bindMode.equalsIgnoreCase("tr")) {
			// for (int k = 0; k < Preference.nTheardMO; k++) {
			// OK
			// new RequestProcessor(requestQueue, toSMSC).start();
			// }
			requestprocessor = new RequestProcessor[Preference.nTheardMO];
			int check = 1000;
			for (int k = 0; k < Preference.nTheardMO; k++) {
				// OK
				check = check + 1;
				requestprocessor[k] = new RequestProcessor(requestQueue, toSMSC);
				requestprocessor[k].setPriority(Thread.MAX_PRIORITY);
				requestprocessor[k].setThreadId(check);
				requestprocessor[k].start();
			}
			if (!Preference.asynchronous) {
				// OK
				// new SMSCReceiver(sessionr, requestQueue).start();
				smscreceiver = new SMSCReceiver[1];
				smscreceiver[0] = new SMSCReceiver(sessionr, requestQueue);
				smscreceiver[0].setPriority(Thread.MAX_PRIORITY);
				smscreceiver[0].start();
			}

		}

		if (Preference.bindMode.equalsIgnoreCase("r")
				&& Preference.asynchronous) {
			// for (int i = 0; i < Preference.nTheardMT; i++) {
			// new SMSCSender(toSMSC, this).start();
			// }

			smscsender = new SMSCSender[Preference.nTheardMT];
			for (int i = 0; i < Preference.nTheardMT; i++) {
				smscsender[i] = new SMSCSender(toSMSC, this);
				smscsender[i].setPriority(Thread.MAX_PRIORITY);
				smscsender[i].start();
			}
		}
		// Log Queue
		if (Preference.nLogQueue == 1) {
			new WriteLogQueue(toSMSC, requestQueue, responseQueue, EMSQueue,
					SendLogQueue, ResendQueue, wait4ResponseTable).start();

		}
		// All cases
		new EnquireLinkThread(this).start();
		if (Preference.logToConsole == 1) {
			new KeyboardReader().start();
		}
		Logger.info("Gateway", "Gateway started.");

		DBTools.ALERT(Preference.mobileOperator, "Gateway Started!",
				Constants.ALERT_NONE, "Gateway " + Preference.Channel
						+ " started.", Preference.ALERT_CONTACT);

		/*
		 * while (running) { try { this.showMenu(); } catch (Exception e) {
		 * Logger.error("Gateway", e.getMessage()); } }
		 */
		// Logger.info("Gateway", "END.");
		isAllThreadStarted = true;

		checkstatusthread = new CheckStatusThread();
		checkstatusthread.start();

	

	}

	// ============================ BINDING METHODS
	// ===========================//
	// 1. bind, unbind
	// 2. submit :: SubmitSM
	// ========================================================================//
	/**
	 * The first method called to start communication betwen an ESME and a SMSC.
	 * A new instance of <code>TCPIPConnection</code> is created and the IP
	 * address and port obtained from user are passed to this instance. New
	 * <code>Session</code> is created which uses the created
	 * <code>TCPIPConnection</code>.
	 * 
	 * All the parameters required for a bind are set to the
	 * <code>BindRequest</code> and this request is passed to the
	 * <code>Session</code>'s <code>bind</code>method. If the call is
	 * successful, the application should be bound to the SMSC.
	 * 
	 * Note: In the SYNC mode, the receive() method is blocked until a pdu is
	 * received. So if we send and receive PDU on the same session's connection,
	 * the first pdu (request) is sent e.g: response = session.submit(request)),
	 * but its response is received by the session.receive() method => The
	 * submit() method is blocked until timeout; with exception of
	 * NullPointerException of the return value. A solution is that in the
	 * tr-SYNC mode, create 2 sessions: one fortransmitting (t) and another for
	 * receiving (r).
	 */
	synchronized public void bind() {
		if (bound) { // connected (Open) and bound to SMSC
			Logger.info("Gateway.bind", "Already bound, unbind first");
			return;
		}
		bound_or_unbound_time = System.currentTimeMillis();
		if (Preference.asynchronous) {
			bindASync();
		} else {
			if (Preference.bindMode.compareToIgnoreCase("t") == 0) {
				bindSyncTransmitter();
			} else if (Preference.bindMode.compareToIgnoreCase("r") == 0) {
				bindSyncReceiver();
			} else if (Preference.bindMode.compareToIgnoreCase("tr") == 0) {
				bindSyncTransmitter();
				bindSyncReceiver();
			} else {
				Logger.error("Gateway.bind", "Invalid bind mode ("
						+ Preference.bindMode
						+ ") expected t, r or tr. Operation canceled.");
				return;
			}
		}
		bound_or_unbound_time = System.currentTimeMillis();
	}
	
	synchronized public  void rebind() {
		bound_or_unbound_time = System.currentTimeMillis();
		if (Preference.asynchronous) {
			rebindASync();
		} else {
			if (Preference.bindMode.compareToIgnoreCase("t") == 0) {
				bindSyncTransmitter();
			} else if (Preference.bindMode.compareToIgnoreCase("r") == 0) {
				bindSyncReceiver();
			} else if (Preference.bindMode.compareToIgnoreCase("tr") == 0) {
				bindSyncTransmitter();
				bindSyncReceiver();
			} else {
				Logger.error("Gateway.bind", "Invalid bind mode ("
						+ Preference.bindMode
						+ ") expected t, r or tr. Operation canceled.");
				return;
			}
		}
		bound_or_unbound_time = System.currentTimeMillis();
	}


	synchronized public void bindSyncTransmitter() {
		BindRequest request = null;
		BindResponse response = null;
		TCPIPConnection connection = null;
		while (!bound) {
			try {
				Logger.info("Gateway.bindSyncTransmitter",
						"Connecting to SMSC " + Preference.ipAddress + ":"
								+ Preference.port);
				connection = new TCPIPConnection(Preference.ipAddress,
						Preference.port);
				connection.setReceiveTimeout(2 * 1000); // REVIEW
				session = new Session(connection);
				request = new BindTransmitter();
				// set values
				request.setSystemId(Preference.systemId); // username
				request.setPassword(Preference.password);
				request.setSystemType(Preference.systemType);
				request.setInterfaceVersion((byte) 0x34); // SMPPv3.4
				request.setAddressRange(Preference.addressRange);

				// send the request
				Logger.info("Gateway.bindSyncTransmitter", "Bind request "
						+ request.debugString());
				// ///////////////////////////////////////////////
				response = session.bind(request);
				// ///////////////////////////////////////////////
				Logger.info("Gateway.bindSyncTransmitter", "Bind response "
						+ response.debugString());
				if (response.getCommandStatus() == Data.ESME_ROK) { // no error
					bound = true;
					Logger.info("Gateway.bindSyncTransmitter",
							"Succesfully Bound to SMSC in t-sync mode At "
									+ new java.sql.Timestamp(System
											.currentTimeMillis()) + "!!!");

				} else {
					SMSCTools.printCommandStatus(response.getCommandStatus());
				}
			} catch (Exception e) {
				Logger.error("Gateway.bindSyncTransmitter",
						"Bind operation FAILT. Try later in "
								+ (Preference.timeRebind / 1000) + " seconds");

				DBTools.ALERT("Gateway", "bindSyncTransmitter",
						Constants.ALERT_WARN, Preference.Channel
								+ "@Bind operation FAILT. Try later",
						Preference.ALERT_CONTACT);
				Logger.printStackTrace(e);
				try {
					sleep(Preference.timeRebind);
				} catch (InterruptedException ie) {
				}
			}
		}
	}

	synchronized public void bindSyncReceiver() {
		BindRequest request = null;
		BindResponse response = null;
		org.smpp.Connection connection = null;
		while (!boundr) {
			try {
				Logger.info("Gateway", "bindSyncReceiver.Connecting to SMSC "
						+ Preference.ipAddress + ":" + Preference.port);
				connection = new TCPIPConnection(Preference.ipAddress,
						Preference.port);
				connection.setReceiveTimeout(2 * 1000); // REVIEW
				sessionr = new Session(connection);
				request = new BindReceiver();
				// set values
				request.setSystemId(Preference.systemId); // username
				request.setPassword(Preference.password);
				request.setSystemType(Preference.systemType);
				request.setInterfaceVersion((byte) 0x34); // SMPPv3.4
				request.setAddressRange(Preference.addressRange);

				// send the request
				Logger.info(this.getClass().getName(), "Bind request "
						+ request.debugString());
				// ///////////////////////////////////////////////
				response = sessionr.bind(request);
				// ///////////////////////////////////////////////
				Logger.info(this.getClass().getName(), "Bind response "
						+ response.debugString());
				if (response.getCommandStatus() == Data.ESME_ROK) { // no error
					boundr = true;
					Logger.info(this.getClass().getName(),
							"Succesfully Bound to SMSC in r-sync mode At "
									+ new java.sql.Timestamp(System
											.currentTimeMillis()) + "!!!");

				} else {
					SMSCTools.printCommandStatus(response.getCommandStatus());
				}
			} catch (Exception e) {
				Logger.info(this.getClass().getName(), e.toString());
				Logger.info(this.getClass().getName(),
						"Bind operation FAILT. Try later in "
								+ (Preference.timeRebind / 1000) + " seconds");

				try {
					sleep(Preference.timeRebind);
				} catch (InterruptedException ie) {
				}
			}
		}
	}

	synchronized public void bindASync() {
		BindRequest request = null;
		BindResponse response = null;
		org.smpp.Connection connection = null;

		while (!bound) {
			try {
				Logger.info(this.getClass().getName(), "Connecting to SMSC "
						+ Preference.ipAddress + ":" + Preference.port);
				connection = new TCPIPConnection(Preference.ipAddress,
						Preference.port);
				connection.setReceiveTimeout(2 * 1000); // REVIEW
				session = new Session(connection);

				// Bind in which mode?: Transmitter, Receiver or Transceiver.
				if (Preference.bindMode.compareToIgnoreCase("t") == 0) {
					request = new BindTransmitter();
				} else if (Preference.bindMode.compareToIgnoreCase("r") == 0) {
					request = new BindReceiver();
				} else if (Preference.bindMode.compareToIgnoreCase("tr") == 0) {
					request = new BindTransciever();
				} else {
					Logger
							.error(
									this.getClass().getName(),
									"Invalid bind mode ("
											+ Preference.bindMode
											+ ") expected t, r or tr. Operation canceled.");
					return;
				}

				// set values
				request.setSystemId(Preference.systemId); // username
				request.setPassword(Preference.password);
				request.setSystemType(Preference.systemType);
				request.setInterfaceVersion((byte) 0x34); // SMPPv3.4
				request.setAddressRange(Preference.addressRange);
				// //////////////////////////////////////////////////////////////
				if (pduListener == null) { // Important when rebind
					// automatically
					// Note: PDUEventListener instance must be placed before
					// receiving any response.
					pduListener = new PDUEventListener(requestQueue,
							responseQueue, deliveryQueue, toSMSC);
				}
				// //////////////////////////////////////////////////////////////
				// send the request
				Logger.info(this.getClass().getName(), "Bind request "
						+ request.debugString());
				response = session.bind(request, pduListener);
				Logger.info(this.getClass().getName(), "Bind response "
						+ response.debugString());
				if (response.getCommandStatus() == Data.ESME_ROK) { // no error
					bound = true;
					Logger.info(this.getClass().getName(),
							"Succesfully Bound to SMSC in "
									+ Preference.bindMode
									+ (Preference.asynchronous ? "-async"
											: "-sync")
									+ " mode At "
									+ new java.sql.Timestamp(System
											.currentTimeMillis()) + "!!!");

				} else {
					SMSCTools.printCommandStatus(response.getCommandStatus());

					DBTools.ALERT(Preference.mobileOperator,
							"Gateway bindASync!", Constants.ALERT_MAJOR,
							Preference.Channel + "@Gateway:"
									+ Preference.mobileOperator + "-> ERROR: "
									+ response.getCommandStatus(),
							Preference.ALERT_CONTACT);
					// Added on 28/11/2003
					try {
						sleep(Preference.timeRebind);
					} catch (InterruptedException ie) {
					}
				}
			} catch (Exception e) {
				Logger.error(this.getClass().getName(),
						"Bind operation FAILT. Try later in "
								+ (Preference.timeRebind / 1000) + " seconds");

				try {
					Logger.crisis(this.getClass().getName(), "-Gateway"
							+ Preference.mobileOperator
							+ "-> Bind operation FAILT");

					DBTools.ALERT("Bind", "Bind.Exception",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ Preference.mobileOperator
									+ "-> Bind operation FAILT",
							Preference.ALERT_CONTACT);
					sleep(Preference.timeRebind);
				} catch (Exception ie) {
				}

			}
		}
	}
	
	synchronized public void rebindASync() {
		BindRequest request = null;
		BindResponse response = null;
		org.smpp.Connection connection = null;

		while (rebound) {
			try {
				Logger.info(this.getClass().getName(), "Connecting to SMSC "
						+ Preference.ipAddress + ":" + Preference.port);
				connection = new TCPIPConnection(Preference.ipAddress,
						Preference.port);
				connection.setReceiveTimeout(2 * 1000); // REVIEW
				session = new Session(connection);

				// Bind in which mode?: Transmitter, Receiver or Transceiver.
				if (Preference.bindMode.compareToIgnoreCase("t") == 0) {
					request = new BindTransmitter();
				} else if (Preference.bindMode.compareToIgnoreCase("r") == 0) {
					request = new BindReceiver();
				} else if (Preference.bindMode.compareToIgnoreCase("tr") == 0) {
					request = new BindTransciever();
				} else {
					Logger
							.error(
									this.getClass().getName(),
									"Invalid bind mode ("
											+ Preference.bindMode
											+ ") expected t, r or tr. Operation canceled.");
					return;
				}

				// set values
				request.setSystemId(Preference.systemId); // username
				request.setPassword(Preference.password);
				request.setSystemType(Preference.systemType);
				request.setInterfaceVersion((byte) 0x34); // SMPPv3.4
				request.setAddressRange(Preference.addressRange);
				// //////////////////////////////////////////////////////////////
				if (pduListener == null) { // Important when rebind
					// automatically
					// Note: PDUEventListener instance must be placed before
					// receiving any response.
					pduListener = new PDUEventListener(requestQueue,
							responseQueue, deliveryQueue, toSMSC);
				}
				// //////////////////////////////////////////////////////////////
				// send the request
				Logger.info(this.getClass().getName(), "Bind request "
						+ request.debugString());
				response = session.bind(request, pduListener);
				Logger.info(this.getClass().getName(), "Bind response "
						+ response.debugString());
				if (response.getCommandStatus() == Data.ESME_ROK) { // no error
					rebound = false;
					Logger.info(this.getClass().getName(),
							"Succesfully Bound to SMSC in "
									+ Preference.bindMode
									+ (Preference.asynchronous ? "-async"
											: "-sync")
									+ " mode At "
									+ new java.sql.Timestamp(System
											.currentTimeMillis()) + "!!!");

				} else {
					SMSCTools.printCommandStatus(response.getCommandStatus());

					DBTools.ALERT(Preference.mobileOperator,
							"Gateway bindASync!", Constants.ALERT_MAJOR,
							Preference.Channel + "@Gateway:"
									+ Preference.mobileOperator + "-> ERROR: "
									+ response.getCommandStatus(),
							Preference.ALERT_CONTACT);
					// Added on 28/11/2003
					try {
						sleep(Preference.timeRebind);
					} catch (InterruptedException ie) {
					}
				}
			} catch (Exception e) {
				Logger.error(this.getClass().getName(),
						"Bind operation FAILT. Try later in "
								+ (Preference.timeRebind / 1000) + " seconds");

				try {
					Logger.crisis(this.getClass().getName(), "-Gateway"
							+ Preference.mobileOperator
							+ "-> Bind operation FAILT");

					DBTools.ALERT("Bind", "Bind.Exception",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ Preference.mobileOperator
									+ "-> Bind operation FAILT",
							Preference.ALERT_CONTACT);
					sleep(Preference.timeRebind);
				} catch (Exception ie) {
				}

			}
		}
	}



	/**
	 * Ubinds (logs out) from the SMSC and closes the connection.
	 */
	synchronized public static void unbind() {
		try {
			if (bound || boundr) {
				Logger.info("Gateway", "Going to unbind...");
			}
			if (bound) {
				UnbindResp response = session.unbind();
				Logger.info("Gateway", "Unbind response "
						+ response.debugString());
				bound = false;
			}
			if (boundr) { // Bound in Receiver (& SYNC) mode
				if (sessionr.getReceiver().isReceiver()) {
					Logger.info("Gateway",
							"It can take a while to stop the receiver.");
					Logger.info("Gateway", "Doi chut xiu!!!");
				}
				UnbindResp response = sessionr.unbind();
				Logger.info("Gateway", "Unbind response "
						+ response.debugString());
				boundr = false;
			}
			if (!bound && !boundr) {
				Logger.info("Gateway", "UnBound to SMSC At "
						+ new java.sql.Timestamp(System.currentTimeMillis())
						+ "!!!");
			}
		} catch (Exception e) {
			Logger.error("Gateway", "Unbind operation failed. " + e);
		}
	}

	// Save wait4Report_Table into file for loading on the next startup
	// File format:
	// messageId <tab> LogId <tab> time_in_longvalue <tab> time_text_format
	public static void saveWait4ReportTable() {
		if (wait4ReportTable == null || wait4ReportTable.size() == 0) {
			return;
		}
		int nTimeout = 0;

		Logger.info("Gateway", "Saving wait4report_Table...");
		try {
			java.io.PrintWriter fout = new java.io.PrintWriter(
					new java.io.FileOutputStream("wait4report.dat", false)); // append
			// =
			// false
			java.io.PrintWriter fTimeout = new java.io.PrintWriter(
					new java.io.FileOutputStream("wait4report_timeout.dat",
							true)); // append = true
			synchronized (wait4ReportTable) {
				for (Enumeration e = new Vector(wait4ReportTable.keySet())
						.elements(); e.hasMoreElements();) {
					String key = (String) e.nextElement(); // key=messageId
					Wait4Report value = (Wait4Report) wait4ReportTable.get(key);
					if (value.isTimeout()) {
						fTimeout.print(key + "\t");
						fTimeout.print(value.logId + "\t");
						fTimeout.print(value.time.getTime() + "\t");
						fTimeout.println(value.time);
						nTimeout++;
						continue;
					}
					fout.print(key + "\t");
					fout.print(value.logId + "\t");
					fout.print(value.time.getTime() + "\t");
					fout.println(value.time);
				}
			}
			fout.flush();
			fout.close();
			fTimeout.flush();
			fTimeout.close();
			Logger.info("Gateway", " [" + nTimeout + " entries timeout]!");
		} catch (IOException ex) {
			Logger.error("Gateway", "Gateway.saveWait4ReportTable: "
					+ ex.getMessage());
		}
	}

	public static void saveWait4ResponseTable() {
		synchronized (wait4ResponseTable) {
			if (wait4ResponseTable == null || wait4ResponseTable.size() == 0) {
				return;
			}
			int nTimeout = 0;

			Logger.info("Gateway", "Saving wait4ResponseTable...");
			try {

				if (wait4ResponseTable.size() > 0) {
					for (Enumeration e = new Vector(wait4ResponseTable.keySet())
							.elements(); e.hasMoreElements();) {
						String key = (String) e.nextElement(); // key=messageId
						EMSData value = (EMSData) wait4ResponseTable.get(key);
						if (value != null) {
							saveSMSObject(nTimeout + ".w4rt", value);
							nTimeout++;
							continue;
						}
					}
				}
			}

			catch (Exception ex) {
				Logger.error("Gateway",
						"IOException:Loi luu file tu wait4ResponseTable: "
								+ ex.getMessage());
			}
		}
	}

	public static void saveMO() {

		PDUData pdud = null;
		PDU pdu = null;
		int nTimeout = 1;

		Logger.info("Gateway", "Saving MO-MT-Response to file...");
		try {
			Logger.info("Gateway.SaveMO", "Save MO queue size");
			while (!(requestQueue == null || requestQueue.size() == 0)) {
				Logger.info("Gateway", "MO Queue size: " + requestQueue.size());
				pdud = (PDUData) requestQueue.dequeue(); // blocks until
				// having an item
				pdu = pdud.getPDU();
				if (pdu.isRequest()) {
					saveToFile(nTimeout + ".mo", pdu);
					nTimeout++;

				}
			}

			Logger.info("Gateway.SaveMO", "Save Response queue size");
			while (!(responseQueue == null || responseQueue.size() == 0)) {
				Logger.info("Gateway", "Response Queue size: "
						+ responseQueue.size());
				pdu = (PDU) responseQueue.dequeue(); // blocks until having
				// an item
				if (pdu.isResponse()) {
					saveToFile(nTimeout + ".respond", pdu);
					nTimeout++;

				}
			}

			Logger.info("Gateway.SaveMO",
					"Save  MT get from DB Queue queue size");
			while (!(EMSQueue == null || EMSQueue.size() == 0)) {
				Logger.info("Gateway", "MT get from DB Queue size: "
						+ EMSQueue.size());
				EMSData ems = (EMSData) EMSQueue.dequeue(); // blocks until
				// having an item
				if (ems != null) {
					saveSMSObject(nTimeout + ".mt", ems);
					nTimeout++;

				}
			}

			Logger.info("Gateway.SaveMO", "Save SendLogQueue");
			while (!(SendLogQueue == null || SendLogQueue.size() == 0)) {
				Logger.info("Gateway", "SendLogQueue size: "
						+ SendLogQueue.size());
				EMSData ems = (EMSData) SendLogQueue.dequeue(); // blocks until
				// having an
				// item
				if (ems != null) {
					saveSMSObject(nTimeout + ".slog", ems);
					nTimeout++;
					System.out.print(".");
				}
			}

			Logger.info("Gateway.SaveMO", "Save ResendQueue queue size");
			while (!(ResendQueue == null || ResendQueue.size() == 0)) {
				Logger.info("Gateway", "ResendQueue size: "
						+ ResendQueue.size());
				EMSData ems = (EMSData) ResendQueue.dequeue(); // blocks until
				// having an
				// item
				if (ems != null) {
					saveSMSObject(nTimeout + ".rsend", ems);
					nTimeout++;
					System.out.print(".");
				}
			}
			Logger.info("Gateway.SaveMO", "Save toSMSC queue");
			while (!(toSMSC == null || toSMSC.size() == 0)) {
				Logger.info("Gateway", "toSMSC size: " + toSMSC.size());
				pdu = (PDU) toSMSC.dequeue(); // blocks until having an item
				// pdu = pdud.getPDU();
				saveToFile(nTimeout + ".tosmsc", pdu);
				nTimeout++;
				System.out.print(".");
			}
		} catch (Exception ex) {
			Logger.error("Gateway.SaveMO", ex.getMessage());
		}

	}

	private static void saveToFile(String pduFile, PDU pdu) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Date date = new java.util.Date();
		String datetime = dateFormat.format(date);
		try {
			byte[] b = pdu.getData().getBuffer();
			java.io.FileOutputStream fout = new java.io.FileOutputStream(
					"./MOQueue/" + datetime + pduFile);
			fout.write(b);
			fout.flush();
			fout.close();
		} catch (Exception ex) {
			Logger.error("Gateway", "saveToFile:" + ex.getMessage());
		}
	}

	public static void saveSMSObject(String sfile, EMSData object) {
		Logger.info("Gateway", " Saving EMSData into file " + sfile);
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Date date = new java.util.Date();
		String datetime = dateFormat.format(date);
		try {
			fout = new java.io.FileOutputStream("./MOQueue/" + datetime + sfile);
			objOut = new ObjectOutputStream(fout);
			objOut.writeObject(object);
			objOut.flush();
		} catch (IOException ex) {
			Logger.error("Gateway", "Save data error: " + ex.getMessage());
		} finally {
			try {
				objOut.close();
				fout.close();
			} catch (IOException ex) {
			}
		}
	}

	public static void saveSMSObject1(String sfile, EMSData object) {
		Logger.info("Gateway", " Saving EMSData into file " + sfile);
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		// BigDecimal receiveid = object.getReceiveId();
		// String sfile = receiveid.toString() + object.getDestAddress();
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			java.util.Date date = new java.util.Date();
			String datetime = dateFormat.format(date);
			fout = new java.io.FileOutputStream("./MOQueue/" + datetime + sfile);
			objOut = new ObjectOutputStream(fout);
			objOut.writeObject(object);
			objOut.flush();
		} catch (IOException ex) {
			Logger.error("{Gateway}", "Save data error: " + sfile + " -> "
					+ ex.getMessage());
		} finally {
			try {
				objOut.close();
				fout.close();
			} catch (IOException ex) {
			}
		}
	}

	public static EMSData readSMSObject(File file) {

		FileInputStream fin = null;
		ObjectInputStream objIn = null;
		EMSData object = null;
		try {
			fin = new FileInputStream(file);
			objIn = new ObjectInputStream(fin);
			object = (EMSData) objIn.readObject();
			return object;
		} catch (ClassNotFoundException ex1) {
			return null;
		} catch (IOException ex) {
			Logger.error("{Gateway}", "ReadData: " + ex.getMessage());
			return null;
		} finally {

			try {
				objIn.close();
				fin.close();
				boolean success = file.renameTo(new File("./MOQueue/bak/"
						+ file.getName() + ".bak"));
				if (!success) {
					Logger.error("Gateway", "Rename failed:" + file.getName());
					if (!(file.delete())) {
						Logger.error("Gateway", "Delete: deletion failed:"
								+ file.getName());
					}
				}

				return object;
			} catch (IOException ex) {
				Logger.error("{Gateway}",
						"Save data error can not close file: -> "
								+ ex.getMessage());

			}
		}
	}

	public static void saveMOData(String fileName) {
		Logger.info("Gateway", "Saving " + fileName + " . . .");
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		PDUData pdu = null;
		int nTimeout = 0;
		if (requestQueue == null || requestQueue.size() == 0) {
			Logger.info("Gateway", "MO queue size = 0");
		}

		try {
			fout = new java.io.FileOutputStream(fileName, false); // append =
			// false
			objOut = new ObjectOutputStream(fout);
			while (!(requestQueue == null || requestQueue.size() == 0)) {
				pdu = (PDUData) requestQueue.dequeue(); // blocks until having
				// an item
				objOut.writeObject(pdu);

				// objOut.w
				objOut.flush();
				nTimeout++;
			}
			while (!(requestQueue == null || requestQueue.size() == 0)) {
				pdu = (PDUData) requestQueue.dequeue(); // blocks until having
				// an item
				objOut.writeObject(pdu);
				objOut.flush();
				nTimeout++;
			}
			Logger.info("Gateway.saveMOData", "num of Data: " + nTimeout
					+ " MOs Saved");
		} catch (IOException ex) {
			Logger.error("Gateway.saveMOData", "Save data error: "
					+ ex.getMessage());
		} finally {
			try {
				objOut.close();
				fout.close();
			} catch (IOException ex) {
				Logger.error("Gateway.saveMOData", "Save data error: "
						+ ex.getMessage());
			}
		}
	}

	public static void loadSMSMOData(String fileName) {
		Logger.info("Gateway.loadSMSMOData", "Loading data from file...");
		boolean flag = true;
		FileInputStream fin = null;
		ObjectInputStream objIn = null;
		try {

			fin = new java.io.FileInputStream(fileName);
			objIn = new ObjectInputStream(fin);

			while (flag) {
				try {
					PDU object = (PDU) objIn.readObject();
					// fin.r
					requestQueue.enqueue(object);
				} catch (Exception ex) {
					flag = false;
				}
			}
			Logger.info("Gateway.loadSMSMOData", " Load data successful!");
		} catch (IOException ex) {
			Logger.error("Gateway.loadSMSMOData", "Load data error: "
					+ ex.getMessage());
		} finally {
			try {
				fin.close();
			} catch (Exception ex) {
				Logger.error("Gateway.loadSMSMOData", "Load data error: "
						+ ex.getMessage());
			}
		}
	}

	public static void loadWait4ReportTable() {
		Map map = new HashMap();
		Logger.info("Gateway", "Loading wait4report_Table...");
		try {
			java.io.BufferedReader fin = new java.io.BufferedReader(
					new java.io.FileReader("wait4report.dat"));
			java.io.PrintWriter fTimeout = new java.io.PrintWriter(
					new java.io.FileOutputStream("wait4report_timeout.dat",
							true)); // append = true

			String line = null;
			int nTimeout = 0;
			while ((line = fin.readLine()) != null) {
				java.util.Collection coll = StringTool.parseString(line, "\t");
				String s[] = new String[coll.size()];
				coll.toArray(s);
				String msgId = s[0];
				String logId = s[1];
				long ltime = Long.parseLong(s[2]);
				String stime = s[3];

				Wait4Report wait4report = new Wait4Report(
						new BigDecimal(logId), new Timestamp(ltime));
				if (!wait4report.isTimeout()) {
					map.put(msgId, wait4report);
				} else {
					fTimeout.print(msgId + "\t");
					fTimeout.print(logId + "\t");
					fTimeout.print(ltime + "\t");
					fTimeout.println(stime);
					nTimeout++;
				}
			}
			fin.close();
			fTimeout.flush();
			fTimeout.close();

			wait4ReportTable.putAll(map);
			Logger.info("Gateway", " [" + map.size() + " entries loaded, "
					+ nTimeout + " entries timeout]!");
		} catch (IOException ex) {
			Logger.error("Gateway", "Gateway.loadWait4ReportTable: "
					+ ex.getMessage());
		}
	}

	public static void loadWait4ResponseTable() {
		Map map = new HashMap();
		Logger.info("Gateway", "Loading wait4ResponseTable...");
		try {
			java.io.BufferedReader fin = new java.io.BufferedReader(
					new java.io.FileReader("wait4Response.dat"));
			java.io.PrintWriter fTimeout = new java.io.PrintWriter(
					new java.io.FileOutputStream("wait4Response_timeout.dat",
							true)); // append = true

			String line = null;
			int nTimeout = 0;
			while ((line = fin.readLine()) != null) {
				java.util.Collection coll = StringTool.parseString(line, "\t");
				String s[] = new String[coll.size()];
				coll.toArray(s);
				String msgId = s[0];
				String logId = s[1];
				long ltime = Long.parseLong(s[2]);
				String stime = s[3];

				Wait4Report wait4report = new Wait4Report(
						new BigDecimal(logId), new Timestamp(ltime));
				if (!wait4report.isTimeout()) {
					map.put(msgId, wait4report);
				} else {
					fTimeout.print(msgId + "\t");
					fTimeout.print(logId + "\t");
					fTimeout.print(ltime + "\t");
					fTimeout.println(stime);
					nTimeout++;
				}
			}
			fin.close();
			fTimeout.flush();
			fTimeout.close();

			wait4ReportTable.putAll(map);
			Logger.info("Gateway", " [" + map.size() + " entries loaded, "
					+ nTimeout + " entries timeout]!");
		} catch (IOException ex) {
			Logger.info("Gateway", "Gateway.loadWait4ReportTable: "
					+ ex.getMessage());
		}
	}

	/**
	 * If bound, unbinds and then exits this application.
	 */
	/**
	 * Prompts the user to enter a string value for a parameter.
	 */
	private String getParam(String prompt, String defaultValue) {
		String value = "";
		String promptFull = prompt;
		promptFull += defaultValue == null ? "" : " [" + defaultValue + "] ";
		Logger.info("Gateway", promptFull);
		try {
			value = keyboard.readLine();
		} catch (IOException e) {
			Logger.error("Gateway", "Got exception getting a param. " + e);
		}
		if (value.compareTo("") == 0) {
			return defaultValue;
		} else {
			return value;
		}
	}

	private void showMenu() {

		System.out.println();
		System.out.println("R - Reload"); // reload gateway.cfg file
		System.out.println("Q - Quit");
		System.out.println("M - Load MO");
		System.out.println("T - Add new Thread");
		System.out.println();
		String option = "";
		try {
			// Blocked until a line input
			option = keyboard.readLine();
			option = option.toUpperCase();
			if ("R".equals(option)) {
				try {
					Preference.loadProperties(propsFilePath);

				} catch (IOException e) {
					Logger.error("Gateway",
							"Gateway.showMenu(): khong tim thay file cau hinh "
									+ propsFilePath);
				}
			} else if ("TH".equals(option)) {
				synchronized (wait4ResponseTable) {
					Logger.info(Preference.mobileOperator + "-toSMSQueueTX: "
							+ toSMSC.size() + "\n" + Preference.mobileOperator
							+ "-fromSMSQueueRX: " + requestQueue.size() + "\n"
							+ Preference.mobileOperator
							+ "-RespondSMSQueueRX: " + responseQueue.size()
							+ "\nWait4ResponseTable: "
							+ wait4ResponseTable.size() + "\nSendLogQueue: "
							+ SendLogQueue.size() + "\nGetMT2Queue: "
							+ EMSQueue.size() + "\nMOSimQueue: "
							+ ResendQueue.size());
				}

			} else if ("M".equals(option)) {
				this.PullMO2Queue();
			} else if ("Q".equals(option)) {
				// exit();
			} else if (option.equalsIgnoreCase("T")) {

				if (Preference.nAddNewTheardMO > 0) {
					for (int i = 0; i < Preference.nAddNewTheardMO; i++) {
						Logger.info("Gateway", "New Thread MO added");
						new RequestProcessor(requestQueue, toSMSC).start();
					}
				}
				if (Preference.nAddNewTheardMT > 0) {
					for (int i = 0; i < Preference.nAddNewTheardMT; i++) {
						Logger.info("Gateway", "New Thread MT added");
						new SMSCSender(toSMSC, this).start();
					}
				}

				if (Preference.nAddNewTheardResp > 0) {
					for (int i = 0; i < Preference.nAddNewTheardResp; i++) {
						Logger.info("Gateway", "New Thread Respond added");
						new ResponseProcessor(responseQueue,
								wait4ResponseTable, SendLogQueue, ResendQueue)
								.start();
					}
				}

				if (Preference.nAddNewBuildEMS > 0) {
					for (int i = 0; i < Preference.nAddNewBuildEMS; i++) {
						Logger.info("Gateway", "New Thread EMSBuilder added");
						new EMSBuilderfromQueue(toSMSC, EMSQueue,
								wait4ResponseTable, BlackListQueue).start();
					}
				}

				if (Preference.nAddNewSendLog > 0) {
					for (int i = 0; i < Preference.nAddNewSendLog; i++) {
						Logger.info("Gateway", "New Thread Send Log added");
						new Add2EmsSendLog(SendLogQueue).start();
					}
				}

			}
		} catch (Exception e) {
			Logger.error("Gateway", "Gateway::" + e.getMessage());

			DBTools.ALERT("Gateway", "Gateway", Constants.ALERT_WARN,
					Preference.Channel + "@Gateway:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
		}
	}

	public void PullMO2Queue() throws IOException {
		byte[] buffer = null;
		PDU pdu = null;
		EMSData ems = null;
		Queue resp = new Queue();
		try {
			File f = new File("./MOQueue/");
			File[] listFile = f.listFiles();
			int numOfFile = 0;
			for (int i = 0; i < listFile.length; i++) {
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyyMMddhhmmssSSS");
				java.util.Date date = new java.util.Date();
				String datetime = dateFormat.format(date);
				if (listFile[i].getName().toUpperCase().endsWith(".MO")) {
					FileInputStream fin = new FileInputStream(listFile[i]);
					buffer = new byte[fin.available()];
					fin.read(buffer);
					ByteBuffer bf = new ByteBuffer();
					bf.appendBytes(buffer);
					pdu = pdu.createPDU(bf);
					PDUData pdud = new PDUData();
					pdud.setPDU(pdu);
					pdud.setRequestID(datetime);
					requestQueue.enqueue(pdud);
					fin.close();
					boolean success = listFile[i].renameTo(new File(
							"./MOQueue/bak/" + listFile[i].getName() + ".bak"));
					if (!success) {
						Logger.info(this.getClass().getName(), "Rename failed:"
								+ listFile[i].getName());
						if (!(listFile[i].delete())) {
							Logger.info(this.getClass().getName(),
									"Delete: deletion failed:"
											+ listFile[i].getName());
						}
					}
				}

				if (listFile[i].getName().toLowerCase().endsWith(".tosmsc")) {
					FileInputStream fin = new FileInputStream(listFile[i]);
					buffer = new byte[fin.available()];
					fin.read(buffer);
					ByteBuffer bf = new ByteBuffer();
					bf.appendBytes(buffer);
					pdu = pdu.createPDU(bf);
					// PDUData pdud = new PDUData();
					// pdud.setPDU(pdu);
					// pdud.setRequestID(datetime);
					toSMSC.enqueue(pdu);
					fin.close();
					boolean success = listFile[i].renameTo(new File(
							"./MOQueue/bak/" + listFile[i].getName() + ".bak"));
					if (!success) {
						Logger.info(this.getClass().getName(), "Rename failed:"
								+ listFile[i].getName());
						if (!(listFile[i].delete())) {
							Logger.info(this.getClass().getName(),
									"Delete: deletion failed:"
											+ listFile[i].getName());
						}
					}
				}

				if (listFile[i].getName().toUpperCase().endsWith(".RESPOND")) {
					FileInputStream fin = new FileInputStream(listFile[i]);
					buffer = new byte[fin.available()];
					fin.read(buffer);
					ByteBuffer bf = new ByteBuffer();
					bf.appendBytes(buffer); // appendBytes(buffer);
					pdu = pdu.createPDU(bf);
					resp.enqueue(pdu);
					fin.close();
					boolean success = listFile[i].renameTo(new File(
							"./MOQueue/bak/" + listFile[i].getName() + ".bak"));
					if (!success) {
						Logger.info(this.getClass().getName(), "Rename failed:"
								+ listFile[i].getName());
						if (!(listFile[i].delete())) {
							Logger.info(this.getClass().getName(),
									"Delete: deletion failed:"
											+ listFile[i].getName());
						}
					}
				}

				if (listFile[i].getName().toLowerCase().endsWith(".rsend")) {
					ems = readSMSObject(listFile[i]);
					if (ems != null) {
						ResendQueue.enqueue(ems);
					}
				}

				if (listFile[i].getName().toLowerCase().endsWith(".slog")) {
					ems = readSMSObject(listFile[i]);
					if (ems != null) {
						SendLogQueue.enqueue(ems);
					}
				}

				if (listFile[i].getName().toLowerCase().endsWith(".w4rt")) {
					ems = readSMSObject(listFile[i]);
					if (ems != null) {
						synchronized (wait4ResponseTable) {
							wait4ResponseTable.put((ems.getId().intValue())
									+ "", ems);
						}
					}
				}

				if (listFile[i].getName().toLowerCase().endsWith(".mt")) {
					ems = readSMSObject(listFile[i]);
					if (ems != null) {
						EMSQueue.enqueue(ems);
					}
				}

				if (listFile[i].getName().toLowerCase().endsWith(".modb")) {

					//ems = readSMSObject(listFile[i]);
					//DBTools.add2SMSReceiveQueueR(ems.getUserId(), ems
					//		.getServiceId(), ems.getMobileOperator(), ems
					//		.getCommandCode(), ems.getText(), ems
					//		.getsRequestID(),ems.ge);
				}

				if (listFile[i].getName().toLowerCase().endsWith(".cdr")) {

					EMSData sms = readSMSObject(listFile[i]);
					if ((sms.getMessageType() == 1)
							|| (sms.getMessageType() == 3)) {
						if (DBTools.add2CdrQueueb(sms, "1")) {
							Logger.info(this.getClass().getName(),
									"{CDR write=yes}{User_ID="
											+ sms.getUserId() + "}{Service_ID="
											+ sms.getServiceId()
											+ "}{MessageType=1}{RequestID="
											+ sms.getRequestId() + "}");
						} else {
							Logger.info(this.getClass().getName(),
									"{CDR write Err}{User_ID="
											+ sms.getUserId() + "}{Service_ID="
											+ sms.getServiceId()
											+ "}{MessageType=1}{RequestID="
											+ sms.getRequestId() + "}");
						}
					} else if ((sms.getMessageType() + "").startsWith("2")) {
						if (DBTools.add2CdrQueueb(sms, "0")) {
							Logger.info(this.getClass().getName(),
									"{CDR write=yes}{User_ID="
											+ sms.getUserId() + "}{Service_ID="
											+ sms.getServiceId()
											+ "}{MessageType=0}{RequestID="
											+ sms.getRequestId() + "}");
						} else {
							Logger.info(this.getClass().getName(),
									"{CDR write Err}{User_ID="
											+ sms.getUserId() + "}{Service_ID="
											+ sms.getServiceId()
											+ "}{MessageType=0}{RequestID="
											+ sms.getRequestId() + "}");
						}

					}
				}
			}

			while (resp.size() > 0) {
				PDU rsp = (PDU) resp.dequeue();
				responseQueue.enqueue(rsp);
			}
		}
		// End try
		catch (Exception ex) {
			ex.printStackTrace();
			Logger.error("Gateway", "Load queue files err: " + ex.getMessage());
		}
	}

	public static void exit() {
		// DBTools.ALERT(Preference.mobileOperator, "Gateway stop!",
		// Constants.ALERT_NONE, Preference.Channel
		// + "Gateway stopped"
		// , Preference.ALERT_CONTACT);

		running = false;
		if (bound || boundr) {
			unbind();
		}
		// Close all db connections in the pool.
		// closeAllConnectionInPool();
		// Save wait4Report_Table into file for loading
		// on the next startup.

		saveWait4ResponseTable();
		saveMO();
		Logger.info("Gateway", "Waiting all threads to die");
		int nLoop = 0;
		while (nLoop <= 5) {

			System.out.print(".");
			try {
				sleep(50);
				nLoop++;
			} catch (InterruptedException ex) {
			}
		}
		Logger.info("Gateway.exit", "Gateway stopped.");

	}

	// /////////////////////////////////////////////////////////////////////////

	public static void restartthreadloadmt(int i) {
		Logger.info("Gateway.thread", "getmt2emsqueue[" + i + "] is alive:"
				+ getmt2emsqueue[i].isAlive() + "@getmt2emsqueue[" + i
				+ "] is Interrupted:" + getmt2emsqueue[i].isInterrupted());
		Logger.info("Gateway.thread", "restart getmt2emsqueue[" + i + "]");
		getmt2emsqueue[i].stop();
		getmt2emsqueue[i] = new GetMt2EmsQueue(EMSQueue,
				Preference.nGetMTfromDB + "", i + "",getmt2emsqueue[i].iDB);
		getmt2emsqueue[i].setPriority(Thread.MAX_PRIORITY);
		getmt2emsqueue[i].start();
	}

	public static void restartthread_getmt(int i) {
		Logger.info("Gateway.thread", "getmt2emsqueue[" + i + "] is alive:"
				+ getmt2emsqueue[i].isAlive() + "@getmt2emsqueue[" + i
				+ "] is Interrupted:" + getmt2emsqueue[i].isInterrupted());
		Logger.info("Gateway.thread", "stop getmt2emsqueue[" + i + "]");
		getmt2emsqueue[i].stop();
		Logger.info("Gateway.thread", "start getmt2emsqueue[" + i + "]");
		getmt2emsqueue[i] = new GetMt2EmsQueue(EMSQueue,
				Preference.nGetMTfromDB + "", i + "",getmt2emsqueue[i].iDB);
		getmt2emsqueue[i].setPriority(Thread.MAX_PRIORITY);
		getmt2emsqueue[i].start();
	}

	public static void restartthread_smscsender(int i) {
		Logger.info("Gateway.thread", "smscsender[" + i + "] is alive:"
				+ smscsender[i].isAlive() + "@smscsender[" + i
				+ "] is Interrupted:" + smscsender[i].isInterrupted());
		Logger.info("Gateway.thread", "restart smscsender[" + i + "]");

		smscsender[i].stop();
		smscsender[i] = new SMSCSender(toSMSC, gateway);
		smscsender[i].setPriority(Thread.MAX_PRIORITY);
		smscsender[i].start();
	}

	public static void restartthread_emsbuilder(int i) {
		Logger.info("Gateway.thread", "emsbuilderfromqueue[" + i
				+ "] is alive:" + emsbuilderfromqueue[i].isAlive()
				+ "@emsbuilderfromqueue[" + i + "] is Interrupted:"
				+ emsbuilderfromqueue[i].isInterrupted());
		Logger.info("Gateway.thread", "restart emsbuilderfromqueue[" + i + "]");

		emsbuilderfromqueue[i].stop();
		emsbuilderfromqueue[i] = new EMSBuilderfromQueue(toSMSC, EMSQueue,
				wait4ResponseTable, BlackListQueue);
		emsbuilderfromqueue[i].setPriority(Thread.MAX_PRIORITY);
		emsbuilderfromqueue[i].start();
	}

	public static void restartthread_responseprocess(int i) {

		Logger.info("Gateway.thread", "responseprocessor[" + i + "] is alive:"
				+ responseprocessor[i].isAlive() + "@responseprocessor[" + i
				+ "] is Interrupted:" + responseprocessor[i].isInterrupted());
		Logger.info("Gateway.thread", "restart responseprocessor[" + i + "]");

		responseprocessor[i].stop();
		responseprocessor[i] = new ResponseProcessor(responseQueue,
				wait4ResponseTable, SendLogQueue, ResendQueue);
		responseprocessor[i].setPriority(Thread.NORM_PRIORITY);
		responseprocessor[i].start();
	}

	public static void restartthread_add2emssendlog(int i) {
		Logger.info("Gateway.thread", "add2emssendlog[" + i + "] is alive:"
				+ add2emssendlog[i].isAlive() + "@add2emssendlog[" + i
				+ "] is Interrupted:" + add2emssendlog[i].isInterrupted());
		Logger.info("Gateway.thread", "restart add2emssendlog[" + i + "]");

		add2emssendlog[i].stop();
		add2emssendlog[i] = new Add2EmsSendLog(SendLogQueue);
		add2emssendlog[i].setPriority(Thread.MIN_PRIORITY);
		add2emssendlog[i].start();
	}

	public static void restartthread_resenderrmt(int i) {
		Logger.info("Gateway.thread", "resenderrmt2telcos[" + i + "] is alive:"
				+ resenderrmt2telcos[i].isAlive() + "@resenderrmt2telcos[" + i
				+ "] is Interrupted:" + resenderrmt2telcos[i].isInterrupted());
		Logger.info("Gateway.thread", "restart resenderrmt2telcos[" + i + "]");

		resenderrmt2telcos[i].stop();
		resenderrmt2telcos[i] = new ResendErrMt2Telcos(ResendQueue, EMSQueue);
		resenderrmt2telcos[i].start();
	}

	public static void restartthread_checktimeout(int i) {

		Logger.info("Gateway.thread", "checktimeoutwait4response[" + i
				+ "] is alive:" + checktimeoutwait4response[i].isAlive()
				+ "@checktimeoutwait4response[" + i + "] is Interrupted:"
				+ checktimeoutwait4response[i].isInterrupted());
		Logger.info("Gateway.thread", "restart checktimeoutwait4response[" + i
				+ "]");

		checktimeoutwait4response[i].stop();
		checktimeoutwait4response[i] = new CheckTimeOutWait4Response(
				wait4ResponseTable, SendLogQueue);
		checktimeoutwait4response[i].start();
	}

	public static void restartthread_requestprocess(int i) {
		Logger.info("Gateway.thread", "requestprocessor[" + i + "] is alive:"
				+ requestprocessor[i].isAlive() + "@requestprocessor[" + i
				+ "] is Interrupted:" + requestprocessor[i].isInterrupted());
		Logger.info("Gateway.thread", "restart requestprocessor[" + i + "]");

		requestprocessor[i].stop();
		requestprocessor[i] = new RequestProcessor(requestQueue, toSMSC);
		requestprocessor[i].start();
	}

	public static void restartthread_smscreceiver(int i) {
		Logger.info("Gateway.thread", "smscreceiver[" + i + "] is alive:"
				+ smscreceiver[i].isAlive() + "@smscreceiver[" + i
				+ "] is Interrupted:" + smscreceiver[i].isInterrupted());
		Logger.info("Gateway.thread", "restart smscreceiver[" + i + "]");

		smscreceiver[i].stop();
		smscreceiver[i] = new SMSCReceiver(sessionr, requestQueue);
		smscreceiver[i].start();
	}

	public static void checkstatus_thread() {
		//

		if (Preference.bindMode.equalsIgnoreCase("t")
				|| Preference.bindMode.equalsIgnoreCase("tr")) {
			// OK
			for (int i = 0; i < Preference.nTheardMT; i++) {
				Logger.info("checkstatus_thread@" + "smscsender[" + i + "/"
						+ Preference.nTheardMT + "]", smscsender[i].getState()
						.toString());
				if (smscsender[i].isInterrupted() || !smscsender[i].isAlive()) {
					restartthread_smscsender(i);
					

					DBTools.ALERT("CheckStatusOfThread", "RestartThread",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ "restartthread_smscsender: " + i,
							Preference.ALERT_CONTACT);
				}
			}
			// OK
			for (int k = 0; k < Preference.nGetMTfromDB; k++) {

				Logger.info("checkstatus_thread@" + "getmt2emsqueue[" + k + "/"
						+ Preference.nGetMTfromDB + "]", getmt2emsqueue[k]
						.getState().toString());
				if (getmt2emsqueue[k].isInterrupted()
						|| !getmt2emsqueue[k].isAlive()) {
					restartthread_getmt(k);

					DBTools.ALERT("CheckStatusOfThread", "RestartThread",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ "restartthread_getmt: " + k,
							Preference.ALERT_CONTACT);
				}
			}
			// OK
			for (int n = 0; n < Preference.nBuildEMS; n++) {
				
				Logger.info("checkstatus_thread@" + "emsbuilderfromqueue[" + n
						+ "/" + Preference.nBuildEMS + "]",
						emsbuilderfromqueue[n].getState().toString());

				if (emsbuilderfromqueue[n].isInterrupted()
						|| !emsbuilderfromqueue[n].isAlive()) {
					restartthread_emsbuilder(n);

					DBTools.ALERT("CheckStatusOfThread", "RestartThread",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ "restartthread_emsbuilder: " + n,
							Preference.ALERT_CONTACT);

				}
			}
			// OK
			for (int j = 0; j < Preference.nTheardResp; j++) {
				Logger.info("checkstatus_thread@" + "responseprocessor[" + j
						+ "/" + Preference.nTheardResp + "]",
						responseprocessor[j].getState().toString());

				if (responseprocessor[j].isInterrupted()
						|| !responseprocessor[j].isAlive()) {
					restartthread_responseprocess(j);

					DBTools.ALERT("CheckStatusOfThread", "RestartThread",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ "restartthread_responseprocess: " + j,
							Preference.ALERT_CONTACT);

				}
			}
			// OK
			for (int l = 0; l < Preference.nSendLog; l++) {
				Logger.info("checkstatus_thread@" + "add2emssendlog[" + l + "/"
						+ Preference.nSendLog + "]", add2emssendlog[l]
						.getState().toString());

				if (add2emssendlog[l].isInterrupted()
						|| !add2emssendlog[l].isAlive()) {
					restartthread_add2emssendlog(l);

					DBTools.ALERT("CheckStatusOfThread", "RestartThread",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ "restartthread_add2emssendlog: " + l,
							Preference.ALERT_CONTACT);
				}

			}
			// OK
			if (resenderrmt2telcos[0].isInterrupted()
					|| !resenderrmt2telcos[0].isAlive()) {

				Logger.info("checkstatus_thread@" + "resenderrmt2telcos[" + 0
						+ "/" + 1 + "]", resenderrmt2telcos[0].getState()
						.toString());

				restartthread_resenderrmt(0);

				DBTools.ALERT("CheckStatusOfThread", "RestartThread",
						Constants.ALERT_SERIOUS, Preference.Channel
								+ "restartthread_resenderrmt: " + 0,
						Preference.ALERT_CONTACT);
			}

			// OK
			if (checktimeoutwait4response[0].isInterrupted()
					|| !checktimeoutwait4response[0].isAlive()) {
				Logger.info("checkstatus_thread@"
						+ "checktimeoutwait4response[" + 0 + "/" + 1 + "]",
						checktimeoutwait4response[0].getState().toString());
				restartthread_checktimeout(0);

				DBTools.ALERT("CheckStatusOfThread", "RestartThread",
						Constants.ALERT_SERIOUS, Preference.Channel
								+ "restartthread_checktimeout: " + 0,
						Preference.ALERT_CONTACT);
			}

		}
		if (Preference.bindMode.equalsIgnoreCase("r")
				|| Preference.bindMode.equalsIgnoreCase("tr")) {
			for (int k = 0; k < Preference.nTheardMO; k++) {
				// OK
				Logger.info("checkstatus_thread@" + "requestprocessor[" + k
						+ "/" + Preference.nTheardMO + "]", requestprocessor[k]
						.getState().toString());
				if (requestprocessor[k].isInterrupted()
						|| !requestprocessor[k].isAlive()) {
					restartthread_requestprocess(k);

					DBTools.ALERT("CheckStatusOfThread", "RestartThread",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ "restartthread_requestprocess: " + k,
							Preference.ALERT_CONTACT);
				}

			}
			if (!Preference.asynchronous) {
				// OK
				if (smscreceiver[0].isInterrupted()
						|| !smscreceiver[0].isAlive()) {

					Logger.info("checkstatus_thread@" + "smscreceiver[" + 0
							+ "/" + 0 + "]", smscreceiver[0].getState()
							.toString());
					restartthread_smscreceiver(0);

					DBTools.ALERT("CheckStatusOfThread", "RestartThread",
							Constants.ALERT_SERIOUS, Preference.Channel
									+ "restartthread_smscreceiver: " + 0,
							Preference.ALERT_CONTACT);
				}
			}

		}

	}

	// ///////
}
