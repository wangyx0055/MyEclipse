package MySportVote;

import java.util.Date;

public class SubscriberObject
{
	public String MSISDN = null;
	public int TelcoID = 0;
	public Date RegisterDate = null;
	public Date FirstQuestionDate = null;
	public int LastQuestionID = 0;
	public Date LastQuestionDate = null;
	public int PreQuestionID = 0;
	public Date LastAnswerDate = null;
	public int LastAnswer = 0;
	public int RightAnswerAgain = 0;
	public int WrongAnswerAgain = 0;
	public int LastMark = 0;
	public int TotalMark = 0;
	public int PID = 0;
	public int StatusID = 0;
	public Date RemindDate = null;
	public int RemindCount = 0;
	public String ListQuestionID = null;
	public int QuestionCount = 0;
	public int QuestionCountByDay = 0;
	public Date QuestionByDayDate = null;
	public boolean IsNull()
	{
		if(MSISDN == null || MSISDN.equalsIgnoreCase(""))
			return true;
		else
			return false;
	}
}
