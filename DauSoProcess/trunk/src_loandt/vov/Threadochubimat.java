package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadochubimat extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassOchubimat lastestnumber = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS");

		Threadochubimat smsConsole = new Threadochubimat();

		smsConsole.start();

	}

	public void run() {
		
			Util.logger.info("Starting RunClassOchubimat");
			lastestnumber = new RunClassOchubimat();
			lastestnumber.start();
		

	}

}
