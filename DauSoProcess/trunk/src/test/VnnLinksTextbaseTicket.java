package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.vmg.sms.common.Util;

public class VnnLinksTextbaseTicket {

	private String startdate;
	private String senddate;
	private String userid;
	private String serviceid;
	private String mtctn;
	private String services;
	private String operator;
	private String route;
	private String requestid;

	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public static VnnLinksTextbaseTicket createReservationTicket(
			String mtcontent, String userid, String serviceid, String mtdate,
			String operator, String route, String services, String requestid) {
		VnnLinksTextbaseTicket ticket = new VnnLinksTextbaseTicket();

		ticket.setStartdate(new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new java.util.Date()));
		ticket.setSenddate(mtdate);
		ticket.setUserid(userid);
		ticket.setServiceid(serviceid);
		ticket.setMtctn(mtcontent);
		ticket.setServices(services);
		ticket.setOperator(operator);
		ticket.setRoute(route);
		ticket.setRequestid(requestid);

		return ticket;
	}

	public static String fromObjectToXML(VnnLinksTextbaseTicket reservation) {

		XStream xstream = new XStream(new DomDriver()); // does not require XPP3
		// library
		String xml = xstream.toXML(reservation);
		return xml;

	}

	public static VnnLinksTextbaseTicket fromXMLtoObject(String xml) {

		// Util.logger.sysLog(4, "fromXMLtoObject", "!!!!!!!xml: " + xml);

		XStream xstream = new XStream(new DomDriver()); // does not require XPP3
		// library
		VnnLinksTextbaseTicket reservation = (VnnLinksTextbaseTicket) xstream
				.fromXML(xml);
		// Util.logger.sysLog(4, "fromXMLtoObject", "Object Returned");
		return reservation;
	}

	public static void storeReservationTicket(VnnLinksTextbaseTicket ticket,
			String directory, String msisdn) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
		String timeNow = sdf.format(Calendar.getInstance().getTime());
		String random = "" + ((int) (Math.random() * 10000));
		String random1 = "" + ((int) (Math.random() * 50000));
		String nameFile = timeNow + "_" + random1 + "_" + msisdn.substring(1)
				+ "_" + random + "textbaseticket.xml";
		String xmlConetnt = fromObjectToXML(ticket);
		writeFile(xmlConetnt, nameFile, directory);
	}

	private static void writeFile(String textFile, String nameFile,
			String directory) throws Exception {

		String pathSeparator = File.separator;
		String pathFile = directory + pathSeparator + nameFile;

		Util.logger.sysLog(5, "Ticket", "separator=" + File.separator);
		Util.logger.sysLog(5, "Ticket", "directory=" + directory);
		/*
		 * while (pathFile.indexOf(pathSeparator + pathSeparator) != -1) {
		 * String pre = pathFile.substring(0, pathFile.indexOf(pathSeparator +
		 * pathSeparator)); String post =
		 * pathFile.substring(pathFile.indexOf(pathSeparator + pathSeparator)) +
		 * (pathSeparator + pathSeparator).length(); pathFile = pre +
		 * pathSeparator + post; Util.logger.sysLog(5, "Ticket", "pre=" + pre);
		 * Util.logger.sysLog(5, "post", "post=" + post); }
		 */
		Util.logger.sysLog(5, "BikiniFoto.vasc.cdr.CDRTicket", "pathFile="
				+ pathFile);

		FileOutputStream outFile = new FileOutputStream(pathFile);
		DataOutputStream outData = new DataOutputStream(outFile);

		outData.write(textFile.getBytes());
		outData.flush();
		outFile.close();
		outData.close();

		Util.logger.sysLog(5, "VnnLinksTextbaseTicket", "writer job done");

	}

	public static VnnLinksTextbaseTicket retrieveTicketFromFile(File fileJob) {

		try {
			// Util.logger.sysLog(4, "VnnLinksTextbaseTicket",
			// "!!!!!!!getAbsolutePath: " + fileJob.getAbsolutePath());

			FileInputStream fis = new FileInputStream(fileJob);
			InputStreamReader inputStreamReader = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringWriter stringWriter = new StringWriter();
			BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);

			String line = bufferedReader.readLine();
			while (line != null) {
				bufferedWriter.write(line);
				bufferedWriter.newLine();
				line = bufferedReader.readLine();
			}

			bufferedReader.close();
			inputStreamReader.close();
			fis.close();
			bufferedWriter.close();
			stringWriter.close();
			String xml = stringWriter.toString();

			// Util.logger.sysLog(4, "VnnLinksTextbaseTicket", "!!!!!!! XML: "
			// + xml);

			return fromXMLtoObject(xml);

		} catch (Exception e) {
			Util.logger.sysLog(3, "VnnLinksTextbaseTicket", "ERROR  :"
					+ e.getMessage());

			return null;
		}
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getSenddate() {
		return senddate;
	}

	public void setSenddate(String senddate) {
		this.senddate = senddate;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getServiceid() {
		return serviceid;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}

	public String getMtctn() {
		return mtctn;
	}

	public void setMtctn(String mtctn) {
		this.mtctn = mtctn;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

}
