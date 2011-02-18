package edu.cmu.ri.createlab.menu;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DefaultMenuStatusManager implements MenuStatusManager
   {
   private MenuItem activeMenuItem = null;
   private final AtomicBoolean willProcessEvents = new AtomicBoolean(true);

   public MenuItem getActiveMenuItem()
      {
      return activeMenuItem;
      }

   public void setActiveMenuItem(final MenuItem newMenuItem)
      {
      if (activeMenuItem != null)
         {
         activeMenuItem.getMenuItemAction().deactivate();
         }
      activeMenuItem = newMenuItem;
      if (newMenuItem != null)
         {
         newMenuItem.getMenuItemAction().activate();
         }
      }

   public void handleStartEvent()
      {
      if (activeMenuItem != null)
         {
         if (willProcessEvents.getAndSet(false))
            {
            activeMenuItem.getMenuItemAction().start();
            willProcessEvents.set(true);
            }
         }
      }

   public void handleStopEvent()
      {
      if (activeMenuItem != null)
         {
         if (willProcessEvents.getAndSet(false))
            {
            activeMenuItem.getMenuItemAction().stop();
            willProcessEvents.set(true);
            }
         }
      }

   public void handleUpEvent()
      {
      if (activeMenuItem != null)
         {
         if (willProcessEvents.getAndSet(false))
            {
            activeMenuItem.getMenuItemAction().upEvent();
            willProcessEvents.set(true);
            }
         }
      }

   public void handleRightEvent()
      {
      if (activeMenuItem != null)
         {
         if (willProcessEvents.getAndSet(false))
            {
            activeMenuItem.getMenuItemAction().rightEvent();
            willProcessEvents.set(true);
            }
         }
      }

   public void handleDownEvent()
      {
      if (activeMenuItem != null)
         {
         if (willProcessEvents.getAndSet(false))
            {
            activeMenuItem.getMenuItemAction().downEvent();
            willProcessEvents.set(true);
            }
         }
      }

   public void handleLeftEvent()
      {
      if (activeMenuItem != null)
         {
         if (willProcessEvents.getAndSet(false))
            {
            activeMenuItem.getMenuItemAction().leftEvent();
            willProcessEvents.set(true);
            }
         }
      }

   @Override
   public void handleAccessoryOneEvent()
      {
      //To change body of implemented methods use File | Settings | File Templates.
      }

   @Override
   public void handleAccessoryTwoEvent()
      {
      //To change body of implemented methods use File | Settings | File Templates.
      }

   @Override
   public void handleAccessoryThreeEvent()
      {
      //To change body of implemented methods use File | Settings | File Templates.
      }
   }