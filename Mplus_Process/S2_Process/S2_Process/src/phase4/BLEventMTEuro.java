package phase4;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;
import icom.common.ResultCode;

public class BLEventMTEuro extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		Ph4Common cmmObj = new Ph4Common();
		DBInsert dbInsert = new DBInsert();
		
		int resultCharge = msgObject.getChargeResult();
		// if not enough money
		if(resultCharge == ResultCode.NOK_NOT_ENOUGH_CREDIT || 
				resultCharge == ResultCode.NOK_NO_MORE_AVAILABLE_CREDIT){
			
			msgObject.setUsertext("Hien tai khoan cua ban khong du de su dung," +
					" cuoc phi se duoc tinh sau khi ban nap tien vao tai khoan." +
					" Soan DK gui 9209 de duoc ho tro ve cu phap cac goi dich" +
					" vu khac cua mPlus Chi tiet tai  http://m.mplus.vn." +
					" Dien thoai ho tro 9244");
			
		}else if(resultCharge == ResultCode.OK ||
				resultCharge == ResultCode.NOK_ACCOUNT_NOT_FOUND){
			
			String[] arrTmp = msgObject.getUsertext().split(" ");
			String blId = arrTmp[1];
			cmmObj.insertBLEventContent(msgObject.getUserid(), msgObject.getUsertext(), blId);

				msgObject.setUsertext("Tin nhan cua ban da duoc gui den he thong." +
					" Ban co the truy cap vao trang web http://m.mplus.vn" +
					" de biet neu thong tin duoc hien thi." +
					" Cam on ban da su dung dich vu cua MobiFone." +
					" Dien thoai ho tro 9244");
																		
		}else{
			msgObject.setUsertext("He thong khong tru tien cua ban, xin vui long thu lai!");
		}
		
		dbInsert.sendMT(msgObject);
							
		return null;
	}

}
