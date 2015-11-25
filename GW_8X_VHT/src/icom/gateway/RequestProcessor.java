package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import icom.common.*;
import icom.common.Queue;

import java.io.*;
import java.util.*;
import java.math.BigDecimal;

import org.smpp.*;
import org.smpp.pdu.*;
import org.smpp.util.ByteBuffer;
import org.smpp.util.NotEnoughDataInByteBufferException;

import java.sql.Timestamp;
import java.text.*;

/**
 * For Processing PDUs (responsed Requests) from SMSC
 */
public class RequestProcessor extends Thread {
	// private Logger logger = new Logger("RequestProcessor");
	
	private int threadId = 0;
	
	private Queue fromSMSC = null;
	private Queue toSMSC = null;
	// private Queue InvQueue = null;
	private Queue MOSimQueue = null;
	// private Map wait4ResponseTable = null;

	private PDU pdu = null;
	private PDUData pdud = null;
	private DeliverSM dsm = null;

	// Variables storing info from SMSC (in Deliver_SM pdu)
	private String userId = null; // src_address
	private String serviceId = null; // dest_address
	private String operator = null;
	private String commandCode = null;
	private String info = null; // short_message
	private String RequestID = null;
	private String Dport = "0";

	private DBTools dbTools = null;
	private ReportMsgParser parser = null;

	// Queue luu cac so dien thoai cung voi thoi gian
	// su dung dich vu gan day nhat.
	private MobileQueue mQueue = new MobileQueue();

	public RequestProcessor(Queue fromSMSC, Queue toSMSC) {
		this.fromSMSC = fromSMSC; // contains only request PDUs.
		this.toSMSC = toSMSC;
		// this.InvQueue = InvQueue;
		// this.MOSimQueue = MOSimQueue;
		// this.wait4ResponseTable = wait4ResponseTable;
		this.dbTools = new DBTools();
		this.parser = new ReportMsgParser();
	}

