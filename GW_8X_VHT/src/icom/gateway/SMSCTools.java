package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.smpp.*;
import org.smpp.pdu.*;
import org.smpp.util.ByteBuffer;

public class SMSCTools {
	// private static Logger logger = new Logger("SMSCTools");

	/**
	 * Creates a new instance of <code>EnquireSM</code> class. This PDU is used
	 * to check that application level of the other party is alive. It can be
	 * sent both by SMSC and ESME.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.11 ENQUIRE_LINK Operation."
	 * 
	 * @see Session#enquireLink(EnquireLink)
	 * @see EnquireLink
	 * @see EnquireLinkResp
	 */
	public static void enquireLink() {
		try {
			EnquireLink request = new EnquireLink();
			EnquireLinkResp response;
			// System.out.println("Enquire Link request " +
			// request.debugString());

			if (Preference.asynchronous) {
				Gateway.session.enquireLink(request);
			} else {
				response = Gateway.session.enquireLink(request);
				// System.out.println("Enquire Link response "
				// + response.debugString());
			}
		} catch (IOException ex) {
			// System.out.println("enquireLink: " + ex);
			// Gateway.bound = false;
			// Gateway.bound = false;
			// gateway.bind();
		} catch (Exception ex) {
			// System.out.println("enquireLink: " + ex);
		}
	}

	public static SubmitSM buildSubmitSM1(String srcAddr, String destAddr,
			String shortMsg, int MsgType, String sCommand_code,
			int TotalSegments) {
		SubmitSM request = null;
		try {
			request = new SubmitSM();
			// set values
			request.setServiceType(Preference.serviceType);
			// /////////////////////////////////////////////
			request.setSourceAddr(Preference.buildSrcAddress(srcAddr));
			request.setDestAddr(Preference.buildDestAddress(destAddr));
			request.setShortMessage(shortMsg);
			// /////////////////////////////////////////////
			request.setReplaceIfPresentFlag(Preference.replaceIfPresentFlag);
			request.setScheduleDeliveryTime(Preference.scheduleDeliveryTime);
			request.setValidityPeriod(Preference.validityPeriod);
			request.setEsmClass(Preference.esmClass);
			request.setProtocolId(Preference.protocolId);
			request.setPriorityFlag(Preference.priorityFlag);
			// request.setRegisteredDelivery(Preference.registeredDelivery);
			request.setRegisteredDelivery((byte) 0);
			request.setDataCoding(Preference.dataCoding);
			request.setSmDefaultMsgId(Preference.smDefaultMsgId);
			// SETTING THE SEQ NUMBER
			request.assignSequenceNumber(true); // Auto assigned
			// Trung DK add SourceSubaddress
			if ((MsgType == 2) || ((MsgType > 19) && (MsgType < 30))) {
				MsgType = 0;
			} else if ((MsgType == 1) || (MsgType == 3)) {
				MsgType = 1;
			} else {
				MsgType = 4;
			}
			if ((srcAddr.equals("996"))
					&& (MsgType == 0)
					&& ((sCommand_code.equals("XSMN"))
							|| (sCommand_code.equals("XSMT"))
							|| (sCommand_code.equals("XSMN")) || (sCommand_code
							.equals("XSTD")))) {
				MsgType = 4;
			}

			if (TotalSegments == 0) {
				TotalSegments = 1;
			}

			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			Charge.appendByte((byte) 0xA0);
			Charge.appendString(sCommand_code + "," + MsgType + ","
					+ TotalSegments);
			request.setSourceSubaddress(Charge);

			return request;
		}

		catch (Exception e) {
			// System.out.println("buildSubmitSM: " + e.getMessage());
			return null;
		}
	}

