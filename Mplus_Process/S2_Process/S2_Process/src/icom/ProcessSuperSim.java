package icom;

import DAO.MTDAO;
import DAO.MoQueueDAO;
import icom.common.Util;

public class ProcessSuperSim extends Thread {
	MsgQueue queue;
	public ProcessSuperSim(int processIndex,int processNum,MsgQueue queue)
	{
		this.queue=queue;
	}
	@Override
	public void run()
	{
		MsgObject msgObj=null;
		while(Sender.getData)
		{			
		try
		{
			msgObj=(MsgObject)queue.remove();
			if(msgObj!=null)
			{
				// thuc hien send mt
				Keyword keyword = Sender.loadconfig.getKeyword(msgObj.getCommandCode()
						.toUpperCase(), msgObj.getServiceid());
				if(keyword!=null)
				{
					if(!"".equals(keyword.getMtSupersim()) && keyword.getMtSupersim()!=null) 
					{
					msgObj.setUsertext(keyword.getMtSupersim());
					// thuc hien send mt cho khach hang
					MTDAO.sendMT(msgObj);
					// thuc hien day vao table moqueue_approve
					MoQueueDAO.writeToMoQueue(msgObj, Constants.TABLENAME_MOQUEUEAPPROVE);
					}
				}
				// thuc hien ghi log mo
				MoQueueDAO.add2molog(msgObj);
			}
			else
			{
				sleep(30*1000);
				continue;
			}
			
			sleep(30*1000);
		}
		catch(InterruptedException ex)
		{
		Util.logger.error(this.getClass().getName()+" error: "+ex.getMessage());	
		}
		}
	}

}
