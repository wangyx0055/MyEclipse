package daugia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DAO.MTDAO;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.DBUtil;
import icom.common.Util;

public class _DGMOHandle {

	private MsgObject msgObj = null;
	private DaugiaCommon dgCommon = null;
	private double dgAmount = 0;
	private Keyword keyword;
	private String strAmount = "0";
	Boolean isSendMT = false;

	public _DGMOHandle(MsgObject msgObject, double _dgAmount, String _strAmount) {

		msgObj = new MsgObject(msgObject);
		dgAmount = _dgAmount;
		strAmount = _strAmount;
		String info = "";
		if (msgObj.getCommandCode().equals("DAUGIA")) {
			Boolean isExist = DBUtil.isexist_in_mlist(msgObj.getUserid(),
					DGConstants.TABLE_MLIST_DG, "DAUGIA", 0, "");
			if (isExist) {
				msgObject.setCommandCode("DAUGIA");
				// update into database

				String strUpdate = "UPDATE mlist_daugia_icom SET command_code = 'DAUGIA', service ='DAUGIA' WHERE user_id = '"
						+ msgObject.getUserid() + "'";
				Util.logger.info("DGMOHandle chuyen sang tap DG @strUpdate :"
						+ strUpdate);
				if (DBUtil.Update(strUpdate)) {
					Util.logger.info("DGMOHandle @strUpdate success");
				}

			}
		}
		if (msgObject.getCommandCode().toUpperCase().equals("DAUGIA")) {
			info = "DG";
			keyword = Sender.loadconfig.getKeyword(info.toUpperCase(), msgObj
					.getServiceid());
			msgObj.setKeyword("DAUGIA");
			msgObj.setAmount(keyword.getAmount());
			msgObj.setCommandCode("DAUGIA");
			msgObj.setServiceName("DAUGIA");
			msgObj.setChannelType(0);
			msgObj.setContentId(keyword.getService_type());
		}

		dgCommon = new DaugiaCommon();
	}

	public void handleMO() {

		if (DGConstants.DG_MODE == 0) {
			DBSelect dbSelect = new DBSelect();
			Boolean check = dbSelect.isUserTest(msgObj.getUserid());
			if (!check) {
				msgObj
						.setUsertext("Dich vu chua chay. xin vui long thu lai sau!");
				dgCommon.sendMT(msgObj);

				return;
			}

		}

		strAmount = formatStrAmount(strAmount);
		if (strAmount.equals("0")) {
			// String info = DGConstants.MTINFO_WRONG_AMOUNT;
			String info = "";
			try {
				info = MTDAO.getRandomMessage(DGProcess.hMessageReminder
						.get("71"), DGProcess.hMessageReminder.get("71").split(
						";").length);
			} catch (Exception e) {
				Util.logger.printStackTrace(e);
			}
			// tuannq add
			if (msgObj.getCommandCode().equals("DAUGIA"))
				info = info.replace(DGConstants.COMMAND_CODE, "DG");
			else {
				info = info.replace(DGConstants.COMMAND_CODE, msgObj
						.getCommandCode());
			}
			msgObj.setUsertext(info);
			dgCommon.sendMT(msgObj);
			return;
		}

		if (dgAmount > 0) {

			// registry
			registryDG();

			// send charge
			sendToDGCharge();

			// Thread thSendCharge = new Thread(new Runnable() {
			// public void run() {
			//					
			// sendToDGCharge();
			//					
			// registryDG();
			// }
			// });
			//			
			// thSendCharge.start();

			// if (isExist) {
			//				
			// }else{
			// //isSendMT = true;
			// registryDG();
			// }

		}

		if (dgAmount <= 0 || isSendMT) {
			sendMT();
		}

		// thRegistry.start();

	}

