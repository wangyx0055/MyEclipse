package com.violet.sdp.wappush;

import java.security.MessageDigest;

public class NonceGenerator
{

	private static final NonceGenerator instance = new NonceGenerator();

	private NonceGenerator()
	{

	}

	public static NonceGenerator getInstance()
	{
		return instance;
	}

	public String getNonce(String s)
	{
		char hexDigits[] =
		{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		try
		{
			byte[] strTemp = s.getBytes();
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(strTemp);
			byte[] md = messageDigest.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++)
			{
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return String.valueOf(str);
		}
		catch (Exception e)
		{
			return null;
		}
	}

}
