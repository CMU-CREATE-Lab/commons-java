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

   private ByteUtils()
      {
      // private to prevent instantiation
      }
   }
