package phase4;

public class ChatSendDailyObj {

	private int id = 0;
	private String userSend = "";	
	private String userReceive = "";	
	private String timeSend = "";
	private int isResponse	= 0;
	private int numberSms = 0;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserSend() {
		return userSend;
	}
	public void setUserSend(String userSend) {
		this.userSend = userSend;
	}
	public String getUserReceive() {
		return userReceive;
	}
	public void setUserReceive(String userReceive) {
		this.userReceive = userReceive;
	}
	public String getTimeSend() {
		return timeSend;
	}
	public void setTimeSend(String timeSend) {
		this.timeSend = timeSend;
	}
	public int getIsResponse() {
		return isResponse;
	}
	public void setIsResponse(int isResponse) {
		this.isResponse = isResponse;
	}
	public int getNumberSms() {
		return numberSms;
	}
	public void setNumberSms(int numberSms) {
		this.numberSms = numberSms;
	}
	
}
