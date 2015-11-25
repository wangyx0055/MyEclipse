package daily;

public class ListSendObj {

	//ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, OPTIONS, MESSAGE_TYPE, 
	// REQUEST_ID, MESSAGE_ID, INSERT_DATE, SERVICE_NAME, CHANNEL_TYPE, 
	// CONTENT_ID, AMOUNT, TIME_DELIVERY, COMPANY_ID, IS_THE_SEND, LAST_CODE
	
	private int id = -1;
	private String userId = "";
	private String serviceId = "";
	private String mobileOperator = "";
	private String commandCode = "";
	private String options = "";
	private int messageType = 0;
	private String requestId = "";
	private int messageId = 0;
	private String insertDate = "";
	private String serviceName = "";
	private int channelType = 0;
	private String contentId = "";
	private int amount = 0;
	private String timeDelivery = "";
	private String companyId = "";
	private int isTheSend = 1;
	private String lastCode = "0";
	
	
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
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	public String getInsertDate() {
		return insertDate;
	}
	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
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
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getTimeDelivery() {
		return timeDelivery;
	}
	public void setTimeDelivery(String timeDelivery) {
		this.timeDelivery = timeDelivery;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public int getIsTheSend() {
		return isTheSend;
	}
	public void setIsTheSend(int isTheSend) {
		this.isTheSend = isTheSend;
	}
	public String getLastCode() {
		return lastCode;
	}
	public void setLastCode(String lastCode) {
		this.lastCode = lastCode;
	}
	
	
}
