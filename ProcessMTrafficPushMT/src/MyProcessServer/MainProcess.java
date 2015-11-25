package MyProcessServer;

import java.util.Vector;

import Config.MTrafficConfig;
import MyDefine.MTObject;
import MyDefine.PushThreadObject;
import MyProcess.CheckPushMT;
import MyProcess.RetryPushMT;
import MyProcess.RetryPushMTFail;
import MyUtility.MyFile;
import MyUtility.MyLogger;
import MyUtility.MyThread;
import Service.ActionLog;
import Service.News;
import Service.PushTimeLog;
import Service.Service;
import Service.ServiceObject;

public class MainProcess extends Thread
{
	static MyLogger mLog = new MyLogger(MainProcess.class.toString());

	/**
	 * Cho biết chương trình đang chạy hay không
	 */
	public static boolean ProcessData = true;

	/**
	 * Dừng push MT xuống khách hàng
	 */
	public static boolean StopPushMT = false;

	/**
	 * Chứa danh sách các MT Push thất bại
	 */
	public static Vector<MTObject> mListMTFail = new Vector<MTObject>();
	
	/**
	 * Cho biết mListMTFail đang được sử dụng ở một thread khác
	 */
	public static boolean ListMTFailIsUsing = false;

	/**
	 * Chứa danh sách các thead đang bắn tin
	 */
	public static Vector<PushThreadObject> mListPushThreadStop = new Vector<PushThreadObject>();

	/**
	 * Cho biết mListPushThreadStop đang được sử dụng ở một thread khác
	 */
	public static boolean ListPushThreadStopIsUsing = false;

	/**
	 * Chứa danh sách các dịch vụ đang được kích hoạt
	 */
	public static Vector<ServiceObject> mListServiceObject = new Vector<ServiceObject>();

	public static TableTemplate mTemplate = new TableTemplate();

