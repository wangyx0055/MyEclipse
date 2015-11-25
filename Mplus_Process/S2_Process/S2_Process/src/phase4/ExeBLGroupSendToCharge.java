package phase4;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.Sender;
import icom.common.Util;

public class ExeBLGroupSendToCharge extends Thread {

	public void run(){
		
		String timeToCharge = Constants._prop.getProperty("BL_GROUP_TIMECHARGE", "12:00");
		String timeResetCharge = Constants._prop.getProperty("BL_GROUP_TIME_RESET_CHARGE", "12:00");
		String[] arrTimeCharge = timeToCharge.split(";");
		
		Ph4Common cmmObj = new Ph4Common();
		
		while (Sender.processData) {

			String currHour = getCurrHour();
			
			if(timeResetCharge.equals(currHour)){
				cmmObj.updateAllChargingMember(0);
			}
			
			Boolean check = false;
			for (int i = 0; i < arrTimeCharge.length; i++) {
				String timeObj = arrTimeCharge[i];
				if (timeObj.equals(currHour)) {
					check = true;
					break;
				}
			}
			if (!check) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

			Util.logger.info("ExeBLGroupSendToCharge, time send charge online = " + timeToCharge);
			
			try {
			
				ArrayList<BLGroupMemberObj> arrMember = cmmObj
						.getMemberToCharge();

				Keyword keyword = Sender.loadconfig.getKeyword("BLN", "9209");

				for (int i = 0; i < arrMember.size(); i++) {

					if (!Sender.processData)
						break;

					BLGroupMemberObj memberObj = arrMember.get(i);
					String info = "BLN_CHARGING;memberId="
							+ memberObj.getId();
					MsgObject msgObject = getMsgObj(memberObj, keyword);
					msgObject.setUsertext(info);
					
					if(cmmObj.isFreeList(msgObject.getUserid(), "BLN")){
						cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT,
								msgObject);
					}else{
						cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE,
								msgObject);
					}
					cmmObj.updateChargingMember(memberObj.getId(), 2);

				}

				Thread.sleep(1000);

			} catch (Exception ex) {
				Util.logger.error(ex.getMessage());
			}

		}
		
	}
	
	private String getCurrHour(){
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		java.util.Date today = new java.util.Date();			
		String currHour = formatter.format(today);
		return currHour;
	}
	
	private MsgObject getMsgObj(BLGroupMemberObj memberObj,Keyword keyword){
		
		MsgObject msgObj = new MsgObject();
		
		msgObj.setUserid(memberObj.getUserId());
		msgObj.setMobileoperator("VMS");
		msgObj.setChannelType(0);
		msgObj.setServiceid("9209");
		msgObj.setRequestid(
				BigDecimal.valueOf(Calendar.getInstance().getTimeInMillis()) );
		msgObj.setCommandCode("BLN");
		msgObj.setKeyword("BLN");
		msgObj.setServiceName("BLN");
		msgObj.setContenttype(0);
		msgObj.setMsgtype(0);
		
		msgObj.setCommandCode(keyword.getService_ss_id());
		msgObj.setAmount(keyword.getAmount());
		msgObj.setContentId(keyword.getService_type());
		msgObj.setTimeSendMO(Util.getCurrentDate());
		
		return msgObj;
	}
	
}
