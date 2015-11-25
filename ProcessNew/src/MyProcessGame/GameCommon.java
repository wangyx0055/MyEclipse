package MyProcessGame;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import MySportVote.SessionObject;

public class GameCommon
{
	/**
	 * Kiểm tra session có tồn tại hay không
	 * 
	 * @param ListSessionObject
	 *            Danh sách các Session Đã được lấy lên từ database
	 * @param ReceiveDate
	 *            Thời gian nhận được tin nhắn
	 * @return
	 * 
	 * @throws Exception
	 */
	public static boolean CheckSession(Vector<SessionObject> ListSessionObject, Timestamp ReceiveDate) throws Exception
	{
		try
		{
			if (ReceiveDate == null)
			{
				java.util.Date mDate = new java.util.Date();
				ReceiveDate = new Timestamp(mDate.getTime());
			}
			for (SessionObject mOject : ListSessionObject)
			{
				if (ReceiveDate.after(mOject.BeginDate) && ReceiveDate.before(mOject.EndDate))
					return true;
			}

			return false;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}
