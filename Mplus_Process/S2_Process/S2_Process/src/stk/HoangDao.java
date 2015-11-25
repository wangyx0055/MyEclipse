package stk;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.Util;

public class HoangDao extends QuestionManager {

	public HashMap getParametersAsString(String params) {
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

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {
		Collection messages = new ArrayList();

		try {

			boolean infodetails = false;
			String HOROSCOPE = "";

			HashMap _option = new HashMap();

			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			String poolname = getStringfromHashMap(_option, "pool", "content");

			String horotypecode = getStringfromHashMap(_option, "horotypecode",
					"3");
			int number = Integer.parseInt(horotypecode);
			String type = getStringfromHashMap(_option, "type", "2");
			Util.logger.info("type: " + type);

			String currDate = new SimpleDateFormat("dd/MM/yyyy")
					.format(new Date());

			String info = msgObject.getUsertext().toUpperCase().trim();

			info = info.replace('-', ' ');
			info = info.replace(';', ' ');
			info = info.replace('+', ' ');
			info = info.replace('.', ' ');
			info = info.replace(',', ' ');
			info = info.replace('_', ' ');
			info = info.replace('/', ' ');

			info = replaceAllWhiteWithOne(info.trim());
			String[] arrInfo = info.split(" ");

			if (arrInfo.length > number) {

				String sngay = arrInfo[number];
				String sthang = arrInfo[number + 1];

				long requesttime = validdate(sngay, sthang);

				String shoroscope = getHoroscope(requesttime);

				if (!"x".equalsIgnoreCase(shoroscope)) {
					infodetails = true;
					HOROSCOPE = shoroscope;
					String content = getContent2(poolname, type, HOROSCOPE,
							currDate);

					if ((content == null) || "".equalsIgnoreCase(content)) {
						msgObject.setUsertext("Thong tin chua duoc cap nhat");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(0);
						messages.add(new MsgObject(msgObject));
						return messages;

					} else {
						if ("1".equalsIgnoreCase(type)) {
							String[] contents = content.split("###");
							for (int i = 0; i < contents.length; i++) {
								if (!"".equalsIgnoreCase(contents[i])) {
									if (i == 0) {
										msgObject.setMsgtype(1);
									}
									msgObject.setUsertext(contents[i]);
									msgObject.setContenttype(0);
									messages.add(new MsgObject(msgObject));
								}
							}
							return messages;
						} else {
							msgObject.setUsertext(content);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							messages.add(new MsgObject(msgObject));
							return messages;
						}
					}
				}

			} else {
				msgObject.setUsertext("Tin nhan sai cu phap. Soan tin "
						+ msgObject.getKeyword()
						+ " <ngaysinh> <thangsinh> gui "
						+ msgObject.getServiceid()
						+ " de nhan thong tin trong ngay");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);

		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private String getContent2(String dbcontent, String type, String infoid,
			String sDate) {

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection(dbcontent);
			String content = "content_short";
			// Xac dinh bang nao can lay du lieu
			if ("3".equalsIgnoreCase(type)) {
				content = "content_vi";
			} else if ("2".equalsIgnoreCase(type)) {
				content = "content";
			}

			String query = "SELECT "
					+ content
					+ " FROM ( SELECT TOP 1 * FROM horoscope_info WHERE upper(horotypecode) = '"
					+ infoid.toUpperCase()
					+ "' AND CONVERT(varchar(25), [isdate], 103) ='" + sDate
					+ "')x";

			Vector result = DBUtil.getVectorTable(connection, query);
			if (result.size() == 0) {
				return null;
			} else {

				for (int i = 0; i < result.size(); i++) {

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

	String getHoroscope(long requesttime) {

		try {

			if (requesttime < 1)
				return "x";
			long bachduong1 = validdate("21", "3");
			long bachduong2 = validdate("19", "4");

			if (requesttime >= bachduong1 && requesttime <= bachduong2) {
				return "BACHDUONG";
			}

			long kimnguu1 = validdate("20", "4");
			long kimnguu2 = validdate("20", "5");

			if (requesttime >= kimnguu1 && requesttime <= kimnguu2) {
				return "KIMNGUU";
			}

			long songtu1 = validdate("21", "5");
			long songtu2 = validdate("21", "6");

			if (requesttime >= songtu1 && requesttime <= songtu2) {
				return "SONGTU";
			}

			long cugiai1 = validdate("22", "6");
			long cugiai2 = validdate("22", "7");

			if (requesttime >= cugiai1 && requesttime <= cugiai2) {
				return "CUGIAI";
			}

			long sutu1 = validdate("23", "7");
			long sutu2 = validdate("22", "8");

			if (requesttime >= sutu1 && requesttime <= sutu2) {
				return "SUTU";
			}

			long xunu1 = validdate("23", "8");
			long xunu2 = validdate("22", "9");

			if (requesttime >= xunu1 && requesttime <= xunu2) {
				return "XUNU";
			}

			long thienbinh1 = validdate("23", "9");
			long thienbinh2 = validdate("22", "10");

			if (requesttime >= thienbinh1 && requesttime <= thienbinh2) {
				return "THIENBINH";
			}

			long hocap1 = validdate("23", "10");
			long hocap2 = validdate("21", "11");

			if (requesttime >= hocap1 && requesttime <= hocap2) {
				return "HOCAP";
			}

			long nhanma1 = validdate("22", "11");
			long nhanma2 = validdate("21", "12");
			if (requesttime >= nhanma1 && requesttime <= nhanma2) {
				return "HOCAP";
			}

			long maket1 = validdate("22", "12");
			long maket2 = validdate("19", "1");
			if (requesttime >= maket1 && requesttime <= maket2) {
				return "MAKET";
			}

			long baobinh1 = validdate("20", "1");
			long baobinh2 = validdate("18", "2");

			if (requesttime >= baobinh1 && requesttime <= baobinh2) {
				return "BAOBINH";
			}

			long songngu1 = validdate("19", "2");
			long songngu2 = validdate("20", "3");

			if (requesttime >= songngu1 && requesttime <= songngu2) {
				return "SONGNGU";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "x";

	}

	long validdate(String sday, String smonth) {
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
			int iyear = calendarcur.get(Calendar.YEAR);
			if (imonth == 0 && iday <= 19) {
				iyear = iyear + 1;
			}
			calendar.set(iyear, imonth, iday);
			return calendar.getTime().getTime();

		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	/*
	 * Thay nhieu dau _____ -> _
	 */
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

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));

			if (temp == null) {
				return _defaultval;
			}
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return _defaultval;
			}

			return temp;
		} catch (Exception e) {
			return _defaultval;
		}
	}

}
