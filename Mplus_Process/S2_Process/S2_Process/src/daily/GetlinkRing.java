package daily;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Vector;
import icom.Constants;
import icom.DBPool;
import icom.Sender;

import icom.common.DBUpdate;
import icom.common.DBUtil;
import icom.common.Util;

public class GetlinkRing extends Thread {

	DBPool dbpool = new DBPool();
	int processnum = 1;
	int processindex = 1;
	Boolean isGetLinkRing = false;

	public static void main(String[] args) {
		GetlinkRing smsConsole = new GetlinkRing();
		smsConsole.start();
	}

	public static String FormatNumber(int i) {
		try {
			if (i >= 0 && i < 10)
				return "0" + i;
			else
				return "" + i;
		} catch (Exception e) {
			return "";
		}
	}

	public static long String2MilisecondNew(String strInputDate) {
		String strDate = strInputDate.trim();
		int i, nYear, nMonth, nDay, nHour, nMinute, nSecond;
		String strSub = null;
		if (strInputDate == null || "".equals(strInputDate)) {
			return 0;
		}
		strDate = strDate.replace('-', '/');
		strDate = strDate.replace('.', '/');
		strDate = strDate.replace(' ', '/');
		strDate = strDate.replace('_', '/');
		strDate = strDate.replace(':', '/');
		i = strDate.indexOf("/");

		// System.err.println("String2Milisecond.strDate:" + strDate);
		if (i < 0) {
			return 0;
		}
		try {
			// Get Nam
			String[] arrDate = strDate.split("/");
			nYear = (new Integer(arrDate[0].trim())).intValue();
			nMonth = (new Integer(arrDate[1].trim())).intValue() - 1;
			nDay = (new Integer(arrDate[2].trim())).intValue();
			nHour = (new Integer(arrDate[3].trim())).intValue();
			nMinute = (new Integer(arrDate[4].trim())).intValue();
			nSecond = (new Integer(arrDate[5].trim())).intValue();
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.set(nYear, nMonth, nDay, nHour, nMinute, nSecond);

			return calendar.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void run() {

		while (Sender.processData) {

			int runhour = Integer.parseInt(Constants._prop.getProperty(
					"GETLINK_RING_HOUR", "4"));


			int NumberCommandGetLinkGame = Integer.parseInt(Constants._prop
					.getProperty("NumberCommandGetLinkGame", ""));
			int TimeDelayGetLinkGame = Integer.parseInt(Constants._prop
					.getProperty("TimeDelayGetLinkGame", ""));
			Calendar cal = Calendar.getInstance();
			int nowhour = cal.get(Calendar.HOUR_OF_DAY);
			// Lay ma game
			String serviceName = "";
			if (nowhour == runhour) {
				
				isGetLinkRing = true;
				
				int numberThread = 1;
				try {
					numberThread = Integer.parseInt(Constants._prop
							.getProperty("NUMBER_THREAD_GET_LINK_GAME", "1"));
				} catch (Exception ex) {
					Util.logger
							.error("Error Config file at NUMBER_THREAD_GET_LINK_GAME!!!!");
				}

				for (int i = 0; i < numberThread; i++) {
					Util.logger.info(".......Thread get Link Ring  " + i);
					SubThreadGetLinkRing subThread = new SubThreadGetLinkRing(i + 1);
					subThread.start();
				}
				
				try {
					sleep(1000 * 60 * 60);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			isGetLinkRing = false;
			
			try {
				sleep(1000 * 60);

			} catch (InterruptedException e) {
				e.printStackTrace();

			}

		}

	}

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	public static int GateCateid(String command_code) {
		int cateid = 0;
		if (command_code.equalsIgnoreCase("RINGHOT")) {
			cateid = 420;
		}

		if (command_code.equalsIgnoreCase("RINGQT")) {
			cateid = 423;
		}

		if (command_code.equalsIgnoreCase("RINGTRE")) {
			cateid = 421;
		}

		if (command_code.equalsIgnoreCase("RINGTT")) {
			cateid = 447;
		}

		if (command_code.equalsIgnoreCase("RINGROCK")) {
			cateid = 448;
		}
		
		if (command_code.equalsIgnoreCase("RINGMOI")) {
			cateid = 480;
		}
		
		if (command_code.equalsIgnoreCase("RINGDOCDAO")) {
			cateid = 434;
		}
		
		if (command_code.equalsIgnoreCase("RINGTHUC")) {
			cateid = 478;
		}

		if (command_code.equalsIgnoreCase("RINGQC")) {
			cateid = 479;
		}
		
		return cateid;
	}

	public static String GateCateName(String command_code) {
		String catename = "";
		if (command_code.equalsIgnoreCase("RINGHOT")) {
			catename = "Nhac chuong hot";
		}

		if (command_code.equalsIgnoreCase("RINGQT")) {
			catename = "Nhac chuong quoc te";
		}

		if (command_code.equalsIgnoreCase("RINGTT")) {
			catename = "Nhac tru tinh";
		}

		if (command_code.equalsIgnoreCase("RINGROCK")) {
			catename = "Nhac rock";
		}

		if (command_code.equalsIgnoreCase("RINGTRE")) {
			catename = "Nhac tre";
		}
		
		if (command_code.equalsIgnoreCase("RINGMOI")) {
			catename = "Nhac moi nhat";
		}
		
		if (command_code.equalsIgnoreCase("RINGDOCDAO")) {
			catename = "Nhac doc dao";
		}
		
		if (command_code.equalsIgnoreCase("RINGTHUC")) {
			catename = "Nhac am thanh thuc";
		}

		if (command_code.equalsIgnoreCase("RINGQC")) {
			catename = "Nhac quang cao";
		}
		
		return catename;
	}

	public static int getCodeStore(int CateID, String lastcode) {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT top 1 [RingtoneID]  FROM [dbo].[Ringtone]  where CateID ="
				+ CateID
				+ " and [RingtoneID]  not in ("
				+ lastcode
				+ ") order by newid()";

		try {
			connection = dbpool.getConnection("store");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return code;
	}

	public int insertLinkqueue(String user_id, String command_code,
			String link, int gameid) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into linkgame_queue(user_id, command_code, gameid,link) values ('"
				+ user_id.toUpperCase()
				+ "','"
				+ command_code.toUpperCase()
				+ "'," + gameid + ",'" + link + "')";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  link queue Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName()
					+ ":Insert  link game queue Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}
	
	private static boolean isexist(String userid, String mlist,
			String command_code) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist + " where user_id='"
					+ userid + "' and command_code='" + command_code + "' ";

			Util.logger.info(query3);

			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}
	
	class SubThreadGetLinkRing extends Thread{
		
		int numberThread = 0;
		
		public SubThreadGetLinkRing(int numberThrd){
			numberThread = numberThrd;
		}
		
		@Override
		public void run(){
			QueueListSendMng queue = QueueListSendMng.getInstance();
			QueueMlistTable queueMlist = QueueMlistTable.getInstance();
			GetLinkManager getLinkMng = new GetLinkManager();
			DBUpdate dbUpdate = new DBUpdate();
			
			while (isGetLinkRing) {

				String serviceName = "RING";
				ListSendObj obj = queue.getListSendObj(serviceName);

				if (obj == null){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return;
				}
				queue.updateListSendStatus(obj.getId(), 1);	
				
				

				int cateId = GetlinkRing.GateCateid(obj.getCommandCode());
				int mediaId = getCodeStore(cateId, obj.getLastCode());
				String result = getLinkMng.getLinkRing(mediaId, now(), obj.getUserId(), 1000);
				
				if(result == null){
					Util.logger.error("GetlinkRing ERROR ## HET LINK => Nhap them #link result = null; " +
							"user_id = " + obj.getUserId() 
							+ ";; last_code = " + obj.getLastCode() 
							+ " #### command_code = " + obj.getCommandCode() 
							+ " ;; MediaID = " + mediaId);
					//queue.updateListSendStatus(obj.getId(), 0);	
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				Util.logger.info("SubThreadGetLinkRing Thread " + numberThread
						+ " ### CommandCode = " + obj.getCommandCode()
						+ " ### Link Result = " + result
						+ " ### Tham so truyen vao: "
						+ " MediaId = " + mediaId + " ; UserId = " + obj.getUserId()
						+ " ; price = 1000");

				String temp = "";
				if ((result.startsWith("0"))
						&& (!"".equalsIgnoreCase(result.substring(2)))) {
					temp = result.substring(2);
					Util.logger.info("SubThreadGetLinkRing @Lay link thanh cong:Game[request_id:"
							+ obj.getRequestId()
							+ "] ; Duong link tai Image: " + temp);
					if (!isexist(obj.getUserId(), "linkgame_queue", obj
							.getCommandCode())) {
						
						insertLinkqueue(obj.getUserId(), obj.getCommandCode(),
								temp, mediaId);
						
						String lastcode = obj.getLastCode();
						if (lastcode.length() > 1500) {
							lastcode = "0";
						} else {
							if (mediaId != 0) {
								lastcode = lastcode + "," + mediaId;
							}							
						}
						
						String tblMlist = queueMlist.getMlistTableName(obj.getCommandCode());
						dbUpdate.updateLastCodeMlist(tblMlist, obj.getCommandCode(),
								obj.getUserId(), lastcode);
						
					} else {
						Util.logger.info("Dupplicate ko insert user_id=" + obj.getUserId() + "CommandCode"
								+ obj.getCommandCode());
					}
										
				}else{
					queue.updateListSendStatus(obj.getId(), 0);
				}
				
				obj = null;
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(Sender.processData == false){
					isGetLinkRing = false;
				}
				
			}

		}// End Run method
		
	}// End class SubThread

}
