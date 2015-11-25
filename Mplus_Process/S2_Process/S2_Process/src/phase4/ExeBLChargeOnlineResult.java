package phase4;

import java.math.BigDecimal;
import java.util.ArrayList;

import SyncSky.SyncAPI;

import icom.MsgObject;
import icom.Sender;
import icom.common.DBDelete;
import icom.common.DBInsert;
import icom.common.ResultCode;
import icom.common.Util;

public class ExeBLChargeOnlineResult extends Thread {

	
	public void run(){
	
		while(Sender.processData){
			
			Ph4Common cmmObj = new Ph4Common();
			
			ArrayList<ChargeOnlineObject> arrResult = cmmObj.getBLChargeOnlineResult(); 
			for(int i = 0;i<arrResult.size();i++){
				
				if(!Sender.processData) break;
								
				ChargeOnlineObject resultObj = arrResult.get(i);
				
				DBDelete dbDelete = new DBDelete();
				dbDelete.deleteTableByID(resultObj.getId(), Ph4Common.TBL_CHARGE_ONLINE_RESULT);
				
				String commandCode = resultObj.getCommandCode();
				int resultCharge = resultObj.getResultCharge();
				DBInsert dbInsert = new DBInsert();
				
				MsgObject msgObject = getMsgObj(resultObj);
				
				if(resultObj.getInfo().toUpperCase().matches("(.*)SKY(.*)")){
					String response = SyncAPI.SyncMR(resultObj.getUserId(), resultObj.getAmount()+"", resultObj.getRequestId());
					Util.logger.info("ExeBLChargeOnlineResult @synchronization MR to sky response:"+response);
				}
				
				if( resultObj.getInfo().toUpperCase().equals("BL HOT")){
					
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
										
				}else if(commandCode.equals("BL")){
					
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
						
						msgObject.setUsertext("Tin nhan cua ban da duoc gui den he thong." +
								" Ban co the truy cap vao trang web http://m.mplus.vn" +
								" de biet neu thong tin duoc hien thi." +
								" Cam on ban da su dung dich vu cua MobiFone." +
								" Dien thoai ho tro 9244");
						
						String[] arrTmp = resultObj.getInfo().split(" ");
						String blId = arrTmp[1];
						resultObj.setBlid(blId);
						cmmObj.insertBLContent(resultObj);
												
					}else{
						msgObject.setUsertext("He thong khong tru tien cua ban, xin vui long thu lai!");
					}
										
					
				}else if(commandCode.equals("BLE")){
					
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
						
						msgObject.setUsertext("Tin nhan cua ban da duoc gui den he thong." +
								" Ban co the truy cap vao trang web http://m.mplus.vn" +
								" de biet neu thong tin duoc hien thi." +
								" Cam on ban da su dung dich vu cua MobiFone." +
								" Dien thoai ho tro 9244");
						
						String[] arrTmp = resultObj.getInfo().split(" ");
						String blId = arrTmp[1];
						resultObj.setBlid(blId);
						cmmObj.insertBLContent(resultObj);
												
					}else{
						msgObject.setUsertext("He thong khong tru tien cua ban, xin vui long thu lai!");
					}
										
					
				}
				
				msgObject.setCommandCode("BL");
				msgObject.setKeyword("BL");
				msgObject.setServiceName("BL");
				dbInsert.sendMT(msgObject);
				
			}
			
		}// end While
		
		
		
	}// End Run
	
	
	private MsgObject getMsgObj(ChargeOnlineObject rslInfo){
		
		MsgObject msgObj = new MsgObject();

//		Keyword keyword = Sender.loadconfig.getKeyword(info.toUpperCase(), rslInfo.getServiceId());
		
		msgObj.setUserid(rslInfo.getUserId());
		msgObj.setMobileoperator(rslInfo.getMobiOperator());
		msgObj.setChannelType(rslInfo.getChanelType());
		msgObj.setServiceid(rslInfo.getServiceId());
		msgObj.setRequestid(
				BigDecimal.valueOf(Double.parseDouble(rslInfo.getRequestId())) );
		msgObj.setCommandCode(rslInfo.getCommandCode());
		msgObj.setContenttype(rslInfo.getContentType());
		msgObj.setMsgtype(rslInfo.getMsgType());
//		msgObj.setKeyword("DAUGIA");
		
		return msgObj;
	}
	
}
