package icom;

import icom.common.Util;
import DAO.MTDAO;
import DAO.MoQueueDAO;

public class MOHandleSuperSim {
	
	public static void handle(MsgObject message)
	{
		try
		{
			// thuc hien search trong bang mo_queue9222
			MsgObject msg=MoQueueDAO.getMOQueue9222(message.getUserid());
			if(msg!=null)
			{					  		
				MoQueueDAO.writeToMoQueue(msg,Constants.tblMOQueue);				
			}
			else
			{
				// thuc hien tra tin nhan sai cu phap
				message.setUsertext(Constants.MESSAGENOTINFOTOCONFIRM);
				MTDAO.sendMT(message);
			}
			
		}
		catch(Exception ex)
		{
			Util.logger.error("MOHandleSuperSim "+" error: "+ex.getMessage());
		}
	}

}
