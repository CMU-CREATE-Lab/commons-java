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

   public void testParse()
      {
      assertEquals("1.2", StandardVersionNumber.parse("1.2").toString());
      assertEquals("1.2", StandardVersionNumber.parse("1.2.").toString());
      assertEquals("10.2", StandardVersionNumber.parse("10.2").toString());
      assertEquals("10.20", StandardVersionNumber.parse("10.20").toString());
      assertEquals("1.20", StandardVersionNumber.parse("1.20").toString());
      assertEquals("1.20.40", StandardVersionNumber.parse("1.20.40").toString());
      assertNull(StandardVersionNumber.parse(""));
      assertNull(StandardVersionNumber.parse(null));
      assertNull(StandardVersionNumber.parse(" "));
      assertNull(StandardVersionNumber.parse("\t"));
      assertNull(StandardVersionNumber.parse("foobar"));
      assertNull(StandardVersionNumber.parse("1."));
      assertNull(StandardVersionNumber.parse("1.2.2.2"));
      assertNull(StandardVersionNumber.parse(".1"));
      assertNull(StandardVersionNumber.parse(".10"));
      }

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

   public void testToString()
      {
      assertEquals("1.2", v1a.toString());
      assertEquals("1.2", v1b.toString());
      assertEquals("1.2", v1c.toString());
      assertEquals("2.3.a", v2.toString());
      assertEquals("0.0", v3.toString());
      for (final VersionNumber v : v3Equivalents)
         {
         assertEquals("0.0", v.toString());
         }
      }
   }