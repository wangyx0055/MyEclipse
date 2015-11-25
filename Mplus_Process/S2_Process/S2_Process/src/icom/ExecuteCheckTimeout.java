/**
 * 
 */
package icom;

import icom.common.Util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

/**
 * @author conglt
 * 
 */
public class ExecuteCheckTimeout extends Thread {

	private MsgQueue queueLog = null;
	private Map mapResponse = null;

	private MsgQueue queueResponseReceive = null;
	private MsgQueue queueTimeout = null;

	public ExecuteCheckTimeout(MsgQueue queuelog, Map mapresponse,
			MsgQueue queueresponseReceive, MsgQueue queueTimeout) {
		this.queueLog = queuelog;
		this.mapResponse = mapresponse;
		this.queueResponseReceive = queueresponseReceive;
		this.queueTimeout = queueTimeout;
	}

	/**
	 * @param args
	 */

	public static void main(String[] args) {

	}

	public boolean isTimeOut(Timestamp time) {
		long currTime = System.currentTimeMillis();
		if ((currTime - time.getTime()) > Constants.TIME_OUT * 20 * 1000) {
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		// ///////////////////////////

		while (Sender.processData) {

			if (mapResponse.size() > 0) {
				try {
					Thread.sleep(Constants.TIME_OUT * 10 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (Sender.processData) {
					// Not over Max messages in queue
					synchronized (mapResponse) {

						for (Enumeration e = new Vector(mapResponse.keySet())
								.elements(); e.hasMoreElements();) {

							if (!Sender.processData) {
								break;
							}

							String key = (String) e.nextElement();

							MsgObject msgobj = (MsgObject) mapResponse.get(key);
							if (msgobj == null)
								continue;
							if (isTimeOut(msgobj.getTSubmitTime())) {
								// msgobj.setProcess_result(Constants.TIME_OUT);
								String currTime = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss")
										.format(new Date());
								msgobj.setMsgNotes(1, currTime
										+ ":No ACK return for this message!!!");
								// queueLog.add(msgobj); se insert vao
								// mt_send_queue
								// nen ko add2log nua
								mapResponse.remove(key);

								Util.logger
										.info("No ACK return for this message:"
												+ msgobj.getUserid() + "@"
												+ msgobj.getMsg_id());

								// DBUtil.Alert("CheckTimeout", "No ACK return",
								// "warn", "No ACK return for message",
								// "conglt-0963536888");

								Keyword keyword = Sender.loadconfig.getKeyword(
										msgobj.getKeyword().toUpperCase(),
										msgobj.getServiceid());

								long lamount = 0;

								if ((msgobj.getMsgtype() + "")
										.equals(Constants.MT_CHARGING)) {

									lamount = keyword.getAmount();

									Prices price = Sender.loadconfig
											.getPrice(keyword
													.getService_ss_id());

									if (price != null) {
										lamount = price.getPrice_new();
									}
								}

								msgobj.setAmount(lamount);

								msgobj.setMsgNotes(1, currTime
										+ ":add2:mt_send_queue&mt_timeout");

								queueResponseReceive.add(msgobj);
								queueTimeout.add(msgobj);
							}

						}

					}
				}
			}// end if
			else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}// end while

	}

}
