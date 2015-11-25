package packet.responseMT;

import icom.Constants;
import icom.LoadConfig;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import sub.DeliveryManager;
import sub.soicau.common;
import vmg.itrd.ws.MTSenderVMS;

public class PacketCauXSDaily extends DeliveryManager {
	String CLASSNAME = "PacketCauXSDaily";

	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {

		try {
			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);
			String x = "x";
			String MLIST = "mlist_cauxs_packet";
			MLIST = Util.getStringfromHashMap(_option, "mlist", "mlist_cauxs_packet");
			
			String scompanyid = Util.getStringfromHashMap(_option, "companyid", "1");
			int companyId = Integer.parseInt(scompanyid);

			Util.logger.info(CLASSNAME + "@companyid=" + companyId + ": start");
			CLASSNAME = CLASSNAME + scompanyid;
			//TODO: PhuongDT
			String strResult = common.getDataCAU(companyId);
			//String strResult ="Test cau AG";

			if ("x".equalsIgnoreCase(strResult) || strResult == null) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra dich vu soi cau companyId=" + companyId + ",ko lay duoc content", "");
				return null;
			}
			Util.logger.info("Ket qua CAU XS: " + strResult);

			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");
			
			// gui mt den nhung thue bao da charge tu truoc
			SendMT2UsersHasCharged("2", serviceName, companyId, notcharge, MLIST, sSplit, strResult);
			icom.Services service  = new icom.Services(); 
			try{
			service = icom.Services.getService(serviceName, LoadConfig.hServices);
			}catch(Exception ex)
			{
				Util.logger.error("Cauxsdaily: co loi khi get service");
				DBUtil
				.Alert(
						"Process.VMS",
						"Cauxsdaily",
						"major",
						"Cauxsdaily.Exception: co loi khi get service.ex=" + ex.toString(),"");
			}

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
	private void SendMT2UsersHasCharged(String type, String serviceName, int companyId, int notcharge, String MLIST, String sSplit,String strInfo) throws Exception {
		Util.logger.info("@CAUXSDaily@SendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send,service name="+ serviceName);
		
		Vector vtUsers = DBUtil.getListUserFromListSendForXoso(serviceName,companyId);
		int cUser = vtUsers.size();
		Util.logger.info("@CAUXSDaily@SendMT2UsersHasCharged: vtUsers="+ cUser);
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
			
			String content = strInfo;
			// failures: khong gui tin cho KH vi content bi null
			if ("".equalsIgnoreCase(content)) {
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
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					content, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0,
					amount, content_id);
			msgObj.setServiceName(serviceName);
			
			if (MTSenderVMS.insertMTQueueVMS(msgObj) == 1) {
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

