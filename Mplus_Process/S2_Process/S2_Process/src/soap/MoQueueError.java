package soap;

import java.sql.Timestamp;

public class MoQueueError {
	public MoQueueError (){
		
	}
	
	private int id;
	private String userId;
	private String serviceId;
	private String mobile;
	private String commandCode;
	private String info;
	private Timestamp recevieDate;
	private String req;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Timestamp getRecevieDate() {
		return recevieDate;
	}
	public void setRecevieDate(Timestamp recevieDate) {
		this.recevieDate = recevieDate;
	}
	public String getReq() {
		return req;
	}
	public void setReq(String req) {
		this.req = req;
	}
	
}
