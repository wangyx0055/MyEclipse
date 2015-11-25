package icom.gateway;

import org.smpp.pdu.PDU;
import java.io.Serializable;

public class PDUData implements Serializable {
	private PDU pdu = null;
	private String requestID = "0";

	public void setPDU(PDU value) {
		this.pdu = value;
	}

	public PDU getPDU() {
		return this.pdu;
	}

	public void setRequestID(String value) {
		this.requestID = value;
	}

	public String getRequestID() {
		return this.requestID;
	}

}