	public static SubmitSM buildSubmitSMold(String srcAddr, String destAddr,
			String shortMsg, int seqNumber, int MsgType, String sCommand_code,
			int TotalSegments) {
		SubmitSM request = null;
		try {
			request = new SubmitSM();
			// set values
			request.setServiceType(Preference.serviceType);
			// /////////////////////////////////////////////
			request.setSourceAddr(Preference.buildSrcAddress(srcAddr));
			request.setDestAddr(Preference.buildDestAddress(destAddr));
			request.setShortMessage(shortMsg);
			// /////////////////////////////////////////////
			request.setReplaceIfPresentFlag(Preference.replaceIfPresentFlag);
			request.setScheduleDeliveryTime(Preference.scheduleDeliveryTime);
			request.setValidityPeriod(Preference.validityPeriod);
			request.setEsmClass(Preference.esmClass);
			request.setProtocolId(Preference.protocolId);
			request.setPriorityFlag(Preference.priorityFlag);
			// request.setRegisteredDelivery(Preference.registeredDelivery);
			request.setRegisteredDelivery((byte) 0);
			request.setDataCoding(Preference.dataCoding);
			request.setSmDefaultMsgId(Preference.smDefaultMsgId);
			// SETTING THE SEQ NUMBER
			request.setSequenceNumber(seqNumber); // Manually assigned
			// request.assignSequenceNumber(true); //Auto assigned
			// Trung DK add SourceSubaddress
			if ((MsgType == 2) || ((MsgType > 19) && (MsgType < 30))) {
				MsgType = 0;
			} else if ((MsgType == 1) || (MsgType == 3)) {
				MsgType = 1;
			} else {
				MsgType = 4;
			}
			if ((srcAddr.equals("996"))
					&& (MsgType == 0)
					&& ((sCommand_code.equals("XSMN"))
							|| (sCommand_code.equals("XSMT"))
							|| (sCommand_code.equals("XSMN")) || (sCommand_code
							.equals("XSTD")))) {
				MsgType = 4;
			}

			if (TotalSegments == 0) {
				TotalSegments = 1;
			}

			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			Charge.appendByte((byte) 0xA0);
			Charge.appendString(sCommand_code + "," + MsgType + ","
					+ TotalSegments);
			request.setSourceSubaddress(Charge);

			return request;
		} catch (Exception e) {
			// System.out.println("buildSubmitSM: " + e);
			return null;
		}
	}

	// registeredDelivery:
	// = 0: No SMSC delivery receipt requested (default)
	// = 1: SMSC Delivery Receipt requested where final delivery outcome is
	// delivery SUCCESS or FAILURE
	// = 2: SMSC Delivery Receipt requested where the final delivery outcome is
	// delivery FAILURE
	public static SubmitSM buildSubmitSMold(String srcAddr, String destAddr,
			String shortMsg, int seqNumber, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments) {
		SubmitSM request = null;
		try {
			request = new SubmitSM();
			// set values
			request.setServiceType(Preference.serviceType);

			// /////////////////////////////////////////////
			request.setSourceAddr(Preference.buildSrcAddress(srcAddr));
			request.setDestAddr(Preference.buildDestAddress(destAddr));
			request.setShortMessage(shortMsg);
			request.setRegisteredDelivery(registeredDelivery);
			// /////////////////////////////////////////////

			request.setReplaceIfPresentFlag(Preference.replaceIfPresentFlag);
			request.setScheduleDeliveryTime(Preference.scheduleDeliveryTime);
			request.setValidityPeriod(Preference.validityPeriod);
			request.setEsmClass(Preference.esmClass);
			request.setProtocolId(Preference.protocolId);
			request.setPriorityFlag(Preference.priorityFlag);
			request.setDataCoding(Preference.dataCoding);
			request.setSmDefaultMsgId(Preference.smDefaultMsgId);

			// SETTING THE SEQ NUMBER
			request.setSequenceNumber(seqNumber); // Manually assigned

			// request.assignSequenceNumber(true); //Auto assigned
			// Trung DK add SourceSubaddress
			/*
			 * if ( (MsgType == 2) || ( (MsgType > 19) && (MsgType < 30))) {
			 * MsgType = 0; } else if ( (MsgType == 1) || (MsgType == 3)) {
			 * MsgType = 1; } else { MsgType = 4; }
			 * 
			 * if (TotalSegments == 0) { TotalSegments = 1; } if
			 * ((srcAddr.equals("996")) && (MsgType == 0) && (
			 * (sCommand_code.equals("XSMN")) || (sCommand_code.equals("XSMT"))
			 * ||
			 * (sCommand_code.equals("XSMN"))||(sCommand_code.equals("XSTD")))){
			 * MsgType = 4; }
			 * 
			 * ByteBuffer Charge = new ByteBuffer(); //Charge.appendShort(
			 * (short) 0x000C); Charge.appendByte( (byte) 0xA0);
			 * Charge.appendString(sCommand_code + "," + MsgType + "," +
			 * TotalSegments); request.setSourceSubaddress(Charge);
			 */

			return request;
		} catch (Exception e) {
			// System.out.println("buildSubmitSM: " + e);
			Gateway.util.log("SMSCTools", "{buildSubmitSM:}" + e);
			DBTools.ALERT("SMSCTools", "buildSubmitSMold",
					Constants.ALERT_SERIOUS, Preference.Channel + "Exception: "
							+ e.getMessage(), Preference.ALERT_CONTACT);
			return null;
		}
	}

