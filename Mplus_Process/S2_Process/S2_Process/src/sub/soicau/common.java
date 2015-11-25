package sub.soicau;

import icom.DBPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class common {
	String x = "x";

	public static String getDataCAU(long companyid) {
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String temp = "";
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection("servicelottery");

			stmt = connection.prepareStatement(
					"SELECT RESULT_TEXT FROM LASTEST_CAU_FULL "
							+ "WHERE result_company_id = " + companyid,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			
			
			if (stmt.executeUpdate() != -1) {
				rs = stmt.getResultSet();

				while (rs.next()) {

					temp = rs.getString(1);
				}
				String ret = temp;

				int i = ret.indexOf("*So ban thich");
				int j = ret.indexOf("8551###");
				
				
				ret =  ret.subSequence(0, i) + ret.substring(j+4);
				return ret;
			}

		} catch (Exception e) {
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}
		return "x";
	}
}
