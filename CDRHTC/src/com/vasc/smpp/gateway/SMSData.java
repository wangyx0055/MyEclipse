package com.vasc.smpp.gateway;

/**
 * <p>Title: M-Commerce Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
* @author Huynh Ngoc Tuan
 * @version 1.0
 */
import java.math.BigDecimal;
import java.sql.Timestamp;
import com.vasc.smpp.gateway.Preference;

/**
 * This class storing info from SMS_SEND_QUEUE or SMS_SEND_LOG
 */
public class SMSData {

    private BigDecimal id           = null; // queue_id or log_id
    private String user_id          = null; // user_id: src_address, 849xxx
    private String service_id       = null; // service_id: dest_address, 997, (without 04 or 84)
    private String mobile_operator  = null; // e.g: GPC, VMS, VIETEL
    private String command_code     = null; // ma yeu cau,e.g: XSTD, ACB,...
    private String info             = null; // short message

    private Timestamp first_send_time = null;
    private Timestamp last_send_time  = null;
    private String submit_date        = null; // YYMMDDHHMMSS
    private String done_date          = null; // YYMMDDHHMMSS, time receiving delivery report

    private int number_of_send      = 0;
    private int process_result      = 0;
    private int message_type        = 0;
    private BigDecimal request_id   = null;
    private int total_segments      = 0;
    private int segment_seqnum      = 0;
    private int more_msgs_to_send   = 0;
    private String message_id       = null;


    public BigDecimal getId()           { return id;                }
    public String getUserId()           { return user_id;           }
    public String getServiceId()        { return service_id;        }
    public String getMobileOperator()   { return mobile_operator;   }
    public String getCommandCode()      { return command_code;      }
    public String getInfo()             { return info;              }
    public Timestamp getFirstSendTime() { return first_send_time;   }
    public Timestamp getLastSendTime()  { return last_send_time;    }

    // extented methods: begin
    public String getSubmitDate()       {
        if (submit_date != null) return submit_date;
        else return getCurrentTime();
    }
    public String getDoneDate()         {
        if (done_date != null) return done_date;
        else return getCurrentTime();
    }
    public static String getCurrentTime() {
        String sdate = com.vasc.common.DateProc.getYYYYMMDDHHMMSSString(
            new java.sql.Timestamp(System.currentTimeMillis()));
        return sdate.substring(2); // YYMMDDhhmmss
    }
    // extented methods: end.

    public int getNumberOfSend()        { return number_of_send;    }
    public int getProcessResult()       { return process_result;    }
    public int getMessageType()         { return message_type;      }
    public BigDecimal getRequestId()    { return request_id;        }
    public int getTotalSegments()       { return total_segments;    }
    public int getSegmentSeqnum()       { return segment_seqnum;    }
    public int getMoreMsgsToSend()      { return more_msgs_to_send; }
    public String getMessageId()        { return message_id;        }

    //Get userId in defferent format: 84, 09,& 9.
    public String getUserIdEx(int formatType) {
        return formatUserId(user_id, formatType);
    }
    //Get serviceId in defferent format: 84, or shortcode
    public String getServiceIdEx(int formatType) {
        return formatServiceId(service_id, formatType);
    }
    //Get Info with or without CRLF
    public String getInfoEx(boolean noCRLF) {
        if(!noCRLF || info == null) {
            return info; //return message as it is
        } else {
            String temp = info.replace('\n', ' ');
            temp = temp.replace('\r', ' ');
            return temp;
        }
    }

    public void setId(BigDecimal value) {
        this.id = value;
    }
    public void setUserId(String value) {
        if (value != null && value.length() > 0) {
            value.toLowerCase();
            value.replace('o', '0');
        }
        if (value.startsWith("+")) {
            value = value.substring(1); //remove plus(+) sign
        }
        this.user_id = value;
    }
    public void setServiceId(String value) {
        if (value == null) return;
        if (value.length() > 0) {
            value.toLowerCase();
            value.replace('o', '0');
        }
        if (value.startsWith("+")) {
            value = value.substring(1); //remove plus(+) sign
        }
        if (value.startsWith("04") || value.startsWith("84")) {
            value = value.substring(2); //remove region code
        }
        this.service_id = value; //now, only shortcode: 996,997,...
    }
    public void setMobileOperator(String value)   {
        this.mobile_operator = value;
    }
    public void setCommandCode(String value) {
        this.command_code = value;
    }
    public void setInfo(String value) {
        if (value == null) {
            value = " ";
        } else if (value.length() > 160) {
            value = value.substring(0, 160); // 0--159
        }
        this.info = value;
    }

