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

/**
 * Maintains FTP client settings & context.
 *
 * @version 0.71 06/09/2003
 * @author Bea Petrovicova <beapetrovicova@yahoo.com>
 * @see Ftp
 */
public class FtpContext extends FtpSetting {

    private String textfilter[] = {".TXT",
            ".HTM", ".HTML", ".SHTML", ".CSS", ".JS", ".PL", ".PHP",
            ".H", ".C", ".HPP", ".CPP", ".JAVA",
            ".SQL", ".4GL", ".BAT", ".SH", ".AWK" };

    private CoConsole console = new CoConsole() {
        public void print(String message) {
            System.out.println(message);
        }
    };

    FtpContext() {
    }

    /** Sets array of strings representing text-file extensions.
     * @param textfilter must be array of uppercase strings with a leading '.' sign;
     * example: { ".TXT", ".HTM", ".HTML", etc ... };
       default settings is quite flexible */
    public void setTextFilter(String textfilter[]) {
        this.textfilter = textfilter;
    }

    /** Sets array of strings representing text-file extensions.
     * @return array of uppercase strings with a leading '.' sign;
     * example: { ".TXT", ".HTM", ".HTML", etc ... } */
    public String[] getTextFilter() {
        return textfilter;
    }

    /** Sets output console.
     * @see cz.dhl.ui.CoConsole */
    synchronized public void setConsole(CoConsole console) {
        this.console = console;
    }

    /** Gets output console.
     * @see cz.dhl.ui.CoConsole */
    synchronized public CoConsole getConsole() {
        return console;
    }

    /** Prints message line to output console. */
    public synchronized void printlog(String message) {
        if (console != null)
            console.print(message);
    }

    /** Prints object to standard output. */
    public void printerr(Exception exception) {
        System.out.println("Thread: " + Thread.currentThread().getName());
        System.out.println("Exception:");
        exception.printStackTrace();
    }
}
