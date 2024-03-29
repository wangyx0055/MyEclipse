/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 *
 * JvFTP was developed by Bea Petrovicova <beapetrovicova@yahoo.com>.
 * The sources was donated to sourceforge.net under the terms
 * of GNU General Public License (GPL). Redistribution of any
 * part of JvFTP or any derivative works must include this notice.
 */
package com.vasc.ftp;

import com.vasc.ftp.ui.CoConsole;
import java.io.InputStream;
import java.io.IOException;

/**
 * Allows reading from FTP file.
 *
 * @version 0.71 06/09/2003
 * @author Bea Petrovicova <beapetrovicova@yahoo.com>
 * @see Ftp
 * @see FtpFile
 */
public class FtpInputStream extends InputStream {
    Ftp client;
    FtpDataSocket data;
    InputStream stream;

    FtpInputStream() {}

    /** Open 'retreive' InputStream for given filename.
     * <P><B>RETR</B> - retrieve</P>
     * <P>This command causes the server-DTP to transfer a copy of
     * the file, specified in the pathname, to the server or
     * user-DTP at the other end of the data connection.
     * The status and contents of the file at the server site
     * shall be unaffected.</P>
     * @param file the file to be opened for reading
     * @exception IOException socket error
     * @exception FileNotFoundException Server file not found. */
    public FtpInputStream(FtpFile file) throws IOException {
        client = null;
        data = new FtpDataSocket(file.client);
        stream = data.getInputStream("RETR " + file, file.getDataType());
    }

    /** Open 'retreive' concurent InputStream for given filename.
     * <P>Single ftp connection cannot handle <B>multiple concurent
     * data transfers</B>. This limitation can be eliminated by
     * creating new ftp connection for each concurent data
     * transfer. This constructor creates separate Ftp instance.</P>
     * <P>Note (1) supplying same CoConsole instance for
     * multiple sessions will produce messed output.</P>
     * <P>Note (2) This code may need to be run in separate
     * thread to process multiple files concurently.</P>
     * <P><B>RETR</B> - retrieve</P>
     * This command causes the server-DTP to transfer a copy of
     * the file, specified in the pathname, to the server or
     * user-DTP at the other end of the data connection.
     * The status and contents of the file at the server site
     * shall be unaffected.</P>
     * @param file the file to be opened for reading
     * @param connect login details
     * @param console message output
     * @exception IOException socket error
     * @see #FtpInputStream(FtpFile) */
    public FtpInputStream(FtpFile file, FtpConnect connect, CoConsole console)
        throws IOException {
        client = new Ftp();
        if (client.connect(connect)) {
            if (console != null)
                client.getContext().setConsole(console);
            file = new FtpFile(file.toString(), client);
            data = new FtpDataSocket(file.client);
            stream = data.getInputStream("RETR " + file, file.getDataType());
        } else
            throw new IOException("Connect failed.");
    }

