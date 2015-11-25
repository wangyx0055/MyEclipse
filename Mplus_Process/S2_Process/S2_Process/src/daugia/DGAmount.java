package daugia;

public class DGAmount {
	
	//ID, USER_ID, SERVICE_ID, TIME_SEND_MO, DG_AMOUNT, MA_SP
	private int id = -1;
	private String userId = "";
	private String serviceId = "9209";
	private String timeSendMO = "0000-00-00 00:00:00";
	private String dgAmount = "0";
	private String maSP = "";
	private int numberDG = 0;
	private int numberWinnerTmp = 0;
	private int winRank = 0; // 1: giai nhat; 2: nhi; 3: ba
	private String tenSp ="";
	public String getTenSp() {
		return tenSp;
	}
	public void setTenSp(String tenSp) {
		this.tenSp = tenSp;
	}
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
	public String getTimeSendMO() {
		return timeSendMO;
	}
	public void setTimeSendMO(String timeSendMO) {
		this.timeSendMO = timeSendMO;
	}
	public String getDgAmount() {
		return dgAmount;
	}
	public void setDgAmount(String dgAmount) {
		this.dgAmount = dgAmount;
	}
	public String getMaSP() {
		return maSP;
	}
	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}
	
	public int getNumberDG() {
		return numberDG;
	}
	public void setNumberDG(int numberDG) {
		this.numberDG = numberDG;
	}
	
	public int getNumberWinnerTmp() {
		return numberWinnerTmp;
	}
	public void setNumberWinnerTmp(int numberWinnerTmp) {
		this.numberWinnerTmp = numberWinnerTmp;
	}
	
	public int getWinRank() {
		return winRank;
	}
	public void setWinRank(int winRank) {
		this.winRank = winRank;
	}
	
	
	
		
}
