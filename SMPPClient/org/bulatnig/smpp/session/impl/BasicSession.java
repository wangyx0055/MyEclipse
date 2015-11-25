package org.bulatnig.smpp.session.impl;

import org.bulatnig.smpp.net.Connection;
import org.bulatnig.smpp.pdu.CommandId;
import org.bulatnig.smpp.pdu.CommandStatus;
import org.bulatnig.smpp.pdu.Pdu;
import org.bulatnig.smpp.pdu.PduException;
import org.bulatnig.smpp.pdu.impl.EnquireLink;
import org.bulatnig.smpp.pdu.impl.EnquireLinkResp;
import org.bulatnig.smpp.pdu.impl.Unbind;
import org.bulatnig.smpp.session.MessageListener;
import org.bulatnig.smpp.session.Session;
import org.bulatnig.smpp.session.State;
import org.bulatnig.smpp.session.StateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous session implementation.
 *
 * @author Bulat Nigmatullin
 */
public class BasicSession implements Session {

    private static final Logger logger = LoggerFactory.getLogger(BasicSession.class);

    private final Connection conn;

    private int smscResponseTimeout = DEFAULT_SMSC_RESPONSE_TIMEOUT;
    private int pingTimeout = DEFAULT_ENQUIRE_LINK_TIMEOUT;
    private int reconnectTimeout = DEFAULT_RECONNECT_TIMEOUT;
    private MessageListener messageListener = new DefaultMessageListener();
    private StateListener stateListener = new DefaultStateListener();
    private PingThread pingThread;
    private ReadThread readThread;

    private Pdu bindPdu;
    private volatile long sequenceNumber = 0;
    private volatile long lastActivity;
    private volatile State state = State.DISCONNECTED;

