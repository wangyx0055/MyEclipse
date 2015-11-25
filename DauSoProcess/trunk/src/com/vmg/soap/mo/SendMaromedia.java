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
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sun.xml.internal.messaging.saaj.util.Base64;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.DateProc;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;

import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import com.vmg.soap.mo.MOSender;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.tempuri.IcomLocator;
import org.tempuri.IcomSoap;

public class SendMaromedia extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		String url = "http://smsgateway.maromedia.vn/api/icom";
		url = Constants._prop.getProperty("mo.soap.MaroMedia.ur", url);

		URL serviceURL = new URL(url);

		IcomLocator locator = new IcomLocator();
		IcomSoap icom = locator.geticomSoap(serviceURL);
		String username = "icom";
		username = Constants._prop.getProperty("mo.soap.MaroMedia.username", username);
		String password = "icommaromedia";
		password = Constants._prop.getProperty("mo.soap.MaroMedia.password", password);
		
		Util.logger.info("msgObject.getUsertext():" + msgObject.getUsertext());
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("username", username);
			params.put("password", password);
			params.put("moid", msgObject.getRequestid().toString());
			params.put("moseq", msgObject.getRequestid().toString());
			params.put("src", msgObject.getUserid());
			params.put("dest", msgObject.getServiceid());
			params.put("cmdcode", msgObject.getKeyword());
			params.put("msgbody", msgObject.getUsertext());
			boolean success=false;
			try {		
				Long ret = (Long)RestClient.callMethod(url, "ReceiveMO", params);			
				if(ret == 0) success = true;
				Util.logger.info("result:" + ret);
				
			}
			catch(Exception ex) {
				Util.logger.error("Error when send mo to maromedia : "+ex.getMessage());
			}
			
	
//		int result = icom.receiveMO(msgObject.getRequestid().toString(),
//				msgObject.getRequestid().toString(), msgObject.getUserid(),
//				msgObject.getServiceid(), msgObject.getKeyword(),new String(Base64.encode(msgObject
//						.getUsertext().getBytes())), username, password);
	
		if (success == true) {
			Util.logger.info("OK!!!");

		} else {
			add2SMSSendFailed(msgObject);
		}
		}
		catch (Exception ex)
		{
			add2SMSSendFailed(msgObject);
		}
		return null;

	}

	 public static void main(String[] args)
	    {
	    	String test="@&^+_123244";
	    	String encode=new String(Base64.encode(test.getBytes()));
	    	System.out.println("encode "+encode);
	    	// giai ma
	    	System.out.println("decode "+Base64.base64Decode(encode));
	    	
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
