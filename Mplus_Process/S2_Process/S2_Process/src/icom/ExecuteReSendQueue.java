package icom;

import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Hashtable;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class ExecuteReSendQueue extends Thread {

	int threadID = 0;
	static MsgQueue queue = null;

	BigDecimal AM = new BigDecimal(-1);

	public ExecuteReSendQueue(MsgQueue queue, int threadID) {
		ExecuteReSendQueue.queue = queue;

		this.threadID = threadID;
	}

	public static void add2queueResend(MsgObject msgObject) {
		queue.add(msgObject);
	}

	@Override
	public void run() {

		MsgObject msgObject = null;
		String serviceId = "";
		String info = "";

		String process_result = "";

		try {
			sleep(50);
		} catch (InterruptedException ex1) {
		}

		while (Sender.processData) {
			process_result = "";
			try {
				if(queue.getSize()==0)
				{
					sleep(60 * 1000);
				}
				msgObject = (MsgObject) queue.remove();
				if (isTimeResend(msgObject.getTTimes())) {
					serviceId = msgObject.getServiceid();
					info = msgObject.getUsertext();

					// Keyword keyword = Sender.loadconfig.getKeyword(info,
					// serviceId);

					String sgetkeyword = info;

					if (msgObject.getObjtype() != 0) {
						sgetkeyword = msgObject.getKeyword();

					}

					Keyword keyword = Sender.loadconfig.getKeyword(sgetkeyword
							.toUpperCase(), serviceId);

					if (msgObject.getObjtype() == 0) {
						msgObject.setKeyword(keyword.getKeyword());
					}

					msgObject.setRetries_num(msgObject.getRetries_num() + 1);
					
					
					process_result = processQueueMsg(msgObject, keyword ,LoadConfig.hServices);
				} else {
					queue.add(msgObject);
				}
				sleep(5 * 100);

			} catch (Exception ex) {
				Util.logger.error("Execute queue resend queue. Ex:" + ex.toString());
				queue.add(msgObject);

			}

		}

	}

	public boolean isTimeResend(Timestamp time) {
		long currTime = System.currentTimeMillis();
		if ((currTime - time.getTime()) > 30 * 1000) {
			return true;
		}
		return false;
	}

	private String processQueueMsg(MsgObject msgObject, Keyword keyword, Hashtable services) {

		try {
			QuestionManager delegate = null;
			// String classname = "com.vmg.sms.process.SoapQM";
			// String classname = keyword.getClassname();
			String classname = "com.vmg.sms.process.SoapQM";
			// String classname = keyword.getClassname();
			if (msgObject.getObjtype() == 0) {
				classname = keyword.getClass_mo();
			} else {
				classname = keyword.getClass_mt();
			}
			Util.logger.info("{processReQueueMsg}{"
					+ ((msgObject.getObjtype() == 0) ? "MO" : "MT")
					+ "}@resend@" + msgObject.getUserid() + "@"
					+ msgObject.getUsertext() + "@"
					+ msgObject.getRequestid().toString() + "@" + classname);

			Class delegateClass = Class.forName(classname);
			Object delegateObject = delegateClass.newInstance();
			delegate = (QuestionManager) delegateObject;

			delegate.start(Constants._prop, msgObject, keyword, services);
			return "OK";

		} catch (Exception e) {
			Util.logger.crisis("processReQueueMsg:" + msgObject.getUserid()
					+ "@" + msgObject.getUsertext() + "@"
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return msgObject.getUserid() + ":" + e.toString();
		}

	}

}
