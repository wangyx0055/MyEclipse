/*
 * Copyright (c) 1996-2001
 * Logica Mobile Networks Limited
 * All rights reserved.
 *
 * This software is distributed under Logica Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package org.smpp;

import icom.gateway.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Calendar;
import java.util.Date;
import java.lang.Integer;

import org.smpp.util.*;

/**
 * Implementation of TCP/IP type of communication. Covers both client
 * (peer-to-peer) connection and server (new connections from clients accepting
 * and creating) type of connections.
 * 
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version 1.0.1, 1 Oct 2001
 * @see Connection
 * @see java.net.Socket
 * @see java.net.ServerSocket
 * @see java.io.BufferedInputStream
 * @see java.io.BufferedOutputStream
 */

/*
 * 26-09-01 ticp@logica.com debug code categorized to groups
 * 27-09-01 * ticp@logica.com receive() rewritten not to consume cpu time while waiting for
 * data on socket 27-09-01 ticp@logica.com added customizable limit on maximum
 * received bytes in one call to receive() 27-09-01 ticp@logica.com added
 * prealocated buffer for socket reads with customizable size 28-09-01
 * ticp@logica.com the io streams buffer size is customizable now 01-10-01
 * ticp@logica.com traces added
 */

public class CopyOfTCPIPConnection extends Connection {
	/**
	 * The IP address of the remote end of the <code>socket</code>.
	 */
	private String address = null;

	/**
	 * The port number on the remote host to which the <code>socket</code> is
	 * connected or port number where <code>receiverSocket</code> is acception
	 * connections.
	 */
	private int port = 0;

	/**
	 * The TCP/IP (client) socket.
	 * 
	 * @see java.net.Socket
	 */
	private Socket socket = null;

	/**
	 * An input stream for reading bytes from the <code>socket</code>.
	 * 
	 * @see java.io.BufferedInputStream
	 */
	private BufferedInputStream inputStream = null;

	/**
	 * An output stream for writting bytes to the <code>socket</code>.
	 * 
	 * @see java.io.BufferedOutputStream
	 */
	private BufferedOutputStream outputStream = null;

	/**
	 * Indication if the <code>socket</code> is opened.
	 */
	private boolean opened = false;

	/**
	 * The server socket used for accepting connection on <code>port</code>.
	 * 
	 * @see #port
	 * @see java.net.ServerSocket
	 */
	private ServerSocket receiverSocket = null;

	/**
	 * Indicates if the connection represents client or server socket.
	 */
	private byte connType = CONN_NONE;

	/**
	 * Indicates that the connection type hasn't been set yet.
	 */
	private static final byte CONN_NONE = 0;

	/**
	 * Indicates that the connection is client type connection.
	 * 
	 * @see #socket
	 */
	private static final byte CONN_CLIENT = 1;

	/**
	 * Indicates that the connection is server type connection.
	 * 
	 * @see #receiverSocket
	 */
	private static final byte CONN_SERVER = 2;

	/**
	 * Default size for socket io streams buffers.
	 * 
	 * @see #initialiseIOStreams(Socket)
	 */
	private static final int DFLT_IO_BUF_SIZE = 2 * 1024;

	/**
	 * Default size for the receiving buffer.
	 */
	private static final int DFLT_RECEIVE_BUFFER_SIZE = 4 * 1024;

	/**
	 * The default maximum of bytes received in one call to the
	 * <code>receive</code> function.
	 * 
	 * @see #maxReceiveSize
	 * @see #receive()
	 */
	private static final int DFLT_MAX_RECEIVE_SIZE = 128 * 1024;

	/**
	 * The size for IO stream's buffers. Used by the instances of
	 * <code>BufferedInputStream</code> which are used as sreams for accessing
	 * the socket.
	 * 
	 * @see #setIOBufferSize(int)
	 */
	private int ioBufferSize = DFLT_IO_BUF_SIZE;

	/**
	 * The receiver buffer size. Can be changed by call to the function
	 * <code>setReceiveBufferSize</code>. This is the maximum count of bytes
	 * which can be read from the socket in one call to the socket's input
	 * stream's <code>read</code>.
	 * 
	 * @see #setReceiveBufferSize(int)
	 * @see #receive()
	 */
	private int receiveBufferSize;

	/**
	 * The buffer for storing of received data in the <code>receive</code>
	 * function.
	 * 
	 * @see #setReceiveBufferSize(int)
	 * @see #receive()
	 */
	private byte[] receiveBuffer;

	/**
	 * Max count of bytes which can be returned from one call to the
	 * <code>receive</code> function. If the returned data seems to be
	 * incomplete, you might want to call the <code>receive</code> again.
	 * 
	 * @see #setMaxReceiveSize(int)
	 * @see #receive()
	 */
	private int maxReceiveSize = DFLT_MAX_RECEIVE_SIZE;

