package MyProcess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import MyStore.Ringback;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyLogger;
import MyUtility.MyText;
import MyUtility.MyConfig.Telco;
import MyCallWebservice.SetRingback;
import MyProcessServer.*;

public class GiftToneRingback extends ContentAbstract
{
	MyLogger mLog = new MyLogger(RingbackProcess.class.toString());

	Ringback mRingback;

	public GiftToneRingback()
	{
		try
		{
			mRingback = new Ringback("HBStore");
		}
		catch (Exception ex)
		{
			mLog.log.error("Ringback_Error", ex);
		}
	}

	Collection<MsgObject> MessOject = new ArrayList<MsgObject>();

	/**
	 * Trả về một mảng [MSISDN][SongName][RingbackCode]
	 * 
	 * @param Info
	 * @return
	 * @throws Exception
	 */
	private String[] AnalyticInfo(String Info) throws Exception
	{
		try
		{
			Info = Info.trim();
			// Xóa bỏ tất cả các ký tự đặc biệt, chỉ giữ lại ký tự Số,Chữ,
			// KHoảng trắng
			Info = MyText.RemoveSpecialLetter(2, Info, " ");

			String CheckNumberList = "0123456789";
			boolean FirstNumber = false;
			if (CheckNumberList.indexOf(Info.substring(0, 1)) >= 0)
			{
				FirstNumber = true;
			}

			String strRingbackCode = "";
			String strMSISDN = "";
			String strSongName = "";

			if (FirstNumber)
			{
				for (char i : Info.toCharArray())
				{
					if (CheckNumberList.indexOf(i) < 0)
					{
						break;
					}
					strRingbackCode += i;
				}

				String Temp_Info = Info.substring(strRingbackCode.length(), Info.length());

				// Xoa cac ky tu dac biet, chi giu lai ky tu so
				strMSISDN = MyText.RemoveSpecialLetter(1, Temp_Info);

				// Xóa các ký tự đặc biệt chỉ giữ lại ký tự chữ
				strSongName = MyText.RemoveSpecialLetter(3, Temp_Info);

			}
			else
			{
				for (char i : Info.toCharArray())
				{
					if (CheckNumberList.indexOf(i) >= 0)
					{
						break;
					}
					strSongName += i;
				}

				String Temp_Info = Info.substring(strSongName.length(), Info.length());

				// xoa bo ky tu trang
				strSongName = MyText.RemoveSpecialLetter(3, strSongName);
				strMSISDN = MyText.RemoveSpecialLetter(1, Temp_Info);
			}

			if (strRingbackCode != "")
			{
				String Temp_MSISDN = MyCheck.CheckPhoneNumber(strMSISDN);

				if (Temp_MSISDN == "")
				{
					Temp_MSISDN = MyCheck.CheckPhoneNumber(strRingbackCode);

					if (Temp_MSISDN != "")
					{
						// Doi gia tri
						strRingbackCode = strMSISDN;
						strMSISDN = Temp_MSISDN;
					}
				}
				else
				{
					strMSISDN = Temp_MSISDN;
				}
			}
			else
			{
				strMSISDN = MyCheck.CheckPhoneNumber(strMSISDN);
			}

			return new String[] { strMSISDN, strRingbackCode, strSongName };
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	@Override
	protected Collection<MsgObject> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{

		String MTContent = "Tin nhan sai cu phap, xin hay kiem tra lai tin nhan.";
		String MTPromo = "";

		String SongName = "";
		String RingbackCode = "";
		String DesMSISDN = "";
		try
		{
			String Info = msgObject.getUsertext().toLowerCase();
			Info = Info.trim();
			if (Info.length() <= msgObject.getKeyword().length())
			{
				return AddToQueue(msgObject, MTContent, MTPromo);
			}

			MyConfig.Telco mTelco = Telco.NOTHING;
			mTelco = MyCheck.GetTelco(msgObject.getUserid());

			// Kiểm tra các mạng được phép cài đặt nhạc chờ
			if (mTelco != Telco.GPC && mTelco != Telco.VMS)
			{
				MTContent = "Hien he thong khong ho tro Nhac cho cho mang " + MyConfig.GetTelCoName(mTelco) + ".";
				return AddToQueue(msgObject, MTContent, MTPromo);
			}

			Info = Info.substring(msgObject.getKeyword().length(), Info.length()).trim();

			String[] Arr = AnalyticInfo(Info);

			DesMSISDN = Arr[0];
			RingbackCode = Arr[1];
			SongName = Arr[2];

			if (DesMSISDN == "")
			{
				MTContent = "Ma,Ten bai hat hoac So dien thoai nguoi nhan khong chinh xac. Vui long kiem tra lai.";
				return AddToQueue(msgObject, MTContent, MTPromo);
			}

			MyConfig.Telco mTelco_Des = Telco.NOTHING;
			mTelco_Des = MyCheck.GetTelco(DesMSISDN);

			if (mTelco_Des != mTelco)
			{
				MTContent = "So dien thoai nguoi nhan khong thuoc mang Vinaphone, xin vui long kiem tra lai.";
				return AddToQueue(msgObject, MTContent, MTPromo);
			}

			Hashtable<String, String> mList = new Hashtable<String, String>();

			if (RingbackCode.length() > 0)
			{
				mList = mRingback.GetRingbackByCode(RingbackCode, mTelco);
			}
			else
			{
				mList = mRingback.GetRingbackByName(SongName, mTelco);
			}

			// Nếu bài hát không tồn tại trong hệ thống
			if (mList.isEmpty())
			{
				MTContent = "Ma hoac Ten bai hat ban yeu cau khong co tren he thong 6x83. Vui long kiem tra lai.";
				return AddToQueue(msgObject, MTContent, MTPromo);
			}

			for (String key : mList.keySet())
			{
				RingbackCode = key;
				SongName = mList.get(key);
				break;
			}

			MTContent = GiftTone(msgObject, DesMSISDN, RingbackCode, SongName);

		}
		catch (Exception ex)
		{
			mLog.log.error("Ringback_Error", ex);
			MTContent = "Ma hoac Ten bai hat ban yeu cau khong co tren he thong 6x83. Vui long kiem tra lai.";
		}

		msgObject.setUsertext(MTContent + MTPromo);
		msgObject.setContenttype(21);
		msgObject.setMsgtype(1);

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("NhacCho-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

		return MessOject;

	}

	private String GiftTone(MsgObject mObject, String DesMSISDN, String RingbackCode, String SongName) throws Exception
	{
		try
		{
			String Result = SetRingback.CallWebservice(mObject.getUserid() + "|" + DesMSISDN, "PresentTone", RingbackCode,"");
			int iResult = Integer.parseInt(Result);

			String MT = "Tang nhac cho khong thanh cong, xin thu lai voi mot bai hat khac.";
			switch (iResult)
			{
				case 0:
					MT = "Bai hat ban tang da duoc gui thanh cong den so [" + DesMSISDN + "]. Chi phi tang bai hat da duoc tinh cho so may di dong cua ban.";
					break;
				case 605:
					MT = "Bai hat ban gui tang da co trong bo suu tap cua thue bao [" + DesMSISDN + "]. Bai hat se duoc tu dong gia han thoi gian su dung.";
					break;
				case 604:
					MT = "Bo suu tap cua the bao [" + DesMSISDN + "] da day, nen khong the gui tang.";
					break;
				case 200:
					MT = "Rat tiec. Ban chua dang ky su dung dich vu Nhac cho. De su dung dich vu soan DKNC gui 6783.";
					break;
				case 602:
					MT = "Rat tiec, gui tang khong thanh cong, do thue bao ban gui tang [" + DesMSISDN + "] chua dang ky su dung dich vu Nhac cho.";
					break;
				case 107:
					MT = "Trang thai thue bao cua ban hien khong the tang bai hat, xin vui long lien he voi bo phan cham soc khach hang Vinaphone de duoc giai dap them.";
					break;
				case 606:
					MT = "Rat tiec, gui tang khong thanh cong, do thue bao ban gui tang [" + DesMSISDN + "] chua dang ky su dung dich vu Nhac cho.";
					break;
				case 500:
					MT = "Ma hoac Ten bai hat ban yeu cau khong co tren he thong 6x83. Vui long kiem tra lai.";
					break;
				case 101:
					MT = "Tai khoan cua ban khong du. Vui long nap them tien vao tai khoan va thu lai.";
					break;
				case 402:
					MT = "Ma hoac Ten bai hat ban yeu cau khong co tren he thong 6x83. Vui long kiem tra lai.";
					break;
				case 702:
					MT = "Tai khoan cua ban khong du. Vui long nap them tien vao tai khoan va thu lai.";
					break;
			}

			return MT;
		}
		catch (Exception ex)
		{
			throw ex;
		}
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