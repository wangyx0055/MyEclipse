package dangky;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import com.vmg.soap.mo.sendXMLRING;

import cs.ExecuteADVCR;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * </pre>
 * 
 * @author Vietnamnet I-Com HaPTT
 * @version 1.0
 */
public class Nhacchotructiep3g extends ContentAbstract {

	/* First String */
	private static String urlring = "http://203.162.71.165:8686/SetRingBack.asmx?";
	private static String vinaphoneGuide = "Buoc1:Dang ki su dung,soan:DK gui 9194.Buoc 2:Chon bai hat,soan:TUNE maso gui 9194.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String mobifoneGuide = "Buoc1:Dang ki su dung,soan:DK gui 9224.Buoc 2:Chon bai hat,soan:CHON maso gui 9224.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String beelineGuide = "De tai bai hat.Soan tin:CHON masobaihat gui 1221.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";

	private static String viettelGuide = "Buoc1:Dang ki su dung,soan:DK gui 1221.Buoc 2:Chon bai hat,soan:BH maso gui 1221.CSKH:1900571566.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private String[] musicHot_Viettel = { "100 yeu em - 633395",
			"Cau vong khuyet - 633519", "Con duong mua - 633407",
			"Dong thoi gian - 633655", "Let you go - 633429",
			"Gia vo yeu - 633423", "Don coi 1 - 633364",
			"The gioi to that to - 633448",
			"Hot boy Thue bao het tien - 63318",
			"Tai em yeu don phuong = 633902" };
	private static final String[] musicHot_Mobifone = { "Let you go - 5201094",
			"Hoa Thien Dieu - 5201026", "Neu - 5201014", "Boi roi - 520364",
			"Loi nguyen - 520375", "Con duong mua - 520369",
			"Doi mat - 520370", "Boi roi - 520364", "Don coi - 520371",
			"Ngoi nha tuyet trang - 520971" };
	/* Beeline 10 bai hat */
	private static final String[] musicHot_Beeline = {
			"Dau mot lan de biet yeu mot lan - 651047",
			"Teen vong co - 655237", "Ngoi nha hanh phuc 1 - 652956",
			"30 ngay yeu - 650017", "Con duong tinh yeu 1 - 650890",
			"Nho Anh May Mua - 653103", "Mat uot mi cay - 652501",
			"Nang gio va la - 652793", "Co nang hay ghen - 650843",
			"Nang - 652786" };

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		String numberid = "8751";
		String ismtadd = "1";
		try {
			Collection messages = new ArrayList();

			String infoid = "HOT";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";
			String singname = "ngaytetqueem";
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String dtbase = "gateway";
			String last_msg = "MCA tencasy gui 8751.DTHT 1900571566";
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				last_msg = getString(_option, "last_msg", last_msg);
				dtbase = getString(_option, "dtbase", dtbase);
				numberid = getString(_option, "numberid", numberid);
				singname = getString(_option, "singname", singname);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			/* kiem tra thue bao khach hang */
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
			} else {
				infoid = "other";
			}

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			// lay so thue bao nguoi gui
			String mt1 = "";
			String userid = msgObject.getUserid();
			/* lay noi dung gui */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext()
					.toUpperCase());
			String operator = msgObject.getMobileoperator();
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			String serviceid = msgObject.getServiceid();
			BigDecimal request_id = msgObject.getRequestid();
			/*
			 * nc ten bai hat
			 */

			// MT1
			if (infoid.equalsIgnoreCase("mobifone")) {

				String subTokens = singname;

				Util.logger.info("SubTokens : " + subTokens);

				String subTokens2 = subTokens.replace(" ", "");
				String[] resultMusicID = new String[2];
				// 0 la ten bai hat, 1 ma ma bai hat
				resultMusicID = findMusicID(subTokens2, infoid);
				if ("".equalsIgnoreCase(resultMusicID[1])) {
					resultMusicID = findMusicIDlike(subTokens2, infoid);
				}
				// MT1
				if (!"".equalsIgnoreCase(resultMusicID[1])) {
					Random iRandom = new Random();
					int imusic = iRandom.nextInt(10);
					if ("viettel".equalsIgnoreCase(infoid)) {
						if (musicHot_Viettel[imusic]
								.startsWith(resultMusicID[0].trim())) {
							if (imusic == musicHot_Viettel.length) {
								imusic = imusic - 1;
							} else {
								imusic = imusic + 1;
							}
						}
						resultMusicID[1] = "De tai "
								+ resultMusicID[0]
								+ " ban soan: B1: Soan DK gui 1221.B2: Soan BH "
								+ resultMusicID[1]
								+ " gui 1221.Bai hat HOT nhat trong thang: "
								+ musicHot_Viettel[imusic];
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						if (musicHot_Mobifone[imusic]
								.startsWith(resultMusicID[0].trim())) {
							if (imusic == musicHot_Mobifone.length) {
								imusic = imusic - 1;
							} else {
								imusic = imusic + 1;
							}
						}
						resultMusicID[1] = "De tai "
								+ resultMusicID[0]
								+ " ban soan: B1: Soan DK gui 9224.B2: Soan CHON "
								+ resultMusicID[1]
								+ " gui 9224.Bai hat HOT nhat trong thang: "
								+ musicHot_Mobifone[imusic];
					} else if ("beeline".equalsIgnoreCase(infoid)) {

						// New mang Beeline
						if (musicHot_Beeline[imusic]
								.startsWith(resultMusicID[0].trim())) {
							if (imusic == musicHot_Beeline.length) {
								imusic = imusic - 1;
							} else {
								imusic = imusic + 1;
							}
						}
						resultMusicID[1] = "De tai " + resultMusicID[0]
								+ " ban soan: CHON " + resultMusicID[1]
								+ " gui 1221.Bai hat HOT nhat trong thang: "
								+ musicHot_Beeline[imusic];
					}
					if (resultMusicID[1].length() > 160) {
						String[] subString = new String[2];
						subString[0] = resultMusicID[1].substring(0, 159);
						subString[1] = resultMusicID[1].substring(160);
						for (int k = 0; k < 2; k++) {
							if (!"".equalsIgnoreCase(subString[k])) {
								msgObject.setUsertext(subString[k]);
								if (k == 0) {
									msgObject.setMsgtype(1);
								} else {
									msgObject.setMsgtype(0);
								}
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(1000);
							}
						}

					} else {

						msgObject.setUsertext(resultMusicID[1]);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						// messages.add(new MsgObject(ms//gObject));
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

					}

					// tim danh sach bai hat hot
					// Kiem tra so lan gui tin cua khach hang
					int time2send = getUserID(userid, 1);
					String lastid = "";
					if (time2send >= 1) {
						lastid = getMusicID(userid, 1);
					} else {
						// Ghi lai danh sach khach hang da gui
						saverequest(userid, 1);
					}

					// Lay danh sach cac bai hat HOT
					String musichotsend = findListofMusic(userid, infoid,
							lastid, 1);

					String[] listsendClient = splitString(musichotsend);

					if (!"".equalsIgnoreCase(listsendClient[0])) {
						msgObject.setUsertext(listsendClient[0]);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						// messages.add(new MsgObject(msgObject));
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}

					// Send MT4 cho khach hang
					String sendtoClient = "Ngoai nhung BH ban vua nhan duoc,van con hang ngan BH khac tren "
							+ numberid
							+ ".De tiep tuc nhan duoc ma so BH.Soan:"
							+ last_msg;
					msgObject.setUsertext(sendtoClient);
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					// messages.add(new MsgObject(msgObject));
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);

					return null;

				} else {

					// Bai hat chua co trong he thong.
					// Send MT 1
					String info2Client = "";
					if ("viettel".equalsIgnoreCase(infoid)) {
						info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 1221.Buoc2, soan: BH maso gui 1221. Tang ban ma so BH HOT:";
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 9194.Buoc2, soan: TUNE maso gui 9194. Tang ban ma so BH HOT:";
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat.Buoc1, soan DK gui 9224.Buoc2, soan: CHON maso gui 9224. Tang ban ma so BH HOT:";
					} else if ("beeline".equalsIgnoreCase(infoid)) {
						info2Client = "Bai hat ban thich hien tai chua co trong he thong. De tai bai hat, soan: CHON masobaihat gui 1221. Tang ban ma so BH HOT:";
					}

					msgObject.setUsertext(info2Client);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					// Send MT 2 - 3
					// tim danh sach bai hat hot
					// Kiem tra so lan gui tin cua khach hang
					int time2send = getUserID(userid, 1);
					String lastid = "";
					if (time2send >= 1) {
						lastid = getMusicID(userid, 1);
					} else {
						// Ghi lai danh sach khach hang da gui
						saverequest(userid, 1);
					}

					// Lay danh sach cac bai hat HOT
					String musichotsend = findListofMusic(userid, infoid,
							lastid, 1);

					String[] listsendClient = splitString(musichotsend);

					if (!"".equalsIgnoreCase(listsendClient[0])) {
						msgObject.setUsertext(listsendClient[0]);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						// messages.add(new MsgObject(msgObject));
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}

					// Send MT4 cho khach hang
					String sendtoClient = "Ngoai nhung BH ban vua nhan duoc, van con hang ngan BH khac tren "
							+ numberid
							+ ".De tiep tuc nhan duoc ma so BH.Soan: "
							+ last_msg;
					msgObject.setUsertext(sendtoClient);
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
					return null;
				}
			} else {
				Random iRandom = new Random();
				int imusic = iRandom.nextInt(10);

				String giff_userid = "";
				String giff_telco = "";
				boolean giff = false;

				if (sTokens.length > 2) {
					giff_userid = ValidISDN(sTokens[sTokens.length - 1]);
					if (!giff_userid.equalsIgnoreCase("-")) {
						giff_telco = getMobileOperatorNew(giff_userid);

						if ("-".equals(giff_telco)) {
							giff = false;
						} else {
							giff = true;
						}

					}
				}

				String subTokens = singname;
			
				Util.logger.info("SubTokens : " + subTokens);
				String subTokens2 = subTokens.replace(" ", "");
				String[] resultMusicID = new String[2];
				// tach so dien thoai ra khoi chuoi

				// 0 la ten bai hat, 1 ma ma bai hat
				Util.logger.info("Tim ma bai hat");

				resultMusicID = findMusicID(subTokens2, infoid);
				if ("".equalsIgnoreCase(resultMusicID[0])) {
					resultMusicID = findMusicIDlike(subTokens2, infoid);
				}
				if (!"".equalsIgnoreCase(resultMusicID[0])) {
					if ("viettel".equalsIgnoreCase(infoid)) {

						try {
							mt1 = "De tai "
									+ resultMusicID[0]
									+ " ban soan: B1: Soan DK gui 1221.B2: Soan BH "
									+ resultMusicID[1]
									+ " gui 1221.. Tang ban TOP bai hat HOT";
							String result = sendXMLRING.SendXML(urlring,
									"SetTone", userid, "", resultMusicID[1],
									now());
							Util.logger.info("result" + result);
							if (result.equalsIgnoreCase("501")) {
								mt1 = "Bai hat da co trong bo suu tap cua ban";
								Util.logger
										.info("Bai hat da co trong bo suu tap. Tang ban TOP bai hat HOT:");
							} else if (result.equalsIgnoreCase("502")) {
								mt1 = "Bai hat khong ton tai cho mang so dien thoai dang dung. Tang ban TOP bai hat HOT:";
								Util.logger
										.info("Bai hat khong ton tai cho mang so dien thoai dang dung");
							} else if (result.equalsIgnoreCase("0")) {
								mt1 = "Ban cai dat bai hat "
										+ resultMusicID[0]
										+ " thanh cong.  Tang ban TOP bai hat HOT:";
								Util.logger
										.info("Bo suu tap cua ban da day.Xin vui long xoa bot neu muon cai bai hat moi.Tang ban TOP bai hat HOT:");

							} else if (result.equalsIgnoreCase("101")) {
								mt1 = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:";
								Util.logger
										.info("Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:");

							} else if (result.equalsIgnoreCase("504")) {
								mt1 = "Bo suu tap cua ban da day.Xin vui long xoa bot neu muon cai bai hat moi.Tang ban TOP bai hat HOT:";
								Util.logger
										.info("Bo suu tap cua ban da day.Xin vui long xoa bot neu muon cai bai hat moi.Tang ban TOP bai hat HOT:");

							}

						} catch (Exception ex) {
							Util.logger.error("Khong thanh cong");

						}

					} else if ("vinaphone".equalsIgnoreCase(infoid)) {

						try {
							mt1 = "De tai "
									+ resultMusicID[0]
									+ " ban soan: B1: Soan DK gui 9194.B2: Soan TUNE "
									+ resultMusicID[1]
									+ " gui 9194.. Tang ban TOP bai hat HOT";
							String result = sendXMLRING.SendXML(urlring,
									"SetTone", userid, "", resultMusicID[1],
									now());
							Util.logger.info("result" + result);

							if (result.equalsIgnoreCase("501")) {
								mt1 = "Bai hat da co trong bo suu tap cua ban";
								Util.logger
										.info("Bai hat da co trong bo suu tap. Tang ban TOP bai hat HOT:");
							} else if (result.equalsIgnoreCase("0")) {
								mt1 = "Ban cai dat bai hat "
										+ resultMusicID[0]
										+ " thanh cong.  Tang ban TOP bai hat HOT:";
								Util.logger.info("Cai dat nhac cho thanh cong");
								msgObject.setUsertext(mt1);
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(1000);
								return null;

							} else if (result.equalsIgnoreCase("101")) {
								mt1 = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:";
								Util.logger.info("Cai dat nhac cho thanh cong");

							} else if (result.equalsIgnoreCase("504")) {
								mt1 = "Bo suu tap cua ban da day.Xin vui long xoa bot neu muon cai bai hat moi.Tang ban TOP bai hat HOT:";
								Util.logger.info("Cai dat nhac cho thanh cong");

							}

						} catch (Exception ex) {
							Util.logger.error("Khong thanh cong");

						}
					} else if ("mobifone".startsWith(infoid)) {
						try {
							mt1 = "Ban cai dat nhac cho chua thanh cong, xin vui long thu lai. Tang ban TOP bai hat HOT";
							String result = sendXMLRING.SendXML(urlring,
									"SetTone", userid, "", resultMusicID[1],
									now());
							Util.logger.info("result" + result);
							if (result.equalsIgnoreCase("200")) {
								try {
									String result2 = sendXMLRING.SendXML(
											urlring, "GetValidcode", userid,
											"", "", now());

									if (result2.equalsIgnoreCase("0")) {
										String result3 = sendXMLRING.SendXML(
												urlring, "SetTone", userid, "",
												resultMusicID[1], now());
										if (result3.equalsIgnoreCase("0")) {
											mt1 = "Ban cai dat bai hat "
													+ resultMusicID[0]
													+ " thanh cong.  Tang ban TOP bai hat HOT:";
											Util.logger
													.info("Cai dat nhac cho thanh cong");

										} else if (result3
												.equalsIgnoreCase("501")) {
											mt1 = "Bai hat da co trong bo suu tap cua ban";
											Util.logger
													.info("Bai hat da co trong bo suu tap. Tang ban TOP bai hat HOT:");
										} else if (result
												.equalsIgnoreCase("504")) {
											mt1 = "Bo suu tap cua ban da day.Xin vui long xoa bot neu muon cai bai hat moi.Tang ban TOP bai hat HOT:";
											Util.logger
													.info("Cai dat nhac cho thanh cong");

										} else if (result
												.equalsIgnoreCase("101")) {
											mt1 = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:";
											Util.logger
													.info("Cai dat nhac cho thanh cong");

										}

									}

								} catch (Exception ex) {
									Util.logger.error("Khong thanh cong");

								}
							} else if (result.equalsIgnoreCase("501")) {
								mt1 = "Bai hat da co trong bo suu tap cua ban";
								Util.logger
										.info("Bai hat da co trong bo suu tap. Tang ban TOP bai hat HOT:");
							} else if (result.equalsIgnoreCase("0")) {
								mt1 = "Ban cai dat bai hat "
										+ resultMusicID[0]
										+ " thanh cong.  Tang ban TOP bai hat HOT:";

								Util.logger.info("Cai dat nhac cho thanh cong");

							} else if (result.equalsIgnoreCase("101")) {
								mt1 = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:";
								Util.logger.info("Cai dat nhac cho thanh cong");

							} else if (result.equalsIgnoreCase("504")) {
								mt1 = "Bo suu tap cua ban da day.Xin vui long xoa bot neu muon cai bai hat moi.Tang ban TOP bai hat HOT:";
								Util.logger.info("Cai dat nhac cho thanh cong");

							}

						} catch (Exception ex) {
							Util.logger.error("Khong thanh cong");

						}

					}
					if (!mt1.equalsIgnoreCase("")) {
						msgObject.setUsertext(mt1);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					}
					// tim danh sach bai hat hot
					// Kiem tra so lan gui tin cua khach hang
					int time2send = getUserID(userid, 1);
					String lastid = "";
					if (time2send >= 1) {
						lastid = getMusicID(userid, 1);
					} else {
						// Ghi lai danh sach khach hang da gui
						saverequest(userid, 1);
					}

					// Lay danh sach cac bai hat HOT
					String musichotsend = findListofMusic(userid, infoid,
							lastid, 1)
							+ ".DTHT 1900571566";

					if (!"".equalsIgnoreCase(musichotsend)) {
						msgObject.setUsertext(musichotsend);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}

					return null;
				} else {
					// Bai hat chua co trong he thong.
					// Send MT 1
					String info2Client = "";
					info2Client = "Bai hat ban thich hien tai chua co trong he thong.Tang ban top bai hat HOT:";

					msgObject.setUsertext(info2Client);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					// Send MT 2
					// tim danh sach bai hat hot
					// Kiem tra so lan gui tin cua khach hang
					int time2send = getUserID(userid, 1);
					String lastid = "";
					if (time2send >= 1) {
						lastid = getMusicID(userid, 1);
					} else {
						// Ghi lai danh sach khach hang da gui
						saverequest(userid, 1);
					}

					// Lay danh sach cac bai hat HOT
					String musichotsend = findListofMusic(userid, infoid,
							lastid, 1)
							+ ".DTHT 1900571566";

					if (!"".equalsIgnoreCase(musichotsend)) {
						msgObject.setUsertext(musichotsend);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}

					return null;
				}
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {

			if ("1".equalsIgnoreCase(ismtadd))
				ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
						.getServiceid(), msgObject.getUserid(), msgObject
						.getKeyword(), msgObject.getRequestid(), msgObject
						.getTTimes(), msgObject.getMobileoperator());
		}
	}

	private void sendGifMsg(String serviceid, String userid, String operator,
			String service, String mtcontent, BigDecimal requestid,
			int contenttype) {
		try {

			MsgObject msg = new MsgObject();
			msg.setContenttype(contenttype);
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

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	private static String findListofMusic(String userid, String infoid,
			String lastid, int subcode) {
		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("store");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			String ope = "";
			if (infoid.equalsIgnoreCase("vinaphone"))
				ope = "VNPCode";
			else if (infoid.equalsIgnoreCase("mobifone"))
				ope = "VMSCode";
			else
				ope = "VTCode";

			String sqlSelect = "SELEct [IComStore].[dbo].Ringback.RingbackName_E,"
					+ ope
					+ " FROM [IComStore].[dbo].Ringback where "
					+ ope
					+ " is not null and "
					+ ope
					+ " <>'' and RingbackName_E<> ''";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND " + ope + " not in("
						+ fomatLastid(lastid) + ")  ";
			}
			sqlSelect = sqlSelect + " order by newid()";
			Util.logger.info("SEARCH SONG MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			int num = 0;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = result + rs.getString(1) + ";";
					if ((result.length() > 120) || (num == 5)) {
						// cap nhat cho khach hang list danh sach da gui
						updateListMusic(userid, lastid, subcode);
						return result;
					} else {
						num++;
						if ("".equalsIgnoreCase(lastid)) {

							lastid = lastid + rs.getString(2);
						} else {
							lastid = lastid + "," + rs.getString(2);
						}
					}
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static String fomatLastid(String lastid) {
		String newlastid = "";
		String[] slast = lastid.split(",");
		for (int i = 0; i < slast.length; i++) {
			newlastid = newlastid + ",'" + slast[i] + "'";
		}

		return newlastid.substring(1);
	}

	private static String[] splitString(String splitS) {
		String[] result = new String[2];
		boolean resultBoolean = false;
		int i = 160;
		int j = 0;
		String tempString = splitS;
		if (splitS.length() >= 160) {
			while (!(resultBoolean)) {
				if (splitS.charAt(i) == ';') {
					result[0] = splitS.substring(0, i);
					j = i + 1;
					resultBoolean = true;
				}
				i--;
			}
			resultBoolean = false;
			i = tempString.length() - 1;

			/* tach thanh 2 */
			while (!(resultBoolean)) {
				// neu <160
				if ((tempString.charAt(i) == ';') && ((i - j) <= 160)) {
					result[1] = tempString.substring(j, i);
					resultBoolean = true;
				}
				i--;
			}
		} else {
			result[0] = splitS;
			result[1] = "";
		}

		return result;

	}

	/* tim list bai hat HOT */

	public String ValidISDN(String sISDN) {

		Util.logger.info(this.getClass().getName() + "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}

		String tempisdn = "-";

		if (sISDN.trim().length() < 8) {
			return "-";
		}
		try {
			long itemp = Long.parseLong(sISDN.trim());
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	// Replace ____ with _
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

	/* tim ma so cac bai hat */
	private static String[] findMusicID(String find, String infoid) {

		String[] result = new String[2];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		for (int i = 0; i < 2; i++) {
			result[i] = "";
		}

		try {
			connection = dbpool.getConnection("store");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			String ope = "";
			if (infoid.equalsIgnoreCase("vinaphone"))
				ope = "VNPCode";
			else if (infoid.equalsIgnoreCase("mobifone"))
				ope = "VMSCode";
			else
				ope = "VTCode";

			String sqlSelect = "SELECT top 1 RingbackName_E,"
					+ ope
					+ " FROM [IComStore].[dbo].Ringback WHERE SearchRingbackName LIKE '%, "
					+ find + ",%' and " + ope + " is not null and " + ope
					+ " <>''";

			Util.logger.info("SEARCH MA SO  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SEARCH MA SO");
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	private static String[] findMusicIDlike(String find, String infoid) {

		String[] result = new String[2];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		for (int i = 0; i < 2; i++) {
			result[i] = "";
		}

		try {
			connection = dbpool.getConnection("store");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			String ope = "";
			if (infoid.equalsIgnoreCase("vinaphone"))
				ope = "VNPCode";
			else if (infoid.equalsIgnoreCase("mobifone"))
				ope = "VMSCode";
			else
				ope = "VTCode";

			String sqlSelect = "SELECT top 1 RingbackName_E,"
					+ ope
					+ " FROM [IComStore].[dbo].Ringback WHERE SearchRingbackName LIKE '%"
					+ find + "%' and " + ope + " is not null and " + ope
					+ " <>''";

			Util.logger.info("SEARCH MA SO  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SEARCH MA SO");
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getString(1);
					result[1] = rs.getString(2);
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	/* tim list bai hat HOT */
	private String getMobileOperatorNew(String mobileNumber) {
		if (mobileNumber.startsWith("8491") || mobileNumber.startsWith("+8491")
				|| mobileNumber.startsWith("091")
				|| mobileNumber.startsWith("91")
				|| mobileNumber.startsWith("8494")
				|| mobileNumber.startsWith("+8494")
				|| mobileNumber.startsWith("094")
				|| mobileNumber.startsWith("94")
				|| mobileNumber.startsWith("0123")
				|| mobileNumber.startsWith("84123")
				|| mobileNumber.startsWith("84125")
				|| mobileNumber.startsWith("0125")
				|| mobileNumber.startsWith("0127")
				|| mobileNumber.startsWith("0129")) {
			return "GPC";
		} else if (mobileNumber.startsWith("8490")
				|| mobileNumber.startsWith("+8490")
				|| mobileNumber.startsWith("090")
				|| mobileNumber.startsWith("90")
				|| mobileNumber.startsWith("8493")
				|| mobileNumber.startsWith("+8493")
				|| mobileNumber.startsWith("093")
				|| mobileNumber.startsWith("93")
				|| mobileNumber.startsWith("0122")
				|| mobileNumber.startsWith("84122")
				|| mobileNumber.startsWith("84126")
				|| mobileNumber.startsWith("0126")) {
			return "VMS";
		} else if (mobileNumber.startsWith("8498")
				|| mobileNumber.startsWith("+8498")
				|| mobileNumber.startsWith("098")
				|| mobileNumber.startsWith("98")
				|| mobileNumber.startsWith("8497")
				|| mobileNumber.startsWith("+8497")
				|| mobileNumber.startsWith("097")
				|| mobileNumber.startsWith("97")
				|| mobileNumber.startsWith("84168")
				|| mobileNumber.startsWith("0168")
				|| mobileNumber.startsWith("84168")
				|| mobileNumber.startsWith("0169")
				|| mobileNumber.startsWith("84169")
				|| mobileNumber.startsWith("8416")
				|| mobileNumber.startsWith("016")) {
			return "VIETTEL";
		} else if (mobileNumber.startsWith("8495")
				|| mobileNumber.startsWith("+8495")
				|| mobileNumber.startsWith("095")
				|| mobileNumber.startsWith("95")) {
			return "SFONE";
		} else if (mobileNumber.startsWith("8492")
				|| mobileNumber.startsWith("+8492")
				|| mobileNumber.startsWith("092")
				|| mobileNumber.startsWith("92")) {
			return "HTC";
		} else if (mobileNumber.startsWith("8496")
				|| mobileNumber.startsWith("+8496")
				|| mobileNumber.startsWith("096")
				|| mobileNumber.startsWith("96")) {
			return "EVN";
		} else {
			return "-";
		}

	}

	/* Chia thanh 2 MT */
	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static int getUserID(String userid, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT times FROM  icom_dangky_nhaccho_tructiep WHERE userid= '"
				+ userid.toUpperCase() + "' AND subcode =" + subcode;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			if (statement.execute()) {
				rs = statement.getResultSet();
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

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static String getMusicID(String userid, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT musichotid FROM icom_dangky_nhaccho_tructiep WHERE userid= '"
					+ userid.toUpperCase() + "' AND subcode=" + subcode;
			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return sequence_temp;
			}
			statement = cnn.prepareStatement(query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return sequence_temp;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return sequence_temp;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
	}

	private static boolean saverequest(String userid, int subcode) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_dangky_nhaccho_tructiep (userid, subcode) VALUES ('"
					+ userid + "'," + subcode + ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into dbcontent");
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

	private static boolean saveuser(String userid, String dtbase,
			String contentid, String musicname, String usergif) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO " + dtbase
					+ "(userid, contentid,musicname,usergift) VALUES ('"
					+ userid + "','" + contentid + "','" + musicname + "','"
					+ usergif + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into dbcontent");
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

	// cap nhat danh sach khach hang
	private static boolean updateListMusic(String userid, String lastid,
			int subcode) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update cac danh sach bai hat da gui cho khach hang
			String sqlUpdate = "UPDATE  icom_dangky_nhaccho_tructiep SET musichotid = '"
					+ lastid
					+ "' WHERE upper(userid)='"
					+ userid.toUpperCase()
					+ "' AND subcode=" + subcode;
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list music to send " + userid
						+ " to dbcontent");
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

	/* Tim list bai hat */

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

}
