package edu.cmu.ri.createlab.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>StringUtils</code> provides some convenience methods for dealing with {@link String}s.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class StringUtils
   {
   private static final Logger LOG = Logger.getLogger(StringUtils.class);

   /** Converts the given {@link String} to a {@link Double}, or returns <code>null</code> if the conversion failed. */
   public static Double convertStringToDouble(final String str)
   {
   if (str != null && !"".equals(str))
      {
      try
         {
         return Double.parseDouble(str);
         }
      catch (NumberFormatException e)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("NumberFormatException while trying to parse string [" + str + "] as a Double", e);
            }
         }
      }
   return null;
   }

   /** Converts the given {@link String} to a {@link Integer}, or returns <code>null</code> if the conversion failed. */
   public static Integer convertStringToInteger(final String str)
   {
   if (str != null && !"".equals(str))
      {
      try
         {
         return Integer.parseInt(str);
         }
      catch (NumberFormatException e)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("NumberFormatException while trying to parse string [" + str + "] as an Integer", e);
            }
         }
      }
   return null;
   }

   private StringUtils()
      {
      // private to prevent instantiation
      }
   }
