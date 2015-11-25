package MyProcessServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import MyDataSource.MyTableModel;
import MyGateway.ems_send_queue;
import MyUtility.MyCheck;
import MyUtility.MyLogger;
import MySportVote.*;
import MySportVote.DefineMT.MTType;

public class Common
{
	static MyLogger mLog = new MyLogger(Common.class.toString());

	public static String getDBName_SendQueue(String userid)
	{

		int idsendqueue = LocalConfig.getIDdbsendqueue(userid);

		String dbname = LocalConfig.LIST_DB_SENDQUEUE[idsendqueue];

		return dbname;
	}

	private static synchronized Collection<String> splitMsg(String arg)
	{
		String[] result = new String[3];
		Vector<String> v = new Vector<String>();
		int segment = 0;

		if (arg.length() <= 160)
		{
			result[0] = arg;
			v.add(result[0]);
			return v;

		}
		else
		{
			segment = 160;
		}

		StringTokenizer tk = new StringTokenizer(arg, " ");
		String temp = "";
		int j = 0;

		int tksize = tk.countTokens();
		int tkcount = 0;
		while (tk.hasMoreElements())
		{
			String token = (String) tk.nextElement();
			tkcount++;
			if (temp.equals(""))
			{
				temp = temp + token;
			}
			else
			{
				temp = temp + " " + token;
			}

			if (temp.length() > segment)
			{
				temp = token;
				j++;
				if ((tkcount == tksize) && (j <= 3))
				{
					result[j] = token;
				}
			}
			else
			{
				result[j] = temp;
			}

			if (j == 3)
			{
				break;
			}
		}

		for (int i = 0; i < result.length; i++)
		{
			if (result[i] != null)
			{
				v.add(result[i]);
			}
		}

		return v;
	}

