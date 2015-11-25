package com.vasc.common;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * <p>Title: VASC-MCommerce</p>
 * <p>Description: </p>
 * <p>Copyright: (c) 2002 by VASC</p>
 * <p>Company: VASC</p>
 * @author Huynh Ngoc Tuan
 * @version 1.0
 */

public class Mail {

  static String smtpHost = "smtp.vasc.com.vn";
  static String username = "trongtho";
  static String password = "lyduc";

  public static void main(String args[]) {
    Mail mail = new Mail();
    //mail.Send("president@whitehouse.gov", "George Bush", "trongtho@yahoo.com", "Hello ", "Chao cac ban :)<br>/George Bush");
    //mail.SendMailwithAttachment("president@whitehouse.gov", "George Bush", "tuanhn@vasc.com.vn", "Hello ", "Chao cac ban :)<br>/George Bush", "Thongke.cmd");
    try {
      mail.sendMsg2Mail(smtpHost, username, password,
                        "tuanhn@vasc.com.vn", "trongtho@yahoo.com",
                        "Hello", "Hi !!!");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void sendMsg2Mail(String MAIL_SERVER, String MAIL_USER,
                                  String MAIL_PWD, String FROM_ADDRESS,
                                  String TO_ADDRESS, String strBody,
                                  String strSubject) throws Exception {
    Properties props = new Properties();
    props.put("mail.smtp.host", MAIL_SERVER);
    props.put("mail.smtp.auth", "true"); // For check login
    props.put("mail.smtp.timeout", "120000");

    Session session = Session.getDefaultInstance(props, null);

    MimeMessage message = new MimeMessage(session);
    message.setHeader("X-Priority", "1 (Highest)");
    message.setHeader("X-MSMail-Priority", "High");
    message.setHeader("Sensitivity", "Company-Confidential");
    message.setHeader("Importance", "High");
    message.setFrom(new

                    InternetAddress(FROM_ADDRESS, "AloFun WebMaster"));
    message.addRecipient(Message.RecipientType.TO,
                         new InternetAddress(TO_ADDRESS));
    message.addRecipient(Message.RecipientType.BCC,
                         new InternetAddress("hdquy@vasc.com.vn"));
    message.setSubject(strSubject);
    MimeMultipart mp = new MimeMultipart();
    BodyPart tp = new MimeBodyPart();
//          tp.setText(strBody);
//          mp.addBodyPart(tp);
//          tp = new MimeBodyPart();
    tp.setContent(strBody, "text/html");
    mp.addBodyPart(tp);
    mp.setSubType("alternative");
//      message.setText(strBody);
    message.setContent(mp);
    Transport transport = session.getTransport("smtp");
//          transport.connect();
    transport.connect(MAIL_SERVER, MAIL_USER, MAIL_PWD); //for check log in

    transport.sendMessage(message, message.getAllRecipients());
    transport.close();
  }

  /**
   *
   * @param from : E-mail address of the sender
   * @param name : Display name of the sender
   * @param to   : E-mail address of the recipient
   * @param subject :
   * @param text : E-mail content, HTML Format
   * @return
   */

  public static boolean Send(String from, String name, String to,
                             String subject, String text) {
    try {
      // Get system properties
      Properties props = System.getProperties();

      // Setup mail server
      props.put("mail.smtp.host", smtpHost);

      // Get session
      Session session = Session.getDefaultInstance(props, null);

      // Define message
      MimeMessage msg = new MimeMessage(session);
      msg.setHeader("X-Priority", "1 (Highest)");
      msg.setHeader("X-MSMail-Priority", "High");
      msg.setHeader("Sensitivity", "Company-Confidential");
      msg.setHeader("Importance", "High");

      msg.setFrom(new InternetAddress(from, name));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      msg.setSubject(subject);
      MimeMultipart mp = new MimeMultipart();
      BodyPart tp = new MimeBodyPart();
      tp.setText(text);
      mp.addBodyPart(tp);
      tp = new MimeBodyPart();
      tp.setContent(text, "text/html");
      mp.addBodyPart(tp);
      mp.setSubType("alternative");
      msg.setContent(mp);
      Transport.send(msg);
      return true;
    }
    catch (MessagingException ex) {
      ex.printStackTrace();
      return false;
    }
    catch (java.io.UnsupportedEncodingException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean SendMailwithAttachment(String from, String name, String to,
                                        String subject, String text,
                                        String filename) {
    try {
      // Get system properties
      Properties props = System.getProperties();

      // Setup mail server
      props.put("mail.smtp.host", smtpHost);

      // Get session
      Session session = Session.getInstance(props, null);

      // Define message
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from, name));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSubject(subject);

      // Create the message part
      BodyPart messageBodyPart = new MimeBodyPart();

      // Fill the message
      //messageBodyPart.setText(text);
      messageBodyPart.setContent(text, "text/html");

      // Create a Multipart
      Multipart multipart = new MimeMultipart();

      // Add part one
      multipart.addBodyPart(messageBodyPart);

      /* Part two is attachment */
      // Create second body part
      messageBodyPart = new MimeBodyPart();

      // Get the attachment
      DataSource source = new FileDataSource(filename);

      // Set the data handler to the attachment
      messageBodyPart.setDataHandler(new DataHandler(source));

      // Set the filename
      messageBodyPart.setFileName(filename);

      // Add part two
      multipart.addBodyPart(messageBodyPart);

      // Put parts in message
      message.setContent(multipart);

      // Send the message
      Transport.send(message);

      return true;
    }
    catch (MessagingException ex) {
      ex.printStackTrace();
      return false;
    }
    catch (java.io.UnsupportedEncodingException ex) {
      ex.printStackTrace();
      return false;
    }

  }

}
