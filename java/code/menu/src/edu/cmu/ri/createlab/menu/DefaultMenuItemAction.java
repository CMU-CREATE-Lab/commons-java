package edu.cmu.ri.createlab.menu;

/**
 * @author Chris Bartley (bartley@cmu.edu)  
 */
public class DefaultMenuItemAction extends AbstractMenuItemAction
   {
   public DefaultMenuItemAction(final MenuItem menuItem, final MenuStatusManager menuStatusManager)
      {
      super(menuItem, menuStatusManager);
      }

   public void start()
      {
      if (getMenuItem().hasChildren())
         {
         getMenuStatusManager().setActiveMenuItem(getMenuItem().getFirstChild());
         }
      }

   public void stop()
      {
      final MenuItem parent = getMenuItem().getParent();
      if (!parent.isRoot())
         {
         getMenuStatusManager().setActiveMenuItem(parent);
         }
      }

   public void upEvent()
      {
      final MenuItem self = getMenuItem();
      final MenuItem sibling = self.getPreviousSibling();
      if (!self.equals(sibling))
         {
         getMenuStatusManager().setActiveMenuItem(sibling);
         }
      }

   public void downEvent()
      {
      final MenuItem self = getMenuItem();
      final MenuItem sibling = self.getNextSibling();
      if (!self.equals(sibling))
         {
         getMenuStatusManager().setActiveMenuItem(sibling);
         }
      }
   }
