package MyProcess;

import MyProcessServer.*;

import java.util.ArrayList;
import java.util.Collection;

import MyStore.Ringback;
import MyUtility.MyCheck;
import MyUtility.MyConfig;
import MyUtility.MyLogger;
import MyUtility.MyConfig.Telco;
import MyCallWebservice.SetRingback;

public class RegisterRingback extends ContentAbstract
{
	MyLogger mLog = new MyLogger(RingbackProcess.class.toString());

	Ringback mRingback;

	public RegisterRingback()
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

		String MTContent = "Tin nhan sai cu phap";
		String MTPromo = "";

		try
		{
			MyConfig.Telco mTelco = Telco.NOTHING;
			mTelco = MyCheck.GetTelco(msgObject.getUserid());

			// Kiểm tra các mạng được phép cài đặt nhạc chờ
			if (mTelco != Telco.GPC && mTelco != Telco.VMS)
			{
				MTContent = "Hien he thong khong ho tro Nhac cho cho mang " + MyConfig.GetTelCoName(mTelco) + ".";
				return AddToQueue(msgObject, MTContent, MTPromo);
			}

			MTContent = Register(msgObject);

		}
		catch (Exception ex)
		{
			mLog.log.error("Ringback_Error", ex);
			MTContent = "Dang ky dich vu Nha cho khong thanh cong, xin vui long thu lai sau it phut.";
		}

		msgObject.setUsertext(MTContent + MTPromo);
		msgObject.setContenttype(21);
		msgObject.setMsgtype(1);

		MessOject.add(new MsgObject(msgObject));

		mLog.log.info("NhacCho-->UserID:" + msgObject.getUserid() + "||MT:" + msgObject.getUsertext());

		return MessOject;
	}

	private String Register(MsgObject mObject) throws Exception
	{
		try
		{
			String Result = SetRingback.CallWebservice(mObject.getUserid(), "Register", "","");
			int iResult = Integer.parseInt(Result);

			String MT = "Dang ky dich vu Nhac cho khong thanh cong, xin thu lai sau it phut.";
			switch (iResult)
			{
				case 0:
					MT = "Chuc mung ban da dang ky dich vu Nhac cho thanh cong. De tai bai hat soan tin: NC_tenbaihat gui 6583.";
					break;
				case 1:
				case -1:
				case 2:
				case -2:
					MT = "Hanh dong dang ky dich vu Nhac cho khong thanh cong, xin vui long thu lai sau it phut.";
					break;

				case 107:
					MT = "Trang thai thue bao cua ban hien khong the mua bai hat, xin vui long lien he voi bo phan cham soc khach hang Vinaphone de duoc giai dap them";
					break;
				case 205:
					MT = "Ban da dang ky su dung dich vu Nhac cho roi. De tai bai hat soan tin: NC_tenbaihat gui 6583";
					break;
				case 103:
					MT = "So lan dang ky da vuot qua so lan cho phep trong ngay, xin thu lai vao ngay tiep theo.";
					break;
				case 402:
					MT = "Thong tin bai hat khong hop le, xin vui long kiem tra lai.";
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