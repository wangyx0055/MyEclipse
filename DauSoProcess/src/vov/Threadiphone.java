package vov;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgQueue;

public class Threadiphone extends Thread {

	MsgQueue queueADV = new MsgQueue();
	public static RunClassIphone lastestnumber = null;

	public static void main(String[] args) {
		Threadiphone smsConsole = new Threadiphone();

		smsConsole.start();

	}

	public void run() {
		
			Util.logger.info("Starting Thread Iphone");
			lastestnumber = new RunClassIphone();
			lastestnumber.start();
		

	}

}
