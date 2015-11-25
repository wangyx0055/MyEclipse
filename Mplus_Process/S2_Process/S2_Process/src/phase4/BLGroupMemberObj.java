package phase4;

public class BLGroupMemberObj {
	
	private int id = 0;
	private String userId = "";
	private int groupId = 0;
	private String timeJoin = "";
	private int isCharging = 0; // 0: not charge, 1: charge success, 2: already sent to charge
	
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
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getTimeJoin() {
		return timeJoin;
	}
	public void setTimeJoin(String timeJoin) {
		this.timeJoin = timeJoin;
	}
	
	/** 0: not charge, 1: charge success, 2: already sent to charge
	 * 
	 * @return
	 */
	public int getIsCharging() {
		return isCharging;
	}
	public void setIsCharging(int isCharging) {
		this.isCharging = isCharging;
	}


	
}
