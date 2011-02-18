package edu.cmu.ri.createlab.util.sequence;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BoundedSequenceNumber implements SequenceNumber
   {
   private final Lock lock = new ReentrantLock();
   private final int min;
   private final int max;
   private int sequenceNumber;

   public BoundedSequenceNumber(final int min, final int max)
      {
      if (min >= max)
         {
         throw new IllegalArgumentException("The min value [" + min + "] must be less than the max value [" + max + "]");
         }

      this.min = min;
      this.max = max;

      sequenceNumber = min;
      }

   public int getMin()
      {
      return min;
      }

   public int getMax()
      {
      return max;
      }

   @Override
   public int next()
      {
      lock.lock();  // block until condition holds
      try
         {
         final int val = sequenceNumber++;
         if (sequenceNumber > max)
            {
            sequenceNumber = min;
            }
         return val;
         }
      finally
         {
         lock.unlock();
         }
      }
   }
