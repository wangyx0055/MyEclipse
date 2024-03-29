package vtv6;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.tempuri.Tv_updateSoapProxy;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

/**
 * Game.<br>
 * 
 * <pre>
 * Dich vu cho phep vote bai hat
 * </pre>
 * 
 * @author Haptt
 * @version 1.0
 */
public class votebaihat extends ContentAbstract {

	/* First String */

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "1";
		String sodiem = "5 diem";
		String url_vote = Constants._prop.getProperty("url_bcbh_v6");
		String username = Constants._prop.getProperty("user_bcbh_v6");
		String password = Constants._prop.getProperty("pass_bcbh_v6");
		Tv_updateSoapProxy wsvote = new Tv_updateSoapProxy();
		wsvote.setEndpoint(url_vote);
		String result = "";
		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";
			String mt1 = "De tai Game theo ma so, soan: GAME maso gui 8751.Cai dat GPRS 3.0 tu dong ve may de co the luot web va choi game,tai nhac,soan tin GPRS gui 8751.";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				inv_telco = getString(_option, "inv_telco", inv_telco);
				ismtadd = getString(_option, "ismtadd", ismtadd);
				sodiem = getString(_option, "sodiem", sodiem);
				mt1 = getString(_option, "mt1", mt1);
				if("8751".equalsIgnoreCase(msgObject.getServiceid())) {
					sodiem = "15";
				} else if("8551".equalsIgnoreCase(msgObject.getServiceid())) {
					sodiem = "5";
				}
			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			/* kiem tra thue bao khach hang */
			if ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator())) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}


			// lay so thue bao nguoi gui
			String userid = msgObject.getUserid();
			/* lay noi dung gui */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext());
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			String mt2 = "Tang ban 3 game online HOT nhat hien nay.DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm";

			if (sTokens.length == 1) {
				msgObject
						.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				
				msgObject.setUsertext(mt2);
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {
				userText = userText.replace(";", "");
				userText = userText.replace("%", "");
				userText = userText.replace("#", "");
				userText = userText.replace("<", "");
				userText = userText.replace(">", "");
				userText = userText.replace("(", "");
				userText = userText.replace(")", "");
				userText = userText.replace("[", "");
				userText = userText.replace("]", "");
				userText = userText.replace("-", "");
				userText = userText.replace(",", "");
				userText = userText.replace(".", "");
				userText = userText.replace("%", "");
				userText = userText.replace("{", "");
				userText = userText.replace("}", "");
				userText = userText.replace("/", "");
				userText = userText.replace("?", "");
				userText = userText.replace("$", "");
				userText = userText.replace("&", "");
				userText = userText.replace("@", "");
				userText = userText.replace("*", "");
				userText = userText.replace("'", "");
				String susertext = userText.replaceAll(" ", "");
				susertext = susertext.replaceAll("j", "i");
				
				String tenbaihat = susertext.substring(msgObject.getKeyword()
						.length(), susertext.length());
				
				String ten_bh = "";
				String baihat = "";
				baihat = wsvote.searchSongHot(username, password, "", 3);
				if("-1".equalsIgnoreCase(baihat)) {
					Util.logger.info("@class:votebaihat_Khong tim thay bai hat");
				} else if("-1".equalsIgnoreCase(baihat) || "-2".equalsIgnoreCase(baihat)) {
					Util.logger.info("@class: goi ws loi");
				} 

				susertext = msgObject.getKeyword() + " " + tenbaihat;
				if(sTokens.length==2) {
					//check xem truong hop la ma bh hay ten bai hat.
					if(sTokens[1].matches("[0-9]+$")) {
						//TH toan so search theo so. con lai search theo ten bh
						result = wsvote.voteSongById(username, password, sTokens[1], userid, sodiem);
						ten_bh = wsvote.searchSongName(username, password, sTokens[1], 1);
					} else {
						result = wsvote.voteSongByName(username, password, sTokens[1], userid, sodiem);
					}
				} else if(sTokens.length > 2){
					for(int i = 1; i<sTokens.length; i++) {
						ten_bh += sTokens[i] + " ";
					}
					result = wsvote.voteSongByName(username, password, tenbaihat, userid, sodiem);
				}
				
				// MT2
				String listgame = "";
				
				if ("1".equalsIgnoreCase(result)) {
					listgame = "Ban da binh chon "
							+ sodiem
							+ " diem cho bai hat "
							+ ten_bh
							+ ".Cam on da tham gia iMbox. Lien he 1900571566 de duoc ho tro.";
					msgObject.setUsertext(listgame);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					
					msgObject.setUsertext("Cac bai hat Hot tren iMbox: "+baihat+".Binh chon bai hat soan VO tenbaihat Gui 8751");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					msgObject.setUsertext("Cai dat bai hat " + ten_bh
							+ "lam nhac cho soan NC " + ten_bh.replace(" ", "")
							+ " (la ten bai hat viet lien) Gui 8751");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				} else if("-1".equalsIgnoreCase(result)){
					listgame = "Bai hat ban thich hien chua co tren he thong. Tra cuu ma bai hat soan VC tenbaihat Gui 8351. Len he 1900571566 de duoc ho tro";
					msgObject.setUsertext(listgame);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					msgObject.setUsertext("Cac bai hat Hot tren iMbox:"
							+ baihat
							+ ".Binh chon bai hat soan VO tenbaihat Gui 8751");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				}
				return null;
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {

			if ("1".equalsIgnoreCase(ismtadd))
				ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
						.getServiceid(), msgObject.getUserid(), msgObject
						.getKeyword(), msgObject.getRequestid(), msgObject
						.getTTimes(), msgObject.getMobileoperator());
		}
	}

	/* ghi lai dsach khach hang */
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

	// Replace ____ with _
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

	/* Chia thanh 2 MT */
	private static String[] splitString(String splitS) {
		String[] result = new String[2];
		boolean resultBoolean = false;
		int i = 160;
		int j = 0;
		String tempString = splitS;
		if (splitS.length() >= 160) {
			while (!(resultBoolean)) {
				if (splitS.charAt(i) == ';') {
					result[0] = splitS.substring(0, i);
					j = i + 1;
					resultBoolean = true;
				}
				i--;
			}
			resultBoolean = false;
			i = tempString.length() - 1;

			/* tach thanh 2 */
			while (!(resultBoolean)) {
				// neu <160
				if ((tempString.charAt(i) == ';') && ((i - j) <= 160)) {
					result[1] = tempString.substring(j, i);
					resultBoolean = true;
				}
				i--;
			}
		} else {
			result[0] = splitS;
			result[1] = "";
		}

		return result;

	}

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ((temp == null) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}
	}

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


}
