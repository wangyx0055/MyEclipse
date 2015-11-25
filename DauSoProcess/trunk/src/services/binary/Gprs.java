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

public class Gprs extends ContentAbstract {

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

		if ("GPC".equalsIgnoreCase(operator)) {
			mtReturn[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Vinaphone hay soan tin GPRS ON gui 333";

			mtReturn[1] = "01062F1F2DB69181923137354131393539423745314330343234313434334642383537373639393832344245453232354400030B6A00C54601C6560187070603385838355F56494E41000101C6000155018736000006037732000187070603694E4554000187220603694E455400018700013900000603765F500001C600015901873A00000603687474703A";
			mtReturn[2] = "2F2F7761702E766E0001871C010101C6510187150603565F50000187070603385838355F56494E410001C653018723060338303830000101C65201872F0603694E455400018720060331302E312E31302E343600018721068503000187220603694E455400010101C6550187110603694E4554000187070603385838355F56494E410001871006AB03000187";
			mtReturn[3] = "0806036D332D776F726C64000187090689030001C65A01870C069A030001870D06036D6D730001870E06036D6D730001010101";
			mtReturn[4] = giff_vinaphone;

		} else if ("VMS".equalsIgnoreCase(operator)) {
			mtReturn[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Mobifone hay soan tin  GPRS gui 994";
			mtReturn[1] = "01062c1f2a6170706c69636174696f6e2f782d7761702d70726f762e62726f777365722d73657474696e67730081ea01016a0045c6060187124901871311033230332e3136322e32312e3130370001871c11036d2d776170000187227001872311036d6d730001872311036d6d7300010186071103687474703a2f2f7761702e766e0001c60801871511034d";
			mtReturn[2] = "6f62692d694e4554000101c67f01871511034d6f62692d694e4554000187171103687474703a2f2f7761702e766e00010101";
			mtReturn[3] = giff_mobifone;

		} else if (("VIETTEL".equalsIgnoreCase(operator))
				|| ("VIETEL".equalsIgnoreCase(operator))) {
			String[] mtReturn1 = new String[5];
			mtReturn1[0] = "Hay nhap ma PIN 1111 trong ban tin sau neu duoc yeu cau sau do khoi dong lai may.Neu ban chua dang ky su dung GPRS voi Viettel hay soan tin DK gui 191";
			mtReturn1[1] = "01062F1F2DB69181924234434339384644313538383336463144323544364635373842313543364145323543324239413900030B6A00C54601C6560187070603385838355F56494554454C000101C6000155018736000006037732000187070603694E4554000187220603694E455400018700013900000603565F540001C600015901873A00000603687474";
			mtReturn1[2] = "703A2F2F7761702E766E0001871C010101C6510187150603565F54000187070603385838355F56494554454C0001C653018723060338303830000101C65201872F0603694E45540001872006033139322E3136382E3233332E313000018721068503000187220603694E455400010101C6550187110603694E4554000187070603385838355F56494554454C";
			mtReturn1[3] = "0001871006AB03000187080603762D776170000187090689030001C65A01870C069A030001870D0601870E0601010101";
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
