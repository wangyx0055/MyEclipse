/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 *
 * JvFTP was developed by Bea Petrovicova <beapetrovicova@yahoo.com>.
 * The sources was donated to sourceforge.net under the terms
 * of GNU General Public License (GPL). Redistribution of any
 * part of JvFTP or any derivative works must include this notice.
 */
package com.vasc.ftp.io;

import com.vasc.ftp.ui.CoConsole;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Defines interface to get input /
 * output stream on CoFile objects.
 *
 * @version 0.71 06/09/2003
 * @author Bea Petrovicova <beapetrovicova@yahoo.com>
 * @see CoFile
 */
public interface CoOpen {
    /** Get type of data transfer.
     * @return One of following optional values:
     * 'A'=ASCII, 'I'=BINARY; */
    abstract public char getDataType();
    /** Returns an input stream for this file. */
    abstract public InputStream getInputStream() throws IOException;
    /** Returns an output stream for this file. */
    abstract public OutputStream getOutputStream(boolean append)
        throws IOException;
    /** Returns an output stream for this file. */
    abstract public OutputStream getOutputStream() throws IOException;
    /** Creates a new file instance from this
     * abstract pathname and a child string. */
    abstract public CoFile newFileChild(String child);
    /** Creates a new file instance from a parent
     * of this abstract pathname and a name string. */
    abstract public CoFile newFileRename(String name);
    /** Gets console if implemented or null. */
    abstract public CoConsole getConsole();
}
