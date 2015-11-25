package MyProcess;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import MyGateway.sms_receive_queue_inv;
import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyLogger;
import MyUtility.MyConfig.Telco;

public class SportMillionProcess extends ContentAbstract
{
	static MyLogger mLog = new MyLogger(InvalidProcess.class.toString());

	MsgObject mObject = null;
	Collection<MsgObject> ListMessOject = new ArrayList<MsgObject>();
	String MSISDN = "";
	MyConfig.Telco mTelco = Telco.NOTHING;
	String MTContent = "Cam on ban da tham gia chuong trinh Vui giang sinh, rinh qua khung cua Trieu phu The thao. Ban se duoc tham gia quay thuong trung iPhone 5s cung nhieu giai thuong co gia tri. Chuc ban may man.";
	String Info = "";
	String Keyword = "";

	@Override
	protected Collection<MsgObject> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{
		mObject = msgObject;
		// nếu là 1 thì không insert vào table invalid và trả mt về cho khách
		// hàng
		
		msgObject.setMsgtype(1);
		mObject.setUsertext(MTContent);
		mObject.setContenttype(21);

		ListMessOject.add(new MsgObject(mObject));

		return ListMessOject;

	}
}
