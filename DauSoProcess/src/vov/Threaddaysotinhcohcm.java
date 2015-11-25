package vov;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threaddaysotinhcohcm extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassDaysotinhcoHCM lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS");

		Threaddaysotinhcohcm smsConsole = new Threaddaysotinhcohcm();

		smsConsole.start();

	}

	public void run() {

		
			Util.logger.info("Starting ThreadCS");
			lastestnumber = new RunClassDaysotinhcoHCM();
			lastestnumber.start();
		

	}

}
