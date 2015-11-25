package mtPush;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import mtPush.PushMTConstants;

public class RejectAdvertise extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		MTPushCommon commonObj = new MTPushCommon();
		
		int numberTable = PushMTConstants.getNumberMTPushTable() + 1;

		int result = 1;
		for (int j = 0; j < PushMTConstants.PUSH_MT_SERVICE.length; j++) {

			String tableMTpush = PushMTConstants.TABLE_MT_PUSH + "_" +
						PushMTConstants.PUSH_MT_SERVICE[j];
			tableMTpush = tableMTpush.toLowerCase();
			
			for (int i = 0; i < numberTable; i++) {
				int id = commonObj.isExistInMTPush(tableMTpush, msgObject.getUserid());
				if (id > 0) {
					result = deleteById(id, tableMTpush);
					break;
				} else {
					
//					int check = i + 1;
//					tableMTpush = PushMTConstants.TABLE_MT_PUSH 
//								+ PushMTConstants.PUSH_MT_SERVICE[j].toLowerCase().trim() + check;
//					if (i > 0) {
//						tableMTpush = PushMTConstants.TABLE_MT_PUSH 
//							+ PushMTConstants.PUSH_MT_SERVICE[j].toLowerCase() + i;
//					} else {
//						tableMTpush = PushMTConstants.TABLE_MT_PUSH 
//							+ PushMTConstants.PUSH_MT_SERVICE[j].toLowerCase() + "1";
//					}
				}
			}

		}

		if (result == 1) {
			msgObject.setUsertext(keyword.getSubMsg());
		} else {
			msgObject.setUsertext(keyword.getUnsubMsg());
		}

		ArrayList<MsgObject> arrMsg = new ArrayList<MsgObject>();

		arrMsg.add(msgObject);
		return arrMsg;
	}

	
	public int deleteById(int id, String tableName) {
		int ireturn = 1;

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "DELETE FROM " + tableName + " WHERE ID = " + id;

		Util.logger.info("TC QUANG CAO @@@ deleteById: SQL = " + sqlUpdate);

		try {

			connection = dbpool.getConnection(PushMTConstants.pushMTPool);

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("TC QC @@@ deleteById: table Name =  "
					+ tableName + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}

		return ireturn;
	}
}
