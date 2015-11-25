package daugia;

public class SanPhamDG {

	//ID, MA_SP, TEN_SP, START_DATE, END_DATE, IMAGE_LINK, MOTA_SP
	
	private int id = -1;
	private String maSP = "";
	private String tenSP = "";
	private String startDate = "";
	private String endDate = "";
	private String imageLink = "";
	private String motaSP = "";
	private int isSendMTWeekly = 0; // 0 chua tra MT weekly; 1: da tra MT weekly
	private String giaSP = "";
	
	private String timeDaily = "";
	private String timeAlert = "";
	private String timeManual = "";
		
	public String getGiaSP() {
		return giaSP;
	}
	public void setGiaSP(String giaSP) {
		this.giaSP = giaSP;
	}
	public int getIsSendMTWeekly() {
		return isSendMTWeekly;
	}
	public void setIsSendMTWeekly(int isSendMTWeekly) {
		this.isSendMTWeekly = isSendMTWeekly;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMaSP() {
		return maSP;
	}
	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}
	public String getTenSP() {
		return tenSP;
	}
	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getImageLink() {
		return imageLink;
	}
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}
	public String getMotaSP() {
		return motaSP;
	}
	public void setMotaSP(String motaSP) {
		this.motaSP = motaSP;
	}
	
	public String getTimeDaily() {
		return timeDaily;
	}
	public void setTimeDaily(String timeDaily) {
		this.timeDaily = timeDaily;
	}
	public String getTimeAlert() {
		return timeAlert;
	}
	public void setTimeAlert(String timeAlert) {
		this.timeAlert = timeAlert;
	}
	
	public String getTimeManual() {
		return timeManual;
	}
	public void setTimeManual(String timeManual) {
		this.timeManual = timeManual;
	}
		
}
