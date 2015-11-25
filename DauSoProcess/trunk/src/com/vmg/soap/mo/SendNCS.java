package com.vmg.soap.mo;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.math.BigDecimal;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Collection;

import javax.xml.namespace.QName;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import com.vmg.sms.common.Util;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;

import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import ncs2.ReceiverStub;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.engine.DefaultObjectSupplier;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.commons.httpclient.Credentials;

import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.params.DefaultHttpParams;

public class SendNCS extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		int result = callWS(msgObject.getKeyword(), msgObject
				.getMobileoperator(), msgObject.getRequestid().toString(),
				msgObject.getUserid(), msgObject.getServiceid(), msgObject
						.getUsertext());
		Util.logger.info("result:" + result + ",user_id:"
				+ msgObject.getUserid() + ",command_code:"
				+ msgObject.getKeyword());

		if (result == 200) {
			Util.logger.info("OK!!!");

		} else {
			add2SMSSendFailed(msgObject);
		}
		return null;

	}

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

	public int callWS(String commandCode, String gatewayId, String messageId,
			String phoneNumber, String serviceNumber, String message) {

		int result = -1;

		try {
			ReceiverStub.MoReceiver reStub = new ReceiverStub.MoReceiver();

			reStub.setCommand_Code(commandCode);
			reStub.setMessage(Base64.encode(message.getBytes()));
			reStub.setOperator(gatewayId);
			reStub.setRequest_ID(messageId);
			reStub.setService_ID(serviceNumber);
			reStub.setUser_ID(phoneNumber);
			ReceiverStub.AuthHeader auth1 = new ReceiverStub.AuthHeader();

			auth1.setUsername("ws_authe_user2");
			auth1.setPassword("uS3rl_Ws2o1l");
			ReceiverStub.AuthHeaderE authHeader1 = new ReceiverStub.AuthHeaderE();
			authHeader1.setAuthHeader(auth1);
			ReceiverStub receiverServiceStub = new ReceiverStub();
			Options options = new Options();

			// set if realm or domain is known

			options.setProperty(HTTPConstants.CHUNKED, "false");
			options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");
			ReceiverStub.MoReceiverResponse receiverStandardResponse = receiverServiceStub
					.moReceiver(reStub, authHeader1);

			System.out.println("result:"
					+ receiverStandardResponse.getMoReceiverResult());
			result = receiverStandardResponse.getMoReceiverResult();

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void setupCredential() {
		final NTCredentials nt = new NTCredentials("user", "pass", "", "domain");
		final CredentialsProvider myCredentialsProvider = new CredentialsProvider() {
			public Credentials getCredentials(AuthScheme scheme, String host,
					int port, boolean proxy)
					throws CredentialsNotAvailableException {
				return nt;
			}
		};
		DefaultHttpParams.getDefaultParams().setParameter(
				"http.authentication.credential-provider",
				myCredentialsProvider);
	}

	private static BigDecimal add2SMSSendFailed(MsgObject msgObject) {

		Util.logger.info("add2SMSSendFailed:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CPID)"
				+ " values(?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getKeyword());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

}