    public void setFirstSendTime(Timestamp value) {
        this.first_send_time = value;
    }
    public void setLastSendTime(Timestamp value)  {
        this.last_send_time = value;
    }

    // extented methods: begin
    public void setSubmitDate(String value) {
        this.submit_date = value;
    }
    public void setDoneDate(String value) {
        this.done_date = value;
    }
    // extented methods: end.

    public void setNumberOfSend(int numOfSend) {
        this.number_of_send = numOfSend;
    }
    public void setProcessResult(int processResult) {
        this.process_result = processResult;
    }
    public void setMessageType(int messageType) {
        this.message_type = messageType;
    }
    public void setRequestId(BigDecimal requestId) {
        this.request_id = requestId;
    }
    public void setTotalSegments(int totalSegments) {
        this.total_segments = totalSegments;
    }
    public void setSegmentSeqnum(int segmentSeqnum) {
        this.segment_seqnum = segmentSeqnum;
    }
    public void setMoreMsgsToSend(int moreMsgsToSend) {
        this.more_msgs_to_send = moreMsgsToSend;
    }
    public void setMessageId(String messageId) {
        this.message_id = messageId;
    }

    ////////////////////////////////////////////////////////////////
    public boolean isNotSentYet() {
        if (number_of_send == 0)
            return true;
        else
            return false;
    }
    //Timeout wait for response
    public boolean isTimeout() {
        boolean result = false;
        if (last_send_time != null) {
            long currTime = System.currentTimeMillis();
            if ((currTime - last_send_time.getTime()) > Preference.timeResend) { //timeout
                result = true;
            }
        }
        return result;
    }

    public boolean isValidServiceId() {
       return Preference.isValidServiceId(this.service_id);
    }
/*
    public boolean isValidServiceId() {
        String serviceId = this.service_id;
        if (serviceId == null || "".equals(serviceId)) {
            return false;
        }
        if (serviceId.equals("996") || serviceId.equals("84996") || serviceId.equals("04996") ||
            serviceId.equals("997") || serviceId.equals("84997") || serviceId.equals("04997") ||
            serviceId.equals("998") || serviceId.equals("84998") || serviceId.equals("04998") ||
            serviceId.equals("19001255") || serviceId.equals("8419001255") || serviceId.equals("0419001255") ||
            serviceId.equals("19001522") || serviceId.equals("8419001522") || serviceId.equals("0419001522") ||
            serviceId.equals("19001799") || serviceId.equals("8419001799") || serviceId.equals("0419001799")) {
            return true;
        } else {
            return false;
        }
    }
*/

    public boolean isValidUserId() {
        String userId = user_id;
        if (userId == null || "".equals(userId)) {
            return false;
        }
        if (((userId.startsWith("90") ||userId.startsWith("93")  || userId.startsWith("91") ||userId.startsWith("94")  || userId.startsWith("98")||userId.startsWith("95"))&& userId.length() == 9)  ||
            ((userId.startsWith("090")  || userId.startsWith("091")  || userId.startsWith("098")||userId.startsWith("093")||userId.startsWith("094")||userId.startsWith("095"))  && userId.length() == 10) ||
            ((userId.startsWith("8490") || userId.startsWith("8491") || userId.startsWith("8498")||userId.startsWith("8493")||userId.startsWith("8494")||userId.startsWith("8495")) && userId.length() == 11)) {
            return true;
        } else {
            return false;
        }
    }

    // Consider if the MOBILE_OPERATOR field is needed to rebuilt and updated
    // returns true if changed, else false;
    public boolean rebuildMobileOperator() {
        String userId = user_id;
        if (userId == null || "".equals(userId))
            return false;

        String new_mobile_operator = null;
        if (userId.startsWith("90") ||
            userId.startsWith("090") ||
            userId.startsWith("8490")||
            userId.startsWith("93") ||
           userId.startsWith("093")||
           userId.startsWith("8493") ) {
            if (!"VMS".equals(mobile_operator)) //MUST be UPPERCASE
                new_mobile_operator = "VMS";
        } else
        if (userId.startsWith("91") ||
            userId.startsWith("091") ||
            userId.startsWith("8491")||
            userId.startsWith("94") ||
           userId.startsWith("094")||
           userId.startsWith("8494")) {
            if (!"GPC".equals(mobile_operator))
                new_mobile_operator = "GPC";
        } else
        if (userId.startsWith("98") ||
            userId.startsWith("098") ||
            userId.startsWith("8498")) {
            if (!"VIETEL".equals(mobile_operator))
                new_mobile_operator = "VIETEL";
        }else if (userId.startsWith("95") ||
            userId.startsWith("095") ||
            userId.startsWith("8495")) {
            if (!"SFONE".equals(mobile_operator))
                new_mobile_operator = "SFONE";
        }
        if (new_mobile_operator != null) {
            this.mobile_operator = new_mobile_operator;
            return true;
        }
        return false;
    }


