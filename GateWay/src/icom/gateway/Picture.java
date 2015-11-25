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
import org.smpp.util.*;

//This class represents a picture message
public class Picture {
    static final int PIC_TYPE_OTB = 1;
    static final int PIC_TYPE_NPM = 2;
    static final int PIC_WIDTH_DEFAULT  = 72;
    static final int PIC_HEIGHT_DEFAULT = 28;

    private int width   = 72;
    private int height  = 28;
    private String text = ""; //max=120chars
    private byte[] data = null;
    // Encoded binary data of picture message,
    // include text, data,...
    private ByteBuffer encoded = null;

    public int getWidth()      { return this.width;   }
    public int getHeight()     { return this.height;  }
    public String getText()    { return this.text;    }
    public byte[] getData()    { return this.data;    }
    public ByteBuffer getEncoded() { return this.encoded; }

    //Encode all contents into Binary format
    public boolean encode()   {
        ByteBuffer buffer = new ByteBuffer();
        // CLI Part:
        // Identifier for version, current version is (ASCII) zero "0".
        // If it is not "0", stop processing of the message.
        buffer.appendByte( (byte) 0x30); //version 0

        // <Item-length> <ISO-8859-1-char>*
        buffer.appendByte( (byte) 0x00); //"00"
        //Text length (2 octets)
        if (text != null) {
            buffer.appendShort( (short) text.length());
            // Text
            buffer.appendString(text);
        } else {
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
        buffer.appendByte( (byte) 0x00);

        // Width of the bitmap
        buffer.appendByte( (byte) width);

        // Height of the bitmap
        buffer.appendByte( (byte) height);

        // Depth of the bitmap (number of gray scales)
        buffer.appendByte( (byte) 0x01);

        // OTA bitmap visible data
        buffer.appendBytes(data);

        this.encoded = buffer;
        return true;
    }

    public void setWidth(int w) {
        this.width = w;
    }
    public void setHeigth(int h) {
        this.height = h;
    }
    public void setText(String t) {
        this.text = t;
    }
    public void setData(byte[] b) {
        this.data = b;
    }

    public Picture() {
    }
    public Picture(String text, byte[] data) {
        this.text = text;
        this.data = data;
    }
    // 2 file types: .npm & .otb
    public Picture(String filename) throws EMSException {
        if (filename == null) {
            throw new EMSException("File name is not set");
        }
        filename = filename.toLowerCase();
        int fileType = 0;
        if (filename.endsWith(".npm")) {
            fileType = PIC_TYPE_NPM;
        } else if (filename.endsWith(".otb")) {
            fileType = PIC_TYPE_OTB;
        } else {
            throw new EMSException("Invalid Nokia Picture Message file");
        }

        int width   = 0;
        int height  = 0;
        byte[] data = null;
        try {
            ByteBuffer buf = loadByteBuffer(filename);
            switch(fileType) {
                case PIC_TYPE_NPM:
                    //NPM format as follows:
                    //  4E504D00 ("NPM")
                    //  <Text_length> (3 chars)
                    //  <Text\00>
                    //  <Width , 1byte>(72)
                    //  <Height, 1byte>(28)
                    //  01 01
                    //  <?, 1 byte>
                    //  <OTA data>
                    if (!"NPM".equals(buf.removeCString())) {
                        throw new EMSException("File " + filename + " is not NPM file!");
                    }
                    byte textLength = buf.removeByte();
                    String text = buf.removeCString();
                    if (text != null && textLength > 0 && text.length() != textLength) {
                        throw new EMSException("<Text Length> field = " + textLength + " but <text> field = " + text);
                    }

                    width  = buf.removeByte();
                    height = buf.removeByte();
                    if (width != 72 || height != 28) {
                        throw new EMSException("Invalid width (" + width + ") or height(" + height + ")");
                    }
                    buf.removeByte(); //01
                    buf.removeByte(); //01
                    buf.removeByte(); //somebyte (?)
                    data = buf.removeBuffer( (int) (width * height) / 8).getBuffer();

                    //setWidth();
                    //setHeigth();
                    setText(text);
                    setData(data);
                    break;
                case PIC_TYPE_OTB:
                    //NPM format as follows:
                    //   00 -The first byte of the bitmap
                    //   <Width , 1byte>
                    //   <Height, 1byte>
                    //   01 - The depth of the bitmap (number of grey scales)
                    //   <OTA data>
                    if (buf.removeByte() != (byte)0x00) {
                        throw new EMSException("First byte of .otb file is invalid, expected 0");
                    }

                    width  = buf.removeByte();
                    height = buf.removeByte();
                    if (width != 72 || height != 28) {
                        throw new EMSException("Invalid width (" + width + ") or height(" + height + ")");
                    }

                    buf.removeByte();
                    data = buf.removeBuffer( (int) (width * height) / 8).getBuffer();

                    //setWidth();
                    //setHeigth();
                    //setText(text);//no text
                    setData(data);
                    break;
                default:
                   System.out.println("Picture: Invalid fileType!");
            }

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
//            Picture pic = new Picture();
//            ByteBuffer buf = pic.loadByteBuffer("candle.npm");

//            if (!"NPM".equals(buf.removeCString())) {
//                System.out.println("Not NPM file");
//            }
//            byte textLength = buf.removeByte();
//
//            String text = buf.removeCString();
//
//            System.out.println("Length: " + textLength);
//            System.out.println("Text: " + text);
//
//            byte width  = buf.removeByte();
//            byte height = buf.removeByte();
//
//            buf.removeByte(); //01
//            buf.removeByte(); //01
//
//            byte token43hex = buf.removeByte();
//            System.out.println("Token: " + token43hex);
//            int numOfBytes = (int) width * height/8;
//            byte[] data = buf.removeBuffer(numOfBytes).getBuffer();
//            pic.setText(text);
//            pic.setData(data);
//            pic.encode();
//            System.out.println(pic.getEncoded().getHexDump());



            Picture pic = new Picture("test.otb");
            pic.encode();
            System.out.println(pic.getEncoded().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
