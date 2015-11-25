package MyProcess;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyDefine.PushThreadObject;
import MyDefine.PushThreadObject.PushType;
import MyProcessServer.Common;
import MyProcessServer.LocalConfig;
import MyProcessServer.MainProcess;
import MyUtility.MyConfig;
import MyUtility.MyLogger;
import MyUtility.MyThread;
import Service.ActionLog.ActionLogType;
import Service.News;
import Service.NewsObject;
import Service.Service.ServiceType;
import Service.ServiceObject;

/**
 * Kiểm tra liên tục thời gian trả tin cho từng dịch vụ. Nếu tồn tại tin theo
 * đúng giờ trả tin của dịch vụ, thì tiến hành trả tin cho khách hàng
 * 
 * @author Administrator
 * 
 */
public class CheckPushMT extends MyThread
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	public CheckPushMT()
	{

	}

	public void doRun()
	{
		while (MainProcess.ProcessData)
		{
			mLog.log.debug("---------------BAT DAU CHECK PUSH --------------------");
			boolean IsPushMT = false;
			try
			{
				// Xóa bỏ thread đã push tin xong
				Common.DeleteFisnishThread();

				for (ServiceObject mServiceObject : MainProcess.mListServiceObject)
				{
					boolean IsPush = false;
					if (mServiceObject.mServiceType == ServiceType.PushByTimer)
					{
						// Nếu dịch vụ bắn tin theo khung giờ
						// Kiểm tra thời gian push tin của dịch vụ
						for (Date mDate : mServiceObject.GetPushTime())
						{
							if (CheckPushTime(mDate))
							{
								// lưu lại khung giờ push tin của dịch vụ
								mServiceObject.CurrentPushTime = mDate;
								IsPush = true;
								break;
							}
						}
					}
					else if (mServiceObject.mServiceType == ServiceType.PushNow)
					{
						IsPush = true;
					}
					else if (mServiceObject.mServiceType == ServiceType.PushByTimerAndHot)
					{
						IsPush = true;
					}

					if (IsPush)
					{
						NewsObject mNewsObject = GetOneLastMT(mServiceObject);

						// Kiểm tra tin tức # null thì mới push tin và
						if (mNewsObject.IsNull())
						{
							mLog.log.debug("Khong co ban tin nao ban cho dich vu:" + mServiceObject.ServiceName);
							continue;
						}

						// Dịch vụ này đang push tin thì không push tin nữa
						if (Common.CheckPushingMT(mServiceObject, PushType.Pushing))
						{
							mLog.log.debug("Dich vu nay dang push tin, nen ko the tiep tuc push, dich vu:" + mServiceObject.ServiceName);
							continue;
						}

						// Update tình trạng cho tin
						if (UpdateNewsStatus(mNewsObject, News.Status.Complete))
						{
							mLog.log.debug("Update Thanh cong tinh trang cho tin da duoc push NewsID:" + mNewsObject.NewsID.toString());
						}
						else
						{
							mLog.log.debug("Update Fail tinh trang cho tin da duoc push NewsID:" + mNewsObject.NewsID.toString());
						}

						// Chạy thread Push tin
						RunThreadPushMT(mServiceObject, mNewsObject);
					}
				}
			}
			catch (Exception ex)
			{
				mLog.log.error(ex);
			}
			try
			{
				if (!IsPushMT)
				{
					mLog.log.debug("Khong co dich vu  nao can push MT");
				}

				mLog.log.debug("CHECK PUSH SE Delay " + LocalConfig.CHECK_PUSH_TIME_DELAY + " Phut.");
				mLog.log.debug("---------------KET THUC CHECK PUSH --------------------");
				sleep(LocalConfig.CHECK_PUSH_TIME_DELAY * 60 * 1000);
			}
			catch (InterruptedException ex)
			{
				mLog.log.error("Error Sleep thread", ex);
			}
		}
	}

	/**
	 * Update tình trang cho tin đang hoặc đã bắn.
	 * 
	 * @param mNewsObject
	 * @param mStatus
	 * @return
	 * @throws Exception
	 */
	private boolean UpdateNewsStatus(NewsObject mNewsObject, News.Status mStatus) throws Exception
	{
		try
		{
			MyTableModel mTable = MainProcess.mTemplate.mNews;
			mTable.Clear();
			MyDataRow mRow = mTable.CreateNewRow();
			mRow.SetValueCell("NewsID", mNewsObject.NewsID);
			mRow.SetValueCell("StatusID", mStatus.GetValue());
			mRow.SetValueCell("StatusName", mStatus.toString());

			mTable.AddNewRow(mRow);

			News mNews = new News(LocalConfig.PoolName_Data_SQL);
			return mNews.Update(1, mTable.GetXML());
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Gửi MT cho khách hàng của dịch vụ
	 * 
	 * @param mServiceObject
	 * @param mNewsObject
	 */
	private void RunThreadPushMT(ServiceObject mServiceObject, NewsObject mNewsObject)
	{
		try
		{
			mLog.log.debug("-------------------------");
			mLog.log.debug("Bat dau ban tin cho dich vu:" + mServiceObject.ServiceName + " || va ban tin co NewsID:" + mNewsObject.NewsID);

			for (int j = 0; j < LocalConfig.PUSH_MT_PROCESS_NUMBER; j++)
			{
				PushMT mPushMT = new PushMT();
				mPushMT.mPTObject.mActionLogType = ActionLogType.PustMT;
				mPushMT.mPTObject.mServiceObject = mServiceObject;
				mPushMT.mPTObject.mNewsObject = mNewsObject;
				mPushMT.mPTObject.ProcessIndex = j;
				mPushMT.mPTObject.mPushType = PushType.Pushing;
				mPushMT.mPTObject.ProcessNumber = LocalConfig.PUSH_MT_PROCESS_NUMBER;
				mPushMT.mPTObject.RowCount = LocalConfig.PUSH_MT_ROWCOUNT;
				mPushMT.mPTObject.StartDate = Calendar.getInstance().getTime();

				Calendar mCal_MaxPushTime = Calendar.getInstance();
				mCal_MaxPushTime.setTime(mPushMT.mPTObject.mServiceObject.CurrentPushTime);
				mCal_MaxPushTime.add(Calendar.MINUTE, LocalConfig.CHECK_PUSH_TIME_INTERVAL);

				// Thời gian sẽ cho phép dịch vụ này push tin tiếp
				mPushMT.mPTObject.MaxPushTime = mCal_MaxPushTime.getTime();

				// Integer ThreadIndex = MainProcess.mListPushMTThread.size();
				MainProcess.mListPushThreadStop.add(mPushMT.mPTObject);

				mPushMT.setPriority(Thread.MAX_PRIORITY);
				mPushMT.start();
			}
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	/**
	 * Lấy tin tức cho dịch vụ
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private NewsObject GetMTByService(ServiceObject mServiceObject) throws Exception
	{
		try
		{
			NewsObject mNewsObject = new NewsObject();
			Vector<NewsObject> mList = new Vector<NewsObject>();

			News mNews = new News(LocalConfig.PoolName_Data_SQL);

			if (mServiceObject.mServiceType == ServiceType.PushByTimer)
			{
				// Push tin theo khung giờ
				Calendar mCal_Begin = Calendar.getInstance();
				Calendar mCal_End = Calendar.getInstance();

				mCal_Begin.add(Calendar.MINUTE, LocalConfig.CHECK_PUSH_TIME_INTERVAL * -1);
				mCal_End.add(Calendar.MINUTE, LocalConfig.CHECK_PUSH_TIME_INTERVAL);

				MyTableModel mTable = mNews.Select(8, mServiceObject.ServiceID.toString(), MyConfig.Get_DateFormat_InsertDB().format(mCal_Begin.getTime()),
						MyConfig.Get_DateFormat_InsertDB().format(mCal_End.getTime()));

				mList = Common.GetNews(mTable);
			}
			else if (mServiceObject.mServiceType == ServiceType.PushNow)
			{
				// Lấy 1 tin mới nhất của dịch vụ
				MyTableModel mTable = mNews.Select(11, mServiceObject.ServiceID.toString());
				mList = Common.GetNews(mTable);
			}
			else if (mServiceObject.mServiceType == ServiceType.PushByTimerAndHot)
			{
				// Push tin theo khung giờ, nếu có tin hot thì push trước
				MyTableModel mTable = mNews.Select(10, mServiceObject.ServiceID.toString(), News.NewsType.HOT.GetValue().toString());

				if (mTable.IsEmpty())
				{
					// Push tin theo khung giờ
					Calendar mCal_Begin = Calendar.getInstance();
					Calendar mCal_End = Calendar.getInstance();

					mCal_Begin.add(Calendar.MINUTE, LocalConfig.CHECK_PUSH_TIME_INTERVAL * -1);
					mCal_End.add(Calendar.MINUTE, LocalConfig.CHECK_PUSH_TIME_INTERVAL);

					mTable = mNews.Select(8, mServiceObject.ServiceID.toString(), MyConfig.Get_DateFormat_InsertDB().format(mCal_Begin.getTime()),
							MyConfig.Get_DateFormat_InsertDB().format(mCal_End.getTime()));
				}
				mList = Common.GetNews(mTable);
			}

			if (mList.size() > 0)
				mNewsObject = mList.get(0);

			return mNewsObject;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	private NewsObject GetOneLastMT(ServiceObject mServiceObject) throws Exception
	{
		News mNews = new News(LocalConfig.PoolName_Data_SQL);
		NewsObject mNewsObject = new NewsObject();
		Vector<NewsObject> mList = new Vector<NewsObject>();

		// Lấy 1 tin mới nhất của dịch vụ
		MyTableModel mTable = mNews.Select(11, mServiceObject.ServiceID.toString());
		mList = Common.GetNews(mTable);

		if (mList.size() > 0)
			mNewsObject = mList.get(0);

		return mNewsObject;
	}

	/**
	 * Kiểm tra thời gian pushtime So sánh giữa PushTime và thời gian hiện tại,
	 * nếu bằng thì return = true Không thỏa mãn thì return = false;
	 * 
	 * @param PushTime
	 * @return
	 * @throws Exception
	 */
	private boolean CheckPushTime(Date PushTime) throws Exception
	{
		try
		{
			if(PushTime == null)
				return false;
			
			Calendar mCal_Current = Calendar.getInstance();
			Calendar mCal_Begin = Calendar.getInstance();
			Calendar mCal_End = Calendar.getInstance();

			mCal_Begin.setTime(PushTime);
			mCal_End.setTime(PushTime);

			mCal_Begin.add(Calendar.MINUTE, LocalConfig.CHECK_PUSH_TIME_INTERVAL * -1);
			mCal_End.add(Calendar.MINUTE, LocalConfig.CHECK_PUSH_TIME_INTERVAL);

			/*
			 * String Current =
			 * MyConfig.Get_DateFormat_InsertDB().format(mCal_Current.getTime());
			 * String Begin =
			 * MyConfig.Get_DateFormat_InsertDB().format(mCal_Begin.getTime()); String
			 * End = MyConfig.Get_DateFormat_InsertDB().format(mCal_End.getTime());
			 * System.out.print("Current::::" + Current);
			 * System.out.print("Begin::::" + Begin); System.out.print("End::::"
			 * + End);
			 */

			if (mCal_Current.after(mCal_Begin) && mCal_Current.before(mCal_End))
				return true;
			else
				return false;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

}
