package edu.cmu.ri.createlab.util;

import java.util.Arrays;
import junit.framework.TestCase;

/**
 * <p>
 * <code>ByteUtilsTest</code> tests the {@link ByteUtils} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ByteUtilsTest extends TestCase
   {
   public ByteUtilsTest(final String test)
      {
      super(test);
      }

   public void testAsciiHexBytesToByteArray1()
      {
      assertNull(ByteUtils.asciiHexBytesToByteArray(null));

      for (int i = 0; i < 256; i++)
         {
         final byte b = (byte)i;
         final String hexString = ByteUtils.byteToHexString(b);
         final byte[] byteArray1 = ByteUtils.asciiHexBytesToByteArray(hexString.getBytes());
         final byte[] byteArray2 = ByteUtils.asciiHexBytesToByteArray(hexString.toUpperCase().getBytes());
         assertTrue(Arrays.equals(byteArray1, byteArray2));
         assertEquals(b, byteArray1[0]);
         }

      final String asciiHexString = "000033000F000023000001FF";
      final byte[] asciiHexBytes = new byte[]{48, 48, 48, 48, 51, 51, 48, 48, 48, 70, 48, 48, 48, 48, 50, 51, 48, 48, 48, 48, 48, 49, 70, 70};

      final byte[] bytesFromString = asciiHexString.getBytes();
      assertTrue(Arrays.equals(asciiHexBytes, bytesFromString));

      final byte[] byteArray3 = ByteUtils.asciiHexBytesToByteArray(asciiHexBytes);
      final byte[] byteArray4 = new byte[]{0, 0, 51, 0, 15, 0, 0, 35, 0, 0, 1, -1};
      assertTrue(Arrays.equals(byteArray3, byteArray4));

      for (int i = 0; i < asciiHexBytes.length; i += 2)
         {
         final byte[] newArray = new byte[asciiHexBytes.length - i];
         System.arraycopy(asciiHexBytes, i, newArray, 0, newArray.length);

         final byte[] b1 = ByteUtils.asciiHexBytesToByteArray(newArray);
         final byte[] b2 = ByteUtils.asciiHexBytesToByteArray(asciiHexBytes, i, newArray.length);
         assertTrue(Arrays.equals(b1, b2));
         }

      // test negative offset
      try
         {
         ByteUtils.asciiHexBytesToByteArray(asciiHexBytes, -1, 10);
         fail("ArrayIndexOutOfBoundsException expected");
         }
      catch (ArrayIndexOutOfBoundsException e)
         {
         // test passed
         }
      catch (Exception e)
         {
         fail("ArrayIndexOutOfBoundsException expected");
         }

      // test zero length
      try
         {
         ByteUtils.asciiHexBytesToByteArray(new byte[]{});
         fail("ArrayIndexOutOfBoundsException expected");
         }
      catch (ArrayIndexOutOfBoundsException e)
         {
         // test passed
         }
      catch (Exception e)
         {
         fail("ArrayIndexOutOfBoundsException expected");
         }

      // test negative length
      try
         {
         ByteUtils.asciiHexBytesToByteArray(asciiHexBytes, 1, -10);
         fail("IllegalArgumentException expected");
         }
      catch (IllegalArgumentException e)
         {
         // test passed
         }
      catch (Exception e)
         {
         fail("IllegalArgumentException expected");
         }

      // test odd length
      try
         {
         ByteUtils.asciiHexBytesToByteArray(asciiHexBytes, 1, 9);
         fail("IllegalArgumentException expected");
         }
      catch (IllegalArgumentException e)
         {
         // test passed
         }
      catch (Exception e)
         {
         fail("IllegalArgumentException expected");
         }

      // test length
      try
         {
         ByteUtils.asciiHexBytesToByteArray(asciiHexBytes, 0, 9000);
         fail("ArrayIndexOutOfBoundsException expected");
         }
      catch (ArrayIndexOutOfBoundsException e)
         {
         // test passed
         }
      catch (Exception e)
         {
         fail("ArrayIndexOutOfBoundsException expected");
         }

      // test length
      try
         {
         ByteUtils.asciiHexBytesToByteArray(asciiHexBytes, 2, 24);
         fail("ArrayIndexOutOfBoundsException expected");
         }
      catch (ArrayIndexOutOfBoundsException e)
         {
         // test passed
         }
      catch (Exception e)
         {
         fail("ArrayIndexOutOfBoundsException expected");
         }
      }

   // TODO: write tests for the other methods
   }
