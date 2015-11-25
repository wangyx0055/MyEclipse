package DTO;

import java.sql.Timestamp;

public class PromotionCodeDTO {
	private String msisdn;
	private String commandCode;
	private Timestamp activeTime;
	private int promotionCode;
	private int active;
	private int mtStatus;
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}
	public Timestamp getActiveTime() {
		return activeTime;
	}
	public void setActiveTime(Timestamp activeTime) {
		this.activeTime = activeTime;
	}
	public int getPromotionCode() {
		return promotionCode;
	}
	public void setPromotionCode(int promotionCode) {
		this.promotionCode = promotionCode;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public int getMtStatus() {
		return mtStatus;
	}
	public void setMtStatus(int mtStatus) {
		this.mtStatus = mtStatus;
	}

}
