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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import multiService.LoadMultiService;

import soap.ListSender;
import soap.MOSender;
import soap.SoapMoThienNgan;

public class MsgObject implements Serializable {

	int id = -1; // id in mlist
	
	String serviceid = "";
	String userid = "";
	String keyword = "";
	String usertext = "";
	BigDecimal requestid = new BigDecimal(-1);
	int channel_type= 0;
	Timestamp tTimes;
	String mobileoperator = "VMS";
	int msgtype = 1;
	int contenttype = 0;
	String msgnotes = "";
	int process_result = 0;
	int retries_num = 0;
	int cp_mo = 0;
	int cp_mt = 0;
	int objtype = 0;
	Timestamp tSubmitTime;
	Timestamp tDoneTime;
	long msg_id = 0;
	int content_id = 0;
	long amount = 0;
	int notcharge = 0;
	
	
	// Them ngay 17.08.2010
	String servicename = "";
	String command_code = "";
	String option = "";
	String company_id = "0";
	String last_code = "0";
	String hours = "0";
	String minutes = "0";

	// DanND Add
	String timeSendMO = "";// date is received MO
	String classSendMT = "";
	int mtCount = 0;
	int mtFree = -1;
	int duration = -1;
	int active = 0;
	int regCount = 0;
	int chargeResult = 0;
	int isIcom =0;
	public int getIsIcom() {
		return isIcom;
	}