	// registeredDelivery:
	// = 0: No SMSC delivery receipt requested (default)
	// = 1: SMSC Delivery Receipt requested where final delivery outcome is
	// delivery SUCCESS or FAILURE
	// = 2: SMSC Delivery Receipt requested where the final delivery outcome is
	// delivery FAILURE
	public static SubmitSM buildSubmitUTF8SM(String srcAddr, String destAddr,
			String shortMsg, int seqNumber, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments) {
		SubmitSM request = null;
		try {
			request = new SubmitSM();
			// set values
			request.setServiceType(Preference.serviceType);

			// /////////////////////////////////////////////
			request.setSourceAddr(Preference.buildSrcAddress(srcAddr));
			request.setDestAddr(Preference.buildDestAddress(destAddr));
			request.setShortMessage(shortMsg, "UnicodeBigUnmarked");
			request.setRegisteredDelivery(registeredDelivery);
			// /////////////////////////////////////////////

			request.setReplaceIfPresentFlag(Preference.replaceIfPresentFlag);
			request.setScheduleDeliveryTime(Preference.scheduleDeliveryTime);
			request.setValidityPeriod(Preference.validityPeriod);
			request.setEsmClass(Preference.esmClass);
			request.setProtocolId(Preference.protocolId);
			request.setPriorityFlag(Preference.priorityFlag);
			request.setDataCoding((byte) 0x08);
			request.setSmDefaultMsgId(Preference.smDefaultMsgId);

			// SETTING THE SEQ NUMBER
			request.setSequenceNumber(seqNumber); // Manually assigned
			// request.assignSequenceNumber(true); //Auto assigned
			// Trung DK add SourceSubaddress
			if ((MsgType == 2) || ((MsgType > 19) && (MsgType < 30))) {
				MsgType = 0;
			} else if ((MsgType == 1) || (MsgType == 3)) {
				MsgType = 1;
			} else {
				MsgType = 4;
			}
			if ((srcAddr.equals("996"))
					&& (MsgType == 0)
					&& ((sCommand_code.equals("XSMN"))
							|| (sCommand_code.equals("XSMT"))
							|| (sCommand_code.equals("XSMN")) || (sCommand_code
							.equals("XSTD")))) {
				MsgType = 4;
			}

			if (TotalSegments == 0) {
				TotalSegments = 1;
			}

			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			Charge.appendByte((byte) 0xA0);
			Charge.appendString(sCommand_code + "," + MsgType + ","
					+ TotalSegments);
			request.setSourceSubaddress(Charge);

			return request;
		} catch (Exception e) {
			// System.out.println("buildSubmitSMUTF8: " + e);
			return null;
		}
	}

