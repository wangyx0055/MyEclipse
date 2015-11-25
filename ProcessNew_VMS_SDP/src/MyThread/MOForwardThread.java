package MyThread;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import MyCallWebservice.ThuDoForwardMO;
import MyDataSource.MyTableModel;
import MyGateway.sms_receive_forward;
import MyProcess.IPlayProcess;
import MyProcess.ThuDoProcess;
import MyProcess.TimweProcess;
import MyProcessServer.Common;
import MyProcessServer.ConsoleSRV;
import MyProcessServer.Keyword;
import MyProcessServer.LocalConfig;
import MyProcessServer.MsgObject;
import MyUtility.MyLogger;
import WS.Iplay.Sms;

public class MOForwardThread extends Thread
{
	public static boolean IsProcessData = false;
	MyLogger mLog = new MyLogger(MOForwardThread.class.toString());

	/*
	 * Thời gian mỗi lần quét (tính bằng phút)
	 */
	public int DelayTime = LocalConfig.MOFORWAR_DELAYTIME;
	String MTContent = LocalConfig.MOFORWARD_MT_NOT_FORWARD;
	Integer BeforeMinute = LocalConfig.MOFORWARD_BEFORE_GET_TIME;
	Integer RetryMax = LocalConfig.MOFORWARD_MAX_RETRY;
	Integer IsRefund = LocalConfig.MOFORWARD_IS_REFUND_MONEY;

	sms_receive_forward mSMSReceive = null;

	public MOForwardThread()
	{
		try
		{
			mSMSReceive = new sms_receive_forward(LocalConfig.PoolName_Gateway);
		}
		catch (Exception ex)
		{
			mLog.log.error("Contructor ReminderThread Error:", ex);
		}
	}

	/**
	 * Retry forward MO cho đối tác, và cấp nhật số lần retry vào database
	 * 
	 * @param msgObject
	 * @param RetryCount
	 * @throws Exception
	 */
	private void RetryForwardMO(MsgObject msgObject, Integer RetryCount) throws Exception
	{
		String Result = "-1";
		String ClassName = "";
		try
		{
			Keyword mKey = ConsoleSRV.mLoadKeyword.getKeyword(msgObject.getUsertext(), msgObject.getServiceid());
			ClassName = mKey.getClassname();

			String ReceiveDate = new SimpleDateFormat("yyyyMMddHHmmss").format(msgObject.getTTimes());

			if (mKey.getClassname().equalsIgnoreCase(TimweProcess.class.getName()))
			{
				String infor = java.net.URLEncoder.encode(msgObject.getUsertext().replace(msgObject.getKeyword(), ""), "UTF-8");
				String CommandCode = java.net.URLEncoder.encode(msgObject.getKeyword(), "UTF-8");
				String Para = "User_ID=" + msgObject.getUserid() + "&Service_ID=" + msgObject.getServiceid() + "&Command_Code=" + CommandCode + "&Info="
						+ infor + "&Request_ID=" + msgObject.getRequestid() + "&Receive_Date=" + ReceiveDate + "&Operator=" + msgObject.getMobileoperator()
						+ "&UserName=PartnerFRQWE&Password=977CE426A4FABE0AC6F0FC39044831F3";

				String URL = "http://xc.emea.timwe.com/mg/vn/hbcom/recmo?" + Para;

				try
				{
					Result = MyUtility.MyText.ReadFromURL(URL);
				}
				catch (Exception ex)
				{
					mLog.log.error(ex);
				}
				finally
				{
					RetryCount++;
				}

			}
			else if (mKey.getClassname().equalsIgnoreCase(IPlayProcess.class.getName()))
			{
				WS.Iplay.Sms mSMS = new Sms();
				WS.Iplay.SmsSoap mSoapSMS = mSMS.getSmsSoap();

				try
				{
					Result = mSoapSMS.moForward(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getKeyword(), msgObject.getUsertext(), msgObject
							.getRequestid().toString(), ReceiveDate, msgObject.getMobileoperator(), "iplayviethorizon", "E5F3D005A8922E9AC132767920C5E8E0");
				}
				catch (Exception ex)
				{
					mLog.log.error(ex);
				}
				finally
				{
					RetryCount++;
				}
			}
			else if (mKey.getClassname().equalsIgnoreCase(ThuDoProcess.class.getName()))
			{
				try
				{
					Result = ThuDoForwardMO
							.CallWebservice(msgObject.getUserid(), msgObject.getServiceid(), msgObject.getKeyword(), msgObject.getUsertext(), msgObject
									.getRequestid().toString(), ReceiveDate, msgObject.getMobileoperator(), "PartnerFRQWE", "977CE426A4FABE0AC6F0FC39044831F3");
				}
				catch (Exception ex)
				{
					mLog.log.error(ex);
				}
				finally
				{
					RetryCount++;
				}
			}
			else
				return;

			mSMSReceive.Update_RetryCount(msgObject.getId(), RetryCount, Result);

		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
		}
		finally
		{
			mLog.log.info("Retry MO: ClassName:" + ClassName + " || RetryCount:" + RetryCount.toString() + " || Result:" + Result + " || "
					+ Common.GetStringLog(msgObject));
		}
	}

