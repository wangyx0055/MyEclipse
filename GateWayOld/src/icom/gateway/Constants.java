package icom.gateway;

/**
 * <p>
 * Title: SMPP Client
 * </p>
 * <p>
 * Description: SMPP Gateway Project
 * </p>
 * <p>
 * Copyright: (c) 2003
 * </p>
 * <p>
 * Company: ICom
 * </p>
 * 

 */

public class Constants {
	static final short PORT_RING_TONE = 0x1581;

	// Operator Logo
	static final short PORT_OPER_LOGO = 0x1582;

	// CLI (Caller Line Identification) icons
	// (also called ‘Caller group graphics’)
	static final short PORT_CLI_ICON = 0x1583;

	// Picture message
	static final short PORT_PIC_MSG = 0x158A;

	// Wap Push
	static final short PORT_WAP_PUSH = 0x0B84;

	// Wap Connectionless
	static final short PORT_WAP_CONNECTIONLESS = 0x23F0; // 9200

	// Wap Browser-bookmar|settings
	static final short PORT_WAP_BROWSER = (short) 0xC34F;
	static final short PORT_WAP_BSOURCE = (short) 0xC002;

	static final short PORT_VCARD = 0x23F4;
	static final short PORT_VCALENDAR = 0x23F5;
	static final short PORT_411A = 0x411A;

	// Content_Type (User-defined)
	// Type of EMS:
	// = 0-Text (default)
	// = 1-Ringtone
	// = 2-Operator Logo
	// = 4-Picture Message
	// = 8- Wap push.
	static final int CT_TEXT = 0;

	static final int CT_RING_TONE = 1;
	static final int CT_OPER_LOGO = 2;
	static final int CT_CLI_ICON = 3;
	static final int CT_PIC_MSG = 4;
	static final int CT_WAP_SI = 8; // service indication

	static final int CT_CUTE_TEXT = 5; // UTF16 hexa string
	static final int CT_MMS_NOTIFY = 6;
	static final int CT_WAP_BROWSER = 7; // settings or bookmark

	static final int CT_TEXT_UTF8 = 15;
	static final int CT_VCARD = 10;
	static final int CT_VCALENDAR = 11;
	static final int CT_DALINK = 13; // SDKM cua Dalink
	static final int CT_KARAOKE = 14;
	static final int CT_DALINK_VSSA = 16;
	static final int CT_MC_8000 = 17;

	static final int CT_BINARY = 18;
	static final int CT_BANKING = 19;
	
	static final int CT_DPORT = 22;

	static final int CT_VALUE_MIN = 0;
	static final int CT_VALUE_MAX = 20;
	
	static final int CT_411A = 9;
	
	static final int CT_TEXT_LONG = 21;
	static final int CT_OMA_LONG = 23;

	// Process_Result:
	// = 0-Gui ko thanh cong (default)
	// = 1–Gui thanh cong den SMSC
	// = 2–Da den duoc mobile
	// = 3-Delivery failt
	// = 5-Vuot qua so tin nhan toi da trong ngay
	static final int MSG_NOT_SENT = 0;
	static final int MSG_SENT_OK = 1;
	static final int MSG_SENT_FAILT = 4;
	static final int MSG_DELIVERED = 2;
	static final int MSG_UNDELIVERED = 3;
	static final int MSG_OVER_MAX_MT = 5;
	static final int MSG_NOT_RESEND_MT = 5;

	// Message_Type:
	// = 0-La tin nhan xuat phat tu he thong
	// = 1-Tin nhan tra loi cho 1 msg dung format and found
	// = 2-Tin nhan tra loi cho 1 msg sai format or not found
	static final int MT_PUSH = 0;
	static final int MT_RESP_VALID = 1;
	static final int MT_RESP_VALID_MORE = 11; // from second MT (valid type)
	static final int MT_RESP_INVALID = 2; // any (other) invalid
	static final int MT_RESP_INVALID_PREFIX = 21;
	static final int MT_RESP_INVALID_SYNTAX = 22;
	static final int MT_RESP_INVALID_MSISDN = 23;
	static final int MT_RESP_CONTENT_NOT_FOUND = 24;
	static final int MT_RESP_GAME_OVER = 25;
	static final int MT_RESP_GET_MARK = 3;
	static final int MT_RESP_OVER_MAX_MO = 4;
	static final int MT_RESP_OVER_MAX_MT = 5;

	// Mobile number format type
	public final static int USERID_FORMAT_INTERNATIONAL = 0; // 84xxx: 11 digits
	public final static int USERID_FORMAT_NATIONAL_ZERO = 1; // 09xxx: 10 digits
	public final static int USERID_FORMAT_NATIONAL_NINE = 2; // 9xxxx: 9 digits

	public final static int SERVICEID_FORMAT_INTERNATIONAL = 10; // 84997
	public final static int SERVICEID_FORMAT_REGIONAL = 11; // 84997
	public final static int SERVICEID_FORMAT_SHORTCODE = 12; // 997

	// Delivery Notification
	static final int DN_SUCCESS = 0; // successful,
	static final int DN_FAILT = 1; // not successful

	public final static long MIN_TIME_BETWEEN_MO = 30; // 30secs
	public final static int MAX_MO_PER_DAY = 101; // 100 sms/day
	public final static int MAX_MT_PER_DAY = 301;
	public final static int MAX_CDR_PER_DAY = 101;

	// Alert

	public static String ALERT_NONE = "none";
	public static String ALERT_WARN = "warn";
	public static String ALERT_MINOR = "minor";
	public static String ALERT_MAJOR = "major";
	public static String ALERT_SERIOUS = "serious";
	
	
	// Message type
	
	public static int CDR_CHARGE = 1;
	public static int CDR_REFUND = 2;
	public static int CDR_NOCHARGE = 0;

}
