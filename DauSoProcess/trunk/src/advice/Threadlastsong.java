package advice;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadlastsong extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassLastSong lastestsong = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadCS ");

		Threadlastsong smsConsole = new Threadlastsong();

		smsConsole.start();

	}

	public void run() {

		Util.logger.info("Starting ThreadCS");
		lastestsong = new RunClassLastSong();
		lastestsong.start();

	}

}
