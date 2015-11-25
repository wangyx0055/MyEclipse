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
import java.io.*;

/**
 * This class represents a picture message
 *
 * Operator logo bitmaps are usually 72 pixels wide by 14 pixels high,
 * as this is a suitable size for the phone's display. However, other
 * sizes (at least, smaller ones) are also supported by the phones.
 *
 * In GSM, an operator logo message always requires 2 SMS messages
 * (assuming the usual 72x14 bitmap). An operator logo message with
 * e.g. a 64x14 bitmap can be sent in one SMS message.
 *
 * Note:
 * We can encode an operator logo with 72x14 bitmap into a single SMS
 * by get out the 2 octets (DOES NOT FOLLOW THE SPECIFICATIONS):
 *    + OTA bitmap-version-number, and
 *    + Linefeed character.
 *
 * (Neu encode theo dung chuan cho anh 72x14 (=126 octets), ta co:
 * 7 octets (UDH) + 9 octets (OTA bitmap headers) + 126 = 142 octets.
 * De truyen tren 1 SMS, max = 140octets.)
 *
 */


public class OperatorLogo {
    static final int LOGO_WIDTH_DEFAULT  = 72; //48(Hex)
    static final int LOGO_HEIGHT_DEFAULT = 14; //0E(Hex)

    private String mobile_operator = ""; //vms, gpc, or vietel
    //OTB format as follows:
    //   00 -The first byte of the bitmap
    //   <Width , 1byte>
    //   <Height, 1byte>
    //   01 - The depth of the bitmap (number of grey scales)
    //   <OTA data>
    private byte[] OTB = null;
    // Encoded binary data of picture message,
    // include text, data,...
    private ByteBuffer encoded = null;

    public void setMobileOperator(String mo) {
        this.mobile_operator = mo;
    }
    public void setOTB(byte[] b) {
        this.OTB = b;
    }
    public byte[] getOTB() {
        return this.OTB;
    }
    public ByteBuffer getEncoded() {
        return this.encoded;
    }

    //Encode all contents into Binary format
    public boolean encode() throws EMSException {
        ByteBuffer buffer = new ByteBuffer();
        // Logo Part:
        // Identifier for version, current version is (ASCII) zero "0".
        // If (!=0) stop processing of the message.
        // (Just ignore it)
        // buffer.appendByte( (byte) 0x30);

        // Mobile Country code (<MCC>) is always a three (3) digit code and
        // Mobile Network Code (<MNC>) is always a two (2) digit code
        //    Vietnam     VMS    452   01
        //    Vietnam     GPC    452   02
        //    Vietnam     VIETEL 452   04
        // MCC = 452 --> 54F2 (little-endian BCD, filled with F16, e.g: 123 -> 21 F3
        buffer.appendShort( (short) 0x54F2);

        // MNC: 01 --> 10, 02 --> 20
        mobile_operator = mobile_operator.toUpperCase();
        if("VMS".equals(mobile_operator)) {
            buffer.appendByte( (byte) 0x10);
        } else if ("GPC".equals(mobile_operator)) {
            buffer.appendByte( (byte) 0x20);
        } else if ("VIETEL".equals(mobile_operator) ) {
            buffer.appendByte( (byte) 0x40);
        } else if ("SFONE".equals(mobile_operator)) {
            buffer.appendByte( (byte) 0x30);
        } else if ("HTC".equals(mobile_operator)) {
            buffer.appendByte( (byte) 0x50);
        } else if ("EVN".equals(mobile_operator)) {
            buffer.appendByte( (byte) 0x60);
        }else {
            throw new EMSException("Invalid mobile operator");
        }

        //"Line feed" character
        //(Just ignore it)
        // buffer.appendByte( (byte) 0x0A);

        // InfoField
        // buffer.appendByte( (byte) 0x00);

        // Width of the bitmap, 72
        // buffer.appendByte( (byte) width);

        // Height of the bitmap, 14
        // buffer.appendByte( (byte) height);

        // Depth of the bitmap (number of gray scales)
        // buffer.appendByte( (byte) 0x01);

        // OTA bitmap visible data
        buffer.appendBytes(OTB);

        this.encoded = buffer;
        return true;
    }

