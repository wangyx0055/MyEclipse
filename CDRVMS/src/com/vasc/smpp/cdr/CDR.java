package com.vasc.smpp.cdr;

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

/**
 * This class storing info from CDR_QUEUE or CDR_LOG
 */
public class CDR {
  private BigDecimal id = null; // queue_id or log_id
  private String user_id = null; // user_id: src_address, 849xxx
  private String service_id = null; // service_id: dest_address, 997,
  private String mobile_operator = null; // e.g: GPC, VMS
  private String command_code = null; // ma yeu cau,e.g: XSTD, ACB,...
  private String info = null; // short message
  private String submit_date = null; // sending time, YYMMDDHHMM
  private String done_date = null; // receiving time, YYMMDDHHMM
  private int total_segments = 0;
  private String process_result = null; //maivq them ngay 13/09/2006
  private String Message_Type = null; //maivq them ngay 11/03/2008
  private String Request_Id = null; //maivq them ngay 11/03/2008
  private int CP_Id = 0; //maivq them ngay 11/03/2008
  private Timestamp Submit_date_timestamp = null;
  private Timestamp Done_date_timestamp = null;

  public BigDecimal getId() {
    return id;
  }

  public String getUserId() {
    return user_id;
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

  public String getInfo() {
    return info;
  }

  public String getSubmitDate() {
    if (submit_date != null) {
      return submit_date;
    }
    else {
      return getCurrentDate();
    }
  }

  public String getDoneDate() {
    if (done_date != null) {
      return done_date;
    }
    else {
      return getCurrentDate();
    }
  }

  public String getSubmitDateFULL() {
    if (submit_date != null) {
      return "20" + submit_date;
    }
    else {
      return getCurrentDateFULL();
    }
  }

  public String getDoneDateFULL() {
    if (done_date != null) {
      return "20" + done_date;
    }
    else {
      return getCurrentDateFULL();
    }
  }

  public int getTotalSegments() {
    return total_segments;
  }

  public String getProcessResult() {
    return process_result;
  }

  public int getCPid() {
    return CP_Id;
  }

  public String getMessageType() {
    return Message_Type;
  }

  public String getRequestId() {
    return Request_Id;
  }

  public Timestamp getSubmit_date_timestamp() {
    return Submit_date_timestamp;
  }

  public Timestamp getDone_date_timestamp() {
    return Done_date_timestamp;
  }

  private String getCurrentDateFULL() {

    String sdate = com.vasc.common.DateProc.getYYYYMMDDHHMMSSString(
        new java.sql.Timestamp(System.currentTimeMillis()));

    return sdate; // YYYYMMDDhhmmss
  }

  private String getCurrentDate() {

    String sdate = com.vasc.common.DateProc.getYYYYMMDDHHMMString(
        new java.sql.Timestamp(System.currentTimeMillis()));

    return sdate.substring(2); // YYMMDDhhmm
  }

  public void setId(BigDecimal value) {
    this.id = value;
  }

  public void setUserId(String value) {
    this.user_id = value;
  }

  public void setServiceId(String value) {
    this.service_id = value;
  }

  public void setMobileOperator(String value) {
    this.mobile_operator = value;
  }

  public void setCommandCode(String value) {
    this.command_code = value;
  }

  public void setInfo(String value) {
    this.info = value;
    if (this.info == null) {
      this.info = " ";
    }
  }

  public void setSubmitDate(String value) {
    if ("20".equals(value.substring(0, 2))) {
      this.submit_date = value;
    }
    else {

      this.submit_date = "20" + value;
    }
  }

  /*
    public void setDoneDate(String value) {
        this.done_date = value;
    }
   */
  public void setDoneDate(String value) {
    if ("20".equals(value.substring(0, 2))) {
      this.done_date = value;
    }
    else {
      this.done_date = "20" + value;
    }
  }

  public void setTotalSegments(int totalSegments) {
    this.total_segments = totalSegments;
  }

  public void setProcessResult(String ProcessResult) {
    if (ProcessResult == null) {
      process_result = "1";
    }
    else {
      this.process_result = ProcessResult;
    }
  }

  public void setMessageType(String MessageType) {
    if (MessageType == null) {
      Message_Type = "1";
    }
    else {
      this.Message_Type = MessageType;
    }
  }

  public void setRequestId(String RequestId) {
    if (RequestId == null) {
      Request_Id = "0";
    }
    else {
      this.Request_Id = RequestId;
    }
  }

  public void setCPId(int CPId) {
    this.CP_Id = CPId;
  }

  public void setSubmit_date_timestamp(Timestamp Submit_date_timestamp) {
    this.Submit_date_timestamp = Submit_date_timestamp;
  }

  public void setDone_date_timestamp(Timestamp Done_date_timestamp) {
    this.Done_date_timestamp = Done_date_timestamp;
  }

  public static void main(String args[]) {
    CDR cdr = new CDR();
    System.out.println();
  }
}
