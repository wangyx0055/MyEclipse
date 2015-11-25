package daugia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.DBUtil;
import icom.common.Util;

public class DGMOHandle {
	
	private MsgObject msgObj = null;
	private DaugiaCommon dgCommon = null;
	private double dgAmount = 0;
	private Keyword keyword;
	private String strAmount = "0";
	Boolean isSendMT = false;
	
	public DGMOHandle(MsgObject msgObject, double _dgAmount, String _strAmount){
		
		msgObj = new MsgObject(msgObject);
		dgAmount = _dgAmount;
		strAmount = _strAmount;
		String info = "DG";
		
		keyword = Sender.loadconfig.getKeyword(info.toUpperCase(), msgObj.getServiceid());
		msgObj.setKeyword("DAUGIA");
		msgObj.setAmount(keyword.getAmount());
		msgObj.setCommandCode("DAUGIA");
		msgObj.setServiceName("DAUGIA");
		msgObj.setChannelType(0);
		msgObj.setContentId(keyword.getService_type());
		
		
		dgCommon = new DaugiaCommon();
		
	}
	
	public void handleMO(){
		
		if(DGConstants.DG_MODE==0){
			DBSelect dbSelect = new DBSelect();
			Boolean check = dbSelect.isUserTest(msgObj.getUserid());
			if(!check){
				msgObj.setUsertext("Dich vu chua chay. xin vui long thu lai sau!");
				dgCommon.sendMT(msgObj);
				
				return;
			}
			
		}
		
		strAmount = formatStrAmount(strAmount);
		if(strAmount.equals("0")){
			msgObj.setUsertext(DGConstants.MTINFO_WRONG_AMOUNT);
			dgCommon.sendMT(msgObj);
			return;
		}
		
		if(dgAmount > 0){
			
//			Boolean isExist = DBUtil.isexist_in_mlist(msgObj.getUserid(),
//					DGConstants.TABLE_MLIST_DG,"DAUGIA",0);
			
			// send charge
			sendToDGCharge();
			// registry
			registryDG();
			
//			Thread thSendCharge = new Thread(new Runnable() {
//				public void run() {
//					
//					sendToDGCharge();
//					
//					registryDG();
//				}
//			});
//			
//			thSendCharge.start();
			
//			if (isExist) {
//				
//			}else{
//				//isSendMT = true;
//				registryDG();
//			}
									
		}
		
		if(dgAmount <=0 || isSendMT){
			sendMT();			
		}
						
		//thRegistry.start();
				
	}
	
