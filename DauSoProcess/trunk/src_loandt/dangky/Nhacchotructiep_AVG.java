package dangky;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import com.vmg.soap.mo.sendXMLRING;

import cs.ExecuteADVCR;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * </pre>
 * 
 * @author Vietnamnet I-Com HaPTT
 * @version 1.0
 */
public class Nhacchotructiep_AVG extends ContentAbstract {

	/* First String */
	private static String urlring = "http://203.162.71.165:8686/SetRingBack.asmx?";
	private static String vinaphoneGuide = "Bai hat ban thich hien tai chua co trong he thong.Buoc1:Dang ki su dung,soan:DK gui 9194.Buoc 2:Chon bai hat,soan:TUNE maso gui 9194.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String mobifoneGuide = "Bai hat ban thich hien tai chua co trong he thong.Buoc1:Dang ki su dung,soan:DK gui 9224.Buoc 2:Chon bai hat,soan:CHON maso gui 9224.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String beelineGuide = "Bai hat ban thich hien tai chua co trong he thong.De tai bai hat.Soan tin:CHON masobaihat gui 1221.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String viettelGuide = "Bai hat ban thich hien tai chua co trong he thong.Buoc1:Dang ki su dung,soan:DK gui 1221.Buoc 2:Chon bai hat,soan:BH maso gui 1221.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		
		
		// Xac thuc quyen 
		
		String urlAVG = "http://118.70.205.134:880/avgmtws/services/Receiver";
		String usernameAVG = "icom";
		String passwordAVG = "R3o11v0rP1110cI";
		urlAVG = Constants._prop.getProperty("urlAVG" +"", urlAVG);
		usernameAVG = Constants._prop.getProperty("usernameAVG" +"", usernameAVG);
		passwordAVG = Constants._prop.getProperty("passwordAVG" +"", passwordAVG);
		
		String infoid = "VIETTEL";		
		String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";
		
		/* kiem tra thue bao khach hang */
		String mt = "";		
		if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
				|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {
			infoid = "viettel";
			mt = viettelGuide;
		} else if (("VMS".equalsIgnoreCase(msgObject.getMobileoperator()))
				|| "mobifone".equalsIgnoreCase(msgObject
						.getMobileoperator())) {
			infoid = "mobifone";
			mt = mobifoneGuide;
		} else if (("GPC".equalsIgnoreCase(msgObject.getMobileoperator()))
				|| ("VINAPHONE".equalsIgnoreCase(msgObject
						.getMobileoperator()))) {
			infoid = "vinaphone";
			mt = vinaphoneGuide;
		} else {
			infoid = "other";
			mt = inv_telco;
		}
		
		// lay ngay thang hien tai
		
	//	String now = now();
		
	//	Util.logger.info(" NOW____" + now);
		String commandCode = msgObject.getKeyword();
		String msisdn = msgObject.getUserid();
		String info =  msgObject.getUsertext();
		info = replaceAllWhiteWithOne (info);
		String songname = info.substring(commandCode.length() +1);
		songname = songname.replace(" ", "");
		
		if(!songname.equalsIgnoreCase(""))
		{
			// Check ma bai hat co tren he thong
			// 0 la ten bai hat, 1 ma ma bai hat
			// Tim kiem tuyet doitruoc
			// Neu ko co thi tim kiem theo like
			
			String[] resultMusicID = new String[2];			
			resultMusicID = findMusicID(songname, infoid);
			if ("".equalsIgnoreCase(resultMusicID[1])) {
				resultMusicID = findMusicIDlike(songname, infoid);
			}			
			
			// Bài hay co tren he thong
			if (!"".equalsIgnoreCase(resultMusicID[0])) 
			{
				// Rieng voi VMS chi tra ma
				if(infoid.equalsIgnoreCase("mobifone"))
				{
					// check xem so thue bao da dky sd nhac cho
					
					String resultStatus = sendXMLRING.SendXML(urlring, "GetStatus", msisdn, "",resultMusicID[1], now());
					if(resultStatus.equalsIgnoreCase("0"))
					{
						mt = "Ma so bai hat " + resultMusicID[1] + " la: " + resultMusicID[0]+",soan: CHON " + resultMusicID[0]+" gui 9224.";
					}
					else if(resultStatus.equalsIgnoreCase("200"))
					{
						mt = "De dang ky. Buoc1, soan DK gui 9224.Buoc2, soan: CHON " + resultMusicID[0]+" gui 9224.";
					}
					else if(resultStatus.equalsIgnoreCase("1"))
					{
						mt = "Khong thanh cong!";
					}
				}
				else
				{
					String resultSet = sendXMLRING.SendXML(urlring, "SetTone", msisdn, "",resultMusicID[1], now());
					if(resultSet.equalsIgnoreCase("0"))
					{
						mt = "Ban da cai dat thanh cong bai " + resultMusicID[1]+" lam nhac cho";
					}
					else if(resultSet.equalsIgnoreCase("101"))
					{
						mt = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau.";
					}
					else if(resultSet.equalsIgnoreCase("501"))
					{
						mt = "Bai hat da co trong bo suu tap cua ban.";
					}
					else if(resultSet.equalsIgnoreCase("504"))
					{
						mt = "Bo suu tap cua ban da day.Xin vui long xoa bot neu muon cai bai hat moi.";
					}
				}			
			}			
		}
		else
		{
			mt = "Tin nhan sai cu phap. Soan NCC <tenbaihat> gui 6787.";
		}
		// forward MT sang AVG
		
