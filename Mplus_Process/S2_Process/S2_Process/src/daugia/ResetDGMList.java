package daugia;

import java.util.Calendar;

import icom.Sender;


public class ResetDGMList extends Thread{

	@Override
	public void run(){
		
		DaugiaCommon commonObj = new DaugiaCommon();
		
		while(Sender.getData){
			
			Calendar gc = Calendar.getInstance();
			int currHour = gc.get(Calendar.HOUR_OF_DAY);
			
			if(currHour == 0){			
				commonObj.resetMlistActive(DGConstants.TABLE_MLIST_DG);				
			}
			
			try {
				Thread.sleep(5*60*1000);
			} catch (InterruptedException e) {
			}
			
		}
	}
}

