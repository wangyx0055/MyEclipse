/**
 * SendPushMessageServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.csapi.www.schema.parlayx.wap_push.send.v1_0.local.GetPushMessageDeliveryStatusE;
import org.csapi.www.schema.parlayx.wap_push.send.v1_0.local.GetPushMessageDeliveryStatusResponseE;
import org.csapi.www.schema.parlayx.wap_push.send.v1_0.local.SendPushMessageE;
import org.csapi.www.schema.parlayx.wap_push.send.v1_0.local.SendPushMessageResponse;
import org.csapi.www.schema.parlayx.wap_push.send.v1_0.local.SendPushMessageResponseE;

/**
 * SendPushMessageServiceSkeleton java skeleton for the axisService
 */
public class SendPushMessageServiceSkeleton
{

	/**
	 * Auto generated method signature
	 * 
	 * @param getPushMessageDeliveryStatus
	 * @return getPushMessageDeliveryStatusResponse
	 * @throws PolicyException
	 * @throws ServiceException
	 */

	public GetPushMessageDeliveryStatusResponseE getPushMessageDeliveryStatus(
			GetPushMessageDeliveryStatusE getPushMessageDeliveryStatus)
			throws PolicyException, ServiceException
	{
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#getPushMessageDeliveryStatus");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param sendPushMessage
	 * @return sendPushMessageResponse
	 * @throws PolicyException
	 * @throws ServiceException
	 */

	public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
			"http://www.huawei.com.cn/schema/common/v2_1", "RequestSOAPHeader",
			"ns1");

	public SendPushMessageResponseE sendPushMessage(
			SendPushMessageE sendPushMessage) throws PolicyException,
			ServiceException
	{
		try
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

			SendPushMessageResponse mRes = new SendPushMessageResponse();
			mRes.setRequestIdentifier("100001200301130605033504614941");
			SendPushMessageResponseE mResE = new SendPushMessageResponseE();
			mResE.setSendPushMessageResponse(mRes);
			return mResE;
		}
		catch (Exception ex)
		{
			// TODO : fill this with the necessary business logic
			throw new java.lang.UnsupportedOperationException(ex.getMessage()
					+ this.getClass().getName() + "#sendPushMessage");
		}

	}
}
