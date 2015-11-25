package com.vasc.smpp.gateway;

/**
 * <p>Title: M-Commerce Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
* @author Huynh Ngoc Tuan
 * @version 1.0
 */

public class Constants {
    //Process_Result:
    // = 0-Gui ko thanh cong (default)
    // = 1–Gui thanh cong den SMSC
    // = 2–Da den duoc mobile
    // = 3-Delivery failt
    // = 5-Vuot qua so tin nhan toi da trong ngay
    static final int MSG_NOT_SENT    = 0;
    static final int MSG_SENT_OK     = 1;
    static final int MSG_SENT_FAILT  = 4;
    static final int MSG_DELIVERED   = 2;
    static final int MSG_UNDELIVERED = 3;
    static final int MSG_OVER_MAX_MT = 5;

    //Message_Type:
    // = 0-La tin nhan xuat phat tu he thong
    // = 1-Tin nhan tra loi cho 1 msg dung format
    // = 2-Tin nhan tra loi cho 1 msg sai format
    static final int MT_PUSH         = 0;
    public static final int MT_RESP_VALID   = 1;
    static final int MT_RESP_INVALID = 2; //any (other) invalid
    static final int MT_RESP_INVALID_PREFIX    = 21;
    static final int MT_RESP_INVALID_SYNTAX    = 22;
    static final int MT_RESP_INVALID_MSISDN    = 23;
    static final int MT_RESP_CONTENT_NOT_FOUND = 24;
    static final int MT_RESP_GAME_OVER         = 25;
    static final int MT_RESP_GET_MARK    = 3;
    static final int MT_RESP_OVER_MAX_MO = 4;
    static final int MT_RESP_OVER_MAX_MT = 5;

    // Mobile number format type
    public final static int USERID_FORMAT_INTERNATIONAL = 0; //84xxx: 11 digits
    public final static int USERID_FORMAT_NATIONAL_ZERO = 1; //09xxx: 10 digits
    public final static int USERID_FORMAT_NATIONAL_NINE = 2; //9xxxx:  9 digits

    public final static int SERVICEID_FORMAT_INTERNATIONAL = 10; //84997
    public final static int SERVICEID_FORMAT_REGIONAL      = 11; //04997
    public final static int SERVICEID_FORMAT_SHORTCODE     = 12; //997

    public final static long MIN_TIME_BETWEEN_MO = 5; //5secs
    public final static int MAX_MO_PER_DAY = 100; //100 sms per day
    public final static int MAX_MT_PER_DAY = 100; //100 sms per day

    public Constants() {
    }
}