    public boolean isAddressToSend() {
        boolean result = false;
        //Check service_id(996,997,998,19001255)
        if (!Preference.sourceAddressList.contains(service_id)) {
            return false;
        }

        //check user_id
        if ("VMS".equals(Preference.mobileOperator)) {
            if (user_id.startsWith("90") || user_id.startsWith("090") || user_id.startsWith("8490")||
                user_id.startsWith("93") || user_id.startsWith("093") || user_id.startsWith("8493")) {
                result = true;
            }
        } else if ("GPC".equals(Preference.mobileOperator)) {
            if (user_id.startsWith("91") || user_id.startsWith("091") || user_id.startsWith("8491")||
                user_id.startsWith("94") || user_id.startsWith("094") || user_id.startsWith("8494")) {
                result = true;
            }
        } else if ("VIETEL".equals(Preference.mobileOperator)) {
            if (user_id.startsWith("98") || user_id.startsWith("098") || user_id.startsWith("8498")) {
                result = true;
              }
        } else if ("SFONE".equals(Preference.mobileOperator)) {
           if (user_id.startsWith("95") || user_id.startsWith("095") ||
               user_id.startsWith("8495")) {
             result = true;
          }

        } else {
            System.out.println(
                "invalid value of mobile_operator in the configuration file.");
        }
        return result;
    }
// Writen by Tuanhn
   public boolean isAddressToSendEx() {
        boolean result = false;
        //Check service_id(996,997,998,19001255)
        if (!Preference.sourceAddressList.contains(service_id)) {
            return false;
        }

        //check user_id
        /*
        if ("VMS".equals(Preference.mobileOperator)) {
            if (user_id.startsWith("90") || user_id.startsWith("090") || user_id.startsWith("8490")) {
                result = true;
            }
        } else if ("GPC".equals(Preference.mobileOperator)) {
            if (user_id.startsWith("91") || user_id.startsWith("091") || user_id.startsWith("8491")) {
                result = true;
            }
        } else if ("VIETEL".equals(Preference.mobileOperator)) {
            if (user_id.startsWith("98") || user_id.startsWith("098") || user_id.startsWith("8498")) {
                result = true;
            }
        }
        else {
            System.out.println(
                "invalid value of mobile_operator in the configuration file.");
        }
    */
        return result;
    }

    //Get userId in defferent format: 84, 09,& 9.
    public static String formatUserId(String userId, int formatType) {
        if (userId == null || "".equals(userId)) {
            return null;
        }
        String temp = userId;
        switch(formatType) {
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
                System.out.println("Invalid userId format type " + formatType);
                return null;
        }
        return temp;
    }
    //Get serviceId in defferent format: 84, 04, or shortcode
    public static String formatServiceId(String serviceId, int formatType) {
        if (serviceId == null || "".equals(serviceId)) {
            return null;
        }
        String temp = serviceId;
        switch(formatType) {
            case Constants.SERVICEID_FORMAT_INTERNATIONAL:
                if (temp.startsWith("04")) {
                    temp = "84" + temp.substring(2);
                } else if (!temp.startsWith("84")) {
                    temp = "84" + temp;
                } //else temp.startsWith("84")
                else if(temp.length()<5){
                   temp = "84" + temp;
                }
                break;
            case Constants.SERVICEID_FORMAT_REGIONAL:
                if (temp.startsWith("84") && (temp.length()>4)) {
                    temp = "04" + temp.substring(2);
                } else if (!temp.startsWith("04")) {
                    temp = "04" + temp;
                } //else temp.startsWith("04")
                break;
            case Constants.SERVICEID_FORMAT_SHORTCODE:
                if ((temp.startsWith("84") && (temp.length()>4)) || temp.startsWith("04"))
                    temp = temp.substring(2);

                break;
            default:
                System.out.println("Invalid serviceId format type " + formatType);
                return null;
        }
        return temp;
    }


    public static void main (String args[]) {
        SMSData sms = new SMSData();
        System.out.println(sms.formatUserId("0904060007", Constants.USERID_FORMAT_INTERNATIONAL));
        System.out.println(sms.formatServiceId("04997", Constants.SERVICEID_FORMAT_REGIONAL));
    }
}
