package daugiaplus;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MaHoa {

	 public static String ecrypt(String pass) {
	        MessageDigest md5 = null;
	        try {
	            md5 = MessageDigest.getInstance("MD5"); // Ma hoa MD5
	        }
	        catch (NoSuchAlgorithmException ex) {
	            return null;
	        }
	        System.out.println("MD5: " + md5);
	        md5.update(pass.getBytes());
	        BigInteger bg = new BigInteger(1, md5.digest());
	        return bg.toString(16);    // Biểu diễn bằng chuổi số Hexa
	    }
	 
	 public static final String DATE_FORMAT_NOW = "yyyyMMdd HH:mm:ss";

	  public static String now() {
	    /*Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);*/
		  Calendar now = Calendar.getInstance();
		  String currDate = "";
		  int date1 = now.get(Calendar.DAY_OF_MONTH);
		  String hour = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
		  String second = String.valueOf(now.get(Calendar.SECOND));
		  String minute = String.valueOf(now.get(Calendar.MINUTE));
		  String date = String.valueOf(now.get(Calendar.DATE));
		  String month = String.valueOf(now.get(Calendar.MONTH)+1);
		  String year = String.valueOf(now.get(Calendar.YEAR));
		  if((Integer.parseInt(hour) <20) || (Integer.parseInt(hour) == 20 && Integer.parseInt(minute)==0)) {
			System.out.println("chay ngay hom truoc");  
			 if((Integer.parseInt(hour) >10) || (Integer.parseInt(hour) == 20 && Integer.parseInt(minute)==0)) {
				  if(month.length()==1) {
					  month = "0"+month;
				  }
				  if(date.length() ==1) {
					  date = "0"+date1+1;
				  }
				 currDate = year+month+date;
			  }else {
				  currDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			  }
			  System.out.println(date1);
			  System.out.println(year);
			  String dateYestorday = year+month + date1;
			  System.out.println("date: " + currDate);
		  } else {
			  System.out.println("chay ngay hien tai");
			  
		  }
	    return hour+":"+minute+":"+second;

	  }

	 public static void main(String arg[]) {
		 /*System.out.println(ecrypt("User_8x51DGHws@#$62~!"));
		 System.out.println("Chieu dai cua chuoi là: "+ecrypt("User_8x51DGHws@#$62~!").length());
		 System.out.println("Now : " + now());*/
		 String chuoi = "4\r\nDB:53089#1:200762:29891-136953:13005-10600-47802-07292-49344-209854:6319-4436-2825-14715:3191-0934-2371-7373-9994-14386:717-974-4387:19-52-01-65";
		// String chuoi1 = "4DB:????1:200762:29891-136953:13005-10600-47802-07292-49344-209854:6319-4436-2825-14715:3191-0934-2371-7373-9994-14386:717-974-4387:19-52-01-65";
		 if (chuoi.indexOf("DB:") >= 0 && chuoi.indexOf("?") < 0) {
			 System.out.println("chuoi " + chuoi);
		 }
		 System.out.println("cat1: " + chuoi.substring(11));
		 System.out.println("cat2: " + chuoi.substring(0, 11) );
		 System.out.println("cat2: " + chuoi.substring(11).concat(chuoi.substring(0, 11)).replace("4\r\nDB:", "#"));
		
		 
	 }
}
