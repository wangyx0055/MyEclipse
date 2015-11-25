package MyProcess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import MyProcessServer.ContentAbstract;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MyStore.TextBase;
import MyUtility.MyLogger;
import MyUtility.MyText;

public class TextBaseProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	TextBase mTextBase;

	public TextBaseProcess()
	{
		try
		{
			mTextBase = new TextBase("HBStore");
		}
		catch (Exception ex)
		{
			mLog.log.error("Ringback_Error", ex);
		}
	}

	Collection<MsgObject> MessOject = new ArrayList<MsgObject>();

	@Override
	protected Collection<MsgObject> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{

		String MTContent = "Tin nhan sai cu phap";
		try
		{
			String Info = msgObject.getUsertext().trim();
			String TextBaseID = Info.substring(msgObject.getKeyword().length(), Info.length()).trim();

			String TextBaseName = "";

			TextBaseID = MyText.RemoveSpecialLetter(1, TextBaseID);
			if (TextBaseID == "")
			{
				MTContent = "Ban tin dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Ban tin khac.";
				return AddToQueue(msgObject, MTContent, "");
			}

			MyDataSource.MyTableModel mTable = mTextBase.Select(1, TextBaseID);

			// Nếu bài hát không tồn tại trong hệ thống
			if (mTable.GetRowCount() < 1)
			{
				MTContent = "Ban tin dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Ban tin khac.";
				return AddToQueue(msgObject, MTContent, "");
			}

			TextBaseName = mTable.GetValueAt(0, "STKTextName").toString();
			TextBaseName = MyText.RemoveSignVietnameseString(TextBaseName);
			MTContent = mTable.GetValueAt(0, "Content").toString();
			MTContent = MyText.RemoveSignVietnameseString(MTContent);			
			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);

		}
		catch (Exception ex)
		{
			mLog.log.error("Error", ex);
			MTContent = "Ban tin dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Ban tin khac.";
			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);
		}

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("TextBase-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

		return MessOject;

	}

	private Collection<MsgObject> AddToQueue(MsgObject msgObject, String MTContent, String MTPromo)
	{
		msgObject.setUsertext(MTContent + MTPromo);
		msgObject.setContenttype(21);
		msgObject.setMsgtype(1);

		MessOject.add(new MsgObject(msgObject));

		return MessOject;
	}

}
