package cdr;

import ftp.*;
import ftp.io.*;

import java.util.Calendar;
import java.util.Vector;
import java.io.File;
import java.io.IOException;

import MyUtility.*;

public class FTP2CDRServer extends Thread
{
	static MyLogger mLog = new MyLogger(FTP2CDRServer.class.toString());
	private Vector<File> vFiles = new Vector<File>(); // of File objects
	private Ftp ftp = null;
	private String foldername = "";

	public FTP2CDRServer()
	{

	}

	public void runftp()
	{
		if (CDRServer.IsRunning)
		{
			try
			{
				// System.out.println();
				this.vFiles = MyFile.GetAllFiles(new File(LocalConfig.LOCAL_FOLDER), LocalConfig.FILE_EXTENSION);
				if (vFiles.size() > 0)
				{
					this.makeFtp();

					String sDateVMS = MyConfig.DateFormat_yyyyMMdd.format(Calendar.getInstance().getTime());

					CoFile dir = new FtpFile(ftp.pwd(), ftp);
					CoFile fls[] = dir.listCoFiles();
					if (fls.length > 0)
					{
						for (int n = 0; n < fls.length; n++)
						{
							if (fls[n].getName() == sDateVMS)
							{
								foldername = sDateVMS;
								break;
							}
							else
							{
								this.ftp.mkdir(sDateVMS);
								foldername = sDateVMS;
							}
						}
					}
					else
					{
						this.ftp.mkdir(sDateVMS);
						foldername = sDateVMS;
					}

					this.send2FtpServer();
					this.ftp.disconnect();
				}
				else
				{
					mLog.log.info("No CDR file found, sleep in 1 min");

					sleepEx(1);
				}

			}
			catch (IOException e)
			{
				mLog.log.error(e);
			}

		}

	}

	public void run()
	{
		sleepEx(1);
		while (CDRServer.IsRunning)
		{
			try
			{
				System.out.println();
				this.vFiles = MyFile.GetAllFiles(new File(LocalConfig.LOCAL_FOLDER), LocalConfig.FILE_EXTENSION);
				if (vFiles.size() > 0)
				{
					this.makeFtp();
					this.send2FtpServer();
					this.ftp.disconnect();
					sleepByScheduleTime();
				}
				else
				{
					mLog.log.info("No CDR file found, sleep in 1 min");

					sleepEx(1);
				}
			}
			catch (IOException e)
			{
				mLog.log.error(e);

			}
			catch (InterruptedException e)
			{
				mLog.log.error(e);
			}
		}
	}

	private void sleepByScheduleTime() throws InterruptedException
	{
		/* Ngay sau 00h --> Tao 1 file cuoc moi */

		Calendar.getInstance();
		int currHour = Calendar.HOUR;
		if ((24 - currHour) * 60 < 2 * LocalConfig.SCHEDULE_TIME)
		{
			sleep((LocalConfig.SCHEDULE_TIME + 1) * 60000); // 00h:01
		}
		else
		{
			sleep(LocalConfig.SCHEDULE_TIME * 60000); // n * minutes;
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
		FtpConnect conn = FtpConnect.newConnect(LocalConfig.FTP_SERVER);
		conn.setUserName(LocalConfig.FTP_USER);
		conn.setPassWord(LocalConfig.FTP_PASSWORD);
		conn.setPathName(LocalConfig.FTP_PATH);

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

		boolean loadResult = false;
		boolean renameFileResult = false;
		for (int i = 0; i < vFiles.size(); i++)
		{
			filename = ((File) (vFiles.elementAt(i))).getName();

			file = new LocalFile(LocalConfig.LOCAL_FOLDER, filename);

			File f = new File(LocalConfig.LOCAL_FOLDER + "/" + filename);
			String fileLength = String.valueOf(f.length());

			to = new FtpFile(foldername + "/tmp_" + filename, this.ftp);

			loadResult = CoLoad.copy(to, file);
			mLog.log.info("FTP-SIZE:" + loadResult);

			renameFileResult = this.ftp.mv(foldername + "/tmp_" + filename, foldername + "/" + filename);
			mLog.log.info("FTP-RENAME: " + renameFileResult);
			if (loadResult && renameFileResult)
			{
				// ok -->delete file.
				try
				{
					String sDateFolder = MyConfig.DateFormat_yyyyMMdd.format(Calendar.getInstance().getTime());

					File dest_dir = new File(LocalConfig.SENT_FOLDER_VMS + "/" + sDateFolder);

					boolean exists = dest_dir.exists();
					if (!exists)
					{
						// Directory does not exist --> create it
						dest_dir.mkdirs();
					}

					MyFile.Copy(LocalConfig.LOCAL_FOLDER + "/" + file.getName(), LocalConfig.SENT_FOLDER_VMS + "/" + sDateFolder + "/" + file.getName());
					mLog.log.info("backup file: from: " + LocalConfig.LOCAL_FOLDER + "/" + file.getName() + " to: " + LocalConfig.SENT_FOLDER_VMS + "/"
							+ sDateFolder + "/" + file.getName());
					file.delete();
					LocalConfig.setNewFilenameVMS(filename);
					mLog.log.info("FTP : file name: " + filename + "# filesize: " + fileLength + " bytes");
				}
				catch (IOException ex)
				{
					mLog.log.error(ex);
				}
			}
			else
			{
				try
				{
					if ((loadResult = false) && (renameFileResult = true))
					{
						mLog.log.warn("Billing system -> ERROR: Khong ftp duoc file cuoc sang telcos! ");
					}
					else if ((loadResult = true) && (renameFileResult = false))
					{

						mLog.log.warn("Billing system -> ERROR: Khong rename duoc file cuoc sau khi ftp ! ");
					}
					else
					{
						mLog.log.warn("Billing system -> ERROR: Loi ftp file cuoc!");
					}

				}
				catch (Exception ex)
				{
					mLog.log.error(ex);
				}
			}
		}
		return this.vFiles.size();
	}

	public static void main(String[] arguments) throws Exception
	{
		FTP2CDRServer sender = new FTP2CDRServer();
		sender.start();
	}
}
