package daily;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import DAO.SubcriberDAO_4All;

import sub.DeliveryManager;

public class InfoDaily4All extends DeliveryManager {
	@SuppressWarnings("unchecked")
	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {
		try {
			// Fix 11/05/2010 10:04
			String INFO_ID = "x";
			String MLIST = "x";
			String CLASSNAME = "Ykhoa";
			Util.logger.info(CLASSNAME + ": start:" + serviceName);
			HashMap _option = new HashMap();
			Hashtable<String,String> message=new Hashtable<String, String>();
			_option = Util.getParametersAsString(option);
			String x = "x";
			INFO_ID = Util.getStringfromHashMap(_option, "infoid",	"YK");
			MLIST = Util.getStringfromHashMap(_option, "mlist", "mlist_ykhoa");
			String DBCONTENT = Util.getStringfromHashMap(_option, "dbcontent",	"content");
			String type = Util.getStringfromHashMap(_option, "type", "2");

			if ("x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + serviceName + "",
						"");
				return null;
			}	
			// thuc thi chay service
			String sqlUpdateRunning = "update services set result="
				+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);
			
			String listYkhoa = "";
			listYkhoa = SubcriberDAO_4All.GetOption(INFO_ID);
			String arrTamp ="";
			
			if(!listYkhoa.equalsIgnoreCase("") )
			{
				String[] arrOption =listYkhoa.split(";") ;
				int length = arrOption.length ;
				String[] arrInfo = new String[length];

			
				for (int i = 0; i < arrOption.length; i++) {
					// arrInfo[i] = getInfo(currDate, arrOption[i]);
					//String database, String type,String newstypecode
					arrInfo[i] = getContent0(DBCONTENT, type, arrOption[i]);
					if(arrInfo[i]!=null)
						message.put(arrOption[i],arrInfo[i]);
					else
					{
						Util.logger.error("@Dich vu '"+arrInfo[i]+"' khong doc duoc content!. Kiem tra ket noi hoac noi dung?");
						DBUtil.Alert("DeliveryDaily", "RUNING", "major",
								"Dich vu '"+arrInfo[i]+"': khong doc duoc content!. Kiem tra ket noi hoac noi dung?",
								"");
						continue;
					}
				}	
				// thuc hien tra tin
				if(message.size()>0)
					sendMTMessage(serviceName, message);
				else
				{
					Util.logger.info("Chua co tat ca cac noi dung cua y khoa ");					
				}

			}
			else
			{
				arrTamp = getContent0(DBCONTENT, type, INFO_ID);
				
				String sSplit = Constants._prop.getProperty("numbermt_queue", "4");
				// gui mt den nhung thue bao da charge tu truoc
				SendMT2UsersHasCharged_2(ssid, serviceName, MLIST, DBCONTENT, type,
						CLASSNAME, notcharge, INFO_ID, sSplit, arrTamp);	
			}		
									
			// cap nhat tinh trang
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
		} catch (Exception e) {
			// TODO: handle exception
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
	
private void sendMTMessage(String serviceName,Hashtable<String, String> message){
		
		ArrayList<ListSendObj> arrListSend = new ArrayList<ListSendObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,service_name,request_id, message_type, mobile_operator "
			+ " , amount, content_id, options,channel_type from list_send "
			+ " where  upper(command_code)  like '" + serviceName +"%' limit 100 ";

		// System.out.println("getExistMessage QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {		
					
					String option=rs.getString("options").trim();
					MsgObject msg=new MsgObject();
					msg.setUserid(rs.getString("user_id"));
					String info=message.get(option);
					if(info==null)
					{
						Util.logger.info("Kiem tra khong co noi dung cua y khoa, option="+option);
						continue;
					}
					msg.setUsertext(info); 
					msg.setServiceid(rs.getString("service_id"));
					msg.setCommandCode(rs.getString("command_code"));
					msg.setMobileoperator(rs.getString("mobile_operator"));
					msg.setRequestid(rs.getBigDecimal("id"));
					msg.setContenttype(0);
					msg.setChannelType(rs.getInt("channel_type"));
					msg.setMsgtype(rs.getInt("message_type"));
					// thuc hien send mt
					Sender.msgPushMTQueue.add(msg);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("QueueListSendMng - getArrResponseMT. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("QueueListSendMng - getArrResponseMT. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(connection,stmt);
		}
		
	}
	
	
	
	// send mt online : send to vms_mt_queue, INProcess read vms_mt_queue,
	// charge and then insert into mt_queue
	
	private void SendMT2UsersHasCharged_2(String ssid, String serviceName,
			String MLIST, String DBCONTENT, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String sSplit,String strInfo) throws Exception {
		Util.logger.info("@Infodaily:SendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send.service name="+ serviceName);
		
		Vector vtUsers = DBUtil.getListUserFromListSend(serviceName);
		int cUser = vtUsers.size();
		Util.logger.info("@Infodaily:SendMT2UsersHasCharged: vtUsers="+ cUser);
		
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
			String content = strInfo;

			Util.logger.info("Infodaily@Noi dung tra ve cho khach hang: "
					+ content);
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
				DBUtil.Alert("DeliveryDaily@Infodaily", "RUNING", "major",
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


	
	public String getContent0(String database, String type,String newstypecode) {

		String record = null;		

		Connection connection = null;
		DBPool dbpool = new DBPool();				

		String query = "SELECT Content "			
			+ " FROM ( SELECT TOP 1 * FROM [TextServices] WHERE cateid = (select top 1 cateid from Categories " +
			" where upper(prefixcode) = '"+newstypecode.toUpperCase()+"' order by cateid desc)"; 
		query = query + " and DATEDIFF(day, returndate, GETDATE()) = 0  order by returndate desc)x ";
		try {
			connection = dbpool.getConnection(database);

			Util.logger.info("SQL Query get content: " + query);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "@getContent: queryStatement:" + result.size() + "@"
					+ query);

			if (result.size() == 0) {
				return null;
			} else {
				for (int i = 0; i < result.size(); i++) {
					Vector item = (Vector) result.elementAt(i);
					record= (String) item.elementAt(0);
				}
			}
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);			
		} finally {
			dbpool.cleanup(connection);
		}		
		return record;
	}
	
}
