package my.send.sms;

/**
 * Kết quả trả về từ VMS khi gửi MT
 * 
 * @author Administrator
 * 
 */
public class SendResultObject
{
	/**
	 * nếu gửi thành công thì đây là ID của MT từ phía VMS
	 */
	public String VMS_MT_ID = "";

	/**
	 * Mã lỗi trong trường hợp gửi không thành công
	 */
	public String faultcode = "";

	/**
	 * Chuỗi chi tiết ý nghĩa mã lỗi
	 */
	public String faultstring = "";

	/**
	 * Cho biết gửi thành công hay không
	 */
	public Boolean Result()
	{
		if (VMS_MT_ID.equalsIgnoreCase("")) return false;
		else return true;
	}

}
