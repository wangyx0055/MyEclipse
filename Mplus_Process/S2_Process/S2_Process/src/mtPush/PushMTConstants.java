package mtPush;

import icom.Constants;
import icom.common.Util;

public class PushMTConstants {
	
	public static String[] PUSH_MT_SERVICE = new String[]{"CUOI","RING"};
	public static String PROMOTION_TIME_START = "MTPUSH_PROMOTION_START_DATE";
	public static String PROMOTION_TIME_END = "MTPUSH_PROMOTION_END_DATE";
	public static String PROMOTION_TAG = "_promotion";
	
	public static String pushMTPool = "s2mtpush";
	public static String TABLE_MT_PUSH = "mtpush_s2vms";
	
	public static String getDatePromStart(){
		String startDate = "";
		try{
			startDate = Constants._prop.getProperty(PROMOTION_TIME_START, "").trim();
		}catch(Exception ex){
			
		}		
		return startDate;
	}
	
	public static String getDatePromEnd(){
		String endDate = "";
		try{
			endDate = Constants._prop.getProperty(PROMOTION_TIME_END, "").trim();
		}catch(Exception ex){
			
		}		
		return endDate;
	}
	
	/**
	 * 
	 * @return true if now is in promotion Push MT </br>
	 *         false out of time promotion
	 */
	public static Boolean isInPromo(){
		
		String startDate = getDatePromStart();
		String endDate = getDatePromEnd();
		String today = Util.getCurrentDate();
		
		if(today.compareTo(startDate)<0 || today.compareTo(endDate)> 0) return false;
		
		return true;
	}
	
	public static int getNumberMTPushTable(){
		int number = -1;
		String strNumber = "-1";
		try{
			strNumber = Constants._prop.getProperty("MTPUSH.LIMIT_TABLE_MTPUSH_USER", "-1").trim();
			number = Integer.parseInt(strNumber);
		}catch(Exception ex){
			
		}		
		return number;
	}
}
