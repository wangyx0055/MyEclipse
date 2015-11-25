package com.vmg.sms.process;

import java.math.BigDecimal;

import com.vmg.sms.common.Util;
import com.vmg.sms.common.Utilities;

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
public class ExecuteQueue extends Thread {

	int threadID = 0;
	MsgQueue queue = null;
	MsgQueue queueLog = null;

	BigDecimal AM = new BigDecimal(-1);

	public ExecuteQueue(MsgQueue queue, MsgQueue queueLog, int threadID) {
		this.queue = queue;
		this.queueLog = queueLog;
		this.threadID = threadID;
	}

	public void run() {

		MsgObject msgObject = null;
		String serviceId = "";
		String info = "";
		Keyword keyword = null;
		String process_result = "";

		try {
			sleep(1500);
		} catch (InterruptedException ex1) {
		}

		while (ConsoleSRV.processData) {
			process_result = "";
			try {
				msgObject = (MsgObject) queue.remove();
				serviceId = msgObject.getServiceid();
				info = msgObject.getUsertext();
				keyword = ConsoleSRV.loadconfig.getKeyword(info, serviceId);

				if (Constants.INV_KEYWORD
						.equalsIgnoreCase(keyword.getKeyword())) {
					keyword = ConsoleSRV.loadconfig.getKeywordInvalid(info,
							serviceId);
					if (!Constants.INV_KEYWORD.equalsIgnoreCase(keyword
							.getKeyword())) {
						String newinfo = Utilities.replaceWhiteLetter(info);
						msgObject.setUsertext(newinfo);
						Util.logger.info("{userid=" + msgObject.getUserid()
								+ "}{info_old=" + info + "}{info_new="
								+ newinfo + "}");
					} else {
						keyword = ConsoleSRV.loadconfig.getKeywordInvalidLast(
								info, serviceId);

						if (!Constants.INV_KEYWORD.equalsIgnoreCase(keyword
								.getKeyword())) {
							String newinfo = Utilities.replaceWhiteLetter(info);
							newinfo = newinfo.replace(".", "");
							newinfo = newinfo.replace(" ", "");

							msgObject.setUsertext(newinfo);
							Util.logger.info("{userid=" + msgObject.getUserid()
									+ "}{info_old=" + info + "}{info_new="
									+ newinfo + "}");
						}

					}
				}

				msgObject.setKeyword(keyword.getKeyword());
				msgObject.setCpid(keyword.getCpid());
				process_result = processQueueMsg(msgObject, keyword);
				// if (!OK.equalsIgnoreCase(process_result)) {
				// queue.add(msgObject);
				// }
				// else {
				msgObject.setMsgNotes(process_result);

				ConsoleSRV.incrementAndGet_process(msgObject
						.getMobileoperator());

				queueLog.add(new MsgObject(serviceId, msgObject.getUserid(),
						keyword.getKeyword(), info, msgObject.getRequestid(),
						msgObject.getTTimes(), msgObject.getMobileoperator(),
						0, 0, msgObject.getCpid(), msgObject.getMsgnotes()));
				// }

			} catch (Exception ex) {
				Util.logger.error("Execute queue. Ex:" + ex.toString());
				queue.add(msgObject);

			}

		}

	}

	private String processQueueMsg(MsgObject msgObject, Keyword keyword) {
		ContentAbstract delegate = null;
		try {
			String classname = "";
			if (keyword.getClassname().startsWith("~")) {
				classname = "com.vmg.sms.process.ChatSMS";
				// Cau hinh tra loi luon
				// $msgtype$info
				String sInfo = keyword.getClassname();
				String[] arrInfo = sInfo.split("~");
				String mtreply = "";
				int msgtype = 2;
				if (arrInfo.length > 2) {
					mtreply = arrInfo[2];
					if (Constants.MT_CHARGING.equals(arrInfo[1])) {
						msgtype = 1;
					} else if (Constants.MT_PUSH.equals(arrInfo[1])) {
						msgtype = 3;
					} else if (Constants.MT_REFUND_SYNTAX.equals(arrInfo[1])) {
						msgtype = 21;
					} else if (Constants.MT_REFUND_CONTENT.equals(arrInfo[1])) {
						msgtype = 22;
					} else {
						msgtype = 2;
					}

				} else {
					mtreply = arrInfo[1];
					msgtype = 2;
				}
				msgObject.setUsertext(mtreply);
				msgObject.setMsgtype(msgtype);

			} else {
				classname = keyword.getClassname();

			}
			Util.logger.info("processQueueMsg:" + msgObject.getUserid() + "@"
					+ msgObject.getUsertext() + "@"
					+ msgObject.getRequestid().toString() + "@" + classname);
			Class delegateClass = Class.forName(classname);
			Object delegateObject = delegateClass.newInstance();
			delegate = (ContentAbstract) delegateObject;

			delegate.start(msgObject, keyword);
			return "OK";

		} catch (Exception e) {
			Util.logger.crisis("processQueueMsg:" + msgObject.getUserid() + "@"
					+ msgObject.getUsertext() + "@"
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return msgObject.getUserid() + ":" + e.toString();
		}

	}

}
