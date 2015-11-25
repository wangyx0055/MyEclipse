package ws.vmscharge;

import java.rmi.RemoteException;

import icom.Constants;

public class ReceiverSoapBindingImpl implements Receiver {
	public int mtReceiver(java.lang.String USER_ID,
			java.lang.String SERVICE_ID, java.lang.String COMMAND_CODE,
			java.lang.String MESSAGE_TYPE, java.lang.String REQUEST_ID,
			java.lang.String MESSAGE_ID, java.lang.String SERVICE_NAME,
			java.lang.String CHANNEL_TYPE, java.lang.String CONTENT_ID,
			java.lang.String AMOUNT, java.lang.String TIME_DELIVERY,
			java.lang.String COMPANY_ID, java.lang.String IS_THE_SEND,
			java.lang.String LAST_CODE, java.lang.String OPTIONS)
			throws java.rmi.RemoteException {
		ReceiverSoapBindingStub binding = null;
		try {
			ReceiverServiceLocator locator = new ReceiverServiceLocator();
			// locator.setReceiverEndpointAddress("");
			binding = (ReceiverSoapBindingStub) locator.getReceiver();

			binding.setUsername(Constants._prop.getProperty(
					"wsICOM2VMSCharge_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMSCharge_PassWord", ""));

			return binding.mtReceiver(USER_ID, SERVICE_ID, COMMAND_CODE,
					MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, SERVICE_NAME,
					CHANNEL_TYPE, CONTENT_ID, AMOUNT, TIME_DELIVERY,
					COMPANY_ID, IS_THE_SEND, LAST_CODE, OPTIONS);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	public int insertVmsChargeResultPackage(String USER_ID, String SERVICE_ID,
			String MOBILE_OPERATOR, String COMMAND_CODE, String CONTENT_TYPE,
			String INFO, String SUBMIT_DATE, String DONE_DATE,
			String PROCESS_RESULT, String MESSAGE_TYPE, String REQUEST_ID,
			String MESSAGE_ID, String TOTAL_SEGMENTS, String RETRIES_NUM,
			String INSERT_DATE, String NOTES, String SERVICE_NAME,
			String CHANNEL_TYPE, String CONTENT_ID, String AMOUNT,
			String DAY_NUM, String RESULT_CHARGE, String IS_THE_PACKET)
			throws RemoteException {
		ReceiverSoapBindingStub binding = null;
		try {
			ReceiverServiceLocator locator = new ReceiverServiceLocator();
			// locator.setReceiverEndpointAddress("");
			binding = (ReceiverSoapBindingStub) locator.getReceiver();

			binding.setUsername(Constants._prop.getProperty(
					"wsICOM2VMSCharge_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMSCharge_PassWord", ""));

			return binding.insertVmsChargeResultPackage(USER_ID, SERVICE_ID,
					MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,
					SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE,
					REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM,
					INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID,
					AMOUNT, DAY_NUM, RESULT_CHARGE, IS_THE_PACKET);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	public int insertVmsReChargeResultPackage(String USER_ID,
			String SERVICE_ID, String MOBILE_OPERATOR, String COMMAND_CODE,
			String CONTENT_TYPE, String INFO, String SUBMIT_DATE,
			String DONE_DATE, String PROCESS_RESULT, String MESSAGE_TYPE,
			String REQUEST_ID, String MESSAGE_ID, String TOTAL_SEGMENTS,
			String RETRIES_NUM, String INSERT_DATE, String NOTES,
			String SERVICE_NAME, String CHANNEL_TYPE, String CONTENT_ID,
			String AMOUNT, String DAY_NUM, String RESULT_CHARGE,
			String IS_THE_PACKET) throws RemoteException {
		ReceiverSoapBindingStub binding = null;
		try {
			ReceiverServiceLocator locator = new ReceiverServiceLocator();
			// locator.setReceiverEndpointAddress("");
			binding = (ReceiverSoapBindingStub) locator.getReceiver();

			binding.setUsername(Constants._prop.getProperty(
					"wsICOM2VMSCharge_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMSCharge_PassWord", ""));

			return binding.insertVmsReChargeResultPackage(USER_ID, SERVICE_ID,
					MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,
					SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE,
					REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM,
					INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID,
					AMOUNT, DAY_NUM, RESULT_CHARGE, IS_THE_PACKET);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	

}
