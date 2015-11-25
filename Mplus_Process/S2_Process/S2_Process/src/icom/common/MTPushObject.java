package icom.common;

/**
 * 
 * @author DANND
 *
 */
public class MTPushObject {
	
	private int ID = -1;
	private String userId = "";
	private int status = 0;
	private String commandCode = "";
	private String lastCode = "0";
	private String linkRing = "x";
	
		
	public String getLinkRing() {
		return linkRing;
	}
	public void setLinkRing(String linkRing) {
		this.linkRing = linkRing;
	}
	
	public String getLastCode() {
		return lastCode;
	}
	public void setLastCode(String lastCode) {
		this.lastCode = lastCode;
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}

	
}
