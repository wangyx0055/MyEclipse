package servicesPkg;

import vmg.itrd.ws.MTSenderVMS;
import icom.Constants;
import icom.Sender;
import icom.common.ResultCode;
import icom.common.Util;


public class ExecuteSubChargeICOM extends Thread {

	private ChargeResultQueue rslQueue = null;
	String chargePacketTable = "vms_charge_packet_result";
	
	int processnum = 1;
	int processindex = 1;
	// private Logger logger = null;
	static int TIME_DELAY_LOAD_RESULT_CHARGE = 100;
	ServiceMng serviceMng = new ServiceMng();

	public ExecuteSubChargeICOM(ChargeResultQueue queue, int processnum, int processindex) {
		this.rslQueue = queue;
		this.processnum = processnum;
		this.processindex = processindex;

	}
	
	public ExecuteSubChargeICOM(){
		
	}

	@Override
	public void run() {
				
		Util.logger.info("LoadChargeResult - Start");
						
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
			// GET Info
			String mlistTable = serviceMng.getMlistTableName(rslInfo.getCommandCode());
			
			int handleResult = -1;
			if(rslInfo.getReslultCharge() == ResultCode.OK || 
					rslInfo.getReslultCharge() == ResultCode.NOK_ACCOUNT_NOT_FOUND){
												
				MlistInfo objInfo = serviceMng.convertChargeInfoToMlistInfo(rslInfo);													
				// Tim trong mlist_cancel => neu co thi delete ( danh cho DK lai )
				
				MlistInfo mlistSearch = serviceMng.getMlistInfoObject(mlistTable + "_cancel", rslInfo.getUserId(), 
						rslInfo.getServiceId(), rslInfo.getCommandCode());
				if(mlistSearch != null){ // delete
					objInfo.setRegCount(mlistSearch.getRegCount() + 1); // Update Regcount do dang ky lai
					serviceMng.deleteChargeResultByID(mlistSearch.getId(), mlistTable + "_cancel");					
				}
				
				// insert into mlist_service
				objInfo.setService(rslInfo.getCommandCode());
				handleResult = serviceMng.insertMlistCancelFromMlistInfo(mlistTable, objInfo);		// Insert To Mlist
				
				if (objInfo != null) {
					serviceMng.insertToMlistSubcriber(Constants.MLIST_SUBCRIBER, objInfo);
					// insert to list_send
					serviceMng.insertToListSend(objInfo, "0", Constants.PACKET_VMS);
					// ICOM => send result to VMS synchronize DB
					MTSenderVMS.insertMlist(mlistTable, objInfo);
				}				
			}else if (rslInfo.getReslultCharge() == ResultCode.NOK_NOT_ENOUGH_CREDIT
					|| rslInfo.getReslultCharge() == ResultCode.SUSPENDED){
				MlistInfo objInfo = serviceMng.convertChargeInfoToMlistInfo(rslInfo);													
				// Tim trong mlist_cancel => neu co thi delete ( danh cho DK lai )
				
				MlistInfo mlistSearch = serviceMng.getMlistInfoObject(mlistTable + "_cancel", rslInfo.getUserId(), 
						rslInfo.getServiceId(), rslInfo.getCommandCode());
				if(mlistSearch != null){ // delete
					objInfo.setRegCount(mlistSearch.getRegCount() + 1); // Update Regcount do dang ky lai
					serviceMng.deleteChargeResultByID(mlistSearch.getId(), mlistTable + "_cancel");					
				}
				
				// insert into mlist_service
				objInfo.setDateRetry(serviceMng.getDate(0));
				objInfo.setService(rslInfo.getCommandCode());
				handleResult = serviceMng.insertMlistCancelFromMlistInfo(mlistTable, objInfo);		// Insert To Mlist
				if(handleResult == 1){
					Util.logger.info("VMS@ DANG KY VOI TAI KHOAN HET TIEN:" +
							" INSERT INTO MLIST " + mlistTable+"+ SUCCESSFUL!,"+
							" USERID = " + objInfo.getUserId()+ ", ServiceID = " + objInfo.getServiceId());
				}else{
					Util.logger.info("VMS@ DANG KY VOI TAI KHOAN HET TIEN:" +
							" INSERT INTO MLIST " + mlistTable+"+ FAILURE!,"+
							" USERID = " + objInfo.getUserId()+ ", ServiceID = " + objInfo.getServiceId());
				}				
				if (objInfo != null) {
					serviceMng.insertToMlistSubcriber(Constants.MLIST_SUBCRIBER, objInfo);
					// ICOM => send result to VMS synchronize DB
					MTSenderVMS.insertMlist(mlistTable, objInfo);
				}				
				
			}		
			
			if(handleResult == 1){ // handle thanh cong => delete in vms_charge_result
				serviceMng.deleteChargeResultByID(rslInfo.getId(),chargePacketTable);
			}
			
		}
	}
}
