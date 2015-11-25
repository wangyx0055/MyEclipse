package dbcdr;

/**
 * Định nghĩ về nội dung file CDR dành cho VMS Wap charging
 * @author Administrator
 *
 */
public class CDRObject 
{
	public String ID = "";
	public String ShortCode = "";
	public String MSISDN  ="";
	public String ReceiveDate = "";
	public String PushDate ="";
	public String Keyword = "";
	public String CreateDate = "";
	public String Price = "";
	public String CPCode = "HBCOM";
	
	/**
	 * Cho biết record này có được ghi CDR sang VNP hay không
	 * (Chỉ có những tin nhắn sai cú phát mới được ghi cdr)
	 */
	public boolean IsWriteCDR = false;
	/*1.Đầu shortcode.
	2. Số thuê bao gửi tin sai cú pháp.
	3. Thời gian thuê bao gửi tin lên hệ thống của CP: dd/mm/yyyy hh24:mi:ss: 27052012 17:39:54
	4. Thời gian CP trả tin cho khách hàng: dd/mm/yyyy hh24:mi:ss (ví dụ: 27052012 17:39:54).
	5. Cú pháp nhắn tin của thuê bao (cú pháp sai)
	6. Ngày gửi xử lý: dd/mm/yyyy: 27052012
	7. Mức cước cần hoàn 
	8. Mã CP (có thể đặt tên viết tắt của CP).
*/
	
	

	public boolean IsNull()
	{
		if(MSISDN.equals(""))
			return true;
		else
			return false;
	}
	
	public String GetData()
	{
		String DataLine = ShortCode+";"+MSISDN+";"+ReceiveDate+";"+PushDate+";"+Keyword+";"+CreateDate+";"+Price+";"+CPCode;
		return DataLine;
	}
	
}
