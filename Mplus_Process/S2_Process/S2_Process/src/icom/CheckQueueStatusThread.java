package icom;

import icom.common.DBUtil;
import java.util.HashMap;
import java.util.Map;

public class CheckQueueStatusThread extends Thread {

	String SEND_MSG = "";
	long lasttimealert = 0;

	Map mapResponse = new HashMap();
	MsgQueue queueResponse = new MsgQueue();
	MsgQueue queueRequest = new MsgQueue();
	MsgQueue queue = new MsgQueue();
	MsgQueue queueMO1 = new MsgQueue();
	MsgQueue queueResend = new MsgQueue();
	MsgQueue queueLog = new MsgQueue();

	public CheckQueueStatusThread(Map mapresponse, MsgQueue queueresponse,
			MsgQueue queuerequest, MsgQueue _queue, MsgQueue queuemo1,
			MsgQueue resend, MsgQueue log) {

		mapResponse = mapresponse;
		queueResponse = queueresponse;
		queueRequest = queuerequest;
		queue = _queue;
		queueMO1 = queuemo1;
		queueResend = resend;
		queueLog = log;

	}

	@Override
	public void run() {
		// ///////////////////////////

		lasttimealert = System.currentTimeMillis();

		while (Sender.processData) {
			try {

				if (Sender.LOADED) {

					
					//savemapResponse();
				//	saveSMSDataTable("data.dat", queue);
					//saveSMSDataTable("resend.dat", queueResend);
					//saveSMSDataTable("log.dat", queueLog);
					//saveSMSDataTable("response.dat", queueResponse);
					//saveSMSDataTable("mo1.dat", queueMO1);
					//saveSMSDataTable("request.dat", queueRequest);
					//saveSMSDataTable("responsereq.dat", queueResponseReceive);
					//Util.logger.info("Mapresponse:" + mapResponse.size());
					//Util.logger.info("queueResend:" + queueResend.getSize());
					//Util.logger.info("queueLog:" + queueLog.getSize());
					//Util.logger.info("queueRequest:" + queueRequest.getSize());
					//Util.logger.info("queueMO1:" + queueMO1.getSize());
					//Util.logger.info("queue:" + queue.getSize());
					
					
					if (queue.getSize() > 10) {
						long currenttime = System.currentTimeMillis();

						if ((currenttime - lasttimealert) > (1000 * 60 * 5)) {
							lasttimealert = currenttime;
							SEND_MSG = "URGENT: PROCESS S2 QUEUE: "
									+ queue.getSize();

							DBUtil.Alert("Trafic", "Trafic", "major", SEND_MSG,
									"processAdmin");
						}
					}

					if (queueRequest.getSize() > 10) {
						long currenttime = System.currentTimeMillis();

						if ((currenttime - lasttimealert) > (1000 * 60 * 5)) {
							lasttimealert = currenttime;
							SEND_MSG = "URGENT: PROCESS S2 queueRequest: "
									+ queueRequest.getSize();

							DBUtil.Alert("Trafic", "Trafic", "major", SEND_MSG,
									"processAdmin");
						}
					}
					if (queueMO1.getSize() > 10) {
						long currenttime = System.currentTimeMillis();

						if ((currenttime - lasttimealert) > (1000 * 60 * 5)) {
							lasttimealert = currenttime;
							SEND_MSG = "URGENT: PROCESS S2 queueMO1: "
									+ queueMO1.getSize();

							DBUtil.Alert("Trafic", "Trafic", "major", SEND_MSG,
									"processAdmin");
						}
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				sleep(20 * 1000);
			} catch (InterruptedException ex) {
			}
		} // while

	}

}
