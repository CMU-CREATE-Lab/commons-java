package edu.cmu.ri.createlab.util;

import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * <code>VersionNumberReader</code> reads a version number from a file named <code>version.properties</code> and located
 * at the classpath root.  The version number must be specified with the <code>version</code> property key.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VersionNumberReader
   {
   private static final Log LOG = LogFactory.getLog(VersionNumberReader.class);

   private static final String VERSION_PROPERTIES_FILEPATH = "/version.properties";
   private static final String VERSION_PROPERTY_KEY = "version";
   private static final String VERSION_NUMBER;

   static
      {
      final Properties properties = new Properties();
      String versionNumber = "Unknown";

      try
         {
         properties.load(VersionNumberReader.class.getResourceAsStream(VERSION_PROPERTIES_FILEPATH));
         versionNumber = properties.getProperty(VERSION_PROPERTY_KEY, versionNumber);
         }
      catch (IOException e)
         {
         // log, but otherwise ignore
         if (LOG.isErrorEnabled())
            {
            LOG.error("IOException while trying to read the version number from [" + VERSION_PROPERTIES_FILEPATH + "]", e);
            }
         }

      VERSION_NUMBER = versionNumber;
      }

   public static String getVersionNumber()
      {
      return VERSION_NUMBER;
      }

   private VersionNumberReader()
      {
      // private to prevent instantiation
      }
   }