	/**
	 * Creates a new instance of <code>SubmitMultiSM</code> class, lets you set
	 * subset of fields of it. This PDU is used to send SMS message to multiple
	 * devices.
	 * 
	 * Use session.submitMulti(SubmitMultiSM) to send this request.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.5 SUBMIT_MULTI Operation."
	 * 
	 * @see Session#submitMulti(SubmitMultiSM)
	 * @see SubmitMultiSM
	 * @see SubmitMultiSMResp
	 */
	public static SubmitMultiSM buildSubmitMultiSM(String srcAddr,
			String[] ListOfDestAddr, String shortMsg) {
		SubmitMultiSM request = null;
		try {
			request = new SubmitMultiSM();

			// input values and set some :-)
			for (int i = 0; i < ListOfDestAddr.length; i++) {
				request.addDestAddress(new DestinationAddress(Preference
						.buildDestAddress(ListOfDestAddr[i])));
			}
			request.setSourceAddr(Preference.buildSrcAddress(srcAddr));

			// set other values
			request.setServiceType(Preference.serviceType);
			request.setReplaceIfPresentFlag(Preference.replaceIfPresentFlag);
			// //////////////////////////////////
			request.setShortMessage(shortMsg);
			// //////////////////////////////////
			request.setScheduleDeliveryTime(Preference.scheduleDeliveryTime);
			request.setValidityPeriod(Preference.validityPeriod);
			request.setEsmClass(Preference.esmClass);
			request.setProtocolId(Preference.protocolId);
			request.setPriorityFlag(Preference.priorityFlag);
			request.setRegisteredDelivery(Preference.registeredDelivery);
			request.setDataCoding(Preference.dataCoding);
			request.setSmDefaultMsgId(Preference.smDefaultMsgId);

			// send the request
			// if (Preference.asynchronous) {
			// Gateway.session.submitMulti(request);
			// } else {
			// SubmitMultiSMResp response =
			// Gateway.session.submitMulti(request);
			// Preference.messageId = response.getMessageId();
			// }
			return request;
		} catch (Exception e) {
			// System.out.println("buildSubmitMultiSM: " + e);
			return null;
		}
	}

	/**
	 * Creates a new instance of <code>ReplaceSM</code> class, lets you set
	 * subset of fields of it. This PDU is used to replace certain attributes of
	 * already submitted message providing that you 'remember' message id of the
	 * submitted message. The message id is assigned by SMSC and is returned to
	 * you with the response to the submision PDU (SubmitSM, DataSM etc.).
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.10 REPLACE_SM Operation."
	 * 
	 * @see Session#replace(ReplaceSM)
	 * @see ReplaceSM
	 * @see ReplaceSMResp
	 */
	public void replace(String msgId, String srcAddr, String shortMsg) {
		try {
			ReplaceSM request = new ReplaceSM();
			ReplaceSMResp response;

			// set values
			request.setMessageId(msgId);
			request.setSourceAddr(srcAddr);
			request.setShortMessage(shortMsg);
			request.setScheduleDeliveryTime(Preference.scheduleDeliveryTime);
			request.setValidityPeriod(Preference.validityPeriod);
			request.setRegisteredDelivery(Preference.registeredDelivery);
			request.setSmDefaultMsgId(Preference.smDefaultMsgId);

			// send the request
			// System.out.println("Replace request " + request.debugString());
			if (Preference.asynchronous) {
				Gateway.session.replace(request);
			} else {
				response = Gateway.session.replace(request);
				// System.out
				// .println("Replace response " + response.debugString());
			}

		} catch (Exception e) {
			// System.out.println("replace: " + e);
		}
	}

	/**
	 * Creates a new instance of <code>CancelSM</code> class, lets you set
	 * subset of fields of it. This PDU is used to cancel an already submitted
	 * message. You can only cancel a message which haven't been delivered to
	 * the device yet.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.9 CANCEL_SM Operation."
	 * 
	 * @see Session#cancel(CancelSM)
	 * @see CancelSM
	 * @see CancelSMResp
	 */
	public void cancel(String msgId, String srcAddr, String destAddr) {
		try {
			CancelSM request = new CancelSM();
			CancelSMResp response;

			// set values
			request.setServiceType(Preference.serviceType);
			request.setMessageId(msgId);
			request.setSourceAddr(srcAddr);
			request.setDestAddr(destAddr);

			// send the request
			// System.out.println("Cancel request " + request.debugString());
			if (Preference.asynchronous) {
				Gateway.session.cancel(request);
			} else {
				response = Gateway.session.cancel(request);
				// System.out.println("Cancel response " +
				// response.debugString());
			}
		} catch (Exception e) {
			// System.out.println("cancel: " + e);
		}
	}

