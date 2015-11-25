/**
 * SendPushMessageServiceStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service;

/*
 *  SendPushMessageServiceStub java implementation
 */

public class SendPushMessageServiceStub extends org.apache.axis2.client.Stub
{
	protected org.apache.axis2.description.AxisOperation[] _operations;

	// hashmaps to keep the fault mapping
	private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();
	private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();
	private java.util.HashMap faultMessageMap = new java.util.HashMap();

	private static int counter = 0;

	private static synchronized java.lang.String getUniqueSuffix()
	{
		// reset the counter if it is greater than 99999
		if (counter > 99999)
		{
			counter = 0;
		}
		counter = counter + 1;
		return java.lang.Long.toString(java.lang.System.currentTimeMillis()) + "_" + counter;
	}

	private void populateAxisService() throws org.apache.axis2.AxisFault
	{

		// creating the Service with a unique name
		_service = new org.apache.axis2.description.AxisService("SendPushMessageService" + getUniqueSuffix());
		addAnonymousOperations();

		// creating the operations
		org.apache.axis2.description.AxisOperation __operation;

		_operations = new org.apache.axis2.description.AxisOperation[2];

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface",
				"getPushMessageDeliveryStatus"));
		_service.addOperation(__operation);

		_operations[0] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface",
				"sendPushMessage"));
		_service.addOperation(__operation);

		_operations[1] = __operation;

	}

	// populates the faults
	private void populateFaults()
	{

		faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "PolicyException"), "getPushMessageDeliveryStatus"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException");
		faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "PolicyException"), "getPushMessageDeliveryStatus"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException");
		faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "PolicyException"), "getPushMessageDeliveryStatus"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub$PolicyExceptionE");

		faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "ServiceException"), "getPushMessageDeliveryStatus"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException");
		faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "ServiceException"), "getPushMessageDeliveryStatus"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException");
		faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "ServiceException"), "getPushMessageDeliveryStatus"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub$ServiceExceptionE");

		faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "PolicyException"), "sendPushMessage"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException");
		faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "PolicyException"), "sendPushMessage"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException");
		faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "PolicyException"), "sendPushMessage"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub$PolicyExceptionE");

		faultExceptionNameMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "ServiceException"), "sendPushMessage"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException");
		faultExceptionClassNameMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "ServiceException"), "sendPushMessage"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException");
		faultMessageMap.put(new org.apache.axis2.client.FaultMapKey(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "ServiceException"), "sendPushMessage"),
				"org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub$ServiceExceptionE");

	}

	/**
	 * Constructor that takes in a configContext
	 */

	public SendPushMessageServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext,
			java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault
	{
		this(configurationContext, targetEndpoint, false);
	}

	/**
	 * Constructor that takes in a configContext and useseperate listner
	 */
	public SendPushMessageServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext,
			java.lang.String targetEndpoint, boolean useSeparateListener) throws org.apache.axis2.AxisFault
	{
		// To populate AxisService
		populateAxisService();
		populateFaults();

		_serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);

		_serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
		_serviceClient.getOptions().setUseSeparateListener(useSeparateListener);

	}

	/**
	 * Default Constructor
	 */
	public SendPushMessageServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext)
			throws org.apache.axis2.AxisFault
	{

		this(configurationContext, "http://localhost:9080/SendPushMessageService/services/SendPushMessage");

	}

	/**
	 * Default Constructor
	 */
	public SendPushMessageServiceStub() throws org.apache.axis2.AxisFault
	{

		this("http://localhost:9080/SendPushMessageService/services/SendPushMessage");

	}

	/**
	 * Constructor taking the target endpoint
	 */
	public SendPushMessageServiceStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault
	{
		this(null, targetEndpoint);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageService#getPushMessageDeliveryStatus
	 * @param getPushMessageDeliveryStatus0
	 * 
	 * @throws org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException
	 *             :
	 * @throws org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException
	 *             :
	 */

	public org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE getPushMessageDeliveryStatus(

			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusE getPushMessageDeliveryStatus0)

	throws java.rmi.RemoteException

	, org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException,
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException
	{
		org.apache.axis2.context.MessageContext _messageContext = null;
		try
		{
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
			_operationClient
					.getOptions()
					.setAction(
							"http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface/SendPushMessage/getPushMessageDeliveryStatusRequest");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			_messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(
					getFactory(_operationClient.getOptions().getSoapVersionURI()),
					getPushMessageDeliveryStatus0,
					optimizeContent(new javax.xml.namespace.QName(
							"http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface", "getPushMessageDeliveryStatus")),
					new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface",
							"getPushMessageDeliveryStatus"));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(
					_returnEnv.getBody().getFirstElement(),
					org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE.class,
					getEnvelopeNamespaces(_returnEnv));

			return (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE) object;

		}
		catch (org.apache.axis2.AxisFault f)
		{

			org.apache.axiom.om.OMElement faultElt = f.getDetail();
			if (faultElt != null)
			{
				if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
						"getPushMessageDeliveryStatus")))
				{
					// make the fault by reflection
					try
					{
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
										"getPushMessageDeliveryStatus"));
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(String.class);
						java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap
								.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
										"getPushMessageDeliveryStatus"));
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage", new java.lang.Class[]
						{messageClass});
						m.invoke(ex, new java.lang.Object[]
						{messageObject});

						if (ex instanceof org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException) { throw (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException) ex; }

						if (ex instanceof org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException) { throw (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException) ex; }

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					}
					catch (java.lang.ClassCastException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.ClassNotFoundException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.NoSuchMethodException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.reflect.InvocationTargetException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.IllegalAccessException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.InstantiationException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
				}
				else
				{
					throw f;
				}
			}
			else
			{
				throw f;
			}
		}
		finally
		{
			if (_messageContext.getTransportOut() != null)
			{
				_messageContext.getTransportOut().getSender().cleanup(_messageContext);
			}
		}
	}

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageService#startgetPushMessageDeliveryStatus
	 * @param getPushMessageDeliveryStatus0
	 */
	public void startgetPushMessageDeliveryStatus(

			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusE getPushMessageDeliveryStatus0,

			final org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceCallbackHandler callback)

	throws java.rmi.RemoteException
	{

		org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
		_operationClient
				.getOptions()
				.setAction(
						"http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface/SendPushMessage/getPushMessageDeliveryStatusRequest");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.

		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), getPushMessageDeliveryStatus0,
				optimizeContent(new javax.xml.namespace.QName(
						"http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface", "getPushMessageDeliveryStatus")),
				new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface",
						"getPushMessageDeliveryStatus"));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback()
		{
			public void onMessage(org.apache.axis2.context.MessageContext resultContext)
			{
				try
				{
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(
							resultEnv.getBody().getFirstElement(),
							org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE.class,
							getEnvelopeNamespaces(resultEnv));
					callback.receiveResultgetPushMessageDeliveryStatus((org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE) object);

				}
				catch (org.apache.axis2.AxisFault e)
				{
					callback.receiveErrorgetPushMessageDeliveryStatus(e);
				}
			}

			public void onError(java.lang.Exception error)
			{
				if (error instanceof org.apache.axis2.AxisFault)
				{
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();
					if (faultElt != null)
					{
						if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
								"getPushMessageDeliveryStatus")))
						{
							// make the fault by reflection
							try
							{
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
												"getPushMessageDeliveryStatus"));
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(String.class);
								java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap
										.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
												"getPushMessageDeliveryStatus"));
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[]
										{messageClass});
								m.invoke(ex, new java.lang.Object[]
								{messageObject});

								if (ex instanceof org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException)
								{
									callback.receiveErrorgetPushMessageDeliveryStatus((org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException) ex);
									return;
								}

								if (ex instanceof org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException)
								{
									callback.receiveErrorgetPushMessageDeliveryStatus((org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException) ex);
									return;
								}

								callback.receiveErrorgetPushMessageDeliveryStatus(new java.rmi.RemoteException(ex
										.getMessage(), ex));
							}
							catch (java.lang.ClassCastException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorgetPushMessageDeliveryStatus(f);
							}
							catch (java.lang.ClassNotFoundException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorgetPushMessageDeliveryStatus(f);
							}
							catch (java.lang.NoSuchMethodException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorgetPushMessageDeliveryStatus(f);
							}
							catch (java.lang.reflect.InvocationTargetException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorgetPushMessageDeliveryStatus(f);
							}
							catch (java.lang.IllegalAccessException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorgetPushMessageDeliveryStatus(f);
							}
							catch (java.lang.InstantiationException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorgetPushMessageDeliveryStatus(f);
							}
							catch (org.apache.axis2.AxisFault e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorgetPushMessageDeliveryStatus(f);
							}
						}
						else
						{
							callback.receiveErrorgetPushMessageDeliveryStatus(f);
						}
					}
					else
					{
						callback.receiveErrorgetPushMessageDeliveryStatus(f);
					}
				}
				else
				{
					callback.receiveErrorgetPushMessageDeliveryStatus(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext)
			{
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete()
			{
				try
				{
					_messageContext.getTransportOut().getSender().cleanup(_messageContext);
				}
				catch (org.apache.axis2.AxisFault axisFault)
				{
					callback.receiveErrorgetPushMessageDeliveryStatus(axisFault);
				}
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
		if (_operations[0].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener())
		{
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[0].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);

	}

	/**
	 * Auto generated method signature
	 * 
	 * @see org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageService#sendPushMessage
	 * @param sendPushMessage2
	 * 
	 * @throws org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException
	 *             :
	 * @throws org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException
	 *             :
	 */

	public org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE sendPushMessage(

	org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE sendPushMessage2)

	throws java.rmi.RemoteException

	, org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException,
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException
	{
		org.apache.axis2.context.MessageContext _messageContext = null;
		try
		{
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
			_operationClient.getOptions().setAction(
					"http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface/SendPushMessage/sendPushMessageRequest");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			_messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendPushMessage2,
					optimizeContent(new javax.xml.namespace.QName(
							"http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface", "sendPushMessage")),
					new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface",
							"sendPushMessage"));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(
					_returnEnv.getBody().getFirstElement(),
					org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE.class,
					getEnvelopeNamespaces(_returnEnv));

			return (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE) object;

		}
		catch (org.apache.axis2.AxisFault f)
		{

			org.apache.axiom.om.OMElement faultElt = f.getDetail();
			if (faultElt != null)
			{
				if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
						"sendPushMessage")))
				{
					// make the fault by reflection
					try
					{
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "sendPushMessage"));
						java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(String.class);
						java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap
								.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "sendPushMessage"));
						java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage", new java.lang.Class[]
						{messageClass});
						m.invoke(ex, new java.lang.Object[]
						{messageObject});

						if (ex instanceof org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException) { throw (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException) ex; }

						if (ex instanceof org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException) { throw (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException) ex; }

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					}
					catch (java.lang.ClassCastException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.ClassNotFoundException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.NoSuchMethodException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.reflect.InvocationTargetException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.IllegalAccessException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
					catch (java.lang.InstantiationException e)
					{
						// we cannot intantiate the class - throw the original
						// Axis fault
						throw f;
					}
				}
				else
				{
					throw f;
				}
			}
			else
			{
				throw f;
			}
		}
		finally
		{
			if (_messageContext.getTransportOut() != null)
			{
				_messageContext.getTransportOut().getSender().cleanup(_messageContext);
			}
		}
	}

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageService#startsendPushMessage
	 * @param sendPushMessage2
	 */
	public void startsendPushMessage(

	org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE sendPushMessage2,

	final org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceCallbackHandler callback)

	throws java.rmi.RemoteException
	{

		org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
		_operationClient.getOptions().setAction(
				"http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface/SendPushMessage/sendPushMessageRequest");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.

		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendPushMessage2,
				optimizeContent(new javax.xml.namespace.QName(
						"http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface", "sendPushMessage")),
				new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/wap_push/send/v1_0/interface",
						"sendPushMessage"));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback()
		{
			public void onMessage(org.apache.axis2.context.MessageContext resultContext)
			{
				try
				{
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(
							resultEnv.getBody().getFirstElement(),
							org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE.class,
							getEnvelopeNamespaces(resultEnv));
					callback.receiveResultsendPushMessage((org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE) object);

				}
				catch (org.apache.axis2.AxisFault e)
				{
					callback.receiveErrorsendPushMessage(e);
				}
			}

			public void onError(java.lang.Exception error)
			{
				if (error instanceof org.apache.axis2.AxisFault)
				{
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();
					if (faultElt != null)
					{
						if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
								"sendPushMessage")))
						{
							// make the fault by reflection
							try
							{
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "sendPushMessage"));
								java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(String.class);
								java.lang.Exception ex = (java.lang.Exception) constructor.newInstance(f.getMessage());
								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap
										.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(), "sendPushMessage"));
								java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[]
										{messageClass});
								m.invoke(ex, new java.lang.Object[]
								{messageObject});

								if (ex instanceof org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException)
								{
									callback.receiveErrorsendPushMessage((org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.PolicyException) ex);
									return;
								}

								if (ex instanceof org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException)
								{
									callback.receiveErrorsendPushMessage((org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.ServiceException) ex);
									return;
								}

								callback.receiveErrorsendPushMessage(new java.rmi.RemoteException(ex.getMessage(), ex));
							}
							catch (java.lang.ClassCastException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsendPushMessage(f);
							}
							catch (java.lang.ClassNotFoundException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsendPushMessage(f);
							}
							catch (java.lang.NoSuchMethodException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsendPushMessage(f);
							}
							catch (java.lang.reflect.InvocationTargetException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsendPushMessage(f);
							}
							catch (java.lang.IllegalAccessException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsendPushMessage(f);
							}
							catch (java.lang.InstantiationException e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsendPushMessage(f);
							}
							catch (org.apache.axis2.AxisFault e)
							{
								// we cannot intantiate the class - throw the
								// original Axis fault
								callback.receiveErrorsendPushMessage(f);
							}
						}
						else
						{
							callback.receiveErrorsendPushMessage(f);
						}
					}
					else
					{
						callback.receiveErrorsendPushMessage(f);
					}
				}
				else
				{
					callback.receiveErrorsendPushMessage(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext)
			{
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete()
			{
				try
				{
					_messageContext.getTransportOut().getSender().cleanup(_messageContext);
				}
				catch (org.apache.axis2.AxisFault axisFault)
				{
					callback.receiveErrorsendPushMessage(axisFault);
				}
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
		if (_operations[1].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener())
		{
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[1].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);

	}

	/**
	 * A utility method that copies the namepaces from the SOAPEnvelope
	 */
	private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env)
	{
		java.util.Map returnMap = new java.util.HashMap();
		java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
		while (namespaceIterator.hasNext())
		{
			org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
			returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
		}
		return returnMap;
	}

	private javax.xml.namespace.QName[] opNameArray = null;
	private boolean optimizeContent(javax.xml.namespace.QName opName)
	{

		if (opNameArray == null) { return false; }
		for (int i = 0; i < opNameArray.length; i++)
		{
			if (opName.equals(opNameArray[i])) { return true; }
		}
		return false;
	}
	// http://localhost:9080/SendPushMessageService/services/SendPushMessage
	public static class SendPushMessageE implements org.apache.axis2.databinding.ADBBean
	{

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "sendPushMessage", "ns2");

		/**
		 * field for SendPushMessage
		 */

		protected SendPushMessage localSendPushMessage;

		/**
		 * Auto generated getter method
		 * 
		 * @return SendPushMessage
		 */
		public SendPushMessage getSendPushMessage()
		{
			return localSendPushMessage;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            SendPushMessage
		 */
		public void setSendPushMessage(SendPushMessage param)
		{

			this.localSendPushMessage = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
			return factory.createOMElement(dataSource, MY_QNAME);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it

			if (localSendPushMessage == null) { throw new org.apache.axis2.databinding.ADBException(
					"sendPushMessage cannot be null!"); }
			localSendPushMessage.serialize(MY_QNAME, xmlWriter);

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) { return "ns2"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it
			return localSendPushMessage.getPullParser(MY_QNAME);

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static SendPushMessageE parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				SendPushMessageE object = new SendPushMessageE();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					while (!reader.isEndElement())
					{
						if (reader.isStartElement())
						{

							if (reader.isStartElement()
									&& new javax.xml.namespace.QName(
											"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
											"sendPushMessage").equals(reader.getName()))
							{

								object.setSendPushMessage(SendPushMessage.Factory.parse(reader));

							} // End of if for expected property start element

							else
							{
								// A start element we are not expecting
								// indicates an invalid parameter was passed
								throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
										+ reader.getName());
							}

						}
						else
						{
							reader.next();
						}
					} // end of while loop

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class ServiceException implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * ServiceException Namespace URI =
		 * http://www.csapi.org/schema/parlayx/common/v2_1 Namespace Prefix =
		 * ns1
		 */

		/**
		 * field for MessageId
		 */

		protected java.lang.String localMessageId;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getMessageId()
		{
			return localMessageId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            MessageId
		 */
		public void setMessageId(java.lang.String param)
		{

			this.localMessageId = param;

		}

		/**
		 * field for Text
		 */

		protected java.lang.String localText;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getText()
		{
			return localText;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Text
		 */
		public void setText(java.lang.String param)
		{

			this.localText = param;

		}

		/**
		 * field for Variables This was an Array!
		 */

		protected java.lang.String[] localVariables;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localVariablesTracker = false;

		public boolean isVariablesSpecified()
		{
			return localVariablesTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String[]
		 */
		public java.lang.String[] getVariables()
		{
			return localVariables;
		}

		/**
		 * validate the array for Variables
		 */
		protected void validateVariables(java.lang.String[] param)
		{

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Variables
		 */
		public void setVariables(java.lang.String[] param)
		{

			validateVariables(param);

			localVariablesTracker = param != null;

			this.localVariables = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param
		 *            java.lang.String
		 */
		public void addVariables(java.lang.String param)
		{
			if (localVariables == null)
			{
				localVariables = new java.lang.String[]{};
			}

			// update the setting tracker
			localVariablesTracker = true;

			java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localVariables);
			list.add(param);
			this.localVariables = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/common/v2_1");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":ServiceException", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ServiceException", xmlWriter);
				}

			}

			namespace = "";
			writeStartElement(null, namespace, "messageId", xmlWriter);

			if (localMessageId == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("messageId cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localMessageId);

			}

			xmlWriter.writeEndElement();

			namespace = "";
			writeStartElement(null, namespace, "text", xmlWriter);

			if (localText == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("text cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localText);

			}

			xmlWriter.writeEndElement();
			if (localVariablesTracker)
			{
				if (localVariables != null)
				{
					namespace = "";
					for (int i = 0; i < localVariables.length; i++)
					{

						if (localVariables[i] != null)
						{

							writeStartElement(null, namespace, "variables", xmlWriter);

							xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(localVariables[i]));

							xmlWriter.writeEndElement();

						}
						else
						{

							// we have to do nothing since minOccurs is zero

						}

					}
				}
				else
				{

					throw new org.apache.axis2.databinding.ADBException("variables cannot be null!!");

				}

			}
			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/common/v2_1")) { return "ns1"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			elementList.add(new javax.xml.namespace.QName("", "messageId"));

			if (localMessageId != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMessageId));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("messageId cannot be null!!");
			}

			elementList.add(new javax.xml.namespace.QName("", "text"));

			if (localText != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localText));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("text cannot be null!!");
			}
			if (localVariablesTracker)
			{
				if (localVariables != null)
				{
					for (int i = 0; i < localVariables.length; i++)
					{

						if (localVariables[i] != null)
						{
							elementList.add(new javax.xml.namespace.QName("", "variables"));
							elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(localVariables[i]));
						}
						else
						{

							// have to do nothing

						}

					}
				}
				else
				{

					throw new org.apache.axis2.databinding.ADBException("variables cannot be null!!");

				}

			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static ServiceException parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				ServiceException object = new ServiceException();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"ServiceException".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (ServiceException) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					java.util.ArrayList list3 = new java.util.ArrayList();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "messageId").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "messageId" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setMessageId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "text").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "text" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setText(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "variables").equals(reader.getName()))
					{

						// Process the array and step past its final element's
						// end.
						list3.add(reader.getElementText());

						// loop until we find a start element that is not part
						// of this array
						boolean loopDone3 = false;
						while (!loopDone3)
						{
							// Ensure we are at the EndElement
							while (!reader.isEndElement())
							{
								reader.next();
							}
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement())
							{
								// two continuous end elements means we are
								// exiting the xml structure
								loopDone3 = true;
							}
							else
							{
								if (new javax.xml.namespace.QName("", "variables").equals(reader.getName()))
								{
									list3.add(reader.getElementText());

								}
								else
								{
									loopDone3 = true;
								}
							}
						}
						// call the converter utility to convert and set the
						// array

						object.setVariables((java.lang.String[]) list3.toArray(new java.lang.String[list3.size()]));

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class GetPushMessageDeliveryStatusE implements org.apache.axis2.databinding.ADBBean
	{

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "getPushMessageDeliveryStatus", "ns2");

		/**
		 * field for GetPushMessageDeliveryStatus
		 */

		protected GetPushMessageDeliveryStatus localGetPushMessageDeliveryStatus;

		/**
		 * Auto generated getter method
		 * 
		 * @return GetPushMessageDeliveryStatus
		 */
		public GetPushMessageDeliveryStatus getGetPushMessageDeliveryStatus()
		{
			return localGetPushMessageDeliveryStatus;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            GetPushMessageDeliveryStatus
		 */
		public void setGetPushMessageDeliveryStatus(GetPushMessageDeliveryStatus param)
		{

			this.localGetPushMessageDeliveryStatus = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
			return factory.createOMElement(dataSource, MY_QNAME);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it

			if (localGetPushMessageDeliveryStatus == null) { throw new org.apache.axis2.databinding.ADBException(
					"getPushMessageDeliveryStatus cannot be null!"); }
			localGetPushMessageDeliveryStatus.serialize(MY_QNAME, xmlWriter);

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) { return "ns2"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it
			return localGetPushMessageDeliveryStatus.getPullParser(MY_QNAME);

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static GetPushMessageDeliveryStatusE parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception
			{
				GetPushMessageDeliveryStatusE object = new GetPushMessageDeliveryStatusE();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					while (!reader.isEndElement())
					{
						if (reader.isStartElement())
						{

							if (reader.isStartElement()
									&& new javax.xml.namespace.QName(
											"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
											"getPushMessageDeliveryStatus").equals(reader.getName()))
							{

								object.setGetPushMessageDeliveryStatus(GetPushMessageDeliveryStatus.Factory.parse(reader));

							} // End of if for expected property start element

							else
							{
								// A start element we are not expecting
								// indicates an invalid parameter was passed
								throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
										+ reader.getName());
							}

						}
						else
						{
							reader.next();
						}
					} // end of while loop

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class DeliveryInformation implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * DeliveryInformation Namespace URI =
		 * http://www.csapi.org/schema/parlayx/wap_push/v1_0 Namespace Prefix =
		 * ns3
		 */

		/**
		 * field for Address
		 */

		protected org.apache.axis2.databinding.types.URI localAddress;

		/**
		 * Auto generated getter method
		 * 
		 * @return org.apache.axis2.databinding.types.URI
		 */
		public org.apache.axis2.databinding.types.URI getAddress()
		{
			return localAddress;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Address
		 */
		public void setAddress(org.apache.axis2.databinding.types.URI param)
		{

			this.localAddress = param;

		}

		/**
		 * field for Status
		 */

		protected DeliveryStatus localStatus;

		/**
		 * Auto generated getter method
		 * 
		 * @return DeliveryStatus
		 */
		public DeliveryStatus getStatus()
		{
			return localStatus;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Status
		 */
		public void setStatus(DeliveryStatus param)
		{

			this.localStatus = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/wap_push/v1_0");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":DeliveryInformation", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "DeliveryInformation",
							xmlWriter);
				}

			}

			namespace = "";
			writeStartElement(null, namespace, "address", xmlWriter);

			if (localAddress == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("address cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAddress));

			}

			xmlWriter.writeEndElement();

			if (localStatus == null) { throw new org.apache.axis2.databinding.ADBException("status cannot be null!!"); }
			localStatus.serialize(new javax.xml.namespace.QName("", "status"), xmlWriter);

			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/v1_0")) { return "ns3"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			elementList.add(new javax.xml.namespace.QName("", "address"));

			if (localAddress != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAddress));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("address cannot be null!!");
			}

			elementList.add(new javax.xml.namespace.QName("", "status"));

			if (localStatus == null) { throw new org.apache.axis2.databinding.ADBException("status cannot be null!!"); }
			elementList.add(localStatus);

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DeliveryInformation parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				DeliveryInformation object = new DeliveryInformation();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"DeliveryInformation".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (DeliveryInformation) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "address").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "address" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setAddress(org.apache.axis2.databinding.utils.ConverterUtil.convertToAnyURI(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "status").equals(reader.getName()))
					{

						object.setStatus(DeliveryStatus.Factory.parse(reader));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class GetPushMessageDeliveryStatusResponse implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * getPushMessageDeliveryStatusResponse Namespace URI =
		 * http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local
		 * Namespace Prefix = ns2
		 */

		/**
		 * field for DeliveryStatus This was an Array!
		 */

		protected DeliveryInformation[] localDeliveryStatus;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localDeliveryStatusTracker = false;

		public boolean isDeliveryStatusSpecified()
		{
			return localDeliveryStatusTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return DeliveryInformation[]
		 */
		public DeliveryInformation[] getDeliveryStatus()
		{
			return localDeliveryStatus;
		}

		/**
		 * validate the array for DeliveryStatus
		 */
		protected void validateDeliveryStatus(DeliveryInformation[] param)
		{

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            DeliveryStatus
		 */
		public void setDeliveryStatus(DeliveryInformation[] param)
		{

			validateDeliveryStatus(param);

			localDeliveryStatusTracker = param != null;

			this.localDeliveryStatus = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param
		 *            DeliveryInformation
		 */
		public void addDeliveryStatus(DeliveryInformation param)
		{
			if (localDeliveryStatus == null)
			{
				localDeliveryStatus = new DeliveryInformation[]{};
			}

			// update the setting tracker
			localDeliveryStatusTracker = true;

			java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localDeliveryStatus);
			list.add(param);
			this.localDeliveryStatus = (DeliveryInformation[]) list.toArray(new DeliveryInformation[list.size()]);

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":getPushMessageDeliveryStatusResponse", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							"getPushMessageDeliveryStatusResponse", xmlWriter);
				}

			}
			if (localDeliveryStatusTracker)
			{
				if (localDeliveryStatus != null)
				{
					for (int i = 0; i < localDeliveryStatus.length; i++)
					{
						if (localDeliveryStatus[i] != null)
						{
							localDeliveryStatus[i].serialize(new javax.xml.namespace.QName(
									"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "deliveryStatus"),
									xmlWriter);
						}
						else
						{

							// we don't have to do any thing since minOccures is
							// zero

						}

					}
				}
				else
				{

					throw new org.apache.axis2.databinding.ADBException("deliveryStatus cannot be null!!");

				}
			}
			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) { return "ns2"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localDeliveryStatusTracker)
			{
				if (localDeliveryStatus != null)
				{
					for (int i = 0; i < localDeliveryStatus.length; i++)
					{

						if (localDeliveryStatus[i] != null)
						{
							elementList.add(new javax.xml.namespace.QName(
									"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "deliveryStatus"));
							elementList.add(localDeliveryStatus[i]);
						}
						else
						{

							// nothing to do

						}

					}
				}
				else
				{

					throw new org.apache.axis2.databinding.ADBException("deliveryStatus cannot be null!!");

				}

			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static GetPushMessageDeliveryStatusResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception
			{
				GetPushMessageDeliveryStatusResponse object = new GetPushMessageDeliveryStatusResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"getPushMessageDeliveryStatusResponse".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (GetPushMessageDeliveryStatusResponse) ExtensionMapper.getTypeObject(nsUri, type,
										reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					java.util.ArrayList list1 = new java.util.ArrayList();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"deliveryStatus").equals(reader.getName()))
					{

						// Process the array and step past its final element's
						// end.
						list1.add(DeliveryInformation.Factory.parse(reader));

						// loop until we find a start element that is not part
						// of this array
						boolean loopDone1 = false;
						while (!loopDone1)
						{
							// We should be at the end element, but make sure
							while (!reader.isEndElement())
								reader.next();
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement())
							{
								// two continuous end elements means we are
								// exiting the xml structure
								loopDone1 = true;
							}
							else
							{
								if (new javax.xml.namespace.QName(
										"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "deliveryStatus")
										.equals(reader.getName()))
								{
									list1.add(DeliveryInformation.Factory.parse(reader));

								}
								else
								{
									loopDone1 = true;
								}
							}
						}
						// call the converter utility to convert and set the
						// array

						object.setDeliveryStatus((DeliveryInformation[]) org.apache.axis2.databinding.utils.ConverterUtil
								.convertToArray(DeliveryInformation.class, list1));

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class PolicyExceptionE implements org.apache.axis2.databinding.ADBBean
	{

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "PolicyException", "ns1");

		/**
		 * field for PolicyException
		 */

		protected PolicyException localPolicyException;

		/**
		 * Auto generated getter method
		 * 
		 * @return PolicyException
		 */
		public PolicyException getPolicyException()
		{
			return localPolicyException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            PolicyException
		 */
		public void setPolicyException(PolicyException param)
		{

			this.localPolicyException = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
			return factory.createOMElement(dataSource, MY_QNAME);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it

			if (localPolicyException == null) { throw new org.apache.axis2.databinding.ADBException(
					"PolicyException cannot be null!"); }
			localPolicyException.serialize(MY_QNAME, xmlWriter);

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/common/v2_1")) { return "ns1"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it
			return localPolicyException.getPullParser(MY_QNAME);

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static PolicyExceptionE parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				PolicyExceptionE object = new PolicyExceptionE();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					while (!reader.isEndElement())
					{
						if (reader.isStartElement())
						{

							if (reader.isStartElement()
									&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_1",
											"PolicyException").equals(reader.getName()))
							{

								object.setPolicyException(PolicyException.Factory.parse(reader));

							} // End of if for expected property start element

							else
							{
								// A start element we are not expecting
								// indicates an invalid parameter was passed
								throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
										+ reader.getName());
							}

						}
						else
						{
							reader.next();
						}
					} // end of while loop

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class SendPushMessageResponseE implements org.apache.axis2.databinding.ADBBean
	{

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "sendPushMessageResponse", "ns2");

		/**
		 * field for SendPushMessageResponse
		 */

		protected SendPushMessageResponse localSendPushMessageResponse;

		/**
		 * Auto generated getter method
		 * 
		 * @return SendPushMessageResponse
		 */
		public SendPushMessageResponse getSendPushMessageResponse()
		{
			return localSendPushMessageResponse;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            SendPushMessageResponse
		 */
		public void setSendPushMessageResponse(SendPushMessageResponse param)
		{

			this.localSendPushMessageResponse = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
			return factory.createOMElement(dataSource, MY_QNAME);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it

			if (localSendPushMessageResponse == null) { throw new org.apache.axis2.databinding.ADBException(
					"sendPushMessageResponse cannot be null!"); }
			localSendPushMessageResponse.serialize(MY_QNAME, xmlWriter);

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) { return "ns2"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it
			return localSendPushMessageResponse.getPullParser(MY_QNAME);

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static SendPushMessageResponseE parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				SendPushMessageResponseE object = new SendPushMessageResponseE();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					while (!reader.isEndElement())
					{
						if (reader.isStartElement())
						{

							if (reader.isStartElement()
									&& new javax.xml.namespace.QName(
											"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
											"sendPushMessageResponse").equals(reader.getName()))
							{

								object.setSendPushMessageResponse(SendPushMessageResponse.Factory.parse(reader));

							} // End of if for expected property start element

							else
							{
								// A start element we are not expecting
								// indicates an invalid parameter was passed
								throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
										+ reader.getName());
							}

						}
						else
						{
							reader.next();
						}
					} // end of while loop

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class MessagePriority implements org.apache.axis2.databinding.ADBBean
	{

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/wap_push/v1_0", "MessagePriority", "ns3");

		/**
		 * field for MessagePriority
		 */

		protected java.lang.String localMessagePriority;

		private static java.util.HashMap _table_ = new java.util.HashMap();

		// Constructor

		protected MessagePriority(java.lang.String value, boolean isRegisterValue)
		{
			localMessagePriority = value;
			if (isRegisterValue)
			{

				_table_.put(localMessagePriority, this);

			}

		}

		public static final java.lang.String _Default = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("Default");

		public static final java.lang.String _Low = org.apache.axis2.databinding.utils.ConverterUtil.convertToString("Low");

		public static final java.lang.String _Normal = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("Normal");

		public static final java.lang.String _High = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("High");

		public static final MessagePriority Default = new MessagePriority(_Default, true);

		public static final MessagePriority Low = new MessagePriority(_Low, true);

		public static final MessagePriority Normal = new MessagePriority(_Normal, true);

		public static final MessagePriority High = new MessagePriority(_High, true);

		public java.lang.String getValue()
		{
			return localMessagePriority;
		}

		public boolean equals(java.lang.Object obj)
		{
			return (obj == this);
		}
		public int hashCode()
		{
			return toString().hashCode();
		}
		public java.lang.String toString()
		{

			return localMessagePriority.toString();

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
			return factory.createOMElement(dataSource, MY_QNAME);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it

			java.lang.String namespace = parentQName.getNamespaceURI();
			java.lang.String _localName = parentQName.getLocalPart();

			writeStartElement(null, namespace, _localName, xmlWriter);

			// add the type details if this is used in a simple type
			if (serializeType)
			{
				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/wap_push/v1_0");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":MessagePriority", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "MessagePriority", xmlWriter);
				}
			}

			if (localMessagePriority == null)
			{

				throw new org.apache.axis2.databinding.ADBException("MessagePriority cannot be null !!");

			}
			else
			{

				xmlWriter.writeCharacters(localMessagePriority);

			}

			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/v1_0")) { return "ns3"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it
			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(MY_QNAME, new java.lang.Object[]
			{org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT,
					org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMessagePriority)}, null);

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			public static MessagePriority fromValue(java.lang.String value) throws java.lang.IllegalArgumentException
			{
				MessagePriority enumeration = (MessagePriority)

				_table_.get(value);

				if ((enumeration == null) && !((value == null) || (value.equals("")))) { throw new java.lang.IllegalArgumentException(); }
				return enumeration;
			}
			public static MessagePriority fromString(java.lang.String value, java.lang.String namespaceURI)
					throws java.lang.IllegalArgumentException
			{
				try
				{

					return fromValue(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(value));

				}
				catch (java.lang.Exception e)
				{
					throw new java.lang.IllegalArgumentException();
				}
			}

			public static MessagePriority fromString(javax.xml.stream.XMLStreamReader xmlStreamReader,
					java.lang.String content)
			{
				if (content.indexOf(":") > -1)
				{
					java.lang.String prefix = content.substring(0, content.indexOf(":"));
					java.lang.String namespaceUri = xmlStreamReader.getNamespaceContext().getNamespaceURI(prefix);
					return MessagePriority.Factory.fromString(content, namespaceUri);
				}
				else
				{
					return MessagePriority.Factory.fromString(content, "");
				}
			}

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static MessagePriority parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				MessagePriority object = null;
				// initialize a hash map to keep values
				java.util.Map attributeMap = new java.util.HashMap();
				java.util.List extraAttributeList = new java.util.ArrayList<org.apache.axiom.om.OMAttribute>();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					while (!reader.isEndElement())
					{
						if (reader.isStartElement() || reader.hasText())
						{

							nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
							if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
									"The element: " + "MessagePriority" + "  cannot be null"); }

							java.lang.String content = reader.getElementText();

							if (content.indexOf(":") > 0)
							{
								// this seems to be a Qname so find the
								// namespace and send
								prefix = content.substring(0, content.indexOf(":"));
								namespaceuri = reader.getNamespaceURI(prefix);
								object = MessagePriority.Factory.fromString(content, namespaceuri);
							}
							else
							{
								// this seems to be not a qname send and empty
								// namespace incase of it is
								// check is done in fromString method
								object = MessagePriority.Factory.fromString(content, "");
							}

						}
						else
						{
							reader.next();
						}
					} // end of while loop

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class ExtensionMapper
	{

		public static java.lang.Object getTypeObject(java.lang.String namespaceURI, java.lang.String typeName,
				javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
		{

			if ("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local".equals(namespaceURI)
					&& "getPushMessageDeliveryStatus".equals(typeName)) {

			return GetPushMessageDeliveryStatus.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/common/v2_1".equals(namespaceURI)
					&& "ServiceException".equals(typeName)) {

			return ServiceException.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/wap_push/v1_0".equals(namespaceURI)
					&& "MessagePriority".equals(typeName)) {

			return MessagePriority.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/wap_push/v1_0".equals(namespaceURI)
					&& "DeliveryInformation".equals(typeName)) {

			return DeliveryInformation.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/common/v2_1".equals(namespaceURI) && "PolicyException".equals(typeName)) {

			return PolicyException.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local".equals(namespaceURI)
					&& "sendPushMessageResponse".equals(typeName)) {

			return SendPushMessageResponse.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/common/v2_1".equals(namespaceURI) && "SimpleReference".equals(typeName)) {

			return SimpleReference.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local".equals(namespaceURI)
					&& "getPushMessageDeliveryStatusResponse".equals(typeName)) {

			return GetPushMessageDeliveryStatusResponse.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local".equals(namespaceURI)
					&& "sendPushMessage".equals(typeName)) {

			return SendPushMessage.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/common/v2_1".equals(namespaceURI)
					&& "ChargingInformation".equals(typeName)) {

			return ChargingInformation.Factory.parse(reader);

			}

			if ("http://www.csapi.org/schema/parlayx/wap_push/v1_0".equals(namespaceURI)
					&& "DeliveryStatus".equals(typeName)) {

			return DeliveryStatus.Factory.parse(reader);

			}

			throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
		}

	}

	public static class PolicyException implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * PolicyException Namespace URI =
		 * http://www.csapi.org/schema/parlayx/common/v2_1 Namespace Prefix =
		 * ns1
		 */

		/**
		 * field for MessageId
		 */

		protected java.lang.String localMessageId;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getMessageId()
		{
			return localMessageId;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            MessageId
		 */
		public void setMessageId(java.lang.String param)
		{

			this.localMessageId = param;

		}

		/**
		 * field for Text
		 */

		protected java.lang.String localText;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getText()
		{
			return localText;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Text
		 */
		public void setText(java.lang.String param)
		{

			this.localText = param;

		}

		/**
		 * field for Variables This was an Array!
		 */

		protected java.lang.String[] localVariables;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localVariablesTracker = false;

		public boolean isVariablesSpecified()
		{
			return localVariablesTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String[]
		 */
		public java.lang.String[] getVariables()
		{
			return localVariables;
		}

		/**
		 * validate the array for Variables
		 */
		protected void validateVariables(java.lang.String[] param)
		{

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Variables
		 */
		public void setVariables(java.lang.String[] param)
		{

			validateVariables(param);

			localVariablesTracker = param != null;

			this.localVariables = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param
		 *            java.lang.String
		 */
		public void addVariables(java.lang.String param)
		{
			if (localVariables == null)
			{
				localVariables = new java.lang.String[]{};
			}

			// update the setting tracker
			localVariablesTracker = true;

			java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localVariables);
			list.add(param);
			this.localVariables = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/common/v2_1");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":PolicyException", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "PolicyException", xmlWriter);
				}

			}

			namespace = "";
			writeStartElement(null, namespace, "messageId", xmlWriter);

			if (localMessageId == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("messageId cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localMessageId);

			}

			xmlWriter.writeEndElement();

			namespace = "";
			writeStartElement(null, namespace, "text", xmlWriter);

			if (localText == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("text cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localText);

			}

			xmlWriter.writeEndElement();
			if (localVariablesTracker)
			{
				if (localVariables != null)
				{
					namespace = "";
					for (int i = 0; i < localVariables.length; i++)
					{

						if (localVariables[i] != null)
						{

							writeStartElement(null, namespace, "variables", xmlWriter);

							xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(localVariables[i]));

							xmlWriter.writeEndElement();

						}
						else
						{

							// we have to do nothing since minOccurs is zero

						}

					}
				}
				else
				{

					throw new org.apache.axis2.databinding.ADBException("variables cannot be null!!");

				}

			}
			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/common/v2_1")) { return "ns1"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			elementList.add(new javax.xml.namespace.QName("", "messageId"));

			if (localMessageId != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMessageId));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("messageId cannot be null!!");
			}

			elementList.add(new javax.xml.namespace.QName("", "text"));

			if (localText != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localText));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("text cannot be null!!");
			}
			if (localVariablesTracker)
			{
				if (localVariables != null)
				{
					for (int i = 0; i < localVariables.length; i++)
					{

						if (localVariables[i] != null)
						{
							elementList.add(new javax.xml.namespace.QName("", "variables"));
							elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(localVariables[i]));
						}
						else
						{

							// have to do nothing

						}

					}
				}
				else
				{

					throw new org.apache.axis2.databinding.ADBException("variables cannot be null!!");

				}

			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static PolicyException parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				PolicyException object = new PolicyException();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"PolicyException".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (PolicyException) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					java.util.ArrayList list3 = new java.util.ArrayList();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "messageId").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "messageId" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setMessageId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "text").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "text" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setText(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "variables").equals(reader.getName()))
					{

						// Process the array and step past its final element's
						// end.
						list3.add(reader.getElementText());

						// loop until we find a start element that is not part
						// of this array
						boolean loopDone3 = false;
						while (!loopDone3)
						{
							// Ensure we are at the EndElement
							while (!reader.isEndElement())
							{
								reader.next();
							}
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement())
							{
								// two continuous end elements means we are
								// exiting the xml structure
								loopDone3 = true;
							}
							else
							{
								if (new javax.xml.namespace.QName("", "variables").equals(reader.getName()))
								{
									list3.add(reader.getElementText());

								}
								else
								{
									loopDone3 = true;
								}
							}
						}
						// call the converter utility to convert and set the
						// array

						object.setVariables((java.lang.String[]) list3.toArray(new java.lang.String[list3.size()]));

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class GetPushMessageDeliveryStatusResponseE implements org.apache.axis2.databinding.ADBBean
	{

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "getPushMessageDeliveryStatusResponse",
				"ns2");

		/**
		 * field for GetPushMessageDeliveryStatusResponse
		 */

		protected GetPushMessageDeliveryStatusResponse localGetPushMessageDeliveryStatusResponse;

		/**
		 * Auto generated getter method
		 * 
		 * @return GetPushMessageDeliveryStatusResponse
		 */
		public GetPushMessageDeliveryStatusResponse getGetPushMessageDeliveryStatusResponse()
		{
			return localGetPushMessageDeliveryStatusResponse;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            GetPushMessageDeliveryStatusResponse
		 */
		public void setGetPushMessageDeliveryStatusResponse(GetPushMessageDeliveryStatusResponse param)
		{

			this.localGetPushMessageDeliveryStatusResponse = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
			return factory.createOMElement(dataSource, MY_QNAME);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it

			if (localGetPushMessageDeliveryStatusResponse == null) { throw new org.apache.axis2.databinding.ADBException(
					"getPushMessageDeliveryStatusResponse cannot be null!"); }
			localGetPushMessageDeliveryStatusResponse.serialize(MY_QNAME, xmlWriter);

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) { return "ns2"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it
			return localGetPushMessageDeliveryStatusResponse.getPullParser(MY_QNAME);

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static GetPushMessageDeliveryStatusResponseE parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception
			{
				GetPushMessageDeliveryStatusResponseE object = new GetPushMessageDeliveryStatusResponseE();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					while (!reader.isEndElement())
					{
						if (reader.isStartElement())
						{

							if (reader.isStartElement()
									&& new javax.xml.namespace.QName(
											"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
											"getPushMessageDeliveryStatusResponse").equals(reader.getName()))
							{

								object.setGetPushMessageDeliveryStatusResponse(GetPushMessageDeliveryStatusResponse.Factory
										.parse(reader));

							} // End of if for expected property start element

							else
							{
								// A start element we are not expecting
								// indicates an invalid parameter was passed
								throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
										+ reader.getName());
							}

						}
						else
						{
							reader.next();
						}
					} // end of while loop

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class ServiceExceptionE implements org.apache.axis2.databinding.ADBBean
	{

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/common/v2_1", "ServiceException", "ns1");

		/**
		 * field for ServiceException
		 */

		protected ServiceException localServiceException;

		/**
		 * Auto generated getter method
		 * 
		 * @return ServiceException
		 */
		public ServiceException getServiceException()
		{
			return localServiceException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            ServiceException
		 */
		public void setServiceException(ServiceException param)
		{

			this.localServiceException = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
			return factory.createOMElement(dataSource, MY_QNAME);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it

			if (localServiceException == null) { throw new org.apache.axis2.databinding.ADBException(
					"ServiceException cannot be null!"); }
			localServiceException.serialize(MY_QNAME, xmlWriter);

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/common/v2_1")) { return "ns1"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it
			return localServiceException.getPullParser(MY_QNAME);

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static ServiceExceptionE parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				ServiceExceptionE object = new ServiceExceptionE();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					while (!reader.isEndElement())
					{
						if (reader.isStartElement())
						{

							if (reader.isStartElement()
									&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/common/v2_1",
											"ServiceException").equals(reader.getName()))
							{

								object.setServiceException(ServiceException.Factory.parse(reader));

							} // End of if for expected property start element

							else
							{
								// A start element we are not expecting
								// indicates an invalid parameter was passed
								throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
										+ reader.getName());
							}

						}
						else
						{
							reader.next();
						}
					} // end of while loop

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class SendPushMessageResponse implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * sendPushMessageResponse Namespace URI =
		 * http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local
		 * Namespace Prefix = ns2
		 */

		/**
		 * field for RequestIdentifier
		 */

		protected java.lang.String localRequestIdentifier;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getRequestIdentifier()
		{
			return localRequestIdentifier;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            RequestIdentifier
		 */
		public void setRequestIdentifier(java.lang.String param)
		{

			this.localRequestIdentifier = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":sendPushMessageResponse", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "sendPushMessageResponse",
							xmlWriter);
				}

			}

			namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
			writeStartElement(null, namespace, "requestIdentifier", xmlWriter);

			if (localRequestIdentifier == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("requestIdentifier cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localRequestIdentifier);

			}

			xmlWriter.writeEndElement();

			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) { return "ns2"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			elementList.add(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
					"requestIdentifier"));

			if (localRequestIdentifier != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localRequestIdentifier));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("requestIdentifier cannot be null!!");
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static SendPushMessageResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				SendPushMessageResponse object = new SendPushMessageResponse();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"sendPushMessageResponse".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (SendPushMessageResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"requestIdentifier").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "requestIdentifier" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setRequestIdentifier(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class SendPushMessage implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * sendPushMessage Namespace URI =
		 * http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local
		 * Namespace Prefix = ns2
		 */

		/**
		 * field for Addresses This was an Array!
		 */

		protected org.apache.axis2.databinding.types.URI[] localAddresses;

		/**
		 * Auto generated getter method
		 * 
		 * @return org.apache.axis2.databinding.types.URI[]
		 */
		public org.apache.axis2.databinding.types.URI[] getAddresses()
		{
			return localAddresses;
		}

		/**
		 * validate the array for Addresses
		 */
		protected void validateAddresses(org.apache.axis2.databinding.types.URI[] param)
		{

			if ((param != null) && (param.length < 1)) { throw new java.lang.RuntimeException(); }

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Addresses
		 */
		public void setAddresses(org.apache.axis2.databinding.types.URI[] param)
		{

			validateAddresses(param);

			this.localAddresses = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param
		 *            org.apache.axis2.databinding.types.URI
		 */
		public void addAddresses(org.apache.axis2.databinding.types.URI param)
		{
			if (localAddresses == null)
			{
				localAddresses = new org.apache.axis2.databinding.types.URI[]{};
			}

			java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localAddresses);
			list.add(param);
			this.localAddresses = (org.apache.axis2.databinding.types.URI[]) list
					.toArray(new org.apache.axis2.databinding.types.URI[list.size()]);

		}

		/**
		 * field for TargetURL
		 */

		protected org.apache.axis2.databinding.types.URI localTargetURL;

		/**
		 * Auto generated getter method
		 * 
		 * @return org.apache.axis2.databinding.types.URI
		 */
		public org.apache.axis2.databinding.types.URI getTargetURL()
		{
			return localTargetURL;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            TargetURL
		 */
		public void setTargetURL(org.apache.axis2.databinding.types.URI param)
		{

			this.localTargetURL = param;

		}

		/**
		 * field for SenderAddress
		 */

		protected java.lang.String localSenderAddress;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localSenderAddressTracker = false;

		public boolean isSenderAddressSpecified()
		{
			return localSenderAddressTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSenderAddress()
		{
			return localSenderAddress;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            SenderAddress
		 */
		public void setSenderAddress(java.lang.String param)
		{
			localSenderAddressTracker = param != null;

			this.localSenderAddress = param;

		}

		/**
		 * field for Subject
		 */

		protected java.lang.String localSubject;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localSubjectTracker = false;

		public boolean isSubjectSpecified()
		{
			return localSubjectTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSubject()
		{
			return localSubject;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Subject
		 */
		public void setSubject(java.lang.String param)
		{
			localSubjectTracker = param != null;

			this.localSubject = param;

		}

		/**
		 * field for Priority
		 */

		protected MessagePriority localPriority;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localPriorityTracker = false;

		public boolean isPrioritySpecified()
		{
			return localPriorityTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return MessagePriority
		 */
		public MessagePriority getPriority()
		{
			return localPriority;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Priority
		 */
		public void setPriority(MessagePriority param)
		{
			localPriorityTracker = param != null;

			this.localPriority = param;

		}

		/**
		 * field for Charging
		 */

		protected ChargingInformation localCharging;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localChargingTracker = false;

		public boolean isChargingSpecified()
		{
			return localChargingTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return ChargingInformation
		 */
		public ChargingInformation getCharging()
		{
			return localCharging;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Charging
		 */
		public void setCharging(ChargingInformation param)
		{
			localChargingTracker = param != null;

			this.localCharging = param;

		}

		/**
		 * field for ReceiptRequest
		 */

		protected SimpleReference localReceiptRequest;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localReceiptRequestTracker = false;

		public boolean isReceiptRequestSpecified()
		{
			return localReceiptRequestTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return SimpleReference
		 */
		public SimpleReference getReceiptRequest()
		{
			return localReceiptRequest;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            ReceiptRequest
		 */
		public void setReceiptRequest(SimpleReference param)
		{
			localReceiptRequestTracker = param != null;

			this.localReceiptRequest = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":sendPushMessage", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "sendPushMessage", xmlWriter);
				}

			}

			if (localAddresses != null)
			{
				namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
				for (int i = 0; i < localAddresses.length; i++)
				{

					if (localAddresses[i] != null)
					{

						writeStartElement(null, namespace, "addresses", xmlWriter);

						xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(localAddresses[i]));

						xmlWriter.writeEndElement();

					}
					else
					{

						throw new org.apache.axis2.databinding.ADBException("addresses cannot be null!!");

					}

				}
			}
			else
			{

				throw new org.apache.axis2.databinding.ADBException("addresses cannot be null!!");

			}

			namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
			writeStartElement(null, namespace, "targetURL", xmlWriter);

			if (localTargetURL == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("targetURL cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTargetURL));

			}

			xmlWriter.writeEndElement();
			if (localSenderAddressTracker)
			{
				namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
				writeStartElement(null, namespace, "senderAddress", xmlWriter);

				if (localSenderAddress == null)
				{
					// write the nil attribute

					throw new org.apache.axis2.databinding.ADBException("senderAddress cannot be null!!");

				}
				else
				{

					xmlWriter.writeCharacters(localSenderAddress);

				}

				xmlWriter.writeEndElement();
			}
			if (localSubjectTracker)
			{
				namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
				writeStartElement(null, namespace, "subject", xmlWriter);

				if (localSubject == null)
				{
					// write the nil attribute

					throw new org.apache.axis2.databinding.ADBException("subject cannot be null!!");

				}
				else
				{

					xmlWriter.writeCharacters(localSubject);

				}

				xmlWriter.writeEndElement();
			}
			if (localPriorityTracker)
			{
				if (localPriority == null) { throw new org.apache.axis2.databinding.ADBException("priority cannot be null!!"); }
				localPriority.serialize(new javax.xml.namespace.QName(
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "priority"), xmlWriter);
			}
			if (localChargingTracker)
			{
				if (localCharging == null) { throw new org.apache.axis2.databinding.ADBException("charging cannot be null!!"); }
				localCharging.serialize(new javax.xml.namespace.QName(
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "charging"), xmlWriter);
			}
			if (localReceiptRequestTracker)
			{
				if (localReceiptRequest == null) { throw new org.apache.axis2.databinding.ADBException(
						"receiptRequest cannot be null!!"); }
				localReceiptRequest.serialize(new javax.xml.namespace.QName(
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "receiptRequest"), xmlWriter);
			}
			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) { return "ns2"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			if (localAddresses != null)
			{
				for (int i = 0; i < localAddresses.length; i++)
				{

					if (localAddresses[i] != null)
					{
						elementList.add(new javax.xml.namespace.QName(
								"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "addresses"));
						elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAddresses[i]));
					}
					else
					{

						throw new org.apache.axis2.databinding.ADBException("addresses cannot be null!!");

					}

				}
			}
			else
			{

				throw new org.apache.axis2.databinding.ADBException("addresses cannot be null!!");

			}

			elementList.add(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
					"targetURL"));

			if (localTargetURL != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTargetURL));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("targetURL cannot be null!!");
			}
			if (localSenderAddressTracker)
			{
				elementList.add(new javax.xml.namespace.QName(
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "senderAddress"));

				if (localSenderAddress != null)
				{
					elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSenderAddress));
				}
				else
				{
					throw new org.apache.axis2.databinding.ADBException("senderAddress cannot be null!!");
				}
			}
			if (localSubjectTracker)
			{
				elementList.add(new javax.xml.namespace.QName(
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "subject"));

				if (localSubject != null)
				{
					elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSubject));
				}
				else
				{
					throw new org.apache.axis2.databinding.ADBException("subject cannot be null!!");
				}
			}
			if (localPriorityTracker)
			{
				elementList.add(new javax.xml.namespace.QName(
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "priority"));

				if (localPriority == null) { throw new org.apache.axis2.databinding.ADBException("priority cannot be null!!"); }
				elementList.add(localPriority);
			}
			if (localChargingTracker)
			{
				elementList.add(new javax.xml.namespace.QName(
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "charging"));

				if (localCharging == null) { throw new org.apache.axis2.databinding.ADBException("charging cannot be null!!"); }
				elementList.add(localCharging);
			}
			if (localReceiptRequestTracker)
			{
				elementList.add(new javax.xml.namespace.QName(
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "receiptRequest"));

				if (localReceiptRequest == null) { throw new org.apache.axis2.databinding.ADBException(
						"receiptRequest cannot be null!!"); }
				elementList.add(localReceiptRequest);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static SendPushMessage parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				SendPushMessage object = new SendPushMessage();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"sendPushMessage".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (SendPushMessage) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					java.util.ArrayList list1 = new java.util.ArrayList();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"addresses").equals(reader.getName()))
					{

						// Process the array and step past its final element's
						// end.
						list1.add(reader.getElementText());

						// loop until we find a start element that is not part
						// of this array
						boolean loopDone1 = false;
						while (!loopDone1)
						{
							// Ensure we are at the EndElement
							while (!reader.isEndElement())
							{
								reader.next();
							}
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement())
							{
								// two continuous end elements means we are
								// exiting the xml structure
								loopDone1 = true;
							}
							else
							{
								if (new javax.xml.namespace.QName(
										"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local", "addresses")
										.equals(reader.getName()))
								{
									list1.add(reader.getElementText());

								}
								else
								{
									loopDone1 = true;
								}
							}
						}
						// call the converter utility to convert and set the
						// array

						object.setAddresses((org.apache.axis2.databinding.types.URI[]) org.apache.axis2.databinding.utils.ConverterUtil
								.convertToArray(org.apache.axis2.databinding.types.URI.class, list1));

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"targetURL").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "targetURL" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setTargetURL(org.apache.axis2.databinding.utils.ConverterUtil.convertToAnyURI(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"senderAddress").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "senderAddress" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setSenderAddress(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"subject").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "subject" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setSubject(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"priority").equals(reader.getName()))
					{

						object.setPriority(MessagePriority.Factory.parse(reader));

						reader.next();

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"charging").equals(reader.getName()))
					{

						object.setCharging(ChargingInformation.Factory.parse(reader));

						reader.next();

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"receiptRequest").equals(reader.getName()))
					{

						object.setReceiptRequest(SimpleReference.Factory.parse(reader));

						reader.next();

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class SimpleReference implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * SimpleReference Namespace URI =
		 * http://www.csapi.org/schema/parlayx/common/v2_1 Namespace Prefix =
		 * ns1
		 */

		/**
		 * field for Endpoint
		 */

		protected org.apache.axis2.databinding.types.URI localEndpoint;

		/**
		 * Auto generated getter method
		 * 
		 * @return org.apache.axis2.databinding.types.URI
		 */
		public org.apache.axis2.databinding.types.URI getEndpoint()
		{
			return localEndpoint;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Endpoint
		 */
		public void setEndpoint(org.apache.axis2.databinding.types.URI param)
		{

			this.localEndpoint = param;

		}

		/**
		 * field for InterfaceName
		 */

		protected java.lang.String localInterfaceName;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getInterfaceName()
		{
			return localInterfaceName;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            InterfaceName
		 */
		public void setInterfaceName(java.lang.String param)
		{

			this.localInterfaceName = param;

		}

		/**
		 * field for Correlator
		 */

		protected java.lang.String localCorrelator;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getCorrelator()
		{
			return localCorrelator;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Correlator
		 */
		public void setCorrelator(java.lang.String param)
		{

			this.localCorrelator = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/common/v2_1");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":SimpleReference", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "SimpleReference", xmlWriter);
				}

			}

			namespace = "";
			writeStartElement(null, namespace, "endpoint", xmlWriter);

			if (localEndpoint == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("endpoint cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEndpoint));

			}

			xmlWriter.writeEndElement();

			namespace = "";
			writeStartElement(null, namespace, "interfaceName", xmlWriter);

			if (localInterfaceName == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("interfaceName cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localInterfaceName);

			}

			xmlWriter.writeEndElement();

			namespace = "";
			writeStartElement(null, namespace, "correlator", xmlWriter);

			if (localCorrelator == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("correlator cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localCorrelator);

			}

			xmlWriter.writeEndElement();

			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/common/v2_1")) { return "ns1"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			elementList.add(new javax.xml.namespace.QName("", "endpoint"));

			if (localEndpoint != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEndpoint));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("endpoint cannot be null!!");
			}

			elementList.add(new javax.xml.namespace.QName("", "interfaceName"));

			if (localInterfaceName != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInterfaceName));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("interfaceName cannot be null!!");
			}

			elementList.add(new javax.xml.namespace.QName("", "correlator"));

			if (localCorrelator != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCorrelator));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("correlator cannot be null!!");
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static SimpleReference parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				SimpleReference object = new SimpleReference();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"SimpleReference".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (SimpleReference) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "endpoint").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "endpoint" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setEndpoint(org.apache.axis2.databinding.utils.ConverterUtil.convertToAnyURI(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("", "interfaceName").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "interfaceName" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setInterfaceName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "correlator").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "correlator" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setCorrelator(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class GetPushMessageDeliveryStatus implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * getPushMessageDeliveryStatus Namespace URI =
		 * http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local
		 * Namespace Prefix = ns2
		 */

		/**
		 * field for RequestIdentifier
		 */

		protected java.lang.String localRequestIdentifier;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getRequestIdentifier()
		{
			return localRequestIdentifier;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            RequestIdentifier
		 */
		public void setRequestIdentifier(java.lang.String param)
		{

			this.localRequestIdentifier = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":getPushMessageDeliveryStatus", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							"getPushMessageDeliveryStatus", xmlWriter);
				}

			}

			namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
			writeStartElement(null, namespace, "requestIdentifier", xmlWriter);

			if (localRequestIdentifier == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("requestIdentifier cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localRequestIdentifier);

			}

			xmlWriter.writeEndElement();

			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) { return "ns2"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			elementList.add(new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
					"requestIdentifier"));

			if (localRequestIdentifier != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localRequestIdentifier));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("requestIdentifier cannot be null!!");
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static GetPushMessageDeliveryStatus parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception
			{
				GetPushMessageDeliveryStatus object = new GetPushMessageDeliveryStatus();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"getPushMessageDeliveryStatus".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (GetPushMessageDeliveryStatus) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"requestIdentifier").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "requestIdentifier" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setRequestIdentifier(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class ChargingInformation implements org.apache.axis2.databinding.ADBBean
	{
		/*
		 * This type was generated from the piece of schema that had name =
		 * ChargingInformation Namespace URI =
		 * http://www.csapi.org/schema/parlayx/common/v2_1 Namespace Prefix =
		 * ns1
		 */

		/**
		 * field for Description
		 */

		protected java.lang.String localDescription;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getDescription()
		{
			return localDescription;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Description
		 */
		public void setDescription(java.lang.String param)
		{

			this.localDescription = param;

		}

		/**
		 * field for Currency
		 */

		protected java.lang.String localCurrency;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localCurrencyTracker = false;

		public boolean isCurrencySpecified()
		{
			return localCurrencyTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getCurrency()
		{
			return localCurrency;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Currency
		 */
		public void setCurrency(java.lang.String param)
		{
			localCurrencyTracker = param != null;

			this.localCurrency = param;

		}

		/**
		 * field for Amount
		 */

		protected java.math.BigDecimal localAmount;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localAmountTracker = false;

		public boolean isAmountSpecified()
		{
			return localAmountTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.math.BigDecimal
		 */
		public java.math.BigDecimal getAmount()
		{
			return localAmount;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Amount
		 */
		public void setAmount(java.math.BigDecimal param)
		{
			localAmountTracker = param != null;

			this.localAmount = param;

		}

		/**
		 * field for Code
		 */

		protected java.lang.String localCode;

		/*
		 * This tracker boolean wil be used to detect whether the user called
		 * the set method for this attribute. It will be used to determine
		 * whether to include this field in the serialized XML
		 */
		protected boolean localCodeTracker = false;

		public boolean isCodeSpecified()
		{
			return localCodeTracker;
		}

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getCode()
		{
			return localCode;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param
		 *            Code
		 */
		public void setCode(java.lang.String param)
		{
			localCodeTracker = param != null;

			this.localCode = param;

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
			return factory.createOMElement(dataSource, parentQName);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();
			writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

			if (serializeType)
			{

				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/common/v2_1");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":ChargingInformation", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ChargingInformation",
							xmlWriter);
				}

			}

			namespace = "";
			writeStartElement(null, namespace, "description", xmlWriter);

			if (localDescription == null)
			{
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException("description cannot be null!!");

			}
			else
			{

				xmlWriter.writeCharacters(localDescription);

			}

			xmlWriter.writeEndElement();
			if (localCurrencyTracker)
			{
				namespace = "";
				writeStartElement(null, namespace, "currency", xmlWriter);

				if (localCurrency == null)
				{
					// write the nil attribute

					throw new org.apache.axis2.databinding.ADBException("currency cannot be null!!");

				}
				else
				{

					xmlWriter.writeCharacters(localCurrency);

				}

				xmlWriter.writeEndElement();
			}
			if (localAmountTracker)
			{
				namespace = "";
				writeStartElement(null, namespace, "amount", xmlWriter);

				if (localAmount == null)
				{
					// write the nil attribute

					throw new org.apache.axis2.databinding.ADBException("amount cannot be null!!");

				}
				else
				{

					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAmount));

				}

				xmlWriter.writeEndElement();
			}
			if (localCodeTracker)
			{
				namespace = "";
				writeStartElement(null, namespace, "code", xmlWriter);

				if (localCode == null)
				{
					// write the nil attribute

					throw new org.apache.axis2.databinding.ADBException("code cannot be null!!");

				}
				else
				{

					xmlWriter.writeCharacters(localCode);

				}

				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/common/v2_1")) { return "ns1"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			java.util.ArrayList elementList = new java.util.ArrayList();
			java.util.ArrayList attribList = new java.util.ArrayList();

			elementList.add(new javax.xml.namespace.QName("", "description"));

			if (localDescription != null)
			{
				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDescription));
			}
			else
			{
				throw new org.apache.axis2.databinding.ADBException("description cannot be null!!");
			}
			if (localCurrencyTracker)
			{
				elementList.add(new javax.xml.namespace.QName("", "currency"));

				if (localCurrency != null)
				{
					elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCurrency));
				}
				else
				{
					throw new org.apache.axis2.databinding.ADBException("currency cannot be null!!");
				}
			}
			if (localAmountTracker)
			{
				elementList.add(new javax.xml.namespace.QName("", "amount"));

				if (localAmount != null)
				{
					elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAmount));
				}
				else
				{
					throw new org.apache.axis2.databinding.ADBException("amount cannot be null!!");
				}
			}
			if (localCodeTracker)
			{
				elementList.add(new javax.xml.namespace.QName("", "code"));

				if (localCode != null)
				{
					elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCode));
				}
				else
				{
					throw new org.apache.axis2.databinding.ADBException("code cannot be null!!");
				}
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static ChargingInformation parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				ChargingInformation object = new ChargingInformation();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null)
					{
						java.lang.String fullTypeName = reader.getAttributeValue(
								"http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null)
						{
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1)
							{
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"ChargingInformation".equals(type))
							{
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (ChargingInformation) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "description").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "description" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setDescription(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{
						// A start element we are not expecting indicates an
						// invalid parameter was passed
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "currency").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "currency" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setCurrency(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "amount").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "amount" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setAmount(org.apache.axis2.databinding.utils.ConverterUtil.convertToDecimal(content));

						reader.next();

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("", "code").equals(reader.getName()))
					{

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "code" + "  cannot be null"); }

						java.lang.String content = reader.getElementText();

						object.setCode(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						reader.next();

					} // End of if for expected property start element

					else
					{

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
						throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class DeliveryStatus implements org.apache.axis2.databinding.ADBBean
	{

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/wap_push/v1_0", "DeliveryStatus", "ns3");

		/**
		 * field for DeliveryStatus
		 */

		protected java.lang.String localDeliveryStatus;

		private static java.util.HashMap _table_ = new java.util.HashMap();

		// Constructor

		protected DeliveryStatus(java.lang.String value, boolean isRegisterValue)
		{
			localDeliveryStatus = value;
			if (isRegisterValue)
			{

				_table_.put(localDeliveryStatus, this);

			}

		}

		public static final java.lang.String _DeliveredToNetwork = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("DeliveredToNetwork");

		public static final java.lang.String _DeliveryUncertain = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("DeliveryUncertain");

		public static final java.lang.String _DeliveryImpossible = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("DeliveryImpossible");

		public static final java.lang.String _MessageWaiting = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("MessageWaiting");

		public static final java.lang.String _DeliveredToTerminal = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("DeliveredToTerminal");

		public static final java.lang.String _DeliveryNotificationNotSupported = org.apache.axis2.databinding.utils.ConverterUtil
				.convertToString("DeliveryNotificationNotSupported");

		public static final DeliveryStatus DeliveredToNetwork = new DeliveryStatus(_DeliveredToNetwork, true);

		public static final DeliveryStatus DeliveryUncertain = new DeliveryStatus(_DeliveryUncertain, true);

		public static final DeliveryStatus DeliveryImpossible = new DeliveryStatus(_DeliveryImpossible, true);

		public static final DeliveryStatus MessageWaiting = new DeliveryStatus(_MessageWaiting, true);

		public static final DeliveryStatus DeliveredToTerminal = new DeliveryStatus(_DeliveredToTerminal, true);

		public static final DeliveryStatus DeliveryNotificationNotSupported = new DeliveryStatus(
				_DeliveryNotificationNotSupported, true);

		public java.lang.String getValue()
		{
			return localDeliveryStatus;
		}

		public boolean equals(java.lang.Object obj)
		{
			return (obj == this);
		}
		public int hashCode()
		{
			return toString().hashCode();
		}
		public java.lang.String toString()
		{

			return localDeliveryStatus.toString();

		}

		/**
		 * 
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException
		{

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
			return factory.createOMElement(dataSource, MY_QNAME);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{
			serialize(parentQName, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, javax.xml.stream.XMLStreamWriter xmlWriter,
				boolean serializeType) throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it

			java.lang.String namespace = parentQName.getNamespaceURI();
			java.lang.String _localName = parentQName.getLocalPart();

			writeStartElement(null, namespace, _localName, xmlWriter);

			// add the type details if this is used in a simple type
			if (serializeType)
			{
				java.lang.String namespacePrefix = registerPrefix(xmlWriter,
						"http://www.csapi.org/schema/parlayx/wap_push/v1_0");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0))
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
							+ ":DeliveryStatus", xmlWriter);
				}
				else
				{
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "DeliveryStatus", xmlWriter);
				}
			}

			if (localDeliveryStatus == null)
			{

				throw new org.apache.axis2.databinding.ADBException("DeliveryStatus cannot be null !!");

			}
			else
			{

				xmlWriter.writeCharacters(localDeliveryStatus);

			}

			xmlWriter.writeEndElement();

		}

		private static java.lang.String generatePrefix(java.lang.String namespace)
		{
			if (namespace.equals("http://www.csapi.org/schema/parlayx/wap_push/v1_0")) { return "ns3"; }
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * Utility method to write an element start tag.
		 */
		private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
			if (writerPrefix != null)
			{
				xmlWriter.writeStartElement(namespace, localPart);
			}
			else
			{
				if (namespace.length() == 0)
				{
					prefix = "";
				}
				else if (prefix == null)
				{
					prefix = generatePrefix(namespace);
				}

				xmlWriter.writeStartElement(prefix, localPart, namespace);
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			if (xmlWriter.getPrefix(namespace) == null)
			{
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
				javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException
		{
			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attValue);
			}
		}

		/**
		 * Util method to write an attribute without the ns prefix
		 */
		private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
				javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			java.lang.String attributeNamespace = qname.getNamespaceURI();
			java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
			if (attributePrefix == null)
			{
				attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
			}
			java.lang.String attributeValue;
			if (attributePrefix.trim().length() > 0)
			{
				attributeValue = attributePrefix + ":" + qname.getLocalPart();
			}
			else
			{
				attributeValue = qname.getLocalPart();
			}

			if (namespace.equals(""))
			{
				xmlWriter.writeAttribute(attName, attributeValue);
			}
			else
			{
				registerPrefix(xmlWriter, namespace);
				xmlWriter.writeAttribute(namespace, attName, attributeValue);
			}
		}
		/**
		 * method to handle Qnames
		 */

		private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String namespaceURI = qname.getNamespaceURI();
			if (namespaceURI != null)
			{
				java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
				if (prefix == null)
				{
					prefix = generatePrefix(namespaceURI);
					xmlWriter.writeNamespace(prefix, namespaceURI);
					xmlWriter.setPrefix(prefix, namespaceURI);
				}

				if (prefix.trim().length() > 0)
				{
					xmlWriter.writeCharacters(prefix + ":"
							+ org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}
				else
				{
					// i.e this is the default namespace
					xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
				}

			}
			else
			{
				xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
			}
		}

		private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException
		{

			if (qnames != null)
			{
				// we have to store this data until last moment since it is not
				// possible to write any
				// namespace data after writing the charactor data
				java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
				java.lang.String namespaceURI = null;
				java.lang.String prefix = null;

				for (int i = 0; i < qnames.length; i++)
				{
					if (i > 0)
					{
						stringToWrite.append(" ");
					}
					namespaceURI = qnames[i].getNamespaceURI();
					if (namespaceURI != null)
					{
						prefix = xmlWriter.getPrefix(namespaceURI);
						if ((prefix == null) || (prefix.length() == 0))
						{
							prefix = generatePrefix(namespaceURI);
							xmlWriter.writeNamespace(prefix, namespaceURI);
							xmlWriter.setPrefix(prefix, namespaceURI);
						}

						if (prefix.trim().length() > 0)
						{
							stringToWrite.append(prefix).append(":")
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
						else
						{
							stringToWrite
									.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
						}
					}
					else
					{
						stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
					}
				}
				xmlWriter.writeCharacters(stringToWrite.toString());
			}

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException
		{
			java.lang.String prefix = xmlWriter.getPrefix(namespace);
			if (prefix == null)
			{
				prefix = generatePrefix(namespace);
				javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
				while (true)
				{
					java.lang.String uri = nsContext.getNamespaceURI(prefix);
					if (uri == null || uri.length() == 0)
					{
						break;
					}
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}
			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 * 
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException
		{

			// We can safely assume an element has only one type associated with
			// it
			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(MY_QNAME, new java.lang.Object[]
			{org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT,
					org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDeliveryStatus)}, null);

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory
		{

			public static DeliveryStatus fromValue(java.lang.String value) throws java.lang.IllegalArgumentException
			{
				DeliveryStatus enumeration = (DeliveryStatus)

				_table_.get(value);

				if ((enumeration == null) && !((value == null) || (value.equals("")))) { throw new java.lang.IllegalArgumentException(); }
				return enumeration;
			}
			public static DeliveryStatus fromString(java.lang.String value, java.lang.String namespaceURI)
					throws java.lang.IllegalArgumentException
			{
				try
				{

					return fromValue(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(value));

				}
				catch (java.lang.Exception e)
				{
					throw new java.lang.IllegalArgumentException();
				}
			}

			public static DeliveryStatus fromString(javax.xml.stream.XMLStreamReader xmlStreamReader,
					java.lang.String content)
			{
				if (content.indexOf(":") > -1)
				{
					java.lang.String prefix = content.substring(0, content.indexOf(":"));
					java.lang.String namespaceUri = xmlStreamReader.getNamespaceContext().getNamespaceURI(prefix);
					return DeliveryStatus.Factory.fromString(content, namespaceUri);
				}
				else
				{
					return DeliveryStatus.Factory.fromString(content, "");
				}
			}

			/**
			 * static method to create the object Precondition: If this object
			 * is an element, the current or next start element starts this
			 * object and any intervening reader events are ignorable If this
			 * object is not an element, it is a complex type and the reader is
			 * at the event just after the outer start element Postcondition: If
			 * this object is an element, the reader is positioned at its end
			 * element If this object is a complex type, the reader is
			 * positioned at the end element of its outer element
			 */
			public static DeliveryStatus parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception
			{
				DeliveryStatus object = null;
				// initialize a hash map to keep values
				java.util.Map attributeMap = new java.util.HashMap();
				java.util.List extraAttributeList = new java.util.ArrayList<org.apache.axiom.om.OMAttribute>();

				int event;
				java.lang.String nillableValue = null;
				java.lang.String prefix = "";
				java.lang.String namespaceuri = "";
				try
				{

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					// Note all attributes that were handled. Used to differ
					// normal attributes
					// from anyAttributes.
					java.util.Vector handledAttributes = new java.util.Vector();

					while (!reader.isEndElement())
					{
						if (reader.isStartElement() || reader.hasText())
						{

							nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
							if ("true".equals(nillableValue) || "1".equals(nillableValue)) { throw new org.apache.axis2.databinding.ADBException(
									"The element: " + "DeliveryStatus" + "  cannot be null"); }

							java.lang.String content = reader.getElementText();

							if (content.indexOf(":") > 0)
							{
								// this seems to be a Qname so find the
								// namespace and send
								prefix = content.substring(0, content.indexOf(":"));
								namespaceuri = reader.getNamespaceURI(prefix);
								object = DeliveryStatus.Factory.fromString(content, namespaceuri);
							}
							else
							{
								// this seems to be not a qname send and empty
								// namespace incase of it is
								// check is done in fromString method
								object = DeliveryStatus.Factory.fromString(content, "");
							}

						}
						else
						{
							reader.next();
						}
					} // end of while loop

				}
				catch (javax.xml.stream.XMLStreamException e)
				{
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	private org.apache.axiom.om.OMElement toOM(
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusE param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault
	{

		try
		{
			return param
					.getOMElement(
							org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusE.MY_QNAME,
							org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault
	{

		try
		{
			return param
					.getOMElement(
							org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE.MY_QNAME,
							org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.PolicyExceptionE param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault
	{

		try
		{
			return param
					.getOMElement(
							org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.PolicyExceptionE.MY_QNAME,
							org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.ServiceExceptionE param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault
	{

		try
		{
			return param
					.getOMElement(
							org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.ServiceExceptionE.MY_QNAME,
							org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault
	{

		try
		{
			return param
					.getOMElement(
							org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE.MY_QNAME,
							org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault
	{

		try
		{
			return param
					.getOMElement(
							org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE.MY_QNAME,
							org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE param,
			boolean optimizeContent, javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault
	{

		try
		{

			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope
					.getBody()
					.addChild(
							param.getOMElement(
									org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE.MY_QNAME,
									factory));
			return emptyEnvelope;
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	/* methods to provide back word compatibility */

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
			org.apache.axiom.soap.SOAPFactory factory,
			org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusE param,
			boolean optimizeContent, javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault
	{

		try
		{

			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope
					.getBody()
					.addChild(
							param.getOMElement(
									org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusE.MY_QNAME,
									factory));
			return emptyEnvelope;
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	/* methods to provide back word compatibility */

	/**
	 * get the default envelope
	 */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory)
	{
		return factory.getDefaultEnvelope();
	}

	private java.lang.Object fromOM(org.apache.axiom.om.OMElement param, java.lang.Class type, java.util.Map extraNamespaces)
			throws org.apache.axis2.AxisFault
	{

		try
		{

			if (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusE.class
					.equals(type)) {

			return org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusE.Factory
					.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE.class
					.equals(type)) {

			return org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE.Factory
					.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.PolicyExceptionE.class
					.equals(type)) {

			return org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.PolicyExceptionE.Factory
					.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.ServiceExceptionE.class
					.equals(type)) {

			return org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.ServiceExceptionE.Factory
					.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE.class
					.equals(type)) {

			return org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageE.Factory
					.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE.class
					.equals(type)) {

			return org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE.Factory
					.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.PolicyExceptionE.class
					.equals(type)) {

			return org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.PolicyExceptionE.Factory
					.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.ServiceExceptionE.class
					.equals(type)) {

			return org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.ServiceExceptionE.Factory
					.parse(param.getXMLStreamReaderWithoutCaching());

			}

		}
		catch (java.lang.Exception e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
		return null;
	}

}
