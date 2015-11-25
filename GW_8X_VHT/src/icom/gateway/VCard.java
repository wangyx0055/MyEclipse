package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import org.smpp.util.ByteBuffer;

/**
 * vCard bao gom cac thuoc tinh (property), co dang nhu sau:
 * Propery_Name [;Parameters]":"Property_Value
 *
 * The sample vCard message content:
 *   BEGIN:VCARD
 *   VERSION:2.1
 *   N:Smith;Mike
 *   TEL;PREF:+84904060008
 * (or TEL;PREF;VOICE:0904060008)
 * (or TEL;HOME:7722728)
 *   END:VCARD
 */
public class VCard {
    private String name = "TUANHN;NGUYEN";
    private String tel = "0904060008";
    private ByteBuffer vcard = null;

    public String getName() {
        return this.name;
    }
    public void setName(String value) {
        this.name = value;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String value) {
        this.tel = value;
    }

    public ByteBuffer getEncoded() {
        return this.vcard;
    }

    public void encode() {
        ByteBuffer buffer = new ByteBuffer();
        String strCard =
            "BEGIN:VCARD\n" +
            "VERSION:2.1\n" +
            "N:" + this.name  + "\n" +
            "TEL;PREF:" + this.tel + "\n" +
            "END:VCARD";
        for (int i=0; i<strCard.length(); i++) {
            buffer.appendByte( (byte)strCard.charAt(i));
        }
        this.vcard = buffer;
    }

    public VCard() {
    }

    public static void main(String args[]) {
        VCard vcard = new VCard();
        vcard.encode();
        //System.out.println(vcard.getEncoded().getHexDump());
        //System.out.println(vcard.getEncoded().length());
    }

}
