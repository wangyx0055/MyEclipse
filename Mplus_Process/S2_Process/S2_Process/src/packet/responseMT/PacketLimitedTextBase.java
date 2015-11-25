package packet.responseMT;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import servicesPkg.MlistInfo;
import servicesPkg.PacketLimitedObj;
import servicesPkg.ServiceMng;
import sub.DeliveryManager;

public class PacketLimitedTextBase extends DeliveryManager {

	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {

		String CLASSNAME = "servicePkg.PacketTextBaseRandom";
		int limitedMT = 30;

		try {
			
			ServiceMng serviceMng = new ServiceMng();
			PacketLimitedObj limitedObj = serviceMng.getPacketLimitedObj(serviceName);
			if(limitedObj != null){
				limitedMT = limitedObj.getNumberMT();
			}

			Util.logger.info(CLASSNAME + ": start:" + serviceName);

			String MLIST = "x";
			String INFO_ID = "x";

			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);

			INFO_ID = Util.getStringfromHashMap(_option, "infoid", "x");
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");

			String DBCONTENT = Util.getStringfromHashMap(_option, "dbcontent", "content");

			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");

			// Lay du lieu cua cai nao
			String type = Util.getStringfromHashMap(_option, "type", "2");

			if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu, options=?:" + serviceName
								+ "", "");
				return null;
			}

			String sqlUpdateRunning = "update services set result="
					+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);

			// gui mt den nhung thue bao da charge tu truoc
			SendMT2UsersHasCharged(ssid, serviceName, MLIST, DBCONTENT, type,
					CLASSNAME, notcharge, INFO_ID, sSplit);
			
			handleLimited(MLIST, limitedMT);
			
			// gui mt den nhung thue bao vua moi dang ky
			icom.Services service  = new icom.Services(); 
			try{
				service = icom.Services.getService(serviceName, Sender.loadconfig.hServices);
			}catch(Exception ex)
			{
				Util.logger.error("Textbaserandom: co loi khi get service");
				DBUtil
				.Alert(
						"Process.VMS",
						"Textbaserandom",
						"major",
						"Textbaserandom.Exception: co loi khi get service.ex=" + ex.toString(),"");
			}

			// cap nhat tinh trang
			String sqlUpdate = "update services set result=" + Constants.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_FAILED
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
			DBUtil.Alert("DeliveryDaily@Textbaserandom", "RUNING", "major",
					"Kiem tra dich vu:" + serviceName + "", "");

		}
				
		return null;
	}
	private void SendMT2UsersHasCharged(String ssid, String serviceName,
			String MLIST, String DBCONTENT, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String sSplit) throws Exception {
		Util.logger.info("@Textbaserandom@SendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send,service name="+ serviceName);
		
		Vector vtUsers = DBUtil.getListUserFromListSend(serviceName);
		int cUser = vtUsers.size();
		Util.logger.info("@Textbaserandom@SendMT2UsersHasCharged: vtUsers="+ cUser);
		
		for (int i = 0; i < cUser; i++) {

			Vector item = (Vector) vtUsers.elementAt(i);

			String id = (String) item.elementAt(0);
			String userid = (String) item.elementAt(1);
			String serviceid = (String) item.elementAt(2);
			String lastcode = (String) item.elementAt(3);
			String commandcode = (String) item.elementAt(4);
			String requestid = (String) item.elementAt(5);
			String messagetype = (String) item.elementAt(6);
			String mobileoperator = (String) item.elementAt(7);

			int msgtype = Integer.parseInt(messagetype);

			if (notcharge == Constants.MODE_NOTCHARGE) {
				// phan biet viec charge theo goi va charge binh thuong
				msgtype = Integer.parseInt(Constants.MT_PUSH);
			}
			// Amount
			long amount = Long.parseLong((String) item.elementAt(8));
			int content_id = Integer.parseInt((String) item.elementAt(9));
			int channel_type  = Integer.parseInt((String) item.elementAt(11));
			String[] arrContents = getContent0(DBCONTENT, type, lastcode,
					INFO_ID);

			String lastid = arrContents[0];
			String content = arrContents[1];

			Util.logger.info("PacketTextBaseRandom@Noi dung tra ve cho khach hang: " + content);
			// failures: khong gui tin cho KH vi content bi null
			if ("".equalsIgnoreCase(lastid) || "".equalsIgnoreCase(content)) {
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
										+ userid + "'");
				// return null;
				DBUtil.Alert("DeliveryDaily@Textbaserandom", "RUNING", "major",
						"Kiem tra dich vu: content bi null. de nghi nhap content ngay."
								+ serviceName + "", "");
			}
			if (lastcode.length() > 1500) {

				String templastcode = lastcode.substring(500);
				if (templastcode.startsWith(",")) {
					lastcode = "0" + templastcode;
				}
			} else {
				if (!"".equalsIgnoreCase(lastid)) {
					lastcode = lastcode + "," + lastid;
				}
			}
									
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					content, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0,
					amount, content_id);
			
			msgObj.setChannelType(channel_type);
			
			if (DBUtil.sendMTQueue(type, msgObj, CLASSNAME, serviceName, sSplit, i,1) == 1) {
				Util.logger.info("Textbaserandom: charge offline: update last code trong mlist. \tmlist:" + MLIST + "\tuser_id:" + userid+ "\tcommand_code:" + serviceName);
				DBUtil.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set last_code = '"
										+ lastcode
										+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where user_id = '"
										+ userid + "' and upper(command_code) like '"
										+ serviceName.toUpperCase() + "%'");
				/**********
				 * update last code trong list_send
				 * *****/
				Util.logger.info("Textbaserandom: charge offline: update last code trong list_send. \tuser_id:" + userid+ "\tcommand_code:" + serviceName);
				DBUtil.executeSQL(
						"gateway",
						"update list_send "								
								+ " set last_code ='"
								+ lastcode
								+ "' where user_id ='"
								+ userid + "'" + " and upper(command_code) like '"
								+ serviceName.toUpperCase() + "%'");
			} else {
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
										+ userid + "'");
			}
			
			
			
		}
				
	}


	public String[] getContent0(String database, String type, String lastcode,
			String newstypecode) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String content = "content_short";

		// Xac dinh bang nao can lay du lieu
		if ("3".equalsIgnoreCase(type)) {
			content = "content_vi";
		} else if ("2".equalsIgnoreCase(type)) {
			content = "content";
		}

		Util.logger.info("content = " + content);

		String query = "SELECT ID,"
			+ content
			+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE status=1 AND type=2 AND upper(newstypecode) = '"
			+ newstypecode.toUpperCase() + "'";

		query = query + " and id not in (" + lastcode + ") )x ";

		Util.logger.info("Textbaserandom: Sql select content: Sql = " + query);

		try {
			connection = dbpool.getConnection(database);

			Util.logger.info("SQL Query get content: " + query);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "@getContent: queryStatement:" + result.size() + "@"
					+ query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);

				Util.logger.info(this.getClass().getName()
						+ "getContent: record[0]:" + record[0]);
				Util.logger.info(this.getClass().getName()
						+ "getContent: record[1]:" + record[1]);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return record;
		} finally {
			dbpool.cleanup(connection);
		}
	}
	
	
	private void handleLimited(String mlistTable, int limitMT){
		
		ArrayList<MlistInfo> arrMlist = getMlistInfo(mlistTable, limitMT);
		for(int i = 0;i< arrMlist.size(); i++){
			
			MlistInfo mlistObj = arrMlist.get(i);
			MsgObject msgObj = new MsgObject();
			msgObj.setUserid(mlistObj.getUserId());
			msgObj.setCommandCode(mlistObj.getCommandCode());
			msgObj.setServiceName(mlistObj.getService());
			msgObj.setServiceid(mlistObj.getServiceId());
			msgObj.setMobileoperator(mlistObj.getMobiOperator());
			msgObj.setChannelType(mlistObj.getChanelType());
			msgObj.setCompany_id(mlistObj.getCompanyId());
			msgObj.setAmount(mlistObj.getAmount());
			
			Keyword keyword = Sender.loadconfig.getKeyword(mlistObj.getCommandCode()
					.toUpperCase(), mlistObj.getServiceId());
			
			try {
				DBUtil.UnRegisterServices(msgObj, keyword);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private ArrayList<MlistInfo> getMlistInfo(String tableName, int limitMT) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName + " WHERE MT_COUNT >= " + limitMT ;

		ArrayList<MlistInfo> arrMlistInfo = new ArrayList<MlistInfo>();
		MlistInfo mlistInfo;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
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

					arrMlistInfo.add(mlistInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("PacketLimitedTextBase - getMlistInfo. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("PacketLimitedTextBase  - getMlistInfo. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}
	
	
}

