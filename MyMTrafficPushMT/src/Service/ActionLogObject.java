package Service;

import java.sql.Timestamp;

import Service.ActionLog.ActionLogType;

public class ActionLogObject
{
	public Integer ServiceID = 0;
	public String MSISDN = "";
	public Timestamp LogDate = null;
	
	public ActionLog.ActionLogType mActionType = ActionLogType.Default;
	
	public String LogContent="";
	public String MO="";
	public String MT="";
	public String RequestID="";
	public Integer LogPID = 0;
	
	public boolean IsNull()
	{
		if (MSISDN == "")
			return true;
		else
			return false;
	}
}
