package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadochubimathcm extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassOchubimathcm lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS");

		Threadochubimathcm smsConsole = new Threadochubimathcm();

		smsConsole.start();

	}

	public void run() {
		
			Util.logger.info("Starting RunClassOchubimat");
			lastestnumber = new RunClassOchubimathcm();
			lastestnumber.start();
		

	}

}
