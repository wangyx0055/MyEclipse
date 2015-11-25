package packet.responseMT;

import icom.Constants;
import icom.LoadConfig;
import icom.DBPool;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.sql.Connection;
import java.util.Collection;

import sub.DeliveryManager;

public class PacketInfoServicesTest extends  DeliveryManager  {

	@Override
	protected Collection getMessages(String services, String option,
			String servicename, int notcharge) throws Exception {
		
		return null;
	}

}
