package com.vasc.smpp.cdr;

/**
 * <p>Title: M-Commerce Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
 * @author Huynh Ngoc Tuan
 * @version 1.0
 */

import java.util.Vector;
import java.sql.Timestamp;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import com.vasc.common.DateProc;
import com.vasc.common.FileTool;
import com.vasc.smpp.gateway.*;

public class CDRTool
{
	public CDRTool()
	{
	}

	public synchronized static void add2CDRFile(CDR cdr)
	{
		String fileCDR = null;
		String fileCDRftp = null;
		String prefixfile = null;
		String writeData = "";
		if ("GPC".equals(cdr.getMobileOperator()) && cdr.getServiceId().length() < 6)
		{
			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".vasc");
			if (v.size() > 0)
			{
				for (int i = 0; i < v.size(); i++)
				{
					prefixfile = ((File) v.elementAt(i)).getName().substring(0, 4);
					if (!"1900".equals(prefixfile.trim()))
					{
						fileCDR = ((File) v.elementAt(i)).getName();
						break;
					}

					else
					{
						Timestamp ts = DateProc.createTimestamp();
						fileCDR = DateProc.getYYYYMMDDHHMMSSString(ts) + ".vasc";
					}

				}

			}

			else
			{
				Timestamp ts = DateProc.createTimestamp();
				fileCDR = DateProc.getYYYYMMDDHHMMSSString(ts) + ".vasc";
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_INTERNATIONAL);
				writeData = "M\t" + sId + "\t" + cdr.getUserId() + "\t" + "20" + cdr.getSubmitDate() + "\t" + "D\t" + "20" + cdr.getDoneDate() + "\t" + "0\t"
						+ "MAPMO.1:1\t" + "0\t" + "MAP.1:1\t" + "1/160\t" + "1\n";
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}

		} // Author:Maivq
			// Date: 29/08/2006 :
			// Tach 1900 rieng ra file _1900.vasc
		else if ("GPC".equals(cdr.getMobileOperator()) && cdr.getServiceId().length() > 6)
		{
			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".vasc");
			// if (v.size() > 0) {
			// fileCDR = ( (File) v.elementAt(0)).getName();
			// }
			if (v.size() > 0)
			{
				for (int i = 0; i < v.size(); i++)
				{
					prefixfile = ((File) v.elementAt(i)).getName().substring(0, 4);
					if ("1900".equals(prefixfile.trim()))
					{
						fileCDR = ((File) v.elementAt(i)).getName();
						break;
					}

					else
					{
						Timestamp ts = DateProc.createTimestamp();
						fileCDR = "1900vasc" + DateProc.getYYYYMMDDHHMMSSString(ts) + ".vasc";
					}

				}

			}

			else
			{
				Timestamp ts = DateProc.createTimestamp();
				fileCDR = "1900vasc" + DateProc.getYYYYMMDDHHMMSSString(ts) + ".vasc";
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_INTERNATIONAL);
				writeData = "M\t" + sId + "\t" + cdr.getUserId() + "\t" + "20" + cdr.getSubmitDate() + "\t" + "D\t" + "20" + cdr.getDoneDate() + "\t" + "0\t"
						+ "MAPMO.1:1\t" + "0\t" + "MAP.1:1\t" + "1/160\t" + "1\n";
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
			// CDR for VMS
			// The format of the CDR is a series of fields delimited by a colon
			// �:� as followed:
			// Sender:service_id:Service_Code:Service_Name:Receiver:TimeReceive:TimeSend:
			// UnitCharged:VolumnCharge:ProcResult:Info

		}
		else if ("VMS".equals(cdr.getMobileOperator()))
		{
			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".bin");
			if (v.size() > 0)
			{
				for (int i = 0; i < v.size(); i++)
				{
					prefixfile = ((File) v.elementAt(i)).getName().substring(18, 3);
					if ("bin".equals(prefixfile.trim()))
					{
						fileCDR = ((File) v.elementAt(i)).getName();
						break;
					}

					else
					{
						fileCDR = CdrFilename4vms.getNewFilename();
					}

				}

			}

			else
			{
				fileCDR = CdrFilename4vms.getNewFilename();
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_REGIONAL);
				String commandCode = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());
				String info = cdr.getInfo();
				if (info != null && info.length() > 15)
				{
					info = info.substring(0, 9) + "...";
				}

				writeData = cdr.getUserId() + ":" + sId + ":" + commandCode.substring(0, 2) + ":" + commandCode + ":" + cdr.getUserId() + ":" + "20"
						+ cdr.getSubmitDate() + ":" + "20" + cdr.getDoneDate() + ":" + cdr.getTotalSegments() + ":" + "0:" + "1:" + info + "\n";
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
			// Create file .cdr for FTP to VMS
			Vector vftp = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".cdr");
			if (vftp.size() > 0)
			{
				for (int i = 0; i < vftp.size(); i++)
				{
					prefixfile = ((File) vftp.elementAt(i)).getName().substring(19, 3);
					if ("cdr".equals(prefixfile.trim()))
					{
						fileCDRftp = ((File) vftp.elementAt(i)).getName();
						break;
					}

					else
					{
						fileCDRftp = CdrFilename4vms.getNewFilenameFTPforVMS();
					}

				}

			}

			else
			{
				fileCDRftp = CdrFilename4vms.getNewFilenameFTPforVMS();
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDRftp, true)); // append
																																						// =
																																						// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_REGIONAL);
				String commandCode = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());
				String info = cdr.getInfo();
				if (info != null && info.length() > 15)
				{
					info = info.substring(0, 9) + "...";
				}

				writeData = cdr.getUserId() + ":" + sId + ":" + commandCode.substring(0, 2) + ":" + commandCode + ":" + cdr.getUserId() + ":" + "20"
						+ cdr.getSubmitDate() + ":" + "20" + cdr.getDoneDate() + ":" + cdr.getTotalSegments() + ":" + "0:" + "1:" + info + "\n";
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}

			// CDR for VIETEL
			// The format of the CDR is a series of fields delimited by a colon
			// �:� as followed:
			// User_Id:Service_Id:Command_Code:TimeReceive:TimeSend:1:MTs
		}
		else if ("VIETEL".equals(cdr.getMobileOperator()))
		{
			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".bil");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				Timestamp ts = DateProc.createTimestamp();
				fileCDR = "VASC" + DateProc.getYYYYMMDDHHMMString(ts) + ".bil";
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_INTERNATIONAL);
				// Command_Code
				String commandCode = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());
				/*
				 * //User_Id fout.writeBytes(cdr.getUserId() + ":");
				 * //Service_Id String sId =
				 * SMSData.formatServiceId(cdr.getServiceId(),
				 * Constants.SERVICEID_FORMAT_INTERNATIONAL);
				 * fout.writeBytes(sId + ":"); //Command_Code String commandCode
				 * = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());
				 * fout.writeBytes(commandCode + ":"); //Time receive Request
				 * from handset fout.writeBytes("20" + cdr.getSubmitDate() +
				 * ":"); //Time send Reponse to handset fout.writeBytes("20" +
				 * cdr.getDoneDate() + ":"); //Process_Result = 1
				 * fout.writeBytes("1:"); //Number of MTs
				 * fout.writeBytes(cdr.getTotalSegments() + "\n");
				 */
				writeData = cdr.getUserId() + ":" + sId + ":" + commandCode + ":" + "20" + cdr.getSubmitDate() + ":" + "20" + cdr.getDoneDate() + ":" + "1:"
						+ cdr.getTotalSegments() + "\n";
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
		} // CDR for SFONE
			// The format of the CDR is a series of fields delimited by a colon
			// �:� as followed:
			//
			// User_Id:Service_Id:Command_Code:TimeReceive:TimeSend:1:MTs

		else if ("SFONE".equals(cdr.getMobileOperator()))
		{
			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".bil");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				Timestamp ts = DateProc.createTimestamp();
				fileCDR = "VASC_" + DateProc.getYYYYMMDDHHMMString(ts) + ".bil";
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_INTERNATIONAL);
				// Command_Code
				String commandCode = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());

				writeData = cdr.getUserId() + ":" + sId + ":" + commandCode + ":" + "20" + cdr.getSubmitDate() + ":" + "20" + cdr.getDoneDate() + ":" + "1:"
						+ cdr.getTotalSegments() + "\n";
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
		}
		// CDR for HTC
		// The format of the CDR is a series of fields delimited by a colon �:�
		// as followed:
		// ServiceType[1] \t Service_Id[11] \t User_Id[]11 \t SubmitTime[14] \t
		// \ServiceState[1] \t TimeSend[14]

		else if ("HTC".equals(cdr.getMobileOperator()))
		{

			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".txt");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				fileCDR = CdrFilename4vms.getNewFilenameELCOM();
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true

				String ServiceType = "M";
				String sender_number = cdr.getUserId();
				String UserId = SMSData.formatUserId(sender_number, Constants.USERID_FORMAT_INTERNATIONAL);
				String Service_Id = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_SHORTCODE);

				String TimeSend = cdr.getDoneDateFULL();
				String TimeReceive = cdr.getSubmitDateFULL();

				int ProcResult = 1;

				String ServiceState = "D";
				if (ProcResult == 1)
				{
					ServiceState = "D";
				}
				else
				{
					ServiceState = "U";

					/*
					 * int SegmentID=cdr.getTotalSegments(); int segmentNum=1;
					 * // Chi de TEST int SubscriberType=0; String MO="1";
					 * String MT="0";
					 */

				}
				String sData = ServiceType + "\t" + Service_Id + "\t" + UserId + "\t" + TimeReceive + "\t" + ServiceState + "\t" + TimeSend + "\r\n";

				fout.writeBytes(sData);
				fout.flush();
				fout.close();

			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}

		}
	}

	public synchronized static void add2CDRFileEx(CDR cdr)
	{
		String fileCDR = null;
		String fileCDRftp = null;
		String prefixfile = null;
		String sufixfile = null;
		boolean flag = false;
		int i = -1;
		
		if ("VIETTEL".equals(cdr.getMobileOperator().toUpperCase()))
		{
			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".bil");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
				i = 1;
			}
			else
			{
				Timestamp ts = DateProc.createTimestamp();
				// fileCDR = "VASC" + DateProc.getYYYYMMDDHHMMString(ts) +
				// "_997.bil";
				fileCDR = "hb" + DateProc.getYYYYMMDDHHMMSSString(ts) + ".bil";

				try
				{
					CdrFilename4vms.setNewFilename(fileCDR);
					i = 0;
				}
				catch (IOException ex1)
				{
				}

			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "/" + fileCDR, true)); // append
																																					// =
																																					// true
				// //841685951941:84996:go:20080212000008:20080212000010:1:2
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_INTERNATIONAL);
				String commandCode = cdr.getCommandCode().toUpperCase();
				if (commandCode.length() > 20)
				{
					commandCode = commandCode.substring(0, 10);
				}
				commandCode = commandCode.replaceAll(" ", "");
				commandCode = commandCode.replaceAll(":", "");
				String messageType = cdr.getMessageType().trim();
				String ProcessResult = cdr.getProcessResult().trim();
				int totalSeg = cdr.getTotalSegments();
				
				if ("0".equals(ProcessResult))
				{ 
					// khong gui thanh cong sang telcos
					messageType = "0"; // ghi cuoc hoan tien
				}
				else
				{ 
					// gui thanh cong sang telcos
					if (messageType.startsWith("2"))
					{ // ghi cuoc hoan tien tien
						messageType = "0";
					}
					else
					{
						messageType = "1";
					}
				}
				if (totalSeg == 0)
				{
					totalSeg = 1;
				}

				// sData = sData.substring(0,sData.length()-1);
				String sData = "";
				if (i == 0)
				{
					sData = cdr.getUserId() + ":" + sId + ":" + commandCode + ":" + cdr.getSubmitDate() + ":" + cdr.getDoneDate() + ":" + messageType + ":"
							+ totalSeg;
					fout.writeBytes(sData);
				}
				else if (i == 1)
				{
					sData = "\r\n" + cdr.getUserId() + ":" + sId + ":" + commandCode + ":" + cdr.getSubmitDate() + ":" + cdr.getDoneDate() + ":" + messageType
							+ ":" + totalSeg;
					fout.writeBytes(sData);
				}
				// Logger.info("KQQQQQQQQQ", i);
				System.out.println("sData_____:" + sData);
				System.out.println("iiii_____:" + i);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				// System.out.println("CDRTool::" + ex.getMessage());
				Logger.info("CDRTool:", ex.getMessage());
			}
		}
	}

	public static String getChargeValue(String serviceID)
	{
		String sShortCode = SMSData.formatServiceId(serviceID, Constants.SERVICEID_FORMAT_SHORTCODE);
		if (sShortCode == null || "".equals(sShortCode))
		{
			return "0";
		}
		if (sShortCode.startsWith("996") || sShortCode.startsWith("82"))
		{
			return "2000";
		}
		else if (sShortCode.startsWith("998") || sShortCode.startsWith("83"))
		{
			return "3000";
		}
		else if (sShortCode.startsWith("8499"))
		{
			return "4000";
		}
		else if (sShortCode.startsWith("85"))
		{
			return "5000";
		}
		else if (sShortCode.startsWith("87"))
		{
			return "15000";
		}
		else if (sShortCode.startsWith("190017") || sShortCode.startsWith("81") || sShortCode.startsWith("997"))
		{
			return "1000";
		}
		else
		{
			return "500";
		}
	}

	public synchronized static void add2CDRFile8x99(CDR cdr)
	{
		String fileCDR = null;
		String writeData = "";
		if ("GPC".equals(cdr.getMobileOperator()))
		{
			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".vasc");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				Timestamp ts = DateProc.createTimestamp();
				fileCDR = DateProc.getYYYYMMDDHHMMSSString(ts) + "_8x99.vasc";
			}
			try
			{

				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_INTERNATIONAL);
				writeData = "M\t" + sId + "\t" + cdr.getUserId() + "\t" + "20" + cdr.getSubmitDate() + "\t" + "D\t" + "20" + cdr.getDoneDate() + "\t" + "0\t"
						+ "MAPMO.1:1\t" + "0\t" + "MAP.1:1\t" + "1/160\t" + "9\n";
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
			// CDR for VMS
			// The format of the CDR is a series of fields delimited by a colon
			// �:� as followed:
			// Sender:service_id:Service_Code:Service_Name:Receiver:TimeReceive:TimeSend:
			// UnitCharged:VolumnCharge:ProcResult:Info

		}
		else if ("VMS".equals(cdr.getMobileOperator()))
		{

			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".cdr");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				fileCDR = CdrFilename4vms.getNewFilename8x99();
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_REGIONAL);
				String commandCode = cdr.getCommandCode();
				String info = cdr.getInfo();
				if (info != null && info.length() > 15)
				{
					info = info.substring(0, 9) + "...";
				}
				writeData = cdr.getUserId() + ":" + sId + ":" + commandCode + ":" + commandCode + ":" + cdr.getUserId() + ":" + "20" + cdr.getSubmitDate()
						+ ":" + "20" + cdr.getDoneDate() + ":" + "1:" + info + "\n";
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
			// CDR for VIETEL
			// The format of the CDR is a series of fields delimited by a colon
			// �:� as followed:
			// User_Id:Service_Id:Command_Code:TimeReceive:TimeSend:1:MTs
		}
		else if ("VIETEL".equals(cdr.getMobileOperator()))
		{
			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".bil");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				Timestamp ts = DateProc.createTimestamp();
				fileCDR = "VASC" + DateProc.getYYYYMMDDHHMMString(ts) + "_8x99.bil";
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_INTERNATIONAL);
				// Command_Code
				String commandCode = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());
				/*
				 * //User_Id fout.writeBytes(cdr.getUserId() + ":");
				 * //Service_Id String sId =
				 * SMSData.formatServiceId(cdr.getServiceId(),
				 * Constants.SERVICEID_FORMAT_INTERNATIONAL);
				 * fout.writeBytes(sId + ":"); //Command_Code String commandCode
				 * = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());
				 * fout.writeBytes(commandCode + ":"); //Time receive Request
				 * from handset fout.writeBytes("20" + cdr.getSubmitDate() +
				 * ":"); //Time send Reponse to handset fout.writeBytes("20" +
				 * cdr.getDoneDate() + ":"); //Process_Result = 1
				 * fout.writeBytes("1:"); //Number of MTs
				 * fout.writeBytes(cdr.getTotalSegments() + "\n");
				 */
				writeData = cdr.getUserId() + ":" + sId + ":" + commandCode + ":" + "20" + cdr.getSubmitDate() + ":" + "20" + cdr.getDoneDate() + ":" + "1:"
						+ cdr.getTotalSegments();
				fout.writeBytes(writeData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
		}
		else if ("EVN".equals(cdr.getMobileOperator()))
		{

			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".bil");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				fileCDR = CdrFilename4vms.getNewFilenameEVN();
				try
				{
					CdrFilename4vms.setNewFilename(fileCDR);
				}
				catch (IOException ex1)
				{
				}
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				// Sender
				// Service_Id (Note: VMS = 04xxx)
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_SHORTCODE);
				String commandCode = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());
				String info = cdr.getInfo();
				if (info != null && info.length() > 15)
				{
					info = info.substring(0, 9) + "...";
				}
				String sData = cdr.getUserId() + "," + sId + "," + "20" + cdr.getSubmitDate() + "," + "20" + cdr.getDoneDate() + "," + commandCode + "," + "1"
						+ "," + "1:1" + getChargeValue(sId) + "\n";
				fout.writeBytes(sData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
			// CDR for VIETEL
			// The format of the CDR is a series of fields delimited by a colon
			// �:� as followed:
			// User_Id:Service_Id:Command_Code:TimeReceive:TimeSend:1:MTs
		}
		else if ("SFONE".equals(cdr.getMobileOperator()))
		{

			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".bil");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				fileCDR = CdrFilename4vms.getNewFilename8x99SFONE();
			}
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																					// =
																																					// true
				// Sender
				// Service_Id (Note: VMS = 04xxx)
				// Service_Id (Note: SFONE 8495xxx)
				String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_SHORTCODE);
				// String commandCode = buildCommandCode(cdr.getServiceId(),
				// cdr.getCommandCode());
				String commandCode = "VASC";
				String info = cdr.getInfo();
				/*
				 * if (info != null && info.length() > 15) { info =
				 * info.substring(0, 9) + "..."; }
				 */

				int MessageLength = info.length();
				String MessageLength_a = Integer.toString(MessageLength);
				while (MessageLength_a.length() < 3)
				{
					MessageLength_a = MessageLength_a.concat(" ");
					// System.out.println(MessageLength+":"+info.length());
				}
				// System.out.println(MessageLength +" : " +info.length());

				String UserId = cdr.getUserId();
				while (UserId.length() < 20)
				{
					UserId = UserId.concat(" ");
				}
				String Service_Id = cdr.getServiceId();
				Service_Id = Service_Id.substring(2, Service_Id.length());
				while (Service_Id.length() < 20)
				{
					Service_Id = Service_Id.concat(" ");
				}
				String TimeSend = cdr.getDoneDateFULL();
				String TimeReceive = cdr.getSubmitDateFULL();

				while (commandCode.length() < 10)
				{
					commandCode = commandCode.concat(" ");
				}
				int ProcResult = 1;
				int SegmentID = cdr.getTotalSegments();
				int segmentNum = 1; // Chi de TEST
				int SubscriberType = 0;
				String MO = "1";
				String MT = "0";

				String sData = UserId + Service_Id + TimeSend + TimeReceive + commandCode + ProcResult + SegmentID + segmentNum + MessageLength_a
						+ SubscriberType + MT;

				fout.writeBytes(sData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
			// CDR for HTC
			// The format of the CDR is a series of fields delimited by a colon
			// �:� as followed:
			// ServiceType[1] \t Service_Id[11] \t User_Id[]11 \t SubmitTime[14]
			// \t \ServiceState[1] \t TimeSend[14]
		}
		else if ("HTC".equals(cdr.getMobileOperator()))
		{

			Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".txt");
			if (v.size() > 0)
			{
				fileCDR = ((File) v.elementAt(0)).getName();
			}
			else
			{
				fileCDR = CdrFilename4vms.getNewFilenameELCOM();
			}
			try
			{
				// java.io.DataOutputStream fout = new java.io.DataOutputStream(
				// new FileOutputStream(Preference.cdroutFolder + "\\" +
				// fileCDR, true)); // append = true
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true));

				String ServiceType = "M";

				String UserId = SMSData.formatUserId(cdr.getUserId(), Constants.USERID_FORMAT_INTERNATIONAL);
				String Service_Id = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_SHORTCODE);

				String TimeSend = cdr.getDoneDateFULL();
				String TimeReceive = cdr.getSubmitDateFULL();
				String ContCode = "103"; // Maivq them ngay 31/08/2006 ;Mobile
											// Terminated SMS
				String ContType = "2"; // Maivq them ngay 31/08/2006 ;ContType
										// SMS
				String Desc = "VASC-infor:" + cdr.getInfo(); // Maivq them ngay
																// 31/08/2006
																// ;Describer of
																// SMS
				int ProcResult = 1;

				String ServiceState = "D";
				if (ProcResult == 1)
				{
					ServiceState = "D";
				}
				else
				{
					ServiceState = "U";

					/*
					 * int SegmentID=cdr.getTotalSegments(); int segmentNum=1;
					 * // Chi de TEST int SubscriberType=0; String MO="1";
					 * String MT="0";
					 */

					/*
					 * String sData = ServiceType+"\t"+
					 * Service_Id+"\t"+UserId+"\t"+TimeReceive+"\t"+
					 * ServiceState+"\t"+TimeSend+"\r\n";
					 */
					// Maivq Edit:31/08/2006
					// Dinh dang noi dung 1 file nhu sau:
					// SERVICE_ID USER_ID SUBMIT_DATE SERVICE_STATE DONE_DATE
					// CONT_CODE CONT_TYPE DESC

				}
				String sData = Service_Id + "\t" + UserId + "\t" + TimeReceive + "\t" + ServiceState + "\t" + TimeSend + "\t" + ContCode + "\t" + ContType
						+ "\t" + Desc + "\r\n";
				fout.writeBytes(sData);
				fout.flush();
				fout.close();

			}
			catch (IOException ex)
			{
				System.out.println("CDRTool::" + ex.getMessage());
			}
		}
	}

	/* 996: DA; 997: XS; 998: IM */
	private static String buildCommandCode(String service_id, String originalCode)
	{
		String cc = "";
		if ("04996".equals(service_id) || "84996".endsWith(service_id) || "996".equals(service_id))
		{
			cc = "DA";
		}
		else if ("04997".equals(service_id) || "84997".endsWith(service_id) || "997".equals(service_id))
		{
			cc = "XS";
		}
		else if ("04998".equals(service_id) || "84998".endsWith(service_id) || "998".equals(service_id))
		{
			cc = "IM";
		}
		else
		{
			cc = originalCode;
		}
		return cc;
	}

	public synchronized static void add2CDRFile8x99ForPushVMS(CDR cdr)
	{
		String fileCDR = null;
		String writeData = "";
		Vector v = FileTool.getAllFiles(new File(".\\"), ".cdr");
		if (v.size() > 0)
		{
			fileCDR = ((File) v.elementAt(0)).getName();
		}
		else
		{
			System.out.println(">>>Khong tim thay file ...");
			return;
			// fileCDR = CdrFilename4vms.getNewFilename8x99();
		}
		try
		{
			java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(Preference.cdroutFolder + "\\" + fileCDR, true)); // append
																																				// =
																																				// true
			String sId = SMSData.formatServiceId(cdr.getServiceId(), Constants.SERVICEID_FORMAT_REGIONAL);
			String commandCode = buildCommandCode(cdr.getServiceId(), cdr.getCommandCode());
			String info = cdr.getInfo();
			if (info != null && info.length() > 15)
			{
				info = info.substring(0, 9) + "...";
			}
			writeData = cdr.getUserId() + ":" + sId + ":" + commandCode.substring(0, 2) + ":" + commandCode + ":" + cdr.getUserId() + ":" + "20"
					+ cdr.getSubmitDate() + ":" + "20" + cdr.getDoneDate() + ":" + "1:" + info + "\n";
			fout.writeBytes(writeData);
			fout.flush();
			fout.close();
		}
		catch (IOException ex)
		{
			System.out.println("CDRTool::" + ex.getMessage());
		}
		// CDR for VIETEL

		// The format of the CDR is a series of fields delimited by a colon �:�
		// as followed:
		// User_Id:Service_Id:Command_Code:TimeReceive:TimeSend:1:MTs
	}
}
