package Test;

import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import MyUtility.MyLogger;

public class ThreadCalWS extends Thread
{
	MyLogger mLog = new MyLogger(this.getClass().toString());

	public String Template = "";
	public String URL = "";
	public Object[] mListMSISDN = null;

	public Integer ThreadIndex = 0;
	
	public void run()
	{
		for (Object MSISDN : mListMSISDN)
		{
			String TextResponse = "";

			String XMLReqeust = String.format(Template, MSISDN);

			HttpPost mPost = new HttpPost(URL);

			StringEntity mEntity = new StringEntity(XMLReqeust, ContentType.create("text/xml", "UTF-8"));

			mPost.setEntity(mEntity);
			HttpClient mClient = new DefaultHttpClient();
			try
			{
				HttpResponse response = mClient.execute(mPost);

				HttpEntity entity = response.getEntity();
				TextResponse = EntityUtils.toString(entity);

				System.out.println(TextResponse);

				EntityUtils.consume(entity);
			}
			catch (Exception ex)
			{
				mLog.log.error(ex);
			}
			finally
			{
				mLog.log.debug( "REQUEST --> " + XMLReqeust);
				mLog.log.debug( "RESPONSE --> " + TextResponse);
			}
		}
		mLog.log.debug( "KET THUC THEAD " + ThreadIndex);
	}
}