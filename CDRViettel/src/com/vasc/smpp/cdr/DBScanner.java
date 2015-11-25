package com.vasc.smpp.cdr;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Collection;
import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import com.vasc.common.DateProc;
import com.vasc.common.FileTool;
import com.vasc.common.util.Queue;
import com.vasc.smpp.gateway.*;

public class DBScanner extends Thread
{
	private int maxcols = 2;
	private int col = 0;
	private DBTools dbTools = null;

	public DBScanner()
	{
		this.dbTools = new DBTools();
	}

	public void run()
	{
		int numOfCdrs = 0;
		// System.out.println("Getting data from queue table");
		Logger.info("CDRServer:", "Getting data from queue table");
		while (CDRServer.running)
		{
			try
			{
				
				this.readCDRinQueueEx();
				if ("VIETTEL".equals(Preference.mobileOperator))
				{
					CdrFileCopier4vms cdrcopy = new CdrFileCopier4vms();
					// System.out.println("Starting FTP");
					Logger.info("FTP:", " starting FTP cdr file");
					
					//Tạo file cdr truoc					
					Vector v = FileTool.getAllFiles(new File(Preference.cdroutFolder), ".bil");
					if (v.size() <=0)					
					{
						//Neu chua co file thi tao 1 file rong
						String fileCDR= "";
						
						Timestamp ts = DateProc.createTimestamp();
						
						fileCDR = "hb" + DateProc.getYYYYMMDDHHMMSSString(ts) + ".bil";

						fileCDR=Preference.cdroutFolder + "/" +fileCDR;
						
						try
						{
							CdrFilename4vms.setNewFilename(fileCDR);
							java.io.File mFile = new File(fileCDR);
							if(!mFile.exists())
							{
								mFile.createNewFile();
							}
							
						}
						catch (IOException ex1)
						{
							System.out.println("tao file CDR bi loi:" + ex1.getMessage());
						}

					}
					
					
					Ftp2CdrServer ftp = new Ftp2CdrServer(); // .start();
					ftp.runftp();
				}
				else
				{
					// System.out.println("Invalid mobile operator: " +
					// Preference.mobileOperator);
					Logger.info("Invalid mobile operator:", Preference.mobileOperator);
					exit();
				}
				sleep10Minutes();

			}
			catch (InterruptedException ex)
			{
			}
			catch (DBException ex)
			{ // when lost connection to db
				System.out.println("DBScanner::" + ex.getMessage());
				try
				{
					dbTools.log_alert("Billing system", "-> ERROR: Ket noi Database bi loi: " + ex.getMessage(), 1, 0, "serious", Preference.alert_person);
				}
				catch (Exception e)
				{
				}
				GatewayCDR.rebuildDBConnections(1); // 1 connection
			}
			catch (Exception ex)
			{
				// System.out.println("DBScanner::" + ex.getMessage());
				Logger.info("DBScanner:", ex.getMessage());
				try
				{
					// dbTools.log_alert(Preference.sourceAddressList.toString(),
					// "CDR->DBSCanner",
					// "<-" + Preference.mobileOperator + "-> ERROR: " +
					// ex.getMessage(),
					// 1, Preference.alert_person,
					// Preference.alert_mobile);
					dbTools.log_alert("Billing system", "-> ERROR: Loi dinh dang MOBILE OPERATOR: " + ex.getMessage(), 1, 0, "serious", Preference.alert_person);
				}
				catch (Exception e)
				{
				}
			}
		}
	}

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

	private static void exit()
	{
		CDRServer.running = false;
		GatewayCDR.closeAllConnectionInPool();
		System.out.println("Stop.");
		System.exit(0);
	}

	/**
	 * Read all CDRs of a mobile operator (GPC or VMS - uppercase)
	 * 
	 * @return number of messages sent
	 */
	public int readCDRinQueue() throws DBException
	{
		int numOfCdrs = 0;

		Collection collection = dbTools.getAllCDRsInQueue(Preference.mobileOperator);
		if ((collection != null) && (collection.size() > 0))
		{
			numOfCdrs = collection.size();
			BigDecimal cdrId = null;
			for (Iterator it = collection.iterator(); it.hasNext();)
			{
				cdrId = (BigDecimal) it.next();
				CDR cdr = dbTools.getCDRinQueue(cdrId);
				if (cdr == null)
				{ // removed by other processes
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
					continue;
				}
				// //////////////////////////////////
				cdr = dbTools.moveCDRFromQueueToLog(cdrId);
				if (cdr != null)
				{
					CDRTool.add2CDRFile(cdr);
					System.out.print(cdr.getUserId() + "-->" + cdr.getServiceId() + ":" + cdr.getCommandCode() + "\t\t");

					if (col >= maxcols - 1)
					{
						System.out.println();
						col = 0;
					}
					else
					{
						col++;
					}
				}
				else
				{
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
				}

			} // end for
		}
		return numOfCdrs;
	}

	/**
	 * Read all CDRs of a mobile operator (GPC or VMS - uppercase)
	 * 
	 * @return number of messages sent
	 */
	public int readCDRinQueueALL() throws DBException
	{
		int numOfCdrs = 0;

		Collection collection = dbTools.getAllCDRsInQueueALL();
		// System.out.println("truoc khi chay collection"+collection.size());
		if ((collection != null) && (collection.size() > 0))
		{
			numOfCdrs = collection.size();
			CDR cdr = null;
			BigDecimal cdrId = null;
			// System.out.println("truoc khi chay Iterator");
			for (Iterator it = collection.iterator(); it.hasNext();)
			{
				cdr = (CDR) it.next();
				cdrId = cdr.getId();
				// System.out.println("cdrId"+cdrId);
				if (cdr == null)
				{ // removed by other processes
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
					continue;
				}
				// //////////////////////////////////
				cdr = dbTools.moveCDRFromQueueToLogEx(cdr);
				if (cdr != null)
				{
					CDRTool.add2CDRFileEx(cdr);
					System.out.print(cdr.getUserId() + "-->" + cdr.getServiceId() + ":" + cdr.getCommandCode() + "\t\t");

					if (col >= maxcols - 1)
					{
						System.out.println();
						col = 0;
					}
					else
					{
						col++;
					}
				}
				else
				{
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
				}
				// //////////////////////////////////
			} // end for
		}
		return numOfCdrs;
	}

