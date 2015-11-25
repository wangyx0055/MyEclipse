package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import icom.common.HexaTool;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Vector;
import java.util.Collection;
import java.math.BigDecimal;
import org.smpp.*;
import org.smpp.pdu.*;
import org.smpp.util.ByteBuffer;
import org.smpp.pdu.tlv.TLVOctets;

/**
 * Note: To send binary data in the shortMessage field of SubmitSM pdu, use the
 * setMessageData() method. To send text, use setShortMessage(). ESM_CLASS can
 * be set to 0x40 (UDHI indicator + default messaging mode) or 0x40 + 0x03 (UDHI
 * indicator + Store & Forward mode)
 * 
 * For VMS: + TON, NPI must be set: source_addr_ton=0, source_addr_npi=1
 * dest_addr_ton =1, dest_addr_npi =1 + Send binary data in the ShortMessage
 * field + The Service_Type can be set to "CMT" or NULL(default)
 * 
 * For GPC, + TON, NPI can be ignored or set like VMS + Binary data can be sent
 * in the Message_Payload or ShortMessage field. + The Service_Type can be
 * ignored or set to "GEN"
 * 
 */

public class EMSTools {
	// private static Logger logger = new Logger("EMSTools");

	// Max length for binary data per sms
	// 133 if single pdu or 128 bytes if concatnated
	private static int MAX_LENGTH = 133;

	private static boolean isFragmented = false;
	private static byte totalSegments = 1;

	public EMSTools() {
	}

	// Used for ems except ringtone
	// Input: otadata is OTA bitmap
	private static ByteBuffer addHeaders2OTAData(ByteBuffer otaData,
			String mobileOperator, int contentType) throws EMSException {
		// Before setting OTA data, except ringtone, the following data
		// is required
		ByteBuffer buffer = null;
		switch (contentType) {
		case Constants.CT_CLI_ICON:
			Cli cli = new Cli();
			cli.setOTB(otaData.getBuffer());
			cli.encode();
			buffer = cli.getEncoded();
			break;
		case Constants.CT_OPER_LOGO:
			OperatorLogo logo = new OperatorLogo();
			logo.setOTB(otaData.getBuffer());
			logo.setMobileOperator(mobileOperator);
			logo.encode();
			buffer = logo.getEncoded();
			break;
		default:
			buffer = otaData;
		}
		return buffer;
	}

