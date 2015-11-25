package MyProcess;

import MyProcessServer.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import MyStore.Video;
import MyUtility.MyLogger;
import MyUtility.MyText;
import WS.GetLinkMedia.GetMedia;

public class VideoProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(RingtoneProcess.class.toString());

	Video mVideo;

	public VideoProcess()
	{
		try
		{
			mVideo = new Video("HBStore");
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
			String VideoID = Info.substring(msgObject.getKeyword().length(), Info.length()).trim();

			String VideoName = "";
			VideoID = MyText.RemoveSpecialLetter(1, VideoID);
			if (VideoID == "")
			{
				MTContent = "Clip dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Clip khac.";
				return AddToQueue(msgObject, MTContent, "");
			}
			MyDataSource.MyTableModel mTable = mVideo.Select(1, VideoID);

			// Nếu bài hát không tồn tại trong hệ thống
			if (mTable.GetRowCount() < 1)
			{
				MTContent = "Clip dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Clip khac.";
				return AddToQueue(msgObject, MTContent, "");
			}

			VideoName = mTable.GetValueAt(0, "VideoName").toString();
			VideoName = MyText.RemoveSignVietnameseString(VideoName);
			WS.GetLinkMedia.GetMedia mGetMedia = new GetMedia();
			double Price = MyUtility.MyConvert.ShortCodeToPrice(msgObject.getServiceid());

			SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");

			Date mDate = (Date) Calendar.getInstance().getTime();

			String RequestTime = mFormat.format(mDate);
			RequestTime += "00";

			String Result = mGetMedia.getGetMediaSoap().getLinkMedia(LocalConfig.GetLink_User, LocalConfig.GetLink_Password, msgObject.getUserid(), 3, VideoID, 3, Price, RequestTime);

			String Link = "";
			if (!Result.startsWith("0"))
			{
				MTContent = "Clip dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Clip khac.";
				return AddToQueue(msgObject, MTContent, "");
			}
			Link = Result.split("\\|")[1];

			if (!Link.startsWith("http"))
			{
				MTContent = "Clip dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Clip khac.";

				return AddToQueue(msgObject, MTContent, "");
			}

			MTContent = "De tai Clip " + VideoName + " hay truy cap vao link:" + Link;

			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);

		}
		catch (Exception ex)
		{
			mLog.log.error("Error", ex);
			MTContent = "Clip dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Clip khac.";
			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);
		}

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("Video-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

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