package MyProcess;

import java.util.Calendar;
import java.util.Iterator;

import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyDefine.MTObject;
import MyDefine.PushThreadObject;
import MyDefine.PushThreadObject.PushType;
import MyProcessServer.Common;
import MyProcessServer.LocalConfig;
import MyProcessServer.MainProcess;
import MyUtility.MyConfig;
import MyUtility.MyConvert;
import MyUtility.MyLogger;
import MyUtility.MyThread;
import Service.ActionLog.ActionLogType;

public class RetryPushMT extends MyThread
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	public RetryPushMT()
	{

	}

	public void doRun()
	{
		while (MainProcess.ProcessData)
		{
			try
			{
				mLog.log.debug("---------------BAT DAU RETRY THEAD STOP--------------------");

				for (Iterator<PushThreadObject> mItem = MainProcess.mListPushThreadStop.listIterator(); mItem.hasNext();)
				{
					if (MainProcess.StopPushMT)
					{
						return;
					}
					PushThreadObject mPTObject = mItem.next();

					// Nếu không phải là thread bị lỗi hoặc bị dừng đột ngột thì
					// ko xử lý
					if (mPTObject.mPushType != PushType.Error && mPTObject.mPushType != PushType.Stop)
					{
						continue;
					}

					// Nếu dịch vụ đang push tin thì không được phép bắn MT của
					// dịch vụ này
					if (LocalConfig.RETRY_THREAD_STOP_ALLOW_PUSHING == 0)
					{
						// Dịch vụ này đang push tin thì không push tin nữa
						if (Common.CheckPushingMT(mPTObject.mServiceObject, PushType.RetryPushing))
						{
							mLog.log.debug("Dich vu nay dang push tin, nen ko RETRY THREAD STOP, dich vu:" + mPTObject.mServiceObject.ServiceName);
							continue;
						}
					}

					// Kiểm tra thời gian, nếu thời gian vướt quá thời gian cho
					// phép thì MT sẽ bị Remove mà ko bắn cho khách hàng
					Calendar mCal_Current = Calendar.getInstance();
					Calendar mCal_QueueDate = Calendar.getInstance();

					mCal_QueueDate.setTime(mPTObject.QueueDate);

					mCal_QueueDate.add(Calendar.MINUTE, LocalConfig.RETRY_THREAD_STOP_TIME_INTERVAL);
					if (mCal_QueueDate.before(mCal_Current))
					{
						MyLogger.WriteDataLog(LocalConfig.LogDataFolder, "_MT_FAIL",
								"XOA THREAD STOP --> " + mPTObject.GetLogString("Thoi gian vuot qua " + LocalConfig.RETRY_THREAD_STOP_TIME_INTERVAL + " phut."));
						if (MainProcess.ListPushThreadStopIsUsing)
						{
							synchronized (MainProcess.mListPushThreadStop)
							{
								MainProcess.mListPushThreadStop.wait();
								MainProcess.ListPushThreadStopIsUsing = true;
								mItem.remove();
								MainProcess.mListPushThreadStop.notify();
								MainProcess.ListPushThreadStopIsUsing = false;
							}
						}
						else
						{
							synchronized (MainProcess.mListPushThreadStop)
							{
								MainProcess.ListPushThreadStopIsUsing = true;
								mItem.remove();
								MainProcess.mListPushThreadStop.notify();
								MainProcess.ListPushThreadStopIsUsing = false;
							}
						}
						continue;
					}
					if (mPTObject.RetryCount >= LocalConfig.RETRY_THREAD_STOP_MAX_COUNT)
					{
						MyLogger.WriteDataLog(LocalConfig.LogDataFolder, "_THREAD_STOP",
								"XOA THREAD STOP --> " + mPTObject.GetLogString("RetryCount vuot qua " + LocalConfig.RETRY_THREAD_STOP_MAX_COUNT + " phut."));

						if (MainProcess.ListPushThreadStopIsUsing)
						{
							synchronized (MainProcess.mListPushThreadStop)
							{
								MainProcess.mListPushThreadStop.wait();
								MainProcess.ListPushThreadStopIsUsing = true;
								mItem.remove();
								MainProcess.mListPushThreadStop.notify();
								MainProcess.ListPushThreadStopIsUsing = false;
							}
						}
						else
						{
							synchronized (MainProcess.mListPushThreadStop)
							{
								MainProcess.ListPushThreadStopIsUsing = true;
								mItem.remove();
								MainProcess.mListPushThreadStop.notify();
								MainProcess.ListPushThreadStopIsUsing = false;
							}
						}

						continue;
					}

					mPTObject.RetryDate = Calendar.getInstance().getTime();
					mPTObject.RetryCount++;

					PushMT mPushMT = new PushMT();
					mPTObject.mActionLogType = ActionLogType.PushMTThreadStop_Retry;
					mPTObject.mPushType = PushType.RetryPushing;
					mPushMT.mPTObject = mPTObject;

					mPushMT.setPriority(Thread.MAX_PRIORITY);
					mPushMT.start();

					if (MainProcess.ListPushThreadStopIsUsing)
					{
						synchronized (MainProcess.mListPushThreadStop)
						{
							MainProcess.mListPushThreadStop.wait();
							MainProcess.ListPushThreadStopIsUsing = true;
							mItem.remove();
							MainProcess.mListPushThreadStop.notify();
							MainProcess.ListPushThreadStopIsUsing = false;
						}
					}
					else
					{
						synchronized (MainProcess.mListPushThreadStop)
						{
							MainProcess.ListPushThreadStopIsUsing = true;
							mItem.remove();
							MainProcess.mListPushThreadStop.notify();
							MainProcess.ListPushThreadStopIsUsing = false;
						}
					}

				}
				synchronized (MainProcess.Locker)
				{
					MainProcess.Locker.notify();
				}

			}
			catch (Exception ex)
			{
				mLog.log.error(ex);

			}

			try
			{
				synchronized (MainProcess.Locker)
				{
					MainProcess.Locker.notify();
				}

				mLog.log.debug("RETRY THREAD STOP SE DELAY " + LocalConfig.RETRY_THREAD_STOP_TIME_DELAY + " Phut.");
				mLog.log.debug("---------------KET THUC RETRY THEAD STOP--------------------");

				sleep(LocalConfig.RETRY_THREAD_STOP_TIME_DELAY * 60 * 1000);
			}
			catch (InterruptedException ex)
			{
				mLog.log.error("Error Sleep thread", ex);
			}
		}
	}
}
