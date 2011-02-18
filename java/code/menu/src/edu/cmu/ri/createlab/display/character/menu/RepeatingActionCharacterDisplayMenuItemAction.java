package edu.cmu.ri.createlab.display.character.menu;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.display.character.CharacterDisplay;
import edu.cmu.ri.createlab.menu.MenuItem;
import edu.cmu.ri.createlab.menu.MenuStatusManager;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>RepeatingActionCharacterDisplayMenuItemAction</code> is a {@link CharacterDisplayMenuItemAction} with the
 * ability to repeatedly execute some action whenever the menu item is active.  This class makes it easier to poll for
 * some variable value and display it as it changes.
 * </p>
 * <p>
 * This class starts and stops the repeating action upon {@link #activate()} and {@link #deactivate()} so subclasses
 * which need to run code upon activate or deactive should override the {@link #preActivate()}, {@link #postActivate()},
 * {@link #preDeactivate()}, or {@link #postDeactivate()} methods as appropriate.  Unless specified otherwise in the
 * constructor, the repeating action is started {@link #DEFAULT_INITIAL_DELAY} milliseconds after the menu item is
 * {@link #activate() activated} and executes with a fixed delay of {@link #DEFAULT_DELAY} milliseconds while the menu
 * item is active.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"NoopMethodInAbstractClass"})
public abstract class RepeatingActionCharacterDisplayMenuItemAction extends CharacterDisplayMenuItemAction
   {
   private static final Logger LOG = Logger.getLogger(RepeatingActionCharacterDisplayMenuItemAction.class);

   public static final long DEFAULT_INITIAL_DELAY = 0;
   public static final long DEFAULT_DELAY = 200;
   public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

   private final long initialDelay;
   private final long delay;
   private final TimeUnit timeUnit;
   private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("RepeatingActionCharacterDisplayMenuItemAction"));
   private ScheduledFuture<?> scheduledFuture = null;
   private final Runnable repeatedActionRunnable =
         new Runnable()
         {
         public void run()
            {
            try
               {
               performAction();
               }
            catch (Exception e)
               {
               LOG.error("Exception while trying to execute the repeated action", e);
               }
            }
         };

   public RepeatingActionCharacterDisplayMenuItemAction(final MenuItem menuItem,
                                                        final MenuStatusManager menuStatusManager,
                                                        final CharacterDisplay characterDisplay)
      {
      this(menuItem, menuStatusManager, characterDisplay, null);
      }

   public RepeatingActionCharacterDisplayMenuItemAction(final MenuItem menuItem,
                                                        final MenuStatusManager menuStatusManager,
                                                        final CharacterDisplay characterDisplay,
                                                        final Map<String, String> properties)
      {
      this(menuItem, menuStatusManager, characterDisplay, properties, DEFAULT_INITIAL_DELAY, DEFAULT_DELAY, DEFAULT_TIME_UNIT);
      }

   public RepeatingActionCharacterDisplayMenuItemAction(final MenuItem menuItem,
                                                        final MenuStatusManager menuStatusManager,
                                                        final CharacterDisplay characterDisplay,
                                                        final long initialDelay,
                                                        final long delay,
                                                        final TimeUnit timeUnit)
      {
      this(menuItem, menuStatusManager, characterDisplay, null, initialDelay, delay, timeUnit);
      }

   public RepeatingActionCharacterDisplayMenuItemAction(final MenuItem menuItem,
                                                        final MenuStatusManager menuStatusManager,
                                                        final CharacterDisplay characterDisplay,
                                                        final Map<String, String> properties,
                                                        final long initialDelay,
                                                        final long delay,
                                                        final TimeUnit timeUnit)
      {
      super(menuItem, menuStatusManager, characterDisplay, properties);
      this.initialDelay = initialDelay;
      this.delay = delay;
      this.timeUnit = timeUnit;
      if (initialDelay < 0)
         {
         throw new IllegalArgumentException("Invalid initial delay [" + initialDelay + "].  Value must be non-negative.");
         }
      if (delay < 0)
         {
         throw new IllegalArgumentException("Invalid delay [" + delay + "].  Value must be non-negative.");
         }
      if (timeUnit == null)
         {
         throw new IllegalArgumentException("TimeUnit may not be null.");
         }
      }

   /** The action to be performed repeatedly. */
   protected abstract void performAction();

   protected void preActivate()
      {
      // do nothing
      }

   @Override
   public final void activate()
      {
      preActivate();
      try
         {
         scheduledFuture = executor.scheduleWithFixedDelay(repeatedActionRunnable, initialDelay, delay, timeUnit);
         }
      catch (Exception e)
         {
         LOG.error("RepeatingActionCharacterDisplayMenuItemAction.activate(): failed to schedule task", e);
         }
      postActivate();
      }

   protected void postActivate()
      {
      // do nothing
      }

   protected void preDeactivate()
      {
      // do nothing
      }

   @Override
   public final void deactivate()
      {
      preDeactivate();
      if (scheduledFuture != null)
         {
         scheduledFuture.cancel(true);
         }
      postDeactivate();
      }

   protected void postDeactivate()
      {
      // do nothing
      }
   }
