package services.binary;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;

import com.vmg.sms.common.Util;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class SoiCau extends ContentAbstract {

	final static String[] sounthKeys = { "BDH", "VL", "TV", "DT", "CM", "BTH",
			"VT", "BL", "CT", "ST", "BT", "TN", "AG", "HCM", "LA", "BP", "TG",
			"KG", "DLK", "KT", "HG", "NT", "KH", "DNG", "DNO", "QNI", "QNM",
			"PY", "GL", "DL", "BD", "DN", "TTH", "QB", "QT", "KH", "HCM", "DNG" };

	private final static String hour2sendMienTrung = "17:00";
	private final static String hour2sendMienBac = "19:00";
	private final static String hour2sendMienNam = "16:00";
	// Sounth Area
	final static int HANOI_COMPANY_ID = 1;
	final static int BINHDUONG_COMPANY_ID = 11;
	final static int SAIGON_COMPANY_ID = 131;
	final static int VUNGTAU_COMPANY_ID = 81;
	final static int BENTRE_COMPANY_ID = 71;
	final static int BACLIEU_COMPANY_ID = 91;
	final static int DONGNAI_COMPANY_ID = 41;
	final static int CANTHO_COMPANY_ID = 51;
	final static int SOCTRANG_COMPANY_ID = 61;
	final static int VINHLONG_COMPANY_ID = 21;
	final static int TRAVINH_COMPANY_ID = 31;
	final static int LONGAN_COMPANY_ID = 201;
	final static int BINHPHUOC_COMPANY_ID = 191;
	final static int DALAT_COMPANY_ID = 181;
	final static int KONTUM_COMPANY_ID = 211;
	final static int KIENGIANG_COMPANY_ID = 171;
	final static int TIENGIANG_COMPANY_ID = 161;
	final static int DONGTHAP_COMPANY_ID = 141;
	final static int CAMAU_COMPANY_ID = 151;
	final static int BINHTHUAN_COMPANY_ID = 101;
	final static int TAYNINH_COMPANY_ID = 111;
	final static int ANGIANG_COMPANY_ID = 121;
	final static int HAUGIANG_COMPANY_ID = 231;
	final static int NINHTHUAN_COMPANY_ID = 241;
	final static int KHANHHOA_COMPANY_ID = 251;
	final static int DANANG_COMPANY_ID = 261;
	final static int BINHDINH_COMPANY_ID = 281;
	final static int DAKLAK_COMPANY_ID = 271;
	final static int GIALAI_COMPANY_ID = 221;
	final static int PHUYEN_COMPANY_ID = 291;
	final static int QUANGNAM_COMPANY_ID = 301;
	final static int QUANGNGAI_COMPANY_ID = 311;
	final static int DACNONG_COMPANY_ID = 321;
	final static int QUANGBINH_COMPANY_ID = 341;
	final static int QUANGTRI_COMPANY_ID = 351;
	final static int HUE_COMPANY_ID = 331;
	final static int BINHDUONG6_COMPANY_ID = 361;

	// North Area
	final static int BACGIANG_COMPANY_ID = 511;
	final static int BACKAN_COMPANY_ID = 521;
	final static int BACNINH_COMPANY_ID = 531;
	final static int CAOBANG_COMPANY_ID = 541;
	final static int HOABINH_COMPANY_ID = 551;
	final static int HAIDUONG_COMPANY_ID = 561;
	final static int HAGIANG_COMPANY_ID = 571;
	final static int HANAM_COMPANY_ID = 581;
	final static int HAIPHONG_COMPANY_ID = 591;
	final static int HATAY_COMPANY_ID = 601;
	final static int HATINH_COMPANY_ID = 611;
	final static int HUNGYEN_COMPANY_ID = 621;
	final static int LAICHAU_COMPANY_ID = 631;
	final static int LAOCAI_COMPANY_ID = 641;
	final static int LANGSON_COMPANY_ID = 651;
	final static int NINHBINH_COMPANY_ID = 671;
	final static int NAMDINH_COMPANY_ID = 681;
	final static int PHUTHO_COMPANY_ID = 691;
	final static int QUANGNINH_COMPANY_ID = 711;
	final static int SONLA_COMPANY_ID = 731;
	final static int THAIBINH_COMPANY_ID = 741;
	final static int THANHHOA_COMPANY_ID = 751;
	final static int THAINGUYEN_COMPANY_ID = 761;
	final static int TUYENQUANG_COMPANY_ID = 661;
	final static int VINHPHUC_COMPANY_ID = 701;
	final static int YENBAI_COMPANY_ID = 721;
	final static int NGHEAN_COMPANY_ID = 771;
	final static int DIENBIEN_COMPANY_ID = 781;

	// ID cac mien
	final static int MIENNAM_COMPANY_ID = 901;
	final static int MIENTRUNG_COMPANY_ID = 902;

	final static int[] southCompanies = { BINHDINH_COMPANY_ID,
			VINHLONG_COMPANY_ID, TRAVINH_COMPANY_ID, DONGTHAP_COMPANY_ID,
			CAMAU_COMPANY_ID, BINHTHUAN_COMPANY_ID, VUNGTAU_COMPANY_ID,
			BACLIEU_COMPANY_ID, CANTHO_COMPANY_ID, SOCTRANG_COMPANY_ID,
			BENTRE_COMPANY_ID, TAYNINH_COMPANY_ID, ANGIANG_COMPANY_ID,
			SAIGON_COMPANY_ID, LONGAN_COMPANY_ID, BINHPHUOC_COMPANY_ID,
			TIENGIANG_COMPANY_ID, KIENGIANG_COMPANY_ID, HAUGIANG_COMPANY_ID,
			DALAT_COMPANY_ID, BINHDUONG_COMPANY_ID, DONGNAI_COMPANY_ID,
			MIENNAM_COMPANY_ID };

	final static int[] middleCompanies = { DAKLAK_COMPANY_ID,
			QUANGNGAI_COMPANY_ID, QUANGNAM_COMPANY_ID, HUE_COMPANY_ID,
			KHANHHOA_COMPANY_ID, DANANG_COMPANY_ID, QUANGBINH_COMPANY_ID,
			QUANGTRI_COMPANY_ID, KONTUM_COMPANY_ID, DACNONG_COMPANY_ID,
			KHANHHOA_COMPANY_ID, NINHTHUAN_COMPANY_ID, GIALAI_COMPANY_ID,
			MIENTRUNG_COMPANY_ID };

	final static int[] northCompanies = { BACGIANG_COMPANY_ID,
			BACKAN_COMPANY_ID, BACNINH_COMPANY_ID, CAOBANG_COMPANY_ID,
			HOABINH_COMPANY_ID, HAIDUONG_COMPANY_ID, HAGIANG_COMPANY_ID,
			HANAM_COMPANY_ID, HAIPHONG_COMPANY_ID, HATINH_COMPANY_ID,
			HATAY_COMPANY_ID, HUNGYEN_COMPANY_ID, LAICHAU_COMPANY_ID,
			LAOCAI_COMPANY_ID, LANGSON_COMPANY_ID, NINHBINH_COMPANY_ID,
			NAMDINH_COMPANY_ID, PHUTHO_COMPANY_ID, QUANGNINH_COMPANY_ID,
			SONLA_COMPANY_ID, THAIBINH_COMPANY_ID, THANHHOA_COMPANY_ID,
			THAINGUYEN_COMPANY_ID, TUYENQUANG_COMPANY_ID, VINHPHUC_COMPANY_ID,
			YENBAI_COMPANY_ID, NGHEAN_COMPANY_ID, DIENBIEN_COMPANY_ID,
			HANOI_COMPANY_ID };
	public static String[] prefixCAU = { "CAU ", "CAU.", "CAU,", "CAU<" };
	public static String[] prefixSOI = { "SOI ", "SOI.", "SOI,", "SOI<" };

	public static String[] prefix = { "CAU", "SOI" };

	// Kiểm tra xem Msg vào có đúng là với mã tỉnh không?
	public static String validMsgInput(String sMsg) {
		String strTemp = replaceAllPointWithSpace(sMsg);
		strTemp = replaceAllWhiteWithOne(strTemp.trim()).trim().toUpperCase();

		for (int i = 0; i < prefix.length; i++) {
			if (strTemp.startsWith(prefix[i])) {
				strTemp = "CAU"
						+ replaceWhiteLetter(strTemp.substring(prefix[i]
								.length()));
			}
		}

		for (int i = 0; i < prefixCAU.length; i++) {
			if (strTemp.startsWith(prefixCAU[i])) {
				return prefixCAU[i].substring(0, 3)
						+ replaceWhiteLetter(strTemp.substring(4));
			}
		}

		for (int i = 0; i < prefixSOI.length; i++) {
			if (strTemp.startsWith(prefixSOI[i])) {
				return prefixSOI[i].substring(0, 3)
						+ replaceWhiteLetter(strTemp.substring(4));
			}
		}

		return strTemp;
	}

	public static String replaceAllPointWithSpace(String sInput) {
		String strTmp = sInput;
		for (int i = 0; i < sInput.length(); i++) {
			char ch = sInput.charAt(i);
			if (ch == '.') {
				strTmp = strTmp.replace(ch, ' ');
			}
		}
		return strTmp;
	}

	public static String replaceWhiteLetter(String sInput) {
		String strTmp = sInput;
		String sReturn = "";
		boolean flag = true;
		int i = 0;
		while (i < sInput.length() && flag) {
			char ch = sInput.charAt(i);
			if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')
					|| (ch >= 'a' && ch <= 'z')) {
				flag = false;
			} else {
				strTmp = sInput.substring(i + 1);
			}
			i++;

		}
		i = strTmp.length() - 1;
		flag = true;
		sReturn = strTmp;
		while (i >= 0 && flag) {
			char ch = strTmp.charAt(i);
			if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')
					|| (ch >= 'a' && ch <= 'z')) {
				flag = false;
			} else {
				sReturn = strTmp.substring(0, i);
			}
			i--;
		}
		return sReturn;
	}

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		// String dbcontent = "textbase";
		String gameid = "soicau";
		String sServiceID = msgObject.getServiceid();
		String sMsg = replaceAllWhiteLetter(msgObject.getUsertext());

		// String COMPANY_NAME = "";
		String COMMAND_CODE = "INV";
		String sMsg1 = replaceAllWhiteWithOne(sMsg);
		sMsg1 = validMsgInput(sMsg1);
		// Lấy CAUXXX
		COMMAND_CODE = getCommandCode(sMsg1);

		try {

			// Neu ma tinh khong ton tai thi return luon
			if ("INV".equalsIgnoreCase(COMMAND_CODE)) {
				msgObject
						.setUsertext("Tin sai cu phap. Hay soan tin:\n*CAU<MaTinh> gui "
								+ sServiceID
								+ " de soi cau.\n*XS<MaTinh> de xem Tuong thuat truc tiep ket qua");
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			int companyId = getCompanyId(COMMAND_CODE);
			// Lay noi dung va tra ve
			String hour = getTimeLottery(companyId);

			// Neu thoi gian ma lon hon
			if (!isNewSession(hour)) {
				msgObject
						.setUsertext("Hien tai he thong chua cap nhat cho ngay moi. Vui long lien he lai");
			} else {
				String content = getContent(gameid, COMMAND_CODE);
				String[] sTokens = content.split("###");

				for (int i = 0; i < sTokens.length; i++) {
					if (!"".equalsIgnoreCase(sTokens[i])) {
						msgObject.setUsertext(sTokens[i]);
						if (i == 0) {
							msgObject.setMsgtype(1);
						} else {
							msgObject.setMsgtype(0);
						}
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
					}
				}
			}
			return messages;

		} catch (Exception e) {
			msgObject
					.setUsertext("Tin sai cu phap. Hay soan tin:\n*CAU<MaTinh> gui "
							+ sServiceID
							+ " de soi cau.\n*XS<MaTinh> de xem Tuong thuat truc tiep ket qua");
		}
		return messages;

	}

	// Lay noi dung trong icom_textbase_data
	private String getContent(String gameid, String commandcode) {

		// String content = "";
		// String area = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		// String[] result = null;
		String result = "";

		try {

			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			// Lấy company_id của commandcode
			// /int company_id = getCompanyId(commandcode);
			// String company_name = "";

			String query = "SELECT content FROM icom_textbase_data WHERE ( upper(gameid) = '"
					+ gameid.toUpperCase()
					+ "') AND ( upper(subcode1) ='"
					+ commandcode.toUpperCase() + "')";
			statement = connection.prepareStatement(query);
			
			Util.logger.info("LOST MESSAGE" + query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);

				}
			}
			return result;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + " SoiCauXoSo: Failed"
					+ ex.getMessage());
			ex.printStackTrace();
			// content = "Ma tinh khong hop le. Soan tin: CAU<Matinh> gui 8551
			// de soi cau chinh xac nhat";
			return result;
		} finally {

			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}

	}

	private static String replaceAllWhiteLetter(String sInput) {
		String strTmp = sInput;
		for (int i = strTmp.length() - 1; i >= 0; i--) {
			char ch = strTmp.charAt(i);
			if ((ch <= '/') || (ch > '9' && ch < 'A') || (ch > 'Z' && ch < 'a')
					|| (ch > 'z')) {
				strTmp = strTmp.replace(ch, ' ');
			}
		}
		return strTmp;
	}

	public static String replaceAllWhiteWithOne(String sInput) {

		String strTmp = sInput.trim();
		strTmp = strTmp.replace('-', ' ');
		strTmp = strTmp.replace('.', ' ');
		strTmp = strTmp.replace('/', ' ');
		strTmp = strTmp.replace('_', ' ');
		strTmp = strTmp.replace(',', ' ');

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

	public static String getCommandCodeFull(String sMsg, String keyword) {

		String strTemp = sMsg.trim().toUpperCase();
		for (int i = 0; i < prefix.length; i++) {
			if (strTemp.startsWith(prefix[i])) {
				return "CAU" + sounthKeys[i];
			}
		}
		return keyword;
	}

	public static String getCommandCode(String sMsg) {
		String strTemp = sMsg.trim();

		if (strTemp.startsWith("CAUTD")) {
			return "CAUTD";
		}

		if (strTemp.startsWith("CAUMN")) {
			return "CAUMN";
		}
		if (strTemp.startsWith("CAUMT")) {
			return "CAUMT";
		}
		if (strTemp.startsWith("CAUHNM")) {
			return "CAUHNM";
		}
		if (strTemp.startsWith("CAUHNA")) {
			return "CAUHNA";
		}

		if (strTemp.startsWith("CAUMB") || strTemp.startsWith("CAUMA")) {
			return "CAUMB";
		}
		if (strTemp.startsWith("CAUHN")) {
			return "CAUHN";
		}

		if (strTemp.startsWith("CAUHCM")) {
			return "CAUHCM";
		}
		if (strTemp.startsWith("CAUVT")) {
			return "CAUVT";
		}

		if (strTemp.startsWith("CAUBTN")) {
			return "CAUBTN";
		}

		if (strTemp.startsWith("CAUBTH")) {
			return "CAUBTH";
		}

		if (strTemp.startsWith("CAUDLK")) {
			return "CAUDLK";
		}

		if (strTemp.startsWith("CAUDL")) {
			return "CAUDL";
		}

		if (strTemp.startsWith("CAULD")) {
			return "CAULD";
		}
		if (strTemp.startsWith("CAUBDI")) {
			return "CAUBDI";
		}
		if (strTemp.startsWith("CAUBDH")) {
			return "CAUBDH";
		}

		if (strTemp.startsWith("CAUDNA")) {
			return "CAUDNA";
		}
		if (strTemp.startsWith("CAUDNG")) {
			return "CAUDNG";
		}
		if (strTemp.startsWith("CAUQNG")) {
			return "CAUQNG";
		}
		if (strTemp.startsWith("CAUTNG")) {
			return "CAUTNG";
		}

		if (strTemp.startsWith("CAUBK")) {
			return "CAUBK";
		}

		if (strTemp.startsWith("CAUHGG")) {
			return "CAUHGG";
		}

		if (strTemp.startsWith("CAUHTH")) {
			return "CAUHTH";
		}

		if (strTemp.startsWith("CAULCH")) {
			return "CAULCH";
		}

		if (strTemp.startsWith("CAUDN0")) {
			return "CAUDNO";
		}
		if (strTemp.startsWith("CAUKONTUM")) {
			return "CAUKT";
		}
		if (strTemp.startsWith("CAUBD6")) {
			return "CAUBD6";
		}
		if (strTemp.startsWith("CAUMH")) {
			return "CAUBL";
		}

		if (strTemp.startsWith("CAUSB")) {
			return "CAUBD";
		}
		//
		if (strTemp.startsWith("CAUTPHCM")) {
			return "CAUTPHCM";
		}
		if (strTemp.startsWith("CAUTP")) {
			return "CAUTP";
		}
		if (strTemp.startsWith("CAUDNG")) {
			return "CAUDNG";
		}
		if (strTemp.startsWith("CAUQNM")) {
			return "CAUQNM";
		}
		if (strTemp.startsWith("CAUQNA")) {
			return "CAUQNA";
		}

		if (strTemp.startsWith("CAUQNI")) {
			return "CAUQNI";
		}

		if (strTemp.startsWith("CAUBRVT")) {
			return "CAUBRVT";
		}
		if (strTemp.startsWith("CAUBC")) {
			return "CAUBC";
		}
		if (strTemp.startsWith("CAUBTE")) {
			return "CAUBTE";
		}
		if (strTemp.startsWith("CAUBTR")) {
			return "CAUBTR";
		}
		if (strTemp.startsWith("CAUHTA")) {
			return "CAUHTA";
		}
		if (strTemp.startsWith("CAUHTY")) {
			return "CAUHTY";
		}
		if (strTemp.startsWith("CAUHTA")) {
			return "CAUHTA";
		}
		if (strTemp.startsWith("CAULCI")) {
			return "CAULCI";
		}
		if (strTemp.startsWith("CAULCA")) {
			return "CAULCA";
		}
		if (strTemp.startsWith("CAUQNH")) {
			return "CAUQNH";
		}
		if (strTemp.startsWith("CAUTNN")) {
			return "CAUTNN";
		}
		if (strTemp.startsWith("CAUHNA")) {
			return "CAUHNA";
		}
		if (strTemp.startsWith("CAULCU")) {
			return "CAULCU";
		}
		if (strTemp.startsWith("CAUHTI")) {
			return "CAUHTI";
		}
		if (strTemp.startsWith("CAUHGI")) {
			return "CAUHGI";
		}
		if (strTemp.startsWith("CAUHAG")) {
			return "CAUHAG";
		}

		for (int i = 0; i < sounthKeys.length; i++) {
			if (strTemp.startsWith("CAU" + sounthKeys[i])) {
				return "CAU" + sounthKeys[i];
			}
		}

		if (strTemp.startsWith("CAU")) {
			return "CAU";
		}

		return "INV";
	}

	// Xử lý xâu đưa vào xem ??
	static public int getCompanyId(String commandCode) {

		if ("CAU".equals(commandCode)) {
			return HANOI_COMPANY_ID;
		}
		if ("CAUTD".equals(commandCode)) {
			return HANOI_COMPANY_ID;
		}

		if ("CAUMN".equals(commandCode)) {
			return MIENNAM_COMPANY_ID;
		}
		if ("CAUMT".equals(commandCode)) {
			return MIENTRUNG_COMPANY_ID;
		}
		if ("CAUMB".equals(commandCode)) {
			return HANOI_COMPANY_ID;
		}
		if ("CAUHN".equals(commandCode)) {
			return HANOI_COMPANY_ID;
		}
		if ("CAUBD6".equals(commandCode)) {
			return BINHDUONG_COMPANY_ID;
		}
		//
		if ("CAUTPHCM".equals(commandCode)) {
			return SAIGON_COMPANY_ID;
		}

		if ("CAUTP".equals(commandCode)) {
			return SAIGON_COMPANY_ID;
		}
		if ("CAUDNG".equals(commandCode)) {
			return DANANG_COMPANY_ID;
		}
		if ("CAUDNA".equals(commandCode)) {
			return DANANG_COMPANY_ID;
		}

		if ("CAUQNI".equals(commandCode)) {
			return QUANGNGAI_COMPANY_ID;
		}
		if ("CAUQNM".equals(commandCode)) {
			return QUANGNAM_COMPANY_ID;
		}
		if ("CAUQNA".equals(commandCode)) {
			return QUANGNAM_COMPANY_ID;
		}

		if ("CAUBRVT".equals(commandCode)) {
			return VUNGTAU_COMPANY_ID;
		}
		if ("CAUBDI".equals(commandCode)) {
			return BINHDINH_COMPANY_ID;
		}
		if ("CAUBC".equals(commandCode)) {
			return BACKAN_COMPANY_ID;
		}
		if ("CAUBTE".equals(commandCode)) {
			return BENTRE_COMPANY_ID;
		}
		if ("CAUBTR".equals(commandCode)) {
			return BENTRE_COMPANY_ID;
		}
		if ("CAUBTN".equals(commandCode)) {
			return BINHTHUAN_COMPANY_ID;
		}
		if ("CAULD".equals(commandCode)) {
			return DALAT_COMPANY_ID;
		}
		if ("CAUHAG".equals(commandCode)) {
			return HAGIANG_COMPANY_ID;
		}
		if ("CAUHGI".equals(commandCode)) {
			return HAGIANG_COMPANY_ID;
		}
		if ("CAUHTA".equals(commandCode)) {
			return HATAY_COMPANY_ID;
		}
		if ("CAUHTY".equals(commandCode)) {
			return HATAY_COMPANY_ID;
		}
		if ("CAUHTI".equals(commandCode)) {
			return HATINH_COMPANY_ID;
		}
		if ("CAULCI".equals(commandCode)) {
			return LAOCAI_COMPANY_ID;
		}
		if ("CAULCA".equals(commandCode)) {
			return LAOCAI_COMPANY_ID;
		}
		if ("CAULCH".equals(commandCode)) {
			return LAICHAU_COMPANY_ID;
		}
		if ("CAUQNH".equals(commandCode)) {
			return QUANGNINH_COMPANY_ID;
		}
		if ("CAUTNN".equals(commandCode)) {
			return THAINGUYEN_COMPANY_ID;
		}
		if ("CAUBRVT".equals(commandCode)) {
			return VUNGTAU_COMPANY_ID;
		}
		if ("CAUHNA".equals(commandCode)) {
			return HANAM_COMPANY_ID;
		}
		if ("CAUQNG".equals(commandCode)) {
			return QUANGNGAI_COMPANY_ID;
		}

		//

		for (int j = 0; j < southCompanies.length; j++) {
			if (commandCode.equals("CAU" + sounthKeys[j])) {
				return southCompanies[j];
			}
		}

		return -1;

	}

	// Có thuộc miền Bắc không?
	static public String getTimeLottery(int companyId) {
		for (int i = 0; i < northCompanies.length; i++) {
			if (companyId == northCompanies[i]) {
				return hour2sendMienBac;
			}
		}
		for (int i = 0; i < southCompanies.length; i++) {
			if (companyId == southCompanies[i]) {
				return hour2sendMienNam;
			}
		}
		for (int i = 0; i < middleCompanies.length; i++) {
			if (companyId == northCompanies[i]) {
				return hour2sendMienNam;
			}
		}
		return null;
	}

	// Co phai la > 9:00 khong??
	public boolean isNewSession(String hour2send) {
		// String hour2send = "09:00";
		String sTime2Queue = hour2send;

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

		/* Lấy thời gian hiện tại */
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
}
