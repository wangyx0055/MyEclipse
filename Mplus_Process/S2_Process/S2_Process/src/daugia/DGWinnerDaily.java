package daugia;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import DAO.MTDAO;

import servicesPkg.MlistInfo;
import icom.Constants;
import icom.MsgObject;
import icom.Sender;
import icom.common.Util;

public class DGWinnerDaily extends Thread{
	
	private String CLASSNAME = "DGWinnerDaily";
	
	public DGWinnerDaily (){
		
	}
	public void run(){
		
		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		DGAmountManager amountMng = new DGAmountManager();
		DaugiaCommon commonObj = new DaugiaCommon();
		while(Sender.getData){
			SanPhamDG spDG = spMng.getSanPhamDG();
			if (spDG != null) {
				
				DateFormat formatter = new SimpleDateFormat("HH:mm");
				java.util.Date today = new java.util.Date();
				String currHour = formatter.format(today);
				
				String endDate = spDG.getEndDate();  // get end date
				endDate = endDate.substring(0, 10);
				String dt="2012-04-30";
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Calendar c = Calendar.getInstance();
				
				try {
					c.setTime(sdf.parse(dt));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Util.logger.error(CLASSNAME + " Can't convert time :"+e);
					continue;
				}
				//c.add(Calendar.DATE, 1);  // number of days to add
				dt = sdf.format(c.getTime());				
				DGConstants.TIME_WIN_DAILY = Constants._prop.getProperty("TIME_WIN_DAILY").trim();
				
				//Check correct time 
				if (currHour.equals(DGConstants.TIME_WIN_DAILY)) {
					//Check incorrect end day of session
					if(!dt.equals(endDate)){
						//push MT_win
						Util.logger.info("end day start push mt day winner...");
						String info = DGConstants.MT_WIN_DAILY;
						try {
							info = MTDAO.getRandomMessage(
								DGProcess.hMessageReminder.get("92"),
								DGProcess.hMessageReminder.get("92").split(
										";").length);
						} catch (Exception e) {
							Util.logger.printStackTrace(e);
						}
						DGAmount dgWin = amountMng.getUserWin();
						if (dgWin != null) {
							
							info = info.replace("USER_ID", dgWin.getUserId());
							info = info.replace("CUR_DATE", dt);
							info =  info.replace("TEN_SP", dgWin.getMaSP());
							
							MsgObject msgObj = new MsgObject();
							
							MlistInfo mlist = commonObj.getAllMlistInfoByUser(DGConstants.TABLE_MLIST_DG,dgWin.getUserId());
							msgObj.setUserid(mlist.getUserId());
							msgObj.setMobileoperator(mlist.getMobiOperator());
							msgObj.setChannelType(mlist.getChanelType());
							msgObj.setServiceid(mlist.getServiceId());
							msgObj.setCommandCode(mlist.getCommandCode());
							msgObj.setContenttype(0);
							msgObj.setMsgtype(mlist.getMessageType());
							msgObj.setKeyword(mlist.getCommandCode());
							try {
								msgObj.setRequestid(new BigDecimal(mlist.getRequestId()));
							} catch (Exception e) {
								Util.logger.error("DGResponseMTDaily @reqId error:" + e
										+ " @ set default reqId =123 @user:" + msgObj.getUserid());
								msgObj.setRequestid(new BigDecimal(123));
							}
							commonObj.pushMT(msgObj);
						}
					}
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Util.logger.info(CLASSNAME + "Thread can't sleep");
				e.printStackTrace();
			}
		}
	}
}
