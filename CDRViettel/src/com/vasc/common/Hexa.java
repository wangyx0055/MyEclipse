package com.vasc.common;
public class Hexa {

    private  char Hex;

    public Hexa (int i) {

	switch(i){
	    case 0 : Hex='0';break;
	    case 1 : Hex='1';break;
	    case 2 : Hex='2';break;
	    case 3 : Hex='3';break;
	    case 4 : Hex='4';break;
	    case 5 : Hex='5';break;
	    case 6 : Hex='6';break;
	    case 7 : Hex='7';break;
	    case 8 : Hex='8';break;
	    case 9 : Hex='9';break;
	    case 10: Hex='A';break;
	    case 11: Hex='B';break;
	    case 12: Hex='C';break;
	    case 13: Hex='D';break;
	    case 14: Hex='E';break;
	    case 15: Hex='F';break;
	}
    }

    public Hexa (char c) {
	Hex = c;
    }

    public Hexa (String Bits) {
	if (Bits.length() != 4)
	    System.out.println("Impossible de convertir en Hexa la chaine de bits");
	else {
	    int hex = 0;
	    for(int i=0;i<4;i++) {
		char temp = Bits.charAt(i);
		int j = 0;
		if (temp == '1') j=1;
		hex = 2*hex + j;
	    }
	    switch(hex){
	    case 0 : Hex='0';break;
	    case 1 : Hex='1';break;
	    case 2 : Hex='2';break;
	    case 3 : Hex='3';break;
	    case 4 : Hex='4';break;
	    case 5 : Hex='5';break;
	    case 6 : Hex='6';break;
	    case 7 : Hex='7';break;
	    case 8 : Hex='8';break;
	    case 9 : Hex='9';break;
	    case 10: Hex='A';break;
	    case 11: Hex='B';break;
	    case 12: Hex='C';break;
	    case 13: Hex='D';break;
	    case 14: Hex='E';break;
	    case 15: Hex='F';break;
	    }
  	}
    }


    public int getInt() {
	int i=0;
	switch(Hex){
	    case '0' : i= 0;break;
	    case '1' : i= 1;break;
	    case '2' : i= 2;break;
	    case '3' : i= 3;break;
	    case '4' : i= 4;break;
	    case '5' : i= 5;break;
	    case '6' : i= 6;break;
	    case '7' : i= 7;break;
	    case '8' : i= 8;break;
	    case '9' : i= 9;break;
	    case 'A' : i= 10;break;
	    case 'B' : i= 11;break;
	    case 'C' : i= 12;break;
	    case 'D' : i= 13;break;
	    case 'E' : i= 14;break;
	    case 'F' : i= 15;break;
	}
	return i;
    }

    public String HexaToBits() {
	int i = getInt();
	String bits = "";
	switch(i){
	case 0 : bits =  "0000";break;
	case 1 : bits =  "0001";break;
	case 2 : bits =  "0010";break;
	case 3 : bits =  "0011";break;
	case 4 : bits =  "0100";break;
	case 5 : bits =  "0101";break;
	case 6 : bits =  "0110";break;
	case 7 : bits =  "0111";break;
	case 8 : bits =  "1000";break;
	case 9 : bits =  "1001";break;
	case 10: bits =  "1010";break;
	case 11: bits =  "1011";break;
	case 12: bits =  "1100";break;
	case 13: bits =  "1101";break;
	case 14: bits =  "1110";break;
	case 15: bits =  "1111";break;
	}
	return bits;
    }

    public boolean isHexa() {
	return (getInt()>=0 && getInt()<=15);
    }

    public char getHexa() {
	return Hex;
    }

    public void setHexa(char c) {
	Hex = c;
    }

   public String toString() {
       return HexaToBits();
   }

    public void display() {
	System.out.println("Hexa : " + Hex);
    }
}
