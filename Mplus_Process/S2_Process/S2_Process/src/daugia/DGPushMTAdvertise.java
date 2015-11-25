package daugia;

import java.math.BigDecimal;
import java.util.ArrayList;

import DAO.MTDAO;

import servicesPkg.MlistInfo;

import icom.MsgObject;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.Util;

public class DGPushMTAdvertise extends Thread {
	
	@Override
	public void run(){
		
		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		DaugiaCommon commonObj = new DaugiaCommon();
		
		Boolean check = false;
		
		while(Sender.getData){
			SanPhamDG spObj = spMng.getSanPhamDG();
			
			if(spObj != null){
				
				check = false;
				
				ArrayList<MlistInfo> arrMlist = new ArrayList<MlistInfo>();
				arrMlist = commonObj.getMlistToPushMTAdvertise(DGConstants.TABLE_MLIST_DG);	

				// int numberMlist = arrMlist.size();
				//				
				// ArrayList<MlistInfo> arrsub = new ArrayList<MlistInfo>();
				// arrsub =
				// commonObj.getMlistToPushMTAdvertise(DGConstants.TABLE_MLIST_DG_TMP);
				// arrMlist.addAll(arrsub);
				
				for(int i = 0;i<arrMlist.size();i++){
					
					
					
					MlistInfo mlistInfo = arrMlist.get(i);
					MsgObject msgObj = getMsgObj(mlistInfo);
					
					if(DGConstants.DG_MODE==0){
						DBSelect dbSelect = new DBSelect();
						Boolean checkTest = dbSelect.isUserTest(msgObj.getUserid());
						if(!checkTest){														
							continue;
						}
						
					}
					
					//String info = DGConstants.MTINFO_START_DAUGIA;
					String info="";
					try {
						info = MTDAO.getRandomMessage(
							DGProcess.hMessageReminder.get("50"),
							DGProcess.hMessageReminder.get("50").split(
									";").length);
					} catch (Exception e) {
						Util.logger.printStackTrace(e);
					}
					String strStartDate = commonObj.formatDate(spObj.getStartDate());
					info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_START_DATE,
								strStartDate);
					
					String strEndDate = commonObj.formatDate(spObj.getEndDate());
					info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_END_DATE,
								strEndDate);
					info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_GIA_SP, 
								spObj.getGiaSP());
					info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_TEN_SP, 
								spObj.getTenSP());
					//tuannq add
					if(msgObj.getCommandCode().equals("DAUGIA")) 
						info = info.replace(DGConstants.COMMAND_CODE, "DG");
					else{
						info =  info.replace(DGConstants.COMMAND_CODE, msgObj.getCommandCode());
					}
					
					String hour = commonObj.getHour(spObj.getEndDate());
					info = info.replaceAll(DGConstants.STRING_REGEX_END_HOUR, hour);
					
					msgObj.setUsertext(info);
					
					commonObj.pushMT(msgObj);
					
					commonObj.updateIsPushAD(DGConstants.TABLE_MLIST_DG, 1,
							mlistInfo.getId());

//					if (i >= numberMlist) {
//						commonObj.updateIsPushAD(
//								DGConstants.TABLE_MLIST_DG_TMP, 1, mlistInfo
//										.getId());
//					}
					
				}// End for
				

				
			}else{
				
				if(!check){
					if(commonObj.getSPDGNotResponseWeekly() == null){
						commonObj.resetIsPushAD(DGConstants.TABLE_MLIST_DG, 0);
					}
				}
				check = true;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
		}
	}
	
	private MsgObject getMsgObj(MlistInfo mlistInfo) {

		MsgObject msgObj = new MsgObject();

		msgObj.setUserid(mlistInfo.getUserId());
		msgObj.setMobileoperator(mlistInfo.getMobiOperator());
		msgObj.setChannelType(mlistInfo.getChanelType());
		msgObj.setServiceid(mlistInfo.getServiceId());
		msgObj.setCommandCode(mlistInfo.getCommandCode());
		msgObj.setContenttype(0);
		msgObj.setMsgtype(mlistInfo.getMessageType());
		msgObj.setKeyword("DAUGIA");
		try {
			msgObj.setRequestid(new BigDecimal(mlistInfo.getRequestId()));
		} catch (Exception e) {
			Util.logger.error("DGResponseMTDaily @reqId error:" + e
					+ " @ setdefault reqId =123 @user:" + msgObj.getUserid());
			msgObj.setRequestid(new BigDecimal(123));
		}
		return msgObj;
	}
}
