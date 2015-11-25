package services.textbases;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
// com.services.sfone.ConSoMayMan
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import java.math.BigDecimal;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.ResultSet;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.DBUtils;

import cs.ExecuteADVCR;

public class Capsohoanhao extends ContentAbstract {
	String GAMEID = "CAPSOHOANHAO";
	String INFOCS = "*Soan:AC gui 8551 de so huu Nokia E71";
	String SUPPORT = "0435561862";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {

			Collection messages = new ArrayList();
			MsgObject mt = msgObject;

			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();

			String sMTReturn = mtReturn(sUserid, sServiceid, sKeyword,
					msgObject.getRequestid());
			mt.setUsertext(sMTReturn);
			mt.setMsgtype(1);
			messages.add(new MsgObject(mt));
			return messages;
		} catch (Exception e) {
			// TODO: handle exception
			return null;

		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
	}

	int getRANDOM(int iBalance) {
		Random iRandom = new Random();
		int i = iRandom.nextInt(99);
		boolean nextloop = true;
		while (nextloop) {
			i = iRandom.nextInt(99);
			if (i > 0) {
				nextloop = false;
			}
		}

		return i;
	}

	private String getMTSolech(String usernumber, String keyword,
			String serviceid) {

		Random iRandom = new Random();
		int i = iRandom.nextInt(2);
		String temp = "";
		switch (i) {
		case 0:
			temp = "Ban nhan so "
					+ usernumber
					+ ", ban chua trung thuong\n*Soan tin: "
					+ keyword
					+ " gui "
					+ serviceid
					+ " de la nguoi chien thang voi cac phan thuong 5tr, 10tr, 15tr"
					+ "\n*HT:" + SUPPORT;
			break;
		case 1:
			temp = "So cua ban:" + usernumber
					+ ", ban chua tim duoc CAP SO HOAN HAO\n*Tiep tuc: "
					+ keyword + " gui " + serviceid
					+ " de truy tim giai thuong 5tr" + "\n" + INFOCS + "\n*HT:"
					+ SUPPORT;
			break;
		}

		return temp;
	}

