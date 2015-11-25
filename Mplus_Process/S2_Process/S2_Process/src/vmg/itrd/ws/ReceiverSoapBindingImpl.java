/**
 * ReceiverSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package vmg.itrd.ws;

import java.rmi.RemoteException;


import icom.Constants;

public class ReceiverSoapBindingImpl implements vmg.itrd.ws.Receiver {

	public int insertMTQueueVMS(java.lang.String USER_ID,
			java.lang.String SERVICE_ID, java.lang.String MOBILE_OPERATOR,
			java.lang.String COMMAND_CODE, java.lang.String CONTENT_TYPE,
			java.lang.String INFO, java.lang.String SUBMIT_DATE,
			java.lang.String DONE_DATE, java.lang.String PROCESS_RESULT,
			java.lang.String MESSAGE_TYPE, java.lang.String REQUEST_ID,
			java.lang.String MESSAGE_ID, java.lang.String TOTAL_SEGMENTS,
			java.lang.String RETRIES_NUM, java.lang.String INSERT_DATE,
			java.lang.String NOTES, java.lang.String AMOUNT,
			java.lang.String CHANNEL_TYPE) throws java.rmi.RemoteException {
		ReceiverSoapBindingStub binding = null;
		try {
			ReceiverServiceLocator locator = new ReceiverServiceLocator();
			// locator.setReceiverEndpointAddress("");
			binding = (ReceiverSoapBindingStub) locator.getReceiver();

			binding.setUsername(Constants._prop.getProperty(
					"wsICOM2VMS_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMS_PassWord", ""));

			return binding.insertMTQueueVMS(USER_ID, SERVICE_ID,
					MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,
					SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE,
					REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM,
					INSERT_DATE, NOTES, AMOUNT, CHANNEL_TYPE);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	public int insertVMSChargeOnline(java.lang.String USER_ID,
			java.lang.String SERVICE_ID, java.lang.String MOBILE_OPERATOR,
			java.lang.String COMMAND_CODE, java.lang.String CONTENT_TYPE,
			java.lang.String INFO, java.lang.String SUBMIT_DATE,
			java.lang.String DONE_DATE, java.lang.String PROCESS_RESULT,
			java.lang.String MESSAGE_TYPE, java.lang.String REQUEST_ID,
			java.lang.String MESSAGE_ID, java.lang.String TOTAL_SEGMENTS,
			java.lang.String RETRIES_NUM, java.lang.String INSERT_DATE,
			java.lang.String NOTES, java.lang.String SERVICE_NAME,
			java.lang.String CHANNEL_TYPE, java.lang.String CONTENT_ID,
			java.lang.String AMOUNT) throws java.rmi.RemoteException {
		ReceiverSoapBindingStub binding = null;
		try {
			ReceiverServiceLocator locator = new ReceiverServiceLocator();
			// locator.setReceiverEndpointAddress("");
			binding = (ReceiverSoapBindingStub) locator.getReceiver();

			binding.setUsername(Constants._prop.getProperty(
					"wsICOM2VMS_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMS_PassWord", ""));

			return binding.insertVMSChargeOnline(USER_ID, SERVICE_ID,
					MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,
					SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE,
					REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM,
					INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID,
					AMOUNT);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	public int insertVmsChargePackage(String USER_ID, String SERVICE_ID,
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
					"wsICOM2VMS_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMS_PassWord", ""));

			return binding.insertVmsChargePackage(USER_ID, SERVICE_ID, MOBILE_OPERATOR,
					COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, 
					PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, 
					RETRIES_NUM, INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, 
					CONTENT_ID, AMOUNT, DAY_NUM, RESULT_CHARGE, IS_THE_PACKET);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	public int insertVmsReChargePackage(String USER_ID, String SERVICE_ID,
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
					"wsICOM2VMS_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMS_PassWord", ""));

			return binding.insertVmsReChargePackage(USER_ID, SERVICE_ID, MOBILE_OPERATOR,
					COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, 
					PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, 
					RETRIES_NUM, INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, 
					CONTENT_ID, AMOUNT, DAY_NUM, RESULT_CHARGE, IS_THE_PACKET);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

//	public int deleteInTable(String tableName, String userId,
//			String commandCode, String serviceId) throws RemoteException {
//		// TODO Auto-generated method stub
//		ReceiverSoapBindingStub binding = null;
//		try {
//			ReceiverServiceLocator locator = new ReceiverServiceLocator();
//			// locator.setReceiverEndpointAddress("");
//			binding = (ReceiverSoapBindingStub) locator.getReceiver();
//
//			binding.setUsername(Constants._prop.getProperty(
//					"wsICOM2VMSCharge_UserName", ""));
//			binding.setPassword(Constants._prop.getProperty(
//					"wsICOM2VMSCharge_PassWord", ""));
//
//			return binding.deleteInTable(tableName, userId, commandCode, serviceId);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return -3;
//	}

	public int insertMlist(String mlistTable, String USER_ID,
			String SERVICE_ID, String DATE, String OPTIONS, String FAILURES,
			String LAST_CODE, String AUTOTIMESTAMPS, String COMMAND_CODE,
			String REQUEST_ID, String MESSAGE_TYPE, String MOBILE_OPERATOR,
			String MT_COUNT, String MT_FREE, String DURATION, String AMOUNT,
			String CONTENT_ID, String SERVICE, String COMPANY_ID,
			String ACTIVE, String CHANNEL_TYPE, String REG_COUNT,
			String DATE_RETRY) throws RemoteException {
		ReceiverSoapBindingStub binding = null;
		try {
			ReceiverServiceLocator locator = new ReceiverServiceLocator();
			// locator.setReceiverEndpointAddress("");
			binding = (ReceiverSoapBindingStub) locator.getReceiver();

			binding.setUsername(Constants._prop.getProperty(
					"wsICOM2VMSCharge_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMSCharge_PassWord", ""));

			return binding.insertMlist(mlistTable, USER_ID, SERVICE_ID, DATE, OPTIONS, FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT, DATE_RETRY);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	public int insertToMlistFromMlist(String toMlistTable,
			String fromMlistTable, String registry_Again, String userId,
			String commandCode, String serviceId) throws RemoteException {
		ReceiverSoapBindingStub binding = null;
		try {
			ReceiverServiceLocator locator = new ReceiverServiceLocator();
			// locator.setReceiverEndpointAddress("");
			binding = (ReceiverSoapBindingStub) locator.getReceiver();

			binding.setUsername(Constants._prop.getProperty(
					"wsICOM2VMSCharge_UserName", ""));
			binding.setPassword(Constants._prop.getProperty(
					"wsICOM2VMSCharge_PassWord", ""));

			return binding.insertToMlistFromMlist(toMlistTable, fromMlistTable, registry_Again, userId, commandCode, serviceId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

}
