package WS.Iplay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="User_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Service_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Command_Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Request_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Receive_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Operator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "userID", "serviceID", "commandCode", "info", "requestID", "receiveDate", "operator", "userName", "password" })
@XmlRootElement(name = "MOForward")
public class MOForward
{

	@XmlElement(name = "User_ID")
	protected String userID;
	@XmlElement(name = "Service_ID")
	protected String serviceID;
	@XmlElement(name = "Command_Code")
	protected String commandCode;
	@XmlElement(name = "Info")
	protected String info;
	@XmlElement(name = "Request_ID")
	protected String requestID;
	@XmlElement(name = "Receive_Date")
	protected String receiveDate;
	@XmlElement(name = "Operator")
	protected String operator;
	@XmlElement(name = "UserName")
	protected String userName;
	@XmlElement(name = "Password")
	protected String password;

	/**
	 * Gets the value of the userID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUserID()
	{
		return userID;
	}

	/**
	 * Sets the value of the userID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUserID(String value)
	{
		this.userID = value;
	}

	/**
	 * Gets the value of the serviceID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getServiceID()
	{
		return serviceID;
	}

	/**
	 * Sets the value of the serviceID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setServiceID(String value)
	{
		this.serviceID = value;
	}

	/**
	 * Gets the value of the commandCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCommandCode()
	{
		return commandCode;
	}

	/**
	 * Sets the value of the commandCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCommandCode(String value)
	{
		this.commandCode = value;
	}

	/**
	 * Gets the value of the info property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getInfo()
	{
		return info;
	}

	/**
	 * Sets the value of the info property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setInfo(String value)
	{
		this.info = value;
	}

	/**
	 * Gets the value of the requestID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRequestID()
	{
		return requestID;
	}

	/**
	 * Sets the value of the requestID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRequestID(String value)
	{
		this.requestID = value;
	}

	/**
	 * Gets the value of the receiveDate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getReceiveDate()
	{
		return receiveDate;
	}

	/**
	 * Sets the value of the receiveDate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setReceiveDate(String value)
	{
		this.receiveDate = value;
	}

	/**
	 * Gets the value of the operator property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOperator()
	{
		return operator;
	}

	/**
	 * Sets the value of the operator property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOperator(String value)
	{
		this.operator = value;
	}

	/**
	 * Gets the value of the userName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the value of the userName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUserName(String value)
	{
		this.userName = value;
	}

	/**
	 * Gets the value of the password property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the value of the password property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPassword(String value)
	{
		this.password = value;
	}

}
