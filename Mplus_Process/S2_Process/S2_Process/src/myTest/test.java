package myTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String test = "1259";
		if(checkAmount(test)){
			System.out.println("ok con de");
		}else{
			System.out.println("not ok con be");
		}
	}
	private static Boolean checkAmount(String amount){
		
		Pattern icomPattern = Pattern.compile("\\D");
		Matcher match = icomPattern.matcher(amount);
		
		if(match.find()){
			return false;
		}
		
		return true;
	}

}
