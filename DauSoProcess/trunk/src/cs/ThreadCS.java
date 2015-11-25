package cs;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class ThreadCS extends Thread {

	MsgQueue queueADV = new MsgQueue();
	LastestCS lastestcs = null;
	ExecuteADVCR executeADVCR = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS ");

		ThreadCS smsConsole = new ThreadCS();

		smsConsole.start();

	}

	public void run() {

		Util.logger.info("Starting ThreadCS");
		lastestcs = new LastestCS();
		lastestcs.start();

		while (!lastestcs.CS_LOADED) {
			try {
				System.out.println("Loading.........");
				sleep(50);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		executeADVCR = new ExecuteADVCR(queueADV);
		executeADVCR.start();

	}

}
