package Service;

import java.sql.Timestamp;
import java.util.Vector;

import MyDataSource.MyTableModel;
import Service.News.NewsType;
import Service.News.Status;

public class NewsObject implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Integer NewsID = 0;
	public Integer ServiceID = 0;
	/**
	 * Nội dung của bản tin
	 */
	public String Content = "";
	public Integer CharCount = 0;

	public News.Status mStatus = Status.NoThing;
	public News.NewsType mNewsType = NewsType.NoThing;

	/**
	 * Danh sách tên đường cho bản tin (nếu có)
	 */
	public Vector<Integer> ListStreetID = new Vector<Integer>();

	/**
	 * Thời gian push tin cho khách hàng
	 */
	public Timestamp PushTime = null;

	public boolean IsNull()
	{
		if (NewsID < 1 || ServiceID < 1 || Content == "")
			return true;
		else
			return false;
	}

	/**
	 * Lấy số MT bắn xuống cho khách hàng
	 * @return
	 */
	public Integer MTCount()
	{
		if(Content == null)
			return 0;
		Integer MTLength = Content.length();
		Integer Count = MTLength / 160;
		if(MTLength % 160 != 0)
			Count++;
		
		return Count;
	}
	
	public NewsObject()
	{

	}

	public NewsObject Convert(MyTableModel mTable) throws Exception
	{
		try
		{
			NewsObject mNewsObject = new NewsObject();

			if (mTable.IsEmpty())
				return mNewsObject;
			
			
			return mNewsObject;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
