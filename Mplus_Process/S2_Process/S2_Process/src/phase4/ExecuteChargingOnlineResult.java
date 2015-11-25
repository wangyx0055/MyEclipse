package phase4;

import java.math.BigDecimal;
import java.util.ArrayList;

import SyncSky.SyncAPI;

import icom.Constants;
import icom.Keyword;
import icom.LoadConfig;
import icom.MsgObject;
import icom.QuestionManager;
import icom.Sender;
import icom.common.DBDelete;
import icom.common.DBInsert;
import icom.common.ResultCode;
import icom.common.Util;

public class ExecuteChargingOnlineResult extends Thread {

	public void run() {

		Ph4Common cmmObj = new Ph4Common();
		DBDelete dbDelete = new DBDelete();
		DBInsert dbInsert = new DBInsert();

		while (Sender.processData) {

			ArrayList<ChargeOnlineObject> arrResult = cmmObj
					.getChargeOnlineResult();

			for (int i = 0; i < arrResult.size(); i++) {

				if (!Sender.processData)
					break;

				ChargeOnlineObject onlineObj = arrResult.get(i);

				dbDelete.deleteTableByID(onlineObj.getId(),
						Ph4Common.TBL_CHARGE_ONLINE_RESULT);

				if (onlineObj.getInfo().toUpperCase().matches("(.*)SKY(.*)")
						|| onlineObj.getCommandCode().toUpperCase().matches(
								"(.*)SKY(.*)")) {
					String response = SyncAPI.SyncMR(onlineObj.getUserId(),
							onlineObj.getAmount() + "", onlineObj.getRequestId());
					Util.logger
							.info("ExeBLChargeOnlineResult @synchronization MR to sky response:"
									+ response);
				}

				Keyword keyword = Sender.loadconfig.getKeyword(onlineObj
						.getInfo().toUpperCase(), onlineObj.getServiceId());
				
				
				
				MsgObject msgObj = getMsgObj(onlineObj);
				msgObj.setKeyword(keyword.getKeyword());

				String classMT = keyword.getClass_mt();
				
				if (classMT.equals("none") || classMT.equals("")
						|| classMT.equals("x") || classMT.equals(Constants.INV_MT_CLASS))
					continue;

				int resultCharge = onlineObj.getResultCharge();
				msgObj.setChargeResult(resultCharge);

				try {
					Class delegateClass = Class.forName(classMT);
					Object delegateObject = delegateClass.newInstance();

					QuestionManager delegate = null;
					delegate = (QuestionManager) delegateObject;
					delegate.start(Constants._prop, msgObj, keyword,
							LoadConfig.hServices);
				} catch (Exception ex) {
					Util.logger.error(ex.getMessage());
				}

			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	private MsgObject getMsgObj(ChargeOnlineObject rslInfo) {

		MsgObject msgObj = new MsgObject();

		msgObj.setUserid(rslInfo.getUserId());
		msgObj.setMobileoperator(rslInfo.getMobiOperator());
		msgObj.setChannelType(rslInfo.getChanelType());
		msgObj.setServiceid(rslInfo.getServiceId());
		msgObj.setRequestid(BigDecimal.valueOf(Double.parseDouble(rslInfo
				.getRequestId())));
		msgObj.setCommandCode(rslInfo.getCommandCode());
		msgObj.setContenttype(rslInfo.getContentType());
		msgObj.setMsgtype(rslInfo.getMsgType());
		msgObj.setUsertext(rslInfo.getInfo());

		return msgObj;
	}

}
