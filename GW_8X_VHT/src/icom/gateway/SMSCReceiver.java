package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */
import icom.common.Queue;

import org.smpp.*;
import org.smpp.pdu.*;


/**
 * For receiving PDUs from SMSC, in Sync mode --> Only request PDUs are
 * received.
 */
public class SMSCReceiver extends Thread {
	// private Logger logger = new Logger("SMSCReceiver");

	private PDU pdu = null;
	private DeliverSM dsm = null;
	private Session session = null;
	private Queue requestQueue = null;

	public SMSCReceiver(Session session, Queue requestQueue) {
		this.session = session;
		this.requestQueue = requestQueue;
	}

	public void run() {
		while (Gateway.running) {
			if (Gateway.bound) {
				try {
					pdu = session.receive(Preference.receiveTimeout);
					// pdu = session.receive();
					if (pdu != null && pdu.isValid()) {
						if (pdu.isRequest()) {
							// Logger.println2C("==> " + pdu.debugString());

							// Make default response
							Response response = ((Request) pdu).getResponse();

							// Reply with default response
							// Logger.println2C("<== " +
							// response.debugString());
							session.respond(response);

							// Add to requestQueue for further processing
							if (pdu.getCommandId() != Data.ENQUIRE_LINK) {
								if (pdu.getCommandId() == Data.DELIVER_SM) {
									dsm = (DeliverSM) pdu;

									// logger.printPDU(dsm);

									requestQueue.enqueue(pdu);
								} else {
									// System.out.println("Not a Deliver_SM: " +
									// pdu.debugString());
									Gateway.util.log(this.getClass().getName(),
											"Not a Deliver_SM: "
													+ pdu.debugString());
								}
							}
						} else if (pdu.isResponse()) {
							// Process response
							// System.out.println("received a response(?), while expect requests only.");
							Gateway.util
									.log(this.getClass().getName(),
											"received a response(?), while expect requests only.");
						} else {
							// System.out.println(
							// "pdu of unknown class (not request nor response) "
							// +
							// "received; Discarding " + pdu.debugString());
							Logger.info(this.getClass().getName(),
									"pdu of unknown class (not request nor response) "
											+ "received; Discarding "
											+ pdu.debugString());
						}
					} else {
						// System.out.println("Received an invalid pdu!");

						Logger.info(this.getClass().getName(),
								"Received an invalid pdu!");
					}
				} catch (Exception ex) {
					// System.out.println(">>>Receiver");
					// System.out.println("run: " + ex.getMessage());
					Logger.info(this.getClass().getName(), "Exception:"
							+ ex.getMessage());
				}

			} else {
				Logger.info(this.getClass().getName(), "Delay-receiver");
				try {
					sleep(5*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
				}
				Logger.info(this.getClass().getName(), "Delay-receiver");
			}
		}
	}
}
