package daugia;

import icom.Sender;

public class LoadSanPhamDG extends Thread{
	
	@Override
	public void run(){
		
		while(Sender.getData){
			SanPhamDGManager spMng = SanPhamDGManager.getInstance();
			spMng.getLastestSPDauGia();
			
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}						
			
		}
		
	}

}
