package cs;

import java.math.*;
import java.sql.*;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.DateProc;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;
import com.vmg.sms.process.MsgQueue;

public class ExecuteADVCR_icom extends Thread {

	// Utilities utl = new Utilities();
	static MsgQueue queueADV = null;

	static String myURL = "jdbc:oracle:thin:@10.4.3.169:1521:itrd";
	static String myDriver = "oracle.jdbc.driver.OracleDriver";
	static String userDB = "lotterydb";
	static String passwordDB = "lotterydb";
	public static String Hour2Queue_North = "19:00";
	public static String Hour2Queue_South = "16:00";
	// SMSLottery smsLottery = new SMSLottery();
	public static Hashtable MTADD = null;
	static int[] companyIDdayMN = { 161, 131, 81, 41, 111, 11, 131 };
	static String[] companyNAMEdayMN = { "Tien Giang", "TP HCM", "Vung Tau",
			"Dong Nai", "Tay Ninh", "Binh Duong", "TP HCM" };

	static int[] companyIDdayMT = { 161, 131, 81, 41, 111, 11, 131 };
	static String[] companyNAMEdayMT = { "Tien Giang", "TP HCM", "Vung Tau",
			"Dong Nai", "Tay Ninh", "Binh Duong", "TP HCM" };

	public ExecuteADVCR_icom(MsgQueue queueADV) {
		this.queueADV = queueADV;

	}

