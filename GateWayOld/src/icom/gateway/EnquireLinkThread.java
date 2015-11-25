package icom.gateway;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.smpp.*;
import org.smpp.pdu.*;

public class EnquireLinkThread extends Thread {
	// private Logger logger = new Logger("EnquireLinkThread");

	private Gateway gateway = null;

	public EnquireLinkThread(Gateway gateway) {
		this.gateway = gateway;
	}

	public void run() {
		while (Gateway.running) {
			try {
				this.sleep(Preference.timeEnquireLink);
			} catch (InterruptedException ex) {
			}
			if (Gateway.bound) // or Gateway.running (since the gateway may
				// stopped after sleeping)
				this.enquireLink();
		}
	}

	/**
	 * Creates a new instance of <code>EnquireSM</code> class. This PDU is
	 * used to check that application level of the other party is alive. It can
	 * be sent both by SMSC and ESME.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.11 ENQUIRE_LINK Operation."
	 * 
	 * @see Session#enquireLink(EnquireLink)
	 * @see EnquireLink
	 * @see EnquireLinkResp
	 */
	private void enquireLink() {
		try {
			EnquireLink request = new EnquireLink();
			EnquireLinkResp response;
			Logger.info(this.getClass().getName(), "<== "
					+ request.debugString());

			if (Preference.asynchronous) {
				Gateway.session.enquireLink(request);
			} else {
				response = Gateway.session.enquireLink(request);
				Logger.info(this.getClass().getName(), "==> "
						+ response.debugString());

			}
		} catch (Exception ex) {
			Logger.crisis(this.getClass().getName(), "enquireLink: "
					+ ex.getMessage());
			
			if ( Logger.isverbose()) 
			DBTools.ALERT("enquireLink", "enquireLink.Exception",
					Constants.ALERT_SERIOUS, Preference.Channel
							+ "Exception: " + ex.getMessage()
							+ "\n"
							+ new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss")
									.format(new Date()),
					Preference.ALERT_CONTACT);

			if (Gateway.running) {
				Gateway.bound = false;
				Gateway.boundr = false;
				Logger.info(this.getClass().getName(), "Start rebind....");
				gateway.bind();
				//gateway.rebound=true;
				//gateway.rebind();
			}
		}
	}
}
