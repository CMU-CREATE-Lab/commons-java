package edu.cmu.ri.createlab.collections;

import java.util.List;

/**
 * A <code>Dataset</code> is a data structure for storing numbers in a set of a fixed size.  When the dataset is full,
 * adding a new item forces the oldest item to be removed.  This class is thread safe.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Dataset<E extends Number>
   {
   public static final int DEFAULT_SIZE = 256;
   private final CircularArray<E> data;
   private final byte[] dataSynchronizationLock = new byte[0];

   /** Creates a dataset capable of storing at most {@link #DEFAULT_SIZE} values. */
   public Dataset()
   {
   this(DEFAULT_SIZE);
   }

   /**
    * Creates a dataset capable of storing at most <code>size</code> values.
    *
    * @throws IllegalArgumentException if the given <code>size</code> is not a positive integer.
    */
   public Dataset(final int size)
   {
   if (size <= 0)
      {
      throw new IllegalArgumentException("Size must be positive.");
      }
   data = new CircularArray<E>(size);
   }

   /**
    * Adds the given <code>value</code> to the dataset.  If the dataset already contains the maximum number of items,
    * the least recently added item is removed.
    */
   public void append(final E value)
   {
   if (value != null)
      {
      synchronized (dataSynchronizationLock)
         {
         data.add(value);
         }
      }
   }

   /**
    * Returns the current number of items stored in the dataset.  To get the maximum number of items which can be stored
    * in the dataset, use {@link #size()}.
    *
    * @see #size()
    */
   public int count()
   {
   synchronized (dataSynchronizationLock)
      {
      return data.count();
      }
   }

   /**
    * Returns the maximum number of items this dataset can hold.  To get the current number of items stored in the
    * dataset, use {@link #count()}.
    *
    * @see #count()
    */
   public int size()
   {
   synchronized (dataSynchronizationLock)
      {
      return data.size();
      }
   }

   /**
    * Returns a {@link List} containing all the elements in this dataset ordered such that the most recently added item
    * will be at the beginning of the list and the least recently added item will be at the end of the list. The
    * returned list will have a length equal to the number of elements currently stored in this dataset.
    */
   public List<E> getData()
   {
   synchronized (dataSynchronizationLock)
      {
      return data.get();
      }
   }

   /**
    * Returns the element at the given <code>index</code> or <code>null</code> if the index is invalid.  An index is
    * considered invalid if it is negative or greater than the number of elements which have been added to the array.
    */
   public E get(final int index)
   {
   synchronized (dataSynchronizationLock)
      {
      return data.get(index);
      }
   }

   /**
    * Returns the most recently added item or <code>null</code> if no items have been added.  This is a convenience
    * method which returns the same value as calling {@link #get(int) get(0) }.
    *
    * @see #get(int)
    */
   public E head()
   {
   synchronized (dataSynchronizationLock)
      {
      return data.head();
      }
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

      final Dataset dataset = (Dataset)o;

      synchronized (dataSynchronizationLock)
         {
         return !(data != null ? !data.equals(dataset.data) : dataset.data != null);
         }
      }

   public int hashCode()
      {
      synchronized (dataSynchronizationLock)
         {
         return data != null ? data.hashCode() : 0;
         }
      }

   public String toString()
      {
      synchronized (dataSynchronizationLock)
         {
         return "Dataset{" +
                "data=" + data +
                '}';
         }
      }
   }

