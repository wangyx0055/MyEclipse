package services.binary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ConsoleSRV;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;

public class XoSoWap extends Thread {

	private String sTime = "16:10;17:10;19:05";
	private String dbcontent = "lotteryincom";

	// Cate ID
	final static int HANOI_COMPANY_ID = 37;
	final static int BINHDUONG_COMPANY_ID = 289;
	final static int SAIGON_COMPANY_ID = 293;
	final static int VUNGTAU_COMPANY_ID = 276;
	final static int BENTRE_COMPANY_ID = 281;
	final static int BACLIEU_COMPANY_ID = 287;
	final static int DONGNAI_COMPANY_ID = 274;
	final static int CANTHO_COMPANY_ID = 280;
	final static int SOCTRANG_COMPANY_ID = 283;
	final static int VINHLONG_COMPANY_ID = 277;
	final static int TRAVINH_COMPANY_ID = 285;
	final static int LONGAN_COMPANY_ID = 378;
	final static int BINHPHUOC_COMPANY_ID = 288;
	final static int DALAT_COMPANY_ID = 294;
	final static int KONTUM_COMPANY_ID = 271;
	final static int KIENGIANG_COMPANY_ID = 278;
	final static int TIENGIANG_COMPANY_ID = 286;
	final static int DONGTHAP_COMPANY_ID = 292;
	final static int CAMAU_COMPANY_ID = 291;
	final static int BINHTHUAN_COMPANY_ID = 268;
	final static int TAYNINH_COMPANY_ID = 284;
	final static int ANGIANG_COMPANY_ID = 290;
	final static int HAUGIANG_COMPANY_ID = 279;
	final static int NINHTHUAN_COMPANY_ID = 267;
	final static int KHANHHOA_COMPANY_ID = 272;
	final static int DANANG_COMPANY_ID = 264;
	final static int BINHDINH_COMPANY_ID = 269;
	final static int DAKLAK_COMPANY_ID = 270;
	final static int GIALAI_COMPANY_ID = 273;
	final static int PHUYEN_COMPANY_ID = 266;
	final static int QUANGNAM_COMPANY_ID = 261;
	final static int QUANGNGAI_COMPANY_ID = 260;
	final static int DACNONG_COMPANY_ID = 265;
	final static int QUANGBINH_COMPANY_ID = 262;
	final static int QUANGTRI_COMPANY_ID = 259;
	final static int HUE_COMPANY_ID = 263;
	final static int BINHDUONG6_COMPANY_ID = 361;

	private final static String[] sCompanyName = { "AGiang", "BDinh", "BDuong",
			"BLieu", "BPhuoc", "BThuan", "BTre", "CMau", "CTho", "DLak",
			"DLat", "DNai", "DNang", "DNong", "DThap", "GLai", "HCM", "HGiang",
			"Hue", "KGiang", "KHoa", "KTum", "LAn", "MB", "NThuan", "PYen",
			"QBinh", "QNam", "QNgai", "QTri", "STrang", "TGiang", "TNinh",
			"TVinh", "VLong", "VTau" };

	private final static String[] sName = { "An Giang", "Binh Dinh",
			"Binh Duong", "Bac Lieu", "Binh Phuoc", "Binh Thuan", "Ben Tre",
			"Ca Mau", "Can Tho", "Dak Lak", "Lam Dong", "Dong Nai", "Da Nang",
			"Dac Nong", "Dong Thap", "Gia Lai", "TP Ho Chi Minh", "Ha Giang",
			"Hue", "Kien Giang", "Khanh Hoa", "Kon Tum", "Long An", "Mien Bac",
			"Ninh Thuan", "Phu Yen", "Quang Binh", "Quang Nam", "Quang Ngai",
			"Quang Tri", "Soc Trang", "Tien Giang", "Tay Ninh", "Tra Vinh",
			"Vinh Long", "Vung Tau" };

