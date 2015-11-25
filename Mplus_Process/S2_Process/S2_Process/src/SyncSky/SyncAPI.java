package SyncSky;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.common.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncAPI {
	public static String SyncMO(MsgObject msgObject) {
		String sRespone = "";
		String sUrl = "";
		String info = msgObject.getUsertext();
		String msisdn = msgObject.getUserid();
		String shortCode = msgObject.getServiceid();
		try {

			sUrl = Constants._prop.getProperty("url_mo_sky",
					"http://115.238.91.226:17020/chargeoversea/mo.do");
			// sUrl = "http://115.238.91.226:17020/chargeoversea/mo.do";
			info = info.replaceAll(" ", "+");

			String sLinkid = msgObject.getRequestid() + "";
			int iMoType = 9;
			int iSpCode = 315;
			int fee = 0;
			String feeType = "ITEM";
			String motime = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());
			String sOprCode = "45201";
			String sCountry = "452";

			sUrl = sUrl + "?linkid=" + sLinkid + "&motype=" + iMoType
					+ "&spcode=" + iSpCode + "&mobile=" + msisdn + "&content="
					+ info + "&shortcode=" + shortCode + "&cost=" + fee
					+ "&feetype=" + feeType + "&motime=" + motime + "&Oprcode="
					+ sOprCode + "&country=" + sCountry;

			Util.logger.info("URL synchronization Mo to sky :" + sUrl);
			// System.out.println("URL synchronization Mo to sky :" + sUrl);
			URL url = new URL(sUrl);

			BufferedReader in = new BufferedReader(new InputStreamReader(url
					.openStream()));
			String str;

			while ((str = in.readLine()) != null) {
				sRespone = sRespone + str;
			}

			Util.logger.info("synchronization Mo to sky @response:" + sRespone);

			in.close();
		} catch (Exception e) {
			Util.logger.info("synchronization Mo to sky error:" + e
					+ " \t@ insert into table error_sky");
			Util.logger.printStackTrace(e);
			insertErrorSky(msisdn, info, shortCode, "0", sUrl, 0, e.toString());
		}
		return sRespone;
	}

	public static String SyncMR(String msisdn, String cost, String requestId) {
		String sRespone = "";
		String sUrl = "";
		try {
			sUrl = Constants._prop.getProperty("url_mr_sky",
					"http://115.238.91.226:17020/chargeoversea/mr.do");
			// sUrl = "http://115.238.91.226:17020/chargeoversea/mr.do";
			String sLinkid = requestId;
			int iSpCode = 315;
			String status = "DELIVRD";
			String mrtime = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());
			String sOprCode = "45201";
			String sCountry = "452";

			sUrl = sUrl + "?linkid=" + sLinkid + "&spcode=" + iSpCode
					+ "&mobile=" + msisdn + "&status=" + status + "&mrtime="
					+ mrtime + "&Oprcode=" + sOprCode + "&country=" + sCountry
					+ "&cost=" + cost;

			Util.logger.info("URL synchronization Mo to sky :" + sUrl);

			// System.out.println("URL synchronization Mo to sky :" + sUrl);
			URL url = new URL(sUrl);

			BufferedReader in = new BufferedReader(new InputStreamReader(url
					.openStream()));
			String str;

			while ((str = in.readLine()) != null) {
				sRespone = sRespone + str;
			}

			in.close();
		} catch (Exception e) {

			Util.logger.info("synchronization Mr to sky error:" + e);
			Util.logger.printStackTrace(e);
			insertErrorSky(msisdn, "", "9209", cost, sUrl, 1, e.toString());
		}
		return sRespone;
	}

	private static int insertErrorSky(String msisdn, String info,
			String shortCode, String cost, String url, int type, String note) {
		int iReturn = 0;

		Connection con = null;
		PreparedStatement ps = null;
		DBPool pool = new DBPool();
		String sql = "INSERT INTO `error_sky`(`user_id`,`info`,`short_code`,`cost`,`url`,`type`,`note`)"
				+ " VALUES('"
				+ msisdn
				+ "','"
				+ info
				+ "','"
				+ shortCode
				+ "','"
				+ cost
				+ "','"
				+ url
				+ "','"
				+ type
				+ "','"
				+ note
				+ "')";
		try {
			con = pool.getConnectionGateway();
			ps = con.prepareStatement(sql);

			if (ps.executeUpdate() < 0) {
				iReturn = 0;
			}
			iReturn = 1;
		} catch (Exception e) {
			Util.logger.error("insertErrorSky error: " + e);
			Util.logger.printStackTrace(e);
		} finally {
			pool.cleanup(ps);
			pool.cleanup(con);
		}

		return iReturn;
	}

}