	public int readCDRinQueueEx() throws DBException
	{
		int numOfCdrs = 0;

		Collection collection = dbTools.getAllCDRsInQueueEx();
		// System.out.println("truoc khi chay collection"+collection.size());
		if ((collection != null) && (collection.size() > 0))
		{
			numOfCdrs = collection.size();
			CDR cdr = null;
			BigDecimal cdrId = null;
			//System.out.println("truoc khi chay Iterator");
			for (Iterator it = collection.iterator(); it.hasNext();)
			{
				cdr = (CDR) it.next();
				cdrId = cdr.getId();
				// System.out.println("cdrId"+cdrId);
				if (cdr == null)
				{ // removed by other processes
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
					continue;
				}
				// Chuyển cdr và cdr_log và xóa trong table cdr_queue
				cdr = dbTools.moveCDRFromQueueToLogEx(cdr);
				if (cdr != null)
				{
					CDRTool.add2CDRFileEx(cdr);
					System.out.print(cdr.getUserId() + "-->" + cdr.getServiceId() + ":" + cdr.getCommandCode() + "\t\t");

					if (col >= maxcols - 1)
					{
						System.out.println();
						col = 0;
					}
					else
					{
						col++;
					}
				}
				else
				{
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
				}
				// //////////////////////////////////
			} // end for
		}
		return numOfCdrs;
	}

	public int readCDRinQueueEx8x99() throws DBException
	{
		int numOfCdrs = 0;
		Collection collection = dbTools.getAllCDRsInQueueEx8x99();
		if ((collection != null) && (collection.size() > 0))
		{
			numOfCdrs = collection.size();
			CDR cdr = null;
			BigDecimal cdrId = null;
			for (Iterator it = collection.iterator(); it.hasNext();)
			{
				cdr = (CDR) it.next();
				cdrId = cdr.getId();
				if (cdr == null)
				{ // removed by other processes
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
					continue;
				}
				// //////////////////////////////////
				cdr = dbTools.moveCDRFromQueueToLogEx(cdr);
				if (cdr != null)
				{
					CDRTool.add2CDRFile8x99(cdr);
					System.out.print(cdr.getUserId() + "-->" + cdr.getServiceId() + ":" + cdr.getCommandCode() + "\t\t");

					if (col >= maxcols - 1)
					{
						System.out.println();
						col = 0;
					}
					else
					{
						col++;
					}
				}
				else
				{
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
				}
				// //////////////////////////////////
			} // end for
		}
		return numOfCdrs;
	}

	/**
	* Them phat day ra file
	 */
	public static int pushCDRinQueueEx8x99(String fromNum, String toNum) throws DBException
	{
		int numOfCdrs = 0;
		int maxcols = 2;
		int col = 0;
		DBTools dbTools = new DBTools();
		Collection collection = dbTools.getAllCDRsInLogEx8x99(fromNum, toNum);
		if ((collection != null) && (collection.size() > 0))
		{
			numOfCdrs = collection.size();
			CDR cdr = null;
			BigDecimal cdrId = null;
			for (Iterator it = collection.iterator(); it.hasNext();)
			{
				cdr = (CDR) it.next();
				cdrId = cdr.getId();
				if (cdr == null)
				{ // removed by other processes
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
					continue;
				}
				// //////////////////////////////////
				// cdr = dbTools.moveCDRFromQueueToLogEx(cdr);
				if (cdr != null)
				{
					CDRTool.add2CDRFile8x99ForPushVMS(cdr);
					System.out.print(cdr.getUserId() + "-->" + cdr.getServiceId() + ":" + cdr.getCommandCode() + "\t\t");

					if (col >= maxcols - 1)
					{
						System.out.println();
						col = 0;
					}
					else
					{
						col++;
					}
				}
				else
				{
					// System.out.println("CDR is null (queueId=" + cdrId +
					// ")");
					Logger.info("CDR:", "CDR is null (queueId=" + cdrId + ")");
				}
				// //////////////////////////////////
			} // end for
		}
		return numOfCdrs;
	}

	public static void main(String[] args)
	{
		try
		{
			GatewayCDR gateway = new GatewayCDR();
			try
			{
				Preference.loadProperties("gateway.cfg");
				// if ("GPC".equals(Preference.mobileOperator) ||
				// "VIETEL".equals(Preference.mobileOperator))
				FtpData.loadProperties("ftp2cdrserver.cfg");
			}
			catch (Exception e)
			{
				// System.out.println("CDRServer: khong tim thay file cau hinh ");
				Logger.info("CDRServer:", "khong tim thay file cau hinh");
			}
			gateway.addMoreConnection2Pool(1);

			if (args != null && args.length > 1)
			{
				System.out.println(">>>" + args[0] + "  >>>  " + args[1]);
				DBScanner.pushCDRinQueueEx8x99(args[0].trim(), args[1].trim());
			}
			else
			{
				System.out.println(">>>Thang 04 !!!");
				DBScanner.pushCDRinQueueEx8x99("060401000000", "060425093500");
			}
		}
		catch (DBException ex)
		{
			// System.out.println(">>>>Loi: " + ex.toString());
			Logger.info("CDRServer:", ex.toString());
		}
	}
}
