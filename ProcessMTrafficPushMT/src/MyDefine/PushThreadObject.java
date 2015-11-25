package MyDefine;

import java.util.Calendar;
import java.util.Date;

import MyUtility.MyConfig;
import Service.ActionLog;

import Service.ActionLog.ActionLogType;
import Service.NewsObject;
import Service.ServiceObject;

/**
 * Class khai báo các thread đang push tin cho từng dịch vụ, ứng với từng tin
 * khác nhau. Thường sẽ được lưu xuống file khi stop chương trình với thông tin
 * (Push tới đâu (PID, OrderID), cho dịch nào, với tin tức nào Khi start lại
 * trường trình sẽ push lại
 * 
 * @author Administrator
 * 
 */
public class PushThreadObject implements java.io.Serializable
{
	/**
	 * Cho biết tình trạng của Thread này
	 * 
	 * @author Administrator
	 * 
	 */
	public enum PushType
	{
		Default(1),

		/**
		 * bị dừng đột ngột do người quản trị
		 */
		Stop(2),
		/**
		 * Bị lỗi trong quá trình bắn tin
		 */
		Error(3),
		/**
		 * Hoàn thành bắn tin
		 */
		Complete(4),
		/**
		 * đang bắn tin
		 */
		Pushing(5),
		/**
		 * Cho biết thread đang bắn tin retry
		 */
		RetryPushing(6),
		/**
		 * Cho biết thread đang push các MT đã push không thành công
		 */
		RetryPushing_MTFail(7), ;

		private int value;

		private PushType(int value)
		{
			this.value = value;
		}

		public int GetValue()
		{
			return this.value;
		}

		public static PushType FromInt(int iValue)
		{
			for (PushType type : PushType.values())
			{
				if (type.GetValue() == iValue)
					return type;
			}
			return Default;
		}

	}

	/**
	 * Cho biết tình trạng của Thread
	 */
	public PushType mPushType = PushType.Default;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Cho biết đang push mt theo khung giờ hay là là push lại (trường hợp đang
	 * push dở thì bị tắt chương trình)
	 */
	public ActionLog.ActionLogType mActionLogType = ActionLogType.Default;

	/**
	 * Cho biết đang push tới PID nào
	 */
	public Integer CurrentPID = 1;

	public String MSISDN = "";

	/**
	 * Số lượng process Push MT được tạo ra
	 */
	public Integer ProcessNumber = 1;

	/**
	 * Thứ tự của 1 process
	 */
	public Integer ProcessIndex = 1;

	/**
	 * Số thứ tự (OrderID) trong table Subscriber, process sẽ lấy những record
	 * có OrderID >= MaxOrderID
	 */
	public Integer MaxOrderID = 0;

	/**
	 * Tổng số record mỗi lần lấy lên để xử lý
	 */
	public Integer RowCount = 10;

	/**
	 * Đối tượng dịch vụ cần push tin
	 */
	public ServiceObject mServiceObject = new ServiceObject();

	/**
	 * Chứa nội dung tin nhắn còn push cho khách hàng
	 */
	public NewsObject mNewsObject = new NewsObject();

	/**
	 * CurrentPushTime + LocalConfig.CHECK_PUSH_TIME_INTERVAL
	 */
	public Date MaxPushTime = null;

	/**
	 * Thời gian bắt đầu chạy thead
	 */
	public Date StartDate = null;

	/**
	 * Thời gian kết thúc chạy thead
	 */
	public Date FinishDate = null;

	/**
	 * Số MT bắn thành công đối với thead này
	 */
	public Integer SuccessNumber = 0;

	/**
	 * Số MT bắn không thành công
	 */
	public Integer FailNumber = 0;

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
			String Fomart = "MSISDN:%s || ProcessIndex:%s || CurrentPID:%s || MaxOrderID:%s || ServiceID:%s || NewsID:%s || PushTime:%s || QueueDate:%s || RetryCount:%s || RetryDate:%s || Suffix:%s";
			return String.format(Fomart, new Object[] { MSISDN, ProcessIndex, CurrentPID, MaxOrderID, mServiceObject.ServiceID, mNewsObject.NewsID,
					MyConfig.Get_DateFormat_InsertDB().format(mServiceObject.CurrentPushTime), MyConfig.Get_DateFormat_InsertDB().format(QueueDate), RetryCount,
					(RetryDate == null ? "null" : MyConfig.Get_DateFormat_InsertDB().format(RetryDate)), Suffix });
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Cho biết Thread này đã push tin hoàn thành hay chưa
	 * 
	 * @return
	 */
	public boolean IsComplete()
	{
		// nếu chưa kết thúc, thì ko được push
		if (this.mPushType != PushType.Complete)
			return false;

		if (this.MaxPushTime == null)
			return true;
		else
		{
			/*
			 * Nếu thời gian hiện tại mà nhỏ hơn Thời gian bắt đầu Push tin +
			 * LocalConfig.CHECK_PUSH_TIME_INTERVAL * 2 Thì không xóa bỏ, nhằm
			 * mục đính để không push tin 2 lần cho 1 khung giờ nhất định. VD:
			 * Ta có: - LocalConfig.CHECK_PUSH_TIME_INTERVAL = 5 -
			 * LocalConfig.CHECK_PUSH_TIME_DELAY = 1 - Thời gian push tin của
			 * dịch vụ: PushTime = 6:30; - Khoảng thời gian kiểm tra sẽ là
			 * [6:25,6:35] nếu 6:25 bắn tin và đến 6:27 bắn tin xong, và 6:28
			 * lại bắt đầu check thời gian bắn tin. lúc này sẽ lại tiếp tục bắn
			 * lại, vì thời gian hiện tại vẫn còn trong khoảng [6:25,6:35] -->
			 * nên sẽ bị lặp tin.
			 */
			if (this.MaxPushTime.before(Calendar.getInstance().getTime()))
				return true;
		}
		return false;
	}
}
