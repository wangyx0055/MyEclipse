package phase4;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;
import icom.common.ResultCode;

public class BLEventHotMT extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		Ph4Common cmmObj = new Ph4Common();
		DBInsert dbInsert = new DBInsert();
		
		int resultCharge = msgObject.getChargeResult();
		
		if(resultCharge == ResultCode.NOK_NOT_ENOUGH_CREDIT || 
				resultCharge == ResultCode.NOK_NO_MORE_AVAILABLE_CREDIT){
			
			msgObject.setUsertext("Hien tai khoan cua ban khong du de su dung," +
					" cuoc phi se duoc tinh sau khi ban nap tien vao tai khoan." +
					" Soan DK gui 9209 de duoc ho tro ve cu phap cac goi dich" +
					" vu khac cua mPlus Chi tiet tai  http://m.mplus.vn." +
					" Dien thoai ho tro 9244");
			
		}else if(resultCharge == ResultCode.OK ||
				resultCharge == ResultCode.NOK_ACCOUNT_NOT_FOUND){
			
			String[][] blHot = cmmObj.getBinhLuanIdHot();
			String[] arrBlIdHot = new String[blHot.length];
			for(int j =0;j<arrBlIdHot.length;j++){
				arrBlIdHot[j] = blHot[j][0];
			}
			
			String[] arrNumberComment = new String[blHot.length];
			for(int j = 0;j<arrNumberComment.length;j++){
				arrNumberComment[j] =  blHot[j][1];
			}
			
			String[] arrContent = cmmObj.getBLContentHot(arrBlIdHot);
			String info = "";
			if(arrContent[2] == null){
				info = "TOP BL Hay nhat co luong KH comment dong nhat: ";
			}else{
				info = "TOP 3 BL Hay nhat co luong KH comment dong nhat: ";
			}
			
			for(int j = 0;j<arrContent.length;j++){
				if(arrContent[j] == null) break;
				int length = arrContent[j].length();
				if(length > 100) length = 100;
				info = info + arrContent[j].substring(0, length -1) + "..."
						+ "(" + arrNumberComment[j] + "BL) \n"; 
				
			}
			msgObject.setUsertext(info);
			
		}else{
			msgObject.setUsertext("He thong khong tru tien cua ban, xin vui long thu lai!");
		}
		
		msgObject.setCommandCode("BL");
		msgObject.setKeyword("BL");
		msgObject.setServiceName("BL");
		dbInsert.sendMT(msgObject);
							
		return null;

	}
	
	

}
