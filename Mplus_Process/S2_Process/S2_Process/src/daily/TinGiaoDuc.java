package daily;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import sub.DeliveryManager;

public class TinGiaoDuc  extends DeliveryManager{
	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {

		String CLASSNAME = "TinGiaoDuc";

		try {

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

			/*if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu, options=?:" + serviceName
								+ "", "");
				return null;
			}*/

			if ("x".equalsIgnoreCase(MLIST)) {
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
		Util.logger.info("@TinGiaoDuc@SendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send,service name="+ serviceName);
		Vector vtUsers = DBUtil.getListUserFromListSend(serviceName);
		String strInfo = getContent0(serviceName);
		int cUser = vtUsers.size();
		Util.logger.info("@TinGiaoDuc@SendMT2UsersHasCharged: vtUsers="+ cUser);
		for (int i = 0; i <cUser; i++) {
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
			String content = strInfo;
			Util.logger.info("TinGiaoDuc@Noi dung tra ve cho khach hang: "
					+ content);
			if ("".equalsIgnoreCase(content)) {
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
										+ userid + "'");
				// return null;
				DBUtil.Alert("TinGiaoDuc@Infoservices", "RUNING", "major",
						"Kiem tra dich vu: content bi null. de nghi nhap content ngay."
								+ serviceName + "", "");
			}
			
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					content, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0,
					amount, content_id);
			msgObj.setChannelType(channel_type);
			if (DBUtil.sendMTQueue(type, msgObj, CLASSNAME, serviceName, sSplit, i,1) == 1) {
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set last_code = '"
										+ lastcode
										+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where user_id = '"
										+ userid + "'");
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
	

	public String getContent0(String serviceName) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String table ="";
		if(serviceName.equalsIgnoreCase("KHOIA")) table = "TextKhoiA";
		if(serviceName.equalsIgnoreCase("KHOIB")) table = "TextKhoiB";
		if(serviceName.equalsIgnoreCase("KHOIC")) table = "TextKhoiC";
		if(serviceName.equalsIgnoreCase("KHOID")) table = "TextKhoiD";
		if(serviceName.equalsIgnoreCase("TINMT")) table = "TextDiemDH";
		try {

			connection = dbpool.getConnection("content2012");

			String query = " select TOP 1 Content , returndate " +
							" from "+table+" where returndate <= getdate() ORDER BY returndate DESC ";

			Util.logger.info("TINGIOADUC: Sql select content : sql =" + query);

			Vector result = DBUtil.getVectorTable(connection, query);
			if (result.size() == 0) {
				Util.logger.error("TINGIAODUC: Cannot get content");
				return null;
			} else {
				for (int i = 0; i < result.size();i++) {
					Vector item = (Vector) result.elementAt(i);
					return (String) item.elementAt(0);
				}
			}
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getInfo: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);

		} finally {
			dbpool.cleanup(connection);
		}
		return null;
	}
	
	
}
