package edu.cmu.ri.createlab.util;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class StandardVersionNumber implements VersionNumber
   {
   private final String majorVersion;
   private final String minorVersion;
   private final String revision;

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
    * defaults to the empty {@link String}.
    */
   public StandardVersionNumber(final String majorVersion, final String minorVersion, final String revision)
      {
      this.majorVersion = (majorVersion == null || majorVersion.length() == 0) ? "0" : majorVersion;
      this.minorVersion = (minorVersion == null || minorVersion.length() == 0) ? "0" : minorVersion;
      this.revision = (revision == null) ? "" : revision;
      }

   @Override
   public final String getMajorVersion()
      {
      return majorVersion;
      }

   @Override
   public final String getMinorVersion()
      {
      return minorVersion;
      }

   @Override
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

      if (majorVersion != null ? !majorVersion.equals(that.majorVersion) : that.majorVersion != null)
         {
         return false;
         }
      if (minorVersion != null ? !minorVersion.equals(that.minorVersion) : that.minorVersion != null)
         {
         return false;
         }
      if (revision != null ? !revision.equals(that.revision) : that.revision != null)
         {
         return false;
         }

      return true;
      }

   @Override
   public int hashCode()
      {
      int result = majorVersion != null ? majorVersion.hashCode() : 0;
      result = 31 * result + (minorVersion != null ? minorVersion.hashCode() : 0);
      result = 31 * result + (revision != null ? revision.hashCode() : 0);
      return result;
      }
   }
