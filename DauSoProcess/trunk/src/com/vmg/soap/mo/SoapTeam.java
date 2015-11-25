package com.vmg.soap.mo;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.MOSender;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.vmg.soap.mo.sendXMLLongcheer;
public class SoapTeam extends ContentAbstract {
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		int iRetries = 1;
		int iTimeout = 1;

		String result = "";
		while (iRetries > 0) {
			try {
				String url = Constants._prop.getProperty("mo.soap." + keyword.getOptions() + ".url");
				String username = Constants._prop.getProperty("mo.soap." +keyword.getOptions()
						+ ".username");
				String password = Constants._prop.getProperty("mo.soap." +keyword.getOptions()
						+ ".password");
				if (url == null)
					throw new Exception("In the profile is missing mo.soap." +keyword.getOptions()
							+ ".url");
				if (msgObject.getMobileoperator().equalsIgnoreCase("BEELINE")
						|| msgObject.getMobileoperator()
								.equalsIgnoreCase("VNM")) {
					msgObject
							.setUsertext("Dich vu khong ho tro mang cua ban.DTHT 1900571566");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
					return null;
				}
				if (msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
					msgObject
							.setUsertext("Dich vu khong ho tro mang cua ban.DTHT 1900571566");
					msgObject.setMsgtype(2);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
					return null;
				}
				String[] split = msgObject.getUsertext().split(" ");
				if (!split[0].equalsIgnoreCase(msgObject.getKeyword())) {
					msgObject
							.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
					return null;
				}
				String src=msgObject.getUserid();
				String smsbody=msgObject.getUsertext().replace(" ", "%20");
				String smsid=msgObject.getRequestid().toString();
				String dest=msgObject.getServiceid();
				// http://27.0.14.41/icom/sms.php?src=84914710711&dest=8551&smsbody=TEAM%20NAP%20LAMKXKX&smsid=123456&username=icom&password=icom94567
			    url=url+"src="+src+"&dest="+dest+"&smsbody="+smsbody+"&smsid="+smsid+"&username="+username+"&password="+password;
				
				Http_Post(url);	
				//Util.logger.info("Result: " + result);
				
				return null;
				

			} catch (Exception e) {
				Util.logger.error(this.getClass().getName() + "@"
						+ "Some Exception..!! Got " + result
						+ ", Going For Retry, Sleeping,Details: " + "Msisdn: "
						+ msgObject.getUserid() + " Shortcode: "
						+ msgObject.getServiceid() + " Keyword: "
						+ msgObject.getKeyword() + " RequestID: "
						+ msgObject.getRequestid() + "CommandCode: "
						+ msgObject.getKeyword() + " Online Retry countdown: "
						+ iRetries);

				Util.logger.info(this.getClass().getName() + "@"
						+ "Exception: " + e.toString());

				Util.logger.printStackTrace(e);
				iRetries--;
				Thread.sleep(iTimeout * 1000);
				continue;
			}

		}
		add2SMSSendFailed(msgObject);
		return null;
	}

	 public static void Http_Post(String url ) {
		   try {
			Util.logger.info("url:"+url);
		    HttpClient client = new HttpClient();
		    PostMethod method = new PostMethod( url );
		
			 // Execute the POST method
		    int statusCode = client.executeMethod( method );
		    Util.logger.info("statusCode:"+statusCode);
		    
			  
		    if( statusCode != 200) {
		    Util.logger.info("Post Fales.statusCode:"+statusCode);
			    
		      String contents = method.getResponseBodyAsString();
		      method.releaseConnection();
		    }
		   }
		   catch( Exception e ) {
		    e.printStackTrace();
		   }
		 }

	private static BigDecimal add2SMSSendFailed(MsgObject msgObject) {

		Util.logger.info("add2SMSSendFailed:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CPID)"
				+ " values(?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getKeyword());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

}
