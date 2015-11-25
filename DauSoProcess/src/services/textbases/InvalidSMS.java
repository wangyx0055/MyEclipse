package services.textbases;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class InvalidSMS extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String info = "Tin nhan sai cu phap"
				+ "\n*Tuong thuat TT KQXS: XS<matinh> gui 8551"
				+ "\n*Cai dat GPRS tu dong: GPRS gui 8751"
				+ "\n*Suu tam OMNIA: TE gui 8551"
				+ "\n*Tham gia Sieu Toc: AC gui 8551";
		msgObject.setUsertext(info);
		msgObject.setKeyword(Constants.INV_KEYWORD);
		msgObject.setMsgtype(1);

		messages.add(new MsgObject(msgObject));

		return messages;
	}
	private static boolean checkblacklist(String sname) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("content");

			String sqlInsert = "select count(*) from [Spammer].[dbo].[BlackList] where Name='"
					+ sname + "'";
			//Util.logger.warn("detete:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() == 1)
				return true;

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
