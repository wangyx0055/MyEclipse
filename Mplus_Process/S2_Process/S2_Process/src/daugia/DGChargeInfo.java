package daugia;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DGChargeInfo {

	int id;
	String userId = "";
	String serviceId = "";
	String mobileOperator = "";
	String commandCode = "";
	int contentType;
	String info = "";
	Timestamp submitDate;
	String doneDate = "";
	int processResult;
	int msgType;
	BigDecimal requestID ;
	String msgID = "";
	int totalSegments;
	int retriesNumber;
	Timestamp insertDate;
	String notes;
	String serviceName = "";
	int channelType;
	String contendID = "";
	int amount;
	int reslultCharge;
	String dgAmount = "";
	String timeSendMO = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getMobileOperator() {
		return mobileOperator;
	}
	public void setMobileOperator(String mobileOperator) {
		this.mobileOperator = mobileOperator;
	}
	public String getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}
	public int getContentType() {
		return contentType;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Timestamp getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Timestamp submitDate) {
		this.submitDate = submitDate;
	}
	public String getDoneDate() {
		return doneDate;
	}
	public void setDoneDate(String doneDate) {
		this.doneDate = doneDate;
	}
	public int getProcessResult() {
		return processResult;
	}
	public void setProcessResult(int processResult) {
		this.processResult = processResult;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public BigDecimal getRequestID() {
		return requestID;
	}
	public void setRequestID(BigDecimal requestID) {
		this.requestID = requestID;
	}
	public String getMsgID() {
		return msgID;
	}
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}
	public int getTotalSegments() {
		return totalSegments;
	}
	public void setTotalSegments(int totalSegments) {
		this.totalSegments = totalSegments;
	}
	public int getRetriesNumber() {
		return retriesNumber;
	}
	public void setRetriesNumber(int retriesNumber) {
		this.retriesNumber = retriesNumber;
	}
	public Timestamp getInsertDate() {
		return insertDate;
	}
	public void setInsertDate(Timestamp insertDate) {
		this.insertDate = insertDate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public int getChannelType() {
		return channelType;
	}
	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}
	public String getContendID() {
		return contendID;
	}
	public void setContendID(String contendID) {
		this.contendID = contendID;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getReslultCharge() {
		return reslultCharge;
	}
	public void setReslultCharge(int reslultCharge) {
		this.reslultCharge = reslultCharge;
	}
	public String getDgAmount() {
		return dgAmount;
	}
	public void setDgAmount(String dgAmount) {
		this.dgAmount = dgAmount;
	}
	public String getTimeSendMO() {
		return timeSendMO;
	}
	public void setTimeSendMO(String timeSendMO) {
		this.timeSendMO = timeSendMO;
	}

}
