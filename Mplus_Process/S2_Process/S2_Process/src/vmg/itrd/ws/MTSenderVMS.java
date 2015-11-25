package vmg.itrd.ws;

import servicesPkg.ChargeResultInfo;
import servicesPkg.MlistInfo;
import icom.MsgObject;

public class MTSenderVMS {
	public static int insertMTQueueVMS(MsgObject ems)
	{
		//TODO: PhuongDT
		//if(1==1)return 1;
		String currentDate = icom.common.Util.getCurrentDate();
		icom.common.Util.logger.info("InsertMTQueueVMS@User_ID=" + ems.getUserid() + ",SERVICE_ID="+ ems.getServiceid() + ",MOBILE_OPERATOR=" + ems.getMobileoperator()
				+ ",COMMAND_CODE=" + ems.getCommandCode() + ",CONTENT_TYPE=" + ems.getContenttype() + ",INFO=" + ems.getUsertext() + ",SUBMIT_DATE=" + currentDate+""
				+ ",DONE_DATE=" + currentDate + ",PROCESS_RESULT="+ems.getProcess_result() +", MESSAGE_TYPE="+ ems.getMsgtype()
				+ ",REQUEST_ID="+ ems.getRequestid() + ",MESSAGE_ID=" + ems.getMsg_id()+ ",TOTAL_SEGMENTS=1"+ ",RETRIES_NUM=0" 
				+ ",INSERT_DATE=" + currentDate + ",AMOUNT=" + ems.getAmount() + ",CHANNEL_TYPE=" +ems.getChannelType());		
				
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
			return receiver.insertMTQueueVMS(ems.getUserid(), ems.getServiceid(), ems.getMobileoperator()
					, ems.getCommandCode(), ems.getContenttype()+"", ems.getUsertext(), currentDate 
					,currentDate, ems.getProcess_result()+"", ems.getMsgtype()+"",ems.getRequestid() +""
					, ems.getMsg_id() +"", "1", "0", currentDate
					, "", ems.getAmount() + "", ems.getChannelType() +"");
		  }catch (Exception e) {
              e.printStackTrace();
          }
		  return -3;
	}
	public static int insertVMSChargeOnline(MsgObject ems)
	{
		//TODO: PhuongDT
		//if(1==1) return 1;
		String currentDate = icom.common.Util.getCurrentDate();	
		icom.common.Util.logger.info("insertVMSChargeOnline@User_ID=" + ems.getUserid() + ",SERVICE_ID="+ ems.getServiceid() + ",MOBILE_OPERATOR=" + ems.getMobileoperator()
				+ ",COMMAND_CODE=" + ems.getKeyword() + ",CONTENT_TYPE=" + ems.getContenttype() + ",INFO=" + ems.getUsertext() + ",SUBMIT_DATE=" + currentDate+""
				+ ",DONE_DATE=" +  currentDate + ",PROCESS_RESULT="+ems.getProcess_result() +", MESSAGE_TYPE="+ ems.getMsgtype()
				+ ",REQUEST_ID="+ ems.getRequestid() + ",MESSAGE_ID=" + ems.getMsg_id()+ ",TOTAL_SEGMENTS=1"+ ",RETRIES_NUM=0" 
				+ ",INSERT_DATE=" + currentDate + ",SERVICE_NAME="+ems.getServiceName() 
				+ ",CHANNEL_TYPE=" + ems.getChannelType()+ ",CONTENT_ID=" 
				+ ems.getContentId() +",AMOUNT="+ems.getAmount());		
			
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
		return receiver.insertVMSChargeOnline(ems.getUserid(), ems.getServiceid(), ems.getMobileoperator()
				, ems.getKeyword(), ems.getContenttype() +"", ems.getUsertext(), currentDate +"", currentDate+""
				, ems.getProcess_result()+"", ems.getMsgtype() + "", ems.getRequestid() + "", ems.getMsg_id() + "", "1"
				, "0", currentDate +"", "", ems.getServiceName(), ems.getChannelType() + "", ems.getContentId()+"", ems.getAmount() + "");		
		  }catch (Exception e) {
              e.printStackTrace();
          }
		  return -3;
	}
	
	// DandND add
    public static int insertVmsChargePackage(ChargeResultInfo objInfo){
    	
    	//TODO: DanNd
		String currentDate = objInfo.getDoneDate();	
		icom.common.Util.logger.info("insertVmsChargePackage@User_ID=" + objInfo.getUserId() 
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
				+ objInfo.getContendID() +",AMOUNT="+objInfo.getAmount() + ", IS_THE_PACKET = -1");		
			
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
		return receiver.insertVmsChargePackage(objInfo.getUserId(), objInfo.getServiceId(), objInfo.getMobileOperator()
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
    public static int insertVmsReChargePackage(ChargeResultInfo objInfo){
    	
    	//TODO: DanNd
		String currentDate = objInfo.getDoneDate();	
		icom.common.Util.logger.info("insertVmsReChargePackage@User_ID=" + objInfo.getUserId() + ",SERVICE_ID="+ objInfo.getServiceId() + ",MOBILE_OPERATOR=" + objInfo.getMobileOperator()
				+ ",COMMAND_CODE=" + objInfo.getCommandCode() + ",CONTENT_TYPE=" + objInfo.getContentType() + ",INFO=" + objInfo.getInfo() + ",SUBMIT_DATE=" + currentDate+""
				+ ",DONE_DATE=" +  currentDate + ",PROCESS_RESULT="+objInfo.getProcessResult() +", MESSAGE_TYPE="+ objInfo.getMsgType()
				+ ",REQUEST_ID="+ objInfo.getRequestID() + ",MESSAGE_ID=" + objInfo.getMsgID()+ ",TOTAL_SEGMENTS=1"+ ",RETRIES_NUM=0" 
				+ ",INSERT_DATE=" + currentDate + ",SERVICE_NAME="+objInfo.getServiceName() + ",CHANNEL_TYPE=" + objInfo.getChannelType()+ 
				",CONTENT_ID=" + objInfo.getContendID() +",AMOUNT="+objInfo.getAmount() +  ", IS_THE_PACKET = -1");		
			
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
		return receiver.insertVmsReChargePackage(objInfo.getUserId(), objInfo.getServiceId(), objInfo.getMobileOperator()
				, objInfo.getCommandCode(), objInfo.getContentType() +"", objInfo.getInfo(), currentDate +"", currentDate+""
				, objInfo.getProcessResult()+"", objInfo.getMsgType() + "", objInfo.getRequestID() + "", objInfo.getMsgID() + "", "1"
				, "0", currentDate +"", "", objInfo.getServiceName(), objInfo.getChannelType() + "", objInfo.getContendID()+"", 
				objInfo.getAmount() + "", objInfo.getDayNumber()+"", objInfo.getReslultCharge()+"", objInfo.getIsThePacket()+"");		
		  }catch (Exception e) {
              e.printStackTrace();
          }
	
    	return -3;
    }
    
//    public static int deleteInTable(String tableName, String userId,
//			String commandCode, String serviceId) {
//		icom.common.Util.logger.info("deleteInTable@tableName = " + tableName+" ,User_ID=" + userId + ",SERVICE_ID="+ serviceId
//				+ ",COMMAND_CODE=" + commandCode);
//			
//		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
//		 try {
//		return receiver.deleteInTable(tableName, userId, commandCode, serviceId);
//		  }catch (Exception e) {
//              e.printStackTrace();
//          }
//	
//    	return -3;
//	}

	public static int insertMlist(String mlistTable, MlistInfo objInfo ) {
		//TODO: DanNd
		String currentDate = objInfo.getAutoTimeStamps().toString();	
		icom.common.Util.logger.info("insertMlist@ mlistatble = "+ mlistTable+ " ,User_ID=" + objInfo.getUserId() + ",SERVICE_ID="+ objInfo.getServiceId() + ",MOBILE_OPERATOR=" + objInfo.getMobiOperator()
				+ ",COMMAND_CODE=" + objInfo.getCommandCode() + ",CONTENT_TYPE=" + objInfo.getContentId() + ",SUBMIT_DATE=" + currentDate+""
				+ ",DONE_DATE=" +  currentDate + ", MESSAGE_TYPE="+ objInfo.getMessageType()
				+ ",REQUEST_ID="+ objInfo.getRequestId()
				+ ",INSERT_DATE=" + currentDate + ",SERVICE_NAME="+objInfo.getService() + ",CHANNEL_TYPE=" + objInfo.getChanelType()+ 
				",CONTENT_ID=" + objInfo.getContentId() +",AMOUNT="+objInfo.getAmount() +  ", IS_THE_PACKET = -1");		
			
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
		return receiver.insertMlist(mlistTable, 
				objInfo.getUserId(), objInfo.getServiceId(), objInfo.getToday(), objInfo.getOptions(), objInfo.getFailures()
				, objInfo.getLastCode(), String.valueOf(objInfo.getAutoTimeStamps())
				, objInfo.getCommandCode(),
				objInfo.getRequestId(),objInfo.getMessageType() + "", objInfo.getMobiOperator(),
				objInfo.getMtCount() + "", objInfo.getMtFree() + "", objInfo.getDuration() + "", 
				objInfo.getAmount() + "", objInfo.getContentId() + "", objInfo.getService(),
				objInfo.getCompanyId(), objInfo.getActive() + "", objInfo.getChanelType() + "",
				objInfo.getRegCount() + "", objInfo.getDateRetry());
		  }catch (Exception e) {
              e.printStackTrace();
          }
	
    	return -3;
	}

	public static int insertToMlistFromMlist(String toMlistTable,
			String fromMlistTable, String registry_Again, String userId,
			String commandCode, String serviceId)  {
		
		//TODO: DanNd
		icom.common.Util.logger.info("insertToMlistFromMlist@toMlistTable = "+toMlistTable +
				", fromMlist = "+ fromMlistTable+"User_ID=" + userId + 
				", commandCode = "+ commandCode + ", ServiceId = " + serviceId + " , Registry_Again = " + registry_Again);		
			
		ReceiverSoapBindingImpl receiver = new ReceiverSoapBindingImpl();	
		 try {
			 return receiver.insertToMlistFromMlist(toMlistTable, fromMlistTable, registry_Again, userId, commandCode, serviceId);
		  }catch (Exception e) {
              e.printStackTrace();
          }
	
    	return -3;
		
	}



}