	/**
	 * Creates a new instance of <code>DataSM</code> class, lets you set subset
	 * of fields of it. This PDU is an alternative to the <code>SubmitSM</code>
	 * and </code>DeliverSM</code>. It delivers the data to the specified
	 * device.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.7 DATA_SM Operation."
	 * 
	 * @see Session#data(DataSM)
	 * @see DataSM
	 * @see DataSMResp
	 */
	public static DataSM buildDataSM(String srcAddr, String destAddress,
			int MsgType, String sCommand_code, int TotalSegments) {
		try {
			DataSM request = new DataSM();

			// set values
			request.setServiceType(Preference.serviceType);
			request.setSourceAddr(Preference.buildSrcAddress(srcAddr));
			request.setDestAddr(Preference.buildDestAddress(destAddress));
			request.setEsmClass(Preference.esmClass);
			request.setRegisteredDelivery(Preference.registeredDelivery);
			request.setDataCoding(Preference.dataCoding);
			// Trung DK add SourceSubaddress
			if ((MsgType == 2) || ((MsgType > 19) && (MsgType < 30))) {
				MsgType = 0;
			} else if ((MsgType == 1) || (MsgType == 3)) {
				MsgType = 1;
			} else {
				MsgType = 4;
			}
			if ((srcAddr.equals("996"))
					&& (MsgType == 0)
					&& ((sCommand_code.equals("XSMN"))
							|| (sCommand_code.equals("XSMT"))
							|| (sCommand_code.equals("XSMB")) || (sCommand_code
							.equals("XSTD")))) {
				MsgType = 4;
			}
			if (TotalSegments == 0) {
				TotalSegments = 1;
			}
			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			Charge.appendByte((byte) 0xA0);
			Charge.appendString(sCommand_code + "," + MsgType + ","
					+ TotalSegments);
			request.setSourceSubaddress(Charge);

			// send the request
			// if (Preference.asynchronous) {
			// Gateway.session.data(request);
			// } else {
			// DataSMResp response = Gateway.session.data(request);
			// Preference.messageId = response.getMessageId();
			// }
			return request;
		} catch (Exception e) {
			// System.out.println("buildDataSM: " + e);
			return null;
		}
	}

	/**
	 * Creates a new instance of <code>QuerySM</code> class, lets you set subset
	 * of fields of it. This PDU is used to fetch information about status of
	 * already submitted message providing that you 'remember' message id of the
	 * submitted message. The message id is assigned by SMSC and is returned to
	 * you with the response to the submision PDU (SubmitSM, DataSM etc.).
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.8 QUERY_SM Operation."
	 * 
	 * @see Session#query(QuerySM)
	 * @see QuerySM
	 * @see QuerySMResp
	 */
	public void query(String msgId, String srcAddr) {
		try {
			QuerySM request = new QuerySM();
			QuerySMResp response;

			// set values
			request.setMessageId(msgId);
			request.setSourceAddr(Preference.buildSrcAddress(srcAddr));

			// send the request
			// /System.out.println("Query request " + request.debugString());
			if (Preference.asynchronous) {
				Gateway.session.query(request);
			} else {
				response = Gateway.session.query(request);
				// System.out.println("Query response " +
				// response.debugString());
				Preference.messageId = response.getMessageId();
			}
		} catch (Exception e) {
			// System.out.println("query: " + e);
		}
	}

	public static void printCommandStatus(int commandStatus) {
		String commandStatus_Hex = "0x"
				+ Integer.toHexString(commandStatus).toUpperCase();
		String strTemp = null;
		switch (commandStatus) {
		case Data.ESME_RINVMSGLEN:
			strTemp = "MESSAGE LENGTH IS INVALID";
			break;
		case Data.ESME_RINVCMDLEN:
			strTemp = "COMMAND LENGTH IS INVALID";
			break;
		case Data.ESME_RSYSERR:
			strTemp = "SYSTEM ERROR";
			break;
		case Data.ESME_RINVSRCADR:
			strTemp = "INVALID SOURCE ADDRESS";
			break;
		case Data.ESME_RINVDSTADR:
			strTemp = "INVALID DEST ADDRESS";
			break;
		case Data.ESME_RSUBMITFAIL:
			strTemp = "SUBMIT FAILED";
			break;
		case Data.ESME_RTHROTTLED:
			strTemp = "Throttling error (ESME has exceeded allowed message limits)";
			break;
		case Data.ESME_RALYBND:
			strTemp = "ESME ALREADY IN BOUND STATE";
			break;
		case Data.ESME_RBINDFAIL:
			strTemp = "BIND FAILED";
			break;
		case Data.ESME_RINVPASWD:
			strTemp = "INVALID PASSWORD";
			break;
		case Data.ESME_RINVSYSID:
			strTemp = "INVALID SYSTEM_ID";
			break;
		case Data.ESME_RINVSERTYP:
			strTemp = "INVALID SERVICE TYPE";
			break;
		case Data.ESME_RINVSRCTON:
			strTemp = "INVALID SOURCE_ADDR_TON";
			break;
		case Data.ESME_RINVSRCNPI:
			strTemp = "INVALID SOURCE_ADDR_NPI";
			break;
		case Data.ESME_RINVDSTTON:
			strTemp = "INVALID DEST_ADDR_TON";
			break;
		case Data.ESME_RINVDSTNPI:
			strTemp = "INVALID DEST_ADDR_NPI";
			break;
		case Data.ESME_RMSGQFUL:
			strTemp = "MESSAGE QUEUE FULL";
			break;
		case Data.ESME_RINVSYSTYP:
			strTemp = "INVALID SYSTEM_TYPE";
			break;
		default:
			strTemp = "ERROR";
		}
		// /System.out.println("    " + strTemp + ":" + commandStatus_Hex);
	}