	public static int sendMT(MsgObject msgObject)
	{

		if ("".equalsIgnoreCase(msgObject.getUsertext().trim()) || msgObject.getUsertext() == null)
		{
			// Truong hop gui ban tin loi
			mLog.log.info(Common.GetStringLog("SendMT", "MT Is NULL, Lost Message", msgObject));
			return 1;

		}

		if (msgObject.getContenttype() == 0 && msgObject.getUsertext().length() > 160)
		{

			String mtcontent = msgObject.getUsertext();

			Collection<String> listmt = splitMsg(mtcontent);
			Iterator<String> itermt = listmt.iterator();
			int cnttype = msgObject.getContenttype();

			for (int j = 1; itermt.hasNext(); j++)
			{
				String temp = (String) itermt.next();

				msgObject.setUsertext(temp);
				if (j == 1)
				{
					msgObject.setMsgtype(cnttype);
					sendMT1(msgObject);
					try
					{
						Thread.sleep(300);
					}
					catch (InterruptedException e)
					{
						mLog.log.error(e);
					}
				}
				else
				{
					msgObject.setMsgtype(0);
					sendMT1(msgObject);
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						mLog.log.error(e);
					}
				}

			}
			return 1;

		}
		else return sendMT1(msgObject);

	}

	public static int sendMT1(MsgObject msgObject)
	{

		if ("".equalsIgnoreCase(msgObject.getUsertext().trim()) || msgObject.getUsertext() == null)
		{
			// Truong hop gui ban tin loi
			mLog.log.info(Common.GetStringLog("SendMT", "MT Is NULL, Lost Message", msgObject));
			return 1;

		}

		int idsendqueue = LocalConfig.getIDdbsendqueue(msgObject.getUserid());
		mLog.log.info(Common.GetStringLog("SendMT", msgObject));

		try
		{
			ems_send_queue mSendQueue = new ems_send_queue(getDBName_SendQueue(msgObject.getUserid()));

			String idseq = LocalConfig.getTableSendQueue(idsendqueue);

			boolean Result = false;

			Result = mSendQueue.Insert(idseq, msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getMobileoperator(), msgObject.getKeyword(), msgObject.getUsertext(), null, null,
					Integer.toString(msgObject.getMsgtype()), msgObject.getRequestid().toString(), "1",
					Integer.toString(msgObject.getContenttype()), Integer.toString(msgObject.getCpid()));

			if (!Result)
			{
				mLog.log.info(Common.GetStringLog("SendMT Is FAIL", msgObject));
				return -1;
			}
			return 1;
		}
		catch (Exception e)
		{
			mLog.log.error(Common.GetStringLog(msgObject), e);
			return -1;
		}

	}

	/**
	 * Lấy PID theo số điện thoại VD: 097(99)67755 thì (99%20+1) là số được lấy
	 * làm PID
	 * 
	 * @param MSISDN
	 * @return
	 * @throws Exception
	 */
	public static int GetPIDByMSISDN(String MSISDN) throws Exception
	{
		try
		{
			int PID = 1;
			String PID_Temp = "1";

			// hiệu chỉnh số điện thoại thành dạng 9xxx hoặc 1xxx
			String MSISDN_Temp = MyCheck.ValidPhoneNumber(MSISDN, "");

			if (MSISDN_Temp.startsWith("9"))
			{
				PID_Temp = MSISDN_Temp.substring(2, 4);
			}
			else
			{
				// là số điện thoại 11 số
				PID_Temp = MSISDN_Temp.substring(3, 5);
			}

			PID = Integer.parseInt(PID_Temp);

			PID = PID % 20 + 1;

			return PID;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Kiếm tra trong danh sách đăng ký (trong db) có số điện thoại này chưa
	 * 
	 * @param PID
	 * @param MSISDN
	 * @return
	 * @throws Exception
	 */
	public static boolean CheckRegister(int PID, String MSISDN) throws Exception
	{
		try
		{
			Subscriber mSub = new Subscriber(LocalConfig.PoolName_Data_SQL);

			MyTableModel mTable = mSub.Select(2, Integer.toString(PID), MSISDN);
			if (mTable.GetRowCount() > 0) return true;
			else return false;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Lấy MT đã được định nghĩa trong DB, nếu ko có thì lấy MT mặc định
	 * 
	 * @param mMTType
	 * @return
	 * @throws Exception
	 */
	public static String GetDefineMT_Message(MTType mMTType) throws Exception
	{
		try
		{
			return DefineMT.GetMTContent(ConsoleSRV.ListDefineMT, mMTType);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Lấy chuỗi log
	 * 
	 * @param Prefix
	 * @param MO
	 * @param MT
	 * @param Message
	 * @param mObject
	 * @param mServiceObject
	 * @return
	 * @throws Exception
	 */
	public static String GetStringLog(String Prefix, String MO, String Message, MsgObject mObject)
	{
		try
		{
			String FormatLog = Prefix
					+ "-->ServiceID:%s || MSISDN:%s || Keyword:%s || Info:%s || MessageType:%s || ContentType:%s || RequestID:%s || RequestTime:%s || ChannelType:%s || TenDV:%s || MaDV:%s || MO:%s ||  Note:%s";
			String RequestTime = mObject.getTTimes().toString();
			String ServiceName = "";
			String ServiceID = "";

			if (MO == "") MO = mObject.getMO();

			String[] Arr =
			{mObject.getServiceid(), mObject.getUserid(), mObject.getKeyword(), mObject.getUsertext(),
					Integer.toString(mObject.getMsgtype()), Integer.toString(mObject.getContenttype()),
					mObject.getRequestid().toString(), RequestTime, Integer.toString(mObject.getChannelType()),
					ServiceName, ServiceID, MO, Message};

			return String.format(FormatLog, (Object[]) Arr);
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
			return "Loi trong qua trinh ghi log";
		}
	}

	public static String GetStringLog(String Prefix, String Message, MsgObject mObject)
	{
		return GetStringLog(Prefix, "", Message, mObject);
	}

	public static String GetStringLog(String Prefix, MsgObject mObject)
	{
		return GetStringLog(Prefix, "", "", mObject);
	}

	public static String GetStringLog(MsgObject mObject)
	{
		return GetStringLog("", "", "", mObject);
	}

	public static SessionObject GetCurrentSession() throws Exception
	{
		try
		{
			if (ConsoleSRV.ListSessionObject.size() > 0) return ConsoleSRV.ListSessionObject.get(0);
			else return new SessionObject();
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public static String GetMoneyByShortCode(String ServiceID)
	{
		if(ServiceID.equalsIgnoreCase("6083"))
			return "500";
		else if(ServiceID.equalsIgnoreCase("6183"))
			return "1000";
		else if(ServiceID.equalsIgnoreCase("6283"))
			return "2000";
		else if(ServiceID.equalsIgnoreCase("6383"))
			return "3000";
		else if(ServiceID.equalsIgnoreCase("6483"))
			return "500";
		else if(ServiceID.equalsIgnoreCase("6583"))
			return "5000";
		else if(ServiceID.equalsIgnoreCase("6683"))
			return "10000";
		else if(ServiceID.equalsIgnoreCase("6783"))
			return "15000";
		
		return "0";
	}
	
	public static String GetTelcoName(String Operator)
	{
		if(Operator.equalsIgnoreCase("VMS"))
			return "Mobifone";
		if(Operator.equalsIgnoreCase("VIETTEL"))
			return "Viettel";
		if(Operator.equalsIgnoreCase("GPC"))
			return "Vinaphone";
		return "";
	}
	
	/**
	 * Gửi MT lỗi và hoàn tiền cho khách hàng
	 * 
	 * @param mObject
	 * @return
	 */
	public static Collection<MsgObject> GetMTSystemError(MsgObject mObject)
	{
		Collection<MsgObject> ListMessOject = new ArrayList<MsgObject>();
		mObject.setUsertext(LocalConfig.MT_SYSTEM_ERROR);
		mObject.setContenttype(21);
		mObject.setMsgtype(2);

		ListMessOject.add(new MsgObject(mObject));
		return ListMessOject;
	}
}
