package tracuudiemthi;

import icom.Constants;
import icom.DBPool;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.DBUtils;
import icom.common.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import daily.ListSendObj;


public class Check_DADH_Thread extends Thread{
	
	public void run(){
		
		while(Sender.processData){
			
				//goi ws check xem da co dap an chua,
			//+ neu chua co thi insert vao table dapan_mon
			String url = Constants._prop.getProperty("url_tuyensinh");
			String call = url + "gameID=20&type=22";
			String xml = "";
			try {
				xml = Http_post(call);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			String somon = getValue(xml, "soMon");
			String mamon_dh = "";
			if(!"0".equalsIgnoreCase(somon)) {
				mamon_dh = getValue(xml, "mamon");
			}
			String[] mon_da = mamon_dh.split(",");
			for(int i =0; i< mon_da.length; i++) {
				if(!isExistMaMon(mon_da[i])) {
					insertDapan_Bo(mon_da[i]);
				}
			}
			
			try {
				Thread.sleep(60*1000 *15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	//Check xem mon da co trong table chua
	private boolean isExistMaMon(String mamon) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  mamon_DH where mamon = '"+ mamon+ "'";

			Vector result = DBUtils.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistMaMon Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}
	
	public static String Http_post(String surl) throws IOException {
		URL url = new URL(surl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		// Util.logger.info("getResponseCode : " +
		// connection.getResponseCode());
		String line = null;
		String data = "";
		while ((line = reader.readLine()) != null) {
			data += line;
		}
		// Util.logger.info("data :   " + data);
		return data;
	}
	
	private static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);

		return (f > l) ? "" : xml.substring(f, l);
	}
	private int insertDapan_Bo(String mamon) {
		
		int r = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		connection = (Connection) dbpool.getConnection("gateway");
		PreparedStatement stmt = null;
		
		if (connection != null) {
			try {
				String strQuery = "INSERT INTO mamon_DH (mamon)"
						+ " VALUES(?)";
				stmt = connection.prepareStatement(strQuery);
				stmt.setString(1, mamon);
				r = (stmt.executeUpdate() == 1 ? 0 : 1);
			} catch (SQLException e) {
				Util.logger.info("insertSendQueue Failed! " + e);
				r = 1;
			} finally {
				try {
					stmt.close();
					connection.close();
				} catch (SQLException ex) {
				}
			}
		} else {
			Util.logger.info("insertSendQueue Connection Null!");
			r = 1;
		}
		return r;
	}
	
	public static double ParserDouble(Object o) {
		try {
			return Double.parseDouble(o.toString());
		} catch (Exception ex) {
			return 0;
		}
	}
	
	public int updateStatusMT(int id, int status){

		int ireturn = 1;
			
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE list_send SET STATUS_MT = "
				+ status + " WHERE ID = " + id;
		
		
		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateListSendStatus@"
						+ ": uppdate Statement: UPDATE  "
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateMlistDateRetry@: UPDATE  "
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return ireturn;

	}

	
}