    public OperatorLogo() {
    }
    public OperatorLogo(String text, byte[] data) {
        this.mobile_operator = text;
        this.OTB = data;
    }
    public OperatorLogo(String filename) throws EMSException {
        if (filename == null) {
            throw new EMSException("File name is not set");
        }
        filename = filename.toLowerCase();
        if (!filename.endsWith(".otb")) {
            throw new EMSException("Invalid OTB file");
        }
        try {
            ByteBuffer buf = loadByteBuffer(filename);
            // OTB format as follows:
            // 00<Width,1byte><Height,1byte>01<OTA data>
            setOTB(buf.getBuffer());
        } catch (Exception ex) {
            throw new EMSException(ex.getMessage());
        }
    }

    /**
     * RESTORING THE ORIGINAL OPERATOR LOGO
     * The easiest way to remove a previously downloaded operator logo and
     * restore the original one is to send an almost normal operator logo
     * message to the phone. The difference from the normal operator logo
     * message is the value of the octets—9, 10 (MCC) and 11 (MNC).
     * These octets must have different values than the current operator's
     * values. For example, the values of the octets 9-11 could be 000000.
     *
     * The content of the OTA bitmap has no relevance in the message, therefore
     * the OTA bitmap data can be missing (header information is mandatory).
     * When receiving this kind of operator logo message the logo must be saved
     * and after that the original operator logo appears on the screen.
     * For example, here is the user data of the message that removes an old
     * operator logo:
     *      06050415820000300000000A00000001
     *
     */
    public static ByteBuffer getMe4RestoringOriginalOne(String mobileOperator, boolean withUDH)
        throws EMSException {
        ByteBuffer buffer = new ByteBuffer();
        if (withUDH) {
            // Add UDH headers
            buffer.appendByte( (byte) 0x06);
            buffer.appendByte( (byte) 0x05);
            buffer.appendByte( (byte) 0x04);
            buffer.appendShort( (short) 0x1582); //destination port
            buffer.appendShort( (short) 0x0000); //original port
        }
        // Identifier for version
        buffer.appendByte( (byte) 0x30);
        // MCC = 452 --> 54F2
        buffer.appendShort( (short) 0x0000);
//        buffer.appendShort( (short) 0x54F2);
        // MNC: 01 --> 10, 02 --> 20
        buffer.appendByte( (byte) 0x00);
//        mobileOperator = mobileOperator.toUpperCase();
//        if("VMS".equals(mobileOperator)) {
//            buffer.appendByte( (byte) 0x10);
//        } else if ("GPC".equals(mobileOperator)) {
//            buffer.appendByte( (byte) 0x20);
//        } else if ("VIETEL".equals(mobileOperator)) {
//            buffer.appendByte( (byte) 0x40);
//        } else {
//            throw new EMSException("Invalid mobile operator");
//        }

        //"Line feed" character
        buffer.appendByte( (byte) 0x0A);
        // InfoField
        buffer.appendByte( (byte) 0x00);
        // Width of the bitmap
        buffer.appendByte( (byte) 0x00);
        // Height of the bitmap
        buffer.appendByte( (byte) 0x00);
        // Depth of the bitmap (number of gray scales)
        buffer.appendByte( (byte) 0x01);
        return buffer;
    }

    // for your convenience here is the code for loading
    // of the data from a file
    public ByteBuffer loadByteBuffer(String fileName) throws IOException {
        FileInputStream is = new FileInputStream(fileName);
        byte[] data = new byte[is.available()];
        is.read(data);
        is.close();
        return new ByteBuffer(data);
    }

    public static void main (String args[]) {
        try {
            OperatorLogo pic = new OperatorLogo("logo.otb");
            pic.setMobileOperator("VMS");
            pic.encode();
            System.out.println(pic.getEncoded().getHexDump());
            System.out.println(pic.getEncoded().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
