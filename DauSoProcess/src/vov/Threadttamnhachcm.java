package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadttamnhachcm extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassTtamnhachcm lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadTTAmNhac HCM");

		Threadttamnhachcm smsConsole = new Threadttamnhachcm();

		smsConsole.start();

	}

	public void run() {
		
			Util.logger.info("Starting ThreadTTAmNhac");
			lastestnumber = new RunClassTtamnhachcm();
			lastestnumber.start();
		

	}

}
