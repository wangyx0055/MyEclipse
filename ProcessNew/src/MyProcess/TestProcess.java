package MyProcess;

import java.util.ArrayList;
import java.util.Collection;

import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.MsgObject;
import MyUtility.MyLogger;

public class TestProcess extends ContentAbstract
{
	Collection<MsgObject> MessOject = new ArrayList<MsgObject>();

	protected Collection<MsgObject> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{
		MyLogger mLog = new MyLogger(this.getClass().toString());

		String MTContent = "TestProcess";
		try
		{
			MTContent = "TestProcess";
		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}

		msgObject.setUsertext(MTContent);
		msgObject.setContenttype(21);
		msgObject.setMsgtype(1);

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("TESTProcess-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

		return MessOject;

	}
}
