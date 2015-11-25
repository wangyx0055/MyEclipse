package phase4;

public class ChatBlockedObj {

	private int id = 0;
	private String userRequest = "";	
	private String userBlocked = "";	
	private String timeRequest = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserRequest() {
		return userRequest;
	}
	public void setUserRequest(String userRequest) {
		this.userRequest = userRequest;
	}
	public String getUserBlocked() {
		return userBlocked;
	}
	public void setUserBlocked(String userBlocked) {
		this.userBlocked = userBlocked;
	}
	public String getTimeRequest() {
		return timeRequest;
	}
	public void setTimeRequest(String timeRequest) {
		this.timeRequest = timeRequest;
	}
	
}
