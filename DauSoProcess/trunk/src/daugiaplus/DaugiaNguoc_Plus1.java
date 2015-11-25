package daugiaplus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class DaugiaNguoc_Plus1 extends ContentAbstract{

	@SuppressWarnings("unchecked")
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			Collection messages = new ArrayList();
			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();
			String sUsertext = msgObject.getUsertext();
			String sMobileOperator = msgObject.getMobileoperator();
			Timestamp sReceiveDate = msgObject.getTTimes();
			int number = 0;
			String code = "";
			String price = "";
			
			sUsertext = replaceAllWhiteWithOne(sUsertext);
			String[] sTokens = sUsertext.split(" ");
			number =  sTokens[0].length();
			if(sTokens.length >1) {
				for(int i = 1; i<sTokens.length; i++) {
					if(!isInteger(sTokens[i])) {
						msgObject.setUsertext("Tin nhan sai cu phap. Soan tin "
								+ " DG HD gui "
								+ keyword.getServiceid()
								+ " de duoc huong dan chi tiet. Vui long truy cap vao http://daugiaplus.vn de biet them chi tiet.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;
					}
				}
			}
			String checkLeng = "";
			if (sTokens.length == 1) {
				if(number > 3) {
					price = sTokens[0].substring(3);
					if(!isInteger(price)) {
						msgObject
								.setUsertext("Tin nhan sai cu phap. Soan tin "
										+ " DG HD gui "
										+ keyword.getServiceid()
										+ " de duoc huong dan chi tiet. Vui long truy cap vao http://daugiaplus.vn de biet them chi tiet.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						Thread.sleep(1000);
						messages.add(new MsgObject(msgObject));
						return messages;
					}
				} else {
					msgObject
					.setUsertext("Tin nhan sai cu phap. Soan tin "
							+ " DG HD gui "
							+ keyword.getServiceid()
							+ " de duoc huong dan chi tiet. Vui long truy cap vao http://daugiaplus.vn de biet them chi tiet.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					Thread.sleep(1000);
					messages.add(new MsgObject(msgObject));
					return messages;
				}
			} 
				Connection connection = null;
				PreparedStatement statement = null;
				String sqlString = null;
				ResultSet rs = null;
				DBPool dbpool = new DBPool();
				try {
					connection = dbpool.getConnectionGateway();
					if (connection == null) {
						Util.logger.error("Impossible to connect to DB");
					}
					String sqlInsert = "Insert into daugiaplus_queue (USER_ID,SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, receive_date) values ('"
							+ sUserid
							+ "','"
							+ sServiceid
							+ "','"
							+ sMobileOperator
							+ "','"
							+ keyword.getKeyword()
							+ "','"
							+ sUsertext
							+ "','" + sReceiveDate + "')"; 
							
					statement = connection.prepareStatement(sqlInsert);
					if (statement.executeUpdate() != 1) {
						Util.logger
								.error("daugianguoc_plus: Insert into daugianguoc_plus Failed");
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					dbpool.cleanup(statement);
					dbpool.cleanup(connection);
				}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public boolean isInteger( String input )  
	{  
	   try {  
	      Integer.parseInt( input );  
	      return true;  
	   } catch(NumberFormatException  ext) {  
	      return false;  
	   }  
	}  
	
	public static String replaceAllWhiteWithOne(String sInput) {
		String strTmp = sInput.trim();
		String strResult = "";
		for (int i = 0; i < strTmp.length(); i++) {
			char ch = strTmp.charAt(i);
			if (ch == ' ') {
				for (int j = i; j < strTmp.length(); j++) {
					char ch2 = strTmp.charAt(j);
					if (ch2 != ' ') {
						i = j;
						strResult = strResult + ' ' + ch2;
						break;
					}
				}

			} else {
				strResult = strResult + ch;
			}
		}
		return strResult;
	}

}
