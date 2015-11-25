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

import java.math.*;
import java.sql.*;
import java.util.*;

import java.io.Serializable;

/**
 * This class represents the data of the EMS_SEND_QUEUE or EMS_SEND_LOG
 * 
 */
public class EMSData implements Serializable {
	// private Logger logger = new Logger("EMSData");

	private BigDecimal id = null; // id of message in send_queue or send_log
	private String user_id = null; // user_id: src_address, 849xxx
	private String service_id = null; // service_id: dest_address, 997,
	private String mobile_operator = null; // e.g: GPC, VMS, VIETEL
	private String command_code = null; // ma yeu cau,e.g: TONE, LOGO,...

	// Type of EMS (Ref. Constants.java):
	// = 0-Text (default)
	// = 1-Ringtone (NBS port: 0x1581)
	// = 2-Operator Logo (NBS port: 0x1582)
	// = 3-CLI (NBS port: 0x1583)
	// = 4-Picture Message (NBS port: 0x158A)
	// = 5-Cute text (postcard).
	private int content_type = 0;

	private String text = ""; // info
	private byte[] binary = null; // raw info
	private Timestamp submit_date = null; // Thoi diem gui tin den SMSC
	private Timestamp done_date = null; // Thoi diem thue bao nhan duoc tin nhan
	private int process_result = 0;

	// Message_Type:
	// = 0-La tin nhan xuat phat tu he thong
	// = 1-Tin nhan tra loi cho 1 msg dung format
	// = 2-Tin nhan tra loi cho 1 msg sai format
	private int message_type = 0; // in decimal format
	private BigDecimal request_id = null; // id of request message, in
											// SEND_LOG
	private String message_id = null; // id of message in the SMSC
	private int total_Segments = 0;
	private int send_num = 0;
	private String sRequestid = "";
	private String sNotes = "";
	private int cpid = 0;

	public int getCpid() {
		return cpid;
	}

	public void setCpid(int cpid) {
		this.cpid = cpid;
	}

	public BigDecimal getId() {
		return id;
	}

	public String getUserId() {
		return user_id;
	}

	public String getsRequestID() {
		return sRequestid;
	}

	public void setsRequestID(String values) {
		this.sRequestid = values;
	}

	public String getServiceId() {
		return service_id;
	}

	public String getMobileOperator() {
		return mobile_operator;
	}

	public String getCommandCode() {
		return command_code;
	}

	public int getContentType() {
		return content_type;
	}

	public byte[] getBytes() {
		return binary;
	}

	public String getText() {
		if ((content_type == Constants.CT_TEXT) && (text != null)) {
			text = StringTool.removeChar(text, '\r');
			text = (text.length() > 160) ? text.substring(0, 160 + 1) : text; // max
																				// 160
																				// chars
		}
		return text;
	}

	public Timestamp getSubmitDate() {
		if (submit_date == null) {
			return new Timestamp(System.currentTimeMillis());
		}
		return submit_date;
	}

	public Timestamp getDoneDate() {
		if (done_date == null) {
			return new Timestamp(System.currentTimeMillis());
		}
		return done_date;
	}

	public int getProcessResult() {
		return process_result;
	}

	public int getMessageType() {
		return message_type;
	}

	public BigDecimal getRequestId() {
		return request_id;
	}

	public String getMessageId() {
		return message_id;
	}

	public int getTotalSegments() {
		return total_Segments;
	}

	// Get userId in defferent format: 84, 09,& 9.
	public String getUserIdEx(int formatType) {
		return formatUserId(user_id, formatType);
	}

	// Get serviceId in defferent format: 84, or shortcode
	public String getServiceIdEx(int formatType) {
		return formatServiceId(service_id, formatType);
	}

	// Get Info with or without CRLF
	public String getTextEx(boolean noCRLF) {
		if (!noCRLF || text == null) {
			return text; // return message as it is
		} else {
			String temp = text.replace('\n', ' ');
			temp = temp.replace('\r', ' ');
			return temp;
		}
	}

	public String getNotes() {
		return this.sNotes;
	}

