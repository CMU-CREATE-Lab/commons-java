package edu.cmu.ri.createlab.util.thread;

/**
 * <p>
 * <code>ThreadUtils</code> provides utilities for working with {@link Thread}s.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ThreadUtils
   {
   // NOTE:  Thread enumeration code based on code from http://www.java-forums.org/java-lang/7345-listing-all-threads-threadgroups-vm.html

   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   /** Returns a {@link String} containing an enumeration of all {@link Thread}s in all {@link ThreadGroup}s. */
   public static String enumerateAllThreads()
      {
      ThreadGroup root = Thread.currentThread().getThreadGroup();

      // climb up the tree until we read the parent group
      while (root.getParent() != null)
         {
         root = root.getParent();
         }

      final StringBuffer str = new StringBuffer();
      collectThreadGroupInfo(root, str, "");

      return str.toString();
      }

   /** Collect info about a thread group */
   private static void collectThreadGroupInfo(final ThreadGroup g, final StringBuffer str, final String indent)
      {
      if (g == null)
         {
         return;
         }
      int numThreads = g.activeCount();
      int numGroups = g.activeGroupCount();
      final Thread[] threads = new Thread[numThreads * 2];
      final ThreadGroup[] groups = new ThreadGroup[numGroups * 2];

      numThreads = g.enumerate(threads, false);
      numGroups = g.enumerate(groups, false);

      str.append(indent)
            .append("Thread Group: [")
            .append(g.getName())
            .append("]  Max Priority: [")
            .append(g.getMaxPriority())
            .append(g.isDaemon() ? " Daemon" : "")
            .append("]")
            .append(LINE_SEPARATOR);

      for (int i = 0; i < numThreads; i++)
         {
         if (threads[i] != null)
            {
            collectThreadInfo(threads[i], str, indent + "    ");
            }
         }
      for (int i = 0; i < numGroups; i++)
         {
         collectThreadGroupInfo(groups[i], str, indent + "    ");
         }
      }

   /** Collect info about a thread  */
   private static void collectThreadInfo(final Thread t, final StringBuffer str, final String indent)
      {
      if (t == null)
         {
         return;
         }
      str.append(indent)
            .append("Thread: [")
            .append(t.getName())
            .append("]  Priority: [")
            .append(t.getPriority())
            .append(t.isDaemon() ? " Daemon" : "")
            .append("] ")
            .append(t.isAlive() ? "" : " Not Alive")
            .append(LINE_SEPARATOR);
      }

   private ThreadUtils()
      {
      // private to prevent instantiation
      }
   }