		/* String resultSendMT = sendXMLRING.sendMTXML(urlAVG, 
				msisdn, msgObject.getKeyword(), msgObject.getServiceid(), mt, msgObject.getMsgtype(), 
				msgObject.getRequestid(), 1,
				1, 1, 0);
		 String result = sendXMLRING.parseResult(resultSendMT);
		 Util.logger.info( " result" + result);
		if(result.equalsIgnoreCase("1"))
			Util.logger.info( " Foward thanh cong !");
		else
			Util.logger.info( " Foward loi !"); */
		
		// insert vao bang cdr_log va ems_send_log cuar ICOM
		addCDRlog(msgObject,mt);
		addEmslog(msgObject,mt);		
		return null;
	}
	
	
	private static BigDecimal addtoINV(MsgObject msgObject, String mt) {

		Util.logger.info("sms_receive_queue_inv:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_queue_inv";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE)"
				+ " values(?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getUsertext());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2moinv:" + msgObject.getUserid() + ":"
						+ msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext() + ":Error add row from moinv:"
					+ e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext() + ":Error add row from moinv:"
					+ e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}
	private static BigDecimal addCDRlog(MsgObject msgObject, String mt) {

		Util.logger.info("cdr_log:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext() );
		PreparedStatement statement = null;
		String sSQLInsert = null;
		String yyyymm = yyyymm();
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tblCDRlog = "cdr_log" + yyyymm ;
		sSQLInsert = "insert into "
				+ tblCDRlog
				+ "(user_id,service_id,mobile_operator,command_code,info" +
				",submit_date,done_date, CPId,REQUEST_ID,MESSAGE_TYPE," +
				"PROCESS_RESULT,CONTENT_TYPE)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, mt);
			statement.setTimestamp(6, msgObject.getTTimes());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setString(8, "26");
			statement.setBigDecimal(9, msgObject.getRequestid());
			statement.setInt(10, 1);
			statement.setInt(11, 1);
			statement.setInt(12, msgObject.getContenttype());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add cdr_log"+yyyymm+":" + msgObject.getUserid() + ":"
						+ msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add cdr_log"+yyyymm+":" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext() + ":Error add row from cdr log:"
					+ e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext() + ":Error add row from cdr log:"
					+ e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private static BigDecimal addEmslog(MsgObject msgObject, String mt) {

		Util.logger.info("ems_send_log:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext() );
		PreparedStatement statement = null;
		String sSQLInsert = null;
		String yyyymm = yyyymm();
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tblCDRlog = "ems_send_log" + yyyymm ;
		sSQLInsert = "insert into "
				+ tblCDRlog
				+ "(user_id,service_id,mobile_operator,command_code,info" +
				",submit_date,done_date, CPId,REQUEST_ID,MESSAGE_TYPE," +
				"PROCESS_RESULT,CONTENT_TYPE)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, mt);
			statement.setTimestamp(6, msgObject.getTTimes());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setString(8, "26");
			statement.setBigDecimal(9, msgObject.getRequestid());
			statement.setInt(10, 1);
			statement.setInt(11, 1);
			statement.setInt(12, msgObject.getContenttype());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add ems_send_log"+yyyymm+":" + msgObject.getUserid() + ":"
						+ msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add ems_send_log"+yyyymm+":" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext() + ":Error add row from cdr log:"
					+ e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext() + ":Error add row from cdr log:"
					+ e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}
	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	public static final String YYYYMM_FORMAT_NOW = "yyyyMM";

	public static String yyyymm() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(YYYYMM_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

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

	/* tim list bai hat HOT */

	public String ValidISDN(String sISDN) {

		Util.logger.info(this.getClass().getName() + "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}

		String tempisdn = "-";

		if (sISDN.trim().length() < 8) {
			return "-";
		}
		try {
			long itemp = Long.parseLong(sISDN.trim());
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
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

	/* tim ma so cac bai hat */
	private static String[] findMusicID(String find, String infoid) {

		String[] result = new String[2];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		for (int i = 0; i < 2; i++) {
			result[i] = "";
		}

		try {
			connection = dbpool.getConnection("store");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			String ope = "";
			if (infoid.equalsIgnoreCase("vinaphone"))
				ope = "VNPCode";
			else if (infoid.equalsIgnoreCase("mobifone"))
				ope = "VMSCode";
			else
				ope = "VTCode";

			String sqlSelect = "SELECT top 1 RingbackName_E,"
					+ ope
					+ " FROM [IComStore].[dbo].Ringback WHERE REPLACE(RingbackName_E,' ','') = '"
					+ find + "' and " + ope + " is not null and " + ope
					+ " <>''";

			Util.logger.info("SEARCH MA SO  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SEARCH MA SO");
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	private static String[] findMusicIDlike(String find, String infoid) {

		String[] result = new String[2];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		for (int i = 0; i < 2; i++) {
			result[i] = "";
		}

		try {
			connection = dbpool.getConnection("store");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			String ope = "";
			if (infoid.equalsIgnoreCase("vinaphone"))
				ope = "VNPCode";
			else if (infoid.equalsIgnoreCase("mobifone"))
				ope = "VMSCode";
			else
				ope = "VTCode";

			String sqlSelect = "SELECT top 1 RingbackName_E,"
					+ ope
					+ " FROM [IComStore].[dbo].Ringback WHERE REPLACE(RingbackName_E,' ','') LIKE '"
					+ find + "%' and " + ope + " is not null and " + ope
					+ " <>''";

			Util.logger.info("SEARCH MA SO  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SEARCH MA SO");
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	/* tim list bai hat HOT */
	private String getMobileOperatorNew(String mobileNumber) {
		if (mobileNumber.startsWith("8491") || mobileNumber.startsWith("+8491")
				|| mobileNumber.startsWith("091")
				|| mobileNumber.startsWith("91")
				|| mobileNumber.startsWith("8494")
				|| mobileNumber.startsWith("+8494")
				|| mobileNumber.startsWith("094")
				|| mobileNumber.startsWith("94")
				|| mobileNumber.startsWith("0123")
				|| mobileNumber.startsWith("84123")
				|| mobileNumber.startsWith("84125")
				|| mobileNumber.startsWith("0125")
				|| mobileNumber.startsWith("0127")
				|| mobileNumber.startsWith("0129")) {
			return "GPC";
		} else if (mobileNumber.startsWith("8490")
				|| mobileNumber.startsWith("+8490")
				|| mobileNumber.startsWith("090")
				|| mobileNumber.startsWith("90")
				|| mobileNumber.startsWith("8493")
				|| mobileNumber.startsWith("+8493")
				|| mobileNumber.startsWith("093")
				|| mobileNumber.startsWith("93")
				|| mobileNumber.startsWith("0122")
				|| mobileNumber.startsWith("84122")
				|| mobileNumber.startsWith("84126")
				|| mobileNumber.startsWith("0126")) {
			return "VMS";
		} else if (mobileNumber.startsWith("8498")
				|| mobileNumber.startsWith("+8498")
				|| mobileNumber.startsWith("098")
				|| mobileNumber.startsWith("98")
				|| mobileNumber.startsWith("8497")
				|| mobileNumber.startsWith("+8497")
				|| mobileNumber.startsWith("097")
				|| mobileNumber.startsWith("97")
				|| mobileNumber.startsWith("84168")
				|| mobileNumber.startsWith("0168")
				|| mobileNumber.startsWith("84168")
				|| mobileNumber.startsWith("0169")
				|| mobileNumber.startsWith("84169")
				|| mobileNumber.startsWith("8416")
				|| mobileNumber.startsWith("016")) {
			return "VIETTEL";
		} else if (mobileNumber.startsWith("8495")
				|| mobileNumber.startsWith("+8495")
				|| mobileNumber.startsWith("095")
				|| mobileNumber.startsWith("95")) {
			return "SFONE";
		} else if (mobileNumber.startsWith("8492")
				|| mobileNumber.startsWith("+8492")
				|| mobileNumber.startsWith("092")
				|| mobileNumber.startsWith("92")) {
			return "HTC";
		} else if (mobileNumber.startsWith("8496")
				|| mobileNumber.startsWith("+8496")
				|| mobileNumber.startsWith("096")
				|| mobileNumber.startsWith("96")) {
			return "EVN";
		} else {
			return "-";
		}

	}

		/* Tim list bai hat */

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
