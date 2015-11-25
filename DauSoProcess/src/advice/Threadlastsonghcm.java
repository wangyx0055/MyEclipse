package advice;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadlastsonghcm extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassLastSongHCM lastestsong = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS ");

		Threadlastsonghcm smsConsole = new Threadlastsonghcm();
		smsConsole.start();

	}

	public void run() {

		Util.logger.info("Starting ThreadCS");
		lastestsong = new RunClassLastSongHCM();
		lastestsong.start();

	}

}
