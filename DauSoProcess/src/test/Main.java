package test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main {
	public static void main (String[] args){
		Queue<String> myQueue = new LinkedList<String>();
		myQueue.add("A");
		myQueue.add("B");
		 
		List<String> myList = new ArrayList<String> (myQueue);
		for (Object ob : myList){
			System.out.println(ob);
		}
		long milisecond = System.currentTimeMillis();
		System.out.println(milisecond);
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(new java.util.Date(milisecond));
		System.out.println(calendar.get(calendar.HOUR_OF_DAY));
		System.out.println(calendar.get(calendar.MONTH));
		System.out.println(calendar.get(calendar.DATE));
		String stringReplace = "java.vietnam.org";
		System.out.println("Replace . by dot : " + stringReplace.replace("\\.", "dot"));
		
	} 

}
