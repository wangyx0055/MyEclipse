package icom;

import icom.common.Util;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;


import daugia.DGConstants;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
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
public class Constants {

	// Constants cua VMS
	public static final String StringSplit = ":";
	public static String PROMO_DATE = "'2007-12-20 00:00:01'";

	// Xu ly ben Icom neu TELCOMODE = 0
	// NEu bang 1 thi o Beeline va khong load MO len nua
	public static int TELCOMODE = 1;
	public static int MO1 = 0;
	public static int TEST = 0;

	public static int LUCKYNUMBER = 1;
	// che do gui lai
	public static int MODE_NORMAL = 0;
	public static int MODE_NOTCHARGE = 1;
	public static int MODE_RESENDFAIL = 2;
	// che do blacklist

	public static String BLACKLIST = "1";
	public static int RECEIVE_MO_MODE = 1;// 0:webservice, 1:jms
	// service type
	public static int PACKAGE_SERVICE = 1;
	public static int DAILY_SERVICE = 0;

	public static int CP_ID = 6;

	// Load MT len bang nay truoc
	
	// Bang nay dung de luu thong tin khi insert vao khi tra ban tin hang ngay
	public static String tblVMSChargeOnline = "vms_charge_online";
	public static String tblMTTimeout = "mt_timeout";
	public static String tblCharge = "vms_charge";
	
	// Neu tra thong tin luon thi vao bang nay
	public static String tblMTQueue = "mt_queue";
	public static String tblMOQueue = "mo_queue";
	public static String tblMO1Queue = "mo1_queue";
	public static String tblKeyword = "keywords";
	public static String tblCP = "cp";
	
	// Che do khuyen mai: 0:
	public static int MODE_ADV = 0;
	// Free hoan toan
	public static String FREE_ALL = "0";
	// 0: theo MT
	// 1: theo ngay dang ky
	public static String MODE_PROMO = "1";

	public static int TIME_REBIND = 10;
	public static int NUM_THREAD = 10;
	public static int NUM_THREAD_SEND_REQUEST = 10;
	public static int NUM_THREAD_RESPONSE_REQUEST = 10;
	public static int NUM_THREAD_LOAD_MO = 2;
	public static int NUM_THREAD_LOAD_MT = 2;
	public static int NUM_THREAD_INSERTLOG = 1;

	public static int NUM_THREAD_DELIVERY = 5;

	public static int MAX_RETRIES = 3;
	public static String LOAD_MO_MODE = "DB";
	public static String MO_DIR = "Z:/";
	public static String LOGFILE = "${yyyy-MM-dd}.log";
	public static String LOGPATH = "log/";

	public static String LOGLEVEL = "info,warn,error,crisis";
	public static String[] RUNCLASS = null;

	public static int TIME_DELAY_LOAD_MO = 100;

	public static Properties _prop;

	public static String MT_CHARGING = "1";
	public static String MT_NOCHARGE = "0";
	public static String MT_PUSH = "3";
	public static String MT_REFUND = "2";
	public static String MT_REFUND_SYNTAX = "21";
	public static String MT_REFUND_CONTENT = "22";

	public static String INV_MO_CLASS = "services.Help";
	public static String INV_MT_CLASS = "icom.SoapQM";
	public static String INV_KEYWORD = "INV";
	public static String INV_PARTNER = "INVALID";
	public static int INV_CP = 0;

	public static String ACTIVE_VNL = "1";
	public static String ACTIVE_LOTTERY = "1";
	public static int RETRIES_NUM = 3;

	// process result
	public static int RET_OK = 1;
	public static int RET_PROCESS_ERROR = 2;
	public static int RET_INGW_ERROR = 3;
	public static int RET_BLACKLIST = 4;
	public static int RET_TIMEOUT = 5;
	public static int RET_FAILED = 6;
	public static int RET_SEND_FAILED = 7;
	
	//dau gia
	public static String GOLD_HOUR_BEGIN ="12:00";
	public static String GOLD_HOUR_END ="13:00";

	// ma chung khoan

	public static String[] STOCK_SYMBOLS = null;
	// time out
	public static int TIME_OUT = 1;

