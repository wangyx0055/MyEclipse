package daugia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import icom.DBPool;
import icom.Sender;
import icom.common.Util;

/**
 * Tìm ra người danh chiến thắng do 
 * người đặt giá thấp hơn bị mất ưu thế
 * 
 * @author DanND
 * @date 2011-10-06
 *
 */

public class FindWinTmpLucky extends Thread {
	
	public void run(){
		
		while(Sender.processData){
			
			DGAmountManager amountMng = new DGAmountManager();
			DGAmount dgWin = amountMng.getUserWin();
			
			if(dgWin == null){
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			Boolean check = isExistWinTmp(dgWin);
			if(!check){
				DaugiaCommon dgComm = new DaugiaCommon();
				dgComm.insertWinnerTmp(dgWin.getUserId(),
						dgWin.getDgAmount(),
						dgWin.getMaSP(), DGConstants.TYPE_WIN_TMP_LAST);
			}
			
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	private Boolean isExistWinTmp(DGAmount dgWin){
		
		int count = 0;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM " +
				" daugia_winner_tmp " +
				" WHERE user_id = '" + dgWin.getUserId() + "' " +
				" AND MA_SP = '" + dgWin.getMaSP() + "' " +
				" AND amount = '" + dgWin.getDgAmount() + "'";
		// System.out.println("getMlistTableName: " + sqlQuery);
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
					count = rs.getInt("number");
				}
			} else {
				Util.logger
						.error("FindWinTmpLucky - isExistWinTmp : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("FindWinTmpLucky - isExistWinTmp. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("FindWinTmpLucky - isExistWinTmp. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		if(count == 0) return false;
		
		return true;
	}
	
	
}
