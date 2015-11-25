package WS.GetLinkMedia;

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
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MSISDN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MediaType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CateID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="MediaID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ChannelType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="RequestTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "userName", "password", "msisdn", "mediaType", "cateID", "mediaID", "channelType", "price", "requestTime" })
@XmlRootElement(name = "GetLinkMediaByCate")
public class GetLinkMediaByCate
{

	@XmlElement(name = "UserName")
	protected String userName;
	@XmlElement(name = "Password")
	protected String password;
	@XmlElement(name = "MSISDN")
	protected String msisdn;
	@XmlElement(name = "MediaType")
	protected int mediaType;
	@XmlElement(name = "CateID")
	protected int cateID;
	@XmlElement(name = "MediaID")
	protected String mediaID;
	@XmlElement(name = "ChannelType")
	protected int channelType;
	@XmlElement(name = "Price")
	protected double price;
	@XmlElement(name = "RequestTime")
	protected String requestTime;

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

	/**
	 * Gets the value of the msisdn property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMSISDN()
	{
		return msisdn;
	}

	/**
	 * Sets the value of the msisdn property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMSISDN(String value)
	{
		this.msisdn = value;
	}

	/**
	 * Gets the value of the mediaType property.
	 * 
	 */
	public int getMediaType()
	{
		return mediaType;
	}

	/**
	 * Sets the value of the mediaType property.
	 * 
	 */
	public void setMediaType(int value)
	{
		this.mediaType = value;
	}

	/**
	 * Gets the value of the cateID property.
	 * 
	 */
	public int getCateID()
	{
		return cateID;
	}

	/**
	 * Sets the value of the cateID property.
	 * 
	 */
	public void setCateID(int value)
	{
		this.cateID = value;
	}

	/**
	 * Gets the value of the mediaID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMediaID()
	{
		return mediaID;
	}

	/**
	 * Sets the value of the mediaID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMediaID(String value)
	{
		this.mediaID = value;
	}

	/**
	 * Gets the value of the channelType property.
	 * 
	 */
	public int getChannelType()
	{
		return channelType;
	}

	/**
	 * Sets the value of the channelType property.
	 * 
	 */
	public void setChannelType(int value)
	{
		this.channelType = value;
	}

	/**
	 * Gets the value of the price property.
	 * 
	 */
	public double getPrice()
	{
		return price;
	}

	/**
	 * Sets the value of the price property.
	 * 
	 */
	public void setPrice(double value)
	{
		this.price = value;
	}

	/**
	 * Gets the value of the requestTime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRequestTime()
	{
		return requestTime;
	}

	/**
	 * Sets the value of the requestTime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRequestTime(String value)
	{
		this.requestTime = value;
	}

}
