package spam;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.MsgObject;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public abstract class SpamQM {

	public boolean isSpam(MsgObject msgObject, String autoreply, String mtspam,
			String msgtype) throws Exception {
		try {

			String[] bret = new String[2];
			bret = checkspam(msgObject);
			if (bret[0].equalsIgnoreCase("1")) {

				return true;
			}
			return false;
		} catch (Exception e) {
			Util.logger.info("SpamQM@start:" + msgObject.getUserid() + "@TO"
					+ msgObject.getServiceid() + "@" + msgObject.getUsertext()
					+ ": LOST MESSAGE" + e.toString());

		}
		return false;

	}

	protected abstract String[] checkspam(MsgObject msgObject) throws Exception;

}
