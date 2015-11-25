package com.vasc.common;

/**
 * <p>Title: M-Commerce Team</p>
 * <p>Description: SMPP Gateway Project</p>
 * <p>Copyright: (c) 2003</p>
 * <p>Company: VASC</p>
 * @author Huynh Ngoc Tuan
 * @version 1.0
 */
import java.io.*;
import java.util.*;

public class FileTool
{
	public FileTool()
	{
	}

	// returns a vector of <code>File</code> objects
	public static Vector getAllFiles(java.io.File location, String fileExt)
	{
		Vector v = new Vector();
		java.io.File[] list = location.listFiles();
		if (list != null && list.length > 0)
		{
			for (int i = 0; i < list.length; i++)
			{
				if (list[i].toString().endsWith(fileExt))
				{
					v.addElement(list[i]);
				}
				/* also list file in subfolders */
				// if ((list[i]).isDirectory()) {
				// getAllFiles(list[i]);
				// }
			}
		}
		return v;
	}

	public static void copy(String source_name, String dest_name) throws IOException
	{
		File source_file = new File(source_name);
		File dest_file = new File(dest_name);
		copy(source_file, dest_file);
	}

	public static void copy(File source_file, File dest_file) throws IOException
	{
		FileInputStream source = null;
		FileOutputStream destination = null;

		byte[] buffer;
		int bytes_read;

		// First make sure the specified source file
		// exists, is a file, and is readable.
		if (!source_file.exists() || !source_file.isFile())
			throw new FileCopyException("FileCopy: no such source file: " + source_file.getPath());
		if (!source_file.canRead())
			throw new FileCopyException("FileCopy: source file is unreadable: " + source_file.getPath());
		// If we've gotten this far, then everything is okay;
		// we can copy the file.
		source = new FileInputStream(source_file);
		destination = new FileOutputStream(dest_file);
		buffer = new byte[1024];
		while ((bytes_read = source.read(buffer)) != -1)
		{
			destination.write(buffer, 0, bytes_read);
		}
		// No matter what happens, always close any streams we've opened.
		try
		{
			if (source != null)
				source.close();
			if (destination != null)
				destination.close();
		}
		catch (IOException e)
		{
		}
	}

	public static void move(String source_name, String dest_name) throws IOException
	{
		File source_file = new File(source_name);
		File dest_file = new File(dest_name);

		copy(source_file, dest_file);
		source_file.delete();
	}

	// Maivq them ngay 08/09/2006
	public static void moveVMS(File source_file, File dest_file) throws IOException
	{
		copy(source_file, dest_file);
		// source_file.delete();
	}

	// End Maivq

	public static void move(File source_file, File dest_file) throws IOException
	{
		copy(source_file, dest_file);
		// source_file.delete(); voi VMS thi chua xoa, ma ftp sang vms(test)
	}

	public static byte[] readFile(String filename) throws IOException
	{
		byte[] buffer = null;
		FileInputStream fin = new FileInputStream(filename);
		buffer = new byte[fin.available()];
		fin.read(buffer);
		return buffer;
	}

	public static void saveToFile(byte[] output, String filename) throws IOException
	{
		File f = new File(filename);
		FileOutputStream out = new FileOutputStream(f);

		out.write(output);
		out.close();
	}

	/**
	 * Put a Serialized object to a file The object to be serialized must
	 * implement java.io.Serializable.
	 */
	public static void saveObjectToFile(Object object, String filename) throws IOException
	{
		// Serialize to a file
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(filename));
		out.writeObject(object);
		out.close();
	}

	public static byte[] objectToByte(Object object) throws IOException
	{
		// Serialize to a byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(object);
		out.close();

		// Get the bytes of the serialized object
		byte[] buf = bos.toByteArray();
		return buf;
	}

	public static Object readObjectFromFile(String filename) throws IOException, ClassNotFoundException
	{
		File file = new File(filename);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
		// Deserialize the object
		Object object = in.readObject();
		in.close();
		return object;
	}

	public static void main(String args[])
	{
		FileTool fTool = new FileTool();
		String s = "..\\TEST";
		File f = new File(s);
		Vector v = fTool.getAllFiles(f, ".txt");
		System.out.println("Size: " + v.size());
	}
}

class FileCopyException extends IOException
{
	public FileCopyException(String msg)
	{
		super(msg);
	}
}
