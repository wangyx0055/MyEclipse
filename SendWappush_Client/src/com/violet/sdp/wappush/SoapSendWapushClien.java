package com.violet.sdp.wappush;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.ADBException;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
/*import org.csapi.www.schema.parlayx.wap_push.send.v1_0.local.SendPushMessage;
import org.csapi.www.schema.parlayx.wap_push.send.v1_0.local.SendPushMessageE;*/
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessage;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE;

import cn.com.huawei.www.schema.common.v2_1.RequestSOAPHeader;
import cn.com.huawei.www.schema.common.v2_1.RequestSOAPHeaderE;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.*;

public class SoapSendWapushClien
{
	public static RequestSOAPHeaderE createHeader()
	{
		RequestSOAPHeaderE requestHeaderE = new RequestSOAPHeaderE();
		RequestSOAPHeader requestHeader = new RequestSOAPHeader();
		String spId = "chilinh123";
		String serviceId = "serviceId123";
		String spPassword = "spPassword123";
		String oa = "oa123";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String created = sdf.format(Calendar.getInstance().getTime());
		String password = NonceGenerator.getInstance().getNonce(spId + spPassword + created);
		requestHeader.setSpId(spId);
		requestHeader.setSpPassword(password);
		requestHeader.setServiceId(serviceId);
		requestHeader.setTimeStamp(created);
		requestHeader.setOA(oa);
		requestHeaderE.setRequestSOAPHeader(requestHeader);
		
		return requestHeaderE;
	}

	public static SendPushMessageE createBody()
	{
		try
		{
			URI address = new URI("tel:84906294479");
			URI TargetURL = new URI("http://muaban.net");
			SendPushMessage param = new SendPushMessage();
			param.addAddresses(address);
			//
			param.setTargetURL(TargetURL);
			param.setSenderAddress("9005");
			param.setSubject("test wappush");
			// param.setSourceport(123);
			SendPushMessageE pushMessage = new SendPushMessageE();
			pushMessage.setSendPushMessage(param);
			return pushMessage;
		}
		catch (MalformedURIException e)
		{
			return null;
		}
	}

	public static void sendPush(RequestSOAPHeaderE header, SendPushMessageE body)
	{
		try
		{
			SendPushMessageServiceStub stub = new SendPushMessageServiceStub(
					"http://localhost:8089/axis2/services/SendPushMessageService.SendPushMessageServiceHttpSoap11Endpoint/");
			stub._getServiceClient().addHeader(
					header.getOMElement(RequestSOAPHeaderE.MY_QNAME, OMAbstractFactory.getSOAP11Factory()));
			
			SendPushMessageResponseE response = stub.sendPushMessage(body);
			System.out
					.println(response.getOMElement(SendPushMessageResponseE.MY_QNAME, OMAbstractFactory.getSOAP11Factory()));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		sendPush(createHeader(), createBody());
	}
}
