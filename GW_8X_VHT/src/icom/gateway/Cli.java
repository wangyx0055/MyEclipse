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
 * CLI (Caller Line Identification) icons (also called ‘Caller group graphics’)
 * are sent to the phones as Smart Messages with port number 1583 hex.
 *
 * CLI icon bitmaps are usually 72 pixels wide by 14 pixels high, as this is
 * a suitable size for the phone's display. However, other sizes (at least,
 * smaller ones) are also supported by the phones.
 *
 * In GSM, a CLI icon message requires only one SMS message
 * (assuming the usual 72x14 bitmap).
 *
 */


public class Cli {
    static final int CLI_WIDTH_DEFAULT  = 72; //48(Hex)
    static final int CLI_HEIGHT_DEFAULT = 14; //0E(Hex)
    // OTB format as follows:
    // 00<Width,1byte><Height,1byte>01<OTA data>
    private byte[] OTB = null; //otb
    // Encoded binary data of picture message,
    // include header, data,...
    private ByteBuffer encoded = null;

    public byte[] getOTB() {
        return this.OTB;
    }
    public void setOTB(byte[] b) {
        this.OTB = b;
    }


    public ByteBuffer getEncoded() { return this.encoded; }

    //Encode all contents into Binary format
    public boolean encode()   {
        ByteBuffer buffer = new ByteBuffer();
        // CLI Part:
        // CLI version number. ISO-8859-1 character "0"
        buffer.appendByte( (byte) 0x30);

        // InfoField
        // buffer.appendByte( (byte) 0x00);

        // Width of the bitmap. Hex 48 -> 72 decimal
        // buffer.appendByte( (byte) width);

        // Height of the bitmap. Hex 0E -> 14 decimal
        // buffer.appendByte( (byte) height);

        // Depth of the bitmap (number of gray scales)
        // buffer.appendByte( (byte) 0x01);

        // OTA bitmap data
        buffer.appendBytes(OTB);

        this.encoded = buffer;
        return true;
    }


    public Cli() {
    }
    public Cli(byte[] data) {
        this.OTB = data;
    }
    public Cli(String filename) throws EMSException {
        if (filename == null) {
            throw new EMSException("File name is not set");
        }
        filename = filename.toLowerCase();
        if (!filename.endsWith(".otb")) {
            throw new EMSException("Invalid OTB file");
        }
        try {
            ByteBuffer buf = loadByteBuffer(filename);
            setOTB(buf.getBuffer());
        } catch (Exception ex) {
            throw new EMSException(ex.getMessage());
        }
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
            Cli pic = new Cli("CLI.otb");
            pic.encode();
            //System.out.println(pic.getEncoded().getHexDump());
            //System.out.println("SIZE: " + pic.getEncoded().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
