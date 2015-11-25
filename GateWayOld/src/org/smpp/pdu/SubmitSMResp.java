/*
 * Copyright (c) 1996-2001
 * Logica Mobile Networks Limited
 * All rights reserved.
 *
 * This software is distributed under Logica Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package org.smpp.pdu;

import icom.gateway.Logger;

import org.smpp.Data;
import org.smpp.util.ByteBuffer;
import org.smpp.util.NotEnoughDataInByteBufferException;
import org.smpp.util.TerminatingZeroNotFoundException;

/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version 1.0, 11 Jun 2001
 * 
 *          paolo@bulksms.com: patched to take into account that no body should
 *          be present when command_status is non-zero.
 */

public class SubmitSMResp extends Response {

	private String messageId = Data.DFLT_MSGID;

	public SubmitSMResp() {
		super(Data.SUBMIT_SM_RESP);
	}

	public void setBody(ByteBuffer buffer)
			throws NotEnoughDataInByteBufferException,
			TerminatingZeroNotFoundException, WrongLengthOfStringException,
			InvalidPDUException {
		if (getCommandStatus() == 0) {
			setMessageId(buffer.removeCString());
			return;
		}

		if (buffer.length() > 0) {
			Logger.verbose("setBody");
			Logger
					.verbose("invalid SubmitSMResp: command_status non-zero, but body was present (ignoring body)");

			Logger
					.verbose("invalid SubmitSMResp sequenceNumber ["
							+ getSequenceNumber()
							+ "]: command_status non-zero, but body was present (ignoring body)");
			// This is broken in so many implementations that it's not practical
			// to be so pedantic about it, so we now just accept it.
			// throw new
			// InvalidPDUException(this,"command_status non-zero, but body was present");
			buffer.removeBytes(buffer.length()); // discard body
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		if (getCommandStatus() == 0)
			buffer.appendCString(messageId);
		return buffer;
	}

	public void setMessageId(String value) throws WrongLengthOfStringException {
		checkString(value, Data.SM_MSGID_LEN);
		messageId = value;
	}

	public String getMessageId() {
		return messageId;
	}

	public String debugString() {
		String dbgs = "(submit_resp: ";
		dbgs += super.debugString();
		dbgs += getMessageId();
		dbgs += " ";
		dbgs += debugStringOptional();
		dbgs += ") ";
		return dbgs;
	}

}