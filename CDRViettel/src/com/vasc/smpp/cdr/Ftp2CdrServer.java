package com.vasc.smpp.cdr;

import com.vasc.ftp.*;
import com.vasc.ftp.io.*;
import com.vasc.common.FileTool;
import java.util.Vector;
import java.io.File;
import java.io.IOException;
import com.vasc.common.*;
import com.vasc.smpp.gateway.DBTools;
import com.vasc.smpp.gateway.Preference;

public class Ftp2CdrServer extends Thread
{
	private Vector vFiles = new Vector(); // of File objects
	private Ftp ftp = null;
	private FileTool fileTool = null;
	private String foldername = "";
	private Logger Logger;

	public Ftp2CdrServer()
	{
		fileTool = new FileTool();
	}

	public void runftp()
	{

		if (CDRServer.running)
		{
			try
			{
				// System.out.println();
				this.vFiles = fileTool.getAllFiles(new File(FtpData.LOCAL_FOLDER), FtpData.FILE_EXTENSION);
				if (vFiles.size() > 0)
				{
					// System.out.println("**********************************************");
					// System.out.println("Time: " + new
					// java.sql.Timestamp(System.currentTimeMillis()));
					this.makeFtp();
					this.send2FtpServer();
					this.ftp.disconnect();
					// sleepByScheduleTime();
				}
				else
				{
					// System.out.println("No CDR file found, sleep in 1 min.");
					Logger.info("FTP ", "No CDR file found, sleep in 1 min");
					try
					{
						DBTools.log_alert("Billing system", "-> ERROR: No CDR file found ! ", 1, 0, "serious", Preference.alert_person);
					}
					catch (Exception e)
					{
					}

					sleepEx(1);
				}
			}
			catch (IOException e)
			{
				// System.out.println("Ftp2CdrServer error: " + e.getMessage());
				Logger.info("FTP ", "Ftp2CdrServer error: " + e.getMessage());
			}
			// catch (InterruptedException e) {}
		}

	}

	public void run()
	{
		// System.out.println("Ftp2CdrServer started (sleep in 1 min)");
		sleepEx(1);
		while (CDRServer.running)
		{
			try
			{
				// System.out.println();
				this.vFiles = fileTool.getAllFiles(new File(FtpData.LOCAL_FOLDER), FtpData.FILE_EXTENSION);
				if (vFiles.size() > 0)
				{
					System.out.println("**********************************************");
					System.out.println("Time: " + new java.sql.Timestamp(System.currentTimeMillis()));
					this.makeFtp();
					this.send2FtpServer();
					this.ftp.disconnect();
					sleepByScheduleTime();
				}
				else
				{
					System.out.println("No CDR file found, sleep in 1 min.");
					try
					{
						// DBTools.log_alert(Preference.sourceAddressList.toString(),
						// "CDR->FTP",
						// "<-" + Preference.mobileOperator +
						// "-> ERROR: No CDR file found !!",
						// 1, Preference.alert_person,
						// Preference.alert_mobile);
						DBTools.log_alert("Billing system", "-> ERROR: No CDR file found ! ", 1, 0, "serious", Preference.alert_person);
					}
					catch (Exception e)
					{
					}

					sleepEx(1);
				}
			}
			catch (IOException e)
			{
				// System.out.println("Ftp2CdrServer error: " + e.getMessage());
				Logger.info("FTP ", "Ftp2CdrServer error: " + e.getMessage());

			}
			catch (InterruptedException e)
			{
			}
		}
	}

	private void sleepByScheduleTime() throws InterruptedException
	{
		/* Ngay sau 00h --> Tao 1 file cuoc moi */
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(new java.util.Date(System.currentTimeMillis()));
		int currHour = calendar.get(calendar.HOUR_OF_DAY);
		if ((24 - currHour) * 60 < 2 * FtpData.SCHEDULE_TIME)
		{
			sleep((FtpData.SCHEDULE_TIME + 1) * 60000); // 00h:01
		}
		else
		{
			sleep(FtpData.SCHEDULE_TIME * 60000); // n * minutes;
		}
	}

	private void sleepEx(int minute)
	{
		try
		{
			sleep(minute * 60000); // n * minutes;
		}
		catch (Exception ex)
		{
		}
	}

	public void makeFtp() throws IOException
	{
		FtpConnect conn = FtpConnect.newConnect(FtpData.FTP_SERVER);
		conn.setUserName(FtpData.FTP_USER);
		conn.setPassWord(FtpData.FTP_PASSWORD);
		conn.setPathName(FtpData.FTP_PATH);

		Ftp ftp = new Ftp();
		/* connect & login to host */
		ftp.connect(conn);
		this.ftp = ftp;
	}

