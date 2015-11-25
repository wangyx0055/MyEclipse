package phase4;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import servicesPkg.MlistInfo;

import icom.Constants;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

public class ChatExecuteCharge extends Thread{
	
	public void run(){
		
		String timeToCharge = Constants._prop.getProperty("CHAT_TIME_SEND_CHARGE", "12:00");
		String timeResetCharge = Constants._prop.getProperty("CHAT_TIME_RESET_CHARGE", "12:00");
		String[] arrTimeCharge = timeToCharge.split(";");
		
		Ph4Common cmmObj = new Ph4Common();
		ChatCommon chComm = new ChatCommon();
		
		while (Sender.processData) {

			String currHour = getCurrHour();
			
			if(timeResetCharge.equals(currHour)){
				chComm.resetMlist("mlist_chat",0, 0);
				
				String sqlDelete = "delete from chat_send_daily";				
				DBUtil.executeSQL("gateway", sqlDelete);
				
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

			Util.logger.info("ChatExecuteCharge, time send charge online = " + timeToCharge);
			
			try {
			
				ArrayList<MlistInfo> arrMlistInfo = chComm.getMlistChatInfoToCharge("mlist_chat");

				for (int i = 0; i < arrMlistInfo.size(); i++) {

					if (!Sender.processData)
						break;

					MlistInfo mlistInfo = arrMlistInfo.get(i);
					String info = "CHAT_CHARGING;mlistid="
							+ mlistInfo.getId();
					MsgObject msgObject = getMsgObj(mlistInfo);
					msgObject.setUsertext(info);
					
					chComm.updateMlistActive("mlist_chat",mlistInfo.getId(), 2);
					
					if(cmmObj.isFreeList(msgObject.getUserid(), mlistInfo.getCommandCode())){
						cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT,
								msgObject);
					}else{
						cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE,
								msgObject);
					}


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
	
	private MsgObject getMsgObj(MlistInfo mlistInfo){
		
		MsgObject msgObj = new MsgObject();
		
		msgObj.setUserid(mlistInfo.getUserId());
		msgObj.setMobileoperator(mlistInfo.getMobiOperator());
		msgObj.setChannelType(0);
		msgObj.setServiceid(mlistInfo.getServiceId());
		msgObj.setRequestid(
				BigDecimal.valueOf(Calendar.getInstance().getTimeInMillis()) );
		msgObj.setCommandCode(mlistInfo.getCommandCode());
		msgObj.setKeyword(mlistInfo.getCommandCode());
		msgObj.setServiceName(mlistInfo.getCommandCode());
		msgObj.setContenttype(0);
		msgObj.setMsgtype(0);

		msgObj.setAmount(mlistInfo.getAmount());
		msgObj.setContentId(mlistInfo.getContentId());
		msgObj.setTimeSendMO(Util.getCurrentDate());
		
		return msgObj;
	}
		
	

}
