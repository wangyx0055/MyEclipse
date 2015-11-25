package vov;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadnhandienhn extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassNhandienhn lastestnumber = null;

	public static void main(String[] args) {
		Threadnhandienhn smsConsole = new Threadnhandienhn();
		smsConsole.start();
	}

	public void run() {
		Util.logger.info("Starting Thread Nhan dien HN");
		lastestnumber = new RunClassNhandienhn();
		lastestnumber.start();
	}

}
