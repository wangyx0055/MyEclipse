package MostStep;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.SoapLongcheerMO;

import services.textbases.VnnLinksTextbaseTicket;
import services.textbases.LogValues;

import cs.ExecuteADVCR;

/**
 * Giamcan class.<br>
 * 
 * <pre>
 * ・</pre>
 * 
 * @author Vietnamnet I-Com TrungVD
 * @version 1.0
 */
public class Giamcan extends ContentAbstract {

	// Nơi lưu trữ các ticket
	private String directory = "C:/ticket";

	/**
	 * getMessages.<br>
	 * 
	 * <pre>
	 * 
	 * ◆ Processing order
	 * 
	 * ◆ Handle exception
	 *   ・
	 * </pre>
	 * 
	 * @param msgObject
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		int MILI_SECOND = 1000 * 60 * 60 * 24;

		try {

			directory = Constants._prop.getProperty("dirtextbase", directory);
			HashMap _option = new HashMap();
			Collection messages = new ArrayList();

			String options = keyword.getOptions();
			String GAME_ID = "";
			int NUM_SUBCODE = 0; // so luong subcode
			// String MTCS = "0";
			String GAME_TYPE = "";
			// Thue bao nhan
			String GIFF_MSG = "";
			// invalid message
			// String INV_MSG = "";

			String SUBCODE1 = "";
			String SUBCODE2 = "";
			String SUBCODETYPE = "0";
			// so dong
			int NUM_DAY = 0;
			int NUM_CNT = 0;
			String GIFF_USER = "-";
			String GIFF_USER_TELCO = "-";
			String DBCONTENT = "textbase";
			// 
			String SUCCESS_MSG = "";
			String service = msgObject.getKeyword();

			// 
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);

				// Phan tich option
				// gameid=ddd&numsubcode=0&

				GAME_ID = ((String) _option.get("game_id")).toUpperCase();

				NUM_SUBCODE = Integer.parseInt((String) _option
						.get("num_subcode"));
				NUM_DAY = Integer.parseInt((String) _option.get("num_day"));
				GAME_TYPE = (String) _option.get("game_type");// 1: tra hang
				// ngay
				// khong theo thu
				// tu, 2: tra hang
				// ngay theo thu tu
				GIFF_MSG = (String) _option.get("giff_msg");// Thong bao gui
				// tang:
				// Ban da gui tang thanh
				// cong den thue bao
				// $GIFF_USER
				SUBCODETYPE = ((String) _option.get("subcodetype"))
						.toUpperCase();
				NUM_CNT = Integer.parseInt((String) _option.get("num_cnt"));
				DBCONTENT = getString(_option, "dbcontent", DBCONTENT);
				SUCCESS_MSG = ((String) _option.get("success_msg"));
			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			/*  */
			String sInfo = msgObject.getUsertext();
			sInfo = replaceAllWhiteWithOne(sInfo.trim()).trim().toUpperCase();
			String sKeyword = msgObject.getKeyword();
			sKeyword = sKeyword.toUpperCase();
			String sServiceid = msgObject.getServiceid();

