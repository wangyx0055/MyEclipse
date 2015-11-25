package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright:(c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import org.smpp.*;
import org.smpp.pdu.*;

import icom.common.Queue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An implementation of a PDU listener which handles PDUs received from SMSC, in
 * <b>Asynchronous</b> mode. It puts the received requests into a queue and
 * discards all received responses. Requests then can be fetched (should be)
 * from the queue by calling to the method <code>getRequestEvent</code>.
 */
public class PDUEventListener extends SmppObject implements
		ServerPDUEventListener {
	// private Logger logger = new Logger("PDUEventListener");

	private Queue requestQueue = null;
	private Queue responseQueue = null;
	private Queue deliveryQueue = null;
	private Queue toSMSC = null;

	private PDU pdu = null;
	private DeliverSM dsm = null;
	private SubmitSMResp ssmr = null;

	public PDUEventListener(Queue requestQueue, Queue responseQueue,
			Queue deliveryQueue, Queue toSMSC) {
		this.requestQueue = requestQueue;
		this.responseQueue = responseQueue;
		this.deliveryQueue = deliveryQueue;
		this.toSMSC = toSMSC;
	}

	/**
	 * Means to process PDUs received from the SMSC. This method is called by
	 * the <code>Receiver</code> whenever a PDU is received from the SMSC.
	 * 
	 * @param request
	 *            the request received from the SMSC.
	 */
	public void handleEvent(ServerPDUEvent event) {
		PDU pdu = event.getPDU();
		//System.out.println("pdu.ServerPDUEvent()" + pdu.debugString());
		if (pdu.isValid()) {
			if (pdu.isRequest()) {
				// Logger.println("==> " + pdu.debugString());

				// Make default response
				Response response = ((Request) pdu).getResponse();
				this.toSMSC.enqueue(response);
				if (pdu.getCommandId() != Data.ENQUIRE_LINK) {
					this.processRequest(pdu);
				}

			} else if (pdu.isResponse()) {
				// System.out.println("pdu.getCommandId()" +
				// pdu.getCommandId());
				// System.out.println("Data.ENQUIRE_LINK_RESP" +
				// Data.ENQUIRE_LINK_RESP);
				if (pdu.getCommandId() != Data.ENQUIRE_LINK_RESP)
					this.responseQueue.enqueue(pdu);
				// Gateway.util.log(this.getClass().getName(),"{Respond for
				// MT}{MessageId=" + pdu.getSequenceNumber() +"}");
			} else {
				//System.out
				//		.println("pdu of unknown class (not request nor response) "
				//				+ "received; Discarding " + pdu.debugString());

				DBTools.ALERT("PDUEventListener", "PDUEventListener",
						Constants.ALERT_WARN,
						"pdu of unknown class (not request nor response) "
								+ "received; Discarding "
								+ pdu.debugString()
								,
						Preference.ALERT_CONTACT);
			}
		} else {
			//System.out.println("Received an invalid pdu!");

			DBTools.ALERT("PDUEventListener", "PDUEventListener",
					Constants.ALERT_WARN, "Received an invalid pdu!"
							,
					Preference.ALERT_CONTACT);
		}
	}

	// ===================================================================
	private void processRequest(PDU pdu) {
		try {
			switch (pdu.getCommandId()) {
			case Data.DELIVER_SM:
				dsm = (DeliverSM) pdu;
				if (dsm.getEsmClass() == 0x04) {
					// this.deliveryQueue.enqueue(pdu);
					Logger.info(this.getClass().getName(),
							"dsm.getEsmClass() == 0x04");
				} else {
					DateFormat dateFormat = new SimpleDateFormat(
							"yyMMddHHmmssSSS");
					java.util.Date date = new java.util.Date();
					String datetime = dateFormat.format(date);

					PDUData pd = new PDUData();
					pd.setPDU(pdu);
					pd.setRequestID(Preference.prefix_requestid + datetime);

					this.requestQueue.enqueue(pd);
					// dsm.setSourceAddr(new Address("095"));
					String userid = dsm.getSourceAddr().getAddress();

					dsm.setSourceAddr(Preference.formatUserIdMO(userid,
							Constants.USERID_FORMAT_INTERNATIONAL));
					
					

					String dsmLog = "{MO-comes}{Request_ID="
							+ Preference.prefix_requestid + datetime
							+ "}{UserID=" + dsm.getSourceAddr().getAddress()
							+ "}{ServiceID=" + dsm.getDestAddr().getAddress()
							+ "}{Info=" + dsm.getShortMessage() + "}";
					Logger.info(this.getClass().getName(), dsmLog);

				}
				break;
			case Data.DATA_SM:
				// System.out.println(" Data_SM --> Not processed.");
				Logger.error(this.getClass().getName(),
						"  Data_SM --> Not processed.");
				break;
			case Data.UNBIND:
				Logger.info(this.getClass().getName(),
						"  Data.UNBIND --> Not processed.");
				this.requestQueue.enqueue(pdu);
				break;
			default:
				Logger.error("processRequest: Unspecified SM "
						+ pdu.debugString());

			}
		} catch (Exception e) {

			Logger
					.error(this.getClass().getName(), "Exception "
							+ e.toString());
			
			DBTools.ALERT("PDUEventListener", "processRequest",
					Constants.ALERT_WARN, Preference.Channel
							+ "@processRequest:" + e.toString(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
		}
	}

	private void saveToFile(String pduFile, PDU pdu) {
		Logger.info(" Saving PDU into file " + pduFile);
		try {
			byte[] b = pdu.getData().getBuffer();
			java.io.FileOutputStream fout = new java.io.FileOutputStream(
					pduFile);
			fout.write(b);
			fout.flush();
			fout.close();
		} catch (Exception ex) {
			Logger.error("saveToFile:" + ex.getMessage());
		}
	}

	private String assignMessageId() {
		String messageId = "Smsc";
		// intMessageId++;
		// messageId += intMessageId;
		return messageId;
	}

}
