package icom.common;

/**
 * <p>Title: SMPP Client</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: ICom</p>
 * @author ICom
 * @version 1.0
 */
import java.io.*;
import java.util.*;

public class FileTool {
    public FileTool() {
    }
    // returns a vector of <code>File</code> objects
    public static Vector getAllFiles(java.io.File location, String fileExt) {
      Vector v = new Vector();
      java.io.File[] list = location.listFiles();
      for (int i = 0; i < list.length; i++) {
          if (list[i].toString().endsWith(fileExt)) {
              v.addElement(list[i]);
          }
          /* also list file in subfolders */
//               if ((list[i]).isDirectory()) {
//                   getAllFiles(list[i]);
//              }
      }
      return v;
  }


    public static byte[] readFile(String filename) throws IOException {
        byte[] buffer = null;
        FileInputStream fin = new FileInputStream(filename);
        buffer = new byte[fin.available()];
        fin.read(buffer);
        return buffer;
    }

    public static void saveToFile(byte[] output, String filename) {
        try {
            File f = new File(filename);
            FileOutputStream out = new FileOutputStream(f);
            out.write(output);
            out.close();
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }


    public static void main(String args[]) {
        FileTool fTool = new FileTool();
        String s = "..\\TEST";
        File f = new File(s);
        Vector v = fTool.getAllFiles(f, ".txt");
        //System.out.println("Size: " + v.size());
    }
}