	protected void registryDG() {
		Util.logger
				.info("registryDG @ commandcode :" + msgObj.getCommandCode());
		try {
			Keyword keyword = new Keyword();
			if (msgObj.getCommandCode().equals("DAUGIA")) {
				keyword = Sender.loadconfig.getKeyword(msgObj.getCommandCode()
						.toUpperCase(), msgObj.getServiceid());
				registerDG(msgObj, "mlist_daugia_icom", 0, keyword);
			}

			// comment to save code
			/*
			 * Boolean isExist = DBUtil.isexist_in_mlist(msgObj.getUserid(),
			 * DGConstants.TABLE_MLIST_DG,msgObj.getCommandCode(),0); if
			 * (!isExist) { dgCommon.insertMlistService(msgObj, keyword,
			 * DGConstants.TABLE_MLIST_DG);
			 * dgCommon.deleteTableByUserID(msgObj.getUserid(),
			 * DGConstants.TABLE_MLIST_DG+"_cancel");
			 * 
			 * DBUtil.InsertSubcriber(DGConstants.TABLE_MLIST_DG, msgObj,
			 * "0",0);
			 * 
			 * String sqlDelete = "DELETE FROM mlist_subcriber_cancel WHERE " +
			 * " USER_ID = '" + msgObj.getUserid() +
			 * "' AND COMMAND_CODE = 'DAUGIA'"; DBUtil.executeSQL("gateway",
			 * sqlDelete);
			 * 
			 * }else{
			 * 
			 * String strUpdate =
			 * "UPDATE mlist_daugia_icom SET REG_COUNT = REG_COUNT + 1 " +
			 * "WHERE user_id = '" + msgObj.getUserid() + "'" +
			 * " AND COMMAND_CODE = 'DAUGIA'";
			 * 
			 * Util.logger.info("Update RegCount, sql = " + strUpdate);
			 * 
			 * DBUtil.Update(strUpdate);
			 * 
			 * }
			 */

		} catch (Exception e) {
			Util.logger.info("DGMOHandle @@ RegisterServices Error:"
					+ e.getMessage());
		}

	}

