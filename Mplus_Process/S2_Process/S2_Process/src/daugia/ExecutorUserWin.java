package daugia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import DAO.MTDAO;

import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.Util;

public class ExecutorUserWin extends Thread {

	
	@Override
	public void run() {
		
		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		
		while (Sender.getData) {
						
			DGAmountManager amountMng = new DGAmountManager();		
			DaugiaCommon commonObj = new DaugiaCommon();

			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			String currTime = Util.getCurrentDate();
			//currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			SanPhamDG spDG = spMng.getSanPhamDG();

			if (spDG == null)
				continue;

			String endDay = getDayEnd(spDG.getEndDate());

			// Check ngay cuoi cua phien dau gia
			if (endDay.equals(getToday())) {

				// check gio tra tin trung thuong
//				System.out.println("End Date = " + spDG.getEndDate());
//				System.out.println("Current Time = " + currTime);
				
				if (spDG.getEndDate().compareTo(currTime) > 0) { 
					// Chua den gio
					continue;
				}
				
				sendMTWin(spDG, amountMng, commonObj, spMng);
				

			} else {
				// Chua den ngay
				continue;
			}

		}
	}
	
	private void sendMTWin(SanPhamDG spDG,DGAmountManager amountMng,
			DaugiaCommon commonObj, SanPhamDGManager spMng){
		
//		try {
//			Thread.sleep(10*60*1000);
//		} catch (InterruptedException e) {
//		}
		
		Boolean check = true;
		while(check){
			
			int count1 = commonObj.getNumberRecord(DGConstants.TABLE_DG_CHARGE);
			int count2 = commonObj.getNumberRecord(DGConstants.TABLE_DG_CHARGE_RESULT);
			
			if(count1 > 0 || count2 > 0 ){
			
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
				}
								
			}else{
				check = false;
			}
			
		}
		
