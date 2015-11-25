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
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.DBUtils;

public class InvalidMO extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		String info = "[QC ICM2]" + "\nBan nhan tin nhan sai cu phap"
				+ "\n*Tuong thuat TT KQXS: XS<matinh> gui 8551"
				+ "\n*Cai dat GPRS tu dong: GPRS gui 8751"
				+ "\n*Tham gia Sieu Toc: AC gui 8551";

		if (checkblacklist(msgObject.getUserid())) {
			info = "Ban da nhan tin sai cu phap";
		}

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

			String query3 = "select * from [Spammer].[dbo].[BlackList] where Name='"
					+ sname + "'";

			Vector result3 = DBUtils.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}
}
