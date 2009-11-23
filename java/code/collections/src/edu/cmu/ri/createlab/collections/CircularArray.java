package edu.cmu.ri.createlab.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CircularArray<E>
   {
   private final ArrayList<E> data;
   private int head = 0;
   private final int size;

   /** Creates a <code>CircularArray</code> capable of storing at most <code>size</code> elements. */
   public CircularArray(final int size)
      {
      if (size <= 0)
         {
         throw new IllegalArgumentException("Size must be positive.");
         }

      this.size = size;
      data = new ArrayList<E>(size);
      }

   /**
    * Adds the given value to the array and returns the number of items stored in the array.  Does not add the given
    * value if the value is <code>null</code>.  If the array already contains the maximum number of elements, the oldest
    * element (i.e. the least recently added element) is removed.
    */
   public int add(final E value)
      {
      if (value != null)
         {
         if (count() < size)
            {
            data.add(value);
            }
         else
            {
            data.set(head, value);
            }
         head++;

         if (head >= size)
            {
            head = 0;
            }
         }

      return count();
      }

   /**
    * Returns the maximum number of items this array can hold.  To get the current number of items stored in the array,
    * use {@link #count()}.
    *
    * @see #count()
    */
   public int size()
      {
      return size;
      }

   /**
    * Returns the current number of items stored in the array.  To get the maximum number of items which can be stored
    * in the array, use {@link #size()}.
    *
    * @see #size()
    */
   public int count()
      {
      return data.size();
      }

   /**
    * Returns the element at the given <code>index</code> or <code>null</code> if the index is invalid.  An index is
    * considered invalid if it is negative or greater than the number of elements which have been added to the array.
    */
   public E get(final int index)
      {
      if (index >= 0 && index < count())
         {
         final int offset = head - 1 - index;
         if (index <= head - 1)
            {
            return data.get(offset);
            }
         else
            {
            return data.get(size + offset);
            }
         }
      return null;
      }

   /**
    * Returns a {@link List} containing all the elements in this circular array ordered such that the most recently
    * added item will be at the beginning of the list and the least recently added item will be at the end of the list.
    * The returned list will have a length equal to the number of elements currently stored in this circular array.
    */
   public List<E> get()
      {
      final int count = count();
      final List<E> dataCopy = new ArrayList<E>(count);

      if (count > 0)
         {
         // Copy everything in the range [0, head) (but do it in reverse order so
         // that the most recently added item is at the beginning of the list)
         for (int sourcePos = head - 1; sourcePos >= 0; sourcePos--)
            {
            dataCopy.add(data.get(sourcePos));
            }

         // Now, if the array is full, then we know we still need
         // to copy all the elements in the range [head, data.length).  Do that
         // in reverse order as well.
         if (count == size)
            {
            for (int sourcePos = size - 1; sourcePos >= head; sourcePos--)
               {
               dataCopy.add(data.get(sourcePos));
               }
            }
         }

      return dataCopy;
      }

   /**
    * Returns the most recently added item or <code>null</code> if no items have been added.  This is a convenience
    * method which returns the same value as calling {@link #get(int) get(0) }.
    *
    * @see #get(int)
    */
   public E head()
      {
      if (count() > 0)
         {
         final int pos = (head == 0) ? size - 1 : head - 1;
         return data.get(pos);
         }
      return null;
      }

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

      final CircularArray that = (CircularArray)o;

      if (size != that.size)
         {
         return false;
         }
      if (data != null ? !data.equals(that.data) : that.data != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result = data != null ? data.hashCode() : 0;
      result = 31 * result + size;
      return result;
      }

   public String toString()
      {
      final StringBuffer s = new StringBuffer("CircularArray{");

      final List<E> list = get();

      for (int i = 0; i < list.size(); i++)
         {
         s.append(list.get(i));
         if (i < list.size() - 1)
            {
            s.append(",");
            }
         }
      return s.append("}").toString();
      }
   }

