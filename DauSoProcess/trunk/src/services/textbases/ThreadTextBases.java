package services.textbases;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.MsgObject;

public class ThreadTextBases extends Thread {

	private String Dir = "/home/oracle/Proces8x51/textbase/ticket/";
	private String delDir = "/home/oracle/Proces8x51/textbase/";

	public ThreadTextBases() {
		Dir = Constants._prop.getProperty("dirtextbase", Dir);
		delDir = Constants._prop.getProperty("deldirtextbase", delDir);
	}

	public java.sql.Timestamp String2timestamp(String input) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		java.util.Date date = null;
		try {
			date = sdf.parse(input);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (new java.sql.Timestamp(date.getTime()));
	}

	public static java.sql.Timestamp createTimestamp() {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		return new java.sql.Timestamp((calendar.getTime()).getTime());
	}

	public void run() {

		long timeout = 30 * 1000;
		Util.logger.sysLog(4, this.getClass().getName(), "Entering Run... ");
		try {
			while (true) {
				// Kiem tra tin ngam
				Vector[] result = retrieveTicketAndJobToDelete(Dir); // [0]TICKETS

				Vector tickets = result[0];
				Vector fileToDelete = result[1];
				Calendar cal = Calendar.getInstance();
				int nDay = cal.get(cal.DAY_OF_WEEK);
				int nHour = cal.get(cal.HOUR_OF_DAY);

				Util.logger.sysLog(4, this.getClass().getName(),
						"Textbase: tickets         " + tickets.size());
				// Util.logger.sysLog(4, this.getClass().getName(),
				// "Textbase:fileToDelete    " + fileToDelete.size());
				for (int i = 0; i < tickets.size(); i++) {
					VnnLinksTextbaseTicket rticket = (VnnLinksTextbaseTicket) tickets
							.get(i);
					String msisdn = rticket.getUserid();
					// String date = new SimpleDateFormat("yyyy-MM-dd")
					// .format(Calendar.getInstance().getTime());

					java.sql.Timestamp currTS = createTimestamp();

					// if (rticket.getSenddate().equalsIgnoreCase(date)) {
					if (currTS.after(String2timestamp(rticket.getSenddate()))) {

						// Util.logger.sysLog(4, this.getClass().getName(),
						// "Textbase:Ticket Details:  "
						// + rticket.getMtctn() + " "
						// + rticket.getSenddate() + " "
						// + rticket.getServices() + " "
						// + rticket.getUserid() + " "
						// + rticket.getOperator() + " "
						// + rticket.getServiceid());
						// send final result message
						MsgObject msg = new MsgObject();
						msg.setContenttype(0);
						msg.setUserid(rticket.getUserid());
						msg.setMobileoperator(rticket.getOperator()
								.toUpperCase());
						msg.setMsgtype(0);
						msg
								.setRequestid(new BigDecimal(rticket
										.getRequestid()));
						msg.setServiceid(rticket.getServiceid());
						msg.setKeyword(rticket.getServices());
						msg.setUsertext(rticket.getMtctn());

						File fileDel = (File) fileToDelete.get(i);
						if (!fileDel.renameTo(new File(delDir
								+ fileDel.getName()))) {
							Util.logger.sysLog(4, this.getClass().getName(),
									"TextBase:NOT ABLE TO MOVE TICKETS");
							throw new Exception(
									"TextBase:NOT ABLE TO MOVE TICKETS.!!");
						} else {
							// Xoa thanh cong --> gui

							Util.logger.sysLog(4, this.getClass().getName(),
									"textbase:begin send................"
											+ msisdn);

							DBUtil.sendMT(msg);

							Util.logger.sysLog(4, this.getClass().getName(),
									"textbase:Ticket sent.. ");

						}
						Util.logger.sysLog(4, this.getClass().getName(),
								"TextBase:Ticket Removed.. ");

					}

					Util.logger.sysLog(4, this.getClass().getName(),
							"Job Done, sleeping for " + timeout / 60000
									+ " Minute(s)....");

				}
				sleep(timeout);
			}
		} catch (Exception e) {
			Util.logger.sysLog(4, this.getClass().getName(), "Exception: ");
			Util.logger.sysLog(3, this.getClass().getName(), "ERROR   :"
					+ e.getMessage());

			try {
				sleep(timeout);
			} catch (Exception sleep) {
			}
			// SendMail.writeException(e, "vn");
		}

	}

