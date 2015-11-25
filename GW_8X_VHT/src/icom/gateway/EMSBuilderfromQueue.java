package icom.gateway;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author IT-VMG
 * @version 1.0
 */

import icom.common.*;
import icom.gateway.Constants;
import icom.gateway.EMSTools;

import java.sql.Timestamp;
import java.math.BigDecimal;
import org.smpp.pdu.*;
import org.smpp.util.ByteBuffer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class EMSBuilderfromQueue extends Thread {
	private Queue toSMSC = null;

	private Queue EMSQueue = null;

	private Map Wait4ResponseTable = null;

	private DBTools dbTools = null;

	private Queue BlackListQueue = null;

	public EMSBuilderfromQueue(Queue toSMSC, Queue EMSQueue,
			Map Wait4ResponseTable, Queue blacklistQueue) {
		this.toSMSC = toSMSC;
		this.EMSQueue = EMSQueue;
		this.Wait4ResponseTable = Wait4ResponseTable;
		this.dbTools = new DBTools();
		this.BlackListQueue = blacklistQueue;
	}

	boolean isRepeat(String userid) {

		Logger.info(this.getName(), "-CheckRepeat: {user_id=" + userid + "}");

		return false;
	}

	public void run() {
		// ///////////////////////////
		Gateway.addLiveThread(this);
		// ///////////////////////////

		while (Gateway.running) {
			try {
				if (EMSQueue.size() > -1) {
					Logger.info("{EMSQueue=" + this.getName()
							+ "}{1.EMSQueue.size=" + EMSQueue.size() + "}");
				}
				EMSData ems = (EMSData) EMSQueue.dequeue();

				if (EMSQueue.size() > -1) {
					Logger.info("{EMSQueue=" + this.getName()
							+ "}{2.EMSQueue.size=" + EMSQueue.size() + "}");
				}
				String user_id = ems.getUserId();

				if ("1".equalsIgnoreCase(Preference.BLACKLIST)) {
					if (Gateway.loadrepeat.isRepeat(user_id)) {
						ems.setNotes("Blacklist");
						this.BlackListQueue.enqueue(ems);
					} else {
						this.sendEMSQueueEx(ems);
					}
				} else {
					this.sendEMSQueueEx(ems);
				}

				sleep(20);
			}

			catch (InterruptedException ex) {
				Gateway.util.logErr(this.getClass().getName(),
						"{InterruptedException}" + ex.getMessage());
			} catch (DBException ex) { // when lost connection to db
				Gateway.util.logErr(this.getClass().getName(), "{DBException}"
						+ ex.getMessage());
			} catch (Exception ex) {
				Gateway.util.logErr(this.getClass().getName(),
						"EMSBuilderfromQueue:: " + ex.getMessage());
			}
		} // while

		// ///////////////////
		Gateway.util.log(this.getClass().getName(), "{"
				+ this.getClass().getName() + " stopped}");
		this.destroy();

		// ///////////////////
	}

	public void destroy() {
		Gateway.removeThread(this);
	}

	/**
	 * We only send new messages in the queue and the messages having not
	 * received the ACK and staying in the queue longer than the TIME_RESEND.
	 * 
	 * @return number of messages sent
	 */
	public int sendEMSQueueEx(EMSData ems) throws DBException, EMSException {
		int numOfEms = 0;
		byte registeredDelivery = 0; // (default) No Delivery Receipt
		// requested
		Timestamp time = null;
		boolean isCharged = false;
		try {

			// Collection collection = dbTools.getAllEMSSendQueue(Preference.
			// SEND_MODE);
			numOfEms = EMSQueue.size();
			// BigDecimal emsId = ems.getId();

			if (ems == null) { // removed by other processes
				Gateway.util
						.log(this.getClass().getName(),
								"{EMSBuilderfromQueue: inValid ContentType}{ems is null}");
			}
			registeredDelivery = 0;
			time = new Timestamp(System.currentTimeMillis());
			ems.setSubmitDate(time);
			
			if (ems.getContentType() == Constants.CT_TEXT) {
				SubmitSM ssm = SMSCTools.buildSubmitSM(ems
						.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
						ems.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
						ems.getText(), // text max 160 chars
						ems.getId().intValue(), // seq = <emsId><segment_seqnum>
						registeredDelivery, ems.getMessageType(), ems
								.getCommandCode(), ems.getRequestId());
				
				
				synchronized (Wait4ResponseTable) {
					this.Wait4ResponseTable.put((ems.getId().intValue()) + "",
							ems);
				}
				Gateway.util.log(this.getClass().getName(),
						"{Add2Wait4ResponseTable}{ems=" + ems.getId() + "}"
								+ "{RequestID=" + ems.getRequestId() + "}{userid=" + ems.getUserId() + "}");
				//time = new Timestamp(System.currentTimeMillis());
				//ems.setSubmitDate(time);
				this.toSMSC.enqueue(ssm);
				
			} else if ((ems.getContentType() == Constants.CT_WAP_SI)) {
				if ("SFONE".equalsIgnoreCase(ems.getMobileOperator())) {
					ByteBuffer otaData = null;
					String sSi = ems.getText();
					int dot_idx = sSi.indexOf(":");
					if (dot_idx > 0) {
						String sMsg = sSi.substring(0, dot_idx);
						String sUrl = sSi.substring(dot_idx + 1);
						Si si = new Si();
						si.setURL(sUrl);
						si.setMessage(sMsg);
						si.encodeSI();
						otaData = si.getEncodedSI();
					} else {
						Gateway.util.logErr(this.getClass().getName(),
								"{User_ID=" + ems.getUserId() + "}{Invalid SI:"
										+ ems.getText() + "}");
					}
					Collection vSubmits = EMSTools
							.buildSubmitEMS(
									otaData,
									ems
											.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
									ems
											.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
									ems.getContentType(), ems.getId(),
									registeredDelivery, ems.getMessageType(),
									ems.getRequestId(),ems.getCommandCode());

					int i = 1;
					ems.setTotalSegments(vSubmits.size());
					/*
					 * synchronized (Wait4ResponseTable) {
					 * this.Wait4ResponseTable.put((ems.getId().intValue()) +
					 * "", ems); } Gateway.util.log(this.getClass().getName(),
					 * "{Add2Wait4ResponseTable}{ems=" + ems.getId() + "}" +
					 * "{RequestID=" + ems.getRequestId() + "}");
					 */

					for (Iterator it2 = vSubmits.iterator(); it2.hasNext(); i++) {
						SubmitSM ssm = (SubmitSM) it2.next();

						// ///
						// Conglt: 20080715
						if (i == 1) {
							synchronized (Wait4ResponseTable) {
								this.Wait4ResponseTable.put((ssm
										.getSequenceNumber())
										+ "", ems);
							}
							Gateway.util.log(this.getClass().getName(),
									"{Add2Wait4ResponseTable}{ems="
											+ ssm.getSequenceNumber() + "}"
											+ "{RequestID="
											+ ems.getRequestId() + "}");

						}
						// End conglt20080715
						// ********************************************//
						this.toSMSC.enqueue(ssm);
						// ********************************************//
					}

				} else {
					/*
					 * ByteBuffer otaData = null; String sSi = ems.getText();
					 * int dot_idx = sSi.indexOf(":"); if (dot_idx > 0) { String
					 * sMsg = sSi.substring(0, dot_idx); String sUrl =
					 * sSi.substring(dot_idx + 1); SIgsm si = new SIgsm();
					 * si.setURL(sUrl); si.setMessage(sMsg); si.encodeSI();
					 * otaData = si.getEncodedSI(); } else {
					 * Gateway.util.logErr(this.getClass().getName(),
					 * "{User_ID=" + ems.getUserId() + "}{Invalid SI:" +
					 * ems.getText() + "}"); }
					 */
					ByteBuffer otaData = null;
					boolean infoError = false;
					String sSi = ems.getText();
					int dot_idx = sSi.indexOf(":");
					if (dot_idx > 0) {
						String sMsg = sSi.substring(0, dot_idx);
						Gateway.util.log(this.getClass().getName(), "msg="
								+ sMsg);
						String sUrl = sSi.substring(dot_idx + 1);
						Gateway.util.log(this.getClass().getName(), "url="
								+ sUrl);
						SIgsm si = new SIgsm();
						si.setURL(sUrl);
						si.setMessage(sMsg);
						si.encodeSI();
						otaData = si.getEncodedSI();
					} else {
						// System.out.println("invalid SI: " + ems.getText());
						Gateway.util.log(this.getClass().getName(),
								"invalid SI: " + ems.getText());
						infoError = true;
					}

					/*
					 * 
					 * buildSubmitEMS(ByteBuffer otaData, String srcAddr, String
					 * destAddr, int contentType, BigDecimal emsId, byte
					 * registeredDelivery) throws EMSException {
					 */

					Collection vSubmits = EMSTools
							.buildSubmitEMS(
									otaData,
									ems
											.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
									ems
											.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
									ems.getContentType(), ems.getId(),
									registeredDelivery,ems.getMessageType(),ems.getCommandCode());

					int i = 1;
					ems.setTotalSegments(vSubmits.size());
					/*
					 * synchronized (Wait4ResponseTable) {
					 * this.Wait4ResponseTable.put((ems.getId().intValue()) +
					 * "", ems); } Gateway.util.log(this.getClass().getName(),
					 * "{Add2Wait4ResponseTable}{ems=" + ems.getId() + "}" +
					 * "{RequestID=" + ems.getRequestId() + "}");
					 */

					for (Iterator it2 = vSubmits.iterator(); it2.hasNext(); i++) {
						SubmitSM ssm = (SubmitSM) it2.next();

						// Conglt: 20080715
						if (i == 1) {
							synchronized (Wait4ResponseTable) {
								this.Wait4ResponseTable.put((ssm
										.getSequenceNumber())
										+ "", ems);
							}
							Gateway.util.log(this.getClass().getName(),
									"{Add2Wait4ResponseTable}{ems="
											+ ssm.getSequenceNumber() + "}"
											+ "{RequestID="
											+ ems.getRequestId() + "}");

						}
						// End conglt20080715
						// ********************************************//
						this.toSMSC.enqueue(ssm);
						// ********************************************//
					}

				}
			} else if (ems.getContentType() == Constants.CT_BINARY) {
				// ByteBuffer ota2build = new ByteBuffer(
				// Charge.appendShort( (short) 0x000C);
				byte[] b = new byte[ems.getText().length() / 2];

				b = HexaTool.fromHexString(ems.getText());

				ByteBuffer ota2build = new ByteBuffer(b);

				SubmitSM ssm = EMSTools.buildSubmitEMSfromBIN(ota2build, ems
						.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
						ems.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
						ems.getId(), registeredDelivery, ems.getMessageType(),
						ems.getCommandCode(), 1, ems.getRequestId());
				synchronized (Wait4ResponseTable) {
					this.Wait4ResponseTable.put(((ems.getId().intValue()) + "")
							+ "1", ems);
				}
				Gateway.util.log(this.getClass().getName(),
						"{Add2Wait4ResponseTable}{ems=" + ems.getId() + "1}"
								+ "{RequestID=" + ems.getRequestId() + "}");
				this.toSMSC.enqueue(ssm);
				time = new Timestamp(System.currentTimeMillis());
				ems.setSubmitDate(time);

			} else if (ems.getContentType() == Constants.CT_BANKING) {
				SubmitSM ssm = EMSTools.buildSubmitBanking(ems.getText(), ems
						.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
						ems.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
						ems.getId(), registeredDelivery, ems.getMessageType(),
						ems.getCommandCode(), ems.getTotalSegments(), ems
								.getsRequestID());
				time = new Timestamp(System.currentTimeMillis());
				synchronized (Wait4ResponseTable) {
					this.Wait4ResponseTable.put((ems.getId().intValue()) + ""
							+ "1", ems);
				}
				Gateway.util.log(this.getClass().getName(),
						"{Add2Wait4ResponseTable}{ems=" + ems.getId() + "1"
								+ "}" + "{RequestID=" + ems.getRequestId()
								+ "}");
				this.toSMSC.enqueue(ssm);

			} else if (ems.getContentType() == Constants.CT_411A) {
				SubmitSM ssm = EMSTools.buildSubmit411A(ems.getText(), ems
						.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
						ems.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
						ems.getId(), registeredDelivery, ems.getMessageType(),
						ems.getCommandCode(), ems.getTotalSegments(), ems
								.getsRequestID());
				time = new Timestamp(System.currentTimeMillis());
				synchronized (Wait4ResponseTable) {
					this.Wait4ResponseTable.put((ems.getId().intValue()) + ""
							+ "1", ems);
				}
				Gateway.util.log(this.getClass().getName(),
						"{Add2Wait4ResponseTable}{ems=" + ems.getId() + "1"
								+ "}" + "{RequestID=" + ems.getRequestId()
								+ "}");
				this.toSMSC.enqueue(ssm);

			}else if (ems.getContentType() == Constants.CT_DPORT) {
				SubmitSM ssm = EMSTools.buildSubmitDPORT(ems.getText(), ems
						.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
						ems.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
						ems.getId(), registeredDelivery, ems.getMessageType(),
						ems.getCommandCode(), ems.getTotalSegments(), ems
								.getsRequestID());
				time = new Timestamp(System.currentTimeMillis());
				synchronized (Wait4ResponseTable) {
					this.Wait4ResponseTable.put((ems.getId().intValue()) + ""
							+ "1", ems);
				}
				Gateway.util.log(this.getClass().getName(),
						"{Add2Wait4ResponseTable}{ems=" + ems.getId() + "1"
								+ "}" + "{RequestID=" + ems.getRequestId()
								+ "}");
				this.toSMSC.enqueue(ssm);

			}
			else if (ems.getContentType() == Constants.CT_TEXT_LONG) {

				Collection vSubmits = EMSTools
						.buildSubmitLongMsg(
								ems.getText(),
								ems
										.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
								ems
										.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
								ems.getContentType(), ems.getId(),
								registeredDelivery,ems.getMessageType(),ems.getCommandCode());

				int i = 1;
				ems.setTotalSegments(vSubmits.size());

				for (Iterator it2 = vSubmits.iterator(); it2.hasNext(); i++) {
					SubmitSM ssm = (SubmitSM) it2.next();

					// Conglt: 20080715
					if (i == 1) {
						synchronized (Wait4ResponseTable) {
							this.Wait4ResponseTable.put((ssm
									.getSequenceNumber())
									+ "", ems);
						}
						Gateway.util.log(this.getClass().getName(),
								"{Add2Wait4ResponseTable}{ems="
										+ ssm.getSequenceNumber() + "}"
										+ "{RequestID=" + ems.getRequestId()
										+ "}");

					}
					// End conglt20080715
					// ********************************************//
					this.toSMSC.enqueue(ssm);
					// ********************************************//
				}

			}else if (ems.getContentType() == Constants.CT_OMA_LONG) {

				Collection vSubmits = EMSTools
						.buildSubmitOMA(
								ems.getText(),
								ems
										.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
								ems
										.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
								ems.getContentType(), ems.getId(),
								registeredDelivery,ems.getMessageType(),ems.getCommandCode());

				int i = 1;
				ems.setTotalSegments(vSubmits.size());

				for (Iterator it2 = vSubmits.iterator(); it2.hasNext(); i++) {
					SubmitSM ssm = (SubmitSM) it2.next();

					// Conglt: 20080715
					if (i == 1) {
						synchronized (Wait4ResponseTable) {
							this.Wait4ResponseTable.put((ssm
									.getSequenceNumber())
									+ "", ems);
						}
						Gateway.util.log(this.getClass().getName(),
								"{Add2Wait4ResponseTable}{ems="
										+ ssm.getSequenceNumber() + "}"
										+ "{RequestID=" + ems.getRequestId()
										+ "}");

					}
					// End conglt20080715
					// ********************************************//
					this.toSMSC.enqueue(ssm);
					
					try {
						sleep(400);
					}
					catch (Exception e) {
						// TODO: handle exception
					}
					
					// ********************************************//
				}

			} 
			else {
				// /////
				/*
				 * 20080530
				 */
				ByteBuffer otaData = null;
				boolean infoError = false;

				if (ems.getBytes() != null && ems.getBytes().length > 0) {
					otaData = new ByteBuffer(ems.getBytes());
				} else if (ems.getText() != null && ems.getText().length() > 0) {
					try {
						byte[] b = HexaTool.fromHexString(ems.getText());
						otaData = new ByteBuffer(b);
					} catch (IllegalArgumentException ex) {
						// System.out.println("fromHexString: " +
						// ex.getMessage());
						infoError = true;
					}
				} else {
					Gateway.util.log(this.getClass().getName(),
							"Both Info and Raw_Info are null");
					infoError = true;
				}

				if (infoError) {
					Gateway.util.log(this.getClass().getName(), "infoError: "
							+ ems.getUserId() + ":" + ems.getCommandCode()
							+ ":" + ems.getRequestId() + ":" + ems.getText());
					// System.out.println("    --> Move to send log.");
					// /dbTools.moveEMSFromSendQueueToSendLog(emsId);
					// /continue;
				} else {
					// ///////////////////////1
					Collection vSubmits = EMSTools
							.buildSubmitEMS(
									otaData,
									ems
											.getServiceIdEx(Constants.SERVICEID_FORMAT_SHORTCODE),
									ems
											.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL),
									ems.getContentType(), ems.getId(),
									registeredDelivery, ems.getMessageType(),
									ems.getCommandCode(), ems
											.getTotalSegments());

					int i = 1;
					/**
					 * DKTRUNG 0904098489 Update submit_date
					 */
					ems.setTotalSegments(vSubmits.size());
					synchronized (Wait4ResponseTable) {
						this.Wait4ResponseTable.put((ems.getId().intValue())
								+ "", ems);
					}
					Gateway.util.log(this.getClass().getName(),
							"{Add2Wait4ResponseTable}{ems=" + ems.getId() + "}"
									+ "{RequestID=" + ems.getRequestId() + "}");

					for (Iterator it2 = vSubmits.iterator(); it2.hasNext(); i++) {
						SubmitSM ssm = (SubmitSM) it2.next();
						// ********************************************//
						this.toSMSC.enqueue(ssm);
						// ********************************************//
					}

					// //////////////////////1
				}

				// ////////////////conglt20080530
			}

		} catch (Exception ex) {
			Gateway.util.logErr(this.getClass().getName(), "Err:"
					+ ex.getMessage() + "{ems=" + ems.getId() + "}"
					+ "{RequestID=" + ems.getRequestId() + "}{"
					+ ems.getUserId() + "}");
		}

		return numOfEms;
	}
}
