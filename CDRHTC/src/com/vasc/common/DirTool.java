package com.vasc.common;

/**
 * <p>Title: M-Commerce Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
* @author Huynh Ngoc Tuan
 * @version 1.0
 */
import java.io.File;

/**
 * A tool for directory accessing and creating!
 */
public class DirTool {

    /* Create a directory; all ancestor directories must exist */
    public static boolean createOneDirectory(String dirName) {
        boolean success = (new File(dirName)).mkdir();
        if (!success) {
            // Directory creation failed
            return false;
        }
        return true;
    }

    /**
     * Create a directory; all non-existent ancestor directories are
     * automatically created
     */
    public static boolean createAllDirectory(String dirName) {
        boolean success = (new File(dirName)).mkdirs();
        if (!success) {
            // Directory creation failed
            return false;
        }
        return true;
    }


    /* Check if a file or directory is existing ? */
    public static boolean isExist(String dirName) {
        boolean exists = (new File(dirName)).exists();
        return exists;
    }

    /* Getting the Current Working Directory */
    public static String getWorkingDirectory() {
        String curDir = System.getProperty("user.dir");
        return curDir;
    }



    /* Delete an empty directory */
    public static boolean deleteEmptyDirectory(String dirName) {
        boolean success = (new File(dirName)).delete();
        return success;
    }

    /**
     * Delete a non-empty directory
     * Deletes all files and subdirectories under dir.
     * Returns true if all deletions were successful.
     * If a deletion fails, the method stops attempting
     * to delete and returns false.
     */
    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }





    public DirTool() {
    }

}