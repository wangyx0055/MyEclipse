package dbSynch;

import icom.Constants;
import icom.DBPool;
import icom.Sender;
import icom.common.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import servicesPkg.MlistInfo;

/**
 * 
 * @author DanND
 * @date 2011-12-21
 *
 */

public class SynVasgate extends Thread{

	private static String CONNECTION_TIMEOUT_RESULT = "255";
	private static String RESPONSE_TIMEOUT = "256";
	private String url = "";
	
	public SynVasgate(){
		url = Constants._prop.getProperty("VASGATE_SYN_URL");
	}
	
	public void run(){
		
		if(url.trim().equals("")){
			Util.logger.error("ERROR FILE CONFIG AT VASGATE_SYN_URL");
			return;
		}
		
		Util.logger.info("Start Synchronize DB to VasGate url = " + url);
		
		while(Sender.getData){
			Util.logger.info("Step 1 : Start send, get data from table mlist_subcriber ...");
			ArrayList<MlistInfo> arrReg = getMlistInfo("mlist_subcriber");
			for(int i = 0;i<arrReg.size();i++){
				
				MlistInfo mlistInfo = arrReg.get(i);
				// status = 1 - Subcriber, 0: Unsub
				String xml = getXmlRequest(mlistInfo, 1);
				Util.logger.info("for i = " + i + " | xml = " + xml );
				httpPostSynVasGate(url,xml);
			}
			Util.logger.info("Step 2 : Get data from table mlist_subcriber_cancel ...");
			ArrayList<MlistInfo> arrUnSub = getMlistInfo("mlist_subcriber_cancel");
			for(int i = 0;i<arrUnSub.size();i++){
				MlistInfo mlistInfo = arrUnSub.get(i);
				String xml = getXmlRequest(mlistInfo, 0);
				Util.logger.info("for i = " + i + " | xml = " + xml );
				httpPostSynVasGate(url, xml);
			}
			Util.logger.info("Step 3 : end step, thread sleep...");
			try {
				Thread.sleep(5*50*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	private static String httpPostSynVasGate(String url,String xml){
		
		String sReturn = "";
		
		int timeOutConnect = 1000;
		int timeOutResponseData = 60*1000; 
		
		MultiThreadedHttpConnectionManager connectionMng = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = connectionMng.getParams();
		
		params.setConnectionTimeout(timeOutConnect);
		params.setSoTimeout(timeOutResponseData);
		
		BufferedReader reader = null;
		PostMethod method = null;		
		HttpClient client = null;
		
		
		try {
						
			if(client == null){
				client = new HttpClient(connectionMng);
			}
			
			HttpState state = client.getState();
			client.setState(state);
			
			// set time out response from socket
			client.getParams().setParameter("http.socket.timeout", timeOutResponseData);
						
			method = new PostMethod(url);
			method.setDoAuthentication(true);
			
			method.getHostAuthState().setAuthAttempted(true);
			method.getHostAuthState().setAuthRequested(true);
			method.getHostAuthState().setPreemptive();
			method.addRequestHeader("Content-Type", "text/html");
			
			method.addRequestHeader("SOAPAction", "receiveMessage");
			
			ByteArrayRequestEntity entity = null;
			try {
				entity = new ByteArrayRequestEntity(xml.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				Util.logger.error("SendXML - httpPost Error: " + e1.getMessage());
				return null;
			}
			method.setRequestEntity(entity);
			
			client.executeMethod(method);
			InputStream inputStream = method.getResponseBodyAsStream();
			StringBuffer sb = new StringBuffer();
			String line;
			reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			String textResponse = sb.toString();
			//Logging
			Util.logger.info("/***/"+xml+"/***/"+","+"/***/ xmlResponse = "+textResponse);

			sReturn = textResponse;
				
		}
		catch(ConnectException connectEx){
			sReturn = CONNECTION_TIMEOUT_RESULT;
			Util.logger.info("httpPostSynVasGate ### connect time out to server : " + url);
		}
		catch (ConnectTimeoutException cte ){
			//Took too long to connect to remote host
			sReturn = CONNECTION_TIMEOUT_RESULT;
			Util.logger.info("httpPostSynVasGate @ Connection TimeOut");
		}
		catch (SocketTimeoutException ste){
			//Remote host didn�t respond in time
			sReturn = RESPONSE_TIMEOUT;
			Util.logger.info("httpPostSynVasGate :: Socket TimeOut: " +
					" Remote host didn't respond in time! \n ### url Request = " +  url);			
		}catch(SocketException se){
			sReturn = RESPONSE_TIMEOUT;
			Util.logger.info("httpPostSynVasGate :: Socket TimeOut :: " +
					" Remote host didn't respond in time! \n ### xml Request = " + url);
		}catch(HttpException e0){
			sReturn = RESPONSE_TIMEOUT;
			e0.printStackTrace();
			Util.logger.printStackTrace(e0);
			
		}catch(IOException ioe){
			sReturn = RESPONSE_TIMEOUT;
			ioe.printStackTrace();
			Util.logger.printStackTrace(ioe);
			
		}finally{
			
			Util.logger.info("######## httpPostSynVasGate - start released connection!!! #########");
			method.releaseConnection();
			Util.logger.info("httpPostSynVasGate - Connection released !!!");
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
					
		return sReturn;
	}
	
	private ArrayList<MlistInfo> getMlistInfo(String tableName) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName
				+ " WHERE autotimestamps > (NOW() - INTERVAL 5 MINUTE)";
		Util.logger.info("Sql get mlistinfo : "+sqlSelect);	
		ArrayList<MlistInfo> arrMlistInfo = new ArrayList<MlistInfo>();
		MlistInfo mlistInfo;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
				Util.logger.info(" SynVasgate : getMlistInfo : DBConnect!");	
			}
			stmt = connection.prepareStatement(sqlSelect);
			if (stmt.execute()) {
				Util.logger.info(" SynVasgate : getMlistInfo : execute query success ...");
				rs = stmt.getResultSet();
				while (rs.next()) {
					mlistInfo = null;
					mlistInfo = new MlistInfo();
					mlistInfo.setId(rs.getInt("ID"));
					mlistInfo.setUserId(rs.getString("USER_ID"));
					mlistInfo.setServiceId(rs.getString("SERVICE_ID"));
					mlistInfo.setToday(rs.getString("DATE"));
					mlistInfo.setOptions(rs.getString("OPTIONS"));
					mlistInfo.setFailures(rs.getString("FAILURES"));
					mlistInfo.setLastCode(rs.getString("LAST_CODE"));
					mlistInfo.setAutoTimeStamps(rs
							.getTimestamp("AUTOTIMESTAMPS"));
					mlistInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					mlistInfo.setRequestId(rs.getString("REQUEST_ID"));
					mlistInfo.setMessageType(rs.getInt("MESSAGE_TYPE"));
					mlistInfo.setMobiOperator(rs.getString("MOBILE_OPERATOR"));
					mlistInfo.setMtCount(rs.getInt("MT_COUNT"));
					mlistInfo.setMtFree(rs.getInt("MT_FREE"));
					mlistInfo.setDuration(rs.getInt("DURATION"));
					mlistInfo.setAmount(rs.getInt("AMOUNT"));
					mlistInfo.setContentId(rs.getInt("CONTENT_ID"));
					mlistInfo.setService(rs.getString("SERVICE"));
					mlistInfo.setCompanyId(rs.getString("COMPANY_ID"));
					mlistInfo.setActive(rs.getInt("ACTIVE"));
					mlistInfo.setChanelType(rs.getInt("CHANNEL_TYPE"));
					mlistInfo.setRegCount(rs.getInt("REG_COUNT"));
					Util.logger.info(" SynVasgate : getMlistInfo : mlistinfo: "+rs.getInt("ID"));
					Util.logger.info(" Id: "+rs.getInt("ID"));
					Util.logger.info(" USER_ID: "+rs.getString("USER_ID"));
					Util.logger.info(" SERVICE_ID: "+rs.getString("SERVICE_ID"));
					arrMlistInfo.add(mlistInfo);
				}
			}else{
				Util.logger.info(" SynVasgate : getMlistInfo : Can't execute query!");
			}

		} catch (SQLException ex3) {
			Util.logger.error("SynVasgate - getMlistInfo. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("SynVasgate - getMlistInfo. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}
	
	private String getXmlRequest(MlistInfo mlistInfo, int status){
		String userDongBo  = "";
		String passDongBo =	"";
		try{
			userDongBo  = Constants._prop.getProperty("userdongbo",userDongBo);
			passDongBo  = Constants._prop.getProperty("passdongbo",passDongBo);
		}catch (Exception ex) {
			Util.logger.error("Can't get property userDongBo and passDongBo :"  + ex.toString());
		}
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"		
		+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.vasgate.com\">"
		+ "<soapenv:Header/>"
		+ "  <soapenv:Body>"
		     	+"<ws:receiveMessage>"
		        +"<!--Optional:-->"
		        +"<ws:serviceid>" + mlistInfo.getServiceId() +"</ws:serviceid>"
		        + "<!--Optional:-->"
		        + "<ws:msisdn>" + mlistInfo.getUserId() + "</ws:msisdn>"
		        +"<!--Optional:-->"
		        +"<ws:reqId>" + mlistInfo.getRequestId() + "</ws:reqId>"
		        + "<!--Optional:-->"
		        + "<ws:reqMsg>0</ws:reqMsg>"
		        +"<!--Optional:-->"
		        +"<ws:reqTime>" + mlistInfo.getAutoTimeStamps() + "</ws:reqTime>"
		        +"<!--Optional:-->"
		        +"<ws:status>" + status + "</ws:status>"
		        +"<!--Optional:-->"
		        +"<ws:extend>?</ws:extend>"
		        +"<!--Optional:-->"
		        +"<ws:otherInfo>?</ws:otherInfo>"
		        +"<!--Optional:-->"
		        +"<ws:username>" + userDongBo + "</ws:username>"
		        +"<!--Optional:-->"
		        +"<ws:password>" + passDongBo + "</ws:password>"
		      +"</ws:receiveMessage>"
		   +"</soapenv:Body>"
		+"</soapenv:Envelope>";
		
		return xml;
	}
	
}