	public static int executeSQL(String sql) {

		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		Connection obj = dbpool.getConnectionGateway();
		try {

			statement = obj.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {

			return -1;
		} catch (Exception e) {
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(obj);

		}
	}

	private String mtReturn(String userid, String serviceid, String keyword,
			BigDecimal requestid) throws Exception {
		String mtReturn = "";
		long lSequence = 1;
		boolean isWinner = false;

		lSequence = getSequence(GAMEID, serviceid);
		Util.logger.info("sequence:" + lSequence);

		int iBalance = getBalance(userid);

		if (iBalance > 0) {
			if (iBalance < 4000000) {
				int iusernumber = getSOLECH(iBalance);
				if ((lSequence % 5) == 0) {
					iusernumber = getRANDOM(iBalance);
				}
				String usernumber = "" + iusernumber;
				usernumber = "00".substring(0, 2 - usernumber.length())
						+ usernumber;

				Util.logger.sysLog(2, this.getClass().getName(),
						"usernumber(<4.000.000):" + usernumber);

				if (iusernumber % 11 != 0) {

					mtReturn = getMTSolech(usernumber, keyword, serviceid);
				} else {

					String sSQLupdate = "update icom_capsohoanhao set  balance = balance + 1000000 where msisdn ='"
							+ userid + "'";

					executeSQL(sSQLupdate);

					Util.logger.sysLog(2, this.getClass().getName(),
							"usernumber(<5.000.000).update:" + sSQLupdate);
					int iBalanceNew = getBalance(userid);
					int iBalanceNewTrieu = iBalanceNew / 1000000;

					Util.logger.sysLog(2, this.getClass().getName(),
							"usernumber(<5.000.000).newbalance:" + iBalanceNew);

					Random iRandom = new Random();
					int i = iRandom.nextInt(2);

					switch (i) {
					case 0:
						mtReturn = "Ban nhan:" + usernumber
								+ ", tai khoan cua ban la " + iBalanceNewTrieu
								+ " trieu\n*Tiep tuc " + keyword + " gui "
								+ serviceid + " de nhan giai thuong 5 trieu"
								+ "\n*HT:" + SUPPORT;

					case 1:
						mtReturn = "Cap so:"
								+ usernumber
								+ ", ban duoc cong 1 trieu vao tai khoan\n*Ban dang co "
								+ iBalanceNewTrieu + " trieu. Tiep tuc "
								+ keyword + " gui " + serviceid
								+ " de nhan giai thuong 5 trieu" + "\n*HT:"
								+ SUPPORT;

					}

					isWinner = true;

				}

			} else {

				int iusernumber = getSOLECH(iBalance);
				String usernumber = "" + iusernumber;
				usernumber = "00".substring(0, 2 - usernumber.length())
						+ usernumber;
				mtReturn = getMTSolech(usernumber, keyword, serviceid);
				Util.logger.sysLog(2, this.getClass().getName(), "is solech");
			}

		} else {
			mtReturn = "Tang ban 1.000.000d vao tai khoan cua ban trong c/t CAP SO HOAN HAO. TK nay chi duoc rut khi ban du 5trieu"
					+ "\n*Soan "
					+ keyword
					+ " gui "
					+ serviceid
					+ " de chien thang" + "\n*HT:" + SUPPORT;
			String sDate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
					.format(new Date());
			String sSQLupdate = "insert into  icom_capsohoanhao(msisdn,balance,date) values('"
					+ userid + "',1000000,'" + sDate + "')";

			executeSQL(sSQLupdate);

			Util.logger.sysLog(2, this.getClass().getName(), "is 1.000.000");

		}

		boolean isSaveFailed = false;
		if (isWinner) {

			if (saveWinner(GAMEID, keyword, serviceid, userid, mtReturn,
					lSequence) < 0) {

				isSaveFailed = true;

			} else {
				// mtReturn = mtReturn;
				sendGifMsg(serviceid, "84963536888", "EVN", keyword, userid
						+ ":" + keyword + "@" + mtReturn, requestid);
			}
		}
		if (isSaveFailed) {
			isWinner = false;
			mtReturn = "Ban nhan so "
					+ "25"
					+ ", ban chua trung thuong\n*Soan tin: "
					+ keyword
					+ " gui "
					+ serviceid
					+ " de la nguoi chien thang voi cac phan thuong 5 trieu, 10 trieu, 15 trieu";
		}

		return mtReturn;
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

			DBUtils.sendMT(msg);

		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}

	int getSOLECH(int iBalance) {
		Random iRandom = new Random();
		int i = iRandom.nextInt(99);
		boolean nextloop = true;
		while (nextloop) {
			i = iRandom.nextInt(99);
			if (i > 0 && (i % 11 != 0)) {
				nextloop = false;
			}
		}
		return i;
	}

	private long getSequence(String gameid, String serviceid) {
		long sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select sequence from icom_sequence where gameid='"
				+ gameid.toUpperCase() + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			String sequence_temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}

			sequence = Long.parseLong(sequence_temp);
			if (sequence > 0) {
				dbpool.cleanup(statement);
				String sqlUpdate = "update icom_sequence set sequence = sequence + 1 where gameid='"
						+ gameid.toUpperCase() + "'";
				statement = cnn.prepareStatement(sqlUpdate);
				if (statement.executeUpdate() != 1) {
					Util.logger
							.error("GetSequence: Update icom_sequence Failed");
				}
			}

		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}

		return sequence;
	}

	private int getBalance(String userid) {
		int sequence = 0;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select balance from icom_capsohoanhao where msisdn='"
				+ userid + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			String sequence_temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();

				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}

			sequence = (int) Long.parseLong(sequence_temp);

		} catch (SQLException ex2) {
			Util.logger.error("getGameid. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("GetGameid. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return sequence;
	}

	private int saveWinner1(String gameid, String keyword, String serviceid,
			String userid, String mttext, long lsequence) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		// Util.logger.info("sendMT:" + msgObject.getUserid()+ "@TO" +
		// msgObject.getServiceid() + "@" + msgObject.getUsertext() );
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return -1;
			}
			String sqlInsert = "Insert into icom_winner (game_id,command_code,user_id,service_id,info,sequence) values ('"
					+ gameid
					+ "','"
					+ keyword
					+ "','"
					+ userid
					+ "','"
					+ serviceid + "','" + mttext + "'," + lsequence + ")";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger
						.error(gameid + ": Insert in to sfone_winner Failed");
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.error(gameid + ": Error:" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error(gameid + ": Error:" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private int saveWinner(String gameid, String keyword, String serviceid,
			String userid, String mttext, long lsequence) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		// Util.logger.info("sendMT:" + msgObject.getUserid()+ "@TO" +
		// msgObject.getServiceid() + "@" + msgObject.getUsertext() );
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return -1;
			}
			String sqlInsert = "Insert into icom_winner (game_id,command_code,user_id,service_id,info,sequence) values ('"
					+ gameid
					+ "','"
					+ keyword
					+ "','"
					+ userid
					+ "','"
					+ serviceid + "','" + mttext + "'," + lsequence + ")";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger
						.error(gameid + ": Insert in to sfone_winner Failed");
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.error(gameid + ": Error:" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error(gameid + ": Error:" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
}