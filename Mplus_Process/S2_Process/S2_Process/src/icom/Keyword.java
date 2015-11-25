package icom;

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

import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

public class Keyword {

	// private final static String tablekeyword = "keywords";
	// Ten dich vu
	private String service_ss_id = "";
	// Dau so
	private String serviceid;
	// Keyword
	private String keyword = Constants.INV_KEYWORD;
	// Class xu ly
	private String class_mo = Constants.INV_MO_CLASS;
	private String class_mt = Constants.INV_MT_CLASS;
	
	
	// Ten doi tac
	private String partner_mo = Constants.INV_PARTNER;
	private String partner_mt = Constants.INV_PARTNER;
	private String options;
	private int cpmo = 0;
	private int cpmt = 0;

	private long duration = 0;
	private int service_type = 0;
	private long amount = 0;
	
	//submessage, unsubmessage, warningmessage, errormessage, existmessage,
	private String subMsg;
	private String unsubMsg;
	private String warMsg;
	private String errMsg;
	private String existMsg;
	private String promoMsg;
	private String mtSupersim;
	private int isIcom;
	public int getIsIcom() {
		return isIcom;
	}

	public void setIsIcom(int isIcom) {
		this.isIcom = isIcom;
	}

	public String getMtSupersim() {
		return mtSupersim;
	}

	public void setMtSupersim(String mtSupersim) {
		this.mtSupersim = mtSupersim;
	}

	// S2- charge_package
	private String notEnoughMoneyMsg;
	//

	public String getPromoMsg() {
		return promoMsg;
	}

	public void setPromoMsg(String promoMsg) {
		this.promoMsg = promoMsg;
	}

	public String getSubMsg() {
		return subMsg;
	}

	public void setSubMsg(String subMsg) {
		this.subMsg = subMsg;
	}

	public String getUnsubMsg() {
		return unsubMsg;
	}

	public void setUnsubMsg(String unsubMsg) {
		this.unsubMsg = unsubMsg;
	}

	public String getWarMsg() {
		return warMsg;
	}

