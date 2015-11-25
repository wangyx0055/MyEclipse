package vms.to.gamefarm.moqueue;

import icom.MsgObject;
public class MOQueueSenderGameFarm{
	public static int insertMOQueueGameFarm(MsgObject ems)
	{
		String currentDate = icom.common.Util.getCurrentDate();
		icom.common.Util.logger.info("insertMOQueueGameFarm@User_ID=" + ems.getUserid() + ",SERVICE_ID="+ ems.getServiceid() + ",MOBILE_OPERATOR=" + ems.getMobileoperator()
				+ ",COMMAND_CODE=" + ems.getCommandCode() + ",INFO=" + ems.getKeyword() + ",RECEIVE_DATE=" + currentDate+""
				+ ",RESPONDED=" + 0 + ",REQUEST_ID="+ems.getRequestid() +", CHANNEL_TYPE="+ ems.getChannelType());
		
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		try {
			 return receiver.insertMOQueueICOM(ems.getUserid(), ems.getServiceid(), ems.getMobileoperator()
					 , ems.getCommandCode(), ems.getKeyword(), currentDate, "0", ems.getRequestid() + "", ems.getChannelType() +"");	
				
		  }catch (Exception e) {
             e.printStackTrace();
         }
		  return -3;
	}
}
