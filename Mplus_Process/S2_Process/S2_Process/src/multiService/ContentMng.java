package multiService;

import icom.common.Util;
import java.util.Hashtable;


public class ContentMng {

	private static Hashtable<String, String> hOptionsICOM = new Hashtable<String, String>();
	private static Hashtable<String, String> hOptionsVMS = new Hashtable<String, String>();
	
	private static ContentMng contentMng = null;
	
	ContentMng(){		
	}
	
	public static ContentMng getInstance(){
		
		if(contentMng == null){
			contentMng = new ContentMng();
		}
		
		return contentMng;
	}
		
	public String getOptionIcom(String commandCode){
		String options = null;
		options = hOptionsICOM.get(commandCode);
		if(options == null){
			LoadMultiService loadObj = new LoadMultiService();
			options = loadObj.getOptionsICOM(commandCode);
			Util.logger.info("services:" + commandCode + " OPTION = " + options);
			if(options != null){
				if(!options.trim().equals("")){
					hOptionsICOM.put(commandCode, options);
				}
			}
		}
		return options;
	}
	
	public String getOptionVMS(String commandCode){
		String options = null;
		options = hOptionsVMS.get(commandCode);
		if(options == null){
			LoadMultiService loadObj = new LoadMultiService();
			options = loadObj.getOptionsVMS(commandCode);
			Util.logger.info("services:" + commandCode + " OPTION = " + options);
			if(options != null){
				if(!options.trim().equals("")){
					hOptionsVMS.put(commandCode, options);
				}
			}
		}
		return options;
	}
	
	
			
}
