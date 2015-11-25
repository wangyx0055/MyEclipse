package icom;

import icom.common.Util;

import java.util.ArrayList;
import java.util.List;

class testuannq {
	public static void main(String[] args) {
		// init
		String amount = "6";
		List<Integer> lTop20 = new ArrayList<Integer>();

		// init
		for (int i = 0; i < 20; i++) {
			lTop20.add(new Integer(i));
		}

		String sOut = "Top 20 gia can chien thang truoc:\t";
		for (Integer index : lTop20) {
			sOut = sOut + index.intValue() + " ";
		}
		System.out.println(sOut);
		if (Integer.parseInt(amount) < lTop20.get(19).intValue()) {
			// remote gia thu 19
			lTop20.remove(19);
			lTop20.add(new Integer(amount));
			for (int i = 0; i < 19; i++) {
				for (int j = i+1; j < 20; j++) {
					if (lTop20.get(i).intValue() > lTop20.get(j).intValue()) {
						
						int temp = lTop20.get(i).intValue();
						lTop20.set(i, new Integer(lTop20.get(j).intValue()));
						lTop20.set(j, new Integer(temp)); 
					}
				}
			}
		}
		
		sOut = "Top 20 gia can chien thang sau:\t";
		for (Integer index : lTop20) {
			sOut = sOut + index.intValue() + " ";
		}
		System.out.println(sOut);
	}

	public void test() {
		List<ObjTest> lTest = new ArrayList<ObjTest>();
		for (int i = 0; i < 1000; i++) {
			ObjTest obj = null;
			if (i % 10 == 9)
				obj = new ObjTest(i + "", "0");
			else
				obj = new ObjTest(i + "", "1");
			lTest.add(obj);
		}

		// search min
		for (ObjTest objTest : lTest) {
			if (objTest.getStatus().equals("0")) {
				System.out.println(objTest.getAmount());
				break;
			}
		}

		// search with index
		ObjTest objSearch = lTest.get(51);
		System.out.println(objSearch.getAmount() + " : "
				+ objSearch.getStatus());

		System.out.println("done");
	}

}

class ObjTest {
	String amount;

	public ObjTest(String amount, String status) {
		super();
		this.amount = amount;
		this.status = status;
	}

	String status;

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}