package my.define;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import MyUtility.MyConfig;

public class MTObject implements Serializable
{
	public enum Status
	{
		NOTHING(0),
		/**
		 * Load lên MTQueue
		 */
		InMTQueue(1),
		/**
		 * Đang gửi MT sang VMS
		 */
		SendMT(2),
		/**
		 * Đang lưu MT xuống table Log trong DB
		 */
		SaveMTLog(3),
		/**
		 * Đang lưu vào CDR queue
		 */
		SaveCDR(4),

		/**
		 * Hoàn thành
		 */
		Complete(5);

		private final int value;

		private Status(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

	/**
	 * Kết quả gửi MT đến khách hàng
	 * 
	 * @author Administrator
	 * 
	 */
	public enum MTResult
	{
		NOTHING(0),
		/**
		 * Thành công
		 */
		Success(1), ;
		private final int value;

		private MTResult(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}
	public Long ID = 0l;
	public String USER_ID;
	public String SERVICE_ID;
	public String MOBILE_OPERATOR;
	public String COMMAND_CODE;
	public Integer CONTENT_TYPE = 0;
	public String INFO;
	public Timestamp SUBMIT_DATE = null;
	public Timestamp DONE_DATE = null;
	public Integer PROCESS_RESULT = 0;
	public Integer MESSAGE_TYPE = 0;
	public String REQUEST_ID = "";
	public String MESSAGE_ID = "";
	public Integer TOTAL_SEGMENTS = 0;
	public Integer RETRIES_NUM = 0;
	public Timestamp INSERT_DATE = null;
	public Integer CPID = 0;
	public String VMS_MT_ID = "";
	
	public String VMS_SVID="";
	/**
	 * ID của MTResult được nhận từ VMS
	 */
	public Integer SEND_RESULT = 0;
	

	public Status CurrentStatus = Status.NOTHING;

	public MTObject(long ID, String USER_ID, String SERVICE_ID, String MOBILE_OPERATOR, String COMMAND_CODE,
			Integer CONTENT_TYPE, String INFO, Timestamp SUBMIT_DATE, Timestamp DONE_DATE, Integer PROCESS_RESULT,
			Integer MESSAGE_TYPE, String REQUEST_ID, String MESSAGE_ID, Integer TOTAL_SEGMENTS, Integer RETRIES_NUM,
			Timestamp INSERT_DATE, Integer CPID, String VMS_SVID)
	{
		this.ID = ID;
		this.USER_ID = USER_ID;
		this.SERVICE_ID = SERVICE_ID;
		this.MOBILE_OPERATOR = MOBILE_OPERATOR;
		this.COMMAND_CODE = COMMAND_CODE;
		this.CONTENT_TYPE = CONTENT_TYPE;
		this.INFO = INFO;
		this.SUBMIT_DATE = SUBMIT_DATE;
		this.DONE_DATE = DONE_DATE;
		this.PROCESS_RESULT = PROCESS_RESULT;
		this.MESSAGE_TYPE = MESSAGE_TYPE;
		this.REQUEST_ID = REQUEST_ID;
		this.MESSAGE_ID = MESSAGE_ID;
		this.TOTAL_SEGMENTS = TOTAL_SEGMENTS;
		this.RETRIES_NUM = RETRIES_NUM;
		this.INSERT_DATE = INSERT_DATE;
		this.CPID = CPID;
		this.VMS_SVID = VMS_SVID;
	}

	public String GetShortLog()
	{
		try
		{
			String LogFormat = "USER_ID:%s||SERVICE_ID:%s||MOBILE_OPERATOR:%s||COMMAND_CODE:%s||CONTENT_TYPE:%s||INFO:%s||SUBMIT_DATE:%s||REQUEST_ID:%s||RETRIES_NUM:%s||Status:%s||VMS_MT_ID:%s||VMS_SVID:%s";
			String[] Arr =
			{USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE.toString(), INFO,
					MyConfig.Get_DateFormat_LongFormat().format(new Date(SUBMIT_DATE.getTime())), REQUEST_ID, RETRIES_NUM.toString(),
					CurrentStatus.toString(), VMS_MT_ID,VMS_SVID};

			return String.format(LogFormat, (Object[]) Arr);
		}
		catch (Exception ex)
		{
			return "Loi khi GetLog";
		}
	}
	public String GetLog()
	{
		try
		{
			String LogFormat = "ID:%s||USER_ID:%s||SERVICE_ID:%s||MOBILE_OPERATOR:%s||COMMAND_CODE:%s||CONTENT_TYPE:%s||INFO:%s||SUBMIT_DATE:%s||DONE_DATE:%s||PROCESS_RESULT:%s||MESSAGE_TYPE:%s||REQUEST_ID:%s||MESSAGE_ID:%s||TOTAL_SEGMENTS:%s||RETRIES_NUM:%s||INSERT_DATE:%s||CPID:%s||Status:%s||VMS_MT_ID:%s||VMS_SVID:%s";
			String[] Arr =
			{ID.toString(), USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE.toString(), INFO,
					MyConfig.Get_DateFormat_LongFormat().format(new Date(SUBMIT_DATE.getTime())),
					MyConfig.Get_DateFormat_LongFormat().format(new Date(DONE_DATE.getTime())), PROCESS_RESULT.toString(),
					MESSAGE_TYPE.toString(), REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS.toString(), RETRIES_NUM.toString(),
					MyConfig.Get_DateFormat_LongFormat().format(INSERT_DATE), CPID.toString(),
					CurrentStatus.toString(), VMS_MT_ID,VMS_SVID};

			return String.format(LogFormat, (Object[]) Arr);
		}
		catch (Exception ex)
		{
			return "Loi khi GetLog";
		}
	}

}
