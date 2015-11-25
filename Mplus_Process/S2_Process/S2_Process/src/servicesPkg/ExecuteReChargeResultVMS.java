package servicesPkg;

import ws.vmscharge.VMSChargeSender;
import icom.Constants;
import icom.Sender;
import icom.common.ResultCode;
import icom.common.Util;

public class ExecuteReChargeResultVMS extends Thread{
	
	private ChargeResultQueue rslQueue = null;
	String chargePacketTable = "recharge_packet_result";
	
	int processnum = 1;
	int processindex = 1;
	// private Logger logger = null;
	static int TIME_DELAY_LOAD_RESULT_CHARGE = 100;
	ServiceMng serviceMng = new ServiceMng();

	public ExecuteReChargeResultVMS(ChargeResultQueue queue, int processnum, int processindex) {
		this.rslQueue = queue;
		this.processnum = processnum;
		this.processindex = processindex;

	}
	
	public ExecuteReChargeResultVMS(){
		
	}

	@Override
	public void run() {
				
		Util.logger.info("VMS@ExecuteReChargeResult - Start");		
		
		while (Sender.getData) {
			rslQueue = new ChargeResultQueue();
			serviceMng.loadChargeResult(rslQueue,chargePacketTable);
			this.handleResultQueue(rslQueue);
			
			try {
				Thread.sleep(TIME_DELAY_LOAD_RESULT_CHARGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleResultQueue(ChargeResultQueue resultQueue){
		for(int i = 0;i< resultQueue.getSize(); i++){
			
			if(!Sender.getData) break;
			
			ChargeResultInfo rslInfo = resultQueue.getElement(i);
			
			int handleResult = 1;
			if(rslInfo.getIsThePacket() == 1){ // Not send to Other CP
				
				if(rslInfo.getReslultCharge() == ResultCode.OK || 
						rslInfo.getReslultCharge() == ResultCode.NOK_ACCOUNT_NOT_FOUND){
					handleResult = serviceMng.updateMlistDateRetry(rslInfo);
					Util.logger.info("ExecuteReChargeResultVMS@ ReCharge Successful with: "
							+ " userid = " + rslInfo.getUserId() + ", CommandCode = " 
							+ rslInfo.getCommandCode() + ", ServiceId = " + rslInfo.getServiceId());
					
					// Insert List_Send
					MlistInfo mlistObj = serviceMng.getMlistInfoObject(
							serviceMng.getMlistTableName(rslInfo.getCommandCode()),
							rslInfo.getUserId(), rslInfo.getServiceId(), rslInfo.getCommandCode());
					int test = serviceMng.insertToListSend(mlistObj,"0", Constants.PACKET_VMS);
					if(test == 1){
						Util.logger.info("ExecuteReChargeResultVMS@ Insert to List_Send SUCCESSFUL!" +
								" UserId = "+ mlistObj.getUserId() 
								+ " ;	ServiceId = "+ mlistObj.getServiceId()
								+ "	;	CommandCode = " + mlistObj.getServiceId());
					}else{
						Util.logger.info("ExecuteReChargeResultVMS@ Insert to List_Send SUCCESSFUL!" +
								" UserId = "+ mlistObj.getUserId() 
								+ " ;	ServiceId = "+ mlistObj.getServiceId()
								+ "	;	CommandCode = " + mlistObj.getServiceId());
					}
					
				}else{
					Util.logger.info("ExecuteReChargeResultVMS@ ReCharge NOT SUCCESS with: "
							+ " userid = " + rslInfo.getUserId() + ", CommandCode = " 
							+ rslInfo.getCommandCode() + ", ServiceId = " + rslInfo.getServiceId() 
							+ ", Result Charge = " + rslInfo.getReslultCharge());
				}

			}
			
			if(rslInfo.getIsThePacket()== -1){ 
				// send to ICOM								
				handleResult = VMSChargeSender.insertVmsReChargeResultPackage(rslInfo);
				if(handleResult == 1){
					Util.logger.info(" ExecuteReChargeResultVMS@SEND TO RECHARGE RESULT TO ICOM SUCCESSFUL" +
							": UserId = " +
							rslInfo.getUserId() + ", CommandCode = " 
							+ rslInfo.getCommandCode() + ", ServiceId = " + rslInfo.getServiceId() 
							+ ", Result Charge = " + rslInfo.getReslultCharge());
				}else{
					Util.logger.info("FAILURE: ExecuteReChargeResultVMS@SEND TO RECHARGE RESULT TO ICOM FAILURE" +
							": UserId = " +
							rslInfo.getUserId() + ", CommandCode = " 
							+ rslInfo.getCommandCode() + ", ServiceId = " + rslInfo.getServiceId() 
							+ ", Result Charge = " + rslInfo.getReslultCharge());
				}
			}
			
			if(handleResult == 1){ // handle thanh cong => delete in vms_charge_result
				serviceMng.deleteChargeResultByID(rslInfo.getId(),chargePacketTable);
			}
			
		}
	}

}
