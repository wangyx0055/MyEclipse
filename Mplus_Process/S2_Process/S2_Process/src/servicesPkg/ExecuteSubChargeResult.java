package servicesPkg;

import ws.vmscharge.VMSChargeSender;
import icom.Constants;
import icom.Keyword;
import icom.Sender;
import icom.common.ResultCode;
import icom.common.Util;

public class ExecuteSubChargeResult extends Thread {

	private ChargeResultQueue rslQueue = null;
	String chargePacketTable = "vms_charge_packet_result";

	int processnum = 1;
	int processindex = 1;
	// private Logger logger = null;
	static int TIME_DELAY_LOAD_RESULT_CHARGE = 100;
	ServiceMng serviceMng = new ServiceMng();
	static int SERVICE_TYPE_GIAOTHONG = 156;

	public ExecuteSubChargeResult(ChargeResultQueue queue, int processnum,
			int processindex) {
		this.rslQueue = queue;
		this.processnum = processnum;
		this.processindex = processindex;

	}

	public ExecuteSubChargeResult() {

	}

	@Override
	public void run() {

		Util.logger.info("LoadChargeResult - Start");

		while (Sender.getData) {
			rslQueue = new ChargeResultQueue();
			serviceMng.loadChargeResult(rslQueue, chargePacketTable);
			this.handleResultQueue(rslQueue);

			try {
				Thread.sleep(TIME_DELAY_LOAD_RESULT_CHARGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleResultQueue(ChargeResultQueue resultQueue) {
		for (int i = 0; i < resultQueue.getSize(); i++) {

			if (!Sender.getData)
				break;

			ChargeResultInfo rslInfo = resultQueue
					.getElement(i);
			Keyword keyword = Sender.loadconfig.getKeyword(rslInfo
					.getCommandCode().toUpperCase(), rslInfo.getServiceId());
			
			int serviceType = keyword.getService_type();
			String serviceName = keyword.getService_ss_id();
			
			String mlistTable = serviceMng.getMlistTableName(rslInfo
					.getCommandCode());
			int handleResult = -1;

			// --- START GET MESSAGE RESPONSE
			if (rslInfo.getReslultCharge() == ResultCode.OK
					|| rslInfo.getReslultCharge() == ResultCode.NOK_ACCOUNT_NOT_FOUND) {
				
				if(rslInfo.getDayNumber() == 30){
					rslInfo.setInfo(keyword.getSubMsg());
				}else{
					String strContent = serviceMng.getContentPacketAmount(rslInfo.getCommandCode(), rslInfo.getAmount());
					rslInfo.setInfo(strContent);
				}
				
				if(serviceType == SERVICE_TYPE_GIAOTHONG){
					String content = phase3.GTContentMng.getContent(serviceName);
					rslInfo.setInfo(content);
				}
												
			} else if (rslInfo.getReslultCharge() == ResultCode.NOK_NOT_ENOUGH_CREDIT
					|| rslInfo.getReslultCharge() == ResultCode.SUSPENDED) {
				rslInfo.setInfo(keyword.getNotEnoughMoneyMsg());
			} else {
				rslInfo.setInfo(keyword.getErrMsg());
			}
			// --- END GET MESSAGE RESPONSE
						

			if (rslInfo.getIsThePacket() == 1 && serviceType != SERVICE_TYPE_GIAOTHONG) { // Not send to Other CP
				if (rslInfo.getReslultCharge() == ResultCode.OK
						|| rslInfo.getReslultCharge() == ResultCode.NOK_ACCOUNT_NOT_FOUND) {
					MlistInfo objInfo = 
						serviceMng.convertChargeInfoToMlistInfo(rslInfo);
					// Tim trong mlist_cancel => neu co thi delete
					MlistInfo mlistSearch = serviceMng.getMlistInfoObject(
							mlistTable + "_cancel", rslInfo.getUserId(),
							rslInfo.getServiceId(), rslInfo.getCommandCode());
					if (mlistSearch != null) { // delete
						// Update Regcount do dang ky lai 
						objInfo.setRegCount(mlistSearch.getRegCount() + 1); 
						
						// Delete in mlist_cancel
						serviceMng.deleteChargeResultByID(mlistSearch.getId(),
								mlistTable + "_cancel");
					}
					
					// Update max Amount
					objInfo.setAmount(Integer.parseInt(String.valueOf(keyword.getAmount())));
					objInfo.setService(rslInfo.getCommandCode());
					// insert into mlist_service
					handleResult = serviceMng.insertMlistCancelFromMlistInfo(
							mlistTable, objInfo); // Insert To Mlist										
					
					if (objInfo != null) {
						serviceMng.insertToMlistSubcriber(
								Constants.MLIST_SUBCRIBER, objInfo);
						// insert to list_send
						serviceMng.insertToListSend(objInfo, "0", Constants.PACKET_VMS);
					}

				}else if(rslInfo.getReslultCharge() == ResultCode.NOK_NOT_ENOUGH_CREDIT
						|| rslInfo.getReslultCharge() == ResultCode.SUSPENDED){
					
					MlistInfo objInfo = 
						serviceMng.convertChargeInfoToMlistInfo(rslInfo);
					// Tim trong mlist_cancel => neu co thi delete
					MlistInfo mlistSearch = serviceMng.getMlistInfoObject(
							mlistTable + "_cancel", rslInfo.getUserId(),
							rslInfo.getServiceId(), rslInfo.getCommandCode());
					if (mlistSearch != null) { // delete
						// Update Regcount do dang ky lai 
						objInfo.setRegCount(mlistSearch.getRegCount() + 1); 
						
						// Delete in mlist_cancel
						serviceMng.deleteChargeResultByID(mlistSearch.getId(),
								mlistTable + "_cancel");
					}
					
					// Update max Amount
					objInfo.setAmount(Integer.parseInt(String.valueOf(keyword.getAmount())));
					
					// insert into mlist_service with date_retry = today
					objInfo.dateRetry = serviceMng.getDate(0);
					objInfo.setService(rslInfo.getCommandCode());
					handleResult = serviceMng.insertMlistCancelFromMlistInfo(
							mlistTable, objInfo); // Insert To Mlist
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
						serviceMng.insertToMlistSubcriber(
								Constants.MLIST_SUBCRIBER, objInfo);
						// no insert to list_send
					}
				}
			}

			handleResult = serviceMng.sendMT(rslInfo);

			if (rslInfo.getIsThePacket() == -1) {
				if (rslInfo.getReslultCharge() == ResultCode.OK
						|| rslInfo.getReslultCharge() == ResultCode.NOK_ACCOUNT_NOT_FOUND) {
					// Update Max Amount
					rslInfo.setAmount(Integer.parseInt(String.valueOf(keyword.getAmount())));
					// Send to ICOM voi truong hop dang ky thanh cong.
					// Send to ICOM and with isThePacket = -1 and Max Amount
					handleResult = VMSChargeSender
							.insertVmsChargeResultPackage(rslInfo);
					Util.logger.info("insertVmsChargeResultPackage@ SEND TO ICOM VOI TRUONG HOP" +
							"CHARGE DANG KY THANH CONG Result_Send = "
							+ handleResult + " , IS_THE_PACKET = "
							+ rslInfo.getIsThePacket() + " , Day Number = "
							+ rslInfo.getDayNumber());
				}
			}

			if (handleResult == 1) { // handle thanh cong => delete in
										// vms_charge_result
				serviceMng.deleteChargeResultByID(rslInfo.getId(),
						chargePacketTable);
			}

		}
	}
}
