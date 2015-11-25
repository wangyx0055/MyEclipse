package servicesPkg;

import icom.Constants;
import icom.common.ChannelType;
import icom.common.Util;

import java.math.BigDecimal;
import java.util.ArrayList;
import vmg.itrd.ws.MTSenderVMS;

public class ProcessReCharge {

	public void handleRecharge(String mlisTable, String commandCode,
			int icomOrVMS) { // 1 VMS; -1: ICOM

		System.out.println("ProcessReCharge......");

		ServiceMng serviceMng = new ServiceMng();
		ArrayList<MlistInfo> arrMlistInfo = new ArrayList<MlistInfo>();

		arrMlistInfo = serviceMng.getMlistInfo(mlisTable);

		int handleResult = -1;

		for (int j = 0; j < arrMlistInfo.size(); j++) {

			MlistInfo mlistInfo = arrMlistInfo.get(j);

			if (!mlistInfo.getCommandCode().toUpperCase().equals(commandCode))
				continue;
			if (mlistInfo.getActive() == 1)
				continue;

			int xDay = 30;
			try {
				xDay = Integer.parseInt(Constants._prop.getProperty(
						"PACKET_DATE_LIMIT", ""));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// Check time gia han
			String dateRetryAddXday = serviceMng.getDateAddDay(mlistInfo
					.getDateRetry(), xDay);
			String today = serviceMng.getDate(0);

			Boolean isSendVMS = false;

			if (dateRetryAddXday.compareTo(today) < 0) {
				// Het han recharge => delete in mlist and add in mlist cancel
				mlistInfo.setChanelType(ChannelType.TYPE_SUB_OVER_DATERETRY);
				serviceMng.insertMlistCancelFromMlistInfo(
						mlisTable + "_cancel", mlistInfo);
				// delete in mlist
				serviceMng.deleteInMlist(mlisTable, mlistInfo.getUserId(),
						mlistInfo.getCommandCode(), mlistInfo.getServiceId());
			} else if (mlistInfo.getDateRetry().compareTo(today) < 0) {
				// insert in Recharge_packet
				isSendVMS = true;
				if (serviceMng.insertInRechargePacket(mlistInfo, icomOrVMS) == 1) {
					serviceMng.updateMlistActive1(mlisTable, mlistInfo.getId());
				}

			}

			if (isSendVMS) {
				if (icomOrVMS == Constants.PACKET_ICOM) {
					// send to VMS with is_the_packet = -1

					ChargeResultInfo rslInfo = new ChargeResultInfo();
					rslInfo.setUserId(mlistInfo.getUserId());
					rslInfo.setServiceId(mlistInfo.getServiceId());
					rslInfo.setMobileOperator(mlistInfo.getMobiOperator());
					rslInfo.setCommandCode(mlistInfo.getCommandCode());
					rslInfo.setContentType(mlistInfo.getContentId());
					rslInfo.setInfo("");
					rslInfo.setSubmitDate(mlistInfo.getAutoTimeStamps());
					rslInfo.setDoneDate(serviceMng.getDate(0));
					rslInfo.setProcessResult(0);
					rslInfo.setMsgType(mlistInfo.getMessageType());
					rslInfo.setRequestID(BigDecimal.valueOf(Integer
							.parseInt(mlistInfo.getRequestId())));
					rslInfo.setMsgID("0");
					rslInfo.setTotalSegments(0);
					rslInfo.setRetriesNumber(0);
					rslInfo.setInsertDate(mlistInfo.getAutoTimeStamps());
					rslInfo.setNotes("");
					rslInfo.setServiceName(mlistInfo.getService());
					rslInfo.setChannelType(mlistInfo.getChanelType());
					rslInfo.setContendID("0");
					rslInfo.setAmount(mlistInfo.getAmount());
					rslInfo.setDayNumber(0);
					rslInfo.setReslultCharge(0);
					rslInfo.setIsThePacket(-1);

					handleResult = MTSenderVMS
							.insertVmsReChargePackage(rslInfo);
					if (handleResult == 1) {
						Util.logger
								.info("Send to VMS_RECHARGE_PACKET SUCCESS: USER_ID = "
										+ rslInfo.getUserId()
										+ " , COMMAND_CODE = "
										+ rslInfo.getCommandCode()
										+ " , Service_ID = "
										+ rslInfo.getServiceId());
						serviceMng.deleteInTable("recharge_packet", rslInfo
								.getUserId(), rslInfo.getCommandCode(), rslInfo
								.getServiceId());
					} else {
						Util.logger
								.info("Send to VMS_RECHARGE_PACKET FAILURE: USER_ID = "
										+ rslInfo.getUserId()
										+ " , COMMAND_CODE = "
										+ rslInfo.getCommandCode()
										+ " , Service_ID = "
										+ rslInfo.getServiceId());
					}
				}

			}
		}

	}
}