    public BasicSession(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    @Override
    public void setSmscResponseTimeout(int timeout) {
        this.smscResponseTimeout = timeout;
    }

    @Override
    public void setEnquireLinkTimeout(int timeout) {
        this.pingTimeout = timeout;
    }

    @Override
    public void setReconnectTimeout(int timeout) {
        this.reconnectTimeout = timeout;
    }

    @Override
    public synchronized Pdu open(Pdu pdu) throws PduException, IOException {
        bindPdu = pdu;
        return open();
    }

    @Override
    public synchronized long nextSequenceNumber() {
        if (sequenceNumber == 2147483647L)
            sequenceNumber = 1;
        else
            sequenceNumber++;
        return sequenceNumber;
    }

    @Override
    public synchronized boolean send(Pdu pdu) throws PduException {
        if (State.CONNECTED != state)
            return false;
        try {
            conn.write(pdu);
            return true;
        } catch (IOException e) {
            logger.debug("Send failed.", e);
            reconnect(e);
            return false;
        }
    }

    @Override
    public synchronized void close() {
        if (State.RECONNECTING == state || closeInternal(null))
            updateState(State.DISCONNECTED);
    }

    private synchronized Pdu open() throws PduException, IOException {
        logger.trace("Opening new session...");
        conn.open();
        logger.trace("TCP connection established. Sending bind request.");
        bindPdu.setSequenceNumber(nextSequenceNumber());
        conn.write(bindPdu);
        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.schedule(new Runnable() {
                    @Override
                    public void run() {
                        logger.warn("Bind response timed out.");
                        conn.close();
                    }
                }, smscResponseTimeout, TimeUnit.MILLISECONDS);
        logger.trace("Bind request sent. Waiting for bind response.");
        try {
            Pdu bindResp = conn.read();
            es.shutdownNow();
            logger.trace("Bind response command status: {}.", bindResp.getCommandStatus());
            if (CommandStatus.ESME_ROK == bindResp.getCommandStatus()) {
                updateLastActivity();
                pingThread = new PingThread();
                pingThread.setName("Ping");
                pingThread.start();
                readThread = new ReadThread();
                Thread t2 = new Thread(readThread);
                t2.setName("Read");
                t2.start();
                updateState(State.CONNECTED);
                logger.trace("Session successfully opened.");
            }
            return bindResp;
        } finally {
            if (!es.isShutdown())
                es.shutdownNow();
        }
    }

    /**
     * Actually close session.
     *
     * @param reason exception, caused session close, or null
     * @return session closed
     */
    private synchronized boolean closeInternal(Exception reason) {
        if (State.DISCONNECTED != state) {
            logger.trace("Closing session...");
            pingThread.stopAndInterrupt();
            pingThread = null;
            if (!(reason instanceof IOException) && readThread.run) {
                try {
                    synchronized (conn) {
                        Pdu unbind = new Unbind();
                        unbind.setSequenceNumber(nextSequenceNumber());
                        send(unbind);
                        conn.wait(smscResponseTimeout);
                    }
                } catch (Exception e) {
                    logger.debug("Unbind request send failed.", e);
                }
            }
            readThread.stop();
            readThread = null;
            conn.close();
            logger.trace("Session closed.");
            return true;
        } else {
            logger.trace("Session already closed.");
            return false;
        }
    }

    private void reconnect(Exception reason) {
        // only one thread should do reconnect
        boolean doReconnect = false;
        synchronized (state) {
            if (State.RECONNECTING != state) {
                doReconnect = true;
                state = State.RECONNECTING;
            }
        }
        if (doReconnect) {
            closeInternal(reason);
            new Thread(new ReconnectThread(reason)).start();
        }
    }


    private void updateLastActivity() {
        lastActivity = System.currentTimeMillis();
    }

    private void updateState(State newState) {
        updateState(newState, null);
    }

    private void updateState(State newState, Exception e) {
        this.state = newState;
        stateListener.changed(newState, e);
    }

    private class PingThread extends Thread {

        private volatile boolean run = true;

        @Override
        public void run() {
            logger.trace("Ping thread started.");
            try {
                while (run) {
                    logger.trace("Checking last activity.");
                    try {
                        Thread.sleep(pingTimeout);
                        if (pingTimeout < (System.currentTimeMillis() - lastActivity)) {
                            long prevLastActivity = lastActivity;
                            Pdu enquireLink = new EnquireLink();
                            enquireLink.setSequenceNumber(nextSequenceNumber());
                            send(enquireLink);
                            synchronized (conn) {
                                conn.wait(smscResponseTimeout);
                            }
                            if (run && lastActivity == prevLastActivity) {
                                reconnect(new IOException("Enquire link response not received. Session closed."));
                            }
                        }
                    } catch (InterruptedException e) {
                        logger.trace("Ping thread interrupted.");
                    }
                }
            } catch (PduException e) {
                if (run) {
                    logger.warn("EnquireLink request failed.", e);
                    run = false;
                    reconnect(e);
                }
            } finally {
                logger.trace("Ping thread stopped.");
            }
        }

        void stopAndInterrupt() {
            run = false;
            interrupt();
        }

    }

    private class ReadThread implements Runnable {

        private volatile boolean run = true;

        @Override
        public void run() {
            logger.trace("Read thread started.");
            try {
                while (run) {
                    Pdu request = conn.read();
                    updateLastActivity();
                    Pdu response;
                    if (CommandId.ENQUIRE_LINK == request.getCommandId()) {
                        response = new EnquireLinkResp();
                        response.setSequenceNumber(request.getSequenceNumber());
                        send(response);
                    } else if (CommandId.ENQUIRE_LINK_RESP == request.getCommandId()) {
                        synchronized (conn) {
                            conn.notifyAll();
                        }
                    } else if (CommandId.UNBIND_RESP == request.getCommandId()) {
                        synchronized (conn) {
                            conn.notifyAll();
                        }
                        stop();
                    } else {
                        messageListener.received(request);
                    }
                }
            } catch (PduException e) {
                if (run) {
                    logger.warn("Incoming message parsing failed.", e);
                    run = false;
                    reconnect(e);
                }
            } catch (IOException e) {
                if (run) {
                    logger.warn("Reading IO failure.", e);
                    run = false;
                    reconnect(e);
                }
            } finally {
                logger.trace("Read thread stopped.");
            }
        }

        void stop() {
            run = false;
        }

    }

    private class ReconnectThread implements Runnable {

        private final Exception reason;

        private ReconnectThread(Exception reason) {
            this.reason = reason;
        }

        @Override
        public void run() {
            logger.debug("Reconnect started.");
            stateListener.changed(state, reason);
            boolean reconnectSuccessful = false;
            while (!reconnectSuccessful && state == State.RECONNECTING) {
                logger.debug("Reconnecting...");
                try {
                    Pdu bindResponse = open();
                    if (CommandStatus.ESME_ROK == bindResponse.getCommandStatus()) {
                        reconnectSuccessful = true;
                    } else {
                        logger.warn("Reconnect failed. Bind response error code: {}.",
                                bindResponse.getCommandStatus());
                    }
                } catch (Exception e) {
                    logger.warn("Reconnect failed.", e);
                    try {
                        Thread.sleep(reconnectTimeout);
                    } catch (InterruptedException e1) {
                        logger.trace("Reconnect sleep interrupted.", e1);
                    }
                }
            }
            if (reconnectSuccessful)
                state = State.CONNECTED;
            logger.debug("Reconnect done.");
        }
    }

}
