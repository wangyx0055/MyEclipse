package services;

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

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.Sender;
import icom.common.Util;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import DAO.MListDAO;
import DTO.MlistInfoDTO;

public class UnSubDauGia extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		Util.logger.info("Starting Handle MO with user= "
				+ msgObject.getUserid() + ", info=" + msgObject.getUsertext());
		try {

			// xac dinh mlist
			HashMap _option = Util.getParametersAsString(keyword.getOptions());
			String MLIST = "x";
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");
			String dbContent = Util.getStringfromHashMap(_option, "dbcontent",
					"x");
			if ("x".equalsIgnoreCase(MLIST) || "x".equalsIgnoreCase(dbContent)) {

				Util.logger
						.error("Khong xac dinh duoc MLIst hoac dbcontent duoc get ra tu param options:"
								+ " "
								+ msgObject.getUserid()
								+ ","
								+ keyword.getOptions());
				return null;
			} else {
				msgObject.setCommandCode(keyword.getService_ss_id());
				// kiem tra ton tai trong tap mlist
				if (!MListDAO.checkMlist(MLIST, msgObject.getUserid(),
						dbContent)) {
					msgObject.setUsertext(keyword.getWarMsg());
					Sender.msgPushMTQueue.add(msgObject);

				} else {
					// move sang mlist_cancel
					MlistInfoDTO mlist = MListDAO.getUserDauGia(MLIST, msgObject
							.getUserid(), dbContent);
					MListDAO.insertMListDGTichDiem(mlist, MLIST + "_cancel",
							dbContent);
					// sau do huy trong cac mlist va mlist subcriber
					MListDAO.deleteMlist(MLIST, msgObject.getUserid(),
							dbContent);
					// insert vao mlist_subcriber_cancel
					MListDAO.InsertSubcriber("mlist_subcriber_cancel",
							msgObject);
					MListDAO.deleteMlist("mlist_subcriber", msgObject
							.getUserid(), "gateway");
					// thong bao huy thanh cong toi khach hang
					msgObject.setUsertext(keyword.getUnsubMsg());
					Sender.msgPushMTQueue.add(msgObject);

				}
			}

		} catch (Exception ex) {
			Util.logger.error("@Error at class sub mohandles: "
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
		}
		Util.logger.info("End  Handle MO with user " + msgObject.getUserid());

		return null;
	}
}
