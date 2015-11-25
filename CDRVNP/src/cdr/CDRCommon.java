package cdr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import dbcdr.CDRObject;
import MyUtility.MyLogger;

public class CDRCommon
{
	public static MyLogger mLog = new MyLogger(CDRCommon.class.toString());

	public synchronized static String GetNewFilenameFTPforVNP()
	{

		String new_filename = null;

		SimpleDateFormat DateFormat_Day = new SimpleDateFormat("yyyyMMdd");
		new_filename = LocalConfig.CP_NAME + "." + DateFormat_Day.format(Calendar.getInstance().getTime()) + LocalConfig.FILE_EXTENSION;

		return new_filename;
	}

	/**
	 * Ghi nội dung CDR xuống file trên thư mục Local
	 * 
	 * @param mObject
	 */
	public synchronized static void WriteCDRLocal(CDRObject mObject) throws Exception
	{
		String fileCDRftp = null;
		int i = -1;

		fileCDRftp = GetNewFilenameFTPforVNP();
		String PathFile = LocalConfig.LOCAL_FOLDER + "/" + fileCDRftp;
		File CheckFile = new File(PathFile);

		
		if (CheckFile.exists() && CheckFile.length() > 0)
		{
			i = 1; //đã có file hoặc dữ liệu. nên sẽ thêm ký tự xuống dòng
		}
		else
		{
			i = 0;
		}

		System.out.println("ten file cdr:" + fileCDRftp);

		try
		{
			java.io.DataOutputStream fout = new java.io.DataOutputStream(new FileOutputStream(LocalConfig.LOCAL_FOLDER + "/" + fileCDRftp, true));

			String sData = "";
			if (i == 0)
			{
				sData = mObject.GetData();
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