    /** Close current data transfer and close data connection.
     * <P>If no reply <B>ABOR</B> - abort</P>
     * <P>This command tells the server to abort the previous FTP
     * service command and any associated transfer of data. No
     * action is to be taken if the previous command has been
     * completed (including data transfer). The control connection
     * is not to be closed by the server, but the data connection
     * must be closed. There are two cases for the server upon
     * receipt of this command:</P>
     * <P>(1) the FTP service command was already completed. The
     * server closes the data connection (if it is open) and
     * responds with a 226 reply, indicating that the abort
     * command was successfully processed.</P>
     * <P>(2) the FTP service command is still in progress. The
     * server aborts the FTP service in progress and closes the
     * data connection, returning a 426 reply to indicate that
     * the service request terminated abnormally. The server then
     * sends a 226 reply, indicating that the abort command was
     * successfully processed.</P>
     * @exception IOException socket error */
    public void close() throws IOException {
        IOException x = null;
        while (stream != null || data != null || client != null)
            try {
                InputStream i;
                FtpDataSocket d;
                Ftp c;
                if (stream != null) {
                    i = stream;
                    stream = null;
                    i.close();
                }
                if (data != null) {
                    d = data;
                    data = null;
                    d.close();
                }
                if (client != null) {
                    c = client;
                    client = null;
                    c.disconnect();
                }
            } catch (IOException e) {
                x = e;
            }
        if (x != null)
            throw x;
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read() throws IOException {
        return stream.read();
    }

    /**
     * Reads some number of bytes from the input stream and stores them into
     * the buffer array <code>b</code>. The number of bytes actually read is
     * returned as an integer.  This method blocks until input data is
     * available, end of file is detected, or an exception is thrown.
     *
     * <p> If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown.  If the length of
     * <code>b</code> is zero, then no bytes are read and <code>0</code> is
     * returned; otherwise, there is an attempt to read at least one byte. If
     * no byte is available because the stream is at end of file, the value
     * <code>-1</code> is returned; otherwise, at least one byte is read and
     * stored into <code>b</code>.
     *
     * <p> The first byte read is stored into element <code>b[0]</code>, the
     * next one into <code>b[1]</code>, and so on. The number of bytes read is,
     * at most, equal to the length of <code>b</code>. Let <i>k</i> be the
     * number of bytes actually read; these bytes will be stored in elements
     * <code>b[0]</code> through <code>b[</code><i>k</i><code>-1]</code>,
     * leaving elements <code>b[</code><i>k</i><code>]</code> through
     * <code>b[b.length-1]</code> unaffected.
     *
     * <p> If the first byte cannot be read for any reason other than end of
     * file, then an <code>IOException</code> is thrown. In particular, an
     * <code>IOException</code> is thrown if the input stream has been closed.
     *
     * <p> The <code>read(b)</code> method for class <code>InputStream</code>
     * has the same effect as: <pre><code> read(b, 0, b.length) </code></pre>
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> is there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        cz.dhl.ftp.FtpInputStream#read(byte[], int, int)
     */
    public int read(byte b[]) throws IOException {
        return stream.read(b);
    }

    /**
     * Reads up to <code>len</code> bytes of data from the input stream into
     * an array of bytes.  An attempt is made to read as many as
     * <code>len</code> bytes, but a smaller number may be read, possibly
     * zero. The number of bytes actually read is returned as an integer.
     *
     * <p> This method blocks until input data is available, end of file is
     * detected, or an exception is thrown.
     *
     * <p> If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown.
     *
     * <p> If <code>off</code> is negative, or <code>len</code> is negative, or
     * <code>off+len</code> is greater than the length of the array
     * <code>b</code>, then an <code>IndexOutOfBoundsException</code> is
     * thrown.
     *
     * <p> If <code>len</code> is zero, then no bytes are read and
     * <code>0</code> is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at end of
     * file, the value <code>-1</code> is returned; otherwise, at least one
     * byte is read and stored into <code>b</code>.
     *
     * <p> The first byte read is stored into element <code>b[off]</code>, the
     * next one into <code>b[off+1]</code>, and so on. The number of bytes read
     * is, at most, equal to <code>len</code>. Let <i>k</i> be the number of
     * bytes actually read; these bytes will be stored in elements
     * <code>b[off]</code> through <code>b[off+</code><i>k</i><code>-1]</code>,
     * leaving elements <code>b[off+</code><i>k</i><code>]</code> through
     * <code>b[off+len-1]</code> unaffected.
     *
     * <p> In every case, elements <code>b[0]</code> through
     * <code>b[off]</code> and elements <code>b[off+len]</code> through
     * <code>b[b.length-1]</code> are unaffected.
     *
     * <p> If the first byte cannot be read for any reason other than end of
     * file, then an <code>IOException</code> is thrown. In particular, an
     * <code>IOException</code> is thrown if the input stream has been closed.
     *
     * <p> The <code>read(b,</code> <code>off,</code> <code>len)</code> method
     * for class <code>InputStream</code> simply calls the method
     * <code>read()</code> repeatedly. If the first such call results in an
     * <code>IOException</code>, that exception is returned from the call to
     * the <code>read(b,</code> <code>off,</code> <code>len)</code> method.  If
     * any subsequent call to <code>read()</code> results in a
     * <code>IOException</code>, the exception is caught and treated as if it
     * were end of file; the bytes read up to that point are stored into
     * <code>b</code> and the number of bytes read before the exception
     * occurred is returned.  Subclasses are encouraged to provide a more
     * efficient implementation of this method.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in array <code>b</code>
     *                   at which the data is written.
     * @param      len   the maximum number of bytes to read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        cz.dhl.ftp.FtpInputStream#read()
     */
    public int read(byte b[], int off, int len) throws IOException {
        return stream.read(b, off, len);
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from this input
     * stream. The <code>skip</code> method may, for a variety of reasons, end
     * up skipping over some smaller number of bytes, possibly <code>0</code>.
     * This may result from any of a number of conditions; reaching end of file
     * before <code>n</code> bytes have been skipped is only one possibility.
     * The actual number of bytes skipped is returned.  If <code>n</code> is
     * negative, no bytes are skipped.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     */
    public long skip(long n) throws IOException {
        return stream.skip(n);
    }

    /**
     * Returns the number of bytes that can be read (or skipped over) from
     * this input stream without blocking by the next caller of a method for
     * this input stream.  The next caller might be the same thread or or
     * another thread.
     *
     * <p> The <code>available</code> method for class <code>InputStream</code>
     * always returns <code>0</code>.
     *
     * @return     the number of bytes that can be read from this input stream
     *             without blocking.
     * @exception  IOException  if an I/O error occurs.
     */
    public int available() throws IOException {
        return stream.available();
    }

    /**
     * Marks the current position in this input stream. A subsequent call to
     * the <code>reset</code> method repositions this stream at the last marked
     * position so that subsequent reads re-read the same bytes.
     *
     * <p> The <code>readlimit</code> arguments tells this input stream to
     * allow that many bytes to be read before the mark position gets
     * invalidated.
     *
     * <p> The general contract of <code>mark</code> is that, if the method
     * <code>markSupported</code> returns <code>true</code>, the stream somehow
     * remembers all the bytes read after the call to <code>mark</code> and
     * stands ready to supply those same bytes again if and whenever the method
     * <code>reset</code> is called.  However, the stream is not required to
     * remember any data at all if more than <code>readlimit</code> bytes are
     * read from the stream before <code>reset</code> is called.
     *
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     cz.dhl.ftp.FtpInputStream#reset()
     */
    public synchronized void mark(int readlimit) {
        stream.mark(readlimit);
    }

    /**
     * Repositions this stream to the position at the time the
     * <code>mark</code> method was last called on this input stream.
     *
     * <p> The general contract of <code>reset</code> is:
     *
     * <p><ul>
     *
     * <li> If the method <code>markSupported</code> returns
     * <code>true</code>, then:
     *
     *     <ul><li> If the method <code>mark</code> has not been called since
     *     the stream was created, or the number of bytes read from the stream
     *     since <code>mark</code> was last called is larger than the argument
     *     to <code>mark</code> at that last call, then an
     *     <code>IOException</code> might be thrown.
     *
     *     <li> If such an <code>IOException</code> is not thrown, then the
     *     stream is reset to a state such that all the bytes read since the
     *     most recent call to <code>mark</code> (or since the start of the
     *     file, if <code>mark</code> has not been called) will be resupplied
     *     to subsequent callers of the <code>read</code> method, followed by
     *     any bytes that otherwise would have been the next input data as of
     *     the time of the call to <code>reset</code>. </ul>
     *
     * <li> If the method <code>markSupported</code> returns
     * <code>false</code>, then:
     *
     *     <ul><li> The call to <code>reset</code> may throw an
     *     <code>IOException</code>.
     *
     *     <li> If an <code>IOException</code> is not thrown, then the stream
     *     is reset to a fixed state that depends on the particular type of the
     *     input stream and how it was created. The bytes that will be supplied
     *     to subsequent callers of the <code>read</code> method depend on the
     *     particular type of the input stream. </ul></ul>
     *
     * @exception  IOException  if this stream has not been marked or if the
     *               mark has been invalidated.
     * @see     cz.dhl.ftp.FtpInputStream#mark(int)
     * @see     java.io.IOException
     */
    public synchronized void reset() throws IOException {
        stream.reset();
    }

    /**
     * Tests if this input stream supports the <code>mark</code> and
     * <code>reset</code> methods.
     *
     * @return  <code>true</code> if this true type supports the mark and reset
     *          method; <code>false</code> otherwise.
     * @see     cz.dhl.ftp.FtpInputStream#mark(int)
     * @see     cz.dhl.ftp.FtpInputStream#reset()
     */
    public boolean markSupported() {
        return stream.markSupported();
    }
}