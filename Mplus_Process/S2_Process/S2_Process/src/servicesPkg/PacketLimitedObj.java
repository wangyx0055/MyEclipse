package servicesPkg;

public class PacketLimitedObj {

	private int id = -1;
	private String commandCode = "";
	private int numberMT = 0;
	private int dayNumb = 0;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}
	public int getNumberMT() {
		return numberMT;
	}
	public void setNumberMT(int numberMT) {
		this.numberMT = numberMT;
	}
	public int getDayNumb() {
		return dayNumb;
	}
	public void setDayNumb(int dayNumb) {
		this.dayNumb = dayNumb;
	}
	
	
}
