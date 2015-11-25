package MyGateway;

import java.sql.SQLException;

import MyConnection.*;

public class SMSSendQueue
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public SMSSendQueue() throws Exception
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

	public SMSSendQueue(String PoolName) throws Exception
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

	public boolean Insert(String idseq, String USER_ID, String SERVICE_ID, String MOBILE_OPERATOR, String COMMAND_CODE, String INFO, String SUBMIT_DATE,
			String DONE_DATE, String PROCESS_RESULT, String MESSAGE_TYPE, String REQUEST_ID, String MESSAGE_ID, String CONTENT_TYPE, String CPID)
			throws Exception
	{
		String Query = "";
		try
		{
			String Format_Query = "INSERT INTO ems_send_queue" + idseq + "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,"
					+ " MESSAGE_ID, CONTENT_TYPE,CPID) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s','%s')";

			Object[] arr_value = { USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID };

			Query = String.format(Format_Query, arr_value);

			return mExec.Execute_Query(Query);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public Boolean Insert(String USER_ID, String SERVICE_ID, String MOBILE_OPERATOR, String COMMAND_CODE, String CONTENT_TYPE, String INFO,
			String PROCESS_RESULT, String MESSAGE_TYPE, String REQUEST_ID, String MESSAGE_ID, String TOTAL_SEGMENTS, String RETRIES_NUM, String CPId)
			throws Exception, SQLException
	{
		try
		{
			String Format = "	INSERT INTO ems_send_queue(USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,CONTENT_TYPE,INFO,PROCESS_RESULT,MESSAGE_TYPE,REQUEST_ID,MESSAGE_ID,TOTAL_SEGMENTS,RETRIES_NUM,CPId) "
					+ " 	VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')";

			Object[] arr_value = { USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,
					MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM, CPId };

			String Query = String.format(Format, arr_value);

			return mExec.Execute_Query(Query);

		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public Boolean Insert_VMS(String USER_ID, String SERVICE_ID, String MOBILE_OPERATOR, String COMMAND_CODE, String CONTENT_TYPE, String INFO,
			String PROCESS_RESULT, String MESSAGE_TYPE, String REQUEST_ID, String MESSAGE_ID, String TOTAL_SEGMENTS, String RETRIES_NUM, String CPId,
			String VMS_SVID) throws Exception, SQLException
	{
		try
		{
			String Format = "	INSERT INTO ems_send_queue(USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,CONTENT_TYPE,INFO,PROCESS_RESULT,MESSAGE_TYPE,REQUEST_ID,MESSAGE_ID,TOTAL_SEGMENTS,RETRIES_NUM,CPId,VMS_SVID) "
					+ " 	VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')";

			Object[] arr_value = { USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,
					MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM, CPId, VMS_SVID };

			String Query = String.format(Format, arr_value);

			return mExec.Execute_Query(Query);

		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

}
