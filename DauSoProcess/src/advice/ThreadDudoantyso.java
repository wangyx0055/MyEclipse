package advice;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class ThreadDudoantyso extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassLastSong lastestsong = null;

	public static void main(String[] args) {
		System.out.println("Starting ThreadDudoantyso ");

		ThreadDudoantyso smsConsole = new ThreadDudoantyso();

		smsConsole.start();

	}

	public void run() {

		Util.logger.info("Starting ThreadDudoantyso");
		lastestsong = new RunClassLastSong();
		lastestsong.start();

	}

}
