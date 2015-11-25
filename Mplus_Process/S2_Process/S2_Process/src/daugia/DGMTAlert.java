package daugia;

import icom.MsgObject;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.Util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import DAO.MTDAO;

import servicesPkg.MlistInfo;
import servicesPkg.ServiceMng;

public class DGMTAlert extends Thread{
	
	String USER_ALERT = "093825xxx";
	String AMOUNT_MIN = "10000";
	String AMOUNT_MAX = "30000";
	int AMOUNT_ADD = 50000;
	
	int numberAlert = 0;
	
	public void run() {

		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		DGAmountManager amountMng = new DGAmountManager();
		ServiceMng serviceMng = new ServiceMng();
		DaugiaCommon commonObj = new DaugiaCommon();
				
		while (Sender.getData) {
			
			SanPhamDG spDG = spMng.getSanPhamDG();
			if (spDG != null) {
				
				String endDay = getDayEnd(spDG.getEndDate());				

				if (endDay.equals(getToday())) {// Check ngay cuoi cua phien dau
												// gia
					
					DateFormat formatter = new SimpleDateFormat("HH:mm");
					java.util.Date today = new java.util.Date();
					String currHour = formatter.format(today);

					String[] arrTimes = spDG.getTimeAlert().split(";");

					if (isTimeAlert(arrTimes, currHour)) { // Dung thoi gian gui
															// alert
						String info = DGConstants.MT_ALERT_1;
						try {
							info = MTDAO.getRandomMessage(
								DGProcess.hMessageReminder.get("95"),
								DGProcess.hMessageReminder.get("95").split(
										";").length);
						} catch (Exception e) {
							Util.logger.printStackTrace(e);
						}
						if(numberAlert == 2){
							//info = DGConstants.MT_ALERT_2;
							try {
								info = MTDAO.getRandomMessage(
									DGProcess.hMessageReminder.get("94"),
									DGProcess.hMessageReminder.get("94").split(
											";").length);
							} catch (Exception e) {
								Util.logger.printStackTrace(e);
							}

						}
						
						DGAmount dgWin = amountMng.getUserWin();
						
						DaugiaCommon dgComm = new DaugiaCommon();
						endDay = dgComm.formatDate(spDG.getEndDate());
						String endHour = dgComm.getHour(spDG.getEndDate());
						
						if (dgWin != null) {
							String phoneNumber = dgWin.getUserId();
							if (phoneNumber.length() > 5) {
								phoneNumber = phoneNumber.substring(0,
										phoneNumber.length() - 4);
								phoneNumber = phoneNumber + "xxx";
							}
							
							int amountWin = Integer.parseInt(dgWin
									.getDgAmount());
							
							Random random = new Random();							
							int randomMin = 1000 + random.nextInt(20)*1000;
							int randomMax = 10000 + random.nextInt(20)*1000;
							int minAmount = amountWin - randomMin;
							if(minAmount<0) minAmount = 1000;
							int maxAmount = amountWin + randomMax;
							
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_USER_ID,
									phoneNumber);
							
							
							info = info
									.replaceAll(
											DGConstants.STRING_REGEX_REPLACE_AMOUNT_MIN,
											minAmount + "");
							info = info
									.replaceAll(
											DGConstants.STRING_REGEX_REPLACE_AMOUNT_MAX,
											maxAmount + "");
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_TEN_SP,
									spDG.getTenSP());
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_GIA_SP,
									spDG.getGiaSP());
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_END_DATE,
									endDay);
							

						} else {
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_USER_ID,
									USER_ALERT);

							info = info
									.replaceAll(
											DGConstants.STRING_REGEX_REPLACE_AMOUNT_MIN,
											AMOUNT_MIN);
							info = info
									.replaceAll(
											DGConstants.STRING_REGEX_REPLACE_AMOUNT_MAX,
											AMOUNT_MAX);

							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_TEN_SP,
									spDG.getTenSP());
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_GIA_SP,
									spDG.getGiaSP());
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_END_DATE,
									endDay);
						}
						
						info = info.replaceAll(DGConstants.STRING_REGEX_END_HOUR, endHour);
						
						ArrayList<MlistInfo> arrMlist = commonObj
								.getAllMlistInfo(DGConstants.TABLE_MLIST_DG);

						for (int i = 0; i < arrMlist.size(); i++) {

							MlistInfo mlistInfo = arrMlist.get(i);
							MsgObject msgObj = getMsgObj(mlistInfo);
							
							//tuannq add
							if(msgObj.getCommandCode().equals("DAUGIA")) 
								info.replaceAll(DGConstants.COMMAND_CODE, "DG");
							else{
								info.replaceAll(DGConstants.COMMAND_CODE, "DA");
							}							
							msgObj.setUsertext(info);
							
							if(DGConstants.DG_MODE==0){
								DBSelect dbSelect = new DBSelect();
								Boolean checkTest = dbSelect.isUserTest(msgObj.getUserid());
								if(!checkTest){														
									continue;
								}
								
							}
							
							commonObj.pushMT(msgObj);
							serviceMng.updateMlistActive1(
									DGConstants.TABLE_MLIST_DG, mlistInfo
											.getId());

						} // End if 2
					}// End if 1
				}
			}
			
			try {
				Thread.sleep(60*1000);
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
		msgObj.setRequestid(new BigDecimal(mlistInfo.getRequestId()));
		return msgObj;
	}
	
	private String getDayEnd(String timeEnd){				
		return timeEnd.substring(0, 10).trim();
	}
	
	private String getToday(){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date today = new java.util.Date();
		String sDay = formatter.format(today);
		return sDay;
	}
	
	private Boolean isTimeAlert(String[] arrTimes, String currTime){
		
		Boolean check = false;
		for(int i = 0;i< arrTimes.length;i++){
			if( currTime.equals(arrTimes[i].trim()) ){
				numberAlert = i+1;
				check = true;
				break;
			}		
		}
		return check;
	}
}
