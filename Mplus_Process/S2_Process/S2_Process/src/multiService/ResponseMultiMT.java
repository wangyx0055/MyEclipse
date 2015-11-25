package multiService;

import java.util.ArrayList;
import icom.Constants;
import icom.Sender;
import icom.common.Util;

public class ResponseMultiMT extends Thread{
	
	@Override
	public void run(){
		LoadMultiService loadObj = new LoadMultiService();
				
		while(Sender.getData){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			
			ArrayList<MapMultiSerivce> arrMapMulti = loadObj.getMapMultiService();
			
			for(int iMap = 0;iMap< arrMapMulti.size(); iMap ++){
				MapMultiSerivce mapObj = arrMapMulti.get(iMap);
				
				ArrayList<MTQueueObj> arrMTMulti = null;
				arrMTMulti = loadObj.getMTMultiByMOId(mapObj.getMoId());
				
				String services = "";
				
				if(arrMTMulti.size() == mapObj.getNumberService()){
					Util.logger.info("MultiService ResponseMultiMT ##" +
							" Number Service DK/HUY = " + arrMTMulti.size());
					
					for(int j = 0;j<arrMTMulti.size();j++){
						MTQueueObj mtMulti = arrMTMulti.get(j);
						if(j == 0){
							services = mtMulti.getCommandCode();
						}else{
							services = services + "," + mtMulti.getCommandCode();
						}
						
						Util.logger.info("MultiService ResponseMultiMT ## DK/HUY thanh cong service" + j 
								+ " " + mtMulti.getCommandCode());
						
						loadObj.deleteByID(mtMulti.getID(), "mt_queue_multi");
					}
					
					MTQueueObj mtInsert = arrMTMulti.get(0);
										
					if(mapObj.getKeyword().trim().equals("DK")){
						mtInsert.setInfo(loadObj.getMTInfoReg(services));
						mtInsert.setCommandCode("DK MULTI");
					}else if(mapObj.getKeyword().trim().equals("HUY")){
						mtInsert.setInfo(loadObj.getMTInfoDestry(services));
						mtInsert.setCommandCode("HUY MULTI");
					}
					
					mtInsert.setChanelType(13);
					loadObj.insertMTQueue(mtInsert, Constants.tblMTQueue);
					
					loadObj.deleteByID(mapObj.getID(), LoadMultiService.tblMapMulti);
				}
																				
			}
			
		
						
		}
	}
}
