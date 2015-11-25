package phase4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;
import icom.common.Util;

public class BLGroupMOCreate extends QuestionManager{

	private String className = "BLGroupMOCreate";
	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		String[] arrInfo = msgObject.getUsertext().split(" ");

		String strCode = arrInfo[0].toUpperCase();
		DBInsert dbInsert = new DBInsert();

		Ph4Common cmmObj = new Ph4Common();

		if (arrInfo.length < 2) {
			String userText = keyword.getErrMsg();
			msgObject.setUsertext(userText);
			dbInsert.sendMT(msgObject);
			return null;
		}

		// create new BINH LUAN GROUP
		// 1. Check Valid Group Name
		// 2. check group name is exist?
		String groupName = arrInfo[1].trim();
				
		if (!cmmObj.isGroupNameValid(groupName)) {
			// Send Invalid MT
			String userText = keyword.getExistMsg();
			msgObject.setUsertext("Tin nhan chua dung cu phap, soan BLT_Tennhom_SDT gui 9209," +
					" ten nhom viet lien, khong dai qua 20 ky tu, khong dung ky tu dac biet." +
					" DTHT: 9244. Chi tiet:http://m.mplus.vn");
			dbInsert.sendMT(msgObject);
			return null;

		}
		
		if(!cmmObj.isGroupAdmin(msgObject.getUserid())){
			// not admin
			String userText = BinhLuanConstant.MT_NOT_AUTHENTICATE_CREATE_GROUP;
			msgObject.setUsertext(userText);
			dbInsert.sendMT(msgObject);
			return null;
		}

		if (isExistGroupName(groupName)) {
			String userText = keyword.getExistMsg();
			msgObject.setUsertext(userText);
			dbInsert.sendMT(msgObject);
		} else {
			// 1. Get phone number and check phone number
			// 2. insert binh luan group

			ArrayList<String> arrPhoneMember = new ArrayList<String>();

			for (int i = 2; i < arrInfo.length; i++) {
				String phoneMember = arrInfo[i];
				phoneMember = cmmObj.validPhoneNumber(phoneMember);
				if (cmmObj.isValidPhoneNumber(phoneMember)) {
					arrPhoneMember.add(phoneMember);
				}
			}

			String userText = "";
			if (arrPhoneMember.size() <= 0) {
				userText = BinhLuanConstant.MT_CREATE_GROUP_SUCCESS;
				userText = userText.replace(
						BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
				
				// Insert bl_group
				int result = cmmObj.insertBinhLuanGroup(groupName, msgObject.getUserid());
				if(result != 1){
					userText = "Qua trinh tao nhom cua ban bi loi, xin vui long thu lai!";
				}else{
					BinhLuanGroupObj blGroupObj = cmmObj.getBLGroupObj(groupName);
					ArrayList<String> arrMember = new ArrayList<String>();
					arrMember.add(msgObject.getUserid());
					cmmObj.insertBLGroupMember(blGroupObj.getId(),arrMember);
				}
				
			} else {
				String sMembers = "";
				for (int i = 0; i < arrPhoneMember.size(); i++) {
					if (i == 0) {
						sMembers = arrPhoneMember.get(i);
					} else {
						sMembers = sMembers + "," + arrPhoneMember.get(i);
					}
				}
				
				// Insert bl_group
				int result = cmmObj.insertBinhLuanGroup(groupName, msgObject.getUserid());
				
				// set user text
				if (result == 1) {
					// insert bl_group_member
					BinhLuanGroupObj blGroupObj = cmmObj.getBLGroupObj(groupName);
					ArrayList<String> arrMember = new ArrayList<String>();
					arrMember.add(msgObject.getUserid());
					cmmObj.insertBLGroupMember(blGroupObj.getId(),arrMember);
					
					userText = BinhLuanConstant.MT_CREATE_GROUP_SUCCESS;
					userText = userText.replace(
							BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
					userText = userText.replace(
							BinhLuanConstant.REPLACE_LIST_MEMBERS, sMembers);
				} else {
					userText = "Qua trinh tao nhom cua ban bi loi, xin vui long thu lai!";
				}
			}

			msgObject.setUsertext(userText);
			dbInsert.sendMT(msgObject);
			
			String infoInvite = BinhLuanConstant.MT_GROUP_INVITE;
			infoInvite = infoInvite.replace(BinhLuanConstant.REPLACE_USER_CREATOR, msgObject.getUserid());
			infoInvite = infoInvite.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
			for(int i=0;i<arrPhoneMember.size();i++){
				
				msgObject.setUsertext(infoInvite);
				msgObject.setUserid(arrPhoneMember.get(i));
				dbInsert.sendMT(msgObject);
				
			}

		}

		return null;
	}
	
	private Boolean isExistGroupName(String groupName){
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
						.error(className + "checkGroupName : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error(className + "checkGroupName. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error(className + "checkGroupName. SQLException:"
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
	
	

}
