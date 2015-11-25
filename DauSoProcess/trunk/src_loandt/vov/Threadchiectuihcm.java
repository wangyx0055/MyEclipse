package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadchiectuihcm extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassChiectuihcm lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting Threadchiectui");

		Threadchiectuihcm smsConsole = new Threadchiectuihcm();

		smsConsole.start();

	}

	public void run() {
		
			Util.logger.info("Starting ThreadCS");
			lastestnumber = new RunClassChiectuihcm();
			lastestnumber.start();
		

	}

}
