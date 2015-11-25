package MyProcess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.math.BigDecimal;
import java.sql.*;

import MyGateway.sms_receive_queue_inv;
import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyConfig.Telco;
import MyUtility.MyLogger;

/**
 * 
 * @author Administrator
 * 
 */
public class InvalidProcess extends ContentAbstract
{
	static MyLogger mLog = new MyLogger(InvalidProcess.class.toString());

	MsgObject mObject = null;
	Collection<MsgObject> ListMessOject = new ArrayList<MsgObject>();
	String MSISDN = "";
	MyConfig.Telco mTelco = Telco.NOTHING;
	String MTContent = "";
	String Info = "";
	String Keyword = "";

	@Override
	protected Collection<MsgObject> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{
		mObject = msgObject;
		MTContent = LocalConfig.INV_INFO;
		String IS_PUSH_MT = LocalConfig.IS_PUSH_MT;

		// nếu là 1 thì không insert vào table invalid và trả mt về cho khách
		// hàng
		if (IS_PUSH_MT.equalsIgnoreCase("1"))
		{
			String MSISDN = msgObject.getUserid();
			MyConfig.Telco mTelco = Telco.NOTHING;
			mTelco = MyCheck.GetTelco(MSISDN);

			if (mTelco == Telco.VIETTEL)
			{
				// theo chinh sach của Viettel thì các MO Invalid không được
				// tính đối soát
				msgObject.setMsgtype(2);
			}
			else  if(mTelco == Telco.GPC)
			{
				msgObject.setMsgtype(1);//voi VNP thi van tinh tien
			}
			else  if(mTelco == Telco.VMS)
			{
				msgObject.setMsgtype(2);
			}
			else
			{
				msgObject.setMsgtype(1);
			}

			mObject.setUsertext(MTContent);
			mObject.setContenttype(21);

			ListMessOject.add(new MsgObject(mObject));

			return ListMessOject;
		}
		else
		{
			addtoINV(msgObject);
		}

		return null;

	}

	String getString(HashMap<?, ?> _option1, String field, String defaultvalue)
	{
		try
		{
			String temp = ((String) _option1.get(field));
			if ((temp == null) || "".equalsIgnoreCase(temp))
			{
				return defaultvalue;
			}
			return temp;
		}
		catch (Exception e)
		{
			return defaultvalue;
		}
	}

	private static BigDecimal addtoINV(MsgObject msgObject)
	{
		mLog.log.info("sms_receive_queue_inv:" + msgObject.getUserid() + "@" + msgObject.getUsertext());
		try
		{
			sms_receive_queue_inv mQueueInvalid = new sms_receive_queue_inv(LocalConfig.PoolName_Gateway);

			boolean Result = mQueueInvalid.Insert(msgObject.getRequestid().toString(), msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getMobileoperator(), msgObject.getKeyword(), msgObject.getUsertext(), msgObject.getTTimes());
			if (!Result)
			{
				mLog.log.info("add2moinv:" + msgObject.getUserid() + ":" + msgObject.getUsertext() + ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			return msgObject.getRequestid();
		}
		catch (SQLException e)
		{
			mLog.log.error("add2ReceiveLog:" + msgObject.getUserid() + ":" + msgObject.getUsertext() + ":Error add row from moinv:" + e.toString(), e);

			return new BigDecimal(-1);
		}
		catch (Exception e)
		{
			mLog.log.error("add2ReceiveLog:" + msgObject.getUserid() + ":" + msgObject.getUsertext() + ":Error add row from moinv:" + e.toString(), e);
			return new BigDecimal(-1);
		}

	}

}
