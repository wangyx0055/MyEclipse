package daugia;

import java.util.ArrayList;

public class DGQueueCharge {

	private static ArrayList<DGChargeInfo> arrCharge = new ArrayList<DGChargeInfo>();
	
	private static DGQueueCharge queueCharge = null;
	
	public static synchronized DGQueueCharge getInstance(){
		if(queueCharge == null){
			queueCharge = new DGQueueCharge();
		}
		return queueCharge;
	}
	
	public synchronized DGChargeInfo getDGChargeObj(){
		
		try {
			// wait
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DGChargeInfo chargeObj = null;
		DaugiaCommon cmm = new DaugiaCommon();
		
		if(arrCharge.size() == 0){
			arrCharge  = cmm.getDGChargeResult();
		}
		if(arrCharge.size()>0){
			chargeObj = arrCharge.remove(0);
//			cmm.deleteTableByID(chargeObj.getId(), DGConstants.TABLE_DG_CHARGE_RESULT);
		}
		
		return chargeObj;
		
	}
	
}
