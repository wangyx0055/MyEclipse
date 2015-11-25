package services.textbases;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class HuyQuangCao extends ContentAbstract {

	public HuyQuangCao() {

	}

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		MsgObject mt = msgObject;
		String sUserid = msgObject.getUserid();
		String sMTReturn = "Yeu cau Huy nhan quang cao cua quy khach da duoc thuc hien. Tin nhan HUY la tin nhan khong tinh phi dich vu. DTHT: 043.556.1776";
		updaterequest(sUserid, "Mobile", "00000");

		updatesendlog(msgObject.getServiceid(), sUserid, sMTReturn);
		mt.setUsertext(sMTReturn);
		mt.setMsgtype(2);
		messages.add(new MsgObject(mt));
		return messages;
	}

	public void updatesendlog(String serviceid, String userid, String message) {
		PreparedStatement stmt = null;
		Connection conn = null;
		String strQuery = "";

		DBPool dbPool = new DBPool();

		try {
			conn = (Connection) dbPool.getConnection("content");

			strQuery = "insert into [Spammer].[dbo].[SMSQueue_Log]([AId],[Sender] ,[Receiver],[Detail] ,[Created_At],[Send_At],[Sent_At]) values(?,?,?,?,getdate(),getdate(),getdate())";
			// Util.logger.info(strQuery + code);
			stmt = conn.prepareStatement(strQuery);
			stmt.setString(1, "0");
			stmt.setString(2, serviceid);
			stmt.setString(3, userid);
			stmt.setString(4, message);

			stmt.executeUpdate();

		} catch (SQLException e) {
			Util.logger.info(strQuery + e.toString());
		} finally {
			dbPool.cleanup(stmt);
			dbPool.cleanup(conn);
		}

	}

	private static boolean updaterequest(String sname, String stype,
			String scode) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("content");

			String sqlInsert = "insert into [Spammer].[dbo].[BlackList](Name,Type,Detail,Created_At) values('"
					+ sname + "','" + stype + "','Cancel by sms',getdate())";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() == 1)
				return true;
			Util.logger.error("Update in to SpamList");

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return true;
	}
}