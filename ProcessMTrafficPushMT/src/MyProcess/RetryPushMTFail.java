package MyProcess;

import java.util.Calendar;
import java.util.Iterator;

import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyDefine.MTObject;
import MyDefine.PushThreadObject.PushType;
import MyProcessServer.Common;
import MyProcessServer.LocalConfig;
import MyProcessServer.MainProcess;
import MyUtility.MyConfig;
import MyUtility.MyConvert;
import MyUtility.MyLogger;
import MyUtility.MyThread;
import Service.ActionLog;
import Service.ActionLog.ActionLogType;

/**
 * Xử lý các MT gửi không thành công (MTFail)
 * 
 * @author Administrator
 * 
 */
public class RetryPushMTFail extends MyThread
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	public RetryPushMTFail()
	{

	}

	public void doRun()
	{
		while (MainProcess.ProcessData)
		{

			MyTableModel mTable_ActionLog = null;

			try
			{
				mTable_ActionLog = MainProcess.mTemplate.mActionLog.clone();
				mTable_ActionLog.Clear();
				Integer RowCount = 0;
				mLog.log.debug("---------------BAT DAU RETRY PUSH MT FAIL--------------------");

				for (Iterator<MTObject> mItem = MainProcess.mListMTFail.listIterator(); mItem.hasNext();)
				{
					if (MainProcess.StopPushMT)
					{
						return;
					}
					MTObject mMTObject = mItem.next();

					// Nếu dịch vụ đang push tin thì không được phép bắn MT của
					// dịch vụ này
					if (LocalConfig.RETRY_PUSH_MT_FAIL_ALLOW_PUSHING == 0)
					{
						// Dịch vụ này đang push tin thì không push tin nữa
						if (Common.CheckPushingMT(mMTObject.mServiceObject, PushType.RetryPushing_MTFail))
						{
							mLog.log.debug("Dich vu nay dang push tin, nen ko RETRY PUSH MT, dich vu:" + mMTObject.mServiceObject.ServiceName);
							continue;
						}
					}

					// Kiểm tra thời gian, nếu thời gian vướt quá thời gian cho
					// phép thì MT sẽ bị Remove mà ko bắn cho khách hàng
					Calendar mCal_Current = Calendar.getInstance();
					Calendar mCal_QueueDate = Calendar.getInstance();

					mCal_QueueDate.setTime(mMTObject.QueueDate);

					mCal_QueueDate.add(Calendar.MINUTE, LocalConfig.RETRY_PUSH_MT_FAIL_TIME_INTERVAL);
					if (mCal_QueueDate.before(mCal_Current))
					{
						MyLogger.WriteDataLog(LocalConfig.LogDataFolder, "_MT_FAIL",
								"XOA MT --> " + mMTObject.GetLogString("Thoi gian vuot qua " + LocalConfig.RETRY_PUSH_MT_FAIL_TIME_INTERVAL + " phut."));

						if (MainProcess.ListMTFailIsUsing)
						{
							synchronized (MainProcess.mListMTFail)
							{
								MainProcess.mListMTFail.wait();
								MainProcess.ListMTFailIsUsing = true;
								mItem.remove();
								MainProcess.mListMTFail.notify();
								MainProcess.ListMTFailIsUsing = false;
							}
						}
						else
						{
							synchronized (MainProcess.mListMTFail)
							{
								MainProcess.ListMTFailIsUsing = true;
								mItem.remove();
								MainProcess.mListMTFail.notify();
								MainProcess.ListMTFailIsUsing = false;
							}
						}
						continue;
					}
					if (mMTObject.RetryCount >= LocalConfig.RETRY_PUSH_MT_FAIL_MAX_COUNT)
					{
						MyLogger.WriteDataLog(LocalConfig.LogDataFolder, "_MT_FAIL",
								"XOA MT --> " + mMTObject.GetLogString("RetryCount vuot qua " + LocalConfig.RETRY_PUSH_MT_FAIL_MAX_COUNT + " phut."));

						if (MainProcess.ListMTFailIsUsing)
						{
							synchronized (MainProcess.mListMTFail)
							{
								MainProcess.mListMTFail.wait();
								MainProcess.ListMTFailIsUsing = true;
								mItem.remove();
								MainProcess.mListMTFail.notify();
								MainProcess.ListMTFailIsUsing = false;
							}
						}
						else
						{
							synchronized (MainProcess.mListMTFail)
							{
								MainProcess.ListMTFailIsUsing = true;
								mItem.remove();
								MainProcess.mListMTFail.notify();
								MainProcess.ListMTFailIsUsing = false;
							}
						}

						continue;
					}

					// Kiểm tra xem số lượng MT được đã vượt quá số lượng MT cho
					// phép hay chưa
					if (Common.CheckMTNumber(mMTObject.MSISDN, mMTObject.mServiceObject, mMTObject.mNewsObject.MTCount()))
					{
						MyLogger.WriteDataLog(LocalConfig.LogDataFolder, "_MT_FAIL",
								"XOA MT --> " + mMTObject.GetLogString("So luong MT vuat qua so luong cho phep trong ngay."));

						if (MainProcess.ListMTFailIsUsing)
						{
							synchronized (MainProcess.mListMTFail)
							{
								MainProcess.mListMTFail.wait();
								MainProcess.ListMTFailIsUsing = true;
								mItem.remove();
								MainProcess.mListMTFail.notify();
								MainProcess.ListMTFailIsUsing = false;
							}
						}
						else
						{
							synchronized (MainProcess.mListMTFail)
							{
								MainProcess.ListMTFailIsUsing = true;
								mItem.remove();
								MainProcess.mListMTFail.notify();
								MainProcess.ListMTFailIsUsing = false;
							}
						}

						continue;
					}

					mMTObject.RetryDate = Calendar.getInstance().getTime();
					mMTObject.RetryCount++;

					if (Common.SendMT(mMTObject, ActionLogType.PushMTFail_Retry))
					{
						RowCount++;
						MyDataRow mRow_Action = mTable_ActionLog.CreateNewRow();
						mRow_Action.SetValueCell("ServiceID", mMTObject.mServiceObject.ServiceID);
						mRow_Action.SetValueCell("MSISDN", mMTObject.MSISDN);
						mRow_Action.SetValueCell("LogDate", MyConfig.Get_DateFormat_InsertDB().format(Calendar.getInstance().getTime()));
						mRow_Action.SetValueCell("ActionTypeID", ActionLogType.PushMTFail_Retry.GetValue());
						mRow_Action.SetValueCell("ActionTypeName", ActionLogType.PushMTFail_Retry.toString());
						mRow_Action.SetValueCell("LogContent", "NewsID:" + mMTObject.mNewsObject.NewsID);
						mRow_Action.SetValueCell("MO", "");
						mRow_Action.SetValueCell("MT", mMTObject.mNewsObject.Content);

						mRow_Action.SetValueCell("LogPID", MyConvert.GetPIDByDate(Calendar.getInstance().getTime()));

						mTable_ActionLog.AddNewRow(mRow_Action);

						if (MainProcess.ListMTFailIsUsing)
						{
							synchronized (MainProcess.mListMTFail)
							{
								MainProcess.mListMTFail.wait();
								MainProcess.ListMTFailIsUsing = true;
								mItem.remove();
								MainProcess.mListMTFail.notify();
								MainProcess.ListMTFailIsUsing = false;
							}
						}
						else
						{
							synchronized (MainProcess.mListMTFail)
							{
								MainProcess.ListMTFailIsUsing = true;
								mItem.remove();
								MainProcess.mListMTFail.notify();
								MainProcess.ListMTFailIsUsing = false;
							}
						}
					}

					if (RowCount >= 10)
					{
						InsertActionLog(mTable_ActionLog);
						mTable_ActionLog.Clear();
						RowCount = 0;
					}
				}

			}
			catch (Exception ex)
			{
				mLog.log.error(ex);
			}

			try
			{
				if (mTable_ActionLog.GetRowCount() > 0)
				{
					InsertActionLog(mTable_ActionLog);
					mTable_ActionLog.Clear();
				}

				mLog.log.debug("RETRY PUSH MT SE DELAY " + LocalConfig.RETRY_PUSH_MT_FAIL_TIME_DELAY + " Phut.");
				mLog.log.debug("---------------KET THUC RETRY PUSH MT FAIL--------------------");
				sleep(LocalConfig.RETRY_PUSH_MT_FAIL_TIME_DELAY * 60 * 1000);

			}
			catch (InterruptedException ex)
			{
				mLog.log.error("Error Sleep thread", ex);
			}
			catch (Exception ex)
			{
				mLog.log.error("Error Sleep thread", ex);
			}
		}
	}

	private boolean InsertActionLog(MyTableModel mTable) throws Exception
	{
		try
		{
			if (mTable.GetRowCount() < 1)
				return true;

			ActionLog mActionLog = new ActionLog(LocalConfig.PoolName_Data_SQL);
			return mActionLog.Insert(0, mTable.GetXML());
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
