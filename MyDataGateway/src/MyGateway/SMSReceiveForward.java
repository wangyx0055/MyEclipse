package MyGateway;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;
import MyUtility.MyLogger;

public class SMSReceiveForward
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	public MyExecuteData mExec;
	public MyGetData mGet;

	public SMSReceiveForward() throws Exception
	{
		try
		{
			mExec = new MyExecuteData();
			mGet = new MyGetData();
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public SMSReceiveForward(String PoolName) throws Exception
	{
		try
		{
			mExec = new MyExecuteData(PoolName);
			mGet = new MyGetData(PoolName);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * lấy dữ liệu table sms_receive_forward (table dùng để lưu các thông tin MO
	 * đã forward cho đối tác
	 * 
	 * @param Type
	 *            : Type = 1: lấy dữ liệu theo RequestID = Para_1
	 * @param Para_1
	 * @return
	 * @throws Exception
	 */
	public MyTableModel Select(int Type, String Para_1) throws Exception
	{

		String Query = "";
		try
		{
			if (Type == 1) // Lấy dữ liệu theo RequestID
			{
				Query = " SELECT * FROM sms_receive_forward WHERE REQUEST_ID = '" + Para_1 + "'";
			}
			mLog.log.info("-------------Query check RequestID: " + Query);
			return mGet.GetData_Query(Query);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public MyTableModel Select(String ServiceID, String UserID, String RequestID) throws Exception
	{

		String Query = "";
		try
		{
			Query = " SELECT * FROM sms_receive_forward WHERE SERVICE_ID ='" + ServiceID + "' AND USER_ID ='" + UserID + "' AND (REQUEST_ID = '" + RequestID
					+ "' OR NOTES = '"+RequestID+"') ";

			mLog.log.info("-------------Query check RequestID: " + Query);
			return mGet.GetData_Query(Query);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public Boolean Insert(String USER_ID, String SERVICE_ID, String MOBILE_OPERATOR, String COMMAND_CODE, String INFO, String insert_date, String receive_date,
			String RESPONDED, String REQUEST_ID, String NOTES, String CPId, String SPAM, int Status) throws Exception
	{
		try
		{
			String Query = " INSERT INTO sms_receive_forward (USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,insert_date,receive_date,RESPONDED,REQUEST_ID,NOTES,CPId,SPAM,Status) "
					+ " VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')";
			Query = String.format(Query, USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, insert_date, receive_date, RESPONDED, REQUEST_ID, NOTES,
					CPId, SPAM, Status);

			return mExec.Execute_Query(Query);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	/**
	 * Xóa dữ liệu theo RequestID
	 * 
	 * @param RequestID
	 * @return
	 * @throws Exception
	 */
	public Boolean Delete(String RequestID) throws Exception
	{
		String Query = "DELETE FROM sms_receive_forward WHERE REQUEST_ID= '" + RequestID + "' OR NOTES = '" + RequestID + "'";
		try
		{
			return mExec.Execute_Query(Query);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

}
