package daugia;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExeMTNewSession extends Thread{

	public String DAUGIA_TIME_NEW_SESSION = "08:00";
	
	public ExeMTNewSession(){
		DAUGIA_TIME_NEW_SESSION = 
			Constants._prop.getProperty("DAUGIA_TIME_NEW_SESSION");
	}
	
	public void run(){
		
		while(Sender.processData){
			
			DaugiaCommon dgCommon = new DaugiaCommon();
			String currHour = getCurrHour();
			SanPhamDG spDg = SanPhamDGManager.getInstance().getSanPhamDG();
			
			if(spDg == null){
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			String mtNewSession = DGConstants.MT_NEW_SESSION;
			String startHour = dgCommon.getHour(spDg.getStartDate());
			String endHour = dgCommon.getHour(spDg.getEndDate());
			
			String startDate = dgCommon.formatDate(spDg.getStartDate());
			String endDate = dgCommon.formatDate(spDg.getEndDate());
			
			mtNewSession = mtNewSession.replaceAll(
					DGConstants.STRING_REGEX_START_HOUR, 
					startHour);
			mtNewSession = mtNewSession.replaceAll(
					DGConstants.STRING_REGEX_REPLACE_START_DATE,
					startDate);
			mtNewSession = mtNewSession.replaceAll(
					DGConstants.STRING_REGEX_END_HOUR, 
					endHour);
			mtNewSession = mtNewSession.replaceAll(
					DGConstants.STRING_REGEX_REPLACE_END_DATE,
					endDate); 
			
			if(currHour.compareTo(DAUGIA_TIME_NEW_SESSION)>0){
				
				ArrayList<DGAmount> arrMaxSession = getMaxSession();
				
				for(int i = 0;i<arrMaxSession.size();i++){
					
					DGAmount amountObj = arrMaxSession.get(i);
					
					//send MT
					MsgObject msgObj = getMsgObj(amountObj.getUserId(),i);
					mtNewSession = mtNewSession.replaceAll(
							DGConstants.COMMAND_CODE,
							msgObj.getCommandCode()); 
					
					msgObj.setUsertext(mtNewSession);
					dgCommon.sendMT(msgObj);
					dgCommon.deleteTableByID(amountObj.getId(),
							"daugia_max_session");
				}
				
			}
			
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	
    private ArrayList<DGAmount> getMaxSession(){
		
		ArrayList<DGAmount> arrMaxSession = new ArrayList<DGAmount>();
	
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String currDate = getBeforeDate();

		String sqlQuery = "SELECT ID, USER_ID, DATE_INSERT " +
				" FROM daugia_max_session WHERE DATE_INSERT = '" 
				+ currDate + "'";
		
//		System.out.println("getMaxSession: " + sqlQuery);
		
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
					DGAmount obj = new DGAmount();
					obj.setId(rs.getInt("ID"));
					obj.setUserId(rs.getString("user_id"));
					obj.setTimeSendMO(currDate);
					arrMaxSession.add(obj);
				}
			} else {
				Util.logger
						.error("DAUGIA - ExeMTNewSession.getMaxSession : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - ExeMTNewSession.getMaxSession. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - ExeMTNewSession.getMaxSession. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMaxSession;
		
	}

	private String getBeforeDate() {
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar curDate = Calendar.getInstance();
		curDate.set(Calendar.DAY_OF_MONTH, curDate.get(Calendar.DAY_OF_MONTH) - 1);
		String beforeDate = formatter.format(curDate.getTime());

		return beforeDate;
	}

	private String getCurrHour(){
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		java.util.Date today = new java.util.Date();			
		String currHour = formatter.format(today);
		return currHour;
	}
	
	private String getRequestId(int i){
		DateFormat formatter = new SimpleDateFormat("MMddHHmmss");
		java.util.Date today = new java.util.Date();			
		String currHour = formatter.format(today) + i;
		return currHour;
	}
	
	private MsgObject getMsgObj(String userId, int i){
		
		MsgObject msgObj = new MsgObject();
		
		msgObj.setUserid(userId);
		msgObj.setMobileoperator("VMS");
		msgObj.setChannelType(0);
		msgObj.setServiceid("9209");
		msgObj.setRequestid(new BigDecimal(getRequestId(i)));
		msgObj.setCommandCode("DAUGIA");
		msgObj.setContenttype(0);
		msgObj.setMsgtype(0);
		msgObj.setKeyword("DAUGIA");		
		
		return msgObj;
	}
	
	
}