	public void setIsIcom(int isIcom) {
		this.isIcom = isIcom;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
		
	public int getMtCount() {
		return mtCount;
	}

	public void setMtCount(int mtCount) {
		this.mtCount = mtCount;
	}
	
	public int getMtFree() {
		return mtFree;
	}

	public void setMtFree(int mtFree) {
		this.mtFree = mtFree;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
			
	public int getRegCount() {
		return regCount;
	}

	public void setRegCount(int regCount) {
		this.regCount = regCount;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public String getTimeSendMO() {
		return timeSendMO;
	}

	public void setTimeSendMO(String timeSendMO) {
		this.timeSendMO = timeSendMO;
	}


	BigDecimal moId = new BigDecimal(-1);
	
	public BigDecimal getMoId() {
		return moId;
	}

	public void setMoId(BigDecimal moId) {
		this.moId = moId;
	}


	int multiService = 0;
	
	/***
	 * 
	 * @return 1 - Dang ky nhieu services 1 luc <br/>
	 * 		   0 - Dang ky 1 dich vu
	 */
	public int getMultiService() {
		return multiService;
	}

	public void setMultiService(int multiService) {
		this.multiService = multiService;
	}


	/********
	 * 2010-11-08: PhuongDT
	 * sub_cp=0: dich vu ben vms
	 * sub_cp=1: dich vu tu doi tac khac
	 * sub_cp=2: dich vu ben VASC va la Multi Service // DanNd Add
	 * *****/
	int sub_cp = 0;
	public int getSubCP()
	{
		return this.sub_cp;
	}
		
	public void setSubCP(int subCP)
	{
		this.sub_cp = subCP;
	}
	

	private Boolean isPkgService = false;
	/****
	 *  <p>Kiem tra xem MO la dang ky theo ngay hay theo goi </p>
	 *  
	 *  isPkgService = true: Dich vu Dang ky theo goi <br/>
	 *  isPkgService = false: Dich vu Dang ky theo ngay <br/>
	 * @return Boolean Object <br/>
	 * @author DanND <br/>
	 * @Date	2011-01-11
	 */
	public Boolean getPkgService(){
		return isPkgService;
	}
	
	public void setPkgService(Boolean isPkgServiceValue){
		this.isPkgService = isPkgServiceValue;
	}
	
	private int chargingPackage = -1;
	/******
	 * 
	 * @return
	 * 		0: Da Dang Ky <br/>
	 * 		1: Dang ky Lan Dau Va trong thoi gian KM <br/>
	 * 		2: Het KM <br/>
	 * 		3: Dang ky lai nhung today < date retry
	 */
	public int getChargingPackage(){
		return this.chargingPackage;
	}
	
	public void setChargingPackage(int charge){
		this.chargingPackage = charge;
	}

	public String mlistTableName = "";
	public void setMlistTableName(String tableName){
		this.mlistTableName = tableName;
	}
	public String getMlistTableName(){
		return this.mlistTableName;
	}
	
	//************
	
	public int getContentId() {
		return content_id;
	}

	public void setContentId(int contentid) {
		this.content_id = contentid;
	}

	// Them ngay 18.08
	public String getServiceName() {
		return servicename;
	}
	public void setCommandCode(String command_code) {
		this.command_code = command_code;
	}

	public String getCommandCode() {
		return command_code;
	}
	public void setServiceName(String servicename) {
		this.servicename = servicename;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getLast_code() {
		return last_code;
	}

	public void setLast_code(String last_code) {
		this.last_code = last_code;
	}

	// ///////////////

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}
	public long getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(long msg_id) {
		this.msg_id = msg_id;
	}

	public Timestamp getTSubmitTime() {
		return tSubmitTime;
	}

	public void setTSubmitTime(Timestamp submitTime) {
		tSubmitTime = submitTime;
	}

	public Timestamp getTDoneTime() {
		return tDoneTime;
	}

	public void setTDoneTime(Timestamp doneTime) {
		tDoneTime = doneTime;
	}

	public int getCp_mo() {
		return cp_mo;
	}

	public void setCp_mo(int cp_mo) {
		this.cp_mo = cp_mo;
	}

	public int getCp_mt() {
		return cp_mt;
	}

	public void setCp_mt(int cp_mt) {
		this.cp_mt = cp_mt;
	}

	public MsgObject() {

	}

	public MsgObject(MsgObject msgobject) {

		this.serviceid = msgobject.serviceid;
		this.userid = msgobject.userid;
		this.keyword = msgobject.keyword;
		this.usertext = msgobject.usertext;
		this.requestid = msgobject.requestid;
		this.tTimes = msgobject.tTimes;
		this.mobileoperator = msgobject.mobileoperator;
		this.msgtype = msgobject.msgtype;
		this.contenttype = msgobject.contenttype;
		this.msgnotes = msgobject.msgnotes;
		this.process_result = msgobject.process_result;
		this.retries_num = msgobject.retries_num;
		this.cp_mo = msgobject.cp_mo;
		this.cp_mt = msgobject.cp_mt;
		this.objtype = msgobject.objtype;
		this.tSubmitTime = msgobject.tSubmitTime;
		this.tDoneTime = msgobject.tDoneTime;
		this.msg_id = msgobject.msg_id;
		this.content_id = msgobject.content_id;
		this.amount = msgobject.amount;
		this.sub_cp = msgobject.sub_cp;
		this.command_code = msgobject.command_code;
		this.amount = msgobject.amount;
		this.multiService = msgobject.multiService;
		this.servicename = msgobject.servicename;
		this.moId = msgobject.moId;
		this.timeSendMO = msgobject.timeSendMO;
	}
	// Them ngay 17.08.2010
	public MsgObject(int objtype, String sUser_Id,String sService_Id,String sMobile_Operator, String sCommand_Code,
			String sOption,String sMessage_Type,String sRequest_Id, String sMessage_Id, String sService_Name, String sChannel_Type,
			String sContent_Id,String sAmount, String sCompany_Id, String sLast_Code)
		{
		this.objtype = objtype;
		this.userid = sUser_Id;
		this.serviceid = sService_Id;
		this.mobileoperator = sMobile_Operator;
		this.command_code = sCommand_Code;
		this.option = sOption;
		this.msgtype = Integer.parseInt(sMessage_Type);
		this.requestid = new BigDecimal(sRequest_Id);
		this.msg_id =  Long.parseLong(sMessage_Id);
		this.servicename = sService_Name;
		this.channel_type = Integer.parseInt(sChannel_Type);
		this.content_id = Integer.parseInt(sContent_Id);
		this.amount = Long.parseLong(sAmount);
		this.company_id = sCompany_Id;
		this.last_code = sLast_Code;
	}

	public MsgObject(int objtype, String serviceid, String userid,
			String keyword, String usertext, BigDecimal requestid,
			Timestamp tTimes, String mobileoperator, int msgtype,
			int contenttype) {
		this.objtype = objtype;
		this.serviceid = serviceid;
		this.userid = userid;
		this.keyword = keyword;
		this.usertext = usertext;
		this.requestid = requestid;
		this.tTimes = tTimes;
		this.mobileoperator = mobileoperator;
		this.msgtype = msgtype;
		this.contenttype = contenttype;

	}

	// Cai nay moi tao hom 02/06/2010.
	public MsgObject(int objtype, String serviceid, String userid,
			String keyword, String usertext, BigDecimal requestid,
			Timestamp tTimes, String mobileoperator, int msgtype,
			int contenttype, long amount, int contentid) {
		this.objtype = objtype;
		this.serviceid = serviceid;
		this.userid = userid;
		this.keyword = keyword;
		this.usertext = usertext;
		this.requestid = requestid;
		this.tTimes = tTimes;
		this.mobileoperator = mobileoperator;
		this.msgtype = msgtype;
		this.contenttype = contenttype;
		// THem 2 truong nay vao
		this.amount = amount;
		this.content_id = contentid;
		this.command_code = keyword;
	}

	// cai nay dung cho bang
	public MsgObject(int objtype, String serviceid, String userid,
			String keyword, String usertext, BigDecimal requestid,
			Timestamp tTimes, String mobileoperator, int msgtype,
			int contenttype, int cpmo, int cpmt, String msgnotes) {
		this.objtype = objtype;
		this.serviceid = serviceid;
		this.userid = userid;
		this.keyword = keyword;
		this.usertext = usertext;
		this.requestid = requestid;
		this.tTimes = tTimes;
		this.mobileoperator = mobileoperator;
		this.msgtype = msgtype;
		this.contenttype = contenttype;
		this.cp_mo = cpmo;
		this.cp_mt = cpmt;
		this.msgnotes = msgnotes;

	}

	// cai nay dung cho bang _ cai nay moi lam hom 02-06-2010
	public MsgObject(int objtype, String serviceid, String userid,
			String keyword, String usertext, BigDecimal requestid,
			Timestamp tTimes, String mobileoperator, int msgtype,
			int contenttype, int cpmo, int cpmt, String msgnotes, long amount,
			int contentid) {
		this.objtype = objtype;
		this.serviceid = serviceid;
		this.userid = userid;
		this.keyword = keyword;
		this.usertext = usertext;
		this.requestid = requestid;
		this.tTimes = tTimes;
		this.mobileoperator = mobileoperator;
		this.msgtype = msgtype;
		this.contenttype = contenttype;
		this.cp_mo = cpmo;
		this.cp_mt = cpmt;
		this.msgnotes = msgnotes;
		this.amount = amount;
		this.content_id = contentid;

	}

	// Cai nay dung cho bang mt_queue
	public MsgObject(int objtype, String serviceid, String userid,
			String keyword, String usertext, BigDecimal requestid,
			Timestamp tTimes, String mobileoperator, int msgtype,
			int contenttype, int cpmo, int cpmt, String msgnotes,
			int process_result, int retries_num, long msgid) {

		this.objtype = objtype;
		this.serviceid = serviceid;
		this.userid = userid;
		this.keyword = keyword;
		this.usertext = usertext;
		this.requestid = requestid;
		this.tTimes = tTimes;
		this.mobileoperator = mobileoperator;
		this.msgtype = msgtype;
		this.contenttype = contenttype;
		this.cp_mo = cpmo;
		this.cp_mt = cpmt;
		this.msgnotes = msgnotes;
		this.process_result = process_result;
		this.retries_num = retries_num;
		this.msg_id = msgid;

	}

	// Cai nay dung cho bang nao?
	public MsgObject(int objtype, String serviceid, String userid,
			String keyword, String usertext, BigDecimal requestid,
			Timestamp tTimes, String mobileoperator, int msgtype,
			int contenttype, int cpmo, int cpmt, String msgnotes,
			int process_result, int retries_num, long msgid, long amount, int channelType, String commandCode) {
		this.objtype = objtype;
		this.serviceid = serviceid;
		this.userid = userid;
		this.keyword = keyword;
		this.usertext = usertext;
		this.requestid = requestid;
		this.tTimes = tTimes;
		this.mobileoperator = mobileoperator;
		this.msgtype = msgtype;
		this.contenttype = contenttype;
		this.cp_mo = cpmo;
		this.cp_mt = cpmt;
		this.msgnotes = msgnotes;
		this.process_result = process_result;
		this.retries_num = retries_num;
		this.msg_id = msgid;
		this.amount = amount;
		this.channel_type = channelType;
		this.command_code = commandCode;
	}

	public MsgObject(int objtype, String serviceid, String userid,
			String keyword, String usertext, BigDecimal requestid,
			Timestamp tTimes, String mobileoperator, int msgtype,
			int contenttype, int cpmo, int cpmt, String msgnotes,
			int process_result, int retries_num, String partnermo,
			String partnermt) {
		this.objtype = objtype;
		this.serviceid = serviceid;
		this.userid = userid;
		this.keyword = keyword;
		this.usertext = usertext;
		this.requestid = requestid;
		this.tTimes = tTimes;
		this.mobileoperator = mobileoperator;
		this.msgtype = msgtype;
		this.contenttype = contenttype;
		this.cp_mo = cpmo;
		this.cp_mt = cpmt;
		this.msgnotes = msgnotes;
		this.process_result = process_result;
		this.retries_num = retries_num;

	}

	public String getServiceid() {
		return serviceid;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}

	public String getUserid() {
		return userid;
	}

	public String getUseridEx() {
		if (userid.startsWith("84"))
			return "0" + userid.substring(2);

		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getMsgnotes() {
		return msgnotes;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getUsertext() {
		return usertext;
	}

	public void setUsertext(String usertext) {
		this.usertext = usertext;
	}

	public BigDecimal getRequestid() {
		return requestid;
	}
	public int getChannelType() {
		return channel_type;
	}

	public void setRequestid(BigDecimal requestid) {
		this.requestid = requestid;
	}
	public void setChannelType(int channelType) {
		this.channel_type= channelType;
	}

	public Timestamp getTTimes() {
		return tTimes;
	}

	public void setTTimes(Timestamp times) {
		tTimes = times;
	}

	public String getMobileoperator() {
		return mobileoperator;
	}

	public void setMobileoperator(String mobileoperator) {
		this.mobileoperator = mobileoperator;
	}

	public int getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(int msgtype) {
		this.msgtype = msgtype;
	}

	public int getContenttype() {
		return contenttype;
	}

	public void setContenttype(int contenttype) {
		this.contenttype = contenttype;
	}

	public void setMsgNotes(String msgnotes) {
		if ("null".equalsIgnoreCase(this.msgnotes) || "".equals(this.msgnotes)
				|| this.msgnotes == null) {
			this.msgnotes = msgnotes;
		} else {
			this.msgnotes = this.msgnotes + "@" + msgnotes;
		}

	}

	public void setMsgNotes(int type, String msgnotes) {
		if (type == 0) {
			this.msgnotes = msgnotes;

		} else {
			if ("null".equalsIgnoreCase(this.msgnotes)
					|| "".equals(this.msgnotes) || this.msgnotes == null) {
				this.msgnotes = msgnotes;
			} else {
				this.msgnotes = this.msgnotes + "@" + msgnotes;
			}
		}

	}

	public void setMsgnotes(String msgnotes) {
		this.msgnotes = msgnotes;
	}

	public int getProcess_result() {
		return process_result;
	}

	public void setProcess_result(int process_result) {
		this.process_result = process_result;
	}

	public int getRetries_num() {
		return retries_num;
	}

	public void setRetries_num(int retries_num) {
		this.retries_num = retries_num;
	}

	public String sendMessageMO(String textMessage, String partner,
			String commandcode) throws Exception {

		String url = Constants._prop.getProperty("mo.soap." + partner + ".url");
		String username = Constants._prop.getProperty("mo.soap." + partner
				+ ".username");
		String password = Constants._prop.getProperty("mo.soap." + partner
				+ ".password");
		//password = IcomMD5.MD5Passwd(password);
		
		String action = Constants._prop.getProperty("mo.soap." + partner
				+ ".action");
		String module = Constants._prop.getProperty("mo.soap." + partner
				+ ".module");
		String encode = Constants._prop.getProperty("mo.soap." + partner
				+ ".encode");

		if (url == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".url");

		if (module == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".module");
		soap.MOSender sender = null;
		
		sender = (soap.MOSender) Class.forName(module)
				.newInstance();
		String result = "-2";
		try{
			result = sender.sendMO(url, username, password, action, this, textMessage, commandcode, encode);
		}catch(Exception ex)
		{
			Util.logger.error("ws co van de, url=" + url + ",partner=" + partner +",command_code=" + commandcode);
			Util.logger.error("day roi : ex :"+ex.toString());
			DBUtil.Alert("MsgObject", "RUNING", "major",
					"ws co van de, url=" + url + ",partner=" + partner +",command_code=" + commandcode,"");
		}
		
		result = result.trim();
		if(this.multiService == 1){
			this.sub_cp = 2;
			if(result.equals("0") || result.equals("2")){
				LoadMultiService.insertMTMulti(this);
			}
		}
		
		return result;
	}

	public String sendVMS(String textMessage, String partner,
			String commandcode, String sHours, MsgObject msgObject1)
			throws Exception {

		String url = Constants._prop.getProperty("mo.soap." + partner + ".url");
		String username = Constants._prop.getProperty("mo.soap." + partner
				+ ".username");
		String password = Constants._prop.getProperty("mo.soap." + partner
				+ ".password");
		String action = Constants._prop.getProperty("mo.soap." + partner
				+ ".action");
		String module = Constants._prop.getProperty("mo.soap." + partner
				+ ".module");
		String encode = Constants._prop.getProperty("mo.soap." + partner
				+ ".encode");

		Util.logger.info("url: " + url);
		Util.logger.info("Module: " + module);

		if (url == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".url");

		if (module == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".module");

		ListSender sender1 = (ListSender) Class.forName(module).newInstance();

		//Util.logger.info("ABCDEF");

		String result = sender1.sendMO(url, username, password, action,
				msgObject1, commandcode, encode, sHours);

		return result;
	}

	public String sendMessage2JMSserver() {

		try {

		} catch (Exception e) {
			icom.common.Util.logger.error(e.getMessage());
		}

		return "-1";
	}

	public String sendMessageMT(String textMessage, String partner,
			String commandcode) throws Exception {

		String url = Constants._prop.getProperty("mt.soap." + partner + ".url");
		String username = Constants._prop.getProperty("mt.soap." + partner
				+ ".username");
		String password = Constants._prop.getProperty("mt.soap." + partner
				+ ".password");
		String action = Constants._prop.getProperty("mt.soap." + partner
				+ ".action");
		String module = Constants._prop.getProperty("mt.soap." + partner
				+ ".module");
		String encode = Constants._prop.getProperty("mt.soap." + partner
				+ ".encode");

		if (url == null)
			throw new Exception("In the profile is missing mt.soap." + partner
					+ ".url");

		if (module == null)
			throw new Exception("In the profile is missing mt.soap." + partner
					+ ".module");

		MOSender sender = (MOSender) Class.forName(module).newInstance();
		String result = sender.sendMO(url, username, password, action, this,
				textMessage, commandcode, encode);

		return result;
	}

	public int getObjtype() {
		return objtype;
	}

	public void setObjtype(int objtype) {
		this.objtype = objtype;
	}

	public long getMsg_idlong() {
		return msg_id;
	}

	public long getLongRequestid() {
		return Long.parseLong(requestid.toString());
	}

	public int getNotcharge() {
		return notcharge;
	}

	public void setNotcharge(int notcharge) {
		this.notcharge = notcharge;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getClassSendMT() {
		return classSendMT;
	}

	public void setClassSendMT(String classSendMT) {
		this.classSendMT = classSendMT;
	}

	public int getChargeResult() {
		return chargeResult;
	}

	public void setChargeResult(int chargeResult) {
		this.chargeResult = chargeResult;
	}
	

}
