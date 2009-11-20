package edu.cmu.ri.createlab.util.net;

import junit.framework.TestCase;

/**
 * <p>
 * <code>HostAndPortTest</code> tests the {@link HostAndPort} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HostAndPortTest extends TestCase
   {
   private static final String TEST_NULL = null;
   private static final String TEST_0 = "";
   private static final String TEST_1 = "192.168.0.1:3712";
   private static final String TEST_2 = "192.168.0.1:";
   private static final String TEST_3 = "192.168.0.1";
   private static final String TEST_4 = ":3712";
   private static final String TEST_5 = "3712";
   private static final String TEST_6 = "192.168.0.1 :3712";
   private static final String TEST_7 = "192.168.0.1: 3712";
   private static final String TEST_8 = "192.168.0.1 : 3712";
   private static final String TEST_9 = " 192.168.0.1 :3712 ";
   private static final String TEST_10 = " 192.168.0.1: 3712 ";
   private static final String TEST_11 = " 192.168.0.1 : 3712 ";
   private static final String TEST_12 = " 192.168.0.1 3712 ";
   private static final String TEST_13 = " : 3712 ";
   private static final String TEST_14 = " 192.168.0.1 ";

   public HostAndPortTest(final String test)
      {
      super(test);
      }

   public void testAll()
      {
      checkIsNull(TEST_0);
      checkAssertions(TEST_1, "192.168.0.1", "3712", TEST_1);
      checkIsNull(TEST_2);
      checkAssertions(TEST_3, "192.168.0.1", null, "192.168.0.1");
      checkIsNull(TEST_4);
      checkAssertions(TEST_5, "3712", null, "3712");
      checkAssertions(TEST_6, "192.168.0.1", "3712", TEST_1);
      checkAssertions(TEST_7, "192.168.0.1", "3712", TEST_1);
      checkAssertions(TEST_8, "192.168.0.1", "3712", TEST_1);
      checkAssertions(TEST_9, "192.168.0.1", "3712", TEST_1);
      checkAssertions(TEST_10, "192.168.0.1", "3712", TEST_1);
      checkAssertions(TEST_11, "192.168.0.1", "3712", TEST_1);
      checkIsNull(TEST_12);
      checkIsNull(TEST_13);
      checkAssertions(TEST_14, "192.168.0.1", null, "192.168.0.1");
      try
         {
         HostAndPort.createHostAndPort(TEST_NULL);
         fail("Creating a HostAndPort with a null input should throw an IllegalArgumentException");
         }
      catch (IllegalArgumentException e)
         {
         assertTrue(true);
         }
      }

   private void checkIsNull(final String testString)
      {
      assertNull(HostAndPort.createHostAndPort(testString));
      }

   private void checkAssertions(final String testString, final String expectedHost, final String expectedPort, final String expectedHostAndPort)
      {
      final HostAndPort hostAndPort = HostAndPort.createHostAndPort(testString);
      assertEquals(expectedHost, hostAndPort.getHost());
      assertEquals(expectedPort, hostAndPort.getPort());
      assertEquals(expectedHostAndPort, hostAndPort.getHostAndPort());
      }

   public void testIsValid()
      {
      assertFalse(HostAndPort.isValid(null));
      assertFalse(HostAndPort.isValid(""));
      assertFalse(HostAndPort.isValid(":"));
      assertFalse(HostAndPort.isValid(":42"));
      assertFalse(HostAndPort.isValid("-"));
      assertFalse(HostAndPort.isValid("foo-"));
      assertFalse(HostAndPort.isValid("foo_"));
      assertFalse(HostAndPort.isValid("-foo"));
      assertFalse(HostAndPort.isValid("_foo"));
      assertFalse(HostAndPort.isValid("1.2.3:"));
      assertFalse(HostAndPort.isValid("1.2.3 :"));
      assertFalse(HostAndPort.isValid("1.2.3 : "));
      assertFalse(HostAndPort.isValid("localhost:port"));
      assertFalse(HostAndPort.isValid("www.terk.ri. cmu.edu"));
      assertFalse(HostAndPort.isValid("www.terk.ri.cmu.edu:42:"));
      assertFalse(HostAndPort.isValid("www.terk.ri.cmu.edu:42:101"));
      assertFalse(HostAndPort.isValid("www.terk.ri.cmu.edu:foo"));
      assertFalse(HostAndPort.isValid("www.terk.ri.cmu.edu:-42"));
      assertFalse(HostAndPort.isValid("www.terk.ri_.cmu.edu"));
      assertFalse(HostAndPort.isValid("www.terk._ri.cmu.edu"));
      assertFalse(HostAndPort.isValid("_www.terk.ri.cmu.edu"));
      assertFalse(HostAndPort.isValid("www.terk.ri.cmu.edu_"));
      assertFalse(HostAndPort.isValid("_www.terk.ri.cmu.edu_"));
      assertFalse(HostAndPort.isValid("-www.terk.ri.cmu.edu"));
      assertFalse(HostAndPort.isValid("www.terk.ri.cmu.edu-"));
      assertFalse(HostAndPort.isValid("-www.terk.ri.cmu.edu-"));

      assertTrue(HostAndPort.isValid("foo"));
      assertTrue(HostAndPort.isValid("4foo"));
      assertTrue(HostAndPort.isValid("foo2"));
      assertTrue(HostAndPort.isValid("4foo2"));
      assertTrue(HostAndPort.isValid("f.o"));
      assertTrue(HostAndPort.isValid("1"));
      assertTrue(HostAndPort.isValid("1.2"));
      assertTrue(HostAndPort.isValid("1.2.3"));
      assertTrue(HostAndPort.isValid("1.2.3:42"));
      assertTrue(HostAndPort.isValid("1.2.3: 42"));
      assertTrue(HostAndPort.isValid("1.2.3 :42"));
      assertTrue(HostAndPort.isValid("1.2.3 : 42"));
      assertTrue(HostAndPort.isValid(" 1.2.3 : 42"));
      assertTrue(HostAndPort.isValid("1.2.3 : 42 "));
      assertTrue(HostAndPort.isValid(" 1.2.3 : 42 "));
      assertTrue(HostAndPort.isValid("localhost"));
      assertTrue(HostAndPort.isValid("localhost:10101"));
      assertTrue(HostAndPort.isValid("localhost: 10101"));
      assertTrue(HostAndPort.isValid("localhost :10101"));
      assertTrue(HostAndPort.isValid("localhost : 10101"));
      assertTrue(HostAndPort.isValid("localhost : 10101 "));
      assertTrue(HostAndPort.isValid(" localhost : 10101 "));
      assertTrue(HostAndPort.isValid(" www.terk.ri.cmu.edu "));
      assertTrue(HostAndPort.isValid(" www.terk.ri.cmu.edu : 10101 "));
      assertTrue(HostAndPort.isValid(" www.t--k.ri.cmu.edu "));
      assertTrue(HostAndPort.isValid(" www.4--k.ri.cmu.edu "));
      assertTrue(HostAndPort.isValid(" www.t--2.ri.cmu.edu "));
      assertTrue(HostAndPort.isValid(" www.4--2.ri.cmu.edu "));
      assertTrue(HostAndPort.isValid(" www.4__2.ri.cmu.edu "));
      }
   }