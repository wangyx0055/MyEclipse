package packet.responseMT;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import sub.DeliveryManager;

public class PacketWapPicture extends DeliveryManager {

	String DomainServer = Constants._prop.getProperty("domainServer", "");
	String DomainMobinet = Constants._prop.getProperty("domainmobinet", "");

	String CLASSNAME = "daily.PacketWapPicture";

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
			String DBCONTENT = Util.getStringfromHashMap(_option,
					"dbcontentwp", "wap");

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
			// TODO: PhuongDT
			String code = DBUtil.getCode("gateway", type, INFO_ID);

			// String code = "A";
			Util.logger
					.info("WapPicture: Ma Hinh nen, nhac chuong, hinh dong tuan nay: "
							+ code);

			if ("".equalsIgnoreCase(code)) {
				Util.logger.info("WapPicture Code is null");
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"WapPic Code is null, plz check now, serviceName="
								+ serviceName, "");
				return null;
			}

			String namecode = DBUtil.getNameCode(code, INFO_ID);

			Util.logger.info("WapPicture: namecode:" + namecode);

			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");

			SendMT2UsersHasCharged(ssid, DBCONTENT, serviceName, MLIST, type,
					CLASSNAME, notcharge, INFO_ID, sSplit, code, namecode,
					typehd, GID);

			icom.Services service = new icom.Services();
			try {
				service = icom.Services.getService(serviceName,
						Sender.loadconfig.hServices);
			} catch (Exception ex) {
				Util.logger.error("WapPicture: co loi khi get service");
				DBUtil.Alert("Process.VMS", "WapPicture", "major",
						"WapPicture:Exception: co loi khi get service.ex="
								+ ex.toString(), "");
			}

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

	private void SendMT2UsersHasCharged(String ssid, String DBCONTENT,
			String serviceName, String MLIST, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String sSplit, String code,
			String namecode, String typehd, String GID) throws Exception {
		Util.logger
				.info("Wappicture : SendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send.service name="
						+ serviceName);

		Vector vtUsers = DBUtil.getListUserFromListSend(serviceName);
		int cUser = vtUsers.size();
		Util.logger.info("Wappicture : SendMT2UsersHasCharged: vtUsers="
				+ cUser);
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
			int channel_type = Integer.parseInt((String) item.elementAt(11));
			MsgObject msgObject = new MsgObject(1, serviceid, userid,
					commandcode, code, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 8,
					amount, content_id);

			// Noi dung se gui cho khach hang
			String content = "";
			int iGID = Integer.parseInt(GID);

			if (DBUtil.saveRequest(DBCONTENT, userid, code, typehd, iGID)) {
				content = namecode + ":" + DomainMobinet + "/?p=" + userid
						+ "&c=" + code + "&f=" + typehd + "&g=" + GID;

				String sisWapPage = Constants._prop.getProperty("IsWapPage",
						"0");
				int IsWapPage = Util.PaseInt(sisWapPage);

				if (IsWapPage == 1) {
					content = DomainServer
							+ "?glink="
							+ content.replace(namecode + ":" + DomainMobinet
									+ "/", "");
				}
				
				Util.logger.info("Noi dung tra ve cho khach hang, user_id:"
						+ userid + "\tlink:" + content);

				MsgObject msgObj = new MsgObject(1, serviceid, userid,
						commandcode, content, new BigDecimal(requestid),
						DateProc.createTimestamp(), mobileoperator, msgtype, 8,
						amount, content_id);
				msgObj.setChannelType(channel_type);

				if (sendMTVMS(msgObj) == 1) {
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
	
	private int sendMTVMS(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		int iReturn = -1;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("PacketWapPicture@sendMTVMS\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return iReturn;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("PacketWapPicture@sendMTVMS\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnection("s2mplus");
			if (connection == null) {
				Util.logger
						.crisis("PacketWapPicture@sendMTVMS: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				iReturn = -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setInt(9, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("PacketWapPicture@sendMTVMS: Error@userid="
						+ msgObject.getUserid() + "@service_id="
						+ msgObject.getServiceid() + "@user_text="
						+ msgObject.getUsertext() + "@message_type="
						+ msgObject.getMsgtype() + "@request_id="
						+ msgObject.getRequestid().toString()
						+ "@channel_type=" + msgObject.getChannelType());
				iReturn = -1;
			}
			iReturn = 1;
		} catch (SQLException e) {
			Util.logger.crisis("PacketWapPicture@sendMTVMS: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger.crisis("PacketWapPicture@sendMTVMS: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
		
	}

}
