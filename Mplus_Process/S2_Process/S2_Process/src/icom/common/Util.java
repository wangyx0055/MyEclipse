package icom.common;

import icom.COMObject;
import icom.Constants;
import icom.Logger;
import icom.MsgObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class Util {
	public static Logger logger = null;

	public Util() {
		String logfile = "log_process/${yyyy-MM-dd}.log";
		String loglevel = "info,warn,error,crisis";
		logger = new Logger();
		try {
			logger.setLogWriter(logfile);
		} catch (IOException ex) {
		}
		logger.setLogLevel(loglevel);
	}

	public Util(String logfile, String loglevel) {
		logger = new Logger();
		try {
			logger.setLogWriter(logfile);
		} catch (IOException ex) {
		}
		logger.setLogLevel(loglevel);

	}

	public static String getCurrentYearMonthDay() {
		return new Timestamp(System.currentTimeMillis()) + "";

	}

	public static String getCurrentDate() {
		Calendar now = Calendar.getInstance();
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now
				.getTime());
	}

	public static String getBeforeOneDay() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, -1);
		return new java.text.SimpleDateFormat("yyyy-MM-dd").format(now
				.getTime());
	}

	public static HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "&");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			// setParameter(key, value);
			_params.put(key, value);
		}
		return _params;
	}

	public static String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));
			if (temp == null) {
				return _defaultval;
			}

			if ("".equalsIgnoreCase(temp)) {
				return _defaultval;
			}
			return temp;
		} catch (Exception e) {
			return _defaultval;
		}
	}

	public static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);

		return (f > l) ? "" : xml.substring(f, l);
	}

	public static int PaseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception ex) {
			return -1;
		}
	}

	/*****
	 * Ham lay request_id tu link tra ve ben FifMedia
	 * **/
	public static String GetRequestId(String link) {
		String s = link;
		int i = s.indexOf("reqid=");
		if (i > 0)
			s = s.substring(i);
		return s;
	}

	public static String replaceAllWhiteWithOne(String sInput) {
		String strTmp = sInput.trim();
		String strResult = "";
		for (int i = 0; i < strTmp.length(); i++) {
			char ch = strTmp.charAt(i);
			if (ch == ' ') {
				for (int j = i; j < strTmp.length(); j++) {
					char ch2 = strTmp.charAt(j);
					if (ch2 != ' ') {
						i = j;
						strResult = strResult + ' ' + ch2;
						break;
					}
				}

			} else {
				strResult = strResult + ch;
			}

		}
		return strResult;
	}

	public static long ValidDayMonth(String sday, String smonth) {
		try {

			java.util.Calendar calendarcur = java.util.Calendar.getInstance();

			int iday = Integer.parseInt(sday);
			int imonth = Integer.parseInt(smonth) - 1;
			if (iday > 31 && iday < 1) {
				return 0;
			}
			if (imonth > 11 && imonth < 0) {
				return 0;
			}

			java.util.Calendar calendar = java.util.Calendar.getInstance();
			int iyear = calendarcur.get(calendarcur.YEAR);
			if (imonth == 0 && iday <= 19) {
				iyear = iyear + 1;
			}
			calendar.set(iyear, imonth, iday);
			return calendar.getTime().getTime();

		} catch (Exception e) {
		}
		return 0;
	}

	public static String getHoroscope(long requesttime) {

		try {

			if (requesttime < 1)
				return "x";
			long bachduong1 = ValidDayMonth("21", "3");
			long bachduong2 = ValidDayMonth("19", "4");

			if (requesttime >= bachduong1 && requesttime <= bachduong2) {
				return "BACHDUONG";
			}

			long kimnguu1 = ValidDayMonth("20", "4");
			long kimnguu2 = ValidDayMonth("20", "5");

			if (requesttime >= kimnguu1 && requesttime <= kimnguu2) {
				return "KIMNGUU";
			}

			long songtu1 = ValidDayMonth("21", "5");
			long songtu2 = ValidDayMonth("21", "6");

			if (requesttime >= songtu1 && requesttime <= songtu2) {
				return "SONGTU";
			}

			long cugiai1 = ValidDayMonth("22", "6");
			long cugiai2 = ValidDayMonth("22", "7");

			if (requesttime >= cugiai1 && requesttime <= cugiai2) {
				return "CUGIAI";
			}

			long sutu1 = ValidDayMonth("23", "7");
			long sutu2 = ValidDayMonth("22", "8");

			if (requesttime >= sutu1 && requesttime <= sutu2) {
				return "SUTU";
			}

			long xunu1 = ValidDayMonth("23", "8");
			long xunu2 = ValidDayMonth("22", "9");

			if (requesttime >= xunu1 && requesttime <= xunu2) {
				return "XUNU";
			}

			long thienbinh1 = ValidDayMonth("23", "9");
			long thienbinh2 = ValidDayMonth("23", "10");

			if (requesttime >= thienbinh1 && requesttime <= thienbinh2) {
				return "THIENBINH";
			}

			long hocap1 = ValidDayMonth("24", "10");
			long hocap2 = ValidDayMonth("21", "11");

			if (requesttime >= hocap1 && requesttime <= hocap2) {
				return "HOCAP";
			}

			long nhanma1 = ValidDayMonth("22", "11");
			long nhanma2 = ValidDayMonth("21", "12");
			if (requesttime >= nhanma1 && requesttime <= nhanma2) {
				return "NHANMA";
			}

			long maket1 = ValidDayMonth("22", "12");
			long maket2 = ValidDayMonth("19", "1");
			if (requesttime >= maket1 && requesttime <= maket2) {
				return "MAKET";
			}

			long baobinh1 = ValidDayMonth("20", "1");
			long baobinh2 = ValidDayMonth("18", "2");

			if (requesttime >= baobinh1 && requesttime <= baobinh2) {
				return "BAOBINH";
			}

			long songngu1 = ValidDayMonth("19", "2");
			long songngu2 = ValidDayMonth("20", "3");

			if (requesttime >= songngu1 && requesttime <= songngu2) {
				return "SONGNGU";
			}
		} catch (Exception e) {
			logger.info("Util@getHoroscope@ex=" + e.toString());
		}
		return "x";

	}

	/********
	 * function kiem tra co trong giai doan khuyen mai hay khong Su dung khi
	 * dang ky dich vu *
	 ***/
	public static boolean IsServiceFree(icom.Services service) {
		java.util.Date today = new java.util.Date();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(today
				.getTime());
		Util.logger.info("\tactive:" + service.getActiveFree()
				+ "\tservice.getFromDateFree():" + service.getFromDateFree()
				+ "\tservice.getToDateFree():" + service.getToDateFree()
				+ "\tcurrentTimestamp:" + currentTimestamp);
		return service.getActiveFree() == 1
				&& currentTimestamp.compareTo(service.getFromDateFree()) >= 0
				&& currentTimestamp.compareTo(service.getToDateFree()) <= 0;
	}

	/********
	 * function kiem tra thue bao co' trong giai doan KM hay khong Su dung khi
	 * gui command charge sang IN reg_count > 1: dang ky lan dau tien date: thoi
	 * diem dang ky dich vu *
	 ***/
	public static boolean IsUserIdInFreeCharge(int iReg_Count, String sDate,
			String command_code, icom.Services service) throws Exception {

		boolean b = false;
		
		java.sql.Timestamp date;
		// check thoi gian user dang ky co nam trong thoi gian khuyen mai cua service hay khong?
		try {
			date = java.sql.Timestamp.valueOf(sDate);
			b = date.compareTo(service.getFromDateFree()) >= 0
					&& date.compareTo(service.getToDateFree()) <= 0;
		} catch (Exception ex) {
			return false;
		}
		// check 
		
		
		
		java.util.Date today = new java.util.Date();

		/******
		 * lay so ngay *
		 **/
		long ONE_HOUR = 60 * 60 * 1000L;

		long nday = (today.getTime() - date.getTime() + ONE_HOUR)
				/ (ONE_HOUR * 24);

		Util.logger.info("check free charge: command_code=" + command_code
				+ "\treg_count:" + iReg_Count + "\tactive_free:"
				+ service.getActiveFree() + "\tnumber_free:"
				+ service.getNumberFree());
		Util.logger.info("check free charge: so ngay da dung nday:" + nday);
		
		return (iReg_Count == 1 && service.getActiveFree() == 1 && b && nday <= service
				.getNumberFree());
		//return b;
	}

	
	/********
	 * Ham nay dung de split ma code game *
	 ****/
	public static String SplitLastCode(String lastcode) {

		String newlastcode = "";
		String[] tam = null;

		tam = lastcode.split(",");
		for (int i = 0; i < tam.length; i++) {
			if ("".equalsIgnoreCase(newlastcode)) {
				newlastcode = newlastcode + "'" + tam[i] + "'";
			} else {
				newlastcode = newlastcode + ",'" + tam[i] + "'";
			}
		}

		return newlastcode;

	}

	/********
	 * Ham nay dung de tao mot thread xu ly viec luu thong tin vao db type: =1:
	 * save last_code vao mlist type: =2: update autotimestamps,failures=1 vao
	 * mlist, trong truong hop lay link failt type: =3: update last code vao
	 * list send theo dk user id va command code type: =4: luu thong tin game
	 * vao icom_game de sau nay doi soat *
	 ***/
	public static void CallThread(final COMObject obj, final int type) {
		String sIsLogGameInfo = Constants._prop
				.getProperty("IsLogGameInfo", "");

		if (type == 4 && sIsLogGameInfo.equalsIgnoreCase("0"))
			return;

		Thread _thread = new Thread(new Runnable() {
			public void run() {
				switch (type) {
				case 1:
					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ obj.getMlist()
											+ " set last_code = '"
											+ obj.getLastCode()
											+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where user_id = '"
											+ obj.getUserId()
											+ "' and upper(command_code) like '"
											+ obj.getCommandCode() + "%'");

					// Util.logger.info("Update last code: Sql inser:" +
					// "update "
					// + obj.getMlist()
					// + " set last_code = '"
					// + obj.getLastCode()
					// +
					// "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where user_id = '"
					// + obj.getUserId() + "'");
					break;
				case 2:
					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ obj.getMlist()
											+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
											+ obj.getUserId() + "'");
					break;
				case 3:
					DBUtil.executeSQL("gateway", "update list_send "
							+ " set last_code = '" + obj.getLastCode()
							+ "' where user_id = '" + obj.getUserId() + "'"
							+ " and upper(command_code) like '"
							+ obj.getCommandCode().toUpperCase() + "%'");

					// Util.logger.info("Update last code: Sql inser:" +
					// "update list_send "
					// + " set last_code = '"
					// + obj.getLastCode()
					// + " where user_id = '"
					// + obj.getUserId() + "'" +
					// " and upper(command_code) like '"
					// + obj.getCommandCode().toUpperCase() + "%'");
					break;
				case 4: // luu thong tin vao db icom-game
					String tableName = "icom_game"
							+ new SimpleDateFormat("yyyyMM").format(new Date());
					DBUtil.executeSQL("gateway", "INSERT INTO " + tableName
							+ "(USER_ID,TITLE, MA_GAME, LINK)VALUES('"
							+ obj.getUserId() + "'" + ",'"
							+ obj.getNameCateGame() + "', '"
							+ obj.getGameCode() + "', '" + obj.getLinkGame()
							+ "')");
					break;
				default:
					break;
				}
			}
		});
		_thread.start();
	}

	/********
	 * Ham nay dung de init mot thread de xu ly push sang Fifmedia Type: 0:
	 * offline Type: 1: online *
	 *****/
	public static void ThreadGetLinkGameFF(final MsgObject msgObject,
			final String cp, final String MLIST, final String lastcode,
			final String namecode, final String userid,
			final String serviceName, final String code,
			final String DomainServer, final int number_retries, final int type)
			throws Exception {
		Thread _thread = new Thread(new Runnable() {
			public void run() {
				try {
					if (type == 0) {
						// Game.ProcessGetLinkFFOffline(msgObject, cp, MLIST,
						// lastcode, namecode, userid, serviceName, code,
						// DomainServer, number_retries);
					}
					if (type == 1) {
						// Game.ProcessGetLinkFFOnline(msgObject, cp, MLIST,
						// lastcode, namecode, userid, serviceName, code,
						// DomainServer, number_retries);
					}
				} catch (Exception e) {
					Util.logger
							.error("Loi khi lay link game. user_id:" + userid
									+ "\tcommand_code:"
									+ msgObject.getCommandCode()
									+ "\request_id:" + msgObject.getRequestid()
									+ "\tex:" + e.toString());
					DBUtil.Alert("DeliveryDaily@Textbaserandom", "RUNING",
							"major",
							"Kiem tra dich vu: link game lay ve bi null. check ws lay link ngay."
									+ serviceName + "", "");
				}
			}
		});
		_thread.start();
	}
}