	/**
	 * Initialises the connection with port only, which means that the
	 * connection will serve as connection receiving server. The accepting of
	 * the connection must be invoked explicitly by calling of
	 * <code>accept</code> method.
	 * 
	 * @param port
	 *            the port number to listen on
	 */
	public CopyOfTCPIPConnection(int port) {
		if ((port >= Data.MIN_VALUE_PORT) && (port <= Data.MAX_VALUE_PORT)) {
			this.port = port;
		} else {
			Logger.verbose("Invalid port.");
		}
		connType = CONN_SERVER;
	}

	/**
	 * Initialises the connection for client communication.
	 * 
	 * @param address
	 *            the address of the remote end of the <code>socket</code>
	 * @param port
	 *            the port number on the remote host
	 */
	public CopyOfTCPIPConnection(String address, int port) {
		if (address.length() >= Data.MIN_LENGTH_ADDRESS) {
			this.address = address;
		} else {
			Logger.verbose("Invalid address.");
		}
		if ((port >= Data.MIN_VALUE_PORT) && (port <= Data.MAX_VALUE_PORT)) {
			this.port = port;
		} else {
			Logger.verbose("Invalid port.");
		}
		connType = CONN_CLIENT;
		setReceiveBufferSize(DFLT_RECEIVE_BUFFER_SIZE);
	}

	/**
	 * Initialises the connection with existing socket. It's intended for use
	 * with one server connection which generates new sockets and creates
	 * connections with the sockets.
	 * 
	 * @param socket
	 *            the socket to use for communication
	 * @see #accept()
	 */
	public CopyOfTCPIPConnection(Socket socket) throws IOException {
		connType = CONN_CLIENT;
		this.socket = socket;
		address = socket.getInetAddress().getHostAddress();
		port = socket.getPort();
		initialiseIOStreams(socket);
		opened = true;
		setReceiveBufferSize(DFLT_RECEIVE_BUFFER_SIZE);
	}

