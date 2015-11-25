package dbcdr;

/**
 * Định nghĩ về nội dung file CDR dành cho VMS Wap charging
 * @author Administrator
 *
 */
public class CDRObject 
{
	public int ChargingID = 0;
	
	/**
	 * Ngày tháng theo định dạng: yyyymmddhhmmss
	 */
	public String Datetime = "";
	/**
	 * Số điện thoại:
	 */
	public String A_Number ="";
	/**
	 * Đầu số
	 */
	public String B_Number="";
	/*
	 * Category ID  theo quy định của VMS:
	 */
	public String EventID ="";
	public String CPID="000066";
	/**
	 * ID của nội dung 
	 */
	public String ContentID ="";
	/**
	 * Cho biết trang thái CDR: 0 là không thành công, 1 là thành công
	 */
	public String Status = "1";
	/**
	 * Giá của nội dung
	 */
	public String Cost = "0";
	/**
	 * Kệnh thực hiện charging: mặc định là WAP
	 */
	public String ChannelType="WAP";
	
	/**
	 * Thông tin thêm, nếu ko gì gì thì để mặc định là 1
	 */
	public String Information = "1";

	public boolean IsNull()
	{
		if(A_Number.equals(""))
			return true;
		else
			return false;
	}
	
	public String GetData()
	{
		String DataLine = Datetime+":"+A_Number+":"+B_Number+":"+EventID+":"+CPID+":"+ContentID+":"+Status+":"+Cost+":"+ChannelType+":"+Information;
		return DataLine;
	}
	
}
