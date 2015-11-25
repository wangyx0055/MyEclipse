package ws.example;

import javax.xml.soap.SOAPHeader;

/**
 * Temperature Converter Implementation Class
 */
public class TemperatureConverter
{
	
	/**
	 * util method to convert celsius to fahrenheit
	 * 
	 * @param cValue
	 *            : double value of celsius
	 * @return calculated value of fahrenheit
	 */
	public double c2fConvertion(double cValue)
	{
		return ((cValue * 9.0) / 5.0) + 32.0;
	}

	/**
	 * util method to convert fahrenheit to celsius
	 * 
	 * @param fValue
	 *            : double value of fahrenheit
	 * @return calculated value of celsius
	 */
	public double f2cConvertion(double fValue)
	{
		return ((fValue - 32.0) * 5.0) / 9.0;
	}
}
