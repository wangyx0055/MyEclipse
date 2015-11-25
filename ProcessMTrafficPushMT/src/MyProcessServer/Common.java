package MyProcessServer;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyDefine.MTObject;
import MyDefine.PushThreadObject;
import MyDefine.PushThreadObject.PushType;

import MyProcess.PushMT;
import MySQL.ems_send_queue;
import MyUtility.MyConfig;

import MyUtility.MyConvert;
import MyUtility.MyLogger;
import MyUtility.MySeccurity;
import Service.ActionLog;
import Service.ActionLogObject;
import Service.ServiceObject;

import Service.NewsStreet;
import Service.News.NewsType;
import Service.News.Status;
import Service.NewsObject;
import Sub.Subscriber;

public class Common
{
	static MyLogger mLog = new MyLogger(Common.class.toString());

	/**
	 * Insert Log xuống database
	 * 
	 * @param mObject
	 * @return
	 * @throws Exception
	 */
	public static boolean Insert_ActionLog(ActionLogObject mObject) throws Exception
	{
		try
		{
			ActionLog mActionLog = new ActionLog(LocalConfig.PoolName_Data_SQL);
			MyTableModel mTable_ActionLog = MainProcess.mTemplate.mActionLog;
			mTable_ActionLog.Clear();
			MyDataRow mRow_ActionLog = mTable_ActionLog.CreateNewRow();

			mRow_ActionLog.SetValueCell("ServiceID", mObject.ServiceID);
			mRow_ActionLog.SetValueCell("MSISDN", mObject.MSISDN);

			mRow_ActionLog.SetValueCell("LogDate", MyConfig.Get_DateFormat_InsertDB().format(mObject.LogDate));
			mRow_ActionLog.SetValueCell("ActionTypeID", mObject.mActionType.GetValue());
			mRow_ActionLog.SetValueCell("ActionTypeName", mObject.mActionType.toString());

			mRow_ActionLog.SetValueCell("LogContent", mObject.LogContent);
			mRow_ActionLog.SetValueCell("MO", mObject.MO);
			mRow_ActionLog.SetValueCell("MT", mObject.MT);
			mRow_ActionLog.SetValueCell("RequestID", mObject.RequestID);
			mRow_ActionLog.SetValueCell("LogPID", mObject.LogPID);

			mTable_ActionLog.AddNewRow(mRow_ActionLog);
			return mActionLog.Insert(0, mTable_ActionLog.GetXML());
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
			throw ex;
		}
	}

