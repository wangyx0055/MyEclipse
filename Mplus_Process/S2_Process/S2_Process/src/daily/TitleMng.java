package daily;

import icom.common.DBSelect;

import java.util.Hashtable;

public class TitleMng {
	
	//String 1: service Name (key); String2 title
	private static Hashtable<String, String> hTitle = new Hashtable<String, String>();
	
	private static TitleMng titleMng = null;
	
	private TitleMng(){}
	
	public static TitleMng getInstance(){
		
		if(titleMng == null){
			titleMng = new TitleMng();
		}
		return titleMng;
		
	}
	
	public String getTitle(String serviceName){
		String strTitle = "";
		strTitle = hTitle.get(serviceName);
		
		if(strTitle == null ){			
			DBSelect dbSelect = new DBSelect();
			strTitle = dbSelect.getTitle(serviceName);
			hTitle.put(serviceName, strTitle);
		}
		
		return strTitle;

	}
}
