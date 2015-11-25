package tracuudiemthi;

import icom.MsgQueue;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

public class UpdateListSendDiemThi extends Thread {
	

	@Override
	public void run()
	{
		UpdateListSentObject msgObj=null;
		while(Sender.processData)
		{
			try
			{
				msgObj=(UpdateListSentObject)Sender.queueUpdate.remove();
				if(msgObj!=null)
				{
					// thuc hien update vao mlist vi du nhu
					if("1".equalsIgnoreCase(msgObj.getStatus())) {
						String sqlUpdateRunning = "update mlist_diemthi_dk set STATUS="+msgObj.getStatus()+", COUNT_SMS="+msgObj.getCount_sms()+", LAST_TIME=current_timestamp() where COMMAND_CODE='"+ msgObj.getCommand_code()+"' and USER_ID=" + msgObj.getUserid() + " and REQUEST_ID=" +msgObj.getRequestid();
						DBUtil.executeSQL("gateway", sqlUpdateRunning);
					} else if(msgObj.getResponse_status()==1) {
						String sqlUpdateRunning = "update mlist_diemthi_dk set RESPONSE_STATUS="+msgObj.getResponse_status()+",COUNT_SMS="+msgObj.getCount_sms()+",LAST_TIME=current_timestamp() where COMMAND_CODE='"+ msgObj.getCommand_code()+"' and USER_ID=" + msgObj.getUserid() + " and REQUEST_ID=" +msgObj.getRequestid();
						DBUtil.executeSQL("gateway", sqlUpdateRunning);
					}
				}
				else
				{
					sleep(10*1000);
					continue;
				}
			}
			catch(InterruptedException ex)
			{
				Util.logger.error(this.getClass().getName()+" error :"+ex.getMessage());
			}
			catch(Exception ex)
			{
				Util.logger.error(this.getClass().getName()+" error :"+ex.getMessage());
			}
			
		}
	}

}
