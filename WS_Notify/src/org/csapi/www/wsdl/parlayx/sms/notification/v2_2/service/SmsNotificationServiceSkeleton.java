/**
 * SmsNotificationServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package org.csapi.www.wsdl.parlayx.sms.notification.v2_2.service;

import my.config.MySetting;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.databinding.ADBException;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.codec.binary.Hex;

import org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE;
import org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponse;
import org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE;
import org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE;
import org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponse;
import org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE;

import cn.com.huawei.www.schema.common.v2_1.NotifySOAPHeaderE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.mail.search.ReceivedDateTerm;

import MyUtility.*;
import MyGateway.*;

/**
 * SmsNotificationServiceSkeleton java skeleton for the axisService
 */
public class SmsNotificationServiceSkeleton
{

	MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), this.getClass().toString());

	/**
	 * Auto generated method signature
	 * 
	 * @param notifySmsDeliveryReceipt
	 * @throws IOException
	 */
	public NotifySmsDeliveryReceiptResponseE notifySmsDeliveryReceipt(NotifySmsDeliveryReceiptE notifySmsDeliveryReceipt)
			throws IOException
	{
		String address = "";
		String deliveryStatus = "";
		String correlator = "";

		try
		{
			System.out.println(notifySmsDeliveryReceipt.getOMElement(NotifySmsDeliveryReceiptE.MY_QNAME,
					OMAbstractFactory.getSOAP11Factory()));

			correlator = notifySmsDeliveryReceipt.getNotifySmsDeliveryReceipt().getCorrelator().toString();
			deliveryStatus = notifySmsDeliveryReceipt.getNotifySmsDeliveryReceipt().getDeliveryStatus()
					.getDeliveryStatus().toString();
			address = notifySmsDeliveryReceipt.getNotifySmsDeliveryReceipt().getDeliveryStatus().getAddress()
					.toString();

			FileWriter fstream = new FileWriter("D:\\log_SDP_SmsNotificationService.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("address: " + address + "\n");
			out.write("deliveryStatus: " + deliveryStatus + "\n");
			out.write("correlator: " + correlator + "\n");
			out.close();

			// saveCDR(notifySmsDeliveryReceipt.getOMElement(
			// NotifySmsDeliveryReceiptE.MY_QNAME,
			// OMAbstractFactory.getSOAP11Factory()).toString());

		}

		catch (ADBException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NotifySmsDeliveryReceiptResponseE responseE = new NotifySmsDeliveryReceiptResponseE();
		NotifySmsDeliveryReceiptResponse response = new NotifySmsDeliveryReceiptResponse();
		responseE.setNotifySmsDeliveryReceiptResponse(response);
		return responseE;
	}

	public static String decodeMO(String hexString) throws Exception
	{
		String strReturn = "";

		String strEncodec = hexString.substring(0, 2); // 08 - unicode
		String strInfo = hexString.substring(2, hexString.length());
		byte[] bytes = Hex.decodeHex(strInfo.toCharArray());
		if (strEncodec.equals("00"))
		{
			strReturn = new String(bytes, "ASCII");
			System.out.println(strReturn);

		}
		else if (strEncodec.equals("08"))
		{
			strReturn = new String(bytes, "UnicodeBigUnmarked");
			System.out.println(strReturn);
		}
		return strReturn;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param notifySmsReception
	 * @throws Exception
	 */
	public NotifySmsReceptionResponseE notifySmsReception(NotifySmsReceptionE notifySmsReception) throws Exception
	{
		String Resul = "Khong Insert DB";
		// Thong tin trong header
		String spRevId = "";
		String spRevpassword = "";
		String spId = "";
		String serviceId = "";
		String timeStamp = "";
		String traceUniqueID = "";

		// Thong tin trong body
		String message = "";
		String senderAddress = "";
		String smsServiceActivationNumber = "";
		String dateTime = "";
		String LogInfo = "";
		try
		{
			sms_receive_queue mQueue = new sms_receive_queue(MySetting.GetProxoolConfigPath(),
					MySetting.PoolName_GateWay);

			System.out.println(notifySmsReception.getOMElement(NotifySmsReceptionE.MY_QNAME,
					OMAbstractFactory.getSOAP11Factory()));

			org.apache.axiom.soap.SOAPHeader header = org.apache.axis2.context.MessageContext
					.getCurrentMessageContext().getEnvelope().getHeader();
			OMElement headElement = header.getFirstChildWithName(NotifySOAPHeaderE.MY_QNAME);

			message = notifySmsReception.getNotifySmsReception().getMessage().getMessage();
			senderAddress = notifySmsReception.getNotifySmsReception().getMessage().getSenderAddress().toString();
			smsServiceActivationNumber = notifySmsReception.getNotifySmsReception().getMessage()
					.getSmsServiceActivationNumber().toString();

			dateTime = MyConfig.Get_DateFormat_InsertDB().format(
					notifySmsReception.getNotifySmsReception().getMessage().getDateTime().getTime());

			for (Iterator<OMElement> List_item = headElement.getChildElements(); List_item.hasNext();)
			{
				OMElement item = List_item.next();
				String Child_LocalName = item.getLocalName();

				if (Child_LocalName.equalsIgnoreCase("spRevId"))
				{
					spRevId = item.getText();
				}
				else if (Child_LocalName.equalsIgnoreCase("spRevpassword"))
				{
					spRevpassword = item.getText();
				}
				else if (Child_LocalName.equalsIgnoreCase("spId"))
				{
					spId = item.getText();
				}
				else if (Child_LocalName.equalsIgnoreCase("serviceId"))
				{
					serviceId = item.getText();
				}
				else if (Child_LocalName.equalsIgnoreCase("timeStamp"))
				{
					timeStamp = item.getText();
				}
				else if (Child_LocalName.equalsIgnoreCase("traceUniqueID"))
				{
					traceUniqueID = item.getText();

				}
			}

			Date Recieve_Date = null;
			if (!timeStamp.equalsIgnoreCase(""))
			{
				// yyyymmddhhmmss
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
				Recieve_Date = dateFormat.parse(timeStamp);
			}
			String USER_ID = senderAddress.replace("tel:", "").trim();
			String SERVICE_ID = smsServiceActivationNumber.replace("tel:", "").trim();
			String MOBILE_OPERATOR = "VMS";
			String COMMAND_CODE = "INV";
			String INFO = decodeMO(message);
			DateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSSS");
			java.util.Date date = new java.util.Date();
			String datetime = dateFormat.format(date);
			String REQUEST_ID = USER_ID.substring(USER_ID.length() - 6, USER_ID.length() - 2) + datetime;
			String receive_date = dateTime;
			String VMS_SVID = serviceId;

			if (Recieve_Date != null)
			{
				dateTime = MyConfig.Get_DateFormat_InsertDB().format(Recieve_Date);
				receive_date = dateTime;
			}
			receive_date = dateTime;

			if (mQueue.Insert_VMS(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, REQUEST_ID, receive_date,
					VMS_SVID))
			{
				Resul = "Insert DB is OK";
			}
			else
			{
				Resul = "Insert DB is FAIL";
			}

			String ConfigPath = MySetting.GetLog4JConfigPath();

			LogInfo = "RECEIVE MO--->spRevId:" + spRevId + "|spRevpassword:" + spRevpassword + "|spId:" + spId
					+ "|serviceId:" + serviceId + "|timeStamp:" + timeStamp + "|traceUniqueID:" + traceUniqueID
					+ "|message:" + message + "|INFO:" + INFO + "|senderAddress:" + senderAddress
					+ "|smsServiceActivationNumber:" + smsServiceActivationNumber + "|dateTime:" + dateTime
					+ "|REQUEST_ID:" + REQUEST_ID + "|ConfigPath:" + ConfigPath;

		}
		catch (Exception e)
		{
			mLog.log.error(e);
		}
		finally
		{
			mLog.log.info(LogInfo);
		}

		NotifySmsReceptionResponseE responseE = new NotifySmsReceptionResponseE();
		NotifySmsReceptionResponse respSonse = new NotifySmsReceptionResponse();

		responseE.setNotifySmsReceptionResponse(respSonse);
		return responseE;

	}

	/*
	 * private SendSmsE createBody(NotifySmsReceptionE notifySmsReception) { try
	 * { URI address = new URI("tel:84938112129"); //
	 * notifySmsReception.getNotifySmsReception
	 * ().getMessage().getSenderAddress(); URI endpoint = new
	 * URI("http://10.54.21.18:9091/notify"); SimpleReference sim = new
	 * SimpleReference(); sim.setCorrelator("123"); sim.setEndpoint(endpoint);
	 * sim.setInterfaceName("SmsNotification"); SendSms param = new SendSms();
	 * 
	 * param.addAddresses(address); // param.setData_coding(0); //
	 * param.setDestinationport(0); // param.setEncode("utf-8"); //
	 * param.setEsm_class(1); param.setMessage("test SMS Notification");
	 * param.setReceiptRequest(sim); param.setSenderName("9284"); //
	 * param.setSourceport(123); SendSmsE sendSms = new SendSmsE();
	 * sendSms.setSendSms(param); return sendSms; } catch (MalformedURIException
	 * e) { return null; } }
	 */

	private void saveCDR(String message)
	{
		// String logPath = System.getProperty("com.violet.config");
		// File file = new File(logPath + System.getProperty("file.separator") +
		// "cdr.txt ");
		File file = new File("D:\\cdr.txt");
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		try
		{
			FileWriter writer = new FileWriter(file, true);
			writer.append(message);
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
