package edu.cmu.ri.createlab.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * <code>StringUtils</code> provides some convenience methods for dealing with {@link String}s.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class StringUtils
   {
   private static final Log LOG = LogFactory.getLog(StringUtils.class);

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
            if (LOG.isErrorEnabled())
               {
               LOG.error("NumberFormatException while trying to parse string [" + str + "] as a double", e);
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
