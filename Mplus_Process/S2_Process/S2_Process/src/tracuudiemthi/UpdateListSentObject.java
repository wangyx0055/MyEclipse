package tracuudiemthi;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */


import java.io.Serializable;



public class UpdateListSentObject implements Serializable {

	int id = -1; // id in mlist
	int response_status = 0;
	int count_sms = 0;
	String partner = "";
	String requestid = "";
	String userid = "";
	String keyword = "";
	String command_code = "";
	String usertext = "";
	String mobileoperator = "VMS";
	String status = ""; 
	String db_name = "";
	public UpdateListSentObject() {
		
	}
	
	public UpdateListSentObject(UpdateListSentObject updateListSentObject) {
		this.response_status = updateListSentObject.response_status;
		this.count_sms = updateListSentObject.count_sms;
		this.partner = updateListSentObject.partner;
		this.requestid = updateListSentObject.requestid;
		this.userid = updateListSentObject.userid;
		this.command_code = updateListSentObject.command_code;
		this.status = updateListSentObject.status;
		this.db_name = updateListSentObject.db_name;
	}
	
	public UpdateListSentObject(int response_status, int count_sms, String partner, String requestid, String userid, String command_code, String status, String dbName) {
		this.response_status = response_status;
		this.count_sms = count_sms;
		this.partner = partner;
		this.requestid = requestid;
		this.userid = userid;
		this.command_code = command_code;
		this.status = status;
		this.db_name = dbName;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getRequestid() {
		return requestid;
	}
	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getCommand_code() {
		return command_code;
	}
	public void setCommand_code(String command_code) {
		this.command_code = command_code;
	}
	public String getUsertext() {
		return usertext;
	}
	public void setUsertext(String usertext) {
		this.usertext = usertext;
	}
	public String getMobileoperator() {
		return mobileoperator;
	}
	public void setMobileoperator(String mobileoperator) {
		this.mobileoperator = mobileoperator;
	}

	public int getResponse_status() {
		return response_status;
	}

	public void setResponse_status(int response_status) {
		this.response_status = response_status;
	}

	public int getCount_sms() {
		return count_sms;
	}

	public void setCount_sms(int count_sms) {
		this.count_sms = count_sms;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDb_name() {
		return db_name;
	}

	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}
}
