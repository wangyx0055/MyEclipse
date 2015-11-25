package org.bulatnig.smpp.net.impl;

import org.bulatnig.smpp.net.Connection;
import org.bulatnig.smpp.pdu.Pdu;
import org.bulatnig.smpp.pdu.PduException;
import org.bulatnig.smpp.pdu.PduParser;
import org.bulatnig.smpp.pdu.impl.DefaultPduParser;
import org.bulatnig.smpp.util.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * TCP/IP connection. Additionally logs on debug level all read and sent PDU's.
 * <p/>
 * Not thread safe. Weak against buffer overflow problem.
 *
 * @author Bulat Nigmatullin
 */
public class TcpConnection implements Connection {

    private static final Logger logger = LoggerFactory.getLogger(TcpConnection.class);

    private final SocketAddress socketAddress;
    private final byte[] bytes = new byte[250];
    private PduParser parser = new DefaultPduParser();

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private ByteBuffer bb;

    public TcpConnection(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    @Override
    public void setParser(PduParser parser) {
        this.parser = parser;
    }

    @Override
    public void open() throws IOException {
        socket = new Socket();
        socket.connect(socketAddress);
        socket.setSoTimeout(0); // block read forever
        in = socket.getInputStream();
        out = socket.getOutputStream();
        bb = new ByteBuffer();
    }

    @Override
    public Pdu read() throws PduException, IOException {
        synchronized (in) {
            Pdu pdu = tryToReadBuffer(); // there may be two PDU's read previous time, but only one parsed
            while (pdu == null) {
                int read = in.read(bytes);
                if (read < 0)
                    throw new IOException("Connection closed by SMSC");
                bb.appendBytes(bytes, read);
                pdu = tryToReadBuffer();
            }
            logger.debug("<<< {}", pdu.buffer().hexDump());
            return pdu;
        }
    }

    @Override
    public void write(Pdu pdu) throws PduException, IOException {
        synchronized (out) {
            out.write(pdu.buffer().array());
            out.flush();
            logger.debug(">>> {}", pdu.buffer().hexDump());
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.debug("Connection closing error.", e);
        }
        socket = null;
        in = null;
        out = null;
        bb = null;
    }

    private Pdu tryToReadBuffer() throws PduException {
        if (bb.length() >= Pdu.HEADER_LENGTH) {
            long commandLength = bb.readInt();
            if (bb.length() >= commandLength)
                return parser.parse(new ByteBuffer(bb.removeBytes((int) commandLength)));
        }
        return null;
    }

}
