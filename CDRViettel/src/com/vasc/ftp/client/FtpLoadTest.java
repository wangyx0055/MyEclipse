package com.vasc.ftp.client;
import com.vasc.ftp.io.*;
import com.vasc.ftp.*;
import java.io.IOException;

public class FtpLoadTest {
    public static void main(String args[]) {
        /* host = telsoft.vasc.com.vn;
         * path = tho;
         * port = 21 (default); */
        FtpConnect conn = FtpConnect.newConnect("ftp://telsoft.vasc.com.vn/");
        /* Guest login ok, send your e-mail as password */
        conn.setUserName("tho");
        conn.setPassWord("tho123");
        conn.setPathName("tho");
        Ftp ftp = new Ftp();

        try { /* connect & login to host */
            ftp.connect(conn);

            /* source FtpFile remote file */
//            CoFile file = new FtpFile("a.txt", ftp);
            CoFile file = new LocalFile("D:\\Tho\\", "b.txt");
            System.out.println("From: " + file.toString());

            /* destination LocalFile home dir */
//            CoFile to = new LocalFile(System.getProperty("user.dir"), "tho");
//            CoFile to = new LocalFile("D:\\Tho\\", "b.txt");
            CoFile to = new FtpFile("b.txt", ftp);
            System.out.println("To:   " + to.toString());

            /* download /tho file to d:\tho dir*/
            System.out.println("Load: " + CoLoad.copy(to, file));
        } catch (IOException e) {
            System.out.println(e);
        } finally { /* disconnect from server
               	     * this must be always run */
            ftp.disconnect();
        }
    }
}
