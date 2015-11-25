package advice;

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
import java.util.Vector;


import neo.funring.FunRingGateSoap11BindingImpl;

import com.viettel.service.IMZGWSoap11BindingImpl;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.sendXMLGPC;
import com.vmg.soap.mo.sendXMLRING;
import com.vmg.soap.mo.sendXMLVIETTEL;

import cs.ExecuteADVCR;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * ・
 * </pre>
 * 
 * @author Vietnamnet I-Com HaPTT
 * @version 1.0
 */
public class Nhaccho8x51tructiep extends ContentAbstract {

	/* First String */
	private static String dbcontent = "icom_dangky_nhaccho_tructiep";
	private static String urlring = "http://203.162.71.165:8686/SetRingBack.asmx?";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "1";
		try {
			Collection messages = new ArrayList();

			String infoid = "HOT";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String numberid = "8x51";
			String dtbase = "nhaccho";
			String last_msg = "soan tin XNNC makiemtra gui 8051";
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				last_msg = getString(_option, "last_msg", last_msg);

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

			if (sTokens.length > 1) {

				String[] resultMusicID = new String[2];
				// 0 la ten bai hat, 1 ma ma bai hat
				resultMusicID = findMusicID(dtbase, sTokens[1], infoid);
				// MT1
				if (!"".equalsIgnoreCase(resultMusicID[1])) {

					Random iRandom = new Random();
					int imusic = iRandom.nextInt(10);

					String giff_userid = "";
					String giff_telco = "";
					boolean giff = false;

					if (sTokens.length > 2) {
						giff_userid = ValidISDN(sTokens[2]);
						if (!giff_userid.equalsIgnoreCase("-")) {
							giff_telco = getMobileOperatorNew(giff_userid);

							if ("-".equals(giff_telco)) {
								giff = false;
							} else {
								giff = true;
							}

						}
					}

					if (giff == true) {
						String msisdn = userid + "|" + giff_userid;
						// gui tang: neu chua dang ky thi dang ky cho khach hang
						// roi gui tang
						// 
						if (giff_telco.toUpperCase().equalsIgnoreCase("GPC")
								&& infoid.equalsIgnoreCase("vinaphone")) {

							try {
								String result2 = sendXMLRING.SendXML(urlring,
										"PresentTone", msisdn, "",
										resultMusicID[1], now());
								mt1 = "Ban gui tang chua thanh cong xin vui long thu lai. Tang ban top bai hat HOT:";
								if (result2.equalsIgnoreCase("0")) {
									// gui tang thanh cog
									sendGifMsg(
											msgObject.getServiceid(),
											giff_userid,
											giff_telco,
											msgObject.getKeyword(),
											"Sodt "
													+ msgObject.getUserid()
													+ " tang ban bai nhac cho: "
													+ resultMusicID[0],
											msgObject.getRequestid(), 0);

									msgObject
											.setUsertext("Ban da gui tang  thanh cong toi thue bao "
													+ giff_userid + ".");
									msgObject.setContenttype(0);
									msgObject.setMsgtype(1);
									DBUtil.sendMT(msgObject);
									return null;

								} else if (result2.equalsIgnoreCase("501")) {
									mt1 = "Gui tang khong thanh cong.Bai hat da co trong bo suu tap cua nguoi duoc tang.Tang ban top bai hat HOT:";
								} else if (result2.equalsIgnoreCase("602")) {
									mt1 = "Ban gui tang chua thanh cong do thue bao duoc nhan khong co that hoac chua dang ky dich vu.Tang ban top bai hat HOT:";
								}
							} catch (Exception ex) {
								Util.logger.error("Khong thanh cong");

							}
						} else if (giff_telco.toUpperCase().equalsIgnoreCase(
								"VIETTEL")
								&& infoid.equalsIgnoreCase("viettel")) {
							try {
								mt1 = "Ban gui tang chua thanh cong xin vui long thu lai. Tang ban top bai hat HOT:";
								String result = sendXMLRING.SendXML(urlring,
										"GetStatus", userid, "", "", now());
								Util.logger.info("result" + result);

								if (result.equalsIgnoreCase("0")) {

									try {
										String result2 = sendXMLRING.SendXML(
												urlring, "PresentTone", msisdn,
												"", resultMusicID[1], now());

										if (result2.equalsIgnoreCase("0")) {
											// gui tang thanh cog
											mt1 = "";
										} else if (result2
												.equalsIgnoreCase("602")) {
											mt1 = "Ban gui tang chua thanh cong do thue bao duoc nhan khong co that hoac chua dang ky dich vu.Tang ban top bai hat HOT:";
											Util.logger
													.error("Gui tang khong thanh cong");
										} else if (result2
												.equalsIgnoreCase("501")) {
											mt1 = "Gui tang khong thanh cong.Bai hat da co trong bo suu tap cua nguoi duoc tang.Tang ban top bai hat HOT:";
										} else {
											mt1 = "Ban gui tang chua thanh cong, xin vui long thu lai.";
											Util.logger
													.error("Gui tang khong thanh cong");
										}

									} catch (Exception ex) {
										Util.logger.error("Khong thanh cong");

									}
								} else if (result.equalsIgnoreCase("200")) {
									// chua dang ky
									// Dang ky cho khach hang xong moi gui tang

									mt1 = "Ban gui tang chua thanh cong do chua dang ky thanh cong,xin vui long thu lai.Tang ban TOP bai hat HOT";
									try {
										String result4 = sendXMLRING.SendXML(
												urlring, "GetValidcode",
												userid, "", "", now());

										if (result4.equalsIgnoreCase("0")) {
											mt1 = "De gui tang thanh cong,ban phai cai dat nhac cho truoc, vui long "
													+ last_msg
													+ " de xac nhan cai dat.";
											saveuser(userid, "nhaccho_viettel",
													resultMusicID[1],
													resultMusicID[0],
													giff_userid);
										}

									} catch (Exception ex) {
										Util.logger.error("Khong thanh cong");

									}

								}
							} catch (Exception ex) {
								Util.logger.error("Khong thanh cong");

							}

						} else if (giff_telco.toUpperCase().equalsIgnoreCase(
								"VMS")
								&& infoid.equalsIgnoreCase("mobifone")) {
							try {
								mt1 = "Ban gui tang nhac cho chua thanh cong. Tang ban top bai hat HOT:";
								String result = sendXMLRING.SendXML(urlring,
										"GetStatus", userid, "", "", now());
								Util.logger.info("result" + result);

								if (result.equalsIgnoreCase("0")) {

									try {
										String result2 = sendXMLRING.SendXML(
												urlring, "PresentTone", msisdn,
												"", resultMusicID[1], now());

										if (result2.equalsIgnoreCase("0")) {
											// gui tang thanh cog
											sendGifMsg(
													msgObject.getServiceid(),
													giff_userid,
													giff_telco,
													msgObject.getKeyword(),
													"Sodt "
															+ msgObject
																	.getUserid()
															+ " tang ban bai nhac cho: "
															+ resultMusicID[0],
													msgObject.getRequestid(), 0);

											msgObject
													.setUsertext("Ban da gui tang  thanh cong toi thue bao "
															+ giff_userid + ".");
											msgObject.setContenttype(0);
											msgObject.setMsgtype(1);
											DBUtil.sendMT(msgObject);
											return null;

										} else if (result2
												.equalsIgnoreCase("602")) {
											mt1 = "Ban gui tang chua thanh cong do thue bao duoc nhan khong co that hoac chua dang ky dich vu.Tang ban top bai hat HOT:";
										} else if (result2
												.equalsIgnoreCase("501")) {
											mt1 = "Gui tang khong thanh cong.Bai hat da co trong bo suu tap cua nguoi duoc tang.Tang ban top bai hat HOT:";
										} else {
											mt1 = "Ban gui tang chua thanh cong, xin vui long thu lai. Tang ban top bai hat HOT";
											Util.logger
													.error("Gui tang khong thanh cong");
										}

									} catch (Exception ex) {
										Util.logger.error("Khong thanh cong");

									}
								} else if (result.equalsIgnoreCase("200")) {
									// chua dang ky
									// Dang ky cho khach hang xong moi gui tang
									mt1 = "Ban gui tang chua thanh cong do chua dang ky thanh cong,xin vui long thu lai.Tang ban TOP bai hat HOT";

									try {
										String result2 = sendXMLRING.SendXML(
												urlring, "GetValidcode",
												userid, "", "", now());

										if (result2.equalsIgnoreCase("0")) {
											try {
												String result3 = sendXMLRING
														.SendXML(
																urlring,
																"PresentTone",
																msisdn,
																"",
																resultMusicID[1],
																now());

												if (result3
														.equalsIgnoreCase("0")) {
													// gui tang thanh cog
													sendGifMsg(
															msgObject
																	.getServiceid(),
															giff_userid,
															giff_telco,
															msgObject
																	.getKeyword(),
															"Sodt "
																	+ msgObject
																			.getUserid()
																	+ " tang ban bai nhac cho: "
																	+ resultMusicID[0],
															msgObject
																	.getRequestid(),
															0);

													msgObject
															.setUsertext("Ban da gui tang  thanh cong toi thue bao "
																	+ giff_userid
																	+ ".");
													msgObject.setContenttype(0);
													msgObject.setMsgtype(1);
													DBUtil.sendMT(msgObject);

												} else if (result2
														.equalsIgnoreCase("602")) {
													mt1 = "Ban gui tang chua thanh cong do thue bao duoc nhan khong co that hoac chua dang ky dich vu.Tang ban top bai hat HOT:";
												} else {
													mt1 = "Ban gui tang chua thanh cong, xin vui long thu lai.Tang ban top bai hat HOT:";
													Util.logger
															.error("Gui tang khong thanh cong");
												}

											} catch (Exception ex) {
												Util.logger
														.error("Khong thanh cong");

											}
										}

									} catch (Exception ex) {
										Util.logger.error("Khong thanh cong");

									}

								}
							} catch (Exception ex) {
								Util.logger.error("Khong thanh cong");

							}

						} else

						{
							msgObject
									.setUsertext("Ban gui tang chua thanh cong do thue bao nhan khong cung mang.Tang ban bai hat HOT:");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(1);
							DBUtil.sendMT(msgObject);
						}
						int time2send = getUserID(userid, dbcontent, 1);
						String lastid = "";
						if (time2send >= 1) {
							lastid = getMusicID(userid, dbcontent, 1);
						} else {
							// Ghi lai danh sach khach hang da gui
							saverequest(userid, dbcontent, 1);
						}

						// Lay danh sach cac bai hat HOT
						String musichotsend = findListofMusic(dtbase, userid,
								infoid, lastid, 1)+ " Truy cap http://nhipdap.vn/ de lua chon cac bai hat khac. DTHT 1900571566";
						if (!mt1.equalsIgnoreCase("")) {
							msgObject.setUsertext(mt1);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(1000);
						}
						if (!"".equalsIgnoreCase(musichotsend)) {
							msgObject.setUsertext(musichotsend);
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
						}

						return null;

					} else {

						if ("viettel".equalsIgnoreCase(infoid)) {

							try {
								mt1 = "Ban cai dat nhac cho chua thanh cong, xin vui long thu lai. Tang ban TOP bai hat HOT";
								String result = sendXMLRING.SendXML(urlring,
										"SetTone", userid, "",
										resultMusicID[1], now());
								Util.logger.info("result" + result);
								if (result.equalsIgnoreCase("200")) {
									try {
										String result2 = sendXMLRING.SendXML(
												urlring, "GetValidcode",
												userid, "", "", now());

										if (result2.equalsIgnoreCase("0")) {
											mt1 = "De dang ky duoc bai hat "
													+ last_msg
													+ " de xac nhan cai dat nhac cho";
											saveuser(userid, "nhaccho_viettel",
													resultMusicID[1],
													resultMusicID[0], "0");
										}

									} catch (Exception ex) {
										Util.logger.error("Khong thanh cong");

									}
								} else if (result.equalsIgnoreCase("501")) {
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
											.info("Cai dat nhac cho thanh cong");

								} else if (result.equalsIgnoreCase("101")) {
									mt1 = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:";
									Util.logger
											.info("Cai dat nhac cho thanh cong");

								}

							} catch (Exception ex) {
								Util.logger.error("Khong thanh cong");

							}

						} else if ("vinaphone".equalsIgnoreCase(infoid)) {

							try {
								mt1 = "Ban cai dat nhac cho chua thanh cong, xin vui long thu lai. Tang ban TOP bai hat HOT";
								String result = sendXMLRING.SendXML(urlring,
										"SetTone", userid, "",
										resultMusicID[1], now());
								Util.logger.info("result" + result);

								if (result.equalsIgnoreCase("501")) {
									mt1 = "Bai hat da co trong bo suu tap cua ban";
									Util.logger
											.info("Bai hat da co trong bo suu tap. Tang ban TOP bai hat HOT:");
								} else if (result.equalsIgnoreCase("0")) {
									mt1 = "Ban cai dat bai hat "
											+ resultMusicID[0]
											+ " thanh cong.  Tang ban TOP bai hat HOT:";
									Util.logger
											.info("Cai dat nhac cho thanh cong");

								} else if (result.equalsIgnoreCase("101")) {
									mt1 = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:";
									Util.logger
											.info("Cai dat nhac cho thanh cong");

								}

							} catch (Exception ex) {
								Util.logger.error("Khong thanh cong");

							}
						} else if ("mobifone".startsWith(infoid)) {
							try {
								mt1 = "Ban cai dat nhac cho chua thanh cong, xin vui long thu lai. Tang ban TOP bai hat HOT";
								String result = sendXMLRING.SendXML(urlring,
										"SetTone", userid, "",
										resultMusicID[1], now());
								Util.logger.info("result" + result);
								if (result.equalsIgnoreCase("200")) {
									try {
										String result2 = sendXMLRING.SendXML(
												urlring, "GetValidcode",
												userid, "", "", now());

										if (result2.equalsIgnoreCase("0")) {
											String result3 = sendXMLRING
													.SendXML(urlring,
															"SetTone", userid,
															"",
															resultMusicID[1],
															now());
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
									mt1 = "";
									Util.logger
											.info("Cai dat nhac cho thanh cong");

								} else if (result.equalsIgnoreCase("101")) {
									mt1 = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:";
									Util.logger
											.info("Cai dat nhac cho thanh cong");

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
						int time2send = getUserID(userid, dbcontent, 1);
						String lastid = "";
						if (time2send >= 1) {
							lastid = getMusicID(userid, dbcontent, 1);
						} else {
							// Ghi lai danh sach khach hang da gui
							saverequest(userid, dbcontent, 1);
						}

						// Lay danh sach cac bai hat HOT
						String musichotsend = findListofMusic(dtbase, userid,
								infoid, lastid, 1)+ "Truy cap http://nhipdap.vn/ de lua chon cac bai hat khac.DTHT 1900571566";

						if (!"".equalsIgnoreCase(musichotsend)) {
							msgObject.setUsertext(musichotsend);
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
						}

						return null;
					}
				} else {

					// Bai hat chua co trong he thong.
					// Send MT 1
					String info2Client = "";
					info2Client = "Bai hat ban thich hien tai chua co trong he thong.Truy cap ngay trang:http://nhipdap.vn/ de lua chon cac BH Hot khac.Tang ban top bai hat HOT:";

					msgObject.setUsertext(info2Client);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					// Send MT 2
					// tim danh sach bai hat hot
					// Kiem tra so lan gui tin cua khach hang
					int time2send = getUserID(userid, dbcontent, 1);
					String lastid = "";
					if (time2send >= 1) {
						lastid = getMusicID(userid, dbcontent, 1);
					} else {
						// Ghi lai danh sach khach hang da gui
						saverequest(userid, dbcontent, 1);
					}

					// Lay danh sach cac bai hat HOT
					String musichotsend = findListofMusic(dtbase, userid,
							infoid, lastid, 1)+ "Truy cap http://nhipdap.vn/ de lua chon cac bai hat khac.DTHT 1900571566";

					if (!"".equalsIgnoreCase(musichotsend)) {
						msgObject.setUsertext(musichotsend);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}

					return null;
				}
			} else {
				String info2Client = "Tin nhan sai cu phap. Truy cap ngay trang: http://nhipdap.vn/ de lua chon cac BH Hot khac. CSKH:1900571566";
				msgObject.setUsertext(info2Client);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

				return null;
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
			long itemp = Integer.parseInt(sISDN.trim());
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
	private static String[] findMusicID(String dtbase, String find,
			String infoid) {

		String[] result = new String[2];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();
		for (int i = 0; i < 2; i++) {
			result[i] = "";
		}

		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT musicname_sms, musicid FROM icom_music WHERE ( upper(musicname) = '"
					+ find.toUpperCase()
					+ "') AND ( upper(operator) ='"
					+ infoid.toUpperCase() + "')";

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
	private static String Registry(String userid, String contenid,
			String musicname) {
		String mt = "Ban cai dat bai hat "
				+ musicname
				+ " chua thanh cong. Xin vui long thu lai. Tang ban TOP bai hat HOT:";

		try {
			String result = sendXMLRING.SendXML(urlring, "SetTone", userid, "",
					contenid, now());
			Util.logger.info("result SetTone" + result);

		} catch (Exception ex) {
			Util.logger.error("Khong thanh cong");

		}

		return mt;
	}

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
				|| mobileNumber.startsWith("84166")
				|| mobileNumber.startsWith("0166")) {
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
	private static int getUserID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT times FROM " + dtbase + " WHERE userid= '"
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
	private static String getMusicID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT musichotid FROM " + dtbase
					+ " WHERE userid= '" + userid.toUpperCase()
					+ "' AND subcode=" + subcode;
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

	private static boolean saverequest(String userid, String dtbase, int subcode) {

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
					+ "(userid, subcode) VALUES ('" + userid + "'," + subcode
					+ ")";
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
			String dtbase, int subcode) {

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
			String sqlUpdate = "UPDATE " + dtbase + " SET musichotid = '"
					+ lastid + "' WHERE upper(userid)='" + userid.toUpperCase()
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
	private static String findListofMusic(String dtbase, String userid,
			String infoid, String lastid, int subcode) {
		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT musicname_sms, musicid FROM icom_music WHERE ( upper(operator) = '"
					+ infoid.toUpperCase() + "')";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND musicid not in(" + lastid + ") ";
			}

			Util.logger.info("SEARCH SONG MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			int num = 0;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = result + rs.getString(1) + ";";
					if ((result.length() > 80)||(num>4)) {
						// cap nhat cho khach hang list danh sach da gui
						updateListMusic(userid, lastid, dbcontent, subcode);
						return result;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							num++;
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