	private void sendToDGCharge() {

		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		DaugiaCommon objComm = new DaugiaCommon();

		// Check Black List
		if (!objComm.isValidMOBlackList(msgObj.getUserid())) {

			msgObj
					.setUsertext("He thong khong tien hanh tru tien"
							+ " duoc so dien thoai cua ban. Xin vui long thu lai sau 5 phut!");
			objComm.sendMT(msgObj);
			return;

		}

		if (spMng.checkTimeDG(msgObj.getTimeSendMO())) {
			if (objComm.isFreeList(msgObj.getUserid())) {
				dgCommon.InsertDGCharge(msgObj,
						DGConstants.TABLE_DG_CHARGE_RESULT, strAmount);
				return;
			}
			dgCommon.InsertDGCharge(msgObj, DGConstants.TABLE_DG_CHARGE,
					strAmount);

		} else {
			// insert to MT
			// String info = DGConstants.MT_END_DG;
			String info = "";
			try {
				info = MTDAO.getRandomMessage(DGProcess.hMessageReminder
						.get("72"), DGProcess.hMessageReminder.get("72").split(
						";").length);
			} catch (Exception e) {
				Util.logger.printStackTrace(e);
			}

			SanPhamDG spExpired = this.getSPExpiredDateLastest();
			String endHour = "";
			String endDate = "";

			String nextHour = "";
			String nextStartDate = "";
			String nextSp = "";

			if (spExpired != null) {
				String startDate = dgCommon
						.formatDate(spExpired.getStartDate());
				endDate = dgCommon.formatDate(spExpired.getEndDate());
				info = info.replaceAll(
						DGConstants.STRING_REGEX_REPLACE_START_DATE, startDate);
				endHour = objComm.getHour(spExpired.getEndDate());
				// msgObj.setUsertext(info);

				SanPhamDG spNext = dgCommon.getNextSP();

				if (spNext != null) {
					// info = DGConstants.MT_END_DG_HAVE_NEXT_SP;

					try {
						info = MTDAO
								.getRandomMessage(DGProcess.hMessageReminder
										.get("73"), DGProcess.hMessageReminder
										.get("73").split(";").length);
					} catch (Exception e) {
						Util.logger.printStackTrace(e);
					}
					nextHour = dgCommon.getHour(spNext.getStartDate());
					nextStartDate = dgCommon.formatDate(spNext.getStartDate());
					nextSp = spNext.getTenSP();
				}

			} else {

				endDate = getBeforeDate();
				endHour = "14h";

				SanPhamDG spNext = dgCommon.getNextSP();

				if (spNext != null) {
					info = DGConstants.MT_FIRST_DAUGIA;
					try {
						info = MTDAO.getRandomMessage(
								DGProcess.hMessageReminder.get("104"),
								DGProcess.hMessageReminder.get("104")
										.split(";").length);
					} catch (Exception e) {
						Util.logger.printStackTrace(e);
					}

					nextHour = dgCommon.getHour(spNext.getStartDate());
					nextStartDate = dgCommon.formatDate(spNext.getStartDate());
					nextSp = spNext.getTenSP();
				}

			}

			info = info.replace(DGConstants.STRING_REGEX_REPLACE_END_DATE,
					endDate);
			info = info.replaceAll(DGConstants.STRING_REGEX_END_HOUR, endHour);

			info = info
					.replaceAll(DGConstants.STRING_REGEX_NEXT_HOUR, nextHour);
			info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_START_DATE,
					nextStartDate);
			info = info
					.replaceAll(DGConstants.STRING_REGEX_NEXT_TEN_SP, nextSp);

			msgObj.setUsertext(info);

			dgCommon.sendMT(msgObj);

		}
	}

	private void sendMT() {

		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		SanPhamDG spDG = spMng.getSanPhamDG();

		// String info = DGConstants.MTINFO_WRONG_KEYWORD;
		String info = "";
		try {
			info = MTDAO.getRandomMessage(DGProcess.hMessageReminder.get("21"),
					DGProcess.hMessageReminder.get("21").split(";").length);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		}

		// tuannq add
		if (msgObj.getCommandCode().equals("DAUGIA"))
			info = info.replace(DGConstants.COMMAND_CODE, "DG");
		else {
			info = info.replace(DGConstants.COMMAND_CODE, msgObj
					.getCommandCode());
		}
		if (isVeryPoor(msgObj.getUsertext())) {
			// info = DGConstants.MTINFO_WRONG_AMOUNT;

			try {
				info = MTDAO.getRandomMessage(DGProcess.hMessageReminder
						.get("71"), DGProcess.hMessageReminder.get("71").split(
						";").length);
			} catch (Exception e) {
				Util.logger.printStackTrace(e);
			}
			if (msgObj.getCommandCode().equals("DAUGIA"))
				info = info.replace(DGConstants.COMMAND_CODE, "DG");
			else {
				info = info.replace(DGConstants.COMMAND_CODE, msgObj
						.getCommandCode());
			}
		}

		// if(isSendMT){
		// info = DGConstants.MT_NOT_REGISTRY;
		// }

		isSendMT = false;

		String strStartDate = "";
		String strEndDate = "";
		String giaSp = "";
		String tenSp = "";

		String nextHour = "";
		String nextStartDate = "";
		String nextSp = "";

		boolean noError = true;

		if (spDG != null) {
			strStartDate = dgCommon.formatDate(spDG.getStartDate());
			strEndDate = dgCommon.formatDate(spDG.getEndDate());
			giaSp = spDG.getGiaSP();
			tenSp = spDG.getTenSP();
		} else {

			SanPhamDG spExpired = this.getSPExpiredDateLastest();

			// info = DGConstants.MT_END_DG;

			try {
				info = MTDAO.getRandomMessage(DGProcess.hMessageReminder
						.get("72"), DGProcess.hMessageReminder.get("72").split(
						";").length);
			} catch (Exception e) {
				Util.logger.printStackTrace(e);
			}

			SanPhamDG spNext = dgCommon.getNextSP();

			if (spNext != null) {
				// info = DGConstants.MT_END_DG_HAVE_NEXT_SP;
				try {
					info = MTDAO.getRandomMessage(DGProcess.hMessageReminder
							.get("73"), DGProcess.hMessageReminder.get("73")
							.split(";").length);
				} catch (Exception e) {
					Util.logger.printStackTrace(e);
				}
				nextHour = dgCommon.getHour(spNext.getStartDate());
				nextStartDate = dgCommon.formatDate(spNext.getStartDate());
				nextSp = spNext.getTenSP();

				info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_HOUR,
						nextHour);
				info = info
						.replaceAll(DGConstants.STRING_REGEX_NEXT_START_DATE,
								nextStartDate);
				info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_TEN_SP,
						nextSp);
			}

			if (spExpired != null) {
				strStartDate = dgCommon.formatDate(spExpired.getStartDate());
				strEndDate = dgCommon.formatDate(spExpired.getEndDate());
				giaSp = spExpired.getGiaSP();
				tenSp = spExpired.getTenSP();
			} else {
				strEndDate = getBeforeDate();
				noError = true;
			}
		}

		DaugiaCommon commObj = new DaugiaCommon();

		String endHour = "";
		if (!noError) {
			endHour = commObj.getHour(strEndDate);
		} else {
			endHour = "14h";
		}

		info = info.replaceAll(DGConstants.STRING_REGEX_END_HOUR, endHour);
		info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_START_DATE,
				strStartDate);
		info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_END_DATE,
				strEndDate);
		info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_GIA_SP, giaSp);
		info = info.replaceAll(DGConstants.STRING_REGEX_REPLACE_TEN_SP, tenSp);

		info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_HOUR, nextHour);
		info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_START_DATE,
				nextStartDate);
		info = info.replaceAll(DGConstants.STRING_REGEX_NEXT_TEN_SP, nextSp);

		msgObj.setUsertext(info);
		dgCommon.sendMT(msgObj);

	}

	/***
	 * Lay thong tin san pham dau gia Het han gan nhat
	 * 
	 * @return
	 */

	private SanPhamDG getSPExpiredDateLastest() {

		SanPhamDG spObj = null;

		String currTime = Util.getCurrentDate();
		// currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, MA_SP, TEN_SP, START_DATE, END_DATE, IMAGE_LINK, MOTA_SP, IS_SENT_MT_WEEKLY"
				+ " FROM daugia_sanpham WHERE END_DATE < '"
				+ currTime
				+ "' ORDER BY END_DATE DESC limit 1";

		Util.logger.info("DAUGIA getSPExpiredDateLastest sql = " + sqlQuery);
		// System.out.println("getSPExpiredDateLastest sqlQuerry = " +
		// sqlQuery);
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
			Util.logger.error("DAUGIA - getSPExpiredDateLastest. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - getSPExpiredDateLastest. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return spObj;
	}

	private Boolean isVeryPoor(String info) {
		Pattern icomPattern = Pattern.compile("[.,]");
		Matcher match = icomPattern.matcher(info);
		return match.find();
	}

	private String getBeforeDate() {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) - 1);
		return new java.text.SimpleDateFormat("dd/MM/yyyy").format(now
				.getTime());
	}

	private String formatStrAmount(String strAmount) {

		String testAmount = strAmount;
		String amountResult = "";

		int indexStop = 0;
		int amountLength = testAmount.length();
		for (int i = 0; i < amountLength; i++) {
			char chTest = testAmount.charAt(i);
			if (chTest == '0') {
				if (i == amountLength - 1) {
					indexStop = amountLength - 1;
				}
				continue;
			}

			indexStop = i;
			break;
		}

		if (indexStop == (amountLength - 1)) {
			amountResult = "0";
		} else {
			amountResult = testAmount.substring(indexStop);
		}

		return amountResult;
	}

	/**
	 * tuannq add for daugia ung voi command_code DG
	 * 
	 * @param msgObject
	 * @param MLIST
	 * @param iCompanyId
	 * @param keyword
	 * @return
	 */
	public static void registerDG(MsgObject msgObject, String MLIST,
			int iCompanyId, Keyword keyword) {

		DBUtil db = new DBUtil();
		if (DBUtil.isexist_in_mlist(msgObject.getUserid(), MLIST, "DA",
				iCompanyId, "")) {
			String strUpdate = "UPDATE "
					+ MLIST
					+ " SET command_code = 'DAUGIA', service ='DAUGIA' WHERE user_id = '"
					+ msgObject.getUserid() + "'";
			Util.logger.info("registerDG @strUpdate :" + strUpdate);
			if (DBUtil.Update(strUpdate))
				Util.logger.info("registerDG @strUpdate  success!");
		}
		/*****
		 * Neu da ton tai trong mlist thi lay exist message tu keyword
		 * **/
		if (DBUtil.isexist_in_mlist(msgObject.getUserid(), MLIST, "DAUGIA",
				iCompanyId, "")) {

		} else {
			/****
			 * Trong truong hop chua co dich vu: - da ton tai trong
			 * mlist_cancel: move toan bo tu mlist_cancel, increase reg_count
			 * ++; update autotime=current_date - neu khong ton tai trong
			 * mlist_cancel: truong hop dang ky moi
			 * **/

			/****
			 * Neu da ton tai trong mlist_cancel: Dang ky lai Move toan bo tu
			 * mlist_cancel sang, increase reg_count ++, update autotimes
			 * **/
			if (DBUtil.isexist_in_cancel(msgObject.getUserid(), MLIST,
					"DAUGIA", iCompanyId, "")) {
				DBUtil.InsertMlistCancel2Mlist(MLIST, msgObject.getUserid(),
						keyword.getAmount(), msgObject.getChannelType(),
						"DAUGIA", "", msgObject.getIsIcom(), "");
				/******
				 * move mlist_cancel sang subcriber *
				 **/
				DBUtil.MoveMlistCancel2Subcriber("mlist_subcriber", msgObject
						.getUserid(), "DAUGIA", msgObject.getChannelType(),
						iCompanyId, "");
				/***
				 * Sau khi move sang mlist thi xoa ben mlist_cancel
				 * **/
				DBUtil.DelMlist(MLIST + "_cancel", msgObject.getUserid(),
						"DAUGIA", "");
				/******
				 * delete Subcriber_cancel *
				 **/
				DBUtil.DeleteSubcriberCancel(msgObject.getUserid(), "DAUGIA",
						msgObject.getChannelType(), "");

				if (msgObject.getLast_code() != null
						&& !msgObject.getLast_code().equals("0")
						&& !msgObject.getLast_code().trim().equals("")) {

					DBUtil.updateLastCode(MLIST, msgObject.getUserid(),
							msgObject.getCommandCode(), msgObject
									.getLast_code());
				}

			} else {
				/****
				 * Neu khong ton tai trong mlist_cancel: Dang ky moi Trong giai
				 * doan khuyen mai, MODE_ADV = 0: change Mode_adv: khong dung
				 * key nay nua, chuyen qua viec check trong bang services
				 * active_free=0: khong khuyen mai, = 1: khuyen mai Doi voi
				 * truong hop nay khong co khuyen mai
				 * **/
				String mtfree = "0";
				/*****
				 * String mlist, MsgObject ems, String mtfree, int msgtype, long
				 * lduration, long amount *
				 **/
				DBUtil.Insert2Mlist(MLIST, msgObject, mtfree, 1, 0, keyword
						.getAmount());
				/******
				 * Them user_id vao Subcriber Doi command_code thanh daugia de
				 * insert vao bang subcrible *
				 **/
				msgObject.setCommandCode("DAUGIA");
				DBUtil.InsertSubcriber(MLIST, msgObject, mtfree, 1);
			}
		}

	}

	/**
	 * Tuannq add for daugia truong hop keyword ung voi DA
	 * 
	 * @param msgObject
	 * @param MLIST
	 * @param iCompanyId
	 * @param keyword
	 * @return
	 */
	public static String registerDA(MsgObject msgObject, String MLIST,
			int iCompanyId, Keyword keyword) {
		String mtReturn = "";

		/*****
		 * Neu da ton tai trong mlist thi lay exist message tu keyword
		 * **/
		if (DBUtil.isexist_in_mlist(msgObject.getUserid(), MLIST, "DA", 0, "")
				|| DBUtil.isexist_in_mlist(msgObject.getUserid(), MLIST,
						"DAUGIA", 0, "")) {
			// mtReturn = keyword.getExistMsg();
		} else {
			/****
			 * Trong truong hop chua co dich vu: - da ton tai trong
			 * mlist_cancel: move toan bo tu mlist_cancel, increase reg_count
			 * ++; update autotime=current_date - neu khong ton tai trong
			 * mlist_cancel: truong hop dang ky moi
			 * **/

			// mtReturn = keyword.getSubMsg();

			/****
			 * Neu da ton tai trong mlist_cancel: Dang ky lai Move toan bo tu
			 * mlist_cancel sang, increase reg_count ++, update autotimes
			 * **/
			if (DBUtil.isexist_in_cancel(msgObject.getUserid(), MLIST, "DA",
					iCompanyId, "")) {
				DBUtil.InsertMlistCancel2Mlist(MLIST, msgObject.getUserid(),
						keyword.getAmount(), msgObject.getChannelType(), "DA",
						"", msgObject.getIsIcom(), "");
				/******
				 * move mlist_cancel sang subcriber *
				 **/
				DBUtil.MoveMlistCancel2Subcriber("mlist_subcriber", msgObject
						.getUserid(), "DAUGIA", msgObject.getChannelType(),
						iCompanyId, "");
				/***
				 * Sau khi move sang mlist thi xoa ben mlist_cancel
				 * **/
				DBUtil.DelMlist(MLIST + "_cancel", msgObject.getUserid(), "DA",
						"");

				/******
				 * delete Subcriber_cancel *
				 **/
				DBUtil.DeleteSubcriberCancel(msgObject.getUserid(), "DAUGIA",
						msgObject.getChannelType(), "");

				if (msgObject.getLast_code() != null
						&& !msgObject.getLast_code().equals("0")
						&& !msgObject.getLast_code().trim().equals("")) {

					DBUtil.updateLastCode(MLIST, msgObject.getUserid(),
							msgObject.getCommandCode(), msgObject
									.getLast_code());
				}

			} else {
				/****
				 * Neu khong ton tai trong mlist_cancel: Dang ky moi Trong giai
				 * doan khuyen mai, MODE_ADV = 0: change Mode_adv: khong dung
				 * key nay nua, chuyen qua viec check trong bang services
				 * active_free=0: khong khuyen mai, = 1: khuyen mai Doi voi
				 * truong hop nay khong co khuyen mai
				 * **/
				String mtfree = "0";
				/*****
				 * String mlist, MsgObject ems, String mtfree, int msgtype, long
				 * lduration, long amount *
				 **/
				DBUtil.Insert2Mlist(MLIST, msgObject, mtfree, 1, 0, keyword
						.getAmount());
				/******
				 * Them user_id vao Subcriber Doi command_code thanh daugia de
				 * insert vao bang subcrible *
				 **/
				msgObject.setCommandCode("DAUGIA");
				DBUtil.InsertSubcriber(MLIST, msgObject, mtfree, 1);
			}
		}
		return mtReturn;
	}

}
