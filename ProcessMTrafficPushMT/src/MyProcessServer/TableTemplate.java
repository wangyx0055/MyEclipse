package MyProcessServer;

import MyDataSource.MyTableModel;

/**
 * Chứa các đối tượng đã table mẫu ở Database Các đối tượng này sẽ được load khi
 * chương trình bắt đầu chạy
 * 
 * @author Administrator
 * 
 */
public class TableTemplate
{
	public MyTableModel mActionLog = null;
	public MyTableModel mNews = null;
	public MyTableModel mPushTimeLog = null;
}
