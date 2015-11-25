package My.Webservice;

import MyDataSource.MyTableModel;
import MyGateway.SMSReceiveForward;
import MyGateway.SMSSendQueue;
import MyUtility.*;
import MyUtility.MyConfig.Telco;

public class ReceiveMT
{

	MyLogger mLog = new MyLogger(ReceiveMT.class.toString());

	/**
	 * @param User_ID
	 *            So dien thoai khach hang
	 * @param Message
	 *            MT gui cho khach hang
	 * @param Service_ID
	 *            Dau so dich vu
	 * @param Command_Code
	 *            Keyword dich vu
	 * @param Message_Type
	 *            = 1 tính tiền, 0 là không tính tiền
	 * @param Request_ID
	 *            lấy từ thông tin MO
	 * @param Total_Message
	 *            Tổng số MT/160
	 * @param Content_Type
	 *            = 0 là text, =8 là wappush
	 * @param Operator
	 *            Nhà mạng (VD: VIETTEL, VMS, GPC)
	 * @param UserName
	 *            user danh nhap
	 * @param Password
	 *            Mat khau dang nhap Encrypt(User+passowrd)
	 * @return kết quả trả về là 1 kiểu chuỗi:
	 * @throws Exception
	 */
	public String SendMT(String User_ID, String Message, String Service_ID, String Command_Code, String Message_Type, String Request_ID, String Total_Message,
			String Content_Type, String Operator, String UserName, String Password) throws Exception
	{
		SMSReceiveForward mForward;
		/* return User_ID; */

		String LogData = "";
		String Result = "1";

		try
		{
			Message = Message.replace("'", "’");

			if (!CheckPartner(UserName, Password))
			{
				Result = "2";
				return Result;
			}

			MyConfig.Telco mTelco = Telco.NOTHING;

			String PhoneNumber = MyCheck.CheckPhoneNumber(User_ID);

			mTelco = MyCheck.GetTelco(PhoneNumber);

			if (PhoneNumber.equals("") || mTelco == Telco.NOTHING || Request_ID.equals(""))
			{
				Result = "3";
				return Result;
			}
			if (!MyCheck.CheckServiceID(Service_ID))
			{
				Result = "4";
				return Result;
			}
			if (!MyCheck.CheckMobileOperator(Operator))
			{
				Result = "4";
				return Result;
			}

			MyTableModel mTable = CheckRequestID(Service_ID, User_ID, Request_ID, mTelco);

			if (mTable.GetRowCount() < 1)
			{
				Result = "9";
				return Result;
			}
			String VMS_SVID = "";
			if (mTable.CheckColumnsExists("VMS_SVID"))
			{
				VMS_SVID = mTable.GetValueAt(0, "VMS_SVID").toString();
			}
			Boolean InsertResult = false;

			String MessageType_Temp = Message_Type;
			String ConnentType_Temp = Content_Type;
			if (!Message_Type.equals("2"))
			{
				// Nếu bản tin không phải là hoàn tiền thì chuyển hết sang dạng
				// bản tin tính tiền.
				MessageType_Temp = "1";
			}

			if (Content_Type == "1" && Message.length() > 160)
			{
				ConnentType_Temp = "21"; // là bản tin dài
			}
			String Message_RemoveVN = MyUtility.MyText.RemoveSignVietnameseString(Message);

			mForward = new SMSReceiveForward(GetPoolName(mTelco));
			SMSSendQueue mSendQueueVT = new SMSSendQueue(GetPoolName(mTelco));
			if (mTelco == Telco.VMS)
			{
				String VMSRequestID = mTable.GetValueAt(0, "REQUEST_ID").toString();
				InsertResult = mSendQueueVT.Insert_VMS(User_ID, Service_ID, Operator, Command_Code, ConnentType_Temp, Message_RemoveVN, "1", MessageType_Temp,
						VMSRequestID, "1", Total_Message, "0", "2", VMS_SVID);
			}
			else
			{
				InsertResult = mSendQueueVT.Insert(User_ID, Service_ID, Operator, Command_Code, ConnentType_Temp, Message_RemoveVN, "1", MessageType_Temp,
						Request_ID, "1", Total_Message, "0", "2");
			}

			if (InsertResult)
			{
				// Xóa dữ liệu trong table MO Forward
				mForward.Delete(Request_ID);

				Result = "0";
				return Result;
			}
			else
			{
				Result = "1";
				return Result;
			}
		}
		catch (Exception ex)
		{
			mLog.log.error("SendMT Error", ex);
			return "-1";
		}
		finally
		{
			LogData = "BEGIN:Nhan thong tin truyen vao -->User_ID:" + User_ID + " || Message:" + Message + " || Service_ID:" + Service_ID + " || Command_Code:"
					+ Command_Code + " || Message_Type:" + Message_Type + " || Request_ID:" + Request_ID + " || Total_Message:" + Total_Message
					+ " || Content_Type:" + Content_Type + " || Operator:" + Operator + " || UserName:" + UserName + " || Password:" + Password + " || Result:"
					+ Result;

			mLog.log.info(LogData);
		}
	}

	private String GetPoolName(Telco mTelco)
	{
		if (mTelco == Telco.GPC)
		{
			return "gatewayGPC";
		}
		else if (mTelco == Telco.VMS)
		{
			return "gatewayVMS";

		}
		else if (mTelco == Telco.VIETTEL)
		{
			return "gatewayVT";

		}
		else if (mTelco == Telco.HTC)
		{
			return "gatewayHTC";
		}
		return "";
	}

	private Boolean CheckPartner(String user, String pass) throws Exception
	{
		try
		{
			Boolean Result = false;
			if (user.toLowerCase().equalsIgnoreCase("PartnerFRQWE".toLowerCase()))
			{
				String LocalPassword = MySeccurity.MD5("PartnerFRQWE" + "HUFT234@#ER4");
				if (pass.equalsIgnoreCase(LocalPassword))
					return true;
			}
			else if (user.toLowerCase().equalsIgnoreCase("iplayviethorizon".toLowerCase()))
			{
				String LocalPassword = MySeccurity.MD5("iplayviethorizon" + "!PLAY!@#");
				if (pass.equalsIgnoreCase(LocalPassword))
					return true;
			}

			return Result;

		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	private MyDataSource.MyTableModel CheckRequestID(String ServiceID, String UserID, String RequestID, Telco mTelco) throws Exception
	{
		try
		{
			String PoolName = GetPoolName(mTelco);

			SMSReceiveForward mForward = new SMSReceiveForward(PoolName);

			MyDataSource.MyTableModel mTable = mForward.Select(ServiceID, UserID, RequestID.trim());
			return mTable;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
