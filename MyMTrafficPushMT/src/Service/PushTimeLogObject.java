package Service;

import java.sql.Timestamp;

import Service.ActionLog.ActionLogType;

public class PushTimeLogObject
{
	public Integer LogID = 0;
	public Integer ProcessIndex = 0;
	public Integer ProcessNumber = 0;
	public Integer ServiceID = 0;
	public Integer NewsID = 0;
	public String PushTime="";
	
	public Timestamp CreateDate = null;
	public Timestamp FinishDate = null;
	public Integer SuccessNumber =0;
	public Integer FailNumber = 0;
	
	public ActionLog.ActionLogType mActionType = ActionLogType.Default;
		
	public boolean IsNull()
	{
		if (ServiceID == 0 || NewsID == 0 || PushTime == "")
			return true;
		else
			return false;
	}

}
