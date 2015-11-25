package MyProcess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import MyStore.Image;
import MyUtility.MyLogger;
import MyUtility.MyText;
import WS.GetLinkMedia.GetMedia;
import MyProcessServer.*;

public class ImageProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	Image mImage;

	public ImageProcess()
	{
		try
		{
			mImage = new Image("HBStore");
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

			String ImageID = Info.substring(msgObject.getKeyword().length(), Info.length()).trim();

			String ImageName = "";
			ImageID = MyText.RemoveSpecialLetter(1, ImageID);

			if (ImageID == "")
			{
				MTContent = "Hinh Anh dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Hinh anh khac.";
				return AddToQueue(msgObject, MTContent, "");
			}
			MyDataSource.MyTableModel mTable = mImage.Select(1, ImageID);

			// Nếu bài hát không tồn tại trong hệ thống
			if (mTable.GetRowCount() < 1)
			{
				MTContent = "Hinh Anh dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Hinh Anh khac.";
				return AddToQueue(msgObject, MTContent, "");
			}

			ImageName = mTable.GetValueAt(0, "ImageName").toString();
			ImageName = MyText.RemoveSignVietnameseString(ImageName);

			WS.GetLinkMedia.GetMedia mGetMedia = new GetMedia();
			double Price = MyUtility.MyConvert.ShortCodeToPrice(msgObject.getServiceid());

			SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");

			Date mDate = (Date) Calendar.getInstance().getTime();

			String RequestTime = mFormat.format(mDate);
			RequestTime += "00";

			String Result = mGetMedia.getGetMediaSoap().getLinkMedia(LocalConfig.GetLink_User, LocalConfig.GetLink_Password, msgObject.getUserid(), 5, ImageID, 3, Price, RequestTime);

			String Link = "";
			if (!Result.startsWith("0"))
			{
				MTContent = "Hinh Anh dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Hinh anh khac.";
				return AddToQueue(msgObject, MTContent, "");
			}
			Link = Result.split("\\|")[1];

			if (!Link.startsWith("http"))
			{
				MTContent = "Hinh Anh dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Hinh anh khac.";

				return AddToQueue(msgObject, MTContent, "");
			}

			MTContent = "De tai Hinh Anh " + ImageName + " hay truy cap vao link:" + Link;

			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);

		}
		catch (Exception ex)
		{
			mLog.log.error("Error", ex);
			MTContent = "Hinh Anh dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Hình ảnh khac.";
			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);
		}

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("Image-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

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
