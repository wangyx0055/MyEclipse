package com.vasc.ftp.client;

import com.vasc.ftp.io.*;
import com.vasc.ftp.*;
import java.io.IOException;

public class FtpListTest {
    public static void main(String args[]) {
        /*
         * host = telsoft.vasc.com.vn;
         * path = tho;
         * port = 21 (default);
         */
        FtpConnect conn = FtpConnect.newConnect("ftp://telsoft.vasc.com.vn/");
        /* Guest login ok, send your e-mail as password */
        conn.setUserName("tho"); // or anonymous (default)
        conn.setPassWord("tho123");
        conn.setPathName("tho");
        Ftp ftp = new Ftp();

        try { /* connect & login to host */
            ftp.connect(conn);

            /* get current directory */
            CoFile dir = new FtpFile(ftp.pwd(), ftp);

            /* list & print current directory */
            CoFile fls[] = dir.listCoFiles();
            if (fls != null)
                for (int n = 0; n < fls.length; n++)
                    System.out.println(fls[n].getName() + (fls[n].isDirectory() ? "/" : ""));
        } catch (IOException e) {
            System.out.println(e);
        } finally { /* disconnect from server
        	     * this must be always run */
            ftp.disconnect();
        }
    }
}
