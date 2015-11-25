package daugiaplus;




import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.vmg.sms.common.DBUtils;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class NT_DaugiaPlus extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		String password = Constants._prop.getProperty("password_charge");
		String partner_UserName = Constants._prop.getProperty("username_charge");
		String url = Constants._prop.getProperty("url_charge");
		Collection messages = new ArrayList();
		String sUserid = msgObject.getUserid();
		String sKeyword = msgObject.getKeyword();
		String sServiceid = msgObject.getServiceid(); //lay dau so de tính tiền
		String sUsertext = msgObject.getUsertext();
		String sMobileOperator = msgObject.getMobileoperator();
		
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String user_pass = partner_UserName.concat(password);
		String validString = codeUserPass(user_pass); //341ae94bc2a06d3fe68304893c1a8af8
		String charge = "";
		
		String info = msgObject.getUsertext().toUpperCase().trim();
		info = replaceAllWhiteWithOne(info.trim());

		String[] arrInfo = info.split(" ");
		if (arrInfo.length == 1) {
				msgObject
				.setUsertext("Tin nhan sai cu phap. Soan tin "
						+ " DG HD gui "
						+ keyword.getServiceid()
						+ " de duoc huong dan chi tiet. Vui long truy cap vao http://daugiaplus.vn de biet them chi tiet.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				Thread.sleep(1000);
				messages.add(new MsgObject(msgObject));
				return messages;
		}
		charge = MoSendCharge.ReCharge(url, partner_UserName, validString, sUserid, arrInfo[1], "15000", "0", timeStamp);
		int result =0;
		String result1 = "";
		if(charge != "") {
			result = charge.indexOf("|");
			result1 = charge.substring(0, result);
			//result = charge.indexOf("|");
			if("0".equalsIgnoreCase(result1)) {
				msgObject.setUsertext("Ban da nap thanh cong 150 Xeng vao tai khoan "+arrInfo[1]+". Cam on ban da den voi http://daugiaplus.vn. Vui long truy cap http://daugiaplus.vn de biet them chi tiet.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(21);
				DBUtils.sendMT(msgObject);
				Thread.sleep(100);
			}else if("11".equalsIgnoreCase(result1)) {
				msgObject.setUsertext("Tai khoan cua ban khong du tien de nap cho tai khoan "+arrInfo[1]+". Vui long truy cap http://daugiaplus.vn de biet them chi tiet.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(21);
				DBUtils.sendMT(msgObject);
				Thread.sleep(100);
			} else if("30".equalsIgnoreCase(result1)) {
				msgObject.setUsertext("Tai khoan ban vua nap tien khong ton tai. Vui long kiem tra lai thong tin.Truy cap http://daugiaplus.vn de biet them chi tiet.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(21);
				DBUtils.sendMT(msgObject);
				Thread.sleep(100);
			} else {
				msgObject.setUsertext("Nap tien khong thanh cong. Vui long kiem tra lai thong tin.Truy cap http://daugiaplus.vn de biet them chi tiet.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(21);
				DBUtils.sendMT(msgObject);
				Thread.sleep(100);
			}
		}
		
		return null;
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
	
	public String codeUserPass(String code) {
		 MessageDigest md5 = null;
	        try {
	            md5 = MessageDigest.getInstance("MD5"); // Ma hoa MD5
	        }
	        catch (NoSuchAlgorithmException ex) {
	            return null;
	        }
	        md5.update(code.getBytes());
	        BigInteger bg = new BigInteger(1, md5.digest());
	        return bg.toString(16);
	}

}
