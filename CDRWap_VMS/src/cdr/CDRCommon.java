package cdr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import dbcdr.CDRObject;


import MyUtility.MyConfig;
import MyUtility.MyFile;
import MyUtility.MyLogger;


public class CDRCommon
{
	public static MyLogger mLog = new MyLogger(CDRCommon.class.toString());
	
	
	public synchronized static String GetCurrentFilenameVMS()
	{
		try
		{
			byte[] buffer = MyFile.ReadFile(LocalConfig.FILE_STORE_LAST_CDR_FILENAME);
			String filename = new String(buffer);
			if(filename != null && filename.length() > 0)
			{
				filename = filename.trim().replace(" ", "");
			}
			return filename;
		}
		catch (IOException e)
		{
			System.out.println("CdrFilename4vms: " + e.getMessage());
			return null;
		}
	}

	
	public synchronized static String GetNewFilenameFTPforVMS()
	{
		String curr_filename = GetCurrentFilenameVMS();
		mLog.log.info("Noi dung file dat:"+ curr_filename);
		
		String new_filename = null;
		String new_nnnn = null;
	
		
		if (curr_filename == null || !curr_filename.startsWith(LocalConfig.FTP_USER) || !curr_filename.endsWith(LocalConfig.FILE_EXTENSION))
		{
			
			// Set to start
			new_nnnn = "0000";
			mLog.log.info("file .DAT khong hop le:"+ curr_filename);
		}
		else
		{
			int index = curr_filename.lastIndexOf("_") + 1;
			int index2 = index + 4;
			int curr_nnnn = Integer.parseInt(curr_filename.substring(index, index2));
			int next_nnnn = 0;
			if (curr_nnnn == 9999)
			{
				next_nnnn = 0;

			}
			else
			{
				next_nnnn = curr_nnnn + 1;
			}
			// Convert n to string "nnnn"
			new_nnnn = String.valueOf(next_nnnn);
			int leftZero = 4 - new_nnnn.length();
			for (int i = 0; i < leftZero; i++)
			{
				new_nnnn = "0" + new_nnnn;
			}
			
		}
		
		mLog.log.info("new_nnnn:"+ new_nnnn);
		// Now creates new file name
		
		new_filename =LocalConfig.FTP_USER +"_" + MyConfig.DateFormat_yyyyMMdd.format(Calendar.getInstance().getTime()) + "_" + new_nnnn + LocalConfig.FILE_EXTENSION;

		return new_filename;
	}
	
	
	/**
	 * Ghi nội dung CDR xuống file trên thư mục Local
	 * @param mObject
	 */
	public synchronized static void WriteCDRLocal(CDRObject mObject) throws Exception
	{
		String fileCDRftp = null;
		int i = -1;
		
			Vector<File> vftp = MyFile.GetAllFiles(new File(LocalConfig.LOCAL_FOLDER), LocalConfig.FILE_EXTENSION);
			if (vftp.size() > 0)
			{
				fileCDRftp = ((File) vftp.elementAt(0)).getName();
				i = 1;
			}
			else
			{
				fileCDRftp = GetNewFilenameFTPforVMS();
				i = 0;
			}
			System.out.println("ten file cdr:"+fileCDRftp);
			
			try
			{
				java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(LocalConfig.LOCAL_FOLDER + "/" + fileCDRftp, true)); 

				String sData="";
				if (i == 0)
				{
					sData =mObject.GetData();
				}
				else if (i == 1)
				{
					sData = "\r\n" + mObject.GetData();
				}
				
				fout.writeBytes(sData);
				fout.flush();
				fout.close();
			}
			catch (IOException ex)
			{
				mLog.log.error(ex);
				throw ex;
			}
			
	}
}
