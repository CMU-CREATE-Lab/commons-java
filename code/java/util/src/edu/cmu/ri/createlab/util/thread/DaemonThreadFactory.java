package edu.cmu.ri.createlab.util.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * A thread factory that creates only daemon threads.  Code almost entirely stolen from the implementation of the
 * {@link Executors} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DaemonThreadFactory implements ThreadFactory
   {
   private static final AtomicInteger poolNumber = new AtomicInteger(1);
   private final ThreadGroup group;
   private final AtomicInteger threadNumber = new AtomicInteger(1);
   private final String namePrefix;

   public DaemonThreadFactory(final String threadFactoryName)
      {
      final SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      namePrefix = threadFactoryName + " pool-" + poolNumber.getAndIncrement() + "-thread-";
      }

   public Thread newThread(final Runnable r)
      {
      final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      t.setDaemon(true);
      if (t.getPriority() != Thread.NORM_PRIORITY)
         {
         t.setPriority(Thread.NORM_PRIORITY);
         }
      return t;
      }
   }