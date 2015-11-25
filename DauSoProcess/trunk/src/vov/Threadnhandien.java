package vov;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadnhandien extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassNhandien lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting Thread Nhan dien");

		Threadnhandien smsConsole = new Threadnhandien();

		smsConsole.start();

	}

	public void run() {

			Util.logger.info("Starting ThreadCS");
			lastestnumber = new RunClassNhandien();
			lastestnumber.start();
	}

}
