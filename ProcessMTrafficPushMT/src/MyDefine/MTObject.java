package MyDefine;

import java.util.Date;

import MyUtility.MyConfig;
import Service.NewsObject;
import Service.ServiceObject;

/**
 * Là đối tượng lưu trữ các MT cần push cho khách hàng Thường thì sẽ được dùng
 * cho các MT push không thành công
 * 
 * @author Administrator
 * 
 */
public class MTObject implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public String MSISDN = "";

	/**
	 * Order ID của record trong Table Subscriber
	 */
	public Integer OrderID = 0;

	/**
	 * Số thứ tự của Process Push MT này
	 */
	public Integer ProcessIndex = 0;

	public ServiceObject mServiceObject = new ServiceObject();

	public NewsObject mNewsObject = new NewsObject();

	/**
	 * Số lần push lại không thành công
	 */
	public Integer RetryCount = 0;

	/**
	 * Thơi gian đưa MT này vào queue
	 */
	public Date QueueDate = null;

	public String ResultFromVNP = "";

	/**
	 * Thời gian retry lần cuối
	 */
	public Date RetryDate = null;

	public boolean IsNull()
	{
		if (MSISDN == "" || mServiceObject.IsNull() || mNewsObject.IsNull())
			return true;
		else
			return false;
	}

	public String GetLogString(String Suffix) throws Exception
	{
		try
		{
			if (IsNull())
				return "";
			String Fomart = "MSISDN:%s || ProcessIndex:%s || OrderID:%s || ServiceID:%s || NewsID:%s || PushTime:%s || QueueDate:%s || RetryCount:%s || RetryDate:%s || Suffix:%s";
			return String.format(Fomart, new Object[] { MSISDN, ProcessIndex, OrderID, mServiceObject.ServiceID, mNewsObject.NewsID,
					MyConfig.Get_DateFormat_InsertDB().format(mServiceObject.CurrentPushTime), MyConfig.Get_DateFormat_InsertDB().format(QueueDate), RetryCount,
					(RetryDate == null ? "null" : MyConfig.Get_DateFormat_InsertDB().format(RetryDate)), Suffix });
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
