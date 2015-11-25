package services.binary;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.sendXMLRING;

import common.*;
import cs.ExecuteADVCR;

public class AVGGprsSetting extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		

		Collection messages = new ArrayList();

		String sOperator = msgObject.getMobileoperator();
		String info = msgObject.getUsertext();
		String urlgiff = keyword.getOptions();

		boolean giff = false;

		String giff_userid = "";
		String giff_telco = "";
		String[] stokens = info.toUpperCase().split(" ");

		if (stokens.length > 1) {
			giff_userid = ValidISDNNew(stokens[1]);
			if (!giff_userid.equalsIgnoreCase("-")) {

				giff_telco = Utils.getMobileOperator(giff_userid, 1);

				if ("-".equals(giff_telco)
						|| "EVN".equalsIgnoreCase(giff_telco)
						|| ("SFONE".equalsIgnoreCase(giff_telco))) {
					giff = false;
				} else {
					giff = true;
				}
			}
			else
			{
				msgObject
				.setUsertext("Ban da nhan tin sai cu phap.De cai dat GPRS tu dong.Soan:GPC gui 6787.DTHT 1900571566");
		msgObject.setMsgtype(1);
		msgObject.setContenttype(0);
		messages.add(new MsgObject(msgObject));
		return messages;
			}
		}
		String[] sMTReturn = mtReturn((giff == true) ? giff_telco : sOperator,urlgiff);

		if (giff == false) {
			if ("EVN".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "SFONE".equalsIgnoreCase(msgObject.getMobileoperator())) {

				msgObject
						.setUsertext("Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau!De cai dat cho thue bao khac, soan GPRS <sodienthoai> gui 6754.DTHT 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;

			}
			if (sMTReturn.length > 1) {
				for (int j = 0; j < sMTReturn.length; j++) {
					if (j == 0) {
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
					} else if (j == sMTReturn.length - 1) {
						msgObject.setMsgtype(0);
						msgObject.setContenttype(8);
					} else {
						msgObject.setMsgtype(0);
						msgObject.setContenttype(18);
					}
					msgObject.setUsertext(sMTReturn[j]);
					if (j < sMTReturn.length - 1) {
						/*String urlAVG = "http://118.70.205.134:880/avgmtws/services/Receiver";
						String usernameAVG = "icom";
						String passwordAVG = "R3o11v0rP1110cI";
						urlAVG = Constants._prop.getProperty("urlAVG" +"", urlAVG);
						usernameAVG = Constants._prop.getProperty("usernameAVG" +"", usernameAVG);
						passwordAVG = Constants._prop.getProperty("passwordAVG" +"", passwordAVG);
						
						String resultSendMT = sendXMLRING.sendMTXML(urlAVG, 
								msgObject.getUserid(), msgObject.getKeyword(), msgObject.getServiceid(), msgObject.getUsertext(), msgObject.getMsgtype(), 
								msgObject.getRequestid(), 1,
								0, 1, msgObject.getContenttype());
						 String result = sendXMLRING.parseResult(resultSendMT);
						 Util.logger.info( " result" + result);
						if(result.equalsIgnoreCase("1"))
							Util.logger.info( " Foward thanh cong !");
						else
							Util.logger.info( " Foward loi !");*/
						if(msgObject.getMsgtype()==1)
						addCDRlog(msgObject,msgObject.getUsertext());
						
						addEmslog(msgObject,msgObject.getUsertext());
						//DBUtil.sendMT(msgObject);
						
						Thread.sleep(4000);
					} else
						messages.add(new MsgObject(msgObject));
				}
			}
		} 

		return null;
	}
	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Long.parseLong(sISDN);

			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			Util.logger.info("Utils.ValidISDN" + "Exception?*" + e.toString()
					+ "*");
			return "-";
		}
		return tempisdn;
	}
	private String[] mtReturn(String operator,String mtgiff) throws Exception {
		String[] mtReturn = new String[6];

		// System.out.println("TELCO:" + operator);
		if ("VMS".equalsIgnoreCase(operator)) {
			mtReturn[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Mobifone hay soan tin GPRS gui 994";
			mtReturn[1] = "0B05040B8423F0000304040101062F1F2DB69181923441353942373833423645344146424530363132333538433531324142443037383139333342323100030B6A0045C65601870706035654565F4D4F42490001871506035654565F4D4F42495F50726F7879000101C65501870706035654565F4D4F42490001871106035654565F4D4F42495F4E41504944";
			mtReturn[2] = "0B05040B8423F000030404020001871006AB01870806036D2D77617000018709068901C65A01870C069A01870D06036D6D730001870E06036D6D7300010101C65101870706035654565F4D4F42490001871506035654565F4D4F42495F50726F78790001871C0603687474703A2F2F7761702E76696D6F62692E766E0001C659018719069C01871A06036D6D";
			mtReturn[3] = "0B05040B8423F00003040403730001871B06036D6D73000101C65201872F06035654565F4D4F42495F506850726F78790001872006033230332E3136322E3032312E31303700018721068501872206035654565F4D4F42495F4E415049440001C6530187230603393230310001872406CB01010101C60001550187360000060377320001870706035654565F";
			mtReturn[4] = "0B05040B8423F000030404044D4F4249000187000139000006035654565F4D4F42495F50726F78790001C6000159018707000006035654565F4D4F424900018700013A00000603687474703A2F2F7761702E76696D6F62692E766E0001871C01010101";
			mtReturn[5] = mtgiff;

		} else if ("GPC".equalsIgnoreCase(operator)) {
			mtReturn[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Vinaphone hay soan tin GPRS ON gui 333";
			mtReturn[1] = "0B05040B8423F0000304040101062F1F2DB69181923035433244334442443837364536323330423334353337353237343243443435334338373237313300030B6A0045C65601870706035654565F56494E410001871506035654565F56494E415F50726F7879000101C65501870706035654565F56494E410001871106035654565F56494E415F4E41504944";
			mtReturn[2] = "0B05040B8423F000030404020001871006AB01870806036D332D776F726C6400018709068901C65A01870C069A01870D06036D6D730001870E06036D6D7300010101C65101870706035654565F56494E410001871506035654565F56494E415F50726F78790001871C0603687474703A2F2F7761702E76696D6F62692E766E0001C659018719069C01871A06";
			mtReturn[3] = "0B05040B8423F00003040403036D6D730001871B06036D6D73000101C65201872F06035654565F56494E415F506850726F787900018720060331302E312E31302E343600018721068501872206035654565F56494E415F4E415049440001C6530187230603383030300001872406CB01010101C60001550187360000060377320001870706035654565F5649";
			mtReturn[4] = "0B05040B8423F000030404044E41000187000139000006035654565F56494E415F50726F78790001C6000159018707000006035654565F56494E4100018700013A00000603687474703A2F2F7761702E76696D6F62692E766E0001871C01010101";
			mtReturn[5] = mtgiff;

		} else if (("VIETTEL".equalsIgnoreCase(operator))
				|| ("VIETEL".equalsIgnoreCase(operator))) {
			String[] mtReturn1 = new String[5];
			mtReturn1[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Viettel hay soan tin GPRS1 gui 191";
			mtReturn1[1] = "0B05040B8423F000037B030128062F1F2DB69181923639443742424331394637303935323243343234394539353646443137314431383943354543304500020B6A00C54601C65601870706035649455454454C2047505253000101C65101871506035649455454454C5F50524F58590001870706035649455454454C20475052530001C65301872306033000";
			mtReturn1[2] = "0B05040B8423F000037B03020101C65201872F060370726F7879000187200603302E302E302E3000018721068501872206035649455454454C5F4750525300010101C65501871106035649455454454C5F475052530001871006AB018707060342726F7773696E672047505253000187080603762D696E7465726E657400018709068901C65A01870C069A01";
			mtReturn1[3] = "0B05040B8423F000037B0303870D06036D6D730001870E06036D6D7300010101C6000155018736060377320001873906035649455454454C5F50524F58590001C65901870706035649455454454C0001873A0603687474703A2F2F7761702E7669657474656C6D6F62696C652E636F6D2E766E0001871C01010101";
			mtReturn1[4] = mtgiff;
			return mtReturn1;
		} else {

		}
		return mtReturn;
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


}
