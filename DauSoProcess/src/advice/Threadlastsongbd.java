package advice;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadlastsongbd extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassLastSongbd lastestsong = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS ");

		Threadlastsongbd smsConsole = new Threadlastsongbd();

		smsConsole.start();

	}

	public void run() {

		Util.logger.info("Starting Thread Lastsong BD");
		lastestsong = new RunClassLastSongbd();
		lastestsong.start();

	}

}
