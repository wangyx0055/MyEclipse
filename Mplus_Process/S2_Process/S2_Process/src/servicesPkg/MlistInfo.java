package servicesPkg;

import java.sql.Timestamp;

public class MlistInfo {
	
	int id = 0; 
	String userId;
	String serviceId;
	String today;
	String options;
	String failures;
	String lastCode = "0";
	Timestamp autoTimeStamps;
	String commandCode;
	String requestId;
	int messageType;
	String mobiOperator;
	int mtCount; 
	int mtFree; 
	int duration;
	int amount;
	int contentId;
	String service;
	String companyId;
	int active;
	int chanelType; 
	int regCount;
	String dateRetry;
	int numberCharge = 0;
	
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
	public String getToday() {
		return today;
	}
	public void setToday(String today) {
		this.today = today;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public String getFailures() {
		return failures;
	}
	public void setFailures(String failures) {
		this.failures = failures;
	}
	public String getLastCode() {
		return lastCode;
	}
	public void setLastCode(String lastCode) {
		this.lastCode = lastCode;
	}
	public Timestamp getAutoTimeStamps() {
		return autoTimeStamps;
	}
	public void setAutoTimeStamps(Timestamp autoTimeStamps) {
		this.autoTimeStamps = autoTimeStamps;
	}
	public String getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public String getMobiOperator() {
		return mobiOperator;
	}
	public void setMobiOperator(String mobiOperator) {
		this.mobiOperator = mobiOperator;
	}
	public int getMtCount() {
		return mtCount;
	}
	public void setMtCount(int mtCount) {
		this.mtCount = mtCount;
	}
	public int getMtFree() {
		return mtFree;
	}
	public void setMtFree(int mtFree) {
		this.mtFree = mtFree;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getContentId() {
		return contentId;
	}
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public int getChanelType() {
		return chanelType;
	}
	public void setChanelType(int chanelType) {
		this.chanelType = chanelType;
	}
	public int getRegCount() {
		return regCount;
	}
	public void setRegCount(int regCount) {
		this.regCount = regCount;
	}
	public String getDateRetry() {
		return dateRetry;
	}
	public void setDateRetry(String dateRetry) {
		this.dateRetry = dateRetry;
	}
	
	public int getNumberCharge() {
		return numberCharge;
	}
	public void setNumberCharge(int numberCharge) {
		this.numberCharge = numberCharge;
	}
	
	


}
