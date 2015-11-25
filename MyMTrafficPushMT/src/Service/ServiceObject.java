package Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import Service.Service.ServiceType;

public class ServiceObject implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Integer ServiceID = 0;
	public String ServiceName = "";
	public String RegKeyword = "";
	public String DeregKeyword = "";
	public String TableName = "";
	public String PushTime = "";
	public Integer MTNumber = 20;

	public Service.ServiceType mServiceType = ServiceType.NoThing;

	public ServiceObject()
	{

	}

	/**
	 * Lấy danh sách trả tin trong ngày hôm nay của dịch vụ
	 * 
	 * @return
	 * @throws Exception
	 */
	public Vector<Date> GetPushTime() throws Exception
	{
		try
		{
			Vector<Date> mListPushTime = new Vector<Date>();

			if (PushTime == null || PushTime == "")
				return mListPushTime;

			String[] Arr = PushTime.split("\\|");

			if (Arr.length < 1)
				return mListPushTime;

			for (String item : Arr)
			{
				String[] arr_time = item.split("\\:");
				if (arr_time.length != 2)
					continue;

				Integer HourPush = Integer.parseInt(arr_time[0]);
				Integer MinutePush = Integer.parseInt(arr_time[1]);

				Calendar mCalPush = Calendar.getInstance();
				mCalPush.set(Calendar.HOUR_OF_DAY, HourPush);
				mCalPush.set(Calendar.MINUTE, MinutePush);
				mCalPush.set(Calendar.SECOND, 0);
				mCalPush.set(Calendar.MILLISECOND, 0);

				mListPushTime.add(mCalPush.getTime());

			}

			return mListPushTime;
		}
		catch (Exception ex)
		{

			throw ex;
		}
	}

	/**
	 * Khung giờ hiện tại đang push tin
	 */
	public Date CurrentPushTime = null;

	public boolean IsNull()
	{
		if (ServiceID == 0)
			return true;
		else
			return false;
	}

	
}