	public void run() {
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////
		while (Gateway.running) {
			try {
				pdud = (PDUData) fromSMSC.dequeue(); // blocks until having
				// an item
				// pdu = (PDU) fromSMSC.dequeue(); //blocks until having an item
				pdu = (PDU) pdud.getPDU();
				if (pdu.isRequest()) {
					this.RequestID = pdud.getRequestID();
					processRequest(pdu);
				}
			} catch (DBException ex) { // when lost connection to db
				Logger.error(this.getClass().getName(), "DBException: "
						+ ex.getMessage());
				DBTools.ALERT("RequestProcessor", "RequestProcessor",
						Constants.ALERT_WARN, Preference.Channel
								+ "DBException: " + ex.getMessage(),
						Preference.ALERT_CONTACT);
				Logger.error(this.getClass().getName(),
						"Alert2YM DBException: " + ex.getMessage());
			} catch (Exception e) {
				Logger.error(this.getClass().getName(), "Exception: "
						+ e.getMessage());

				DBTools.ALERT("RequestProcessor", "RequestProcessor",
						Constants.ALERT_WARN, Preference.Channel
								+ "Exception: " + e.getMessage(),
						Preference.ALERT_CONTACT);
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// /////////////////////////////
		Logger.info(this.getClass().getName(), "{" + this.getClass().getName()
				+ " stopped}");
		this.destroy();
		// /////////////////////////////
	}

	public void destroy() {
		Gateway.removeThread(this);
	}

	public String stringToHex(String base) {
		StringBuffer buffer = new StringBuffer();
		int intValue;
		for (int x = 0; x < base.length(); x++) {
			int cursor = 0;
			intValue = base.charAt(x);
			String binaryChar = new String(Integer.toBinaryString(base
					.charAt(x)));
			for (int i = 0; i < binaryChar.length(); i++) {
				if (binaryChar.charAt(i) == '1') {
					cursor += 1;
				}
			}
			if ((cursor % 2) > 0) {
				intValue += 128;
			}
			buffer.append(Integer.toHexString(intValue) + " ");
		}
		return buffer.toString();
	}

	private void processRequest(PDU pdu) throws DBException, IOException {
		// Process the request and add to SMS_RECEIVE_QUEUE
		if (pdu.getCommandId() == Data.DELIVER_SM) {

			dsm = (DeliverSM) pdu;
			this.userId = dsm.getSourceAddr().getAddress();
			this.serviceId = dsm.getDestAddr().getAddress();
			this.info = dsm.getShortMessage();

			ByteBuffer da = null;

			try {
				da = dsm.getDestSubaddress();

				String te = Convert.hexToString(da.getHexDump().substring(2));

//				this.RequestID = te;
				Gateway.util.log(this.getClass().getName(),
						"getDestSubaddress:" + te);
			} catch (ValueNotSetException ex) {

			}
			short destport = 0;
			try {
				destport = dsm.getDestinationPort();

				Gateway.util.log(this.getClass().getName(),
						"getDestinationPort:" + destport);
			} catch (ValueNotSetException e) {
				// TODO Auto-generated catch block
				// Tim Dport o day
				// ByteBuffer otaData =new
				// ByteBuffer(info.getBytes(Data.ENC_ISO8859_1));
				ByteBuffer otaData = new ByteBuffer();
				otaData = dsm.getShortMessagebuff();
				try {

					byte udhlength = otaData.removeByte();
					byte udhele1 = otaData.removeByte();
					byte udhele2 = otaData.removeByte();

					short sdport = otaData.removeShort();

					short ssport = otaData.removeShort();

					

					String newmsg = otaData.removeString(otaData.length(),
							Data.ENC_ISO8859_1);

					if (udhlength == 6 && udhele1 == 5) {
						destport = sdport;
						this.info = newmsg;
						Gateway.util.log(this.getClass().getName(),
								"getDestinationPort:" + destport + "@info="
										+ newmsg);
					}

				} catch (NotEnoughDataInByteBufferException e1) {
					// TODO Auto-generated catch block

				}

			}

			this.Dport = Short.toString(destport);

			if (this.info == null) {
				this.info = "null";
			}
			this.userId = removePlusSign(this.userId);
			this.serviceId = rebuildServiceId(this.serviceId);
			this.operator = Preference.mobileOperator;
			// Added on 22//2003 : VinaPhone gui ban tin DeliverReport voi
			// truong esm_class != 0x4. ==> He thong xem nhu ban tin thuong
			// sai format va gui thong bao -- report -- thong bao --> LOOP./
			// To pass over this, set:
			if (dsm.getEsmClass() == 0x4 || info.startsWith("id:")) { // DeliverReport
				// (not
				// processed)!
				Gateway.util.log(this.getClass().getName(),
						"It can be DeliverReport (not processed)!");
				return; // not processed
			}

			// Normal message (request):
			// Neu mobile o che do tieng viet
			// --> Loai bo space (ky tu 00) giua cac ky tu
			this.info = StringTool.removeChar(this.info, '\00');

			String newserviceid = this.serviceId;

			newserviceid = newserviceid.substring(newserviceid.length() - 4);
			
			//DANND add
			DateFormat dateFormat = new SimpleDateFormat(
			"MMddHHmmssSSS");
			java.util.Date date = new java.util.Date();
			String datetime = dateFormat.format(date);
			this.RequestID = Preference.prefix_requestid + datetime + threadId;
			
			
			dbTools.add2SMSReceiveQueueR(this.userId, this.serviceId,
					this.operator, this.commandCode, this.info, this.RequestID,
					this.Dport);
						
		}
	}

	private boolean isValidAddress(String userId, String serviceId) {
		if (userId == null || "".equals(userId) || serviceId == null
				|| "".equals(serviceId)) {
			// System.out.println("Source/dest address NULL --> PDU discarded");
			return false;
		}
		if (!Preference.isValidServiceId(serviceId)) {
			// System.out.println("Invalid dest address:" + serviceId
			// + " --> PDU discarded");
			return false;
		}
		if (userId.startsWith("849") || userId.startsWith("+849")
				|| userId.startsWith("09")) {
			return true;
			// TrungDK EVN -> UNKNOWN
		} else if ("UNKNOWN".equalsIgnoreCase(Preference.mobileOperator)) {
			return true;
		} else {
			// System.out.println("Invalid source address:" + userId + " --> PDU
			// discarded");
			return true;
		}
	}

	// private boolean isTimeExceeded(String mobile) {
	// boolean result = false;
	// long currTime = System.currentTimeMillis() / 1000; //secs
	// long lastTime = mQueue.getTime(mobile);
	// if (lastTime > 0) {
	// mQueue.update(mobile, currTime);
	// if ( (currTime - lastTime) < 5) { // 3secs
	// result = true;
	// }
	// } else {
	// mQueue.add(mobile, currTime);
	// }
	// return result;
	// }

	/**
	 * mobile: 849xxx return: 1: OK; 2: Time exceeded (too fast) =
	 * MAX_MO_PER_DAY: MO counter equals the max value. > MAX_MO_PER_DAY: MO
	 * counter exceeded the max value.
	 */
	private int checkMobileBuffer(String mobile) {
		long currTime = System.currentTimeMillis() / 1000; // secs
		int currDay = getDay(System.currentTimeMillis());
		MobileBufferInfo info = MobileBuffer.lookup(mobile);
		if (info == null) { // not exist --> create new
			MobileBuffer.add(mobile, new MobileBufferInfo(mobile, currTime, 0,
					1, 0, 0));
			return 1; // OK
		}
		// EXIST --> update
		int result = 0;
		long lastTime = info.mo_Time;
		if (lastTime <= 0) { // added by MT thread (no MO before) --> update
			info.mo_Counter = 1; // first MO
			result = 1; // OK
		} else if (currDay != getDay(lastTime * 1000)) { // new day/date
			// //////////////////////////////////////////////////////////
			// /////// CLEAR BUFFER FOR PREVIOUS DAY ////////////////////
			MobileBuffer.clearAll();
			// //////////////////////////////////////////////////////////
			result = 1; // OK
		} else {
			info.mo_Counter++;
			/*
			 * Mark by DKTRUNG 0904098489 if ( (currTime - lastTime) <
			 * Constants.MIN_TIME_BETWEEN_MO) { result = 2; //send too fast }
			 * else
			 */
			if (info.mo_Counter >= Constants.MAX_MO_PER_DAY) {
				// reach max value --> send inform to user
				result = info.mo_Counter;
				// } else if (info.mo_Counter > Constants.MAX_MO_PER_DAY) {
				// send too many
				// result = info.mo_Counter;
			} else {
				result = 1; // OK
			}
		}
		info.mo_Time = currTime;
		MobileBuffer.update(mobile, info);
		return result;
	}

	private int checkMobileBuffer(String mobile, String msg) {
		long currTime = System.currentTimeMillis() / 1000; // secs
		int currDay = getDay(System.currentTimeMillis());
		MobileBufferInfo info = MobileBuffer.lookup(mobile);
		if (info == null) { // not exist --> create new
			MobileBuffer.add(mobile, new MobileBufferInfo(mobile, currTime, 0,
					1, 0, 0, msg));
			return 1; // OK
		}
		// EXIST --> update
		int result = 0;
		long lastTime = info.mo_Time;
		if (lastTime <= 0) { // added by MT thread (no MO before) --> update
			info.mo_Counter = 1; // first MO
			result = 1; // OK
		} else if (currDay != getDay(lastTime * 1000)) { // new day/date
			// //////////////////////////////////////////////////////////
			// /////// CLEAR BUFFER FOR PREVIOUS DAY ////////////////////
			MobileBuffer.clearAll();
			// //////////////////////////////////////////////////////////
			result = 1; // OK
		} else {
			info.mo_Counter++;
			// DKTRUNG 0904098489 Check spam
			if ((currTime - lastTime) < Constants.MIN_TIME_BETWEEN_MO
					&& msg.equals(info.msg)) {
				result = 2; // send too fast
			} else if (info.mo_Counter >= Constants.MAX_MO_PER_DAY) {
				// reach max value --> send inform to user
				result = info.mo_Counter;
				// } else if (info.mo_Counter > Constants.MAX_MO_PER_DAY) {
				// send too many
				// result = info.mo_Counter;
			} else {
				result = 1; // OK
			}
		}
		if (result <= 1) {
			info.msg = msg;
			info.mo_Time = currTime;
			MobileBuffer.update(mobile, info);
		}
		return result;
	}

	private int getDay(long miliSecs) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(miliSecs));
		return calendar.get(calendar.DAY_OF_MONTH);
	}

