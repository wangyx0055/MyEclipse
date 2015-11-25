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
import MyStore.Packet;
import MyUtility.MyLogger;
import MyUtility.MyText;
import WS.GetLinkMedia.GetMedia;

public class PacketProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	Packet mPacket;

	public PacketProcess()
	{
		try
		{
			mPacket = new Packet("HBStore");
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
			String PacketID = Info.substring(msgObject.getKeyword().length(), Info.length()).trim();

			String PacketName = "";

			PacketID = MyText.RemoveSpecialLetter(1, PacketID);
			if (PacketID == "")
			{
				MTContent = "Goi dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Goi khac.";
				return AddToQueue(msgObject, MTContent, "");
			}

			MyDataSource.MyTableModel mTable = mPacket.Select(1, PacketID);

			// Nếu bài hát không tồn tại trong hệ thống
			if (mTable.GetRowCount() < 1)
			{
				MTContent = "Goi dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Goi khac.";
				return AddToQueue(msgObject, MTContent, "");
			}

			PacketName = mTable.GetValueAt(0, "PacketName").toString();
			PacketName = MyText.RemoveSignVietnameseString(PacketName);

			WS.GetLinkMedia.GetMedia mGetMedia = new GetMedia();
			double Price = MyUtility.MyConvert.ShortCodeToPrice(msgObject.getServiceid());

			SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");

			Date mDate = (Date) Calendar.getInstance().getTime();

			String RequestTime = mFormat.format(mDate);
			RequestTime += "00";

			String Result = mGetMedia.getGetMediaSoap().getLinkMedia(LocalConfig.GetLink_User, LocalConfig.GetLink_Password, msgObject.getUserid(), 10, PacketID, 3, Price, RequestTime);

			String Link = "";
			if (!Result.startsWith("0"))
			{
				MTContent = "Goi dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Goi khac.";
				return AddToQueue(msgObject, MTContent, "");
			}
			Link = Result.split("\\|")[1];

			if (!Link.startsWith("http"))
			{
				MTContent = "Goi dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Goi khac.";

				return AddToQueue(msgObject, MTContent, "");
			}

			MTContent = "De tai Goi " + PacketName + " hay truy cap vao link:" + Link;

			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);

		}
		catch (Exception ex)
		{
			mLog.log.error("Error", ex);
			MTContent = "Goi dang bi khoa hoac khong ton tai tren he thong, xin vui long chon mot Goi khac.";
			msgObject.setUsertext(MTContent);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);
		}

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("Packet-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

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
