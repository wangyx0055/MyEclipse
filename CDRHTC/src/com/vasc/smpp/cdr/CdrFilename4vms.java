package com.vasc.smpp.cdr;

import java.io.*;
import java.sql.Timestamp;
import com.vasc.common.DateProc;
import com.vasc.common.FileTool;

// filename like this: VASCyyyymmdd_nnnn.bin (21 chars)
// yyyymmdd   = current day
// where nnnn = 0000 --> 9999
public class CdrFilename4vms
{
	static String FILE_STORE_LAST_CDR_FILENAME = "./lastcdrfileCDR.dat";
	static String FILE_STORE_LAST_CDR_FILENAME_VMS = "./lastcdrfileCDR.dat";
	static FileTool fileTool = null;

	public CdrFilename4vms()
	{
		fileTool = new FileTool();
	}

	public synchronized static String getCurrentFilename()
	{
		try
		{
			byte[] buffer = fileTool.readFile(FILE_STORE_LAST_CDR_FILENAME);
			String filename = new String(buffer);
			return filename;
		}
		catch (IOException e)
		{
			System.out.println("CdrFilename4vms: " + e.getMessage());
			return null;
		}
	}

	// maivq them ngay 15/09/2006 for test VMS
	public synchronized static String getCurrentFilenameVMS()
	{
		try
		{
			byte[] buffer = fileTool.readFile(FILE_STORE_LAST_CDR_FILENAME_VMS);
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

	// END.

	public synchronized static String getCurrentFilenameSFONE()
	{
		try
		{
			byte[] buffer = fileTool.readFile(FILE_STORE_LAST_CDR_FILENAME);
			String filename = new String(buffer);
			return filename;
		}
		catch (IOException e)
		{
			System.out.println("CdrFilename4SFONE: " + e.getMessage());
			return null;
		}
	}

	public synchronized static String getNewFilename()
	{
		String curr_filename = getCurrentFilename();
		String new_filename = null;
		String new_nnnn = null;

		// Build the new sequence number (nnnn)
		if (curr_filename == null || // not found
				curr_filename.length() != 21 || !curr_filename.startsWith("VASC") || !curr_filename.endsWith(".bin"))
		{
			// Set to start
			new_nnnn = "0001";
		}

		else
		{
			String curr_date = curr_filename.substring(4, 12);
			int curr_nnnn = Integer.parseInt(curr_filename.substring(13, 17));
			int next_nnnn = 0;
			if (curr_nnnn == 9999)
			{
				next_nnnn = 1;
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
		// Now creates new file name
		Timestamp ts = DateProc.createTimestamp();
		new_filename = "VASC" + DateProc.Timestamp2YYYYMMDD(ts) + "_" + new_nnnn + ".bin";

		return new_filename;
	}

	public synchronized static String getNewFilenameFTPforVMS()
	{
		String curr_filename = getCurrentFilenameVMS();
		Logger.info("Noi dung file dat:", curr_filename);
		System.out.print("Noi Dung File .Dat:"+curr_filename);
		String new_filename = null;
		String new_nnnn = null;
	
		
		if (curr_filename == null || !curr_filename.startsWith("cp6x83_") || !curr_filename.endsWith(".cdr"))
		{
			
			// Set to start
			new_nnnn = "0000";
			Logger.info("file .DAT khong hop le:", curr_filename);
			System.out.print("file .DAT khong hop le:"+curr_filename);
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
		
		Logger.info("new_nnnn:", new_nnnn);
		System.out.print("new_nnnn:"+new_nnnn);
		// Now creates new file name
		Timestamp ts = DateProc.createTimestamp();
		new_filename = "cp6x83_" + DateProc.Timestamp2YYYYMMDD(ts) + "_" + new_nnnn + ".cdr";

		return new_filename;
	}

	public synchronized static String getNewFilenameEVN()
	{
		String curr_filename = getCurrentFilename();
		String new_filename = null;
		String new_nnnn = null;
		Timestamp ts = DateProc.createTimestamp();
		String today = (DateProc.getYYYYMMDDHHMMString(ts)).substring(0, 10);
		// System.out.println("today:"+today);
		// Build the new sequence number (nnnn)
		if (curr_filename == null || // not found
				curr_filename.length() != 25 || !curr_filename.startsWith("VASC") || !curr_filename.endsWith(".bil"))
		{
			// Set to start
			new_nnnn = "0001";
		}
		else
		{
			String curr_date = curr_filename.substring(4, 14);
			// System.out.println("current date:"+curr_date);
			if (!curr_date.endsWith(today))
			{
				new_nnnn = "0001";
			}
			else
			{
				int curr_nnnn = Integer.parseInt(curr_filename.substring(17, 21));
				// System.out.println("current number"+curr_filename.substring(17,
				// 21));
				int next_nnnn = 0;
				if (curr_nnnn == 9999)
				{
					next_nnnn = 1;
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
		}
		// Now creates new file name
		// Timestamp ts = DateProc.createTimestamp();
		new_filename = "VASC" + DateProc.getYYYYMMDDHHMMString(ts) + "_" + new_nnnn + ".bil";

		return new_filename;
	}

	public synchronized static String getNewFilename8x99()
	{
		String curr_filename = getCurrentFilename();
		String new_filename = null;
		String new_nnnn = null;

		// Build the new sequence number (nnnn)
		if (curr_filename == null || // not found
				curr_filename.length() != 22 || !curr_filename.startsWith("VASC_") || !curr_filename.endsWith(".cdr"))
		{
			// Set to start
			new_nnnn = "0001";
		}
		else
		{
			String curr_date = curr_filename.substring(5, 13);
			int curr_nnnn = Integer.parseInt(curr_filename.substring(14, 18));
			int next_nnnn = 0;
			if (curr_nnnn == 9999)
			{
				next_nnnn = 1;
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
		// Now creates new file name
		Timestamp ts = DateProc.createTimestamp();
		new_filename = "VASC_" + DateProc.Timestamp2YYYYMMDD(ts) + "_" + new_nnnn + ".cdr";

		return new_filename;
	}

	public synchronized static String getNewFilename8x99SFONE()
	{
		String curr_filename = getCurrentFilenameSFONE();
		String new_filename = null;
		String new_nnnn = null;

		// Now creates new file name
		Timestamp ts = DateProc.createTimestamp();
		new_filename = "VASC_" + DateProc.getYYYYMMDDHHMMString(ts) + ".bil";

		return new_filename;
	}

	public synchronized static String getNewFilenameSFONE()
	{

		String new_filename = null;

		Timestamp ts = DateProc.createTimestamp();
		new_filename = "VASC_" + DateProc.getYYYYMMDDHHMMString(ts) + ".bil";

		return new_filename;
	}

	public synchronized static String getNewFilenameHTC()
	{
		String curr_filename = getCurrentFilename();
		String new_filename = null;
		String new_nnnn = null;
		Timestamp ts = DateProc.createTimestamp();
		String today = DateProc.Timestamp2YYYYMMDD(ts);
		String nowHours = (DateProc.Timestamp2HHMM(ts)).substring(0, 2);
		// Build the new sequence number (nnnn)
		if (curr_filename == null || 
				curr_filename == "" ||
				!curr_filename.startsWith("6x83_hbcom_") ||
				!curr_filename.endsWith(".txt"))
		{
			// Set to start
			new_nnnn = "0001";

		}
		else
		{			
			//6x83_hbcom_20121210_14_0001.txt
			//String curr_date = curr_filename.substring(10, 18);
			String curr_time = curr_filename.substring(20, 22);
			int curr_nnnn = Integer.parseInt(curr_filename.substring(23, 27));
			int next_nnnn = 0;
			if (curr_time.equals(nowHours))
			{
				if (curr_nnnn == 9999)
				{
					next_nnnn = 1;
				}
				else
				{
					next_nnnn = curr_nnnn + 1;
				}
			}
			else
			{
				next_nnnn = 1;
				// Convert n to string "nnnn"
			}
			new_nnnn = String.valueOf(next_nnnn);
			int leftZero = 4 - new_nnnn.length();
			for (int i = 0; i < leftZero; i++)
			{
				new_nnnn = "0" + new_nnnn;
			}
		}

		// Now creates new file name
		// Timestamp ts = DateProc.createTimestamp();
		// new_filename = "VASC_" +today+"_"+nowHours+"_"+new_nnnn+".txt";//
		// Maivq edit 31/08/2006
		new_filename = "6x83_hbcom_" + today + "_" + nowHours + "_" + new_nnnn + ".txt";
		return new_filename;
	}

	public synchronized static void setNewFilename(String name) throws IOException
	{
		DataOutputStream fout = new DataOutputStream(new FileOutputStream(FILE_STORE_LAST_CDR_FILENAME, false)); // append
																													// =
																													// false
		fout.writeBytes(name);
		fout.flush();
		fout.close();
	}

	// Maivq them ngay 15/09/2006 for test VMS
	public synchronized static void setNewFilenameVMS(String name) throws IOException
	{
		DataOutputStream fout = new DataOutputStream(new FileOutputStream(FILE_STORE_LAST_CDR_FILENAME_VMS, false)); // append
																														// =
																														// false
		fout.writeBytes(name);
		fout.flush();
		fout.close();
	}

	// END

	public synchronized static void setNewFilenameSFONE(String name) throws IOException
	{
		DataOutputStream fout = new DataOutputStream(new FileOutputStream(FILE_STORE_LAST_CDR_FILENAME, false)); // append
																													// =
																													// false
		fout.writeBytes(name);
		fout.flush();
		fout.close();
	}

	public static void main(String args[])
	{
		CdrFilename4vms cdrFname = new CdrFilename4vms();
		System.out.println("Curr: " + cdrFname.getCurrentFilename());
		System.out.println("New: " + cdrFname.getNewFilename());
		try
		{
			cdrFname.setNewFilename(cdrFname.getNewFilename());
		}
		catch (IOException ex)
		{
		}
	}
}

class CdrFilenameException extends Exception
{
	String message = "";

	public String getMessage()
	{
		return this.message;
	}

	public CdrFilenameException()
	{
		message = "";
	}

	public CdrFilenameException(String message)
	{
		this.message = message;
	}
}