	private String buildMobileOperator(String userId) {
		String temp = userId;
		String result = null;
		if (temp == null || "".equals(temp)) {
			result = "null";
		} else if (temp.startsWith("8490") || temp.startsWith("+8490")
				|| temp.startsWith("8493") || temp.startsWith("+8493")) {
			result = "VMS";
		} else if (temp.startsWith("8491") || temp.startsWith("+8491")
				|| temp.startsWith("8494") || temp.startsWith("+8494")) {
			result = "GPC";
		} else if (temp.startsWith("8498") || temp.startsWith("+8498")
				|| temp.startsWith("8497") || temp.startsWith("+8497")) {
			result = "VIETEL";
		} else if (temp.startsWith("8495") || temp.startsWith("+8495")) {
			result = "SFONE";
		} else if (temp.startsWith("8492") || temp.startsWith("+8492")) {
			result = "HTC";
		} else if (temp.startsWith("8496") || temp.startsWith("+8496")) {
			result = "EVN";
		} else {
			result = "EVN";
		}
		return result;
	}

	// Dest_addr received from SMSC can be: +849xxx or 849xxx
	// if +849xxx then remove the plus (+) sign
	private String removePlusSign(String userId) {
		String temp = userId;
		if (temp.startsWith("+")) {
			temp = temp.substring(1);
		}
		return temp;
	}

	// return dest_address without 84 or 04
	// e.g: 997, 19001255, 18001255
	private String rebuildServiceId(String serviceId) {
		String temp = serviceId;
		if (temp.startsWith("+")) {
			temp = temp.substring(1);
		}
		if ((temp.startsWith("84") && temp.length() > 4)
				|| temp.startsWith("04")) {
			temp = temp.substring(2);
		}
		if ((temp.startsWith("095") && temp.length() > 5)) {
			temp = temp.substring(3);
		}
		return temp;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	
	
}
