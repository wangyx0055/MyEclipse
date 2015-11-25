package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */


public class CheckStatusThread extends Thread {

	public CheckStatusThread() {

	}

	public void run() {
		// ///////////////////////////

		while (Gateway.running) {

			if (Gateway.isAllThreadStarted) {
				
				Gateway.checkstatus_thread();
			}
			try {
				sleep(5*60 * 1000);
			} catch (InterruptedException ex) {
			}
		} // while

	}

}
