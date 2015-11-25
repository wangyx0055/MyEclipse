package com.vasc.smpp.cdr;

import java.io.*;
import java.util.*;

/**
* Debug Logging class for the pools (straight to stdout).
*/
public class DebugLogger {
        private static boolean enabled = false;

        static {
                try {
                        Process p = null;
                        try {
                                p = Runtime.getRuntime().exec("env");
                        } catch (Exception e) {
                                p = Runtime.getRuntime().exec("cmd /c set PRIMROSE_DEBUG");
                        }

                        if (p != null) {
                                p.waitFor();
                                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                                String line;
                                while ((line = br.readLine()) != null) {
                                        if (line.toUpperCase().indexOf("PRIMROSE_DEBUG=TRUE") > -1) {
                                                DebugLogger.setEnabled(true);
                                                break;
                                        }
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static boolean getEnabled() {
                return enabled;
        }

        public static void setEnabled(boolean bEnabled) {
                enabled = bEnabled;
        }

        /**
        * Print a stack trace of the exception to stdout
        */
        public static void printStackTrace(Throwable t) {
                if (!enabled) return;
                t.printStackTrace(System.out);
        }

        /**
        * Print to stdout a debug message
        */
        public static void log(String message) {
                if (!enabled) return;

                Calendar now = Calendar.getInstance();
                String nowString =  now.get(Calendar.DAY_OF_MONTH) +"/" +
                                                        (now.get(Calendar.MONTH)+1) + "/" +
                                                        now.get(Calendar.YEAR) + " " +
                                                        now.get(Calendar.HOUR_OF_DAY ) +":" +
                                                        now.get(Calendar.MINUTE) +":" +
                                                        now.get(Calendar.SECOND);

                message = nowString +" : PRIMROSE_DBG : " +message;
                System.out.println(message);
        }
}
