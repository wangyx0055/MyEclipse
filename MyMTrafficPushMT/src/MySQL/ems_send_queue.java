package MySQL;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import MyConn.*;

public class ems_send_queue
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public ems_send_queue() throws Exception
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

	public ems_send_queue(String PoolName) throws Exception
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

	public boolean Insert( String USER_ID, String SERVICE_ID,  String COMMAND_CODE, String INFO, String REQUEST_ID )
			throws Exception
	{

		try
		{
			String MOBILE_OPERATOR = "GPC";
			String PROCESS_RESULT ="0";
			String MESSAGE_TYPE = "2"; //2 la khong tru tien
			String MESSAGE_ID ="1";
			String CPID = "0";
			String CONTENT_TYPE ="21";
		
			String Format_Query = "INSERT INTO ems_send_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,"
					+ " MESSAGE_ID, CONTENT_TYPE,CPID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			String strDoneDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			String strSubmitDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			
			Object[] arr_value = { USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, strSubmitDate, strDoneDate, PROCESS_RESULT, MESSAGE_TYPE,
					REQUEST_ID, MESSAGE_ID, CONTENT_TYPE, CPID };

			return mExec.Execute_Query(Format_Query, arr_value);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}


