package com.vmg.sms.process;

//import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException; //import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

//import sun.misc.AtomicLong;

import java.util.concurrent.atomic.AtomicLong;

import com.vmg.sms.common.Util;
import com.vmg.soap.mo.DummySSLInitializer;

/**
 * <p>
 * Title:
 * </p>
 * 
 * Description: </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class ConsoleSRV extends Thread {
	// static BufferedReader keyboard = new BufferedReader(new
	// InputStreamReader(
	// System.in));
	public static boolean getData = true;
	public static boolean processData = true;

	public static boolean isAllThreadStarted = false;

	public static DBPool dbpool = null;
	public static LoadConfig loadconfig = null;
	public static Util util = new Util();
	public static MsgQueue queue = new MsgQueue();
	public static MsgQueue queueLog = new MsgQueue();
	public static LoadMO[] loadMO = new LoadMO[2];
	public static Logger logger = null;
	public static ExecuteQueue[] executequeue = new ExecuteQueue[20];

	public static ExecuteInsertReceiveLog[] insert_receive_log = new ExecuteInsertReceiveLog[1];

	static String VERSION = "2009.06.16.12";

	// bo dem

	private static AtomicLong[] moload = { new AtomicLong(0),
			new AtomicLong(0), new AtomicLong(0), new AtomicLong(0) };
	private static AtomicLong[] moprocess = { new AtomicLong(0),
			new AtomicLong(0), new AtomicLong(0), new AtomicLong(0) };

	// public static String[] telcos = { "VIETTEL", "GPC", "VMS", "EVN" };

	/*
	 * history 0706: khog gui dc sang ws thi luu vao bang sms_receive_error thay
	 * doi thu tuc getkeyword 1106: Bo xung tham so INV_INFO, dua vao trong
	 * class InvalidSMS 2006: Neu check ma ko duoc thi thuc hien bo cac khoang
	 * trang
	 */

	public ConsoleSRV() {
		// Constants.loadProperties("param.cfg");
		//DummySSLInitializer.initDummySsl();
		Constants.loadProperties("config.cfg");
		executequeue = new ExecuteQueue[Constants.NUM_THREAD];
		loadMO = new LoadMO[Constants.NUM_THREAD_LOAD_MO];
		insert_receive_log = new ExecuteInsertReceiveLog[Constants.NUM_THREAD_INSERTLOG];

		moload = new AtomicLong[Constants.TELCOS.length];
		moprocess = new AtomicLong[Constants.TELCOS.length];
		for (int i = 0; i < Constants.TELCOS.length; i++) {
			moload[i] = new AtomicLong();
			moprocess[i] = new AtomicLong();
		}

		try {
			logger = new Logger();
			try {
				logger.setLogWriter(Constants.LOGPATH + Constants.LOGFILE);
			} catch (IOException ex) {
			}
			logger.setLogLevel(Constants.LOGLEVEL);
			logger.info("Start :" + VERSION);
			System.out.println("Start :" + VERSION);
			util = new Util(Constants.LOGPATH + Constants.LOGFILE,
					Constants.LOGLEVEL);
			Init();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void Init() throws Exception {

		dbpool = new DBPool();
		dbpool.ConfigDB();

		// loadSMSDataTable("data.dat", queue);

		loadconfig = new LoadConfig();
		loadconfig.setPriority(Thread.MAX_PRIORITY);
		loadconfig.start();

		System.out.println("Loading...");
		while (!loadconfig.isLoaded) {
			try {
				sleep(50);
				System.out.print(".");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Loaded.");

		loadSMSDataTable("data.dat", queue);
		loadSMSDataTable("receivelog.dat", queueLog);

		System.out.println("Start: LoadMO");
		for (int i = 0; i < loadMO.length; i++) {
			loadMO[i] = new LoadMO(queue, loadMO.length, i);
			loadMO[i].setPriority(Thread.MAX_PRIORITY);
			loadMO[i].start();

		}

		System.out.println("Start: ExecuteQueue");
		for (int i = 0; i < executequeue.length; i++) {
			executequeue[i] = new ExecuteQueue(queue, queueLog, i);
			executequeue[i].setPriority(Thread.MAX_PRIORITY);
			executequeue[i].start();

		}
		System.out.println("Start: ExecuteInsertReceiveLog");
		for (int i = 0; i < insert_receive_log.length; i++) {
			insert_receive_log[i] = new ExecuteInsertReceiveLog(queueLog);
			insert_receive_log[i].setPriority(Thread.NORM_PRIORITY);
			insert_receive_log[i].start();
		}

		isAllThreadStarted = true;

		// System.out.println("Start: CheckStatusThread");

		// checkstatusthread = new CheckStatusThread();
		// checkstatusthread.start();

		// trafficthread = new TrafficWatcherThread();
		// trafficthread.start();

	}

	public void windowClosing() {
		int nCount = 0;
		getData = false;
		processData = false;

		System.out.print("\nWaiting .....");
		Util.logger.info("\nWaiting .....");

		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			System.out.println(ex.toString());
		}

		while ((queue.getSize() > 0) && nCount < 5) {
			nCount++;
			try {
				System.out.println("...Queue(" + queue.getSize() + ")");

				Thread.sleep(100);
			} catch (InterruptedException ex) {
				System.out.println(ex.toString());
			}
		}

		Util.logger.info("saveSMSDataTable(data.dat");
		System.out.println("saveSMSDataTable(data.dat");
		saveSMSDataTable("data.dat", queue);

		Util.logger.info("saveSMSDataTable(receivelog.dat");
		System.out.println("saveSMSDataTable(receivelog.dat");
		saveSMSDataTable("receivelog.dat", queueLog);

		Util.logger.info("Shutdown");

		System.out.print("\nExit");

	}

	public static void loadSMSDataTable(String fileName, MsgQueue queue) {

		boolean flag = true;
		FileInputStream fin = null;
		ObjectInputStream objIn = null;
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		Util.logger.info("loadSMSDataTable:" + fileName);
		long nummo = 0;
		try {

			fin = new java.io.FileInputStream(fileName);
			objIn = new ObjectInputStream(fin);

			while (flag) {
				try {
					MsgObject object = (MsgObject) objIn.readObject();
					queue.add(object);
					nummo++;

				} catch (Exception ex) {
					flag = false;
				}
			}
			if (nummo == 0) {
				Util.logger.info(fileName + " is empty");
			} else {
				Util.logger.info("Load data successful: " + nummo + " MO");
			}

		} catch (IOException ex) {
			Util.logger.error("Load data error: " + ex.getMessage());
		} finally {
			try {
				fin.close();
				fout = new java.io.FileOutputStream(fileName, false); // append
				// =
				// false
				fout.close();
				Util.logger.info("Deleting.....: " + fileName);
			} catch (Exception ex) {
			}
		}

	}

	public static void saveSMSDataTable(String fileName, MsgQueue queue) {
		Util.logger.info("Saving " + fileName + " . . .");
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		long numqueue = 0;

		try {
			fout = new java.io.FileOutputStream(fileName, false); // append =
			// false
			objOut = new ObjectOutputStream(fout);
			for (Enumeration e = queue.getVector().elements(); e
					.hasMoreElements();) {
				MsgObject object = (MsgObject) e.nextElement();
				objOut.writeObject(object);
				objOut.flush();
				numqueue++;
			}
			Util.logger.info("complete:" + numqueue);
		} catch (IOException ex) {
			Util.logger.error("Save data error: " + ex.getMessage());
		} finally {
			try {
				objOut.close();
				fout.close();
			} catch (IOException ex) {
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("Starting ProcessServer - version " + VERSION);
		System.out
				.println("Copyright 2006-2008 VietNamNet ICom Jsc. - All Rights Reserved.");
		ConsoleSRV smsConsole = new ConsoleSRV();
		ShutdownInterceptor shutdownInterceptor = new ShutdownInterceptor(
				smsConsole);
		Runtime.getRuntime().addShutdownHook(shutdownInterceptor);
		smsConsole.start();

	}

	public void run() {

		Util.logger.info("Version " + VERSION);

		if (Constants.RUNCLASS != null) {
			for (int i = 0; i < Constants.RUNCLASS.length; i++) {
				runthread(Constants.RUNCLASS[i]);
			}

		}
		System.out.println("Version " + VERSION);

	}

	private void runthread(String classname) {
		Class delegateClass;
		try {
			delegateClass = Class.forName(classname);
			Util.logger.info("{runthread}{Start:" + classname + "}");
			Object delegateObject = null;
			try {
				delegateObject = delegateClass.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				Util.logger.error(e.toString());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				Util.logger.error(e.toString());
				e.printStackTrace();
			}
			Thread delegate = (Thread) delegateObject;

			delegate.start();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Util.logger.error(e.toString());

		}

	}

	public static void checkstatus_thread() {

		try {

			for (int i = 0; i < loadMO.length; i++) {

				if (loadMO[i].isInterrupted() || !loadMO[i].isAlive()) {
					restartthread_loadmo(i);
				}

			}

			for (int i = 0; i < executequeue.length; i++) {
				if (executequeue[i].isInterrupted()
						|| !executequeue[i].isAlive()) {
					restartthread_executequeue(i);
				}

			}
			for (int i = 0; i < insert_receive_log.length; i++) {

				if (insert_receive_log[i].isInterrupted()
						|| !insert_receive_log[i].isAlive()) {
					restartthread_insert_receive_log(i);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Util.logger.error(e.toString());
		}

	}

	public static void restartthread_insert_receive_log(int i) {
		Util.logger.info("insert_receive_log[" + i + "] is alive:"
				+ insert_receive_log[i].isAlive() + "@insert_receive_log[" + i
				+ "] is Interrupted:" + insert_receive_log[i].isInterrupted());
		Util.logger.info("restart insert_receive_log[" + i + "]");
		insert_receive_log[i].stop();
		insert_receive_log[i] = new ExecuteInsertReceiveLog(queueLog);
		insert_receive_log[i].setPriority(Thread.NORM_PRIORITY);
		insert_receive_log[i].start();
	}

	public static void restartthread_executequeue(int i) {
		Util.logger.info("executequeue[" + i + "] is alive:"
				+ executequeue[i].isAlive() + "@executequeue[" + i
				+ "] is Interrupted:" + executequeue[i].isInterrupted());
		Util.logger.info("restart executequeue[" + i + "]");
		executequeue[i].stop();
		executequeue[i] = new ExecuteQueue(queue, queueLog, i);
		executequeue[i].setPriority(Thread.MAX_PRIORITY);
		executequeue[i].start();
	}

	public static void restartthread_loadmo(int i) {
		Util.logger.info("loadMO[" + i + "] is alive:" + loadMO[i].isAlive()
				+ "@loadMO[" + i + "] is Interrupted:"
				+ loadMO[i].isInterrupted());
		Util.logger.info("restart loadMO[" + i + "]");
		loadMO[i].stop();
		loadMO[i] = new LoadMO(queue, loadMO.length, i);
		loadMO[i].setPriority(Thread.MAX_PRIORITY);
		loadMO[i].start();
	}

	public static void incrementAndGet_load(String operator) {

		String[] telcos = Constants.TELCOS;
		for (int i = 0; i < telcos.length; i++) {
			if (operator.equalsIgnoreCase(telcos[i])) {
				moload[i].incrementAndGet();
			}
		}

	}

	public static void incrementAndGet_process(String operator) {
		String[] telcos = Constants.TELCOS;

		for (int i = 0; i < telcos.length; i++) {
			if (operator.equalsIgnoreCase(telcos[i])) {
				moprocess[i].incrementAndGet();
			}
		}

	}

	// getAndSet(0)

	public static long getAndSet_process(String operator) {
		String[] telcos = Constants.TELCOS;
		for (int i = 0; i < telcos.length; i++) {
			if (operator.equalsIgnoreCase(telcos[i])) {
				return moprocess[i].getAndSet(0);
			}
		}
		return 0;

	}

	public static long getAndSet_load(String operator) {
		String[] telcos = Constants.TELCOS;
		for (int i = 0; i < telcos.length; i++) {
			if (operator.equalsIgnoreCase(telcos[i])) {
				return moload[i].getAndSet(0);
			}
		}
		return 0;
	}

}
