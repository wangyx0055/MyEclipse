package servicesPkg;

import vmg.itrd.ws.MTSenderVMS;
import icom.Constants;
import icom.Sender;
import icom.common.ResultCode;
import icom.common.Util;

public class ExecuteReChargeResultICOM extends Thread {

	private ChargeResultQueue rslQueue = null;
	String chargePacketTable = "recharge_packet_result";

	int processnum = 1;
	int processindex = 1;
	// private Logger logger = null;
	static int TIME_DELAY_LOAD_RESULT_CHARGE = 100;
	ServiceMng serviceMng = new ServiceMng();

	public ExecuteReChargeResultICOM(ChargeResultQueue queue, int processnum,
			int processindex) {
		this.rslQueue = queue;
		this.processnum = processnum;
		this.processindex = processindex;

	}

	public ExecuteReChargeResultICOM() {

	}

	@Override
	public void run() {

		Util.logger.info("ICOM - ExecuteReChargeResult - Start");

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

			int handleResult = -1;

			if (rslInfo.getReslultCharge() == ResultCode.OK
					|| rslInfo.getReslultCharge() == ResultCode.NOK_ACCOUNT_NOT_FOUND) {
				// Update date Retry in mlist_dv
				handleResult = serviceMng.updateMlistDateRetry(rslInfo);
				Util.logger
						.info("ICOM ExecuteReChargeResultICOM@ ReCharge Successful with: "
								+ " userid = "
								+ rslInfo.getUserId()
								+ ", CommandCode = "
								+ rslInfo.getCommandCode()
								+ ", ServiceId = " + rslInfo.getServiceId() 
								+ " , DAY_NUMB = " + rslInfo.getDayNumber()
								+ " , RESULT_CHARGE = " + rslInfo.getReslultCharge());

				// Synchronized DB with VMS
				String mlistTable = serviceMng.getMlistTableName(rslInfo
						.getCommandCode());
				MlistInfo objInfo = serviceMng.getMlistInfoObject(mlistTable,
						rslInfo.getUserId(), rslInfo.getServiceId(), rslInfo
								.getCommandCode());

				handleResult = MTSenderVMS.insertMlist(mlistTable, objInfo);
				
				// Insert to ListSend			
				int test = serviceMng.insertToListSend(objInfo,"0", Constants.PACKET_VMS);
				if(test == 1){
					Util.logger.info("ExecuteReChargeResultICOM@ Insert to List_Send SUCCESSFUL!" +
							" UserId = "+ objInfo.getUserId() 
							+ " ;	ServiceId = "+ objInfo.getServiceId()
							+ "	;	CommandCode = " + objInfo.getServiceId());
				}else{
					Util.logger.info("ExecuteReChargeResultVMS@ Insert to List_Send SUCCESSFUL!" +
							" UserId = "+ objInfo.getUserId() 
							+ " ;	ServiceId = "+ objInfo.getServiceId()
							+ "	;	CommandCode = " + objInfo.getServiceId());
				}				
				
				if (handleResult == 1) {
					Util.logger.info("ICOM ExecuteReChargeResultICOM@SEND TO VMS to synchronized DB MLIST SUCCESSFUL"
									+ " with: UserId = "
									+ rslInfo.getUserId()
									+ ", CommandCode = "
									+ rslInfo.getCommandCode()
									+ ", ServiceId = "
									+ rslInfo.getServiceId()
									+ ", Result Charge = "
									+ rslInfo.getReslultCharge()
									+ ", Result Send WS = " + handleResult);
				}else{
					Util.logger.info("ICOM ExecuteReChargeResultICOM@SEND TO VMS to synchronized DB MLIST FAILURE:"
							+ "  UserId = "
							+ rslInfo.getUserId()
							+ ", CommandCode = "
							+ rslInfo.getCommandCode()
							+ ", ServiceId = "
							+ rslInfo.getServiceId()
							+ ", Result Charge = "
							+ rslInfo.getReslultCharge()
							+ ", Result Send WS = " + handleResult);
				}
			}

			if (handleResult == 1) { // handle thanh cong => delete in
										// vms_charge_result
				Util.logger.info("ExecuteReChargeResultICOM@ DELETE IN recharge_packet_result: " +
						" USER_ID = " + rslInfo.getUserId() 
						+ " , COMMAND_CODE = " + rslInfo.getCommandCode()
						+ " , SERVICE_ID = " + rslInfo.getServiceId()
						+ " , DATE_NUMBER = " + rslInfo.getDayNumber()
						+ " , RESULT_CHARGE = " + rslInfo.getReslultCharge());
						
				serviceMng.deleteChargeResultByID(rslInfo.getId(),
						chargePacketTable);
			}

		}
	}

}
