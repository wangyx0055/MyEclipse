package PushMT;

import DAO.MTDAO;
import icom.MsgObject;
import icom.MsgQueue;
import icom.Sender;
import icom.common.Util;




public class PushMt extends Thread {
	
	private int index;
	private MsgQueue queue;
	private String tableName;
	public PushMt (int index, MsgQueue queue){
		this.index = index;
		this.queue = queue;
		
	}
	@Override
	public void run(){
		
				
		Util.logger.info("PushMt start");
				
		while (Sender.processData) {
			MsgObject msgObject = null;
			try{
				//main thread
				if(!queue.isEmpty())
					msgObject = (MsgObject) queue.remove();
				if(msgObject == null){
					Thread.sleep(100);
					continue;
				}else{
					MTDAO.pustMtQueue(msgObject, index);					
				}
			}catch (Exception e) {
				Util.logger.error("PushMt @error:"+e);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}//end while
	}
}
