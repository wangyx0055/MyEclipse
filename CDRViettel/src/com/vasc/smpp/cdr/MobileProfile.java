package com.vasc.smpp.cdr;

/**
 * <p>Title: M-Commerce Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
* @author Huynh Ngoc Tuan
 * @version 1.0
 */
import java.sql.Timestamp;

public class MobileProfile {
    private String soTB84 = null;
    private Timestamp lastChargedTime = null;
    private String lastChargedShortCode = null;

    public void setSoTB84(String value) {
        this.soTB84 = value;
    }
    public void setLastChargedTime(Timestamp value) {
        this.lastChargedTime = value;
    }
    public void setLastChargedShortCode(String value) {
        this.lastChargedShortCode = value;
    }

    public String getSoTB84() {
        return this.soTB84;
    }
    public Timestamp getLastChargedTime() {
        return this.lastChargedTime;
    }
    public String getLastChargedShortCode() {
        return this.lastChargedShortCode;
    }

    public MobileProfile() {
    }

}