	public void run()
	{
		while (ConsoleSRV.getData)
		{
			try
			{
				boolean IsBreak = false;
				Long MaxID = (long) 0;
				Integer RowCount = 10;

				while (!IsBreak && ConsoleSRV.getData)
				{
					
					// Lấy dữ liệu trong DB
					MyTableModel mTable = mSMSReceive.Select(MaxID, RowCount);
					if (mTable.IsEmpty())
					{
						IsBreak = true;
						break;
					}
					String ListID = "";
					try
					{

						Calendar mCalendar_Get = Calendar.getInstance();
						Date mTime = mCalendar_Get.getTime();
						long ONE_MINUTE_IN_MILLIS = 60000;// millisecs

						long t = mTime.getTime();
						Date CheckDate = new Date(t - (BeforeMinute * ONE_MINUTE_IN_MILLIS));

						for (int i = 0; i < mTable.GetRowCount(); i++)
						{
							if (!ConsoleSRV.getData)
								break;

							MaxID = Long.parseLong(mTable.GetValueAt(i, "id").toString());
							String ServiceID = mTable.GetValueAt(i, "SERVICE_ID").toString();
							String MO = mTable.GetValueAt(i, "INFO").toString();
							String UserID = mTable.GetValueAt(i, "USER_ID").toString();
							String Keyword = mTable.GetValueAt(i, "COMMAND_CODE").toString();

							Integer RetryCount = Integer.parseInt(mTable.GetValueAt(i, "RetryCount").toString());
							Integer Status = Integer.parseInt(mTable.GetValueAt(i, "Status").toString());

							String Info = mTable.GetValueAt(i, "INFO").toString();
							BigDecimal RequestId = new BigDecimal(mTable.GetValueAt(i, "REQUEST_ID").toString());
							Timestamp ReceiveDate = (Timestamp) mTable.GetValueAt(i, "receive_date");
							Timestamp InsertDate = (Timestamp) mTable.GetValueAt(i, "insert_date");

							String Operator = mTable.GetValueAt(i, "MOBILE_OPERATOR").toString();
							
							String VMS_SVID="";
							
							if(mTable.CheckColumnsExists("VMS_SVID"))
								VMS_SVID = (String) mTable.GetValueAt(i, "VMS_SVID");
							

							
							MsgObject mOjbect = new MsgObject(MaxID, ServiceID, UserID, Keyword, Info, RequestId, ReceiveDate, Operator, 0, 0, MO,VMS_SVID);

							// Nếu như ngày Insert quá 60 phút hoắc số lần retry
							// quá quy định, thì sẽ hoàn tiền
							if (InsertDate.before(CheckDate) || (Status != 0 && RetryCount >= RetryMax))
							{
								mOjbect.setContenttype(21);
								mOjbect.setUsertext(MTContent);

								if (IsRefund == 1)
								{
									// =2 hoàn tiền cho khách hàng
									mOjbect.setMsgtype(2);
								}
								else
								{
									// =1 tính tiền
									mOjbect.setMsgtype(1);
								}

								int Result_SendMT = Common.sendMT(mOjbect);

								if (Result_SendMT == 1)
								{
									// Chỉ xóa trong db khi gửi MT thành công
									// cho khách hàng
									if (ListID == "")
									{
										ListID += MaxID.toString();
									}
									else
									{
										ListID += "," + MaxID.toString();
									}
								}
							}
							else if (Status != 0)
							{
								RetryForwardMO(mOjbect, RetryCount);
							}
						}
					}
					catch (Exception ex)
					{
						mLog.log.error(ex);
					}
					if (ListID != "")
						mSMSReceive.Delete(1, ListID);
					
				}

			}
			catch (Exception ex)
			{
				mLog.log.error(ex);
			}
			try
			{
				
				sleep(DelayTime * 60 * 1000);
			}
			catch (InterruptedException ex)
			{
				mLog.log.error(ex);
			}
		}
	}
}