package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadttamnhac extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassTtamnhac lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadTTAmNhac");

		Threadttamnhac smsConsole = new Threadttamnhac();

		smsConsole.start();

	}

	public void run() {
		
			Util.logger.info("Starting ThreadTTAmNhac");
			lastestnumber = new RunClassTtamnhac();
			lastestnumber.start();
		

	}

}
