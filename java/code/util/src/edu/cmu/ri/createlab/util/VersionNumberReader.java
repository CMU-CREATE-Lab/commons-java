package edu.cmu.ri.createlab.util;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>VersionNumberReader</code> reads a version number from a file named <code>version.properties</code> and located
 * at the classpath root.  The version number must be specified with the <code>version</code> property key.
 * </p>
 * <p>
 * For version numbers which match the regex pattern <code>(\w+)\.(\w+)\.(\w+)</code> or <code>(\w+)\.(\w+)\.(\w+)\s+\((.+)\)</code>,
 * the version number will be parsed into major, minor, revision numbers and the build info and can be read
 * individually using the {@link VersionDetails} instance returned by {@link #getVersionDetails()}.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VersionNumberReader
   {
   public interface VersionDetails
      {
      String getMajorVersion();

      String getMinorVersion();

      String getRevision();

      String getMajorMinorRevision();

      String getBuildInfo();
      }

   private static final Logger LOG = Logger.getLogger(VersionNumberReader.class);

   private static final String VERSION_PROPERTIES_FILEPATH = "/version.properties";
   private static final String VERSION_PROPERTY_KEY = "version";
   private static final String VERSION_NUMBER;
   private static final VersionDetails VERSION_DETAILS;
   private static final Pattern MAJOR_MINOR_REVISION_PATTERN = Pattern.compile("(\\w+)\\.(\\w+)\\.(\\w+)");
   private static final Pattern MAJOR_MINOR_REVISION_INFO_PATTERN = Pattern.compile(MAJOR_MINOR_REVISION_PATTERN + "\\s+\\((.+)\\)");

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
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("IOException while trying to read the version number from [" + VERSION_PROPERTIES_FILEPATH + "]", e);
            }
         }
      catch (Exception e)
         {
         // log, but otherwise ignore
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("Exception while trying to read the version number from [" + VERSION_PROPERTIES_FILEPATH + "]", e);
            }
         }

      VERSION_NUMBER = versionNumber;

      final Matcher m1 = MAJOR_MINOR_REVISION_PATTERN.matcher(versionNumber);
      final Matcher m2 = MAJOR_MINOR_REVISION_INFO_PATTERN.matcher(versionNumber);
      if (m2.matches())
         {
         VERSION_DETAILS = new MyVersionDetails(m2.group(1), m2.group(2), m2.group(3), m2.group(4));
         }
      else if (m1.matches())
         {
         VERSION_DETAILS = new MyVersionDetails(m1.group(1), m1.group(2), m1.group(3), null);
         }
      else
         {
         VERSION_DETAILS = null;
         }
      }

   public static String getVersionNumber()
      {
      return VERSION_NUMBER;
      }

   /**
    * Returns an instance of {@link VersionDetails} if the version number matches the regex pattern
    * <code>(\w+)\.(\w+)\.(\w+)</code> or <code>(\w+)\.(\w+)\.(\w+)\s+\((.+)\)</code>.  Returns <code>null</code>
    * otherwise.
    */
   public static VersionDetails getVersionDetails()
      {
      return VERSION_DETAILS;
      }

   private VersionNumberReader()
      {
      // private to prevent instantiation
      }

   private static final class MyVersionDetails implements VersionDetails
      {
      private final String major;
      private final String minor;
      private final String revision;
      private final String info;

      private MyVersionDetails(final String major, final String minor, final String revision, final String info)
         {
         this.major = major;
         this.minor = minor;
         this.revision = revision;
         this.info = info;
         }

      public String getMajorVersion()
         {
         return major;
         }

      public String getMinorVersion()
         {
         return minor;
         }

      public String getRevision()
         {
         return revision;
         }

      public String getMajorMinorRevision()
         {
         final StringBuilder sb = new StringBuilder();
         sb.append(major).append(".").append(minor).append(".").append(revision);
         return sb.toString();
         }

      public String getBuildInfo()
         {
         return info;
         }

      @Override
      public boolean equals(final Object o)
         {
         if (this == o)
            {
            return true;
            }
         if (o == null || getClass() != o.getClass())
            {
            return false;
            }

         final MyVersionDetails that = (MyVersionDetails)o;

         if (major != null ? !major.equals(that.major) : that.major != null)
            {
            return false;
            }
         if (minor != null ? !minor.equals(that.minor) : that.minor != null)
            {
            return false;
            }
         if (revision != null ? !revision.equals(that.revision) : that.revision != null)
            {
            return false;
            }
         if (info != null ? !info.equals(that.info) : that.info != null)
            {
            return false;
            }

         return true;
         }

      @Override
      public int hashCode()
         {
         int result = major != null ? major.hashCode() : 0;
         result = 31 * result + (minor != null ? minor.hashCode() : 0);
         result = 31 * result + (revision != null ? revision.hashCode() : 0);
         result = 31 * result + (info != null ? info.hashCode() : 0);
         return result;
         }

      @Override
      public String toString()
         {
         final StringBuilder sb = new StringBuilder();
         sb.append(major).append('.');
         sb.append(minor).append('.');
         sb.append(revision);
         if (info != null)
            {
            sb.append(" (").append(info).append(')');
            }
         return sb.toString();
         }
      }
   }
