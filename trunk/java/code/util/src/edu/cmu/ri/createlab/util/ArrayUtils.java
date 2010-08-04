package edu.cmu.ri.createlab.util;

/**
 * <p>
 * <code>ArrayUtils</code> provides methods for dealing with Arrays.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ArrayUtils
   {
   /**
    * Converts the given array to a {@link String}, with elements separated by a space.  Returns an empty {@link String}
    * if the array is <code>null</code> or empty.
    */
   public static String arrayToString(final boolean[] array)
      {
      return arrayToString(array, " ");
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by the given <code>delimiter</code>.
    * Returns an empty {@link String} if the array is <code>null</code> or empty.
    */
   public static String arrayToString(final boolean[] array, final String delimiter)
      {
      final StringBuffer str = new StringBuffer();
      if ((array != null) && (array.length > 0))
         {
         for (int i = 0; i < array.length; i++)
            {
            final boolean val = array[i];
            str.append(val ? 1 : 0);
            if (i < array.length - 1)
               {
               str.append(delimiter);
               }
            }
         }

      return str.toString();
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by a space.  Returns an empty {@link String}
    * if the array is <code>null</code> or empty.
    */
   public static String arrayToString(final short[] array)
      {
      return arrayToString(array, " ");
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by the given <code>delimiter</code>.
    * Returns an empty {@link String} if the array is <code>null</code> or empty.
    */
   public static String arrayToString(final short[] array, final String delimiter)
      {
      final StringBuffer str = new StringBuffer();
      if ((array != null) && (array.length > 0))
         {
         for (int i = 0; i < array.length; i++)
            {
            final short val = array[i];
            str.append(val);
            if (i < array.length - 1)
               {
               str.append(delimiter);
               }
            }
         }

      return str.toString();
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by a space.  Returns an empty {@link String}
    * if the array is <code>null</code> or empty.
    */
   public static String arrayToString(final int[] array)
      {
      return arrayToString(array, " ");
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by the given <code>delimiter</code>.
    * Returns an empty {@link String} if the array is <code>null</code> or empty.
    */
   public static String arrayToString(final int[] array, final String delimiter)
      {
      final StringBuffer str = new StringBuffer();
      if ((array != null) && (array.length > 0))
         {
         for (int i = 0; i < array.length; i++)
            {
            final int val = array[i];
            str.append(val);
            if (i < array.length - 1)
               {
               str.append(delimiter);
               }
            }
         }

      return str.toString();
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by a space.  Returns an empty {@link String}
    * if the array is <code>null</code> or empty.
    */
   public static String arrayToString(final double[] array)
      {
      return arrayToString(array, " ");
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by the given <code>delimiter</code>.
    * Returns an empty {@link String} if the array is <code>null</code> or empty.
    */
   public static String arrayToString(final double[] array, final String delimiter)
      {
      final StringBuffer str = new StringBuffer();
      if ((array != null) && (array.length > 0))
         {
         for (int i = 0; i < array.length; i++)
            {
            final double val = array[i];
            str.append(val);
            if (i < array.length - 1)
               {
               str.append(delimiter);
               }
            }
         }

      return str.toString();
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by a space.  Returns an empty {@link String}
    * if the array is <code>null</code> or empty.
    */
   public static Object arrayToString(final Object[] array)
      {
      return arrayToString(array, " ");
      }

   /**
    * Converts the given array to a {@link String}, with elements separated by the given <code>delimiter</code>.
    * Returns an empty {@link String} if the array is <code>null</code> or empty.
    */
   public static Object arrayToString(final Object[] array, final String delimiter)
      {
      final StringBuffer str = new StringBuffer();
      if ((array != null) && (array.length > 0))
         {
         for (int i = 0; i < array.length; i++)
            {
            final Object val = array[i];
            str.append(val);
            if (i < array.length - 1)
               {
               str.append(delimiter);
               }
            }
         }

      return str.toString();
      }

   private ArrayUtils()
      {
      // private to prevent instantiation
      }
   }