	// Check resend MT
	public static int CheckResend(int commandStatus, String MessageID) {
		String commandStatus_Hex = "0x"
				+ Integer.toHexString(commandStatus).toUpperCase();
		String strTemp = null;
		int resend = 1;
		int alert = 0;
		switch (commandStatus) {
		case Data.ESME_RINVMSGLEN:
			strTemp = "MESSAGE LENGTH IS INVALID";
			resend = 0;
			alert = 0;
			break;
		case Data.ESME_RINVCMDLEN:
			strTemp = "COMMAND LENGTH IS INVALID";
			resend = 0;
			alert = 0;
			break;
		case Data.ESME_RSYSERR:
			strTemp = "SYSTEM ERROR";
			resend = 1;
			alert = 1;
			break;
		case Data.ESME_RINVSRCADR:
			strTemp = "INVALID SOURCE ADDRESS";
			resend = 0;
			alert = 1;
			break;
		case Data.ESME_RINVDSTADR:
			strTemp = "INVALID DEST ADDRESS";
			resend = 0;
			alert = 1;
			break;
		case Data.ESME_RSUBMITFAIL:
			strTemp = "SUBMIT FAILED";
			resend = 1;
			alert = 1;
			break;
		case Data.ESME_RTHROTTLED:
			strTemp = "Throttling error (ESME has exceeded allowed message limits)";
			resend = 1;
			alert = 0;
			break;
		case Data.ESME_RALYBND:
			strTemp = "ESME ALREADY IN BOUND STATE";
			resend = 0;
			break;
		case Data.ESME_RBINDFAIL:
			strTemp = "BIND FAILED";
			resend = 1;
			break;
		case Data.ESME_RINVPASWD:
			strTemp = "INVALID PASSWORD";
			resend = 1;
			break;
		case Data.ESME_RINVSYSID:
			strTemp = "INVALID SYSTEM_ID";
			resend = 1;
			break;
		case Data.ESME_RINVSERTYP:
			strTemp = "INVALID SERVICE TYPE";
			resend = 1;
			alert = 1;
			break;
		case Data.ESME_RINVSRCTON:
			strTemp = "INVALID SOURCE_ADDR_TON";
			resend = 1;
			alert = 1;
			break;
		case Data.ESME_RINVSRCNPI:
			strTemp = "INVALID SOURCE_ADDR_NPI";
			resend = 1;
			alert = 1;
			break;
		case Data.ESME_RINVDSTTON:
			strTemp = "INVALID DEST_ADDR_TON";
			resend = 1;
			alert = 1;
			break;
		case Data.ESME_RINVDSTNPI:
			strTemp = "INVALID DEST_ADDR_NPI";
			resend = 1;
			alert = 1;
			break;
		case Data.ESME_RMSGQFUL:
			strTemp = "MESSAGE QUEUE FULL";
			resend = 1;
			alert = 1;
			break;
		case Data.ESME_RINVSYSTYP:
			strTemp = "INVALID SYSTEM_TYPE";
			resend = 1;
			alert = 1;
			break;
		default:
			strTemp = "ERROR";
			resend = 1;
			alert = 0;
		}
		if (resend == 1)
			Gateway.util.log("SMSCTools", "{MessageID=" + MessageID + "}{ERR="
					+ strTemp + ":" + commandStatus_Hex + "{Resend=Yes}");
		else {
			Gateway.util.log("SMSCTools", "{MessageID=" + MessageID + "}{ERR="
					+ strTemp + ":" + commandStatus_Hex + "{Resend=No}");

		}

		if (alert == 1) {
			DBTools.ALERT("CheckResend", "CheckResend", Constants.ALERT_WARN,
					"{ERR=" + strTemp + ":" + commandStatus_Hex + "{Resend="
							+ resend + "}", Preference.ALERT_CONTACT);
		}

		return resend;
	}

