package dangky;

import java.math.BigDecimal;
import java.sql.CallableStatement;
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

import com.vmg.soap.mo.SoapSky;
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
public class Nhacchotructiepsuasai_testlisthot extends ContentAbstract {

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

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String dtbase = "gateway";
			String last_msg = "MCA tencasy gui 8751.DTHT 1900571566";

			
			
			String [] ListHost = ListMusicHost(msgObject.getMobileoperator());
			
			String[] arrCode = ListHost[0].split(";");
			String[] arrName = ListHost[1].split(";");
			
			for (int i=0;i<= arrCode.length -1;i++)
			{
				
				Util.logger.info("Code phan tu thu " + i + ":__" + arrCode[i])	;						
				Util.logger.info("Name phan tu thu " + i + ":__" + arrName[i])	;
			}
			return null;

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
	
	

	private static String[] ListMusicHost(String operator) {
		String[] result = new String [2];
		result[0] ="";
		result[1]="";
		
		int operatorID = 0;
		
		if(operator.equalsIgnoreCase("VIETTEL") )
			operatorID =1;
		else if(operator.equalsIgnoreCase("GPC") )
			operatorID =2;
		else if(operator.equalsIgnoreCase("VMS") )
			operatorID =3;
		else if(operator.equalsIgnoreCase("BEELINE") )
			operatorID =4;
		
		
		// String musicid = "";
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("store");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}				
			
			// lay code theo operator
			
			
			String sqlSelect = "EXEC [Mping360].[dbo].[Sp_TotalMedia_Search_Ringback] " +
								"@Type = 0, " + 
								"@BeginRow = 1, " + 
								"@EndRow = 10, " + 
								"@IsActive = 1, " + 
								"@IsHot = 1, " +  
								"@TelcoID = "+operatorID+", " +
								"@NotInCode = ''  ";			
			
			Util.logger.info("GET LIST HOT MUSIC : " + sqlSelect);
			callStmt = connection.prepareCall(sqlSelect);
			Util.logger.info("GET LIST HOT MUSIC");
			if (callStmt.execute()) {
				rs = callStmt.getResultSet();
				// phan tu 0 la Code
				// phan tu 1 la ten bai hat
				while (rs.next()) {
					if(operator.equalsIgnoreCase("VIETTEL") )
						result[0] += rs.getString(5)+ ";";
					else if(operator.equalsIgnoreCase("GPC") )
						result[0] += rs.getString(7)+ ";";
					else if(operator.equalsIgnoreCase("VMS") )
						result[0] += rs.getString(6)+ ";";
					else if(operator.equalsIgnoreCase("BEELINE") )
						result[0] += rs.getString(9)+ ";";
					
					result[1] += rs.getString(13)+ ";";
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
			dbpool.cleanup(callStmt);
			dbpool.cleanup(connection);
		}
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
	
	
	
	
}
