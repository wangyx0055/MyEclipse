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

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class SubXoso extends QuestionManager {

	public final static int MIENNAM_COMPANY_ID = 901;
	public final static int MIENTRUNG_COMPANY_ID = 902;

	// Sounth Area
	public final static String HANOI_COMPANY_ID = "1";
	public final static String BINHDUONG_COMPANY_ID = "11";
	public final static String SAIGON_COMPANY_ID = "131";
	public final static String VUNGTAU_COMPANY_ID = "81";
	public final static String BENTRE_COMPANY_ID = "71";
	public final static String BACLIEU_COMPANY_ID = "91";
	public final static String DONGNAI_COMPANY_ID = "41";
	public final static String CANTHO_COMPANY_ID = "51";
	public final static String SOCTRANG_COMPANY_ID = "61";
	public final static String VINHLONG_COMPANY_ID = "21";
	public final static String TRAVINH_COMPANY_ID = "31";
	public final static String LONGAN_COMPANY_ID = "201";
	public final static String BINHPHUOC_COMPANY_ID = "191";
	public final static String DALAT_COMPANY_ID = "181";
	public final static String KONTUM_COMPANY_ID = "211";
	public final static String KIENGIANG_COMPANY_ID = "171";
	public final static String TIENGIANG_COMPANY_ID = "161";
	public final static String DONGTHAP_COMPANY_ID = "141";
	public final static String CAMAU_COMPANY_ID = "151";
	public final static String BINHTHUAN_COMPANY_ID = "101";
	public final static String TAYNINH_COMPANY_ID = "111";
	public final static String ANGIANG_COMPANY_ID = "121";
	public final static String HAUGIANG_COMPANY_ID = "231";
	public final static String NINHTHUAN_COMPANY_ID = "241";
	public final static String KHANHHOA_COMPANY_ID = "251";
	public final static String DANANG_COMPANY_ID = "261";
	public final static String BINHDINH_COMPANY_ID = "281";
	public final static String DAKLAK_COMPANY_ID = "271";
	public final static String GIALAI_COMPANY_ID = "221";
	public final static String PHUYEN_COMPANY_ID = "291";
	public final static String QUANGNAM_COMPANY_ID = "301";
	public final static String QUANGNGAI_COMPANY_ID = "311";
	public final static String DACNONG_COMPANY_ID = "321";
	public final static String QUANGBINH_COMPANY_ID = "341";
	public final static String QUANGTRI_COMPANY_ID = "351";
	public final static String HUE_COMPANY_ID = "331";
	public final static String BINHDUONG6_COMPANY_ID = "361";

	// Mien Trung 902
	public final static String[] middleCompanies = { BINHDINH_COMPANY_ID,
			DAKLAK_COMPANY_ID, QUANGNGAI_COMPANY_ID, QUANGNAM_COMPANY_ID,
			HUE_COMPANY_ID, KHANHHOA_COMPANY_ID, DANANG_COMPANY_ID,
			QUANGBINH_COMPANY_ID, QUANGTRI_COMPANY_ID, KONTUM_COMPANY_ID,
			DACNONG_COMPANY_ID, NINHTHUAN_COMPANY_ID, GIALAI_COMPANY_ID,
			PHUYEN_COMPANY_ID };

	public final static String[] middleNames = { "BDI", "DLK", "QNG", "QNA",
			"TTH", "KH", "DNA", "QB", "QT", "KT", "DNO", "NT", "GL", "PY" };

	// Mien Nam 901
	public final static String[] southCompanies = { VINHLONG_COMPANY_ID,
			TRAVINH_COMPANY_ID, DONGTHAP_COMPANY_ID, CAMAU_COMPANY_ID,
			BINHTHUAN_COMPANY_ID, VUNGTAU_COMPANY_ID, BACLIEU_COMPANY_ID,
			CANTHO_COMPANY_ID, SOCTRANG_COMPANY_ID, BENTRE_COMPANY_ID,
			TAYNINH_COMPANY_ID, ANGIANG_COMPANY_ID, SAIGON_COMPANY_ID,
			LONGAN_COMPANY_ID, BINHPHUOC_COMPANY_ID, TIENGIANG_COMPANY_ID,
			KIENGIANG_COMPANY_ID, HAUGIANG_COMPANY_ID, DALAT_COMPANY_ID,
			BINHDUONG_COMPANY_ID, DONGNAI_COMPANY_ID };

	public final static String[] southNames = { "VL", "TV", "DT", "CM", "BTH",
			"VT", "BL", "CT", "ST", "BT", "TN", "AG", "HCM", "LA", "BP", "TG",
			"KG", "HG", "DL", "BD", "DN" };

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {
		/****
		 * Set subCP=1: vi la dich vu tu ICOM
		 * **/
		msgObject.setSubCP(1);
		return DBUtil.RegisterServices(msgObject, keyword,
				Constants.TYPE_OF_SERVICE_CAU_XS,services);
	}
}
