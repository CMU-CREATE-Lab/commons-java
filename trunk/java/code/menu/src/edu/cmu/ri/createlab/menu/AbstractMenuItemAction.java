package edu.cmu.ri.createlab.menu;

/**
 * <p>
 * <code>AbstractMenuItemAction</code> is an abstract adapter class for {@link MenuItemAction}s. The methods in this
 * class which implement {@link MenuItemAction} are empty. This class exists as a convenience for creating
 * {@link MenuItemAction}s.
 * </p>
 * <p>
 * Extend this class to create a {@link MenuItemAction} and override the methods for the events of interest. (If you
 * implement the {@link MenuItemAction} interface, you have to define all of the methods in it. This abstract class
 * defines no-op methods for them all, so you only have to define methods for events you care about.)
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"NoopMethodInAbstractClass"})
public abstract class AbstractMenuItemAction implements MenuItemAction
   {
   private final MenuItem menuItem;
   private final MenuStatusManager menuStatusManager;

   protected AbstractMenuItemAction(final MenuItem menuItem, final MenuStatusManager menuStatusManager)
      {
      this.menuItem = menuItem;
      this.menuStatusManager = menuStatusManager;
      }

   protected final MenuItem getMenuItem()
      {
      return menuItem;
      }

   protected final MenuStatusManager getMenuStatusManager()
      {
      return menuStatusManager;
      }

   public void activate()
      {
      }

   public void deactivate()
      {
      }

   public void start()
      {
      }

   public void stop()
      {
      }

   public void upEvent()
      {
      }

   public void rightEvent()
      {
      }

   public void downEvent()
      {
      }

   public void leftEvent()
      {
      }
   }