	public int getSendNum() {
		return send_num;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public void setUserId(String value) {
		if (value != null && value.length() > 0) {
			value.toLowerCase();
			value.replace('o', '0');
		}
		this.user_id = value;
	}

	public void setServiceId(String value) {
		if (value != null && value.length() > 0) {
			value.toLowerCase();
			value.replace('o', '0');
		}
		this.service_id = value;
	}

	public void setMobileOperator(String value) {
		this.mobile_operator = value;
	}

	public void setCommandCode(String value) {
		this.command_code = value;
	}

	public void setContentType(int value) {
		this.content_type = value;
	}

	// Note: text can be > 160 chars
	// (hexa for binary data)
	public void setText(String value) {
		this.text = value;
	}

	public void setBytes(byte[] value) {
		this.binary = value;
	}

	public void setSubmitDate(Timestamp value) {
		this.submit_date = value;
	}

	public void setDoneDate(Timestamp value) {
		this.done_date = value;
	}

	public void setProcessResult(int value) {
		this.process_result = value;
	}

	public void setMessageType(int value) {
		this.message_type = value;
	}

	public void setRequestId(BigDecimal value) {
		this.request_id = value;
	}

	public void setMessageId(String value) {
		this.message_id = value;
	}

	public void setTotalSegments(int value) {
		this.total_Segments = value;
	}

	public void setSendNum(int value) {
		this.send_num = value;
	}

	public void setNotes(String value) {
		this.sNotes = value;
	}

	public boolean isWaiting4Response() {
		if (submit_date != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isTimeout() {
		boolean result = false;
		if (submit_date != null) {
			long currTime = System.currentTimeMillis();
			if ((currTime - submit_date.getTime()) > Preference.timeResend) { // timeout
				result = true;
			}
		}
		return result;
	}

	public boolean isValidServiceId() {
		return Preference.isValidServiceId(this.service_id);
	}

	/*
	 * public boolean isValidServiceId() { String serviceId = this.service_id;
	 * if (serviceId == null || "".equals(serviceId)) { return false; } if
	 * (serviceId.equals("996") || serviceId.equals("84996") ||
	 * serviceId.equals("04996") || serviceId.equals("997") ||
	 * serviceId.equals("84997") || serviceId.equals("04997") ||
	 * serviceId.equals("998") || serviceId.equals("84998") ||
	 * serviceId.equals("04998") || serviceId.equals("19001255") ||
	 * serviceId.equals("8419001255") || serviceId.equals("0419001255") ||
	 * serviceId.equals("19001522") || serviceId.equals("8419001522") ||
	 * serviceId.equals("0419001522") || serviceId.equals("19001799") ||
	 * serviceId.equals("8419001799") || serviceId.equals("0419001799")) {
	 * return true; } else { return false; } }
	 */

	public boolean isValidUserId() {
		String userId = user_id;
		if (userId == null || "".equals(userId)) {
			return false;
		}
		if (((userId.startsWith("90") || userId.startsWith("91")
				|| userId.startsWith("98") || userId.startsWith("95")
				|| userId.startsWith("93") || userId.startsWith("94") || userId
				.startsWith("92")) && userId.length() == 9)
				|| ((userId.startsWith("090") || userId.startsWith("091")
						|| userId.startsWith("098") || userId.startsWith("097")
						|| userId.startsWith("095") || userId.startsWith("093")
						|| userId.startsWith("094") || userId.startsWith("092")) && userId
						.length() == 10)
				|| ((userId.startsWith("8490") || userId.startsWith("8491")
						|| userId.startsWith("8498")
						|| userId.startsWith("8497")
						|| userId.startsWith("8495")
						|| userId.startsWith("8493")
						|| userId.startsWith("8494")
						|| userId.startsWith("8492")
						|| (userId.startsWith("8496")) || userId
						.startsWith("096")
						&& userId.length() == 11))) {
			return true;
			// TrungDK EVN -> UNKNOWN
		} else if ("UNKNOWN".equalsIgnoreCase(Preference.mobileOperator)) {
			return true;
		} else {
			return true;
		}
	}

	public boolean isValidContentType() {
		if (content_type >= Constants.CT_VALUE_MIN
				&& content_type <= Constants.CT_VALUE_MAX) {
			return true;
		} else {
			return false;
		}
	}

	// Consider if the MOBILE_OPERATOR field is needed to rebuilt and updated
	// returns true if changed, else false;
	public boolean rebuildMobileOperator1() {
		String userId = user_id;
		if (userId == null || "".equals(userId)) {
			return false;
		}

		String new_mobile_operator = null;
		if (userId.startsWith("90") || userId.startsWith("090")
				|| userId.startsWith("8490") || userId.startsWith("93")
				|| userId.startsWith("093") || userId.startsWith("8493")) {
			if (!"VMS".equals(mobile_operator)) {
				new_mobile_operator = "VMS";
			}
		} else if (userId.startsWith("91") || userId.startsWith("091")
				|| userId.startsWith("8491") || userId.startsWith("94")
				|| userId.startsWith("094") || userId.startsWith("8494")) {
			if (!"GPC".equals(mobile_operator)) {
				new_mobile_operator = "GPC";
			}
		} else if (userId.startsWith("98") || userId.startsWith("098")
				|| userId.startsWith("8498") || userId.startsWith("97")
				|| userId.startsWith("097") || userId.startsWith("8497")) {
			if (!"VIETEL".equals(mobile_operator)) {
				new_mobile_operator = "VIETEL";
			}
		} else if (userId.startsWith("95") || userId.startsWith("095")
				|| userId.startsWith("8495")) {
			if (!"SFONE".equals(mobile_operator)) {
				new_mobile_operator = "SFONE";
			}
		} else if (userId.startsWith("92") || userId.startsWith("092")
				|| userId.startsWith("8492")) {
			if (!"HTC".equals(mobile_operator)) {
				new_mobile_operator = "HTC";
			}
		} else if (userId.startsWith("96") || userId.startsWith("096")
				|| userId.startsWith("8496")) {
			if (!"EVN".equals(mobile_operator)) {
				new_mobile_operator = "EVN";
			}
		} else

		if (new_mobile_operator != null) {
			this.mobile_operator = new_mobile_operator;
			return true;
		}
		return false;
	}

	public boolean isAddressToSend1() {
		boolean result = false;

		/**
		 * Check service_id(996, 997, 998, 19001255) Tr.hop 998, service_id co
		 * the la 998xxx; Khac 998, neu srcAddress != service_id --> khong phai
		 * cua gateway nay, ignore.
		 */
		for (Iterator it = Preference.sourceAddressList.iterator(); it
				.hasNext();) {
			String srcAddr = (String) it.next();
			if (service_id.startsWith(srcAddr)) {
				result = true;
				break;
			}
		}
		if (result == false) {
			return false;
		}

		// Check user_id
		if ("VMS".equals(Preference.mobileOperator)) {
			if (user_id.startsWith("90") || user_id.startsWith("090")
					|| user_id.startsWith("8490") || user_id.startsWith("93")
					|| user_id.startsWith("093") || user_id.startsWith("8493")) {
				result = true;
			}
		} else if ("GPC".equals(Preference.mobileOperator)) {
			if (user_id.startsWith("91") || user_id.startsWith("091")
					|| user_id.startsWith("8491") || user_id.startsWith("94")
					|| user_id.startsWith("094") || user_id.startsWith("8494")) {
				result = true;
			}
		} else if ("VIETEL".equals(Preference.mobileOperator)) {
			if (user_id.startsWith("98") || user_id.startsWith("098")
					|| user_id.startsWith("8498") || user_id.startsWith("97")
					|| user_id.startsWith("097") || user_id.startsWith("8497")) {
				result = true;
			}
		} else if ("SFONE".equals(Preference.mobileOperator)) {
			if (user_id.startsWith("95") || user_id.startsWith("095")
					|| user_id.startsWith("8495")) {
				result = true;
			}
		} else if ("HTC".equals(Preference.mobileOperator)) {
			if (user_id.startsWith("92") || user_id.startsWith("092")
					|| user_id.startsWith("8492")) {
				result = true;
			}
		} else if ("EVN".equals(Preference.mobileOperator)) {
			// if (user_id.startsWith("96") || user_id.startsWith("096") ||
			// user_id.startsWith("8496")) {
			{
				result = true;
			}
		} else {
			Logger.info("isAddressToSend: Invalid mobile_operator in the configuration file: "
							+ Preference.mobileOperator);
		}
		return result;
	}

	// Get userId in defferent format: 84, 09,& 9.
	public String formatUserId(String userId, int formatType) {
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
			break;
		case Constants.USERID_FORMAT_NATIONAL_NINE:
			if (temp.startsWith("84")) {
				temp = temp.substring(2);
			} else if (temp.startsWith("09")) {
				temp = temp.substring(1);
			} // else startsWith("9")
			break;
		case Constants.USERID_FORMAT_NATIONAL_ZERO:
			if (temp.startsWith("84")) {
				temp = "0" + temp.substring(2);
			} else if (temp.startsWith("9")) {
				temp = "0" + temp;
			} // else startsWith("09")
			break;
		default:
			//System.out.println("formatUserId: Invalid userId format_type "
			//		+ formatType);
			return temp; // TrungDK
		}
		return temp;
	}

	// Get serviceId in defferent format: 84, 04, or shortcode
	public String formatServiceId(String serviceId, int formatType) {
		if (serviceId == null || "".equals(serviceId)) {
			return null;
		}
		String temp = serviceId;
		/*
		 * switch (formatType) { case Constants.SERVICEID_FORMAT_INTERNATIONAL:
		 * if (temp.startsWith("04")) { temp = "84" + temp.substring(2); } else
		 * if (!temp.startsWith("84") || temp.length()<5) { temp = "84" + temp; }
		 * //else temp.startsWith("84") break; case
		 * Constants.SERVICEID_FORMAT_REGIONAL: if (temp.startsWith("84") &&
		 * temp.length()>4) { temp = "04" + temp.substring(2); } else if
		 * (!temp.startsWith("04")) { temp = "04" + temp; } //else
		 * temp.startsWith("04") break; case
		 * Constants.SERVICEID_FORMAT_SHORTCODE: if
		 * ((temp.startsWith("84")&&temp.length()>4) || temp.startsWith("04"))
		 * temp = temp.substring(2); break; default:
		 * System.out.println("formatServiceId: Invalid serviceId format_type " +
		 * formatType); return null; }
		 */
		return temp;
	}
}
