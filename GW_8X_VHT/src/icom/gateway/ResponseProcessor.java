package icom.gateway;

/*
 * Copyright (c) 2006-2008
 * IT R&D Center, VietNamNet Media Group
 * All rights reserved.
 * @version 2008.03
 */

import icom.common.Queue;

import java.util.Map;
import java.math.BigDecimal;

import org.smpp.*;
import org.smpp.pdu.*;

import java.sql.Timestamp;

/**
 * For Processing PDUs (responsed Requests) from SMSC
 */
public class ResponseProcessor extends Thread {
	private Queue fromSMSC = null;
	// private Queue EMSQueue = null;
	private Queue SendLogQueue = null;
	private Queue ResendQueue = null;
	private Map wait4ResponseTable = null;

	private PDU pdu = null;
	private SubmitSMResp ssmr = null;

	// Variables storing info from SMSC (in Deliver_SM pdu)
	// private String messageId = null; // in decimal format
	private int seqNumber = 0;
	private int commandStatus = 0;

	// private DBTools dbTools = null;
	private ReportMsgParser parser = null;

	public ResponseProcessor(Queue fromSMSC, Map wait4ResponseTable,
			Queue SendLogQueue, Queue ResendQueue) {
		this.fromSMSC = fromSMSC; // contains only request PDUs.
		this.wait4ResponseTable = wait4ResponseTable;
		// this.EMSQueue = EMSQueue;
		this.SendLogQueue = SendLogQueue;
		this.ResendQueue = ResendQueue;
		// this.dbTools = new DBTools();
		this.parser = new ReportMsgParser();
	}

	public void run() {
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////
		while (Gateway.running) {
			try {
				pdu = (PDU) fromSMSC.dequeue(); // blocks until having an item
				// System.out.print("fromSMSC.size:" + fromSMSC.size());
				if (pdu.isResponse()) {
					processResponse(pdu);
				}
			} catch (Exception e) {
				Gateway.util.logErr(this.getClass().getName(),
						"ResponseProcessor.run: " + e.getMessage());
			}
		}
		// /////////////////////////////
		Gateway.util.log(this.getClass().getName(),
				"{ResponseProcessor stoped}");
		this.destroy();
		// /////////////////////////////
	}

	public void destroy() {
		Gateway.removeThread(this);
	}

	private void processResponse(PDU pdu) throws Exception {
		int resend = 1;
		String sErr = "";
		Timestamp time = new Timestamp(System.currentTimeMillis());
		if (pdu.getCommandId() == Data.SUBMIT_SM_RESP) {
			ssmr = (SubmitSMResp) pdu;
			commandStatus = ssmr.getCommandStatus();
			if (commandStatus == Data.ESME_ROK) {
				Gateway.util.log(this.getClass().getName(),
						"{Respond for MT}{MessageId="
								+ ssmr.getSequenceNumber() + "}");
				synchronized (wait4ResponseTable) {
					EMSData ems = (EMSData) wait4ResponseTable.remove(ssmr
							.getSequenceNumber()
							+ "");

					if (ems == null) {
						Gateway.util.logErr(this.getClass().getName(),
								"{wait4ResponseTable: ems==null}{emsId="
										+ ssmr.getSequenceNumber()
										+ "}{MessageID=" + ssmr.getMessageId()
										+ "}");
						return;
					}
					ems.setDoneDate(time);
					ems.setMessageId(ssmr.getMessageId());
					ems.setProcessResult(Constants.MSG_SENT_OK);
					SendLogQueue.enqueue(ems);
					
				}
			} else {
				resend = SMSCTools.CheckResend(commandStatus, ssmr
						.getMessageId());
				sErr = SMSCTools.GetErrName(commandStatus, ssmr.getMessageId());

				this.seqNumber = ssmr.getSequenceNumber();

				if (this.seqNumber < 10) { // =1: response for Invalid message
					Gateway.util.log(this.getClass().getName(),
							"ResponseProcessor: seqNumber < 10, ="
									+ this.seqNumber);
					return; // =2,3,... send file from console
				}

				// BigDecimal emsId = new BigDecimal(this.seqNumber / 10);
				// int currSegmentNumber = this.seqNumber % 10;

				
				
				
				// Get EMSData from wait4ResponseTable
				synchronized (wait4ResponseTable) {
					EMSData ems = (EMSData) wait4ResponseTable.remove(ssmr
							.getSequenceNumber()
							+ "");

					if (ems == null) {
						Gateway.util.log(this.getClass().getName(),
								"{processResponse: ems==null}{emsId="
										+ this.seqNumber + "}{MessageID="
										+ ssmr.getSequenceNumber() + "}");
						return;
					}
					ems.setNotes(ems.getNotes() + ":" + sErr);
					Gateway.util.log(this.getClass().getName(),
							"{Response for MT}{Request_ID="
									+ ems.getRequestId() + "}{User_ID="
									+ ems.getUserId() + "}{Message_ID="
									+ ems.getMessageId() + "}");
					// Last segment ?
					// if (currSegmentNumber != ems.getTotalSegments()) {
					// Gateway.util.log(this.getClass().getName(),"{ProccessRespond=NotLastSegment}{emsId="
					// + emsId + "}{MessageID=" + ssmr.getMessageId() + "}");
					// return;
					// }

					if ((resend == 1)
							&& (ems.getSendNum() < Preference.NumOfRetries)) {
						if ((time.compareTo(ems.getSubmitDate())) > 0) {
							ems.setSendNum(ems.getSendNum() + 1);
						}
						Gateway.util.log(this.getClass().getName(),
								"{Add MT to Resend Queue}{Request_ID="
										+ ems.getRequestId() + "}{User_ID="
										+ ems.getUserId() + "}{Message_ID="
										+ ems.getMessageId() + "}{Resend num="
										+ ems.getSendNum() + "}");
						ResendQueue.enqueue(ems);
					} else {
						Gateway.util.log(this.getClass().getName(),
								"{MT not Resend}{Request_ID="
										+ ems.getRequestId() + "}{User_ID="
										+ ems.getUserId() + "}{Message_ID="
										+ ems.getMessageId() + "}{Resend num="
										+ ems.getSendNum() + "}");
						
						//ems.setMessageType(2);
						ems.setProcessResult(Constants.MSG_NOT_RESEND_MT);
						
						if (ems.getMessageType() == Constants.CDR_CHARGE) {
							ems.setMessageType(Constants.CDR_REFUND);
						}
						
						SendLogQueue.enqueue(ems);
					}
				}
			}
		}
		else {
			Gateway.util.logErr(this.getClass().getName(),
			"processResponse (not processed).");
		}
		// Process response
		if (pdu.isGNack()) {
			Gateway.util.logErr(this.getClass().getName(),
					"GENERIC_NAK (not processed).");
		}
	}
}
