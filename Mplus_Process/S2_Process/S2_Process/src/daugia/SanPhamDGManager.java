package daugia;

import icom.Keyword;
import icom.Sender;
import icom.common.Util;

import java.util.ArrayList;
import java.util.Calendar;

import DAO.MTDAO;

public class SanPhamDGManager {
	
	private static ArrayList<SanPhamDG> arrSanPham = new ArrayList<SanPhamDG>();
	
	private static SanPhamDGManager spDauGia = null;
	
	private SanPhamDGManager(){
		
	}
	
	public static SanPhamDGManager getInstance(){
		if(spDauGia == null){
			spDauGia = new SanPhamDGManager();
		}
		return spDauGia;
	}
	
	public SanPhamDG getSanPhamDG(){
		synchronized (arrSanPham) {
			if(arrSanPham.size() == 0){
				DaugiaCommon objCommon = new DaugiaCommon();
				SanPhamDG objSp = objCommon.getSPDauGia();
				if(objSp == null){
					
					Calendar gc = Calendar.getInstance();
					int currHour = gc.get(Calendar.HOUR_OF_DAY);					
					if(currHour == 6 || currHour == 14 || currHour == 22){
						Util.logger.info("@@ DAU GIA: Hien tai chua co san pham DAU GIA!!!");
					}
					
					return null;
				}
				arrSanPham.add(objSp);
			}
			return arrSanPham.get(0);	
		}			
	}
	
	public void getLastestSPDauGia(){
		DaugiaCommon objCommon = new DaugiaCommon();
		SanPhamDG objSp = objCommon.getSPDauGia();
		
		synchronized(arrSanPham){
			if(arrSanPham.size()>0){
				arrSanPham.remove(0);
			}
		}
								
		if(objSp == null){
			
			Calendar gc = Calendar.getInstance();
			int currHour = gc.get(Calendar.HOUR_OF_DAY);
			
			if(currHour == 6 || currHour == 14 || currHour == 22){
				Util.logger.info("@@ DAU GIA: Hien tai chua co san pham DAU GIA!!!");
				
				try {
					Thread.sleep(5*60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}else{
			
			String existMsg = DGConstants.MT_EXIST_MSG;
			try {
				existMsg = MTDAO.getRandomMessage(
					DGProcess.hMessageReminder.get("22"),
					DGProcess.hMessageReminder.get("22").split(
							";").length);
			} catch (Exception e) {
				Util.logger.printStackTrace(e);
			}

			existMsg = existMsg.replaceAll(
					DGConstants.STRING_REGEX_REPLACE_TEN_SP,
					objSp.getTenSP());
			existMsg = existMsg.replaceAll(
					DGConstants.STRING_REGEX_REPLACE_GIA_SP,
					objSp.getGiaSP());
			existMsg = existMsg.replaceAll(
					DGConstants.STRING_REGEX_REPLACE_END_DATE,
					objCommon.formatDate(objSp.getEndDate()));
			String strTemp = existMsg;
			
			existMsg = existMsg.replaceAll(
					DGConstants.COMMAND_CODE,
					"DG");
			// reset keyword
			Keyword keywordOne = Sender.loadconfig.getKeyword("DK DAUGIA","9209");
			keywordOne.setExistMsg(existMsg);
			
			Keyword keywordTWO = Sender.loadconfig.getKeyword("DK DG","9209");
			keywordTWO.setExistMsg(existMsg);
			
			Keyword keywordThree = Sender.loadconfig.getKeyword("DAUGIA","9209");
			keywordThree.setExistMsg(existMsg);
			
			Keyword keyword4 = Sender.loadconfig.getKeyword("DG","9209");
			keyword4.setExistMsg(existMsg);
			
			Keyword keyword5 = Sender.loadconfig.getKeyword("DK DG","9209");
			keyword5.setExistMsg(existMsg);
			
			
			strTemp = strTemp.replaceAll(
					DGConstants.COMMAND_CODE,
					"DA");
			Keyword keyword6 = Sender.loadconfig.getKeyword("DK DA","9209");
			keyword6.setExistMsg(strTemp);
			
			Keyword keyword7 = Sender.loadconfig.getKeyword("DA","9209");
			keyword7.setExistMsg(strTemp);
			
			//Sender.loadconfig.updateKeyword(keyword.getKeyword(),"9209", keyword);
		
			synchronized(arrSanPham){
				arrSanPham.add(objSp);
			}
		}
				
	}
	
	/****
	 * 
	 * @return - true if timeSendMO nam trong thoi gian DAU GIA <br/>
	 * 		   - false het thoi gian dau gia!
	 */
	Boolean checkTimeDG(String timeSendMO){
		
		SanPhamDGManager sanPhamMng = SanPhamDGManager.getInstance();
		SanPhamDG spObj = sanPhamMng.getSanPhamDG();		
		
		if(spObj == null) return false;
		
		if(spObj.getStartDate().compareTo(timeSendMO)>0 || 
				spObj.getEndDate().compareTo(timeSendMO)<0){
			return false;
		}
		
		return true;
	}

}
