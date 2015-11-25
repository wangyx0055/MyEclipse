package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threaddautri extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassDautri lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS");

		Threaddautri smsConsole = new Threaddautri();

		smsConsole.start();

	}

	public void run() {
		
			Util.logger.info("Starting ThreadCS");
			lastestnumber = new RunClassDautri();
			lastestnumber.start();
		

	}

}
