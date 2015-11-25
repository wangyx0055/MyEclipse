package test;

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

import cs.ExecuteADVCR;

public class giamcan extends ContentAbstract {

	// Nơi lưu trữ các ticket
	private String directory = "C:/textbase/ticket";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String userid = "";
		int MILI_SECOND = 1000 * 60 * 60 * 24;

		try {

			directory = Constants._prop.getProperty("dirtextbase", directory);
			HashMap _option = new HashMap();
			Collection messages = new ArrayList();

			String options = keyword.getOptions();
			String GAME_ID = "";
			int NUM_SUBCODE = 0; // so luong subcode
			String MTCS = "0";
			String GAME_TYPE = "";
			// Thue bao nhan
			String GIFF_MSG = "";
			// invalid message
			String INV_MSG = "";
			// invalid subcode
			String INV_SUBCODE_MSG = "";
			String SUBCODE1 = "";
			String SUBCODE2 = "";
			String SUBCODETYPE = "0";
			// so dong
			int NUM_DAY = 0;
			// Gui ntn?
			int NUM_MINUTE_DELAY = 24 * 60;
			int NUM_CNT = 0;

			String GIFF_USER = "-";
			String GIFF_USER_TELCO = "-";
			String DBCONTENT = "textbase";
			// Thông báo thành công.
			String SUCCESS_MSG = "";
			
			String INV_GIFF_USER = "Ban da tn sai cu phap. Giam can hieu qua trong 3 tuan Soan tin: GC  gui 6754";
			
			
			String service = msgObject.getKeyword();

			// String motext = msgObject.getUsertext().replaceAll("'", "");
			String sInfo = msgObject.getUsertext();
			sInfo = replaceAllWhiteWithOne(sInfo.trim()).trim().toUpperCase();
			String sKeyword = msgObject.getKeyword();
			sKeyword = sKeyword.toUpperCase();
			String sServiceid = msgObject.getServiceid();

			// Lay sdt nguoi dung
			String USERID = msgObject.getUserid();
			String[] sTokens = sInfo.split(" ");
			String CTX_MTCONTENT = "";
			Util.logger.sysLog(2, this.getClass().getName(), "sToken: "
					+ sTokens.length);
			Util.logger.sysLog(2, this.getClass().getName(), "NUM_SUBCODE: "
					+ NUM_SUBCODE);

			// Neu so luong subcode lon hon chieu dai
			if ((NUM_SUBCODE + 1) > sTokens.length) {

				// Tin nhan sai cu phap
				CTX_MTCONTENT = INV_MSG;
				Util.logger.sysLog(2, this.getClass().getName(),
						"Tin nhan sai cu phap");
			} else {

				// Kiem tra xem co phai gui tang hay khong? neu co thi ghi sdt
				// nguoinhan vao csdl neu khong thi ghi sdt gui
				if (sTokens.length > (NUM_SUBCODE + 1)) {
					Util.logger.sysLog(2, this.getClass().getName(),
							"Kiem tra xem co phai gui tang hay khong?*"
									+ sTokens[NUM_SUBCODE + 1] + "*");
					// Lay sdt nguoi dung.
					GIFF_USER = ValidISDNNew(sTokens[NUM_SUBCODE + 1]);
					

					// Ghi thong tin vao file log
					Util.logger.sysLog(2, this.getClass().getName(),
							"GIFF_USER?*" + GIFF_USER + "*");
					

					// Kiem tra xem Telco cua sdt nguoi nhan
					if (!"-".equalsIgnoreCase(GIFF_USER)) {
						GIFF_USER_TELCO = getMobileOperatorNew(GIFF_USER, 2);
						userid = GIFF_USER;
					} else {
						// Thong bao gui tang sai
						sendGifMsg(sServiceid, USERID, msgObject.getMobileoperator(), sKeyword, INV_GIFF_USER , msgObject.getRequestid());
					}
					Util.logger.sysLog(2, this.getClass().getName(),
							"GIFF_USER_TELCO?*" + GIFF_USER_TELCO + "*");

					
				} else {
					userid = USERID;
				}
				
				// Ghi vao csdl
				// neu chua co thi insert, co roi thi update

				if (getUserID(userid) <= 0) {
					saverequest(userid);
				} else {
					updateTimes(userid);
				}
			}

			// Subcode
			if (NUM_SUBCODE == 1) {
				SUBCODE1 = sTokens[1];
			}
			if (NUM_SUBCODE == 2) {
				SUBCODE1 = sTokens[1];
				SUBCODE2 = sTokens[2];
			}
			
			try {
				/*
				 * if (getUserID(userid) == 1) { options =
				 * "game_id=Giamcantuan1&num_subcode=0&num_minute=1&num_day=6&mtcs=&game_type=2&subcodetype=0&num_cnt=0&giff_msg=Ban
				 * da gui tang thanh cong&inv_msg=Ban da nhan sai cu phap"; //
				 * GAME_ID = "Giamcantuan1"; } else if (getUserID(userid) == 2) { //
				 * GAME_ID = "Giamcantuan2"; options =
				 * "game_id=Giamcantuan2&num_subcode=0&num_minute=1&num_day=6&mtcs=&game_type=2&subcodetype=0&num_cnt=0&giff_msg=Ban
				 * da gui tang thanh cong&inv_msg=Ban da nhan sai cu phap"; }
				 * else if (getUserID(userid) == 3) { options =
				 * "game_id=Giamcantuan3&num_subcode=0&num_minute=1&num_day=6&mtcs=&game_type=2&subcodetype=0&num_cnt=0&giff_msg=Ban
				 * da gui tang thanh cong den so thue bao #1&inv_msg=Ban da nhan sai cu
				 * phap&success_msg=bạn da duoc so dt #1 gui tang huong dan giam
				 * can. cai dat GPRS tu dong Soan tin: GPRS gui 6754"; //
				 * GAME_ID = "Giamcantuan3"; }
				 */

				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);

				// Phan tich option
				// gameid=ddd&numsubcode=0&

				GAME_ID = ((String) _option.get("game_id")).toUpperCase();

				// Số lần đã gửi.
				int timesSend = 0;
				timesSend = getUserID(userid);

				if (timesSend == 1) {
					// options =
					// "game_id=Giamcantuan1&num_subcode=0&num_minute=1&num_day=6&mtcs=&game_type=2&subcodetype=0&num_cnt=0&giff_msg=Ban
					// da gui tang thanh cong&inv_msg=Ban da nhan sai cu phap";
					// GAME_ID = "Giamcantuan1";
					GAME_ID = GAME_ID + "1";
				} else if (timesSend == 2) {
					GAME_ID = GAME_ID + "2";
					// GAME_ID = "Giamcantuan2";
					// options =
					// "game_id=Giamcantuan2&num_subcode=0&num_minute=1&num_day=6&mtcs=&game_type=2&subcodetype=0&num_cnt=0&giff_msg=Ban
					// da gui tang thanh cong&inv_msg=Ban da nhan sai cu phap";
				} else if (timesSend == 3) {
					GAME_ID = GAME_ID + "3";
					// options =
					// "game_id=Giamcantuan3&num_subcode=0&num_minute=1&num_day=6&mtcs=&game_type=2&subcodetype=0&num_cnt=0&giff_msg=Ban
					// da gui tang thanh cong&inv_msg=Ban da nhan sai cu phap";
					// GAME_ID = "Giamcantuan3";
				}
				NUM_SUBCODE = Integer.parseInt((String) _option
						.get("num_subcode"));
				NUM_DAY = Integer.parseInt((String) _option.get("num_day"));
				NUM_MINUTE_DELAY = Integer.parseInt((String) _option
						.get("num_minute"));
				// num_minute
				MTCS = (String) _option.get("mtcs");
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
				INV_MSG = (String) _option.get("inv_msg");
				SUBCODETYPE = ((String) _option.get("subcodetype"))
						.toUpperCase();
				NUM_CNT = Integer.parseInt((String) _option.get("num_cnt"));
				// DBCONTENT = getString(_option, "dbcontent", DBCONTENT);
				DBCONTENT = "textbase";
				SUCCESS_MSG = ((String) _option.get("success_msg"));
			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			// Lấy toàn bộ nội dung của tuần
			String[] ALLCONTENTS = new String[NUM_DAY];

			ALLCONTENTS = getAllcontent(GAME_ID, SUBCODE1, SUBCODE2, NUM_DAY,
					GAME_TYPE, SUBCODETYPE, NUM_CNT, DBCONTENT);

			// Util.logger.sysLog(2, this.getClass().getName(),
			// "ALLCONTENTS.length: la lalalaa " + ALLCONTENTS.length);
			// Util.logger.info("DAY REMAIN la " + getUserID(userid));

			try {
				// Số lần đc gửi tặng và nhận của userid
				int times = 0;
				times = getUserID(userid);

				if (ALLCONTENTS.length > 1) {
					// Tao ticket
					String userticket = "";
					String telcoticket = "";
					if ("-".equalsIgnoreCase(GIFF_USER_TELCO)) {
						userticket = USERID;
						telcoticket = msgObject.getMobileoperator();
					} else {
						userticket = GIFF_USER;
						telcoticket = GIFF_USER_TELCO;
					}

					
					// Kiem tra so lan gui ??? neu > 2, 3 và thời gian hiện tại.
					// Nếu thời gian hiện tại > thời gian kết thúc gửi tin thì
					// thông báo
					// Sẽ gửi vào sau khi kết thúc tuần.
					// Lấy thời gian tin nhắn gửi tới.
					Timestamp timeReceived = msgObject.getTTimes();

					// Util.logger.info("THOI GIAN NHAN TIN LA :" +
					// timeReceived);
					// Lấy thời gian kết thúc gửi tin nhắn của tuần i ( Lưu
					// trong database)
					Timestamp timeEndDate = getEndDate(userid);
					// Util.logger.info("THOI GIAN NHAN TIN LA :" +
					// timeEndDate);

					// Nếu số lần gửi của thuê bao > 1 và số thời gian nhận tin
					// < thời gian gửi tin thì
					if ((times > 1) && (!timeReceived.after(timeEndDate))) {

						for (int i = 0; i < ALLCONTENTS.length; i++) {

							String content = ALLCONTENTS[i];

							String[] mt2send = ALLCONTENTS[i].split("###");

							// String date2send = getTimebyMinute(i *
							// NUM_MINUTE_DELAY);
							String date2send = "";
							// Lấy sau thời gian gửi tin 1 ngày
							Date date = new Date(timeEndDate.getTime()
									+ MILI_SECOND * (i + 1));
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							date2send = getCalendarString080000(calendar);

							Util.logger
									.sysLog(2, this.getClass().getName(),
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
													telcoticket, telcoticket,
													service, msgObject
															.getRequestid()
															.toString());

									VnnLinksTextbaseTicket
											.storeReservationTicket(ticket,
													directory, userticket);
								}
							}
						}
						// Cập nhật vào csdl thời gian gửi cuối có dạng
						// timestamp
						Timestamp date1 = new Timestamp(timeEndDate.getTime()
								+ 7 * MILI_SECOND);
						updateEndDate(userid, date1);

					} else {
						/*
						 * Số lần gửi tin nhắn = 1 tức là gửi lần đầu tiên hoặc
						 * gửi lần 2, 3 nhưng thời gian gửi tuần 1 đã hết
						 */
						for (int i = 1; i < ALLCONTENTS.length; i++) {

							String content = ALLCONTENTS[i];

							String[] mt2send = ALLCONTENTS[i].split("###");

							// Thời gian gửi
							// String date2send = getTimebyMinute(i
							// * NUM_MINUTE_DELAY);
							String date2send = "";
							// Lấy sau thời gian nhận được MO thêm ngày để
							// render vào ticket
							Date date = new Date(timeReceived.getTime()
									+ MILI_SECOND * i);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							date2send = getCalendarString080000(calendar);

							Util.logger
									.sysLog(2, this.getClass().getName(),
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
													telcoticket, telcoticket,
													service, msgObject
															.getRequestid()
															.toString());

									VnnLinksTextbaseTicket
											.storeReservationTicket(ticket,
													directory, userticket);
								}
							}
						}
						// Update thời gian nhận cuối cùng vào time end_date
						Timestamp date1 = new Timestamp(timeReceived.getTime()
								+ 7 * MILI_SECOND);
						// Chuyển sang định dạng 08:00:00
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date1);
						String date2 = getCalendarString080000(calendar);

						// Chuyển sang định dạng timestamp
						date1 = java.sql.Timestamp.valueOf(date2);

						// Cập nhật thời gian sẽ gửi
						updateEndDate(userid, date1);
					}

					// / NEW
					
					if ("-".equalsIgnoreCase(GIFF_USER_TELCO)) {
						// CTX_MTCONTENT = ALLCONTENTS[1];
						CTX_MTCONTENT = ALLCONTENTS[0];

					} else {
						// Gửi ngày đầu của tuần.
						SUCCESS_MSG = SUCCESS_MSG.replace("#1", USERID);
						// Thông báo thuê bao xxx đã được thuê bao GIFF_USER gửi
						// tặng

						sendGifMsg(sServiceid, GIFF_USER, GIFF_USER_TELCO.toUpperCase(),
								service, SUCCESS_MSG, msgObject.getRequestid());

						CTX_MTCONTENT = GIFF_MSG;
						CTX_MTCONTENT = CTX_MTCONTENT.replace("#1", GIFF_USER);

						if (times == 1) {
							String[] mt2send = ALLCONTENTS[0].split("###");
							for (int i = 0; i < mt2send.length; i++) {
								if (!"".equalsIgnoreCase(mt2send[i])) {
									sendGifMsg(sServiceid, GIFF_USER,
											GIFF_USER_TELCO.toUpperCase(), service,
											mt2send[i], msgObject
													.getRequestid());
								}
							}
						}
					}
					// Nếu nội dung = ""
					if ("".equalsIgnoreCase(ALLCONTENTS[0])) {
						CTX_MTCONTENT = INV_SUBCODE_MSG;
					}

					Util.logger.sysLog(2, this.getClass().getName(),
							"CTX_MTCONTENT:" + CTX_MTCONTENT);

					// mt2 luu vao ticket??
					// Neu lan thu 2 ma van trong time gui cua lan 1
					String[] mt2send = CTX_MTCONTENT.split("###");

					// Gửi tin thông báo khi có MO đến
					if ((times > 1) && (!timeReceived.after(timeEndDate))) {
						// msgObject.setUsertext(mt2send[i]);
						int day = times - 1;
						msgObject
								.setUsertext("Ban dang trong qua trinh nhan tu van giam can cua tuan: "
										+ day
										+ ".Tu van giam can cua tuan tiep theo se dc gui den ban khi ket thuc tuan: "
										+ day);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
					} else {
						for (int i = 0; i < mt2send.length; i++) {
							if (!"".equalsIgnoreCase(mt2send[i])) {
								msgObject.setUsertext(mt2send[i]);
								if (i == 0) {
									msgObject.setMsgtype(1);

								} else {
									msgObject.setMsgtype(0);
								}
								messages.add(new MsgObject(msgObject));
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
						+ e.getMessage());
			}

			return messages;

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
			// TODO: handle exception
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

				// record = (String[]) result.get(0);

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

	String getTimebyMinute(int iminute) {
		long milliSecond = System.currentTimeMillis();

		long lNewtime = milliSecond + iminute * 60 * 1000;

		// /* return date with format: dd/mm/yyyy */
		if (iminute < 24 * 60) {
			return Milisec2YYYYMMDDHHMISSS(lNewtime);
		} else {
			return Milisec2YYYYMMDD080000(lNewtime);
		}

	}

	// return a string in the following format "yyyy/mm/dd hh:mm:ss"
	public static String Milisec2YYYYMMDDHHMISSS(long ts) {
		if (ts == 0) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts));
			return getCalendarString(calendar);

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
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			// TODO: handle exception
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

			String sqlInsert = "INSERT INTO icom_giamcan( userid) VALUES ('"
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

	// Cập nhật số ngày còn phải gửi vào csdl.
	private static boolean updateTimes(String userid) {

		Connection connection = null;
		PreparedStatement statement2 = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			int times = getUserID(userid);
			times = times + 1;

			// Xu ly truong hop nguoi gui so tin nhan > 3
			if ((times % 3) == 1) {
				times = 1;
			} else if ((times % 3) == 2) {
				times = 2;
			}

			// Update so lan giam can.
			String sqlUpdate = "UPDATE icom_giamcan SET times = " + times
					+ " WHERE userid = '" + userid.toUpperCase() + "'";
			Util.logger.info("UPDATE: " + sqlUpdate);
			statement2 = connection.prepareStatement(sqlUpdate);
			if (statement2.execute()) {
				Util.logger.error("Update times of " + userid
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
			dbpool.cleanup(statement2);
			dbpool.cleanup(connection);
		}
	}

	// Cập nhật thời gian kết thúc gửi tin vào database icom_giamcan
	private static boolean updateEndDate(String userid, Timestamp date) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update thời gian kết thúc quá trình gửi tin nhắn
			String sqlUpdateEndDate = "UPDATE icom_giamcan SET end_date = '"
					+ date + "' WHERE userid = '" + userid.toUpperCase() + "'";
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

	// Cập nhật thời gian kết thúc gửi tin vào database icom_giamcan
	private static Timestamp getEndDate(String userid) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		Timestamp ts = java.sql.Timestamp.valueOf("2009-07-25 08:00:00");
		ResultSet rs = null;
		String sqlEndDate = "SELECT end_date FROM icom_giamcan WHERE upper(userid) = '"
				+ userid.toUpperCase() + "'";
		Timestamp ts1 = null;

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return ts;
			}

			Util.logger.info(" SELECT END DATE: " + sqlEndDate);
			statement = connection.prepareStatement(sqlEndDate);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					// Lấy thời gian end date
					ts1 = rs.getTimestamp(1);
					Util.logger.error("SELECT end date of " + userid
							+ " to icom_giamcan");
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
