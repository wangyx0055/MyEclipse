/**
 * SendPushMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

package org.csapi.www.schema.parlayx.wap_push.send.v1_0.local;

/**
 * SendPushMessage bean class
 */
@SuppressWarnings({ "unchecked", "unused" })
public class SendPushMessage implements org.apache.axis2.databinding.ADBBean {
	/*
	 * This type was generated from the piece of schema that had name =
	 * sendPushMessage Namespace URI =
	 * http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local Namespace
	 * Prefix = ns2
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
	public org.apache.axis2.databinding.types.URI[] getAddresses() {
		return localAddresses;
	}

	/**
	 * validate the array for Addresses
	 */
	protected void validateAddresses(
			org.apache.axis2.databinding.types.URI[] param) {

		if ((param != null) && (param.length < 1)) {
			throw new java.lang.RuntimeException();
		}

	}

	/**
	 * Auto generated setter method
	 * 
	 * @param param
	 *            Addresses
	 */
	public void setAddresses(org.apache.axis2.databinding.types.URI[] param) {

		validateAddresses(param);

		this.localAddresses = param;
	}

	/**
	 * Auto generated add method for the array for convenience
	 * 
	 * @param param
	 *            org.apache.axis2.databinding.types.URI
	 */
	public void addAddresses(org.apache.axis2.databinding.types.URI param) {
		if (localAddresses == null) {
			localAddresses = new org.apache.axis2.databinding.types.URI[] {};
		}

		java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil
				.toList(localAddresses);
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
	public org.apache.axis2.databinding.types.URI getTargetURL() {
		return localTargetURL;
	}

	/**
	 * Auto generated setter method
	 * 
	 * @param param
	 *            TargetURL
	 */
	public void setTargetURL(org.apache.axis2.databinding.types.URI param) {

		this.localTargetURL = param;

	}

	/**
	 * field for SenderAddress
	 */

	protected java.lang.String localSenderAddress;

	/*
	 * This tracker boolean wil be used to detect whether the user called the
	 * set method for this attribute. It will be used to determine whether to
	 * include this field in the serialized XML
	 */
	protected boolean localSenderAddressTracker = false;

	public boolean isSenderAddressSpecified() {
		return localSenderAddressTracker;
	}

	/**
	 * Auto generated getter method
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getSenderAddress() {
		return localSenderAddress;
	}

	/**
	 * Auto generated setter method
	 * 
	 * @param param
	 *            SenderAddress
	 */
	public void setSenderAddress(java.lang.String param) {
		localSenderAddressTracker = param != null;

		this.localSenderAddress = param;

	}

	/**
	 * field for Subject
	 */

	protected java.lang.String localSubject;

	/*
	 * This tracker boolean wil be used to detect whether the user called the
	 * set method for this attribute. It will be used to determine whether to
	 * include this field in the serialized XML
	 */
	protected boolean localSubjectTracker = false;

	public boolean isSubjectSpecified() {
		return localSubjectTracker;
	}

	/**
	 * Auto generated getter method
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getSubject() {
		return localSubject;
	}

	/**
	 * Auto generated setter method
	 * 
	 * @param param
	 *            Subject
	 */
	public void setSubject(java.lang.String param) {
		localSubjectTracker = param != null;

		this.localSubject = param;

	}

	/**
	 * field for Priority
	 */

	protected org.csapi.www.schema.parlayx.wap_push.v1_0.MessagePriority localPriority;

	/*
	 * This tracker boolean wil be used to detect whether the user called the
	 * set method for this attribute. It will be used to determine whether to
	 * include this field in the serialized XML
	 */
	protected boolean localPriorityTracker = false;

	public boolean isPrioritySpecified() {
		return localPriorityTracker;
	}

	/**
	 * Auto generated getter method
	 * 
	 * @return org.csapi.www.schema.parlayx.wap_push.v1_0.MessagePriority
	 */
	public org.csapi.www.schema.parlayx.wap_push.v1_0.MessagePriority getPriority() {
		return localPriority;
	}

	/**
	 * Auto generated setter method
	 * 
	 * @param param
	 *            Priority
	 */
	public void setPriority(
			org.csapi.www.schema.parlayx.wap_push.v1_0.MessagePriority param) {
		localPriorityTracker = param != null;

		this.localPriority = param;

	}

	/**
	 * field for Charging
	 */

	protected org.csapi.www.schema.parlayx.common.v2_1.ChargingInformation localCharging;

	/*
	 * This tracker boolean wil be used to detect whether the user called the
	 * set method for this attribute. It will be used to determine whether to
	 * include this field in the serialized XML
	 */
	protected boolean localChargingTracker = false;

	public boolean isChargingSpecified() {
		return localChargingTracker;
	}

	/**
	 * Auto generated getter method
	 * 
	 * @return org.csapi.www.schema.parlayx.common.v2_1.ChargingInformation
	 */
	public org.csapi.www.schema.parlayx.common.v2_1.ChargingInformation getCharging() {
		return localCharging;
	}

	/**
	 * Auto generated setter method
	 * 
	 * @param param
	 *            Charging
	 */
	public void setCharging(
			org.csapi.www.schema.parlayx.common.v2_1.ChargingInformation param) {
		localChargingTracker = param != null;

		this.localCharging = param;

	}

	/**
	 * field for ReceiptRequest
	 */

	protected org.csapi.www.schema.parlayx.common.v2_1.SimpleReference localReceiptRequest;

	/*
	 * This tracker boolean wil be used to detect whether the user called the
	 * set method for this attribute. It will be used to determine whether to
	 * include this field in the serialized XML
	 */
	protected boolean localReceiptRequestTracker = false;

	public boolean isReceiptRequestSpecified() {
		return localReceiptRequestTracker;
	}

	/**
	 * Auto generated getter method
	 * 
	 * @return org.csapi.www.schema.parlayx.common.v2_1.SimpleReference
	 */
	public org.csapi.www.schema.parlayx.common.v2_1.SimpleReference getReceiptRequest() {
		return localReceiptRequest;
	}

	/**
	 * Auto generated setter method
	 * 
	 * @param param
	 *            ReceiptRequest
	 */
	public void setReceiptRequest(
			org.csapi.www.schema.parlayx.common.v2_1.SimpleReference param) {
		localReceiptRequestTracker = param != null;

		this.localReceiptRequest = param;

	}

	/**
	 * 
	 * @param parentQName
	 * @param factory
	 * @return org.apache.axiom.om.OMElement
	 */
	public org.apache.axiom.om.OMElement getOMElement(
			final javax.xml.namespace.QName parentQName,
			final org.apache.axiom.om.OMFactory factory)
			throws org.apache.axis2.databinding.ADBException {

		org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(
				this, parentQName);
		return factory.createOMElement(dataSource, parentQName);

	}

	public void serialize(final javax.xml.namespace.QName parentQName,
			javax.xml.stream.XMLStreamWriter xmlWriter)
			throws javax.xml.stream.XMLStreamException,
			org.apache.axis2.databinding.ADBException {
		serialize(parentQName, xmlWriter, false);
	}

	public void serialize(final javax.xml.namespace.QName parentQName,
			javax.xml.stream.XMLStreamWriter xmlWriter, boolean serializeType)
			throws javax.xml.stream.XMLStreamException,
			org.apache.axis2.databinding.ADBException {

		java.lang.String prefix = null;
		java.lang.String namespace = null;

		prefix = parentQName.getPrefix();
		namespace = parentQName.getNamespaceURI();
		writeStartElement(prefix, namespace, parentQName.getLocalPart(),
				xmlWriter);

		if (serializeType) {

			java.lang.String namespacePrefix = registerPrefix(xmlWriter,
					"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local");
			if ((namespacePrefix != null)
					&& (namespacePrefix.trim().length() > 0)) {
				writeAttribute("xsi",
						"http://www.w3.org/2001/XMLSchema-instance", "type",
						namespacePrefix + ":sendPushMessage", xmlWriter);
			} else {
				writeAttribute("xsi",
						"http://www.w3.org/2001/XMLSchema-instance", "type",
						"sendPushMessage", xmlWriter);
			}

		}

		if (localAddresses != null) {
			namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
			for (int i = 0; i < localAddresses.length; i++) {

				if (localAddresses[i] != null) {

					writeStartElement(null, namespace, "addresses", xmlWriter);

					xmlWriter
							.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(localAddresses[i]));

					xmlWriter.writeEndElement();

				} else {

					throw new org.apache.axis2.databinding.ADBException(
							"addresses cannot be null!!");

				}

			}
		} else {

			throw new org.apache.axis2.databinding.ADBException(
					"addresses cannot be null!!");

		}

		namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
		writeStartElement(null, namespace, "targetURL", xmlWriter);

		if (localTargetURL == null) {
			// write the nil attribute

			throw new org.apache.axis2.databinding.ADBException(
					"targetURL cannot be null!!");

		} else {

			xmlWriter
					.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(localTargetURL));

		}