	private final static int[] iCompanyID = { ANGIANG_COMPANY_ID,
			BINHDINH_COMPANY_ID, BINHDUONG_COMPANY_ID, BACLIEU_COMPANY_ID,
			BINHPHUOC_COMPANY_ID, BINHTHUAN_COMPANY_ID, BENTRE_COMPANY_ID,
			CAMAU_COMPANY_ID, CANTHO_COMPANY_ID, DAKLAK_COMPANY_ID,
			DALAT_COMPANY_ID, DONGNAI_COMPANY_ID, DANANG_COMPANY_ID,
			DACNONG_COMPANY_ID, DONGTHAP_COMPANY_ID, GIALAI_COMPANY_ID,
			SAIGON_COMPANY_ID, HAUGIANG_COMPANY_ID, HUE_COMPANY_ID,
			KIENGIANG_COMPANY_ID, KHANHHOA_COMPANY_ID, KONTUM_COMPANY_ID,
			LONGAN_COMPANY_ID, HANOI_COMPANY_ID, NINHTHUAN_COMPANY_ID,
			PHUYEN_COMPANY_ID, QUANGBINH_COMPANY_ID, QUANGNAM_COMPANY_ID,
			QUANGNGAI_COMPANY_ID, QUANGTRI_COMPANY_ID, SOCTRANG_COMPANY_ID,
			TIENGIANG_COMPANY_ID, TAYNINH_COMPANY_ID, TRAVINH_COMPANY_ID,
			VINHLONG_COMPANY_ID, VUNGTAU_COMPANY_ID };

	@SuppressWarnings("static-access")
	@Override
	public void run() {

		// Get value from config.cfg
		sTime = Constants._prop.getProperty("timerun", sTime);
		dbcontent = Constants._prop.getProperty("dbcontentincom", dbcontent);
		
		try {
			while (ConsoleSRV.processData) {

				this.sleep(30 * 1000);
				// in true time
				if (isNewSession(sTime)) {

					// Current Date Time
					String currDate = new SimpleDateFormat("dd/MM/yyyy")
							.format(new Date());
					Util.logger.info("Date Time get Lottery Result: "
							+ currDate);

					// Lay Ket qua xo so tu INCOM
					Vector result = getResult(dbcontent, currDate);

					if ((result.size() > 0)) {

						for (int i = 0; i < result.size(); i++) {

							Vector item = (Vector) result.elementAt(i);

							String sKetqua = (String) item.elementAt(0);
							Util.logger.info("Ket qua:" + sKetqua);
							String sGiaiTam = (String) item.elementAt(1);
							String sGiaiBay = (String) item.elementAt(2);
							String sGiaiSau = (String) item.elementAt(3);
							String sGiaiNam = (String) item.elementAt(4);
							String sGiaiTu = (String) item.elementAt(5);
							String sGiaiBa = (String) item.elementAt(6);
							String sGiaiHai = (String) item.elementAt(7);
							String sGiaiNhat = (String) item.elementAt(8);
							String sGiaiDB5 = (String) item.elementAt(9);
							String sGiaiDB6 = (String) item.elementAt(10);
							String sMaTinh = (String) item.elementAt(11);

							// Ma tinh
							int iCateID = 0;
							String TextTitle = "KQ XS ";
							// Delete result of MaTinh in current Date
							// Lay Ma Tinh tu sMaTinh
							for (int j = 0; j < sCompanyName.length; j++) {
								if (sMaTinh.equalsIgnoreCase(sCompanyName[j])) {
									iCateID = iCompanyID[j];
									TextTitle = TextTitle + sName[j] + " ngay "
											+ currDate;
								}
							}

							Util.logger.info("Ma Tinh: " + iCateID);

							// Kiem tra xem da co ton tai ket qua chua ?
							String[] sResultLottery = new String[3];
							sResultLottery = getLottery(iCateID, currDate);
							String sResult = "";
							if (iCateID == 37) {
								sResult = "<div><table id=\"le_ketqua\" width=\"100%\" border=\"0\" cellpadding=\"4\" cellspacing=\"1\"><tbody><tr><td align=\"left\" nowrap=\"nowrap\"><strong>Giai Dac Biet</strong></td>                        <td align=\"center\"><strong>##0 </strong></td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Nhat</td>                        <td align=\"center\">##1 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Nhi</td>                        <td align=\"center\">##2 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Ba</td>                        <td align=\"center\">##3 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Tu</td>                        <td align=\"center\">##4 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Nam</td>                        <td align=\"center\">##5 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Sau</td>                        <td align=\"center\">##6 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Bay</td>                        <td align=\"center\">##7 </td></tr></tbody></table></div>";
							} else {
								sResult = "<div><table id=\"le_ketqua\" width=\"100%\" border=\"0\" cellpadding=\"4\" cellspacing=\"1\"><tbody><tr><td align=\"left\" nowrap=\"nowrap\"><strong>Giai Dac Biet</strong></td>                        <td align=\"center\"><strong>##0 </strong></td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Nhat</td>                        <td align=\"center\">##1 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Nhi</td>                        <td align=\"center\">##2 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Ba</td>                        <td align=\"center\">##3 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Tu</td>                        <td align=\"center\">##4 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Nam</td>                        <td align=\"center\">##5 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Sau</td>                        <td align=\"center\">##6 </td>                     </tr>                                          <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Bay</td>                        <td align=\"center\">##7 </td></tr>                     <tr>                        <td align=\"left\" nowrap=\"nowrap\">Giai Tam</td>                        <td align=\"center\">##8 </td></tr></tbody></table></div>";
							}
							sResult = sResult.replace("##1", sGiaiNhat);
							sResult = sResult.replace("##2", sGiaiHai);
							sResult = sResult.replace("##3", sGiaiBa);
							sResult = sResult.replace("##4", sGiaiTu);
							sResult = sResult.replace("##5", sGiaiNam);
							sResult = sResult.replace("##6", sGiaiSau);
							sResult = sResult.replace("##7", sGiaiBay);
							sResult = sResult.replace("##8", sGiaiTam);
							if (iCateID == 37) {
								sResult = sResult.replace("##0", sGiaiDB5);
							} else {

								// Chu y cai nay. Co nhung tinh se co giai dac
								// biet 5. Co nhung tinh chi co Giai Dac Biet 6
								if ("".equalsIgnoreCase(sGiaiDB6)
										|| "null".equalsIgnoreCase(sGiaiDB6)
										|| sGiaiDB6 == null) {
									sResult = sResult.replace("##0", sGiaiDB5);
								} else {
									sResult = sResult.replace("##0", sGiaiDB6);
								}
							}

							Util.logger.info("Result Lottery: " + sResult);

							Util.logger.info("Result_: " + sResultLottery[0]);
							// Neu chua co noi dung cua iCompanyNumber trong he
							// thong

							if ("".equalsIgnoreCase(sResultLottery[0])) {

								String sCode = getCode();
								Util.logger.info("TCode: " + sCode);

								// Convert Code
								String sConvertCode = convertCode(sCode);
								Util.logger.info("T Code Convert: "
										+ sConvertCode);

								// Insert
								try {
									insertResultLottery(sResult, iCateID,
											sConvertCode, TextTitle);

									String TextID = getTCode(sConvertCode);
									String SearchContent = TextID + ", "
											+ TextTitle;

									// Insert TextbaseSearch
									insertTextBaseSearch(TextID, sConvertCode,
											TextTitle, iCateID, SearchContent);

								} catch (Exception e) {
									Util.logger.error("Error: "
											+ e.getMessage());
								}

							} else {

								try {
									updateLottery(sResult, iCateID, currDate);

								} catch (Exception e) {
									Util.logger.error("Error: "
											+ e.getMessage());
								}

							}

						}

					}
				}
			}
		} catch (Exception ex) {
			Util.logger.info("Error: executeMsg.run :" + ex.toString());
		}

	}