	public static String GetErrName(int commandStatus, String MessageID) {
		String commandStatus_Hex = "0x"
				+ Integer.toHexString(commandStatus).toUpperCase();
		String strTemp = null;
		switch (commandStatus) {
		case Data.ESME_RINVMSGLEN:
			strTemp = "MESSAGE LENGTH IS INVALID";
			break;
		case Data.ESME_RINVCMDLEN:
			strTemp = "COMMAND LENGTH IS INVALID";
			break;
		case Data.ESME_RSYSERR:
			strTemp = "SYSTEM ERROR";
			break;
		case Data.ESME_RINVSRCADR:
			strTemp = "INVALID SOURCE ADDRESS";
			break;
		case Data.ESME_RINVDSTADR:
			strTemp = "INVALID DEST ADDRESS";
			break;
		case Data.ESME_RSUBMITFAIL:
			strTemp = "SUBMIT FAILED";
			break;
		case Data.ESME_RTHROTTLED:
			strTemp = "Throttling error (ESME has exceeded allowed message limits)";
			break;
		case Data.ESME_RALYBND:
			strTemp = "ESME ALREADY IN BOUND STATE";
			break;
		case Data.ESME_RBINDFAIL:
			strTemp = "BIND FAILED";
			break;
		case Data.ESME_RINVPASWD:
			strTemp = "INVALID PASSWORD";
			break;
		case Data.ESME_RINVSYSID:
			strTemp = "INVALID SYSTEM_ID";
			break;
		case Data.ESME_RINVSERTYP:
			strTemp = "INVALID SERVICE TYPE";
			break;
		case Data.ESME_RINVSRCTON:
			strTemp = "INVALID SOURCE_ADDR_TON";
			break;
		case Data.ESME_RINVSRCNPI:
			strTemp = "INVALID SOURCE_ADDR_NPI";
			break;
		case Data.ESME_RINVDSTTON:
			strTemp = "INVALID DEST_ADDR_TON";
			break;
		case Data.ESME_RINVDSTNPI:
			strTemp = "INVALID DEST_ADDR_NPI";
			break;
		case Data.ESME_RMSGQFUL:
			strTemp = "MESSAGE QUEUE FULL";
			break;
		case Data.ESME_RINVSYSTYP:
			strTemp = "INVALID SYSTEM_TYPE";
			break;
		default:
			strTemp = "UNKNOW ERROR";
		}
		Gateway.util.log("SMSCTools", "{MessageID=" + MessageID + "}{ERR="
				+ strTemp + ":" + commandStatus_Hex);
		return strTemp;
	}

