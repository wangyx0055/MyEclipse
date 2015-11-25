package spam;

import java.sql.*;
import java.util.*;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;

public class DBUtils2 {

	public static String getStringValue(Connection cnn, String fldName,
			String tblName, String con) throws Exception {
		String tmpVal = "";

		PreparedStatement pstm = null;
		Vector vtValue = null;
		ResultSet rs = null;

		String strSQL = "SELECT " + fldName + " FROM " + tblName;
		if (!con.equals("")) {
			strSQL = strSQL + " WHERE " + con;
		}

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();

			tmpVal = (rs.next()) ? rs.getString(1) : "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return tmpVal;
	}
	public static boolean saverequest(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into listspam( user,operator,info) values ('"
					+ msgObject.getUserid() + "','" + msgObject.getMobileoperator()  + "','" + msgObject.getUsertext()+ "')";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to list spam");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}
	public static boolean deletelistspam(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		boolean result = true;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		int total = 0;
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlDelete = "DELETE FROM listspam where user='" +msgObject.getUserid()+"'";
			

			statement = connection.prepareStatement(sqlDelete);
			Util.logger.info("Delete:" + sqlDelete);
			if (statement.execute()) {
				return result;
			}

			return false;
		} catch (SQLException e) {
			Util.logger.error(": Error1: " + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error2: " + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
	
	public static int getIntValue(Connection cnn, String fldName,
			String tblName, String con) throws Exception {
		int tmpVal = 0;

		PreparedStatement pstm = null;
		Vector vtValue = null;
		ResultSet rs = null;

		String strSQL = "SELECT " + fldName + " FROM " + tblName;
		if (!con.equals("")) {
			strSQL = strSQL + " WHERE " + con;
		}

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();

			tmpVal = (rs.next()) ? rs.getInt(1) : 0;
		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return tmpVal;
	}

	public static double getDoubleValue(Connection cnn, String fldName,
			String tblName, String con) throws Exception {
		double tmpVal = 0.0;

		PreparedStatement pstm = null;
		Vector vtValue = null;
		ResultSet rs = null;

		String strSQL = "SELECT " + fldName + " FROM " + tblName;
		if (!con.equals("")) {
			strSQL = strSQL + " WHERE " + con;
		}

		try {

			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();

			tmpVal = (rs.next()) ? rs.getDouble(1) : 0.0;
		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return tmpVal;
	}

	public float getFloatValue(Connection cnn, String fldName, String tblName,
			String con) throws Exception {

		float tmpVal = 0;

		PreparedStatement pstm = null;
		Vector vtValue = null;
		ResultSet rs = null;

		String strSQL = "SELECT " + fldName + " FROM " + tblName;
		if (!con.equals("")) {
			strSQL = strSQL + " WHERE " + con;
		}

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			tmpVal = (rs.next()) ? rs.getFloat(1) : 0;
		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return tmpVal;
	}

	public static String getSequenceValue(Connection cnn, String sequenceName)
			throws Exception {
		// SQL command to sequence value
		String strSQL = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
		String strReturn = "";

		PreparedStatement pstm = null;

		ResultSet rs = null;

		try {
			// Get query data
			pstm = cnn.prepareStatement(strSQL);
			rs = pstm.executeQuery();

			// Validation
			if (!rs.next()) {
				throw new Exception("Sequence " + sequenceName
						+ " does not exist");
			}

			strReturn = rs.getString(1);

		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return strReturn;
	}
	public static String[] checkspam(MsgObject msgObject) throws Exception
	{
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		String operator=msgObject.getMobileoperator();
		if (operator.toUpperCase().equalsIgnoreCase("BEELINE"))
		{
		return 	checkspamBEE(msgObject);
		}
		else
			if (operator.toUpperCase().equalsIgnoreCase("EVN"))
			{
			return 	checkspamEVN(msgObject);
			}
			else
				 
				if (operator.toUpperCase().equalsIgnoreCase("GPC"))
				{
				return 	checkspamGPC(msgObject);
				}
				else
					 
					if (operator.toUpperCase().equalsIgnoreCase("SFONE"))
					{
					return 	checkspamSFONE(msgObject);
					}
					else
						 
						if (operator.toUpperCase().equalsIgnoreCase("VIETTEL"))
						{
						return 	checkspamVIETTEL(msgObject);
						}
						else
							 
							if (operator.toUpperCase().equalsIgnoreCase("VMS"))
							{
							return 	checkspamVMS(msgObject);
							}
							else
								 
								if (operator.toUpperCase().equalsIgnoreCase("VNM"))
								{
								return 	checkspamVNM(msgObject);
								}
					
			
		
		return sRet;
	}
	
	
	protected static String[] checkspamBEE(MsgObject msgObject) throws Exception {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {
			return checkspamindbBEE(msgObject.getUserid(), msgObject
					.getMobileoperator());
		} catch (Exception e) {
			Util.logger.error("checkspam:" + e.getMessage());
		}
		return sRet;
	}
	public static boolean checklistspam(MsgObject msgObject) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  listspam where user='"
					+ msgObject.getUserid()
					+ "' and info='"+msgObject.getUsertext() +"'";
		
			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}
	private static String[] checkspamindbBEE(String user_id, String operator){

		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";

		String sqlselect = "";
		String sql5min = "select count(*)-3,5  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-5,current_timestamp )";
		String sql10min = "select count(*)-5,10  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-10,current_timestamp )";
		String sql60min = "select count(*)-30,60  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-60,current_timestamp )";
		String sqlallday = "select count(*)-300,1440  from sms_receive_day_"
				+ operator.toLowerCase() + " where user_id='" + user_id + "' ";
		Connection connection = null;

		sqlselect = sql5min + " union " + sql10min + " union " + sql60min
				+ " union " + sqlallday;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = getVectorTable(connection, sqlselect);
			for (int i = 0; i < result2.size(); i++) {
				Vector item = (Vector) result2.elementAt(i);
				String scount = (String) item.elementAt(0);
				String sdesc = (String) item.elementAt(1);
				long icount=0;
				try 
				{
				 icount = Long.parseLong(scount);
				}
				catch (Exception ex)
				{
				Util.logger.info("icount ko co");
				}

				if (icount > 0) {
					Util.logger.info("checkspam:userid=" + user_id + " @rule:"
							+ icount + "MO/" + sdesc + "minute");
					sRet[0] = "1";

					String mt1 = Constants._prop.getProperty("spam.mt."
							+ operator, "Ban da vi pham quy dinh chong Spam");
					sRet[1] = Constants._prop.getProperty("spam.mt." + operator
							+ "." + sdesc, mt1);

					return sRet;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}
	protected static String[] checkspamEVN(MsgObject msgObject) throws Exception {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {
			return checkspamindbEVN(msgObject.getUserid(), msgObject
					.getMobileoperator());
		} catch (Exception e) {
			Util.logger.error("checkspam:" + e.getMessage());
		}
		return sRet;
	}

	private static String[] checkspamindbEVN(String user_id, String operator) {

		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";

		String sqlselect = "";
		String sql10min = "select count(*)-3,10  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-10,current_timestamp )";
			String sqlallday = "select count(*)-10,1440  from sms_receive_day_"
				+ operator.toLowerCase() + " where user_id='" + user_id + "' ";
		Connection connection = null;

		sqlselect = sql10min + " union " + sqlallday;
		
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = getVectorTable(connection, sqlselect);
			for (int i = 0; i < result2.size(); i++) {
				Vector item = (Vector) result2.elementAt(i);
				String scount = (String) item.elementAt(0);
				String sdesc = (String) item.elementAt(1);
				long icount=0;
				try 
				{
				 icount = Long.parseLong(scount);
				}
				catch (Exception ex)
				{
				Util.logger.info("icount ko co");
				}

				if (icount > 0) {
					Util.logger.info("checkspam:userid=" + user_id + " @rule:"
							+ icount + "MO/" + sdesc + "minute");
					sRet[0] = "1";

					String mt1 = Constants._prop.getProperty("spam.mt."
							+ operator, "Ban da vi pham quy dinh chong Spam");
					sRet[1] = Constants._prop.getProperty("spam.mt." + operator
							+ "." + sdesc, mt1);

					return sRet;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}
	protected static String[] checkspamGPC(MsgObject msgObject) throws Exception {
		// TODO Auto-generated method stub
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {
			sRet = checkspamindbbycountGPC(msgObject.getUserid(), msgObject
					.getMobileoperator().toLowerCase());

			return sRet;
		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.error("checkspam:" + e.getMessage());
		}
		return sRet;
	}

	private static String[] checkspamindbbycountGPC(String user_id, String operator) {

		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";

		String sqlselect = "";
		String sql5min = "select count(*)-3,5  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-5,current_timestamp )";
		String sql10min = "select count(*)-5,10  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-10,current_timestamp )";
		String sql60min = "select count(*)-30,60  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-60,current_timestamp )";
		String sqlallday = "select count(*)-300,1440  from sms_receive_day_"
				+ operator.toLowerCase() + " where user_id='" + user_id + "' ";
		Connection connection = null;

		sqlselect = sql5min + " union " + sql10min + " union " + sql60min
				+ " union " + sqlallday;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = getVectorTable(connection, sqlselect);
			for (int i = 0; i < result2.size(); i++) {
				Vector item = (Vector) result2.elementAt(i);
				String scount = (String) item.elementAt(0);
				String sdesc = (String) item.elementAt(1);
				int icount=0;
				try 
				{
				 icount = Integer.parseInt(scount);
				}
				catch (Exception ex)
				{
				Util.logger.info("icount ko co");
				}
				

				if (icount > 0) {
					Util.logger.info("checkspam:userid=" + user_id + " @rule:"
							+ icount + "MO/" + sdesc + "minute");
					sRet[0] = "1";

					String mt1 = Constants._prop.getProperty("spam.mt."
							+ operator, "Ban da vi pham quy dinh chong Spam");
					sRet[1] = Constants._prop.getProperty("spam.mt." + operator
							+ "." + sdesc, mt1);

					return sRet;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}
	protected static String[] checkspamSFONE(MsgObject msgObject) throws Exception {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {

			String[] checkdbbycount = checkspamindbbycountSFONE(msgObject
					.getUserid(), msgObject.getMobileoperator());
			if (checkdbbycount[0].equalsIgnoreCase("1")) {
				return sRet;
			}
			return checkspamindbSFONE(msgObject.getUserid(), msgObject
					.getMobileoperator());
		} catch (Exception e) {
			Util.logger.error("checkspam:" + e.getMessage());
		}
		return sRet;
	}

	private static String[] checkspamindbSFONE(String user_id, String operator) {

		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		String sqlselect = "select sum(t1.price) from  sms_receive_day_"
				+ operator.toLowerCase()
				+ " t2  join price t1 on  t1.service_id=t2.service_id and t2.user_id='"
				+ user_id + "'";
		Connection connection = null;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = getVectorTable(connection, sqlselect);
			if (result2.size() > 0) {
				Vector item = (Vector) result2.elementAt(0);
				String scount = (String) item.elementAt(0);
				long icount=0;
				try 
				{
				 icount = Long.parseLong(scount);
				}
				catch (Exception ex)
				{
				Util.logger.info("icount ko co");
				}
				if (icount > 150000) {
					Util.logger.info("checkspam:userid=" + user_id
							+ " @amount:>150.000vnd");
					sRet[0] = "1";

					
					sRet[1] =  Constants._prop.getProperty("spam.mt."
							+ operator, "Ban da vi pham quy dinh chong Spam");
					
					return sRet;

				}

			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}

	private static String[] checkspamindbbycountSFONE(String user_id, String operator) {

		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		String sqlselect = "";
		String sql5min = "select count(*)-3,5  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-5,current_timestamp )";
		String sql10min = "select count(*)-5,10  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-10,current_timestamp )";
		String sql60min = "select count(*)-30,60  from sms_receive_day_"
				+ operator.toLowerCase()
				+ " where user_id='"
				+ user_id
				+ "' and receive_date > TIMESTAMPADD (minute,-60,current_timestamp )";
		String sqlallday = "select count(*)-300,1440  from sms_receive_day_"
				+ operator.toLowerCase() + " where user_id='" + user_id + "' ";
		Connection connection = null;

		sqlselect = sql5min + " union " + sql10min + " union " + sql60min
				+ " union " + sqlallday;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = getVectorTable(connection, sqlselect);
			for (int i = 0; i < result2.size(); i++) {
				Vector item = (Vector) result2.elementAt(i);
				String scount = (String) item.elementAt(0);
				String sdesc = (String) item.elementAt(1);
				long icount=0;
				try 
				{
				 icount = Long.parseLong(scount);
				}
				catch (Exception ex)
				{
				Util.logger.info("icount ko co");
				}
				if (icount > 0) {
					Util.logger.info( "checkspam:userid=" + user_id + " @rule:"
							+ icount + "MO/" + sdesc + "minute");
					sRet[0] = "1";

					String mt1 = Constants._prop.getProperty("spam.mt."
							+ operator, "Ban da vi pham quy dinh chong Spam");
					sRet[1] = Constants._prop.getProperty("spam.mt." + operator
							+ "." + sdesc, mt1);
					return sRet;
				}
			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}

	protected static String[] checkspamVIETTEL(MsgObject msgObject) throws Exception {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {
			return checkspamindbVIETTEL(msgObject.getUserid(), msgObject
					.getMobileoperator());
		} catch (Exception e) {
			Util.logger.error("checkspam:" + e.getMessage());
		}
		return sRet;
	}

	private static String[] checkspamindbVIETTEL(String user_id, String operator) {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		String sqlselect = "select sum(t1.price) from  sms_receive_day_"
				+ operator.toLowerCase()
				+ " t2  join price t1 on  t1.service_id=t2.service_id and t2.user_id='"
				+ user_id + "'";
		Connection connection = null;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = getVectorTable(connection, sqlselect);
			if (result2.size() > 0) {
				Vector item = (Vector) result2.elementAt(0);
				String scount = (String) item.elementAt(0);
				long icount=0;
				try 
				{
				 icount = Long.parseLong(scount);
				}
				catch (Exception ex)
				{
				Util.logger.info("icount ko co");
				}
				if (icount > 150000) {
					Util.logger.info("checkspam:userid=" + user_id
							+ " @amount:>150.000vnd");
					sRet[0] = "1";

					//sRet[1] = "Tin nhan SPAM. Ban khong duoc su dung dich vu qua 150.000d/ngay. Vui long goi 1900561558 de duoc huong dan them";
					sRet[1] = Constants._prop.getProperty("spam.mt."
							+ operator,
							"Ban da vi pham quy dinh chong Spam");
					return sRet;
				}

			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}
	protected static String[] checkspamVMS(MsgObject msgObject) throws Exception {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {
			return checkspamindbVMS(msgObject.getUserid(),msgObject.getMobileoperator());
		} catch (Exception e) {
			Util.logger.error("checkspam:" + e.getMessage());
		}
		return sRet;
	}

	private static String[] checkspamindbVMS(String user_id,String operator) {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		String sqlselect = "select sum(t1.price) from  sms_receive_day_" + operator.toLowerCase() +" t2  join price t1 on  t1.service_id=t2.service_id and t2.user_id='"
				+ user_id + "'";
		Connection connection = null;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = getVectorTable(connection, sqlselect);
			if (result2.size() > 0) {
				Vector item = (Vector) result2.elementAt(0);
				String scount = (String) item.elementAt(0);
				long icount=0;
				try 
				{
				 icount = Long.parseLong(scount);
				}
				catch (Exception ex)
				{
				Util.logger.info("icount ko co");
				}
				
				if (icount > 150000) {
					Util.logger.info( "checkspam:userid=" + user_id
							+ " @amount:>150.000vnd");
					sRet[0] = "1";

					//sRet[1] = "Tin nhan SPAM. Ban khong duoc su dung dich vu qua 150.000d/ngay. Vui long goi 1900561558 de duoc huong dan them";
					sRet[1] = Constants._prop.getProperty("spam.mt."
							+ operator,
							"Ban da vi pham quy dinh chong Spam");
					return sRet;
				}

			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}
	protected static String[] checkspamVNM(MsgObject msgObject) throws Exception {
		// TODO Auto-generated method stub
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		try {
			return checkspamindbVNM(msgObject.getUserid(), msgObject
					.getMobileoperator());
		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.error("checkspam:" + e.getMessage());
		}
		return sRet;
	}

	private static String[] checkspamindbVNM(String user_id, String operator) {
		String[] sRet = new String[2];
		sRet[0] = "0";
		sRet[1] = "";
		String sqlselect = "select sum(t1.price) from  sms_receive_day_"
				+ operator.toLowerCase()
				+ " t2  join price t1 on  t1.service_id=t2.service_id and t2.user_id='"
				+ user_id + "'";
		Connection connection = null;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			Vector result2 = getVectorTable(connection, sqlselect);
			if (result2.size() > 0) {
				Vector item = (Vector) result2.elementAt(0);
				String scount = (String) item.elementAt(0);
				long icount=0;
				try 
				{
				 icount = Long.parseLong(scount);
				}
				catch (Exception ex)
				{
				Util.logger.info("icount ko co");
				}
				if (icount > 150000) {
					Util.logger.info( "checkspam:userid=" + user_id
							+ " @amount:>150.000vnd");
					sRet[0] = "1";

					//sRet[1] = "Tin nhan SPAM. Ban khong duoc su dung dich vu qua 150.000d/ngay. Vui long goi 1900561558 de duoc huong dan them";
					sRet[1] = Constants._prop.getProperty("spam.mt."
							+ operator,
							"Ban da vi pham quy dinh chong Spam");
					return sRet;
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return sRet;
	}


	public static Vector getVectorTable(Connection cnn, String strSQL)
			throws Exception {
		Vector vt = null;
		PreparedStatement pstm = null;
		Vector vtValue = null;
		ResultSet rs = null;

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			vt = DBUtil.convertToVector(rs);
			// System.err.println("AAA:" + vt.size());
		} catch (SQLException ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return vt;
	}

	public static List getListTable(Connection cnn, String strSQL)
			throws Exception {
		List lst = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List lsValue = null;

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (lsValue != null) {
				for (int i = 0; i < lsValue.size(); i++) {
					pstm.setString(i + 1, lsValue.get(i).toString());
				}
				lsValue = null;
			}
			rs = pstm.executeQuery();
			lst = DBUtil.convertToList(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return lst;
	}

	public static List getListTablePartition(Connection cnn, String strSQL)
			throws Exception {
		List lst = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List lsValue = null;

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (lsValue != null) {
				for (int i = 0; i < lsValue.size(); i++) {
					pstm.setString(i + 1, lsValue.get(i).toString());
				}
				lsValue = null;
			}
			rs = pstm.executeQuery();
			lst = DBUtil.convertToList(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			lst = null;

		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return lst;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Convert data from ResultSet into Vector
	 * 
	 * @param rsData
	 *            Opened ResultSet
	 * @throws Exception
	 *             when working with ResultSet
	 */
	// ///////////////////////////////////////////////////////////////
	public static Vector convertToVector(ResultSet rsData) throws Exception {
		Vector vctReturn = new Vector();
		int iColumnCount = rsData.getMetaData().getColumnCount();
		while (rsData.next()) {
			Vector vctRow = new Vector();
			for (int i = 1; i <= iColumnCount; i++) {
				String strValue = rsData.getString(i);
				if (strValue == null) {
					strValue = "";
				}
				vctRow.addElement(strValue);
			}
			vctReturn.addElement(vctRow);
		}
		vctReturn.trimToSize();
		return vctReturn;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Convert data from ResultSet into List
	 * 
	 * @param rsData
	 *            Opened ResultSet
	 * @throws Exception
	 *             when working with ResultSet
	 */
	// ///////////////////////////////////////////////////////////////
	public static List convertToList(ResultSet rsData) throws Exception {
		List lstReturn = new ArrayList();
		int iColumnCount = rsData.getMetaData().getColumnCount();
		while (rsData.next()) {
			List lstRow = new ArrayList();
			for (int i = 1; i <= iColumnCount; i++) {
				String strValue = rsData.getString(i);
				if (strValue == null) {
					strValue = "";
				}
				lstRow.add(strValue);
			}
			lstReturn.add(lstRow);
		}
		return lstReturn;
	}

	public static String addCondition(String strField, String strValue) {
		String strReturn = "";
		if (!strValue.equals("") && strValue.indexOf("%") == -1) {
			strReturn = " AND " + strField + " = '" + strValue + "' ";
		} else if (!strValue.equals("")) {
			strReturn = " AND " + strField + " like '" + strValue + "' ";
		}
		return strReturn;
	}

	public static String addDateCondition(String strField, String strValue,
			String strCompare) {
		String strReturn = "";
		if (!strValue.equals("")) {
			strReturn = " AND " + strField + " " + strCompare + " TO_DATE('"
					+ strValue + "','DD/MM/YYYY')";
		}
		return strReturn;
	}

	public static String addDateTextCondition(String strField, String strValue,
			String strCompare) {
		String strReturn = "";
		if (!strValue.equals("")) {
			// to_date(TO_CHAR (param2, 'dd-MM-yyyy hh24:mi:ss'),'dd-mm-yyyy
			// hh24:mi:ss')
			// strReturn = " AND " + strField + " " + strCompare + " TO_DATE('"
			// + strValue + "','DD/MM/YYYY')";
			strReturn = " AND to_date(TO_CHAR (" + strField
					+ ", 'dd-MM-yyyy hh24:mi:ss'),'dd-mm-yyyy hh24:mi:ss') "
					+ strCompare + " TO_DATE('" + strValue + "','DD/MM/YYYY')";
		}
		return strReturn;
	}

	public static String addTimeCondition(String strField, String strValue,
			String strCompare) {
		String strReturn = "";
		if (!strValue.equals("")) {
			strReturn = " AND " + strField + " " + strCompare + " TO_DATE('"
					+ strValue + "','DD/MM/YYYY hh24:mi:ss')";
		}
		return strReturn;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(PreparedStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(CallableStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Statement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(ResultSet obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Connection obj) {
		try {
			if (obj != null) {
				if (!obj.isClosed()) {
					if (!obj.getAutoCommit()) {
						obj.rollback();
					}
					obj.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getDayOfWeek(Timestamp ts) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(ts.getTime()));
		return calendar.get(calendar.DAY_OF_WEEK);
	}

	public static int getDayOfWeek() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(ts.getTime()));
		return calendar.get(calendar.DAY_OF_WEEK);
	}

	public static int getHourOfDay() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(ts.getTime()));
		return calendar.get(calendar.HOUR_OF_DAY);
	}

	public static int getHourOfDay(Timestamp ts) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(ts.getTime()));
		return calendar.get(calendar.HOUR_OF_DAY);
	}

	public static int executeSQL(Connection obj, String sql) {

		PreparedStatement statement = null;

		try {

			statement = obj.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.info("DBUtil" + ": uppdate Statement: "
					+ e.getMessage());
			return -1;
		} catch (Exception e) {
			Util.logger.info("DBUtil" + ": uppdate Statement: "
					+ e.getMessage());
			return -1;
		} finally {
			closeObject(statement);

		}
	}

	public static int sendMT(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("DBUtil@sendMT@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("DBUtil@sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ msgObject.getUserid()
								+ "@TO"
								+ msgObject.getServiceid()
								+ "@"
								+ msgObject.getUsertext()
								+ "@requestid="
								+ msgObject.getRequestid().toString());
				return -1;
			}
			sqlString = "INSERT INTO ems_send_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,"
					+ " MESSAGE_ID, CONTENT_TYPE,cpid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, msgObject.getUsertext());
			statement.setTimestamp(6, null);
			statement.setTimestamp(7, null);
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getMsgtype());
			statement.setBigDecimal(10, msgObject.getRequestid());
			statement.setString(11, "1");
			statement.setInt(12, msgObject.getContenttype());
			statement.setInt(13, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("DBUtil@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("DBUtil@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("DBUtil@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

	public static int Alert(String domain, String issue, String level,
			String alertmsg, String contact) {

		Connection connection = null;
		PreparedStatement stmt = null;
		DBPool dbpool = new DBPool();
		String newissue = issue;
		if (issue.length() > 20) {
			newissue = issue.substring(0, 19);
		}
		String newalert = alertmsg;
		if (newalert.length() > 130) {
			newalert = newalert.substring(0, 130);
		}
		String sSQL = "insert into msg_alerter( domain, issue, level,alertmsg,contact) "
				+ "values(?,?,?,?,?)";
		try {
			if (connection == null)
				connection = dbpool.getConnection("alert");
			stmt = connection.prepareStatement(sSQL);
			stmt.setString(1, domain);
			stmt.setString(2, newissue);
			stmt.setString(3, level);
			stmt.setString(4, newalert);
			stmt.setString(5, contact);
			if (stmt.executeUpdate() == -1) {
				return -1;
			}
			return 1;
		} catch (Exception ex) {
			Util.logger.info("DBTools" + "{Alert Error:}" + ex.getMessage());

			return -1;
		} finally {
			dbpool.cleanup(connection, stmt);
		}

	}

}
