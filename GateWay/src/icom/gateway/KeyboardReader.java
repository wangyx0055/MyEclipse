package icom.gateway;

import java.io.*;
import java.util.*;
import java.sql.Timestamp;

import org.smpp.*;
import org.smpp.pdu.*;


public class KeyboardReader extends Thread {
 //   private Logger logger = new Logger("KeyboardReader");

    static BufferedReader keyboard =
        new BufferedReader(new InputStreamReader(System.in));

    public void run() {
        showMenu();
        String command = "";
        while (Gateway.running) {
            try {
                command = keyboard.readLine();
                this.processCommand(command);
            } catch (Exception e) {
                System.out.println(">>>Keyboard");
                System.out.println("run: " + e.getMessage());
            }
        }
    }

    private void showMenu() {
        System.out.println("");
        System.out.println("S - submit");
        System.out.println("R - reload");
        System.out.println("Q - quit");
        System.out.println("");
    }

    private void processCommand(String command) {
        if (command == null) return;
        command = command.trim().toUpperCase();

        if ("?".equals(command)) {
            showMenu();
        } else  if ("S".equals(command)) {
            String sFrom = this.getParam("Srce Address: ", "996");
            String sTo = this.getParam("Dest Address: ", "");
            // ContentType (Note that: these values must be corresponding to CT_... in the Contants.java file
            String sType = this.getParam("Type (0-TEXT, 1-RINGTONE, 2-OPER_LOGO, 3-CLI_ICON, 4-PIC_MSG):", "0");
            if ("0".equals(sType)) {
                String sMessage = this.getParam("Short Message: ", "");
                //SubmitSM ssm = SMSCTools.buildSubmitSM(sFrom, sTo, sMessage, 2,"Test",1);
               // Gateway.toSMSC.enqueue(ssm);
            } else {
                String sFile = this.getParam("File Path: ", "");
                try {
                    java.util.Collection vSubmits = EMSTools.buildSubmitEMS(sFile, sFrom, sTo, Integer.parseInt(sType),0,"Test",1);
                    if (vSubmits != null && vSubmits.size() > 0) {
                        for (java.util.Iterator it = vSubmits.iterator(); it.hasNext();) {
                            SubmitSM ssm = (SubmitSM) it.next();
                            Gateway.toSMSC.enqueue(ssm);
                        }
                    } else {
                        System.out.println("processCommand: No SubmitSM");
                    }
                } catch (Exception ex) {
                    System.out.println("processCommand: " + ex.getMessage());
                }
            }
        } else if ("R".equals(command)) {
            try {
                Preference.loadProperties(Gateway.propsFilePath);
                
            } catch (IOException e) {
                System.out.println("processCommand: khong tim thay file cau hinh " + Gateway.propsFilePath);
            }
        } else if ("Q".equals(command)) {
            Gateway.exit();
        } else if (command.startsWith("MB")) {
            // MB [849xxxx]
            String mobile = null;
            if (command.length() > 2) {
                mobile = command.substring(2).trim();
            }
            if (mobile != null && !"".equals(mobile)) {
                System.out.println("Looking for mobile: " + mobile);
                MobileBufferInfo info = MobileBuffer.lookup(mobile);
                if (info != null) {
                    System.out.println("MO Time  : " + new Timestamp(info.mo_Time * 1000));
                    System.out.println("MO Count : " + info.mo_Counter);
                    System.out.println("MT Time  : " + new Timestamp(info.mt_Time * 1000));
                    System.out.println("MT Count : " + info.mt_Counter);
                    System.out.println("CDR Count: " + info.cdr_Counter);
                } else {
                    System.out.println("Not found");
                }
            } else {
                // MobileBuffer size
                System.out.println("Total size of mobileBuffer: " + MobileBuffer.size());
            }
        } else if (command.startsWith("EB")) {
            String cmd = command.substring(2).trim();
            if (cmd.startsWith("LEARN")) {
                Gateway.learning = !Gateway.learning;
                System.out.println("EMSBuffer learning is " + (Gateway.learning ? "ON": "OFF"));
            }
        }
    }

    /**
     * Prompts the user to enter a string value for a parameter.
     */
    private String getParam(String prompt, String defaultValue) {
        String value = "";
        String promptFull = prompt;
        promptFull += defaultValue == null ? "" : " ["+defaultValue+"] ";
        System.out.print(promptFull);
        try {
            value = keyboard.readLine();
        } catch (IOException e) { }
        if (value.compareTo("") == 0) {
            return defaultValue;
        } else {
            return value;
        }
    }

}
