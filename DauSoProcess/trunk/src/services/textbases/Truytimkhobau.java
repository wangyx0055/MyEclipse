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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import java.util.Random;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.sql.ResultSet;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.DBUtils;

import cs.ExecuteADVCR;

public class Truytimkhobau extends ContentAbstract {
	String GAMEID = "TRUYTIMKHOBAU";
	String SUPPORT = "DT Ho tro: 0435561862";

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

	private String mtReturn(String userid, String serviceid, String keyword,
			BigDecimal requestid) throws Exception {
		String mtReturn = "";
		String mtWinner = "";
		long lSequence = 1;
		boolean isWinner = false;
		long lBalance = 0;

		lSequence = getSequence(GAMEID, serviceid);
		Util.logger.info("sequence:" + lSequence);

		if ((lSequence % 25000000) == 0) {
			mtWinner = "Chuc mung ban so huu kho bau KIM CUONG. Ban co 10 trieu VND\nLien he: 01683431885 de nhan giai thuong";
			isWinner = true;
		} else if ((lSequence % 12500000) == 0) {
			mtWinner = "Chuc mung ban mo kho bau VANG. Ban co 5 trieu VND\nLien he: 01683431885"
					+ "\n*Soan tin: "
					+ keyword
					+ " gui "
					+ serviceid
					+ " de tiep tuc mo kho bau"
					+ "\n*OMNIA 900i chua co chu so huu: TE gui 8551";
			isWinner = true;
		} else if ((lSequence % 250000) == 0) {
			mtWinner = "Chuc mung ban co kho bau BAC, ban co 1 trieuVND"
					+ "\n*Soan tin: " + keyword + " gui " + serviceid
					+ " de de so huu cac kho bau"
					+ "\n*OMNIA 900i chua co chu so huu: TE gui 8551";
			isWinner = true;
		} else if ((lSequence % 2500) == 0) {
			lBalance = getBalance(userid);
			Util.logger.info("userid:" + userid + "@balance:" + lBalance);

			long newbalance = lBalance + 10000;

			if (newbalance == 100000) {
				mtWinner = "Ban da co 100000 VND. Lien he toi so: 0435561862 de linh thuong. Tiep tuc soan: "
						+ keyword
						+ " gui "
						+ serviceid
						+ " de tim that nhieu kho bau";
				updateData(0, userid);
			} else {
				mtWinner = "Ban co 10000 VND, soan: " + keyword + " gui "
						+ serviceid + " de tim kho bau gia tri hon"
						+ "\n*So tien hien tai: " + newbalance
						+ " VND, doi tien khi co du 100.000 VND";
				updateData(newbalance, userid);
			}

			isWinner = true;
		} else {
			Random iRandom = new Random();
			int i = iRandom.nextInt(4);
			switch (i) {
			case 0:
				mtReturn = "Ban chua may man. Co hoi Truy Tim Kho Bau dang cho ban"
						+ "\n*Soan "
						+ keyword
						+ " gui "
						+ serviceid
						+ " de mo that nhieu kho bau gia tri" + "\n*" + SUPPORT;
				break;
			case 1:
				mtReturn = "Cac kho bau dang o gan ban. Nhan cang nhieu co hoi cang lon"
						+ "\n*Soan tin: "
						+ keyword
						+ " gui "
						+ serviceid
						+ " de mo kho bau 10 tr VND"
						+ "\n*OMNIA 900i chua co chu so huu: TE gui 8551";
				break;
			case 2:
				mtReturn = "Kho bau dang dan mo ra truoc ban. Co hoi so huu kho bau tri gia"
						+ "\n*Soan tin: "
						+ keyword
						+ " gui "
						+ serviceid
						+ ". Doi tien khi co du 100 ngan" + "\n*" + SUPPORT;
				break;
			case 3:
				mtReturn = "Ban chua may man. Co hoi Truy Tim Kho Bau dang cho ban"
						+ "\n*Soan "
						+ keyword
						+ " gui "
						+ serviceid
						+ " de mo that nhieu kho bau gia tri"
						+ "\n*OMNIA 900i chua co chu so huu: TE gui 8551";
				break;
			}
		}
		boolean isSaveFailed = false;
		if (isWinner) {

			if (saveWinner(GAMEID, keyword, serviceid, userid, mtWinner,
					lSequence) < 0) {

				isSaveFailed = true;

			} else {
				mtReturn = mtWinner;
				sendGifMsg(serviceid, "84963536888", "EVN", keyword, userid
						+ ":" + keyword + "@" + mtReturn, requestid);
			}
		}
		if (isSaveFailed) {
			isWinner = false;
			mtReturn = "Ban chua may man. Co hoi Truy Tim Kho Bau dang cho ban"
					+ "\n*Soan " + keyword + " gui " + serviceid
					+ " de mo that nhieu kho bau gia tri"
					+ "\n*OMNIA 900i chua co chu so huu: TE gui 8551";
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

	private int updateData(long lbalance, String userid) {
		int ireturn = 1;

		String sqlUpdate = "update icom_truytimkhobau set balance=" + lbalance
				+ " where msisdn ='" + userid + "'";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnectionGateway();

			if (lbalance == 10000) {
				String query3 = "select * from icom_truytimkhobau where msisdn ='"
						+ userid + "'";

				Vector result3 = DBUtils.getVectorTable(connection, query3);
				if (result3.size() == 0) {
					sqlUpdate = "insert into icom_truytimkhobau (msisdn, balance) values('"
							+ userid + "',10000)";
				}

			}

			if (DBUtils.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger
						.sysLog(2, this.getClass().getName(),
								": uppdate Statement: Update icom_truytimkhobau Failed");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					":Update  icom_truytimkhobau Failed");
			ireturn = -1;
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
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
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);

			sequence = Long.parseLong(sequence_temp);
			if (sequence > 0) {
				String sqlUpdate = "update icom_sequence set sequence = sequence + 1 where gameid='"
						+ gameid.toUpperCase() + "'";
				statement = cnn.prepareStatement(sqlUpdate);
				if (statement.executeUpdate() != 1) {
					Util.logger
							.error("GetSequence: Update icom_sequence Failed");
				}
				dbpool.cleanup(statement);
			}

		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());

		} finally {
			// dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return sequence;
	}

	private long getBalance(String userid) {
		long balance = 0;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select balance from icom_truytimkhobau where msisdn='"
				+ userid + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			String sequence_temp = "0";
			if (statement.execute()) {
				rs = statement.getResultSet();
				// rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}

			balance = Long.parseLong(sequence_temp);

		} catch (SQLException ex2) {
			Util.logger.error("getGameid. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("GetGameid. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return balance;
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