	// / Convert Code to Next Code
	private String convertCode(String sCode) {
		String temp = "";
		String sNumber = "";
		try {
			temp = sCode.substring(1);
			Util.logger.info("TCode1: " + temp);
			int iNumber = Integer.parseInt(temp);
			iNumber = iNumber + 1;
			Util.logger.info("TCode Next: " + iNumber);
			sNumber = iNumber + "";

			if (sNumber.length() < 5) {

				if (sNumber.length() == 4) {
					sNumber = "T0" + sNumber;
				} else if (sNumber.length() == 3) {
					sNumber = "T00" + sNumber;
				}

			} else {
				sNumber = "T" + sNumber;
			}

		} catch (Exception ex) {
			Util.logger.error("Loi phat sinh khi Convert");
			return "";
		}
		return sNumber;
	}

	// �?úng gi�? thì mới chạy
	public static boolean isNewSession(String sTime) {

		String[] arrTime = sTime.split(";");

		for (int i = 0; i < arrTime.length; i++) {
			String sTime2Queue = arrTime[i];

			String[] arrH = new String[20];
			int iHour = 0;
			int iMinute = 0;
			arrH = sTime2Queue.split(":");
			if (arrH.length > 1) {
				iHour = Integer.parseInt(arrH[0].trim());
				iMinute = Integer.parseInt(arrH[1].trim());
			} else {
				iHour = Integer.parseInt(arrH[0].trim());

			}
			long milliSecond = System.currentTimeMillis();
			java.util.Calendar calendar = java.util.Calendar.getInstance();

			calendar.setTime(new java.util.Date(milliSecond));
			if (((calendar.get(calendar.HOUR_OF_DAY) == iHour) && (calendar
					.get(calendar.MINUTE) >= iMinute))
					&& (calendar.get(calendar.MINUTE)) <= 40) {
				return true;
			}

		}
		return false;
	}

