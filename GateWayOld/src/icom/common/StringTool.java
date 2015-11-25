package icom.common;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author ICom
 * @version 1.0
 */

import java.util.Random;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;

/**
 * Replace/remove character in a String
 */
public class StringTool {

    //To replace a character at a specified position
    public static String replaceCharAt(String s, int pos, char c) {
        //return s.substring(0, pos) + c + s.substring(pos + 1);
        StringBuffer buf = new StringBuffer( s );
        buf.setCharAt( pos, c );
        return buf.toString( );
    }

    //To remove a character
    public static String removeChar(String s, char c) {
        StringBuffer newString = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char cur = s.charAt(i);
            if (cur != c)
                newString.append(cur);
        }
        return newString.toString();
    }

    //To remove a character at a specified position
    public static String removeCharAt(String s, int pos) {
        //return s.substring(0, pos) + s.substring(pos + 1);
        StringBuffer buf = new StringBuffer( s.length() - 1 );
        buf.append( s.substring(0,pos) ).append( s.substring(pos+1) );
        return buf.toString();
    }

    /*
     * In
     *   text: a string having some seperator(s)
     * Out
     *   a collection of elements without (between) seperator
     */
    public static Collection parseString(String text, String seperator) {
        Vector vResult = new Vector();
        if (text == null || "".equals(text))
            return vResult;

        String tempStr = text.trim();
        String currentLabel = null;

        int index = tempStr.indexOf(seperator);
        while (index != -1) {
            currentLabel = tempStr.substring(0, index).trim();
            //Only accept not null element
            if (!"".equals(currentLabel))
                vResult.addElement(currentLabel);
            tempStr = tempStr.substring(index + 1);
            index = tempStr.indexOf(seperator);
        }
        //Last label
        currentLabel = tempStr.trim();
        if (!"".equals(currentLabel))
            vResult.addElement(currentLabel);
        return vResult;
    }

    final static String[] seperators = {" ", ".", ",", "-", "_", "="};
    public static Collection parseString(String text) {
        Vector vResult = new Vector();
        if (text == null || "".equals(text))
            return vResult;

        String tempStr = text.trim();
        String currentLabel = null;

        int index = getNextIndex(tempStr);
        while (index != -1) {
            currentLabel = tempStr.substring(0, index).trim();
            //Only accept not null element
            if (!"".equals(currentLabel))
                vResult.addElement(currentLabel);
            tempStr = tempStr.substring(index + 1);
            index = getNextIndex(tempStr);
        }
        //Last label
        currentLabel = tempStr.trim();
        if (!"".equals(currentLabel))
            vResult.addElement(currentLabel);
        return vResult;
    }
    private static int getNextIndex(String text) {
        int index = 0;
        int newIdx = 0;
        boolean hasOne = false;
        for (int i = 0; i < seperators.length; i++) {
            newIdx = text.indexOf(seperators[i]);
            if (!hasOne) {
                if (newIdx != -1) {
                    index = newIdx;
                    hasOne = true;
                }
            } else if (newIdx != -1) {
                if (newIdx < index) {
                    index = newIdx;
                }
            }
        }
        if (!hasOne)
            index = -1;
        return index;
    }

    /* Seperator is any charactor not in rage of (0-9), (a-z), (A-Z) */
    public static Collection parseStringEx(String text) {
        Vector vResult = new Vector();
        if (text == null || "".equals(text))
            return vResult;

        String tempStr = text.trim();

        char NINE = (char) 0x39;
        char ZERO = (char) 0x30;
        char CH_a = (char) 'a';
        char CH_z = (char) 'z';
        char CH_A = (char) 'A';
        char CH_Z = (char) 'Z';


        String currLabel = "";
        char currChar = 0;
        for (int i=0; i < tempStr.length(); i++) {
            currChar = tempStr.charAt(i);
            if ( (currChar >= ZERO && currChar <= NINE) ||
                 (currChar >= CH_a && currChar <= CH_z) ||
                 (currChar >= CH_A && currChar <= CH_Z) ) {
                currLabel = currLabel + currChar;
            } else if (currLabel.length() > 0) {
                vResult.add(currLabel);
                currLabel = new String("");
            }
        }
        //last label
        if (currLabel.length() > 0) {
            vResult.add(currLabel);
        }
        return vResult;
    }
    public static boolean isNumberic(String sNumber) {
        if (sNumber == null || "".equals(sNumber)) {
            return false;
        }
        char ch_max = (char) 0x39;
        char ch_min = (char) 0x30;

        for (int i = 0; i < sNumber.length(); i++) {
            char ch = sNumber.charAt(i);
            if ( (ch < ch_min) || (ch > ch_max)) {
                return false;
            }
        }
        return true;
    }

    /**************************************************************************/
    /*                  GENERATE RANDOM STRING OF CHARACTERS                  */
    /**************************************************************************/
    private static char[] charArray = null; //Holds an array of character (used to get the random character for the random string)
    private static Random random = null; //random object
    // Create an arrays of characters (A--Z, 0--9)
    static {
        int numOfChars = 'Z' - 'A' + 1;
        int numOfDigits = '9' - '0' + 1;

        random = new Random(); // create a random object

        charArray = new char[numOfChars + numOfDigits];
        for (int i = 0; i < numOfChars; i++) {
            charArray[i] = (char) ('A' + i);
        }
        for (int i = 0; i < numOfDigits; i++) {
            charArray[numOfChars + i] = (char) ('0' + i);
        }
        //System.out.println(charArray);
    }
    //returns a random string of chars: A--Z, 0--9
    public String generateRandomString(int length) {
        char[] ch = new char[length];
        for (int i = 0; i < length; i++)
            ch[i] = charArray[random.nextInt(charArray.length)];
        return new String(ch);
    }


    public static void main(String args[]) {
        // To replace all occurences of a given character
//        String myString = "Replace ' by * ";
//        //String tmpString = myString.replace('\u0020', '*');
//        String tmpString = myString.replace('\'', '*');
//        System.out.println("Original = " + myString);
//        System.out.println("Result   = " + tmpString);

//        Collection coll = StringTool.parseString("A.B C,D-E.");
//        System.out.println("Size: " + coll.size());
//        for (Iterator it = coll.iterator(); it.hasNext(); ) {
//            System.out.println( (String) it.next());
//        }

        Collection coll = StringTool.parseStringEx("DA.2130444 4455 55595");
        System.out.println("Size: " + coll.size());
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            System.out.println( (String) it.next());
        }


//        String hello = "" + '\00' + 'H' + '\00' + 'E' + '\00' + 'L' + '\00' + 'L' + '\00' + 'O';
//        System.out.println( hello );
//        System.out.println( StringTool.removeChar(hello, '\00'));
    }
}
