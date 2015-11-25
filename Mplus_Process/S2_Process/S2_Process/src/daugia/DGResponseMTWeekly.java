package daugia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import DAO.MTDAO;

import servicesPkg.MlistInfo;
import servicesPkg.ServiceMng;

import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.Util;

public class DGResponseMTWeekly extends Thread {

	@Override
	public void run() {
		
		// send MT Khong Trung Thuong...

		DaugiaCommon commonObj = new DaugiaCommon();
		ServiceMng serviceMng = new ServiceMng();

		while (Sender.getData) {
			
			SanPhamDG spDG = commonObj.getSPDGNotResponseWeekly();

			if (spDG == null) {

				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e) {
				}
				continue;
			}
			
			if(!isSendWeekly()){
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e) {
				}
				continue;
			}
			
			DGAmount dgWin = commonObj.getUserIdWin(spDG.getMaSP().trim());
			String userIdWin = "";
			if (dgWin != null) {
				userIdWin = dgWin.getUserId();
			}

			//String info = DGConstants.MTINFO_NO_WIN;
			String info = "";
			try {
				info = MTDAO.getRandomMessage(
					DGProcess.hMessageReminder.get("102"),
					DGProcess.hMessageReminder.get("102").split(
							";").length);
			} catch (Exception e) {
				Util.logger.printStackTrace(e);
			}
			double test = -1;
			try {
				test = Double.parseDouble(dgWin.getDgAmount());
			} catch (Exception ex) {
			}

			if (test < 1) {
				//info = DGConstants.MTINFO_NO_CUS_WIN;
				try {
					info = MTDAO.getRandomMessage(
						DGProcess.hMessageReminder.get("103"),
						DGProcess.hMessageReminder.get("103").split(
								";").length);
				} catch (Exception e) {
					Util.logger.printStackTrace(e);
				}

			}

			String phoneNumber = dgWin.getUserId();
			if (phoneNumber.length() > 5) {
				phoneNumber = phoneNumber
						.substring(0, phoneNumber.length() - 4);
				phoneNumber = phoneNumber + "xxx";
			}

			String startDate = commonObj.formatDate(spDG.getStartDate());
			String strAmount = dgWin.getDgAmount() + "";
			strAmount = strAmount.replace(".0", "");

			info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_USER_ID,
					phoneNumber);
			info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_START_DATE,
					startDate);
			info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_AMOUNT,
					strAmount);
			info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_TEN_SP,
					spDG.getTenSP());
			
			// send MT Khong Trung Thuong...
			ArrayList<MlistInfo> arrMlist = commonObj
					.getAllMlistInfo(DGConstants.TABLE_MLIST_DG);

			for (int i = 0; i < arrMlist.size(); i++) {

				MlistInfo mlistInfo = arrMlist.get(i);

				if (mlistInfo.getUserId().trim().equals(userIdWin)) {
					// Khong gui tin nay cho nguoi trung thuong
					serviceMng.updateMlistActive1(DGConstants.TABLE_MLIST_DG,
							mlistInfo.getId());
					continue;
				}

				MsgObject msgObj = this.getMsgObj(mlistInfo);
				
				if(DGConstants.DG_MODE==0){
					DBSelect dbSelect = new DBSelect();
					Boolean checkTest = dbSelect.isUserTest(msgObj.getUserid());
					if(!checkTest){														
						continue;
					}
					
				}
				
				if(msgObj.getCommandCode().equals("DAUGIA")) 
					info.replaceAll(DGConstants.COMMAND_CODE, "DG");
				else{
					info.replaceAll(DGConstants.COMMAND_CODE, msgObj.getCommandCode());
				}
				msgObj.setUsertext(info);
				commonObj.pushMTWin(msgObj);
				serviceMng.updateMlistActive1(DGConstants.TABLE_MLIST_DG,
						mlistInfo.getId());
				
				mlistInfo = null;

			}// End For loop

			commonObj.updateSendWeeklyMT(2, spDG.getId());
			updateEmptyAmount(0);
			
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
			}
		}// END IF

	}

	private MsgObject getMsgObj(MlistInfo mlistInfo) {

		MsgObject msgObj = new MsgObject();

		msgObj.setUserid(mlistInfo.getUserId());
		msgObj.setMobileoperator(mlistInfo.getMobiOperator());
		msgObj.setChannelType(mlistInfo.getChanelType());
		msgObj.setServiceid(mlistInfo.getServiceId());
		msgObj.setCommandCode(mlistInfo.getCommandCode());
		msgObj.setContenttype(0);
		msgObj.setMsgtype(mlistInfo.getMessageType());
		msgObj.setKeyword("DAUGIA");
		msgObj.setRequestid(new BigDecimal(mlistInfo.getRequestId()));
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
	
	public Boolean isSendWeekly(){
		
		Boolean check = false;
		DaugiaCommon dgComm = new DaugiaCommon();

		ArrayList<MsgObject> arrWin = getObjPushMTWin();
		for(int i = 0;i<arrWin.size();i++){
			MsgObject msgObj = arrWin.get(i);
			if(msgObj.getContenttype()==1){
				check = true;
			}
			msgObj.setCommandCode("DAUGIA");
			msgObj.setMobileoperator("VMS");
			msgObj.setKeyword("DAUGIA");
			msgObj.setRequestid(new BigDecimal(getCurrentTime()+i));
			msgObj.setContenttype(0);
			msgObj.setServiceid("9209");
			
			dgComm.pushMTWin(msgObj);
			dgComm.deleteTableByUserID(msgObj.getUserid(),"tbl_pushMT_trungthuong");
			
		}
		
		return check;
		
	}
	
	private ArrayList<MsgObject> getObjPushMTWin(){
		
		ArrayList<MsgObject> arrWin = new ArrayList<MsgObject>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT id,user_id,GIAI,STATUS,INFO " +
				" FROM tbl_pushMT_trungthuong WHERE STATUS = 1" +
				" ORDER BY GIAI DESC";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);;

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					MsgObject msgObject = new MsgObject();
//					msgObject.setId(rs.getInt("ID"));
					msgObject.setUserid(rs.getString("user_id"));
					msgObject.setContenttype(rs.getInt("GIAI"));
					msgObject.setUsertext(rs.getString("INFO"));
					arrWin.add(msgObject);
					//rs.deleteRow();
				}
			} else {
				Util.logger
						.error("DAUGIA - getObjPushMTWin : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - getObjPushMTWin. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - getObjPushMTWin. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrWin;
		
	}
	
	public int updateEmptyAmount(int status){
		
		int iReturn = -1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "UPDATE daugia_amount_empty SET status="
				+ status;
		
		Util.logger.info("Update ZERO status " +
				"daugia_amount_empty:: QUERRY =" + sqlQuery);
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			
			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			}

		} catch (SQLException ex3) {
			Util.logger.error("@PushMTWeekLy updateEmptyAmount. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("@PushMTWeekLy updateEmptyAmount SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
	}
	
	private String getCurrentTime() {
		Calendar now = Calendar.getInstance();
		return new java.text.SimpleDateFormat("HHmmss").format(now
				.getTime());
	}
	
}
