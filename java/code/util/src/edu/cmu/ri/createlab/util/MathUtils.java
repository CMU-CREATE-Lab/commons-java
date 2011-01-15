package edu.cmu.ri.createlab.util;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MathUtils
   {
   public static final double EPSILON = 0.0000001;
   private static final double ZERO = 0.0d;

   /**
    * Ensures that the given <code>value</code> is within the range <code>[-|maxValue|, |maxValue|]</code>.  Returns the
    * given <code>value</code> if it's within the range; otherwise, returns <code>-|maxValue|</code> or
    *  <code>|maxValue|</code> as appropriate.
    */
   public static double ensureRange(final double value, final double maxValue)
      {
      final double absMaxValue = Math.abs(maxValue);

      if (value > absMaxValue)
         {
         return absMaxValue;
         }
      if (value < -absMaxValue)
         {
         return -absMaxValue;
         }

      return value;
      }

   /**
    * Ensures that the given <code>value</code> is within the range <code>[minValue, maxValue]</code>.  Returns the
    * given <code>value</code> if it's within the range; otherwise, returns <code>minValue</code> or
    *  <code>maxValue</code> as appropriate.  Performs idiot checking to make sure that <code>minValue</code> isn't
    * greater than <code>maxValue</code> (swaps the values if it is).
    */
   public static double ensureRange(final double value, final double minValue, final double maxValue)
      {
      final double min;
      final double max;

      // idiot check
      if (minValue > maxValue)
         {
         min = maxValue;
         max = minValue;
         }
      else
         {
         min = minValue;
         max = maxValue;
         }

      if (value > max)
         {
         return max;
         }
      if (value < min)
         {
         return min;
         }

      return value;
      }

   /**
    * Ensures that the given <code>value</code> is within the range <code>[minValue, maxValue]</code>.  Returns the
    * given <code>value</code> if it's within the range; otherwise, returns <code>minValue</code> or
    *  <code>maxValue</code> as appropriate.  Performs idiot checking to make sure that <code>minValue</code> isn't
    * greater than <code>maxValue</code> (swaps the values if it is).
    */
   public static int ensureRange(final int value, final int minValue, final int maxValue)
      {
      final int min;
      final int max;

      // idiot check
      if (minValue > maxValue)
         {
         min = maxValue;
         max = minValue;
         }
      else
         {
         min = minValue;
         max = maxValue;
         }

      if (value > max)
         {
         return max;
         }
      if (value < min)
         {
         return min;
         }

      return value;
      }

   /**
    * Returns <code>true</code> if the given numbers are equal within {@link #EPSILON}; <code>false</code> otherwise.
    */
   public static boolean equals(final double d1, final double d2)
      {
      return equalToWithin(d1, d2, EPSILON);
      }

   /**
    * Returns <code>true</code> if the given numbers are equal within <code>epsilon</code>; <code>false</code> otherwise.
    */
   public static boolean equalToWithin(final double d1, final double d2, final double epsilon)
      {
      return Math.abs(d1 - d2) < epsilon;
      }

   public static boolean isNonZero(final double d)
      {
      return !equals(d, ZERO);
      }

   /**
    * Normalizes the given <code>angle</code> (which must be in radians) and returns an angle in the range
    * <code>[-pi,pi)</code>.
    */
   public static double normalizeAngle(final double angle)
      {
      if ((angle < Math.PI) && (angle >= -Math.PI))
         {
         return angle;
         }
      else if (equals(angle, Math.PI))
         {
         return -Math.PI;
         }
      else if (angle > Math.PI)
         {
         return normalizeAngle(angle - 2 * Math.PI);
         }
      else
         {
         return normalizeAngle(angle + 2 * Math.PI);
         }
      }

   private MathUtils()
      {
      // private to prevent instantiation
      }
   }
