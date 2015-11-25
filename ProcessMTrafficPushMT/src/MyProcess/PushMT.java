package MyProcess;

import java.sql.Timestamp;
import java.util.Calendar;

import MyDataSource.MyDataRow;
import MyDataSource.MyTableModel;
import MyDefine.MTObject;
import MyDefine.PushThreadObject;
import MyDefine.PushThreadObject.PushType;
import MyProcessServer.Common;
import MyProcessServer.LocalConfig;
import MyProcessServer.MainProcess;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyConvert;
import MyUtility.MyLogger;
import MyUtility.MyThread;
import Service.ActionLog;
import Service.PushTimeLog;
import Sub.*;

/**
 * Thread sẽ bắn tin cho từng dịch vụ
 * 
 * @author Administrator
 * 
 */
public class PushMT extends MyThread
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	public PushThreadObject mPTObject = new PushThreadObject();

	public PushMT()
	{

	}

	public PushMT(PushThreadObject mPTObject)
	{
		this.mPTObject = mPTObject;
	}

	public void doRun()
	{
		if (MainProcess.ProcessData)
		{
			try
			{
				if (!mPTObject.mServiceObject.IsNull() && !mPTObject.mNewsObject.IsNull())
				{
					PushForEach();
				}

			}
			catch (Exception ex)
			{
				mPTObject.mPushType = PushType.Error;

				mLog.log.debug("Loi xay ra trong qua trinh Push Tin, Thead Index:" + mPTObject.ProcessIndex + "|Service:"
						+ mPTObject.mServiceObject.ServiceName + "|NewsID:" + mPTObject.mNewsObject.NewsID
						+ " Thread nay sẽ được lưu xuống file .dat và sẽ được chạy lại");

				mPTObject.QueueDate = Calendar.getInstance().getTime();
				// Nếu lỗi thì add vào Thread bị stop để push lại sau
				Common.AddToThreadStop(mPTObject);

				mLog.log.error(ex);
			}
		}
	}

	private boolean SavePushTimeLog()
	{
		try
		{
			MyTableModel mTable = MainProcess.mTemplate.mPushTimeLog.clone();
			mTable.Clear();
			MyDataRow mRow = mTable.CreateNewRow();

			mRow.SetValueCell("ProcessIndex", mPTObject.ProcessIndex);
			mRow.SetValueCell("ProcessNumber", mPTObject.ProcessNumber);
			mRow.SetValueCell("ServiceID", mPTObject.mServiceObject.ServiceID);
			mRow.SetValueCell("PushTime", MyConfig.Get_DateFormat_InsertDB().format(mPTObject.mServiceObject.CurrentPushTime));
			mRow.SetValueCell("NewsID", mPTObject.mNewsObject.NewsID);
			mRow.SetValueCell("CreateDate", MyConfig.Get_DateFormat_InsertDB().format(mPTObject.StartDate));
			mRow.SetValueCell("FinishDate", MyConfig.Get_DateFormat_InsertDB().format(mPTObject.FinishDate));
			mRow.SetValueCell("SuccessNumber", mPTObject.SuccessNumber);
			mRow.SetValueCell("FailNumber", mPTObject.FailNumber);
			mRow.SetValueCell("ActionTypeID", mPTObject.mActionLogType.GetValue());
			mRow.SetValueCell("ActionTypeName", mPTObject.mActionLogType.toString());

			mTable.AddNewRow(mRow);

			PushTimeLog mPushTimeLog = new PushTimeLog(LocalConfig.PoolName_Data_SQL);
			return mPushTimeLog.Insert(0, mTable.GetXML());
		}
		catch (Exception ex)
		{
			mLog.log.error("Luu PushTimeLog bi loi ex:" + ex.getMessage());
			return false;
		}
	}

	private boolean PushForEach() throws Exception
	{
		try
		{
			Integer MTCount = mPTObject.mNewsObject.MTCount();

			ActionLog mAcLog = new ActionLog(LocalConfig.PoolName_Data_SQL);
			// MyTableModel mTable_ActionLog =
			// MainProcess.mTemplate.mActionLog.clone();
			MyTableModel mTable_ActionLog = mAcLog.Select(0);

			Integer MinPID = LocalConfig.PUSH_MT_PID_MIN;

			Integer CountLoop = 0;

			if (mPTObject.CurrentPID > 0)
				MinPID = mPTObject.CurrentPID;

			if (mPTObject.mServiceObject.ServiceID == 2)
			{
				CountLoop = 0;
			}
			for (Integer PID = MinPID; PID <= LocalConfig.PUSH_MT_PID_MAX; PID++)
			{
				mPTObject.CurrentPID = PID;

				if (CountLoop++ > 0) // phuc vu cho retry push thread stop
				{
					// Reset lại OrderID cho mỗi PID, nếu không sẽ bị sót. vì
					// PID ko
					// liên quan gì đến tính tăng dần của OrderID
					mPTObject.MaxOrderID = 0;
				}

				MyTableModel mTable = GetSubscriber(PID);

				while (!mTable.IsEmpty())
				{
					mTable_ActionLog.Clear();

					for (Integer i = 0; i < mTable.GetRowCount(); i++)
					{
						// nếu bị dừng đột ngột
						if (MainProcess.StopPushMT)
						{
							mLog.log.debug("Bi dung push tin cho dich vu ServiceID:" + mPTObject.mServiceObject.ServiceID + ", ung voi tin NewsID:"
									+ mPTObject.mNewsObject.NewsID);

							mPTObject.mPushType = PushType.Stop;
							mPTObject.QueueDate = Calendar.getInstance().getTime();

							// Insert vào log
							InsertActionLog(mTable_ActionLog);

							// Update số lượng MT count
							UpdateMTCount_DB(mTable);

							// Nếu khi trương trình stop thì dừng push MT. và
							// lưu thông tin này vào list để lưu ra file
							Common.AddToThreadStop(mPTObject);
							return false;
						}

						mPTObject.MaxOrderID = Integer.parseInt(mTable.GetValueAt(i, "OrderID").toString());

						String MSISDN = mTable.GetValueAt(i, "MSISDN").toString();

						if (!MSISDN.startsWith("84"))
							MSISDN = MyCheck.ValidPhoneNumber(MSISDN, "84");

						mPTObject.MSISDN = MSISDN;

						MTObject mMTObject = new MTObject();
						mMTObject.MSISDN = MSISDN;
						mMTObject.mNewsObject = mPTObject.mNewsObject;
						mMTObject.mServiceObject = mPTObject.mServiceObject;
						mMTObject.ProcessIndex = mPTObject.ProcessIndex;
						mMTObject.OrderID = mPTObject.MaxOrderID;

						Integer TotalMTByDay = 0;
						if (mTable.GetValueAt(i, "TotalMTByDay") != null)
						{
							TotalMTByDay = Integer.parseInt(mTable.GetValueAt(i, "TotalMTByDay").toString());
						}
						Timestamp LastUpdate = null;

						if (mTable.GetValueAt(i, "LastUpdate") != null)
							LastUpdate = (Timestamp) mTable.GetValueAt(i, "LastUpdate");

						if (!CheckMTCount(LastUpdate, TotalMTByDay, MTCount))
						{
							mLog.log.debug("So luong MT trong ngay cua thue bao (" + MSISDN + ") la (" + TotalMTByDay
									+ ") nen ko the push tiep ban tin co so MT (" + MTCount + ")");
							continue;
						}

						if (Common.SendMT(mMTObject, mPTObject.mActionLogType))
						{
							// Tăng số MT bắn thành công
							mPTObject.SuccessNumber++;

							MyDataRow mRow_Action = mTable_ActionLog.CreateNewRow();
							mRow_Action.SetValueCell("ServiceID", mMTObject.mServiceObject.ServiceID);
							mRow_Action.SetValueCell("MSISDN", mMTObject.MSISDN);
							mRow_Action.SetValueCell("LogDate", MyConfig.Get_DateFormat_InsertDB().format(Calendar.getInstance().getTime()));
							mRow_Action.SetValueCell("ActionTypeID", mPTObject.mActionLogType.GetValue());
							mRow_Action.SetValueCell("ActionTypeName", mPTObject.mActionLogType.toString());
							mRow_Action.SetValueCell("LogContent", "NewsID:" + mPTObject.mNewsObject.NewsID);
							mRow_Action.SetValueCell("MO", "");
							mRow_Action.SetValueCell("MT", mPTObject.mNewsObject.Content);

							mRow_Action.SetValueCell("LogPID", mTable.GetValueAt(i, "PID"));

							mTable_ActionLog.AddNewRow(mRow_Action);

							UpdateMTCount_Table(mTable, i);
							continue;
						}
						else
						{
							// Tăng số MT bắn không thành công
							mPTObject.FailNumber++;

							mLog.log.debug("Push tin khong thanh cong va add to MTFail cho MSISDN:" + MSISDN + " | ServiceID:"
									+ mPTObject.mServiceObject.ServiceID + " | NewsID:" + mPTObject.mNewsObject.NewsID);
							// Ghi lại các trường hợp chưa bắn được MT
							// để sau này push lại
							mMTObject.QueueDate = Calendar.getInstance().getTime();

							// Nếu list đang được xử lý ở một thead khác, thì
							// phài chờ đến khi thead đó xử lý xong
							Common.AddToListMTFail(mMTObject);
						}
					}

					// Insert vào log
					InsertActionLog(mTable_ActionLog);

					// Update số lượng MT count
					UpdateMTCount_DB(mTable);

					mTable.Clear();
					mTable = GetSubscriber(PID);
				}
			}
			mPTObject.mPushType = PushType.Complete;
			return true;
		}
		catch (Exception ex)
		{
			mLog.log.debug("Loi trong ban tin cho dich vu:" + mPTObject.mServiceObject.ServiceName + " || va ban tin co NewsID:" + mPTObject.mNewsObject.NewsID);
			throw ex;
		}
		finally
		{
			// Cập nhật thời gian kết thúc bắn tin
			mPTObject.FinishDate = Calendar.getInstance().getTime();

			// Lưu thông tin Pushtim và table PushTimeLog
			SavePushTimeLog();
			mLog.log.debug("KET THUC ban tin cho dich vu:" + mPTObject.mServiceObject.ServiceName + " || va ban tin co NewsID:" + mPTObject.mNewsObject.NewsID);
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

	/**
	 * Kiểm tra xem số lượng MT đã vượt qua số lượng cho phép hay chưa
	 * 
	 * @param LastUpdate
	 * @param TotalMTByDay
	 * @param MTCount
	 * @return
	 * @throws Exception
	 */
	private boolean CheckMTCount(Timestamp LastUpdate, Integer TotalMTByDay, Integer MTCount) throws Exception
	{
		try
		{
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

			if (TotalMTByDay > mPTObject.mServiceObject.MTNumber)
				return false;
			else
				return true;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	private boolean UpdateMTCount_Table(MyTableModel mTable, Integer RowIndex) throws Exception
	{
		try
		{
			Integer MTCount = mPTObject.mNewsObject.MTCount();

			if (MTCount < 1)
				return true;

			Integer TotalMT = 0;
			Integer TotalMTByDay = 0;
			Calendar mCal_Current = Calendar.getInstance();
			Calendar mCal_LastUpdate = Calendar.getInstance();

			boolean LastDateIsNull = true;
			if (mTable.GetValueAt(RowIndex, "LastUpdate") != null)
			{
				LastDateIsNull = false;
				mCal_LastUpdate.setTime((Timestamp) mTable.GetValueAt(RowIndex, "LastUpdate"));
			}

			if (LastDateIsNull)
			{
				TotalMTByDay = MTCount;
			}
			else if (mCal_LastUpdate.get(Calendar.DAY_OF_MONTH) == mCal_Current.get(Calendar.DAY_OF_MONTH)
					&& mCal_LastUpdate.get(Calendar.MONTH) == mCal_Current.get(Calendar.MONTH)
					&& mCal_LastUpdate.get(Calendar.YEAR) == mCal_Current.get(Calendar.YEAR))
			{
				// Nếu trong hôm nay đã được cập nhật rồi thì cộng dồn
				if (mTable.GetValueAt(RowIndex, "TotalMTByDay") != null)
					TotalMTByDay = Integer.parseInt(mTable.GetValueAt(RowIndex, "TotalMTByDay").toString()) + MTCount;
				else
					TotalMTByDay = MTCount;
			}
			else
			{
				// nếu ngày cấp nhật không phải hôm nay, thì tạo mới với giá
				// trị = MTCount
				TotalMTByDay = MTCount;
			}

			// Cập nhật thời gian
			mTable.SetValueAt(MyConfig.Get_DateFormat_InsertDB().format(mCal_Current.getTime()), RowIndex, "LastUpdate");

			if (mTable.GetValueAt(RowIndex, "TotalMT") != null)
				TotalMT = Integer.parseInt(mTable.GetValueAt(RowIndex, "TotalMT").toString()) + MTCount;

			mTable.SetValueAt(TotalMT, RowIndex, "TotalMT");
			mTable.SetValueAt(TotalMTByDay, RowIndex, "TotalMTByDay");

			return true;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	private boolean UpdateMTCount_DB(MyTableModel mTable) throws Exception
	{
		try
		{
			Subscriber mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);
			return mSub.Update(1, mTable.GetXML());
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Lấy dữ liệu từ database
	 * 
	 * @return
	 * @throws Exception
	 */
	public MyTableModel GetSubscriber(Integer PID) throws Exception
	{
		try
		{
			Subscriber mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);
			// Lấy danh sách (Para_1 = RowCount, Para_2 = PID, Para_3 =
			// ServiceID,
			// Para_4 = OrderID, Para_5 = ProcessNumber, Para_6 = ProcessIndex)
			return mSub.Select(4, mPTObject.RowCount.toString(), PID.toString(), mPTObject.mServiceObject.ServiceID.toString(),
					mPTObject.MaxOrderID.toString(), mPTObject.ProcessNumber.toString(), mPTObject.ProcessIndex.toString(), Sub.Subscriber.Status.Active.GetValue()
							.toString());
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

}
