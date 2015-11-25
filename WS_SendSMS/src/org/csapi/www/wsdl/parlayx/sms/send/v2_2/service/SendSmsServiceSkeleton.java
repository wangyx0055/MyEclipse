/**
 * SendSmsServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package org.csapi.www.wsdl.parlayx.sms.send.v2_2.service;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.GetSmsDeliveryStatusE;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.GetSmsDeliveryStatusResponseE;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.SendSmsE;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.SendSmsLogoE;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.SendSmsLogoResponseE;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.SendSmsResponse;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.SendSmsResponseE;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.SendSmsRingtoneE;
import org.csapi.www.schema.parlayx.sms.send.v2_2.local.SendSmsRingtoneResponseE;

/**
 * SendSmsServiceSkeleton java skeleton for the axisService
 */
public class SendSmsServiceSkeleton
{

	public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
			"http://www.huawei.com.cn/schema/common/v2_1", "RequestSOAPHeader",
			"ns1");
	/**
	 * Auto generated method signature
	 * 
	 * @param sendSmsLogo
	 * @return sendSmsLogoResponse
	 * @throws PolicyException
	 * @throws ServiceException
	 */

	public SendSmsLogoResponseE sendSmsLogo(SendSmsLogoE sendSmsLogo) throws PolicyException, ServiceException
	{
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName()
				+ "#sendSmsLogo");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param sendSmsRingtone
	 * @return sendSmsRingtoneResponse
	 * @throws PolicyException
	 * @throws ServiceException
	 */

	public SendSmsRingtoneResponseE sendSmsRingtone(SendSmsRingtoneE sendSmsRingtone) throws PolicyException,
			ServiceException
	{
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName()
				+ "#sendSmsRingtone");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param getSmsDeliveryStatus
	 * @return getSmsDeliveryStatusResponse
	 * @throws PolicyException
	 * @throws ServiceException
	 */

	public GetSmsDeliveryStatusResponseE getSmsDeliveryStatus(GetSmsDeliveryStatusE getSmsDeliveryStatus)
			throws PolicyException, ServiceException
	{
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName()
				+ "#getSmsDeliveryStatus");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param sendSms
	 * @return sendSmsResponse
	 * @throws PolicyException
	 * @throws ServiceException
	 */

	public SendSmsResponseE sendSms(SendSmsE sendSms) throws PolicyException, ServiceException
	{
		org.apache.axiom.soap.SOAPHeader header = org.apache.axis2.context.MessageContext
				.getCurrentMessageContext().getEnvelope().getHeader();

		OMElement headerElem = null;
		String Info_Header = "";
		String LocalName = "";
		String Info_Child = "";
		String Error = "";
		try
		{
			headerElem = header.getFirstChildWithName(MY_QNAME);
			Info_Header = headerElem.toString();
			LocalName = headerElem.getLocalName();

			for (Iterator<OMElement> List_item = headerElem
					.getChildElements(); List_item.hasNext();)
			{
				OMElement item = List_item.next();
				String Child_LocalName = item.getLocalName();

				String Child_URI = item.getNamespaceURI();
				String Child_NameSpace_Prefix = item.getNamespace()
						.getPrefix();
				String Child_NameSpace_Name = item.getNamespace().getName();
				String Child_Value = item.getAttributeValue(new QName(
						Child_URI, Child_LocalName));
				String Child_Value_Text = item.getText();

				OMElement First_Item = item.getFirstElement();

				String First_Item_Value_Text = item.getText();

				Info_Child += "||Child_LocalName->" + Child_LocalName
						+ "|Child_URI->" + Child_URI + "|Child_Value->"
						+ Child_Value + "|Child_Value_Text->"
						+ Child_Value_Text + "|First_Item_Value_Text->"
						+ First_Item_Value_Text
						+ "|Child_NameSpace_Prefix->"
						+ Child_NameSpace_Prefix
						+ "|Child_NameSpace_Name->" + Child_NameSpace_Name;
			}

		}
		catch (Exception ex)
		{
			Error += "Loi headerElem:" + ex.getMessage();
		}

		SendSmsResponse mRes = new SendSmsResponse();
		mRes.setResult("100001200301130605033504614941");
		SendSmsResponseE mResE = new SendSmsResponseE();
		mResE.setSendSmsResponse(mRes);
		return mResE;
	}

}
