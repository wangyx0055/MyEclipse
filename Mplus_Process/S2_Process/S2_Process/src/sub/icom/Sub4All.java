package sub.icom;

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
import icom.common.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import DAO.MTDAO;
import DAO.SubcriberDAO_4All;


import services.SubXoso;

import mtPush.MTPushCommon;
import mtPush.PushMTConstants;
import daugia.DaugiaCommon;

public class Sub4All extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		RegisterServices(msgObject, keyword, services);
		return null;
	}

	public void RegisterServices(MsgObject msgObject, Keyword keyword,
			Hashtable servicces) throws Exception {
		Collection messages = new ArrayList();

		int msg1mt = Integer.parseInt(Constants.MT_PUSH);
		int msg2mt = Integer.parseInt(Constants.MT_CHARGING);

		Util.logger.info("Service_Type: " + keyword.getService_type());
		Util.logger.info("Package: " + Constants.PACKAGE_SERVICE);

		String[] sMTReturn = RegisterMTReturn(msgObject, keyword, msg2mt,servicces);
		if (sMTReturn != null) {
			for (int i = 0; i < sMTReturn.length; i++) {
				msgObject.setUsertext(sMTReturn[i]);
				MTDAO.sendMT(msgObject);				
			}
		}
	}

	private static String[] RegisterMTReturn(MsgObject msgObject,
			Keyword keyword, int msgtype, Hashtable services) throws Exception {	

		HashMap _option = new HashMap();
		String MLIST = "";

		String options = keyword.getOptions();
		_option = Util.getParametersAsString(options);
		MLIST = Util.getStringfromHashMap(_option, "mlist", MLIST);
		if("".equals(MLIST))
		{
			Util.logger.info("Kiem tra lai mlist cua options"+options);
			return null;
		}
		Util.logger.info("MLIST: " + MLIST);		

		String companyid = Util.getStringfromHashMap(_option, "companyid", "0");
		int iCompanyId = Integer.parseInt(companyid);
		String mtfree = Util.getStringfromHashMap(_option, "mtfree", "0");
		mtfree = "0";
		long duration = keyword.getDuration();
		String info = msgObject.getUsertext().toUpperCase().trim();

		info = info.replace('-', ' ');
		info = info.replace(';', ' ');
		info = info.replace('+', ' ');
		info = info.replace('.', ' ');
		info = info.replace(',', ' ');
		info = info.replace('_', ' ');
		info = info.replace('/', ' ');
		info = info.replace('\\', ' ');

		info = Util.replaceAllWhiteWithOne(info.trim());	
		String Getservice = keyword.getService_ss_id();
		String keywords = msgObject.getKeyword();		
		msgObject.setCommandCode(Getservice);
		String GetOptions = info.substring(keywords.length());
		GetOptions = GetOptions.replace(" ", "");
		// lay danh sach detail option ung voi services 
		String listYkhoa = "";
		listYkhoa = SubcriberDAO_4All.GetOption(Getservice);
		
		// load list services
		 if(SubcriberDAO_4All.hashServices.size()==0)
		 {
			Util.logger.info("HashService is null, system checking hash service again");
			 SubcriberDAO_4All.loadServicesDetail(Getservice);  
		 }
		String getGetOptions = SubcriberDAO_4All.hashServices.get(GetOptions);
		if(getGetOptions==null)
		{
			Util.logger.info(" Get option for services is null");
			return null;
		}
		

		String[] mtReturn = new String[1];

		if (listYkhoa.indexOf(GetOptions) >= 0) // dung trong ds cac benh
		{
			msgObject.setOption(GetOptions);
		} else {
			mtReturn[0] = keyword.getErrMsg();
			return mtReturn;
		}

		// Kiem tra xem da co hay chua?

		/*****
		 * Neu da ton tai trong mlist thi lay exist message tu keyword
		 * **/
		if (SubcriberDAO_4All.isexist_in_mlist(msgObject.getUserid(), MLIST,Getservice,GetOptions)) {
			mtReturn[0] = keyword.getExistMsg();		
			mtReturn[0] = mtReturn[0].replace("#", getGetOptions);

		} else {
			/****
			 * 2010-11-06: PhuongDT Trong truong hop chua co dich vu: - da ton
			 * tai trong mlist_cancel: move toan bo tu mlist_cancel, increase
			 * reg_count ++; update autotime=current_date - neu khong ton tai
			 * trong mlist_cancel: truong hop dang ky moi
			 * **/

			/**
			 * 2011-05-05: HaPTT: neu khuyen mai thi lay mt dky khac:
			 * 
			 ***/
			mtReturn[0] = keyword.getSubMsg();	
			mtReturn[0] = mtReturn[0].replace("#", getGetOptions);

			/****
			 * Neu da ton tai trong mlist_cancel: Dang ky lai Move toan bo tu
			 * mlist_cancel sang, increase reg_count ++, update autotimes
			 * **/
			if (SubcriberDAO_4All.isexist_in_cancel(msgObject.getUserid(), MLIST,Getservice,GetOptions)) {
				SubcriberDAO_4All.InsertMlistCancel2Mlist(MLIST, msgObject.getUserid(),
						keyword.getAmount(), msgObject.getChannelType(),
						Getservice, GetOptions);
				/******
				 * move mlist_cancel sang subcriber *
				 **/
				SubcriberDAO_4All.MoveMlistCancel2Subcriber("mlist_subcriber",
						msgObject.getUserid(), Getservice, msgObject
								.getChannelType(), GetOptions);
				/***
				 * Sau khi move sang mlist thi xoa ben mlist_cancel
				 * **/
				SubcriberDAO_4All.DelMlist(MLIST + "_cancel", msgObject.getUserid(),
						Getservice, GetOptions);
				/******
				 * delete Subcriber_cancel *
				 **/
				SubcriberDAO_4All.DeleteSubcriberCancel(msgObject.getUserid(),
						Getservice, msgObject.getChannelType(),GetOptions);

				if (msgObject.getLast_code() != null
						&& !msgObject.getLast_code().equals("0")
						&& !msgObject.getLast_code().trim().equals("")) {

					DBUtil.updateLastCode(MLIST, msgObject.getUserid(),
							msgObject.getCommandCode(), msgObject
									.getLast_code());
				}

			} else {
				/****
				 * Neu khong ton tai trong mlist_cancel: Dang ky moi Trong giai
				 * doan khuyen mai, MODE_ADV = 0 2010-11-12: change Mode_adv:
				 * khong dung key nay nua, chuyen qua viec check trong bang
				 * services active_free=0: khong khuyen mai, = 1: khuyen mai
				 * 
				 * **/
				icom.Services service = icom.Services.getService(Getservice,
						services);

				if (Util.IsServiceFree(service)) {
					Util.logger.info("Dich vu " + Getservice
							+ " duoc free nen lay promoMsg \tuser_id:"
							+ msgObject.getUserid());
					mtReturn[0] = keyword.getPromoMsg();
					mtfree = service.getNumberFree() + "";
					msgtype = 0;
					// mtfree = "0";
				} else {
					Util.logger.info("Dich vu " + Getservice + " duoc free?"
							+ Util.IsServiceFree(service));
					mtfree = "0";
				}
				
				/*****
				 * String mlist, MsgObject ems, String mtfree, int msgtype, long
				 * lduration, long amount *
				 **/
				SubcriberDAO_4All.Insert2Mlist(MLIST, msgObject, mtfree, msgtype,
						duration, keyword.getAmount());
				/******
				 * Them user_id vao Subcriber
				 * 
				 * *
				 **/
				Util.logger
						.info("DBUtil check info to insert subsciber:@Mlist:"
								+ MLIST + "\t@CommandCode:"
								+ msgObject.getCommandCode() + "\t@user:"
								+ msgObject.getUserid());

				SubcriberDAO_4All.InsertSubcriber(MLIST, msgObject, mtfree, msgtype);
			}
		}
		return mtReturn;
	}

	public static String replaceAllWhiteWithOne(String sInput) {
		String strTmp = sInput.trim();
		String strResult = "";
		for (int i = 0; i < strTmp.length(); i++) {
			char ch = strTmp.charAt(i);
			if (ch == ' ') {
				for (int j = i; j < strTmp.length(); j++) {
					char ch2 = strTmp.charAt(j);
					if (ch2 != ' ') {
						i = j;
						strResult = strResult + ' ' + ch2;
						break;
					}
				}

			} else {
				strResult = strResult + ch;
			}
		}
		return strResult;
	}	
}