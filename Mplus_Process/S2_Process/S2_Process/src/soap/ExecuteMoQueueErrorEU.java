package soap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import icom.DBPool;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.Util;

public class ExecuteMoQueueErrorEU extends Thread {
	String className = "ExecuteMoQueueErrorEU ";
	public ExecuteMoQueueErrorEU (){
		
	}
	public void run(){
		String sql = "SELECT * FROM mo_queue_error WHERE COMMAND_CODE = 'EU'";
		Util.logger.info("ExecuteMoQueueErrorEU start:@sql :"+sql);
		Connection connection = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String result ="";
		String tableName ="";
		try{
			connection = dbpool.getConnectionGateway();
			stmt = connection.prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.last()) {
					MsgObject msgObject =  new MsgObject();
					msgObject.setUsertext(rs.getString("INFO"));	
					msgObject.setUserid(rs.getString("USER_ID"));
					msgObject.setServiceid(rs.getString("SERVICE_ID"));
					msgObject.setMobileoperator(rs.getString("MOBILE_OPERATOR"));
					msgObject.setCommandCode(rs.getString("COMMAND_CODE"));
					msgObject.setRequestid(BigDecimal.valueOf(Integer
							.parseInt(rs.getString("REQUEST_ID"))));
					
					Util.logger.info(className +  " send SOAP. Infomation:\t@user:"+msgObject.getUserid() 
							+"\t@CommandCode:"+msgObject.getCommandCode() 
							+"\t@requestId :"+msgObject.getRequestid() 
							+"\t@info"+msgObject.getUsertext());
					
					result = msgObject.sendMessageMO(msgObject.getUsertext(),
							"CD", msgObject.getCommandCode());
					result = result.trim();
					
					Util.logger.info(className +  " send done, result:"+result +"\t@userid:"+msgObject.getUserid());
					//Truong hop thanh cong
					if(result.equals("1")){
						//truong hop huy? move tu  customer_cacel - >customer sau do reset total score
						//huy TC, dk EU
						Util.logger.info(className +" : send MO success! \t@userid:"+msgObject.getUserid()
								+"\t@info"+msgObject.getUsertext());
						if(msgObject.getUsertext().trim().toUpperCase().equals("TC")){
							tableName = "customer";
							if(isexistCustomer(msgObject.getUserid(),tableName)){
								MoveCustomer2Cancel(msgObject.getUserid());
								deleteCustomer(msgObject.getUserid());
							}
						}
						else{
							tableName = "customer_cancel";
							if(isexistCustomer(msgObject.getUserid(),tableName)){
								MoveCustomerCancel2Customer(msgObject.getUserid());
								//delete customer 
								deleteCustomer(msgObject.getUserid());
							}else{
								// neu khong co trong bang huy? thi check co trong bang customer chua  dang ky moi cho nguoi dung
								tableName = "customer";
								if(!isexistCustomer(msgObject.getUserid(),tableName)){
									insertCustomer(msgObject.getUserid(),tableName);
								}
							}
						}
						rs.deleteRow();
					}else{
						//truong hop send khong thanh cong van giu nguyen
						Util.logger.info(className +" : send MO false!Update result \t@userid:"+msgObject.getUserid());
						//update result
						rs.updateString("RESPONDED", result);
					}
				}
			}
		}catch (Exception e) {
			Util.logger.info(className + " has an error: "+e);
		}finally{
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
	}

	public static boolean isexistCustomer(String userid, String mlist) {
		Connection connection;
		DBPool dbpool;
		connection = null;
		boolean check =  false;
		dbpool = new DBPool();
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			connection = dbpool.getConnection("euro_report");

			String query3 = "select 1 from " + mlist + " where pid = '"+getPid(userid)+"' and msisdn='" + userid + "' ";
			Util.logger.info("isexistCustomer:"+query3);
			stmt = connection.prepareStatement(query3,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					check = true;
				}
			}
		} catch (SQLException e) {
			Util.logger.error("isexistCustomer : Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error("isexistCustomer: Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}
		return check;
	}
	
	
	public static void deleteCustomer(String user_id){
		Connection connection = null;
		DBPool dbpool = new DBPool();
		
		String sqlUpdateMlistUser = "delete FROM customer_cancel WHERE pid = '"+getPid(user_id)+"' and msisdn ='"+user_id+"' ";

		Util.logger
				.info("DBUtil@deleteCustomer insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnection("euro_report");

			DBUtil.executeSQL("euro_report", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("deleteCustomer@:move customer to customer_cancel, user_id="
							+ user_id
							+" Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}
	public static void MoveCustomerCancel2Customer(String user_id) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		
		String sqlUpdateMlistUser = "INSERT INTO customer(msisdn,LastQuestionId,SubscriptionType," +
				"TotalRepeatFail,CreateDate"
				+",CustomerLevel,LastUpdate,pid,QuestionSended,CurrentScore,DiemChuaSinhMDT,DiemDaSinhMDT,TotalMO"
				+",TotalScore,TotalMOByDay)"
				 +"SELECT msisdn,LastQuestionId,SubscriptionType,TotalRepeatFail,CreateDate"
				+",CustomerLevel,LastUpdate,pid,QuestionSended,CurrentScore,DiemChuaSinhMDT,DiemDaSinhMDT,0"
				+",0,TotalMOByDay "
				 +"FROM customer_cancel WHERE pid = '"+getPid(user_id)+"' and msisdn ='"+user_id+"' ";

		Util.logger
				.info("DBUtil@MoveCustomer2Cancel insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnection("euro_report");

			DBUtil.executeSQL("euro_report", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("MoveCustomer2Cancel@:move customer to customer_cancel, user_id="
							+ user_id
							+" Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}
	
	public static boolean ResetScore(String userid) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("euro_report");
			if (connection == null) {
				Util.logger.error("DBUtil@Update@connection is null.");
				return false;
			}
			String strUpdate = "UPDATE customer SET TotalScore  = '0' WHERE  pid = '"+getPid(userid)+"' and  MSISDN = '"+userid+"' ";
			Util.logger.info("ResetScore @sql:"+strUpdate);
			statement = connection.prepareStatement(strUpdate);
			if (statement.execute()) {
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error("DBUtil@Update@: Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error("DBUtil@Update@: Error:" + e.toString());
			return false;
		} finally {
		
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
	public static int insertCustomer(String msisdn,String tableName)
	{
		
		DBPool dbpool = new DBPool();
		Connection cnn=null;
		PreparedStatement stmt=null;
		int result=1;
		try
		{
			cnn=dbpool.getConnection("euro_report");
			String sqlInsert="insert into " +tableName+" (MSISDN,Pid" +
					") values ('"+msisdn+"',"+getPid(msisdn)+")"; 
			Util.logger.info("insertCustomer : "+sqlInsert);
			stmt=cnn.prepareStatement(sqlInsert);
			stmt.execute();
					
		}
		catch(Exception ex)
		{
			Util.logger.error("Error at @InsertCustomer :"+ex.getMessage());
			result=-1;
		}
		finally
		{
			dbpool.cleanup(stmt);
			dbpool.cleanup(cnn);
		}
		return result;
	}
	public static int getPid(String msisdn)
	{
		int numberPid=0;
		String pid="";
		if(msisdn.length()==11) pid=msisdn.substring(4,6);
		else
			pid=msisdn.substring(5,7);
		try
		{
			numberPid=Integer.parseInt(pid);
		}
		catch(Exception ex)
		{
			Util.logger.error("Error at Convert string to interger @getCustomer :"+ex.getMessage());
		}
		return numberPid;
	}
	public static void MoveCustomer2Cancel(String user_id) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		
		String sqlUpdateMlistUser = "INSERT INTO customer_cancel(msisdn,LastQuestionId,SubscriptionType," +
				"TotalRepeatFail,CreateDate"
				+",CustomerLevel,LastUpdate,pid,QuestionSended,CurrentScore,DiemChuaSinhMDT,DiemDaSinhMDT,TotalMO"
				+",TotalScore,TotalMOByDay)"
				 +"SELECT msisdn,LastQuestionId,SubscriptionType,TotalRepeatFail,CreateDate"
				+",CustomerLevel,LastUpdate,pid,QuestionSended,CurrentScore,DiemChuaSinhMDT,DiemDaSinhMDT,TotalMO"
				+",TotalScore,TotalMOByDay "
				 +"FROM customer WHERE pid = '"+getPid(user_id)+"' and  msisdn ='"+user_id+"'";

		Util.logger
				.info("DBUtil@MoveCustomer2Cancel insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnection("euro_report");

			DBUtil.executeSQL("euro_report", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("MoveCustomer2Cancel@:move customer to customer_cancel, user_id="
							+ user_id
							+" Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}
}