	// Load files onto FTP server and also delete files.
	public int send2FtpServer()
	{
		CoFile file = null;
		CoFile to = null;
		String filename = "";
		String dest_filename = "";
		String sum_line = "";
		boolean loadResult = false;
		boolean renameFileResult = false;
		for (int i = 0; i < vFiles.size(); i++)
		{
			filename = ((File) (vFiles.elementAt(i))).getName();
			/* source FtpFile remote file */
			file = new LocalFile(FtpData.LOCAL_FOLDER, filename);
			// System.out.println("From: " + file.toString());
			/* destination LocalFile home dir */
			// ---------------------count ftp file
			// site--------------------------------
			File f = new File(FtpData.LOCAL_FOLDER + "/" + filename);
			String fileLength = String.valueOf(f.length());
			// System.out.println("file size: " + fileLength);
			// ---------------------count ftp file
			// site--------------------------------

			// to = new FtpFile(foldername + "/tmp_" + filename, this.ftp);
			to = new FtpFile(foldername + "/" + filename.replaceAll(".bil", ".tmp"), this.ftp);
			// System.out.println("To:   " + to.toString());
			/* download file */
			Logger.info("FTP:", "from:" + file);
			Logger.info("FTP:", "to:" + to);
			loadResult = CoLoad.copy(to, file);
			Logger.info("FTP:" + loadResult);

			// System.out.println("Load: " + loadResult);
			// rename file sau khi ftp sang
			// System.out.print("Start rename file  cdr: " + "tmp_" + filename +
			// "-->" + filename);
			// renameFileResult = this.ftp.mv(foldername + "/tmp_" + filename,
			// foldername + "/" + filename);
			Logger.info("RENAME:" + filename.replaceAll(".bil", ".tmp") + "--->" + filename.replaceAll(".tmp", ".bil"));
			renameFileResult = this.ftp.mv(filename.replaceAll(".bil", ".tmp"), filename.replaceAll(".tmp", ".bil"));
			Logger.info("RENAME: " + renameFileResult);

			if (loadResult && renameFileResult)
			{ // ok -->delete file.
				try
				{
					String sDateFolder = DateProc.Timestamp2YYYYMMDD(DateProc.createTimestamp(), "-");

					// File dest_dir = new File(FtpData.SENT_FOLDER + "\\" +
					// sDateFolder);
					File dest_dir = new File(FtpData.SENT_FOLDER_VMS + "/" + sDateFolder);

					boolean exists = dest_dir.exists();
					if (!exists)
					{
						// Directory does not exist --> create it
						boolean success = dest_dir.mkdirs();
					}
					// fileTool.copy(FtpData.LOCAL_FOLDER + "\\" +
					// file.getName(),
					// FtpData.SENT_FOLDER + "\\" + sDateFolder + "\\" +
					// file.getName());
					fileTool.copy(FtpData.LOCAL_FOLDER + "/" + file.getName(), FtpData.SENT_FOLDER_VMS + "/" + sDateFolder + "/" + file.getName());
					file.delete();
					CdrFilename4vms.setNewFilenameVMS(filename);
					Logger.info("FTP : file name: " + filename + "# filesize: " + fileLength + " bytes");
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					Logger.info("FTP", ex.getMessage());
				}
			}
			else
			{
				try
				{
					if ((loadResult = false) && (renameFileResult = true))
					{
						// DBTools.log_alert(Preference.sourceAddressList.toString(),
						// "CDR->FTP",
						// "<-" + Preference.mobileOperator +
						// "-> ERROR: Khong ftp duoc file cuoc sang telcos!",
						// 1, Preference.alert_person,
						// Preference.alert_mobile);
						DBTools.log_alert("Billing system", "-> ERROR: Khong ftp duoc file cuoc sang telcos! ", 1, 0, "serious", Preference.alert_person);
					}
					else if ((loadResult = true) && (renameFileResult = false))
					{
						// DBTools.log_alert(Preference.sourceAddressList.toString(),
						// "CDR->FTP",
						// "<-" + Preference.mobileOperator +
						// "-> ERROR: Khong rename duoc file cuoc sau khi ftp sang telcos!",
						// 1, Preference.alert_person,
						// Preference.alert_mobile);
						DBTools.log_alert("Billing system", "-> ERROR: Khong rename duoc file cuoc sau khi ftp ! ", 1, 0, "serious", Preference.alert_person);
					}
					else
					{
						// DBTools.log_alert(Preference.sourceAddressList.toString(),
						// "CDR->FTP",
						// "<-" + Preference.mobileOperator +
						// "-> ERROR: Loi ftp file cuoc",
						// 1, Preference.alert_person, Preference.alert_mobile);
						DBTools.log_alert("Billing system", "-> ERROR: Loi ftp file cuoc ! ", 1, 0, "serious", Preference.alert_person);

					}

				}
				catch (Exception e)
				{
				}
			}
		}
		return this.vFiles.size();
	}

	public static void main(String[] arguments) throws Exception
	{
		Ftp2CdrServer sender = new Ftp2CdrServer();
		FtpData.loadProperties("ftp2cdrserver.cfg");
		sender.start();
	}
}
