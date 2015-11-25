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
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessage;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE;
import org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException;

import MyUtility.MyLogger;

import cn.com.huawei.www.schema.common.v2_1.RequestSOAPHeader;
import cn.com.huawei.www.schema.common.v2_1.RequestSOAPHeaderE;

public class SendWappush
{

	static MyLogger mLog = new MyLogger(MySetting.GetLog4JConfigPath(), SendWappush.class.toString());
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
	public static SendPushMessageE createBody(String addresses, String senderName, String subject, String LinkDown)
	{
		try
		{
			URI address = new URI("tel:" + addresses + "");
			URI TargetURL = new URI(LinkDown);
			SendPushMessage param = new SendPushMessage();
			param.addAddresses(address);
			//
			param.setTargetURL(TargetURL);
			param.setSenderAddress(senderName);
			param.setSubject(subject);
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

	public static SendResultObject sendPush(RequestSOAPHeaderE header, SendPushMessageE body)
	{
		String strLog = "";
		SendResultObject mResult = new SendResultObject();
		try
		{
			strLog = "spId:" + header.getRequestSOAPHeader().getSpId() + "|serviceId:"
					+ header.getRequestSOAPHeader().getServiceId() + "|spPassword:"
					+ header.getRequestSOAPHeader().getSpPassword() + "|OA:" + header.getRequestSOAPHeader().getOA()
					+ "|FA:" + header.getRequestSOAPHeader().getFA() + "|TimeStamp:"
					+ header.getRequestSOAPHeader().getTimeStamp().toString() + "|addresses:"
					+ body.getSendPushMessage().getAddresses().toString() + "|SenderAddress:"
					+ body.getSendPushMessage().getSenderAddress() + "|subject:"
					+ body.getSendPushMessage().getSubject() + "|LinkDown:" + body.getSendPushMessage().getTargetURL();

			SendPushMessageServiceStub stub = new SendPushMessageServiceStub(MySetting.LinkSendWappush);

			stub._getServiceClient().getOptions()
					.setProperty(Constants.Configuration.MESSAGE_TYPE, HTTPConstants.MEDIA_TYPE_APPLICATION_ECHO_XML);
			stub._getServiceClient().getOptions()
					.setProperty(Constants.Configuration.DISABLE_SOAP_ACTION, Boolean.TRUE);

			stub._getServiceClient().addHeader(
					header.getOMElement(RequestSOAPHeaderE.MY_QNAME, OMAbstractFactory.getSOAP11Factory()));

			SendPushMessageResponseE response = stub.sendPushMessage(body);

			mResult.VMS_MT_ID = response.getSendPushMessageResponse().getRequestIdentifier();
			
		}
		catch (ServiceException e)
		{
			mResult.faultcode = e.getFaultMessage().getServiceException().getMessageId();
			mResult.faultstring = e.getFaultMessage().getServiceException().getText();
			
			mLog.log.error(e);
		}
		catch (AxisFault e)
		{
			mResult.faultstring = e.getMessage();
			mLog.log.error(e);
		}
		catch (ADBException e)
		{
			mResult.faultcode = e.getMessage();
			mLog.log.error(e);
		}
		catch (RemoteException e)
		{
			mResult.faultstring = e.getMessage();
			mLog.log.error(e);
		}
		catch (PolicyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
