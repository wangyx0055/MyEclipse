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

public class Iphone extends ContentAbstract {
	String SUPPORT = "1900571566";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {

			Collection messages = new ArrayList();
			MsgObject mt = msgObject;

			String sUserid = msgObject.getUserid();
			String sKeyword = msgObject.getKeyword();
			String sServiceid = msgObject.getServiceid();
            
			String sMTReturn = mtReturn(sUserid, "", sServiceid, sKeyword);
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

	private String mtReturn(String userid, String session, String serviceid,
			String keyword) throws Exception {
		String mtReturn = "";
		String mtWinner = "";
		long lSequence = 1;
		boolean isWinner = false;
		lSequence = getSequence(session);
		Util.logger.info("sequence:" + lSequence);
		long lsubsequence = (lSequence % 10);
		String result = "";
		if ((lSequence % 1000000) == 0) {
			// IPHONE
			mtReturn = "Xin  chuc mung ban. Ban da may man nhan duoc chu \"IPHONE\". Chiec IPHONE 4 da thuoc ve ban. Vui long lien he tong dai "
					+ SUPPORT + " de lam thu tuc nhan thuong";
			result = "IPHONE";
		} else if ((lSequence % 100000) == 0) {
			// I
			result = "I";
			int times = countChar(userid, "I", session);
			if (times == 1) {
				mtReturn = "Con cho gi nua!Hay tiep tuc cuoc choi vi ban da co chu \"I\" thuoc ve ban.Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ ".Co hoi so huu chiec IPHONE 4 dang rat gan.DTHT:"
						+ SUPPORT;
			} else if (times == 2) {
				mtReturn = "May man dang den rat gan voi ban! Ban da so huu ky tu \"I\". Nhanh soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " suu tap them cac ky tu con thieu.DTHT:" + SUPPORT;
			} else {
				mtReturn = "Chuc mung ban. Ban da so huu ky tu \"I\"may man.Co hoi so huu Iphone 4 dang den rat gan. Nhanh tay soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap them cac ky tu con thieu.";

			}
		} else if ((lSequence % 2000) == 0) {
			// E
			result="E";
			int times = countChar(userid, "E", session);
			if (times == 1) {
				mtReturn = "Co hoi so huu IPHONE 4 dang  den rat gan. Ban nhan duoc chu \"E\" may man. Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ ".de nhanh tay suu tap cac chu con thieu.DTHT:"
						+ SUPPORT;
			} else if (times == 2) {
				mtReturn = "Chuc mung ban. Ban nhan duoc chu \"E\" may man. Co hoi so hu chiec Iphone 4 dang o rat gan. Hay nhanh tay soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " suu tap them cac ky tu con thieu.DTHT:" + SUPPORT;
			} else {
				mtReturn = "That may man! Ban da so huu ky tu \"E\" Nhanh soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap them cac ky tu con thieu.DTHT:"
						+ SUPPORT;

			}
		} else if ((lsubsequence == 1) || (lsubsequence == 5)
				|| (lsubsequence == 9)) {
			// N
			result="N";
			
			int times = countChar(userid, "N", session);
			if (times == 1) {
				mtReturn = "Chu \"N\" thuoc ve ban. Soan: "
						+ keyword
						+ " gui "
						+ serviceid
						+ " de tim them cho minh nhung chu con thieu trong bo chu IPHONE. Chuc ban may man. DTHT:"
						+ SUPPORT;
			} else if (times == 2) {
				mtReturn = "Ban co them 1 chu \"N\".May man se den voi ban trong tin nhan tiep theo.  Tiep tuc soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de su tap cac chu ban con thieu.DTHT:" + SUPPORT;
			} else {
				mtReturn = "Ban nhan duoc chu \"N\".Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap cac chu khac.Co du bo chu \"PHONE\" trung ngay 500,000d.So huu ngay IPhone4 khi ban co du bo chu \"IPHONE\"";

			}
		} else if ((lsubsequence == 3) || (lsubsequence == 7)) {
			// H
			result="H";
			
			int times = countChar(userid, "H", session);
			if (times == 1) {
				mtReturn = "Chuong trinh gui den ban chu \"H\". Hay soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap cac chu con thieu.Chuc ban may man.DTHT "
						+ SUPPORT;
			} else if (times == 2) {
				mtReturn = "Ban nhan duoc chu \"H\".Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap cac chu khac. Co du bo chu \"PHONE\" trung ngay 500,000d.  So huu ngay IPhone4  khi ban co du bo chu \"IPHONE\"";
			}
		} else if ((lsubsequence == 0) || (lsubsequence == 4)
				|| (lsubsequence == 8)) {
			// P
			result="P";
			
			int times = countChar(userid, "P", session);
			if (times == 1) {
				mtReturn = "Ban da so huu ky tu \"P\".Soan tin:"
						+ keyword
						+ " gui "
						+ serviceid
						+ ".Suu tap du bo chu: I,P,H,O,N,E de so huu ngay chiec Iphone 4 sanh dieu.Dien thoai ho tro:"
						+ SUPPORT;
			} else if (times == 2) {
				mtReturn = "Ban nhan duoc ky tu \"P\".Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap them cac ky tu con lai. Dien thoai ho tro:"
						+ SUPPORT;
			} else if (times == 3) {
				mtReturn = "Ban co them 1 ky tu \"P\".Kien nhan tham gia cuoc choi de suu tap du bo chu IPHONE va danh cho minh chiec Iphone 4 sanh dieu. DTHT"
						+ SUPPORT;
			}
		} else if ((lsubsequence == 2) || (lsubsequence == 6)) {
			// O
			result="O";
			
			int times = countChar(userid, "O", session);

			if (times == 1) {
				mtReturn = "Ban nhan duoc chu \"O\".Soan:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de suu tap cac chu khac.Co du bo chu \"PHONE\" trung ngay 500,000d.So huu ngay IPhon4  khi ban co du bo chu \"IPHONE\"";
			} else if (times == 2) {
				mtReturn = "Ky tu \"O\" da thuoc quyen so huu cua ban.Soan: "
						+ keyword
						+ " gui "
						+ serviceid
						+ ".Suu tap du bo chu: I,P,H,O,N,E de so huu ngay chiec Iphone 4 sanh dieu.DTHT"
						+ SUPPORT;

			} else if (times == 3) {
				mtReturn = "Ban co them 1 ky tu \"O\".Tiep tuc nhan tin den chuong trinh voi cu phap:"
						+ keyword
						+ " gui "
						+ serviceid
						+ " de su tap cai chu ban con thieu.Chuc ban may man.DTHT:"
						+ SUPPORT;
			}

		}
		saveUser(lSequence, userid, result, session);

	
		return mtReturn;
	}

	private long getSequence( String session) {
		long sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select sequence from icom_iphone where session='"
				+ session + "' order by sequence desc limit 1";

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
			Util.logger.error("GetSequence. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return sequence;
	}

	private int countChar(String userid, String c, String session) {
		int sequence = 1;

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select count(1) from icom_iphone where user_id='"
				+ userid + "' and result='" + c + "' and session='" + session
				+ "'";

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

			sequence = Integer.parseInt(sequence_temp);

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

	private int saveUser(long sequence, String user_id, String result,
			String session) throws Exception {

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
			String sqlInsert = "Insert into icom_iphone (sequence,user_id,result,session) values ('"
					+ sequence
					+ "','"
					+ user_id
					+ "','"
					+ result
					+ "','"
					+ session + "')";
			Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger
						.error(user_id + ": Insert in to icom_iphone Failed");
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.error(user_id + ": Error:" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.error(user_id + ": Error:" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

}