			// Lay sdt nguoi dung
			String USERID = msgObject.getUserid();
			String[] sTokens = sInfo.split(" ");
			// String CTX_MTCONTENT = "";
			Util.logger.sysLog(2, this.getClass().getName(), "sToken: "
					+ sTokens.length);
			Util.logger.sysLog(2, this.getClass().getName(), "NUM_SUBCODE: "
					+ NUM_SUBCODE);
			String infoid="";
			if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {
				infoid = "viettel";
			} else if (("VMS".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| "mobifone".equalsIgnoreCase(msgObject
							.getMobileoperator())) {
				infoid = "mobifone";
			} else if (("GPC".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| ("VINAPHONE".equalsIgnoreCase(msgObject
							.getMobileoperator()))) {
				infoid = "vinaphone";
			} else if (("GTEL".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| ("Beeline".equalsIgnoreCase(msgObject
							.getMobileoperator()))) {
				infoid = "beeline";
			} else {
				infoid = "other";
			}
			
			// log send MO		
			String temp1 = SoapMOMT.SendMO(msgObject);
			Util.logger.info("temp1____" + temp1);
			
			
			/*if (SoapLongcheerMO.getMessages(msgObject)) {
				Util.logger.info("OK");
			} else

				Util.logger.info("Chua post duoc");*/
		
			if ("other".equalsIgnoreCase(infoid)) {
				msgObject
						.setUsertext("Hien tai dich vu chua ho tro mang cua ban.Vui long quay tro lai sau.DTHT 1900571566");
				msgObject.setMsgtype(2);
				
				// log send MT		
				String temp2 = SoapMOMT.SendMT(msgObject);
				Util.logger.info("temp2____" + temp2);
				
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			/*  */
			if (sTokens.length > (NUM_SUBCODE + 1)) {
				Util.logger.sysLog(2, this.getClass().getName(),
						"Kiem tra xem co phai gui tang hay khong?*"
								+ sTokens[NUM_SUBCODE + 1] + "*");
				// Lay sdt nguoi dung.
				GIFF_USER = ValidISDNNew(sTokens[NUM_SUBCODE + 1]);

				// Ghi thong tin vao file log
				Util.logger.sysLog(2, this.getClass().getName(), "GIFF_USER?*"
						+ GIFF_USER + "*");

				// Kiem tra xem Telco cua sdt nguoi nhan
				if (!"-".equalsIgnoreCase(GIFF_USER)) {
					GIFF_USER_TELCO = getMobileOperatorNew(GIFF_USER, 2);
				}
				Util.logger.sysLog(2, this.getClass().getName(),
						"GIFF_USER_TELCO?*" + GIFF_USER_TELCO + "*");

			}

			// Subcode
			if (NUM_SUBCODE == 1) {
				SUBCODE1 = sTokens[1];
			}
			if (NUM_SUBCODE == 2) {
				SUBCODE1 = sTokens[1];
				SUBCODE2 = sTokens[2];
			}

		
			if ("-".equalsIgnoreCase(GIFF_USER_TELCO)) {
				int timesSend = 0;
				timesSend = getUserID(USERID);
				// Ghi vào csdl
				if (timesSend < 0) {
					saverequest(USERID);
					timesSend = 1;
				} else {
					timesSend = timesSend + 1;
					if ((timesSend % 3) == 1) {
						timesSend = 1;
					} else if ((timesSend % 3) == 2) {
						timesSend = 2;
					}
				}

				// timesSend = getUserID(USERID);
				// 
				if (timesSend == 1) {
					GAME_ID = GAME_ID + "1";
				} else if (timesSend == 2) {
					GAME_ID = GAME_ID + "2";
				} else if (timesSend == 3) {
					GAME_ID = GAME_ID + "3";
				}

				//
				String[] ALLCONTENTS = new String[NUM_DAY];
				ALLCONTENTS = getAllcontent(GAME_ID, SUBCODE1, SUBCODE2,
						NUM_DAY, GAME_TYPE, SUBCODETYPE, NUM_CNT, DBCONTENT);

				try {
					if (ALLCONTENTS.length > 1) {
						// Tao ticket
						String userticket = "";
						String telcoticket = "";
						userticket = USERID;
						telcoticket = msgObject.getMobileoperator();

						/* */
						Timestamp timeReceived = msgObject.getTTimes();
						Util.logger.info("RECEIVED TIME :" + timeReceived);

						Timestamp timeEndDate = getEndDate(USERID);
						Util.logger.info("END DATE :" + timeEndDate);

						if ((timesSend > 1)
								&& (!timeReceived.after(timeEndDate))) {

							for (int i = 0; i < ALLCONTENTS.length; i++) {

								String content = ALLCONTENTS[i];

								String[] mt2send = content.split("###");

								String date2send = "";
								// 
								Date date = new Date(timeEndDate.getTime()
										+ MILI_SECOND * (i + 1));
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								date2send = getCalendarString080000(calendar);

								Util.logger.sysLog(2,
										this.getClass().getName(),
										"ALLCONTENTS.length: "
												+ ALLCONTENTS.length);

								Util.logger.info("mt2send : " + mt2send.length);

								for (int j = 0; j < mt2send.length; j++) {

									if (!" ".equalsIgnoreCase(mt2send[j])) {
										Util.logger.sysLog(2, this.getClass()
												.getName(),
												"createReservationTicket: "
														+ mt2send[j]);

										VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
												.createReservationTicket(
														mt2send[j], userticket,
														sServiceid, date2send,
														telcoticket,
														telcoticket, service,
														msgObject
																.getRequestid()
																.toString());

										VnnLinksTextbaseTicket
												.storeReservationTicket(ticket,
														directory, userticket);
									}
								}
							}
							// 
							// timestamp
							Timestamp date1 = new Timestamp(timeEndDate
									.getTime()
									+ 7 * MILI_SECOND);
							updateEndDate(USERID, date1, timesSend);
							// 
							int day = timesSend - 1;
							msgObject
									.setUsertext("Ban dang trong qua trinh nhan tu van giam can cua tuan: "
											+ day
											+ ".Tu van giam can cua tuan tiep theo se dc gui den ban khi ket thuc tuan: "
											+ day);
							msgObject.setMsgtype(1);
							// log send MT		
							String temp2 = SoapMOMT.SendMT(msgObject);
							Util.logger.info("temp2____" + temp2);
							
							messages.add(new MsgObject(msgObject));

						} else {
							
							String[] mt2send1 = ALLCONTENTS[0].split("###");
							for (int i = 0; i < mt2send1.length; i++) {
								if (!"".equalsIgnoreCase(mt2send1[i])) {
									msgObject.setUsertext(mt2send1[i]);
									if (i == 0) {
										msgObject.setMsgtype(1);

									} else {
										msgObject.setMsgtype(0);
									}
									// log send MT		
									String temp2 = SoapMOMT.SendMT(msgObject);
									Util.logger.info("temp2____" + temp2);
									messages.add(new MsgObject(msgObject));
								}
							}
							
							for (int i = 1; i < ALLCONTENTS.length; i++) {

								String content = ALLCONTENTS[i];

								String[] mt2send = content.split("###");

								String date2send = "";
								
								Date date = new Date(timeReceived.getTime()
										+ MILI_SECOND * i);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								date2send = getCalendarString080000(calendar);

								Util.logger.sysLog(2,
										this.getClass().getName(),
										"ALLCONTENTS.length: "
												+ ALLCONTENTS.length);

								Util.logger.info("mt2send : " + mt2send.length);

								for (int j = 0; j < mt2send.length; j++) {

									if (!" ".equalsIgnoreCase(mt2send[j])) {
										Util.logger.sysLog(2, this.getClass()
												.getName(),
												"createReservationTicket: "
														+ mt2send[j]);

										VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
												.createReservationTicket(
														mt2send[j], userticket,
														sServiceid, date2send,
														telcoticket,
														telcoticket, service,
														msgObject
																.getRequestid()
																.toString());

										VnnLinksTextbaseTicket
												.storeReservationTicket(ticket,
														directory, userticket);
									}
								}
							}
						
							Timestamp date1 = new Timestamp(timeReceived
									.getTime()
									+ 6 * MILI_SECOND);
								Calendar calendar = Calendar.getInstance();
							calendar.setTime(date1);
							String date2 = getCalendarString080000(calendar);

							date1 = java.sql.Timestamp.valueOf(date2);

							updateEndDate(USERID, date1, timesSend);
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
					Util.logger.sysLog(2, this.getClass().getName(),
							"Exception:" + e.getMessage());
				}
				return messages;
			} else {
				SUCCESS_MSG = SUCCESS_MSG.replace("#1", USERID);
				sendGifMsg(sServiceid, GIFF_USER,
						GIFF_USER_TELCO.toUpperCase(), service, SUCCESS_MSG,
						msgObject.getRequestid());

				int timesSend = 0;
				timesSend = getUserID(GIFF_USER);
				if (timesSend < 0) {
					saverequest(GIFF_USER);
					timesSend = 1;
				} else {
					timesSend = timesSend + 1;
					if ((timesSend % 3) == 1) {
						timesSend = 1;
					} else if ((timesSend % 3) == 2) {
						timesSend = 2;
					}
				}

				timesSend = getUserID(GIFF_USER);
					if (timesSend == 1) {
					GAME_ID = GAME_ID + "1";
				} else if (timesSend == 2) {
					GAME_ID = GAME_ID + "2";
				} else if (timesSend == 3) {
					GAME_ID = GAME_ID + "3";
				}

				String[] ALLCONTENTS = new String[NUM_DAY];
				ALLCONTENTS = getAllcontent(GAME_ID, SUBCODE1, SUBCODE2,
						NUM_DAY, GAME_TYPE, SUBCODETYPE, NUM_CNT, DBCONTENT);

				try {
					if (ALLCONTENTS.length > 1) {
						// Tao ticket
						String userticket = "";
						String telcoticket = "";
						userticket = GIFF_USER;
						telcoticket = GIFF_USER_TELCO;

						Timestamp timeReceived = msgObject.getTTimes();
						Util.logger.info("RECEIVED TIME :" + timeReceived);

						Timestamp timeEndDate = getEndDate(GIFF_USER);
						Util.logger.info("END DATE :" + timeEndDate);

						if ((timesSend > 1)
								&& (!timeReceived.after(timeEndDate))) {

							for (int i = 0; i < ALLCONTENTS.length; i++) {

								String content = ALLCONTENTS[i];

								String[] mt2send = content.split("###");

								String date2send = "";
								Date date = new Date(timeEndDate.getTime()
										+ MILI_SECOND * (i + 1));
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								date2send = getCalendarString080000(calendar);

								Util.logger.sysLog(2,
										this.getClass().getName(),
										"ALLCONTENTS.length: "
												+ ALLCONTENTS.length);

								Util.logger.info("mt2send : " + mt2send.length);

								for (int j = 0; j < mt2send.length; j++) {

									if (!" ".equalsIgnoreCase(mt2send[j])) {
										Util.logger.sysLog(2, this.getClass()
												.getName(),
												"createReservationTicket: "
														+ mt2send[j]);

										VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
												.createReservationTicket(
														mt2send[j], userticket,
														sServiceid, date2send,
														telcoticket,
														telcoticket, service,
														msgObject
																.getRequestid()
																.toString());

										VnnLinksTextbaseTicket
												.storeReservationTicket(ticket,
														directory, userticket);
									}
								}
							}
							Timestamp date1 = new Timestamp(timeEndDate
									.getTime()
									+ 7 * MILI_SECOND);
							updateEndDate(GIFF_USER, date1, timesSend);
							int day = timesSend - 1;
							msgObject
									.setUsertext("Ban dang trong qua trinh nhan tu van giam can cua tuan: "
											+ day
											+ ".Tu van giam can cua tuan tiep theo se dc gui den ban khi ket thuc tuan: "
											+ day);
							msgObject.setMsgtype(0);
							msgObject.setUserid(GIFF_USER);
							
							// log send MT		
							String temp2 = SoapMOMT.SendMT(msgObject);
							Util.logger.info("temp2____" + temp2);
							
							messages.add(new MsgObject(msgObject));

						} else {
							String[] mt2send1 = ALLCONTENTS[0].split("###");
							for (int i = 0; i < mt2send1.length; i++) {
								if (!"".equalsIgnoreCase(mt2send1[i])) {
									sendGifMsg(sServiceid, GIFF_USER,
											GIFF_USER_TELCO.toUpperCase(),
											service, mt2send1[i], msgObject
													.getRequestid());
								}
							}
							/* Ghi vào ticket 6 MT tiếp theo */

							for (int i = 1; i < ALLCONTENTS.length; i++) {

								String content = ALLCONTENTS[i];

								String[] mt2send = content.split("###");
							String date2send = "";
								Date date = new Date(timeReceived.getTime()
										+ MILI_SECOND * i);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								date2send = getCalendarString080000(calendar);

								Util.logger.sysLog(2,
										this.getClass().getName(),
										"ALLCONTENTS.length: "
												+ ALLCONTENTS.length);

								Util.logger.info("mt2send : " + mt2send.length);

								for (int j = 0; j < mt2send.length; j++) {

									if (!" ".equalsIgnoreCase(mt2send[j])) {
										Util.logger.sysLog(2, this.getClass()
												.getName(),
												"createReservationTicket: "
														+ mt2send[j]);

										VnnLinksTextbaseTicket ticket = VnnLinksTextbaseTicket
												.createReservationTicket(
														mt2send[j], userticket,
														sServiceid, date2send,
														telcoticket,
														telcoticket, service,
														msgObject
																.getRequestid()
																.toString());

										VnnLinksTextbaseTicket
												.storeReservationTicket(ticket,
														directory, userticket);
									}
								}
							}
							Timestamp date1 = new Timestamp(timeReceived
									.getTime()
									+ 6 * MILI_SECOND);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date1);
							String date2 = getCalendarString080000(calendar);
					date1 = java.sql.Timestamp.valueOf(date2);

							updateEndDate(GIFF_USER, date1, timesSend);
						}

					}
						GIFF_MSG = GIFF_MSG.replace("#1", GIFF_USER);
					msgObject.setUsertext(GIFF_MSG);
					msgObject.setUserid(USERID);
					msgObject.setMsgtype(1);
					msgObject.setMobileoperator(msgObject.getMobileoperator());
					// log send MT		
					String temp2 = SoapMOMT.SendMT(msgObject);
					Util.logger.info("temp2____" + temp2);
					
					messages.add(new MsgObject(msgObject));
				} catch (Exception e) {
					e.printStackTrace();
					Util.logger.sysLog(2, this.getClass().getName(),
							"Exception:" + e.getMessage());
				}
				return messages;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			e.printStackTrace();
			return null;

		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}

	/**
	 * Sets &quot;virtual field&quot; value for all parameters
	 * 
	 * @param params
	 */
	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "&");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			// setParameter(key, value);
			_params.put(key, value);
		}

		return _params;

	}

	/*
	 * Thay nhieu dau _____ -> _
	 */
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

	/**
	 * ValidISDN.<br>
	 * 
	 * <pre>
	 * 
	 * </pre>
	 * 
	 * @param sISDN
	 * @return
	 */
	public String ValidISDN(String sISDN) {
		Util.logger.sysLog(2, this.getClass().getName(), "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.sysLog(2, this.getClass().getName(), "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "+" + tempisdn;
			} else {
				tempisdn = "+84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.sysLog(2, this.getClass().getName(), "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	// Lay so dien thoai
	public int getNumber(String subcode, int index) {
		try {
			return Integer.parseInt(subcode.substring(index, index + 1));
		} catch (Exception e) {
			return 0;
		}

	}

	public String convertsubcode(String subcode, String typesubcode, int numcnt) {
		if ("1".equalsIgnoreCase(typesubcode)) {
			return subcode.toUpperCase();
		} else if ("2".equalsIgnoreCase(typesubcode)) {
			int sum = 0;
			for (int i = 0; i < subcode.length(); i++) {
				sum += getNumber(subcode, i);
			}
			if (numcnt == 0) {
				return sum + "";
			} else {

				while (sum > numcnt) {
					String ssum = sum + "";
					sum = 0;
					for (int i = 0; i < ssum.length(); i++) {
						sum += getNumber(ssum, i);
					}
				}
				return sum + "";
			}
		} else if ("3".equalsIgnoreCase(typesubcode)) {
			int sum = 0;
			for (int i = 0; i < subcode.length(); i++) {
				if ((subcode.substring(i, i + 1).equalsIgnoreCase("A"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("J"))) {
					sum += 1;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("B"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("K"))) {
					sum += 2;
				}

				else if ((subcode.substring(i, i + 1).equalsIgnoreCase("C"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("L"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("U"))) {
					sum += 3;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("D"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("M"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("V"))) {
					sum += 4;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("E"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("N"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("W"))) {
					sum += 5;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("F"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("O"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("X"))) {
					sum += 6;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("G"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("P"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Y"))) {
					sum += 7;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("H"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Q"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Z"))) {
					sum += 8;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("I"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("R"))) {
					sum += 9;
				} else {
					sum += 0;
				}

			}
			if (numcnt == 0) {
				return sum + "";
			}

			while (sum > numcnt) {
				String ssum = sum + "";
				sum = 0;
				for (int i = 0; i < ssum.length(); i++) {
					sum += getNumber(ssum, i);
				}
			}
			return sum + "";
		}
		return subcode.toUpperCase();
	}

	public String getsubode(int idsubcode, String subcode1, String subcode2,
			String subcodetype, int numcnt) {
		if (idsubcode == 1) {
			if (!"".equalsIgnoreCase(subcode2)) {
				return subcode1.toUpperCase();
			} else {
				return convertsubcode(subcode1, subcodetype, numcnt);
			}

		} else {
			return convertsubcode(subcode2, subcodetype, numcnt);
		}
	}

	public String[] getAllcontent(String gameid, String subcode1,
			String subcode2, int numday, String gametype, String subcodetype,
			int numcnt, String dbcontent) {

		// Lay noi dung tra luon
		String lastid = "";
		String[] cnttemp = new String[numday + 1];
		String sqlSELECT = "SELECT content, id FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid.toUpperCase() + "'";

		if (!"".equalsIgnoreCase(subcode1)) {
			sqlSELECT = sqlSELECT + " AND upper(subcode1)='"
					+ getsubode(1, subcode1, subcode2, subcodetype, numcnt)
					+ "'";
		}

		if (!"".equalsIgnoreCase(subcode2)) {
			sqlSELECT = sqlSELECT + " AND upper(subcode2)='"
					+ getsubode(2, subcode1, subcode2, subcodetype, numcnt)
					+ "'";

		}

		String sqlORDER = "  ORDER BY rand() limit 1";

		for (int i = 0; i < numday + 1; i++) {
			String sqltemp = sqlSELECT;

			if ("2".equalsIgnoreCase(gametype)) {
				sqltemp = sqltemp + "  AND dayid =" + i;
			}

			if (!"".equalsIgnoreCase(lastid)) {
				sqltemp = sqltemp + " AND id not in(" + lastid + ") ";
			}

			// sqltemp = sqltemp + sqlORDER;
			Util.logger.info("dbcontent : " + dbcontent);
			String[] temp = getContent(sqltemp, dbcontent);
			cnttemp[i] = temp[0];
			if (!"".equalsIgnoreCase(temp[1])) {
				if ("".equalsIgnoreCase(lastid)) {
					lastid = temp[1];
				} else {
					lastid = lastid + "," + temp[1];
				}
			}
		}
		return cnttemp;
	}

	String[] getContent(String query, String dbcontent) {
		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(dbcontent);
			Util.logger.info("dbcontent is : " + dbcontent);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.sysLog(1, this.getClass().getName(),
					"getContent: queryStatement:" + query);

			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"getContent: Failed" + ex.getMessage());
			ex.printStackTrace();
			return record;
		} finally {
			dbpool.cleanup(connection);
		}

	}

	/**
	 * This method returns a string with the date in the following format
	 * "yyyy/mm/dd hh:mm:ss". This date represents the Calendar ogject passed as
	 * a parameter.
	 * 
	 * @param Calendar
	 *            calendar The Calendar object
	 * @return a string in the following format "yyyy/mm/dd hh:mm:ss"
	 * 
	 */
	public static String getCalendarString(Calendar calendar) {
		StringBuffer sb = new StringBuffer();
		int i;
		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		i = calendar.get(Calendar.MONTH) + 1;
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append("-");
		i = calendar.get(Calendar.DAY_OF_MONTH);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(" ");
		i = calendar.get(Calendar.HOUR_OF_DAY);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(":");
		i = calendar.get(Calendar.MINUTE);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(":");
		i = calendar.get(Calendar.SECOND);
		if (i < 10)
			sb.append("0");
		sb.append(i);

		return (sb.toString());
	}

	// return a string in the following format "yyyy/mm/dd hh:mm:ss"
	public static String Milisec2YYYYMMDD080000(long ts) {
		if (ts == 0) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts));
			return getCalendarString080000(calendar);
		}
	}

	// Lay time hien tai
	public static String getCalendarString080000(Calendar calendar) {
		StringBuffer sb = new StringBuffer();
		int i;

		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		i = calendar.get(Calendar.MONTH) + 1;
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append("-");
		i = calendar.get(Calendar.DAY_OF_MONTH);
		if (i < 10)
			sb.append("0");
		sb.append(i);
		sb.append(" 08:00:00");

		return (sb.toString());
	}

	private void sendGifMsg(String serviceid, String userid, String operator,
			String service, String mtcontent, BigDecimal requestid) {
		try {

			MsgObject msg = new MsgObject();
			msg.setContenttype(0);
			msg.setUserid(userid);
			msg.setMobileoperator(operator);
			msg.setMsgtype(0);
			msg.setRequestid(requestid);
			msg.setServiceid(serviceid);
			msg.setKeyword(service);
			msg.setUsertext(mtcontent);

			DBUtil.sendMT(msg);

		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);

			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			Util.logger.info("Utils.ValidISDN" + "Exception?*" + e.toString()
					+ "*");
			return "-";
		}
		return tempisdn;
	}

	public static String getMobileOperatorNew(String userid, int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("gateway");

			String query = "SELECT operator FROM icom_isdnseries WHERE prefix= substr('"
					+ userid + "',1, length(prefix)) ";
			if (type == 1 || type == 0) {
				query += " and type=" + type;
			}

			Vector result = DBUtil.getVectorTable(connection, query);
			for (int i = 0; i < result.size(); i++) {
				Vector item = (Vector) result.elementAt(i);
				tmpOperator = (String) item.elementAt(0);
			}

			return tmpOperator;
		} catch (Exception ex) {
			Util.logger.sysLog(LogValues.INFORMATIVE, "Utils",
					"getMobileOperator: Get MobileOpereator Failed"
							+ ex.toString());
			return tmpOperator;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ((temp == null) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	// Luu so lan da dc nhan hay gui cua 1 sdt
	// Luu lan dau tien.

	private static boolean saverequest(String userid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_giamcan(userid) VALUES ('"
					+ userid + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_giamcan");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static int getUserID(String userid) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT times FROM icom_giamcan WHERE userid= '"
				+ userid.toUpperCase() + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getInt(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
		return -1;
	}
private static boolean updateEndDate(String userid, Timestamp date,
			int timesSend) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
		String sqlUpdateEndDate = "UPDATE icom_giamcan SET end_date = '"
					+ date + "', times =" + timesSend + " WHERE userid = '"
					+ userid.toUpperCase() + "'";
			Util.logger.info(" UPDATE DATE: " + sqlUpdateEndDate);
			statement = connection.prepareStatement(sqlUpdateEndDate);
			if (statement.execute()) {
				Util.logger.error("Update end date of " + userid
						+ " to icom_giamcan");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static Timestamp getEndDate(String userid) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		Timestamp ts = java.sql.Timestamp.valueOf("2009-07-25 08:00:00");
		ResultSet rs = null;

		Timestamp ts1 = null;

		try {
			connection = dbpool.getConnectionGateway();

			String sqlEndDate = "SELECT end_date FROM icom_giamcan WHERE upper(userid) = '"
					+ userid.toUpperCase() + "'";

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return ts;
			}

			Util.logger.info(" SELECT END_DATE: " + sqlEndDate);
			statement = connection.prepareStatement(sqlEndDate);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					ts1 = rs.getTimestamp(1);
					Util.logger.info("End Date AAAAAAAA:" + ts1);
				}
			}
			return ts1;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return ts;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return ts;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
}
