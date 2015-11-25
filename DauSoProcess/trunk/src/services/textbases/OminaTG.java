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
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.ResultSet;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class OminaTG extends ContentAbstract {
	String GAMEID = "OMINA";
	String INFOCS = "E71 mien phi, Soan: AJ gui 8551 de so huu";
	String SUPPORT = "0435561862";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {

			Collection messages = new ArrayList();
			MsgObject mt = msgObject;

			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();

			String sMTReturn = mtReturn(sUserid, sServiceid, sKeyword);
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

	private String mtReturn(String userid, String serviceid, String keyword)
			throws Exception {
		String mtReturn = "";
		String mtWinner = "";
		long lSequence = 1;
		boolean isWinner = false;

		lSequence = getSequence(GAMEID, serviceid);
		Util.logger.error("sequence:" + lSequence);

		long lsubsequence = (lSequence % 10);

		if ((lSequence % 2000000) == 0) {
			mtWinner = "Xin chuc mung ban!Ban co ky tu \"A\""
					+ "\nSoan: "
					+ keyword
					+ " gui "
					+ serviceid
					+ " de suu tap du bo chu cai con lai de so huu Samsung Omnia i900 dang cap";
			isWinner = true;
		} else if ((lsubsequence == 1) || (lsubsequence == 5)
				|| (lsubsequence == 9)) {
			mtReturn = "Chuc mung ban da co ky tu \"O\""
					+ "\n*Soan tin: "
					+ keyword
					+ " gui "
					+ serviceid
					+ " de suu tap du 5 ky tu: O,M,N,I,A so huu ngay Samsung Omnia i900"
					+ "\n*HT:" + SUPPORT;

		} else if ((lsubsequence == 3) || (lsubsequence == 7)) {
			mtReturn = "Ky tu \"M\" da la cua ban." + "\n*Tiep tuc soan tin: "
					+ keyword + " gui " + serviceid
					+ ", co hoi so huu Samsung Omnia i900 dang den gan" + "\n*"
					+ INFOCS;

		} else if ((lsubsequence == 2) || (lsubsequence == 4)
				|| (lsubsequence == 6) || (lsubsequence == 8)) {
			mtReturn = "May man den gan voi ban! Ban co ky tu \"N\""
					+ "\n*Soan tin: " + keyword + " gui " + serviceid
					+ " suu tam de so huu Samsung Omnia i900" + "\n*" + INFOCS
					+ "\n*HT:" + SUPPORT;

		} else {
			mtReturn = "Hay tiep tuc vi ban da co chu \"I\"" + "\n*Soan tin: "
					+ keyword + " gui " + serviceid
					+ " de co co hoi so huu Sámung Omnia i900\n*" + INFOCS;
		}
		boolean isSaveFailed = false;
		if (isWinner) {

			if (saveWinner(GAMEID, keyword, serviceid, userid, mtWinner,
					lSequence) < 0) {

				isSaveFailed = true;

			} else {
				mtReturn = mtWinner;
			}
		}
		if (isSaveFailed) {
			isWinner = false;
			mtReturn = "Con cho gi nua! Hay tiep tuc cuoc choi vi ban da co chu \"I\""
					+ "\n*Soan tin: "
					+ keyword
					+ " gui "
					+ serviceid
					+ " de co co hoi so huu Omnia i900\n*HT:" + SUPPORT;
		}
		return mtReturn;
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
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return sequence;
	}

	private long getBalance(String userid) {
		long sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select banlance from icom_truytimkhobau where msisdn='"
				+ userid + "'";

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

}