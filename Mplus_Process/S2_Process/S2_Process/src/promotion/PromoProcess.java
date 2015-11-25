package promotion;

import icom.Constants;
import icom.Sender;
import icom.common.Util;
import servicesPkg.ServiceMng;



public class PromoProcess extends Thread{

	public static Boolean isPromo = false;
	
	
	public void run(){
		
		Boolean isStart = false;
					
		while(Sender.getData){
			isPromo = checkDate();
			
			if(isPromo){
				
				if(!isStart){
					
					GenPromoCode genThread  = new GenPromoCode();
					genThread.start();
			
					SendMTRegPromo mtRegThread = new SendMTRegPromo();
					mtRegThread.start();
			
					SendPromoDaily promoDaily = new SendPromoDaily();
					promoDaily.start();
				}
				
				isStart = true;
			}
			
			try {
				Thread.sleep(60*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	private Boolean checkDate(){
		
		ServiceMng sMng = new ServiceMng();
		String today = sMng.getDate(0);
		
		String dateConfig = Constants._prop.getProperty("PROMOTION_GAME_DATE");
		
		String[] arrDate = dateConfig.split(";");
		
		try{
			if(today.compareTo(arrDate[0].trim()) < 0 ||
				today.compareTo(arrDate[1].trim()) > 0) return false;
		}catch(Exception ex){
			Util.logger.error("config value PROMOTION_GAME_DATE wrong!");
			return false;
		}
		
		return true;
	}
	
}