	// ket qua thuc hien cac cron

	public static int DELIVER_OK = 1;
	public static int DELIVER_NOTRUN = 0;
	public static int DELIVER_FAILED = 2;
	public static int DELIVER_RUNNING = 3;

	// ////////////////////////// xoso
	public static int THUDO_COMPANY_ID = 1;
	public static int BINHDUONG_COMPANY_ID = 11;
	public static int SAIGON_COMPANY_ID = 131;
	public static int VUNGTAU_COMPANY_ID = 81;
	public static int BENTRE_COMPANY_ID = 71;
	public static int BACLIEU_COMPANY_ID = 91;
	public static int DONGNAI_COMPANY_ID = 41;
	public static int CANTHO_COMPANY_ID = 51;
	public static int SOCTRANG_COMPANY_ID = 61;
	public static int VINHLONG_COMPANY_ID = 21;
	public static int TRAVINH_COMPANY_ID = 31;
	public static int LONGAN_COMPANY_ID = 201;
	public static int BINHPHUOC_COMPANY_ID = 191;
	public static int DALAT_COMPANY_ID = 181;
	public static int KONTUM_COMPANY_ID = 211;
	public static int KIENGIANG_COMPANY_ID = 171;
	public static int TIENGIANG_COMPANY_ID = 161;
	public static int DONGTHAP_COMPANY_ID = 141;
	public static int CAMAU_COMPANY_ID = 151;
	public static int BINHTHUAN_COMPANY_ID = 101;
	public static int TAYNINH_COMPANY_ID = 111;
	public static int ANGIANG_COMPANY_ID = 121;
	public static int HAUGIANG_COMPANY_ID = 231;
	public static int NINHTHUAN_COMPANY_ID = 241;
	public static int KHANHHOA_COMPANY_ID = 251;
	public static int DANANG_COMPANY_ID = 261;
	public static int BINHDINH_COMPANY_ID = 281;
	public static int DAKLAK_COMPANY_ID = 271;
	public static int GIALAI_COMPANY_ID = 221;
	public static int PHUYEN_COMPANY_ID = 291;
	public static int QUANGNAM_COMPANY_ID = 301;
	public static int QUANGNGAI_COMPANY_ID = 311;
	public static int DACNONG_COMPANY_ID = 321;
	public static int QUANGBINH_COMPANY_ID = 341;
	public static int QUANGTRI_COMPANY_ID = 351;
	public static int HUE_COMPANY_ID = 331;

	// //////////////////////////////

	public static String INGW_URL = "http://10.151.9.164:18088/";
	public static String INGW_USER = "mplus";
	public static String INGW_PASS = "mplus";
	public static int TIME_REBIND_EXCE_CHARGE = 10;
	
	public static int SERVICE_PAGKET = 1;
	public static int SERVICE_MT = 0;
	
	public static int PACKET_ICOM = -1;
	public static int PACKET_VMS = 1;
	
	public static String REGISTRY_AGAIN = "REGISTRY_AGAIN";
	public static String DESTROY_SUBCRIBER = "DESTROY_SUBCRIBER";
	
	public static String MLIST_SUBCRIBER = "mlist_subcriber";
	public static String MLIST_SUBCRIBER_CANCEL = "mlist_subcriber_cancel";
	
	//tuannq
	public static String TIME_WIN_DAILY ="19:50";
	public static String[] LIST_DB_SENDQUEUE = { "gateway" };
	public static String[] LIST_DB_RECEIVEQUEUE = { "gateway" };
	public static String[] LIST_TABLE_SENDQUEUE = { "0" };
	
