package edu.cmu.ri.createlab.util;

/**
 * <p>
 * <code>ByteUtils</code> provides some convenience methods for dealing with bytes.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ByteUtils
   {
   /**
    * Converts a <code>byte</code> which should be treated as unsigned to an <code>int</code> having a range
    * of [0, 255].
    */
   public static int unsignedByteToInt(final byte b)
      {
      return b & 0xff;
      }

   /**
    * Converts a <code>short</code> which should be treated as unsigned to an <code>int</code> having a range
    * of [0, 65535].
    */
   public static int unsignedShortToInt(final short b)
      {
      return b & 0xffff;
      }

   /**
    * Converts the given two <code>bytes</code>s to a <code>short</code>.
    */
   public static short bytesToShort(final byte highByte, final byte lowByte)
      {
      final short high = (short)(highByte & 0xff);
      final short low = (short)(lowByte & 0xff);
      return (short)(high << 8 | low);
      }

   /**
    * Converts the given <code>int</code> to an unsigned <code>byte</code> by ensuring it is within the range of
    * [0, 255] using the following rules:
    * <ul>
    *    <li>If the given <code>int</code> is already within the range [0, 255], then it is simply cast to a <code>byte</code> and returned.</li>
    *    <li>If the given <code>int</code> is negative, this method returns a <code>byte</code> of 0.</li>
    *    <li>If the given <code>int</code> is greater than 255, this method returns a <code>byte</code> of 255 (i.e. the value 255 cast to a byte).</li>
    * </ul>
    */
   public static byte intToUnsignedByte(final int i)
      {
      if (i < 0)
         {
         return 0;
         }
      else if (i > 255)
         {
         return (byte)255;
         }

      return (byte)i;
      }

   /**
    * <p>
    * Converts the bytes specified by <code>offset</code> and <code>length</code> in the given array of bytes containing
    * ASCII hex values to an array of bytes.  For example, if the given array was created like this:
    * </p>
    * <blockquote>
    * <code>final byte[] asciiHexBytes = new byte[]{48, 48, 48, 48, 51, 51, 48, 48, 48, 70, 48, 48, 48, 48, 50, 51, 48, 48, 48, 48, 48, 49, 70, 70};</code>
    * </blockquote>
    * <p>...or, equivalently,  like this...</p>
    * <blockquote>
    * <code>final byte[] asciiHexBytes = "000033000F000023000001FF".getBytes();</code>
    * </blockquote>
    * <p>
    * ...then this method would convert the hex values (<code>00 00 33 00 0F 00 00 23 00 00 01 FF</code>) to bytes and
    * return the a byte array equal to the following:
    * </p>
    * <blockquote>
    * <code>final byte[] byteArray4 = new byte[]{0,0,51,0,15,0,0,35,0,0,1,-1}</code>
    * </blockquote>
    * <p>Returns <code>null</code> if the given array is <code>null</code> .</p>
    *
    * @throws IllegalArgumentException if the <code>length</code> is negative or odd
    * @throws ArrayIndexOutOfBoundsException if the <code>offset</code> is negative or out of bounds, or if the sum of
    * the <code>offset</code> and the <code>length</code> is greater than the length of the given array
    */
   public static byte[] asciiHexBytesToByteArray(final byte[] asciiHexBytes, final int offset, final int length)
      {
      if (asciiHexBytes != null)
         {
         if (offset < 0)
            {
            throw new ArrayIndexOutOfBoundsException("Offset cannot be negative.");
            }

         if (offset >= asciiHexBytes.length)
            {
            throw new ArrayIndexOutOfBoundsException("Offset is out of bounds.");
            }

         if (length < 0)
            {
            throw new IllegalArgumentException("Length cannot be negative.");
            }

         if (length % 2 != 0)
            {
            throw new IllegalArgumentException("Length must be even.");
            }

         if (offset + length > asciiHexBytes.length)
            {
            throw new ArrayIndexOutOfBoundsException("Specified length is too long, not enough elements.");
            }

         // do the conversion (code based on http://mindprod.com/jgloss/hex.html)
         final byte[] output = new byte[length / 2];

         for (int i = offset, j = 0; i < offset + length; i += 2, j++)
            {
            final int high = charToNibble((char)asciiHexBytes[i]);
            final int low = charToNibble((char)asciiHexBytes[i + 1]);
            output[j] = (byte)((high << 4) | low);
            }
         return output;
         }
      return null;
      }

   /**
    * <p>
    * Converts the given array of bytes containing ASCII hex values to an array of bytes.  For example, if the given
    * array was created like this:
    * </p>
    * <blockquote>
    * <code>final byte[] asciiHexBytes = new byte[]{48, 48, 48, 48, 51, 51, 48, 48, 48, 70, 48, 48, 48, 48, 50, 51, 48, 48, 48, 48, 48, 49, 70, 70};</code>
    * </blockquote>
    * <p>...or, equivalently,  like this...</p>
    * <blockquote>
    * <code>final byte[] asciiHexBytes = "000033000F000023000001FF".getBytes();</code>
    * </blockquote>
    * <p>
    * ...then this method would convert the hex values (<code>00 00 33 00 0F 00 00 23 00 00 01 FF</code>) to bytes and
    * return the a byte array equal to the following:
    * </p>
    * <blockquote>
    * <code>final byte[] byteArray4 = new byte[]{0,0,51,0,15,0,0,35,0,0,1,-1}</code>
    * </blockquote>
    * <p>Returns <code>null</code> if the given array is <code>null</code> .</p>
    *
    * @throws IllegalArgumentException if the given array does not have an even number of bytes
    */
   public static byte[] asciiHexBytesToByteArray(final byte[] asciiHexBytes)
      {
      if (asciiHexBytes != null)
         {
         return asciiHexBytesToByteArray(asciiHexBytes, 0, asciiHexBytes.length);
         }
      return null;
      }

   /** Converts the given byte to a (zero-padded, if necessary) hex {@link String}. */
   public static String byteToHexString(final byte b)
      {
      final String s = Integer.toHexString(ByteUtils.unsignedByteToInt(b));

      return (s.length() == 1) ? "0" + s : s;
      }

   private static final byte[] CORRESPONDING_NIBBLE = new byte['f' + 1];

   static
      {
      // only 0..9 A..F a..f have meaning. rest are errors.
      for (int i = 0; i <= 'f'; i++)
         {
         CORRESPONDING_NIBBLE[i] = -1;
         }
      for (int i = '0'; i <= '9'; i++)
         {
         CORRESPONDING_NIBBLE[i] = (byte)(i - '0');
         }
      for (int i = 'A'; i <= 'F'; i++)
         {
         CORRESPONDING_NIBBLE[i] = (byte)(i - 'A' + 10);
         }
      for (int i = 'a'; i <= 'f'; i++)
         {
         CORRESPONDING_NIBBLE[i] = (byte)(i - 'a' + 10);
         }
      }

   private static int charToNibble(final char c)
      {
      if (c > 'f')
         {
         throw new IllegalArgumentException("Invalid hex character: " + c);
         }
      final int nibble = CORRESPONDING_NIBBLE[c];
      if (nibble < 0)
         {
         throw new IllegalArgumentException("Invalid hex character: " + c);
         }
      return nibble;
      }

   private ByteUtils()
      {
      // private to prevent instantiation
      }
   }