	/*
	 * 
	 */
	public static SubmitSM buildSubmitSM(String srcAddr, String destAddr,
			String shortMsg, int seqNumber, byte registeredDelivery,
			int MsgType, String sCommand_code, BigDecimal RequestID) {
		SubmitSM request = null;
		try {
			request = new SubmitSM();
			// set values
			request.setServiceType(Preference.serviceType);
			String servicetype = "";
			if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
				servicetype = "TE";
				if (srcAddr.length() > 3) {
					servicetype = servicetype + srcAddr.substring(1, 2);
				} else {
					if ("997".endsWith(srcAddr)) {
						servicetype = servicetype + "1";
					} else if ("996".endsWith(srcAddr)) {
						servicetype = servicetype + "2";
					} else if ("998".endsWith(srcAddr)) {
						servicetype = servicetype + "3";
					} else {
						servicetype = servicetype + "0";
					}
				}
				servicetype = servicetype + "01";
				request.setServiceType(servicetype);
			}

			
			// /////////////////////////////////////////////
			request.setSourceAddr(Preference.buildSrcAddress(srcAddr));
			request.setDestAddr(Preference.buildDestAddress(destAddr));
			if (Preference.CheckServiceId(srcAddr)) {
				request.setShortMessage(shortMsg);

			} else {
				return null;
			}
			request.setShortMessage(shortMsg);

			request.setRegisteredDelivery(registeredDelivery);
			// /////////////////////////////////////////////

			request.setReplaceIfPresentFlag(Preference.replaceIfPresentFlag);
			request.setScheduleDeliveryTime(Preference.scheduleDeliveryTime);
			request.setValidityPeriod(Preference.validityPeriod);
			request.setEsmClass(Preference.esmClass);
			request.setProtocolId(Preference.protocolId);
			request.setPriorityFlag(Preference.priorityFlag);
			request.setDataCoding(Preference.dataCoding);
			request.setSmDefaultMsgId(Preference.smDefaultMsgId);

			// SETTING THE SEQ NUMBER
			request.setSequenceNumber(seqNumber); // Manually assigned

			// request.assignSequenceNumber(true); //Auto assigned
			// Trung DK add SourceSubaddress

			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			if ("1".equalsIgnoreCase(Preference.SFONE_ACTIVE)) {
				
				if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
						) {
					Charge.appendByte((byte) 0xA0);
					Charge.appendString(destAddr);
					request.setSourceSubaddress(Charge);
				}
			} else {
				
				if ((MsgType == 2)) {
					MsgType = 0;
				} else if ((MsgType == 1)) {
					MsgType = 1;
				} else {
					MsgType = 4;
				}
				int TotalSegments = 1;

				Charge.appendByte((byte) 0xA0);
				Charge.appendString(sCommand_code + "," + MsgType + ","
						+ TotalSegments);
				request.setSourceSubaddress(Charge);
				
			}

			String DestSubAddress = "";

			if (MsgType != 3 && MsgType != 0) {
				ByteBuffer ChargeDest = new ByteBuffer();
				ChargeDest.appendByte((byte) 0xB0);
				DestSubAddress = "0xB0";
				ChargeDest.appendString(RequestID + "");
				DestSubAddress = DestSubAddress + RequestID;
				if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)) {
					if ("1".equalsIgnoreCase(Preference.REFUND_ACTIVE)) {
						request.setDestSubaddress(ChargeDest);
					}
				}

			}

			String chargeflag = "0x0000";
			if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
					&& "1".equalsIgnoreCase(Preference.REFUND_ACTIVE)) {
				if ((MsgType == 1)) {
					request.setSfoneChargeFlag((short) 0x0100);
					chargeflag = "0x0100";
				} else if ((MsgType + "").startsWith("21")) {
					request.setSfoneChargeFlag((short) 0x0201);
					chargeflag = "0x0201";
				} else if ((MsgType + "").startsWith("22")) {
					request.setSfoneChargeFlag((short) 0x0202);
					chargeflag = "0x0202";
				} else if ((MsgType + "").equals("2")) {
					request.setSfoneChargeFlag((short) 0x0201);
					chargeflag = "0x0201";
				} //else
					//request.setSfoneChargeFlag((short) 0x0000);
			}

			//request.setUssdServiceOp((byte) 0x03);

			if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
				if ((MsgType == 1)) {
					request.setLanguageIndicator((byte) 0x01);
				} else {
					request.setLanguageIndicator((byte) 0x00);
				}

			}

			// request.setSfoneChargeFlag((short) 0x0000);
			Logger.info("SMSCTools.buildSubmitSM", "{RequestID=" + RequestID
					+ "}{srcAddr=" + srcAddr + "}{destAddr=" + destAddr
					+ "}{setDestSubaddress=" + DestSubAddress
					+ "} {chargeflag=" + chargeflag + "}{servicetype="
					+ servicetype + "} ");
			return request;
		} catch (Exception e) {
			// System.out.println("buildSubmitSM: " + e);
			Gateway.util.log("SMSCTools", "{buildSubmitSM:}" + e);
			Logger.printStackTrace(e);

			DBTools.ALERT("SMSCTools", "buildSubmitSM.Exception",
					Constants.ALERT_SERIOUS, Preference.Channel + "Exception: "
							+ e.getMessage(), Preference.ALERT_CONTACT);

			return null;
		}
	}
}
