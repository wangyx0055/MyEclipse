package MyProcess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import MyUtility.MyLogger;
import MyUtility.MyText;

import MyProcessServer.*;
import MyStore.*;
import WS.GetLinkMedia.GetMedia;

public class RingtoneProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	Ringtone mRingtone;

	public RingtoneProcess()
	{
		try
		{
			mRingtone = new Ringtone("HBStore");
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
			String RingtoneID = Info.substring(msgObject.getKeyword().length(), Info.length()).trim();

			String RingtoneName = "";

			RingtoneID = MyText.RemoveSpecialLetter(1, RingtoneID);
			if (RingtoneID == "")
			{
				MTContent = "Bai hat dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Bai hat khac.";
				return AddToQueue(msgObject, MTContent, "");
			}

			MyDataSource.MyTableModel mTable = mRingtone.Select(1, RingtoneID);

			// Nếu bài hát không tồn tại trong hệ thống
			if (mTable.GetRowCount() < 1)
			{
				MTContent = "Bai hat dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Bai hat khac.";
				return AddToQueue(msgObject, MTContent, "");
			}

			RingtoneName = mTable.GetValueAt(0, "RingtoneName").toString();
			RingtoneName = MyText.RemoveSignVietnameseString(RingtoneName);

			WS.GetLinkMedia.GetMedia mGetMedia = new GetMedia();
			double Price = MyUtility.MyConvert.ShortCodeToPrice(msgObject.getServiceid());

			SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");

			Date mDate = (Date) Calendar.getInstance().getTime();

			String RequestTime = mFormat.format(mDate);
			RequestTime += "00";

			String Result = mGetMedia.getGetMediaSoap().getLinkMedia(LocalConfig.GetLink_User, LocalConfig.GetLink_Password, msgObject.getUserid(), 4, RingtoneID, 3, Price, RequestTime);

			String Link = "";
			if (!Result.startsWith("0"))
			{
				MTContent = "Bai hat dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Bai hat khac.";
				return AddToQueue(msgObject, MTContent, "");
			}
			Link = Result.split("\\|")[1];

			if (!Link.startsWith("http"))
			{
				MTContent = "Bai hat dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Bai hat khac.";

				return AddToQueue(msgObject, MTContent, "");
			}

			MTContent = "De tai Bai hat " + RingtoneName + " hay truy cap vao link:" + Link;

			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);

		}
		catch (Exception ex)
		{
			mLog.log.error("Error", ex);
			MTContent = "Bai hat dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Bai hat khac.";
			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);
		}

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("Ringtone-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

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
