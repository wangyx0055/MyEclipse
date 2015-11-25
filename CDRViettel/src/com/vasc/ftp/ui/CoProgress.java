/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 *
 * JvFTP was developed by Bea Petrovicova <beapetrovicova@yahoo.com>.
 * The sources was donated to sourceforge.net under the terms
 * of GNU General Public License (GPL). Redistribution of any
 * part of JvFTP or any derivative works must include this notice.
 */
package com.vasc.ftp.ui;

import com.vasc.ftp.io.CoFile;

/**
 * Defines interface to display progress.
 *
 * @version 0.71 06/09/2003
 * @author Bea Petrovicova <beapetrovicova@yahoo.com>
 */
public interface CoProgress
{
   /** Sets file.
    * @param file Source file */
   abstract public void setFile(CoFile file);
   /** Sets to and file.
    * @param to Destination file
    * @param file Source file */
   abstract public void setFile(CoFile to, CoFile file);
   /** Sets progress.
    * @param increment Bytes transfered  */
   abstract public void setProgress(int increment);
   /** Handles transfer delay. */
   abstract public void setDelay(long increment);
   /** Chcecks for user abort request. */
   abstract public boolean isAborted();
}
