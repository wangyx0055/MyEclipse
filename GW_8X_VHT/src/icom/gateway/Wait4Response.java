package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import java.math.BigDecimal;
import java.sql.Timestamp;
/**
 * This class represents a segment of EMSData which is sent and now waiting
 * for response. EMSData is splitted into segments, then each is packaged
 * in a PDU (SubmitSM), sent via SMPP.
 *
 * Each Wait4Response object contain information for a segment, including:
 *   + Id of EMSData (EMS_SEND_QUEUE).
 *   + SeqNumber of segment (SeqNum of SubmitSM pdu formed by "<EMS_ID>" + <segment_seqNum>)
 *   + Time created
 *
 * This object is stored in the HashMap Table: key=<pdu_SeqNum>, value=<Wait4Response Object>
 */
public class Wait4Response {
    final static long RESPONSE_TIMEOUT = 60*1000;//1min

    BigDecimal emsId;
    int totalSegments;
    int seqNum; //1, 2,...n (n=1-5:max)
    Timestamp time;

    public Wait4Response() {
        this.emsId = null;
        this.time  = new Timestamp(System.currentTimeMillis());
    }

    public Wait4Response(BigDecimal emsId) {
        this.emsId = emsId;
        this.time  = new Timestamp(System.currentTimeMillis());
    }

    public Wait4Response(BigDecimal emsId, int totalSegments, int seqNum) {
        this.emsId  = emsId;
        this.totalSegments = totalSegments;
        this.seqNum = seqNum;
        this.time   = new Timestamp(System.currentTimeMillis());
    }

    public boolean isLastSegment() {
        if (seqNum == totalSegments) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isTimeout() {
        long currTime = System.currentTimeMillis();
        if ((currTime - time.getTime()) > RESPONSE_TIMEOUT) {
            return true;
        }
        return false;
    }
}