	/**
	 * Lấy danh sách các tin tức chưa push theo dịch vụ
	 * 
	 * @param ServiceID
	 * @param BeginTime
	 * @param EndTime
	 * @return
	 * @throws Exception
	 */
	public static Vector<NewsObject> GetNews(MyTableModel mTable) throws Exception
	{
		try
		{
			Vector<NewsObject> mList = new Vector<NewsObject>();

			NewsStreet mNewsStreet = new NewsStreet(LocalConfig.PoolName_Data_SQL);

			for (int i = 0; i < mTable.GetRowCount(); i++)
			{
				NewsObject mObject = new NewsObject();
				mObject.NewsID = Integer.parseInt(mTable.GetValueAt(i, "NewsID").toString());
				mObject.ServiceID = Integer.parseInt(mTable.GetValueAt(i, "ServiceID").toString());
				mObject.Content = mTable.GetValueAt(i, "Content").toString();
				if (mTable.GetValueAt(i, "CharCount") != null)
					mObject.CharCount = Integer.parseInt(mTable.GetValueAt(i, "CharCount").toString());

				if (mTable.GetValueAt(i, "StatusID") != null)
					mObject.mStatus = Status.FromInt(Integer.parseInt(mTable.GetValueAt(i, "StatusID").toString()));

				if (mTable.GetValueAt(i, "NewsTypeID") != null)
					mObject.mNewsType = NewsType.FromInt(Integer.parseInt(mTable.GetValueAt(i, "NewsTypeID").toString()));

				// Nếu là dịch vụ theo tên đường, thì lấy thêm tên đường cho tin
				// tức
				if (mObject.ServiceID == LocalConfig.StreetServiceID)
				{
					MyTableModel mTable_Street = mNewsStreet.Select(2, mObject.NewsID.toString());
					for (int j = 0; j < mTable_Street.GetRowCount(); i++)
					{
						mObject.ListStreetID.add(Integer.parseInt(mTable_Street.GetValueAt(j, "StreetID").toString()));
					}
				}
				if (mTable.GetValueAt(i, "PushTime") != null)
					mObject.PushTime = (Timestamp) mTable.GetValueAt(i, "PushTime");

				mList.add(mObject);
			}
			return mList;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public static void AddToThreadStop(PushThreadObject mPTObject)
	{
		try
		{
			if (MainProcess.ListPushThreadStopIsUsing)
			{
				synchronized (MainProcess.mListPushThreadStop)
				{
					MainProcess.mListPushThreadStop.wait();
					MainProcess.ListPushThreadStopIsUsing = true;

					for (PushThreadObject mObject : MainProcess.mListPushThreadStop)
					{
						if (mObject.mServiceObject.ServiceID.equals(mPTObject.mServiceObject.ServiceID)
								&& mObject.mNewsObject.NewsID.equals(mPTObject.mNewsObject.NewsID) && mObject.ProcessIndex.equals(mPTObject.ProcessIndex)
								&& mObject.ProcessNumber.equals(mPTObject.ProcessNumber))
							return;
					}
					MainProcess.mListPushThreadStop.addElement(mPTObject);

					MainProcess.mListPushThreadStop.notify();
					MainProcess.ListPushThreadStopIsUsing = false;
				}
			}
			else
			{
				synchronized (MainProcess.mListPushThreadStop)
				{
					MainProcess.ListPushThreadStopIsUsing = true;
					for (PushThreadObject mObject : MainProcess.mListPushThreadStop)
					{
						if (mObject.mServiceObject.ServiceID.equals(mPTObject.mServiceObject.ServiceID)
								&& mObject.mNewsObject.NewsID.equals(mPTObject.mNewsObject.NewsID) && mObject.ProcessIndex.equals(mPTObject.ProcessIndex)
								&& mObject.ProcessNumber.equals(mPTObject.ProcessNumber))
							return;
					}

					MainProcess.mListPushThreadStop.addElement(mPTObject);
					MainProcess.mListPushThreadStop.notify();
					MainProcess.ListPushThreadStopIsUsing = false;
				}
			}
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
	}

	/**
	 * Bắn MT xuống cho khách hàng
	 * 
	 * @param SPID
	 * @param ServiceID
	 * @param Address
	 * @param Message
	 * @return
	 * @throws Exception
	 */
	public static boolean SendMT(MTObject mMTObject, ActionLog.ActionLogType mActionLogType) throws Exception
	{
		try
		{
			ems_send_queue mSendQueue = new ems_send_queue(LocalConfig.PoolName_Data_MySQL);
			
			String USER_ID = mMTObject.MSISDN;
			String SERVICE_ID = LocalConfig.SHORT_CODE;
			
			String COMMAND_CODE = mMTObject.mServiceObject.RegKeyword;
			
			String INFO = mMTObject.mNewsObject.Content;
			
			String REQUEST_ID = Long.toString(System.currentTimeMillis());
			
			return mSendQueue.Insert(USER_ID, SERVICE_ID, COMMAND_CODE, INFO, REQUEST_ID);
			
		}
		catch(Exception ex)
		{
			mLog.log.error(ex);
			return false;
		}
	}
	
	
	/**
	 * Kiểm tra xem dịch vụ đang push tin hay không Return = true: đang push tin
	 * Return = false: không push tin
	 * 
	 * @param mServiceObject
	 * @param mNewsObject
	 * @return
	 * @throws Exception
	 */
	public static boolean CheckPushingMT(ServiceObject mServiceObject, PushThreadObject.PushType mPushType) throws Exception
	{
		try
		{
			if (mPushType == PushType.Pushing)
			{
				for (PushThreadObject mPTObject : MainProcess.mListPushThreadStop)
				{
					if (mPTObject.mServiceObject.ServiceID.equals(mServiceObject.ServiceID))
					{
						return true;
					}
				}
			}
			else if (mPushType == PushType.RetryPushing)
			{
				for (PushThreadObject mPTObject : MainProcess.mListPushThreadStop)
				{
					if (mPTObject.mServiceObject.ServiceID.equals(mServiceObject.ServiceID)
							&& (mPTObject.mPushType == PushType.Pushing || mPTObject.mPushType == PushType.Complete))
					{
						return true;
					}
				}
			}
			else if (mPushType == PushType.RetryPushing_MTFail)
			{
				for (PushThreadObject mPTObject : MainProcess.mListPushThreadStop)
				{
					if (mPTObject.mServiceObject.ServiceID.equals(mServiceObject.ServiceID))
					{
						return true;
					}
				}
			}
			return false;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Lấy kết quả (result) do Vinaphone trả về khi Push MT
	 * 
	 * @param XMLResponse
	 * @return
	 * @throws Exception
	 */
	private static String GetResultPushMT(String XMLResponse) throws Exception
	{
		try
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(XMLResponse));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName(LocalConfig.Result_NodeName_VNP);

			Element line = (Element) nodes.item(0);

			if (line == null)
				return "";

			Node child = line.getFirstChild();
			if (child instanceof CharacterData)
			{
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
			return "";
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Kiểm tra xem số lượng MT đã vượt quá số lượng MT cho phép trong ngày hay
	 * chưa
	 * 
	 * @param MSISDN
	 * @param mServiceObject
	 * @param MTCount
	 * @return
	 * @throws Exception
	 */
	public static boolean CheckMTNumber(String MSISDN, ServiceObject mServiceObject, Integer MTCount) throws Exception
	{
		try
		{
			Integer PID = MyConvert.GetPIDByMSISDN(MSISDN,LocalConfig.MAX_PID);
			Subscriber mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);
			MyTableModel mTable = mSub.Select(2, PID.toString(), MSISDN, mServiceObject.ServiceID.toString());
			if (mTable.GetRowCount() < 1)
				return false;

			Timestamp LastUpdate = null;
			Integer TotalMTByDay = 0;
			if (mTable.GetValueAt(0, "LastUpdate") != null)
			{
				LastUpdate = (Timestamp) mTable.GetValueAt(0, "LastUpdate");
			}
			if (mTable.GetValueAt(0, "TotalMTByDay") != null)
			{
				TotalMTByDay = Integer.parseInt(mTable.GetValueAt(0, "TotalMTByDay").toString());
			}

			Calendar mCal_Current = Calendar.getInstance();
			Calendar mCal_LastUpdate = Calendar.getInstance();

			if (LastUpdate == null)
				return true;

			mCal_LastUpdate.setTime(LastUpdate);

			if (mCal_LastUpdate.get(Calendar.DAY_OF_MONTH) == mCal_Current.get(Calendar.DAY_OF_MONTH)
					&& mCal_LastUpdate.get(Calendar.MONTH) == mCal_Current.get(Calendar.MONTH)
					&& mCal_LastUpdate.get(Calendar.YEAR) == mCal_Current.get(Calendar.YEAR))
			{
				TotalMTByDay += MTCount;
			}
			else
			{
				TotalMTByDay = 0;
			}

			if (TotalMTByDay > mServiceObject.MTNumber)
				return false;
			else
				return true;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Xóa bỏ các thread đã push xong
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void DeleteFisnishThread() throws Exception
	{
		try
		{
			// Xóa bỏ các thread đã push xong
			for (Iterator<PushThreadObject> mItem = MainProcess.mListPushThreadStop.listIterator(); mItem.hasNext();)
			{
				PushThreadObject mPTObject = mItem.next();

				// nếu đã hoàn thành push tin thì xóa ra khỏi danh sách
				if (mPTObject.IsComplete())
				{
					// Xóa bỏ các thread PushMT ra khỏi list các Thread đang
					// chạy
					mLog.log.debug("Xoa bo thread PushMT ra khoi list cac thread dang chay." + "Service:" + mPTObject.mServiceObject.ServiceID + "|"
							+ mPTObject.mServiceObject.ServiceName + "||News:" + mPTObject.mNewsObject.NewsID + "|" + "|| ErrorStatus:"
							+ mPTObject.mPushType.toString());

					mItem.remove();

				}

			}
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public static void AddToListMTFail(MTObject mObject) throws Exception
	{
		try
		{
			if (MainProcess.ListMTFailIsUsing)
			{
				synchronized (MainProcess.mListMTFail)
				{
					MainProcess.mListMTFail.wait();
					MainProcess.ListMTFailIsUsing = true;
					MainProcess.mListMTFail.addElement(mObject);
					MainProcess.mListMTFail.notify();
					MainProcess.ListMTFailIsUsing = false;
				}
			}
			else
			{
				synchronized (MainProcess.mListMTFail)
				{
					MainProcess.ListMTFailIsUsing = true;
					MainProcess.mListMTFail.addElement(mObject);
					MainProcess.mListMTFail.notify();
					MainProcess.ListMTFailIsUsing = false;
				}
			}
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
