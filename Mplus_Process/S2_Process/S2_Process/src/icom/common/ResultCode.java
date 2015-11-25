package icom.common;

public class ResultCode {
	public static final int OK = 0;
	public static final int NOK_REJECT_DUE_TO_SERVICE = 2;
	public static final int NOK_MALFORMED_REQUEST = 3;
	public static final int NOK_MALFORMATTED_PARAMETER = 4;
	public static final int NOK_INVALID_PARAMETER = 5;
	public static final int NOK_UNEXPECTED_VALUE = 6;
	public static final int NOK_TIME_OUT = 7;
	public static final int NOK_MALFORMED_XML_PROLOG = 8;
	public static final int NOK_XML_PARSE_ERROR = 9;
	public static final int NOK_NOT_ENOUGH_CREDIT = 10;
	public static final int NOK_SERVICE_IDENTIFIER = 61;
	public static final int BUCKET_NOT_FOUND = 62;
	public static final int BUCKET_INVALID = 63;
	public static final int OK_LAST_SLICE = 69;
	public static final int OK_DEFAULT_FREE_SLICE = 70;
	public static final int NOK_ACCOUNT_status = 202;
	public static final int NOK_REFUND_OUTDATED = 251;
	public static final int NOK_RELOAD_NOT_ALLOWED = 252;
	public static final int NOK_ACCOUNT_NOT_FOUND = 201;
	//public static final int NOK_ACCOUNT_status = 202;
	public static final int NOK_HIGH_CREDIT = 211;
	public static final int NOK_NEG_PARAM = 232;	
	public static final int NOK_NO_MORE_AVAILABLE_CREDIT = 253;	
	
	public static final int SUSPENDED = 210;
	
	public static String getDescription(int ret)
	{
		switch (ret)
		{		
		case -10:
			return "Connection time out";			
		case -7:
			return "Invalid session";
		case -1:
			return "System error";
		case 0:
			return "OK";
		case 2: 
			return "Failure due to a service problem"; 
		case 3:
			return "Request structure is not valid";
		case 4:
			return "A parameter does not respect the format restrictions";
		case 5:
			return "A parameter has not been found in the database";
		case 6:
			return "Failure due to an inconsistent parameter, this value is used if the data charging application detects ";
		case 7:
			return "The transaction has already been closed";
		case 8:
			return "Received XML prologue does not equal waited prologue";
		case 9:
			return "Error during parser treatment";
		case 10:
			return "Not enough credit on the account";
		case 12:
			return "At least one bucket update failed";
		case 61:
			return "The data charging service detects that the parameters used to identify the service do no exist in its database";
		case 62:
			return "Bucket is not found";
		case 63:
			return "Bucket validity NOK";
		case 69:
			return "The quota allocation has been successfully done, but the DCF service informs the external entity that it will be the last booked credit if the end user doesn’t refill his account";
		case 70:
			return "Specific to quota-based scenario (operator configurable)";
		case 201:
			return "The sub is a postpaid";
		case 202:
			return "The account is created, but not validated";
		case 203:
			return "The account is valid, but not activated";		
		case 205:
			return "The account is inactive";
		case 207:
			return "The account is expired";
		case 208:
			return "The account is blocked (or suspended)";
		case 209:
			return "The account status does not allow the requested action";
		case 210:
			return "The account status is recharge suspended";
		case 211:
			return "The amount to credit in the main balance is higher than the maximum allowed amount per credit transaction";
		case 216:
			return "Too much credit on the main balance; the configured maximum credit threshold is already exceeded. The current credit transaction is refused";		
		case 232:
			return "Negative transaction price passed in the direct debit request";
		case 252:
			return "To be managed like the result 2";
		case 253:
			return "Information mentioning that the concerned account does not contain anymore credit";
		default:
			return ret + "";
		}		
	}
}
