package phase4;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import icom.MsgObject;
import icom.Sender;
import icom.common.DBInsert;
import icom.common.Util;

public class ExeSendContentToGroup extends Thread {

	public void run(){
		
		Ph4Common cmmObj = new Ph4Common();
		DBInsert dbInsert = new DBInsert();
		
		while(Sender.processData){
			
			ArrayList<BLGroupContentObj> arrContent = cmmObj.getGroupContentToSend();
			
			if(arrContent == null){
				try {
					Thread.sleep(5*1000);
				} catch (InterruptedException e) {
					Util.logger.error("ExeSendContentToGroup" + e.getMessage());
				}
				continue;
			}
			
			try {
				for (int i = 0; i < arrContent.size(); i++) {

					if (!Sender.processData)
						break;

					BLGroupContentObj contentObj = arrContent.get(i);
					BinhLuanGroupObj groupObj = cmmObj.getBLGroupObj(contentObj
							.getGroupName());

					// update status mt
					cmmObj.updateGroupContentStatusMT(contentObj.getId(), 1);

					if (groupObj.getId() <= 0) {
						continue;
					}

					// send MT to Group
					String infoToSend = contentObj.getInfo();					
					infoToSend = infoToSend.substring(3, infoToSend.length()).trim();
					
					infoToSend = infoToSend.replaceFirst(" ",": ");
					
					infoToSend = contentObj.getUserId() + "-"
							+ infoToSend.trim();
					infoToSend = infoToSend
							+ " . "
							+ "De tra loi soan BLG_Tennhom_Noidung gui 9209. DTHT:9244. Chi tiet:http://m.mplus.vn";

					ArrayList<BLGroupMemberObj> arrMember = cmmObj
							.getArrayMember(groupObj.getId());
					for (int j = 0; j < arrMember.size(); j++) {
						if(arrMember.get(j).getUserId().equals(contentObj.getUserId())){
							continue;
						}
						BLGroupMemberObj memberObj = arrMember.get(j);
						MsgObject msgObject = getMsgObj(memberObj);
						msgObject.setUsertext(infoToSend);
						msgObject.setCommandCode("BLN");
						msgObject.setKeyword("BLN");
						dbInsert.sendMT(msgObject);
					}

				}
			}catch (Exception e) {
				Util.logger.error(e.getMessage());
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	private MsgObject getMsgObj(BLGroupMemberObj memberObj){
		
		MsgObject msgObj = new MsgObject();
		
		msgObj.setUserid(memberObj.getUserId());
		msgObj.setMobileoperator("VMS");
		msgObj.setChannelType(0);
		msgObj.setServiceid("9209");
		msgObj.setRequestid(
				BigDecimal.valueOf(Calendar.getInstance().getTimeInMillis()) );
		msgObj.setCommandCode("BLN");
		msgObj.setContenttype(0);
		msgObj.setMsgtype(0);
		
		return msgObj;
	}
	
}