	public void setWarMsg(String warMsg) {
		this.warMsg = warMsg;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getExistMsg() {
		return existMsg;
	}

	public void setExistMsg(String existMsg) {
		this.existMsg = existMsg;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getService_type() {
		return service_type;
	}

	public void setService_type(int service_type) {
		this.service_type = service_type;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getServiceid() {
		return serviceid;
	}

	public String getOptions() {
		return options;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getService_ss_id() {
		return service_ss_id;
	}

	public void setService_ss_id(String service_ss_id) {
		this.service_ss_id = service_ss_id;
	}
	
	public String getNotEnoughMoneyMsg(){
		return this.notEnoughMoneyMsg;
	}
	
	public void setNotEnoughMoneyMsg(String noMoneyMsg){
		this.notEnoughMoneyMsg = noMoneyMsg;
	}

	@SuppressWarnings("unchecked")
	public static Hashtable retrieveKeyword() throws Exception {

		String query = "select is_icom, keyword, service_id,a.cpname mo, b.cpname mt,cp_mo,cp_mt,class_mo,class_mt,service_type,duration,amount,options,service_name,submessage, unsubmessage, warningmessage, errormessage, existmessage,promomessage, not_enough_money from "
				+ Constants.tblKeyword
				+ " , cp a, cp b  where cp_mo=a.cpid and cp_mt=b.cpid order by length(keyword) desc, keyword asc ";

		Util.logger.info("retrieveKeyword:" + query);

		DBPool dbpool = new DBPool();
		Hashtable keywords = new Hashtable();
		Vector vtkeywords = new Vector();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			connection = dbpool.getConnectionGateway();

			stmt = connection.prepareStatement(query);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					Keyword keywordtemp = new Keyword();
					keywordtemp.serviceid = rs.getString("service_id");
					keywordtemp.keyword = rs.getString("keyword");
					keywordtemp.partner_mo = rs.getString("mo");
					keywordtemp.partner_mt = rs.getString("mt");
					keywordtemp.cpmo = rs.getInt("cp_mo");
					keywordtemp.cpmt = rs.getInt("cp_mt");
					keywordtemp.class_mo = rs.getString("class_mo");
					keywordtemp.class_mt = rs.getString("class_mt");
					// keyword, service_id,a.cpname mo, b.cpname mt,cp_mo,cp_mt,
					// class_mo,class_mt,service_type,duration,amount
					keywordtemp.service_type = rs.getInt("service_type");
					keywordtemp.duration = rs.getLong("duration");
					keywordtemp.amount = rs.getLong("amount");
					keywordtemp.options = rs.getString("options");
					keywordtemp.service_ss_id = rs.getString("service_name").trim();
					
					//submessage, unsubmessage, warningmessage, errormessage, existmessage,
					keywordtemp.subMsg = rs.getString("submessage");
					keywordtemp.unsubMsg = rs.getString("unsubmessage");
					keywordtemp.warMsg = rs.getString("warningmessage");
					keywordtemp.errMsg = rs.getString("errormessage");
					keywordtemp.existMsg = rs.getString("existmessage");
					keywordtemp.promoMsg = rs.getString("promomessage");
					keywordtemp.notEnoughMoneyMsg = rs.getString("not_enough_money");
					keywordtemp.isIcom = rs.getInt("is_icom");
					// keywordtemp
					// keywordtemp.amount = rs.getLong("amount");
					
					keywords.put(keywordtemp.serviceid + "@"
							+ keywordtemp.keyword, keywordtemp);
					vtkeywords.addElement(keywordtemp.serviceid + "@"
							+ keywordtemp.keyword);

				}
			}
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

			dbpool.cleanup(connection);

		} catch (SQLException ex2) {
			Util.logger.error("Load Keyword. Ex:" + ex2.toString());
			DBUtil.Alert("Process.LoadKeyword", "LoadKeyword.SQLException",
					"major", "LoadMO.SQLException:" + ex2.toString(),
					"processAdmin");

		} catch (Exception ex3) {
			Util.logger.error("Load Keyword. Ex3:" + ex3.toString());
			DBUtil.Alert("Process.LoadKeyword", "LoadKeyword.Exception",
					"major", "LoadMO.Exception:" + ex3.toString(),
					"processAdmin");

		}

		finally {
			dbpool.cleanup(connection);
		}

		Sender.loadconfig.vtKeyword = vtkeywords;
		return keywords;
	}

	@SuppressWarnings("unchecked")
	public static Hashtable retrievePrice() throws Exception {

		String query = "select service_ss_id, price_new, mt_free from billing where begin_time <= current_timestamp() and end_time >= current_timestamp() ";

		Util.logger.info("retrievePrice:" + query);

		DBPool dbpool = new DBPool();
		Hashtable keywords = new Hashtable();
		Vector vtkeywords = new Vector();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			connection = dbpool.getConnectionGateway();

			stmt = connection.prepareStatement(query);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					Keyword keywordtemp = new Keyword();
					keywordtemp.serviceid = rs.getString("service_id");
					keywordtemp.keyword = rs.getString("keyword");
					keywordtemp.partner_mo = rs.getString("mo");
					keywordtemp.partner_mt = rs.getString("mt");
					keywordtemp.cpmo = rs.getInt("cp_mo");
					keywordtemp.cpmt = rs.getInt("cp_mt");
					keywordtemp.class_mo = rs.getString("class_mo");
					keywordtemp.class_mt = rs.getString("class_mt");
					// keyword, service_id,a.cpname mo, b.cpname mt,cp_mo,cp_mt,
					// class_mo,class_mt,service_type,duration,amount
					keywordtemp.service_type = rs.getInt("service_type");
					keywordtemp.duration = rs.getLong("duration");
					keywordtemp.amount = rs.getLong("amount");
					keywordtemp.options = rs.getString("options");
					keywords.put(keywordtemp.serviceid + "@"
							+ keywordtemp.keyword, keywordtemp);
					vtkeywords.addElement(keywordtemp.serviceid + "@"
							+ keywordtemp.keyword);

				}
			}
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

			dbpool.cleanup(connection);

		} catch (SQLException ex2) {
			Util.logger.error("Load Keyword. Ex:" + ex2.toString());
			DBUtil.Alert("Process.LoadKeyword", "LoadKeyword.SQLException",
					"major", "LoadMO.SQLException:" + ex2.toString(),
					"processAdmin");

		} catch (Exception ex3) {
			Util.logger.error("Load Keyword. Ex3:" + ex3.toString());
			DBUtil.Alert("Process.LoadKeyword", "LoadKeyword.Exception",
					"major", "LoadMO.Exception:" + ex3.toString(),
					"processAdmin");

		}

		finally {
			dbpool.cleanup(connection);
		}

		Sender.loadconfig.vtKeyword = vtkeywords;
		return keywords;
	}

	public String getPartner_mo() {
		return partner_mo;
	}

	public void setPartner_mo(String partner_mo) {
		this.partner_mo = partner_mo;
	}

	public String getPartner_mt() {
		return partner_mt;
	}

	public void setPartner_mt(String partner_mt) {
		this.partner_mt = partner_mt;
	}

	public int getCpmo() {
		return cpmo;
	}

	public void setCpmo(int cpmo) {
		this.cpmo = cpmo;
	}

	public int getCpmt() {
		return cpmt;
	}

	public void setCpmt(int cpmt) {
		this.cpmt = cpmt;
	}

	public String getClass_mo() {
		return class_mo;
	}

	public void setClass_mo(String class_mo) {
		this.class_mo = class_mo;
	}

	public String getClass_mt() {
		return class_mt;
	}

	public void setClass_mt(String class_mt) {
		this.class_mt = class_mt;
	}

}
