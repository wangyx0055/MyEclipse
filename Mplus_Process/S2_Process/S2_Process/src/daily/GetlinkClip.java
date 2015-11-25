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

public class GetlinkClip extends Thread {

	DBPool dbpool = new DBPool();
	int processnum = 1;
	int processindex = 1;
	
	Boolean isGetLinkClip = false;

	public static void main(String[] args) {
		GetlinkClip smsConsole = new GetlinkClip();

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

	public void run() {

		while (Sender.processData) {

			int runhour = Integer.parseInt(Constants._prop.getProperty(
					"GETLINK_CLIP_HOUR", "4"));
		
			Calendar cal = Calendar.getInstance();
			int nowhour = cal.get(Calendar.HOUR_OF_DAY);

			if (nowhour == runhour) {
				
				isGetLinkClip = true;
												
				int numberThread = 1;
				
				try {
					numberThread = Integer.parseInt(Constants._prop
							.getProperty("NUMBER_THREAD_GET_LINK_GAME", "1"));
				} catch (Exception ex) {
					Util.logger
							.error("Error Config file at NUMBER_THREAD_GET_LINK_GAME!!!!");
				}

				for (int i = 0; i < numberThread; i++) {
					Util.logger.info(".......Thread get Link Clip  " + i);
					SubThreadGetLinkClip subThread = 
						new SubThreadGetLinkClip(i + 1);
					subThread.start();
				}

				try {
					sleep(60*60*1000);

				} catch (InterruptedException e) {
					e.printStackTrace();

				}

				
			}else{
				
				try {
					sleep(1000 * 60);
					Util.logger.info("Sleep Thread getlink 1 phut ....");

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
			try {
				isGetLinkClip = false;
				sleep(100);
				Util.logger.info("Sleep Thread getlink 60 phut ....");

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
	
		if (command_code.equalsIgnoreCase("CLIPTHS")) {
			cateid = 414;
		}

		if (command_code.equalsIgnoreCase("CLIPHAI")) {
			cateid = 415;
		}

		if (command_code.equalsIgnoreCase("CLIPSAO")) {
			cateid = 416;
		}

		if (command_code.equalsIgnoreCase("CLIPHH")) {
			cateid = 462;
		}

		if (command_code.equalsIgnoreCase("CLIPTHT")) {
			cateid = 418;
		}

		return cateid;
	}

	public static String GateCateName(String command_code) {
		String catename = "";
		if (command_code.equalsIgnoreCase("CLIPTHS")) {
			catename = "Clip thoi su";
		}

		if (command_code.equalsIgnoreCase("CLIPHAI")) {
			catename = "Clip hai huoc";
		}

		if (command_code.equalsIgnoreCase("CLIPSAO")) {
			catename = "Clip ngoi sao";
		}

		if (command_code.equalsIgnoreCase("CLIPHH")) {
			catename = "Clip hoat hinh";
		}

		if (command_code.equalsIgnoreCase("CLIPTHT")) {
			catename = "Clip the thao";
		}

	
		return catename;
	}



	public static int getCodeStore(int CateID, String lastcode) {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT top 1 [VideoID] FROM [IComStore].[dbo].[Video] where CateID ="
				+ CateID
				+ " and [VideoID] not in ("
				+ lastcode
				+ ") order by newid()";

		try {
			connection = dbpool.getConnection("store");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
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

	
	class SubThreadGetLinkClip extends Thread{
		
		int numberThread = 0;
		
		public SubThreadGetLinkClip(int numberThrd){
			numberThread = numberThrd;
		}
		
		@Override
		public void run(){
			QueueListSendMng queue = QueueListSendMng.getInstance();
			QueueMlistTable queueMlist = QueueMlistTable.getInstance();
			DBUpdate dbUpdate = new DBUpdate();

			while (isGetLinkClip) {

				String serviceName = "CLIP";
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
				
				GetLinkManager getLinkMng = new GetLinkManager();

				int cateId = GateCateid(obj.getCommandCode());
				int mediaId = getCodeStore(cateId, obj.getLastCode());
				double amount = obj.getAmount();

				String result = getLinkMng.getLinkClip(mediaId, now(), obj.getUserId(), 1000);
				
				if(result == null){
					Util.logger.error("SubThreadGetLinkClip ERROR ## link result = null; " +
							"user_id = " + obj.getUserId() 
							+ "; last_code = " + obj.getLastCode() 
							+ " ;; command_code = " + obj.getCommandCode());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				Util.logger.info("SubThreadGetLinkClip Thread " + numberThread
						+ " ## Link Result = " + result
						+ " ### Tham so truyen vao: cateId = " + cateId
						+ " ; MediaId = " + mediaId + " ; UserId = " 
						+ obj.getUserId() + " ; price = " + amount);

				String temp = "";
				if ((result.startsWith("0"))
						&& (!"".equalsIgnoreCase(result.substring(2)))) {
					temp = result.substring(2);
					Util.logger.info("SubThreadGetLinkClip @Lay link thanh cong:Game[request_id:"
							+ obj.getRequestId()
							+ "] ; Duong link tai clip: " + temp);
					if (!isexist(obj.getUserId(), "linkgame_queue", obj
							.getCommandCode())) {
						insertLinkqueue(obj.getUserId(), obj.getCommandCode(),
								temp, 1);
						
						String lastcode = obj.getLastCode();
						if (lastcode.length() > 1500) {
							lastcode = "0";
						} else {
							if (mediaId != 0) {
								lastcode = lastcode + "," + mediaId;
							}							
						}
						
						String tblMlist = queueMlist.getMlistTableName(obj.getCommandCode().trim());
						
						if(tblMlist.trim().equals("") || tblMlist.trim().equals("x")){
							tblMlist = "mlist_clips";
						}
						
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
					isGetLinkClip = false;
				}
				
			}

		}// End Run method
		
	}// End class SubThread
	
	

}
