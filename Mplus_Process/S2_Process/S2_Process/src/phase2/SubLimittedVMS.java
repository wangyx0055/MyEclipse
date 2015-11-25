package phase2;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

public class SubLimittedVMS extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {		
		
		msgObject.setSubCP(0); // 0: VMS ; other: ICOM
		
		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		_option = Util.getParametersAsString(options);
		String MLIST = Util.getStringfromHashMap(_option, "mlist", "");
		
		MLIST = MLIST + "_cancel";
		updateMTCount(MLIST, msgObject.getUserid(), msgObject.getCommandCode());
		
		return DBUtil.RegisterServices(msgObject, keyword,Constants.TYPE_OF_SERVICE_TEXTBASE, services);
	}
	
	private int updateMTCount(String tableMlist, String userId, String commandCode){
		
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET MT_COUNT = 0 "
				+ " WHERE COMMAND_CODE = '" + commandCode + "' AND USER_ID = '" + userId + "'";

		Util.logger.info("SubLimittedVMS updateMTCount @@@ SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("SubLimittedVMS updateMTCount@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("SubLimittedVMS updateMTCount@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return ireturn;
	}
	
	

}