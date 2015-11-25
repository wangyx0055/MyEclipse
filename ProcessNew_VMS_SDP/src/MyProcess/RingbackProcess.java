package MyProcess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyText;
import MyUtility.MyConfig.Telco;
import MyUtility.MyLogger;

import MyProcessServer.*;
import MyStore.*;
import MyCallWebservice.SetRingback;

public class RingbackProcess extends ContentAbstract
{
	MyLogger mLog = new MyLogger(RingbackProcess.class.toString());

	Ringback mRingback;

	public RingbackProcess()
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

	@Override
	protected Collection<MsgObject> getMessages(MsgObject msgObject, Keyword keyword) throws Exception
	{
		String MTContent = "Cai dat Nhac cho khong thanh cong, xin vui long thu lai sau it phut.";
		String MTPromo = "";
		String Before_SongName = "";
		String RingbackCode = "";
		try
		{
			String SongName = msgObject.getUsertext().toLowerCase();

			if (msgObject.getKeyword().length() >= SongName.length())
			{
				return AddToQueue(msgObject, MTContent, MTPromo);
			}

			Before_SongName = SongName = SongName.substring(msgObject.getKeyword().length(), SongName.length()).trim();

			Before_SongName = Before_SongName.trim();

			SongName = SongName.replace(" ", "");
			RingbackCode = MyText.RemoveSpecialLetter(1, SongName);

			MyConfig.Telco mTelco = Telco.NOTHING;
			mTelco = MyCheck.GetTelco(msgObject.getUserid());

			// Kiểm tra các mạng được phép cài đặt nhạc chờ
			if (mTelco != Telco.GPC && mTelco != Telco.VMS)
			{
				MTContent = "Hien he thong khong ho tro Nhac cho cho mang " + MyConfig.GetTelCoName(mTelco) + ".";
				return AddToQueue(msgObject, MTContent, MTPromo);
			}

			Hashtable<String, String> mList = new Hashtable<String, String>();

			if (RingbackCode.length() >= 4)
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
				if (Before_SongName != "")
				{
					MTContent = SearchAndSettoneRingback(msgObject, Before_SongName);
				}
				else
				{
					MTContent = "Ma hoac Ten bai hat ban yeu cau khong co tren he thong 6x83. Vui long kiem tra lai.";
					return AddToQueue(msgObject, MTContent, MTPromo);
				}
			}
			else
			{
				for (String key : mList.keySet())
				{
					RingbackCode = key;
					SongName = mList.get(key);
					break;
				}

				MTContent = SettoneRingback(msgObject, RingbackCode, SongName);
			}

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

		mLog.log.info("NhacCho-->UserID:" + msgObject.getUserid() + "|| MT:" + msgObject.getUsertext());

		return MessOject;

	}

	private String SettoneRingback(MsgObject mObject, String RingbackCode, String SongName) throws Exception
	{
		try
		{
			String Result = SetRingback.CallWebservice(mObject.getUserid(), "Settone", RingbackCode, "");
			int iResult = Integer.parseInt(Result);

			String MT = "Cai dat nhac cho khong thanh cong, xin thu lai voi mot bai hat khac.";
			switch (iResult)
			{
				case 0:
					MT = "Chuc mung ban da mua thanh cong bai hat [" + SongName + "] [" + RingbackCode
							+ "]  co gia 2000 VND. Bai hat nay se duoc cai dat mac dinh cho nhac cho cua ban.";
					break;
				case 1:
				case -1:
				case 2:
				case -2:
					MT = "Cai dat Nhac cho khong thanh cong, xin vui long thu lai sau it phut.";
					break;
				case 501:
					MT = "Bai hat ban vua mua da co trong bo suu tap cua ban nen ban khong bi tinh phi.";
					break;
				case 106:
					MT = "So lan mua bat hat da vuot qua so lan cho phep trong ngay, xin thu lai vao ngay tiep theo.";
					break;
				case 504:
					MT = "Bo suu tap cua ban da day, de tiep tuc mua bai hat, xin vui long xoa bai hat trong bo suu tap truoc.";
					break;
				case 107:
					MT = "Trang thai thue bao cua ban hien khong the mua bai hat, xin vui long lien he voi bo phan cham soc khach hang Vinaphone de duoc giai dap them.";
					break;
				case 500:
					MT = "Ma hoac Ten bai hat ban yeu cau khong co tren he thong 6x83. Vui long kiem tra lai.";
					break;
				case 101:
					MT = "Tai khoan cua ban khong du. Vui long nap them tien vao tai khoan va thu lai.";
					break;
				case 402:
					MT = "Thong tin bai hat khong hop le, xin vui long kiem tra lai.";
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

	private String SearchAndSettoneRingback(MsgObject mObject, String SongName) throws Exception
	{
		try
		{
			String RingbackCode = "";

			String Result = SetRingback.CallWebservice(mObject.getUserid(), "Search", RingbackCode, SongName);
			String[] Arr_Result = Result.split("\\|");
			int iResult = 1;
			if (Arr_Result.length > 0)
			{
				iResult = Integer.parseInt(Arr_Result[0]);
			}

			if (Arr_Result.length == 3)
			{
				RingbackCode = Arr_Result[1];
				SongName = Arr_Result[2];
			}

			String MT = "Cai dat nhac cho khong thanh cong, xin thu lai voi mot bai hat khac.";
			switch (iResult)
			{
				case 0:
					MT = "Chuc mung ban da mua thanh cong bai hat [" + SongName + "] [" + RingbackCode
							+ "]  co gia 2000 VND. Bai hat nay se duoc cai dat mac dinh cho nhac cho cua ban.";
					break;
				case 1:
				case -1:
				case 2:
				case -2:
					MT = "Cai dat Nhac cho khong thanh cong, xin vui long thu lai sau it phut.";
					break;
				case 501:
					MT = "Bai hat ban vua mua da co trong bo suu tap cua ban nen ban khong bi tinh phi.";
					break;
				case 106:
					MT = "So lan mua bat hat da vuot qua so lan cho phep trong ngay, xin thu lai vao ngay tiep theo.";
					break;
				case 504:
					MT = "Bo suu tap cua ban da day, de tiep tuc mua bai hat, xin vui long xoa bai hat trong bo suu tap truoc.";
					break;
				case 107:
					MT = "Trang thai thue bao cua ban hien khong the mua bai hat, xin vui long lien he voi bo phan cham soc khach hang Vinaphone de duoc giai dap them.";
					break;
				case 500:
					MT = "Ma hoac Ten bai hat ban yeu cau khong co tren he thong 6x83. Vui long kiem tra lai.";
					break;
				case 101:
					MT = "Tai khoan cua ban khong du. Vui long nap them tien vao tai khoan va thu lai.";
					break;
				case 402:
					MT = "Thong tin bai hat khong hop le, xin vui long kiem tra lai.";
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
