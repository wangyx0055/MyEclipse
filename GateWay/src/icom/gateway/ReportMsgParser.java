package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

/*
 * Delivery Receipt:
 * When an ESME submits a message to the SMSC, a unique message ID, encoded in Hex,
 * is returned in the Submit_Sm_Resp. The ESME may store this ID, as it will be
 * returned in the TEXT (id) portion of a delivery receipt. It should be noted that
 * the ID returned in the text portion of the delivery receipt is represented
 * in decimal not hexadecimal. Please note that delivery receipts are only returned
 * to the ESME if the registered flag was set on the original message submitted.
 *
 * Format for a delivery receipt:
 * "id:IIIIIIIIII sub:SSS dlvrd:DDD submit date:YYMMDDhhmm done date:YYMMDDhhmm
 * stat:DDDDDDD err:E Text: .. "
 *
 * Example:
 * Msg="id:0012617184 sub:001 dlvrd:001 submit date:0309271108 done date:0309271108
 * stat:DELIVRD err:000 text:TUANHN"
 * (id:0012617184[dec] = 0xC085E0)
 */
public class ReportMsgParser {
//    private Logger logger = new Logger("ReportMsgParser");


    private String message = null;

    private String id          = null; // message id [decimal string]
    private int submit         = 0;
    private int deliver        = 0;
    private String submit_date = null; // YYMMDDhhmm
    private String done_date   = null; // YYMMDDhhmm
    private String status      = null;
    private String error       = null;
    private String text        = null; // short message in the Submit_SM

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId()         { return this.id;          }
    public int    getSubmit()     { return this.submit;      }
    public int    getDeliver()    { return this.deliver;     }
    public String getSubmitDate() { return this.submit_date; }
    public String getDoneDate()   { return this.done_date;   }
    public String getStatus()     { return this.status;      }
    public String getError()      { return this.error;       }
    public String getText()       { return this.text;        }

    // if (status==DELIVRD)--> true
    private boolean delivered = false;
    public boolean isDelivered() {
        if ("DELIVRD".equals(this.status))
            return true;
        else
            return false;
    }

    public ReportMsgParser() {
    }

    public static ReportMsgParser getInstance() {
        ReportMsgParser parser = new ReportMsgParser();
        String sdate = icom.common.DateProc.getYYYYMMDDHHMMString(
            new java.sql.Timestamp(System.currentTimeMillis()));
        parser.submit_date = sdate.substring(2); // YYMMDDhhmm
        parser.done_date = parser.submit_date;
        return parser;
    }

    public ReportMsgParser(String message) {
        this.message = message;
    }

    public boolean parseMessage(String message) {
        this.message = message;
        return parseMessage();
    }
    /*
     * Msg="id:0012617184 sub:001 dlvrd:001 submit date:0309271108 done date:0309271108
     * stat:DELIVRD err:000 text:TUANHN"
     */
    public boolean parseMessage() {
        if (message == null || "".equals(message))
            return false;
        try {
            String tempStr = this.message.toUpperCase().trim();
            int index1 = tempStr.indexOf(":");
            int index2 = tempStr.indexOf(" ");
            java.math.BigDecimal strId = new java.math.BigDecimal(
                         tempStr.substring(index1 + 1, index2));
            this.id = strId.toString();

            tempStr = tempStr.substring(index2 + 1);
            index1 = tempStr.indexOf(":");
            index2 = tempStr.indexOf(" ");
            this.submit = Integer.parseInt(tempStr.substring(index1 + 1, index2));

            tempStr = tempStr.substring(index2 + 1);
            index1 = tempStr.indexOf(":");
            index2 = tempStr.indexOf(" ");
            this.deliver = Integer.parseInt(tempStr.substring(index1 + 1, index2));

            tempStr = tempStr.substring(index2 + 1);
            index1 = tempStr.indexOf(":");
            index2 = tempStr.indexOf(" ", index1);
            this.submit_date = tempStr.substring(index1 + 1, index2);

            tempStr = tempStr.substring(index2 + 1);
            index1 = tempStr.indexOf(":");
            index2 = tempStr.indexOf(" ", index1);
            this.done_date = tempStr.substring(index1 + 1, index2);

            tempStr = tempStr.substring(index2 + 1);
            index1 = tempStr.indexOf(":");
            index2 = tempStr.indexOf(" ");
            this.status = tempStr.substring(index1 + 1, index2);

            tempStr = tempStr.substring(index2 + 1);
            index1 = tempStr.indexOf(":");
            index2 = tempStr.indexOf(" ");
            this.error = tempStr.substring(index1 + 1, index2);

            tempStr = tempStr.substring(index2 + 1);
            index1 = tempStr.indexOf(":");
            try {
                this.text = tempStr.substring(index1 + 1);
            } catch (Exception ex) {

            }

            return true;
        } catch (Exception e) {
            //System.out.println("parseMessage: " + e.getMessage());
            return false;
        }
    }

    public static void main (String args[]) {
//        ReportMsgParser parser = new ReportMsgParser();
//        String msg="id:0012617184 sub:001 dlvrd:001 submit date:0309271108 done date:0309271108 stat:DELIVRD err:000 text:TUANHN";
//        parser.parseMessage(msg);
//        System.out.println("Id: " + parser.getId());
//        System.out.println("Submit date: " + parser.getSubmitDate());
//        System.out.println("Status: " + parser.isDelivered());

        ReportMsgParser parser = ReportMsgParser.getInstance();
       // System.out.println("Submit date: " + parser.getSubmitDate());
        //System.out.println("Done date: " + parser.getDoneDate());
    }
}
