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
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.DBUtils;
import cs.ExecuteADVCR;

public class Nhaccho extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			Collection messages = new ArrayList();

			String info = "Cu phap khong hop le. DT ho tro: 04-35561862";

			String infoViettel = "DK su dung,soan DK gui1221."
				+"\n Buoc 2:Chon bai hat, soan:BH maso gui 1221"
					
					+ "6331:1 ngay co bao nhieu nam";

			String infoMobiFone = "Dang ky nhac cho MobiFone: DK gui 9224. Chon bai hat nhac cho soan: CHON  maso   gui 9224"
					+ "\n"
					+ "Tai bo hinh nen My Nhan soan: HINH  mynhan gui 8751";

			String infoVinaPhone = "Dang ky nhac cho VinaPhone: DK gui 9194. Chon bai hat nhac cho soan: TUNE  maso  gui 9194. Tai bo hinh nen cho My Nhan, soan: HINH  mynhan gui 8751";

			String infoOther = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau. Cai GPRS tu dong soan: GPRS gui 8751. Tai bo hinh nen my nhan soan HINH  mynhan  gui 8751";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);

			infoViettel = getStringfromHashMap(_option, "viettel", infoViettel);
			infoMobiFone = getStringfromHashMap(_option, "mobifone",
					infoMobiFone);
			infoVinaPhone = getStringfromHashMap(_option, "vinaphone",
					infoVinaPhone);
			infoOther = getStringfromHashMap(_option, "other", infoOther);

			if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {
				info = infoViettel;
			} else if ("VMS".equalsIgnoreCase(msgObject.getMobileoperator())) {
				info = infoMobiFone;

			} else if ("GPC".equalsIgnoreCase(msgObject.getMobileoperator())) {
				info = infoVinaPhone;

			} else {
				info = infoOther;
			}

			msgObject.setUsertext(info);

			msgObject.setMsgtype(1);

			messages.add(new MsgObject(msgObject));

			return messages;

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());

			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
	}

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));

			if (temp == null) {
				return _defaultval;
			}
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return _defaultval;
			}
			return temp;
		} catch (Exception e) {
			// TODO: handle exception
			return _defaultval;
		}
	}

	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "&");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			// setParameter(key, value);
			_params.put(key, value);
		}

		return _params;

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