		xmlWriter.writeEndElement();
		if (localSenderAddressTracker) {
			namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
			writeStartElement(null, namespace, "senderAddress", xmlWriter);

			if (localSenderAddress == null) {
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException(
						"senderAddress cannot be null!!");

			} else {

				xmlWriter.writeCharacters(localSenderAddress);

			}

			xmlWriter.writeEndElement();
		}
		if (localSubjectTracker) {
			namespace = "http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local";
			writeStartElement(null, namespace, "subject", xmlWriter);

			if (localSubject == null) {
				// write the nil attribute

				throw new org.apache.axis2.databinding.ADBException(
						"subject cannot be null!!");

			} else {

				xmlWriter.writeCharacters(localSubject);

			}

			xmlWriter.writeEndElement();
		}
		if (localPriorityTracker) {
			if (localPriority == null) {
				throw new org.apache.axis2.databinding.ADBException(
						"priority cannot be null!!");
			}
			localPriority
					.serialize(
							new javax.xml.namespace.QName(
									"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"priority"), xmlWriter);
		}
		if (localChargingTracker) {
			if (localCharging == null) {
				throw new org.apache.axis2.databinding.ADBException(
						"charging cannot be null!!");
			}
			localCharging
					.serialize(
							new javax.xml.namespace.QName(
									"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"charging"), xmlWriter);
		}
		if (localReceiptRequestTracker) {
			if (localReceiptRequest == null) {
				throw new org.apache.axis2.databinding.ADBException(
						"receiptRequest cannot be null!!");
			}
			localReceiptRequest
					.serialize(
							new javax.xml.namespace.QName(
									"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"receiptRequest"), xmlWriter);
		}
		xmlWriter.writeEndElement();

	}

	private static java.lang.String generatePrefix(java.lang.String namespace) {
		if (namespace
				.equals("http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local")) {
			return "ns2";
		}
		return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
	}

	/**
	 * Utility method to write an element start tag.
	 */
	private void writeStartElement(java.lang.String prefix,
			java.lang.String namespace, java.lang.String localPart,
			javax.xml.stream.XMLStreamWriter xmlWriter)
			throws javax.xml.stream.XMLStreamException {
		java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
		if (writerPrefix != null) {
			xmlWriter.writeStartElement(namespace, localPart);
		} else {
			if (namespace.length() == 0) {
				prefix = "";
			} else if (prefix == null) {
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
	private void writeAttribute(java.lang.String prefix,
			java.lang.String namespace, java.lang.String attName,
			java.lang.String attValue,
			javax.xml.stream.XMLStreamWriter xmlWriter)
			throws javax.xml.stream.XMLStreamException {
		if (xmlWriter.getPrefix(namespace) == null) {
			xmlWriter.writeNamespace(prefix, namespace);
			xmlWriter.setPrefix(prefix, namespace);
		}
		xmlWriter.writeAttribute(namespace, attName, attValue);
	}

	/**
	 * Util method to write an attribute without the ns prefix
	 */
	private void writeAttribute(java.lang.String namespace,
			java.lang.String attName, java.lang.String attValue,
			javax.xml.stream.XMLStreamWriter xmlWriter)
			throws javax.xml.stream.XMLStreamException {
		if (namespace.equals("")) {
			xmlWriter.writeAttribute(attName, attValue);
		} else {
			registerPrefix(xmlWriter, namespace);
			xmlWriter.writeAttribute(namespace, attName, attValue);
		}
	}

	/**
	 * Util method to write an attribute without the ns prefix
	 */
	private void writeQNameAttribute(java.lang.String namespace,
			java.lang.String attName, javax.xml.namespace.QName qname,
			javax.xml.stream.XMLStreamWriter xmlWriter)
			throws javax.xml.stream.XMLStreamException {

		java.lang.String attributeNamespace = qname.getNamespaceURI();
		java.lang.String attributePrefix = xmlWriter
				.getPrefix(attributeNamespace);
		if (attributePrefix == null) {
			attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
		}
		java.lang.String attributeValue;
		if (attributePrefix.trim().length() > 0) {
			attributeValue = attributePrefix + ":" + qname.getLocalPart();
		} else {
			attributeValue = qname.getLocalPart();
		}

		if (namespace.equals("")) {
			xmlWriter.writeAttribute(attName, attributeValue);
		} else {
			registerPrefix(xmlWriter, namespace);
			xmlWriter.writeAttribute(namespace, attName, attributeValue);
		}
	}

	/**
	 * method to handle Qnames
	 */

	private void writeQName(javax.xml.namespace.QName qname,
			javax.xml.stream.XMLStreamWriter xmlWriter)
			throws javax.xml.stream.XMLStreamException {
		java.lang.String namespaceURI = qname.getNamespaceURI();
		if (namespaceURI != null) {
			java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
			if (prefix == null) {
				prefix = generatePrefix(namespaceURI);
				xmlWriter.writeNamespace(prefix, namespaceURI);
				xmlWriter.setPrefix(prefix, namespaceURI);
			}

			if (prefix.trim().length() > 0) {
				xmlWriter.writeCharacters(prefix
						+ ":"
						+ org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qname));
			} else {
				// i.e this is the default namespace
				xmlWriter
						.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(qname));
			}

		} else {
			xmlWriter
					.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(qname));
		}
	}

	private void writeQNames(javax.xml.namespace.QName[] qnames,
			javax.xml.stream.XMLStreamWriter xmlWriter)
			throws javax.xml.stream.XMLStreamException {

		if (qnames != null) {
			// we have to store this data until last moment since it is not
			// possible to write any
			// namespace data after writing the charactor data
			java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
			java.lang.String namespaceURI = null;
			java.lang.String prefix = null;

			for (int i = 0; i < qnames.length; i++) {
				if (i > 0) {
					stringToWrite.append(" ");
				}
				namespaceURI = qnames[i].getNamespaceURI();
				if (namespaceURI != null) {
					prefix = xmlWriter.getPrefix(namespaceURI);
					if ((prefix == null) || (prefix.length() == 0)) {
						prefix = generatePrefix(namespaceURI);
						xmlWriter.writeNamespace(prefix, namespaceURI);
						xmlWriter.setPrefix(prefix, namespaceURI);
					}

					if (prefix.trim().length() > 0) {
						stringToWrite
								.append(prefix)
								.append(":")
								.append(org.apache.axis2.databinding.utils.ConverterUtil
										.convertToString(qnames[i]));
					} else {
						stringToWrite
								.append(org.apache.axis2.databinding.utils.ConverterUtil
										.convertToString(qnames[i]));
					}
				} else {
					stringToWrite
							.append(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(qnames[i]));
				}
			}
			xmlWriter.writeCharacters(stringToWrite.toString());
		}

	}

	/**
	 * Register a namespace prefix
	 */
	private java.lang.String registerPrefix(
			javax.xml.stream.XMLStreamWriter xmlWriter,
			java.lang.String namespace)
			throws javax.xml.stream.XMLStreamException {
		java.lang.String prefix = xmlWriter.getPrefix(namespace);
		if (prefix == null) {
			prefix = generatePrefix(namespace);
			javax.xml.namespace.NamespaceContext nsContext = xmlWriter
					.getNamespaceContext();
			while (true) {
				java.lang.String uri = nsContext.getNamespaceURI(prefix);
				if (uri == null || uri.length() == 0) {
					break;
				}
				prefix = org.apache.axis2.databinding.utils.BeanUtil
						.getUniquePrefix();
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
	public javax.xml.stream.XMLStreamReader getPullParser(
			javax.xml.namespace.QName qName)
			throws org.apache.axis2.databinding.ADBException {

		java.util.ArrayList elementList = new java.util.ArrayList();
		java.util.ArrayList attribList = new java.util.ArrayList();

		if (localAddresses != null) {
			for (int i = 0; i < localAddresses.length; i++) {

				if (localAddresses[i] != null) {
					elementList
							.add(new javax.xml.namespace.QName(
									"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"addresses"));
					elementList
							.add(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(localAddresses[i]));
				} else {

					throw new org.apache.axis2.databinding.ADBException(
							"addresses cannot be null!!");

				}

			}
		} else {

			throw new org.apache.axis2.databinding.ADBException(
					"addresses cannot be null!!");

		}

		elementList.add(new javax.xml.namespace.QName(
				"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
				"targetURL"));

		if (localTargetURL != null) {
			elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
					.convertToString(localTargetURL));
		} else {
			throw new org.apache.axis2.databinding.ADBException(
					"targetURL cannot be null!!");
		}
		if (localSenderAddressTracker) {
			elementList
					.add(new javax.xml.namespace.QName(
							"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
							"senderAddress"));

			if (localSenderAddress != null) {
				elementList
						.add(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(localSenderAddress));
			} else {
				throw new org.apache.axis2.databinding.ADBException(
						"senderAddress cannot be null!!");
			}
		}
		if (localSubjectTracker) {
			elementList
					.add(new javax.xml.namespace.QName(
							"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
							"subject"));

			if (localSubject != null) {
				elementList
						.add(org.apache.axis2.databinding.utils.ConverterUtil
								.convertToString(localSubject));
			} else {
				throw new org.apache.axis2.databinding.ADBException(
						"subject cannot be null!!");
			}
		}
		if (localPriorityTracker) {
			elementList
					.add(new javax.xml.namespace.QName(
							"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
							"priority"));

			if (localPriority == null) {
				throw new org.apache.axis2.databinding.ADBException(
						"priority cannot be null!!");
			}
			elementList.add(localPriority);
		}
		if (localChargingTracker) {
			elementList
					.add(new javax.xml.namespace.QName(
							"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
							"charging"));

			if (localCharging == null) {
				throw new org.apache.axis2.databinding.ADBException(
						"charging cannot be null!!");
			}
			elementList.add(localCharging);
		}
		if (localReceiptRequestTracker) {
			elementList
					.add(new javax.xml.namespace.QName(
							"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
							"receiptRequest"));

			if (localReceiptRequest == null) {
				throw new org.apache.axis2.databinding.ADBException(
						"receiptRequest cannot be null!!");
			}
			elementList.add(localReceiptRequest);
		}

		return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(
				qName, elementList.toArray(), attribList.toArray());

	}

	/**
	 * Factory class that keeps the parse method
	 */
	public static class Factory {

		/**
		 * static method to create the object Precondition: If this object is an
		 * element, the current or next start element starts this object and any
		 * intervening reader events are ignorable If this object is not an
		 * element, it is a complex type and the reader is at the event just
		 * after the outer start element Postcondition: If this object is an
		 * element, the reader is positioned at its end element If this object
		 * is a complex type, the reader is positioned at the end element of its
		 * outer element
		 */
		public static SendPushMessage parse(
				javax.xml.stream.XMLStreamReader reader)
				throws java.lang.Exception {
			SendPushMessage object = new SendPushMessage();

			int event;
			java.lang.String nillableValue = null;
			java.lang.String prefix = "";
			java.lang.String namespaceuri = "";
			try {

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.getAttributeValue(
						"http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
					java.lang.String fullTypeName = reader
							.getAttributeValue(
									"http://www.w3.org/2001/XMLSchema-instance",
									"type");
					if (fullTypeName != null) {
						java.lang.String nsPrefix = null;
						if (fullTypeName.indexOf(":") > -1) {
							nsPrefix = fullTypeName.substring(0,
									fullTypeName.indexOf(":"));
						}
						nsPrefix = nsPrefix == null ? "" : nsPrefix;

						java.lang.String type = fullTypeName
								.substring(fullTypeName.indexOf(":") + 1);

						if (!"sendPushMessage".equals(type)) {
							// find namespace for the prefix
							java.lang.String nsUri = reader
									.getNamespaceContext().getNamespaceURI(
											nsPrefix);
							return (SendPushMessage) org.csapi.www.schema.parlayx.wap_push.send.v1_0.local.ExtensionMapper
									.getTypeObject(nsUri, type, reader);
						}

					}

				}

				// Note all attributes that were handled. Used to differ normal
				// attributes
				// from anyAttributes.
				java.util.Vector handledAttributes = new java.util.Vector();

				reader.next();

				java.util.ArrayList list1 = new java.util.ArrayList();

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.isStartElement()
						&& new javax.xml.namespace.QName(
								"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
								"addresses").equals(reader.getName())) {

					// Process the array and step past its final element's end.
					list1.add(reader.getElementText());

					// loop until we find a start element that is not part of
					// this array
					boolean loopDone1 = false;
					while (!loopDone1) {
						// Ensure we are at the EndElement
						while (!reader.isEndElement()) {
							reader.next();
						}
						// Step out of this element
						reader.next();
						// Step to next element event.
						while (!reader.isStartElement()
								&& !reader.isEndElement())
							reader.next();
						if (reader.isEndElement()) {
							// two continuous end elements means we are exiting
							// the xml structure
							loopDone1 = true;
						} else {
							if (new javax.xml.namespace.QName(
									"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
									"addresses").equals(reader.getName())) {
								list1.add(reader.getElementText());

							} else {
								loopDone1 = true;
							}
						}
					}
					// call the converter utility to convert and set the array

					object.setAddresses((org.apache.axis2.databinding.types.URI[]) org.apache.axis2.databinding.utils.ConverterUtil
							.convertToArray(
									org.apache.axis2.databinding.types.URI.class,
									list1));

				} // End of if for expected property start element

				else {
					// A start element we are not expecting indicates an invalid
					// parameter was passed
					throw new org.apache.axis2.databinding.ADBException(
							"Unexpected subelement " + reader.getName());
				}

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.isStartElement()
						&& new javax.xml.namespace.QName(
								"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
								"targetURL").equals(reader.getName())) {

					nillableValue = reader.getAttributeValue(
							"http://www.w3.org/2001/XMLSchema-instance", "nil");
					if ("true".equals(nillableValue)
							|| "1".equals(nillableValue)) {
						throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "targetURL"
										+ "  cannot be null");
					}

					java.lang.String content = reader.getElementText();

					object.setTargetURL(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToAnyURI(content));

					reader.next();

				} // End of if for expected property start element

				else {
					// A start element we are not expecting indicates an invalid
					// parameter was passed
					throw new org.apache.axis2.databinding.ADBException(
							"Unexpected subelement " + reader.getName());
				}

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.isStartElement()
						&& new javax.xml.namespace.QName(
								"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
								"senderAddress").equals(reader.getName())) {

					nillableValue = reader.getAttributeValue(
							"http://www.w3.org/2001/XMLSchema-instance", "nil");
					if ("true".equals(nillableValue)
							|| "1".equals(nillableValue)) {
						throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "senderAddress"
										+ "  cannot be null");
					}

					java.lang.String content = reader.getElementText();

					object.setSenderAddress(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(content));

					reader.next();

				} // End of if for expected property start element

				else {

				}

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.isStartElement()
						&& new javax.xml.namespace.QName(
								"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
								"subject").equals(reader.getName())) {

					nillableValue = reader.getAttributeValue(
							"http://www.w3.org/2001/XMLSchema-instance", "nil");
					if ("true".equals(nillableValue)
							|| "1".equals(nillableValue)) {
						throw new org.apache.axis2.databinding.ADBException(
								"The element: " + "subject"
										+ "  cannot be null");
					}

					java.lang.String content = reader.getElementText();

					object.setSubject(org.apache.axis2.databinding.utils.ConverterUtil
							.convertToString(content));

					reader.next();

				} // End of if for expected property start element

				else {

				}

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.isStartElement()
						&& new javax.xml.namespace.QName(
								"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
								"priority").equals(reader.getName())) {

					object.setPriority(org.csapi.www.schema.parlayx.wap_push.v1_0.MessagePriority.Factory
							.parse(reader));

					reader.next();

				} // End of if for expected property start element

				else {

				}

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.isStartElement()
						&& new javax.xml.namespace.QName(
								"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
								"charging").equals(reader.getName())) {

					object.setCharging(org.csapi.www.schema.parlayx.common.v2_1.ChargingInformation.Factory
							.parse(reader));

					reader.next();

				} // End of if for expected property start element

				else {

				}

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.isStartElement()
						&& new javax.xml.namespace.QName(
								"http://www.csapi.org/schema/parlayx/wap_push/send/v1_0/local",
								"receiptRequest").equals(reader.getName())) {

					object.setReceiptRequest(org.csapi.www.schema.parlayx.common.v2_1.SimpleReference.Factory
							.parse(reader));

					reader.next();

				} // End of if for expected property start element

				else {

				}

				while (!reader.isStartElement() && !reader.isEndElement())
					reader.next();

				if (reader.isStartElement())
					// A start element we are not expecting indicates a trailing
					// invalid property
					throw new org.apache.axis2.databinding.ADBException(
							"Unexpected subelement " + reader.getName());

			} catch (javax.xml.stream.XMLStreamException e) {
				throw new java.lang.Exception(e);
			}

			return object;
		}

	}// end of factory class

}
