package daugia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import icom.DBPool;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

public class ResetDGAmount extends Thread{
	
	@Override
	public void run(){
		// reset khi het thoi gian DAU GIA
		
		while(Sender.getData){
			SanPhamDG spObj = getSPDauGia();
			Calendar gc = Calendar.getInstance();
			int currHour = gc.get(Calendar.HOUR_OF_DAY);
			
			if(currHour == DGConstants.DG_TIME_RESET){			
				if(spObj == null){
					
					DaugiaCommon dgCommon = new DaugiaCommon();
					DGAmountManager amountMng = new DGAmountManager();
					// write log
					
					if(dgCommon.getNumberRecord(DGConstants.TABLE_DG_AMOUNT)>0){
					
						String tblLog = DGConstants.TABLE_DG_AMOUNT
							+ dgCommon.getCurrYearMonth();
						
						Util.logger.info("DAUGIA ## Move to table daugia_amount_Log = " + tblLog );
						
						amountMng.moveDGAmountLog(tblLog);
						amountMng.resetDGAmount(DGConstants.TABLE_DG_AMOUNT);
					}
																				
					dgCommon.deleteTable("daugia_max_session");
					
					// move to log daugia_winner_tmp_log
					if(dgCommon.getNumberRecord("daugia_winner_tmp") > 0){
						
						Util.logger.info("DAUGIA ## move to log daugia_winner_tmp_log");
						
						String sqlMove = "INSERT INTO daugia_winner_tmp_log(user_id,amount,insert_date,Send_MT,TYPE,MA_SP)" +
							" SELECT user_id,amount,insert_date,Send_MT,TYPE,MA_SP FROM daugia_winner_tmp";
						DBUtil.executeSQL("gateway", sqlMove);
					
						dgCommon.deleteTable("daugia_winner_tmp");
					}
					
					dgCommon.resetIsPushAD(DGConstants.TABLE_MLIST_DG,0);
					
					AmountQueue amountQueue = AmountQueue.getInstance();
					amountQueue.resetAmountQueue();
					
				}			
			}
			
			try {
				Thread.sleep(5*60*1000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	private SanPhamDG getSPDauGia() {

		SanPhamDG spObj = null;

		String currTime = Util.getCurrentDate();
		//currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, MA_SP, TEN_SP, START_DATE," +
				" END_DATE, IMAGE_LINK, MOTA_SP, IS_SENT_MT_WEEKLY," +
				" GIA_SP, TIME_DAILY, TIME_WEEKLY, TIME_DAILY_MANUAL "
				+ " FROM daugia_sanpham WHERE START_DATE <= '"
				+ currTime
				+ "' AND IS_SENT_MT_WEEKLY < 2 ";
		
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
					spObj.setGiaSP(rs.getString("GIA_SP"));
					spObj.setTimeDaily(rs.getString("TIME_DAILY"));
					spObj.setTimeAlert(rs.getString("TIME_WEEKLY"));
					spObj.setTimeManual(rs.getString("TIME_DAILY_MANUAL"));
				}
			} else {
				Util.logger
						.error("DAUGIA - ResetDGAmount : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - ResetDGAmount. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - ResetDGAmount. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return spObj;
	}
}
