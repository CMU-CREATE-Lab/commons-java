package edu.cmu.ri.createlab.util.security;

import junit.framework.TestCase;

/**
 * <p>
 * <code>EncryptionServiceTest</code> tests the {@link EncryptionService} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class EncryptionServiceTest extends TestCase
   {
   public EncryptionServiceTest(final String test)
      {
      super(test);
      }

   public void testEncrypt()
      {
      assertEquals("EZeVXkJEwYvbmzdQdry2ygVBRnI=", EncryptionService.getInstance().encrypt("c0"));
      assertEquals("LyJ2XQSTGgeJCRRcpijSJkyFLX0=", EncryptionService.getInstance().encrypt("c1"));
      assertEquals("ax9TMDpzLMyMaq5mQDmYJ8FSUOM=", EncryptionService.getInstance().encrypt("c2"));
      assertEquals("piVAb2l31FwTkbB49NNlbgt1v8s=", EncryptionService.getInstance().encrypt("c3"));
      assertEquals("5GZqZw8EKHfGeoRHOnFnXuCVCgg=", EncryptionService.getInstance().encrypt("c4"));
      assertEquals("jcKfxYwL2ZBowuXHUqphUh1PEc4=", EncryptionService.getInstance().encrypt("c5"));
      assertEquals("VVw/khi6QaWWUZyPAXCKDsnvghs=", EncryptionService.getInstance().encrypt("c6"));
      assertEquals("3WGptZPfYzNdwKzw/UNJZiswdW0=", EncryptionService.getInstance().encrypt("c7"));
      assertEquals("n4Sta4ncJmcMDW56P4EJO0HARDg=", EncryptionService.getInstance().encrypt("c8"));
      assertEquals("aO5099av4BZP4PEZeqkXfJRtiDQ=", EncryptionService.getInstance().encrypt("c9"));
      assertEquals("I/g0Zadj+XkCMW0d/o/nm8VTauo=", EncryptionService.getInstance().encrypt("r0"));
      assertEquals("VXPjm2YASW1A9JPQDsdlhHmhlgc=", EncryptionService.getInstance().encrypt("r1"));
      assertEquals("pQEmzC1scm3gyiA8O2WfZY01YXM=", EncryptionService.getInstance().encrypt("r2"));
      assertEquals("qokzWL5LUG2K61Kym4qcrN1pW2Q=", EncryptionService.getInstance().encrypt("r3"));
      assertEquals("fiZBONDKEDuHIFeGK5sDWZYuxdI=", EncryptionService.getInstance().encrypt("r4"));
      assertEquals("USNsXa8ZngTCgyVLw6xlWp6UUrc=", EncryptionService.getInstance().encrypt("r5"));
      assertEquals("m/V5qSdll0n+mBceirJ6wPOac0s=", EncryptionService.getInstance().encrypt("r6"));
      assertEquals("2vHM8EKtvJ66U76oAdYXDvvGyao=", EncryptionService.getInstance().encrypt("r7"));
      assertEquals("TkNsgFP1ZVmxuY+RaJg+TdtQRFY=", EncryptionService.getInstance().encrypt("r8"));
      assertEquals("D8ZQUjNLFbYQrYq9XTA+HR6F28E=", EncryptionService.getInstance().encrypt("r9"));
      }
   }