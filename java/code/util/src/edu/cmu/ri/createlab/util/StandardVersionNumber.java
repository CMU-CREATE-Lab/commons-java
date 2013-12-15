package edu.cmu.ri.createlab.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class StandardVersionNumber implements VersionNumber
   {
   private static final String VERSION_PART_DELIMITER = ".";
   private static final Pattern MAJOR_MINOR_REVISION_PATTERN = Pattern.compile("(\\w+)\\.(\\w+)\\.(\\w+)");
   private static final Pattern MAJOR_MINOR_PATTERN = Pattern.compile("(\\w+)\\.(\\w+)\\.?");

   /**
    * Parses the given {@link String} and tries to create a {@link StandardVersionNumber} from it.  Returns
    * <code>null</code> upon failure.
    */
   @Nullable
   public static StandardVersionNumber parse(@Nullable final String versionNumber)
      {
      if (versionNumber != null)
         {
         final Matcher m1 = MAJOR_MINOR_REVISION_PATTERN.matcher(versionNumber);
         if (m1.matches())
            {
            return new StandardVersionNumber(m1.group(1), m1.group(2), m1.group(3));
            }
         else
            {
            final Matcher m2 = MAJOR_MINOR_PATTERN.matcher(versionNumber);
            if (m2.matches())
               {
               return new StandardVersionNumber(m2.group(1), m2.group(2));
               }
            }
         }

      return null;
      }

   @NotNull
   private final String majorVersion;
   @NotNull
   private final String minorVersion;
   @NotNull
   private final String revision;
   @NotNull
   private final String majorMinorRevision;

   /**
    * Creates a {@link StandardVersionNumber} from the given <code>majorVersion</code> and <code>minorVersion</code>.
    * See {@link #StandardVersionNumber(String, String, String)} for more details.
    */
   public StandardVersionNumber(final String majorVersion, final String minorVersion)
      {
      this(majorVersion, minorVersion, "");
      }

   /**
    * Creates a {@link StandardVersionNumber} from the given <code>majorVersion</code>, <code>minorVersion</code>, and
    * <code>revision</code>.  If the <code>majorVersion</code> or <code>minorVersion</code> is <code>null</code> or
    * empty, that component of the version defaults to <code>"0"</code>.  If the revision is <code>null</code>, it
    * defaults to the empty {@link String}. Note that each piece is {@link String#trim() trimmed} before testing whether
    * it is empty.
    */
   public StandardVersionNumber(@Nullable final String majorVersion,
                                @Nullable final String minorVersion,
                                @Nullable final String revision)
      {
      final String majorVersionClean = cleanVersionNumber(majorVersion);
      final String minorVersionClean = cleanVersionNumber(minorVersion);
      final String revisionClean = cleanVersionNumber(revision);
      this.majorVersion = (majorVersionClean == null) ? "0" : majorVersionClean;
      this.minorVersion = (minorVersionClean == null) ? "0" : minorVersionClean;
      this.revision = (revisionClean == null) ? "" : revisionClean;
      this.majorMinorRevision = this.majorVersion +
                                VERSION_PART_DELIMITER +
                                this.minorVersion +
                                (revisionClean == null ? "" : VERSION_PART_DELIMITER + this.revision);
      }

   @Nullable
   private String cleanVersionNumber(@Nullable final String rawVersion)
      {
      if (rawVersion != null)
         {
         final String trimmedVersion = rawVersion.trim();
         if (trimmedVersion.length() > 0)
            {
            return trimmedVersion;
            }
         }
      return null;
      }

   @Override
   @NotNull
   public final String getMajorVersion()
      {
      return majorVersion;
      }

   @Override
   @NotNull
   public final String getMinorVersion()
      {
      return minorVersion;
      }

   @Override
   @NotNull
   public final String getRevision()
      {
      return revision;
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

      final StandardVersionNumber that = (StandardVersionNumber)o;

      if (!majorMinorRevision.equals(that.majorMinorRevision))
         {
         return false;
         }
      if (!majorVersion.equals(that.majorVersion))
         {
         return false;
         }
      if (!minorVersion.equals(that.minorVersion))
         {
         return false;
         }
      if (!revision.equals(that.revision))
         {
         return false;
         }

      return true;
      }

   @Override
   public int hashCode()
      {
      int result = majorVersion.hashCode();
      result = 31 * result + minorVersion.hashCode();
      result = 31 * result + revision.hashCode();
      result = 31 * result + majorMinorRevision.hashCode();
      return result;
      }

   @Override
   @NotNull
   public String toString()
      {
      return majorMinorRevision;
      }
   }
