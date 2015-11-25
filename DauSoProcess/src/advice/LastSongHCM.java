package advice;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class LastSongHCM extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	String SERVER = "http://mobinet.com.vn";
	String URL_INV = "http://mobinet.com.vn/?c=wap3";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		String VALID_CODE = "";

		try {

			// Get info of client
			String user_id = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String service_id = msgObject.getServiceid();
			Timestamp receive_date = msgObject.getTTimes();
			BigDecimal request_id = msgObject.getRequestid();
			String keywords = msgObject.getKeyword();
			info = replaceAllWhiteWithOne(info);
			String mobile_operator = msgObject.getMobileoperator();
			int cpid = msgObject.getCpid();
			String infoid = "";

			// Get Option
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			GID = getOption(_option, 0);
			String type = getString(_option, "type", "media");
			URL_INV = getString(_option, "url_inv", URL_INV);
			Util.logger.info("URL_INV: " + URL_INV);

			/* Kiểm tra xem thuê bao của khách hàng */
			if ("VIETTEL".equalsIgnoreCase(mobile_operator)
					|| "VIETEL".equalsIgnoreCase(mobile_operator)) {
				infoid = "viettel";
			} else if (("VMS".equalsIgnoreCase(mobile_operator))
					|| "mobifone".equalsIgnoreCase(mobile_operator)) {
				infoid = "mobifone";
			} else if (("GPC".equalsIgnoreCase(mobile_operator))
					|| ("VINAPHONE".equalsIgnoreCase(mobile_operator))) {
				infoid = "vinaphone";
			} else {
				infoid = "other";
			}
			

			Hashtable lastSong = Threadlastsonghcm.lastestsong.LAST_SONG;

			// Write log info config
			String id = getSECRET_NUMBER(lastSong, "id");
			Util.logger.info("ID : " + id);
			String start_time = getSECRET_NUMBER(lastSong, "start_time");
			Util.logger.info("Start Time : " + start_time);
			String finish_time = getSECRET_NUMBER(lastSong, "finish_time");
			Util.logger.info("End Time : " + finish_time);
			String block1 = getSECRET_NUMBER(lastSong, "block1");
			Util.logger.info("Block 1 : " + block1);
			String block2 = getSECRET_NUMBER(lastSong, "block2");
			Util.logger.info("Block 2 : " + block2);
			String block3 = getSECRET_NUMBER(lastSong, "block3");
			Util.logger.info("Block 3 : " + block3);
			String block4 = getSECRET_NUMBER(lastSong, "block4");
			Util.logger.info("Block 4 : " + block4);
			String block5 = getSECRET_NUMBER(lastSong, "block5");
			Util.logger.info("Block 5 : " + block5);
			String block6 = getSECRET_NUMBER(lastSong, "block6");
			Util.logger.info("Block 6 : " + block6);
			String block7 = getSECRET_NUMBER(lastSong, "block7");
			Util.logger.info("Block 7 : " + block7);
			String block8 = getSECRET_NUMBER(lastSong, "block8");
			Util.logger.info("Block 8 : " + block8);
			String block9 = getSECRET_NUMBER(lastSong, "block9");
			Util.logger.info("Block 9 : " + block8);
			String start_vote = getSECRET_NUMBER(lastSong, "start_vote");
			Util.logger.info("Start Vote : " + start_vote);
			String end_vote = getSECRET_NUMBER(lastSong, "end_vote");
			Util.logger.info("End Vote : " + end_vote);
			String isProcess = getSECRET_NUMBER(lastSong, "isprocess");
			Util.logger.info("is Process :" + isProcess);
			String answerSub = getSECRET_NUMBER(lastSong, "answer");
			Util.logger.info("Answer: " + answerSub);

			String bhFinal = getSECRET_NUMBER(lastSong, "lastsong");
			Util.logger.info("Last Song :" + bhFinal);

			// Util.logger.info("Process Number : " + processNumber);

			if (Threadlastsonghcm.lastestsong.isProcess == 0) {
				msgObject
						.setUsertext("Thoi gian du doan da het.Hay lang nghe chuong trinh va tham gia du doan bai hat vao 13-14h thu 2,4,6 hang tuan tren VOV giao thong.DTHT:1900571566");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else if (Threadlastsonghcm.lastestsong.isProcess == 1) {
				String[] sTokens = info.split(" ");

				// Xem dang o block thu may
				int status = 0;
				if ((block1.compareToIgnoreCase(start_time) < 0)
						|| (block1.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 1;
				} else if ((block2.compareToIgnoreCase(start_time) < 0)
						|| (block2.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 2;
				} else if ((block3.compareToIgnoreCase(start_time) < 0)
						|| (block3.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 3;
				} else if ((block4.compareToIgnoreCase(start_time) < 0)
						|| (block4.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 4;
				} else if ((block5.compareToIgnoreCase(start_time) < 0)
						|| (block5.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 5;
				} else if ((block6.compareToIgnoreCase(start_time) < 0)
						|| (block6.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 6;
				} else if ((block7.compareToIgnoreCase(start_time) < 0)
						|| (block7.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 7;
				} else if ((block8.compareToIgnoreCase(start_time) < 0)
						|| (block8.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 8;
				} else if ((block9.compareToIgnoreCase(start_time) < 0)
						|| (block9.compareToIgnoreCase(receive_date.toString()) > 0)) {
					status = 9;
				} else if ((finish_time.compareToIgnoreCase(start_time) < 0)
						|| (finish_time.compareToIgnoreCase(receive_date
								.toString()) > 0)) {
					status = 10;
				}

				if (sTokens.length < 2) {
					msgObject
							.setUsertext("Tin nhan sai cu phap. Soan tin"
									+ keywords
									+ " <tenbaihat> gui "
									+ service_id
									+ " de tham gia chuong trinh \"Giai ma ca khuc\".DTHT 1900571566");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {

					String guessSong = sTokens[1];

					// MT 1
					String mt1 = "Ban da du doan ten bai hat cuoi cung la:"
							+ guessSong
							+ ".Hay tiep tuc theo doi de biet ket qua.Du doan cang nhieu co hoi dung cang lon";
					if (mt1.length() > 160) {
						String cutMT = guessSong.substring(0, guessSong
								.length()
								- mt1.length() + 154);
						mt1 = "Ban da du doan ten bai hat cuoi cung la: "
								+ cutMT
								+ "...Hay tiep tuc theo doi de biet ket qua.Du doan cang nhieu co hoi dung cang lon";
					}

					msgObject.setUsertext(mt1);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					int exactly = 0;
					if (guessSong.equalsIgnoreCase(bhFinal)) {
						exactly = 1;
					}

					// Luu lai thong tin khach hang
					saveClient(id, user_id, service_id, mobile_operator,
							guessSong, request_id.toString(), exactly, status,
							receive_date, cpid, keywords);

					// MT 2
					if (!"other".equalsIgnoreCase(infoid)) {
						String[] result = new String[2];
						result = findMusicID("gateway", guessSong, infoid);
						String mt2 = "";
						if (!"".equalsIgnoreCase(result[1])) {
							if ("VIETTEL".equalsIgnoreCase(infoid)) {
								mt2 = "Tang ban MS bai hat "
										+ result[0]
										+ "-"
										+ result[1]
										+ ".De cai nhac cho.Buoc1,soan: DK gui 1221.Buoc2,soan: BH maso gui 1221";
								if (mt2.length() > 160) {
									String cutMT = result[0].substring(0,
											result[0].length() - mt2.length()
													+ 154);
									mt2 = "Tang ban MS bai hat "
											+ cutMT
											+ "...-"
											+ result[1]
											+ ".De cai nhac cho.Buoc1,soan: DK gui 1221.Buoc2,soan: BH maso gui 1221";
								}

								msgObject.setUsertext(mt2);
							} else if ("VINAPHONE".equalsIgnoreCase(infoid)) {
								mt2 = "Tang ban MS bai hat "
										+ result[0]
										+ "-"
										+ result[1]
										+ ".De cai nhac cho.Buoc1,soan: DK gui 9194.Buoc2,soan: TUNE  maso  gui 9194";
								if (mt2.length() > 160) {
									String cutMT = result[0].substring(0,
											result[0].length() - mt2.length()
													+ 154);
									mt2 = "Tang ban MS bai hat "
											+ cutMT
											+ "...-"
											+ result[1]
											+ ".De cai nhac cho.Buoc1,soan: DK gui 9194.Buoc2,soan: TUNE  maso  gui 9194";
								}

								msgObject.setUsertext(mt2);
							} else if ("MOBIFONE".equalsIgnoreCase(infoid)) {

								mt2 = "Tang ban MS bai hat "
										+ result[0]
										+ "-"
										+ result[1]
										+ ".De cai dat nhac cho.Buoc1,soan DK gui 9224.Buoc2,soan: CHON maso gui 9224";

								if (mt2.length() > 160) {
									String cutMT = result[0].substring(0,
											result[0].length() - mt2.length()
													+ 154);
									mt2 = "Tang ban MS bai hat "
											+ cutMT
											+ "...-"
											+ result[1]
											+ "De cai dat nhac cho.Buoc1,soan DK gui 9224.Buoc2,soan: CHON maso gui 9224";
								}

								msgObject.setUsertext(mt2);
							}

							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							messages.add(new MsgObject(msgObject));
						}
					}

				/*	// Send MT gui tang nhac chuong VALID_CODE =
					VALID_CODE = validcode(sTokens[1], GID);
					Util.logger.info("VALID_CODE: " + VALID_CODE);
					if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
						String phone = msgObject.getUserid();
						String code = VALID_CODE;

						if (saverequest(phone, VALID_CODE, type, GID)) {

							msgObject.setUsertext(code + ":" + SERVER + "/?p="
									+ phone + "&c=" + code + "&f=" + type
									+ "&g=" + GID);
							msgObject.setContenttype(8);
							msgObject.setMsgtype(0);
							messages.add(new MsgObject(msgObject));
						}
					} else {
						msgObject.setUsertext("Tang ban TOP nhac:" + URL_INV);
						msgObject.setContenttype(8);
						msgObject.setMsgtype(0);
						messages.add(new MsgObject(msgObject));
						// return messages;
					}
*/
					return messages;
				}
			} else if (Threadlastsonghcm.lastestsong.isProcess == 2) {
				msgObject
						.setUsertext("Hien tai chung toi van chua cong bo cau hoi phu cua chuong trinh.Moi ban quay lai sau khi co tin hieu tu chuong trinh");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else if (Threadlastsonghcm.lastestsong.isProcess == 3) {

				// Chuong trinh doan cau hoi phu.
				String[] sTokens = info.split(" ");
				if (sTokens.length < 2) {
					msgObject.setUsertext("Tin nhan sai cu phap. Soan tin "
							+ keywords + " <Dap an ban chon> gui " + service_id
							+ " de tra loi cau hoi phu cua chuong trinh.");
					msgObject.setContenttype(0);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {
					String answer = sTokens[1];

					if ("1".equalsIgnoreCase(answer)) {
						answer = "A";
					} else if ("2".equalsIgnoreCase(answer)) {
						answer = "B";
					} else if ("3".equalsIgnoreCase(answer)) {
						answer = "C";
					} else if ("4".equalsIgnoreCase(answer)) {
						answer = "D";
					}

					int correctAnswer = 0;
					if (answer.equalsIgnoreCase(answerSub)) {
						correctAnswer = 1;
						msgObject
								.setUsertext("Chuc mung ban da tra loi dung cau hoi phu.Hay tiep tuc theo doi chuong trinh de biet minh co phai la nguoi dau tien doan dung khong nhe!DTHT 1900571566");

					} else {
						msgObject
								.setUsertext("Rat tiec!Dap an ban chon chua dung.Phan qua hap dan van dang cho ban.Hay nhanh tay soan tin de tro thanh nguoi dau tien doan dung nhe!DTHT:1900571566");
					}

					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));

					// Insert vao csdl.
					saveAnswer(id, user_id, service_id, mobile_operator, answer
							.toUpperCase(), request_id.toString(),
							correctAnswer, receive_date, cpid);
					return messages;

				}
			}

		} catch (Exception ex) {
			Util.logger.error("Error:" + ex.toString());
			return null;
		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
		return messages;
	}

	// Replace all space by one space
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

	public static String getSECRET_NUMBER(Hashtable numberDelay,
			String secretNumber) {
		String retobj = "";

		try {
			retobj = (String) numberDelay.get(secretNumber);
			return retobj;

		} catch (Exception e) {
		}

		return "";
	}

	private static boolean saveClient(String session, String user_id,
			String service_id, String mobile_operator, String info,
			String request_id, int exactly, int block, Timestamp receive_date,
			int Cpid, String keyword) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_lastsonghcm_customer(session, user_id, service_id, mobile_operator, info, request_id, exactly, block, receive_time, cpid, keyword ) VALUES ('"
					+ session
					+ "','"
					+ user_id
					+ "','"
					+ service_id
					+ "','"
					+ mobile_operator
					+ "','"
					+ info
					+ "','"
					+ request_id
					+ "',"
					+ exactly
					+ ","
					+ block
					+ ",'"
					+ receive_date
					+ "',"
					+ Cpid + ",'" + keyword + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_");
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

	// Find Music ID
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

	public String ValidISDN(String sISDN) {
		Util.logger.info(this.getClass().getName() + "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	private String validcode(String code, int gid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection("content");
			String query1 = "select code,filetype from icom_wap.dbo.upload  where (upper(code)='"
					+ code.toUpperCase()
					+ "'  or upper(code)='"
					+ code.toUpperCase()
					+ "P' ) and upper(filetype)='MEDIA' and cgroup=" + gid;

			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				String codetemp = (String) item.elementAt(0);

				return codetemp;
			}

		} catch (Exception e) {
		} finally {
			dbpool.cleanup(connection);

		}
		return INVALID_CODE;
	}

	private static boolean saverequest(String userid, String code, String type,
			int gid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection("content");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into icom_wap.dbo.download( phone,code,filetype,cgroup) values ('"
					+ userid + "','" + code + "','" + type + "'," + gid + ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to download");
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

	String getOption(HashMap _option1, String defaultvalue) {
		try {
			return ((String) _option1.get("gid")).toUpperCase();
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	int getOption(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("gid"));
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "###");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			_params.put(key, value);
		}
		return _params;
	}

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)
					|| temp == null) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	// Save customer join sub Question
	private static boolean saveAnswer(String session, String user_id,
			String service_id, String mobile_operator, String answer,
			String request_id, int exactly, Timestamp receive_date, int Cpid) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_lastsonghcm_answer (session, user_id, service_id, mobile_operator, answer, request_id, correct, receive_date, cpid ) VALUES ('"
					+ session
					+ "','"
					+ user_id
					+ "','"
					+ service_id
					+ "','"
					+ mobile_operator
					+ "','"
					+ answer
					+ "','"
					+ request_id
					+ "'," + exactly + ",'" + receive_date + "'," + Cpid + ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_");
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

	// Check phone number
	private boolean checkNumber(String numberString) {
		try {
			long number = Integer.parseInt(numberString);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