	// Get Result lottery from INCOM
	private Vector getResult(String dbcontent, String sDate) {

		Vector result = null;

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection(dbcontent);

			String sQuery = "use xoso SELECT lot_content, lot_prize08, lot_prize07, lot_prize06, lot_prize05, lot_prize04, lot_prize03, lot_prize02, lot_prize01, lot_prize15, lot_prize16, loc_matinh FROM "
					+ " tblKetqua WHERE CONVERT(varchar(25), [lot_timestamp], 103)='"
					+ sDate + "'";

			Util.logger.info("SQL Select DB INCOM: " + sQuery);

			result = getVectorTable(connection, sQuery);

			Util.logger.info("Result size: " + result.size());

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + " getInfo: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);

		} finally {
			dbpool.cleanup(connection);
		}
		return result;
	}

	// Get Result lottery from INCOM
	private String getCode() {

		String result = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection("wapicom");

			String sQuery = "use WAPICOM SELECT Code FROM "
					+ " TextBase WHERE TextID = (select max(TextID) from textbase)";

			Util.logger.info("Lay TCode: " + sQuery);

			Vector result1 = getVectorTable(connection, sQuery);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + sQuery);

			if (result1.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result1.elementAt(0);
				String rs = (String) item.elementAt(0);
				Util.logger.info("TCODE lan dau: " + rs);
				return rs;
			}

			Util.logger.info("Result size: " + result1.size());

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + " getInfo: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);

		} finally {
			dbpool.cleanup(connection);
		}
		return result;
	}

	public String insertResultLottery(String sResult, int iCateID, String Code,
			String TextTitle) throws Exception {

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "TextBase";

		sSQLInsert = "use WAPICOM Insert into " + tablename
				+ "(Code,CateID, TextContent, TextTitle)" + " VALUES (?,?,?,?)";

		Util.logger.info("SQL Insert: " + sSQLInsert);

		try {

			connection = dbpool.getConnection("wapicom");

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, Code);
			statement.setString(2, iCateID + "");
			statement.setString(3, sResult);
			statement.setString(4, TextTitle);

			if (statement.executeUpdate() != 1) {
				Util.logger.error("insert Lottery:"
						+ ":statement.executeUpdate failed");
				return "-1";
			}
			statement.close();
			return "1";

		} catch (SQLException e) {
			Util.logger.error("insert Lottery:"
					+ ":Error add row from TextBase:" + e.toString());
			return "-1";
		} catch (Exception e) {
			Util.logger.error("insertMO2lottery:"
					+ ":Error add row from TextBase:" + e.toString());
			return "-1";
		}

		finally {
			dbpool.cleanup(connection);

		}

	}

	// //
	public String insertTextBaseSearch(String TextID, String Code,
			String TextTitle, int CateID, String SearchContent)
			throws Exception {

		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "TextBaseSearch";

		sSQLInsert = "use WAPICOM Insert into " + tablename
				+ "(TextID,Code, TextTitle, CateID, SearchContent)"
				+ " VALUES (?,?,?,?,?)";

		Util.logger.info("SQL Insert: " + sSQLInsert);

		try {

			connection = dbpool.getConnection("wapicom");

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, TextID);
			statement.setString(2, Code);
			statement.setString(3, TextTitle);
			statement.setString(4, CateID + "");
			statement.setString(5, SearchContent);

			if (statement.executeUpdate() != 1) {
				Util.logger.error("insert Lottery:"
						+ ":statement.executeUpdate failed");
				return "-1";
			}
			statement.close();
			return "1";

		} catch (SQLException e) {
			Util.logger.error("insert Lottery:"
					+ ":Error add row from TextBase:" + e.toString());
			return "-1";
		} catch (Exception e) {
			Util.logger.error("insertMO2lottery:"
					+ ":Error add row from TextBase:" + e.toString());
			return "-1";
		}

		finally {
			dbpool.cleanup(connection);

		}

	}

	public static Vector getVectorTable(Connection cnn, String strSQL)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;
		Vector vt = null;
		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			vt = convertToVector(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			Util.logger.info("SQL Exception: " + ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			// Throw exception if has error
			Util.logger.info("Exception: " + ex.getMessage());
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return vt;
	}

	// Update Lottery
	public boolean updateLottery(String sResult, int iCompanyId, String currDate)
			throws Exception {

		PreparedStatement statement = null;
		String sSQLUpdate = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		boolean result = false;

		String tablename = "TextBase";

		sSQLUpdate = "use WAPICOM UPDATE " + tablename + " SET TextContent='"
				+ sResult + "' WHERE CateID='" + iCompanyId
				+ "' AND CONVERT(varchar(25), [CreateDate], 103)='" + currDate
				+ "'";

		Util.logger.info("SQL Update: " + sSQLUpdate);

		try {
			connection = dbpool.getConnection("wapicom");

			statement = connection.prepareStatement(sSQLUpdate);

			if (statement.executeUpdate() != 1) {
				Util.logger.error("insertMO2lottery:"
						+ ":statement.executeUpdate failed");
				result = false;
			}
			result = true;

		} catch (SQLException e) {
			Util.logger.error("insertMO2lottery:"
					+ ":Error add row from LOTTERY_RESULTS_FULL:"
					+ e.toString());
		} catch (Exception e) {
			Util.logger.error("insertMO2lottery:"
					+ ":Error add row from LOTTERY_RESULTS_FULL:"
					+ e.toString());
		}

		finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return result;

	}

	// Update Lottery
	public boolean updateTextBaseSearch(String sResult, int iCompanyId,
			String currDate) throws Exception {

		PreparedStatement statement = null;
		String sSQLUpdate = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		boolean result = false;

		String tablename = "TextBaseSearch";

		sSQLUpdate = "use WAPICOM UPDATE " + tablename + " SET TextContent='"
				+ sResult + "' WHERE CateID='" + iCompanyId
				+ "' AND CONVERT(varchar(25), [CreateDate], 103)='" + currDate
				+ "'";

		Util.logger.info("SQL Update: " + sSQLUpdate);

		try {
			connection = dbpool.getConnection("wapicom");

			statement = connection.prepareStatement(sSQLUpdate);

			if (statement.executeUpdate() != 1) {
				Util.logger.error("insertMO2lottery:"
						+ ":statement.executeUpdate failed");
				result = false;
			}
			result = true;

		} catch (SQLException e) {
			Util.logger.error("insertMO2lottery:"
					+ ":Error add row from LOTTERY_RESULTS_FULL:"
					+ e.toString());
		} catch (Exception e) {
			Util.logger.error("insertMO2lottery:"
					+ ":Error add row from LOTTERY_RESULTS_FULL:"
					+ e.toString());
		}

		finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return result;

	}

	public static void closeObject(ResultSet obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeObject(PreparedStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Get DB for check is Exist or not Exist
	public String[] getLottery(int companyId, String currDate) {
		Connection conn = null;
		String[] strResult = new String[3];
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {

			conn = dbpool.getConnection("wapicom");
			strResult[0] = "";
			strResult[1] = "";
			strResult[2] = "";
			sSql = "use WAPICOM Select TextID, Code, TextContent From TextBase where CateID = "
					+ companyId
					+ " and CONVERT(varchar(25), [CreateDate], 103)='"
					+ currDate + "' ";

			stmt = conn.prepareStatement(sSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			Util.logger.info("Cau lenh SQL: " + sSql);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				if (rs.next()) {
					strResult[0] = rs.getString("TextID");
					Util.logger.info("TextID: " + strResult[0]);
					strResult[1] = rs.getString("Code");
					strResult[2] = rs.getString("TextContent");
				}
			}

		} catch (SQLException ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		} catch (Exception ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(conn);
		}
		return strResult;
	}

	// Get DB for check is Exist or not Exist
	public String getTCode(String TCode) {
		Connection conn = null;
		String strResult = "";
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {

			conn = dbpool.getConnection("wapicom");

			sSql = "use WAPICOM Select TextID From TextBase where Code = '"
					+ TCode + "'";

			stmt = conn.prepareStatement(sSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			Util.logger.info("Cau lenh SQL 1: " + sSql);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				if (rs.next()) {
					strResult = rs.getString("TextID");
				}
			}

		} catch (SQLException ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		} catch (Exception ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(conn);
		}
		return strResult;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Convert data from ResultSet into Vector
	 * 
	 * @param rsData
	 *            Opened ResultSet
	 * @throws Exception
	 *             when working with ResultSet
	 */
	// ///////////////////////////////////////////////////////////////
	public static Vector convertToVector(ResultSet rsData) throws Exception {
		Vector vctReturn = new Vector();
		int iColumnCount = rsData.getMetaData().getColumnCount();
		while (rsData.next()) {
			Vector vctRow = new Vector();
			for (int i = 1; i <= iColumnCount; i++) {
				String strValue = rsData.getString(i);
				if (strValue == null) {
					strValue = "";
				}
				vctRow.addElement(strValue);
			}
			vctReturn.addElement(vctRow);
		}
		vctReturn.trimToSize();
		return vctReturn;
	}

}
