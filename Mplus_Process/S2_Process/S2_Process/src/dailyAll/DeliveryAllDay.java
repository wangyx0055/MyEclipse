package dailyAll;

import java.util.ArrayList;
import java.util.Calendar;

import sub.DeliveryManager;

import icom.MsgObject;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.Util;

/***
 * 
 * @author DanND
 *
 */
public class DeliveryAllDay extends Thread{

	public DeliveryAllDay(){}
	
	public void run(){
		
		while (Sender.getData) {

			DBSelect dbSelect = new DBSelect();
			ArrayList<MsgObject> arrService = dbSelect.getResendService();

			AlldayRechargeMng allDayRecharge = AlldayRechargeMng.getInstance();

			for (int i = 0; i < arrService.size(); i++) {

				if (!Sender.getData)
					break;

				MsgObject obj = arrService.get(i);

				if (!allDayRecharge.isRechargeAll(obj.getCommandCode())) {
					continue;
				}

//				if (!isTimeDelivery(obj.getHours(), obj.getMinutes())) {
//					continue;
//				}

				try {

					DeliveryManager delegate = null;					
					Class delegateClass = Class.forName(obj.getClassSendMT());
					Object delegateObject = delegateClass.newInstance();
					delegate = (DeliveryManager) delegateObject;

					delegate.start(obj.getMsg_id() + "", obj.getServiceName(),
							obj.getOption(), obj.getNotcharge());

				} catch (Exception ex) {
					Util.logger.error("DeliveryAllDay: " + ex.getMessage());
				}

			}

			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Boolean isTimeDelivery(String hours, String minutes){
		
		String[] arrHours = hours.split(";");
		String[] arrMinutes = minutes.split(";");
		
		Calendar curTime = Calendar.getInstance();
		
		int curHour = curTime.get(Calendar.HOUR_OF_DAY);
		
		Boolean checkHour = false;
		
		for(int i =0;i<arrHours.length;i++){
			
			int hourDelivery = Integer.parseInt(arrHours[i].trim());
			if(curHour >= hourDelivery){
				checkHour = true;
				break;
			}
			
		}
		
		
		if(checkHour == true){
			
			Boolean checkMinute = false;
			int curMinute = curTime.get(Calendar.MINUTE);
			for(int i=0; i< arrMinutes.length;i++){
				
				int minuteDelivery = Integer.parseInt(arrMinutes[i].trim());
				if(curMinute >= minuteDelivery){					
					checkMinute = true;
					break;					
				}				
				
			}
			
			return checkMinute;			
			
		}
		
		
		
		return false;
	}
	
}