		// tra tin trung thuong
		if (spDG.getIsSendMTWeekly() == 0) {
			DGAmount dgWin = amountMng.getUserWin();
			if (dgWin == null) {
				dgWin = new DGAmount();
				dgWin.setDgAmount("0");
				dgWin.setMaSP(spDG.getMaSP());
				dgWin.setServiceId("9209");
				dgWin.setUserId("");
			}

			// in sert bang trung thuong
//			amountMng.insertDGAmount(dgWin,
//					DGConstants.TABLE_DG_TRUNGTHUONG);
			dgWin.setWinRank(DGConstants.WIN_RANK_FIRST);
			amountMng.insertDGAmountWin(dgWin, DGConstants.TABLE_DG_TRUNGTHUONG);
			
			DGAmount dgSecond = 
				commonObj.getUserWinSecond(dgWin.getUserId(),spDG.getMaSP());
			dgSecond.setWinRank(DGConstants.WIN_RANK_SECOND);
			
			amountMng.insertDGAmountWin(dgSecond, DGConstants.TABLE_DG_TRUNGTHUONG);
			
			
//			DGAmount dgThird = commonObj.getUserWinThird(dgWin.getUserId(),
//					dgSecond.getUserId());
			
			
			
			// send MT Trung Thuong
			
			// 1. 
			sendMTforUserWin(dgWin, DGConstants.WIN_RANK_FIRST);
			sendMTforUserWin(dgSecond, DGConstants.WIN_RANK_SECOND);
			
			// win third
			ArrayList<DGAmount> arrThirds =  commonObj.getUserWinThird(dgWin.getUserId(),
					dgSecond.getUserId());
			
			for(int i = 0;i<arrThirds.size();i++){
				DGAmount dgThird = arrThirds.get(i);
				dgThird.setWinRank(DGConstants.WIN_RANK_THIRD);				
				amountMng.insertDGAmountWin(dgThird,DGConstants.TABLE_DG_TRUNGTHUONG);
				sendMTforUserWin(dgThird, DGConstants.WIN_RANK_THIRD);
			}
						
			commonObj.updateSendWeeklyMT(1, spDG.getId());
						
			spMng.getLastestSPDauGia();

		}// End If
		
	}

	private MsgObject getMsgObject(DGAmount amountObj) {
		MsgObject msgObj = new MsgObject();

		msgObj.setUserid(amountObj.getUserId());
		msgObj.setChannelType(0);
		msgObj.setServiceid(amountObj.getServiceId());
		msgObj.setMsgtype(0);
		msgObj.setContenttype(0);
		msgObj.setCommandCode("DAUGIA");
		msgObj.setMobileoperator("VMS");
		msgObj.setKeyword("DAUGIA");
		msgObj.setRequestid(new BigDecimal("1111"));

		return msgObj;
	}

	private String getDayEnd(String timeEnd) {
		return timeEnd.substring(0, 10).trim();
	}

	private String getToday() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date today = new java.util.Date();
		String sDay = formatter.format(today);
		return sDay;
	}

	private String getHourNow() {
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		java.util.Date today = new java.util.Date();
		String sDay = formatter.format(today);
		return sDay;
	}

	public Boolean isTimeSendMTWeekly(String timeEnd) {

		String dayEnd = getDayEnd(timeEnd);
		String sToday = getToday();

		if (sToday.equals(dayEnd.trim())) {

			String timeWeekly = timeEnd.substring(11, 16).trim();
			if (timeWeekly.equals(getHourNow())) {
				return true;
			}
		}

		return false;
	}
	
	private void sendMTforUserWin(DGAmount dgAmount, int winRank){
		
		DaugiaCommon commonObj = new DaugiaCommon();
		SanPhamDG spDG = SanPhamDGManager.getInstance().getSanPhamDG();
		
		double test = -1;
		try {
			test = Double.parseDouble(dgAmount.getDgAmount());
		} catch (Exception ex) {
		}
		
		if(dgAmount.getUserId().trim().equals("")) return;
		
		if (test >= 0) {
			
			MsgObject msgObj = this.getMsgObject(dgAmount);
			
			String info = "";
			if(winRank == DGConstants.WIN_RANK_FIRST){
				//info = DGConstants.MTINFO_WIN;
				try {
					info = MTDAO.getRandomMessage(
						DGProcess.hMessageReminder.get("101"),
						DGProcess.hMessageReminder.get("101").split(
								";").length);
				} catch (Exception e) {
					Util.logger.printStackTrace(e);
				}

				
			}else if(winRank == DGConstants.WIN_RANK_SECOND){
				info = DGConstants.MT_WIN_SECOND;
			}else if(winRank == DGConstants.WIN_RANK_THIRD){
				info = DGConstants.MT_WIN_THIRD;
			}
			
			info = info.replaceAll(
					DGConstants.STRING_REGEX_REPLACE_USER_ID, dgAmount
							.getUserId());

			String strStartDate = commonObj.formatDate(spDG
					.getStartDate());
			info = info.replaceAll(
					DGConstants.STRING_REGEX_REPLACE_START_DATE,
					strStartDate);

			String strAmount = dgAmount.getDgAmount() + "";
			strAmount = strAmount.replace(".0", "");

			info = info.replaceAll(
					DGConstants.STRING_REGEX_REPLACE_AMOUNT,
					strAmount);
			
			info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_TEN_SP,
					spDG.getTenSP());
			
			msgObj.setUsertext(info);

			insertTblPushMTTrungThuong(msgObj, winRank);
		}
		
	}
	
	private int insertTblPushMTTrungThuong(MsgObject msgObject, int winRank){
		
		if(msgObject.getUsertext().trim().equals("")){
			Util.logger.crisis("DAUGIA - Khong tim thay nguoi trung giai " + winRank);
			return -1;
		}
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "INSERT INTO " +
				"`tbl_pushMT_trungthuong`" +
				"(`USER_ID`,`GIAI`,`INFO`)" +
				" VALUES(?,?,?)";
						
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, msgObject.getUserid());
			stmt.setInt(2, winRank);
			stmt.setString(3, msgObject.getUsertext());
			
			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("@ExecuteUserWin insertTblPushMTTrungThuong:" +
								" Error@userid=" 
								+ msgObject.getUserid()
								+ " @GIAI = " + winRank
								+ "; info = " + msgObject.getUsertext());
			}else{

				Util.logger.info("@ExecuteUserWin insertTblPushMTTrungThuong - SUCCESSFUL:" +
						" @userid=" 
						+ msgObject.getUserid()
						+ " ; @GIAI = " + winRank
						+ "; info = " + msgObject.getUsertext());
			}

			return 1;
			
		} catch (SQLException ex3) {
			Util.logger.error("@ExecuteUserWin InsertDGCharge. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger.error("@ExecuteUserWin InsertDGCharge. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		
	}

}
