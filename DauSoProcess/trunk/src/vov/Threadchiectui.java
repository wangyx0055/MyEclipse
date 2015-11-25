package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadchiectui extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassChiectui lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting Threadchiectui");

		Threadchiectui smsConsole = new Threadchiectui();

		smsConsole.start();

	}

	public void run() {
		
			Util.logger.info("Starting ThreadCS");
			lastestnumber = new RunClassChiectui();
			lastestnumber.start();
		

	}

}