	/**
	 * Opens the connection by creating new <code>Socket</code> (for client type
	 * connection) or by creating <code>ServerSocket</code> (for server type
	 * connection). If the connection is already opened, the method is not doing
	 * anything.
	 * 
	 * @see #connType
	 * @see java.net.ServerSocket
	 * @see java.net.Socket
	 */
	public void open() throws IOException {
		Logger.verbose("open socket");
		IOException exception = null;

		if (!opened) {
			if (connType == CONN_CLIENT) {
				try {
					System.err.println("CCCCCCCCREAT SOCKET");
					System.err.println("CCCCCCCCREAT SOCKET");
					socket = new Socket(address, port);
					
					System.err.println("CCCCCCCCREAT initialiseIOStreams");
					initialiseIOStreams(socket);
					opened = true;
					Logger.verbose(DCOM, "opened client tcp/ip connection to "
							+ address + " on port " + port);
				} catch (IOException e) {
					Logger.verbose("IOException opening TCPIPConnection " + e);
					e.printStackTrace();

					exception = e;
				} catch (Exception e) {
					// TODO: handle exception
					Logger.verbose("Exception opening TCPIPConnection " + e);
					e.printStackTrace();
					exception = new IOException();

				}
			} else if (connType == CONN_SERVER) {
				try {
					receiverSocket = new ServerSocket(port);
					opened = true;
					Logger.verbose(DCOM, "listening tcp/ip on port " + port);
				} catch (IOException e) {
					Logger.verbose("IOException creating listener socket " + e);
					exception = e;
				}
			} else {
				Logger.verbose("Unknown connection type = " + connType);
			}
		} else {
			Logger.verbose("attempted to open already opened connection ");
		}

		// debug.exit(DCOM, this);
		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * Closes the client or server connection.
	 * 
	 * @see #connType
	 * @see #open()
	 */
	public void close() throws IOException {
		Logger.verbose("close socket");
		IOException exception = null;

		if (connType == CONN_CLIENT) {
			try {
				inputStream.close();
				outputStream.close();
				socket.close();
				socket = null;
				opened = false;
				Logger.verbose(DCOM, "closed client tcp/ip connection to "
						+ address + " on port " + port);
			} catch (IOException e) {
				Logger.verbose("IOException closing socket " + e);

				exception = e;
			}
		} else if (connType == CONN_SERVER) {
			try {
				receiverSocket.close();
				receiverSocket = null;
				opened = false;
				Logger
						.verbose(DCOM, "stopped listening tcp/ip on port "
								+ port);
			} catch (IOException e) {
				Logger.verbose("IOException closing listener socket " + e);

				exception = e;
			}
		} else {
			Logger.verbose("Unknown connection type = " + connType);
		}

		// debug.exit(DCOM, this);
		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * Sends data over the connection. Must be client type connection. The
	 * timeout for sending is set by <code>setCommsTimeout</code>.
	 * 
	 * @see java.net.Socket
	 */
	public synchronized void send(ByteBuffer data) throws IOException {
		Logger.verbose(DCOM, "send");
		IOException exception = null;

		/*
		 * socket.setSoTimeout((int)getCommsTimeout());
		 * outputStream.write(data.getBuffer(), 0, data.length());
		 * outputStream.flush();
		 */
		if (outputStream == null) {
			// SmppObject.debug.exit(7, this);
			throw new IOException("Not connected");
		}

		if (connType == CONN_CLIENT) {

			try {

				socket.setSoTimeout((int) getCommsTimeout());
				// outputStream = new BufferedOutputStream(socket
				// .getOutputStream(), ioBufferSize);

				outputStream.write(data.getBuffer(), 0, data.length());
				outputStream.flush();

				Logger.verbose(DCOM, "sent " + data.length() + " bytes to "
						+ address + " on port " + port);

			} catch (IOException e) {
				Logger.verbose("IOException sending data " + e);
				// //System.err.println("TPCIP:ex" + e.getMessage());
				e.printStackTrace();
				exception = e;
			}
			// //System.err.println("TCPIPConection:sent " +
			// data.length()
			// + " bytes to "
			// + address + " on port " + port + "@output:");

			// //

		} else if (connType == CONN_SERVER) {
			Logger.verbose("Attempt to send data over server type connection.");
			// System.err
			// .println("Attempt to send data over server type connection.");
		} else {
			Logger.verbose("Unknown connection type = " + connType);
			// System.err.println("Unknown connection type = " + connType);
		}

		// System.err.println("debug.exit(DCOM, this");
		// debug.exit(DCOM, this);
		if (exception != null) {
			throw exception;
		}

	}

	/**
	 * Reads data from the connection. Must be client type connection. The
	 * timeout for receiving is set by <code>setReceiveTimeout</code>. The
	 * timeout for single attempt to read something from socket is set by
	 * <code>setCommsTimeout</code>.
	 * 
	 * @see #setReceiveBufferSize(int)
	 * @see #setMaxReceiveSize(int)
	 * @see Connection#getCommsTimeout()
	 * @see java.net.Socket
	 */
	public ByteBuffer receive() throws IOException {
		// debug.enter(DCOMD, this, "receive");

		// System.err.println(">>>>>>>" + "receive");
		IOException exception = null;

		ByteBuffer data = null;
		if (connType == CONN_CLIENT) {
			// System.err.println(">>>>>>>" + "conn_client");
			data = new ByteBuffer();
			long endTime = Data.getCurrentTime() + getReceiveTimeout();
			// int bytesAvailable = 0;
			int bytesToRead = 0;
			int bytesRead = 0;
			int totalBytesRead = 0;

			try {
				socket.setSoTimeout((int) getCommsTimeout());
				bytesToRead = receiveBufferSize;
				Logger.verbose(DCOMD, "going to read from socket");
				// Logger.verbose(DCOMD, "comms timeout=" + getCommsTimeout()
				// + " receive timeout=" + getReceiveTimeout()
				// + " receive buffer size=" + receiveBufferSize);
				// System.err.println(">>>>>>>" +zf "going to read from socke");

				do {
					System.err.println(".....");
					bytesRead = 0;
					try {
						bytesRead = inputStream.read(receiveBuffer, 0,
								bytesToRead);
					} catch (InterruptedIOException e) {
						// comms read timeout expired, no problem
						Logger.verbose(DCOMD, "timeout reading from socket");
						// System.err.println("timeout reading from socket");
					}
					
					//////////////
					 if (bytesRead > 0) {
						 Logger.verbose(DCOMD,"read "+bytesRead+" bytes from socket");
	                        data.appendBytes(receiveBuffer,bytesRead);
	                        totalBytesRead += bytesRead;
	                    }
	                    bytesToRead = inputStream.available();
	                    
	                    if (bytesToRead>0) {
	                    	Logger.verbose(DCOMD,"more data ("+bytesToRead+" bytes) remains in the socket");
	                    } else {
	                    	Logger.verbose(DCOMD,"no more data remains in the socket");
	                    }
	                    if (bytesToRead > receiveBufferSize) {
	                        bytesToRead = receiveBufferSize;
	                    }
	                    if (totalBytesRead+bytesToRead > maxReceiveSize) {
	                        // would be more than allowed
	                        bytesToRead = maxReceiveSize - totalBytesRead;
	                    }
					
					////////////////
					
				} while (((bytesToRead != 0) && (Data.getCurrentTime() <= endTime))
						&& (totalBytesRead < maxReceiveSize));

				Logger.verbose(DCOM, "totally read " + data.length()
						+ " bytes from socket");
				// System.err.println(">>>>>>" + "totally read " + data.length()
				// + " bytes from socket");
			} catch (IOException e) {
				Logger.verbose("IOException " + e);
				exception = e;
				e.printStackTrace();

			}
		} else if (connType == CONN_SERVER) {
			Logger
					.verbose("Attempt to receive data from server type connection.");
			// System.err.println("Attempt to receive data from server type connection.");
		} else {
			Logger.verbose("Unknown connection type = " + connType);
			// System.err.println("Unknown connection type = " + connType);
		}

		// debug.exit(DCOMD, this);
		if (exception != null) {
			throw exception;
		}
		return data;
	}

	/**
	 * Accepts new connection on server type connection, i.e. on ServerSocket.
	 * If new socket is returned from ServerSocket.accept(), creates new
	 * instance of TCPIPConnection with the new socket and returns it, otherwise
	 * returns null. The timeout for new connection accept is set by
	 * <code>setReceiveTimeout</code>, i.e. waits for new connection for this
	 * time and then, if none is requested, returns with null.
	 * 
	 * @see #TCPIPConnection(Socket)
	 * @see java.net.ServerSocket#accept()
	 * @see java.net.ServerSocket#setSoTimeout(int)
	 */
	public Connection accept() throws IOException {
		Logger.verbose(DCOMD, "receive");
		IOException exception = null;

		Connection newConn = null;
		if (connType == CONN_SERVER) {
			try {
				receiverSocket.setSoTimeout((int) getReceiveTimeout());
			} catch (SocketException e) {
				// don't care, we're just setting the timeout
			}
			Socket acceptedSocket = null;
			try {
				acceptedSocket = receiverSocket.accept();
			} catch (IOException e) {
				Logger.verbose(DCOMD, "Exception accepting socket (timeout?)"
						+ e);
			}
			if (acceptedSocket != null) {
				try {
					newConn = new CopyOfTCPIPConnection(acceptedSocket);
				} catch (IOException e) {
					Logger
							.verbose("IOException creating new client connection "
									+ e);
					//event
					//		.write(e,
					///				"IOException creating new client connection");
					exception = e;
				}
			}
		} else if (connType == CONN_CLIENT) {
			Logger
					.verbose("Attempt to receive data from client type connection.");
		} else {
			Logger.verbose("Unknown connection type = " + connType);
		}

		// Logger.verbose(DCOMD, this);
		if (exception != null) {
			throw exception;
		}
		return newConn;
	}

	/**
	 * Initialises input and output streams to the streams from socket for
	 * client type connection. Streams are used for sending and receiving data
	 * from the socket.
	 * 
	 * @see #inputStream
	 * @see #outputStream
	 * @see java.net.Socket#getInputStream()
	 * @see java.net.Socket#getOutputStream()
	 */
	private void initialiseIOStreams(Socket socket) throws IOException {
		if (connType == CONN_CLIENT) {
			inputStream = new BufferedInputStream(socket.getInputStream(),
					ioBufferSize);
			outputStream = new BufferedOutputStream(socket.getOutputStream(),
					ioBufferSize);
		} else if (connType == CONN_SERVER) {
			Logger
					.verbose("Attempt to initialise i/o streams for server type connection.");
		} else {
			Logger.verbose("Unknown connection type = " + connType);
		}
	}

	/**
	 * Sets the size for the io buffers of streams for accessing the socket. The
	 * size can only be changed before actual opening of the connection.
	 * 
	 * @see #initialiseIOStreams(Socket)
	 */
	public void setIOBufferSize(int ioBufferSize) {
		if (!opened) {
			this.ioBufferSize = ioBufferSize;
		}
	}

	/**
	 * Sets the size of the receiving buffer, which is used for reading from the
	 * socket. A buffer of this size is allocated for the reading.
	 * 
	 * @see #receive()
	 */
	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
		receiveBuffer = new byte[receiveBufferSize];
	}

	/**
	 * Sets the maximum size of the data which can be read in one call to the
	 * <code>receive</code> function. After reading of this amount of bytes the
	 * receive returns the data read even if there are more data in the socket.
	 * 
	 * @see #receive()
	 */
	public void setMaxReceiveSize(int maxReceiveSize) {
		this.maxReceiveSize = maxReceiveSize;
	}

}
