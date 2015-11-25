/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 *
 * JvFTP was developed by Bea Petrovicova <beapetrovicova@yahoo.com>.
 * The sources was donated to sourceforge.net under the terms
 * of GNU General Public License (GPL). Redistribution of any
 * part of JvFTP or any derivative works must include this notice.
 */
package com.vasc.ftp.io;

/**
 * Allows to compare CoFile objects.
 *
 * @version 0.71 06/09/2003
 * @author Bea Petrovicova <beapetrovicova@yahoo.com>
 * @see cz.dhl.io.CoFile
 */
public interface CoOrder
    /* Emulation for Java v1.0 and v1.1 = Remove Comparable! */
     {
    /** Compares two abstract pathnames lexicographically by name.
     * @exception ClassCastException */
    public int compareNameToIgnoreCase(CoOrder file);

    /** Compares two abstract pathnames lexicographically by extension.
     * @exception ClassCastException */
    public int compareExtToIgnoreCase(CoOrder file);

    /** Tests this abstract pathname whether
     * name starts with the given character. */
    public boolean startsWithIgnoreCase(char ch);

    /** Tests this abstract pathname for equality with the given extension.
     * @param filter must be uppercase string with a leading '.' sign;
     * example: ".TXT" or ".HTM" or ".HTML" etc ... */
    public boolean equalsExtTo(String filter);

    /** Tests this abstract pathname for equality with one of the given extensions.
     * @param filter must be array of uppercase strings with a leading '.' sign;
     * example: { ".TXT", ".HTM", ".HTML", etc ... } */
    public boolean equalsExtTo(String filter[]);

    /** Compares two abstract pathnames lexicographically (by pathname). */
    public int compareTo_1(Object o);

    /** Tests if corresponding connection to remote host is active. */
    public boolean isConnected();
}
