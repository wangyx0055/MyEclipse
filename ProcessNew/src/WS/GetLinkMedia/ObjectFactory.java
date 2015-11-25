package WS.GetLinkMedia;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the WS.GetLinkMedia package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory
{

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: WS.GetLinkMedia
	 * 
	 */
	public ObjectFactory()
	{
	}

	/**
	 * Create an instance of {@link GetLinkMediaResponse }
	 * 
	 */
	public GetLinkMediaResponse createGetLinkMediaResponse()
	{
		return new GetLinkMediaResponse();
	}

	/**
	 * Create an instance of {@link GetLinkMediaByCate }
	 * 
	 */
	public GetLinkMediaByCate createGetLinkMediaByCate()
	{
		return new GetLinkMediaByCate();
	}

	/**
	 * Create an instance of {@link GetLinkMedia }
	 * 
	 */
	public GetLinkMedia createGetLinkMedia()
	{
		return new GetLinkMedia();
	}

	/**
	 * Create an instance of {@link GetLinkMediaByCateResponse }
	 * 
	 */
	public GetLinkMediaByCateResponse createGetLinkMediaByCateResponse()
	{
		return new GetLinkMediaByCateResponse();
	}

}
