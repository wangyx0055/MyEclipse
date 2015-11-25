package ws.vmscharge;

import servicesPkg.ChargeResultInfo;
import icom.MsgObject;


public class VMSChargeSender {
	public static int SendToVmsCharge(MsgObject ems, String timedelivery)
	{	
		icom.common.Util.logger.info("icom@SendToVmsCharge@User_ID=" + ems.getUserid() + ",SERVICE_ID="+ ems.getServiceid()				
				+ ",COMMAND_CODE=" + ems.getCommandCode() + ",REQUEST_ID=" + ems.getRequestid() + ",MESSAGE_ID=" + ems.getMsg_id()+""
				+ ",SERVICE_NAME=" + ems.getServiceName() + ",CHANNEL_TYPE="+ems.getChannelType() +", CONTENT_ID="+ ems.getContentId()
				+",AMOUNT=" +  ems.getAmount() + ",TIME_DELIVERY=" +  timedelivery + ",COMPANY_ID=" + ems.getCompany_id() + ",IS_THE_SEND=-1" 
				+ ",LAST_CODE=" + ems.getLast_code() +",OPTIONS=" + ems.getOption() );
		
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
			 return receiver.mtReceiver(ems.getUserid(), ems.getServiceid(), ems.getCommandCode()
					 , ems.getMsgtype() +"", ems.getRequestid() +"", ems.getMsg_id() +"", ems.getServiceName()
					 , ems.getChannelType() +"", ems.getContentId() +"", ems.getAmount() +"", timedelivery +""
					 , ems.getCompany_id() +"", "-1", ems.getLast_code(), ems.getOption());	
				
		  }catch (Exception e) {
             e.printStackTrace();
         }
		return -3;
	}
	
	// DandND add
    public static int insertVmsChargeResultPackage(ChargeResultInfo objInfo){
    	
    	//TODO: DanNd
		String currentDate = icom.common.Util.getCurrentDate();	
		icom.common.Util.logger.info("insertVmsChargePackage@ SEND VMS_CHARGE_PACKET_RESULT TO ICOM: User_ID=" + objInfo.getUserId() 
				+ ",SERVICE_ID="+ objInfo.getServiceId() + ",MOBILE_OPERATOR=" 
				+ objInfo.getMobileOperator()
				+ ",COMMAND_CODE=" + objInfo.getCommandCode() + ",CONTENT_TYPE=" 
				+ objInfo.getContentType() + ",INFO=" + objInfo.getInfo() + ",SUBMIT_DATE=" + currentDate+""
				+ ",DONE_DATE=" +  currentDate + ",PROCESS_RESULT="
				+ objInfo.getProcessResult() +", MESSAGE_TYPE="+ objInfo.getMsgType()
				+ ",REQUEST_ID="+ objInfo.getRequestID() + ",MESSAGE_ID=" 
				+ objInfo.getMsgID()+ ",TOTAL_SEGMENTS=1"+ ",RETRIES_NUM=0" 
				+ ",INSERT_DATE=" + currentDate + ",SERVICE_NAME="
				+objInfo.getServiceName() + ",CHANNEL_TYPE=" 
				+ objInfo.getChannelType()+ ",CONTENT_ID=" 
				+ objInfo.getContendID() +",AMOUNT="+objInfo.getAmount() + ", IS_THE_PACKET = " + objInfo.getIsThePacket());		
			
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
		return receiver.insertVmsChargeResultPackage(objInfo.getUserId(), objInfo.getServiceId(), objInfo.getMobileOperator()
				, objInfo.getCommandCode(), objInfo.getContentType() +"", objInfo.getInfo(), currentDate +"", currentDate+""
				, objInfo.getProcessResult()+"", objInfo.getMsgType() + "", objInfo.getRequestID() + "", objInfo.getMsgID() + "", "1"
				, "0", currentDate +"", "", objInfo.getServiceName(), objInfo.getChannelType() + "", objInfo.getContendID()+"", 
				objInfo.getAmount() + "", objInfo.getDayNumber()+"", objInfo.getReslultCharge()+"", objInfo.getIsThePacket()+"");		
		  }catch (Exception e) {
              e.printStackTrace();
          }
	
    	return -3;
    }
    
    //DanNd Add
    public static int insertVmsReChargeResultPackage(ChargeResultInfo objInfo){
    	
    	//TODO: DanNd
		String currentDate = icom.common.Util.getCurrentDate();	
		icom.common.Util.logger.info("insertVmsReChargePackage@ SEND RECHARGE_PACKET_RESULT TO ICOM: User_ID=" + objInfo.getUserId() + ",SERVICE_ID="+ objInfo.getServiceId() + ",MOBILE_OPERATOR=" + objInfo.getMobileOperator()
				+ ",COMMAND_CODE=" + objInfo.getCommandCode() + ",CONTENT_TYPE=" + objInfo.getContentType() + ",INFO=" + objInfo.getInfo() + ",SUBMIT_DATE=" + currentDate+""
				+ ",DONE_DATE=" +  currentDate + ",PROCESS_RESULT="+objInfo.getProcessResult() +", MESSAGE_TYPE="+ objInfo.getMsgType()
				+ ",REQUEST_ID="+ objInfo.getRequestID() + ",MESSAGE_ID=" + objInfo.getMsgID()+ ",TOTAL_SEGMENTS=1"+ ",RETRIES_NUM=0" 
				+ ",INSERT_DATE=" + currentDate + ",SERVICE_NAME="+objInfo.getServiceName() + ",CHANNEL_TYPE=" + objInfo.getChannelType()+ 
				",CONTENT_ID=" + objInfo.getContendID() +",AMOUNT="+objInfo.getAmount() +  ", IS_THE_PACKET = " + objInfo.getIsThePacket());		
			
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
		return receiver.insertVmsChargeResultPackage(objInfo.getUserId(), objInfo.getServiceId(), objInfo.getMobileOperator()
				, objInfo.getCommandCode(), objInfo.getContentType() +"", objInfo.getInfo(), currentDate +"", currentDate+""
				, objInfo.getProcessResult()+"", objInfo.getMsgType() + "", objInfo.getRequestID() + "", objInfo.getMsgID() + "", "1"
				, "0", currentDate +"", "", objInfo.getServiceName(), objInfo.getChannelType() + "", objInfo.getContendID()+"", 
				objInfo.getAmount() + "", objInfo.getDayNumber()+"", objInfo.getReslultCharge()+"", objInfo.getIsThePacket()+"");		
		  }catch (Exception e) {
              e.printStackTrace();
          }
	
    	return -3;
    }

}
