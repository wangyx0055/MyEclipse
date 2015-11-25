package icom;

import icom.common.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import multiService.HandleMultiService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import tracuudiemthi.DiemThi;
import tracuudiemthi.DiemThi_ThanhCong;

import PushMT.PushMt;
import daily.Delivery_daily;
import dailyAll.DeliveryAllDay;
import dailymulti.Delivery_daily_multi;
import dbSynch.SynVasgate;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * Day la class chay dau tien cua ca truong chinh
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class Sender extends Thread {
	// static BufferedReader keyboard = new BufferedReader(new
	// InputStreamReader(
	// System.in));
	public static boolean getData = true;
	public static boolean processData = true;
	public static DBPool dbpool = null;
	public static Util util = new Util();
	private Map mapResponse = new HashMap();
	MsgQueue queueResponse = new MsgQueue();
	MsgQueue queueResponseReceive = new MsgQueue();
	MsgQueue queueTimeout = new MsgQueue();
	MsgQueue queueRequest = new MsgQueue();
	MsgQueue queue = new MsgQueue();
	MsgQueue queueMO1 = new MsgQueue();
	public static MsgQueue queueUpdate = new MsgQueue();
	// MsgQueue mtqueue = new MsgQueue();
	MsgQueue queueResend = new MsgQueue();
	// MsgQueue mtqueueResend = new MsgQueue();
	MsgQueue queueLog = new MsgQueue();
	MsgQueue queueEvent = new MsgQueue();
	// chitd add them code thuc thi share cac dau mtqueue
	public static MsgQueue msgPushMTQueue = new MsgQueue();
	// end chitd
	// MsgQueue mtqueueLog = new MsgQueue();
	LoadMO[] loadMO = new LoadMO[2];
	LoadMT[] loadMT = new LoadMT[2];
	LoadMO1[] loadMO1 = new LoadMO1[2];
	LoadMTSendQueue[] loadMTSendQueue = new LoadMTSendQueue[2];
	// LoadMTVNNLINKS loadmtvnnlinks = null;
	// LoadMTXS loadmtxs = null;

	public static Logger logger = null;
	ExecuteQueue[] executequeue = new ExecuteQueue[20];
	ExecuteQueueMO1[] executequeueMO1 = new ExecuteQueueMO1[5];
	ExecuteReSendQueue executeReSendQueue = null;
	public static LoadConfig loadconfig = null;
	public static LoadRepeat loadrepeat = null;
	ExecuteInsertSendLog[] insert_send_log = new ExecuteInsertSendLog[1];
	Delivery_daily[] deliverydaily = new Delivery_daily[2];
	Delivery_daily_multi[] deliverydaily_multi = new Delivery_daily_multi[2];
	public static boolean bound;
	static String VERSION = "2012.06.01.12.30";
	public static boolean LOADED = false;
	CheckQueueStatusThread checkqueue = null;

	HandleMultiService handleMultiService = null;

	// DeliveryAllDay deliveryAll = new DeliveryAllDay();

	/* Ham khoi tao sender */
	public Sender() {
		// Lay thong tin tu file config va load cac thong tin vao cac bien toan
		// cuc (static)
		// trong lop Constants
		Constants.loadProperties("config.cfg");

		// Tao mot mang ExecuteQueue voi so luong thread lay trong file config

		executequeue = new ExecuteQueue[Constants.NUM_THREAD];

		// execute_mtqueue = new ExecuteMTQueue[Constants.NUM_THREAD];
		// Tao mang LoadMO voi so luong thread lay trong file config
		// Xu ly tin nhan MO trong table mo_queue
		loadMO = new LoadMO[Constants.NUM_THREAD_LOAD_MO];
		// Tao mang LoadMT voi so luong thread lay trong file config
		// Xu ly tin nhan MT trong table mt_queue
		loadMT = new LoadMT[Constants.NUM_THREAD_LOAD_MT];
		// vms_charge_online
		loadMTSendQueue = new LoadMTSendQueue[Constants.NUM_THREAD_LOAD_MT];
		insert_send_log = new ExecuteInsertSendLog[Constants.NUM_THREAD_INSERTLOG];
		deliverydaily = new Delivery_daily[Constants.NUM_THREAD_DELIVERY];
		deliverydaily_multi = new Delivery_daily_multi[Constants.NUM_THREAD_DELIVERY];
		// insert_send_mtlog = new
		// ExecuteInsertMTSendLog[Constants.NUM_THREAD_INSERTLOG];
		loadconfig = new LoadConfig();
		try {
			logger = new Logger();
			try {
				logger.setLogWriter(Constants.LOGPATH + Constants.LOGFILE);
			} catch (IOException ex) {
			}
			logger.setLogLevel(Constants.LOGLEVEL);
			logger.info("Start :" + VERSION);
			util = new Util(Constants.LOGPATH + Constants.LOGFILE,
					Constants.LOGLEVEL);
			// Bat dau chay ham khoi tao
			Init();
		} catch (Exception e) {
			logger.error("Error : " + e.toString());
			e.printStackTrace();
		}
	}

	private void Init() throws Exception {
		dbpool = new DBPool();
		DBPool.ConfigDB();
		loadSMSDataTable("data.dat", queue);
		loadSMSDataTable("resend.dat", queueResend);
		loadSMSDataTable("log.dat", queueLog);
		loadSMSDataTable("response.dat", queueResponse);
		loadSMSDataTable("mo1.dat", queueMO1);
		loadSMSDataTable("request.dat", queueRequest);
		loadSMSDataTable("responsereq.dat", queueResponseReceive);
		loadSMSDataTable("msgpushmtqueue.dat", msgPushMTQueue);
		loadSMSDataTable("queueupdate.dat", queueUpdate);

		Util.logger.info("Start loading Config");
		loadconfig.start();
		while (!loadconfig.LOADED) {
			sleep(50);
		}
		if ("1".equalsIgnoreCase(Constants.BLACKLIST)) {
			loadrepeat = new LoadRepeat();
			loadrepeat.setPriority(Thread.MAX_PRIORITY);
			loadrepeat.start();
			while (!loadrepeat.isLoaded) {
				try {
					System.out.print(".");
					sleep(50);
				} catch (InterruptedException e) {
					Util.logger.error("Error: " + e.getMessage());
				}
			}
		}
		/*****
		 * Load thong tin free vao vtService *
		 **/
		LoadConfig.hServices = Services.getInfo();
		// Util.logger.info("size=" + Sender.loadconfig.hServices.size());
		if (!Services.loaded) {
			Util.logger.error("Sender: load thong tin tu bang servies bi loi!");
			Util.logger.error("Sender: Khong start duoc process!");
			return;
		}

		// icom.Services s = icom.Services.getService("VANSU",
		// Sender.loadconfig.hServices);
		// Util.logger.info("from_date_free" + s.getFromDateFree()+
		// "to_date_free" + s.getToDateFree() + "number_free" +
		// s.getNumberFree()+ "active_free" + s.getActiveFree());
		// if(1==1)return;
		// System.out.println("Start load mo");

		// Packet
		// ExecuteSubChargeResult loadChargeResult = new
		// ExecuteSubChargeResult();
		// loadChargeResult.start();
		//		
		// ExecuteReChargeResultVMS reChargeRsl = new
		// ExecuteReChargeResultVMS();
		// reChargeRsl.start();

		Util.logger.info("Start load MO");
		for (int i = 0; i < loadMO.length; i++) {
			loadMO[i] = new LoadMO(queue, loadMO.length, i);
			loadMO[i].setPriority(Thread.MAX_PRIORITY);
			loadMO[i].start();
		}

		// Multi Service - DanND
		handleMultiService = new HandleMultiService(queue);
		handleMultiService.setPriority(Thread.MAX_PRIORITY);
		handleMultiService.start();

		if (Constants.TEST == 0) {
			if (Constants.TELCOMODE != 1) {
				Util.logger.info("Start loading MT");
				for (int i = 0; i < loadMT.length; i++) {
					loadMT[i] = new LoadMT(queue, mapResponse, loadMT.length, i);
					loadMT[i].setPriority(Thread.MAX_PRIORITY);
					loadMT[i].start();
				}
			}
		}
		if (Constants.TEST == 0) {
			if (Constants.TELCOMODE != 1) {
				// System.out.println("Start load mt send queue");
				Util.logger.info("Start load mt send queue");
				for (int i = 0; i < loadMTSendQueue.length; i++) {
					loadMTSendQueue[i] = new LoadMTSendQueue(queue,
							loadMT.length, i);
					loadMTSendQueue[i].setPriority(Thread.MAX_PRIORITY);
					loadMTSendQueue[i].start();
				}
			}
		}
		// System.out.println("Start executequeue");
		Util.logger.info("Start executequeue");
		for (int i = 0; i < executequeue.length; i++) {
			executequeue[i] = new ExecuteQueue(queue, queueLog, i);
			executequeue[i].setPriority(Thread.MAX_PRIORITY);
			executequeue[i].start();
		}
		// System.out.println("Start insert log");
		Util.logger.info("Start insert log");
		for (int i = 0; i < insert_send_log.length; i++) {
			insert_send_log[i] = new ExecuteInsertSendLog(queueLog);
			insert_send_log[i].setPriority(Thread.NORM_PRIORITY);
			insert_send_log[i].start();
		}
		if (Constants.TEST == 0) {
			executeReSendQueue = new ExecuteReSendQueue(queueResend, 0);
			executeReSendQueue.setPriority(Thread.NORM_PRIORITY);
			executeReSendQueue.start();
		}
		System.out.println("Start deliverydaily");
		Util.logger.info("Start deliverydaily");
		for (int i = 0; i < deliverydaily.length; i++) {
			deliverydaily[i] = new Delivery_daily(deliverydaily.length, i);
			deliverydaily[i].setPriority(Thread.MAX_PRIORITY);
			deliverydaily[i].start();
		}
		// chitd them share mtqueue
		String[] arrMtQueue = Constants.MTQUEUEPUSH.split(";");
		PushMt[] pushMt = new PushMt[arrMtQueue.length
				* Constants.MTTOTALTHREAD];
		for (int i = 0; i < arrMtQueue.length; i++) {
			for (int k = 0; k < Constants.MTTOTALTHREAD; k++) {
				pushMt[k] = new PushMt(Integer.parseInt(arrMtQueue[i]),
						msgPushMTQueue);
				pushMt[k].setPriority(Thread.MAX_PRIORITY);
				pushMt[k].start();
			}
		}
		if (Constants.MODEDIEMTHI == 1) {
			DiemThi[] diemThiArr = new DiemThi[Constants.NUMBERTHREADSENDDIEMTHI];
			for (int i = 0; i < diemThiArr.length; i++) {
				diemThiArr[i] = new DiemThi(i, diemThiArr.length);
				diemThiArr[i].setPriority(Thread.MAX_PRIORITY);
				diemThiArr[i].start();
			}
			// them class diem thi dang ky thanh cong
			DiemThi_ThanhCong[] diemThiRegisterArr = new DiemThi_ThanhCong[Constants.NUMBERTHREADSENDREGISTERSUCCESS];
			for (int i = 0; i < diemThiRegisterArr.length; i++) {
				diemThiRegisterArr[i] = new DiemThi_ThanhCong(i,
						diemThiRegisterArr.length);
				diemThiRegisterArr[i].setPriority(Thread.MAX_PRIORITY);
				diemThiRegisterArr[i].start();
			}
		}
		// end chitd

		// // System.out.println("Start deliverydaily multi");
		// Util.logger.info("Start deliverydaily multi");
		// for (int i = 0; i < deliverydaily_multi.length; i++)
		// {
		/*
		 * deliverydaily_multi[i] = new Delivery_daily_multi(
		 * deliverydaily_multi.length, i);
		 * deliverydaily_multi[i].setPriority(Thread.MAX_PRIORITY);
		 * deliverydaily_multi[i].start(); }
		 */
		if (Constants.MO1 == 1) {
			// System.out.println("Start load mo1");
			Util.logger.info("Start load mo1");
			for (int i = 0; i < loadMO1.length; i++) {
				loadMO1[i] = new LoadMO1(queueMO1, loadMO1.length, i);
				loadMO1[i].setPriority(Thread.MAX_PRIORITY);
				loadMO1[i].start();
			}
			// System.out.println("Start executequeue MO1");
			Util.logger.info("Start executequeue MO1");
			for (int i = 0; i < executequeueMO1.length; i++) {
				executequeueMO1[i] = new ExecuteQueueMO1(queueMO1, i);
				executequeueMO1[i].setPriority(Thread.MAX_PRIORITY);
				executequeueMO1[i].start();
			}
		}
		LOADED = true;
		// System.out.println("Start CheckQueueStatusThread");
		Util.logger.info("Start CheckQueueStatusThread");
		checkqueue = new CheckQueueStatusThread(mapResponse, queueResponse,
				queueRequest, queue, queueMO1, queueRequest, queueLog);
		checkqueue.start();

		// deliveryAll.start();

		try {
			Thread.sleep(1000 * 3);
		} catch (InterruptedException ex) {
			System.out.println(" \nFunction init - Thread can't sleep : "
					+ ex.toString());
		}

	}

	public void windowClosing() {
		int nCount = 0;
		getData = false;
		processData = false;
		System.out.print("\nWaiting .....");
		Util.logger.info("Waiting .....");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			System.out
					.println("\nFunction windowClosing: Thread can't sleep : "
							+ ex.toString());
		}
		while ((queue.getSize() > 0) && nCount < 5) {
			nCount++;
			try {
				// System.out.println("...Queue(" + queue.getSize() + ")");
				Util.logger.info("...Queue(" + queue.getSize() + ")");
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				System.out.println(ex.toString());
			}
		}
		savemapResponse();
		saveSMSDataTable("data.dat", queue);
		saveSMSDataTable("resend.dat", queueResend);
		saveSMSDataTable("log.dat", queueLog);
		saveSMSDataTable("response.dat", queueResponse);
		saveSMSDataTable("mo1.dat", queueMO1);
		saveSMSDataTable("request.dat", queueRequest);
		saveSMSDataTable("responsereq.dat", queueResponseReceive);
		saveSMSDataTable("msgpushmtqueue.dat", msgPushMTQueue);
		saveSMSDataTable("queueupdate.dat", queueUpdate);

		Util.logger.info("Shutdown");
		System.out.print("\nExit");
	}

	public static MsgObject readSMSObject(File file) {
		FileInputStream fin = null;
		ObjectInputStream objIn = null;
		MsgObject object = null;
		try {
			fin = new FileInputStream(file);
			objIn = new ObjectInputStream(fin);
			object = (MsgObject) objIn.readObject();
			return object;
		} catch (ClassNotFoundException ex1) {
			return null;
		} catch (IOException ex) {
			Util.logger.error("ReadData: " + ex.getMessage());
			return null;
		} finally {
			try {
				objIn.close();
				fin.close();
				boolean success = file.renameTo(new File("queue/bak/"
						+ file.getName() + ".bak"));
				if (!success) {
					Util.logger.error("Rename failed:" + file.getName());
					if (!(file.delete())) {
						Util.logger.error("Delete: deletion failed:"
								+ file.getName());
					}
				}
				return object;
			} catch (IOException ex) {
				Util.logger.error("Save data error can not close file: -> "
						+ ex.getMessage());
			}
		}
	}

	public void loadmapResponse() {
		try {
			File f = new File("queue/");
			File[] listFile = f.listFiles();
			int numOfFile = 0;
			MsgObject obj = null;
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].getName().toLowerCase().endsWith(".w4rt")) {
					obj = readSMSObject(listFile[i]);
					if (obj != null) {
						synchronized (mapResponse) {
							mapResponse.put(obj.getMsg_id() + "", obj);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void savemapResponse() {
		Util.logger.info("Saving mapResponse...");
		synchronized (mapResponse) {
			if (mapResponse == null || mapResponse.size() == 0) {
				return;
			}
			int nTimeout = 0;
			try {
				if (mapResponse.size() > 0) {
					// MsgObject msgobj = (MsgObject) mapResponse.get(key);
					for (Enumeration e = new Vector(mapResponse.keySet())
							.elements(); e.hasMoreElements();) {
						String key = (String) e.nextElement(); // key=messageId
						MsgObject msgObj = (MsgObject) mapResponse.get(key);
						if (msgObj != null) {
							saveSMSObject(nTimeout + ".w4rt", msgObj);
							nTimeout++;
							continue;
						}
					}
				}
			} catch (Exception ex) {
				Util.logger.error("IOException:Loi luu file tu mapResponse: "
						+ ex.getMessage());
			}
		}
	}

	public static void saveSMSObject(String sfile, MsgObject object) {
		Util.logger.info(" Saving MsgObject into file " + sfile);
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Date date = new java.util.Date();
		String datetime = dateFormat.format(date);
		try {
			fout = new java.io.FileOutputStream("queue/" + datetime + sfile);
			objOut = new ObjectOutputStream(fout);
			objOut.writeObject(object);
			objOut.flush();
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

	public static void loadSMSDataTable(String fileName, MsgQueue queue) {
		boolean flag = true;
		FileInputStream fin = null;
		ObjectInputStream objIn = null;
		FileOutputStream fout = null;
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
		System.out.println("Starting process - version " + VERSION);
		System.out
				.println("Copyright 2006-2010 VietNamNet ICom - All Rights Reserved.");
		Sender mosender = new Sender();
		ShutdownInterceptor shutdownInterceptor = new ShutdownInterceptor(
				mosender);
		Runtime.getRuntime().addShutdownHook(shutdownInterceptor);
		mosender.start();
	}

	@Override
	public void run() {
		Util.logger.info("Version " + VERSION);
		System.out.println("Version " + VERSION);
		if (Constants.RUNCLASS != null) {
			for (int i = 0; i < Constants.RUNCLASS.length; i++) {
				runthread(Constants.RUNCLASS[i]);
			}
		}
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
				Util.logger.error(e.toString());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				Util.logger.error(e.toString());
				e.printStackTrace();
			}
			Thread delegate = (Thread) delegateObject;
			delegate.start();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Util.logger.error(e.toString());
		}
	}

	public static void rebind() {
		Util.logger.info("rebind ");
		bound = true;
	}

	public static void bind() {
		bound = true;
	}
}