	public MainProcess()
	{
		try
		{
			LocalConfig.LoadProperties("config.properties");
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	public static void main(String[] args)
	{
		System.out.println("Starting Service ");
		System.out.println("Copyright 2013 NCL. - All Rights Reserved.");
		MainProcess smsConsole = new MainProcess();
		ShutdownInterceptor shutdownInterceptor = new ShutdownInterceptor(smsConsole);
		Runtime.getRuntime().addShutdownHook(shutdownInterceptor);
		smsConsole.start();
	}

	public static Object Locker = new Object();

	public void run()
	{
		try
		{
			// Load cac table Template
			LoadTableTemplate();

			// Load các đối tượng dạng Từ điển
			Service mService = new Service(LocalConfig.PoolName_Data_SQL);
			mListServiceObject = mService.GetAllService();

			// Lấy các MT đã push không thành công, và đã được lưu xuống file,
			// khi chương trình stop
			LoadPushMTFail();

			// Load các thread dang trả tin mà bị ngưng khi stop chương trình
			LoadThreadStop();

			ProcessData = true;
			StopPushMT = false;

			RetryPushMT mRetryPushMT = new RetryPushMT();
			mRetryPushMT.start();

			synchronized (Locker)
			{
				// Chờ cho tới khi nào gọi hàm nofity
				Locker.wait();
			}

			CheckPushMT mCheckPushMT = new CheckPushMT();
			mCheckPushMT.start();

			RetryPushMTFail mRetryPushMTFail = new RetryPushMTFail();
			mRetryPushMTFail.start();
		}
		catch (Exception e)
		{
			mLog.log.error(e);
			e.printStackTrace();
		}

	}

	public static void LoadTableTemplate() throws Exception
	{
		try
		{
			// Load các Table Template
			ActionLog mActionLog = new ActionLog(LocalConfig.PoolName_Data_SQL);

			// Lấy định dạng mẫu theo trong Db của table ActionLog
			mTemplate.mActionLog = mActionLog.Select(0);

			News mNews = new News(LocalConfig.PoolName_Data_SQL);
			// Lấy định dạng mẫu theo trong Db của table ActionLog
			mTemplate.mNews = mNews.Select(0);

			PushTimeLog mPushTimeLog = new PushTimeLog(LocalConfig.PoolName_Data_SQL);
			mTemplate.mPushTimeLog = mPushTimeLog.Select(0);

		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Đọc các đối tượng Push MT không thành công từ file
	 */
	public static void LoadPushMTFail()
	{
		try
		{
			mLog.log.info("Load MT fail tu File:" + LocalConfig.PUSH_MT_FAIL_PATH);

			Vector<Object> mList = MyFile.LoadObjectFromFile(LocalConfig.PUSH_MT_FAIL_PATH);

			for (Object mObject : mList)
			{
				mListMTFail.add((MTObject) mObject);
			}
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	/**
	 * Lưu các đối tượng MT push không thành công xuống file
	 */
	public static void SavePushMTFail()
	{
		mLog.log.info("Luu MT Fail xuong File: " + LocalConfig.PUSH_MT_FAIL_PATH + " || So MT Fail: " + mListMTFail.size());

		try
		{
			Vector<Object> mList = new Vector<Object>();
			for (MTObject mObject : mListMTFail)
			{
				mList.add((Object) mObject);
			}
			MyFile.SaveObjectToFile(LocalConfig.PUSH_MT_FAIL_PATH, mList);
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	public static void LoadThreadStop()
	{
		String Test = "";
		try
		{
			mLog.log.info("Load Thread Stop tu file, FileName:" + LocalConfig.PUSH_MT_STOP_PATH);

			Vector<Object> mList = MyFile.LoadObjectFromFile(LocalConfig.PUSH_MT_STOP_PATH);

			for (Object mObject : mList)
			{
				mListPushThreadStop.add((PushThreadObject) mObject);
				PushThreadObject mItem = (PushThreadObject) mObject;
				Test += "<-->ServiceID:" + mItem.mServiceObject.ServiceID + "|ProcessNumber:" + mItem.ProcessNumber + "|ProcessIndex:" + mItem.ProcessIndex
						+ "|NewsID:" + mItem.mNewsObject.NewsID + "|MaxOrderID:" + mItem.MaxOrderID + "|PushType:" + mItem.mPushType.toString();
			}
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
		finally
		{
			mLog.log.warn(Test);
		}
	}

	public static void SaveThreadStop()
	{
		mLog.log.info("Luu Thread Stop xuong File: " + LocalConfig.PUSH_MT_STOP_PATH + " || So Thread Stop: " + mListPushThreadStop.size());

		try
		{
			Vector<Object> mList = new Vector<Object>();
			for (PushThreadObject mObject : mListPushThreadStop)
			{
				mList.add((Object) mObject);
			}
			MyFile.SaveObjectToFile(LocalConfig.PUSH_MT_STOP_PATH, mList);
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	public void WindowClosing()
	{
		ProcessData = false;
		StopPushMT = true;

		System.out.print("\nWaiting .....");
		mLog.log.info("\nWaiting .....");

		try
		{
			// Nếu còn thread nào đang chạy thì phải chờ cho kết thúc rồi mới
			// tắt chương trình
			Integer DelayCount = 0;
			while (MyThread.ActiveThreasID.size() > 0 && DelayCount++ < LocalConfig.MaxCountClose)
			{
				mLog.log.debug("So luong thread dang chay la:" + MyThread.ActiveThreasID.size() + ", nen se cho them 1 giay. || DelayCount:" + DelayCount);
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException ex)
		{
			System.out.println(ex.toString());
		}

		mLog.log.info("Save PushMT Fail xuong file: " + LocalConfig.PUSH_MT_FAIL_PATH);

		SavePushMTFail();
		SaveThreadStop();

		MTrafficConfig.ShutdownProxool();

		try
		{
			MTrafficConfig.CloseAllConnection();
			// Thread.sleep(1000);
		}
		catch (Exception ex)
		{

		}
		mLog.log.info("Shutdown");

		System.out.print("\nExit");
	}
}
