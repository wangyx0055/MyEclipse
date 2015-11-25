package MostStep;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.process.DBPool;

public class Subutil {
	public static int executeSQL(String poolname, String sql) {

		PreparedStatement statement = null;
		Connection cnn = null;
		DBPool dbpool = new DBPool();

		try {

			cnn = dbpool.getConnection(poolname);
			statement = cnn.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();

			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			closeObject(statement);
			closeObject(cnn);

		}
	}

	public static Vector getVectorTable(String poolname, String strSQL)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;
		Vector vt = null;
		Connection cnn = null;
		DBPool dbpool = new DBPool();
		try {

			cnn = dbpool.getConnection(poolname);
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			vt = DBUtil.convertToVector(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
			closeObject(cnn);
		}
		return vt;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(PreparedStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(CallableStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Statement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(ResultSet obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Connection obj) {
		try {
			if (obj != null) {
				if (!obj.isClosed()) {
					if (!obj.getAutoCommit()) {
						obj.rollback();
					}
					obj.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
