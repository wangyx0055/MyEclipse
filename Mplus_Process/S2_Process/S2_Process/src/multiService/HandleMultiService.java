package multiService;

import java.util.ArrayList;

import icom.MsgObject;
import icom.MsgQueue;
import icom.Sender;


public class HandleMultiService extends Thread {
	
	MsgQueue queue = null;
	
	ContentMng contentMng = ContentMng.getInstance();
	MsgObject msgObj = null;
	LoadMultiService loadMultiObj = new LoadMultiService();
	
	public HandleMultiService(MsgQueue _queue){
		this.queue = _queue;
	}
	
	@Override
	public void run(){
				
		while(Sender.getData){
			ArrayList<MsgObject> arrMO = null;
			arrMO = loadMultiObj.getMO();
			
			for(int i = 0;i<arrMO.size();i++){
				msgObj = null;
				msgObj = arrMO.get(i);
				
				// write to Log
				loadMultiObj.insertToMOLog(msgObj);
				String userText = msgObj.getUsertext();	
				
				handle(userText);
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}
	
	public void handle(String userText){
				
		if(userText.startsWith("DK")){
			handleRegistry(userText);
		}else if(userText.startsWith("HUY")){
			handleDestroy(userText);
		}
		
	}
	
	public void handleRegistry(String userText){
		
		userText = userText.substring(2);
			
		String[] services = userText.split(",");
		int numberService = services.length;
		for(int i = 0;i<numberService;i++){
			
			String commandCode = services[i].trim();
			
			MsgObject msgInsert = new MsgObject();
			msgInsert.setCommandCode(commandCode);
			msgInsert.setMultiService(1);
			msgInsert.setServiceName(commandCode);						
			msgInsert.setUsertext("DK " + commandCode);	
			msgInsert.setRequestid(msgObj.getRequestid());
			msgInsert.setServiceid(msgObj.getServiceid());
			msgInsert.setUserid(msgObj.getUserid());
			msgInsert.setMobileoperator(msgObj.getMobileoperator());
			msgInsert.setObjtype(msgObj.getObjtype());
			msgInsert.setMoId(msgObj.getMoId());
			
			queue.add(msgInsert);
			
		}
		
		// VMS
		
		msgObj.setCommandCode("DK");
		if(! msgObj.getCommandCode().equals(LoadMultiService.ICOM_MULTI)){
			int moId = 1;
			try{
				moId = Integer.parseInt(msgObj.getRequestid()+"");
			}catch(Exception ex){}
			loadMultiObj.insertMapMulti(msgObj.getUserid(), moId, numberService, "DK");
		}
		
	}
	
	public void handleDestroy(String userText){
		
		userText = userText.substring(3);
		
		String[] services = userText.split(",");
		int numberService = services.length;
		
		for(int i = 0;i<services.length;i++){
			String commandCode = services[i].trim();
			
			MsgObject msgInsert = new MsgObject();
			msgInsert.setCommandCode(commandCode);
			msgInsert.setMultiService(1);
			msgInsert.setServiceName(commandCode);						
			msgInsert.setUsertext("HUY " + commandCode);	
			msgInsert.setRequestid(msgObj.getRequestid());
			msgInsert.setServiceid(msgObj.getServiceid());
			msgInsert.setUserid(msgObj.getUserid());
			msgInsert.setMobileoperator(msgObj.getMobileoperator());
			msgInsert.setObjtype(msgObj.getObjtype());
			msgInsert.setMoId(msgObj.getMoId());
			
			queue.add(msgInsert);
			
		}
		// VMS
		msgObj.setCommandCode("HUY");
		if(! msgObj.getCommandCode().equals(LoadMultiService.ICOM_MULTI)){
			
			int moId = 1;
			try{
				moId = Integer.parseInt(msgObj.getRequestid()+"");
			}catch(Exception ex){}
			
			loadMultiObj.insertMapMulti(msgObj.getUserid(), moId, numberService, "HUY");
			
		}
	}
	
}
