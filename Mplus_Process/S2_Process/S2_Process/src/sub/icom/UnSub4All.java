package sub.icom;

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import DAO.MTDAO;
import DAO.SubcriberDAO_4All;


import mtPush.MTPushCommon;
import mtPush.PushMTConstants;
import daugia.DaugiaCommon;

public class UnSub4All  extends QuestionManager{
	/****
	 * 2012-08-24 LoanDT
	 * ***/
		@Override
		protected Collection getMessages(Properties prop, MsgObject msgObject,Keyword keyword,Hashtable services) throws Exception {		
			 UnRegisterServices(msgObject, keyword);
			 return null;
		}
		
		public void UnRegisterServices(MsgObject msgObject,
				Keyword keyword) throws Exception {
			Collection messages = new ArrayList();

			int msg1mt = Integer.parseInt(Constants.MT_NOCHARGE);
			int msg2mt = Integer.parseInt(Constants.MT_PUSH);

			String[] sMTReturn = UnRegisterMTReturn(msgObject, keyword, msg2mt);

			Util.logger.info("Length: " + sMTReturn.length);
			if (sMTReturn != null) {
				for (int i = 0; i < sMTReturn.length; i++) {
					msgObject.setUsertext(sMTReturn[i]);
					MTDAO.sendMT(msgObject);				
				}
			}
		}
		
		private static String[] UnRegisterMTReturn(MsgObject msgObject,
				Keyword keyword, int msgtype) throws Exception {			

			HashMap _option = new HashMap();

			String MLIST = "mlist";

			String options = keyword.getOptions();
			_option = Util.getParametersAsString(options);
			
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
			String[] mtReturn = new String[1];		
			
			// load list services
			 if(SubcriberDAO_4All.hashServices.size()==0)
				 SubcriberDAO_4All.loadServicesDetail(Getservice);
			String getGetOptions = SubcriberDAO_4All.hashServices.get(GetOptions);
			if(getGetOptions==null)
			{
				Util.logger.info("check lai getoptions hoac sai cu phap");
				mtReturn[0] = keyword.getErrMsg();	
				return mtReturn;
			}
			

			String companyid = Util.getStringfromHashMap(_option, "companyid", "0");
			int iCompanyId = Integer.parseInt(companyid);

			MLIST = ((String) _option.get("mlist"));
			if("".equalsIgnoreCase(MLIST))
			{
				Util.logger.error(" Mlist is null:" + _option);
				return null;
			}
			/*
			 * HaPTT update ngay 5/5/2011 Check neu co khuyen mai, thi ktra trong
			 * bang mlist_promotion
			 */
		
		
			if (SubcriberDAO_4All.isexist_in_mlist(msgObject.getUserid(), MLIST, Getservice,GetOptions)) {
				mtReturn[0] = keyword.getUnsubMsg();				
				mtReturn[0] = mtReturn[0].replace("#", getGetOptions);
				/****
				 * Neu ton tai trong mlist, lay thong bao huy dich vu thanh cong,
				 * chuyen thong tin sang mlist_cancel
				 * **/
				SubcriberDAO_4All.InsertMlist2MlistCancel(MLIST, msgObject.getUserid(), keyword
						.getAmount(), msgObject.getChannelType(), Getservice, GetOptions);
				/***
				 * Move sang mlist_subcriber_cancel Sau khi move xong -> xoa user_id
				 * ra khoi mlist_subcriber
				 * **/
				/*****
				 * Move tu mlist sang mlist_subcriber_cancel *
				 ****/
				SubcriberDAO_4All.MoveMlist2SubcriberCancel(MLIST, msgObject.getUserid(),
						Getservice, msgObject.getChannelType(),GetOptions);
				/***
				 * Sau khi move sang mlist_subcriber_cancel thi xoa ben mlist
				 * **/
				SubcriberDAO_4All.DelMlist(MLIST, msgObject.getUserid(), Getservice,GetOptions);
				/*****************************************************************/
				/****
				 * Sau khi move xong -> xoa user_id ra khoi mlist_subcriber
				 * **/
				SubcriberDAO_4All.DeleteSubcriber(msgObject.getUserid(), Getservice, msgObject
						.getChannelType(),GetOptions);

			} else {
				// mtReturn[0] =
				// "Ban chua dang ky dich vu. Hay soan DK MaDichVu gui "
				// + msgObject.getServiceid() + " de dang ky dich vu khac";				

				mtReturn[0] = keyword.getWarMsg();				
				mtReturn[0] = mtReturn[0].replace("#", getGetOptions);
			}
			return mtReturn;
		}
}
