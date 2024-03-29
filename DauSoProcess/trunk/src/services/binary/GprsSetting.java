package services.binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import common.*;
import cs.ExecuteADVCR;

public class GprsSetting extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();

		String sOperator = msgObject.getMobileoperator();
		String info = msgObject.getUsertext();

		HashMap _option = new HashMap();
		String options = keyword.getOptions();

		String giff_mobifone = "Tang ban 3 game online HOT nhat hien nay. DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm";
		String giff_vinaphone = "Tang ban 3 game online HOT nhat hien nay. DTHT 1900571566.:http://s.mobinet.vn/d/list_gf.htm";
		String giff_viettel = "Tang ban 3 game online HOT nhat hien nay. DTHT 1900571566.:http://s.mobinet.vn/d/list_gf.htm";
		String inv_telco = "Dich vu cai dat GPRS khong ho tro mang CDMA.De cai dat cho thue bao khac, soan GPRS sodienthoai gui 8751.DTHT 19001745";

		try {
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);
			_option = getParametersAsString(options);
			giff_mobifone = getString(_option, "giff_mobifone", giff_mobifone);
			Util.logger.info("Giff Mobifonezz: " + giff_mobifone);
			giff_vinaphone = getString(_option, "giff_vinaphone",
					giff_vinaphone);
			Util.logger.info("Giff Vinaphone: " + giff_vinaphone);
			giff_viettel = getString(_option, "giff_viettel", giff_viettel);
			Util.logger.info("Giff Viettel: " + giff_viettel);
			inv_telco = getString(_option, "inv_telco", inv_telco);

		} catch (Exception e) {
			Util.logger.sysLog(2, this.getClass().getName(), "Error: "
					+ e.toString());
			throw new Exception("Wrong config in options");
		}

		boolean giff = false;

		String giff_userid = "";
		String giff_telco = "";
		String[] stokens = info.toUpperCase().split(" ");

		if (stokens.length > 1) {
			giff_userid = ValidISDN(stokens[1]);
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
		}

		String[] sMTReturn = mtReturn((giff == true) ? giff_telco : sOperator,
				giff_mobifone, giff_vinaphone, giff_viettel);

		if (giff == false) {
			if ("EVN".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "SFONE".equalsIgnoreCase(msgObject.getMobileoperator())) {

				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
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
						DBUtil.sendMT(msgObject);
						Thread.sleep(4000);
					} else
						DBUtil.sendMT(msgObject);

				}
			}
		} else {
			if (sMTReturn.length > 1) {
				MsgObject msgGiff = new MsgObject(msgObject);
				msgGiff.setUserid(giff_userid);
				msgGiff.setMobileoperator(giff_telco);

				for (int j = 0; j < sMTReturn.length; j++) {
					if (j == 0) {
						msgGiff.setContenttype(0);
						msgGiff.setMsgtype(0);

					} else if (j == sMTReturn.length - 1) {
						msgGiff.setMsgtype(0);
						msgGiff.setContenttype(8);
					} else {
						msgGiff.setMsgtype(0);
						msgGiff.setContenttype(18);
					}
					msgGiff.setUsertext(sMTReturn[j]);
					if (j < sMTReturn.length - 1) {
						DBUtil.sendMT(msgGiff);
						Thread.sleep(4000);
					} else
						DBUtil.sendMT(msgGiff);

				}

				msgObject
						.setUsertext("Ban da gui tang thanh cong cai dat GPRS toi thue bao "
								+ giff_userid);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);

			}

		}

		ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
				.getServiceid(), msgObject.getUserid(), msgObject.getKeyword(),
				msgObject.getRequestid(), msgObject.getTTimes(), msgObject
						.getMobileoperator());

		return null;
	}
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
			long itemp = Integer.parseInt(sISDN);
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}


	private String[] mtReturn(String operator, String giff_mobifone,
			String giff_vinaphone, String giff_viettel) throws Exception {
		String[] mtReturn = new String[6];

		if ("VMS".equalsIgnoreCase(operator)
				|| ("MOBIFONE".equalsIgnoreCase(operator))) {
			mtReturn[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Mobifone hay soan tin  GPRS gui 994";
			mtReturn[1] = "0B05040B8423F0000304040101062F1F2DB69181923441353942373833423645344146424530363132333538433531324142443037383139333342323100030B6A0045C65601870706035654565F4D4F42490001871506035654565F4D4F42495F50726F7879000101C65501870706035654565F4D4F42490001871106035654565F4D4F42495F4E41504944";
			mtReturn[2] = "0B05040B8423F000030404020001871006AB01870806036D2D77617000018709068901C65A01870C069A01870D06036D6D730001870E06036D6D7300010101C65101870706035654565F4D4F42490001871506035654565F4D4F42495F50726F78790001871C0603687474703A2F2F7761702E76696D6F62692E766E0001C659018719069C01871A06036D6D";
			mtReturn[3] = "0B05040B8423F00003040403730001871B06036D6D73000101C65201872F06035654565F4D4F42495F506850726F78790001872006033230332E3136322E3032312E31303700018721068501872206035654565F4D4F42495F4E415049440001C6530187230603393230310001872406CB01010101C60001550187360000060377320001870706035654565F";
			mtReturn[4] = "0B05040B8423F000030404044D4F4249000187000139000006035654565F4D4F42495F50726F78790001C6000159018707000006035654565F4D4F424900018700013A00000603687474703A2F2F7761702E76696D6F62692E766E0001871C01010101";
			mtReturn[5] = giff_mobifone;

		} else if ("GPC".equalsIgnoreCase(operator)
				|| ("VINAPHONE".equalsIgnoreCase(operator))) {
			mtReturn[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Vinaphone hay soan tin GPRS ON gui 333";
			mtReturn[1] = "0B05040B8423F0000304040101062F1F2DB69181923035433244334442443837364536323330423334353337353237343243443435334338373237313300030B6A0045C65601870706035654565F56494E410001871506035654565F56494E415F50726F7879000101C65501870706035654565F56494E410001871106035654565F56494E415F4E41504944";
			mtReturn[2] = "0B05040B8423F000030404020001871006AB01870806036D332D776F726C6400018709068901C65A01870C069A01870D06036D6D730001870E06036D6D7300010101C65101870706035654565F56494E410001871506035654565F56494E415F50726F78790001871C0603687474703A2F2F7761702E76696D6F62692E766E0001C659018719069C01871A06";
			mtReturn[3] = "0B05040B8423F00003040403036D6D730001871B06036D6D73000101C65201872F06035654565F56494E415F506850726F787900018720060331302E312E31302E343600018721068501872206035654565F56494E415F4E415049440001C6530187230603383030300001872406CB01010101C60001550187360000060377320001870706035654565F5649";
			mtReturn[4] = "0B05040B8423F000030404044E41000187000139000006035654565F56494E415F50726F78790001C6000159018707000006035654565F56494E4100018700013A00000603687474703A2F2F7761702E76696D6F62692E766E0001871C01010101";
			mtReturn[5] = giff_vinaphone;

		} else if (("VIETTEL".equalsIgnoreCase(operator))
				|| ("VIETEL".equalsIgnoreCase(operator))) {
			String[] mtReturn1 = new String[5];
			mtReturn1[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Viettel hay soan tin DK gui 191";
			mtReturn1[1] = "0B05040B8423F000037B030128062F1F2DB69181923639443742424331394637303935323243343234394539353646443137314431383943354543304500020B6A00C54601C65601870706035649455454454C2047505253000101C65101871506035649455454454C5F50524F58590001870706035649455454454C20475052530001C65301872306033000";
			mtReturn1[2] = "0B05040B8423F000037B03020101C65201872F060370726F7879000187200603302E302E302E3000018721068501872206035649455454454C5F4750525300010101C65501871106035649455454454C5F475052530001871006AB018707060342726F7773696E672047505253000187080603762D696E7465726E657400018709068901C65A01870C069A01";
			mtReturn1[3] = "0B05040B8423F000037B0303870D06036D6D730001870E06036D6D7300010101C6000155018736060377320001873906035649455454454C5F50524F58590001C65901870706035649455454454C0001873A0603687474703A2F2F7761702E7669657474656C6D6F62696C652E636F6D2E766E0001871C01010101";
			mtReturn1[4] = giff_viettel;
			return mtReturn1;
		} else {
			String[] mtReturn2 = new String[1];
			return mtReturn2;
		}
		return mtReturn;

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

}
