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
 *         &lt;element name="MOForwardResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "moForwardResult" })
@XmlRootElement(name = "MOForwardResponse")
public class MOForwardResponse
{

	@XmlElement(name = "MOForwardResult")
	protected String moForwardResult;

	/**
	 * Gets the value of the moForwardResult property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMOForwardResult()
	{
		return moForwardResult;
	}

	/**
	 * Sets the value of the moForwardResult property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMOForwardResult(String value)
	{
		this.moForwardResult = value;
	}

}
