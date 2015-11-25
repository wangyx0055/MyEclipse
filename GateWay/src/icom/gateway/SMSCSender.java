package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */
import icom.common.Queue;
import icom.common.Utilities;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.smpp.*;
import org.smpp.pdu.*;

public class SMSCSender extends Thread
{

	private Queue toSMSC = null;
	private Gateway gateway = null;
	private PDU pdu = null;

	public SMSCSender(Queue toSMSC, Gateway gateway)
	{
		this.toSMSC = toSMSC;
		this.gateway = gateway;
	}

	public void run()
	{
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////
		while (Gateway.running)
		{
			if (Gateway.bound)
			{
				try
				{
					pdu = (PDU) toSMSC.dequeue();
					if (pdu != null)
					{
						if (pdu.isResponse())
						{
							Gateway.session.respond((Response) pdu);
							Thread.sleep(50);
						}
						else if (pdu.isRequest())
						{
							this.sendRequest(pdu);

							int ThreadTPS = Preference.maxSMPerSecond / Preference.nTheardMT;
							int time2sleep = (1000 / ThreadTPS);

							Thread.sleep(time2sleep);
						}
					}
					else
					{
						Logger.error(this.getName(), "pdu is null");

						DBTools.ALERT("SMSCSender", "pdu is null", Constants.ALERT_WARN, Preference.Channel + "pdu is null", Preference.ALERT_CONTACT);
						Thread.sleep(50);
					}
				}
				catch (InterruptedException ex1)
				{
					Utilities.log(this.getClass().getName(), "InterruptedException:" + ex1.getMessage());
					DBTools.ALERT("SMSCSender", "InterruptedException", Constants.ALERT_WARN, Preference.Channel + "InterruptedException: " + ex1.getMessage(),
							Preference.ALERT_CONTACT);
				}
				catch (ValueNotSetException ex)
				{
					Utilities.log(this.getClass().getName(), "ValueNotSetException:" + ex.getMessage());

					DBTools.ALERT("SMSCSender", "ValueNotSetException", Constants.ALERT_MAJOR, Preference.Channel + "ValueNotSetException: " + ex.getMessage(),
							Preference.ALERT_CONTACT);

				}
				catch (IOException ex)
				{
					Utilities.log(this.getClass().getName(), "IOException:" + ex.getMessage());
					toSMSC.enqueue(pdu); // Connection error; back to queue.

					DBTools.ALERT("SMSCSender", "IOException", Constants.ALERT_SERIOUS, Preference.Channel + "IOException: " + ex.getMessage(),
							Preference.ALERT_CONTACT);

					// gateway.bound = false;
					// gateway.bind();
				}
				catch (Exception ex)
				{
					Utilities.log(this.getClass().getName(), "Exception:" + ex.getMessage());
					toSMSC.enqueue(pdu);

					DBTools.ALERT("SMSCSender", "Exception", Constants.ALERT_SERIOUS, Preference.Channel + "Exception: " + ex.getMessage(),
							Preference.ALERT_CONTACT);
					Logger.printStackTrace(ex);
				}
			}
			else
			{
				Logger.info(this.getClass().getName(), "Delay-sender");
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Logger.info(this.getClass().getName(), "Delay-sender");
			}

		}// while
			// ////////////////////
		Utilities.log(this.getClass().getName(), "{" + this.getClass().getName() + " stopped}");
		this.destroy();
		// ////////////////////
	}

	public void destroy()
	{
		Gateway.removeThread(this);
	}

	private void sendRequest(PDU pdu) throws IOException
	{
		if (pdu == null)
			return;
		if (pdu.getCommandId() == Data.SUBMIT_SM)
		{
			try
			{
				SubmitSM request = (SubmitSM) pdu;
				// Logger.println("<== " + request.debugString());
				String info = request.getShortMessage();
				if (request.getDataCoding() == 0)
				{
					info = request.getShortMessage();
					// if (Preference.logFullMessage == 0 && info.length() > 20)
					// {
					// info = info.substring(0, 20);
					// }
					// } else {
					/*
					 * if (Preference.logFullMessage == 1) { info =
					 * com.vmg.common.HexaTool.toHexString(request.
					 * getShortMessageData().getBuffer()); } else {
					 * 
					 * info = "*****"; }
					 */
					// info = "*****";
				}
				Utilities.log(this.getClass().getName(), "{toSMSC size= " + toSMSC.size() + "}{User_ID= " + request.getDestAddr().getAddress()
						+ "}{Service_ID=" + request.getSourceAddr().getAddress() + "}{Info = " + info + "}{seq id = "+request.getSequenceNumber()+"}");

				// logger.printPDU(request);

				if (Preference.asynchronous)
				{
					Gateway.session.submit(request);
				}
				else
				{
					SubmitSMResp response = Gateway.session.submit(request);
					// Logger.println("==> " + response.debugString());

					Preference.messageId = response.getMessageId();
					// Add response to queue to process
					gateway.getResponseQueue().enqueue(response);
				}
				
				Utilities.log(this.getClass().getName(), "Linhnc complete toSMSC info:"+ request.debugString()+"{seq id = "+request.getSequenceNumber()+"}");

			}
			catch (WrongSessionStateException ex)
			{
				Utilities.log(this.getClass().getName(), "WrongSessionStateException: " + ex.getMessage());

				DBTools.ALERT("SMSCSender", "WrongSessionStateException", Constants.ALERT_SERIOUS,
						Preference.Channel + "WrongSessionStateException: " + ex.getMessage(), Preference.ALERT_CONTACT);
			}
			catch (PDUException ex)
			{
				Utilities.log(this.getClass().getName(), "PDUException: " + ex.getMessage());

				DBTools.ALERT("SMSCSender", "PDUException", Constants.ALERT_SERIOUS, Preference.Channel + "PDUException: " + ex.getMessage(),
						Preference.ALERT_CONTACT);

			}
			catch (TimeoutException ex)
			{
				Utilities.log(this.getClass().getName(), "TimeoutException: " + ex.getMessage());

				DBTools.ALERT("SMSCSender", "TimeoutException", Constants.ALERT_SERIOUS, Preference.Channel + "TimeoutException: " + ex.getMessage() + "\n"
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Preference.ALERT_CONTACT);
			}
			catch (IOException ex)
			{
				DBTools.ALERT("SMSCSender", "IOException", Constants.ALERT_SERIOUS, Preference.Channel + "IOException: " + ex.getMessage(),
						Preference.ALERT_CONTACT);

				throw ex;
			}
			catch (Exception ex)
			{
				Utilities.log(this.getClass().getName(), "Exception1: " + ex.getMessage());

				DBTools.ALERT("SMSCSender", "Exception", Constants.ALERT_SERIOUS, Preference.Channel + "Exception: " + ex.getMessage(),
						Preference.ALERT_CONTACT);

			}

		}
	}
}
