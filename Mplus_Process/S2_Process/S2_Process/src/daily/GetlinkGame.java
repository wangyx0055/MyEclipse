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

import icom.common.DBUtil;
import icom.common.Util;

public class GetlinkGame extends Thread {

	DBPool dbpool = new DBPool();
	int processnum = 1;
	int processindex = 1;
	Boolean isGetLinkGame = false;

//	public static void main(String[] args) {
//		GetlinkGame smsConsole = new GetlinkGame();
//
//		smsConsole.start();
//
//	}

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
					"GETLINK_HOUR", "4"));

//			int NumberCommandGetLinkGame = Integer.parseInt(Constants._prop
//					.getProperty("NumberCommandGetLinkGame", ""));
//			int TimeDelayGetLinkGame = Integer.parseInt(Constants._prop
//					.getProperty("TimeDelayGetLinkGame", ""));
			Calendar cal = Calendar.getInstance();
			int nowhour = cal.get(Calendar.HOUR_OF_DAY);
			
			if (nowhour == runhour) {
				
				isGetLinkGame = true;
//				Util.logger
//						.info("@Game: \tSendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send. service name:"
//								+ serviceName);

				int numberThread = 1;
				try {
					numberThread = Integer.parseInt(Constants._prop
							.getProperty("NUMBER_THREAD_GET_LINK_GAME", "1"));
				} catch (Exception ex) {
					Util.logger
							.error("Error Config file at NUMBER_THREAD_GET_LINK_GAME!!!!");
				}

				for (int i = 0; i < numberThread; i++) {
					Util.logger.info(".......Thread get Link Game  " + i);
					SubThreadGetLinkGame subThread = new SubThreadGetLinkGame(
							i + 1);
					subThread.start();
				}

				try {
					sleep(60*60*1000);
//					Util.logger
//							.info("Sleep Thread getlink 100 miliseconds ....");

				} catch (InterruptedException e) {
					e.printStackTrace();

				}

			} else {
				try {
					sleep(1000 * 60);
					Util.logger.info("Sleep Thread getlink 1 phut ....");

				} catch (InterruptedException e) {
					e.printStackTrace();

				}
			}
			
			try {
				isGetLinkGame = false;
				sleep(100);
				Util.logger.info("Sleep Thread getlink 1 phut ....");

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
		if (command_code.equalsIgnoreCase("GAMEHD")) {
			cateid = 391;
		}
		if (command_code.equalsIgnoreCase("GAMECT")) {
			cateid = 392;
		}
		if (command_code.equalsIgnoreCase("GAMETT")) {
			cateid = 393;
		}
		if (command_code.equalsIgnoreCase("GAMEDV")) {
			cateid = 394;
		}
		if (command_code.equalsIgnoreCase("GAMEHH")) {
			cateid = 436;
		}
		if (command_code.equalsIgnoreCase("GAMEHOT")) {
			cateid = 435;
		}
		if (command_code.equalsIgnoreCase("GAMENM")) {
			cateid = 396;
		}
		if (command_code.equalsIgnoreCase("GAMETN")) {
			cateid = 437;
		}
		if (command_code.equalsIgnoreCase("GAMEVN")) {
			cateid = 397;
		}
		if(command_code.equalsIgnoreCase("GAMEKD")){
			cateid = 970;
		}
		if(command_code.equalsIgnoreCase("GAMEDK")){
			cateid = 1044;
		}
		if(command_code.equalsIgnoreCase("GAMEIQ")){
			cateid = 1045;
		}
		
		return cateid;
	}

	public static String GateCateName(String command_code) {
		String catename = "";
		if (command_code.equalsIgnoreCase("GAMEHD")) {
			catename = "Game hanh dong";
		}
		if (command_code.equalsIgnoreCase("GAMECT")) {
			catename = "Game chien thuat";
		}
		if (command_code.equalsIgnoreCase("GAMETT")) {
			catename = "Game the thao";
		}
		if (command_code.equalsIgnoreCase("GAMEDV")) {
			catename = "Game do vui";
		}
		if (command_code.equalsIgnoreCase("GAMEHH")) {
			catename = "Game hoat hinh";
		}
		if (command_code.equalsIgnoreCase("GAMEHOT")) {
			catename = "Game hot";
		}
		if (command_code.equalsIgnoreCase("GAMENM")) {
			catename = "Game nguoi mau";
		}
		if (command_code.equalsIgnoreCase("GAMETN")) {
			catename = "Game thieu nhi";
		}
		if (command_code.equalsIgnoreCase("GAMEVN")) {
			catename = "Game vui nhon";
		}
		if(command_code.equalsIgnoreCase("GAMEKD")){
			catename = "Game kinh dien";
		}
		if(command_code.equalsIgnoreCase("GAMEDK")){
			catename = "Game doi khang";
		}
		if(command_code.equalsIgnoreCase("GAMEIQ")){
			catename = "Game tri tue";
		}
		return catename;
	}

	public static int getCodeStore(int CateID, String lastcode) {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT top 1 [GameID] FROM [dbo].[Game] where CateID ="
				+ CateID
				+ " and GameID not in ("
				+ lastcode
				+ ") order by Priority desc ";

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
	
	
	class SubThreadGetLinkGame extends Thread{
		
		int numberThread = 0;
		
		public SubThreadGetLinkGame(int numberThrd){
			numberThread = numberThrd;
		}
		
		@Override
		public void run(){
			QueueListSendMng queue = QueueListSendMng.getInstance();

			while (isGetLinkGame) {

				String serviceName = "GAME";
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

				int cateId = GetlinkGame.GateCateid(obj.getCommandCode());

				String result = getLinkMng.getLinkGame(cateId, -1, now(), obj
						.getUserId(), 10000);
				
				if(result == null){
					Util.logger.error("getLinkGame ERROR ## link result = null; " +
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
				
				Util.logger.info("SubThreadGetLinkGame Thread " + numberThread
						+ " ## Link Result = " + result
						+ " ### Tham so truyen vao: cateId = " + cateId
						+ " ; MediaId = -1" + " ; UserId = " + obj.getUserId()
						+ " ; price = 10000");

				String temp = "";
				if ((result.startsWith("0"))
						&& (!"".equalsIgnoreCase(result.substring(2)))) {
					temp = result.substring(2);
					Util.logger.info("SubThreadGetLinkGame @Lay link thanh cong:Game[request_id:"
							+ obj.getRequestId()
							+ "] ; Duong link tai game: " + temp);
					if (!isexist(obj.getUserId(), "linkgame_queue", obj
							.getCommandCode())) {
						insertLinkqueue(obj.getUserId(), obj.getCommandCode(),
								temp, 1);
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
					isGetLinkGame = false;
				}
				
			}

		}// End Run method
		
	}// End class SubThread

}
