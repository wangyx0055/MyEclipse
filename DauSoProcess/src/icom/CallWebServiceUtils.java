package icom;

import java.sql.CallableStatement;
import java.sql.Connection;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.engine.DefaultObjectSupplier;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.axis2.util.Base64;

//import com.vht.sms.cc.LocalDatabasePooling;

public class CallWebServiceUtils {

	public static String call(String link, String targetNameSpace,
			String function, String userName, String password, Object[] objects) {
		String re = null;
		try {
			ServiceClient serviceClient = new ServiceClient();
			Options options = serviceClient.getOptions();

			if (userName != null && !userName.equals("") && password != null) {
				Authenticator auth = new Authenticator();
				auth.setUsername(userName);
				auth.setPassword(password);
				auth.setPreemptiveAuthentication(true);
				serviceClient.setOptions(options);
				serviceClient
						.getOptions()
						.setProperty(
								org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
								auth);
			}
			EndpointReference linkws = new EndpointReference(link);
			options.setTo(linkws);
			QName qName = new QName(targetNameSpace, function);
			OMElement request = BeanUtil.getOMElement(qName, objects, null,
					false, null);
			OMElement response = serviceClient.sendReceive(request);

			Class[] returnTypes = new Class[] { String.class };

			Object[] result = BeanUtil.deserialize(response, returnTypes,
					new DefaultObjectSupplier());
			if (result != null) {
				re = (String) result[0];
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return re;
	}

	
	public static void main(String[] args) {
		// System.out.println(CallWebServiceUtils.call(
		// "http://112.78.7.137:8686/vht/services/sms?wsdl",
		// "http://core.brand.ws.dnv.com", "send", null, null,
		// new Object[] { "bdfef6", "vht", "84947579583", "CongDanh",
		// "Test from VHT", "Test", "12/16/2010", "Test" }));
		//
		// [bdfef6, vht, 0947579583, CONGDANH, VHT TESTING , , , ]
		// long a = System.currentTimeMillis();
		// System.out
		// .println(CallWebServiceUtils.call(
		// "http://203.162.71.82/wsicom/services/Receiver",
		// "http://203.162.71.82/wsicom/services/Receiver",
		// "mtReceiver",
		// "vht8x51",
		// "jhj09uuii6ll",
		// new Object[] {
		// "84947579583",
		// Base64.encode("Boong...biing...boong...Boong...biing...boong...Boong...biing...boong...
		// Boong...biing...boong...Boong...biing...boong...Boong...biing...boong...
		// Boong...biing"
		// .getBytes()), "8051", "CMO", "1",
		// "12792", "1", "1", "0", "0", "GPC" }));
		// long b = System.currentTimeMillis();
		// System.out.println((a - b));

		// Object[] objects = new Object[] {
		// tokens[KEYWORD],
		// Base64.encode(dm.getMessage().getBytes()),
		// MessageUtils.getOperator(dm.getUserId()),
		// dm.getId(),
		// dm.getServiceId(),
		// dm.getUserId() };

		// System.out.println(CallWebServiceUtils.call(
		// "http://203.162.71.82/SMSContentWS/services/Receiver",
		// "http://203.162.71.82/SMSContentWS/services/Receiver",
		// "moReceiverStandard", "vht", "THv111!@#", new Object[] {
		// "XSHCM", Base64.encode("XSHCM".getBytes()), "GPC",
		// "123456", "8051", "84947579583" }));

		// System.out.println(CallWebServiceUtils.call(
		// "http://112.78.7.137:8686/vht/services/vht-gateway?wsdl",
		// "http://impl.service.gateway.sms.vht.com", "sendMT", null,
		// null, new Object[] { "8051", "84947579583", "0", "1", "1", "1",
		// "0",
		// "Boong...biing...boong...Boong...biing...boong...Boong...biing...boong...
		// Boong...biing...boong...Boong...biing...boong...Boong...biing...boong...
		// Boong...biing",
		// "123458", "vht123", "OCV", "vht123456",
		// "GPC" }));
		// System.out.println("Boong...biing...boong...Boong...biing...boong...Boong...biing...boong...
		// Boong...biing...boong...Boong...biing...boong...Boong...biing...boong...
		// Boong...biing".length());

		// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		// String wsLink =
		// "http://luongsonbac.ftl.vn/ws/services/MessageListener";
		// String username = "vht";
		// String password = "vht.!@#";
		// String targetNameSpace = "http://ws.vassms.ftl.com";
		// String function = "sendMT";
		// Object[] objects = new Object[] { "1", null, null, "6785",
		// "84909465234", "Option", "Hello Test", "SMS", null, 1, 1,
		// "vht", sdf.format(new Date()), 1, "VMS", username, password };
		// String rs = CallWebServiceUtils.call(wsLink, targetNameSpace,
		// function,
		// null, null, objects);
		// System.out.println(rs);
	}
}
