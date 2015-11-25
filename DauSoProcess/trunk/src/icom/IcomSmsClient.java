package icom;

import app.PPGMain;
import app.Putter;
import org.apache.axis2.AxisFault;

public class IcomSmsClient {

	public static void execMT_ACK(Putter p) {
		try {

			_169._71._162._203.wsmozf.services.receiver.ReceiverServiceStub stub = new _169._71._162._203.wsmozf.services.receiver.ReceiverServiceStub();
			_169._71._162._203.wsmozf.services.receiver.ReceiverServiceStub.MoReceiver request = new _169._71._162._203.wsmozf.services.receiver.ReceiverServiceStub.MoReceiver();
			/*******************************************************************
			 * step 3 SMG->icom mtACK
			 * 
			 * WebService
			 */
			//fix bugs of icom VN keyword ZF->ZFVN01 by runus20110107
			if(p.get("content").length()>6)
				p.set("keyword",p.get("content").substring(0,6));
			request.setRequest_ID(p.get("out_linkid"));
			PPGMain.echo("out_linkid:"+p.get("out_linkid"));
			request.setUser_ID(p.get("mobile"));
			request.setMessage(p.get("content"));
			request.setService_ID(p.get("spnum"));
			request.setCommand_Code(p.get("keyword"));
			request.setOperator(p.get("oprator"));
		

			_169._71._162._203.wsmozf.services.receiver.ReceiverServiceStub.MoReceiverResponse response = stub
					.moReceiver(request);

			int responseCode = response.getMoReceiverReturn();

			responseCode = responseCode == 1 ? 0 : responseCode;
			stub.cleanup();
		
			p.set("retcode", responseCode + "");

			PPGMain.echo("act:MT_ACK|gate:" + p.get("gate") + "|out_linkid:"+p.get("out_linkid")+"|linkid:" + p.get("linkid")
					+ "|response code:" + responseCode);

		} catch (AxisFault ae) {
			ae.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void execMt_ENC(Putter p) {
		try {

			
			_169._71._162._203.wsmt3rd.services.receiver.ReceiverServiceStub stub = new _169._71._162._203.wsmt3rd.services.receiver.ReceiverServiceStub();
			_169._71._162._203.wsmt3rd.services.receiver.ReceiverServiceStub.MtReceiver request = new _169._71._162._203.wsmt3rd.services.receiver.ReceiverServiceStub.MtReceiver();
			/*******************************************************************
			 * 
			 * mobie : User_ID content: Message shortcode: Service_ID
			 * commandcode: Command_Code messagetype: Message_Type linkid:
			 * Request_ID: totalmessage: Total_Message messageindex:
			 * Message_Index ismore: IsMore contenttype: Content_Type operator:
			 * Operator deskey: KeyDES Step 5: SMG => Icom¡¯s MT WebService
			 */
			request.setRequest_ID(p.get("out_linkid"));
			PPGMain.echo("out_linkid:"+p.get("out_linkid"));
		
			request.setUser_ID(p.get("mobile"));
			request.setMessage(p.get("content"));
			request.setService_ID(p.get("spnum"));
			request.setMessage_Type(p.get("feetype").equals("")?"1":p.get("feetype"));// 0 free
		
			request.setMessage_Index(p.get("msgi"));//msgindex
			request.setTotal_Message(p.get("msgt"));//p.get("msgt")
			request.setIsMore(p.get("ismore"));//p.get("ismore")
			request.setContent_Type(p.get("msgtype"));//p.get("msgtype")
			request.setCommand_Code(p.get("keyword"));
			request.setOperator(p.get("oprator"));// $operator is used by
													// SMG,so $oprator =raw
													// $operator
			request.setKeyDES(p.get("deskey"));
			request.setMessage(p.get("content"));
			

			_169._71._162._203.wsmt3rd.services.receiver.ReceiverServiceStub.MtReceiverResponse response = stub
					.mtReceiver(request);
			int responseCode = response.getMtReceiverReturn();
			responseCode = responseCode == 1 ? 0 : responseCode;
			stub.cleanup();
			p.set("retcode", responseCode + "");

			PPGMain.echo("act:MT_ENC|gate:" + p.get("gate") + "|out_linkid:"+p.get("out_linkid")+"|linkid:" + p.get("linkid")
					+ "|response code:" + responseCode);

		} catch (AxisFault ae) {
			ae.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