	// Fast build ems in 1 SMS
	public static SubmitSM buildSubmitEMSfromBIN(ByteBuffer otaData,
			String srcAddr, String destAddr, BigDecimal emsId,
			byte registeredDelivery, int MsgType, String sCommand_code,
			int TotalSegments, BigDecimal RequestID) throws EMSException {
		SubmitSM request = new SubmitSM();
		try {
			// set the source and destination phone numbers
			request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
			request.setDestAddr((byte) 1, (byte) 1, destAddr);

			// create a buffer for current message payload
			ByteBuffer message = new ByteBuffer();
			// UDH length
			// //message.appendByte((byte) 0x06);
			// IE Identifier -- 5 means that the following will
			// be destination and originator port numbers
			// /message.appendByte((byte) 0x05);
			// IE Data Length -- the length of the IE
			// two ports per two bytes = 4
			// //message.appendByte((byte) 0x04);
			// destination port number ?
			// //message.appendShort((short) 0x23F4);
			// source port number ?
			// ///message.appendShort((short) 0x0000);
			// VCard data
			message.appendBuffer(otaData);

			String servicetype = "";
			if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
				servicetype = "BI";
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

			// We send all the data as a short_message
			try {
				request.setShortMessageData(message);
			} catch (Exception e) {
				// TODO: handle exception
				Logger.printStackTrace(e);

			}

			// OR as a message_payload: (used for GPC only)
			// request.setMessagePayload(message);

			// Esme class to indicate that the message contains UDH
			request.setEsmClass((byte) (Data.SM_UDH_GSM));

			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Seq number
			request.setSequenceNumber(Integer.parseInt(emsId.toString() + 1));

			// Set DeliveryReport
			request.setRegisteredDelivery(registeredDelivery);
			// Trung DK add SourceSubaddress

			if (TotalSegments == 0) {
				TotalSegments = 1;
			}

			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			if ("1".equalsIgnoreCase(Preference.SFONE_ACTIVE)) {
				Charge.appendByte((byte) 0xA0);
				Charge.appendString(destAddr);
				if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
						&& (MsgType == 1)) {
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

				Charge.appendByte((byte) 0xA0);
				Charge.appendString(sCommand_code + "," + MsgType + "," + "1");
				request.setSourceSubaddress(Charge);
			}

			String DestSubAddress = "";

			if (MsgType != 3) {
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
				} else
					request.setSfoneChargeFlag((short) 0x0000);
			}

			return request;
		} catch (Exception e) {
			// System.out.println("buildSubmitEMS: " + e.getMessage());
			return null;
		}
	}

	// gsm
	public static Collection buildSubmitEMS(ByteBuffer otaData, String srcAddr,
			String destAddr, int contentType, BigDecimal emsId,
			byte registeredDelivery, int MsgType, String commandcode)
			throws EMSException {
		SubmitSM request = null;
		Vector vRequests = new Vector();

		String mobileOperator = Preference.mobileOperator;

		otaData = addHeaders2OTAData(otaData, mobileOperator, contentType);
		try {
			// Calculate # of fragments
			// 1--133: 1 message
			// 134--256: 2 messages
			if (otaData.length() <= 133) {
				MAX_LENGTH = 133;
				isFragmented = false;
				totalSegments = 1;
			} else { // > 133
				MAX_LENGTH = 128;
				isFragmented = true;
				totalSegments = (byte) (otaData.length() / MAX_LENGTH + 1);
			}

			String servicetype = "";
			if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
				servicetype = "WA";
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
				// request.setServiceType(servicetype);
			}

			ByteBuffer message = null;
			for (byte i = 1; i <= totalSegments; i++) {
				request = new SubmitSM();
				request.setServiceType(servicetype);

				if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
					if (i == 1) {
						if ((MsgType == 1)) {
							request.setLanguageIndicator((byte) 0x01);
						} else {
							request.setLanguageIndicator((byte) 0x00);
						}
					} else {
						request.setLanguageIndicator((byte) 0x00);
					}

				}
				// set the source and destination phone numbers
				request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
				request.setDestAddr((byte) 1, (byte) 1, destAddr);
				// request.setSourceAddr(srcAddr);
				// request.setDestAddr(destAddr);

				// create a buffer for current message payload
				message = new ByteBuffer();

				// UDH is needed to tell the mobile phone details
				// how to deliver the data in the message payload
				// first goes UDH length -- this UDH will have 6 bytes
				if (isFragmented) {
					message.appendByte((byte) 0x0B);
				} else {
					message.appendByte((byte) 0x06);
				}

				// then goes IE -- information element
				// IE Identifier -- 5 means that the following will
				// be destination and originator port numbers
				message.appendByte((byte) 0x05);

				// IE Data Length -- the length of the IE
				// two ports per two bytes = 4
				message.appendByte((byte) 0x04);

				// Destination port number ?
				switch (contentType) {
				case Constants.CT_CLI_ICON:
					message.appendShort(Constants.PORT_CLI_ICON);
					break;
				case Constants.CT_OPER_LOGO:
					message.appendShort(Constants.PORT_OPER_LOGO);
					break;
				case Constants.CT_PIC_MSG:
					message.appendShort(Constants.PORT_PIC_MSG);
					break;
				case Constants.CT_RING_TONE:
					message.appendShort(Constants.PORT_RING_TONE);
					break;
				case Constants.CT_WAP_SI:
				case Constants.CT_MMS_NOTIFY:
					message.appendShort(Constants.PORT_WAP_PUSH);
					break;
				case Constants.CT_WAP_BROWSER:
					message.appendShort(Constants.PORT_WAP_BROWSER);
					break;
				case Constants.CT_VCARD:
					message.appendShort(Constants.PORT_VCARD);
					break;
				case Constants.CT_VCALENDAR:
					message.appendShort(Constants.PORT_VCALENDAR);
					break;
				default:
					throw new EMSException("Invalid content type "
							+ contentType + ")");
				}
				// Originator port (unused in fact)
				if (contentType == Constants.CT_WAP_SI
						|| contentType == Constants.CT_MMS_NOTIFY) {
					message.appendShort(Constants.PORT_WAP_CONNECTIONLESS);
				} else if (contentType == Constants.CT_WAP_BROWSER) {
					message.appendShort(Constants.PORT_WAP_BSOURCE);
				} else {
					message.appendShort((short) 0x0000);
				}
				/**
				 * SAR part (IEI=00): There are three parameters for doing this:
				 * SAR_MSG_REF_NUM: reference number of the concatenated
				 * message. SAR_TOTAL_SEGMENTS: total number of segments.
				 * SAR_SEGMENT_SEQNUM: number of actual segment.
				 * 
				 * 00 03 00 02 01
				 */
				if (isFragmented) {
					// IE Identifier Concatenated short messages, 8 bit ref
					// number
					message.appendByte((byte) 0x00);
					// IE Data Length is always 3 decimal
					message.appendByte((byte) 0x03);
					// IE Data as 3 octets
					// 1. Ref number for this concatenated message, 0 decimal
					message.appendByte((byte) 0x00);
					// 2. # of segments
					message.appendByte((byte) totalSegments);
					// 3. sequence # of current segment, start at 1
					message.appendByte((byte) i);
				}

				// Append the ota data to the message payload
				if (otaData.length() <= MAX_LENGTH) {
					message.appendBuffer(otaData);
				} else {
					message.appendBuffer(otaData.removeBuffer(MAX_LENGTH));
				}

				// we send all the data as a short_message
				request.setShortMessageData(message); // used for both GPC and
				// VMS
				// OR as a message_payload: (used for GPC only)
				// request.setMessagePayload(message);

				// you must set the esme class to indicate that the message
				// contains UDH
				request.setEsmClass((byte) (Data.SM_UDH_GSM)); // 0x40: UDHI
				// used +
				// default
				// messaging
				// mode
				// OR:
				// request.setEsmClass( (byte) (0x40 + 0x03)); //0x03: Store and
				// Forward mode

				// the coding is GSM specific
				request.setDataCoding((byte) 0xF5); // or 0x04 or 0x15: binary
				// encoding

				// Seqnumber is made from emsId and segment_seqnum:
				// <emsId><segment_seqnum>
				// e.g: emsId = 100; There are 3 segments:
				// --> seqnum = 1001, 1002, and 1003
				String s_emsId = emsId.toString();
				request.setSequenceNumber(Integer.parseInt(s_emsId + i));
				ByteBuffer Charge = new ByteBuffer();
				// Charge.appendShort( (short) 0x000C);
				if ("1".equalsIgnoreCase(Preference.SFONE_ACTIVE)) {
					Charge.appendByte((byte) 0xA0);
					Charge.appendString(destAddr);
					if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
							&& (MsgType == 1)) {
						request.setSourceSubaddress(Charge);
					}
				} else {
					/*
					 * if ((MsgType == 2)) { MsgType = 0; } else if ((MsgType ==
					 * 1)) { MsgType = 1; } else { MsgType = 4; } // int
					 * TotalSegments = 1; if (i != totalSegments) MsgType = 4;
					 * 
					 * Charge.appendByte((byte) 0xA0);
					 * Charge.appendString(commandcode + "," + MsgType + "," +
					 * totalSegments); request.setSourceSubaddress(Charge);
					 */
				}

				// Set DeliveryReport
				if (i == totalSegments) {
					request.setRegisteredDelivery(registeredDelivery);
				}
				vRequests.addElement(request);
			} // end for
			return vRequests;
		} catch (Exception e) {
			throw new EMSException(e.getMessage());
		}
	}

	// Sfone
	public static Collection buildSubmitEMS(ByteBuffer otaData, String srcAddr,
			String destAddr, int contentType, BigDecimal emsId,
			byte registeredDelivery, int MsgType, BigDecimal RequestID,
			String commandcode) throws EMSException {
		SubmitSM request = null;
		Vector vRequests = new Vector();

		String mobileOperator = "";
		mobileOperator = Preference.mobileOperator;
		otaData = addHeaders2OTAData(otaData, mobileOperator, contentType);
		try {
			// Calculate # of fragments
			// 1--133: 1 message
			// 134--256: 2 messages
			Logger.info("otadata.length:" + otaData.length());
			if (otaData.length() <= 133) {
				MAX_LENGTH = 133;
				isFragmented = false;
				totalSegments = 1;
			} else { // > 133
				MAX_LENGTH = 128;
				isFragmented = true;
				totalSegments = (byte) (otaData.length() / MAX_LENGTH + 1);
			}

			ByteBuffer message = null;
			for (byte i = 1; i <= totalSegments; i++) {
				request = new SubmitSM();
				// set the source and destination phone numbers
				request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
				request.setDestAddr((byte) 1, (byte) 1, destAddr);
				request.setServiceType("WPUSH");
				message = new ByteBuffer();
				if (otaData.length() <= MAX_LENGTH) {
					message.appendBuffer(otaData);
				} else {
					message.appendBuffer(otaData.removeBuffer(MAX_LENGTH));
				}

				// we send all the data as a short_message
				request.setShortMessageData(message); // used for both GPC and
				// VMS
				request.setEsmClass((byte) 0x00); // 0x00: CDMA Wap Push
				request.setProtocolId((byte) 0x00);
				String s_emsId = emsId.toString();
				request.setSequenceNumber(Integer.parseInt(s_emsId + i));

				// Set DeliveryReport
				if (i == totalSegments) {
					request.setRegisteredDelivery(registeredDelivery);
				}
				ByteBuffer Charge = new ByteBuffer();
				// Charge.appendShort( (short) 0x000C);
				if ("1".equalsIgnoreCase(Preference.SFONE_ACTIVE)) {
					Charge.appendByte((byte) 0xA0);
					Charge.appendString(destAddr);
					if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
							&& (MsgType == 1)) {
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
					Charge.appendString(commandcode + "," + MsgType + ","
							+ TotalSegments);
					request.setSourceSubaddress(Charge);
				}

				// Sfone charge
				String DestSubAddress = "0xB0";
				if (MsgType != 3) {

					ByteBuffer ChargeDest = new ByteBuffer();

					ChargeDest.appendByte((byte) 0xB0);

					ChargeDest.appendString(RequestID + "");
					DestSubAddress = DestSubAddress + RequestID;
					if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)) {
						request.setDestSubaddress(ChargeDest);
					}

				}

				String chargeflag = "0x0000";
				if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)) {
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
					} else
						request.setSfoneChargeFlag((short) 0x0000);
				}
				// request.setSfoneChargeFlag((short) 0x0000);
				Logger.info("SMSCTools.buildSubmitSM", "{RequestID="
						+ RequestID + "}{srcAddr=" + srcAddr + "}{destAddr="
						+ destAddr + "}{setDestSubaddress=" + DestSubAddress
						+ "} {chargeflag=" + chargeflag + "} ");

				vRequests.addElement(request);
			}// end for
			return vRequests;
		} catch (Exception e) {
			throw new EMSException(e.getMessage());
		}
	}

	// Dalink
	public static SubmitSM buildSubmitDalink(String srcMsg, String srcAddr,
			String destAddr, BigDecimal emsId, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments)
			throws EMSException {
		SubmitSM request = new SubmitSM();
		// t Test = new TLV();

		ByteBuffer optinal = new ByteBuffer();
		optinal.appendInt(1);
		try {
			// set the source and destination phone numbers
			request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
			request.setDestAddr((byte) 1, (byte) 1, destAddr);
			// request.setExtraOptional((byte) 1,optinal);
			request.setSourceSubaddress(optinal);
			String debug = "";
			// create a buffer for current message payload
			ByteBuffer message = new ByteBuffer();
			// UDH length

			// message.appendByte((byte)(srcMsg.length()+8));
			if (Integer.toHexString(srcMsg.length() + 8).length() < 2) {
				debug += "0" + Integer.toHexString(srcMsg.length() + 8);
			} else {
				debug += Integer.toHexString(srcMsg.length() + 8);
			}

			// message.appendByte( (byte) 0x06);
			message.appendByte((byte) 0x06);
			// IE Identifier -- 5 means that the following will
			// be destination and originator port numbers
			message.appendByte((byte) 0x05);
			// IE Data Length -- the length of the IE
			// two ports per two bytes = 4
			message.appendByte((byte) 0x04);
			debug += "060504";
			// Dalink parameter
			// message.appendByte( (byte) 0x060504);
			// destination port number ?
			message.appendShort((short) 0xc351);
			// message.appendShort( (short) 0x020B);//0x23F4);
			// source port number ?
			message.appendShort((short) 0xc351);
			// message.appendShort( (short) 0x020A);//0x0000);
			message.appendCString(srcMsg);
			// byte k[] = GSMAlphabet.stringTo7BitsBytes(srcMsg);
			// message.appendBytes(k, k.length);

			// We send all the data as a short_message
			debug += "C351C351";
			debug += srcMsg;
			request.setShortMessageData(message);
			// OR as a message_payload: (used for GPC only)
			// request.setMessagePayload(message);

			// Esme class to indicate that the message contains UDH
			request.setEsmClass((byte) (Data.SM_UDH_GSM));

			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Seq number
			request.setSequenceNumber(Integer.parseInt(emsId.toString() + 1));

			// Set DeliveryReport
			request.setRegisteredDelivery(registeredDelivery);
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
			// System.out.println(".: " + e.getMessage());
			return null;
		}
	}

	// Banking
	public static SubmitSM buildSubmitBanking(String srcMsg, String srcAddr,
			String destAddr, BigDecimal emsId, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments,
			String RequestID) throws EMSException {
		SubmitSM request = new SubmitSM();
		// t Test = new TLV();

		ByteBuffer optinal = new ByteBuffer();
		optinal.appendInt(1);
		try {
			// set the source and destination phone numbers
			request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
			request.setDestAddr((byte) 1, (byte) 1, destAddr);
			// request.setExtraOptional((byte) 1,optinal);
			request.setSourceSubaddress(optinal);
			String debug = "";

			ByteBuffer message = new ByteBuffer();
			// /////////////////////aaa

			// message.appendByte((byte)(srcMsg.length()+8));
			if (Integer.toHexString(srcMsg.length() + 8).length() < 2) {
				debug += "0" + Integer.toHexString(srcMsg.length() + 8);
			} else {
				debug += Integer.toHexString(srcMsg.length() + 8);
			}

			// message.appendByte( (byte) 0x06);
			message.appendByte((byte) 0x06);
			// IE Identifier -- 5 means that the following will
			// be destination and originator port numbers
			message.appendByte((byte) 0x05);
			// IE Data Length -- the length of the IE
			// two ports per two bytes = 4
			message.appendByte((byte) 0x04);
			debug += "060504";
			// Dalink parameter
			// message.appendByte( (byte) 0x060504);
			// destination port number ?
			message.appendShort((short) 0x138e);
			// message.appendShort( (short) 0x020B);//0x23F4);
			// source port number ?
			message.appendShort((short) 0x138e);
			// message.appendShort( (short) 0x020A);//0x0000);
			message.appendCString(srcMsg);
			// byte k[] = GSMAlphabet.stringTo7BitsBytes(srcMsg);
			// message.appendBytes(k, k.length);

			// We send all the data as a short_message
			debug += "C351C351";
			debug += srcMsg;
			request.setShortMessageData(message);
			// OR as a message_payload: (used for GPC only)
			// request.setMessagePayload(message);

			// Esme class to indicate that the message contains UDH
			request.setEsmClass((byte) (Data.SM_UDH_GSM));

			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Seq number
			request.setSequenceNumber(Integer.parseInt(emsId.toString() + 1));

			// Set DeliveryReport
			request.setRegisteredDelivery(registeredDelivery);

			// ////////////////////end aaa
			// create a buffer for current message payload
			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Trung DK add SourceSubaddress
			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			Charge.appendByte((byte) 0xA0);
			Charge.appendString(destAddr);
			if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
					&& (MsgType == 1)) {
				request.setSourceSubaddress(Charge);
			}

			String DestSubAddress = "";

			if (MsgType != 3) {
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
				} else
					request.setSfoneChargeFlag((short) 0x0000);
			}
			// request.setSfoneChargeFlag((short) 0x0000);
			Logger.info("SMSCTools.buildSubmitSM", "{RequestID=" + RequestID
					+ "}{srcAddr=" + srcAddr + "}{destAddr=" + destAddr
					+ "}{setDestSubaddress=" + DestSubAddress
					+ "} {chargeflag=" + chargeflag + "} ");

			return request;
		} catch (Exception e) {
			// System.out.println(".: " + e.getMessage());
			return null;
		}
	}

	// Banking
	public static SubmitSM buildSubmitDPORT(String srcMsg, String srcAddr,
			String destAddr, BigDecimal emsId, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments,
			String RequestID) throws EMSException {
		SubmitSM request = new SubmitSM();
		// t Test = new TLV();
		String[] arrmsg = srcMsg.split("###");
		String newsrcMsg = "";
		String sdport = "0";
		if (arrmsg.length > 1) {
			newsrcMsg = arrmsg[1];
			sdport = arrmsg[0];
		} else {
			newsrcMsg = srcMsg;
		}
		Short dport = 0;
		Short sport = 0;
		try {
			dport = Short.parseShort(sdport);
		} catch (Exception e) {
			// TODO: handle exception
		}

		ByteBuffer optinal = new ByteBuffer();
		optinal.appendInt(1);
		try {
			// set the source and destination phone numbers
			request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
			request.setDestAddr((byte) 1, (byte) 1, destAddr);
			// request.setExtraOptional((byte) 1,optinal);
			request.setSourceSubaddress(optinal);
			String debug = "";

			ByteBuffer message = new ByteBuffer();
			// /////////////////////aaa

			// message.appendByte((byte)(srcMsg.length()+8));
			if (Integer.toHexString(newsrcMsg.length() + 8).length() < 2) {
				debug += "0" + Integer.toHexString(newsrcMsg.length() + 8);
			} else {
				debug += Integer.toHexString(newsrcMsg.length() + 8);
			}

			// message.appendByte( (byte) 0x06);
			message.appendByte((byte) 0x06);
			// IE Identifier -- 5 means that the following will
			// be destination and originator port numbers
			message.appendByte((byte) 0x05);
			// IE Data Length -- the length of the IE
			// two ports per two bytes = 4
			message.appendByte((byte) 0x04);
			debug += "060504";
			// Dalink parameter
			// message.appendByte( (byte) 0x060504);
			// destination port number ?
			// /message.appendShort((short) 0x138e);
			message.appendShort(dport);
			// message.appendShort( (short) 0x020B);//0x23F4);
			// source port number ?
			// /message.appendShort((short) 0x138e);
			message.appendShort(sport);
			// message.appendShort( (short) 0x020A);//0x0000);
			message.appendCString(newsrcMsg);
			// byte k[] = GSMAlphabet.stringTo7BitsBytes(srcMsg);
			// message.appendBytes(k, k.length);

			// We send all the data as a short_message
			debug += "C351C351";
			debug += newsrcMsg;
			request.setShortMessageData(message);
			// OR as a message_payload: (used for GPC only)
			// request.setMessagePayload(message);

			// Esme class to indicate that the message contains UDH
			request.setEsmClass((byte) (Data.SM_UDH_GSM));

			// the coding is GSM specific
			// request.setDataCoding((byte) 0xF5);
			// request.setDataCoding((byte) 0x00);

			// Seq number
			request.setSequenceNumber(Integer.parseInt(emsId.toString() + 1));

			// Set DeliveryReport
			request.setRegisteredDelivery(registeredDelivery);

			// ////////////////////end aaa
			// create a buffer for current message payload
			// the coding is GSM specific
			request.setDataCoding((byte) 0x00);

			// Trung DK add SourceSubaddress
			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			Charge.appendByte((byte) 0xA0);
			Charge.appendString(destAddr);
			if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
					&& (MsgType == 1)) {
				request.setSourceSubaddress(Charge);
			}

			String DestSubAddress = "";

			if (MsgType != 3) {
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
				} else
					request.setSfoneChargeFlag((short) 0x0000);
			}
			// request.setSfoneChargeFlag((short) 0x0000);
			Logger.info("SMSCTools.buildSubmitSM", "{RequestID=" + RequestID
					+ "}{srcAddr=" + srcAddr + "}{destAddr=" + destAddr
					+ "}{setDestSubaddress=" + DestSubAddress
					+ "} {chargeflag=" + chargeflag + "} ");

			return request;
		} catch (Exception e) {
			// System.out.println(".: " + e.getMessage());
			return null;
		}
	}

	public static SubmitSM buildSubmit411A(String srcMsg, String srcAddr,
			String destAddr, BigDecimal emsId, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments,
			String RequestID) throws EMSException {
		SubmitSM request = new SubmitSM();
		// t Test = new TLV();

		ByteBuffer optinal = new ByteBuffer();
		optinal.appendInt(1);
		try {
			// set the source and destination phone numbers
			request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
			request.setDestAddr((byte) 1, (byte) 1, destAddr);
			// request.setExtraOptional((byte) 1,optinal);
			request.setSourceSubaddress(optinal);
			String debug = "";

			ByteBuffer message = new ByteBuffer();
			// /////////////////////aaa

			// message.appendByte((byte)(srcMsg.length()+8));
			if (Integer.toHexString(srcMsg.length() + 8).length() < 2) {
				debug += "0" + Integer.toHexString(srcMsg.length() + 8);
			} else {
				debug += Integer.toHexString(srcMsg.length() + 8);
			}

			// message.appendByte( (byte) 0x06);
			message.appendByte((byte) 0x06);
			// IE Identifier -- 5 means that the following will
			// be destination and originator port numbers
			message.appendByte((byte) 0x05);
			// IE Data Length -- the length of the IE
			// two ports per two bytes = 4
			message.appendByte((byte) 0x04);
			debug += "060504";
			// Dalink parameter
			// message.appendByte( (byte) 0x060504);
			// destination port number ?
			// /message.appendShort((short) 0x138e);
			message.appendShort(Constants.PORT_411A);
			// message.appendShort( (short) 0x020B);//0x23F4);
			// source port number ?
			// /message.appendShort((short) 0x138e);
			message.appendShort(Constants.PORT_411A);
			// message.appendShort( (short) 0x020A);//0x0000);
			message.appendCString(srcMsg);
			// byte k[] = GSMAlphabet.stringTo7BitsBytes(srcMsg);
			// message.appendBytes(k, k.length);

			// We send all the data as a short_message
			debug += "C351C351";
			debug += srcMsg;
			request.setShortMessageData(message);
			// OR as a message_payload: (used for GPC only)
			// request.setMessagePayload(message);

			// Esme class to indicate that the message contains UDH
			request.setEsmClass((byte) (Data.SM_UDH_GSM));

			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Seq number
			request.setSequenceNumber(Integer.parseInt(emsId.toString() + 1));

			// Set DeliveryReport
			request.setRegisteredDelivery(registeredDelivery);

			// ////////////////////end aaa
			// create a buffer for current message payload
			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Trung DK add SourceSubaddress
			ByteBuffer Charge = new ByteBuffer();
			// Charge.appendShort( (short) 0x000C);
			Charge.appendByte((byte) 0xA0);
			Charge.appendString(destAddr);
			if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
					&& (MsgType == 1)) {
				request.setSourceSubaddress(Charge);
			}

			String DestSubAddress = "";

			if (MsgType != 3) {
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
				} else
					request.setSfoneChargeFlag((short) 0x0000);
			}
			// request.setSfoneChargeFlag((short) 0x0000);
			Logger.info("SMSCTools.buildSubmitSM", "{RequestID=" + RequestID
					+ "}{srcAddr=" + srcAddr + "}{destAddr=" + destAddr
					+ "}{setDestSubaddress=" + DestSubAddress
					+ "} {chargeflag=" + chargeflag + "} ");

			return request;
		} catch (Exception e) {
			// System.out.println(".: " + e.getMessage());
			return null;
		}
	}

	// Dalink VSSA
	public static SubmitSM buildSubmitDalinkVSSA(String srcMsg, String srcAddr,
			String destAddr, BigDecimal emsId, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments)
			throws EMSException {
		SubmitSM request = new SubmitSM();
		try {
			// set the source and destination phone numbers
			request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
			request.setDestAddr((byte) 1, (byte) 1, destAddr);
			String debug = "";
			// create a buffer for current message payload
			ByteBuffer message = new ByteBuffer();
			// UDH length

			// message.appendByte((byte)(srcMsg.length()+8));
			if (Integer.toHexString(srcMsg.length() + 8).length() < 2) {
				debug += "0" + Integer.toHexString(srcMsg.length() + 8);
			} else {
				debug += Integer.toHexString(srcMsg.length() + 8);
			}

			// message.appendByte( (byte) 0x06);
			message.appendByte((byte) 0x06);
			// IE Identifier -- 5 means that the following will
			// be destination and originator port numbers
			message.appendByte((byte) 0x05);
			// IE Data Length -- the length of the IE
			// two ports per two bytes = 4
			message.appendByte((byte) 0x04);
			debug += "060504";
			// Dalink parameter
			// message.appendByte( (byte) 0x060504);
			// destination port number ?
			message.appendShort((short) 0xc352);
			// message.appendShort( (short) 0x020B);//0x23F4);
			// source port number ?
			message.appendShort((short) 0xc352);
			// message.appendShort( (short) 0x020A);//0x0000);
			message.appendCString(srcMsg);
			// byte k[] = GSMAlphabet.stringTo7BitsBytes(srcMsg);
			// message.appendBytes(k, k.length);

			// We send all the data as a short_message
			debug += "C351C351";
			debug += srcMsg;
			request.setShortMessageData(message);
			// OR as a message_payload: (used for GPC only)
			// request.setMessagePayload(message);

			// Esme class to indicate that the message contains UDH
			request.setEsmClass((byte) (Data.SM_UDH_GSM));

			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Seq number
			request.setSequenceNumber(Integer.parseInt(emsId.toString() + 1));

			// Set DeliveryReport
			request.setRegisteredDelivery(registeredDelivery);
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
			// System.out.println(".: " + e.getMessage());
			return null;
		}
	}

	// Dalink VSSA
	public static SubmitSM buildSubmitMC8000(String srcMsg, String srcAddr,
			String destAddr, BigDecimal emsId, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments)
			throws EMSException {
		SubmitSM request = new SubmitSM();
		try {
			// set the source and destination phone numbers
			request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
			request.setDestAddr((byte) 1, (byte) 1, destAddr);
			String debug = "";
			// create a buffer for current message payload
			ByteBuffer message = new ByteBuffer();
			// UDH length

			// message.appendByte((byte)(srcMsg.length()+8));
			if (Integer.toHexString(srcMsg.length() + 8).length() < 2) {
				debug += "0" + Integer.toHexString(srcMsg.length() + 8);
			} else {
				debug += Integer.toHexString(srcMsg.length() + 8);
			}

			// message.appendByte( (byte) 0x06);
			message.appendByte((byte) 0x06);
			// IE Identifier -- 5 means that the following will
			// be destination and originator port numbers
			message.appendByte((byte) 0x05);
			// IE Data Length -- the length of the IE
			// two ports per two bytes = 4
			message.appendByte((byte) 0x04);
			debug += "060504";
			// Dalink parameter
			// message.appendByte( (byte) 0x060504);
			// destination port number ?
			message.appendShort((short) 0x1D51);
			// message.appendShort( (short) 0x020B);//0x23F4);
			// source port number ?
			message.appendShort((short) 0x0000);
			// message.appendShort( (short) 0x020A);//0x0000);
			message.appendCString(srcMsg);
			// byte k[] = GSMAlphabet.stringTo7BitsBytes(srcMsg);
			// message.appendBytes(k, k.length);

			// We send all the data as a short_message
			debug += "C351C351";
			debug += srcMsg;
			request.setShortMessageData(message);
			// OR as a message_payload: (used for GPC only)
			// request.setMessagePayload(message);

			// Esme class to indicate that the message contains UDH
			request.setEsmClass((byte) (Data.SM_UDH_GSM));

			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Seq number
			request.setSequenceNumber(Integer.parseInt(emsId.toString() + 1));

			// Set DeliveryReport
			request.setRegisteredDelivery(registeredDelivery);
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
			// System.out.println(".: " + e.getMessage());
			return null;
		}
	}

	// KARAOKE

	public static SubmitSM buildSubmitKARAOKE(String srcMsg, String srcAddr,
			String destAddr, BigDecimal emsId, byte registeredDelivery,
			int MsgType, String sCommand_code, int TotalSegments) throws

	EMSException {
		SubmitSM request = new SubmitSM();
		try {
			// set the source and destination phone numbers
			request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
			request.setDestAddr((byte) 1, (byte) 1, destAddr);
			String debug = "";
			// create a buffer for current message payload
			ByteBuffer message = new ByteBuffer();
			// UDH length

			// message.appendByte((byte)(srcMsg.length()+8));
			if (Integer.toHexString(srcMsg.length() + 8).length() < 2) {
				debug += "0" + Integer.toHexString(srcMsg.length() + 8);
			} else {
				debug += Integer.toHexString(srcMsg.length() + 8);
			}

			// message.appendByte( (byte) 0x06);
			message.appendByte((byte) 0x06);
			// IE Identifier -- 5 means that the following will
			// be destination and originator port numbers
			message.appendByte((byte) 0x05);
			// IE Data Length -- the length of the IE
			// two ports per two bytes = 4
			message.appendByte((byte) 0x04);
			debug += "060504";
			// Dalink parameter
			// message.appendByte( (byte) 0x060504);
			// destination port number ?
			message.appendShort((short) 0x1D4C);
			// message.appendShort( (short) 0x020B);//0x23F4);
			// source port number ?
			message.appendShort((short) 0x1D4C);
			// message.appendShort( (short) 0x020A);//0x0000);
			message.appendCString(srcMsg);
			// byte k[] = GSMAlphabet.stringTo7BitsBytes(srcMsg);
			// message.appendBytes(k, k.length);

			// We send all the data as a short_message
			debug += "C351C351";
			debug += srcMsg;
			request.setShortMessageData(message);
			// OR as a message_payload: (used for GPC only)
			// request.setMessagePayload(message);

			// Esme class to indicate that the message contains UDH
			request.setEsmClass((byte) (Data.SM_UDH_GSM));

			// the coding is GSM specific
			request.setDataCoding((byte) 0xF5);

			// Seq number
			request.setSequenceNumber(Integer.parseInt(emsId.toString() + 1));

			// Set DeliveryReport
			request.setRegisteredDelivery(registeredDelivery);
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
			// System.out.println(".: " + e.getMessage());
			return null;
		}
	}

	// Returns a collection of SubmitSM objects
	public static Collection buildSubmitEMS(ByteBuffer otaData, String srcAddr,
			String destAddr, int contentType, BigDecimal emsId,
			byte registeredDelivery, int MsgType, String sCommand_code,
			int TotalSegments) throws EMSException {
		SubmitSM request = null;
		Vector vRequests = new Vector();

		String mobileOperator = Preference.mobileOperator;
		/*
		 * if (destAddr.startsWith("8490") || destAddr.startsWith("8493")) {
		 * mobileOperator = "VMS"; } else if (destAddr.startsWith("8491") ||
		 * destAddr.startsWith("8494")) { mobileOperator = "GPC"; } else if
		 * (destAddr.startsWith("8498") || destAddr.startsWith("8497")) {
		 * mobileOperator = "VIETEL"; } else if (destAddr.startsWith("8495")) {
		 * mobileOperator = "SFONE"; } else if (destAddr.startsWith("8492")) {
		 * mobileOperator = "HTC"; } else if (destAddr.startsWith("8496")) {
		 * mobileOperator = "EVN"; } else { mobileOperator = "EVN"; }
		 */

		otaData = addHeaders2OTAData(otaData, mobileOperator, contentType);
		try {
			// Calculate # of fragments
			// 1--133: 1 message
			// 134--256: 2 messages
			if (otaData.length() <= 133) {
				MAX_LENGTH = 133;
				isFragmented = false;
				totalSegments = 1;
			} else { // > 133
				MAX_LENGTH = 128;
				isFragmented = true;
				totalSegments = (byte) (otaData.length() / MAX_LENGTH + 1);
			}

			ByteBuffer message = null;
			for (byte i = 1; i <= totalSegments; i++) {
				request = new SubmitSM();
				// set the source and destination phone numbers
				request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
				request.setDestAddr((byte) 1, (byte) 1, destAddr);
				// request.setSourceAddr(srcAddr);
				// request.setDestAddr(destAddr);

				// create a buffer for current message payload
				message = new ByteBuffer();

				// UDH is needed to tell the mobile phone details
				// how to deliver the data in the message payload
				// first goes UDH length -- this UDH will have 6 bytes
				if (isFragmented) {
					message.appendByte((byte) 0x0B);
				} else {
					message.appendByte((byte) 0x06);
				}

				// then goes IE -- information element
				// IE Identifier -- 5 means that the following will
				// be destination and originator port numbers
				message.appendByte((byte) 0x05);

				// IE Data Length -- the length of the IE
				// two ports per two bytes = 4
				message.appendByte((byte) 0x04);

				// Destination port number ?
				switch (contentType) {
				case Constants.CT_CLI_ICON:
					message.appendShort(Constants.PORT_CLI_ICON);
					break;
				case Constants.CT_OPER_LOGO:
					message.appendShort(Constants.PORT_OPER_LOGO);
					break;
				case Constants.CT_PIC_MSG:
					message.appendShort(Constants.PORT_PIC_MSG);
					break;
				case Constants.CT_RING_TONE:
					message.appendShort(Constants.PORT_RING_TONE);
					break;
				case Constants.CT_WAP_SI:
				case Constants.CT_MMS_NOTIFY:
					message.appendShort(Constants.PORT_WAP_PUSH);
					break;
				case Constants.CT_WAP_BROWSER:
					message.appendShort(Constants.PORT_WAP_BROWSER);
					break;
				case Constants.CT_VCARD:
					message.appendShort(Constants.PORT_VCARD);
					break;
				case Constants.CT_VCALENDAR:
					message.appendShort(Constants.PORT_VCALENDAR);
					break;
				default:
					throw new EMSException("Invalid content type "
							+ contentType + ")");
				}
				// Originator port (unused in fact)
				if (contentType == Constants.CT_WAP_SI
						|| contentType == Constants.CT_MMS_NOTIFY) {
					message.appendShort(Constants.PORT_WAP_CONNECTIONLESS);
				} else if (contentType == Constants.CT_WAP_BROWSER) {
					message.appendShort(Constants.PORT_WAP_BSOURCE);
				} else {
					message.appendShort((short) 0x0000);
				}
				/**
				 * SAR part (IEI=00): There are three parameters for doing this:
				 * SAR_MSG_REF_NUM: reference number of the concatenated
				 * message. SAR_TOTAL_SEGMENTS: total number of segments.
				 * SAR_SEGMENT_SEQNUM: number of actual segment.
				 * 
				 * 00 03 00 02 01
				 */
				if (isFragmented) {
					// IE Identifier Concatenated short messages, 8 bit ref
					// number
					message.appendByte((byte) 0x00);
					// IE Data Length is always 3 decimal
					message.appendByte((byte) 0x03);
					// IE Data as 3 octets
					// 1. Ref number for this concatenated message, 0 decimal
					message.appendByte((byte) 0x00);
					// 2. # of segments
					message.appendByte((byte) totalSegments);
					// 3. sequence # of current segment, start at 1
					message.appendByte((byte) i);
				}

				// Append the ota data to the message payload
				if (otaData.length() <= MAX_LENGTH) {
					message.appendBuffer(otaData);
				} else {
					message.appendBuffer(otaData.removeBuffer(MAX_LENGTH));
				}

				// we send all the data as a short_message
				request.setShortMessageData(message); // used for both GPC and
				// VMS
				// OR as a message_payload: (used for GPC only)
				// request.setMessagePayload(message);

				// you must set the esme class to indicate that the message
				// contains UDH
				request.setEsmClass((byte) (Data.SM_UDH_GSM)); // 0x40: UDHI
				// used +
				// default
				// messaging
				// mode
				// OR:
				// request.setEsmClass( (byte) (0x40 + 0x03)); //0x03: Store and
				// Forward mode

				// the coding is GSM specific
				request.setDataCoding((byte) 0xF5); // or 0x04 or 0x15: binary
				// encoding

				// Seqnumber is made from emsId and segment_seqnum:
				// <emsId><segment_seqnum>
				// e.g: emsId = 100; There are 3 segments:
				// --> seqnum = 1001, 1002, and 1003
				String s_emsId = emsId.toString();
				request.setSequenceNumber(Integer.parseInt(s_emsId + i));
				// Trung DK add SourceSubaddress
				/*
				 * if ((MsgType == 2) || ((MsgType > 19) && (MsgType < 30))) {
				 * MsgType = 0; } else if (((MsgType == 1) || (MsgType == 3)) &&
				 * (i == 1)) { MsgType = 1; } else { MsgType = 4; } if
				 * ((srcAddr.equals("996")) && (MsgType == 0) &&
				 * ((sCommand_code.equals("XSMN")) ||
				 * (sCommand_code.equals("XSMT")) ||
				 * (sCommand_code.equals("XSMN")) || (sCommand_code
				 * .equals("XSTD")))) { MsgType = 4; }
				 */
				if (TotalSegments == 0) {
					TotalSegments = 1;
				}
				ByteBuffer Charge = new ByteBuffer();
				// Charge.appendShort( (short) 0x000C);
				Charge.appendByte((byte) 0xA0);
				Charge.appendString(sCommand_code + "," + MsgType + ","
						+ TotalSegments);
				request.setSourceSubaddress(Charge);

				// Set DeliveryReport
				if (i == totalSegments) {
					request.setRegisteredDelivery(registeredDelivery);
				}
				vRequests.addElement(request);
			} // end for

			return vRequests;
		} catch (Exception e) {
			throw new EMSException(e.getMessage());
		}
	}

	// Creates a collection of SubmitSM(es) from a file (OTA file)
	public static Collection buildSubmitEMS(String filename, String srcAddress,
			String destAddress, int contentType, int MsgType,
			String sCommand_code, int TotalSegments) throws EMSException {
		SubmitSM request = null;
		Vector vRequests = new Vector();
		try {
			// Load the OTA data into a buffer.
			ByteBuffer otaData = null;

			short port = 0;
			switch (contentType) {
			case Constants.CT_CLI_ICON:
				port = Constants.PORT_CLI_ICON;
				Cli cli = new Cli(filename);
				cli.encode();
				otaData = cli.getEncoded();
				break;
			case Constants.CT_OPER_LOGO:
				port = Constants.PORT_OPER_LOGO;
				OperatorLogo logo = new OperatorLogo(filename);
				/*
				 * conglt20080530 if (destAddress.startsWith("8490") ||
				 * destAddress.startsWith("8493")) {
				 * logo.setMobileOperator("VMS"); } else if
				 * (destAddress.startsWith("8491") ||
				 * destAddress.startsWith("8494")) {
				 * logo.setMobileOperator("GPC"); } else if
				 * (destAddress.startsWith("8498") ||
				 * destAddress.startsWith("8497")) {
				 * logo.setMobileOperator("VIETEL"); } else if
				 * (destAddress.startsWith("8495")) {
				 * logo.setMobileOperator("SFONE"); } else if
				 * (destAddress.startsWith("8492")) {
				 * logo.setMobileOperator("HTC"); } else if
				 * (destAddress.startsWith("8496")) {
				 * logo.setMobileOperator("EVN"); } else {
				 * logo.setMobileOperator("EVN"); }
				 */
				logo.setMobileOperator(Preference.mobileOperator);
				logo.encode();
				otaData = logo.getEncoded();
				break;
			case Constants.CT_PIC_MSG:
				port = Constants.PORT_PIC_MSG;
				PictureMessage pic = new PictureMessage(filename);
				pic.encode();
				otaData = pic.getEncoded();
				break;
			case Constants.CT_RING_TONE:
				port = Constants.PORT_RING_TONE;
				otaData = loadByteBuffer(filename);
				break;
			default:
				throw new EMSException("Invalid content type " + contentType
						+ ")");
			}

			// Calculate # of fragments
			// 1--133: 1 message
			// 134--256: 2 messages
			if (otaData.length() <= 133) {
				MAX_LENGTH = 133;
				isFragmented = false;
				totalSegments = 1;
			} else { // > 133
				MAX_LENGTH = 128;
				isFragmented = true;
				totalSegments = (byte) (otaData.length() / MAX_LENGTH + 1);
			}

			ByteBuffer message = null;
			for (byte i = 1; i <= totalSegments; i++) {
				request = new SubmitSM();
				// set the source and destination phone numbers
				// request.setSourceAddr(srcAddress);
				// request.setDestAddr(destAddress);
				request.setSourceAddr((byte) 0, (byte) 0, srcAddress);
				request.setDestAddr((byte) 1, (byte) 1, destAddress);

				// create a buffer for current message payload
				message = new ByteBuffer();

				// UDH is needed to tell the mobile phone details
				// how to deliver the data in the message payload
				// first goes UDH length -- this UDH will have 6 bytes
				if (isFragmented) {
					message.appendByte((byte) 0x0B);
				} else {
					message.appendByte((byte) 0x06);
				}

				// then goes IE -- information element
				// IE Identifier -- 5 means that the following will
				// be destination and originator port numbers
				message.appendByte((byte) 0x05);

				// IE Data Length -- the length of the IE
				// two ports per two bytes = 4
				message.appendByte((byte) 0x04);

				// destination port number ?
				// message.appendShort( (short) 0x1581);//RINGTONE
				message.appendShort((short) port);

				// originator port (unused in fact)
				message.appendShort((short) 0x0000);

				// SAR part
				// 00 03 00 02 01
				if (isFragmented) {
					// IE Identifier Concatenated short messages, 8 bit ref
					// number
					message.appendByte((byte) 0x00);
					// IE Data Length is always 3 decimal
					message.appendByte((byte) 0x03);
					// IE Data as 3 octets
					// 1. Ref number for this concatenated message, 0 decimal
					message.appendByte((byte) 0x00);
					// 2. # of segments
					message.appendByte((byte) totalSegments);
					// 3. sequence # of current segment, start at 1
					message.appendByte((byte) i);
				}

				// Append the ring tone data to the message payload
				if (otaData.length() <= MAX_LENGTH) {
					message.appendBuffer(otaData);
				} else {
					message.appendBuffer(otaData.removeBuffer(MAX_LENGTH));
				}

				// we send all the data as a message payload
				request.setShortMessageData(message); // used for both GPC and
				// VMS
				// request.setMessagePayload(message);

				// you must set the esme class to indicate that the message
				// contains UDH
				request.setEsmClass((byte) (Data.SM_UDH_GSM)); // 0x40: UDHI
				// used
				// request.setEsmClass( (byte) (0x40 + 0x03)); //0x03: Store and
				// Forward mode

				// the coding is GSM specific
				request.setDataCoding((byte) 0xF5); // or 0x04 or 0x15: binary
				// encoding
				// request.setDataCoding( (byte) 0x04);

				// Set DeliveryReport
				// if(i == totalSegments) {
				// request.setRegisteredDelivery((byte)1);
				// }
				// request.setServiceType("WAP"); //GEN - for GPC, CMT - for VMS
				// Trung sua WAPPUSH EVN

				request.setSequenceNumber(i + 1); // start at 2
				// Trung DK add SourceSubaddress
				if ((MsgType == 2) || ((MsgType > 19) && (MsgType < 30))) {
					MsgType = 0;
				} else if (((MsgType == 1) || (MsgType == 3)) && (i == 1)) {
					MsgType = 1;
				} else {
					MsgType = 4;
				}
				if ((srcAddress.equals("996"))
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

				vRequests.addElement(request);
			}
			return vRequests;
		} catch (Exception e) {
			throw new EMSException(e.getMessage());
		}
	}

	// for your convenience here is the code for loading
	// of the data from a file
	public static ByteBuffer loadByteBuffer(String fileName) throws IOException {
		FileInputStream is = new FileInputStream(fileName);
		byte[] data = new byte[is.available()];
		is.read(data);
		is.close();
		return new ByteBuffer(data);
	}

	public static String[] splString(String strInput, int strLen) {
		try {
			int totalSegments = strInput.length() / strLen;
			totalSegments++;
			String[] strtemp = new String[totalSegments];
			for (int j = 0; j < strtemp.length; j++)
				strtemp[j] = "";

			String temp = "";
			int itemp = 0;
			for (int i = 0; i < strInput.length(); i++) {

				strtemp[itemp] = strtemp[itemp] + strInput.charAt(i);
				if (strtemp[itemp].length() >= strLen)
					itemp++;

			}
			return strtemp;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	// / long message text
	public static Collection buildSubmitLongMsg(String strData, String srcAddr,
			String destAddr, int contentType, BigDecimal emsId,
			byte registeredDelivery, int MsgType, String commandcode)
			throws EMSException {
		SubmitSM request = null;
		Vector vRequests = new Vector();

		ByteBuffer otaData = new ByteBuffer();
		otaData.appendCString(strData);

		Gateway.util.log("buildSubmitLongMsg", "{strData=" + strData + "}");

		try {

			String[] splittedMsg = splString(strData, 153);

			int totalSegs = splittedMsg.length;

			for (int i = 0; i < totalSegs; i++) {

				request = new SubmitSM();
				// set the source and destination phone numbers
				request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
				request.setDestAddr((byte) 1, (byte) 1, destAddr);
				request.setEsmClass((byte) (Data.SM_UDH_GSM)); // 0x40: UDHI

				ByteBuffer message = new ByteBuffer();

				message.appendByte((byte) 0x05);
				// IE Identifier Concatenated short messages, 8 bit ref
				// number
				message.appendByte((byte) 0x00);
				// IE Data Length is always 3 decimal
				message.appendByte((byte) 0x03);
				// IE Data as 3 octets
				// 1. Ref number for this concatenated message, 0 decimal
				message.appendByte((byte) 0x00);
				// 2. # of segments
				message.appendByte((byte) totalSegs);
				// 3. sequence # of current segment, start at 1
				message.appendByte((byte) (i + 1));

				message.appendString(splittedMsg[i], Data.ENC_ISO8859_1);

				Gateway.util.log("buildSubmitLongMsg", "{splittedMsg[" + i
						+ "]=" + splittedMsg[i] + "}");

				request.setShortMessageData(message); // used for both GPC and

				String s_emsId = emsId.toString();
				request.setSequenceNumber(Integer.parseInt(s_emsId + i));
				String servicetype = "";
				if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
					servicetype = "LO";
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

				if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
					if (i == 1) {
						if ((MsgType == 1)) {
							request.setLanguageIndicator((byte) 0x01);
						} else {
							request.setLanguageIndicator((byte) 0x00);
						}
					} else {
						request.setLanguageIndicator((byte) 0x00);
					}

				}

				ByteBuffer Charge = new ByteBuffer();
				// Charge.appendShort( (short) 0x000C);
				if ("1".equalsIgnoreCase(Preference.SFONE_ACTIVE)) {
					Charge.appendByte((byte) 0xA0);
					Charge.appendString(destAddr);
					if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
							&& (MsgType == 1)) {
						request.setSourceSubaddress(Charge);
					}
				} else {
					/*
					 * if ((MsgType == 2)) { MsgType = 0; } else if ((MsgType ==
					 * 1)) { MsgType = 1; } else { MsgType = 4; }
					 * 
					 * if (i != totalSegs) MsgType = 4; Charge.appendByte((byte)
					 * 0xA0); Charge.appendString(commandcode + "," + MsgType +
					 * "," + totalSegs); request.setSourceSubaddress(Charge);
					 */
				}

				// Set DeliveryReport
				if (i == totalSegs) {
					request.setRegisteredDelivery(registeredDelivery);
				}

				Gateway.util.log("buildSubmitLongMsg",
						"{request=" + request.debugString() + "}");
				vRequests.addElement(request);
			} // end for
			return vRequests;
		} catch (Exception e) {
			throw new EMSException(e.getMessage());
		}
	}

	// / long message text
	public static Collection buildSubmitOMA(String strData, String srcAddr,
			String destAddr, int contentType, BigDecimal emsId,
			byte registeredDelivery, int MsgType, String commandcode)
			throws EMSException {
		SubmitSM request = null;
		Vector vRequests = new Vector();

		ByteBuffer otaData = new ByteBuffer();
		otaData.appendCString(strData);

		try {

			String[] splittedMsg = splString(strData, 256);

			int totalSegs = splittedMsg.length;

			for (int i = 0; i < totalSegs; i++) {

				request = new SubmitSM();
				// set the source and destination phone numbers
				request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
				request.setDestAddr((byte) 1, (byte) 1, destAddr);
				request.setEsmClass((byte) (Data.SM_UDH_GSM)); // 0x40: UDHI

				ByteBuffer message = new ByteBuffer();

				// 0B05040B8423F00003040401
				// 0B 05 04 0B 84 23 F0 00 03 04 04 01
				// 0B 05 04 0B 84 23 F0 00 03 04 04
				message.appendByte((byte) 0x0B);
				message.appendByte((byte) 0x05);
				message.appendByte((byte) 0x04);
				message.appendByte((byte) 0x0B);
				message.appendByte((byte) 0x84);
				message.appendByte((byte) 0x23);
				message.appendByte((byte) 0xF0);
				message.appendByte((byte) 0x00);
				message.appendByte((byte) 0x03);
				message.appendByte((byte) 0x04);
				message.appendByte((byte) totalSegs);
				message.appendByte((byte) (i + 1));

				// /
				byte[] b = new byte[splittedMsg[i].length() / 2];

				b = HexaTool.fromHexString(splittedMsg[i]);

				ByteBuffer ota2build = new ByteBuffer(b);

				// message.appendString(splittedMsg[i], Data.ENC_ISO8859_1);

				message.appendBuffer(ota2build);

				// //

				request.setShortMessageData(message); // used for both GPC and

				String s_emsId = emsId.toString();
				request.setSequenceNumber(Integer.parseInt(s_emsId + i));
				String servicetype = "";
				if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
					servicetype = "LO";
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

				if (Preference.mobileOperator.equalsIgnoreCase("BEELINE")) {
					if (i == 1) {
						if ((MsgType == 1)) {
							request.setLanguageIndicator((byte) 0x01);
						} else {
							request.setLanguageIndicator((byte) 0x00);
						}
					} else {
						request.setLanguageIndicator((byte) 0x00);
					}

				}

				ByteBuffer Charge = new ByteBuffer();
				// Charge.appendShort( (short) 0x000C);
				if ("1".equalsIgnoreCase(Preference.SFONE_ACTIVE)) {
					Charge.appendByte((byte) 0xA0);
					Charge.appendString(destAddr);
					if ("SFONE".equalsIgnoreCase(Preference.mobileOperator)
							&& (MsgType == 1)) {
						request.setSourceSubaddress(Charge);
					}
				} else {
					/*
					 * if ((MsgType == 2)) { MsgType = 0; } else if ((MsgType ==
					 * 1)) { MsgType = 1; } else { MsgType = 4; }
					 * 
					 * if (i != totalSegs) MsgType = 4; Charge.appendByte((byte)
					 * 0xA0); Charge.appendString(commandcode + "," + MsgType +
					 * "," + totalSegs); request.setSourceSubaddress(Charge);
					 */
				}

				// Set DeliveryReport
				if (i == totalSegs) {
					request.setRegisteredDelivery(registeredDelivery);
				}

				// the coding is GSM specific
				request.setDataCoding((byte) 0xF5);

				Gateway.util.log("buildSubmitLongMsg",
						"{request=" + request.debugString() + "}");
				vRequests.addElement(request);
			} // end for
			return vRequests;
		} catch (Exception e) {
			throw new EMSException(e.getMessage());
		}
	}

	public static Collection buildSubmitLongMsg2(String strData,
			String srcAddr, String destAddr, int contentType, BigDecimal emsId,
			byte registeredDelivery) throws EMSException {
		SubmitSM request = null;
		Vector vRequests = new Vector();

		String mobileOperator = Preference.mobileOperator;

		ByteBuffer otaData = new ByteBuffer();
		otaData.appendCString(strData);
		// otaData = addHeaders2OTAData(otaData, mobileOperator, contentType);
		try {
			// Calculate # of fragments
			// 1--133: 1 message
			// 134--256: 2 messages
			if (otaData.length() <= 133) {
				MAX_LENGTH = 133;
				isFragmented = false;
				totalSegments = 1;
			} else { // > 133
				MAX_LENGTH = 128;
				isFragmented = true;
				totalSegments = (byte) (otaData.length() / MAX_LENGTH + 1);
			}

			ByteBuffer message = null;
			for (byte i = 1; i <= totalSegments; i++) {
				request = new SubmitSM();
				// set the source and destination phone numbers
				request.setSourceAddr((byte) 0, (byte) 1, srcAddr);
				request.setDestAddr((byte) 1, (byte) 1, destAddr);
				// request.setSourceAddr(srcAddr);
				// request.setDestAddr(destAddr);

				// create a buffer for current message payload
				message = new ByteBuffer();

				// UDH is needed to tell the mobile phone details
				// how to deliver the data in the message payload
				// first goes UDH length -- this UDH will have 6 bytes
				if (isFragmented) {
					message.appendByte((byte) 0x0B);
				} else {
					message.appendByte((byte) 0x06);
				}

				// then goes IE -- information element
				// IE Identifier -- 5 means that the following will
				// be destination and originator port numbers
				message.appendByte((byte) 0x05);

				// IE Data Length -- the length of the IE
				// two ports per two bytes = 4
				message.appendByte((byte) 0x04);

				/**
				 * SAR part (IEI=00): There are three parameters for doing this:
				 * SAR_MSG_REF_NUM: reference number of the concatenated
				 * message. SAR_TOTAL_SEGMENTS: total number of segments.
				 * SAR_SEGMENT_SEQNUM: number of actual segment.
				 * 
				 * 00 03 00 02 01
				 */
				if (isFragmented) {
					// IE Identifier Concatenated short messages, 8 bit ref
					// number
					message.appendByte((byte) 0x00);
					// IE Data Length is always 3 decimal
					message.appendByte((byte) 0x03);
					// IE Data as 3 octets
					// 1. Ref number for this concatenated message, 0 decimal
					message.appendByte((byte) 0x00);
					// 2. # of segments
					message.appendByte((byte) totalSegments);
					// 3. sequence # of current segment, start at 1
					message.appendByte((byte) i);
				}

				// Append the ota data to the message payload
				// conglt
				// if (strData.length() <= MAX_LENGTH) {
				// message.appendBuffer(strData);
				// } else {
				// message.appendBuffer(strData.removeBuffer(MAX_LENGTH));
				// }

				// we send all the data as a short_message
				request.setShortMessageData(message); // used for both GPC and
				// VMS
				// OR as a message_payload: (used for GPC only)
				// request.setMessagePayload(message);

				// you must set the esme class to indicate that the message
				// contains UDH
				request.setEsmClass((byte) (Data.SM_UDH_GSM)); // 0x40: UDHI
				// used +
				// default
				// messaging
				// mode
				// OR:
				// request.setEsmClass( (byte) (0x40 + 0x03)); //0x03: Store and
				// Forward mode

				// the coding is GSM specific
				request.setDataCoding((byte) 0xF5); // or 0x04 or 0x15: binary
				// encoding

				// Seqnumber is made from emsId and segment_seqnum:
				// <emsId><segment_seqnum>
				// e.g: emsId = 100; There are 3 segments:
				// --> seqnum = 1001, 1002, and 1003
				String s_emsId = emsId.toString();
				request.setSequenceNumber(Integer.parseInt(s_emsId + i));

				// Set DeliveryReport
				if (i == totalSegments) {
					request.setRegisteredDelivery(registeredDelivery);
				}
				vRequests.addElement(request);
			} // end for
			return vRequests;
		} catch (Exception e) {
			throw new EMSException(e.getMessage());
		}
	}

}
