package MyProcess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import MyUtility.MyLogger;

import MyProcessServer.*;
import MyStore.*;
import WS.GetLinkMedia.GetMedia;

public class RingtoneProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(RingtoneProcess.class.toString());

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
		// String MTPromo =
		// " (QC)De cap nhap tin tuc giao thong nong hoi trong ngay hay cac thong tin ve kinh te, chinh tri, van hoa, giao duc, bong da noi bat,Cac ban soan tin: DV gui 6783 ";
		String Before_SongName = "";
		try
		{
			String SongName = msgObject.getUsertext().trim();

			Before_SongName = SongName = SongName.substring(msgObject.getKeyword().length(), SongName.length()).trim();

			Before_SongName = Before_SongName.trim();

			SongName = SongName.replace(" ", "");

			List<String> mListCode = mRingtone.GetIDByName(SongName);

			// Nếu bài hát không tồn tại trong hệ thống
			if (mListCode.isEmpty())
			{
				MTContent = "Bai hat "
						+ Before_SongName
						+ " hien chua duoc cap nhap.De cai cac bai hat cua The voice lam nhac cho,ban soan: NC khoang cach ten baihat viet lien khong dau gui 6783;Tuong tu Nhac chuong: NH tenbaihat gui 6783";
				return AddToQueue(msgObject, MTContent, "");
			}

			String RingtoneID = mListCode.get(0).toString();

			WS.GetLinkMedia.GetMedia mGetMedia = new GetMedia();
			double Price = MyUtility.MyConvert.ShortCodeToPrice(msgObject.getServiceid());

			SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");

			Date mDate = (Date) Calendar.getInstance().getTime();

			String RequestTime = mFormat.format(mDate);
			RequestTime += "00";

			String Result = mGetMedia.getGetMediaSoap().getLinkMedia("6x83", "6x83sdadaffasasxa", msgObject.getUserid(), 4, RingtoneID, 3, Price, RequestTime);

			String Link = "";
			if (!Result.startsWith("0"))
			{
				MTContent = "Bai hat "
						+ Before_SongName
						+ " hien chua duoc cap nhap.De cai cac bai hat cua The voice lam nhac cho,ban soan: NC khoang cach ten baihat viet lien khong dau gui 6783;Tuong tu Nhac chuong: NH tenbaihat gui 6783";
				return AddToQueue(msgObject, MTContent, "");
			}
			Link = Result.split("\\|")[1];

			if (!Link.startsWith("http"))
			{
				MTContent = "Bai hat "
						+ Before_SongName
						+ " hien chua duoc cap nhap.De cai cac bai hat cua The voice lam nhac cho,ban soan: NC khoang cach ten baihat viet lien khong dau gui 6783;Tuong tu Nhac chuong: NH tenbaihat gui 6783";
				return AddToQueue(msgObject, MTContent, "");
			}

			MTContent = "De tai bai hat " + Before_SongName + " hay truy cap vao link:" + Link;

			// MTContent = "De tai bai hat " + Before_SongName + " hay soan " +
			// mRingbackCode + ".";
			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(8);
			msgObject.setMsgtype(1);
		}
		catch (Exception ex)
		{
			mLog.log.error("Error", ex);
			MTContent = "Bai hat "
					+ Before_SongName
					+ " hien chua duoc cap nhap.De cai cac bai hat cua The voice lam nhac cho,ban soan: NC khoang cach ten baihat viet lien khong dau gui 6783;Tuong tu Nhac chuong: NH tenbaihat gui 6783";
			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);
		}

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("NhacCho-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

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
