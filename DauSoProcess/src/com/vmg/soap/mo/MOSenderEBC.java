package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderEBC extends MOSender {

	public MOSenderEBC() {
	}

	public void setTemplate() {


		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><ReceiveMo xmlns=\"http://tempuri.org/\">"
			+ "<moId>$Request_ID$</moId>"
			+ "<operatorName>$Operator$</operatorName>"
			+ "<userId>$User_ID$</userId>"
			+ "<serviceId>$Service_ID$</serviceId>"
			+ "<commandCode>$Command_Code$</commandCode>"
			+ "<message>$Message$</message>"
			+ "<username>iComSMS</username>"
			+ "<password>iComHanoi123%^</password>"
			+ "</ReceiveMo>"
			+ "</soap:Body>" + "</soap:Envelope>";  
		
		/*<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:tem="http://tempuri.org/">
		   <soap:Header/>
		   <soap:Body>
		      <tem:InsertMO>
		         <tem:RequestId>12</tem:RequestId>
		         <!--Optional:-->
		         <tem:Operator>VIETTEL</tem:Operator>
		         <!--Optional:-->
		         <tem:UserId>84984328029</tem:UserId>
		         <!--Optional:-->
		         <tem:ReceiverId>84984328029</tem:ReceiverId>
		         <!--Optional:-->
		         <tem:ServiceId>8051</tem:ServiceId>
		         <!--Optional:-->
		         <tem:CommandCode>testebc</tem:CommandCode>
		         <!--Optional:-->
		         <tem:Message>testebc</tem:Message>
		         <tem:MessageType>1</tem:MessageType>
		         <!--Optional:-->
		         <tem:RequestTime>2011-11-02 17:33:00</tem:RequestTime>
		         <tem:ProcessStatus>1</tem:ProcessStatus>
		         <!--Optional:-->
		         <tem:Username>icom@20!!</tem:Username>
		         <!--Optional:-->
		         <tem:Password>icom@20!!</tem:Password>
		      </tem:InsertMO>
		   </soap:Body>
		</soap:Envelope>*/
       
	}
}
