package com.vasc.smpp.cdr;

/**
 * <p>Title: M-Commerce Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
 * @author Huynh Ngoc Tuan
 * @version 1.0
 */

import java.io.*;
import java.util.Vector;
import java.sql.Timestamp;

import com.vasc.common.DateProc;
import com.vasc.common.FileTool;
import com.vasc.smpp.gateway.*;

//This thread will copy .bill files from .\CDROUT dir to .\CDRSENT dir
//It also creates a new file in the .\CDROUT dir.
//Note: this applies only for VMS (not GPC).
public class CdrFileCopier4vms extends Thread
{
	// static String OUT_FOLDER = "..\\CDROUT";
	// static String SENT_FOLDER = "..\\CDRSENT";
	static String VMS_FOLDER = "..\\CDRVMS";
	static String FILE_EXTENSION = ".cdr";
	static String FILE_EXTENSION_SFONE = ".bil";
	static String FILE_EXTENSION_8X = ".cdr";

	// static int SCHEDULE_TIME = 10; // minutes

	private Vector vFiles = new Vector(); // of File objects
	private FileTool fileTool = null;

	public CdrFileCopier4vms()
	{
		fileTool = new FileTool();
	}

	public void run()
	{
		System.out.println("CDRFileCopier started");
		sleepEx(1); // 1min
		while (CDRServer.running)
		{
			try
			{
				System.out.println();
				if ("Y".equals(FtpData.IS_8X))
				{
					if ("SFONE".equals(Preference.mobileOperator))
					{
						this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_8X);
						if (vFiles.size() > 0)
						{
							// 1) Updates a new filename in the
							// vms_lastcdrfile.dat
							CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename8x99SFONE());
							// 2) Moves all current file
							this.move2Sendout();
							sleep10Minutes();
						}
						else
						{
							sleep(10000); // 10secs
						}

					}
					else
					{
						this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_8X);
						if (vFiles.size() > 0)
						{
							// 1) Updates a new filename in the
							// vms_lastcdrfile.dat
							CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename8x99());
							// 2) Moves all current file
							this.move2Sendout();
							sleep10Minutes();
						}
						else
						{
							sleep(10000); // 10secs
						}
					}
				}
				else if ("SFONE".equals(Preference.mobileOperator))
				{
					this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_SFONE);
					if (vFiles.size() > 0)
					{
						// 1) Updates a new filename in the
						// sfone_lastcdrfile.dat
						CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilenameSFONE());
						// 2) Moves all current file
						this.move2Sendout();
						sleep10Minutes();
					}
					else
					{
						sleep(10000); // 10secs
					}
				}
				else
				{
					this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION);
					if (vFiles.size() > 0)
					{
						// 1) Updates a new filename in the vms_lastcdrfile.dat
						CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename());
						// 2) Moves all current file
						this.move2Sendout();
						sleep10Minutes();
					}
					else
					{
						sleep(10000); // 10secs
					}
				}
			}
			catch (InterruptedException e)
			{
			}
			catch (IOException ex)
			{
				// CdrFilename4vms.setNewFilename() may got this error:
				// The requested operation cannot be performed on a file
				// with a user-mapped section open
				System.out.println("CdrFileCopier4vms.run(): " + ex.getMessage());
				try
				{
					sleep(10 * 1000);
				}
				catch (InterruptedException e)
				{
				}
			}
			catch (Exception e)
			{
				System.out.println("CdrFileCopy4vms error: " + e.getMessage());
			}
		}
	}

	public void runcopy()
	{

		System.out.println("CDRFileCopier started");
		// sleepEx(1); //1min
		if (CDRServer.running)
		{
			try
			{
				System.out.println();
				if ("Y".equals(FtpData.IS_8X))
				{
					if ("SFONE".equals(Preference.mobileOperator))
					{
						System.out.println("SFONE");
						this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_SFONE);
						if (vFiles.size() > 0)
						{
							// 1) Updates a new filename in the
							// vms_lastcdrfile.dat
							CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename8x99SFONE());
							// 2) Moves all current file
							this.move2Sendout();
							System.out.println(">>SFONE");
							// sleep10Minutes();
						}
						// else {
						// sleep(10000); //10secs
						// }

					}
					else
					{
						this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_8X);
						if (vFiles.size() > 0)
						{
							// 1) Updates a new filename in the
							// vms_lastcdrfile.dat
							CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename8x99());
							// 2) Moves all current file
							this.move2Sendout();
							// sleep10Minutes();
						}
						// else {
						// sleep(10000); //10secs
						// }
					}
				}
				else if ("SFONE".equals(Preference.mobileOperator))
				{
					this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_SFONE);
					if (vFiles.size() > 0)
					{
						// 1) Updates a new filename in the
						// sfone_lastcdrfile.dat
						CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilenameSFONE());
						// 2) Moves all current file
						this.move2Sendout();
						// sleep10Minutes();
					}
					// else {
					// sleep(10000); //10secs
					// }
				}
				else
				{
					this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION);
					if (vFiles.size() > 0)
					{
						// 1) Updates a new filename in the vms_lastcdrfile.dat
						CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename());
						// 2) Moves all current file
						this.move2Sendout();
						// sleep10Minutes();
					}
					// else {
					// sleep(10000); //10secs
					// }
				}
				// } catch (InterruptedException e) {
			}
			catch (IOException ex)
			{
				// CdrFilename4vms.setNewFilename() may got this error:
				// The requested operation cannot be performed on a file
				// with a user-mapped section open
				System.out.println("CdrFileCopier4vms.run(): " + ex.getMessage());
				try
				{
					sleep(10 * 1000);
				}
				catch (InterruptedException e)
				{
				}
			}
			catch (Exception e)
			{
				System.out.println("CdrFileCopy4vms error: " + e.getMessage());
			}
		}

	}

	// Maivq them ngay 08/09/2006
	public void runcopyVMS()
	{
		System.out.println("CDRFileCopier started");
		// sleepEx(1); //1min
		if (CDRServer.running)
		{
			try
			{
				System.out.println();
				if ("Y".equals(FtpData.IS_8X))
				{
					if ("SFONE".equals(Preference.mobileOperator))
					{
						System.out.println("SFONE");
						this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_SFONE);
						if (vFiles.size() > 0)
						{
							// 1) Updates a new filename in the
							// vms_lastcdrfile.dat
							CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename8x99SFONE());
							// 2) Moves all current file
							this.move2Sendout();
							System.out.println(">>SFONE");
							// sleep10Minutes();
						}
						// else {
						// sleep(10000); //10secs
						// }

					}
					else
					{
						this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_8X);
						if (vFiles.size() > 0)
						{
							// 1) Updates a new filename in the
							// vms_lastcdrfile.dat
							CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename8x99());
							// 2) Moves all current file
							this.move2Sendout();
							// sleep10Minutes();
						}
						// else {
						// sleep(10000); //10secs
						// }
					}
				}
				else if ("SFONE".equals(Preference.mobileOperator))
				{
					this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION_SFONE);
					if (vFiles.size() > 0)
					{
						// 1) Updates a new filename in the
						// sfone_lastcdrfile.dat
						CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilenameSFONE());
						// 2) Moves all current file
						this.move2Sendout();
						// sleep10Minutes();
					}
					// else {
					// sleep(10000); //10secs
					// }
				}
				else
				{
					this.vFiles = fileTool.getAllFiles(new File(Preference.cdroutFolder), FILE_EXTENSION);
					if (vFiles.size() > 0)
					{
						// 1) Updates a new filename in the vms_lastcdrfile.dat
						CdrFilename4vms.setNewFilename(CdrFilename4vms.getNewFilename());
						// 2) Moves all current file
						this.move2SendoutVMS();
						// sleep10Minutes();
					}
					// else {
					// sleep(10000); //10secs
					// }
				}
				// } catch (InterruptedException e) {
			}
			catch (IOException ex)
			{
				// CdrFilename4vms.setNewFilename() may got this error:
				// The requested operation cannot be performed on a file
				// with a user-mapped section open
				System.out.println("CdrFileCopier4vms.run(): " + ex.getMessage());
				try
				{
					sleep(10 * 1000);
				}
				catch (InterruptedException e)
				{
				}
			}
			catch (Exception e)
			{
				System.out.println("CdrFileCopy4vms error: " + e.getMessage());
			}
		}

	}

	private boolean move2SendoutVMS()
	{
		for (int i = 0; i < vFiles.size(); i++)
		{
			File source_file = (File) vFiles.elementAt(i);

			String sDateFolder = DateProc.Timestamp2YYYYMMDD(DateProc.createTimestamp(), "-");
			String sDateVMS = DateProc.Timestamp2YYYYMMDD(DateProc.createTimestamp(), "");
			File dest_dir = new File(Preference.cdrsentFolder + "\\" + sDateFolder);
			File vms_dir = new File(VMS_FOLDER + "\\" + sDateVMS);
			boolean exists = dest_dir.exists();
			if (!exists)
			{
				// Directory does not exist --> create it
				dest_dir.mkdirs();
			}
			exists = vms_dir.exists();
			if (!exists)
			{
				// Directory does not exist --> create it
				vms_dir.mkdirs();
			}
			// File dest_file = new File(SENT_FOLDER + "\\" +
			// source_file.getName());
			File dest_file = new File(Preference.cdrsentFolder + "\\" + sDateFolder + "\\" + source_file.getName());
			File vms_file = null;
			if ("Y".equals(FtpData.IS_8X))
			{
				if ("SFONE".equals(Preference.mobileOperator))
				{
					vms_file = new File(VMS_FOLDER + "\\" + source_file.getName());
					System.out.println("Test moving>>SFONE");
				}
				else
				{
					vms_file = new File(VMS_FOLDER + "\\" + sDateVMS + "\\" + source_file.getName());
				}
			}
			else
			{
				vms_file = new File(VMS_FOLDER + "\\" + source_file.getName());
			}
			try
			{
				long time = System.currentTimeMillis();
				System.out.print(DateProc.getDateTime24hString(new Timestamp(time)));
				System.out.print("  moving file " + source_file + "...");
				fileTool.moveVMS(source_file, dest_file);
				fileTool.copy(dest_file, vms_file);
				// source_file.delete();
				// fileTool.move(source_file, dest_file);
				System.out.println(" OK!");
			}
			catch (IOException e)
			{
				System.out.println("CdrFileCopy4vms.move2Sendout error: " + e.getMessage());
				return false;
			}
		}
		return true;
	}

	// End

	private void sleep10Minutes() throws InterruptedException
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

	// moves all current file & also
	private boolean move2Sendout()
	{
		for (int i = 0; i < vFiles.size(); i++)
		{
			File source_file = (File) vFiles.elementAt(i);

			String sDateFolder = DateProc.Timestamp2YYYYMMDD(DateProc.createTimestamp(), "-");
			String sDateVMS = DateProc.Timestamp2YYYYMMDD(DateProc.createTimestamp(), "");
			File dest_dir = new File(Preference.cdrsentFolder + "\\" + sDateFolder);
			File vms_dir = new File(VMS_FOLDER + "\\" + sDateVMS);
			boolean exists = dest_dir.exists();
			if (!exists)
			{
				// Directory does not exist --> create it
				dest_dir.mkdirs();
			}
			exists = vms_dir.exists();
			if (!exists)
			{
				// Directory does not exist --> create it
				vms_dir.mkdirs();
			}
			// File dest_file = new File(SENT_FOLDER + "\\" +
			// source_file.getName());
			File dest_file = new File(Preference.cdrsentFolder + "\\" + sDateFolder + "\\" + source_file.getName());
			File vms_file = null;
			if ("Y".equals(FtpData.IS_8X))
			{
				if ("SFONE".equals(Preference.mobileOperator))
				{
					vms_file = new File(VMS_FOLDER + "\\" + source_file.getName());
					System.out.println("Test moving>>SFONE");
				}
				else
				{
					vms_file = new File(VMS_FOLDER + "\\" + sDateVMS + "\\" + source_file.getName());
				}
			}
			else
			{
				vms_file = new File(VMS_FOLDER + "\\" + source_file.getName());
			}
			try
			{
				long time = System.currentTimeMillis();
				System.out.print(DateProc.getDateTime24hString(new Timestamp(time)));
				System.out.print("  moving file " + source_file + "...");
				fileTool.move(source_file, dest_file);
				fileTool.copy(dest_file, vms_file);
				// source_file.delete();
				// fileTool.move(source_file, dest_file);
				System.out.println(" OK!");
			}
			catch (IOException e)
			{
				System.out.println("CdrFileCopy4vms.move2Sendout error: " + e.getMessage());
				return false;
			}
		}
		return true;
	}
}