	public static boolean ADV() {
		try {
			if (Constants._prop.getProperty("ADV_VALUE", "1").equalsIgnoreCase(
					"1")) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;
	}

	public static void add2queueADV(int msgType, String srcAddress,
			String destAddress, String sMsg, BigDecimal receivedId,
			Timestamp tTimes, String sOperators) {
		try {
				
			int i=0;
				Boolean dk=true;
			String operator = "";		
			operator = sOperators;		
			
			
			Util.logger
			.info("operator add queue" + operator);
				
		      			
		if ("BEELINE".equalsIgnoreCase(operator))
		{dk=false;}
		
		if (dk){
			if (ADV()) {
				if (queueADV.getSize() < 100) {
					
				queueADV.add(new MsgObject(srcAddress, destAddress, sMsg,
							sMsg, receivedId, tTimes, sOperators, msgType, 0));
				
				}
			}
			
		}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void run() {
		int nCount = 0;
		MsgObject msgObject = null;
		int msgType = 0;

		String serviceId = "";
		String userId = "";
		String info = "";
		String operator = "";
	
		BigDecimal receiveid = new BigDecimal(-1);
		BigDecimal returnId = new BigDecimal(-1);
		Timestamp tTime = null;
		
		try {
			this.sleep(1000);
		} catch (InterruptedException ex1) {
		}

		try {
			while (ConsoleSRV.processData) {
				returnId = new BigDecimal(-1);
				try {
					msgObject = (MsgObject) queueADV.remove();
					
					this.sleep(200);
					
					tTime = msgObject.getTTimes();

					
					serviceId = msgObject.getServiceid();
					userId = msgObject.getUserid();
					info = msgObject.getUsertext();
					receiveid = msgObject.getRequestid();
					operator=msgObject.getMobileoperator();
					
					returnId = processQueueMsg(serviceId, userId, info, tTime,
							msgType, operator, receiveid);
					if (returnId.equals(new BigDecimal(-1))) {
						Util.logger.info("ExecuteADVCR message error: userid="
								+ userId + " info=" + info);
					}
					
				} catch (Exception ex) {
					Util.logger.info("ExecuteADVCR message error(run): Info "
							+ info + " Details:" + ex.toString());
				}
				this.sleep(200);
			}
		}

		catch (Exception ex) {
			Util.logger.info("Error: execute996Msg.run :" + ex.toString());
		}
	}

	public boolean isNewSession() {
		String sTime2Queue = Hour2Queue_North;

		String[] arrH = new String[20];
		int iHour = 0;
		int iMinute = 0;
		arrH = sTime2Queue.split(":");
		if (arrH.length > 1) {
			iHour = Integer.parseInt(arrH[0].trim());
			iMinute = Integer.parseInt(arrH[1].trim());
		} else {
			iHour = Integer.parseInt(arrH[0].trim());

		}
		long milliSecond = System.currentTimeMillis();
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.setTime(new java.util.Date(milliSecond));
		if (((calendar.get(calendar.HOUR_OF_DAY) == iHour) && (calendar
				.get(calendar.MINUTE) >= iMinute))
				|| ((calendar.get(calendar.HOUR_OF_DAY) > iHour))) {
			return true;
		}
		return false;
	}

	public boolean isNewSessionSouth() {
		String sTime2Queue = Hour2Queue_South;

		String[] arrH = new String[20];
		int iHour = 0;
		int iMinute = 0;
		arrH = sTime2Queue.split(":");
		if (arrH.length > 1) {
			iHour = Integer.parseInt(arrH[0].trim());
			iMinute = Integer.parseInt(arrH[1].trim());
		} else {
			iHour = Integer.parseInt(arrH[0].trim());

		}
		long milliSecond = System.currentTimeMillis();
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.setTime(new java.util.Date(milliSecond));
		if (((calendar.get(calendar.HOUR_OF_DAY) == iHour) && (calendar
				.get(calendar.MINUTE) >= iMinute))
				|| ((calendar.get(calendar.HOUR_OF_DAY) > iHour))) {
			return true;
		}
		return false;
	}

	private void sendGifMsg(String serviceid, String userid, String operator,
			String service, String mtcontent, BigDecimal requestid) {
		try {

			MsgObject msg = new MsgObject();
			msg.setContenttype(0);
			msg.setUserid(userid);
			msg.setMobileoperator(operator);
			msg.setMsgtype(0);
			msg.setRequestid(requestid);
			msg.setServiceid(serviceid);
			msg.setKeyword(service);
			msg.setUsertext(mtcontent);
			int i=0;
			String list_operator="BEELINE";
			String[] list;
			Boolean dk=true;
			list_operator= Constants._prop.getProperty("list_operator", list_operator);
			list=list_operator.split(",");
			for ( i=0;i< list.length;i++)
			{ if (i==0 )
				
				dk=!operator.equalsIgnoreCase(list[i]);
			else 
				dk=dk && (!operator.equalsIgnoreCase(list[i]));
			}
		
		if (dk){
			
			DBUtil.sendMT(msg);
		}
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"sendGifMsg Failed:" + ex.getMessage());
		}
	}

	private boolean isExistUserid(String sdate, String userid,
			String commandcode, String serviceid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  ICOM_LUCKYNUMBER_USER where lucky_date='"
					+ sdate
					+ "' and user_id='"
					+ userid
					+ "' and command_code= '"
					+ commandcode
					+ "' and service_id='" + serviceid + "'";

			// query1 = "select db_name()";

		
			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				// System.out.println("AAAAA:" + (String) item.elementAt(0));

				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.sysLog(2, this.getClass().getName(),
					"isExistUserid Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}

	private BigDecimal processQueueMsg(String serviceID, String userID,
			String strMsg, Timestamp tTime, int msgType, String sOperator,
			BigDecimal receiveid) {

		String strMTADDNew = LastestCS.getMTADD(strMsg, serviceID);

		if (strMTADDNew == null || "".equalsIgnoreCase(strMTADDNew)) {
			strMTADDNew = LastestCS.getMTADD("8x51", "8x51");
			if (strMTADDNew == null || "".equalsIgnoreCase(strMTADDNew)) {
				return new BigDecimal(1);
			}

		}

		String sDate = "";

		if (!isNewSession()) {
			sDate = getDate(0);
		} else {
			sDate = getDate(1);
		}
	   if ("VIETTEL".equalsIgnoreCase(sOperator.toUpperCase()))
        	{strMTADDNew=strMTADDNew + "###Chia se va ket noi ban be! Wap FUNZONE voi rat nhieu DV tien ich hap dan: Game online, clip hai dac sac,tin HOT...Truy cap ngay:http://icom.viettelmobile.com.vn";
        	}
        
      String[] arrMTADD = strMTADDNew.split("###");

		if (isExistUserid(sDate, userID, strMsg, serviceID)) {
			return new BigDecimal(1);
		}

		if (arrMTADD[0].equalsIgnoreCase("RANDOM")) {
			if ( arrMTADD.length == 1) {
				return new BigDecimal(1);
			}
			Random iRan = new Random();
			int inum = iRan.nextInt(arrMTADD.length);
			if ( inum ==0) {
				inum = 1;
			}
			
			String mtreturn1 = arrMTADD[inum];
			
			if (mtreturn1 == null || "".equalsIgnoreCase(mtreturn1)) {
				return new BigDecimal(1);
			}
			int bret = 1;

			Random iRandom = new Random();
			int luckynumber = iRandom.nextInt(99999);
			String sLuckynumber = "" + luckynumber;
			sLuckynumber = "00000".substring(0, 5 - sLuckynumber.length())
					+ sLuckynumber;

			boolean save_maso = false;
			if (mtreturn1.indexOf("@MASO") > 0) {
				save_maso = true;
			}
			mtreturn1 = mtreturn1.replaceAll("@MASO", sLuckynumber);
			mtreturn1 = mtreturn1.replaceAll("@NGAYTHANGNAM", sDate);
			mtreturn1 = mtreturn1.replaceAll("@NGAYTHANG", sDate.substring(0,
					5));
			// if (save_maso) {
			bret = savedata(userID, sLuckynumber, sDate, strMsg, serviceID,
					1, 0);
			// }

			if (!"".equalsIgnoreCase(mtreturn1) && mtreturn1 != null
					&& (bret != -1)) {
				sendGifMsg(serviceID, userID, sOperator, strMsg, mtreturn1,
						receiveid);

			}
			//////
			

		} else {
			for (int i = 0; i < arrMTADD.length; i++) {

				String mtreturn = arrMTADD[i];

				if (mtreturn == null || "".equalsIgnoreCase(mtreturn)) {
					return new BigDecimal(1);
				}

				int bret = 1;

				Random iRandom = new Random();
				int luckynumber = iRandom.nextInt(99999);
				String sLuckynumber = "" + luckynumber;
				sLuckynumber = "00000".substring(0, 5 - sLuckynumber.length())
						+ sLuckynumber;

				boolean save_maso = false;
				if (mtreturn.indexOf("@MASO") > 0) {
					save_maso = true;

				}
				mtreturn = mtreturn.replaceAll("@MASO", sLuckynumber);
				mtreturn = mtreturn.replaceAll("@NGAYTHANGNAM", sDate);
				mtreturn = mtreturn.replaceAll("@NGAYTHANG", sDate.substring(0,
						5));

				// if (save_maso) {
				bret = savedata(userID, sLuckynumber, sDate, strMsg, serviceID,
						1, 0);
				// }

				if (!"".equalsIgnoreCase(mtreturn) && mtreturn != null
						&& (bret != -1)) {
					sendGifMsg(serviceID, userID, sOperator, strMsg, mtreturn,
							receiveid);

				}
				
			}
		}

		return new BigDecimal(1);
	}

	public static int savedata(String userid, String luckynumber,
			String luckydate, String commandCode, String serviceId,
			long companyId, int response) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				return -1;
			}
			sqlString = "insert into ICOM_LUCKYNUMBER_USER (service_id,command_code, user_id,  lucky_code, type_cs, lucky_date, response,companyid) values(?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, serviceId);
			statement.setString(2, commandCode);
			statement.setString(3, userid);
			statement.setString(4, luckynumber);
			statement.setString(5, "1");
			statement.setString(6, luckydate);
			statement.setInt(7, response);
			statement.setLong(8, companyId);

			if (statement.executeUpdate() != 1) {
				//System.out.println("Error adding row");
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.error("Error executing " + sqlString + " >>> "
					+ e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error("Error executing " + sqlString + " >>> "
					+ e.toString());
			return -1;
		}

		finally {

			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	String getDate(int iday) {
		long milliSecond = System.currentTimeMillis();

		long lNewtime = milliSecond + iday * 24 * 60 * 60 * 1000;

		return Milisec2DDMMYYYY(lNewtime);

	}

	public static Timestamp getTimestamp(int iDayPlus) {

		java.util.Calendar calendar = java.util.Calendar.getInstance();
		int iDay = calendar.get(calendar.DAY_OF_MONTH);
		calendar.set(calendar.DAY_OF_MONTH, iDay + iDayPlus);

		java.sql.Timestamp tsNew = new java.sql.Timestamp((calendar.getTime())
				.getTime());
		return tsNew;
	}

	/* return date with format: dd/mm/yyyy */
	public static String Milisec2DDMMYYYY(long ts) {
		if (ts == 0) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts));

			String strTemp = Integer.toString(calendar
					.get(calendar.DAY_OF_MONTH));
			if (calendar.get(calendar.DAY_OF_MONTH) < 10) {
				strTemp = "0" + strTemp;
			}
			if (calendar.get(calendar.MONTH) + 1 < 10) {
				return strTemp + "/0" + (calendar.get(calendar.MONTH) + 1)
						+ "/" + calendar.get(calendar.YEAR);
			} else {
				return strTemp + "/" + (calendar.get(calendar.MONTH) + 1) + "/"
						+ calendar.get(calendar.YEAR);
			}
		}
	}
}
