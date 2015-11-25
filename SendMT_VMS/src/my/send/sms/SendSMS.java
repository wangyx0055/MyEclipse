package my.send.sms;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import my.define.MySetting;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.databinding.ADBException;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.axis2.transport.http.HTTPConstants;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.PolicyException;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSmsResponse;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.ServiceException;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSms;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSmsE;
import org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.SendSmsResponseE;

import MyUtility.MyLogger;

import cn.com.huawei.www.schema.common.v2_1.RequestSOAPHeader;
import cn.com.huawei.www.schema.common.v2_1.RequestSOAPHeaderE;

public class SendSMS
{
	static MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), SendSMS.class.toString());

	public static RequestSOAPHeaderE createHeader(String MSISDN, String serviceId)
	{
		String OA = MSISDN;
		String FA = MSISDN;
		String spId = MySetting.spId;
		String spPassword = MySetting.spPassword;

		RequestSOAPHeaderE requestHeaderE = new RequestSOAPHeaderE();
		RequestSOAPHeader requestHeader = new RequestSOAPHeader();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String created = sdf.format(Calendar.getInstance().getTime());
		String password = NonceGenerator.getInstance().getNonce(spId + spPassword + created);
		requestHeader.setSpId(spId);
		requestHeader.setSpPassword(password);
		requestHeader.setServiceId(serviceId);
		requestHeader.setTimeStamp(created);
		requestHeader.setOA(OA);
		requestHeader.setFA(FA);

		requestHeaderE.setRequestSOAPHeader(requestHeader);

		return requestHeaderE;
	}

	public static SendSmsE createBody(String addresses, String senderName, String message)
	{
		try
		{
			URI address = new URI("tel:" + addresses + "");

			SendSms param = new SendSms();
			param.addAddresses(address);
			param.setMessage(message);
			// param.setReceiptRequest(sim);
			param.setSenderName(senderName);
			// param.setSourceport(123);
			SendSmsE sendSms = new SendSmsE();
			sendSms.setSendSms(param);
			return sendSms;
		}
		catch (MalformedURIException e)
		{
			return null;
		}
	}

	public static SendResultObject sendSms(RequestSOAPHeaderE header, SendSmsE body)
	{
		SendResultObject mResult = new SendResultObject();
		String strLog = "";
		try
		{
			strLog = "spId:" + header.getRequestSOAPHeader().getSpId() + "|serviceId:"
					+ header.getRequestSOAPHeader().getServiceId() + "|spPassword:"
					+ header.getRequestSOAPHeader().getSpPassword() + "|OA:" + header.getRequestSOAPHeader().getOA()
					+ "|FA:" + header.getRequestSOAPHeader().getFA() + "|TimeStamp:"
					+ header.getRequestSOAPHeader().getTimeStamp().toString() + "|addresses:"
					+ body.getSendSms().getAddresses().toString() + "|senderName:" + body.getSendSms().getSenderName()
					+ "|message:" + body.getSendSms().getMessage();

			SendSmsServiceStub stub = new SendSmsServiceStub(MySetting.LinkSendSMS);

			// The 2 dong duoi (getOptions) de fix loi: org.apache.axis2.AxisFault: The input
			// stream for an incoming message is null.
			stub._getServiceClient().getOptions()
					.setProperty(Constants.Configuration.MESSAGE_TYPE, HTTPConstants.MEDIA_TYPE_APPLICATION_ECHO_XML);
			stub._getServiceClient().getOptions()
					.setProperty(Constants.Configuration.DISABLE_SOAP_ACTION, Boolean.TRUE);
			
			stub._getServiceClient().addHeader(
					header.getOMElement(RequestSOAPHeaderE.MY_QNAME, OMAbstractFactory.getSOAP11Factory()));

			SendSmsResponseE response = stub.sendSms(body);

			SendSmsResponse mRes = response.getSendSmsResponse();
			mResult.VMS_MT_ID = mRes.getResult();
		}
		catch (AxisFault e)
		{
			mResult.faultstring = e.getMessage();
			mLog.log.error(e);
		}
		catch (RemoteException e)
		{
			mResult.faultstring = e.getMessage();
			mLog.log.error(e);
		}
		catch (PolicyException e)
		{
			mResult.faultstring = e.getMessage();
			mLog.log.error(e);
		}
		catch (ServiceException e)
		{
			mResult.faultcode = e.getFaultMessage().getServiceException().getMessageId();
			mResult.faultstring = e.getFaultMessage().getServiceException().getText();
			mLog.log.error(e);
		}
		catch (ADBException e)
		{
			mResult.faultcode = e.getMessage();
			mLog.log.error(e);
		}

		finally
		{
			strLog += "|VMS_MT_ID:" + mResult.VMS_MT_ID;
			strLog += "|faultcode:" + mResult.faultcode;
			strLog += "|faultstring:" + mResult.faultstring;
			mLog.log.info("SEND MT VMS LOG -->" + strLog);
		}
		return mResult;
	}

}
