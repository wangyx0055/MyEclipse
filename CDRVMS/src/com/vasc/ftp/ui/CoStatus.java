/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 *
 * JvFTP was developed by Bea Petrovicova <beapetrovicova@yahoo.com>.
 * The sources was donated to sourceforge.net under the terms
 * of GNU General Public License (GPL). Redistribution of any
 * part of JvFTP or any derivative works must include this notice.
 */
package com.vasc.ftp.ui;

/**
 * Defines interface to display file status.
 *
 * @version 0.71 06/09/2003
 * @author Bea Petrovicova <beapetrovicova@yahoo.com>
 * @see cz.dhl.io.CoFile
 */
public interface CoStatus
{
   /** Display size. */
   abstract public void setSize(String s);
   /** Display date. */
   abstract public void setDate(String s);
}
