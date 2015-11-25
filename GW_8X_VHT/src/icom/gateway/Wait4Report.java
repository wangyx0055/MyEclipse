package icom.gateway;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Wait4Report {
    final static long WAIT_REPORT_TIMEOUT = 24*60*60*1000;//24hours

    BigDecimal logId = null;
    Timestamp  time  = null;

    public Wait4Report() {
        this.logId = null;
        this.time  = new Timestamp(System.currentTimeMillis());
    }

    public Wait4Report(BigDecimal logId) {
        this.logId = logId;
        this.time  = new Timestamp(System.currentTimeMillis());
    }

    public Wait4Report(BigDecimal logId, Timestamp time) {
        this.logId = logId;
        this.time = time;
    }

    public boolean isTimeout() {
        long currTime = System.currentTimeMillis();
        if ((currTime - time.getTime()) > WAIT_REPORT_TIMEOUT) {
            return true;
        }
        return false;
    }
}
