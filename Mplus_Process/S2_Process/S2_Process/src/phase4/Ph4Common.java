package phase4;

import icom.DBPool;
import icom.MsgObject;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ph4Common {

	public static String TBL_CHARGE_ONLINE = "mplus_charge_online";
	public static String TBL_CHARGE_ONLINE_RESULT = "mplus_charge_online_result";
	
	public static String[] VMS_NUMBER = {"090","093","0120","0121","0122","0124","0126","0128"};
	
	String sclassname = "Ph4Common";
	
	public Ph4Common(){
		
	}
	
	public int insertChargeOnlineNew(String tblName, MsgObject msgObject) {
		
		String stringLog = "Ph4Common@insertChargeOnline:: " + " table = "
				+ tblName + "; userId = " + msgObject.getUserid()
				+ "; service name = " + msgObject.getCommandCode()
				+ "; amount = " + msgObject.getAmount();

		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@sendMT: Error connection == null"
						+ msgObject.getUserid() + "\tTO"
						+ msgObject.getServiceid() + "\t"
						+ msgObject.getUsertext() );
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO "
					+ tblName
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, RESULT_CHARGE,"
					+ " MESSAGE_TYPE, REQUEST_ID, CONTENT_ID, AMOUNT, CHANNEL_TYPE, SERVICE_NAME, SUBMIT_DATE)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			Util.logger.info(sclassname + ":: " + stringLog);

			stmt = connection.prepareStatement(sqlString);
			stmt.setString(1, msgObject.getUserid());
			stmt.setString(2, msgObject.getServiceid());
			stmt.setString(3, msgObject.getMobileoperator());
			stmt.setString(4, msgObject.getCommandCode());
			stmt.setInt(5, msgObject.getContenttype());
			stmt.setString(6, msgObject.getUsertext());
			stmt.setInt(7, 0);
			stmt.setString(8, msgObject.getMsgtype() + "");
			stmt.setBigDecimal(9, msgObject.getRequestid());
			stmt.setString(10, msgObject.getContentId() + "");
			stmt.setLong(11, msgObject.getAmount());
			stmt.setString(12, msgObject.getChannelType() + "");
			stmt.setString(13, msgObject.getCommandCode());
			stmt.setString(14, msgObject.getTimeSendMO());

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common Execute Error; " + stringLog);
			} else {
				Util.logger.info("insertChargeOnlineNew SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public ArrayList<ChargeOnlineObject> getBLChargeOnlineResult(){
		
		ArrayList<ChargeOnlineObject> arrResult = new ArrayList<ChargeOnlineObject>();
		
		String sqlQuery = "SELECT ID,USER_ID,COMMAND_CODE,SUBMIT_DATE,INFO,RESULT_CHARGE,REQUEST_ID " +
							" FROM " + TBL_CHARGE_ONLINE_RESULT +
							" WHERE COMMAND_CODE = 'BL' OR COMMAND_CODE = 'BL HOT' " +
							"OR COMMAND_CODE = 'BLSKY' OR COMMAND_CODE = 'BL HOTSKY'  limit 100";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					ChargeOnlineObject chargeObj = new ChargeOnlineObject();
					chargeObj.setId(rs.getInt("ID"));
					chargeObj.setUserId(rs.getString("USER_ID"));
					chargeObj.setCommandCode(rs.getString("COMMAND_CODE"));
					chargeObj.setSubmitDate(rs.getString("SUBMIT_DATE"));
					chargeObj.setInfo(rs.getString("INFO"));
					chargeObj.setResultCharge(rs.getInt("RESULT_CHARGE"));
					chargeObj.setRequestId(rs.getString("REQUEST_ID"));
					
					arrResult.add(chargeObj);
				}
			} else {
				Util.logger
						.error("getChargeOnlineResult : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("getChargeOnlineResult :. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("getChargeOnlineResult. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrResult;
		
	}
	
	public int insertBLContent(ChargeOnlineObject resultObj){
		
		String stringLog = "Ph4Common@insertBLContent:: " 
			+ " table = bl_content"
			+ " user_id = " + resultObj.getUserId()
			+ "; info = " + resultObj.getInfo();
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@sendMT: Error connection == null"
						+ resultObj.getUserId() + "\tTO"
						+ resultObj.getServiceId() + "\t"
						+ resultObj.getInfo() );
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO bl_content"
					+ "( user_id, blid, info, time_send_mo)"
					+ " VALUES (?, ?, ?, ?)";
			Util.logger.info(sclassname + ":: " + stringLog);

			stmt = connection.prepareStatement(sqlString);
			
			stmt.setString(1, resultObj.getUserId());
			stmt.setString(2, resultObj.getBlid());
			stmt.setString(3, resultObj.getInfo());
			stmt.setString(4, resultObj.getSubmitDate());

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common insertBLContent Execute Error; " + stringLog);
			} else {
				Util.logger.info("insertBLContent SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int insertBLEventContent(String userId, String info,String blId){
		
		String stringLog = "Ph4Common@insertBLContent:: " 
			+ " table = bl_content"
			+ " user_id = " + userId
			+ "; info = " +  info;
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@insertBLEventContent: Error connection == null");
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO bl_content"
					+ "( user_id, blid, info)"
					+ " VALUES (?, ?, ?)";
			Util.logger.info(sclassname + ":: " + stringLog);

			stmt = connection.prepareStatement(sqlString);
			
			stmt.setString(1, userId);
			stmt.setString(2, blId);
			stmt.setString(3, info);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common insertBLEventContent Execute Error; " + stringLog);
			} else {
				Util.logger.info("insertBLEventContent SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public String[] getBLContentHot(String[] arrBlId){
		
		String[] arrContentHot = new String[3];

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT subcode, content FROM Comments " +
				" WHERE subcode IN (";
		
		for(int i = 0;i<arrBlId.length;i++){
			if(i>0)
			sqlQuery = sqlQuery + ",";
			sqlQuery = sqlQuery + "'" + arrBlId[i] + "'";
		}
			
		sqlQuery = sqlQuery + ")";
		
		
		if(arrBlId == null || arrBlId.length == 0 || arrBlId[0] == null){
			sqlQuery = " select TOP 3 content,subcode,drafttime " +
			" from comments where drafttime <= getdate() AND status = 2 " +
			" ORDER BY drafttime DESC ";
		}
		
		Util.logger.info("getBLContentHot:: SQL = " + sqlQuery);
		
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection("content2012");
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				int i = 1;
				int j = 0;
				while (rs.next()) {
					arrContentHot[j] = i + "-" + rs.getString("content");
					j = j + 1;
					i = i + 1;
				}
			} else {
				Util.logger
						.error("getBLContentHot: execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("getBLContentHot :. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("getBLContentHot. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrContentHot;

	}
	
	public String[][] getBinhLuanIdHot(){
		
		String[][] arrHot = new String[3][2];
		
		String sqlQuery = "SELECT blid, COUNT(*) AS number FROM " +
					" bl_content WHERE active = 1 GROUP BY blid ORDER BY number DESC LIMIT 3 ";
		
		Util.logger.info("BL HOT:: getBinhLuanIdHot, SQL = " + sqlQuery);
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				int i = 0;
				while (rs.next()) {
					
					arrHot[i][0] = rs.getString("blid");
					arrHot[i][1] = rs.getString("number");
					i = i + 1;
				}
			} else {
				Util.logger
						.error("getBinhLuanIdHot : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("getBinhLuanIdHot :. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("getBinhLuanIdHot. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrHot;
		
	}
	
	public String getKeyword(String info,String serviceName){
		
		String strKey = "";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT keyword FROM keywords WHERE service_name ='"
				+ serviceName + "' AND keyword LIKE 'BL%' "
				+ " ORDER BY LENGTH(keyword) DESC";
		
		try {
			
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					strKey = rs.getString("keyword");
					if(info.toUpperCase().startsWith(strKey)) break;
				}
			} else {
				Util.logger
						.error("getKeyword: execute Error!!");
			}
			
		} catch (SQLException ex3) {
			Util.logger
					.error("getKeyword: SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("getKeyword: SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return strKey;
		
	}
	
	public Boolean isValidPhoneNumber(String userId){
						
		Pattern pttPhone = Pattern.compile("84((93)|(90)|(120)|(121)|(122)|(124)|(126)|(128))\\d{7}");
		Matcher matchPhone = pttPhone.matcher(userId);
		
		return matchPhone.matches();
				
	}
	
	public String validPhoneNumber(String userId){
		
		userId = userId.replace(",", "");
		userId = userId.replace("+","");
		userId = userId.replace(".","");
		userId = userId.replace(":","");
		userId = userId.replace(";", "");
		userId = userId.replace("(", "");
		userId = userId.replace(")", "");
		userId = userId.replace("[", "");
		userId = userId.replace("]", "");
		userId = userId.replace("-","");
		
		if(userId.startsWith("0")){
			userId = userId.substring(1);
		}
		if(!userId.startsWith("84")){
			userId = "84" + userId;
		}
		return userId;
	}
	
	/**
	 * 
	 * @param groupName
	 * @return true if groupName is valid
	 * else return false
	 */
	public Boolean isGroupNameValid(String groupName){
		
		if(groupName.equals("")) return false;
		
		if(groupName.length()>20) return false;
		
		Pattern ptt = Pattern.compile("\\W");
		Matcher mch = ptt.matcher(groupName);
		
		if(mch.find()) return false;
		
		return true;
	}
	
	public Boolean isFreeList(String userId, String commandCode) {

		Boolean check = false;
		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM free_users WHERE "
				+ " user_id = '" + userId + "' AND command_code = '"
				+ commandCode + "'";
		// System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger.error("Ph4Common - isFreeList : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("Ph4Common - isFreeList. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Ph4Common- isFreeList. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		// System.out.println("count = " + count);
		if (count > 0) {
			check = true;
		}

		return check;
	}
	
	/**
	 * insert into table: bl_group
	 * @param groupName
	 * @param userCreate
	 * @param arrMember
	 * @return
	 */
	public int insertBinhLuanGroup(String groupName, String userCreate ){
		
		String stringLog = "Ph4Common@insertBinhLuanGroup:: " 
			+ " table = bl_group"
			+ " user_create = " + userCreate;
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@insertBinhLuanGroup: Error connection == null"
						+ stringLog );
				return -1;
			}

			sqlString = "INSERT INTO bl_group(group_name,user_create)" +
					"VALUES(?,?)";
			
			Util.logger.info(sclassname + "::insertBinhLuanGroup: " + stringLog);

			stmt = connection.prepareStatement(sqlString);
			
			stmt.setString(1, groupName);
			stmt.setString(2, userCreate);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common insertBinhLuanGroup Execute Error; " + stringLog);
			} else {
				Util.logger.info("insertBLContent SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	public Boolean isExistGroupName(String groupName){
		Boolean check = false;

		int count = 0;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM " +
				" bl_group WHERE " +
				" group_name = '" + groupName + "'";
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger
						.error(sclassname + "checkGroupName : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "checkGroupName. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "checkGroupName. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
//		System.out.println("count = " + count);
		if(count > 0){
			check = true;
		}
		
		return check;

	}
	
	/**
	 * 
	 * @param groupName
	 * @param userCreate
	 * @return <b>ID of record.<b/><br/>
	 *  if (id <= 0) this record is not exist! 
	 */
	public int isUserCreate(String groupName,String userCreate){


		int id = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT id FROM bl_group " +
							" WHERE group_name = '" + groupName 
							+ "' AND user_create = '" + userCreate + "'";
//		System.out.println("isExistAmount: " + sqlQuery);
		Util.logger.info("isUserCreate ? <CHECK>, sql = " + sqlQuery);
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					id = rs.getInt("id");
				}
			} else {
				Util.logger
						.error(sclassname + "isUserCreate : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "isUserCreate. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "isUserCreate. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		
		return id;

	}
	
	public int moveToBLGroupCancel(String groupName, String userCreate){
		
		String stringLog = "Ph4Common@moveToBLGroupCancel:: " 
			+ " table = bl_group_cancel"
			+ " group_name = " + groupName
			+ "; user_create = " + userCreate;
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ " table = bl_group_cancel"
						+ " group_name = " + groupName
						+ "; user_create = " + userCreate);
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO bl_group_cancel(groupid,group_name,user_create,time_create) "
						+ "(SELECT id,group_name,user_create,time_create FROM bl_group " +
						"WHERE group_name = '" + groupName 
						+"' AND user_create = '" + userCreate +"')";
			Util.logger.info(sclassname + ":: " + stringLog);

			stmt = connection.prepareStatement(sqlString);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common moveToBLGroupCancel Execute Error; " + stringLog);
			} else {
				Util.logger.info("moveToBLGroupCancel SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	public int moveToMemberCancel(int memberId){
		
		String stringLog = " Ph4Common@moveToMemberCancel:: " 
			+ " table = bl_group_member_cancel"
			+ " memberId = " + memberId;
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ stringLog);
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO bl_group_member_cancel(user_id,groupid,time_join)" +
					" (SELECT user_id,groupid,time_join FROM bl_group_member WHERE id = " + memberId + ")";
;
			Util.logger.info(sclassname + ":: moveToMemberCancel " + sqlString);

			stmt = connection.prepareStatement(sqlString);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common moveToMemberCancel Execute Error; " + stringLog);
			} else {
				Util.logger.info("moveToMemberCancel SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	public int moveAllToMemberCancel(int groupId){
		
		String stringLog = " Ph4Common@moveAllToMemberCancel:: " 
			+ " table = bl_group_member_cancel"
			+ " groupId = " + groupId;
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ stringLog);
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO bl_group_member_cancel(user_id,groupid,time_join)" +
					" (SELECT user_id,groupid,time_join FROM bl_group_member WHERE groupid = " + groupId + ")";
;
			Util.logger.info(sclassname + ":: moveAllToMemberCancel " + sqlString);

			stmt = connection.prepareStatement(sqlString);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common moveAllToMemberCancel Execute Error; " + stringLog);
			} else {
				Util.logger.info("moveAllToMemberCancel SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	public BinhLuanGroupObj getBLGroupObj(String groupName, String userCreate){
		
		BinhLuanGroupObj blGroupObj = new BinhLuanGroupObj(); 
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT id,group_name,user_create,time_create FROM bl_group " +
							" WHERE group_name = '" + groupName 
							+ "' AND user_create = '" + userCreate + "'";
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					blGroupObj.setId(rs.getInt("id"));
					blGroupObj.setGroupName(rs.getString("group_name"));
					blGroupObj.setUserCreate(rs.getString("user_create"));
					blGroupObj.setTimeCreate(rs.getString("time_create"));
				}
			} else {
				Util.logger
						.error(sclassname + "getBLGroupObj : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "getBLGroupObj. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "getBLGroupObj. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return blGroupObj;
	}
	
	public BLGroupMemberObj getBLGroupMemberObj(int groupId, String userMember){
		
		BLGroupMemberObj memberObj = new BLGroupMemberObj(); 
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT id,user_id,groupid,time_join,is_charging" +
				" FROM bl_group_member WHERE groupid= " + groupId 
				+ " AND user_id='" + userMember + "'";
		Util.logger.info(sclassname + ":: getBLGroupMemberObj: sql = " + sqlQuery);
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					memberObj.setId(rs.getInt("id"));
					memberObj.setUserId(rs.getString("user_id"));
					memberObj.setGroupId(rs.getInt("groupid"));
					memberObj.setTimeJoin(rs.getString("time_join"));
					memberObj.setIsCharging(rs.getInt("is_charging"));
				}
			} else {
				Util.logger
						.error(sclassname + "getBLGroupMemberObj : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "getBLGroupMemberObj. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "getBLGroupMemberObj. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return memberObj;
	}
	
	public ArrayList<BLGroupMemberObj> getArrayMember(int groupId){
		
		ArrayList<BLGroupMemberObj> arrMember = new ArrayList<BLGroupMemberObj>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT id,user_id,groupid,time_join,is_charging" +
				" FROM bl_group_member WHERE groupid= " + groupId ;
		Util.logger.info(sclassname + ":: getArrayMember: sql = " + sqlQuery);
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					BLGroupMemberObj memberObj = new BLGroupMemberObj(); 
					memberObj.setId(rs.getInt("id"));
					memberObj.setUserId(rs.getString("user_id"));
					memberObj.setGroupId(rs.getInt("groupid"));
					memberObj.setTimeJoin(rs.getString("time_join"));
					memberObj.setIsCharging(rs.getInt("is_charging"));
					arrMember.add(memberObj);
				}
			} else {
				Util.logger
						.error(sclassname + "getArrayMember : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "getArrayMember. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "getArrayMember. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMember;
	}
	
	public ArrayList<BLGroupMemberObj> getMemberToCharge(){
		
		ArrayList<BLGroupMemberObj> arrMember = new ArrayList<BLGroupMemberObj>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT id,user_id,groupid,time_join,is_charging" +
				" FROM bl_group_member WHERE is_charging = 0";
		Util.logger.info(sclassname + ":: getArrayMember: sql = " + sqlQuery);
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					BLGroupMemberObj memberObj = new BLGroupMemberObj(); 
					memberObj.setId(rs.getInt("id"));
					memberObj.setUserId(rs.getString("user_id"));
					memberObj.setGroupId(rs.getInt("groupid"));
					memberObj.setTimeJoin(rs.getString("time_join"));
					memberObj.setIsCharging(rs.getInt("is_charging"));
					arrMember.add(memberObj);
				}
			} else {
				Util.logger
						.error(sclassname + "getMemberToCharge : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "getMemberToCharge. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "getMemberToCharge. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMember;
	}
	
	public BinhLuanGroupObj getBLGroupObj(String groupName){
		
		BinhLuanGroupObj blGroupObj = new BinhLuanGroupObj(); 
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT id,group_name,user_create,time_create FROM bl_group " +
							" WHERE group_name = '" + groupName + "'";
		
		Util.logger.info("getBLGroupObj:: " + sqlQuery );
		
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					blGroupObj.setId(rs.getInt("id"));
					blGroupObj.setGroupName(rs.getString("group_name"));
					blGroupObj.setUserCreate(rs.getString("user_create"));
					blGroupObj.setTimeCreate(rs.getString("time_create"));
				}
			} else {
				Util.logger
						.error(sclassname + "isUserCreate : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "isUserCreate. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "isUserCreate. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return blGroupObj;
	}
	
	public ArrayList<BinhLuanGroupObj> getArrGroup(String userCreate){
		
		ArrayList<BinhLuanGroupObj> arrGroup = new ArrayList<BinhLuanGroupObj>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT id,group_name,user_create,time_create FROM bl_group " +
							" WHERE user_create = '" + userCreate + "'";
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					BinhLuanGroupObj blGroupObj = new BinhLuanGroupObj(); 
					blGroupObj.setId(rs.getInt("id"));
					blGroupObj.setGroupName(rs.getString("group_name"));
					blGroupObj.setUserCreate(rs.getString("user_create"));
					blGroupObj.setTimeCreate(rs.getString("time_create"));
					arrGroup.add(blGroupObj);
				}
			} else {
				Util.logger
						.error(sclassname + "isUserCreate : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "isUserCreate. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "isUserCreate. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrGroup;
	}
	
	public int deleteMember(int groupId, String userId){
		
		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				iReturn = -1;
			}

			sqlString = "DELETE FROM bl_group_member"
					+ " WHERE (user_id IN ("+ userId + ") ) AND groupid = " + groupId;
			
			Util.logger.info(sclassname + "deleteMember ###; SQL = " + sqlString);
			
			statement = connection.prepareStatement(sqlString);
			
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("DBDelete deleteTableByID Error@ SQL = " + sqlString);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis(sclassname + ":: deleteMember: Error@ID=" + sqlString);
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis(sclassname + ":: deleteMember: Error@ID=" + sqlString);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	public int deleteAllMember(int groupId){
		
		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				
				iReturn = -1;
			}

			sqlString = "DELETE FROM bl_group_member"
					+ " WHERE groupid = " + groupId;
			
			Util.logger.info(sclassname + "deleteMember ###; SQL = " + sqlString);
			
			statement = connection.prepareStatement(sqlString);
			
			if (statement.executeUpdate() != 1) {
				Util.logger
						.crisis("DBDelete deleteAllMember Error@ SQL = " + sqlString);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger
			.crisis(sclassname + ":: deleteAllMember: Error@ID=" + sqlString);
			iReturn = -1;
		} catch (Exception e) {
			Util.logger
			.crisis(sclassname + ":: deleteAllMember: Error@ID=" + sqlString);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	public Boolean isGroupAdmin(String userId){
		
		Boolean check = false;
		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM bl_group_admin WHERE "
				+ " user_id = '" + userId + "'";
		// System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger.error("Ph4Common - isGroupAdmin : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("Ph4Common - isGroupAdmin. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Ph4Common- isGroupAdmin. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		// System.out.println("count = " + count);
		if (count > 0) {
			check = true;
		}

		return check;
	}
	
	public Boolean isMember(int groupId, String userMember){
		
		Boolean check = false;
		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM bl_group_member WHERE "
				+ " groupid = " + groupId + " AND user_id = '" + userMember + "'";
		// System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger.error("Ph4Common - isMember : execute Error!! sql = " + sqlQuery);
			}
		} catch (SQLException ex3) {
			Util.logger.error("Ph4Common - isGroupAdmin. sql = " + sqlQuery
					+ " SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Ph4Common - isGroupAdmin. sql = " + sqlQuery
					+ " SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		// System.out.println("count = " + count);
		if (count > 0) {
			check = true;
		}

		return check;
		
	}
	
	/**
	 * insert into table: bl_group
	 * @param groupName
	 * @param userCreate
	 * @param arrMember
	 * @return
	 */
	public int insertBLGroupMember(int groupId, ArrayList<String> arrUser){
		
		String sqlString = "INSERT INTO bl_group_member(user_id,groupid)"
				+ "VALUES";

		for (int i = 0; i < arrUser.size(); i++) {

			if (i > 0) sqlString = sqlString + ",";
			
			sqlString = sqlString + "('" + arrUser.get(i) + "'," + groupId
					+ ")";

		}
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@sendMT: Error connection == null"
						+ sqlString );
				return -1;
			}

			
									
			Util.logger.info(sclassname + "::insertBinhLuanGroup: " + sqlString);

			stmt = connection.prepareStatement(sqlString);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common insertBinhLuanGroup Execute Error; " + sqlString);
			} else {
				Util.logger.info("insertBLContent SUCCESSFUL; " + sqlString);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	/**
	 * 
	 * @param id
	 * @param isCharging (0: not charge, 1: charge ok, 2: sending charging)
	 * @return
	 */
	public int updateChargingMember(int memberId, int isCharging) {

		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@updateChargingMember: Error connection == null");
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "UPDATE bl_group_member SET is_charging = " + isCharging 
						+ " WHERE id = " + memberId;
			
			Util.logger.info("updateChargingMember:: SQL = " + sqlString);
			
			stmt = connection.prepareStatement(sqlString);


			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common Execute Error; " + sqlString);
			} else {
				Util.logger.info("updateChargingMember SUCCESSFUL; id = " + memberId);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int updateAllChargingMember(int isCharging) {

		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@updateAllChargingMember: Error connection == null");
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "UPDATE bl_group_member SET is_charging = " + isCharging;
			
			Util.logger.info("updateAllChargingMember:: SQL = " + sqlString);
			
			stmt = connection.prepareStatement(sqlString);


			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common Execute Error; " + sqlString);
			} else {
				Util.logger.info("updateAllChargingMember SUCCESSFUL");
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int insertBLGroupContent(String userId, String groupName, String info){
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@insertBLGroupContent: Error connection == null");
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO bl_group_content(user_id,group_name,info)" +
					" VALUES('" + userId + "','" + groupName + "','" + info + "')";
			Util.logger.info(sclassname + ":: insertBLGroupContent " + sqlString);

			stmt = connection.prepareStatement(sqlString);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common insertBLGroupContent Execute Error; sql =" + sqlString);
			} else {
				Util.logger.info("insertBLGroupContent SUCCESSFUL; ");
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public ArrayList<ChargeOnlineObject> getChargeOnlineResult(){
		
		ArrayList<ChargeOnlineObject> arrResult = new ArrayList<ChargeOnlineObject>();
		
		String sqlQuery = "SELECT ID,USER_ID,COMMAND_CODE,SUBMIT_DATE,INFO,RESULT_CHARGE,REQUEST_ID " +
							" FROM " + TBL_CHARGE_ONLINE_RESULT +
							" limit 100";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					ChargeOnlineObject chargeObj = new ChargeOnlineObject();
					chargeObj.setId(rs.getInt("ID"));
					chargeObj.setUserId(rs.getString("USER_ID"));
					chargeObj.setCommandCode(rs.getString("COMMAND_CODE"));
					chargeObj.setSubmitDate(rs.getString("SUBMIT_DATE"));
					chargeObj.setInfo(rs.getString("INFO"));
					chargeObj.setResultCharge(rs.getInt("RESULT_CHARGE"));
					chargeObj.setRequestId(rs.getString("REQUEST_ID"));
					
					arrResult.add(chargeObj);
				}
			} else {
				Util.logger
						.error("getChargeOnlineResult : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("getChargeOnlineResult :. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("getChargeOnlineResult. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrResult;
		
	}
	
	public ArrayList<BLGroupContentObj> getGroupContentToSend(){
		
		ArrayList<BLGroupContentObj> arrContent = new ArrayList<BLGroupContentObj>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT id,user_id,group_name,info,time_send_mo,active,status_mt" +
				" FROM bl_group_content WHERE active = 1 AND status_mt = 0 LIMIT 100";
		
//		Util.logger.info("getGroupContentToSend:: SQL = " + sqlQuery);
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					BLGroupContentObj contentObj = new BLGroupContentObj();
					contentObj.setId(rs.getInt("id"));
					contentObj.setUserId(rs.getString("user_id"));
					contentObj.setGroupName(rs.getString("group_name"));
					contentObj.setInfo(rs.getString("info"));
					contentObj.setTimeSendMo(rs.getString("time_send_mo"));
					contentObj.setActive(rs.getInt("active"));
					contentObj.setStatusMT(rs.getInt("status_mt"));
					arrContent.add(contentObj);
				}
			} else {
				Util.logger
						.error(sclassname + "getGroupContentToSend : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(sclassname + "getGroupContentToSend. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(sclassname + "getGroupContentToSend. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrContent;
		
	}
	
	public int updateGroupContentStatusMT(int id, int statusMT) {

		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;
		
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@updateChargingMember: Error connection == null");
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "UPDATE bl_group_content SET status_mt = " + statusMT 
						+ " WHERE id = " + id;
			
			Util.logger.info("updateChargingMember:: SQL = " + sqlString);
			
			stmt = connection.prepareStatement(sqlString);


			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common Execute Error; " + sqlString);
			} else {
				Util.logger.info("updateGroupContentStatusMT SUCCESSFUL; id = " + id);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
}
