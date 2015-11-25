package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class ThreaddautriHCM extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassDautriHCM lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS");

		ThreaddautriHCM smsConsole = new ThreaddautriHCM();

		smsConsole.start();

	}

	public void run() {

		
			Util.logger.info("Starting ThreaddautriHCM");
			
			
			lastestnumber = new RunClassDautriHCM();
			lastestnumber.start();
		

	}

}