	// chitd 
	public static String TABLENAME_MOQUEUE9222="mo_queue9222";
	public static String TABLENAME_MOQUEUEAPPROVE="mo_queue9222approved";
	public static String MODECONFIRM="1";
	public static String MESSAGENOTINFOTOCONFIRM="De dang ky dich vu Su kien HOT & Binh luan HAY soan tin: DK BL gui 9209, " +
			"Cap nhat tu dong cac noi dung HOT cua mPlus ban vui long soan tin: DK <madichvu> gui 9209 (Madichvu= Gamehot," +
			" GameHD, XSMB, XSMN, XSMT).Chi tiet goi: 9244";
	public static String MTQUEUEPUSH="1;2;3";
	public static int MTTOTALTHREAD=2;
	public static int NUMBERTHREADSENDDIEMTHI=5;
	public static int NUMBERTHREADSENDREGISTERSUCCESS=5;
	public static int MODEDIEMTHI=1;
	// end chitd
	public static int getIDdbsendqueue(String userid) {
		try {
			int itemp = Integer.parseInt(userid.substring(userid.length() - 6));
			int idtemp = (itemp % Constants.LIST_DB_SENDQUEUE.length);
			return idtemp;

		} catch (Exception e) {
			return 0;
		}
	}
	
	public static String getTableSendQueue(int id) {
		try {
			String temp = LIST_TABLE_SENDQUEUE[id];
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
	
	public Constants() {
	}

	public static boolean loadProperties(String propFile) {
		
		Properties properties = new Properties();
		Util.logger.info("Reading configuration file " + propFile);
		
		try {
			
			FileInputStream fin = new FileInputStream(propFile);
			properties.load(fin);
			_prop = properties;
			fin.close();
			NUM_THREAD = Integer.parseInt(properties.getProperty("NUM_THREAD",
					"10"));
			NUM_THREAD_LOAD_MO = Integer.parseInt(properties.getProperty(
					"NUM_THREAD_LOAD_MO", "2"));
			LOAD_MO_MODE = properties.getProperty("LOAD_MO_MODE", "DB");
			GOLD_HOUR_END = properties.getProperty("GOLD_HOUR_END","13:00");
			GOLD_HOUR_BEGIN = properties.getProperty("GOLD_HOUR_BEGIN","12:00");
			MO_DIR = properties.getProperty("MO_DIR", MO_DIR);
			LOGFILE = properties.getProperty("LOGFILE", LOGFILE);
			LOGLEVEL = properties.getProperty("LOGLEVEL", LOGLEVEL);
			TIME_DELAY_LOAD_MO = Integer.parseInt(properties.getProperty(
					"TIME_DELAY_LOAD_MO", "" + TIME_DELAY_LOAD_MO));

			String runclass = properties.getProperty("RUNCLASS", "");
			RUNCLASS = parseString(runclass, ",");

			INV_MO_CLASS = properties.getProperty("INV_CLASS", INV_MO_CLASS);
			INV_MT_CLASS = properties.getProperty("INV_MT_CLASS", INV_MT_CLASS);
			INV_KEYWORD = properties.getProperty("INV_KEYWORD", INV_KEYWORD);

			ACTIVE_VNL = properties.getProperty("ACTIVE_VNL", "1");
			ACTIVE_LOTTERY = properties.getProperty("ACTIVE_LOTTERY", "1");

			// RECEIVE_MO_MODE
			RECEIVE_MO_MODE = Integer.parseInt(properties.getProperty(
					"RECEIVE_MO_MODE", "" + RECEIVE_MO_MODE));

			CP_ID = Integer.parseInt(properties
					.getProperty("CP_ID", "" + CP_ID));

			// Moi them vao ngay 27
			// Util.logger.info("Reading configuration file " + propFile);
			// FileInputStream fin = new FileInputStream(propFile);
			//properties.load(fin);
			_prop = properties;
			//fin.close();
			// LOGFILE = properties.getProperty("LOGFILE", LOGFILE);
			// LOGLEVEL = properties.getProperty("LOGLEVEL", LOGLEVEL);
			INGW_URL = properties.getProperty("INGW_URL", INGW_URL);
			INGW_USER = properties.getProperty("INGW_USER", INGW_USER);
			INGW_PASS = properties.getProperty("INGW_PASS", INGW_PASS);

			NUM_THREAD_SEND_REQUEST = Integer.parseInt(properties.getProperty(
					"NUM_THREAD_SEND_REQUEST", "" + NUM_THREAD_SEND_REQUEST));
			TIME_REBIND = Integer.parseInt(properties.getProperty(
					"TIME_REBIND", "" + TIME_REBIND));
			
			MODECONFIRM=properties.getProperty("modeConfirm"); 
			MTQUEUEPUSH=properties.getProperty("mtQueuePush");
			MTTOTALTHREAD=Integer.parseInt(properties.getProperty("mtTotalThread"));
			NUMBERTHREADSENDDIEMTHI=Integer.parseInt(properties.getProperty("numberThreadSenDiemThi"));
			NUMBERTHREADSENDREGISTERSUCCESS=Integer.parseInt(properties.getProperty("numberThreadSendRegisterSuccess"));
			MODEDIEMTHI=Integer.parseInt(properties.getProperty("modeDiemThi"));   
			Util.logger.info("Read configuration file, done.");

			return true;

		} catch (Exception e) {
			Util.logger.info("Reading configuration file " + propFile + "@"
					+ e.toString());
			return false;
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

	public final static String[] sounthNames = { "B.Dinh", "V.Long", "T.Vinh",
			"D.Thap", "C.Mau", "B.Thuan", "V.Tau", "B.Lieu", "C.Tho",
			"S.Trang", "B.Tre", "T.Ninh", "A.Giang", "TP HCM", "L.An",
			"B.Phuoc", "T.Giang", "K.Giang", "D.Lak", "K.Tum", "H.Giang",
			"N.Thuan", "K.Hoa", "D.Nang", "D.Nong", "Q.Ngai", "Q.Nam", "P.Yen",
			"G.Lai", "D.Lat", "B.Duong", "D.Nai", "TT Hue", "Q.Binh", "Q.Tri",
			"K.Hoa", "TP HCM", "D.Nang" };

	final static int[] sounthCompanies = { BINHDINH_COMPANY_ID,
			VINHLONG_COMPANY_ID, TRAVINH_COMPANY_ID, DONGTHAP_COMPANY_ID,
			CAMAU_COMPANY_ID, BINHTHUAN_COMPANY_ID, VUNGTAU_COMPANY_ID,
			BACLIEU_COMPANY_ID, CANTHO_COMPANY_ID, SOCTRANG_COMPANY_ID,
			BENTRE_COMPANY_ID, TAYNINH_COMPANY_ID, ANGIANG_COMPANY_ID,
			SAIGON_COMPANY_ID, LONGAN_COMPANY_ID, BINHPHUOC_COMPANY_ID,
			TIENGIANG_COMPANY_ID, KIENGIANG_COMPANY_ID, DAKLAK_COMPANY_ID,
			KONTUM_COMPANY_ID, HAUGIANG_COMPANY_ID, NINHTHUAN_COMPANY_ID,
			KHANHHOA_COMPANY_ID, DANANG_COMPANY_ID, DACNONG_COMPANY_ID,
			QUANGNGAI_COMPANY_ID, QUANGNAM_COMPANY_ID, PHUYEN_COMPANY_ID,
			GIALAI_COMPANY_ID, DALAT_COMPANY_ID, BINHDUONG_COMPANY_ID,
			DONGNAI_COMPANY_ID, HUE_COMPANY_ID, QUANGBINH_COMPANY_ID,
			QUANGTRI_COMPANY_ID, KHANHHOA_COMPANY_ID, SAIGON_COMPANY_ID,
			DANANG_COMPANY_ID };

	public static String getNameOfLottery(int companyId) {
		if (companyId == 1) {
			return "MB";
		}
		for (int i = 0; i < sounthCompanies.length; i++) {
			if (companyId == sounthCompanies[i]) {
				return sounthNames[i];
			}
		}
		return "MB";
	}

	/******
	 * 2010-11-07: PhuongDT
	 * Defined type of service: used to register services.
	 * ***/
	public static final int TYPE_OF_SERVICE_THOI_TIET = 3;
	public static final int TYPE_OF_SERVICE_CAU_XS = 2;
	public static final int TYPE_OF_SERVICE_HOROSCOPE = 1;
	public static final int TYPE_OF_SERVICE_TEXTBASE = 0;
	
}
