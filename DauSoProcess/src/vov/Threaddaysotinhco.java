package vov;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threaddaysotinhco extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassDaysotinhco lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS");

		Threaddaysotinhco smsConsole = new Threaddaysotinhco();

		smsConsole.start();

	}

	public void run() {

		
			Util.logger.info("Starting ThreadCS");
			lastestnumber = new RunClassDaysotinhco();
			lastestnumber.start();
		

	}

}
