package edu.cmu.ri.createlab.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <p>
 * <code>FileUtils</code> provides methods for dealing with {@link File}s.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FileUtils
   {
   private static final Pattern BEGINNING_OF_INPUT = Pattern.compile("\\A");

   /**
    * <p>
    * Reads in the given file into a {@link String}.  Returns <code>null</code> if the given file is null, does not
    * exist, or is not a file.  Throws a {@link FileNotFoundException} if the file could not be found.
    * </p>
    * <p>
    * Got this code from: http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
    * </p>
    */
   public static String getFileAsString(final File file) throws FileNotFoundException
      {
      if (file != null && file.exists() && file.isFile())
         {
         final Scanner s = new Scanner(file);
         final String text = s.useDelimiter(BEGINNING_OF_INPUT).next();
         s.close();

         return text;
         }
      return null;
      }

   /**
    * <p>
    * Reads in the given file into an array of bytes.  Returns <code>null</code> if the given file is null, does not
    * exist, or is not a file.  Throws an {@link IOException} if the file could not be read.
    * </p>
    * <p>
    * Got this code from: http://javaalmanac.com/egs/java.io/File2ByteArray.html
    * </p>
    */
   public static byte[] getFileAsBytes(final File file) throws IOException
      {
      if (file != null && file.exists() && file.isFile())
         {
         final InputStream is = new FileInputStream(file);

         // Get the size of the file
         final long length = file.length();

         // You cannot create an array using a long type. It needs to be an int type. Before converting to an int type,
         // check to ensure that file is not larger than Integer.MAX_VALUE.
         if (length > Integer.MAX_VALUE)
            {
            throw new IOException("File is too large to read " + file.getName());
            }

         // Create the byte array to hold the data
         final byte[] bytes = new byte[(int)length];

         // Read in the bytes
         int offset = 0;
         int numRead;
         while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
            {
            offset += numRead;
            }

         // Ensure all the bytes have been read in
         if (offset < bytes.length)
            {
            throw new IOException("Could not completely read file " + file.getName());
            }

         // Close the input stream and return bytes
         is.close();
         return bytes;
         }
      return null;
      }

   private FileUtils()
      {
      // private to prevent instantiation
      }
   }
