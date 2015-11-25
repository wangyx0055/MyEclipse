package icom.gateway;

import org.smpp.util.ByteBuffer;
/*
 <?xml version="1.0"?>
 <!DOCTYPE si PUBLIC "-//WAPFORUM//DTD SI 1.0//EN"
 "http://www.wapforum.org/DTD/si.dtd">
 <si>
        <indication href=”http://xxxxxx”>
            message here
        </indication>
 </si>
*/

public class SIgsm {
 //   private Logger logger = new Logger("Si");

    private String URL     = null; // url="http://xxxx/"
    private String message = null;
    private ByteBuffer encodedSi = null;

    public String getURL()     { return this.URL;     }
    public String getMessage() { return this.message; }

    public void setURL (String URL)        { this.URL = URL;         }
    public void setMessage(String message) { this.message = message; }


    public ByteBuffer getEncodedSI() {
        return encodedSi;
    }

    public void encodeSI() {
        if (URL == null) {
            //System.out.println("encodeSI: URL not set.");
            Gateway.util.log(this.getClass().getName(),"encodeSI: URL not set.");
            return;
        } else if (message == null || "".equals(message)) {
            //System.out.println("encodeSI: Message not set.");
            Gateway.util.log(this.getClass().getName(),"encodeSI: Message not set.");
            
            return;
        }
        ByteBuffer buffer = new ByteBuffer();

        // WSP
        buffer.appendByte( (byte)0x01); // Transaction id (connectionless WSP)
        buffer.appendByte( (byte)0x06); // Pdu type (06 = push)
        buffer.appendByte( (byte)0x01); // Header Length
        // Content-Type="application/vnd.wap.sic"
        buffer.appendByte( (byte)0xAE); // application/vnd.wap.sic (2E, with MSB is set to 1)

        //SI
        buffer.appendByte( (byte)0x02); // WBXML V1.2;0x00:V1.0;0x01:V1.1
        buffer.appendByte( (byte)0x05); // SI Public ID
        buffer.appendByte( (byte)0x6A); // Charset=UTF-8
        buffer.appendByte( (byte)0x00); // StrTableLen=0
        buffer.appendByte( (byte)0x45); // <si>, with content
        buffer.appendByte( (byte)0xC6); // <indication>, with content & attribute
        // URL
        buffer.appendBuffer(encodeURL(this.URL));
        // Content
        buffer.appendByte( (byte)0x03); // Inline string follows
        buffer.appendCString(message);  // String ends with Zero

        buffer.appendByte( (byte)0x01); // END (of Indication element)
        buffer.appendByte( (byte)0x01); // END (of SI element)
        this.encodedSi = buffer;
    }
    private ByteBuffer encodeURL(String url) {
        ByteBuffer buffer = new ByteBuffer();
        //href = "http://dalink.vietnamnet.vn/si/book.mid"
        buffer.appendByte( (byte)0x0C); //href=http://
        buffer.appendByte( (byte)0x03); //Inline string follows
        String urlPath = url.substring(7);

        //System.out.println("url=" + urlPath);

        buffer.appendCString(urlPath); // string ends with Zero
        //////////////////////////////////////
        buffer.appendByte( (byte)0x07); // [Added on 30/08/2004]
        //////////////////////////////////////
        buffer.appendByte( (byte)0x01); // END (of Indication attribute list)
        return buffer;
    }

    public static void main(String args[]){
        SIgsm si = new SIgsm();
        si.setURL("http://dalink.vietnamnet.vn/si/book.mid");
        si.setMessage("VietnamNet");
        si.encodeSI();
//        si.addWDPHeaders();
        System.out.print(si.getEncodedSI().getHexDump());
    }
}