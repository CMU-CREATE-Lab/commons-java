package edu.cmu.ri.createlab.util;

import junit.framework.TestCase;

/**
 * <p>
 * <code>MathUtilsTest</code> tests the {@link MathUtils} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MathUtilsTest extends TestCase
   {
   public MathUtilsTest(final String test)
      {
      super(test);
      }

   public void testEnsureRange1()
      {
      assertEquals(4.9, MathUtils.ensureRange(4.9, 5.0), MathUtils.EPSILON);
      assertEquals(5.0, MathUtils.ensureRange(5.1, 5.0), MathUtils.EPSILON);
      assertEquals(0, MathUtils.ensureRange(0, 5.0), MathUtils.EPSILON);
      assertEquals(-4.9, MathUtils.ensureRange(-4.9, -5.0), MathUtils.EPSILON);
      assertEquals(-5.0, MathUtils.ensureRange(-5.1, -5.0), MathUtils.EPSILON);

      assertEquals(0, MathUtils.ensureRange(5, 0), MathUtils.EPSILON);
      assertEquals(0, MathUtils.ensureRange(-5, 0), MathUtils.EPSILON);
      assertEquals(0, MathUtils.ensureRange(5, -0), MathUtils.EPSILON);
      assertEquals(0, MathUtils.ensureRange(-5, -0), MathUtils.EPSILON);
      }

   public void testEnsureRange2()
      {
      assertEquals(4.9, MathUtils.ensureRange(4.9, 4.8, 5.0), MathUtils.EPSILON);
      assertEquals(5.0, MathUtils.ensureRange(5.1, 4.8, 5.0), MathUtils.EPSILON);
      assertEquals(4.8, MathUtils.ensureRange(0, 4.8, 5.0), MathUtils.EPSILON);

      assertEquals(4.9, MathUtils.ensureRange(4.9, 5.0, 4.8), MathUtils.EPSILON);
      assertEquals(5.0, MathUtils.ensureRange(5.1, 5.0, 4.8), MathUtils.EPSILON);
      assertEquals(4.8, MathUtils.ensureRange(0, 5.0, 4.8), MathUtils.EPSILON);

      assertEquals(-4.9, MathUtils.ensureRange(-4.9, -4.8, -5.0), MathUtils.EPSILON);
      assertEquals(-5.0, MathUtils.ensureRange(-5.1, -4.8, -5.0), MathUtils.EPSILON);
      assertEquals(-4.8, MathUtils.ensureRange(0, -4.8, -5.0), MathUtils.EPSILON);

      assertEquals(-4.9, MathUtils.ensureRange(-4.9, -5.0, -4.8), MathUtils.EPSILON);
      assertEquals(-5.0, MathUtils.ensureRange(-5.1, -5.0, -4.8), MathUtils.EPSILON);
      assertEquals(-4.8, MathUtils.ensureRange(0, -5.0, -4.8), MathUtils.EPSILON);

      assertEquals(4.9, MathUtils.ensureRange(4.9, -4.8, 5.0), MathUtils.EPSILON);
      assertEquals(5.0, MathUtils.ensureRange(5.1, -4.8, 5.0), MathUtils.EPSILON);
      assertEquals(-4.7, MathUtils.ensureRange(-4.7, -4.8, 5.0), MathUtils.EPSILON);
      assertEquals(-4.8, MathUtils.ensureRange(-4.9, -4.8, 5.0), MathUtils.EPSILON);
      assertEquals(0, MathUtils.ensureRange(0, -4.8, 5.0), MathUtils.EPSILON);

      assertEquals(4.9, MathUtils.ensureRange(4.9, 5.0, -4.8), MathUtils.EPSILON);
      assertEquals(5.0, MathUtils.ensureRange(5.1, 5.0, -4.8), MathUtils.EPSILON);
      assertEquals(-4.7, MathUtils.ensureRange(-4.7, 5.0, -4.8), MathUtils.EPSILON);
      assertEquals(-4.8, MathUtils.ensureRange(-4.9, 5.0, -4.8), MathUtils.EPSILON);
      assertEquals(0, MathUtils.ensureRange(0, 5.0, -4.8), MathUtils.EPSILON);
      }

   // todo: write other tests
   }