	protected void registryDG(){
				
		try {
			
			Boolean isExist = DBUtil.isexist_in_mlist(msgObj.getUserid(),
					DGConstants.TABLE_MLIST_DG,"DAUGIA",0,"");
			if (!isExist) {
				dgCommon.insertMlistService(msgObj, keyword,
						DGConstants.TABLE_MLIST_DG);				
				dgCommon.deleteTableByUserID(msgObj.getUserid(),
						DGConstants.TABLE_MLIST_DG+"_cancel");
				
				DBUtil.InsertSubcriber(DGConstants.TABLE_MLIST_DG, msgObj, "0",0);
				
				String sqlDelete = "DELETE FROM mlist_subcriber_cancel WHERE "
							+ " USER_ID = '" + msgObj.getUserid() 
							+ "' AND COMMAND_CODE = 'DAUGIA'";				
				DBUtil.executeSQL("gateway", sqlDelete);
												
			}else{
				
				String strUpdate = "UPDATE mlist_daugia_icom SET REG_COUNT = REG_COUNT + 1 " +
						"WHERE user_id = '" + msgObj.getUserid() + "'"
						+ " AND COMMAND_CODE = 'DAUGIA'";
				
				Util.logger.info("Update RegCount, sql = " + strUpdate);
				
				DBUtil.Update(strUpdate);
				
			}
			
		} catch (Exception e) {
			Util.logger.info("DGMOHandle @@ RegisterServices Error:" + e.getMessage());
		}
		
	}
	
		
	private void sendToDGCharge(){
		
		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		DaugiaCommon objComm = new DaugiaCommon();
		
		// Check Black List
		if(!objComm.isValidMOBlackList(msgObj.getUserid())){
			
			msgObj.setUsertext("He thong khong tien hanh tru tien" +
					" duoc so dien thoai cua ban. Xin vui long thu lai sau 5 phut!");
			objComm.sendMT(msgObj);
			return;
			
		}
		
		if(spMng.checkTimeDG(msgObj.getTimeSendMO())){
			if(objComm.isFreeList(msgObj.getUserid())){
				dgCommon.InsertDGCharge(msgObj, DGConstants.TABLE_DG_CHARGE_RESULT,strAmount);
				return;
			}
			dgCommon.InsertDGCharge(msgObj, DGConstants.TABLE_DG_CHARGE,strAmount);
			
		}else{
			// insert to MT
			String info = DGConstants.MT_END_DG;
			SanPhamDG spExpired = this.getSPExpiredDateLastest();
			String endHour = "";
			String endDate = "";
			
			String nextHour = "";
			String nextStartDate = "";
			String nextSp = "";
			
			if(spExpired != null){
				String startDate = dgCommon.formatDate(spExpired.getStartDate());											
				endDate = dgCommon.formatDate(spExpired.getEndDate());
				info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_START_DATE, startDate);
				endHour = objComm.getHour(spExpired.getEndDate());
				//msgObj.setUsertext(info);

				SanPhamDG spNext = dgCommon.getNextSP();
				
				if(spNext != null){
					info = DGConstants.MT_END_DG_HAVE_NEXT_SP;
					nextHour = dgCommon.getHour(spNext.getStartDate());
					nextStartDate = dgCommon.formatDate(spNext.getStartDate());
					nextSp = spNext.getTenSP();
				}
				
				
			}else{
				
				endDate = getBeforeDate();
				endHour = "14h";
				
				SanPhamDG spNext = dgCommon.getNextSP();
				
				if(spNext != null){
					info = DGConstants.MT_FIRST_DAUGIA;
					nextHour = dgCommon.getHour(spNext.getStartDate());
					nextStartDate = dgCommon.formatDate(spNext.getStartDate());
					nextSp = spNext.getTenSP();
				}
				
			}
									
			info = info.replace(DGConstants.STRING_REGEX_REPLACE_END_DATE,endDate);
			info = info.replaceAll(DGConstants.STRING_REGEX_END_HOUR, endHour);
			
			info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_HOUR, nextHour);
			info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_START_DATE, nextStartDate);
			info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_TEN_SP, nextSp);
			
			msgObj.setUsertext(info);
			
			dgCommon.sendMT(msgObj);
			
		}
	}
	
	private void sendMT(){
		
		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		SanPhamDG spDG = spMng.getSanPhamDG();
						
		String info = DGConstants.MTINFO_WRONG_KEYWORD;
		if(isVeryPoor(msgObj.getUsertext())){
			info = DGConstants.MTINFO_WRONG_AMOUNT;
		}
		
//		if(isSendMT){
//			info = DGConstants.MT_NOT_REGISTRY;
//		}
		
		isSendMT = false;

		String strStartDate ="";
		String strEndDate = "";
		String giaSp = "";
		String tenSp = "";
		
		String nextHour = "";
		String nextStartDate = "";
		String nextSp = "";
		
		boolean noError = true;
		
		if(spDG != null){			
			strStartDate = dgCommon.formatDate(spDG.getStartDate());					
			strEndDate = dgCommon.formatDate(spDG.getEndDate());
			giaSp = spDG.getGiaSP();
			tenSp = spDG.getTenSP();
		}else{
			
			
			SanPhamDG spExpired = this.getSPExpiredDateLastest();
					
			info = DGConstants.MT_END_DG;
			
			SanPhamDG spNext = dgCommon.getNextSP();
			
			if(spNext != null){
				info = DGConstants.MT_END_DG_HAVE_NEXT_SP;
				nextHour = dgCommon.getHour(spNext.getStartDate());
				nextStartDate = dgCommon.formatDate(spNext.getStartDate());
				nextSp = spNext.getTenSP();
				
				info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_HOUR, nextHour);
				info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_START_DATE, nextStartDate);
				info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_TEN_SP, nextSp);
			}
			
			if(spExpired != null){
				strStartDate = dgCommon.formatDate(spExpired.getStartDate());					
				strEndDate = dgCommon.formatDate(spExpired.getEndDate());
				giaSp = spExpired.getGiaSP();
				tenSp = spExpired.getTenSP();
			}else{
				strEndDate = getBeforeDate();
				noError = true;
			}
		}
		
		DaugiaCommon commObj = new DaugiaCommon();
		
		String endHour = "";
		if(!noError){
			endHour = commObj.getHour(strEndDate);
		}else{
			endHour = "14h";
		}
		
		
		
		info = info.replaceAll(DGConstants.STRING_REGEX_END_HOUR, endHour);
		info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_START_DATE,strStartDate);
		info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_END_DATE,strEndDate);
		info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_GIA_SP, giaSp);
		info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_TEN_SP, tenSp);
		
		info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_HOUR, nextHour);
		info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_START_DATE, nextStartDate);
		info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_TEN_SP, nextSp);
		
					
		msgObj.setUsertext(info);
		dgCommon.sendMT(msgObj);
				
	}
	
	/***
	 * Lay thong tin san pham dau gia Het han gan nhat
	 * @return
	 */

	private SanPhamDG getSPExpiredDateLastest() {

		SanPhamDG spObj = null;

		String currTime = Util.getCurrentDate();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, MA_SP, TEN_SP, START_DATE, END_DATE, IMAGE_LINK, MOTA_SP, IS_SENT_MT_WEEKLY"
				+ " FROM daugia_sanpham WHERE END_DATE < '"
				+ currTime
				+ "' ORDER BY END_DATE DESC limit 1";
		
		Util.logger.info("DAUGIA getSPExpiredDateLastest sql = " + sqlQuery);
//			System.out.println("getSPExpiredDateLastest sqlQuerry = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					spObj = new SanPhamDG();
					spObj.setId(rs.getInt("ID"));
					spObj.setMaSP(rs.getString("MA_SP"));
					spObj.setTenSP(rs.getString("TEN_SP"));
					spObj.setStartDate(rs.getString("START_DATE"));
					spObj.setEndDate(rs.getString("END_DATE"));
					spObj.setImageLink(rs.getString("IMAGE_LINK"));
					spObj.setMotaSP(rs.getString("MOTA_SP"));
					spObj.setIsSendMTWeekly(rs.getInt("IS_SENT_MT_WEEKLY"));
					break;
				}
			} else {
				Util.logger
						.error("DAUGIA - getSPExpiredDateLastest : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - getSPExpiredDateLastest. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - getSPExpiredDateLastest. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return spObj;
	}

	private Boolean isVeryPoor(String info){
		Pattern icomPattern = Pattern.compile("[.,]");
		Matcher match = icomPattern.matcher(info);
		return match.find();
	}
		
	
	private String getBeforeDate() {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) -1);
		return new java.text.SimpleDateFormat("dd/MM/yyyy").format(now
				.getTime());
	}
	
	private String formatStrAmount(String strAmount){
		
		String testAmount = strAmount;
		String amountResult = "";
		
		int indexStop = 0;
		int amountLength = testAmount.length(); 
		for(int i=0;i<amountLength;i++){
			char chTest = testAmount.charAt(i);
			if(chTest == '0'){ 
				if(i== amountLength-1){
					indexStop = amountLength-1;
				}
				continue;
			}
			
			indexStop = i;
			break;			
		}
		
		if(indexStop == (amountLength-1)){
			amountResult = "0";
		}else{
			amountResult = testAmount.substring(indexStop);
		}
		
		return amountResult;
	}
				
}
