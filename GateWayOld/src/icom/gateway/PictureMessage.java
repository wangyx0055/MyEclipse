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

//This class represents a picture message
public class PictureMessage {

    static final int PIC_WIDTH_DEFAULT  = 72;
    static final int PIC_HEIGHT_DEFAULT = 28;

    private String text = ""; //max=120chars
    // OTB format as follows:
    // 00<Width,1byte><Height,1byte>01<OTA data>
    private byte[] OTB = null;
    // Encoded binary data of picture message,
    // include text, data,...:
    //Text+OTB=<version,0x30><itemlength,0x00>
    //<textlength,2bytes><text><itemlength,0x02>...
    private ByteBuffer encoded = null;


    public void setText(String t) {
        this.text = t;
    }
    public void setOTB(byte[] b) {
        this.OTB = b;
    }
    public void setEncoded(byte[] b) {
        this.encoded = new ByteBuffer(b);
    }

    public ByteBuffer getEncoded() {
        return this.encoded;
    }

    //Encode all contents into Binary format
    public boolean encode()   {
        ByteBuffer buffer = new ByteBuffer();
        // PictureMessage Part:
        // Identifier for version, current version is (ASCII) zero "0".
        // If it is not "0", stop processing of the message.
        buffer.appendByte( (byte) 0x30); //version 0

        // <Item-length> <ISO-8859-1-char>*
        buffer.appendByte( (byte) 0x00); //"00"
        //Text length (2 octets)
        if (text != null) {
            // Text length
            buffer.appendShort( (short) text.length());
            // Text
            buffer.appendString(text);
        } else {
            // Text length
            buffer.appendShort( (short) 0x0000);
        }

        //"02" = 2 next elements
        // <Item length><OTA bitmap>
        buffer.appendByte( (byte) 0x02);

        // <Item-length> (2 octets)
        // = 4 octets for header of OTA bitmap data
        buffer.appendShort( (short) 0x0100);

        // The first byte of the bitmap must be 00
        // indicating that there is no animation,
        // just a single static image
        // buffer.appendByte( (byte) 0x00);

        // Width of the bitmap
        // buffer.appendByte( (byte) width);

        // Height of the bitmap
        // buffer.appendByte( (byte) height);

        // Depth of the bitmap (number of gray scales)
        // buffer.appendByte( (byte) 0x01);

        // OTA bitmap visible data
        buffer.appendBytes(OTB);

        this.encoded = buffer;
        return true;
    }

    public PictureMessage() {
    }
    public PictureMessage(String text, byte[] data) {
        this.text = text;
        this.OTB = data;
    }

    public PictureMessage(String filename) throws EMSException {
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
            PictureMessage pic = new PictureMessage();
            ByteBuffer b = pic.loadByteBuffer("test.otb");
            pic.setOTB(b.getBuffer());
            pic.setText("Tho test");
            pic.encode();
            System.out.println(pic.getEncoded().getHexDump());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
