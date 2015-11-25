package MySportVote;

import java.sql.SQLException;

import MyConnection.MyExecuteData;
import MyConnection.MyGetData;
import MyDataSource.MyTableModel;

public class Question
{
	public MyExecuteData mExec;
	public MyGetData mGet;

	public Question() throws Exception
	{
		try
		{
			mExec = new MyExecuteData();
			mGet = new MyGetData();
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public Question(String PoolName) throws Exception
	{
		try
		{
			mExec = new MyExecuteData(PoolName);
			mGet = new MyGetData(PoolName);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}


	/**
	 * 
	 * @param Type : Cách thức lấy dữ liệu
	 * <p>Type = 1: Lấy chi tiết 1 Record (Para_1 = QuestionID)</p>
	 * <p>Type = 2: Lấy 1 câu hỏi không nằm trong ListQuestionID đưa vào (@para_1=ListQuestionID)</p>
	 * <p>Type = 3: Lấy 1 câu hỏi random không nằm trong ListQuestionID đưa vào (@para_1=ListQuestionID)</p>
	 * @param Para_1
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public MyTableModel Select(int Type, String Para_1) throws Exception, SQLException
	{
		try
		{
			String Arr_Name[] = {"Type", "Para_1"};
			String Arr_Value[] ={Integer.toString(Type), Para_1};
			
			return mGet.GetData_Pro("Sp_Question_Select", Arr_Name, Arr_Value);
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		catch(Exception ex)
		{
			throw ex;
		}
	}
	 
}
