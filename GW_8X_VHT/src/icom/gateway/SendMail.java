package icom.gateway;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.*;
import javax.activation.*;

/**
 * @author Ben Keeping
 * 
 * Generic sendMail class, allows for sending emails with attachements.
 */
public class SendMail {
	static String mxServer;
	String toAddress, fromAddress;
	String subject, text;
	MimeBodyPart mbp = null;
	Multipart mp = new MimeMultipart();
	Logger logger = null;

	/**
	 * Construct an email. <BR>
	 * If the mxServer is unknown, then the static method SendMail.nslookup()
	 * can be called to retrieve the domain's mx server. <BR>
	 * Attachments are optional. <BR>
	 * 
	 * @param String
	 *            mxServer - The Mail eXchange server to send the mail to.
	 * @param String
	 *            toAddress - The recipient.
	 * @param String
	 *            fromAddress - The sender - can be anyting as long as looks
	 *            like an email address - eg 'me@somewhere.com'.
	 * @param String
	 *            subject - The mail's subject.
	 * @param String
	 *            text - The body of the mail.
	 */
	public SendMail(Logger logger, String mxServer, String toAddress,
			String fromAddress, String subject, String text) {
		SendMail.mxServer = mxServer;
		this.toAddress = toAddress;
		this.fromAddress = fromAddress;
		this.subject = subject;
		this.text = text;
		this.logger = logger;

	}

	/**
	 * Add an attachment to the mail (from a file on disk).
	 * 
	 * @param File
	 *            file - the File object to attach.
	 */
	public void attach(File file) {
		try {
			mbp = new MimeBodyPart();
			mbp.setFileName(file.getName());
			mbp.setDataHandler(new DataHandler(new FileDataSource(file)));
			mp.addBodyPart(mbp);
		} catch (MessagingException me) {
			logger.printStackTrace(me);
		}
	}

	class SendThread extends Thread {
		public void run() {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", mxServer);
			Session session = Session.getDefaultInstance(props, null);

			try {
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(fromAddress));
				msg.setRecipients(Message.RecipientType.TO, InternetAddress
						.parse(toAddress, false));
				msg.setSubject(subject);
				msg.setHeader("X-Mailer", "JavaMail");

				// If there have been attachments (one or more), then set the
				// text/body
				// as a MimeBodyPart, else set it on the Message. If this isn't
				// done,
				// then either text or an attachment is sent - not both !
				if (mbp != null) {
					MimeBodyPart mbp2 = new MimeBodyPart();
					mbp2.setText(text);
					mp.addBodyPart(mbp2);
					msg.setContent(mp);
				} else {
					msg.setText(text);
				}

				Transport.send(msg);
			} catch (AddressException ae) {
				logger.printStackTrace(ae);
			} catch (MessagingException me) {
				logger.printStackTrace(me);
			}
		}
	}

	/**
	 * Send a message using the contstructor properties. If there is also an
	 * attachment to send, add it too.
	 */
	public void send() {
		new SendThread().start();
	}

	/**
	 * Given a domain name like 'hotmail.com', perform an OS nslookup call, and
	 * loop it, looking for the word 'exchanger' in the line. On Linux and
	 * Windoze the mx mail server is always the last word/token in the line, so
	 * set it as such. This pays no attention to the preference of which mx
	 * server to use, but could (and should !) be built in really. Still, never
	 * mind.
	 * 
	 * @param String
	 *            domain - the domain to lookup.
	 */
	public String nslookup(String domain) {
		String mailserver = null;
		try {
			Process p = Runtime.getRuntime()
					.exec("nslookup -type=mx " + domain);
			BufferedReader br = new BufferedReader(new InputStreamReader(p
					.getInputStream()));

			boolean gotMxLine = false;
			String line = null;
			String token = null;

			while ((line = br.readLine()) != null) {
				gotMxLine = false;
				// System.out.println(line);
				StringTokenizer st = new StringTokenizer(line);
				while (st.hasMoreTokens()) {
					token = st.nextToken();
					if (token.equals("exchanger")) {
						gotMxLine = true;
					}
					if (gotMxLine) {
						mailserver = token;
					}
				}

			}
		} catch (IOException ioe) {
			logger.printStackTrace(ioe);
			return null;
		}

		//System.out.println("Mail Server to use is :: " + mailserver);
		return mailserver;
	}

	public static void main() {
		//System.out.println("AAA:" + "0959099".substring(4));

	}

	/**
	 * Method main.
	 * 
	 * @param args
	 *            Usuage :: <mxServer> <toAddress> <fromAddress> <subject>
	 *            <text>
	 */
	/*
	 * public static void main(String args[]) { if (args.length < 5) {
	 * System.out.println("Usuage :: <mxServer> <toAddress> <fromAddress>
	 * <subject> <text>"); System.exit(1); } String msgText = ""; for (int i =
	 * 4; i < args.length; i++) { msgText += (" " +args[i]); }
	 * 
	 * new SendMail(args[0], args[1], args[2], args[3], msgText).send(); }
	 */
}
