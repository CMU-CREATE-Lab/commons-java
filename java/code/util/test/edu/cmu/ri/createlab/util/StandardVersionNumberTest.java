package edu.cmu.ri.createlab.util;

import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

/**
 * <p>
 * <code>StandardVersionNumberTest</code> tests the {@link StandardVersionNumber} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class StandardVersionNumberTest extends TestCase
   {
   public StandardVersionNumberTest(final String test)
      {
      super(test);
      }

   private final VersionNumber v1a = new StandardVersionNumber("1", "2");
   private final VersionNumber v1b = new StandardVersionNumber("1", "2", "");
   private final VersionNumber v1c = new StandardVersionNumber("1", "2", null);
   private final VersionNumber v2 = new StandardVersionNumber("2", "3", "a");
   private final VersionNumber v3 = new StandardVersionNumber("0", "0");
   private final List<StandardVersionNumber> v3Equivalents = Arrays.asList(
         new StandardVersionNumber("0", "0"),
         new StandardVersionNumber("0", ""),
         new StandardVersionNumber("0", null),
         new StandardVersionNumber("", "0"),
         new StandardVersionNumber("", ""),
         new StandardVersionNumber("", null),
         new StandardVersionNumber(null, "0"),
         new StandardVersionNumber(null, ""),
         new StandardVersionNumber(null, null)
   );

   public void testConstructors()
      {
      assertTrue(v1a.equals(v1b));
      assertTrue(v1a.equals(v1c));
      assertFalse(v1a.equals(v2));
      assertFalse(v1a.equals(v3));
      for (final VersionNumber v : v3Equivalents)
         {
         assertTrue(v3.equals(v));
         assertFalse(v1a.equals(v));
         }
      }

   public void testGetMajorVersion()
      {
      assertEquals("1", v1a.getMajorVersion());
      assertEquals("1", v1b.getMajorVersion());
      assertEquals("1", v1c.getMajorVersion());
      assertEquals("2", v2.getMajorVersion());
      assertEquals("0", v3.getMajorVersion());
      for (final VersionNumber v : v3Equivalents)
         {
         assertEquals("0", v.getMajorVersion());
         }
      }

   public void testGetMinorVersion()
      {
      assertEquals("2", v1a.getMinorVersion());
      assertEquals("2", v1b.getMinorVersion());
      assertEquals("2", v1c.getMinorVersion());
      assertEquals("3", v2.getMinorVersion());
      assertEquals("0", v3.getMinorVersion());
      for (final VersionNumber v : v3Equivalents)
         {
         assertEquals("0", v.getMinorVersion());
         }
      }

   public void testGetRevision()
      {
      assertEquals("", v1a.getRevision());
      assertEquals("", v1b.getRevision());
      assertEquals("", v1c.getRevision());
      assertEquals("a", v2.getRevision());
      assertEquals("", v3.getRevision());
      for (final VersionNumber v : v3Equivalents)
         {
         assertEquals("", v.getRevision());
         }
      }
   }