	private Vector[] retrieveTicketAndJobToDelete() {

		Util.logger.sysLog(4, this.getClass().getName(),
				"Entered Retrieve Tickets");
		File directoryJob = new File(Dir);
		File[] jobFiles = directoryJob.listFiles();

		Vector tickets = new Vector();
		Vector fileToDelete = new Vector();

		int iterations = jobFiles.length;
		Util.logger.sysLog(4, this.getClass().getName(),
				"Retrieve Tickets: Number of files: " + jobFiles.length);
		if (iterations > 15000)
			iterations = 15000;
		for (int i = 0; i < iterations; i++) {
			File singleJob = jobFiles[i];
			if (singleJob.isDirectory())
				continue;

			VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
					.retrieveTicketFromFile(singleJob);
			Util.logger.sysLog(4, this.getClass().getName(),
					"Retrieve Tickets: Ticket Retrieved " + ticket.getUserid());
			if (ticket != null) {
				tickets.add(ticket);
				fileToDelete.add(singleJob);

			}
		}

		Vector[] result = new Vector[3];
		result[0] = tickets;
		result[1] = fileToDelete;

		return result;
	}

	private Vector[] retrieveTicketAndJobToDelete(String sDirectory) {

		Util.logger.sysLog(4, this.getClass().getName(),
				"Entered Retrieve Tickets");
		File directoryJob = new File(sDirectory);
		File[] jobFiles = directoryJob.listFiles();

		Vector tickets = new Vector();
		Vector fileToDelete = new Vector();

		int iterations = jobFiles.length;
		// Util.logger.sysLog(4, this.getClass().getName(),
		// "Retrieve Tickets: Number of files: " + jobFiles.length);
		if (iterations > 15000)
			iterations = 15000;
		for (int i = 0; i < iterations; i++) {
			File singleJob = jobFiles[i];
			if (singleJob.isDirectory())
				continue;

			VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
					.retrieveTicketFromFile(singleJob);
			// Util.logger.sysLog(4, this.getClass().getName(),
			// "Retrieve Tickets: Ticket Retrieved " + ticket.getUserid());
			if (ticket != null) {
				tickets.add(ticket);
				fileToDelete.add(singleJob);

			}
		}

		Vector[] result = new Vector[3];
		result[0] = tickets;
		result[1] = fileToDelete;

		return result;
	}

	private static void writeFile(String textFile, String nameFile,
			String directory) {
		try {

			String pathSeparator = File.separator;
			String pathFile = directory + nameFile;
			while (pathFile.indexOf(pathSeparator + pathSeparator) != -1) {
				String pre = pathFile.substring(0, pathFile
						.indexOf(pathSeparator + pathSeparator));
				String post = pathFile.substring(pathFile.indexOf(pathSeparator
						+ pathSeparator))
						+ (pathSeparator + pathSeparator).length();
				pathFile = pre + pathSeparator + post;
			}

			Util.logger.sysLog(5, "services.vasc.cdr.CDRTicket", "pathFile="
					+ pathFile);

			FileOutputStream outFile = new FileOutputStream(pathFile);
			DataOutputStream outData = new DataOutputStream(outFile);

			outData.write(textFile.getBytes());
			outData.flush();
			outFile.close();
			outData.close();

			Util.logger.sysLog(5, "services.vasc.cdr.CDRTicket",
					"writer job done");

		} catch (Exception ex) {
			Util.logger.sysLog(3, "services.vasc.cdr.CDRTicket",
					"!!!EXCEPTION: " + ex.getMessage());

		}
	}

}
