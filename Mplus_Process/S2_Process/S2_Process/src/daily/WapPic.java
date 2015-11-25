package daily;

import icom.Constants;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import sub.DeliveryManager;

public class WapPic extends DeliveryManager {
	String DomainServer = Constants._prop.getProperty("domainServer", "");
	String CLASSNAME = "daily.WapPic";
	
	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {
		try {

			Util.logger.info(CLASSNAME + ": start:" + serviceName);

			String MLIST = "x";
			String INFO_ID = "x";

			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);
			String x = "x";

			// Noi dung can lay
			INFO_ID = Util.getStringfromHashMap(_option, "infoid", "x");

			// Ten bang luu danh sach khach hang
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");

			// Ket noi voi content wappush
			String DBCONTENT = Util.getStringfromHashMap(_option, "dbcontentwp", "wap");

			// Nhom se lay thong tin VMS
			String GID = Util.getStringfromHashMap(_option, "gid", "21");			

			// The loai se lay
			String typehd = "image";
			typehd = Util.getStringfromHashMap(_option, "typehd", "image");

			// Lay du lieu cua cai nao
			// 1 hinh dong, 2 hinh nen, 3:media
			String type = Util.getStringfromHashMap(_option, "type", "1");

			if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				Util.logger.info("Alert");
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + serviceName + "", "");
				return null;
			}

			String sqlUpdateRunning = "update services set result="
					+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);

			// Lay ma wap push
			String code = DBUtil.getCode("gateway", type, INFO_ID);

			Util.logger.info("Ma Hinh nen, nhac chuong, hinh dong tuan nay: "
					+ code);
			
			if ("".equalsIgnoreCase(code)) {
				Util.logger.info("WapPic Code is null");
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"WapPic Code is null, plz check now, serviceName=" + serviceName, "");
				return null;
			}
			
			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");
			
			SendMT2UsersHasCharged(ssid, DBCONTENT, serviceName, MLIST, type, CLASSNAME,
					notcharge, INFO_ID, sSplit, code, typehd, GID);
			
			// gui mt den nhung thue bao vua moi dang ky
			SendMT2UserHaveJustRegister(ssid, DBCONTENT, serviceName, MLIST,
					type, CLASSNAME, notcharge, INFO_ID, code, typehd, GID);
			DBUtil.updateCode("gateway", type, INFO_ID, code);
			// cap nhat tinh trang
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_FAILED
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
			DBUtil.Alert("DeliveryDaily", "RUNING", "major",
					"Kiem tra dich vu:" + serviceName + "", "");
		}
		return null;
	}
	private void SendMT2UsersHasCharged(String ssid, String DBCONTENT, String serviceName,
			String MLIST, String type, String CLASSNAME, int notcharge,
			String INFO_ID, String sSplit, String code, String typehd, String GID) throws Exception {
		Vector vtUsers = DBUtil.getListUserFromListSend(serviceName);

		for (int i = 0; i < vtUsers.size(); i++) {
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

			MsgObject msgObject = new MsgObject(1, serviceid, userid,
					commandcode, code, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 8);

			// Moi dung se gui cho khach hang
			String content = "";
			int iGID = Integer.parseInt(GID);
			
			if (DBUtil.saveRequest(DBCONTENT, userid, code, typehd, iGID)) {				
				content = code + ":" + DomainServer + "/?p=" + userid + "&c="
				+ code + "&f=" + typehd + "&g=" + GID;
				
				String sisWapPage = Constants._prop.getProperty("IsWapPage", "0");
				int IsWapPage = Util.PaseInt(sisWapPage);
				
				if(IsWapPage==1)
				{
					content  = DomainServer + "/?p=" + userid + "&c="
					+ code + "&f=" + typehd + "&g=" + GID;
				}				
				Util.logger.info("Noi dung tra ve cho khach hang, userid= " + userid + ":" + content);
				
				MsgObject msgObj = new MsgObject(1, serviceid, userid,
						commandcode, content, new BigDecimal(requestid), DateProc
								.createTimestamp(), mobileoperator, msgtype, 8,
						amount, content_id);
				if (DBUtil.sendMTQueue(type, msgObj, CLASSNAME, serviceName,
						sSplit, i,0) == 1) {
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
}
	// send mt online : send to vms_mt_queue, INProcess read vms_mt_queue,
	// charge and then insert into mt_queue
	private void SendMT2UserHaveJustRegister(String ssid, String DBCONTENT, String serviceName,
			String MLIST, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String code, String typehd, String GID) throws Exception {
		Vector vtUsers = DBUtil.getListUserFromMlist(serviceName, MLIST);

		for (int i = 0; i < vtUsers.size(); i++) {
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

			int mtcount = Integer.parseInt((String) item.elementAt(10));
			int mtfree = Integer.parseInt((String) item.elementAt(11));
			int duration = Integer.parseInt((String) item.elementAt(12));
			int nday = Integer.parseInt((String) item.elementAt(13));

			/*if ((nday >= 0) && (nday <= 10) && (mtfree > 0)) {
				msgtype = Integer.parseInt(Constants.MT_PUSH);
				Util.logger.info("WapPic@free:" + userid + "@" + serviceName
						+ "@nday=" + nday);
			}*/
			if(mtfree > 0 && mtcount <= mtfree)
			{
				msgtype = 0;
			}
			if (notcharge == Constants.MODE_NOTCHARGE) {
				msgtype = Integer.parseInt(Constants.MT_PUSH);
			}
			MsgObject msgObject = new MsgObject(1, serviceid, userid,
					commandcode, code, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 8);
			// Moi dung se gui cho khach hang
			String content = "";
			int iGID = Integer.parseInt(GID);
			if (DBUtil.saveRequest(DBCONTENT, userid, code, typehd, iGID)) {				
				content = code + ":" + DomainServer + "/?p=" + userid + "&c="
				+ code + "&f=" + typehd + "&g=" + GID;
				Util.logger.info("Noi dung tra ve cho khach hang, userid= " + userid + ":" + content);
				
				MsgObject msgObj = new MsgObject(1, serviceid, userid,
						commandcode, content, new BigDecimal(requestid), DateProc
								.createTimestamp(), mobileoperator, msgtype, 8,
						amount, content_id);
				if (DBUtil.sendVMSChargeOnline(type, msgObj, CLASSNAME,
						serviceName) == 1) {
					if (duration > 0 && (mtcount > duration)) {
						// xoa thoi
						DBUtil.insertData2cancel(userid, serviceid, commandcode,
								MLIST, msgObj, mtfree + "", msgtype,
								commandcode, mtcount + "");

						DBUtil.executeSQL("gateway", "delete from " + MLIST
								+ " where id =" + id);
					} else {
						DBUtil.executeSQL(
										"gateway",
										"update "
												+ MLIST
												+ " set last_code = '"
												+ lastcode
												+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where id ="
												+ id);
					}
				} else {
					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ MLIST
											+ " set autotimestamps = current_timestamp,failures=1 where id ="
											+ id);
				}
			} else {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra dich vu:" + serviceName
								+ ", Co van de khi tao link, check icom_wap now", "");
			}
		}
	}
}
