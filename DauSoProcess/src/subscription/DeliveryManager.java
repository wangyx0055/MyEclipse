package subscription;

import com.vmg.sms.common.Util;

import java.util.Collection;

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
public abstract class DeliveryManager {

	public int start(String id, String services, String option, int notcharge)
			throws Exception {
		try {
			Util.logger.info("DeliveryManager@start:" + services + "");
			Collection messages = getMessages(id, option, services, notcharge);

			Util.logger.info("DeliveryManager@start:" + services + "  ok");

		} catch (Exception e) {
			Util.logger.info("DeliveryManager@start:" + services + "@"
					+ e.toString());
			Util.logger.printStackTrace(e);

		}
		return 1;

	}

	protected abstract Collection getMessages(String services, String option,
			String servicename, int notcharge) throws Exception;

}
