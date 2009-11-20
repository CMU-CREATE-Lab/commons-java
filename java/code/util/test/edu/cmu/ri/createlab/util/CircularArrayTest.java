package edu.cmu.ri.createlab.util;

import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;

/**
 * <p>
 * <code>CircularArrayTest</code> tests the {@link CircularArray} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CircularArrayTest extends TestCase
   {
   public CircularArrayTest(final String test)
      {
      super(test);
      }

   public void testConstructor()
      {
      new CircularArray(10);

      try
         {
         new CircularArray(0);
         fail("Creating a CircularArray with a non-positive size should throw an IllegalArgumentException");
         }
      catch (Exception e)
         {
         assertTrue(true);
         }
      try
         {
         new CircularArray(-1);
         fail("Creating a CircularArray with a non-positive size should throw an IllegalArgumentException");
         }
      catch (Exception e)
         {
         assertTrue(true);
         }
      }

   public void testAddAndCount()
      {
      final int maxSize = 5;
      final CircularArray<Integer> a = new CircularArray<Integer>(maxSize);

      assertEquals("Count should be 0", 0, a.count());

      for (int i = 0; i < maxSize; i++)
         {
         assertEquals("Count should be " + i, i, a.count());
         final int reportedCount = a.add(i * 10);
         assertEquals("Reported count not equal to actual count", reportedCount, a.count());
         assertEquals("Count should be " + (i + 1), (i + 1), a.count());
         }

      // now that the array should be full, the count should be equal to the max size.
      for (int i = 0; i < maxSize; i++)
         {
         assertEquals("Count should be " + maxSize, maxSize, a.count());
         final int reportedCount = a.add(i * 10);
         assertEquals("Reported count not equal to actual count", reportedCount, a.count());
         assertEquals("Reported count not equal to maxSize", reportedCount, maxSize);
         }
      }

   public void testHead()
      {
      final int maxSize = 5;
      final CircularArray<Integer> a = new CircularArray<Integer>(maxSize);

      assertNull("Head should be null", a.head());

      for (int i = 0; i < 20; i++)
         {
         final Integer val = i * 10;
         a.add(val);
         final Integer head = a.head();
         assertEquals("Head should return [" + val + "]", val, head);
         }
      }

   public void testSize()
      {
      for (int i = 1; i < 20; i++)
         {
         final CircularArray<Integer> a = new CircularArray<Integer>(i);
         assertEquals(i, a.size());
         for (int j = 0; j < 10; j++)
            {
            a.add(j * 10);
            assertEquals(i, a.size());
            }
         }
      }

   public void testGetSingleElement()
      {
      final int size = 5;
      final CircularArray<Integer> a = new CircularArray<Integer>(size);

      for (int i = -10; i < size * 2; i++)
         {
         assertNull(a.get(i));
         }
      final Integer v0 = 1;
      final Integer v1 = 2;
      final Integer v2 = 3;
      final Integer v3 = 4;
      final Integer v4 = 5;
      final Integer v5 = 6;

      // now add some elements
      a.add(v0);
      assertEquals(null, a.get(-1));
      assertEquals(a.head(), a.get(0));
      assertEquals(v0, a.get(0));
      assertEquals(null, a.get(1));
      assertEquals(null, a.get(2));
      assertEquals(null, a.get(3));
      assertEquals(null, a.get(4));
      assertEquals(null, a.get(5));

      a.add(v1);
      assertEquals(null, a.get(-1));
      assertEquals(a.head(), a.get(0));
      assertEquals(v1, a.get(0));
      assertEquals(v0, a.get(1));
      assertEquals(null, a.get(2));
      assertEquals(null, a.get(3));
      assertEquals(null, a.get(4));
      assertEquals(null, a.get(5));

      a.add(v2);
      assertEquals(null, a.get(-1));
      assertEquals(a.head(), a.get(0));
      assertEquals(v2, a.get(0));
      assertEquals(v1, a.get(1));
      assertEquals(v0, a.get(2));
      assertEquals(null, a.get(3));
      assertEquals(null, a.get(4));
      assertEquals(null, a.get(5));

      a.add(v3);
      assertEquals(null, a.get(-1));
      assertEquals(a.head(), a.get(0));
      assertEquals(v3, a.get(0));
      assertEquals(v2, a.get(1));
      assertEquals(v1, a.get(2));
      assertEquals(v0, a.get(3));
      assertEquals(null, a.get(4));
      assertEquals(null, a.get(5));

      a.add(v4);
      assertEquals(null, a.get(-1));
      assertEquals(a.head(), a.get(0));
      assertEquals(v4, a.get(0));
      assertEquals(v3, a.get(1));
      assertEquals(v2, a.get(2));
      assertEquals(v1, a.get(3));
      assertEquals(v0, a.get(4));
      assertEquals(null, a.get(5));

      a.add(v5);
      assertEquals(null, a.get(-1));
      assertEquals(a.head(), a.get(0));
      assertEquals(v5, a.get(0));
      assertEquals(v4, a.get(1));
      assertEquals(v3, a.get(2));
      assertEquals(v2, a.get(3));
      assertEquals(v1, a.get(4));
      assertEquals(null, a.get(5));
      }

   public void testGetAndEquals()
      {
      final CircularArray<Integer> a = new CircularArray<Integer>(5);

      assertEquals(new ArrayList<Integer>(), a.get());
      a.add(1);
      assertEquals(new ArrayList<Integer>(Arrays.asList(1)), a.get());
      a.add(2);
      assertEquals(new ArrayList<Integer>(Arrays.asList(2, 1)), a.get());
      a.add(3);
      assertEquals(new ArrayList<Integer>(Arrays.asList(3, 2, 1)), a.get());
      a.add(4);
      assertEquals(new ArrayList<Integer>(Arrays.asList(4, 3, 2, 1)), a.get());
      a.add(5);
      assertEquals(new ArrayList<Integer>(Arrays.asList(5, 4, 3, 2, 1)), a.get());
      a.add(6);
      assertEquals(new ArrayList<Integer>(Arrays.asList(6, 5, 4, 3, 2)), a.get());
      a.add(7);
      assertEquals(new ArrayList<Integer>(Arrays.asList(7, 6, 5, 4, 3)), a.get());
      a.add(8);
      assertEquals(new ArrayList<Integer>(Arrays.asList(8, 7, 6, 5, 4)), a.get());
      a.add(9);
      assertEquals(new ArrayList<Integer>(Arrays.asList(9, 8, 7, 6, 5)), a.get());
      a.add(10);
      assertEquals(new ArrayList<Integer>(Arrays.asList(10, 9, 8, 7, 6)), a.get());
      a.add(11);
      assertEquals(new ArrayList<Integer>(Arrays.asList(11, 10, 9, 8, 7)), a.get());
      }
   }